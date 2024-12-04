package rememberit.image;

import rememberit.exception.ServiceMethodContext;
import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;

import java.io.FileOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.IOException;

@Service
public class ImageService {

//    private final WebClient webClient;
//    String apiKey = System.getenv();
//    String apiKey = "";


//    public ImageService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("https://api.openai.com").build();
//    }

//    public Mono<String> generate(String word, ServiceMethodContext ctx) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("prompt", word);
//        requestBody.put("n", 1);
//        requestBody.put("size", "512x512");
//
//        return webClient.post()
//                .uri("/v1/images/generations")
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .onStatus(status -> status.isError(), clientResponse ->
//                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
//                            // Parse the error response
//                            String errorMessage = parseErrorMessage(errorBody);
//                            System.out.println("Error occurred: " + errorMessage);
//                            return Mono.error(new RuntimeException(errorMessage));
//                        })
//                )
//                .bodyToMono(String.class);
//    }

//    private String parseErrorMessage(String errorBody) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode errorNode = objectMapper.readTree(errorBody).path("error");
//            String code = errorNode.path("code").asText();
//            String message = errorNode.path("message").asText();
//
//            if ("billing_hard_limit_reached".equals(code)) {
//                return "Billing limit reached: " + message;
//            } else {
//                return "API Error (" + code + "): " + message;
//            }
//        } catch (Exception e) {
//            return "Failed to parse error response: " + e.getMessage();
//        }
//    }

    public byte[] generateWithBackground(
            String text,
            String translatedText,
            String backgroundColor,
            String textColor,
            String translatedTextColor,
            ServiceMethodContext ctx
    ) throws IOException {
        // Use a high DPI directly (e.g., 300 DPI for print-like quality)
        int dpi = 300;
        int targetWidth = 364;
        int targetHeight = 170;
        double scaleFactor = dpi / 72.0; // Scale factor for DPI
        int width = (int) (targetWidth * scaleFactor);
        int height = (int) (targetHeight * scaleFactor);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Enable high-quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Set background color
        g2d.setColor(Color.decode(backgroundColor));
        g2d.fillRect(0, 0, width, height);

        // Draw first line (text), centered
        g2d.setColor(Color.decode(textColor));
        Font font1 = new Font("Arial", Font.BOLD, (int) (26 * scaleFactor));
        g2d.setFont(font1);
        FontMetrics fm1 = g2d.getFontMetrics();
        int textWidth1 = fm1.stringWidth(text);
        int x1 = (width - textWidth1) / 2;
        int y1 = (height - fm1.getHeight()) / 2 + fm1.getAscent();
        g2d.drawString(text, x1, y1);

        // Draw second line (translatedText), at bottom-right
        g2d.setColor(Color.decode(translatedTextColor));
        Font font2 = new Font("Arial", Font.BOLD, (int) (12 * scaleFactor));
        g2d.setFont(font2);
        FontMetrics fm2 = g2d.getFontMetrics();
        int textWidth2 = fm2.stringWidth(translatedText);
        int margin = (int) (20 * scaleFactor);
        int x2 = width - textWidth2 - margin;
        int y2 = height - fm2.getDescent() - margin;
        g2d.drawString(translatedText, x2, y2);

        // Dispose of graphics object
        g2d.dispose();

        // Save the high-DPI image directly without scaling
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        return imageBytes;
    }
}