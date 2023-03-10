package dev.kyberorg.httpsd.ui.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dev.kyberorg.httpsd.db.models.Target;
import dev.kyberorg.httpsd.services.TargetService;
import dev.kyberorg.httpsd.ui.AppUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Layout for {@link Target} ComboBox and {@link Target} delete button.
 */
@Slf4j
public class TargetLayout extends HorizontalLayout {
     @Getter private final ComboBox<Target> targetField = new ComboBox<>();
     @Getter private final Button deleteTargetButton = new Button(VaadinIcon.TRASH.create());

    /**
     * Creates {@link TargetLayout}.
     */
    public TargetLayout() {
         createLayout();
     }

    /**
     * Creates {@link TargetLayout} and set {@link Target}.
     *
     * @param target non-empty {@link Target} object
     */
     public TargetLayout(final Target target) {
         createLayout();
         targetField.setValue(target);
     }

    /**
     * Controls, if stored {@link Target} valid or not.
     *
     * @return true - if {@link Target} valid, false if not.
     */
    public boolean isValidTarget() {
        if (targetField.getValue() == null) {
            targetField.setInvalid(true);
            targetField.setErrorMessage("Target cannot be empty");
            return false;
        } else {
            targetField.setInvalid(false);
            targetField.setErrorMessage("");
            return true;
        }
    }

     private void createLayout() {
         targetField.setItemLabelGenerator(Target::getValue);
         targetField.setClearButtonVisible(true);
         targetField.setHelperText("Select existing or type to add new target");
         targetField.setWidth("100%");
         targetField.setRequired(true);
         targetField.setPlaceholder("ex. localhost:9090");
         targetField.setItems(TargetService.get().getAllTargets());
         targetField.setAllowCustomValue(true);
         targetField.addCustomValueSetListener(this::onNewTarget);

         deleteTargetButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
         deleteTargetButton.getElement().setAttribute("aria-label", "Delete Target");
         deleteTargetButton.setTooltipText("Delete the target");

         add(targetField, deleteTargetButton);
         deleteTargetButton.addClickListener(e -> this.setVisible(false));

         this.setWidthFull();
     }

    private void onNewTarget(final ComboBoxBase.CustomValueSetEvent<ComboBox<Target>> e) {
        String newValue = e.getDetail();
        try {
            if (!TargetService.get().isAlreadyExists(newValue)) {
                TargetService.get().createNewTarget(newValue);
            }
            targetField.setItems(TargetService.get().getAllTargets());
            TargetService.get().getTargetByValue(newValue).ifPresent(targetField::setValue);
            AppUtils.showLessImportantNotification(String.format("New Target '%s' added", newValue), true);
        } catch (Exception ex) {
           log.error("Failed to add Target. Got exception: " + ex.getMessage());
            AppUtils.showLessImportantNotification("Failed to add new target", false);
        }
    }

}
