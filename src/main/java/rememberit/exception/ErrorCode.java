package rememberit.exception;

public enum ErrorCode {
    // Auth
    AUTH_INVALID_CREDENTIALS,
    AUTH_TOKEN_NOT_FOUND,
    AUTH_TOKEN_EXPIRED,
    AUTH_FAILED_TO_GENERATE_TOKEN,
    AUTH_FAILED_TO_REFRESH_TOKEN,

    // User
    USER_EMAIL_ALREADY_EXISTS,
    USER_NOT_FOUND,
    USER_FAILED_TO_CREATE,
    USER_FAILED_TO_UPDATE,
    USER_FAILED_TO_DELETE,

    // Card
    CARD_NOT_FOUND,
    CARD_FAILED_TO_CREATE,
    CARD_FAILED_TO_UPDATE,
    CARD_FAILED_TO_DELETE,
    CARD_FAILED_TO_GENERATE,

    // Google Sheets
    GOOGLE_SPREADSHEET_INVALID_URL,
    GOOGLE_SPREADSHEET_FAILED_TO_RETRIEVE,
    GOOGLE_CREDENTIALS_FAILED_TO_LOAD,
    GOOGLE_API_FAILED_TO_INITIALIZE,
    GOOGLE_SPREADSHEET_FAILED_TO_ACCESS,

    // Translation
    TRANSLATION_NOT_FOUND,
    TRANSLATION_FAILED_TO_CREATE,
    TRANSLATION_FAILED_TO_UPDATE,
    TRANSLATION_FAILED_TO_DELETE,
    TRANSLATION_FAILED_TO_PROCESS,
    TRANSLATION_TEXT_INVALID,

    // Image
    IMAGE_COLOR_FORMAT_INVALID,
    IMAGE_GENERATION_FAILED,
}