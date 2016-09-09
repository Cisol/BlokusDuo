package cn.zhangmin.blokusduo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by zhangmin on 2016/5/11.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydata.db";   //数据库名称
    private static final int version = 1;   //数据库版本

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("onCreate!!!!!!!!!!");
        String sql = "create table HighScore(player varchar(20) not null, score int(3) not null);";
        db.execSQL(sql);
        db.execSQL("insert into HighScore(player, score) values ('Aly' , 60);");
        db.execSQL("insert into HighScore(player, score) values ('Bob', 45);");
        db.execSQL("insert into HighScore(player, score) values ('Candy' , 55);");
        db.execSQL("insert into HighScore(player, score) values ('Julie', 80);");
        db.execSQL("insert into HighScore(player, score) values ('John', 75);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入数据
     * @param p
     * @param s
     */
    public void insertData(String p, int s) {
        System.out.println("insert Data!!!!");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();   //实例化一个ContentValues用来装载待插入的数据
        cv.put("player", p);
        cv.put("score", s);
        db.insert("HighScore", null, cv);
        db.close();
    }


    /**
     * 查询数据
     * @return
     */
    public ArrayList<HighScore> searchData() {
        ArrayList<HighScore> highScores = new ArrayList<HighScore>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("HighScore", null, null, null, null, null, "score desc");
        while(c.moveToNext()) {
            HighScore highScore = new HighScore();
            String player = c.getString(c.getColumnIndex("player"));
            int score = c.getInt(c.getColumnIndex("score"));
            highScore.setPlayer(player);
            highScore.setScore(score);
            highScores.add(highScore);
        }
//        if(highScores != null) {
//            Collections.sort(highScores, new HighScore());
//        }
        c.close();
        db.close();
        return highScores;
    }

    /**
     * 删除数据库
     * @param context
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase("mydata.db");
    }

    /**
     * 返回入榜最低得分
     * @return
     */
    public int getMinHighScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("HighScore", null, null, null, null, null, "score desc");
        int i = 0;
        int minScore = 0;
        while(c.moveToNext()) {
            if(i == 4) {
                minScore = c.getInt(c.getColumnIndex("score"));
                break;
            }
            i++;
        }
        System.out.println("minScore:" + minScore);
        db.execSQL("delete from HighScore where score <" + minScore);
        c.close();
        db.close();
        return minScore;
    }

}
