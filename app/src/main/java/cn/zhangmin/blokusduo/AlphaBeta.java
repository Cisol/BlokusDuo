package cn.zhangmin.blokusduo;

import android.text.LoginFilter;

import org.apache.http.impl.cookie.BestMatchSpec;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by zhangmin on 2016/3/22.
 */
public class AlphaBeta {
    public static Square[][] QUATATION = new Square[GameBoard.COL][GameBoard.COL];  //当前盘面情况
    static ArrayList<Block> ORANGE_BLOCKS_COPY = new ArrayList<>();
    static ArrayList<Block> VIOLET_BLOCKS_COPY = new ArrayList<>();
    static Stack<int[]> BEST_MOVES = new Stack<>();  //保存最佳着法
    /**
     * 构造函数
     */
    public AlphaBeta() {
        for(int i = 0; i < GameBoard.COL; i++) {  //初始化到当前盘面情况
            for(int j = 0; j < GameBoard.COL; j++) {
                QUATATION[i][j] = (Square) GameBoard.CHESSBOARD[i][j].clone();
            }
        }
        for(Block b : GameBoard.ORANGE_BLOCKS) {
            ORANGE_BLOCKS_COPY.add((Block) b.clone());
        }
        for(Block b : GameBoard.VIOLET_BLOCKS) {
            VIOLET_BLOCKS_COPY.add((Block) b.clone());
        }
    }

    /**
     * 初始化盘面情况
     */
    public static void initQuatation() {
        for(int i = 0; i < GameBoard.COL; i++) {  //初始化到当前盘面情况
            for(int j = 0; j < GameBoard.COL; j++) {
                QUATATION[i][j] = (Square) GameBoard.CHESSBOARD[i][j].clone();
            }
        }
        ORANGE_BLOCKS_COPY.removeAll(ORANGE_BLOCKS_COPY);
        VIOLET_BLOCKS_COPY.removeAll(VIOLET_BLOCKS_COPY);
        for(Block b : GameBoard.ORANGE_BLOCKS) {
            ORANGE_BLOCKS_COPY.add((Block) b.clone());
        }
        for(Block b : GameBoard.VIOLET_BLOCKS) {
            VIOLET_BLOCKS_COPY.add((Block) b.clone());
        }
    }

    /**
     * 估值方法
     * 根据当前盘面情况判断下出这一步以后的价值
     * 估值标准：1.盘面面积增加的大小 2.是否增加己方可用角块数量 3.是否减少对方可用角块数量
     * @return
     */
    public static float evaluate(int states) {
        float[] values = new float[2];  //存储双方估计值
        float value = 0;
        for(int i = 0; i < GameBoard.COL; i++) {  //计算面积
            for(int j = 0; j < GameBoard.COL; j++) {
                if(QUATATION[i][j].getStates() == Square.STATES_ORANGE)
                    values[0] = (float) (values[0] - Math.abs(6.5 - i) - Math.abs(6.5 - j) + 20);
                if(QUATATION[i][j].getStates() == Square.STATES_VIOLET)
                    values[1] = (float) (values[1] - Math.abs(6.5 - i) - Math.abs(6.5 - j) + 20);
            }
        }
        switch (states) {  //计算value值
            case Square.STATES_ORANGE :
                values[0] = values[0] + GameRule.allCornerBlocks(Square.STATES_ORANGE, QUATATION).size() * 5;  //计算双方角块数量
                values[1] = values[1] + GameRule.allCornerBlocks(Square.STATES_VIOLET, QUATATION).size() * 20;
                value = values[0] - values[1];
                break;
            case Square.STATES_VIOLET :
                values[0] = values[0] + GameRule.allCornerBlocks(Square.STATES_ORANGE, QUATATION).size() * 20;  //计算双方角块数量
                values[1] = values[1] + GameRule.allCornerBlocks(Square.STATES_VIOLET, QUATATION).size() * 5;
                value = values[1] - values[0];
                break;
            default :
                break;
        }
        return value;
    }

    /**
     * 判断在只旋转的情况下是否可以放置，并记录着点和旋转情况
     * @param block 要放置的方块
     * @param x 要放置位置的x坐标
     * @param y 要放置位置的y坐标
     * @param states 要放置方块的颜色
     * @return
     */
    public static ArrayList<int[]> allCanBePlacedOnlyTurn(Block block, int x, int y, int states) {
        int differX;
        int differY;
        ArrayList<int[]> moves = new ArrayList<>();  //记录可行着法
        for (int i = 0; i < 4; i++) {  //旋转三次
            for (int j = 0; j < block.getSquares().length; j++) {  //将方块的每个小格分别放到可用角块上
                differX = x - block.getSquares()[j].getX();
                differY = y - block.getSquares()[j].getY();
                for(int k = 0; k < block.getSquares().length; k++) {  //变换坐标
                    block.getSquares()[k].setX(block.getSquares()[k].getX() + differX);
                    block.getSquares()[k].setY(block.getSquares()[k].getY() + differY);
                }
                if(GameRule.isAvailable(block, states, QUATATION)) { //如果可行，则添加到可行着法中
                    moves.add(new int[]{i, j});  //i表示旋转次数， j表示棋子哪个方块与角点相连
                    return moves;  ///测试~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                    System.out.print("放上去的方块");
//                    for(Square s : block.getSquares()) {
//                        System.out.print(s.getX() + ":" + s.getY() + "  ");
//                    }
//                    System.out.println();
                }
            }
            block = block.rightRevolveTransformation();  //旋转变换
        }
        return moves;
    }

    /**
     * 找出所有能放置到棋盘上的棋子变形及其着点
     * @param block
     * @param x
     * @param y
     * @param states
     * @return
     */
    public static ArrayList<int[]> allCanBePlaced(Block block, int x, int y, int states) {
        ArrayList<int[]> moves = new ArrayList<>();
        ArrayList<int[]> allCanBePlacedOnlyTurn;
        Block block1 = (Block) block.clone();
        allCanBePlacedOnlyTurn = allCanBePlacedOnlyTurn(block1, x, y, states);  //只通过旋转变换就能够放上去的方块
        for(int i = 0; i < allCanBePlacedOnlyTurn.size(); i++) {
            moves.add(new int[]{0, allCanBePlacedOnlyTurn.get(i)[0], allCanBePlacedOnlyTurn.get(i)[1]});
        }
        if(moves.size() != 0)
            return moves;  //测试~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        block1 = block1.leftRightTransformation();  //左右对称变换，无需考虑上下对称变换，因为左右对称变换旋转两次后便成了上下对称变换
        allCanBePlacedOnlyTurn = allCanBePlacedOnlyTurn(block1, x, y, states);
        for(int i = 0; i < allCanBePlacedOnlyTurn.size(); i++) {
            moves.add(new int[]{1, allCanBePlacedOnlyTurn.get(i)[0], allCanBePlacedOnlyTurn.get(i)[1]});
        }

        return moves;
    }

    /**
     * 生成合理着法
     * 找出当前所有可以放置到棋盘上的棋子及其着点以及变换情况
     * @param states
     * @return ArrayList<int[]> 其中int[]包含6个值：方块编号，是否翻转，旋转次数，哪个子块与角点重合，角点x坐标，角点y坐标
     */
    public static ArrayList<int[]> allPossibleMoves(int states) {
        Block block;
        ArrayList<Block> remainBlocks = new ArrayList<>();  //当前剩余的棋子
        ArrayList<Square> corner = GameRule.allCornerBlocks(states, QUATATION);  //所有可用角块
        ArrayList<int[]> allPossibleMoves = new ArrayList<>();  //所有可行着法
        ArrayList<int[]> allCanBePlaced;  //所有能放上去的棋子位置及变换信息
        int x;
        int y;
        int tempX;
        int tempY;
        switch(states) {
            case Square.STATES_ORANGE :
                remainBlocks = ORANGE_BLOCKS_COPY;
                break;
            case Square.STATES_VIOLET :
                remainBlocks = VIOLET_BLOCKS_COPY;
                break;
            default:
                break;
        }
        for(int i=0; i<remainBlocks.size(); i++) {  //所有剩下的方块
            block = (Block) remainBlocks.get(i).clone();
            for(int j=0; j<corner.size(); j++) {  //所有可用角块
                tempX = corner.get(j).getX(); //此处是棋盘系上的坐标，因此需转成棋子的坐标，否则在isAvailable()判断时会转换回来
                tempY = corner.get(j).getY();
                x = tempY + 1;  //对应棋子系坐标
                y = tempX + 2;
                allCanBePlaced = allCanBePlaced(block, x, y, states);
                for(int k = 0; k <allCanBePlaced.size(); k++ ) {  //所有能放上去的棋子位置及变换信息
                    allPossibleMoves.add(new int[]{i, allCanBePlaced.get(k)[0],
                            allCanBePlaced.get(k)[1], allCanBePlaced.get(k)[2], x, y});
                    if(allPossibleMoves.size() > 50) {
                        return allPossibleMoves;
                    }
//                    System.out.print("可能着法：  " + i + "  ");
//                    for(int t : allCanBePlaced.get(k)) {
//                        System.out.print(t + "  ");
//                    }
//                    System.out.print(x + "  " + y);
//                    System.out.println("  方块数量：" +  block.getSquares().length);
                }
            }
        }
        return allPossibleMoves;
    }

    /**
     * 执行着法
     * @param moves
     * @param states
     * @return Block 返回被删除的棋子，方便后面撤销
     */
    public static Block executeTheMoves(int[] moves, int states) {
//        System.out.println("运行着法：");
//        for(int i : moves) {
//            System.out.print("##" + i + "  ");
//        }
        Block b = null;
        Block b1 = null;
        int differX;
        int differY;
        switch (states) {
            case Square.STATES_ORANGE :
                b = (Block) ORANGE_BLOCKS_COPY.get(moves[0]).clone();
                b1 = (Block) ORANGE_BLOCKS_COPY.get(moves[0]).clone();
                break;
            case Square.STATES_VIOLET :
                b = (Block) VIOLET_BLOCKS_COPY.get(moves[0]).clone();
                b1 = (Block) VIOLET_BLOCKS_COPY.get(moves[0]).clone();
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
//        System.out.println("棋子的方块数目：" + b.getSquares().length);
        differX = moves[4] - b.getSquares()[moves[3]].getX();
        differY = moves[5] - b.getSquares()[moves[3]].getY();
        for(int j = 0; j < b.getSquares().length; j++) {  //变换坐标
            b.getSquares()[j].setX(b.getSquares()[j].getX() + differX);
            b.getSquares()[j].setY(b.getSquares()[j].getY() + differY);
        }
        if(GameRule.isAvailable(b, states, QUATATION)) {
            for (Square s : b.getSquares()) {
                QUATATION[s.getY() - 2][s.getX() - 1].setStates(states);  //更改方块当前所在位置上对应坐标方块的状态
            }
        }
        switch (states) {  //从数组链表中去除用掉的方块
            case 0 :
                ORANGE_BLOCKS_COPY.remove(moves[0]);
                break;
            case 1 :
                VIOLET_BLOCKS_COPY.remove(moves[0]);
                break;
            default:
                break;
        }
        return b1;  //返回未做处理的原始棋子
    }

    public static void cancelMoves(int[] moves, int states, Block b) {
        switch (states) {  //将删除的棋子添加回去
            case Square.STATES_ORANGE :
                ORANGE_BLOCKS_COPY.add(moves[0], (Block) b.clone());
                break;
            case Square.STATES_VIOLET :
                VIOLET_BLOCKS_COPY.add(moves[0], (Block) b.clone());
                break;
            default:
                break;
        }
        //将棋盘恢复至落该子之前的状态
        if(moves[1] == 1) {  //左右对称变换
            b = b.leftRightTransformation();
        }
        for(int i = 0; i < moves[2]; i++) {  //旋转变换
            b = b.rightRevolveTransformation();
        }
        int differX = moves[4] - b.getSquares()[moves[3]].getX();
        int differY = moves[5] - b.getSquares()[moves[3]].getY();
        for(int j = 0; j < b.getSquares().length; j++) {  //变换坐标
            b.getSquares()[j].setX(b.getSquares()[j].getX() + differX);
            b.getSquares()[j].setY(b.getSquares()[j].getY() + differY);
        }
        for(Square s : b.getSquares()) {
            QUATATION[s.getY()-2][s.getX()-1].setStates(Square.STATES_OFF);  //更改方块当前所在位置上对应坐标方块的状态为初始状态
            if((s.getY()-2) == (GameBoard.COL-5) && (s.getX()-1) == 4)  //如果是紫色方初始点
                QUATATION[s.getY()-2][s.getX()-1].setStates(Square.STATES_ORANGE_ZERO);
            if((s.getY()-2) == 4 && (s.getX()-1) == (GameBoard.COL-5))  //如果是橙色方初始点
                QUATATION[s.getY()-2][s.getX()-1].setStates(Square.STATES_VIOLET_ZERO);
        }
    }

    /**
     * AlphaBeta剪枝算法
     * @return
     */
    public static float alphaBeta(int depth, float alpha, float beta, int states) {
        int[] bestMove = new int[6];
        if(depth <=0 || GameRule.isOver(states, QUATATION)) {
            if(states == GameBoard.AI_STATES)   //此时是玩家回合,注意执行过着法后轮到对方下
                return evaluate(GameBoard.AI_STATES);
            else    //此时是电脑回合
                return -evaluate(GameBoard.AI_STATES);  //负负得正
        }
        ArrayList<int[]> allPossibleMoves = allPossibleMoves(states);
//        System.out.println("进入alphabeta算法！");
        //System.out.println("共有" + allPossibleMoves.size() + "种着法！");
        for(int i = 0; i < allPossibleMoves.size(); i++) {
            int[] moves = allPossibleMoves.get(i);
//            System.out.println("着法：");
//            for(int t : moves) {
//                System.out.print(t + "  ");
//            }
            Block block = executeTheMoves(moves, states);  //执行着法
            states = (states + 1) % 2;
            float val = -alphaBeta(depth-1, -beta, -alpha, states);
//            System.out.println("val值：" + val);
            states = (states + 1) % 2;
            cancelMoves(moves, states, block);  //撤销着法
            if(val >= beta) {
                //System.out.println(i + "剪枝！");
                return val;  //beta剪枝
            }
            if(val > alpha) {
                alpha = val;  //保存极大值
                bestMove = moves;
                //System.out.println("极大值： " + alpha);
            }
        }
        BEST_MOVES.add(bestMove);
        //System.out.println("BEST_MOVES:  " + BEST_MOVES.size());
        return alpha;  //返回极大值
    }

    /**
     * 清空最好着法
     */
    public static void clearBestMoves() {
        BEST_MOVES.clear();
    }
}
