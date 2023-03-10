package dev.kyberorg.httpsd.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import dev.kyberorg.httpsd.auth.AccessControl;
import dev.kyberorg.httpsd.auth.AccessControlFactory;
import dev.kyberorg.httpsd.ui.pages.about.AboutPage;
import dev.kyberorg.httpsd.ui.pages.admin.AdminPage;
import dev.kyberorg.httpsd.ui.pages.home.HomePage;
import dev.kyberorg.httpsd.ui.pages.login.LoginPage;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@UIScope
@SpringComponent
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/menu-buttons.css", themeFor = "vaadin-button")
public class MainLayout extends AppLayout implements RouterLayout, BeforeEnterObserver {

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabToTarget = new HashMap<>();

    private static final String LIGHT_MODE = "lightMode";

    private final Button themeToggle = new Button(VaadinIcon.MOON.create());
    private boolean initDone = false;

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        setThemeFromCookie();
        super.onAttach(attachEvent);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        //this one line should be executed every time - even if page already initialized.
        tabs.setSelectedTab(tabToTarget.get(event.getNavigationTarget()));

        if (event.isRefreshEvent()) return;
        if (initDone) return;
        pageInit();
        initDone = true;
    }

    private void pageInit() {
        // Header of the menu (the navbar)
        setPrimarySection(Section.NAVBAR);
        // menu toggle
        final DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClassName("menu-toggle");

        // image, logo
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        top.setClassName("menu-header");
        top.setWidthFull();

        Image logo = new Image("/images/logo.png", "AppLogo");
        logo.setClassName("logo");
        final Label title = new Label("Prometheus HTTP Service Discovery");

        themeToggle.setClassName("theme-toggle-button");
        themeToggle.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        themeToggle.addClickListener(e -> this.toggleTheme());

        top.add(drawerToggle, logo, title, themeToggle);
        addToNavbar(top);

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);

        // Navigation items
        addMenuTab("Home", HomePage.class, VaadinIcon.EDIT);
        addMenuTab("About", AboutPage.class, VaadinIcon.INFO_CIRCLE);
        addMenuTab("Admin", AdminPage.class, VaadinIcon.DOCTOR);

        AccessControl accessControl = AccessControlFactory.get().getAccessControl();
        if (accessControl.isAuthEnabled()) {
            Button logoutButton = new Button("Logout");
            logoutButton.addClickListener(e -> logout());
            logoutButton.getElement().setAttribute("title", "Logout (Ctrl+L)");
            addMenuButton(logoutButton, LoginPage.class, VaadinIcon.SIGN_OUT.create());
        }
    }

    private void addMenuTab(final String label, final Class<? extends Component> target, final VaadinIcon icon) {
        RouterLink link = new RouterLink("", target);
        link.add(icon.create());
        link.add(label);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        Tab tab = new Tab(link);
        tabToTarget.put(target, tab);
        tabs.add(tab);
    }

    @SuppressWarnings("SameParameterValue")
    private void addMenuButton(Button menuButton, final Class<? extends Component> target, final Icon icon) {
        menuButton.setClassName("menu-button");
        menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        menuButton.setIcon(icon);
        icon.setSize("24px");
        Tab tab = new Tab(menuButton);
        tabToTarget.put(target, tab);
        tabs.add(tab);
    }

    private void logout() {
        AccessControlFactory.get().getAccessControl().signOut();
    }

    private void setThemeFromCookie() {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (isLightThemeOn()) {
            themeList.remove(Lumo.DARK);
            themeList.add(Lumo.LIGHT);
        } else {
            themeList.remove(Lumo.LIGHT);
            themeList.add(Lumo.DARK);
        }
    }

    private void toggleTheme() {
        boolean saveLightTheme = true;
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
            themeList.add(Lumo.LIGHT);
        } else {
            themeList.remove(Lumo.LIGHT);
            themeList.add(Lumo.DARK);
            saveLightTheme = false;
        }
        setLightThemeInCookie(saveLightTheme);
    }


    private void setLightThemeInCookie(boolean b) {
        Cookie myCookie = new Cookie(LIGHT_MODE, b ? "true" : "false");
        // Make cookie expire in 30 days
        myCookie.setMaxAge(30 * 24 * 60 * 60);
        myCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
        VaadinService.getCurrentResponse().addCookie(myCookie);
    }

    private String getLightModeCookieValue() {
        for (Cookie c : VaadinService.getCurrentRequest().getCookies()) {
            if (LIGHT_MODE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private boolean isLightThemeOn() {
        String value = getLightModeCookieValue();
        if (value == null) {
            setLightThemeInCookie(true);
            return true;
        }
        return "true".equals(value);
    }
}
