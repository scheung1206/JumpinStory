package Background;

public class Tile {

	private int image; //Texture Image for the specific tile
	private boolean collision;
	private int health;
	private boolean wall;
	
	public Tile(int image, boolean collision, int health, boolean wall) {
		this.image = image;
		this.collision = collision;
		this.health = health;
		this.wall = wall;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
	
	public boolean isCollision() {
		return collision;
	}
	
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
	
	public void loseHealth(int health)
	{
		if (wall == true)
		{
		this.health -= health;
		}
	}
	
	public int getHealth() {
		return health;
	}
}
