package com.boost.memory.repositories;

import com.boost.memory.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, String> {
}
