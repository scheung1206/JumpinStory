package Animation;

public class FrameDef {
	private int image;
	private float frameTime;
	
	public FrameDef(int image, float frameTime)
	{
		this.image = image;
		this.frameTime = frameTime;
	}
	
	public void setFrameTime(float frameTime) 
	{
		this.frameTime = frameTime;
	}
	
	public void setImage(int image) 
	{
		this.image = image;
	}
	
	public int getImage() 
	{
		return image;
	}
	
	public float getFrameTime() 
	{
		return frameTime;
	}
}
