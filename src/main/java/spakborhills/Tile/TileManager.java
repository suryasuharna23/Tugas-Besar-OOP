    package spakborhills.Tile;

    import spakborhills.GamePanel;
    import spakborhills.UtilityTool;

    import javax.imageio.ImageIO;
    import java.awt.*;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.util.ArrayList;

    public class TileManager {
        GamePanel gp;
        public Tile[] tile;
        public int[][] mapTileNum;
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> collisionStatus = new ArrayList<>();

        public TileManager(GamePanel gp){
            this.gp = gp;

            InputStream inputStream = getClass().getResourceAsStream("/maps/oceantiledata.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //Getting Tile Name and Collision Info
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null){
                    fileNames.add(line);
                    collisionStatus.add(bufferedReader.readLine());
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }

            tile = new Tile[fileNames.size()];
            getTilesImage();

            //Get maxWorldCol & maxWorldRow
            inputStream = getClass().getResourceAsStream("/maps/ocean.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));



            try {
                String line2 = bufferedReader.readLine();
                String[] maxTile = line2.split(" ");
                gp.maxWorldCol = maxTile.length;
                gp.maxWorldRow = maxTile.length;
                mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
                bufferedReader.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
            loadMap("/maps/ocean.txt");
        }
        public void getTilesImage(){
            for(int i  = 0; i < fileNames.size(); i++){
                String fileName;
                boolean collision;

                //Get fileName
                fileName = fileNames.get(i);

                //Get collision status
                collision = collisionStatus.get(i).equals("true");
                setup(i, fileName, collision);
            }
        }

        public void setup(int index, String imagePath, boolean collision){
            UtilityTool utilityTool = new UtilityTool();
            try {
                tile[index] = new Tile();
                tile[index].image = ImageIO.read(getClass().getResourceAsStream("/asset/map_resources/ocean/" + imagePath));
                tile[index].collision = collision;
                
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void loadMap(String filePath){
            try{
                InputStream is = getClass().getResourceAsStream(filePath);
                assert is != null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                int col = 0;
                int row = 0;

                while (col < gp.maxWorldCol && row < gp.maxWorldRow){
                    String line = br.readLine();
                    while (col < gp.maxWorldCol){
                        String[] numbers = line.split(" ");

                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[col][row] = num;
                        col++;
                    }
                    if(col == gp.maxWorldCol){
                        col = 0;
                        row++;
                    }
                }
                br.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        public void draw(Graphics2D g2){
            int playerWorldX = gp.player.worldX;
            int playerWorldY = gp.player.worldY;
            int playerScreenX = gp.player.screenX;
            int playerScreenY = gp.player.screenY;
            int tileSize = gp.tileSize;

            // Calculate the world coordinate at the top-left of the screen’s view.
            int cameraWorldLeftX = playerWorldX - playerScreenX;
            int cameraWorldTopY = playerWorldY - playerScreenY;

            // Calculate the starting and ending world tile columns and rows that are potentially visible.
            int startCol = cameraWorldLeftX / tileSize;
            int startRow = cameraWorldTopY / tileSize;
            int endCol = (cameraWorldLeftX + gp.screenWidth) / tileSize + 1; // +1 to ensure tiles partially on screen are included
            int endRow = (cameraWorldTopY + gp.screenHeight) / tileSize + 1; // +1 same reason

            // Iterate through the world tiles that are potentially visible
            for (int worldCol = startCol; worldCol < endCol; worldCol++) {
                for (int worldRow = startRow; worldRow < endRow; worldRow++) {

                    // Check if the current world tile is within the actual world boundaries.
                    if (worldCol >= 0 && worldCol < gp.maxWorldCol && worldRow >= 0 && worldRow < gp.maxWorldRow) {

                        // Calculate screen coordinates for drawing the current world tile
                        int screenX = worldCol * tileSize - cameraWorldLeftX;
                        int screenY = worldRow * tileSize - cameraWorldTopY;

                        // Get the tile number and draw the tile
                        int tileNum = mapTileNum[worldCol][worldRow];
                        g2.drawImage(tile[tileNum].image, screenX, screenY, tileSize, tileSize, null);
                    }
                }
            }
        }
    }