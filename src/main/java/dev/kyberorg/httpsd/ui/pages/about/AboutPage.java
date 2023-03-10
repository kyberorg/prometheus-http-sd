package dev.kyberorg.httpsd.ui.pages.about;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import dev.kyberorg.httpsd.ui.MainLayout;
import lombok.RequiredArgsConstructor;

/**
 * {@link AboutPage}. Info about application.
 */
@RequiredArgsConstructor
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
public class AboutPage extends HorizontalLayout implements BeforeEnterObserver {
    private boolean initDone = false;

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (event.isRefreshEvent()) return;
        if (initDone) return;
        pageInit();
        initDone = true;
    }

    private void pageInit() {
        add(VaadinIcon.INFO_CIRCLE.create());
        add(new Span(" This application is using Vaadin version "
                + Version.getFullVersion() + "."));
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
