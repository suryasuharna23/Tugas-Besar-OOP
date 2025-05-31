
package spakborhills;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class FarmLayoutGenerator {
    private static final int MAP_SIZE = 32;
    private static final int BORDER_SIZE = 1;
    private static final int USABLE_AREA = MAP_SIZE - (BORDER_SIZE * 2);

    private static final int HOUSE_WIDTH = 6;
    private static final int HOUSE_HEIGHT = 6;
    private static final int POND_WIDTH = 4;
    private static final int POND_HEIGHT = 3;
    private static final int SHIPPING_BIN_WIDTH = 3;
    private static final int SHIPPING_BIN_HEIGHT = 2;

    private Random random;

    public FarmLayoutGenerator() {
        this.random = new Random();
    }

    public FarmLayoutGenerator(long seed) {
        this.random = new Random(seed);
    }

    public static class FarmLayout {
        public int houseX, houseY;
        public int pondX, pondY;
        public int shippingBinX, shippingBinY;
        public boolean isValid = false;

        public FarmLayout(int houseX, int houseY, int pondX, int pondY, int shippingBinX, int shippingBinY) {
            this.houseX = houseX;
            this.houseY = houseY;
            this.pondX = pondX;
            this.pondY = pondY;
            this.shippingBinX = shippingBinX;
            this.shippingBinY = shippingBinY;
            this.isValid = true;
        }

        @Override
        public String toString() {
            return String.format("House: (%d,%d), Pond: (%d,%d), Shipping Bin: (%d,%d)",
                    houseX, houseY, pondX, pondY, shippingBinX, shippingBinY);
        }
    }

    public FarmLayout generateRandomLayout() {
        int maxAttempts = 1000;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            int houseX = BORDER_SIZE + random.nextInt(USABLE_AREA - HOUSE_WIDTH + 1);
            int houseY = BORDER_SIZE + random.nextInt(USABLE_AREA - HOUSE_HEIGHT + 1);

            List<int[]> validShippingPositions = getValidShippingBinPositions(houseX, houseY);

            if (validShippingPositions.isEmpty()) {
                continue;
            }

            int[] shippingPos = validShippingPositions.get(random.nextInt(validShippingPositions.size()));
            int shippingBinX = shippingPos[0];
            int shippingBinY = shippingPos[1];

            int pondX = BORDER_SIZE + random.nextInt(USABLE_AREA - POND_WIDTH + 1);
            int pondY = BORDER_SIZE + random.nextInt(USABLE_AREA - POND_HEIGHT + 1);

            if (!isOverlapping(pondX, pondY, POND_WIDTH, POND_HEIGHT,
                    houseX, houseY, HOUSE_WIDTH, HOUSE_HEIGHT) &&
                    !isOverlapping(pondX, pondY, POND_WIDTH, POND_HEIGHT,
                            shippingBinX, shippingBinY, SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT)) {

                System.out.println("[FarmLayoutGenerator] Valid layout found after " + attempts + " attempts");
                return new FarmLayout(houseX, houseY, pondX, pondY, shippingBinX, shippingBinY);
            }
        }

        System.err.println("[FarmLayoutGenerator] Failed to generate layout after " + maxAttempts + " attempts!");
        return getDefaultLayout();
    }

    private List<int[]> getValidShippingBinPositions(int houseX, int houseY) {
        List<int[]> validPositions = new ArrayList<>();

        int[] directions = { -1, 0, 1 };

        for (int offsetX = -(SHIPPING_BIN_WIDTH + 1); offsetX <= HOUSE_WIDTH + 1; offsetX++) {
            for (int offsetY = -(SHIPPING_BIN_HEIGHT + 1); offsetY <= HOUSE_HEIGHT + 1; offsetY++) {
                int candidateX = houseX + offsetX;
                int candidateY = houseY + offsetY;

                if (candidateX >= BORDER_SIZE &&
                        candidateY >= BORDER_SIZE &&
                        candidateX + SHIPPING_BIN_WIDTH <= MAP_SIZE - BORDER_SIZE &&
                        candidateY + SHIPPING_BIN_HEIGHT <= MAP_SIZE - BORDER_SIZE) {

                    if (isExactlyOneTileAway(candidateX, candidateY, SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT,
                            houseX, houseY, HOUSE_WIDTH, HOUSE_HEIGHT)) {
                        validPositions.add(new int[] { candidateX, candidateY });
                    }
                }
            }
        }

        return validPositions;
    }

    private boolean isExactlyOneTileAway(int x1, int y1, int w1, int h1,
            int x2, int y2, int w2, int h2) {

        int leftDistance = Math.max(0, x2 - (x1 + w1));
        int rightDistance = Math.max(0, x1 - (x2 + w2));
        int topDistance = Math.max(0, y2 - (y1 + h1));
        int bottomDistance = Math.max(0, y1 - (y2 + h2));

        int horizontalDistance = leftDistance + rightDistance;
        int verticalDistance = topDistance + bottomDistance;

        return (horizontalDistance == 1 && verticalDistance == 0) ||
                (horizontalDistance == 0 && verticalDistance == 1);
    }

    private boolean isOverlapping(int x1, int y1, int w1, int h1,
            int x2, int y2, int w2, int h2) {
        return !(x1 + w1 <= x2 || x2 + w2 <= x1 || y1 + h1 <= y2 || y2 + h2 <= y1);
    }

    private FarmLayout getDefaultLayout() {
        System.out.println("[FarmLayoutGenerator] Using default safe layout");
        return new FarmLayout(
                10, 10,
                20, 20,
                17, 10);
    }

    public int[][] generateFarmMapData(FarmLayout layout) {
        int[][] mapData = new int[MAP_SIZE][MAP_SIZE];

        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                if (x == 0 || x == MAP_SIZE - 1 || y == 0 || y == MAP_SIZE - 1) {
                    mapData[x][y] = 1;
                } else {
                    mapData[x][y] = 14;
                }
            }
        }

        for (int x = layout.houseX; x < layout.houseX + HOUSE_WIDTH; x++) {
            for (int y = layout.houseY; y < layout.houseY + HOUSE_HEIGHT; y++) {
                mapData[x][y] = 0;
            }
        }

        for (int x = layout.pondX; x < layout.pondX + POND_WIDTH; x++) {
            for (int y = layout.pondY; y < layout.pondY + POND_HEIGHT; y++) {
                mapData[x][y] = 2;
            }
        }

        for (int x = layout.shippingBinX; x < layout.shippingBinX + SHIPPING_BIN_WIDTH; x++) {
            for (int y = layout.shippingBinY; y < layout.shippingBinY + SHIPPING_BIN_HEIGHT; y++) {
                mapData[x][y] = 77;
            }
        }

        return mapData;
    }
}