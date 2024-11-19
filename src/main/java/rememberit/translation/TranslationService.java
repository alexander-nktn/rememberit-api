package rememberit.translation;
import rememberit.exception.ServiceMethodContext;
import com.google.cloud.translate.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.translate.TranslateOptions;


@Service
public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final Translate translate;

    public TranslationService() {
        this.translate = TranslateOptions.getDefaultInstance().getService();
    }

    @Autowired
    private TranslationRepository translationRepository;

    private Translation create(CreateTranslationOptions opts, ServiceMethodContext ctx) {
        Translation card = new Translation(
                opts.text,
                opts.translatedText,
                opts.sourceLanguage,
                opts.targetLanguage
        );

        try {
            return translationRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create translation", error);
        }
    }

    private String translate(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
        try {
            com.google.cloud.translate.Translation translation = translate.translate(
                    opts.text,
                    Translate.TranslateOption.targetLanguage(opts.targetLanguage),
                    Translate.TranslateOption.sourceLanguage(opts.sourceLanguage),
                    Translate.TranslateOption.model("base"));
            return translation.getTranslatedText();
        } catch (Exception error) {
            throw new RuntimeException("Failed to translate word", error);
        }
    }

    public Translation translateAndSave(TranslateTranslationOptions opts, ServiceMethodContext ctx) {
        String translatedText = this.translate(opts, ctx);
        CreateTranslationOptions translationCreateOptions = new CreateTranslationOptions(
                opts.text,
                translatedText,
                opts.sourceLanguage,
                opts.targetLanguage
        );

        return this.create(translationCreateOptions, ctx);
    }
}
