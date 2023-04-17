package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.File;
import dev.kyberorg.httpsd.db.models.Record;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO for {@link Record} table.
 */
@Repository
public interface RecordDao extends CrudRepository<Record, Long> {
    /**
     * Defines if {@link Record} with given name exists or not.
     *
     * @param name non-empty string with record name to check*
     * @return true - if name exists, false - if not.
     */
    boolean existsByName(String name);

    /**
     * Provides {@link Record} by its name.
     *
     * @param recordName non-empty string with name of record.
     * @return {@link Optional} with found {@link Record} object or {@link Optional#empty()}
     */
    Optional<Record> findRecordByName(String recordName);

    /**
     * Provides {@link List} or {@link Record}s bounded to {@link File} with given filename.
     *
     * @param fileName non-empty string with filename (without extension).
     * @return {@link List} with found {@link Record} objects or empty {@link List}. {@link List} is not modifiable.
     */
    List<Record> findByFile_FileName(String fileName);

}