//package com.example.task1.myapplication.game;
//
////import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.Stack;
//
//public class ComputerAI2 {
//
//
////    private static final String TAG ="abc" ;
//    private ArrayList<int[]> storageHadChess = new ArrayList<>();
//    private Stack<String> storageArray = new Stack<>();
////    protected ArrayList<String> cunchusandianArraylist=new ArrayList<>();
//    private int mWidth = 0;
//    public static int mGridArray[][];
//    private int mHeight = 0;
//    public static int Color = Game.WHITE;
////    public static int duishouColor=Game.BLACK;
//    public ComputerAI2(int width, int height) {
//        mWidth=width+1;
//        mHeight=height+1;
//
//    }
//
////    public Coordinate normalautomatic(int [][] map,Coordinate z) {
////        Coordinate v1,v2,v3,v4,v5=null;
////        int x=z.x,y=z.y;
////        mGridArray = new int[mWidth - 1][mHeight - 1];
////        Log.d(TAG, "gridHidth="+mWidth+"  gridHeight="+mHeight);
////        for(int i=0;i<mWidth-1;i++){
////            for(int j=0;j<mHeight-1;j++)
////                mGridArray[i][j]=map[i][j];
////        }
////        //判断我方是否有3个连成一线了
////        for (int i = 0; i < mWidth - 1; i++) //i表示列(根据宽度算出来的)
////            for (int j = 0; j < mHeight - 1; j++) { //i表示行(根据高度算出来的)
////                //检测横轴三个相连
////                if ((((i + 3) < (mWidth - 1)) &&
////                        (mGridArray[i][j] == Color) && (mGridArray[i + 1][j] == Color) && (mGridArray[i + 2][j] == Color)) ||
////                        (((i + 3) < (mWidth - 1)) &&
////                                (mGridArray[i][j] == duishouColor) && (mGridArray[i + 1][j] == duishouColor) && (mGridArray[i + 2][j] == duishouColor))) {
////                    //如果有三个点相连了
////                    //先判断是否已经测试过这三个点
////                    boolean aa = false;
////                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
////                        String sandiantemp = cunchusandianArraylist.get(p);
////                        String[] sandiantemps = sandiantemp.split(":");
////                        //如果这三个点已经存在
////                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
////                                (Integer.parseInt(sandiantemps[1]) == j) &&
////                                (Integer.parseInt(sandiantemps[2]) == (i + 1)) &&
////                                (Integer.parseInt(sandiantemps[3]) == j) &&
////                                (Integer.parseInt(sandiantemps[4]) == (i + 2)) &&
////                                (Integer.parseInt(sandiantemps[5]) == j)) {
////                            aa = true;
////                        }
////                    }
////                    if (aa == true) {
////
////                    } else {
////                        //在两边端点位置随机下一个
////                        v1=ifsangedianxianglian(i - 1, j, i + 3, j);
////                        cunchusandianArraylist.add(i + ":" + j + ":" + (i + 1) + ":" + j + ":" + (i + 2) + ":" + j);
////                        return v1;
////                    }
////                }
////
////                //纵轴3个相连
////                if ((((j + 3) < (mHeight - 1)) &&
////                        (mGridArray[i][j] == Color) && (mGridArray[i][j + 1] == Color) && (mGridArray[i][j + 2] == Color)) ||
////                        (((j + 3) < (mHeight - 1)) &&
////                                (mGridArray[i][j] == duishouColor) && (mGridArray[i][j + 1] == duishouColor) && (mGridArray[i][j + 2] == duishouColor))) {
////                    //如果有三个点相连了
////                    //先判断是否已经测试过这三个点
////                    boolean aa = false;
////                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
////                        String sandiantemp = cunchusandianArraylist.get(p);
////                        String[] sandiantemps = sandiantemp.split(":");
////                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
////                                (Integer.parseInt(sandiantemps[1]) == j) &&
////                                (Integer.parseInt(sandiantemps[2]) == i) &&
////                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
////                                (Integer.parseInt(sandiantemps[4]) == i) &&
////                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
////                            aa = true;
////                        }
////                    }
////                    if (aa == true) {
////
////                    } else {
////                        //在两边端点位置随机下一个
////                        v4=ifsangedianxianglian(i, j - 1, i, j + 3);
////                        cunchusandianArraylist.add(i + ":" + j + ":" + i + ":" + (j + 1) + ":" + i + ":" + (j + 2));
////                        return v4;
////                    }
////                }
////
////                //左上到右下3个相连
////                if ((((j + 3) < (mHeight - 1)) && ((i + 3) < (mWidth - 1)) &&
////                        (mGridArray[i][j] == Color) && (mGridArray[i + 1][j + 1] == Color) && (mGridArray[i + 2][j + 2] == Color)) ||
////                        (((j + 3) < (mHeight - 1)) && ((i + 3) < (mWidth - 1)) &&
////                                (mGridArray[i][j] == duishouColor) && (mGridArray[i + 1][j + 1] == duishouColor) && (mGridArray[i + 2][j + 2] == duishouColor))) {
////                    //如果有三个点相连了
////                    //先判断是否已经测试过这三个点
////                    boolean aa = false;
////                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
////                        String sandiantemp = cunchusandianArraylist.get(p);
////                        String[] sandiantemps = sandiantemp.split(":");
////                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
////                                (Integer.parseInt(sandiantemps[1]) == j) &&
////                                (Integer.parseInt(sandiantemps[2]) == (i + 1)) &&
////                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
////                                (Integer.parseInt(sandiantemps[4]) == (i + 2)) &&
////                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
////                            aa = true;
////                        }
////                    }
////                    if (aa == true) {
////
////                    } else {
////                        v2=ifsangedianxianglian(i - 1, j - 1, i + 3, j + 3);
////                        cunchusandianArraylist.add(i + ":" + j + ":" + (i + 1) + ":" + (j + 1) + ":" + (i + 2) + ":" + (j + 2));
////                        return v2;
////
////                    }
////                }
////                /*if(true){
////
////                    v5.x=10;
////                    v5.y=10;
////                    return v5;
////                }*/
////                //右上到左下3个相连
////                if ((((i - 3) >= 0) && ((j + 3) < (mHeight - 1)) &&
////                        (mGridArray[i][j] == Color) && (mGridArray[i - 1][j + 1] == Color) && (mGridArray[i - 2][j + 2] == Color)) ||
////                        (((i - 3) >= 0) && ((j + 3) < mHeight - 1)) &&
////                                (mGridArray[i][j] == duishouColor) && (mGridArray[i - 1][j + 1] == duishouColor) && (mGridArray[i - 2][j + 2] == duishouColor)){
////                    //如果有三个点相连了
////                    //先判断是否已经测试过这三个点
////                    boolean aa = false;
////                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
////                        String sandiantemp = cunchusandianArraylist.get(p);
////                        String[] sandiantemps = sandiantemp.split(":");
////                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
////                                (Integer.parseInt(sandiantemps[1]) == j) &&
////                                (Integer.parseInt(sandiantemps[2]) == (i - 1)) &&
////                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
////                                (Integer.parseInt(sandiantemps[4]) == (i - 2)) &&
////                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
////                            aa = true;
////                        }
////                    }
////                    if (aa == true) {
////
////                    } else {
////                        v3=ifsangedianxianglian(i + 1, j - 1, i - 3, j + 3);
////                        cunchusandianArraylist.add(i + ":" + j + ":" + (i - 1) + ":" + j + 1 + ":" + (i - 2) + ":" + (j + 2));
////                        return v3;
////                    }
////                }
////            }
////        int[][] temp = {{x - 1, y - 1}, {x, y - 1}, {x + 1, y - 1}, {x - 1, y}, {x + 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}};
////        ArrayList<int[]> templist = new ArrayList<>();
////        for (int k = 0; k < temp.length; k++) {
////            if (temp[k][0] >= 0 && temp[k][0] < 13 && temp[k][1] >= 0 && temp[k][1] < 13) {
////                templist.add(temp[k]);
////            }
////            //判断是否已经下过
////            panduanshifouyijingxiaguo(templist);
////            int num = (int) (Math.random() * templist.size());
////            int a = templist.get(num)[0];
////            int b = templist.get(num)[1];
////            putChess(a,b,Color);
////            Coordinate c = new Coordinate();
////            c.x = a;
////            c.y = b;
////
////        }
////        Coordinate c = new Coordinate();
////        int randx=(int)(Math.random()*mWidth);
////        int randy=(int)(Math.random()*mHeight);
////        c.x = randx;
////        c.y = randy;
////        putChess(randx,randy,Color);
////        return c;
////    }
//    private void panduanshifouyijingxiaguo(ArrayList<int[]> templist) {
//        for (int i = 0; i < storageHadChess.size(); i++) {
//            //如有重复，则删掉
//            for (int j = 0; j < templist.size(); j++) {
//                if (storageHadChess.get(i)[0] == templist.get(j)[0])
//                    if (storageHadChess.get(i)[1] == templist.get(j)[1]) {
//                        templist.remove(j);
//                        //递归防止周围没有字落下时直接崩掉
//                        if (templist.size() == 0) {
//                            templist.add(new int[]{(int) (Math.random() * (mWidth - 2)), (int) (Math.random() * (mHeight - 2))});
//                            //  Log.d("whalea", " " + (int) (Math.random() * (GRID_SIZE - 2)));
//                            panduanshifouyijingxiaguo(templist);
//                        }
//                    }
//            }
//        }
//    }
//    /**
//     * 困难电脑の核心算法
//     * 判断有三个点相连之后的操作
//     */
////    private Coordinate ifsangedianxianglian(int x, int y, int m, int n) {
////        ArrayList<int[]> automaticChesslist = new ArrayList<>();
////        automaticChesslist.add(new int[]{x, y});
////        automaticChesslist.add(new int[]{m, n});
////        panduanshifouyijingxiaguo(automaticChesslist);
////        int randomindex = Math.round(automaticChesslist.size());
////        int a = automaticChesslist.get(randomindex - 1)[0];
////        int b = automaticChesslist.get(randomindex - 1)[1];
////        putChess(a,b,Color);
////        Coordinate c = new Coordinate();
////        c.x = a;
////        c.y = b;
////
////        return c;
////    }
//    public void putChess(int x, int y, int blackwhite) {
//        storageHadChess.add(new int[]{x, y});
//        mGridArray[x][y] = blackwhite;
//        String temp = x + ":" + y;
//        storageArray.push(temp);
//
//    }
//
//}
