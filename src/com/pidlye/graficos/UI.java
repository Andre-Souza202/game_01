package com.pidlye.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.pidlye.entities.Player;

public class UI {
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(6, 2, 54 ,12);
		g.setColor(Color.red);
		g.fillRect(8, 4, 50 ,8);
		g.setColor(Color.green);
		g.fillRect(8, 4,(int)((Player.life/Player.maxLife)*50) ,8);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 9));
		g.drawString((int)Player.life+"/"+(int)Player.maxLife, 9, 12);
	}

}
