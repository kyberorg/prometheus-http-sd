package dev.kyberorg.httpsd.services;

import dev.kyberorg.httpsd.db.dao.LabelDao;
import dev.kyberorg.httpsd.db.dao.LabelKeyDao;
import dev.kyberorg.httpsd.db.dao.LabelValueDao;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.LabelKey;
import dev.kyberorg.httpsd.db.models.LabelValue;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service, for {@link Label}-related operations.
 * It also operates with {@link LabelKey} and {@link LabelValue} models.
 */
@Service
public class LabelService {
    private static LabelService self;

    private final LabelKeyDao labelKeyDao;
    private final LabelValueDao labelValueDao;
    private final LabelDao labelDao;

    /**
     * Provides {@link LabelService} to non-Spring objects.
     *
     * @return this {@link LabelService}.
     */
    public static LabelService get() {
        return self;
    }

    /**
     * Creates {@link LabelService}. Should be called by Spring itself, not intended to use directly.
     *
     * @param labelKeyDao {@link LabelKeyDao} implementation.
     * @param labelValueDao {@link LabelValueDao} implementation.
     * @param labelDao {@link LabelDao} implementation.
     */
    public LabelService(final LabelKeyDao labelKeyDao, final LabelValueDao labelValueDao, final LabelDao labelDao) {
        this.labelKeyDao = labelKeyDao;
        this.labelValueDao = labelValueDao;
        this.labelDao = labelDao;

        self = this;
    }

    /**
     * Defines, if {@link LabelKey} with given value exists or not.
     *
     * @param labelKey non-empty string with label key.
     * @return true if {@link LabelKey} exists, false if not.
     */
    public boolean isLabelKeyAlreadyExist(final String labelKey) {
        if (StringUtils.isBlank(labelKey)) return false;
        return labelKeyDao.existsByValue(labelKey.trim());
    }

    /**
     * Defines, if {@link LabelValue} with given value exists or not.
     *
     * @param labelValue non-empty string with label value.
     * @return true if {@link LabelValue} exists, false if not.
     */
    public boolean isLabelValueAlreadyExist(final String labelValue) {
        if (StringUtils.isBlank(labelValue)) return false;
        return labelValueDao.existsByValue(labelValue.trim());
    }

    /**
     * Provides all {@link LabelKey}s stored in Database.
     *
     * @return {@link List} of {@link LabelKey}s. {@link List} is not modifiable.
     */
    public Collection<LabelKey> getAllLabelKeys() {
        return IterableUtils.toList(labelKeyDao.findAll());
    }

    /**
     * Gets {@link LabelKey} by its value.
     *
     * @param labelKey non-empty string with value.
     * @return {@link Optional} with found {@link LabelKey} or {@link Optional#empty()}.
     */
    public Optional<LabelKey> getLabelKeyByValue(final String labelKey) {
        if (StringUtils.isBlank(labelKey)) return Optional.empty();
        return labelKeyDao.findByValue(labelKey.trim());
    }

    /**
     * Gets {@link LabelValue} by its value.
     *
     * @param labelValue non-empty string with value.
     * @return {@link Optional} with found {@link LabelValue} or {@link Optional#empty()}.
     */
    public Optional<LabelValue> getLabelValueByValue(final String labelValue) {
        if (StringUtils.isBlank(labelValue)) return Optional.empty();
        return labelValueDao.findByValue(labelValue.trim());
    }

    /**
     * Gets {@link Label} by its id.
     *
     * @param labelId {@link Label}'s id
     * @return {@link Optional} with found {@link Label} or {@link Optional#empty()}.
     */
    public Optional<Label> getLabelById(final long labelId) {
        if (labelId < 0) return Optional.empty();
        return labelDao.findById(labelId);
    }

    /**
     * Gets {@link LabelValue}s connected with given {@link LabelKey}.
     *
     * @param labelKey non-empty {@link LabelKey}
     * @return {@link List} with {@link LabelValue}s, that are used with given {@link LabelKey}.
     *   {@link List} is modifiable.
     */
    public List<LabelValue> getLabelValuesFor(final LabelKey labelKey) {
        if (labelKey == null || StringUtils.isBlank(labelKey.getValue())) {
            return Lists.newArrayList();
        }
        List<Label> foundLabels = labelDao.findDistinctByLabelKey(labelKey);
        if (foundLabels.isEmpty()) return Lists.newArrayList();

        return foundLabels.stream().map(Label::getLabelValue).collect(Collectors.toList());
    }

    /**
     * Gets {@link Label} object with given {@link LabelKey} and {@link LabelValue}.
     *
     * @param labelKey non-empty {@link LabelKey}
     * @param labelValue non-empty {@link LabelValue}
     * @return {@link Optional} with found {@link Label} or {@link Optional#empty()}.
     */
    public Optional<Label> getLabelByKeyAndValue(final LabelKey labelKey, final LabelValue labelValue) {
        if (labelKey == null) return Optional.empty();
        if (labelValue == null) return Optional.empty();
        return labelDao.findByLabelKeyAndLabelValue(labelKey, labelValue);
    }

    /**
     * Creates new {@link LabelKey} and saves it to Database.
     *
     * @param labelKeyString non-empty string with label key.
     * @throws IllegalStateException when key is blank.
     */
    public void createNewKey(final String labelKeyString) {
        if (StringUtils.isBlank(labelKeyString)) throw new IllegalArgumentException("Key cannot be null");
        LabelKey labelKey = new LabelKey();
        labelKey.setValue(labelKeyString.trim());
        labelKeyDao.save(labelKey);
    }

    /**
     * Creates new {@link LabelValue} and saves it to Database.
     *
     * @param labelValueString non-empty string with label value.
     * @throws IllegalStateException when provided label value is blank.
     */
    public LabelValue createNewValue(final String labelValueString) {
        if (StringUtils.isBlank(labelValueString)) throw new IllegalArgumentException("Value cannot be null");
        LabelValue labelValue = new LabelValue();
        labelValue.setValue(labelValueString.trim());
        return labelValueDao.save(labelValue);
    }

    /**
     * Creates new {@link Label} and saves it to Database.
     *
     * @param label non-empty {@link Label} object.
     * @throws IllegalStateException when provided {@link Label} object is {@code null}.
     */
    public Label saveLabel(final Label label) {
        if (label == null) throw new IllegalArgumentException("Label cannot be null");
        return labelDao.save(label);
    }
}
