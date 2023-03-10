package dev.kyberorg.httpsd.auth;

import java.io.Serializable;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControl extends Serializable {

    /**
     * Provides If authentication enabled or not.
     *
     * @return true if authorization is enabled, false if not.
     */
    boolean isAuthEnabled();

    /**
     * Performs sign-in.
     *
     * @param username non-empty string with username
     * @param password non-empty string with password
     *
     * @return true - if sign-in successful, false if not.
     */
    boolean signIn(String username, String password);

    /**
     * Checks if user signed in.
     *
     * @return true if user signed in, false if not.
     */
    boolean isUserSignedIn();

    /**
     * Performs sign out.
     */
    void signOut();
}
