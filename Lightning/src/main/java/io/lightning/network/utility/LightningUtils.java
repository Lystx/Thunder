
package io.lightning.network.utility;

public class LightningUtils {

    private LightningUtils() {
        throw new UnsupportedOperationException("LightningUtils can't be instantiated!");
    }

    public static int roundUpToNextMultiple(int num, int multiple) {
        return multiple == 0 ? num : num + multiple - (num % multiple);
    }
}
