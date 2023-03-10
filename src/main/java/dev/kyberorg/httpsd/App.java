package dev.kyberorg.httpsd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class App {
    public static final Gson GSON = new GsonBuilder().create();
    public static final String NO_VALUE = "-";

    private static App self;

    @Getter
    private final Environment env;

    public static App get() {
        return self;
    }

    public App(final Environment env) {
        this.env = env;
        self  = this;
    }

    public boolean isAuthEnabled() {
        return env.getProperty("app.auth.enabled", Boolean.class, false);
    }

    public String getAppUser() {
        return env.getProperty("app.auth.user", NO_VALUE);
    }

    public String getAppPassword() {
        return env.getProperty("app.auth.password", NO_VALUE);
    }
}
