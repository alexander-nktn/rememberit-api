package com.boost.memory.repositories;

import com.boost.memory.models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, String> {
}
