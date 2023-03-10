package dev.kyberorg.httpsd.services;

import dev.kyberorg.httpsd.db.dao.TargetDao;
import dev.kyberorg.httpsd.db.models.Target;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service, for {@link Target}-related operations.
 */
@Service
public class TargetService {
    private static TargetService self;

    private final TargetDao targetDao;

    /**
     * Provides {@link TargetService} to non-Spring objects.
     *
     * @return this {@link TargetService}.
     */
    public static TargetService get() {
        return self;
    }

    /**
     * Creates {@link TargetService}. Should be called by Spring itself, not intended to use directly.
     *
     * @param targetDao {@link TargetDao} implementation.
     */
    public TargetService(final TargetDao targetDao) {
        this.targetDao = targetDao;
        self = this;
    }

    /**
     * Defines, if {@link Target} with given targetValue exists or not.
     *
     * @param targetValue non-empty string with target value.
     * @return true if {@link Target} exists, false if not.
     */
    public boolean isAlreadyExists(final String targetValue) {
        if (StringUtils.isBlank(targetValue)) return false;
        return targetDao.existsByValue(targetValue.trim());
    }

    /**
     * Provides all {@link Target}s stored in Database.
     *
     * @return {@link List} of {@link Target}s. {@link Target} is not modifiable.
     */
    public Collection<Target> getAllTargets() {
        return IterableUtils.toList(targetDao.findAll());
    }

    /**
     * Gets {@link Target} by its value.
     *
     * @param targetValue non-empty string with target value.
     * @return {@link Optional} with found {@link Target} or {@link Optional#empty()}.
     */
    public Optional<Target> getTargetByValue(final String targetValue) {
        if (StringUtils.isBlank(targetValue)) return Optional.empty();
        return targetDao.findByValue(targetValue.trim());
    }

    /**
     * Creates new {@link Target} and saves it to Database.
     *
     * @param targetValue non-empty string with target value.
     */
    public void createNewTarget(final String targetValue) {
        if (StringUtils.isBlank(targetValue)) throw new IllegalArgumentException("Target value cannot be null");
        Target target = new Target();
        target.setValue(targetValue.trim());
        targetDao.save(target);
    }
}
