package dev.kyberorg.httpsd.services;

import dev.kyberorg.httpsd.db.dao.RecordDao;
import dev.kyberorg.httpsd.db.models.File;
import dev.kyberorg.httpsd.db.models.Record;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service, for {@link Record}-related operations.
 */
@Service
public class RecordService {
    private static RecordService self;

    private final RecordDao recordDao;

    /**
     * Provides {@link RecordService} to non-Spring objects.
     *
     * @return this {@link FileService}.
     */
    public static RecordService get() {
        return self;
    }

    /**
     * Creates {@link RecordService}. Should be called by Spring itself, not intended to use directly.
     *
     * @param recordDao {@link RecordDao} implementation.
     */
    public RecordService(final RecordDao recordDao) {
        this.recordDao = recordDao;
        self = this;
    }

    /**
     * Check if {@link Record} with this name exists in Database or not.
     *
     * @param recordName non-empty string with record name.
     *
     * @return true if {@link Record} with this name exists, false if not.
     */
    public boolean isRecordWithThisNameExists(final String recordName) {
        if (StringUtils.isBlank(recordName)) return false;
        return recordDao.existsByName(recordName);
    }

    /**
     * Gets {@link Record} by its name.
     *
     * @param recordName non-empty string with record name.
     * @return {@link Optional} with found {@link Record} or {@link Optional#empty()}.
     */
    public Optional<Record> getRecordByName(final String recordName) {
        if (StringUtils.isBlank(recordName)) return Optional.empty();
        return recordDao.findRecordByName(recordName);
    }

    /**
     * Gets {@link Record} name.
     *
     * @param record non-empty {@link Record} record.
     * @return {@link Record#getName()} if {@link Record} has name,
     *         {@literal Record #<id>} if {@link Record} has id,
     *         {@literal New Record} if none of above.
     */
    public static String getRecordName(final Record record) {
        if (record == null) return "New Record";
        boolean hasId = record.getId() >= 0;
        boolean hasName = StringUtils.isNotBlank(record.getName());

        if (hasName) {
            return record.getName();
        } else if (hasId) {
            return "Record #" + record.getId();
        } else {
            return "New Record";
        }
    }

    /**
     * Get {@link Record} by its id.
     *
     * @param recordId non-negative {@link Record}'s id.
     * @return {@link Optional} with found {@link Record} or {@link Optional#empty()}.
     */
    public Optional<Record> getRecordById(final long recordId) {
        if (recordId < 0) return Optional.empty();
        return recordDao.findById(recordId);
    }

    /**
     * Get all {@link Record}s from Database.
     *
     * @return {@link List} or all {@link Record}s stored in database. {@link List} is not modifiable.
     */
    public Collection<Record> getAllRecords() {
        return IterableUtils.toList(recordDao.findAll());
    }

    /**
     * Gets Records included to given {@link File} with given filename.
     *
     * @param fileName non-empty string with filename.
     *
     * @return {@link Collection} of {@link Record}s included to given file or {@link Collections#emptyList()}.
     */
    public Collection<Record> getRecordsByFile(final String fileName) {
        if (StringUtils.isBlank(fileName)) return Collections.emptyList();
        return recordDao.findByFile_FileName(fileName.trim());
    }

    /**
     * Saves existing {@link Record} to database.
     *
     * @param record non-empty {@link Record} object to update.
     * @throws IllegalArgumentException when {@link Record} is {@code null}
     */
    public void updateRecord(final Record record) {
        if (record == null) throw new IllegalArgumentException("Record cannot be null");
        recordDao.save(record);
    }

    /**
     * Delete {@link Record} from database.
     *
     * @param id non-negative id of {@link Record} to delete.
     */
    public void deleteRecord(final long id) {
        if (id < 0) throw new IllegalArgumentException("ID cannot be negative");
        recordDao.deleteById(id);
    }
}
