package caveapi.util;

@FunctionalInterface
public interface PositionConsumer {
    void accept(int x, int y, int z);
}
