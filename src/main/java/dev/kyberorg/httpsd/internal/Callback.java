package dev.kyberorg.httpsd.internal;

/**
 * Special interface to accept lambdas as method parameters.
 *
 */
@FunctionalInterface
public interface Callback {
    /**
     * Triggers execution.
     */
    void execute();
}
