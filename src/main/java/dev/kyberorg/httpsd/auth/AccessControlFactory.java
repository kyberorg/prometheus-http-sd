package dev.kyberorg.httpsd.auth;

/**
 * Factory to access {@link AccessControl} implementation.
 */
public class AccessControlFactory {
    private static final AccessControlFactory INSTANCE = new AccessControlFactory();
    private final AccessControl accessControl = new BasicAccessControl();

    private AccessControlFactory() {
    }

    /**
     * Provides {@link AccessControlFactory}.
     *
     * @return singleton with {@link AccessControlFactory}
     */
    public static AccessControlFactory get() {
        return INSTANCE;
    }

    /**
     * Provides {@link AccessControl}.
     *
     * @return {@link AccessControl} implementation. Currently, {@link BasicAccessControl}.
     */
    public AccessControl getAccessControl() {
        return accessControl;
    }
}
