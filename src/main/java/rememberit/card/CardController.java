package rememberit.card;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rememberit.exception.ServiceMethodContext;
import rememberit.image.ImageService;
import rememberit.translation.Translation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private CardService cardService;
    private ImageService imageService;

    @Autowired
    public void CardDownloadController(
            CardService cardService,
            ImageService imageService
    ) {
        this.cardService = cardService;
        this.imageService = imageService;
    }

    /**
     * Endpoint to download cards as a ZIP archive containing images and a CSV file.
     *
     * @param ids Optional list of card IDs to download. If not provided, all cards are downloaded.
     * @return ResponseEntity containing the ZIP file as a byte array.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadCards(@RequestParam(required = false) List<String> ids) {
        ServiceMethodContext ctx = new ServiceMethodContext();
        List<Card> cards;

        try {
            if (ids == null || ids.isEmpty()) {
                cards = cardService.getMany();
            } else {
                cards = ids.stream()
                        .map(id -> cardService.getOneOrFail(id, ctx))
                        .toList();
            }

            if (cards.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No cards found to download.".getBytes());
            }

            // Create a ZIP archive in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            // Add images to the ZIP
            for (Card card : cards) {
                Translation translation = card.getTranslation();
                if (translation == null) {
                    continue; // Skip cards without translations
                }

                // Generate image bytes
                byte[] imageBytes = imageService.generateWithBackground(
                        translation.getText(),
                        translation.getTranslatedText(),
                        card.getBackgroundColor(),
                        card.getTextColor(),
                        card.getTranslatedTextColor(),
                        ctx
                );

                // Define the ZIP entry name
                String imageName = "card_" + card.getId() + ".png";

                // Add the image to the ZIP
                ZipEntry imageEntry = new ZipEntry(imageName);
                zos.putNextEntry(imageEntry);
                zos.write(imageBytes);
                zos.closeEntry();
            }

            // Optionally, add a CSV file with card details
            String csvContent = convertCardsToCsv(cards);
            ZipEntry csvEntry = new ZipEntry("cards.csv");
            zos.putNextEntry(csvEntry);
            zos.write(csvContent.getBytes());
            zos.closeEntry();

            // Finalize the ZIP
            zos.close();
            byte[] zipBytes = baos.toByteArray();
            baos.close();

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cards.zip");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(("Card not found: " + e.getMessage()).getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(("Error generating images: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(("An unexpected error occurred: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Converts a list of Card objects to CSV format.
     *
     * @param cards List of cards to convert.
     * @return CSV string representing the cards.
     */
    private String convertCardsToCsv(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Text,TranslatedText,BackgroundColor,TextColor,TranslatedTextColor,ImageUrl,CreatedAt\n");
        for (Card card : cards) {
            Translation translation = card.getTranslation();
            sb.append(escapeCsv(card.getId())).append(",")
                    .append(escapeCsv(translation.getText())).append(",")
                    .append(escapeCsv(translation.getTranslatedText())).append(",")
                    .append(escapeCsv(card.getBackgroundColor())).append(",")
                    .append(escapeCsv(card.getTextColor())).append(",")
                    .append(escapeCsv(card.getTranslatedTextColor())).append(",")
                    .append(escapeCsv(card.getImageUrl())).append(",")
                    .append(card.getCreatedAt()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Escapes CSV special characters in a string.
     *
     * @param value The string to escape.
     * @return Escaped string suitable for CSV.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        boolean containsSpecialChars = value.contains(",") || value.contains("\"") || value.contains("\n");

        if (!containsSpecialChars) {
            return value;
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}