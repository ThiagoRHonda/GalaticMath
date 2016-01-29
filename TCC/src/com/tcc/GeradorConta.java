package com.tcc;

import java.util.Random;

public class GeradorConta {
	
	private Random rand = new Random();
	private int a = 0;
	private int b = 0;
	private int r = 0;
	private int m = 0;
	
	String conta;
	
	//gera operações de soma
	public String gSoma() {

		String conta;
		
		a = rand.nextInt(25)+1; b = rand.nextInt(25)+1;
		
		r = a + b;
		conta = a + " + " + b + " = ";

		
		return conta;
	}
	
	//gera operações de subtração
	public String gSub() {
		
		String conta;
		
		a = rand.nextInt(25)+1; b = rand.nextInt(25)+1;
		
		if(a > b) {
			r = a - b;
			conta = a + " - " + b + " = ";
		} else {
			r = b - a;
			conta = b + " - " + a + " = ";
		}
		
		return conta;
	}
	
	
	//gera operações de divisão
	public String gDiv() {

		String conta2;
		boolean s;
		
		a = rand.nextInt(10)+1; b = rand.nextInt(10)+1;
		m = a * b;
		s = rand.nextBoolean();
		
		if(s == true) {
			r = m / b;
			conta2 = m + " / " + b + " = ";
		} else {
			r = m / a;
			conta2 = m + " / " + a + " = ";
		}
		
		return conta2;
	}
	
	//gera operações de mulitiplicação
	public String gMult() {

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
	
	
	//seleciona uma operação aleatoriamente
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
	
	
	//verifica qual operação deve ser retornada
	public String checkScore(int c) {
		
		if(c == 0) {
			conta = gSoma();
		} else if(c == 1) {
			conta = gSub();
		} else if(c == 2) {
			conta = gMult();
		} else if(c == 3) {
			conta = gDiv();
		} else {
			randConta();
		}
				
		return conta;		
	}
	
	public int result() {
		return r;
	}
	
}
