package com.boost.memory.services;
import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.Card;
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

    public CardService(
            TranslationService translationService
    ) {
        this.translationService = translationService;
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
        String language = cardGenerateOptions.language;
        ArrayList<Card> cards = new ArrayList<>();

        try {
              for (String word : cardGenerateOptions.words) {
                  CardCreateOptions cardCreateOptions = new CardCreateOptions(ctx.user);

                  cardCreateOptions.translation = this.translationService
                          .translateAndSave(new TranslationTranslateOptions(word, language), ctx);

                  cardCreateOptions.imageUrl = "imageUrl";

                  cards.add(this.create(cardCreateOptions, ctx));
                }

                return cards;
        } catch (Exception error) {
            throw new RuntimeException("Failed to generate cards", error);
        }
    }
}
