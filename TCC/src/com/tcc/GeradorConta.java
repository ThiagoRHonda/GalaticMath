package com.tcc;

import java.util.Random;

//import java.applet.*;


public class GeradorConta {
	
	private Random rand = new Random();
	private int a = 0;
	private int b = 0;
	private int r = 0;
	private int m = 0;
	//private int x = 0;
	private int c = 0;
	//private boolean s;
	//private Scanner input = new Scanner(System.in);
	String conta;
	
	public String gSoma() {
		//int a, b, c;
		boolean s;
		String conta;
		
		a = rand.nextInt(11); b = rand.nextInt(11); c = rand.nextInt(11);
		
		s = rand.nextBoolean();
		if(s == true) {
			r = a + b;
			conta = a + " + " + b + " = ";
		} else {
			r = a + b + c;
			conta = a + " + " + b + " + " + c + " = ";
		}
		
		return conta;
	}
	
	public String gSub() {
		//int a, b;
		String conta;
		
		a = rand.nextInt(21); b = rand.nextInt(21);
		
		if(a > b) {
			r = a - b;
			conta = a + " - " + b + " = ";
		} else {
			r = b - a;
			conta = b + " - " + a + " = ";
		}
		
		return conta;
	}
	
	public String gDiv() {
		//int a, b, r;
		String conta2;
		boolean s;
		
		a = rand.nextInt(9)+1; b = rand.nextInt(10)+1;
		m = a * b;
		s = rand.nextBoolean();
		
		if(s == true) {
			r = m / b;
			conta2 = r + " / " + b + " = ";
		} else {
			r = m / a;
			conta2 = r + " / " + a + " = ";
		}
		
		return conta2;
	}
	
	public String gMult() {
		//int a, b;
		String conta2;
		boolean s;
		
		a = rand.nextInt(9)+1; b = rand.nextInt(10)+1;
		s = rand.nextBoolean();
		
		if(s == true) {
			r = a * b;
			conta2 = a + " x " + b + " = ";
		} else {
			r = b * a;
			conta2 = b + " x " + a + " = ";
		}
		
		return conta2;
	}
	
	private void randConta() {
		int c = 0;
		
		
		c = rand.nextInt(4);
		if(c == 0) {
			conta = gSoma();
		} else if(c == 1) {
			conta = gSub();
		} else if(c == 2) {
			conta = gDiv();
		} else if(c == 3) {
			conta = gMult();
		}
		
	}
		
	public String checkScore(int c) {
		

			
			if(c == 0) {
				conta = gSoma();
			} else if(c == 1) {
				conta = gSub();
			} else if(c == 2) {
				conta = gDiv();
			} else if(c == 3) {
				conta = gMult();
			} else {
				randConta();
			}
			
	
		return conta;
		
	}
	
	public int result() {
		return r;
	}

}
