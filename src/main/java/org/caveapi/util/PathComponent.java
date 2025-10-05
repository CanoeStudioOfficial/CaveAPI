package org.caveapi.util;

import lombok.AllArgsConstructor;

import java.util.Optional;

import static org.caveapi.util.CommonMethods.empty;
import static org.caveapi.util.CommonMethods.full;

@AllArgsConstructor
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PathComponent {
    public final Optional<String> key;
    public final Optional<Integer> index;

    public static PathComponent key(String key) {
        return new PathComponent(full(key), empty());
    }

    public static PathComponent index(int index) {
        return new PathComponent(empty(), full(index));
    }
}