package com.pidlye.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.spidlye.main.Game;
import com.spidlye.world.Camera;
import com.spidlye.world.Tile;
import com.spidlye.world.WallTile;
import com.spidlye.world.World;

public class BulletShoot extends Entity {
	
	
	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 100, curLife = 0;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
		if(!World.isFree2((int)(x),this.getY())) {
			if(curLife > 2) {
				Game.bullets.remove(this);
			}
		}
	}
	
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}

}
