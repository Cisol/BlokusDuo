package cn.zhangmin.blokusduo;

/**
 * Created by zhangmin on 2016/3/5.
 * 正方形方块类
 */
public class Square implements Cloneable{
    private int x;  //正方形左下顶点的横坐标
    private int y;  //正方形左下顶点的纵坐标
    private int states;  //方块的颜色
    public static final int STATES_ORANGE = 0;
    public static final int STATES_VIOLET = 1;
    public static final int STATES_OFF = 2;
    public static final int STATES_ORANGE_ZERO = 3;
    public static final int STATES_VIOLET_ZERO = 4;
    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.states = STATES_OFF;
    }

    public int getStates() {
        return states;
    }

    public void setStates(int states) {
        this.states = states;
    }

    public Square(int x, int y , int states) {
        this.x = x;
        this.y = y;
        this.states = states;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public Object clone() {
        Square s = new Square(this.getX(), this.getY());
        s.setStates(this.getStates());
        return s;
    }
}
