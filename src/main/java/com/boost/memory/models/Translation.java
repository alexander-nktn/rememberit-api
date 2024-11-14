package com.boost.memory.models;
import com.boost.memory.types.translation.TranslationCreateOptions;
import jakarta.persistence.*;

@Entity(name = "translations")
public class Translation {

    public Translation() {}
    public Translation(TranslationCreateOptions opts) {
        this.text = opts.text;
        this.translatedText = opts.translatedText;
        this.sourceLanguage = opts.sourceLanguage;
        this.targetLanguage = opts.targetLanguage;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(nullable = false)
    public String text;

    @Column(nullable = false)
    public String translatedText;

    @Column(nullable = false)
    public String sourceLanguage;

    @Column(nullable = false)
    public String targetLanguage;
}
