package cn.zhangmin.blokusduo;

import java.util.Comparator;

/**
 * Created by zhangmin on 2016/5/12.
 * 记录得分信息
 */
public class HighScore implements Comparator{
    private String player;
    private int score;

    public HighScore() {

    }

    public HighScore(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 重写比较方法，根据分数比较大小
     * @param lhs
     * @param rhs
     * @return
     */
    @Override
    public int compare(Object lhs, Object rhs) {
        HighScore h1 = (HighScore) lhs;
        HighScore h2 = (HighScore) rhs;
        if(h1.getScore() >= h2.getScore())
            return -1;
        return 1;
    }
}
