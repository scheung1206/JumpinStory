package Animation;

public class AnimationData {
	private AnimationDef def;
	private int curFrame;
	private float secsUntilNextFrame;

	public AnimationData(AnimationDef def) {
		this.def = def;
		curFrame = 1;
		secsUntilNextFrame = def.getFrames(curFrame).getFrameTime();
	}

	public void update(float deltaTime) {
		secsUntilNextFrame -= deltaTime;
		if (curFrame >= def.getNumFrames()) { //Last frame
			curFrame = 1;
		}
		if (secsUntilNextFrame <= 0) {
			curFrame++;
			secsUntilNextFrame = def.getFrames(curFrame-1).getFrameTime(); //Next frame
		}
	}

	public int getCurFrame() {
		return def.getFrames(curFrame - 1).getImage();
	}
	
	public void getDefault(float deltaTime)
	{
		curFrame = 0;
	}
	
	public int getDefSize()
	{
		return def.getNumFrames();
	}
	
	public int getFrameNum()
	{
	return curFrame;
	}
	
	public void setCurFrame(int curFrame) {
		this.curFrame = curFrame;
	}
}
