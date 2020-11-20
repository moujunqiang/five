package com.example.task1.myapplication.game;

/**
 * 坐标类
 * @author wenziqiao
 */
public class Coordinate {
    public int x;
    public int y;

    public Coordinate(){
        
    }
    
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    
}
