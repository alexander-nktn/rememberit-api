package com.boost.memory.controllers;

import com.boost.memory.types.card.dto.CardGenerateDTO;
import com.boost.memory.exception.ServiceMethodContext;
import com.boost.memory.models.Card;
import com.boost.memory.services.CardService;
import com.boost.memory.types.card.service.CardGenerateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardsController {

    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    @Autowired
    private CardService cardService;

    @GetMapping("/{id}")
    public Card getCardById(@PathVariable String id) {
        ServiceMethodContext ctx = new ServiceMethodContext();

        return cardService.getOneOrFail(id, ctx);
    }

    @GetMapping
    public List<Card> getCards() {
        log.info("Getting all cards");

        return cardService.getMany();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(
            @PathVariable String id
    ) {
        ServiceMethodContext ctx = new ServiceMethodContext();
        cardService.delete(id, ctx);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate")
    public List<Card> generateCard(
            @RequestBody CardGenerateDTO cardGenerateDTO
    ) {
        ServiceMethodContext ctx = new ServiceMethodContext();

        return cardService.generate(
                new CardGenerateOptions(cardGenerateDTO.words, cardGenerateDTO.language),
                ctx
        );
    }
}
