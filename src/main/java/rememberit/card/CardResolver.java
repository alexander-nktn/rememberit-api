package rememberit.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.access.prepost.PreAuthorize;
import rememberit.card.types.resolver.GenerateCardsInput;
import rememberit.card.types.resolver.UpdateCardInput;
import rememberit.card.types.service.GenerateCardsOptions;
import rememberit.card.types.service.GenerateCardsTranslationsOptions;
import rememberit.card.types.service.UpdateCardOptions;
import rememberit.config.ServiceMethodContext;
import rememberit.translation.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import rememberit.translation.TranslationService;
import rememberit.translation.types.service.UpdateTranslationOptions;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

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
//    @PreAuthorize("hasAuthority('CARD_CAN_GET')")
    public Card getCardById(
            @Argument final String id,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        return cardService.getOneOrFail(id, ctx);
    }

    @QueryMapping
    public List<Card> getCards(DataFetchingEnvironment env) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        return cardService.getMany();
    }

    @MutationMapping
    public Card updateCard(
            @Argument UpdateCardInput input,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        if (input.translation != null) {
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
    public String deleteCard(
            @Argument final String id,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");
        cardService.delete(id, ctx);

        return id;
    }

    @MutationMapping
    public List<Card> generateCards(
            @Argument GenerateCardsInput input,
            DataFetchingEnvironment env
    ) {
        ServiceMethodContext ctx = env.getGraphQlContext().get("serviceMethodContext");

        GenerateCardsOptions options = new GenerateCardsOptions.Builder()
            .backgroundColor(input.backgroundColor)
            .spreadsheetUrl(input.spreadsheetUrl)
            .textColor(input.textColor)
            .translatedTextColor(input.translatedTextColor)
            .sourceLanguage(input.sourceLanguage)
            .targetLanguage(input.targetLanguage)
            .translations(
                input.translations.stream()
                    .map(translation -> new GenerateCardsTranslationsOptions.Builder()
                        .text(translation.text)
                        .translatedText(translation.translatedText)
                        .build())
                    .collect(Collectors.toList())
            )
            .build();


        return cardService.generate(options, ctx);
    }

    @SchemaMapping(typeName = "Card", field = "translation")
    public Translation getTranslation(Card card) {
        return card.getTranslation();
    }
}