package spakborhills.Tile;

import spakborhills.GamePanel;
import spakborhills.MapInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> collisionStatus = new ArrayList<>();
    private String currentMapAssetFolder; // Akan menyimpan nama subfolder aset untuk map saat ini

    public TileManager(GamePanel gp) {
        this.gp = gp;
    }

    public void getTilesImage() {
        if (currentMapAssetFolder == null) {
            System.err.println("[TileManager.getTilesImage] CRITICAL: currentMapAssetFolder is null. This should have been set in loadMap(). Cannot load tile images reliably.");
            if (tile == null || fileNames.isEmpty()) {
                tile = new Tile[0];
            }
            return;
        }
        if (fileNames.isEmpty()) {
            System.err.println("[TileManager.getTilesImage] fileNames list is empty for asset folder: " + currentMapAssetFolder + ". No tile images to load.");
            if (tile == null) tile = new Tile[0];
            return;
        }
        if (tile == null || tile.length != fileNames.size()) {
            System.err.println("[TileManager.getTilesImage] Tile array not initialized correctly or wrong size. Expected: " + fileNames.size() + ", Got: " + (tile != null ? tile.length : "null") + ". Re-initializing.");
            tile = new Tile[fileNames.size()];
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            boolean collision = false;

            if (i < collisionStatus.size()) {
                collision = collisionStatus.get(i).equalsIgnoreCase("true");
            } else {
                System.err.println("[TileManager.getTilesImage] WARNING: Missing collision status for tile " + i + " ('" + fileName + "') in " + currentMapAssetFolder + ". Defaulting to collision=false.");
            }
            setup(i, fileName, collision);
        }
    }

    public void setup(int index, String imagePath, boolean collision) {
        try {
            if (tile == null || index < 0 || index >= tile.length) {
                System.err.println("[TileManager.setup] FATAL: Tile array is null, or index " + index + " is out of bounds. Tile array size: " + (tile != null ? tile.length : "null") + ". Skipping setup for this tile.");
                return;
            }
            tile[index] = new Tile();

            String basePath = "/asset/map_resources/";
            // currentMapAssetFolder bisa string kosong jika tile ada di root /asset/map_resources/
            // atau berisi nama subfolder. Tambahkan "/" hanya jika subfolder ada dan tidak kosong.
            if (this.currentMapAssetFolder != null && !this.currentMapAssetFolder.isEmpty()) {
                basePath += this.currentMapAssetFolder + "/";
            }

            String fullImagePath = basePath + imagePath;
            System.out.println("[TileManager.setup] Attempting to load tile image: " + fullImagePath + " (Index: " + index + ")");

            InputStream is = getClass().getResourceAsStream(fullImagePath);

            if (is == null) {
                System.err.println("[TileManager.setup] ERROR: Could not find tile image at path: " + fullImagePath + ". Tile " + index + " ('" + imagePath + "') will have no image.");
                tile[index].image = null; // Eksplisit set ke null
            } else {
                tile[index].image = ImageIO.read(is);
                if (tile[index].image == null) {
                    System.err.println("[TileManager.setup] ERROR: ImageIO.read returned null for path: " + fullImagePath + ". Check image format/corruption or if it's an empty file.");
                }
                is.close();
            }
            tile[index].collision = collision;

        } catch (IOException e) {
            System.err.println("[TileManager.setup] IOException for tile " + index + " ('" + imagePath + "') in '" + (this.currentMapAssetFolder != null ? this.currentMapAssetFolder : "root_assets") + "': " + e.getMessage());
            if (tile[index] != null) tile[index].image = null;
        } catch (IllegalArgumentException e) {
            System.err.println("[TileManager.setup] IllegalArgumentException for tile " + index + " ('" + imagePath + "'): " + e.getMessage() + ". This often means the image path or stream was invalid.");
            if (tile[index] != null) tile[index].image = null;
        } catch (Exception e) {
            System.err.println("[TileManager.setup] Unexpected error for tile " + index + " ('" + imagePath + "'): " + e.getMessage());
            e.printStackTrace();
            if (tile[index] != null) tile[index].image = null;
        }
    }

    public void loadMap(MapInfo mapInfo) {
        System.out.println("[TileManager.loadMap] Configuring map: " + mapInfo.getMapName());

        String mapNameOriginal = mapInfo.getMapName();

        // --- AWAL BAGIAN PENTING UNTUK DISESUAIKAN ---
        // Logika untuk menentukan currentMapAssetFolder.
        // Sesuaikan nama folder ini dengan struktur aktual di src/main/resources/asset/map_resources/
        // Jika tile untuk sebuah map ada langsung di /asset/map_resources/ (tanpa subfolder),
        // maka this.currentMapAssetFolder harus di-set menjadi "" (string kosong).

        if (mapNameOriginal.equalsIgnoreCase("Forest River")) {
            this.currentMapAssetFolder = "forest_river";
        } else if (mapNameOriginal.equalsIgnoreCase("Perry's House")) { // Nama di MapInfo GamePanel
            this.currentMapAssetFolder = "perry_house";    // Nama folder aset sebenarnya
        } else if (mapNameOriginal.equalsIgnoreCase("Farm")) { // Nama di MapInfo GamePanel
            this.currentMapAssetFolder = "farm_map_1";     // Nama folder aset sebenarnya
        } else if (mapNameOriginal.equalsIgnoreCase("Abigail's House")) {
            this.currentMapAssetFolder = "abigail_house"; // Ganti jadi "" jika tidak ada subfolder "abigail_house"
        } else if (mapNameOriginal.equalsIgnoreCase("Caroline's House")) {
            this.currentMapAssetFolder = "caroline_house"; // Ganti jadi "" jika tidak ada subfolder
        } else if (mapNameOriginal.equalsIgnoreCase("Dasco's House")) {
            this.currentMapAssetFolder = "dasco_house";    // Ganti jadi "" jika tidak ada subfolder
        } else if (mapNameOriginal.equalsIgnoreCase("Mayor Tadi's House")) {
            this.currentMapAssetFolder = "mayor_tadi_house"; // Ganti jadi "" jika tidak ada subfolder
        } else if (mapNameOriginal.equalsIgnoreCase("Store")) {
            this.currentMapAssetFolder = "store";          // Ganti jadi "" jika tidak ada subfolder
        } else if (mapNameOriginal.equalsIgnoreCase("Mountain Lake")) {
            this.currentMapAssetFolder = "mountain_lake";  // Ganti jadi "" jika tidak ada subfolder
        } else if (mapNameOriginal.equalsIgnoreCase("Ocean")) {
            this.currentMapAssetFolder = "ocean";          // Ganti jadi "" jika tidak ada subfolder
        }
        // Fallback untuk map yang tidak secara eksplisit didefinisikan di atas.
        // Jika Anda memiliki map lain seperti "world01", "tiledata", "sample", "maps01",
        // Anda perlu menambahkan kondisi 'else if' untuk mereka atau pastikan derivasi generik di bawah ini benar.
        else {
            String processedName = mapNameOriginal.toLowerCase()
                    .replaceAll("'s", "")
                    .replace(" ", "_")
                    .replaceAll("[^a-z0-9_]", "");
            this.currentMapAssetFolder = processedName;
            System.out.println("[TileManager.loadMap] Using generically derived asset folder for '" + mapNameOriginal + "': [" + this.currentMapAssetFolder + "]");
            // Jika untuk map-map ini tile-nya ada di root /asset/map_resources/, set ke ""
            // Contoh: if (mapNameOriginal.equals("world01")) { this.currentMapAssetFolder = ""; }
        }

        if (this.currentMapAssetFolder == null) { // Safety net, seharusnya tidak terjadi jika semua map tercakup
            System.err.println("[TileManager.loadMap] CRITICAL: currentMapAssetFolder is NULL after derivation for map: " + mapNameOriginal + ". Defaulting to empty string (root).");
            this.currentMapAssetFolder = "";
        }
        // --- AKHIR BAGIAN PENTING UNTUK DISESUAIKAN ---

        System.out.println("[TileManager.loadMap] Final asset folder path part for '" + mapInfo.getMapName() + "': [" + this.currentMapAssetFolder + "]");

        fileNames.clear();
        collisionStatus.clear();

        try (InputStream inputStreamData = getClass().getResourceAsStream(mapInfo.getMapDataPath());
             BufferedReader bufferedReaderData = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStreamData)))) {
            String line;
            while ((line = bufferedReaderData.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                fileNames.add(line);
                String collisionLine = bufferedReaderData.readLine();
                if (collisionLine != null) {
                    collisionStatus.add(collisionLine);
                } else {
                    System.err.println("[TileManager.loadMap] ERROR: Missing collision status for file: " + line + " in " + mapInfo.getMapDataPath() + ". Defaulting to true (solid).");
                    collisionStatus.add("true");
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("[TileManager.loadMap] Error loading tile data for " + mapInfo.getMapName() + " from " + mapInfo.getMapDataPath() + ": " + e.getMessage());
            e.printStackTrace();
            gp.gameState = gp.titleState;
            return;
        }

        if (fileNames.isEmpty()) {
            System.err.println("[TileManager.loadMap] ERROR: No tile filenames were loaded from " + mapInfo.getMapDataPath() + " for map " + mapInfo.getMapName() + ". Ensure data file is not empty and path is correct.");
            gp.gameState = gp.titleState;
            return;
        }

        tile = new Tile[fileNames.size()];
        System.out.println("[TileManager.loadMap] Initialized tile array with size: " + tile.length + " for map " + mapInfo.getMapName());
        getTilesImage();

        try (InputStream inputStreamMap = getClass().getResourceAsStream(mapInfo.getMapLayoutPath());
             BufferedReader bufferedReaderMap = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStreamMap)))) {
            String firstLine = bufferedReaderMap.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                throw new IOException("Map layout file is empty or first line is empty: " + mapInfo.getMapLayoutPath());
            }
            String[] mapDimensions = firstLine.trim().split("\\s+");
            gp.maxWorldCol = mapDimensions.length;

            ArrayList<String> lines = new ArrayList<>();
            lines.add(firstLine);
            String currentLine;
            while ((currentLine = bufferedReaderMap.readLine()) != null) {
                if (!currentLine.trim().isEmpty()) {
                    lines.add(currentLine);
                }
            }
            gp.maxWorldRow = lines.size();
            System.out.println("[TileManager.loadMap] Map dimensions set for " + mapInfo.getMapName() + " - MaxWorldCol: " + gp.maxWorldCol + ", MaxWorldRow: " + gp.maxWorldRow);

            if (gp.maxWorldCol == 0 || gp.maxWorldRow == 0) {
                System.err.println("[TileManager.loadMap] ERROR: Map dimensions are zero for " + mapInfo.getMapName());
                gp.gameState = gp.titleState;
                return;
            }
            mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

            for (int row = 0; row < gp.maxWorldRow; row++) {
                if (row >= lines.size()) {
                    System.err.println("[TileManager.loadMap] WARNING: Attempting to read line " + row + " but only " + lines.size() + " lines were read from map layout for " + mapInfo.getMapName() + ". Filling row with 0s.");
                    for (int col = 0; col < gp.maxWorldCol; col++) mapTileNum[col][row] = 0;
                    continue;
                }
                String[] numbers = lines.get(row).trim().split("\\s+");
                if (numbers.length < gp.maxWorldCol) { // Periksa jika jumlah kolom di baris kurang dari yang diharapkan
                    System.err.println("[TileManager.loadMap] WARNING: Row " + row + " in " + mapInfo.getMapLayoutPath() + " has " + numbers.length + " columns, expected " + gp.maxWorldCol + ". Padding with 0s for missing columns. Row content: ["+lines.get(row)+"]");
                }
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    if (col < numbers.length && !numbers[col].isEmpty()) {
                        try {
                            mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                            if (mapTileNum[col][row] < 0 || mapTileNum[col][row] >= tile.length) {
                                System.err.println("[TileManager.loadMap] ERROR: Tile number " + mapTileNum[col][row] + " at [" + col + "," + row + "] is out of bounds for tile array (size: " + tile.length + "). Map: '" + mapInfo.getMapName() + "'. Defaulting to 0.");
                                mapTileNum[col][row] = 0;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("[TileManager.loadMap] Error parsing tile number at [" + col + "," + row + "] for map " + mapInfo.getMapName() + ". Value: '" + numbers[col] + "'. Defaulting to tile 0.");
                            mapTileNum[col][row] = 0;
                        }
                    } else {
                        // System.err.println("[TileManager.loadMap] Missing or empty tile number at [" + col + "," + row + "] for map " + mapInfo.getMapName() + ". Defaulting to tile 0.");
                        mapTileNum[col][row] = 0;
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("[TileManager.loadMap] Error loading map layout for " + mapInfo.getMapName() + " from " + mapInfo.getMapLayoutPath() + ": " + e.getMessage());
            e.printStackTrace();
            gp.gameState = gp.titleState;
            return;
        }
        System.out.println("[TileManager.loadMap] Finished configuring map: " + mapInfo.getMapName());
    }

    public void draw(Graphics2D g2) {
        if (mapTileNum == null || tile == null || gp.player == null) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            g2.setColor(Color.WHITE);
            g2.drawString("Error: Map/Tile/Player data not loaded.", 50, 50);
            return;
        }

        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        int playerScreenX = gp.player.screenX;
        int playerScreenY = gp.player.screenY;
        int tileSize = gp.tileSize;

        int cameraWorldLeftX = playerWorldX - playerScreenX;
        int cameraWorldTopY = playerWorldY - playerScreenY;

        int startCol = Math.max(0, cameraWorldLeftX / tileSize);
        int startRow = Math.max(0, cameraWorldTopY / tileSize);
        int endCol = Math.min(gp.maxWorldCol, (cameraWorldLeftX + gp.screenWidth) / tileSize + 1);
        int endRow = Math.min(gp.maxWorldRow, (cameraWorldTopY + gp.screenHeight) / tileSize + 1);

        for (int worldCol = startCol; worldCol < endCol; worldCol++) {
            for (int worldRow = startRow; worldRow < endRow; worldRow++) {
                // Pengecekan batas yang lebih aman untuk mapTileNum (sebenarnya sudah dilakukan oleh loop endCol/endRow jika gp.maxWorldCol/Row benar)
                // Namun, jika gp.maxWorldCol/Row salah, ini bisa jadi pelindung tambahan, meski idealnya tidak perlu.
                if (worldCol >= 0 && worldCol < gp.maxWorldCol && worldRow >= 0 && worldRow < gp.maxWorldRow) {
                    int screenX = worldCol * tileSize - cameraWorldLeftX;
                    int screenY = worldRow * tileSize - cameraWorldTopY;

                    int tileNum = mapTileNum[worldCol][worldRow];

                    if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null && tile[tileNum].image != null) {
                        g2.drawImage(tile[tileNum].image, screenX, screenY, tileSize, tileSize, null);
                    } else {
                        g2.setColor(Color.MAGENTA);
                        g2.fillRect(screenX, screenY, tileSize, tileSize);
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Arial", Font.PLAIN, 10));
                        String tn = String.valueOf(tileNum);
                        if (tileNum < 0 || tileNum >= tile.length) tn += "!";
                        else if (tile[tileNum] == null) tn += "N";
                        else if (tile[tileNum].image == null) tn += "I";
                        g2.drawString(tn, screenX + 2, screenY + 10);
                    }
                }
            }
        }
    }
}