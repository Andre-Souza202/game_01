package com.spidlye.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.pidlye.entities.*;
import com.pidlye.graficos.Spritesheet;
import com.spidlye.main.Game;

public class World {
	
	public static Tile[] tiles;
	
	public static int WIDHT, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth()*map.getHeight()];
			WIDHT = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth()*map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy*map.getWidth())];
					tiles[xx+(yy*WIDHT)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					if(pixelAtual == 0xFF000000) {
						//ch�o
						tiles[xx+(yy*WIDHT)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					}
					else if(pixelAtual == 0xFFFFFFFF) {
						//parede
						tiles[xx+(yy*WIDHT)] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
					}
					else if(pixelAtual == 0xFF0094FF) {
						//player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}
					else if(pixelAtual == 0xFFFF0000) {
						//enemy
						Enemy en = new Enemy(xx*16, yy*16, 16 ,16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}
					else if(pixelAtual == 0xFF00FF21) {
						//life pack
						Game.entities.add(new LifePack(xx*16, yy*16, 16 ,16, Entity.LIFEPACK_EN));
					}
					else if(pixelAtual == 0xFFFFD800) {
						//bullet
						Game.entities.add(new Bullet(xx*16, yy*16, 16 ,16, Entity.BULLET_EN));
					}
					else if(pixelAtual == 0xFF404040) {
						//weapon
						Game.entities.add(new Weapon(xx*16, yy*16, 16 ,16, Entity.WEAPON_EN));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void restartGame(String level) {
		Player.life = Player.maxLife;
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
	}
	
	public static boolean isFree(int xnext, int ynext) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		return !((tiles[x1 + (y1*World.WIDHT)] instanceof WallTile) ||
				(tiles[x2 + (y2*World.WIDHT)] instanceof WallTile) ||
				(tiles[x3 + (y3*World.WIDHT)] instanceof WallTile) ||
				(tiles[x4 + (y4*World.WIDHT)] instanceof WallTile));
	}
	public static boolean isFree2(int xnext, int ynext) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-14) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-14) / TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-14) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-14) / TILE_SIZE;
		
		return !((tiles[x1 + (y1*World.WIDHT)] instanceof WallTile) ||
				(tiles[x2 + (y2*World.WIDHT)] instanceof WallTile) ||
				(tiles[x3 + (y3*World.WIDHT)] instanceof WallTile) ||
				(tiles[x4 + (y4*World.WIDHT)] instanceof WallTile));
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDHT || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDHT)];
				tile.render(g);
			}
		}
	}

}
