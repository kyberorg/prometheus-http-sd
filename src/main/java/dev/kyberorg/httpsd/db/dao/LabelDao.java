package dev.kyberorg.httpsd.db.dao;

import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.LabelKey;
import dev.kyberorg.httpsd.db.models.LabelValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO for {@link Label} table.
 */
@Repository
public interface LabelDao extends CrudRepository<Label, Long> {
    /**
     * Find {@link Label}s by given {@link LabelKey}.
     *
     * @param labelKey non-empty {@link LabelKey} to search.
     * @return list of found {@link Label} or empty {@link List}. {@link List} should be modifiable.
     */
    List<Label> findDistinctByLabelKey(LabelKey labelKey);

    /**
     * Provides {@link Label} with given {@link LabelKey} and {@link LabelValue}.
     *
     * @param labelKey non-empty {@link LabelKey}
     * @param labelValue non-empty {@link LabelValue}
     * @return {@link Optional} with found {@link Label} object or {@link Optional#empty()}
     */
    Optional<Label> findByLabelKeyAndLabelValue(LabelKey labelKey, LabelValue labelValue);

}