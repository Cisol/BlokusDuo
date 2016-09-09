package cn.zhangmin.blokusduo;

import android.text.style.LineHeightSpan;
import android.webkit.WebIconDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatWidthException;
import java.util.Map;

/**
 * Created by zhangmin on 2016/3/9.
 */
public class GameRule {
    public static boolean isAvailable (Block b, int states, Square[][] CHESSBOARD) {
        int x;  //方块在棋盘数组中的x坐标
        int y;  //方块在棋盘数组中的y坐标
        boolean isCorner = false;  //判断是否角与角相邻
        for(Square s : b.getSquares()) {
            //判断棋子是否放置在棋盘中以及放置处是否有棋子
            if(s.getX()<1 || s.getX()>=(GameBoard.COL+1)
                    || s.getY()<=1 || s.getY()>(GameBoard.COL+1)
                    || CHESSBOARD[s.getY()-2][s.getX()-1].getStates()==Square.STATES_ORANGE
                    || CHESSBOARD[s.getY()-2][s.getX()-1].getStates()==Square.STATES_VIOLET) {
                return false;
            }
            //判断棋子是否与已放置的己方棋子边与边相邻，以及是否与己方棋子的以角对角的方式相邻
            x = s.getY() - 2;  //坐标转换
            y = s.getX() - 1;

            //左上角
            if(x==0 && y==0) {
                if(CHESSBOARD[x][y+1].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x+1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            //右上角
            else if(x==0 && y==(GameBoard.COL-1)) {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x+1][y-1].getStates() == states) {
                    isCorner = true;
                }
            }
            //左下角
            else if(x==(GameBoard.COL-1) && y==0) {
                if(CHESSBOARD[x][y+1].getStates() == states
                        || CHESSBOARD[x-1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            //右下角
            else if(x==(GameBoard.COL-1) && y==(GameBoard.COL-1)) {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x-1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y-1].getStates() == states) {
                    isCorner = true;
                }
            }
            //左边边块
            else if(y == 0) {
                if(CHESSBOARD[x-1][y].getStates() == states
                        || CHESSBOARD[x][y+1].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y+1].getStates() == states
                        || CHESSBOARD[x+1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            //上边边块
            else if(x == 0) {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states
                        || CHESSBOARD[x][y+1].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x+1][y-1].getStates() == states
                        || CHESSBOARD[x+1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            //右边边块
            else if(y == (GameBoard.COL-1)) {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x-1][y].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y-1].getStates() == states
                        || CHESSBOARD[x+1][y-1].getStates() == states) {
                    isCorner = true;
                }
            }
            //下边边块
            else if(x == (GameBoard.COL-1)) {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x-1][y].getStates() == states
                        || CHESSBOARD[x][y+1].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y-1].getStates() == states
                        || CHESSBOARD[x-1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            //中间的方块
            else {
                if(CHESSBOARD[x][y-1].getStates() == states
                        || CHESSBOARD[x-1][y].getStates() == states
                        || CHESSBOARD[x][y+1].getStates() == states
                        || CHESSBOARD[x+1][y].getStates() == states) {
                    return false;
                }
                if(CHESSBOARD[x-1][y-1].getStates() == states
                        || CHESSBOARD[x-1][y+1].getStates() == states
                        || CHESSBOARD[x+1][y-1].getStates() == states
                        || CHESSBOARD[x+1][y+1].getStates() == states) {
                    isCorner = true;
                }
            }
            if(isFirstPlace(states, CHESSBOARD)) { //如果是第一块
                if(CHESSBOARD[s.getY()-2][s.getX()-1].getStates()==(states+3))
                    isCorner = true;
            }
        }
        if (isCorner == false) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是第一次放置
     * @param states
     * @return
     */
    public static boolean isFirstPlace(int states, Square[][] CHESSBOARD) {
        for(int i=0; i<GameBoard.COL; i++) {
            for(int j=0; j<GameBoard.COL; j++) {
                if(CHESSBOARD[i][j].getStates() == states)
                    return false;
            }
        }
        return true;
    }

    /**
     * 找出所有可用角块
     * @param states
     * @return
     */
    public static ArrayList<Square> allCornerBlocks(int states, Square[][] CHESSBOARD) {
        ArrayList<Square> squares= new ArrayList<Square>();
        for(int i=0; i<GameBoard.COL; i++) {
            for(int j=0; j<GameBoard.COL; j++) {
                if(CHESSBOARD[i][j].getStates() == states) {
                    if(i-1>=0 && j-1>=0) {
                        if(CHESSBOARD[i-1][j-1].getStates()==Square.STATES_OFF  //只要该方块的对角块是空的且该方块与对角块中间相邻的两块不是该方块同颜色的方块则可用
                                && CHESSBOARD[i-1][j].getStates()!=states && CHESSBOARD[i][j-1].getStates()!=states) {
                            squares.add(new Square(i - 1, j - 1));
                        }
                    }
                    if(i-1>=0 && j+1<GameBoard.COL) {
                        if(CHESSBOARD[i-1][j+1].getStates()==Square.STATES_OFF && CHESSBOARD[i-1][j].getStates()!=states
                                && CHESSBOARD[i][j+1].getStates()!=states) {
                            squares.add(new Square(i - 1, j + 1));
                        }
                    }
                    if(i+1<GameBoard.COL && j-1>=0) {
                        if(CHESSBOARD[i+1][j-1].getStates()==Square.STATES_OFF && CHESSBOARD[i][j-1].getStates()!=states
                                && CHESSBOARD[i+1][j].getStates()!=states) {
                            squares.add(new Square(i + 1, j - 1));
                        }
                    }
                    if(i+1<GameBoard.COL && j+1<GameBoard.COL) {
                        if (CHESSBOARD[i + 1][j + 1].getStates() == Square.STATES_OFF && CHESSBOARD[i][j + 1].getStates()!=states
                                && CHESSBOARD[i + 1][j].getStates()!=states) {
                            squares.add(new Square(i + 1, j + 1));
                        }
                    }
                }
                if(CHESSBOARD[i][j].getStates() == states + 3)  //如果是第一次放置
                    squares.add(new Square(i, j));
            }
        }
//        System.out.println("all corner: ");
//        for(Square s : squares) {
//            System.out.println(s.getX() + ":" + s.getY());
//        }
        return squares;
    }

    /**
     * 判断在只旋转的情况下是否可以放置，为判断能否放置到棋盘做铺垫
     * @param block 要放置的方块
     * @param x 要放置位置的x坐标
     * @param y 要放置位置的y坐标
     * @param states 要放置方块的颜色
     * @return
     */
    public static boolean isItCanBePlacedOnlyTurn(Block block, int x, int y, int states, Square[][] CHESSBOARD) {
        int differX;
        int differY;
        for (int i = 0; i < 4; i++) {  //旋转四次
            block = block.rightRevolveTransformation();
            for (int j = 0; j < block.getSquares().length; j++) {  //将方块的每个小格分别放到可用角块上
                differX = x - block.getSquares()[j].getX();
                differY = y - block.getSquares()[j].getY();
                for(int k = 0; k < block.getSquares().length; k++) {  //变换坐标
                    block.getSquares()[k].setX(block.getSquares()[k].getX() + differX);
                    block.getSquares()[k].setY(block.getSquares()[k].getY() + differY);
                }
                if(isAvailable(block, states, CHESSBOARD))
                    return true;
            }
        }
        return false;
    }

    /**
     * 判断能否放置到棋盘上
     * @param block
     * @param x
     * @param y
     * @param states
     * @return
     */
    public static boolean isItCanBePlaced(Block block, int x, int y, int states, Square[][] CHESSBOARD) {
        if(isItCanBePlacedOnlyTurn(block, x, y, states, CHESSBOARD))
            return true;
        block = block.leftRightTransformation();  //左右对称变换，无需考虑上下对称变换，因为左右对称变换旋转两次后便成了上下对称变换
        if(isItCanBePlacedOnlyTurn(block, x, y, states, CHESSBOARD))
            return true;
        return false;
    }

    /**
     * 找出当前所有可以放置到棋盘上的棋子
     * @param states
     * @return
     */
    public static ArrayList possibleBlocks(int states, Square[][] CHESSBOARD) {
        Block block;
        ArrayList<Block> remainBlocks = new ArrayList<>();  //当前剩余的棋子
        ArrayList<Square> corner = allCornerBlocks(states, CHESSBOARD);  //所有可用角块
        ArrayList possibleBlocks = new ArrayList<>();  //当前可以放置到棋盘上的棋子
        int x;
        int y;
        int tempX;
        int tempY;
        switch(states) {
            case Square.STATES_ORANGE :
                remainBlocks = GameBoard.ORANGE_BLOCKS;
                break;
            case Square.STATES_VIOLET :
                remainBlocks = GameBoard.VIOLET_BLOCKS;
                break;
            default:
                break;
        }
//        System.out.println("能放上去的方块：");
        for(int i=0; i<remainBlocks.size(); i++) {  //所有剩下的方块
            block = (Block) remainBlocks.get(i).clone();
            for(int j=0; j<corner.size(); j++) {  //所有可用角块
                tempX = corner.get(j).getX(); //此处是棋盘上的坐标，因此需转成棋子的坐标，否则在isAvailable()判断时会转换回来
                tempY = corner.get(j).getY();
                x = tempY + 1;
                y = tempX + 2;
                if(isItCanBePlaced(block, x, y, states, CHESSBOARD)) {
                    possibleBlocks.add(i);
//                    System.out.println("  " + i);
                    break;
                }
            }
        }
        return possibleBlocks;
    }


    /**
     * 判断是否还有棋子可放
     * @param states
     * @return
     */
    public static boolean isOver(int states, Square[][] CHESSBOARD) {
        if(isFirstPlace(states, CHESSBOARD))
            return false;
        if(possibleBlocks(states, CHESSBOARD).size() == 0)
            return true;
        return false;
    }

    /**
     * 计算双方的的分数
     * @return
     */
    public static int[] scores() {
        int[] scores = new int[] {0, 0};
        for(int i = 0; i < GameBoard.COL; i++) {
            for(int j = 0; j < GameBoard.COL; j++) {
                if(GameBoard.CHESSBOARD[i][j].getStates() == Square.STATES_ORANGE)
                    scores[0]++;
                if(GameBoard.CHESSBOARD[i][j].getStates() == Square.STATES_VIOLET)
                    scores[1]++;
            }
        }
        return scores;
    }


    /**
     * 执行电脑的着法
     * @param moves
     * @param states
     * @return
     */
    public static void executeTheAIMoves(int[] moves, int states) {
        Block b = null;
        Block b1 = null;
        int differX;
        int differY;
        switch (states) {
            case Square.STATES_ORANGE :
                b = (Block) GameBoard.ORANGE_BLOCKS.get(moves[0]).clone();
                break;
            case Square.STATES_VIOLET :
                b = (Block) GameBoard.VIOLET_BLOCKS.get(moves[0]).clone();
                break;
            default :
                break;
        }
        if(moves[1] == 1) {  //左右对称变换
            b = b.leftRightTransformation();
        }
        for(int i = 0; i < moves[2]; i++) {  //旋转变换
            b = b.rightRevolveTransformation();
        }
        differX = moves[4] - b.getSquares()[moves[3]].getX();
        differY = moves[5] - b.getSquares()[moves[3]].getY();
        for(int j = 0; j < b.getSquares().length; j++) {  //变换坐标
            b.getSquares()[j].setX(b.getSquares()[j].getX() + differX);
            b.getSquares()[j].setY(b.getSquares()[j].getY() + differY);
        }
        //if(GameRule.isAvailable(b, states, GameBoard.CHESSBOARD)) {
        GameBoard.LASTMOVE.clear();
            for (Square s : b.getSquares()) {
                GameBoard.LASTMOVE.add(new Square(s.getX(), s.getY()));
                GameBoard.CHESSBOARD[s.getY() - 2][s.getX() - 1].setStates(states);  //更改方块当前所在位置上对应坐标方块的状态
            }
            switch (states) {  //从数组链表中去除用掉的方块
                case 0:
                    GameBoard.ORANGE_BLOCKS.remove(moves[0]);
                    break;
                case 1:
                    GameBoard.VIOLET_BLOCKS.remove(moves[0]);
                    break;
                default:
                    break;
            }
        //}
    }


    /**
     * 提示方法
     * @param states
     * @return
     */
    public static void hint(int states) {
        AlphaBeta.initQuatation();
        ArrayList<int[]> moves = AlphaBeta.allPossibleMoves(states);
        ArrayList hintMoves = new ArrayList();
        boolean flag = true;
        for(int i = 0; i < moves.size(); i++) {
            if(i == 0) {
                hintMoves.add(moves.get(i)[0]);
            }
            for(int j = 0; j < hintMoves.size(); j++) {
                if(moves.get(i)[0] == (int) hintMoves.get(j))
                    flag = false;
            }
            if(flag)
                hintMoves.add(moves.get(i)[0]);
            flag = true;
        }
        GameBoard.HINTMOVE = (int) hintMoves.get(GameBoard.HINTTIME % hintMoves.size());
        GameBoard.HINTTIME++;
    }
}
