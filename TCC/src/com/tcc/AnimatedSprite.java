/*****************************************************
* Beginning Java Game Programming, 2nd Edition
* by Jonathan S. Harbour
* AnimatedSprite class
*****************************************************/
package com.tcc;

import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.net.*;
import java.util.*;

public class AnimatedSprite extends Sprite {
    //this image holds the large tiled bitmap
    private Image animImage;
    //temp image passed to parent draw method
    BufferedImage tempImage;
    Graphics2D tempSurface;
    //custom properties
    private int currFrame, totFrames;
    private int animDir;
    private int frCount, frDelay;
    private int frWidth, frHeight;
    private int cols;
    
    Random rand = new Random();

    public AnimatedSprite(Applet applet, Graphics2D g2d) {
        super(applet, g2d);
        //animImage = new ImageEntity(applet);
        currFrame = 0;
        totFrames = 0;
        animDir = 1;
        frCount = 0;
        frDelay = 0;
        frWidth = 0;
        frHeight = 0;
        cols = 0;
    }
    
    private URL getURL(String filename) {
    	URL url = null;
    	try {
    		url = this.getClass().getResource(filename);
    	}
    	catch(Exception e) {}
    	
    	return url;
    }

    public void load(String filename, int columns, int rows,
        int width, int height)
    {
        //load the tiled animation bitmap
    	Toolkit tk = Toolkit.getDefaultToolkit();
    	animImage = tk.getImage(getURL(filename));
        //animImage.load(filename);
        setColumns(columns);
        setTotalFrames(columns * rows);
        setFrameWidth(width);
        setFrameHeight(height);

        //frame image is passed to parent class for drawing
        tempImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        tempSurface = tempImage.createGraphics();
        super.setImage(tempImage);
    }

    public int currentFrame() { return currFrame; }
    public void setCurrentFrame(int frame) { currFrame = frame; }

    public int frameWidth() { return frWidth; }
    public void setFrameWidth(int width) { frWidth = width; }

    public int frameHeight() { return frHeight; }
    public void setFrameHeight(int height) { frHeight = height; }

    public int totalFrames() { return totFrames; }
    public void setTotalFrames(int total) { totFrames = total; }

    public int animationDirection() { return animDir; }
    public void setAnimationDirection(int dir) { animDir = dir; }

    public int frameDelay() { return frDelay; }
    public void setFrameDelay(int delay) { frDelay = delay; }

    public int columns() { return cols; }
    public void setColumns(int num) { cols = num; }

   /* public Image getAnimImage() { return animImage.getImage(); }
    public void setAnimImage(Image image) { animImage.setImage(image); }
    */
    
    public void updateAnimation() {
        frCount += 1;
        if (frCount > frDelay) {
            frCount = 0;
            //update the animation frame
            currFrame += animDir;
            if (currFrame > totFrames - 1) {
                currFrame = 0;
            }
            else if (currFrame < 0) {
                currFrame = totFrames - 1;
            }
        }
    }
    
    public void draw() {
    	//calculate the current frame's X and Y position
        int frameX = (currentFrame() % columns()) * frameWidth();
        int frameY = (currentFrame() / columns()) * frameHeight();
        
        //copy the frame onto the temp image
        tempSurface.drawImage(animImage, 0, 0, frameWidth() -1, frameHeight() -1, frameX, frameY, frameX+frameWidth(), frameY+frameHeight(), applet());
        
        //pass the temp image on to the parent class and draw it
        super.setImage(tempImage);
        super.transform();
        super.draw();
    }

   /* public void updateFrame() {
        if (totFrames > 0) {
            //calculate the current frame's X and Y position
            int frameX = (currentFrame() % columns()) * frameWidth();
            int frameY = (currentFrame() / columns()) * frameHeight();

            if (tempImage == null) {
                tempImage = new BufferedImage(frameWidth(), frameHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
                tempSurface = tempImage.createGraphics();
            }

            //copy the frame onto the temp image
            if (animImage.getImage() != null) {
                tempSurface.drawImage(animImage.getImage(), 0, 0, frameWidth() - 1,
                frameHeight() - 1, frameX, frameY,
                frameX + frameWidth(),
                frameY + frameHeight(), applet());
            }
            //pass the temp image on to the parent class and draw it
            super.setImage(tempImage);
            super.transform();
            super.draw();
        }
    }*/

}

