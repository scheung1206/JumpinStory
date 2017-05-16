package Projectile;

import AABB.AABB;

public class Projectile {

	private int x;
	private int y;
	private int speed;
	private int damage;
	private boolean visible;
	private final int projectileRange = 500;
	private int distanceTraveled = 0;
	private AABB projBox;

	public Projectile(int startX, int startY) {
		this.x = startX;
		this.y = startY;
		this.speed = 10;
		this.damage = 50;
		this.visible = true;
		this.projBox = new AABB(startX, startY, 125, 50);
	}

	public int getDistanceTraveled() {
		return distanceTraveled;
	}

	public void setDistanceTraveled(int distanceTraveled) {
		this.distanceTraveled = distanceTraveled;
	}

	public AABB getprojBox() {
		return projBox;
	}

	public void setprojBox(AABB projBox) {
		this.projBox = projBox;
	}

	public int getProjectileRange() {
		return projectileRange;
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void move() {
		{
		this.x += getSpeed();
		this.distanceTraveled += getSpeed();
		this.projBox.addToXCoord(getSpeed());
		if (distanceTraveled > projectileRange) {
			visible = false;
		}
		}
	}
	
	public void moveLeft() {
		{
		this.x -= getSpeed();
		this.distanceTraveled += getSpeed();
		this.projBox.addToXCoord(-getSpeed());
		if (distanceTraveled > projectileRange) {
			visible = false;
		}
		}
	}
}
