package org.canoestudio.caveapi.compat;

import net.minecraftforge.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for cross-mod compatibility.
 */
public class ModCompatManager {
    private static final List<IModCompat> COMPATS = new ArrayList<>();
    private static final GenericBlockRemapper GENERIC_REMAPPER = new GenericBlockRemapper();

    public static void init() {
        UniversalCompatHandler.registerRemapper(GENERIC_REMAPPER);
        
        registerCompat("quark", "org.canoestudio.caveapi.compat.QuarkCompat");
        registerCompat("undergroundbiomes", "org.canoestudio.caveapi.compat.UBCCompat");

        // Example generic rules
        // if Traverse is loaded, maybe replace some stones?
        // GENERIC_REMAPPER.addModRule("traverse", "minecraft:stone", "traverse:red_rock");

        for (IModCompat compat : COMPATS) {
            try {
                compat.setup();
            } catch (Exception e) {
                // Log error or handle silently if mod is missing
            }
        }
    }

    private static void registerCompat(String modId, String className) {
        if (Loader.isModLoaded(modId)) {
            try {
                Class<?> clazz = Class.forName(className);
                if (IModCompat.class.isAssignableFrom(clazz)) {
                    COMPATS.add((IModCompat) clazz.newInstance());
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId);
    }
}
