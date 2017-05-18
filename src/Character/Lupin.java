package Character;

import java.util.ArrayList;
import java.util.Random;

import AABB.AABB;
import Animation.AnimationData;
import Animation.AnimationDef;
import Projectile.Projectile;

public class Lupin {
	private int x;
	private int y;
	private int width, height;
	private int currentTexture;
	private int defaultTex;
	private AABB aabb;
	private boolean reverse;
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private float delay;
	private AnimationDef throwDef;
	private AnimationData throwData;
	
	public Lupin(int x,int y,int w,int h,int tex, boolean reverse,AnimationDef def)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.currentTexture = tex;
		this.defaultTex = tex;
		this.aabb = new AABB(x,y,w,h);
		this.reverse = reverse;
		Random random = new Random();
		this.delay = random.nextInt(500);
		this.throwDef = def;
		this.throwData = new AnimationData(this.throwDef);
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
	
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}
	
	public void throwBanana(float deltaTime)
	{
		delay += deltaTime;
		if (delay >= 3000)
		{
			throwData.update(deltaTime);
			setCurrentTexture(throwData.getCurFrame());
		if (throwData.getCurFrame() == 26)
			{
			Projectile p = new Projectile(this.x, this.y +10);
			addProjectile(p);
			delay = 0;
			throwData.setCurFrame(1);
			setCurrentTexture(defaultTex);
			}
		}
	}
}
