package com.tcc;

import java.awt.*;
import java.applet.*;

public class Sprite extends Object {
    private ImageEntity entity;
    protected Point2D pos;
    protected Point2D vel;
    protected double rotRate;
    protected int currentState;
    protected int sprType;
    protected boolean _collided;
    protected int _lifespan, _lifeage;

    //construtor
    Sprite(Applet a, Graphics2D g2d) {
        entity = new ImageEntity(a);
        entity.setGraphics(g2d);
        entity.setAlive(false);
        pos = new Point2D(0, 0);
        vel = new Point2D(0, 0);
        rotRate = 0.0;
        currentState = 0;
        _collided = false;
        _lifespan = 0;
        _lifeage = 0;
    }

    //carrega o arquivo bitmap
    public void load(String filename) {
        entity.load(filename);
    }

    //faz transformacoes
    public void transform() {
        entity.setX(pos.X());
        entity.setY(pos.Y());
        entity.transform();
    }

    //desenha a imagem
    public void draw() {
        entity.g2d.drawImage(entity.getImage(),entity.at,entity.applet);
    }

    //desenho o retangulo limiar no sprite
    public void drawBounds(Color c) {
        entity.g2d.setColor(c);
        entity.g2d.draw(getBounds());
    }

    //atualiza a posicao 
    public void updatePosition() {
        pos.setX(pos.X() + vel.X());
        pos.setY(pos.Y() + vel.Y());
    }

    //funcoes da variavel de estado do sprite
    public int state() { return currentState; }
    public void setState(int state) { currentState = state; }

    //retorna o retangulo limiar
    public Rectangle getBounds() { return entity.getBounds(); }

    //posicao do sprite
    public Point2D position() { return pos; }
    public void setPosition(Point2D pos) { this.pos = pos; }

    //velocidade de movimento do sprite
    public Point2D velocity() { return vel; }
    public void setVelocity(Point2D vel) { this.vel = vel; }

    //retorna o centro do sprite com um Point2D
    public Point2D center() {
        return(new Point2D(entity.getCenterX(),entity.getCenterY()));
    }

    //verifica se o sprite esta ativo
    public boolean alive() { return entity.isAlive(); }
    public void setAlive(boolean alive) { entity.setAlive(alive); }

    //indica a direcao em que o sprite esta de frente
    public double faceAngle() { return entity.getFaceAngle(); }
    public void setFaceAngle(double angle) {
        entity.setFaceAngle(angle);
    }
    public void setFaceAngle(float angle) {
        entity.setFaceAngle((double) angle);
    }
    public void setFaceAngle(int angle) {
        entity.setFaceAngle((double) angle);
    }
    
    public void setMoveX(double move) {
    	entity.incX(move);
    }
    
    public double moveX() {return entity.getX();}

    //indica a direcao em que o sprite esta se movendo
    public double moveAngle() { return entity.getMoveAngle(); }
    public void setMoveAngle(double angle) {
        entity.setMoveAngle(angle);
    }
    public void setMoveAngle(float angle) {
        entity.setMoveAngle((double) angle);
    }
    public void setMoveAngle(int angle) {
        entity.setMoveAngle((double) angle);
    }

    //retorna a altura e largura da imagem
    public int imageWidth() { return entity.width(); }
    public int imageHeight() { return entity.height(); }

    //checa a colisao com um retangulo
    public boolean collidesWith(Rectangle rect) {
        return (rect.intersects(getBounds()));
    }
    
    //checa a colisao com outro sprite
    public boolean collidesWith(Sprite sprite) {
        return (getBounds().intersects(sprite.getBounds()));
    }

    public Applet applet() { return entity.applet; }
    public Graphics2D graphics() { return entity.g2d; }
    public Image image() { return entity.image; }
    public void setImage(Image image) { entity.setImage(image); }

    public int spriteType() { return sprType; }
    public void setSpriteType(int type) { sprType = type; }

    public boolean collided() { return _collided; }
    public void setCollided(boolean collide) { _collided = collide; }

}
