package rememberit.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rememberit.card.types.resolver.GenerateCardsInput;
import rememberit.card.types.resolver.UpdateCardInput;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.card.types.service.UpdateCardOptions;
import rememberit.exception.ServiceMethodContext;
import rememberit.translation.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import rememberit.translation.TranslationService;
import rememberit.translation.types.service.UpdateTranslationOptions;

import java.util.List;

    @Controller
    public class CardResolver {

        private static final Logger log = LoggerFactory.getLogger(CardResolver.class);
        private final CardService cardService;
        private final TranslationService translationService;

        @Autowired
        public CardResolver(
                CardService cardService,
                TranslationService translationService
        ) {
            this.cardService = cardService;
            this.translationService = translationService;
        }

    @QueryMapping
    public Card getCardById(@Argument String id) {
        ServiceMethodContext ctx = new ServiceMethodContext();
        return cardService.getOneOrFail(id, ctx);
    }

    @QueryMapping
    public List<Card> getCards() {
        return cardService.getMany();
    }

    @MutationMapping
    public Card updateCard(@Argument UpdateCardInput input) {
        ServiceMethodContext ctx = new ServiceMethodContext();

        System.out.println(5555);
        System.out.println(input.translation);

        if (input.translation != null) {
            System.out.println(6666);
            System.out.println(input.translation.id);
            translationService.update(
                    new UpdateTranslationOptions.Builder(input.translation.id)
                            .text(input.translation.text)
                            .translatedText(input.translation.translatedText)
                            .build()
                    , ctx);
        }

        UpdateCardOptions options = new UpdateCardOptions.Builder(input.id)
                .backgroundColor(input.backgroundColor)
                .translatedTextColor(input.translatedTextColor)
                .textColor(input.textColor)
                .build();

        return cardService.update(options, ctx);
    }

    @MutationMapping
    public boolean deleteCard(@Argument String id) {
        ServiceMethodContext ctx = new ServiceMethodContext();
        cardService.delete(id, ctx);
        return true;
    }

    @MutationMapping
    public List<Card> generateCards(@Argument GenerateCardsInput input) {
        ServiceMethodContext ctx = new ServiceMethodContext();

        // Convert GenerateCardInput to GenerateCardOptions
        GenerateCardsOptions options = new GenerateCardsOptions(
                input.getTexts(),
                input.getSpreadsheetUrl(),
                input.getSourceLanguage(),
                input.getTargetLanguage(),
                input.getBackgroundColor(),
                input.getTextColor(),
                input.getTranslatedTextColor()
        );

        return cardService.generate(options, ctx);
    }

    @SchemaMapping(typeName = "Card", field = "sourceLanguage")
    public String getSourceLanguage(Card card) {
        Translation translation = card.getTranslation();
        return translation != null ? translation.getSourceLanguage() : null;
    }

    @SchemaMapping(typeName = "Card", field = "targetLanguage")
    public String getTargetLanguage(Card card) {
        Translation translation = card.getTranslation();
        return translation != null ? translation.getTargetLanguage() : null;
    }

    @SchemaMapping(typeName = "Card", field = "translation")
    public Translation getTranslation(Card card) {
        return card.getTranslation();
    }
}