package dev.kyberorg.httpsd.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Utility class that encapsulates method, that repeats at least 2 times.
 */
public class AppUtils {
    private static final int NOTIFICATION_DURATION_MILLIS = 1500;

    /**
     * Shows Notification, when action succeeded.
     * Notification closes after {@link #NOTIFICATION_DURATION_MILLIS}.
     *
     * @param text not-empty string with notification text.
     */
    public static void showSuccessNotification(final String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(NOTIFICATION_DURATION_MILLIS); //1.5 seconds

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Div info = new Div(new Text(text));

        HorizontalLayout layout = new HorizontalLayout(icon, info, createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    /**
     * Shows Notification, when action failed.
     * Notification closes after {@link #NOTIFICATION_DURATION_MILLIS}.
     *
     * @param text not-empty string with notification text.
     */
    public static void showFailNotification(final String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(NOTIFICATION_DURATION_MILLIS); //1.5 seconds

        Icon icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
        Div info = new Div(new Text(text));

        HorizontalLayout layout = new HorizontalLayout(icon, info, createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    public static void showLessImportantNotification(final String text, final boolean success) {
        Notification notification = new Notification();
        NotificationVariant variant = success ? NotificationVariant.LUMO_CONTRAST : NotificationVariant.LUMO_ERROR;
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(1000); //1 second

        VaadinIcon icon = success ? VaadinIcon.CHECK_CIRCLE : VaadinIcon.EXCLAMATION_CIRCLE;
        Div info = new Div(new Text(text));

        HorizontalLayout layout = new HorizontalLayout(icon.create(), info, createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();

    }

    private static Button createCloseBtn(final Notification notification) {
        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        return closeBtn;
    }
}
