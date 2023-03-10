package dev.kyberorg.httpsd.ui.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dev.kyberorg.httpsd.db.models.Label;
import dev.kyberorg.httpsd.ui.elements.LabelField;
import lombok.Getter;

/**
 * Layout for {@link LabelField} and delete {@link Label} {@link Button}.
 */
public class LabelLayout extends HorizontalLayout {
     @Getter private final LabelField labelField = new LabelField();
     @Getter private final Button deleteLabelButton = new Button(VaadinIcon.TRASH.create());

    /**
     * Creates {@link LabelLayout}.
     */
    public LabelLayout() {
         createLayout();
     }

    /**
     * Creates {@link LabelLayout} and sets {@link Label} to {@link LabelField}.
     *
     * @param label {@link Label} to set.
     */
     public LabelLayout(final Label label) {
         createLayout();
         labelField.setValue(label);
     }

     private void createLayout() {
         deleteLabelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
         deleteLabelButton.getElement().setAttribute("aria-label", "Delete Label");
         deleteLabelButton.setTooltipText("Delete the label");

         add(labelField, deleteLabelButton);
         deleteLabelButton.addClickListener(e -> this.setVisible(false));

         this.setWidthFull();
     }

}
