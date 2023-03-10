package dev.kyberorg.httpsd.ui.pages.admin;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.kyberorg.httpsd.auth.AccessControl;
import dev.kyberorg.httpsd.auth.AccessControlFactory;
import dev.kyberorg.httpsd.ui.MainLayout;
import dev.kyberorg.httpsd.ui.pages.login.LoginPage;
import lombok.RequiredArgsConstructor;

/**
 * Page for managing application objects like Targets, Labels, Records and so on.
 * To be done in next versions.
 */
@RequiredArgsConstructor
@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Page")
public class AdminPage extends HorizontalLayout implements BeforeEnterObserver {
    private boolean initDone = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        //Redirect to login page if auth enabled, but nobody logged in
        AccessControl accessControl = AccessControlFactory.get().getAccessControl();
        if (accessControl.isAuthEnabled() && !accessControl.isUserSignedIn()) {
            event.forwardTo(LoginPage.class);
        }
        if (initDone) return;
        pageInit();
        initDone = true;
    }

    private void pageInit() {
        add(VaadinIcon.DOCTOR.create());
        add(new Span("This is placeholder for Admin Page. It will be implemented in next versions"));
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
