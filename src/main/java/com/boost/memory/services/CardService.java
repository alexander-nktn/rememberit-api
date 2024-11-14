package com.boost.memory.services;
import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.Card;
import com.boost.memory.models.Translation;
import com.boost.memory.repositories.CardRepository;
import com.boost.memory.types.card.service.CardCreateOptions;
import com.boost.memory.types.card.service.CardGenerateOptions;
import com.boost.memory.types.card.service.CardUpdateOptions;
import com.boost.memory.types.translation.TranslationTranslateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final TranslationService translationService;
    private final ImageService imageService;
    private final WordCollectionService wordCollectionService;


    public CardService(
            TranslationService translationService,
            ImageService imageService,
            WordCollectionService wordCollectionService
    ) {
        this.translationService = translationService;
        this.imageService = imageService;
        this.wordCollectionService = wordCollectionService;
    }

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getMany() {
        return cardRepository.findAll();
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

    private Card create(CardCreateOptions opts, ServiceMethodContext ctx) {
        ctx.addProperty("cardCreateDTO", opts);

        Card card = new Card(opts);

        try {
            return cardRepository.save(card);
        } catch (Exception error) {
            throw new RuntimeException("Failed to create card", error);
        }
    }

    private Card update(CardUpdateOptions cardUpdateOptions, ServiceMethodContext ctx) {
        ctx.addProperty("cardUpdateDTO", cardUpdateOptions);
        Card card = getOneOrFail(cardUpdateOptions.id, ctx);

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

        public ArrayList<Card> generate(CardGenerateOptions cardGenerateOptions, ServiceMethodContext ctx) {
            ctx.addProperty("cardGenerateDTO", cardGenerateOptions);
            ArrayList<Card> cards = new ArrayList<>();

            String spreadsheetId = "1GgN4Mf4Mi3vnB3vtL0zDAoyqkZUw9lohgkX7x2u2TsQ";
            String range = "'Saved translations'!A1:A";
            try {
                List<List<Object>> values = this.wordCollectionService.getSpreadsheetValues(spreadsheetId, range);

                System.out.println(values);

                cardGenerateOptions.texts = values.stream()
                        .map(row -> row.get(0).toString())
                        .toArray(String[]::new);
            } catch (Exception error) {
                throw new RuntimeException("Failed to get spreadsheet values", error);
            }

            try {
                  for (String text : cardGenerateOptions.texts) {
                      CardCreateOptions cardCreateOptions = new CardCreateOptions(ctx.user);

                      Translation translation = this.translationService.translateAndSave(
                              new TranslationTranslateOptions(
                                      text,
                                      cardGenerateOptions.sourceLanguage,
                                      cardGenerateOptions.targetLanguage
                              ), ctx);

                      // implement when AI is ready
//                      cardCreateOptions.imageUrl = this.imageService.generate(cardCreateOptions.translation.translatedWord, ctx).block();

                      this.imageService.generateWithBackground(
                                translation.text,
                                translation.translatedText,
                                cardGenerateOptions.backgroundColor,
                                cardGenerateOptions.textColor,
                                cardGenerateOptions.translatedTextColor,
                                ctx
                      );

                        cardCreateOptions.translation = translation;

                      cards.add(this.create(cardCreateOptions, ctx));
                    }

                    return cards;
            } catch (Exception error) {
                throw new RuntimeException("Failed to generate cards", error);
            }
        }
}
