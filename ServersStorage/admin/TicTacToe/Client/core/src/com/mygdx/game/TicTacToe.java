package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;

import javax.swing.text.PlainDocument;

public class TicTacToe extends ApplicationAdapter {
	SpriteBatch batch;
	Texture backgroundTexture;
	Texture ticTexture;
	Texture tacTexture;
	Texture line;
	Texture gO;
	MyPlane plane= new MyPlane();
	int [][] intplane;
	Vector2[][] vectorPlane;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		backgroundTexture = new Texture("background.png");
		ticTexture= new Texture("tic.png");
		tacTexture= new Texture("tac.png");
		line= new Texture("line.png");
		intplane= new int[plane.PLAINSIZE][plane.PLAINSIZE];
		vectorPlane= new Vector2[plane.PLAINSIZE][plane.PLAINSIZE];
		gO= new Texture("go.png");
	}

	@Override
	public void render () {
		plane.update();

		for (int i = 0; i < plane.PLAINSIZE; i++) {
			for (int j = 0; j < plane.PLAINSIZE; j++) {
				intplane=plane.getPlain();
				vectorPlane=plane.getVectorPlain();
		}
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(backgroundTexture, 0, 0);


			batch.draw(line,250,200);
			batch.draw(line,300,200);
			batch.draw(line,350,250,0,0,line.getWidth(),line.getHeight(),1,1,90,0,0,line.getWidth(),line.getHeight(),false,false);
			batch.draw(line,350,300,0,0,line.getWidth(),line.getHeight(),1,1,90,0,0,line.getWidth(),line.getHeight(),false,false);
			for (int i = 0; i < plane.PLAINSIZE; i++) {
				for (int j = 0; j < plane.PLAINSIZE; j++) {
					if (intplane[i][j] == 1)
						batch.draw(ticTexture, vectorPlane[i][j].x, vectorPlane[i][j].y);
					if (intplane[i][j] == 2)
						batch.draw(tacTexture, vectorPlane[i][j].x, vectorPlane[i][j].y);
				}
			}
		if(plane.checkTheWinner(plane.getPlain(),1,3)|| plane.checkTheWinner(plane.getPlain(),2,3)|| plane.getStep()>8){
			batch.draw(gO,75,50);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);

		}



		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundTexture.dispose();
	}
}
