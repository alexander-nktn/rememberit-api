package rememberit.card;
import rememberit.card.types.service.GenerateCardsTranslationsOptions;
import rememberit.exception.ServiceMethodContext;
import rememberit.translation.Translation;
import rememberit.image.ImageService;
import rememberit.translation.TranslationService;
import rememberit.textCollector.TextCollectorService;
import rememberit.card.types.service.CreateCardOptions;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.card.types.service.UpdateCardOptions;
import rememberit.translation.types.service.CreateTranslationOptions;
import rememberit.translation.types.service.TranslateTranslationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final TranslationService translationService;
    private final ImageService imageService;
    private final TextCollectorService wordCollectionService;


    public CardService(
            TranslationService translationService,
            ImageService imageService,
            TextCollectorService wordCollectionService
    ) {
        this.translationService = translationService;
        this.imageService = imageService;
        this.wordCollectionService = wordCollectionService;
    }

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getMany() {
        List<Card> cards = cardRepository.findAll();

        cards.sort(Comparator.comparing(Card::getCreatedAt).reversed());

        return cards;
    }

    public Optional<Card> getOne(String id) {
        return cardRepository.findById(id);
    }

    public Card getOneOrFail(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<Card> card  = this.getOne(id);

        if (card.isEmpty()) {
            throw new EntityNotFoundException(String.format("Card with id: %s not found", id));
        }

        return card.get();
    }

    private Card create(CreateCardOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("opts", opts);

        Card card = new Card(
                opts.imageUrl,
                opts.backgroundColor,
                opts.textColor,
                opts.translatedTextColor,
                opts.translation,
                opts.user
        );

        try {
            return cardRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create card", error);
        }
    }

    public Card update(UpdateCardOptions cardUpdateOptions, ServiceMethodContext ctx) {
        ctx.addProperty("cardUpdateOptions", cardUpdateOptions);
        Card card = getOneOrFail(cardUpdateOptions.id, ctx);

        card.setBackgroundColor(cardUpdateOptions.backgroundColor);
        card.setTextColor(cardUpdateOptions.textColor);
        card.setTranslatedTextColor(cardUpdateOptions.translatedTextColor);
        card.setImageUrl(cardUpdateOptions.imageUrl);

        try {
            return cardRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create short Card", error);
        }
    }

    public void delete(String id, ServiceMethodContext ctx) {
        ctx.addProperty("id", id);
        Optional<Card> card = this.getOne(id);

        if (card.isEmpty()) {
            return;
        }

        try {
            cardRepository.deleteById(id);
        } catch (Exception error) {
            throw new RuntimeException("Failed to delete card", error);
        }
    }

        public List<Card> generate(GenerateCardsOptions opts, ServiceMethodContext ctx) {
            ctx.addProperty("cardGenerateDTO", opts);
            ArrayList<Card> cards = new ArrayList<>();

            String spreadsheetId;

            if (opts.spreadsheetUrl != null && !opts.spreadsheetUrl.isEmpty()) {
                spreadsheetId = this.getIdFromUrl(opts.spreadsheetUrl);

                String range = "'Saved translations'!A1:A";
                try {
                    List<List<Object>> values = this.wordCollectionService.getSpreadsheetValues(spreadsheetId, range);
                    List<GenerateCardsTranslationsOptions> translations = new ArrayList<>();

                    for (List<Object> row : values) {
                        translations.add(
                                new GenerateCardsTranslationsOptions.Builder()
                                        .text(row.get(0).toString())
                                        .build()
                        );
                    }

                    opts.translations = translations;
                } catch (Exception error) {
                    throw new RuntimeException("Failed to get spreadsheet values", error);
                }
            }

            try {
                  for (GenerateCardsTranslationsOptions generateCardsTranslationsOptions : opts.translations) {
                      Translation translation;

                      if (!generateCardsTranslationsOptions.translatedText.isEmpty()) {
                          translation = this.translationService.create(
                              new CreateTranslationOptions.Builder()
                                  .text(generateCardsTranslationsOptions.text)
                                  .translatedText(generateCardsTranslationsOptions.translatedText)
                                  .sourceLanguage(opts.sourceLanguage)
                                  .targetLanguage(opts.targetLanguage)
                                  .build(),
                              ctx
                          );
                      } else {
                          translation = this.translationService.translateAndSave(
                                  new TranslateTranslationOptions.Builder()
                                          .text(generateCardsTranslationsOptions.text)
                                          .sourceLanguage(opts.sourceLanguage)
                                          .targetLanguage(opts.targetLanguage)
                                          .build()
                                  , ctx);
                      }

                      // implement when AI is ready
//                      cardCreateOptions.imageUrl = this.imageService.generate(cardCreateOptions.translation.translatedWord, ctx).block();

//                      this.imageService.generateWithBackground(
//                          translation.text,
//                          translation.translatedText,
//                          opts.backgroundColor,
//                          opts.textColor,
//                          opts.translatedTextColor,
//                          ctx
//                      );
                      String imageUrl = "no image";

                      if (translation == null) {
                          continue;
                      }

                      cards.add(
                          this.create(
                                new CreateCardOptions.Builder()
                                        .translation(translation)
                                        .imageUrl(imageUrl)
                                        .backgroundColor(opts.backgroundColor)
                                        .textColor(opts.textColor)
                                        .translatedTextColor(opts.translatedTextColor)
                                        .user(ctx.user)
                                        .build(),
                                ctx
                          )
                      );
                    }

                    return cards;
            } catch (Exception error) {
                throw new RuntimeException("Failed to generate cards", error);
            }
        }

        private String getIdFromUrl(String url) {
            String[] parts = url.split("/");
            String id = parts[parts.length - 2];

            if (id.isEmpty()) {
                throw new RuntimeException("Failed to get spreadsheet ID from URL");
            }

            return id;
        }
}
