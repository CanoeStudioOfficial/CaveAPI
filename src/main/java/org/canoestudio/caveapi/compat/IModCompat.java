package org.canoestudio.caveapi.compat;

/**
 * Interface for mod compatibility layers.
 */
public interface IModCompat {
    /**
     * Called during initialization if the target mod is loaded.
     */
    void setup();
}
