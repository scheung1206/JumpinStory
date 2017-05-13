package Background;

public class BackgroundDef {
	private int width;// = 200; // x-world size
	private int height;// = 8; // y-world size
	private Tile[] tiles;
	private static int TILE_HEALTH=100;

	public BackgroundDef(int width, int height, int image, int start, int end, boolean collision, boolean wall) {
		this.width = width;
		this.height = height;
		tiles = new Tile[(width * height) + 1]; //Initialize empty array of world
		for (int i = start; i < end; i++) {
			tiles[i] = new Tile(image, collision,TILE_HEALTH, wall);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Tile getTile(int x, int y) {
		return tiles[(y * width) + x];
	}

	public void setTile(int image, int start, int end, boolean collision,boolean wall) {
		for (int i = start; i < end; i++) {
			tiles[i] = new Tile(image, collision, TILE_HEALTH, wall);
		}
	}
}