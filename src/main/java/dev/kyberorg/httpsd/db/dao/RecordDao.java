package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.File;
import dev.kyberorg.httpsd.db.models.Record;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * Provides {@link List} or {@link Record}s bounded to {@link File} with given filename.
     *
     * @param fileName non-empty string with filename (without extension).
     * @return {@link List} with found {@link Record} objects or empty {@link List}. {@link List} is not modifiable.
     */
    List<Record> findByFile_FileName(String fileName);

}