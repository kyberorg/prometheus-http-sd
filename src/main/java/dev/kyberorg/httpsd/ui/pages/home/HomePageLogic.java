package dev.kyberorg.httpsd.ui.pages.home;

import com.vaadin.flow.component.UI;
import dev.kyberorg.httpsd.db.models.File;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.services.FileService;
import dev.kyberorg.httpsd.services.LabelService;
import dev.kyberorg.httpsd.services.RecordService;
import dev.kyberorg.httpsd.ui.AppUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Logic for {@link HomePage}.
 */
@Slf4j
public class HomePageLogic implements Serializable {
    private final RecordService recordService = RecordService.get();
    @Getter private final HomePage homePage;

    /**
     * Creates {@link HomePageLogic}.
     *
     * @param homePage {@link HomePage} linked with this {@link HomePageLogic}.
     */
    public HomePageLogic(final HomePage homePage) {
        this.homePage = homePage;
    }

    /**
     * Cancel editing {@link Record}.
     */
    public void cancelRecord() {
        setFragmentParameter("");
        homePage.clearSelection();
    }

    /**
     * Saves new {@link Record}.
     *
     * @param record record object to save.
     */
    public void saveRecord(final Record record) {
        if (StringUtils.isBlank(record.getName())) {
            record.setName(RecordService.getRecordName(record));
        }
        homePage.clearSelection();
        homePage.updateRecord(record);
        setFragmentParameter("");
        AppUtils.showSuccessNotification(record.getName() + " created/updated");

        homePage.refreshGrid();
    }

    /**
     * Deletes {@link Record}.
     *
     * @param record record to delete
     */
    public void deleteRecord(final Record record) {
        homePage.clearSelection();
        homePage.removeProduct(record);
        setFragmentParameter("");
        AppUtils.showSuccessNotification(record.getName() + " removed");

        homePage.refreshGrid();
    }

    public void editRecord(final Record record) {
        if (record == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(record.getId() + "");
        }
        homePage.editRecord(record);
    }

    public void newRecord() {
        homePage.clearSelection();
        setFragmentParameter("new");
        homePage.editRecord(new Record());
    }

    public void rowSelected(final Record record) {
        editRecord(record);
    }

    public Collection<File> getAllFiles() {
        return FileService.get().getAllFiles();
    }

    @Nullable
    public File getFile(final String fileName) {
        Optional<File> file = FileService.get().getFileByName(fileName);
        return file.orElse(null);
    }

    public String newFile(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "filename cannot be empty";
        }
        if (fileName.contains(".")) {
            return "filename should be without extension";
        }
        boolean sameFilenameAlreadyExists = FileService.get().isAlreadyExists(fileName);
        if (sameFilenameAlreadyExists) {
            return "file with this name already exists";
        }
        try {
            FileService.get().createNew(fileName);
            return "";
        } catch (Exception e) {
            log.error("failed to save new File. Got exception: {}", e.getMessage());
            return "failed to save new file";
        }
    }

    public Label saveLabel(final Label label) {
        if (label == null) throw new IllegalArgumentException("Label cannot be null");
        if (label.isNewLabel()) {
            return LabelService.get().saveLabel(label);
        } else {
            //compare to DB record and create new one - old should be untouched
            Optional<Label> labelFromDb = LabelService.get().getLabelById(label.getId());
            if (labelFromDb.isPresent()) {
                boolean isSameKey = label.getLabelKey().equals(labelFromDb.get().getLabelKey());
                boolean isSameValue = label.getLabelValue().equals(labelFromDb.get().getLabelValue());
                if (isSameKey && isSameValue) {
                    return label;  //no changes - no action needed
                } else {
                    //save as new record
                    label.setId(null);
                    return LabelService.get().saveLabel(label);
                }
            } else {
                return LabelService.get().saveLabel(label);
            }
        }
    }

    public void addLabelToRecord(final Record currentRecord, final Label label) {
        if (currentRecord == null) throw new IllegalArgumentException("Current Record is null");
        if (label == null) throw new IllegalArgumentException("Cannot add null Label");

        Label labelWithSameKey = null;
        for (Label existingLabel : currentRecord.getLabels()) {
            if (Objects.equals(existingLabel.getLabelKey().getValue(),
                    label.getLabelKey().getValue())) {
                labelWithSameKey = existingLabel;
            }
        }
        boolean isLabelWithSameKeyExist = labelWithSameKey != null;
        if (isLabelWithSameKeyExist) {
            //removing conflicting label first - this should prevent double labeling
            currentRecord.getLabels().remove(labelWithSameKey);
            currentRecord.getLabels().add(label);
        } else {
            //no conflicts - just add it
            currentRecord.getLabels().add(label);
        }
    }

    /**
     * Updates the fragment without causing HomePageLogic navigator to
     * change view. It actually appends the recordId as a parameter to the URL.
     * The parameter is set to keep the view state the same during e.g. a
     * refresh and to enable bookmarking of individual records selections.
     *
     */
    private void setFragmentParameter(final String recordId) {
        String fragmentParameter;
        if (recordId == null || recordId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recordId;
        }

        UI.getCurrent().navigate(HomePage.class, fragmentParameter);
    }

    /**
     * Opens the record form and clears its fields to make it ready for
     * entering a new record if recordId is null, otherwise loads the record
     * with the given recordId and shows its data in the form fields so the
     * user can edit them.
     *
     *
     * @param recordId unique number that identifies Record
     */
    public void enter(final String recordId) {
        if (recordId != null && !recordId.isEmpty()) {
            if (recordId.equals("new")) {
                newRecord();
            } else {
                try {
                    final int recId = Integer.parseInt(recordId);
                    final Optional<Record> record = findRecord(recId);
                    record.ifPresent(homePage::selectRow);
                } catch (final NumberFormatException e) {
                    //TODO handle
                }
            }
        } else {
            homePage.showForm(false);
        }
    }

    private Optional<Record> findRecord(final long recordId) {
        return recordService.getRecordById(recordId);
    }
}
