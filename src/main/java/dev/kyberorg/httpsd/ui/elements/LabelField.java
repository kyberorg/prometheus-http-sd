package dev.kyberorg.httpsd.ui.elements;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.customfield.CustomField;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.db.models.LabelKey;
import dev.kyberorg.httpsd.db.models.LabelValue;
import dev.kyberorg.httpsd.services.LabelService;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Field, that represents {@link Label}.
 */
public class LabelField extends CustomField<Label> {
    @Getter private final ComboBox<LabelKey> keyBox = new ComboBox<>();
    @Getter private final ComboBox<LabelValue> valueBox = new ComboBox<>();

    /**
     * Creates {@link LabelField}.
     */
    public LabelField() {
        keyBox.setClearButtonVisible(true);
        keyBox.setPlaceholder("Choose key");
        keyBox.setItemLabelGenerator(LabelKey::getValue);
        keyBox.setHelperText("Hit enter to add");
        keyBox.setItems(LabelService.get().getAllLabelKeys());
        keyBox.setAllowCustomValue(true);
        keyBox.addCustomValueSetListener(this::onNewKey);
        keyBox.addValueChangeListener(this::onKeySelected);

        valueBox.setClearButtonVisible(true);
        valueBox.setPlaceholder("Choose value");
        valueBox.setItemLabelGenerator(LabelValue::getValue);
        valueBox.setHelperText("Hit enter to add");
        valueBox.setAllowCustomValue(true);
        valueBox.addCustomValueSetListener(this::onNewValue);

        add(keyBox, new Text(" = "), valueBox);
    }

    /**
     * Transforms {@link LabelField} values to {@link Label} object.
     *
     * @return new or existing {@link Label} object with key and labels from {@link ComboBox}es.
     */
    public Label toLabel() {
        return generateModelValue();
    }

    /**
     * Controls if {@link LabelKey} and {@link LabelValue} values.
     *
     * @return true if both values are valid, false if not.
     */
    public boolean isValidLabel() {
        boolean valid = true;
        LabelKey labelKey = keyBox.getValue();
        if (labelKey == null || StringUtils.isBlank(labelKey.getValue())) {
            keyBox.setInvalid(true);
            keyBox.setErrorMessage("Label should have valid key");
            valid = false;
        } else {
            keyBox.setInvalid(false);
            keyBox.setErrorMessage("");
        }
        LabelValue labelValue = valueBox.getValue();
        if (labelValue == null || StringUtils.isBlank(labelValue.getValue())) {
            valueBox.setInvalid(true);
            valueBox.setErrorMessage("Label should have valid value");
            valid = false;
        } else {
            valueBox.setInvalid(false);
            valueBox.setErrorMessage("");
        }
        return valid;
    }

    @Override
    protected Label generateModelValue() {
        Label label;
        Optional<Label> existing = LabelService.get().getLabelByKeyAndValue(keyBox.getValue(), valueBox.getValue());
        if (existing.isPresent()) {
            label = existing.get();
        } else {
            label = new Label();
            label.setLabelKey(keyBox.getValue());
            label.setLabelValue(valueBox.getValue());
        }
        return label;
    }

    @Override
    protected void setPresentationValue(final Label label) {
        keyBox.setValue(label.getLabelKey());
        valueBox.setValue(label.getLabelValue());
    }

    private void onKeySelected(final ComponentValueChangeEvent<ComboBox<LabelKey>, LabelKey> e) {
        LabelKey selectedKey = e.getValue();
        valueBox.clear();
        valueBox.setItems(LabelService.get().getLabelValuesFor(selectedKey));
    }

    private void onNewKey(final ComboBoxBase.CustomValueSetEvent<ComboBox<LabelKey>> e) {
        String key = e.getDetail();
        try {
            if (!LabelService.get().isLabelKeyAlreadyExist(key)) {
                LabelService.get().createNewKey(key);
            }
            keyBox.setItems(LabelService.get().getAllLabelKeys());
            LabelService.get().getLabelKeyByValue(key).ifPresent(keyBox::setValue);
        } catch (Exception ex) {
            //TODO log it  and show error notification
        }
    }

    private void onNewValue(ComboBoxBase.CustomValueSetEvent<ComboBox<LabelValue>> e) {
        String value = e.getDetail();
        LabelValue labelValue = null;
        try {
            if (!LabelService.get().isLabelValueAlreadyExist(value)) {
                labelValue = LabelService.get().createNewValue(value);
            } else {
                Optional<LabelValue> existingLabelValue = LabelService.get().getLabelValueByValue(value);
                if (existingLabelValue.isPresent()) {
                    labelValue = existingLabelValue.get();
                }
            }
            List<LabelValue> values = LabelService.get().getLabelValuesFor(keyBox.getValue());
            if (labelValue != null) { values.add(labelValue); }
            valueBox.clear();
            valueBox.setItems(values);
            if (labelValue != null) { valueBox.setValue(labelValue); }
        } catch (Exception ex) {
            //TODO log it  and show error notification
        }
    }
}
