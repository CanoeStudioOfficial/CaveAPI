package org.caveapi.util;

@FunctionalInterface
public interface PositionPredicate {
    boolean test(int x, int y, int z);
}
