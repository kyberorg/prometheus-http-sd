package dev.kyberorg.httpsd.ui.pages.home;

import com.vaadin.flow.data.provider.ListDataProvider;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.db.models.Target;
import dev.kyberorg.httpsd.services.RecordService;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility class that encapsulates filtering and CRUD operations for
 * {@link Record} entities.
 * <p>
 * Used to simplify the code in {@link HomePage} and {@link HomePageLogic}.
 */
public class RecordDataProvider extends ListDataProvider<Record> {
    /** Text filter that can be changed separately. */
    private String filterText = "";

    public RecordDataProvider() {
        super(RecordService.get().getAllRecords());
    }

    /**
     * Store given record to the backing data service.
     *
     * @param record the updated or new record
     */
    public void save(final Record record) {
        final boolean newRecord = record.isNewRecord();

        RecordService.get().updateRecord(record);
        if (newRecord) {
            refreshAll();
        } else {
            refreshItem(record);
        }
    }

    /**
     * Delete given product from the backing data service.
     *
     * @param record the product to be deleted
     */
    public void delete(final Record record) {
        RecordService.get().deleteRecord(record.getId());
        refreshAll();
    }

    /**
     * Sets the filter to use for this data provider and refreshes data.
     * <p>
     * Filter is compared for record name, file, targets and labels.
     *
     * @param filterText the text to filter by, never null
     */
    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim().toLowerCase(Locale.ENGLISH);

        setFilter(record -> passesFilter(record.getName(), this.filterText)
                || passesFilter(record.getTargets(), this.filterText)
                || labelPassesFilter(record.getLabels(), this.filterText)
                || passesFilter(record.getFile().getFileName(), this.filterText)
                || passesFilter(record.getStatus().name(), this.filterText));
    }

    @Override
    public Long getId(final Record record) {
        Objects.requireNonNull(record, "Cannot provide an id for a null record.");
        return record.getId();
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }

    private boolean passesFilter(Collection<Target> targets, String filterText) {
        if (StringUtils.isBlank(filterText)) return false;
        if (targets == null || targets.isEmpty()) return false;
        for (Target t: targets) {
            if (t.getValue().toLowerCase(Locale.ENGLISH).contains(filterText)) return true;
        }
        return false;
    }

    private boolean labelPassesFilter(Collection<Label> labels, String filterText) {
        if (StringUtils.isBlank(filterText)) return false;
        if (labels == null || labels.isEmpty()) return false;
        for (Label l: labels) {
            boolean hasLabel = l.getLabelKey() != null;
            if (hasLabel && l.getLabelKey().getValue().toLowerCase(Locale.ENGLISH).contains(filterText)) return true;
            boolean hasValue = l.getLabelValue() != null;
            if (hasValue && l.getLabelValue().getValue().toLowerCase(Locale.ENGLISH).contains(filterText)) return true;

            if (hasLabel && hasValue) {
                if (l.toString().toLowerCase(Locale.ENGLISH).contains(filterText)) return true;
            }
        }
        return false;
    }
}
