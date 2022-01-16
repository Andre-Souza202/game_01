package com.spidlye.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {

	private AudioClip clip;
	
	
	public static final Sound musicLevel = new Sound ("/LevelMusic.wav");
	public static final Sound musicMenu = new Sound("/MenuMusic.wav");
	public static final Sound hurt = new Sound("/Hurt.wav");
	public static final Sound getGun = new Sound("/getGun.wav");
	public static final Sound gameOver = new Sound("/GameOver.wav");
	public static final Sound getLife = new Sound("/Life.wav");
	public static final Sound shoot = new Sound("/Shoot.wav");
	public static final Sound reload = new Sound("/Reload .wav");
	
	
	public Sound(String name) {
		try {
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		} catch(Throwable e){}
	}
	
	public void play(){
		try {
			new Thread() {
				public void run() {
					clip.play();;
				}
			}.start();
		}
		catch(Throwable e) {}
	}
	
	public void loop(){
		try {
			new Thread() {
				public void run() {
					clip.loop();;
				}
			}.start();
		}
		catch(Throwable e) {}
	}
	
	public void stop(){
		try {
			new Thread() {
				public void run() {
					clip.stop();;
				}
			}.start();
		}
		catch(Throwable e) {}
	}
	
}
