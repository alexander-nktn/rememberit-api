package rememberit.image;

import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

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
    ) {
        logger.info("Starting image generation with text: {}, translatedText: {}", text, translatedText);

        // Add context properties
        ctx.addProperty("text", text);
        ctx.addProperty("translatedText", translatedText);
        ctx.addProperty("backgroundColor", backgroundColor);
        ctx.addProperty("textColor", textColor);
        ctx.addProperty("translatedTextColor", translatedTextColor);

        try {
            // High DPI settings
            int dpi = 300;
            int targetWidth = 364;
            int targetHeight = 170;
            double scaleFactor = dpi / 72.0;
            int width = (int) (targetWidth * scaleFactor);
            int height = (int) (targetHeight * scaleFactor);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // Rendering hints for high quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            // Set background color
            g2d.setColor(Color.decode(backgroundColor));
            g2d.fillRect(0, 0, width, height);

            // Draw main text (centered)
            g2d.setColor(Color.decode(textColor));
            Font mainFont = new Font("Arial", Font.BOLD, (int) (26 * scaleFactor));
            g2d.setFont(mainFont);
            FontMetrics mainMetrics = g2d.getFontMetrics();
            int textWidth = mainMetrics.stringWidth(text);
            int x = (width - textWidth) / 2;
            int y = (height - mainMetrics.getHeight()) / 2 + mainMetrics.getAscent();
            g2d.drawString(text, x, y);

            // Draw translated text (bottom-right)
            g2d.setColor(Color.decode(translatedTextColor));
            Font translatedFont = new Font("Arial", Font.PLAIN, (int) (12 * scaleFactor));
            g2d.setFont(translatedFont);
            FontMetrics translatedMetrics = g2d.getFontMetrics();
            int translatedTextWidth = translatedMetrics.stringWidth(translatedText);
            int margin = (int) (20 * scaleFactor);
            int translatedX = width - translatedTextWidth - margin;
            int translatedY = height - translatedMetrics.getDescent() - margin;
            g2d.drawString(translatedText, translatedX, translatedY);

            // Dispose graphics object
            g2d.dispose();

            // Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            logger.info("Image generation successful.");
            return imageBytes;

        } catch (IllegalArgumentException ex) {
            logger.error("Invalid color format. Colors - Background: {}, Text: {}, TranslatedText: {}",
                    backgroundColor, textColor, translatedTextColor, ex);
            throw new ApplicationException("Invalid color format", ErrorCode.IMAGE_COLOR_FORMAT_INVALID, ctx, ex);

        } catch (IOException ex) {
            logger.error("Error generating image: {}", ex.getMessage(), ex);
            throw new ApplicationException("Error generating image", ErrorCode.IMAGE_GENERATION_FAILED, ctx, ex);

        } catch (Exception ex) {
            logger.error("Unexpected error while generating image: {}", ex.getMessage(), ex);
            throw new ApplicationException("Unexpected error while generating image", ErrorCode.IMAGE_GENERATION_FAILED, ctx, ex);
        }
    }
}