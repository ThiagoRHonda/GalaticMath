package com.tcc;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;

public class SoundClip {
	//a fonte do audio
	private AudioInputStream sample;
	
	//a propriedade do som é somente leitura
	private Clip clip;
	public Clip getClip() { return clip; }
	
	//propriedade de repeticao para tocar o audio varias vezes
	private int repeat = 0;
	public void setRepeat(int _repeat) { repeat = _repeat; }
	public int getRepeat() { return repeat; }
	
	//propriedade do nome do arquivo
	private String filename = "";
	public void setFilename(String _filename) { filename = _filename; }
	public String getFilename() { return filename; }
	
	//propriedade para verificar se o arquivo esta pronto
	public boolean isLoaded() {
		return (boolean)(sample != null);
	}
	
	//construtor
	public SoundClip() {
		try {
			//cria um buffer de som
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {}
	}
	
	//construtor que recebe um arquivo de audio como parametro
	public SoundClip(String audiofile) {
		this(); //chama o construtor padrao
		load(audiofile); //entao carrega o arquivo de audio
	}
	
	private URL getURL(String filename) {
		URL url = null;
		try {
			url = this.getClass().getResource(filename);
		}
		catch (Exception e) {}
			return url;
	}
	
	//carrega o arquivo de som
	public boolean load(String audiofile) {
		try {
			setFilename(audiofile);
			sample = AudioSystem.getAudioInputStream(getURL(filename));
			clip.open(sample);
			return true;
		} catch (IOException e) {
			return false;
		} catch (UnsupportedAudioFileException e) {
			return false;
		} catch (LineUnavailableException e) {
			return false;
		}
	}
	
	//toca o som
	public void play() {
		if(!isLoaded()) return;
		
		clip.setFramePosition(0);
		
		clip.loop(repeat);
	}
	
	//interrompe o som
	public void stop() {
		clip.stop();
	}
}
