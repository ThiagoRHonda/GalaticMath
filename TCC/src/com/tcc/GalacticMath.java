package com.tcc;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.applet.*;
import java.util.*;
import java.lang.System;


@SuppressWarnings("serial")
public class GalacticMath extends Applet implements Runnable, KeyListener {
	
	

	//Constantes globais
	static int screenWidth = 800;
	static int screenHeight = 600;
	static int centerx = screenWidth / 2;
	static int centery = screenHeight / 2;
	static int bullets = 10;
	static int bullet_speed = 4;
	static double acceleration = 0.05;
	
	//Esatdo do jogo
	final int GAMERUNNING = 1;
	final int GAMEMENU = 0;
	final int GAMEOVER = 2;
	
	//Estado do sprite
	static int sprite_normal = 0;
	static int sprite_collided = 1;
	static int sprite_exploding=2;
	
	//A thread principal do game loop
	Thread gameloop;
		
	//Objetos de buffer
	BufferedImage backbuffer;
	Graphics2D g2d;
	
	boolean collisionTesting = true;
	long collisionTimer = 0;
		
	//Define os objetos do game
	Sprite nave;
	Sprite enemy;
	ImageEntity background, barFrame;
	ImageEntity[] barImage = new ImageEntity[2];
	Sprite[] bullet = new Sprite[bullets], ebullet = new Sprite[bullets];
	AnimatedSprite explosion;
	int currentBullet = 0;
	String letra = "", conta = "";
	String[] enemyType = {"enemyship1.png", "enemyship2.png", "enemyship3.png", "enemyship4.png"};
	int resultado = 0, tamanho= 0;
	GeradorConta gc = new GeradorConta();
	
	//Medidor de vida
	int vida = 20;
	int evida = 25;
	int score = 0;
	int highscore = 0;
	int gamestate = GAMEMENU;
	
	//Cria um gerador de numero aleatorio
	Random rand = new Random();
	
	//Define os objetos de efeitos de som
	SoundClip shoot = new SoundClip("shoot.au");
	SoundClip explode = new SoundClip("explode.au");
	
	//Maneira de manipular multiplas teclas pressionadas
	boolean keyFire, keyNum;
	
	//Contador de frame
	int frameCount = 0, frameRate = 0, fps = 60;
	long startTime = System.currentTimeMillis();
	
	//Evento de iniciacao do applet
	public void init() {
		//Cria o backbuffer
		backbuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		
		//Carrega a imagem de fundo
		background = new ImageEntity(this);
		background.load("bluespace.png");
		
		//Carrega a nave
		nave = new Sprite(this, g2d);
		nave.load("spaceship.png");
		nave.setPosition(new Point2D(centerx - nave.imageWidth()/2, 500));
		nave.setAlive(true);
		
		//Carrega o inimigo
		enemy = new Sprite(this, g2d);
		enemy.load("enemyship2.png");
		enemy.setPosition(new Point2D(centerx - enemy.imageWidth()/2, 20));
		enemy.setAlive(true);
			
		//Carrega as bullets
		for(int n =0; n<bullets; n++) {
			bullet[n] = new Sprite(this, g2d);
			ebullet[n] = new Sprite(this, g2d);
			bullet[n].load("laserRed.png");
			ebullet[n].load("laserGreen.png");
		}
		
		//Carrega as explosoes
		explosion = new AnimatedSprite(this, g2d);
		explosion.load("explosion.png", 4, 4, 96,96);
		explosion.setFrameDelay(2);
		explosion.setAlive(false);
		
		//Carrega o medidor de vida
		barFrame = new ImageEntity(this);
		barFrame.load("barframe.png");
		barImage[0] = new ImageEntity(this);
		barImage[0].load("bar_health.png");
		barImage[1] = new ImageEntity(this);
		barImage[1].load("bar_shield.png");
		
		checkScore();
		
		addKeyListener(this);
		
		
	}
			
	public void update(Graphics g) {
		//Calcula o frame rate
		frameCount++;
		if(System.currentTimeMillis() > startTime + 1000) {
			startTime = System.currentTimeMillis();
			frameRate = frameCount;
			frameCount = 0;
		}
		
		
		//Dependendo do estado de jogo é atualizado
		if(gamestate == GAMEMENU) {
			g2d.setFont(new Font("Verdana", Font.BOLD, 36));
			g2d.drawImage(background.getImage(), 0, 0, screenWidth - 1, screenHeight - 1, this);
			g2d.drawString("Galactic Math", 252, 202);
			g2d.setColor(new Color(200,30,30));
			g2d.drawString("Galactic Math", 250, 200);
			int x = 270, y = 15;
			g2d.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, 20));
			g2d.setColor(Color.YELLOW);
			g2d.drawString("Controles", x, ++y*20);
			g2d.drawString("Use o teclado numerico para digitar os numeros.", x+20, ++y*20);
			g2d.drawString("Use enter para confirmar.", x+20, ++y*20);
			
			g2d.setFont(new Font("Ariel", Font.BOLD, 24));
			g2d.setColor(Color.ORANGE);
			g2d.drawString("Pressione barra de espaço para começar!", 280, 570);
			
		} else if(gamestate == GAMERUNNING) {
			//Desenha o plano de fundo
			g2d.drawImage(background.getImage(), 0, 0, screenWidth - 1, screenHeight - 1, this);
			
			//Desenha os graficos do game
			drawShip();
			drawBullets();
			drawEbullets();
			drawExplosions();
			
			//Desenha as operacoes e resposta
			g2d.setFont(new Font("Verdana", Font.BOLD, 24));
			g2d.setColor(Color.WHITE);
			g2d.drawString(conta, centerx, centery);
			g2d.setFont(new Font("Verdana", Font.BOLD, 24));
			g2d.setColor(Color.WHITE);
			g2d.drawString(letra, centerx - 6, 470);
			

			//Desenha o medidor de vida
			g2d.drawImage(barFrame.getImage(), screenWidth - 132, 18, this);
			for(int n= 0; n < vida; n++) {
				int dx = screenWidth - 130 + n * 5;
				g2d.drawImage(barImage[0].getImage(), dx, 20, this);
			}
			
			g2d.drawImage(barFrame.getImage(), screenWidth - 132, 38, this);
			for(int n= 0; n < evida; n++) {
				int dx = screenWidth - 130 + n * 4;
				g2d.drawImage(barImage[1].getImage(), dx, 40, this);
			}
			
			//verifica a saudo do jogador e inimigo
			if(vida <= 0) {
				gamestate = GAMEOVER;
			} else if(evida <=0) {
				score++;
				enemy.setAlive(false);
				enemy.load(enemyType[rand.nextInt(4)]);
				enemy.setPosition(new Point2D(centerx - enemy.imageWidth()/2, 20));
				enemy.setAlive(true);
				evida = 25;
				if(score % 3 == 0 && score > 0) {
					highscore++;
				}
			}
			
		} else if(gamestate == GAMEOVER) {
			g2d.setFont(new Font("Verdana", Font.BOLD, 36));
			g2d.setColor(new Color(200,30,30));
			g2d.drawString("FIM DE JOGO", 270, 200);
			g2d.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
		}
		
		//Redesenha a janela do applet
		paint(g);
	}
	
	//drawShip chamado pelo update do Applet
	public void drawShip() {
		//Seta a transformacao para a imagem
		nave.transform();
		nave.draw();
		enemy.transform();
		enemy.draw();	
	}
	
	//drawBullets chamado pelo update do Applet
	public void drawBullets() {
		for(int n = 0; n < bullets; n++) {
			if(bullet[n].alive()) {
				//Desenha a bullet
				bullet[n].transform();
				bullet[n].draw();
			}
		}
	}
	
	//drawEbullets chamado pelo update do Applet
	public void drawEbullets() {
		for(int n = 0; n < bullets; n++) {
			if(ebullet[n].alive()) {
				//Desenha a bullet
				ebullet[n].transform();
				ebullet[n].draw();
			}
		}
	}
	
	public void drawExplosions() {
		//Explosoes nao precisam de um metodo update separado
		if(explosion.alive()) {
			explosion.updateAnimation();
			if(explosion.currentFrame() == explosion.totalFrames()-1) {
				explosion.setCurrentFrame(0);
				explosion.setAlive(false);
			}
			else {
				explosion.draw();
			}
		}
	}
	
	//A janela do applet os eventos na tela	
	public void paint(Graphics g) {
		g.drawImage(backbuffer, 0, 0, this);
		
	}
	
	//Thread e inicio do game - inicia o game loop
	public void start() {
		gameloop = new Thread(this);
		gameloop.start();
	}
	
	//Thread de stop
	public void stop() {
		gameloop = null;
	}
	
	//Thread de run do evento (game loop)
	public void run() {
		Thread t = Thread.currentThread();
		//Continua enquanto o thread estiver ativo
		while(t == gameloop) {
			try {
				Thread.sleep(1000/fps);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameUpdate();
			repaint();
		}
	}
	
	//Move a anima os objetos no game
	private void gameUpdate() {
		updateBullets();
		if(collisionTesting) {
			checkCollisions();
			handleEnemyCollisions();
			handleShipCollisions();
			handleBulletCollisions();
		}
	}
	
	//Atualiza as bullets baseado na velocidade
	public void updateBullets() {
		//Move as bullets
		for(int n = 0; n<bullets; n++) {
			if(bullet[n].alive()) {
				//Atualiza a posicao x da bullet
				bullet[n].updatePosition();
				//Bullet desaparece nas extremidades esquerda/direita
				if(bullet[n].position().X() < 0 || bullet[n].position().X() > screenWidth) {
					bullet[n].setAlive(false);
				}
				
				//Atualiza a posicao y da bullet
				bullet[n].updatePosition();
				//Bullet desaparece nas extremidades topo/baixo
				if(bullet[n].position().Y() < 0 || bullet[n].position().Y() > screenHeight) {
					bullet[n].setAlive(false);
				}
				
				bullet[n].setState(sprite_normal);
			}
		}
		
		for(int n = 0; n<bullets; n++) {
			if(ebullet[n].alive()) {
				//Atualiza a posicao x da bullet
				ebullet[n].updatePosition();
				//Bullet desaparece nas extremidades esquerda/direita
				if(ebullet[n].position().X() < 0 || ebullet[n].position().X() > screenWidth) {
					ebullet[n].setAlive(false);
				}
				
				//Atualiza a posicao y da bullet
				ebullet[n].updatePosition();
				//Bullet desaparece nas extremidades topo/baixo
				if(ebullet[n].position().Y() < 0 || ebullet[n].position().Y() > screenHeight) {
					ebullet[n].setAlive(false);
				}
				
				ebullet[n].setState(sprite_normal);
			}
		}
	}
	
	//Testa as colisoes
	public void checkCollisions() {
		//testa colisoes entre nave e projetil
		for(int n = 0; n < bullets; n++) {
			if(ebullet[n].alive()) {
				if(nave.collidesWith(ebullet[n])) {
					nave.setState(sprite_collided);
					vida -= 5;
					ebullet[n].setAlive(false);
					explode.play();
				}
			}
		}
		
		for(int n = 0; n < bullets; n++) {
			if(bullet[n].alive()) {
				if(enemy.collidesWith(bullet[n])) {
					enemy.setState(sprite_collided);
					evida -=5;
					bullet[n].setAlive(false);
					explode.play();
				}
			}
		}
	}
	
	public void handleShipCollisions() {
		if(nave.state() == sprite_collided) {
			collisionTimer = System.currentTimeMillis();
			nave.setVelocity(new Point2D(0,0));
			nave.setState(sprite_exploding);
			startExplosion(nave);
		}
		else if(nave.state() == sprite_exploding) {
			if(collisionTimer + 3000 < System.currentTimeMillis()) {
				nave.setState(sprite_normal);
			}
		}
	}
	
	public void startExplosion(Sprite sprite) {
		if(!explosion.alive()) {
			double x = sprite.position().X();
			double y = sprite.position().Y();
			explosion.setPosition(new Point2D(x, y));
			explosion.setCurrentFrame(0);
			explosion.setAlive(true);
		}
	}
	
	public void handleBulletCollisions() {
		for(int n = 0; n<bullets; n++) {
			if(bullet[n].state() == sprite_collided) {
				bullet[n].setAlive(false);
			}
		}
	}
	
	public void handleEnemyCollisions() {
		if(enemy.state() == sprite_collided) {
			collisionTimer = System.currentTimeMillis();
			enemy.setVelocity(new Point2D(0,0));
			enemy.setState(sprite_exploding);
			startExplosion(enemy);
		}
		else if(enemy.state() == sprite_exploding) {
			if(collisionTimer + 3000 < System.currentTimeMillis()) {
				enemy.setState(sprite_normal);
			}
		}
	}
	
	//Eventos de key listener
	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {}
	
	public void keyReleased(KeyEvent k) {
		switch(k.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			keyFire = false;
			fireBullet();
			break;
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_0:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_1:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_2:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_3:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_4:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD5:
		case KeyEvent.VK_5:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_6:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD7:
		case KeyEvent.VK_7:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_8:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD9:
		case KeyEvent.VK_9:
			updateLetra(k);
			break;
		case KeyEvent.VK_ENTER:
			if(letra == "") {
				checkScore();
				letra = "";
				enemyFireBullet();
				shoot.play();
			} else if(Integer.valueOf(letra) == resultado) {
				checkScore();
				letra = "";
				fireBullet();
			} else {
				checkScore();
				letra = "";
				enemyFireBullet();
			}
			break;
		case KeyEvent.VK_SPACE:
			if(gamestate == GAMEMENU) {
				gamestate = GAMERUNNING;
			}
			break;
		
		case KeyEvent.VK_BACK_SPACE:
			letra = "";
			break;
		}
	}
	
	public void fireBullet() {
		//Atira a bullet
		currentBullet++;
		if(currentBullet > bullets-1) currentBullet = 0;
		bullet[currentBullet].setAlive(true);
		
		//Seta o ponto de inicio da bullet
		int w = bullet[currentBullet].imageWidth();
		int h = bullet[currentBullet].imageHeight();
		double x = nave.center().X() - w/2;
		double y = nave.center().Y() - h/2;
		bullet[currentBullet].setPosition(new Point2D(x,y));
		
		//A bullet aponta na mesma direcao que a frente da nave
		bullet[currentBullet].setFaceAngle(nave.faceAngle());
		bullet[currentBullet].setMoveAngle(nave.faceAngle() - 90);
		
		//Atira a bullet no angulo da nave
		double angle = bullet[currentBullet].moveAngle();
		double svx = calcAngleMoveX(angle) * bullet_speed;
		double svy = calcAngleMoveY(angle) * bullet_speed;
		bullet[currentBullet].setVelocity(new Point2D(svx, svy));
		
		shoot.play();
	}
	
	public void enemyFireBullet() {
		//Atira a bullet
		currentBullet++;
		if(currentBullet > bullets-1) currentBullet = 0;
		ebullet[currentBullet].setAlive(true);
		
		//Seta o ponto de inicio da bullet
		int w = ebullet[currentBullet].imageWidth();
		int h = ebullet[currentBullet].imageHeight();
		double x = enemy.center().X() - w/2;
		double y = enemy.center().Y() - h/2;
		ebullet[currentBullet].setPosition(new Point2D(x,y));
		
		//A bullet aponta na mesma direcao que a frente da nave
		ebullet[currentBullet].setFaceAngle(enemy.faceAngle());
		ebullet[currentBullet].setMoveAngle(enemy.faceAngle() - 270);
		
		//Atira a bullet no angulo da nave
		double angle = ebullet[currentBullet].moveAngle();
		double svx = calcAngleMoveX(angle) * bullet_speed;
		double svy = calcAngleMoveY(angle) * bullet_speed;
		ebullet[currentBullet].setVelocity(new Point2D(svx, svy));
		
		shoot.play();
	}
	
	//Calculo do movimento angular de x e y
	public double calcAngleMoveX(double angle) {
		double movex = Math.cos(angle * Math.PI / 180);
		return movex;
	}
	
	public double calcAngleMoveY(double angle) {
		double movey = Math.sin(angle * Math.PI / 180);
		return movey;
	}
	
	//atualiza o numero digitado
	public void updateLetra(KeyEvent k) {
		letra += k.getKeyChar();
	}
	
	//verifica a quantidade de inimigos derrotados
	public void checkScore() {
	
		conta = gc.checkScore(highscore);
		resultado = gc.result();
		
	}

 }