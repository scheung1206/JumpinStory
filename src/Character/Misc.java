package Character;

import AABB.AABB;

public class Misc {
	private int x,y,h,w;
	private AABB aabb;
	private int currentTexture;
	private boolean reverse;
	
	public Misc(int x, int y, int h, int w, int currentTexture,boolean reverse)
	{
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;
		this.aabb = new AABB(x,y,h,w);
		this.currentTexture = currentTexture;
		this.reverse = reverse;
	}
	
	public AABB getAabb() {
		return aabb;
	}
	
	public int getCurrentTexture() {
		return currentTexture;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getH() {
		return h;
	}
	
	public int getW() {
		return w;
	}
	
	public boolean isReverse() {
		return reverse;
	}
}
