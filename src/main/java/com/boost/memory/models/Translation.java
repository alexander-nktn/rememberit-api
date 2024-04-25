package com.boost.memory.models;
import com.boost.memory.types.translation.TranslationCreateOptions;
import jakarta.persistence.*;

@Entity(name = "translations")
public class Translation {

    public Translation() {}
    public Translation(TranslationCreateOptions opts) {
        this.mainWord = opts.mainWord;
        this.translatedWord = opts.translatedWord;
        this.language = opts.language;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(nullable = false)
    public String mainWord;

    @Column(nullable = false)
    public String translatedWord;

    @Column(nullable = false)
    public String language;
}
