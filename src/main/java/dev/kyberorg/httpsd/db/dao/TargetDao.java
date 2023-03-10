package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.Target;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO for {@link Target} table.
 */
@Repository
public interface TargetDao extends CrudRepository<Target, Long> {
    /**
     * Checks, if {@link Target} with given {@link Target} value exists or not.
     *
     * @param value not-empty string with target value
     * @return true if exists, false if not.
     */
    boolean existsByValue(String value);

    /**
     * Provides {@link Target} by its value.
     *
     * @param value non-empty string with value.
     * @return {@link Optional} with found {@link Target} object or {@link Optional#empty()}
     */
    Optional<Target> findByValue(String value);
}