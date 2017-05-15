package Character;

import AABB.AABB;

public class Lupin {
	private int x;
	private int y;
	private int width, height;
	private int currentTexture;
	private AABB aabb;
	private boolean reverse;
	
	public Lupin(int x,int y,int w,int h,int tex, boolean reverse)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.currentTexture = tex;
		this.aabb = new AABB(x,y,w,h);
		this.reverse = reverse;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getCurrentTexture()
	{
		return currentTexture;
	}
	
	public boolean getReverse()
	{
		return reverse;
	}
	
	public AABB getAabb()
	{
		return aabb;
	}
	
	public void setCurrentTexture(int tex)
	{
		currentTexture = tex;
	}
}
