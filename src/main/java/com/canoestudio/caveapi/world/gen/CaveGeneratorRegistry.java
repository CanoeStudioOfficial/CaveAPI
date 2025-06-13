package com.canoestudio.caveapi.world.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaveGeneratorRegistry {
    private static final List<ICaveGenerator> generators = new ArrayList<>();
    private static final List<ICaveGenerator> immutableGenerators =
            Collections.unmodifiableList(generators);

    public static void registerGenerator(ICaveGenerator generator) {
        generators.add(generator);
    }

    public static List<ICaveGenerator> getGenerators() {
        return immutableGenerators;
    }
}