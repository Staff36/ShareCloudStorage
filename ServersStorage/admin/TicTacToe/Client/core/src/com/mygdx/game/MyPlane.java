package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class MyPlane {

    public void setStartVector(Vector2 startVector) {
        this.startVector = startVector;
    }


    public static int[][] getPlain() {
        return plain;
    }

    public static Vector2[][] getVectorPlain() {
        return vectorPlain;
    }

    public Vector2 getStartVector() {
        return startVector;
    }

    public static final int PLAINSIZE=3;
    private static int[][] plain= new int[PLAINSIZE][PLAINSIZE];
    private static Vector2[][] vectorPlain= new Vector2[PLAINSIZE][PLAINSIZE];
    private Vector2 startVector= new Vector2(150,150);
    private Random random= new Random();
    private int count=1;
    private int step=0;


    public MyPlane(){
        for (int i = 0; i <PLAINSIZE ; i++) {
            for (int j = 0; j <PLAINSIZE ; j++) {
                plain[i][j]=0;
                vectorPlain[i][j]=new Vector2(startVector.x+(i+1)*50,startVector.y+(j+1)*50);

            }
        }
    }
       private void aiTurn(int[][] myField){
           int x,y;

           for (int i = 0; i < myField.length; i++) {
               for (int j = 0; j < myField.length ; j++) {
                   if(!isBusyCeil(i,j,myField)){
                       myField[i][j]=1;
                       //Проверка на победу компьютера
                       if (checkTheWinner(myField,2,3)==true) return;
                       else myField[i][j]=1;
                       //проверку на победу игрока
                       if (checkTheWinner(myField,1,3)==true) {
                           myField[i][j]=2;
                           return;
                       }

                       else myField[i][j]=0;
                   }
               }
           }
           do {
               x=(int)(Math.random()*(myField.length));
               y=(int)(Math.random()*(myField.length));

           }while (isBusyCeil(x,y,myField));
           myField[x][y]=2;
       }

       private boolean isBusyCeil(int x, int y, int[][] field){

           return field[x][y]!=0;
       }



     public boolean checkTheWinner(int[][] myField, int symbol, int rangeToWin){
        for (int i = 0; i <myField.length ; i++) {
            for (int j = 0; j <myField.length ; j++) {
                if (myField[i][j]==symbol) {
                    if (checkTheLinesOnWin(myField, i, j, symbol, rangeToWin)) return true;
                }
            }
        }
        return false;
    }
    static boolean checkTheLinesOnWin(int[][] myField, int x, int y, int symbol, int rangeToWin)
    {   int horizontal=0;
        int vertical=0;
        int forwardDiagonal=0;
        int backwardDiagonal=0;

        for (int i = 0; i <rangeToWin ; i++) {
            if (x+rangeToWin<=myField.length&&y+rangeToWin<=myField.length){
                //Проверяем прямую диагональ
                if (myField[x + i][y + i] == symbol) forwardDiagonal++;
            }
            if(x+rangeToWin<=myField.length&&y-rangeToWin+1>=0)
            {   //Проверяем обратную диагональ
                if (myField[x + i][y - i] == symbol) backwardDiagonal++;
            }
            if (x+rangeToWin<=myField.length){
                //Проверяем горизонтали
                if (myField[x + i][y] == symbol) horizontal++;

            }
            if(y+rangeToWin<=myField.length){
                //Проверяем вертикали
                if (myField[x][y + i] == symbol) vertical++;
            }
        }

        if( vertical==rangeToWin||
                horizontal==rangeToWin||
                forwardDiagonal==rangeToWin||
                backwardDiagonal==rangeToWin) {
            return true;
        }

        else return false;
    }
    static boolean checkTheDraw(int count, int [][] myField){
        if(count==(myField.length*myField.length)){
            return true;
        }
        return false;
    }


    public int getStep() {
        return step;
    }

    public void update() {

            if (count == 1) {
                for (int i = 0; i < PLAINSIZE; i++) {
                    for (int j = 0; j < PLAINSIZE; j++) {
                        if (InputHandler.isClicked()) {
                            if ((InputHandler.getMousePosition().x > vectorPlain[i][j].x &&
                                    InputHandler.getMousePosition().x < vectorPlain[i][j].x + 50) &&
                                    (InputHandler.getMousePosition().y > vectorPlain[i][j].y &&
                                            InputHandler.getMousePosition().y < vectorPlain[i][j].y + 50)) {
                                if (plain[i][j] == 0) {
                                    plain[i][j] = 1;
                                    count=2;
                                    step++;
                                }

                            }
                        }
                    }
                }

            } else {
                aiTurn(plain);
                count=1;
                step++;
            }




    }

}
