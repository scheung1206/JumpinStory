package AABB;

public class AABB {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public AABB(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getH() {
		return h;
	}
	
	public int getW() {
		return w;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	public void setW(int w) {
		this.w = w;
	}
	
	public void addToXCoord(int x) {
		this.x += x;
	}
	
	public void addToYCoord(int y) {
		this.y += y;
	}

//	public boolean AABBIntersect(AABB box1, AABB box2)
//	{
//	 // box1 to the right
//	 if (box1.x > box2.x + box2.w) {
//	 return false;
//	 }
//	 // box1 to the left
//	 if (box1.x + box1.w < box2.x) {
//	 return false;
//	 }
//	 // box1 below
//	 if (box1.y > box2.y + box2.h) {
//	 return false;
//	 }
//	 // box1 above
//	 if (box1.y + box1.h < box2.y) {
//	 return false;
//	 }
//	 return true;
//	}
}
