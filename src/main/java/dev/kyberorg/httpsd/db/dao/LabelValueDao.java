package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.LabelValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO for {@link LabelValue} table.
 */
@Repository
public interface LabelValueDao extends CrudRepository<LabelValue, Long> {
    /**
     * Checks, if {@link LabelValue} with given {@link LabelValue} value exists or not.
     *
     * @param value not-empty string with value
     * @return true if exists, false if not.
     */
    boolean existsByValue(String value);

    /**
     * Provides {@link LabelValue} by its value.
     *
     * @param value non-empty string with value.
     * @return {@link Optional} with found {@link LabelValue} object or {@link Optional#empty()}
     */
    Optional<LabelValue> findByValue(String value);

}