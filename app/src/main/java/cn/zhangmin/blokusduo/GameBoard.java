package cn.zhangmin.blokusduo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.LauncherApps;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.renderscript.Allocation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.*;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.security.cert.CertPathBuilderException;
import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Created by zhangmin on 2016/3/3.
 * 棋盘类，包括游戏界面绘制方法，以及触摸响应方法
 */
public class GameBoard extends SurfaceView implements OnTouchListener, Runnable{

    static int COL = 14;
    static int WIDTH;
    private static int TURN_ORANGE = -1;
    private static int TURN_VIOLET = -1;
    private static float[] LINES = new float[120];  //棋盘网格数组
    static ArrayList<Block> ORANGE_BLOCKS = new ArrayList<>();
    static ArrayList<Block> VIOLET_BLOCKS = new ArrayList<>();
    static Block ON_BLOCK = null;
    static Block ON_BLOCK_CHANGE = null;  //记录变形信息
    private static int SCREEN_WIDTH = 0;
    private static int SCREEN_HEIGHT = 0;
    static Square[][] CHESSBOARD = new Square[COL][COL];
    private static float DOWNX;
    private static float DOWNY;
    private static int  END_GAME = 0;  //判断游戏是否结束，0为双方都未结束，1为某一方结束，2为游戏结束
    private static String WHOSE_TURN;  //游戏当前是橙方还是紫方落子
    static int ON_STATES = 0;
    static int AI_STATES = 1;  //电脑所执棋子的颜色
    private static int[] BESTMOVE = new int[6];
    static int HINTMOVE = -1;   //提示
    static int HINTTIME = 0;   //提示次数
    static ArrayList<Square> LASTMOVE = new ArrayList<>();  //上次落子位置
    //初始化bitmap
    private Bitmap ORANGE_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.orange);  //橙色方块图片
    private Bitmap VIOLET_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.violet);  //紫色方块图片
    private Bitmap BLANK_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.blank);  //空白方块图片
    private Bitmap ORANGE_ZERO_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.orangezero);  //橙色初始点图片
    private Bitmap VIOLET_ZERO_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.violetzero);  //紫色初始点图片
    private Bitmap BACKGROUND = BitmapFactory.decodeResource(getResources(), R.drawable.background);  //背景图片

    /**
     * 构造方法
     * @param context
     */
    public GameBoard(Context context) {
        super(context);
        getHolder().addCallback(callback);
//        for(int i=0; i<14; i++) {
//            for(int j=0; j<14; j++) {
//                CHESSBOARD[i][j] = new Square(j+1, i+2);
//            }
//        }
        setOnTouchListener(this);
    }

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(callback);
        setOnTouchListener(this);
    }

    /**
     * 初始化游戏
     */
    void initGame() {
        for(int i=0; i<COL; i++) {
            for(int j=0; j<COL; j++) {
                CHESSBOARD[i][j] = new Square(j+1, i+2);
                CHESSBOARD[i][j].setStates(Square.STATES_OFF);
            }
        }
        CHESSBOARD[COL-5][4].setStates(Square.STATES_ORANGE_ZERO);  //设置橙色方初始点
        CHESSBOARD[4][COL-5].setStates(Square.STATES_VIOLET_ZERO);  //设置紫色房初始点
        ORANGE_BLOCKS.removeAll(ORANGE_BLOCKS);
        VIOLET_BLOCKS.removeAll(VIOLET_BLOCKS);
        for(int i=0; i<21; i++) {
            ORANGE_BLOCKS.add((Block) (Block.ALL_BLOCK[i].clone()));  //初始化橙色方块
            VIOLET_BLOCKS.add((Block) (Block.ALL_BLOCK[i].clone()));  //初始化紫色方块
        }
        ON_BLOCK = null;
        ON_BLOCK_CHANGE = null;
        TURN_ORANGE = -1;
        TURN_VIOLET = -1;
        ON_STATES = 0;
        END_GAME = 0;
        HINTMOVE = -1;
        HINTTIME = 0;
        LASTMOVE.clear();
    }

    /**
     * 绘图方法
     */
    void ondraw() {
        ArrayList<Block> blocks;
        Canvas c = getHolder().lockCanvas();  //创建Canvas对象并锁定Canvas
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();  //创建画笔对象
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //抗锯齿设置
        paint.setColor(0xFF696969);
        RectF dest = new RectF(0, 0, (COL+2)* WIDTH, (float) ((COL+2.5)*WIDTH));
        Bitmap bitmap;
        c.drawBitmap(BACKGROUND, null, dest, paint);
//        c.drawLine(0, (float) (WIDTH * (COL + 2.5)), WIDTH * (COL + 2), (float) (WIDTH * (COL + 2.5)), paint); //上下分界线
//        c.drawLine(WIDTH * 5, (float) (WIDTH * (COL + 2.5)), WIDTH * 5, SCREEN_HEIGHT, paint);  //左操作按键分界线
//        c.drawLine(WIDTH * 11, (float) (WIDTH * (COL + 2.5)), WIDTH * 11, SCREEN_HEIGHT, paint);  //右操作按键分界线
        //c.drawLines(LINES, paint);
        for(int i=0; i<COL; i++) {
            for(int j=0; j<COL; j++) {
                dest = new RectF(CHESSBOARD[i][j].getX()*WIDTH, (CHESSBOARD[i][j].getY()-1)*WIDTH,
                        (CHESSBOARD[i][j].getX()+1)*WIDTH, CHESSBOARD[i][j].getY()*WIDTH);  //左上右下
                switch (CHESSBOARD[i][j].getStates()) {
                    case Square.STATES_OFF :
//                        paint.setColor(0xFF696969);
//                        paint.setStyle(Paint.Style.STROKE);
                        c.drawBitmap(BLANK_IMAGE, null, dest, paint);  //画出空白方块
                        break;
                    case Square.STATES_ORANGE :
//                        paint.setColor(0xFFFF8C00);
//                        paint.setStyle(Paint.Style.FILL);
                        c.drawBitmap(ORANGE_IMAGE, null, dest, paint);  //画出橙色方块
                        break;
                    case Square.STATES_VIOLET :
//                        paint.setColor(0xFF9B30FF);
//                        paint.setStyle(Paint.Style.FILL);
                        c.drawBitmap(VIOLET_IMAGE, null, dest, paint);  //画出紫色方块
                        break;
                    case Square.STATES_ORANGE_ZERO :
//                        paint.setColor(0xFFFFA161);
//                        paint.setStyle(Paint.Style.FILL);
                        c.drawBitmap(ORANGE_ZERO_IMAGE, null, dest, paint);  //画出橙色初始点
                        break;
                    case Square.STATES_VIOLET_ZERO :
//                        paint.setColor(0xFFCB94FF);
//                        paint.setStyle(Paint.Style.FILL);
                        c.drawBitmap(VIOLET_ZERO_IMAGE, null, dest, paint);  //画出紫色初始点
                        break;
                    default:
                        break;
                }
//                c.drawRect(new RectF(CHESSBOARD[i][j].getX()*WIDTH, CHESSBOARD[i][j].getY()*WIDTH,
//                        (CHESSBOARD[i][j].getX()+1)*WIDTH, (CHESSBOARD[i][j].getY()-1)*WIDTH), paint);
            }
        }
        switch (ON_STATES) {
            case 0 :
                //paint.setColor(0xFFFF8C00);
                bitmap = ORANGE_IMAGE;
                blocks = ORANGE_BLOCKS;
                break;
            case 1 :
                //paint.setColor(0xFF9B30FF);
                bitmap = VIOLET_IMAGE;
                blocks = VIOLET_BLOCKS;
                break;
            default:
                bitmap = BLANK_IMAGE;
                blocks = ORANGE_BLOCKS;
                break;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF00FF00);

        //画出现有棋子
        for(int i = 0; i < blocks.size(); i++) {
            for(Square s : blocks.get(i).getSquares()) {
                dest = new RectF((float) ((s.getX()+2+5*(i%11))*WIDTH*0.29), (float) ((s.getY()+62+7*(i/11))*WIDTH*0.29),
                        (float) ((s.getX()+2+5*(i%11))*WIDTH*0.29+WIDTH*0.29), (float) ((s.getY()+63+7*(i/11))*WIDTH*0.29));
                c.drawBitmap(bitmap, null, dest, paint);
                if(i == HINTMOVE) {
                    c.drawRect(dest, paint);
                }
            }
        }


        paint.setColor(0xFFFF0000);
        //标记出上一手棋
        if(LASTMOVE.size() != 0) {
            for(Square s : LASTMOVE) {
                dest = new RectF(s.getX()*WIDTH, (s.getY()-1)*WIDTH,
                        (s.getX()+1)*WIDTH, s.getY()*WIDTH);
                c.drawRect(dest, paint);
            }
        }

        paint.setColor(0xFF00FF00);
        //画出选中的棋子
        if(ON_BLOCK != null) {
            for (Square s : ON_BLOCK.getSquares()) {
                dest = new RectF(s.getX() * WIDTH, (s.getY() - 1) * WIDTH, (s.getX() + 1) * WIDTH, s.getY() * WIDTH);
                c.drawBitmap(bitmap, null, dest, paint);
                c.drawRect(dest, paint);
            }
        }


        paint.setColor(0xFF0000FF);
        paint.setTextSize(20);
//        paint.setStyle(Paint.Style.STROKE);
        //画出得分
        c.drawText("橙色：  " + GameRule.scores()[0] + "     紫色：  " + GameRule.scores()[1], WIDTH * 3, (float) (WIDTH * (COL + 2)), paint);

        getHolder().unlockCanvasAndPost(c);
    }

    /**
     * 回调方法
     */
    Callback callback = new Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width / (COL + 2);
            initGame();
            SCREEN_HEIGHT = height;
            SCREEN_WIDTH = width;
            ondraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    /**
     * 触摸响应事件
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Toast.makeText(getContext(), event.getX() + ":" + event.getY(), Toast.LENGTH_SHORT).show();
        if(ON_STATES != AI_STATES) {  //只有在玩家回合触摸屏幕才有效
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    DOWNX = event.getX();
                    DOWNY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (DOWNX >= WIDTH * 5 && DOWNX <= WIDTH * 10 && DOWNY > WIDTH * 5 && DOWNY < WIDTH * 10 && ON_BLOCK != null) {  //如果是滑动方块
                        int differX = (int) ((event.getX() - (ON_BLOCK.getSquares()[0].getX() * WIDTH)) / WIDTH); //现在位置与原来位置相差横坐标距离
                        int differY = (int) ((event.getY() - (ON_BLOCK.getSquares()[0].getY() * WIDTH)) / WIDTH);  //现在位置与原来位置相差纵坐标距离
                        for (int i = 0; i < ON_BLOCK.getSquares().length; i++) {
                            ON_BLOCK.getSquares()[i].setX((ON_BLOCK.getSquares()[i].getX()) + differX);
                            ON_BLOCK.getSquares()[i].setY((ON_BLOCK.getSquares()[i].getY()) + differY);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:  //手指离开屏幕
                    float x, y;
                    x = event.getX();
                    y = event.getY();
                    if(y >= (COL+3)*WIDTH && y <= (COL+7)*WIDTH) {
                        ON_BLOCK_CHANGE = null;
                        int i = (int) (x/(WIDTH*0.29*5)) + (11*(int)(((y-(COL+3)*WIDTH)/(WIDTH*2))));   //计算点击的是哪颗棋子
                        switch (ON_STATES) {
                            case 0:
                                if(i < ORANGE_BLOCKS.size()) {
                                    TURN_ORANGE = i;
                                    ON_BLOCK = (Block) ORANGE_BLOCKS.get(i).clone();
                                    ON_BLOCK = ON_BLOCK.placedInTheMiddle();
                                }
                                break;
                            case 1:
                                if(i < VIOLET_BLOCKS.size()) {
                                    TURN_VIOLET = i;
                                    ON_BLOCK = (Block) VIOLET_BLOCKS.get(i).clone();
                                    ON_BLOCK = ON_BLOCK.placedInTheMiddle();
                                }
                                break;
                            default:
                                break;
                        }
                        if(ON_BLOCK != null)
                            ON_BLOCK.placedInTheMiddle();

                    } else {
                        if (ON_BLOCK != null) {
                            if (GameRule.isAvailable(ON_BLOCK, ON_STATES, CHESSBOARD)) {  //如果移动满足条件
                                LASTMOVE.clear();
                                for (Square s : ON_BLOCK.getSquares()) {
                                    LASTMOVE.add(new Square(s.getX(), s.getY()));
                                    CHESSBOARD[s.getY() - 2][s.getX() - 1].setStates(ON_STATES);  //更改方块当前所在位置上对应坐标方块的状态
                                }
                                switch (ON_STATES) {  //从数组链表中去除用掉的方块
                                    case 0:
                                        ORANGE_BLOCKS.remove(TURN_ORANGE);
                                        TURN_ORANGE = TURN_ORANGE % ORANGE_BLOCKS.size();
                                        break;
                                    case 1:
                                        VIOLET_BLOCKS.remove(TURN_VIOLET);
                                        TURN_VIOLET = TURN_VIOLET % VIOLET_BLOCKS.size();
                                        break;
                                    default:
                                        break;
                                }

                                ON_BLOCK = null;
                                ON_BLOCK_CHANGE = null;
                                HINTMOVE = -1;
                                HINTTIME = 0;

                                if (END_GAME == 0)  //若双方均未结束，则轮换
                                    ON_STATES = (ON_STATES + 1) % 2;

                                gameOver(CHESSBOARD);  //判断游戏是否结束

                                if (ON_STATES == AI_STATES)
                                    new Thread(this).start();  //玩家下完有效一着则电脑开始计算下一着

                            } else {  //如果移动不满足条件
                                switch (ON_STATES) {
                                    case 0:
                                        if(ON_BLOCK_CHANGE != null)
                                            ON_BLOCK = ON_BLOCK_CHANGE;
                                        else
                                            ON_BLOCK = (Block) ORANGE_BLOCKS.get(TURN_ORANGE).clone();
                                        break;
                                    case 1:
                                        if(ON_BLOCK_CHANGE != null)
                                            ON_BLOCK = ON_BLOCK_CHANGE;
                                        else
                                            ON_BLOCK = (Block) VIOLET_BLOCKS.get(TURN_VIOLET).clone();
                                        break;
                                    default:
                                        break;
                                }
                                ON_BLOCK = ON_BLOCK.placedInTheMiddle();
                            }
                        }
                    }
                    break;
            }
            ondraw();
        }
        return true;
    }

    /**
     * 轮到电脑行棋
     */
    public void AIMoves() {
        if(ON_STATES == AI_STATES) {
            GameRule.executeTheAIMoves(BESTMOVE, ON_STATES);
            ON_BLOCK = null;
        }
//        switch (ON_STATES) {
//            case 0:
//                TURN_ORANGE = TURN_ORANGE % ORANGE_BLOCKS.size();
//                ON_BLOCK = (Block) VIOLET_BLOCKS.get(TURN_VIOLET).clone();
//                break;
//            case 1:
//                TURN_VIOLET = TURN_VIOLET % VIOLET_BLOCKS.size();
//                ON_BLOCK = (Block) ORANGE_BLOCKS.get(TURN_ORANGE).clone();
//                break;
//            default:
//                break;
//        }
        if(END_GAME == 0)
            ON_STATES = (ON_STATES + 1) % 2;
        gameOver(CHESSBOARD);  //判断游戏是否结束
//        ON_BLOCK = ON_BLOCK.placedInTheMiddle();
        ondraw();
        if(END_GAME !=0 && ON_STATES == AI_STATES) {  //如果玩家已无棋子可放
            new Thread(this).start();
        }
    }

    /**
     * 游戏结束
     */
    public void gameOver(Square[][] CHESSBOARD) {
        if (GameRule.isOver(ON_STATES, CHESSBOARD)) {  //若轮换后已无棋可下
            switch (ON_STATES) {
                case Square.STATES_ORANGE:
                    ON_STATES = Square.STATES_VIOLET;
                    WHOSE_TURN = "橙色方";
                    break;
                case Square.STATES_VIOLET:
                    ON_STATES = Square.STATES_ORANGE;
                    WHOSE_TURN = "紫色方";
                    break;
                default:
                    break;
            }
            Toast.makeText(getContext(), WHOSE_TURN + "已无地方可放！！", Toast.LENGTH_SHORT).show();  //如果已无方块可放，则提示
            END_GAME++;
//            System.out.println("END_GAME:" + END_GAME + "   ON_STATES:" + ON_STATES);
            if (GameRule.isOver(ON_STATES, CHESSBOARD)) {  //如果轮换后对方也无棋可下
                System.out.println("游戏结束！！");
                String s;
                int score = 0;
                if (GameRule.scores()[0] > GameRule.scores()[1]) {
                    s = "恭喜橙色获胜！";
                    score = GameRule.scores()[0];
                } else if (GameRule.scores()[0] < GameRule.scores()[1]) {
                    s = "恭喜紫色获胜！";
                } else {
                    s = "双方打平！";
                }
                ON_STATES = -1;
                System.out.println("minHighScore:" + MainActivity.minHighScore);
                if(score > MainActivity.minHighScore) {
                    System.out.println("进入dialog2！！");
                    dialog2(score);
                }

                dialog("得分  橙色：" + GameRule.scores()[0] + "  紫色:" + GameRule.scores()[1] + "  " + s);
            }
        }
    }

    /**
     * 对话框
     * @param s
     */
    public void dialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(s);
        builder.setTitle("游戏结束");
        builder.setPositiveButton("重来", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                initGame();
                ondraw();
            }
        });
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
         });
        builder.show();
    }

    /**
     * 对话框2
     * @param score
     */
    public void dialog2(final int score) {
        final EditText et = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setView(et);
        builder.setMessage("请输入玩家名(不超过20个字符):");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String player = et.getText().toString();
                if (player.equals("")) {
                    Toast.makeText(getContext(), "玩家名不能为空！", Toast.LENGTH_LONG).show();
                    try {
                        // 不关闭对话框
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(player.length() > 20) {
                    Toast.makeText(getContext(), "玩家名不能超过20个字符！", Toast.LENGTH_LONG).show();
                    try {
                        // 不关闭对话框
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    DatabaseHelper database = new DatabaseHelper(getContext());
                    database.insertData(player, score);
                    MainActivity.minHighScore = database.getMinHighScore();
                    database.close();
                    try {
                        // 关闭对话框
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.show();

    }

    /**
     * 重载run方法
     */
    @Override
    public void run() {
        if(ON_STATES == AI_STATES) {
            System.out.println("run方法!!!");
            AlphaBeta.initQuatation();
            AlphaBeta.clearBestMoves();
            AlphaBeta.alphaBeta(3, -10000, 10000, AI_STATES);
            try {
                BESTMOVE = AlphaBeta.BEST_MOVES.pop();
            } catch (EmptyStackException ese) {  //空栈异常
                ese.printStackTrace();
            }
            System.out.println("best move: " + BESTMOVE[0] + BESTMOVE[1] + BESTMOVE[2] + BESTMOVE[3] + BESTMOVE[4] + BESTMOVE[5]);
            ////Toast或者Dialog中都有一个Handler的成员变量，
            // 在初始化时都会跟着初始化，而Toast或者Dialog中的Handler都需要一个Looper，
            // 所以需要在包含该Toast或者Dialog的线程中初始化Looper。
            Looper.prepare();
            AIMoves();  //如果玩家落子有效则轮到电脑
            Looper.loop();
        }
        }
}

