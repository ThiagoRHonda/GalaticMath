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
	static int asteroids = 10;
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
	int resultado = 0, tamanho= 0;
	
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
	boolean keyDown, keyUp, keyLeft, keyRight, keyFire, keyNum;
	
	//Contador de frame
	int frameCount = 0, frameRate = 0;
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
		nave.setPosition(new Point2D(centerx, 550));
		nave.setAlive(true);
		
		//Carrega o inimigo
		
		enemy = new Sprite(this, g2d);
		enemy.load("enemyship2.png");
		enemy.setPosition(new Point2D(centerx, 20));
		enemy.setAlive(true);
			
		//Carrega as bullets
		for(int n =0; n<bullets; n++) {
			bullet[n] = new Sprite(this, g2d);
			ebullet[n] = new Sprite(this, g2d);
			bullet[n].load("plasmashot.png");
			ebullet[n].load("plasmashot.png");
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
		
		conta = gSoma();
		
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
			g2d.setColor(Color.BLACK);
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
			
			//Desenha os status na tela
			g2d.setColor(Color.WHITE);
			g2d.drawString("FPS: " + frameRate, 5, 10);
			long x = Math.round(nave.position().X());
			long y = Math.round(nave.position().Y());
			g2d.drawString("Nave:" + x + ", " + y, 5, 25);
			g2d.drawString("Move Angle: " + Math.round(nave.moveAngle()) + 90, 5, 40);
			g2d.drawString("Face Angle: " + Math.round(nave.faceAngle()), 5, 55);
			g2d.drawString(conta, 5, 70);
			g2d.drawString(letra, 65, 70);
			
			g2d.setFont(new Font("Verdana", Font.BOLD, 24));
			g2d.setColor(Color.WHITE);
			g2d.drawString(conta, centerx - tamanho/2, centery);
			g2d.setFont(new Font("Verdana", Font.BOLD, 24));
			g2d.setColor(Color.WHITE);
			g2d.drawString(letra, centerx, 535);
			
			
			
			if(nave.state() == sprite_normal)
				g2d.drawString("Estado: NORMAL", 5, 85);
			else if(nave.state() == sprite_collided)
				g2d.drawString("Estado: COLIDIDO", 5, 85);
			else if(nave.state() == sprite_exploding)
				g2d.drawString("Estado: EXPLODINDO", 5, 85);
			
			
			if(enemy.state() == sprite_normal)
				g2d.drawString("Estado do Inimigo: NORMAL", 5, 100);
			else if(enemy.state() == sprite_collided)
				g2d.drawString("Estado do Inimigo: COLIDIDO", 5, 100);
			else if(enemy.state() == sprite_exploding)
				g2d.drawString("Estado do Inimigo: EXPLODINDO", 5, 100);
			
			
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
			
		
			if(vida <= 0) {
				gamestate = GAMEOVER;
			} else if(evida <=0) {
				enemy.setAlive(false);
				enemy.load("enemyship3.png");
				enemy.setPosition(new Point2D(centerx, 20));
				enemy.setAlive(true);
				evida = 25;
			}
			
		} else if(gamestate == GAMEOVER) {
			g2d.setFont(new Font("Verdana", Font.BOLD, 36));
			g2d.setColor(new Color(200,30,30));
			g2d.drawString("FIM DE JOGO", 270, 200);
			g2d.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
			g2d.setColor(Color.ORANGE);
			g2d.drawString("Pressione ESPAÇO para recomeçar", 260, 500);
			stop();
		}
		
		
		
		//Redesenha a janela do applet
		paint(g);
	}
	
	//drawShip chamado pelo update do Applet
	public void drawShip() {
		//Seta a transformacao para a imagem
		nave.transform();
		nave.draw();
		enemy.setFaceAngle(180);
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
				Thread.sleep(17);
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
		//checkInput();
		updateShip();
		updateBullets();
		if(collisionTesting) {
			checkCollisions();
			handleShipCollisions();
			handleEnemyCollisions();
			handleBulletCollisions();
		}
	}
	
	//Atualiza a posicao da nave baseado na velocidade
	public void updateShip() {
		nave.updatePosition();
		double newx = nave.position().X();
		double newy = nave.position().Y();
		
		//Mantem dentro direita/esquerda
		if(nave.position().X() < -10)
			newx = screenWidth + 10;
		else if(nave.position().X() > screenWidth + 10)
			newx = -10;
		
		//Mantem dentro direita/esquerda
		if(nave.position().Y() < -10)
			newy = screenHeight + 10;
		else if(nave.position().Y() > screenHeight + 10)
			newy = -10;
		
		nave.setPosition(new Point2D(newx, newy));
		nave.setState(sprite_normal);
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
			double x = sprite.position().X() - sprite.getBounds().width / 2;
			double y = sprite.position().Y() - sprite.getBounds().height / 2;
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
	
	/*Processa as teclas pressionadas
	public void checkInput() {
		if(keyLeft) {
			//Seta esquerda rotaciona a nave em 5 graus
			nave.setFaceAngle(nave.faceAngle() - 5);
			if(nave.faceAngle() < 0) nave.setFaceAngle(360-5);
		}
		else if (keyRight) {
			//Seta direita rotaciona a navem em 5 graus
			nave.setFaceAngle(nave.faceAngle() + 5);
			if(nave.faceAngle() > 360) nave.setFaceAngle(5);
		}
		
		if(keyUp) {
			//Seta para cima acelera a nave
			applyThrust();
		}
		
	}*/
	
	//Eventos de key listener
	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {
		switch(k.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keyLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = true;
			break;
		case KeyEvent.VK_UP:
			keyUp = true;
			break;
		case KeyEvent.VK_CONTROL:
			keyFire = true;
			break;
		}
	}
	public void keyReleased(KeyEvent k) {
		switch(k.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = false;
			break;
		case KeyEvent.VK_UP:
			keyUp = false;
			break;
		case KeyEvent.VK_CONTROL:
			keyFire = false;
			fireBullet();
			break;
		case KeyEvent.VK_NUMPAD0:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD1:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD2:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD3:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD4:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD5:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD6:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD7:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD8:
			updateLetra(k);
			break;
		case KeyEvent.VK_NUMPAD9:
			updateLetra(k);
			break;
		case KeyEvent.VK_ENTER:
			if(letra == "") {
				checkScore();
				tamanho = conta.length();
				letra = "";
				enemyFireBullet();
				shoot.play();
			} else if(Integer.valueOf(letra) == resultado) {
				checkScore();
				tamanho = conta.length();
				letra = "";
				fireBullet();
				score += 1;
				shoot.play();
			} else {
				checkScore();
				tamanho = conta.length();
				letra = "";
				enemyFireBullet();
				shoot.play();
			}
			break;
		case KeyEvent.VK_SPACE:
			if(gamestate == GAMEMENU) {
				gamestate = GAMERUNNING;
			} else if(gamestate == GAMEOVER) {
				gamestate = GAMERUNNING;
				start();
				run();
			}
			
			break;
		}
	}
	
	public void applyThrust() {
		//Seta para cima adiciona aceleracao na nave
		nave.setMoveAngle(nave.faceAngle() - 90);
		//calcula a velocidade de x e y dependendo da velocidade
		double velx = nave.velocity().X();
		velx += calcAngleMoveX(nave.moveAngle()) * acceleration;
		double vely = nave.velocity().Y();
		vely += calcAngleMoveY(nave.moveAngle()) * acceleration;
		nave.setVelocity(new Point2D(velx, vely));
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
		
		//Toca o som de tiro
		//shoot.play();
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
		ebullet[currentBullet].setMoveAngle(enemy.faceAngle() - 90);
		
		//Atira a bullet no angulo da nave
		double angle = ebullet[currentBullet].moveAngle();
		double svx = calcAngleMoveX(angle) * bullet_speed;
		double svy = calcAngleMoveY(angle) * bullet_speed;
		ebullet[currentBullet].setVelocity(new Point2D(svx, svy));
		
		//Toca o som de tiro
		//shoot.play();
	}
	
	//Movimento angular de x e y calculado
	public double calcAngleMoveX(double angle) {
		double movex = Math.cos(angle * Math.PI / 180);
		return movex;
	}
	
	public double calcAngleMoveY(double angle) {
		double movey = Math.sin(angle * Math.PI / 180);
		return movey;
	}
	
	public void updateLetra(KeyEvent k) {
		letra += k.getKeyChar();
	}
	
	public String gSoma() {
		int a, b, c;
		boolean s;
		String conta;
		
		a = rand.nextInt(11); b = rand.nextInt(11); c = rand.nextInt(11);
		
		s = rand.nextBoolean();
		if(s == true) {
			resultado = a + b;
			conta = a + " + " + b + " = ";
		} else {
			resultado = a + b + c;
			conta = a + " + " + b + " + " + c + " = ";
		}
		
		return conta;
	}
	
	public String gSub() {
		int a, b;
		String conta;
		
		a = rand.nextInt(21); b = rand.nextInt(21);
		
		if(a > b) {
			resultado = a - b;
			conta = a + " - " + b + " = ";
		} else {
			resultado = b - a;
			conta = b + " - " + a + " = ";
		}
		
		return conta;
	}
		
	public void checkScore() {
		if(score < 4) {
			conta = gSoma();
		} else {
			conta = gSub();
		}
	}
	//GeradorConta gc = new GeradorConta();
	/*int g = 0;
		
	while(g < 10) {
		
		if(gc.gSoma()) {
			System.out.println("Esta correto!");
		} else {
			System.out.println("Esta incorreto!");
		}
		
		
		if(gc.gMenos()) {
			System.out.println("Esta correto!");
		} else {
			System.out.println("Esta incorreto!");
		}
	
		g++;
	}*/

 }
