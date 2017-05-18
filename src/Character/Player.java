package Character;

import java.util.ArrayList;
import java.util.List;

import AABB.AABB;



public class Player {
	private int x;
	private int y;
	private int width, height;
	private double yVelocity;
	private int xVelocity;
	private double acceleration;
	private AABB hitbox;
	private boolean reverse, isClimbing, isJumping, bounce;
	private int currentTexture;
	
	public Player(int x, int y, int width, int height, int tex) {
		this.x = x;
		this.y = y;
		hitbox = new AABB(x, y, width, height);
		reverse = false;
		isClimbing = false;
		acceleration = .2;
		yVelocity = 0;
		xVelocity = 0;
		currentTexture = tex;
		this.width = width;
		this.height = height;
		bounce = false;
	}
	
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	public double getyVelocity() {
		return yVelocity;
	}

	public void setyVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}

	public boolean isJumping() {
		return isJumping;
	}

	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public boolean isClimbing() {
		return isClimbing;
	}

	public void setClimbing(boolean isClimbing) {
		this.isClimbing = isClimbing;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		hitbox.setX(x);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		hitbox.setY(y);
	}
	
	
	public AABB getHitbox() {
		return hitbox;
	}
	
	public boolean getReverse() {
		return reverse;
	}

	public void setReverse(boolean bool) {
		reverse = bool;
	}
	

	public int getCurrentTexture() {
		return currentTexture;
	}

	public void setCurrentTexture(int currentTexture) {
		this.currentTexture = currentTexture;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setxVelocity(int xVelocity) {
		this.xVelocity = xVelocity;
	}
	
	public int getxVelocity() {
		return xVelocity;
	}
	
	public boolean isBounce() {
		return bounce;
	}
	
	public void setBounce(boolean bounce) {
		this.bounce = bounce;
	}
	
	
}
