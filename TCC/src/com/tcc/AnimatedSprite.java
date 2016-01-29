package com.tcc;

import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.net.*;
import java.util.*;

public class AnimatedSprite extends Sprite {
    //A imagem do bitmap
    private Image animImage;
    //Imagem temporaria passa para o metodo draw pai
    BufferedImage tempImage;
    Graphics2D tempSurface;
    //Propriedades
    private int currFrame, totFrames;
    private int animDir;
    private int frCount, frDelay;
    private int frWidth, frHeight;
    private int cols;
    
    Random rand = new Random();

    //construtor
    public AnimatedSprite(Applet applet, Graphics2D g2d) {
        super(applet, g2d);
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
        //Carrega o bitmap de animacao
    	Toolkit tk = Toolkit.getDefaultToolkit();
    	animImage = tk.getImage(getURL(filename));
        setColumns(columns);
        setTotalFrames(columns * rows);
        setFrameWidth(width);
        setFrameHeight(height);

        //frame da imagem e passado para classe pai para desenha-la
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
    
    public void updateAnimation() {
        frCount += 1;
        if (frCount > frDelay) {
            frCount = 0;
            //atualiza o frame
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
    	//calcula a posicao do x e y do frame atual
        int frameX = (currentFrame() % columns()) * frameWidth();
        int frameY = (currentFrame() / columns()) * frameHeight();
        
        //copia o frame para a imagem temporaria
        tempSurface.drawImage(animImage, 0, 0, frameWidth() -1, frameHeight() -1, frameX, frameY, frameX+frameWidth(), frameY+frameHeight(), applet());
        
        //passa a imagem temporaria para a classe pai e a desenha
        super.setImage(tempImage);
        super.transform();
        super.draw();
    }
}

