package dev.kyberorg.httpsd.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import dev.kyberorg.httpsd.App;
import org.apache.commons.lang3.StringUtils;

public class BasicAccessControl implements AccessControl {

    @Override
    public boolean isAuthEnabled() {
        return App.get().isAuthEnabled();
    }

    @Override
    public boolean signIn(final String username, final String password) {
        if (!App.get().isAuthEnabled()) {
            return false;
        }
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return false;
        }

        String appUser = App.get().getAppUser();
        if (appUser.equals(App.NO_VALUE)) {
            return false;
        }
        String appPassword = App.get().getAppPassword();
        if (appPassword.equals(App.NO_VALUE)) {
            return false;
        }

        boolean isValidUserPass =  username.equals(appUser) && password.equals(appPassword);
        if (isValidUserPass) {
            CurrentUser.set(username);
        }
        return isValidUserPass;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("");
    }
}
