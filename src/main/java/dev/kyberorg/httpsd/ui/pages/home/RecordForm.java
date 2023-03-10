package dev.kyberorg.httpsd.ui.pages.home;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import dev.kyberorg.httpsd.db.converters.StatusConverter;
import dev.kyberorg.httpsd.db.models.File;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.Record;
import dev.kyberorg.httpsd.db.models.Target;
import dev.kyberorg.httpsd.services.RecordService;
import dev.kyberorg.httpsd.ui.AppUtils;
import dev.kyberorg.httpsd.ui.elements.DeleteConfirmationDialog;
import dev.kyberorg.httpsd.ui.elements.FileComboBox;
import dev.kyberorg.httpsd.ui.elements.LabelField;
import dev.kyberorg.httpsd.ui.layouts.LabelLayout;
import dev.kyberorg.httpsd.ui.layouts.TargetLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Comparator;

/**
 * A form for editing a single record.
 */
@Slf4j
public class RecordForm extends Div {
    private final H4 formTitle;
    private final TextField recordName;
    private final FileComboBox recordFileName;
    private final Select<Record.Status> recordStatus;

    private final Button delete;

    private final HomePageLogic pageLogic;
    private final Collection<File> allFiles;
    private final Binder<Record> binder;
    private Record currentRecord;

    private final VerticalLayout targetFields;

    private final VerticalLayout labelFields;

    public RecordForm(final HomePageLogic pageLogic) {
        this.pageLogic = pageLogic;
        allFiles = pageLogic.getAllFiles();

        setClassName("record-form");

        VerticalLayout content = new VerticalLayout();
        content.setSizeUndefined();
        content.addClassName("record-form-content");
        add(content);

        formTitle = new H4();
        formTitle.setClassName("record-form-title");

        content.add(formTitle);

        recordName = new TextField("Record name");
        recordName.setWidth("100%");
        recordName.setRequired(false);
        recordName.setClearButtonVisible(true);
        recordName.setValueChangeMode(ValueChangeMode.EAGER);
        recordName.setHelperText("Add Record name for self-reference. This is not included to JSON file(s)");

        content.add(recordName);

        recordFileName = new FileComboBox("Record Filename");
        recordFileName.getComboBox().setItems(allFiles);
        recordFileName.getComboBox().addCustomValueSetListener(this::onNewFileName);
        recordFileName.setWidth("100%");
        recordFileName.getComboBox().setRequired(true);


        content.add(recordFileName);

        recordStatus = new Select<>();
        recordStatus.setLabel("Status");
        recordStatus.setWidth("100%");
        recordStatus.setItems(Record.Status.values());
        recordStatus.setHelperText("Active - included to JSON, Disabled - not included");
        content.add(recordStatus);

        Hr firstLine = new Hr();
        content.add(firstLine);

        VerticalLayout targetContent = new VerticalLayout();
        com.vaadin.flow.component.html.Label targetLabel = new com.vaadin.flow.component.html.Label("Targets");
        targetContent.setId("targets");
        targetContent.setPadding(false);
        targetFields = new VerticalLayout();
        targetFields.setId("target-fields");
        targetFields.setPadding(false);
        Button addTargetButton = new Button("Add Target");
        addTargetButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addTargetButton.addClickListener(e -> this.addNewTarget());

        targetContent.removeAll();
        targetContent.add(targetLabel);
        targetContent.add(targetFields);
        targetContent.add(addTargetButton);

        content.add(targetContent);

        Hr secondLine = new Hr();
        content.add(secondLine);

        VerticalLayout labelsContent = new VerticalLayout();
        com.vaadin.flow.component.html.Label labelsLabel = new com.vaadin.flow.component.html.Label("Labels");
        labelsContent.setId("labels");
        labelsContent.setPadding(false);
        labelFields = new VerticalLayout();
        labelFields.setId("label-fields");
        labelFields.setPadding(false);

        labelsContent.removeAll();
        labelsContent.add(labelsLabel);
        labelsContent.add(labelFields);
        Button addNewLabel = new Button("Add label");
        addNewLabel.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addNewLabel.addClickListener(e -> this.addNewLabel());
        labelsContent.add(addNewLabel);

        content.add(labelsContent);

        binder = new BeanValidationBinder<>(Record.class);
        binder.forField(recordName).bind("name");
        binder.forField(recordFileName.getComboBox()).bind("file");
        binder.forField(recordStatus).withConverter(new StatusConverter()).bind("active");
        binder.bindInstanceFields(this);

        Hr thirdLine = new Hr();
        content.add(thirdLine);

        Button save = new Button("Save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentRecord != null
                    && binder.writeBeanIfValid(currentRecord)
                    && recordNameIsValue(currentRecord)
                    && targetsAreValid(currentRecord)
                    && labelsAreValid(currentRecord)
            ) {
                addTargetsToRecord(currentRecord);
                addLabelsToRecord(currentRecord);
                pageLogic.saveRecord(currentRecord);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        Button cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> pageLogic.cancelRecord());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> pageLogic.cancelRecord())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);

        delete.addClickListener(e ->
                DeleteConfirmationDialog.create().setDeleteButtonAction(this::onRecordDelete).show()
        );

        content.add(save, delete, cancel);

    }

    private void onNewFileName(final ComboBoxBase.CustomValueSetEvent<ComboBox<File>> e) {
        String newValue = e.getDetail();
        String err = pageLogic.newFile(newValue);
        if (StringUtils.isBlank(err)) {
            recordFileName.getComboBox().setItems(pageLogic.getAllFiles());
            recordFileName.getComboBox().setValue(pageLogic.getFile(newValue));
            AppUtils.showSuccessNotification(String.format("File '%s' added", newValue));
        } else {
            AppUtils.showFailNotification(err);
        }
    }

    private boolean recordNameIsValue(final Record currentRecord) {
        if (currentRecord == null) return false;
        if (StringUtils.isBlank(currentRecord.getName())) {
            recordName.setInvalid(true);
            recordName.setErrorMessage("Name cannot be null");
            return false;
        }
        if (RecordService.get().isRecordWithThisNameExists(currentRecord.getName().trim())) {
            recordName.setInvalid(true);
            recordName.setErrorMessage("Name already exists");
            return false;
        }
        recordName.setInvalid(false);
        recordName.setErrorMessage("");
        return true;
    }

    private boolean targetsAreValid(final Record currentRecord) {
        if (currentRecord == null) return false;
        boolean targetsAreValid = true;
        for (Component c: targetFields.getChildren().toList()) {
            if (c instanceof TargetLayout && c.isVisible()) {
                targetsAreValid = ((TargetLayout) c).isValidTarget();
                if (!targetsAreValid) {
                    //fall fast
                    break;
                }
            }
        }
        return targetsAreValid;
    }

    public boolean labelsAreValid(final Record currentRecord) {
        if (currentRecord == null) return false;
        boolean labelsAreValid = true;
        for (Component c: labelFields.getChildren().toList()) {
            if (c instanceof LabelLayout && c.isVisible()) {
                LabelField labelField = ((LabelLayout) c).getLabelField();
                labelsAreValid = labelField.isValidLabel();
                if (!labelsAreValid) {
                    //fall fast
                    break;
                }
            }
        }
        return labelsAreValid;
    }

    private void addTargetsToRecord(final Record currentRecord) {
        if (currentRecord == null) return;
        //removing current ones - to keep list actual
        currentRecord.getTargets().clear();
        //populating with actual values
        targetFields.getChildren().forEach(component -> {
            if (component instanceof TargetLayout && component.isVisible()) {
                ComboBox<Target> targetField = ((TargetLayout) component).getTargetField();
                Target target = targetField.getValue();
                if(target != null) {
                    currentRecord.getTargets().add(target);
                }
            }
        });
    }



    public void addLabelsToRecord(final Record currentRecord) {
        if (currentRecord == null) return;
        //removing current ones - to keep list actual
        currentRecord.getLabels().clear();

        labelFields.getChildren().forEach(component -> {
            if (component instanceof LabelLayout && component.isVisible()) {
                LabelField labelField = ((LabelLayout) component).getLabelField();
                Label label = labelField.toLabel();
                try {
                    label = pageLogic.saveLabel(label);
                } catch (final Exception e) {
                    AppUtils.showLessImportantNotification("Failed to save label", false);
                }
                if (label != null) {
                    pageLogic.addLabelToRecord(currentRecord, label);
                }
            }
        });
    }

    private void setDefaultsForNewRecord() {
        recordName.setValue(RecordService.getRecordName(null));
        recordFileName.getComboBox()
                .setValue(allFiles.stream().sorted(Comparator.comparing(File::getId)).toList().get(0));
        recordStatus.setValue(Record.Status.ACTIVE);
    }

    public void editRecord(Record record) {
        boolean isNewRecord = false;
        if (record == null || record.getName() == null) {
            record = new Record();
            isNewRecord = true;
        }
        delete.setVisible((!record.isNewRecord()));
        currentRecord = record;
        binder.readBean(record);
        if (isNewRecord) {
            formTitle.setText("Add new Record");
            //set Defaults for new Records
            setDefaultsForNewRecord();
        } else {
            formTitle.setText("Edit Record");
        }

        setTargets();
        setLabels();
    }

    private void setTargets() {
        targetFields.removeAll();
        if (currentRecord.getTargets().isEmpty()) {
            addNewTarget();
        } else {
            for (Target t: currentRecord.getTargets()) {
                targetFields.add(new TargetLayout(t));
            }
        }
    }

    private void setLabels() {
        labelFields.removeAll();
        if (currentRecord.getLabels().isEmpty()) {
            addNewLabel();
        } else {
            for (Label l: currentRecord.getLabels()) {
                labelFields.add(new LabelLayout(l));
            }
        }
    }

    private void addNewTarget() {
        targetFields.add(new TargetLayout());
    }

    private void addNewLabel() {
        labelFields.add(new LabelLayout());
    }

    private void onRecordDelete() {
        if (currentRecord != null) {
            pageLogic.deleteRecord(currentRecord);
        }
    }

}
