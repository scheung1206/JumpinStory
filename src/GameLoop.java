import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.opengl.*;

import AABB.AABB;
import Animation.AnimationData;
import Animation.AnimationDef;
import Animation.FrameDef;
import Background.BackgroundDef;
import Camera.Camera;
import Character.Player;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GameLoop{
    // Set this to true to make the game loop exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];

    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];
    
    private static int worldX = 100;
	private static int worldY = 40;
	
	 private static int xRes = 800;
	 private static int yRes = 600;

    // Position of the sprite.
    private static int[] spritePos = new int[] { 100, 100 };
    
    // Size of Tile
    private static int[] tileSize = new int[2];

    // Texture for the sprite.
    private static int playerTex;
    
    // Texture for the background
    private static int grassTex;
    private static int groundTex;
    
    // Background
    private static BackgroundDef background;
    private static BackgroundDef backgroundFloor;

//    private static BackgroundDef backgroundBossMainA ;
//    private static BackgroundDef backgroundBossMainB ;
//    private static BackgroundDef backgroundBossFloor ;
//    
//    private static int backgroundCheck = 0;
    
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
        window.setTitle("Jumpin Story");
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
		
		grassTex = glTexImageTGAFile(gl, "res/tileSky.tga", tileSize);
		groundTex = glTexImageTGAFile(gl, "res/tileGround.tga", tileSize);
		playerTex = glTexImageTGAFile(gl, "res/mapleDefault.tga", spriteSize);
		
		FrameDef[] mapleRun = { new FrameDef(glTexImageTGAFile(gl, "res/mapleRun1.tga", spriteSize), 150),
				new FrameDef(glTexImageTGAFile(gl, "res/mapleRun2.tga", spriteSize), 150), 
				new FrameDef(glTexImageTGAFile(gl, "res/mapleRun4.tga", spriteSize), 150)
				};
		AnimationDef mapleRunDef = new AnimationDef("mapleRun", mapleRun);
		AnimationData mapleRunData = new AnimationData(mapleRunDef);
		
		background = new BackgroundDef(worldX,worldY,grassTex, 0, worldX * worldY, false, false);
		background.setTile(groundTex, 0, worldX, true, false); // Top Border
		background.setTile(groundTex, worldX * ((worldY*2/3)+3),worldX * worldY, true, false );//Bottom Border
		background.setTile(groundTex, worldX * (worldY/3),worldX * worldY/3 + 1, true, false );
		
		for (int i=3; i < worldY/8 + 2; i++)
		{
			background.setTile(groundTex,i * worldX + 6,i * worldX + 10, true, true);
			background.setTile(groundTex,i * worldX + 15,i * worldX + 18, true, true);
		}
		for (int i=0; i < worldY; i++)
		{
			background.setTile(groundTex,i * worldX,i * worldX + 1, true, false);
		}
		
		Player player = new Player(spritePos[0], spritePos[1], spriteSize[0], spriteSize[1], playerTex);

//		backgroundBossMainA = new BackgroundDef(bossSkyTexA, true, 0, 50, 10, 10);
//		backgroundBossMainB = new BackgroundDef(bossSkyTexB, true, 0, 50, 10, 10);
//		backgroundBossFloor = new BackgroundDef(bossGroundtex, false, 50, 60, 10, 10);
			
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
        
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
            lastFrameNS = curFrameNS;
            
            curFrameNS = System.nanoTime();
            long deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
			curFrameMs = curFrameNS / 1000000;
 
    		System.out.println(spritePrevX);
            // Actually, this runs the entire OS message pump.
            window.display();
            
            if (player.isJumping()) {
				//playerJumpAnimation.updateSprite(delta);
				//player.setCurrentTexture(playerJumpAnimation.getCurrentFrame());

				player.setY((int) (player.getY() + player.getyVelocity()));
				player.setyVelocity(player.getyVelocity() + player.getAcceleration());
			} else {

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
										player.setY(spritePrevY-5);
										player.setX(player.getX() + player.getxVelocity()/10);
										player.setxVelocity(0);
										player.setBounce(true);
										player.setJumping(false);
										player.setyVelocity(0);
										}
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
            
            /**
			 * Player movement code.
			 */
			if (kbState[KeyEvent.VK_A]) {
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
			if (kbState[KeyEvent.VK_D] && player.getX() < worldX * tileSize[0] - spriteSize[0]) {
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
			 * Press W to jump. Change number in set velocity to determine how
			 * high player jumps.
			 */
			if (kbState[KeyEvent.VK_W]) {
				if (player.isJumping() == false && player.getyVelocity() == 0)
				{
				player.setJumping(true);
				player.setBounce(false);
				player.setyVelocity(-7);
				player.setY(player.getY() - 3);
				}
			}

			// Does nothing as of now
			if (kbState[KeyEvent.VK_S] && player.getY() < background.getHeight() * tileSize[1] - spriteSize[1]) {
				// player.setJumping(true);
				//player.setY(player.getY() + 10);
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

            // Draw the sprite
			if (AABBIntersect(camera.getAabb(), player.getHitbox()))
			{
				glDrawSprite(gl, player.getCurrentTexture(), player.getX() - camera.getX(),player.getY() - camera.getY(), player.getWidth(), player.getHeight(), player.getReverse());
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
