package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.LabelKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO for {@link LabelKey} table.
 */
@Repository
public interface LabelKeyDao extends CrudRepository<LabelKey, Long> {
    /**
     * Checks, if {@link LabelKey} with given {@link LabelKey} value exists or not.
     *
     * @param value not-empty string with value
     * @return true if exists, false if not.
     */
    boolean existsByValue(String value);

    /**
     * Provides {@link LabelKey} by its value.
     *
     * @param value non-empty string with value.
     * @return {@link Optional} with found {@link LabelKey} object or {@link Optional#empty()}
     */
    Optional<LabelKey> findByValue(String value);
}