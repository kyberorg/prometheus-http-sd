package dev.kyberorg.httpsd.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import dev.kyberorg.httpsd.internal.Callback;

/**
 * Dialog, that appears, that appears, when user tries to delete Record.
 */
public class DeleteConfirmationDialog extends Composite<Dialog> {
    private final H4 title = new H4("Confirm Delete");
    private final Span message = new Span("Are you sure? This action cannot be undone.");
    private final Button cancelButton = new Button("Cancel");
    private final Button deleteButton = new Button("Delete");

    private final Dialog dialog = getContent();
    private Registration defaultDeleteButtonAction;

    /**
     * Creates dialog. Created dialog ain't open automatically.
     *
     * @return created {@link DeleteConfirmationDialog} for further modification.
     */
    public static DeleteConfirmationDialog create() {
        return new DeleteConfirmationDialog();
    }

    /**
     * Sets action performed when user pressed Delete Button.
     *
     * @param callback callable method in lambda.
     * @return same {@link DeleteConfirmationDialog} for chaining.
     */
    public DeleteConfirmationDialog setDeleteButtonAction(final Callback callback) {
        defaultDeleteButtonAction.remove();
        this.deleteButton.addClickListener(e -> {
            callback.execute();
            dialog.close();
        });
        return this;
    }

    /**
     * Displays dialog.
     */
    public void show() {
        this.dialog.open();
    }

    private DeleteConfirmationDialog() {
        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);
    }

    private VerticalLayout createDialogLayout() {
        //by default - no action
        cancelButton.addClickListener(e -> dialog.close());
        defaultDeleteButtonAction = deleteButton.addClickListener(e -> dialog.close());

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(title, message, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.setClassName("delete-dialog");

        return dialogLayout;
    }
}
