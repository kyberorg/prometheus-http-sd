package dev.kyberorg.httpsd.ui.elements;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dev.kyberorg.httpsd.db.models.File;

/**
 * ComboBox for selecting {@link File} and {@link Text} element with ".json" string.
 */
public class FileComboBox extends CustomField<File> {

    private final ComboBox<File> fileComboBox = new ComboBox<>();

    public FileComboBox(final String label) {
        fileComboBox.setLabel(label);
        fileComboBox.setClearButtonVisible(true);
        fileComboBox.setHelperText("Select existing or type to add new file. Filename (without extension)");

        fileComboBox.setItemLabelGenerator(File::getFileName);
        fileComboBox.setAllowCustomValue(true);

        HorizontalLayout layout = new HorizontalLayout(fileComboBox, new Text(".json"));
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.setWidthFull();
        layout.expand(fileComboBox);
        add(layout);
    }

    public ComboBox<File> getComboBox() {
        return fileComboBox;
    }

    @Override
    protected File generateModelValue() {
        return fileComboBox.getValue();
    }

    @Override
    protected void setPresentationValue(final File file) {
        fileComboBox.setValue(file);
    }
}
