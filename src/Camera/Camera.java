package Camera;

import AABB.AABB;

public class Camera {
	public int x;
	public int y;
	public AABB aabb;
	
	public static int XSIZE = 800;
	public static int YSIZE = 600;

	public Camera(int x, int y) {
		this.x = x;
		this.y = y;
		this.aabb = new AABB(x,y,XSIZE,YSIZE);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
		this.aabb.setX(x);
	}

	public void setY(int y) {
//		if (y >= 2400)
//		{
//			return;
//		}
//		else
		{
		this.y = y;
		this.aabb.setY(y);
		}
	}
	
	public AABB getAabb() {
		return aabb;
	}
}
