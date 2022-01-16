package com.spidlye.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.pidlye.entities.BulletShoot;
import com.pidlye.entities.Enemy;
import com.pidlye.entities.Entity;
import com.pidlye.entities.Player;
import com.pidlye.graficos.Spritesheet;
import com.pidlye.graficos.UI;
import com.spidlye.world.Camera;
import com.spidlye.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	public int CUR_LEVEL = 1;
	public static int MAX_LEVEL = 2;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	public static Spritesheet icones;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = false;
	private int framesGameOver = 0;
	private boolean enterRestart = false;
	
	public boolean saveGame = false;
	
	public Menu menu;
	
	public static int mx, my;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");

		
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Jogo #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		//icone da janela e cursor
		Image icone = null;
		try {
			icone = ImageIO.read(getClass().getResource("/icones2.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursor = toolkit.getImage(getClass().getResource("/icones2.png"));
		Cursor c = toolkit.createCustomCursor(cursor, new Point(13,13
				), "ing");
		frame.setCursor(c);
		frame.setIconImage(icone);
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level"};
				int[] opt2 = {this.CUR_LEVEL};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo salvo");
			}
			enterRestart = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < bullets.size();i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}
		else if(gameState == "GAME_OVER") {
			Player.life = 0;
			framesGameOver++;
			if(framesGameOver == 30) {
				framesGameOver = 0;
				if(showMessageGameOver) {
					showMessageGameOver = false;
				}
				else {
					showMessageGameOver = true;
				}
			}
			
			if(enterRestart) {
				enterRestart = false;
				gameState = "NORMAL";
				Sound.musicLevel.loop();
				Sound.gameOver.stop();
				String newWorld = "level"+this.CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}
		else if(gameState == "MENU") {
			menu.tick();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/*Renderização do jogo*/
		//Graphics2D g2 = (Graphics2D) g; 
		world.render(g);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size();i++) {
			bullets.get(i).render(g);
		}
			ui.render(g);
			/***/
			g.dispose();
			g = bs.getDrawGraphics();
			g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
			if(player.hasGun) {
				g.setFont(new Font("arial", Font.BOLD,50));
				g.setColor(Color.white);
				g.drawString(player.ammoGun+"/"+player.ammo, 20, 460);
			}
			if(gameState == "GAME_OVER") {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(new Color(0, 0, 0, 150));
				g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
				g.setFont(new Font("arial", Font.BOLD,64));
				g.setColor(Color.red);
				g.drawString("GAME OVER", ((WIDTH*SCALE)/2) - 128 - 64 , ((HEIGHT*SCALE)/2) );
				g.setFont(new Font("arial", Font.BOLD,32));
				g.setColor(Color.white);
				if(showMessageGameOver) {
					g.drawString(">Pressione ENTER para reiniciar<", ((WIDTH*SCALE)/2)- 250 , ((HEIGHT*SCALE)/2) + 42 );
				}
			
		}
		else if(gameState == "MENU") {
			menu.render(g);
		}
			
		//	double angleMouse = Math.atan2(300+25 - my,300+25 - mx);
		//	double angle = Math.toDegrees(angleMouse);
			
			
			
		bs.show();
	}

	public void run(){
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double delta = 0;
		double ns = 1000000000 / amountOfTicks;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
		   e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;		
		}
		if(e.getKeyCode() == KeyEvent.VK_UP ||
		   e.getKeyCode() == KeyEvent.VK_W	) {
			player.up = true;		
			if(gameState == "MENU") {
				menu.up = true;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S	) {
			player.down = true;	
			if(gameState == "MENU") {
				menu.down = true;
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			enterRestart = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_P) {
			if(gameState != "GAME_OVER") {
			gameState = "MENU";
			menu.pause = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_R) {
			if(player.reloadSound) {
				player.reloadCommand = true;
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameState == "NORMAL")
				this.saveGame = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
		   e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;		
		}
		if(e.getKeyCode() == KeyEvent.VK_UP ||
		   e.getKeyCode() == KeyEvent.VK_W	) {
			player.up = false;		
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S	) {
			player.down = false;		
		}
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() /SCALE);	
		player.my = (e.getY() /SCALE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Game.mx = e.getX();
		Game.my = e.getY();
	}
}
