package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO for {@link File} table.
 */
@Repository
public interface FileDao extends CrudRepository<File, Long> {
    /**
     * Check if file exists in Database.
     *
     * @param fileName non-empty string with filename (without extension).
     * @return true if file found in database, false if not.
     */
    boolean existsByFileName(String fileName);

    /**
     * Provides {@link File} by its filename.
     *
     * @param fileName non-empty string with filename (without extension).
     * @return {@link Optional} with found {@link File} object or {@link Optional#empty()}
     */
    Optional<File> findByFileName(String fileName);
}