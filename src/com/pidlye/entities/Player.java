package com.pidlye.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.spidlye.main.Game;
import com.spidlye.main.Sound;
import com.spidlye.world.Camera;
import com.spidlye.world.World;

public class Player extends Entity{
	
	public boolean right, left, up, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public double speed = 0.9;

	private int frames = 0, maxFrames = 11, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	
	private BufferedImage[] playerDamage;
	
	public int ammo = 0, ammoGun = 12;
	
	public boolean reloadCommand = false, reload = false, reloadSound = true;
	
	public boolean hasGun = false;
	
	public boolean shoot = false, mouseShoot = false;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	private int reloadFrames = 0, reloadMaxFrames = 160;
	
	public static double life = 100, maxLife = 100;
	public int mx, my;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		playerDamage = new BufferedImage[4];
		
		playerDamage[0] = Game.spritesheet.getSprite(0, 16, 16, 16);
		playerDamage[1] = Game.spritesheet.getSprite(16, 16, 16, 16);
		playerDamage[2] = Game.spritesheet.getSprite(0, 32, 16, 16);
		playerDamage[3] = Game.spritesheet.getSprite(16, 32, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 16, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 48, 16, 16);
		}
	}
	
	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			x+=speed;
			//dir = right_dir;
		}
		if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			x-=speed;
			//dir = left_dir;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
			//dir = up_dir;
		}
		if(down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
			//dir = down_dir;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		if(reloadCommand) {
			if(!(ammoGun < 12 && ammo > 0)) {
				reloadCommand = false;
			}
			else {
				if(reloadSound) {
					reloadSound = false;
					Sound.reload.play();
				}
				reloadFrames++;
				if(reloadFrames == reloadMaxFrames) {
					reloadFrames = 0;
					reload = true;
					reloadSound = true;
				}
			}
		}
		
		if(reload) {
			reload = false;
				if(ammo >= 12) {
					int ammoCurrent = ammo - (12 - ammoGun);
					ammoGun = 12;
					ammo = ammoCurrent;
				}
				else if(ammo < 12) {
					int ammoCurrent = ammo - (12 - ammoGun);
					if(ammoCurrent <= 0) {
						ammoGun = 12+ammoCurrent;
						ammo = 0;
					}
					else if(ammoCurrent > 0) {
						int test = 0;
						for(int i = 0; i < ammoCurrent && i < 12-ammoGun; i++) {
							test++;
						}
						ammo = ammoCurrent; 
						ammoGun+=test;
					}
				}
			}
		
		
		if(ammoGun == 0 && (mouseShoot || shoot) && reloadSound) {
			mouseShoot = false;
			shoot = false;
			reloadCommand = true;
		}
		
		if(shoot) {
			shoot = false;
			if(reloadCommand == false)
			if(hasGun && ammoGun > 0) {
				ammoGun--;
				Sound.shoot.play();
				int dx = 0;
				int dy = 0;
				int px = 0;
				int py = 8;
				if(dir == right_dir) {
					px = 16;
					dx = 1;	
				}
				else if(dir == left_dir) {
					px = -4;
					dx = -1;
				}
				else if(dir == up_dir) {
					py = 2;
					px = 11;
					dy = -1;
				}
				else if(dir == down_dir) {
					py = 14; 
					px = 2;
					dy = 1;
				}
			
				BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		if(mouseShoot) {
			mouseShoot = false;
			if(reloadCommand == false)
			if(hasGun && ammoGun > 0) {
				ammoGun--;
				Sound.shoot.play();
				int px = 8;
				int py = 8;
				double dy = 0;
				double angle = 0;
				
				if(dir == right_dir) {
					px = 16;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				else if(dir == left_dir) {
					px = -4;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				else if(dir == up_dir) {
					py = 2;
					px = 11;
					dy = -1;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				else if(dir == down_dir) {
					py = 14; 
					px = 2;
					dy = 3;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				
				double dx = Math.cos(angle);
				dy = Math.sin(angle);
			
				BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		//double angleMouse = Math.atan2(300+25 - my,300+25 - mx);
		
		double angleMouse = Math.atan2((this.getX() - Camera.x)+8-(Game.mx/3),(this.getY() - Camera.y)+8-(Game.my/3));
		double angle2 = Math.toDegrees(angleMouse);
		//System.out.println(angle2);
		
		if(angle2 > -45 && angle2 <= 45) {
			dir = up_dir;
		}
		else if(angle2 > 45 && angle2 <= 135) {
			dir = left_dir;
		}
		else if(angle2 > 135 && angle2 >= -135) {
			dir = down_dir;
		}
		else if(angle2 > -135 && angle2 <= -45) {
			dir = right_dir;
		}
		
		if(life <= 0) {
			Sound.musicLevel.stop();
			Sound.gameOver.play();
			Game.gameState = "GAME_OVER";
			Player.life = 0;
		}
		
		this.checkCollisionLifePack();
		this.checkCollisionAmmo();
		this.checkCollisionGun();
		
		if(isDamaged) {
			this.damageFrames++;
			if(damageFrames == 10) {
				damageFrames = 0;
				isDamaged = false;
			}
		}
		updateCamera();
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2-8), 0, World.WIDHT*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2-8), 0, World.HEIGHT*16 - Game.HEIGHT);	
	}
	
	public void checkCollisionGun() {
		for(int i =0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(i);
					Sound.getGun.play();
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i =0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo+= 12;
					//System.out.println("Muninão: "ammo);
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkCollisionLifePack() {
		for(int i =0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof LifePack) {
				if(Entity.isColidding(this, atual)) {
					life+=15;
					if(life > 100)
						life = 100;
					Game.entities.remove(i);
					Sound.getLife.play();
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(dir == right_dir) {
			g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_RIGHT, this.getX()+7 - Camera.x, this.getY() - Camera.y, null);
			}
			if(isDamaged) {
				g.drawImage(playerDamage[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_LEFT, this.getX()-5 - Camera.x, this.getY() - Camera.y, null);
			}
			if(isDamaged) {
				g.drawImage(playerDamage[1], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == up_dir) {
			g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_UP, this.getX()+9 - Camera.x, this.getY()-2 - Camera.y, null);
			}
			if(isDamaged) {
				g.drawImage(playerDamage[2], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == down_dir) {
			g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_DOWN, this.getX() - Camera.x, this.getY()+5 - Camera.y, null);
			}
			if(isDamaged) {
				g.drawImage(playerDamage[3], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
	}

}
