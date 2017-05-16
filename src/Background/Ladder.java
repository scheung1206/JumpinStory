package Background;

import AABB.AABB;

public class Ladder {
	private int x;
	private int y;
	private int w;
	private int h;
	private AABB aabb;
	private boolean reverse;
	private int currentTex;
	
	public Ladder(int x, int y, int w, int h,int currentTex, boolean reverse)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.currentTex = currentTex;
		this.aabb = new AABB(x,y,w,h);
		this.reverse = reverse;
	}
	
	public AABB getAabb() {
		return aabb;
	}
	
	public boolean isReverse() {
		return reverse;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getCurrentTex() {
		return currentTex;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}

}
