package rememberit.textCollector;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import rememberit.config.ServiceMethodContext;
import rememberit.exception.ApplicationException;
import rememberit.exception.ErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class TextCollectorService {
    private static final Logger logger = LoggerFactory.getLogger(TextCollectorService.class);

    private static final String APPLICATION_NAME = "Rememberit";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final Sheets sheetsService;

    public TextCollectorService() {
        logger.info("Initializing TextCollectorService...");
        try {
            // Initialize the transport
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            // Load service account key file from classpath resources
            InputStream in = getClass().getClassLoader().getResourceAsStream("boost-memory-438520-7fea6eea7de1.json");
            if (in == null) {
                logger.error("Resource not found: boost-memory-438520-7fea6eea7de1.json");
                throw new ApplicationException(
                        "Resource file for Google Sheets API credentials not found",
                        ErrorCode.GOOGLE_CREDENTIALS_FAILED_TO_LOAD,
                        null
                );
            }

            // Use GoogleCredentials to get credentials with the required scope
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY));

            sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            logger.info("TextCollectorService successfully initialized.");
        } catch (IOException e) {
            logger.error("IOException occurred while initializing Google Sheets API: {}", e.getMessage(), e);
            throw new ApplicationException(
                    "IOException occurred during Google Sheets API initialization",
                    ErrorCode.GOOGLE_API_FAILED_TO_INITIALIZE,
                    null,
                    e
            );
        } catch (GeneralSecurityException e) {
            logger.error("Security exception while initializing Google Sheets API: {}", e.getMessage(), e);
            throw new ApplicationException(
                    "Security exception occurred during Google Sheets API initialization",
                    ErrorCode.GOOGLE_API_FAILED_TO_INITIALIZE,
                    null,
                    e
            );
        }
    }

    public List<List<Object>> getSpreadsheetValues(String spreadsheetId, String range, ServiceMethodContext ctx) {
        ctx.addProperty("spreadsheetId", spreadsheetId);
        ctx.addProperty("range", range);
        logger.info("Fetching spreadsheet values. Spreadsheet ID: {}, Range: {}", spreadsheetId, range);

        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            logger.info("Successfully fetched {} rows from spreadsheet ID: {}",
                    response.getValues() != null ? response.getValues().size() : 0,
                    spreadsheetId);
            return response.getValues();
        } catch (IOException e) {
            logger.error("Failed to fetch spreadsheet values. Spreadsheet ID: {}, Range: {}", spreadsheetId, range, e);
            throw new ApplicationException(
                    "Failed to fetch spreadsheet values due to an IO error",
                    ErrorCode.GOOGLE_SPREADSHEET_FAILED_TO_ACCESS,
                    ctx,
                    e
            );
        } catch (Exception e) {
            logger.error("Unexpected error while fetching spreadsheet values. Spreadsheet ID: {}, Range: {}", spreadsheetId, range, e);
            throw new ApplicationException(
                    "Unexpected error occurred while fetching spreadsheet values",
                    ErrorCode.GOOGLE_SPREADSHEET_FAILED_TO_ACCESS,
                    ctx,
                    e
            );
        }
    }
}