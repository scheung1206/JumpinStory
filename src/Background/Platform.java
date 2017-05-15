package Background;

import AABB.AABB;

public class Platform {
	private int x;
	private int y;
	private int length;
	private AABB collisionBox;
	
	public Platform(int x, int y, int length) {
		this.setX(x);
		this.setY(y);
		this.length = length;
		this.collisionBox = new AABB(x, y, 75*length, 75);
	}

	public AABB getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(AABB collisionBox) {
		this.collisionBox = collisionBox;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
