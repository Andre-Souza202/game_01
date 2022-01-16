package com.pidlye.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.spidlye.main.Game;
import com.spidlye.main.Sound;
import com.spidlye.world.Camera;
import com.spidlye.world.World;

public class Enemy extends Entity{

	private double speed = 0.5;
	
	private int damaged = 0;
	private double vidaAntiga;
	
	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxFrames = 11, index = 0, maxIndex = 3;
	
	private int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	private int dir = right_dir;
	
	private boolean moved = false;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	private BufferedImage[] upEnemy;
	private BufferedImage[] downEnemy;
	private BufferedImage[] enemyDamage;
	
	private int enemyLife = 5;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		rightEnemy = new BufferedImage[4];
		leftEnemy = new BufferedImage[4];
		upEnemy = new BufferedImage[4];
		downEnemy = new BufferedImage[4];
		enemyDamage = new BufferedImage[3];

			rightEnemy[0] = Game.spritesheet.getSprite(8*16, 0*16, 16, 16);
			rightEnemy[1] = Game.spritesheet.getSprite(9*16, 0*16, 16, 16);
			rightEnemy[2] = Game.spritesheet.getSprite(8*16, 1*16, 16, 16);
			rightEnemy[3] = Game.spritesheet.getSprite(9*16, 1*16, 16, 16);

			leftEnemy[0] = Game.spritesheet.getSprite(8*16, 2*16, 16, 16);
			leftEnemy[1] = Game.spritesheet.getSprite(9*16, 2*16, 16, 16);
			leftEnemy[2] = Game.spritesheet.getSprite(8*16, 3*16, 16, 16);
			leftEnemy[3] = Game.spritesheet.getSprite(9*16, 3*16, 16, 16);

			downEnemy[0] = Game.spritesheet.getSprite(96, 32, 16, 16);
			downEnemy[1] = Game.spritesheet.getSprite(96, 32, 16, 16);
			downEnemy[2] = Game.spritesheet.getSprite(112, 48, 16, 16);
			downEnemy[3] = Game.spritesheet.getSprite(112, 48, 16, 16);

			upEnemy[0] = Game.spritesheet.getSprite(8*16, 4*16, 16, 16);
			upEnemy[1] = Game.spritesheet.getSprite(9*16, 4*16, 16, 16);
			upEnemy[2] = Game.spritesheet.getSprite(8*16, 5*16, 16, 16);
			upEnemy[3] = Game.spritesheet.getSprite(9*16, 5*16, 16, 16);
			
			enemyDamage[0] = Game.spritesheet.getSprite(6*16, 4*16, 16, 16);
			enemyDamage[1] = Game.spritesheet.getSprite(7*16, 4*16, 16, 16);
			enemyDamage[2] = Game.spritesheet.getSprite(7*16, 16, 16, 16);

	}

	public void tick() {
		moved = false;
		if(this.isColiddingWithPlayer() == false) {
				if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
					&& !isColidding((int)(x+speed), this.getY())) {
				x+=speed;
				dir = right_dir;
				moved = true;
			}
			else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColidding((int)(x-speed), this.getY())) {
				x-=speed;
				dir = left_dir;
				moved = true;
			}
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColidding(this.getX(), (int)(y+speed))) {
				y+=speed;
				dir = up_dir;
				moved = true;
			}
			else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColidding(this.getX(), (int)(y-speed))) {
				y-=speed;
				dir = down_dir;
				moved = true;
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
		}
		else {
			vidaAntiga = Game.player.life;
			if(Game.rand.nextInt(100) < 80) {
				Game.player.life -= Game.rand.nextInt(2);
				if(Game.player.life < vidaAntiga) {
					Game.player.isDamaged = true;
					Sound.hurt.play();
				}
				if(damaged == 20) {
					damaged = 0;
				}
				

				}
			}
		if(enemyLife <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			damageCurrent++;
			if(damageCurrent  == damageFrames) {
				damageCurrent = 0;
				isDamaged = false;
			}
		}
		
		coliddingBullet();
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void coliddingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColidding(this, e)) {
					isDamaged = true;
					enemyLife--;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) 
				continue;
				Rectangle targetEnemy = new Rectangle(e.getX()+ maskx, e.getY()+ masky, maskw, maskh);
				if(enemyCurrent.intersects(targetEnemy)) {
					return true;
				
			}
		}	
		return false;
	}
	
	
	public void render(Graphics g) {
		if(dir == right_dir) {
			g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(isDamaged) {
				g.drawImage(enemyDamage[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == left_dir) {
			g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(isDamaged) {
				g.drawImage(enemyDamage[1], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == up_dir) {
			g.drawImage(upEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(isDamaged) {
				g.drawImage(enemyDamage[2], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		if(dir == down_dir) {
			g.drawImage(downEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(isDamaged) {
				g.drawImage(enemyDamage[2], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		} 
		
		// para mostrar a "mascara" de colisão
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskw, maskh);
		
	}
}