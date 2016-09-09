package cn.zhangmin.blokusduo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by zhangmin on 2016/5/5.
 */
public class GameActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        Button restartBtn = (Button) findViewById(R.id.restartBt);
        Button hintBtn = (Button) findViewById(R.id.hintBt);
        Button leftRightBtn = (Button) findViewById(R.id.leftrightBt);
        Button upDown = (Button) findViewById(R.id.upDownBt);
        Button leftRevolveBtn = (Button) findViewById(R.id.leftrevolveBt);
        Button rightRevolveBtn = (Button) findViewById(R.id.rightrevolveBt);
        Button returnBtn = (Button) findViewById(R.id.returnBt);

        final GameBoard gb = (GameBoard) findViewById(R.id.gameView);

        //重新开始按钮
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GameActivity.this).setTitle("提示")
                .setMessage("是否重新开始？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gb.initGame();
                        gb.ondraw();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
            }
        });

        //左右变换按钮
        leftRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gb.ON_BLOCK != null) {
                    gb.ON_BLOCK.leftRightTransformation();
                    gb.ON_BLOCK.placedInTheMiddle();
                    gb.ON_BLOCK_CHANGE = gb.ON_BLOCK;
                    gb.ondraw();
                }
            }
        });

        //上下变换按钮
        upDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gb.ON_BLOCK != null) {
                    gb.ON_BLOCK.upDownTransformation();
                    gb.ON_BLOCK.placedInTheMiddle();
                    gb.ON_BLOCK_CHANGE = gb.ON_BLOCK;
                    gb.ondraw();
                }
            }
        });

        //提示按钮
        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameRule.hint(GameBoard.ON_STATES);
                gb.ondraw();
            }
        });

        //左旋转按钮
        leftRevolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gb.ON_BLOCK != null) {
                    gb.ON_BLOCK.leftRevolveTransformation();
                    gb.ON_BLOCK.placedInTheMiddle();
                    gb.ON_BLOCK_CHANGE = gb.ON_BLOCK;
                    gb.ondraw();
                }
            }
        });

        //右旋转按钮
        rightRevolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gb.ON_BLOCK != null) {
                    gb.ON_BLOCK.rightRevolveTransformation();
                    gb.ON_BLOCK.placedInTheMiddle();
                    gb.ON_BLOCK_CHANGE = gb.ON_BLOCK;
                    gb.ondraw();
                }
            }
        });

        //返回按钮
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GameActivity.this).setTitle("提示")
                        .setMessage("是否返回主界面？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setClass(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                GameActivity.this.finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

    }

    /**
     * 重写返回键响应方法
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(GameActivity.this).setTitle("提示")
                    .setMessage("返回主界面？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setClass(GameActivity.this, MainActivity.class);
                            startActivity(intent);
                            GameActivity.this.finish();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
