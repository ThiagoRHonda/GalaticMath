package com.tcc;
import java.awt.Graphics;
import java.util.Random;
import java.util.Scanner;
import java.applet.*;


public class GeradorConta {
	
	private Random rand = new Random();
	private int a = 0;
	private int b = 0;
	private int r = 0;
	private int x = 0;
	private int c = 0;
	private boolean s;
	private String y;
	private Scanner input = new Scanner(System.in);
	Graphics g2d;
	
	public boolean gSoma() {
		
		a = rand.nextInt(11); b = rand.nextInt(11); c = rand.nextInt(11);
		
		s = rand.nextBoolean();
		if(s == true) {
			r = a + b;
			g2d.drawString(a + " + " + b + " = ", 5, 75);
			//System.out.print(a + " + " + b + " = ");
			//y = Integer.toString(a + b);
		} else {
			r = a + b + c;
			g2d.drawString(a + " + " + b + " + " + c + " = ", 5, 75);
			//System.out.print(a + " + " + b + " + " + c + " = ");
			//y = Integer.toString(a + b + c);
		}
		
		x = input.nextInt();
		
		if(r == x) {
			s = true;
		} else {
			s = false;
		}
		
		return s;
		
	}
	
public boolean gMenos() {
		
		a = rand.nextInt(21); b = rand.nextInt(21);
		
		if(b > a) {
			r = b - a;
			System.out.print(b + " - " + a + " = ");
		} else {
			r = a - b;
			System.out.print(a + " - " + b + " = ");
		}
		
		x = input.nextInt(); 
		
		if(x == r) {
			s = true;
		} else {
			s = false;
		}
		
		return s;
		
	}

}
