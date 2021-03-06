import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.opengl.*;

import AABB.AABB;
import Animation.AnimationData;
import Animation.AnimationDef;
import Animation.FrameDef;
import Background.BackgroundDef;
import Background.Ladder;
import Background.Platform;
import Camera.Camera;
import Character.Lupin;
import Character.Misc;
import Character.Player;
import Music.Sound;
import Projectile.Projectile;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class GameLoop{
    // Set this to true to make the game loop exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];

    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];
    
    private static int worldX = 40;
	private static int worldY = 80;
	
	 private static int xRes = 800;
	 private static int yRes = 600;

    // Position of the sprite.
    private static int[] spritePos = new int[] { 100, 100 };
    
    // Size of Tile
    private static int[] tileSize = new int[2];
    
    // Projectile Size
    private static int[] projectileSize = new int[] {50,50};
    
    // Title Size
    private static int[] screenSize = new int[] {800,600};

    // Texture for the sprite.
    private static int playerTex;
    private static int crawlTex;
    private static int jumpTex;
    private static int climbTex;
    private static int lupinTex;
    private static int bananaTex;
    
    // Texture for the background
    private static int backgroundTex;
    private static int wallTex;
    private static int platformLTex;
    private static int platformMTex;
    private static int platformRTex;
    private static int ladderTex;
    private static int taxiTex;
    private static int titleScreen;
    
    // Boolean
    private static boolean touchLadder;
    private static boolean gameover;
    
    // Background
    private static BackgroundDef background;
    
    private static ArrayList<Projectile> enemyProjectiles;
    private static ArrayList<Projectile> bananaProj;
    private static AABB prjHitBox;
    
    private static int offsetMaxX = worldX * 75 - xRes;
	private static int offsetMinX = 0;
	private static int offsetMaxY = worldY * 75 - yRes;
	private static int offsetMinY = 0;
    
    private static Camera camera = new Camera(0, 0);
    
    // Size of the sprite.
    private static int[] spriteSize = new int[2];

    public static void main(String[] args) {
        GLProfile gl2Profile;

        try {
            // Make sure we have a recent version of OpenGL
            gl2Profile = GLProfile.get(GLProfile.GL2);
        }
        catch (GLException ex) {
            System.out.println("OpenGL max supported version is too low.");
            System.exit(1);
            return;
        }

        // Create the window and OpenGL context.
        GLWindow window = GLWindow.create(new GLCapabilities(gl2Profile));
        window.setSize(xRes, yRes);
        window.setTitle("Jumpin' Story");
        window.setVisible(true);
        window.setDefaultCloseOperation(
                WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = false;
            }
        });

        // Setup OpenGL state.
        window.getContext().makeCurrent();
        GL2 gl = window.getGL().getGL2();
        gl.glViewport(0, 0, 800, 600);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, 800, 600, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
		
        gameover = true;
		backgroundTex = glTexImageTGAFile(gl, "res/backgroundTex.tga", tileSize);
		wallTex = glTexImageTGAFile(gl, "res/wall.tga", tileSize);
		playerTex = glTexImageTGAFile(gl, "res/mapleDefault.tga", spriteSize);
		lupinTex = glTexImageTGAFile(gl, "res/lupinDefault.tga", spriteSize);
		bananaTex = glTexImageTGAFile(gl, "res/bananaPrj.tga", projectileSize);
		crawlTex = glTexImageTGAFile(gl, "res/mapleCrawl.tga", spriteSize);
		jumpTex = glTexImageTGAFile(gl, "res/mapleJump.tga", spriteSize);
		platformLTex = glTexImageTGAFile(gl, "res/platformLeft.tga", tileSize);
		platformMTex = glTexImageTGAFile(gl, "res/platformCenter.tga", tileSize);
		platformRTex = glTexImageTGAFile(gl, "res/platformRight.tga", tileSize);
		ladderTex = glTexImageTGAFile(gl, "res/mapleLadder.tga", tileSize);
		climbTex = glTexImageTGAFile(gl, "res/mapleClimb1.tga", spriteSize);
		taxiTex = glTexImageTGAFile(gl, "res/taxiTex.tga", spriteSize);
		titleScreen = glTexImageTGAFile(gl, "res/titleScreen.tga", screenSize);
		
		FrameDef[] mapleRun = { new FrameDef(glTexImageTGAFile(gl, "res/mapleRun1.tga", spriteSize), 50),
				new FrameDef(glTexImageTGAFile(gl, "res/mapleRun2.tga", spriteSize), 50), 
				new FrameDef(glTexImageTGAFile(gl, "res/mapleRun3.tga", spriteSize), 50),
				new FrameDef(glTexImageTGAFile(gl, "res/mapleRun4.tga", spriteSize), 50)
				};
		AnimationDef mapleRunDef = new AnimationDef("mapleRun", mapleRun);
		AnimationData mapleRunData = new AnimationData(mapleRunDef);
		
		FrameDef[] mapleClimb = { new FrameDef(glTexImageTGAFile(gl, "res/mapleClimb1.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/mapleClimb2.tga", spriteSize), 300)
				};
		AnimationDef mapleClimbDef = new AnimationDef("mapleClimb", mapleClimb);
		AnimationData mapleClimbData = new AnimationData(mapleClimbDef);
		
		FrameDef[] lupinThrow = { new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow1.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow2.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow3.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow4.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow5.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow6.tga", spriteSize), 300),
				new FrameDef(glTexImageTGAFile(gl, "res/lupinThrow7.tga", spriteSize), 300)
				};
		AnimationDef lupinThrowDef = new AnimationDef("lupinThrow", lupinThrow);
		AnimationData lupinThrowData = new AnimationData(lupinThrowDef);
		
		background = new BackgroundDef(worldX,worldY,backgroundTex, 0, worldX * worldY, false, false);
		background.setTile(wallTex, 0, worldX, true, false); // Top Border
		background.setTile(wallTex, worldX * ((worldY*2/3)+3),worldX * worldY, true, false );//Bottom Border
		
		
		for (int i=0; i < worldY; i++)
		{
			background.setTile(wallTex,i * worldX,i * worldX + 1, true, true);
		}
		
		for (int i=34; i < 44; i++)
		{
			background.setTile(wallTex,i * worldX + 18, i * worldX + 19, true, true);
		}
		
		for (int i=30; i < 37; i++)
		{
			background.setTile(wallTex,i * worldX + 12, i * worldX + 13, true, true);
		}
		
		ArrayList<Platform> platforms = new ArrayList<Platform>();
		//First Row
		platforms.add(new Platform(400, 4050, 3)); //3
		platforms.add(new Platform(500, 3900, 3)); //3
		platforms.add(new Platform(625, 3750, 1)); //1
		platforms.add(new Platform(300, 3600, 1)); //1
		platforms.add(new Platform(750, 3500, 3)); //3
		
		platforms.add(new Platform(1200, 3825, 5));//3
		platforms.add(new Platform(1500, 4000, 1));//lupin
		platforms.add(new Platform(1800, 4000, 2));//2
		platforms.add(new Platform(2100, 3900, 3));//3
		platforms.add(new Platform(2400, 4000, 2));//2
		platforms.add(new Platform(2700, 3900, 4));//3
		
		//End of First Row
		//1st -> 2nd Transition
		platforms.add(new Platform(2800, 3750, 1));//1
		platforms.add(new Platform(2450, 3600, 1));//1
		platforms.add(new Platform(2225, 3600, 1));//lupin
		platforms.add(new Platform(2800, 3450, 1));//1
		platforms.add(new Platform(2450, 3300, 1));//1
		platforms.add(new Platform(2225, 3300, 1));//lupin
		platforms.add(new Platform(2800, 3150, 4));//4
		platforms.add(new Platform(2800, 3000, 1));//1
		platforms.add(new Platform(2800, 2700, 1));//1
		
		//Second Row
		platforms.add(new Platform(2300, 2700, 2));//2
		platforms.add(new Platform(2375, 3000, 1));//lupin
		platforms.add(new Platform(1700, 3500, 3));//3
		
		platforms.add(new Platform(1775, 2800, 1));//lupin
		platforms.add(new Platform(1775, 3000, 1));//lupin
		platforms.add(new Platform(1775, 3200, 1));//lupin
		platforms.add(new Platform(1275, 2400, 4));//4
		
		platforms.add(new Platform(1075, 2550, 1)); // walk down
		platforms.add(new Platform(1225, 2700, 1)); // walk down
		platforms.add(new Platform(1075, 2850, 1)); // walk down
		platforms.add(new Platform(1225, 3000, 1)); // walk down
		platforms.add(new Platform(1075, 3150, 1)); // walk down
		platforms.add(new Platform(875, 3200, 1)); // walk down
		
		platforms.add(new Platform(650, 3100, 1)); // Long jumps
		platforms.add(new Platform(200, 3000, 1)); // Long jumps
		platforms.add(new Platform(650, 2900, 1)); // Long jumps
		platforms.add(new Platform(200, 2800, 1)); // Long jumps
		platforms.add(new Platform(650, 2700, 1)); // Long jumps
		platforms.add(new Platform(200, 2600, 1)); // Long jumps
		platforms.add(new Platform(650, 2500, 1)); // Long jumps
		platforms.add(new Platform(200, 2400, 1)); // Long jumps
		//End of Second Row
		
		//Transition 2nd to 3rd
		platforms.add(new Platform(200, 1900, 3)); 
		
		//Third Row
		platforms.add(new Platform(500, 2000, 6)); 
		platforms.add(new Platform(1025, 2000, 3)); 
		platforms.add(new Platform(1025, 1850, 3)); 
		platforms.add(new Platform(1350, 2000, 6));
		platforms.add(new Platform(1875, 2000, 3)); 
		platforms.add(new Platform(1875, 1850, 3));
		platforms.add(new Platform(2200, 2000, 6));
		//End Third
		
		//Transition 3nd to 4rd
		platforms.add(new Platform(2800, 2000, 3));
		platforms.add(new Platform(2600, 1500, 5));
		
		//4th Row
		
		platforms.add(new Platform(2300, 1500, 3));
		
		platforms.add(new Platform(2000, 1600, 3));
		platforms.add(new Platform(2000, 1450, 1));
		platforms.add(new Platform(1700, 1450, 1));
		
		platforms.add(new Platform(2000, 1300, 1));
		
		platforms.add(new Platform(2000, 1150, 1));
		platforms.add(new Platform(1700, 1150, 1));
		platforms.add(new Platform(2300, 1150, 1));
		
		platforms.add(new Platform(2000, 1000, 1));
		
		platforms.add(new Platform(2000, 850, 1));
		platforms.add(new Platform(1700, 850, 1));
		platforms.add(new Platform(2300, 850, 1));
		
		platforms.add(new Platform(2000, 700, 1));
		platforms.add(new Platform(1700, 700, 1));
		platforms.add(new Platform(2300, 700, 1));
		
		platforms.add(new Platform(1700, 550, 5));
		
		platforms.add(new Platform(1550, 550, 1)); // walk down
		platforms.add(new Platform(1400, 700, 1)); // walk down
		platforms.add(new Platform(1250, 850, 1)); // walk down
		platforms.add(new Platform(1400, 1000, 1)); // walk down
		platforms.add(new Platform(1250, 1150, 1)); // walk down
		platforms.add(new Platform(1100, 1300, 1)); // walk down
		platforms.add(new Platform(900, 1400, 1)); // walk down
		
		platforms.add(new Platform(650, 1400, 1)); // Long jumps
		platforms.add(new Platform(200, 1300, 1)); // Long jumps
		platforms.add(new Platform(650, 1200, 1)); // Long jumps
		platforms.add(new Platform(200, 1100, 1)); // Long jumps
		platforms.add(new Platform(650, 1000, 1)); // Long jumps
		platforms.add(new Platform(200, 900, 1)); // Long jumps
		platforms.add(new Platform(650, 800, 1)); // Long jumps
		platforms.add(new Platform(200, 700, 1)); // Long jumps
		
		platforms.add(new Platform(200, 300, 38)); //Finish
		
		
		Player player = new Player(spritePos[0], spritePos[1], spriteSize[0], spriteSize[1], playerTex);
		
		ArrayList<Ladder> ladderList = new ArrayList<Ladder>();
		ladderList.add(new Ladder(2800, 2900,95,100,ladderTex,false));
		ladderList.add(new Ladder(2800, 2800,95,100,ladderTex,false));
		ladderList.add(new Ladder(2800, 2700,95,100,ladderTex,false));
		
		ladderList.add(new Ladder(2100, 3400,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 3300,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 3200,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 3100,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 3000,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2900,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2800,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2700,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2600,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2500,95,100,ladderTex,false));
		ladderList.add(new Ladder(2100, 2400,95,100,ladderTex,false));
		
		ladderList.add(new Ladder(1500, 3300,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 3200,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 3100,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 3000,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2900,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2800,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2700,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2600,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2500,95,100,ladderTex,false));
		ladderList.add(new Ladder(1500, 2400,95,100,ladderTex,false));
		
		ladderList.add(new Ladder(200, 2300,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 2200,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 2100,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 2000,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 1900,95,100,ladderTex,false));
		
		ladderList.add(new Ladder(2900, 1900,95,100,ladderTex,false));
		ladderList.add(new Ladder(2900, 1800,95,100,ladderTex,false));
		ladderList.add(new Ladder(2900, 1700,95,100,ladderTex,false));
		ladderList.add(new Ladder(2900, 1600,95,100,ladderTex,false));
		ladderList.add(new Ladder(2900, 1500,95,100,ladderTex,false));
		
		ladderList.add(new Ladder(200, 600,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 500,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 400,95,100,ladderTex,false));
		ladderList.add(new Ladder(200, 300,95,100,ladderTex,false));
		

		ArrayList<Lupin> lupinList = new ArrayList<Lupin>();
		lupinList.add(new Lupin(1500, 3900,95,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(2225, 3500,95,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(2225, 3200,95,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(2375, 2900,95,100, lupinTex,false,lupinThrowDef));
		
		lupinList.add(new Lupin(1775, 2700,95,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(1775, 2900,95,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(1775, 3100,95,100, lupinTex,false,lupinThrowDef));
		
		lupinList.add(new Lupin(500, 1900,75,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(1025, 1900,75,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(1175, 1900,75,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(1875, 1900,75,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(2025, 1900,75,100, lupinTex,true,lupinThrowDef));
		
		lupinList.add(new Lupin(1700, 1350,75,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(1700, 1050,75,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(1700, 750,75,100, lupinTex,true,lupinThrowDef));
		lupinList.add(new Lupin(1700, 600,75,100, lupinTex,true,lupinThrowDef));
		
		lupinList.add(new Lupin(2300, 1050,75,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(2300, 750,75,100, lupinTex,false,lupinThrowDef));
		lupinList.add(new Lupin(2300, 600,75,100, lupinTex,false,lupinThrowDef));
		
		ArrayList<Misc> miscList = new ArrayList<Misc>();
		miscList.add(new Misc(2800,120,200,150,taxiTex,false));
		
		Sound soundMain = Sound.loadFromFile("res/mapleMusic.wav");
			
        // The game loop
        long lastFrameNS;
        long curFrameNS = System.nanoTime();
        
        int lastPhysicsFrameMs = 0;
		long curFrameMs;
		int physicsDeltaMs = 10;
		
		//Get players previosu x and y
		int spritePrevX = player.getX();
		int spritePrevY = player.getY();
        
        AABB tileAABB;
        
        Clip bgClip = soundMain.playLooping();
        
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
            lastFrameNS = curFrameNS;
            
            curFrameNS = System.nanoTime();
            long deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
			curFrameMs = curFrameNS / 1000000;
 
            // Actually, this runs the entire OS message pump.
			
            window.display();
            
            if (player.isJumping()) {
				player.setY((int) (player.getY() + player.getyVelocity()));
				player.setyVelocity(player.getyVelocity() + player.getAcceleration());
			} 
            else if (player.isClimbing())
			{
			} 
			else 
			{
				player.setY((int) (player.getY() + player.getyVelocity()));
				player.setyVelocity(player.getyVelocity() + player.getAcceleration());
			}
            
            do {
				// Collision with background and resolution
				int startX = camera.getX() / tileSize[0];
				int endX = (camera.getX() + xRes) / tileSize[0];
				int startY = camera.getY() / tileSize[1];
				int endY = (camera.getY() + yRes) / tileSize[1];

				// Collision check player with walls
				for (int i = startX; i < endX; i++) {
					for (int j = startY; j < endY; j++) {
						if (background.getTile(i, j).isCollision()) {
							tileAABB = new AABB(i * tileSize[0], j * tileSize[1], 75, 40);
							boolean coll = AABBIntersect(player.getHitbox(), tileAABB);
							if (coll) {
								if (player.isJumping())
								{
									if (player.isBounce() == false)
										{
										player.setX(spritePrevX);
										player.setY(spritePrevY-7);
										player.setX(player.getX() + player.getxVelocity()/10);
										player.setxVelocity(0);
										player.setBounce(true);
										player.setJumping(false);
										player.setyVelocity(0);
										}
								}
								else if (background.getTile(i, j).isWall())
								{
									player.setX(spritePrevX);
									player.setY(spritePrevY+5);
									player.setJumping(false);
									player.setyVelocity(0);
								}
								else {
								player.setX(spritePrevX);
								player.setY(spritePrevY);
								player.setJumping(false);
								player.setyVelocity(0);
								}
								
							}
						}
					}
				}
				
				for (Platform platform : platforms) {
					AABB playerBox;
					if (player.isJumping()) {
						playerBox = new AABB(player.getHitbox().getX(), player.getHitbox().getY(),
								player.getHitbox().getH() - 20, player.getHitbox().getW());
						if (AABBIntersect(playerBox, platform.getCollisionBox()) && player.getyVelocity() > 0) {
							if (spritePrevY + 80 - platform.getY() < 2) {
								player.setJumping(false);
								player.setCurrentTexture(playerTex);
								player.setyVelocity(0);
								player.setY(spritePrevY - 20);
							}
						}
					} else {
						playerBox = player.getHitbox();
						if (AABBIntersect(player.getHitbox(), platform.getCollisionBox())) {
							if (spritePrevY + 100 - platform.getY() < 2) {
								player.setJumping(false);
								player.setyVelocity(0);
								player.setY(spritePrevY);
							}
						}
					}

				}
				
				for (Lupin lupin: lupinList)
				{
					//Collision PLayer vs Lupin
					if (AABBIntersect(lupin.getAabb(), player.getHitbox()))
					{
						if (player.getReverse())
						{
							player.setX(player.getX() - 200);
						}
						else
						{
							player.setX(player.getX() + 200);
						}
						player.setClimbing(false);
					}
					
					
					for (int i = 0; i < lupin.getProjectiles().size(); i++) {
						bananaProj = lupin.getProjectiles();
						Projectile prj = bananaProj.get(i);
						if (lupin.getReverse())
						{
						prj.move();
						}
						else
						{
						prj.moveLeft();
						}
						prjHitBox = bananaProj.get(i).getprojBox();
						
						for (int a = startX; a < endX; a++) {
							for (int b = startY; b < endY; b++) {
								
								// Collision check enemy projectiles with walls
								if (background.getTile(a, b).isCollision()) {
									tileAABB = new AABB(a * tileSize[0], b * tileSize[1], 75, 75);
									boolean coll = AABBIntersect(prjHitBox, tileAABB);
									if (coll) {
										prj.setVisible(false);
									}
								}
							}
						}
					
						// Collison check enemy projectiles with player
						if (AABBIntersect(prjHitBox, player.getHitbox())) {
							bananaProj.get(i).setVisible(false);
							bananaProj.remove(i);
							player.setClimbing(false);
							if (lupin.getReverse())
							{
								player.setY(player.getY() - 20);
								player.setX(player.getX() + 150);
							}
							else
							{
								player.setY(player.getY() - 20);
								player.setX(player.getX() - 150);
							}
						}
					}
				}
				
				// Collision with Ladder
				Boolean touchingLadder = false;
				for (Ladder ladder: ladderList)
				{
					if (AABBIntersect(ladder.getAabb(), player.getHitbox())) {
						touchingLadder = true;
					}
				}
				if (touchingLadder == true)
				{
					touchLadder = true;
				}
				else
				{
					touchLadder = false;
				}
				
				for (Misc misc: miscList)
				{
					if (AABBIntersect(misc.getAabb(), player.getHitbox())) {
						gameover = true;
						player.setX(100);
						player.setY(100);
					}
				}
				

			lastPhysicsFrameMs += physicsDeltaMs;
			} while (lastPhysicsFrameMs + physicsDeltaMs > curFrameMs);		
			spritePrevX = player.getX();
			spritePrevY = player.getY();
            
            if (!window.isVisible()) {
                shouldExit = true;
                break;
            }

            // Game logic goes here.
            if (kbState[KeyEvent.VK_ESCAPE]) {
                shouldExit = true;
            }
            
            // Enemy actions move or attack
         	for (Lupin lupin:lupinList) {
         		//if (AABBIntersect(camera.getAabb(), lupin.getAabb())) {
         			lupin.throwBanana(deltaTimeMS);
         		//}
         	}
            
            /**
			 * Player movement code.
			 */
			if ((kbState[KeyEvent.VK_LEFT] || kbState[KeyEvent.VK_A]) && !player.isClimbing()) {
				player.setX(player.getX() - 5);
				player.setReverse(false);
				if (player.isJumping() || player.getyVelocity() > 0)
				{
					player.setxVelocity(player.getxVelocity() - 1);
				}
				if (!player.isJumping() && player.getyVelocity() ==0) {
					mapleRunData.update(deltaTimeMS);
					player.setCurrentTexture(mapleRunData.getCurFrame());
				}
			}

			if ((kbState[KeyEvent.VK_LEFT] || kbState[KeyEvent.VK_A]) && player.isClimbing() && kbState[KeyEvent.VK_SPACE]) {
				player.setClimbing(false);
				player.setJumping(true);
				player.setBounce(false);
				player.setyVelocity(-3);
				player.setCurrentTexture(jumpTex);
				player.setY(player.getY() - 2);
				player.setX(player.getX() - 5);
				player.setReverse(false);
			}
			
			if ((kbState[KeyEvent.VK_RIGHT] || kbState[KeyEvent.VK_D]) && player.isClimbing() && kbState[KeyEvent.VK_SPACE]) {
				player.setClimbing(false);
				player.setJumping(true);
				player.setBounce(false);
				player.setyVelocity(-3);
				player.setCurrentTexture(jumpTex);
				player.setY(player.getY() - 2);
				player.setX(player.getX() + 5);
			}
			
			if ((kbState[KeyEvent.VK_RIGHT] || kbState[KeyEvent.VK_D]) && !player.isClimbing()) {
				player.setX(player.getX() + 5);
				player.setReverse(true);
				if (player.isJumping() || player.getyVelocity() > 0)
				{
					player.setxVelocity(player.getxVelocity() + 1);
				}
				if (!player.isJumping() && player.getyVelocity() ==0) {
					mapleRunData.update(deltaTimeMS);
					player.setCurrentTexture(mapleRunData.getCurFrame());
				}
			}

			/**
			 * Press Space to jump. Change number in set velocity to determine how
			 * high player jumps.
			 */
			if (kbState[KeyEvent.VK_SPACE]) {
				if (player.isJumping() == false && player.getyVelocity() == 0)
				{
				player.setJumping(true);
				player.setBounce(false);
				player.setyVelocity(-7);
				player.setCurrentTexture(jumpTex);
				player.setY(player.getY() - 2);
				}
			}
			
			if (kbState[KeyEvent.VK_UP] || kbState[KeyEvent.VK_W]) {
				if (touchLadder)
				{
				player.setClimbing(true);
				player.setJumping(false);
				player.setBounce(false);
				player.setyVelocity(0);
				mapleClimbData.update(deltaTimeMS);
				player.setCurrentTexture(mapleClimbData.getCurFrame());
				player.setY(player.getY() - 2);
				}
				else
				{
					player.setClimbing(false);
				}
			}

			if ((kbState[KeyEvent.VK_DOWN] || kbState[KeyEvent.VK_S]) ) {
				if (touchLadder)
				{
					player.setClimbing(true);
					player.setJumping(false);
					player.setBounce(false);
					player.setyVelocity(0);
					mapleClimbData.update(deltaTimeMS);
					player.setCurrentTexture(mapleClimbData.getCurFrame());
					player.setY(player.getY() + 2);
				}
				else
				{
				player.setCurrentTexture(crawlTex);
				}
			}
            
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            
            camera.setX(player.getX() - xRes / 2);
			camera.setY(player.getY() - yRes / 2);
			if (camera.getX() > offsetMaxX) {
				camera.setX(offsetMaxX);
			} else if (camera.getX() < offsetMinX) {
				camera.setX(offsetMinX);
			}
			if (camera.getY() > offsetMaxY) {
				camera.setY(offsetMaxY);
			} else if (camera.getY() < offsetMinY) {
				camera.setY(offsetMinY);
			}
            
            int tile_x = camera.getX()/tileSize[0];
			int tile_y = camera.getY()/tileSize[1];
			
			for (int i = tile_x; i <= tile_x + (xRes / tileSize[0]) + 1; i++) { //camera tile, to tile width
				for (int j = tile_y; j < tile_y+(yRes / tileSize[1] +1); j++) { //loop height
					if (background.getTile(i, j) != null) {
						glDrawSprite(gl, background.getTile(i, j).getImage(), i * tileSize[0] - camera.getX(),
								j * tileSize[1] - camera.getY(), tileSize[0], tileSize[1],false);
					}
				}
			}
			
			// Draw platforms
			for (Platform platform : platforms) {
				if (AABBIntersect(camera.getAabb(), platform.getCollisionBox())) {
					glDrawSprite(gl, platformLTex, platform.getX() - camera.getX(), platform.getY() - camera.getY(),
							tileSize[0], tileSize[1], false);
					for (int i = 0; i < platform.getLength() - 2; i++) {
						glDrawSprite(gl, platformMTex, platform.getX() + (75 * (i + 1)) - camera.getX(),
							platform.getY() - camera.getY(), tileSize[0], tileSize[1], false);
					}
					glDrawSprite(gl, platformRTex, platform.getX() + (75 * (platform.getLength()-1)) - camera.getX(),
								platform.getY() - camera.getY(), tileSize[0], tileSize[1], false);
					}
				}
			
			for (Lupin lupin: lupinList)
			{
				if (AABBIntersect(camera.getAabb(), lupin.getAabb()))
				{
					glDrawSprite(gl, lupin.getCurrentTexture(), lupin.getX() - camera.getX(),lupin.getY() - camera.getY(), lupin.getWidth(), lupin.getHeight(), lupin.getReverse());
				}
			}
			
			for (Lupin lupin: lupinList)
			{
				enemyProjectiles = lupin.getProjectiles();
				for (int i = 0; i < enemyProjectiles.size(); i++) {
					Projectile prj = enemyProjectiles.get(i);
					if (prj.isVisible()) {
						glDrawSprite(gl, bananaTex, prj.getX() - camera.getX(), prj.getY() - camera.getY(), projectileSize[0],
								projectileSize[1],lupin.getReverse());
					} else {
						lupin.getProjectiles().remove(prj);
					}
				}
			}
			
			for (Ladder ladder: ladderList)
			{
				if (AABBIntersect(camera.getAabb(), ladder.getAabb()))
				{
					glDrawSprite(gl, ladder.getCurrentTex(), ladder.getX() - camera.getX(),ladder.getY() - camera.getY(), ladder.getW(), ladder.getH(), ladder.isReverse());
				}
			}
			
			for (Misc misc: miscList)
			{
				if (AABBIntersect(camera.getAabb(), misc.getAabb()))
				{
					glDrawSprite(gl, misc.getCurrentTexture(), misc.getX() - camera.getX(),misc.getY() - camera.getY(), misc.getW(), misc.getH(), misc.isReverse());
				}
			}
			
			 // Draw the sprite
			if (AABBIntersect(camera.getAabb(), player.getHitbox()))
			{
				glDrawSprite(gl, player.getCurrentTexture(), player.getX() - camera.getX(),player.getY() - camera.getY(), player.getWidth(), player.getHeight(), player.getReverse());
			}
			
			while(gameover == true){
				System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
				glDrawSprite(gl, titleScreen, 0 - camera.getX(),
						0 - camera.getY(), screenSize[0], screenSize[1], false);
				
				if(kbState[KeyEvent.VK_ENTER]){
					gameover = false;
					player.setX(100);
					player.setY(3900);
				}
				window.display();
				if (!window.isVisible()) {
					shouldExit = true;
					break;
				}
				}
			}
    }

    // Load a file into an OpenGL texture and return that texture.
    public static int glTexImageTGAFile(GL2 gl, String filename, int[] out_size) {
        final int BPP = 4;

        DataInputStream file = null;
        try {
            // Open the file.
            file = new DataInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException ex) {
            System.err.format("File: %s -- Could not open for reading.", filename);
            return 0;
        }

        try {
            // Skip first two bytes of data we don't need.
            file.skipBytes(2);

            // Read in the image type.  For our purposes the image type
            // should be either a 2 or a 3.
            int imageTypeCode = file.readByte();
            if (imageTypeCode != 2 && imageTypeCode != 3) {
                file.close();
                System.err.format("File: %s -- Unsupported TGA type: %d", filename, imageTypeCode);
                return 0;
            }

            // Skip 9 bytes of data we don't need.
            file.skipBytes(9);

            int imageWidth = Short.reverseBytes(file.readShort());
            int imageHeight = Short.reverseBytes(file.readShort());
            int bitCount = file.readByte();
            file.skipBytes(1);

            // Allocate space for the image data and read it in.
            byte[] bytes = new byte[imageWidth * imageHeight * BPP];

            // Read in data.
            if (bitCount == 32) {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = file.readByte();
                }
            } else {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = -1;
                }
            }

            file.close();

            // Load into OpenGL
            int[] texArray = new int[1];
            gl.glGenTextures(1, texArray, 0);
            int tex = texArray[0];
            gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
            gl.glTexImage2D(
                    GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0,
                    GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

            out_size[0] = imageWidth;
            out_size[1] = imageHeight;
            return tex;
        }
        catch (IOException ex) {
            System.err.format("File: %s -- Unexpected end of file.", filename);
            return 0;
        }
    }

    public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h, boolean reverse) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glColor3ub((byte)-1, (byte)-1, (byte)-1);
            if (reverse) {
				gl.glTexCoord2f(1, 1);
				gl.glVertex2i(x, y);
				gl.glTexCoord2f(0, 1);
				gl.glVertex2i(x + w, y);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2i(x + w, y + h);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2i(x, y + h);
			}
            else {
            gl.glTexCoord2f(0, 1);
            gl.glVertex2i(x, y);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2i(x + w, y);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2i(x + w, y + h);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2i(x, y + h);
            }
        }
        gl.glEnd();
    }
    
    static boolean AABBIntersect(AABB box1, AABB box2)
	{
	 // box1 to the right
	 if (box1.x > box2.x + box2.w) {
	 return false;
	 }
	 // box1 to the left
	 if (box1.x + box1.w < box2.x) {
	 return false;
	 }
	 // box1 below
	 if (box1.y > box2.y + box2.h) {
	 return false;
	 }
	 // box1 above
	 if (box1.y + box1.h < box2.y) {
	 return false;
	 }
	 return true;
	}
}
