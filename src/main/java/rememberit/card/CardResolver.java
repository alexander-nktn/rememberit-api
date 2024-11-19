package rememberit.card;

import rememberit.card.types.resolver.GenerateCardsInput;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.exception.ServiceMethodContext;
import rememberit.translation.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
public class CardResolver {

    @Autowired
    private CardService cardService;

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

        // Call service method to generate cards
        return cardService.generate(options, ctx);
    }

    // Field-level resolvers for unmapped fields

    @SchemaMapping(typeName = "Card", field = "texts")
    public List<String> getTexts(Card card) {
        Translation translation = card.getTranslation();
        if (translation != null) {
            return Arrays.asList(translation.getText(), translation.getTranslatedText());
        }
        return List.of();
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