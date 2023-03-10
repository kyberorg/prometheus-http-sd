package dev.kyberorg.httpsd.services;

import dev.kyberorg.httpsd.db.dao.FileDao;
import dev.kyberorg.httpsd.db.models.File;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service, for {@link File}-related operations.
 */
@Service
public class FileService {
    private static FileService self;

    private final FileDao fileDao;

    /**
     * Provides {@link FileService} to non-Spring objects.
     *
     * @return this {@link FileService}.
     */
    public static FileService get() {
        return self;
    }

    /**
     * Creates {@link FileService}. Should be called by Spring itself, not intended to use directly.
     *
     * @param fileDao {@link FileDao} implementation.
     */
    public FileService(final FileDao fileDao) {
        this.fileDao = fileDao;
        self = this;
    }

    /**
     * Defines, if {@link File} with given filename exists or not.
     *
     * @param fileName non-empty string with filename.
     * @return true if file exists, false if not.
     */
    public boolean isAlreadyExists(final String fileName) {
        if (StringUtils.isBlank(fileName)) return false;
        return fileDao.existsByFileName(fileName.trim());
    }

    /**
     * Alias for {@link #isAlreadyExists(String)} to improve readability.
     *
     * @param fileName non-empty string with filename.
     * @return true if file exists, false if not.
     */
    public boolean isFileExists(final String fileName) {
        return isAlreadyExists(fileName);
    }

    /**
     * Provides all {@link File}s stored in Database.
     *
     * @return {@link List} of {@link File}s. {@link List} is not modifiable.
     */
    public Collection<File> getAllFiles() {
        return IterableUtils.toList(fileDao.findAll());
    }

    /**
     * Gets {@link File} by its filename.
     *
     * @param fileName non-empty string with filename.
     * @return {@link Optional} with found {@link File} or {@link Optional#empty()}.
     */
    public Optional<File> getFileByName(final String fileName) {
        if (StringUtils.isBlank(fileName)) return Optional.empty();
        return fileDao.findByFileName(fileName);
    }

    /**
     * Creates new {@link File} and saves it to Database.
     *
     * @param fileName non-empty string with filename.
     */
    public void createNew(final String fileName) {
        if (StringUtils.isBlank(fileName)) throw new IllegalArgumentException("file cannot be null");
        File file = new File();
        file.setFileName(fileName.trim());
        fileDao.save(file);
    }
}
