package cn.zhangmin.blokusduo;


/**
 * Created by zhangmin on 2016/3/5.
 * 方块棋子类，由数个Square组成
 */
public class Block implements Cloneable{
    private Square[] squares;  //包含所有方块棋子的数组
    //21种方块
    static final Block BLOCK_0 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(0,1), new Square(0,-1), new Square(0,-2)});
    static final Block BLOCK_1 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(-1,0), new Square(0,-1), new Square(0,-2)});
    static final Block BLOCK_2 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(-1,0), new Square(0,1), new Square(0,-1)});
    static final Block BLOCK_3 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(-1,-1), new Square(0,1), new Square(0,-1)});
    static final Block BLOCK_4 = new Block(new Square[]{new Square(0,0), new Square(0,1), new Square(0,-1), new Square(0,-2), new Square(1,-1)});
    static final Block BLOCK_5 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(0,1), new Square(0,-1), new Square(1,1)});
    static final Block BLOCK_6 = new Block(new Square[]{new Square(0,0), new Square(0,2), new Square(0,1), new Square(0,-1), new Square(0,-2)});
    static final Block BLOCK_7 = new Block(new Square[]{new Square(0,0), new Square(0,1), new Square(0,-1), new Square(1,1), new Square(2,1)});
    static final Block BLOCK_8 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(0,1), new Square(1,0), new Square(1,-1)});
    static final Block BLOCK_9 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(-1,0), new Square(1,0), new Square(1,-1)});
    static final Block BLOCK_10 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(0,1), new Square(0,-1), new Square(1,0)});
    static final Block BLOCK_11 = new Block(new Square[]{new Square(0,0), new Square(-1,0), new Square(0,1), new Square(0,-1), new Square(1,0)});
    static final Block BLOCK_12 = new Block(new Square[]{new Square(0,0), new Square(0,1), new Square(0,-1), new Square(0,-2)});
    static final Block BLOCK_13 = new Block(new Square[]{new Square(0,0), new Square(-1,1), new Square(0,1), new Square(0,-1)});
    static final Block BLOCK_14 = new Block(new Square[]{new Square(0,0), new Square(0,1), new Square(0,-1), new Square(1,0)});
    static final Block BLOCK_15 = new Block(new Square[]{new Square(0,0), new Square(-1,0), new Square(0,-1), new Square(1,-1)});
    static final Block BLOCK_16 = new Block(new Square[]{new Square(0,0), new Square(0,-1), new Square(1,0), new Square(1,-1)});
    static final Block BLOCK_17 = new Block(new Square[]{new Square(0,0), new Square(0,1), new Square(0,-1)});
    static final Block BLOCK_18 = new Block(new Square[]{new Square(0,0), new Square(0,-1), new Square(1,0)});
    static final Block BLOCK_19 = new Block(new Square[]{new Square(0,0), new Square(0,-1)});
    static final Block BLOCK_20 = new Block(new Square[]{new Square(0,0)});


    static final Block[] ALL_BLOCK = new Block[]{BLOCK_0, BLOCK_1, BLOCK_2, BLOCK_3, BLOCK_4, BLOCK_5, BLOCK_6, BLOCK_7, BLOCK_8, BLOCK_9, BLOCK_10, BLOCK_11,
            BLOCK_12, BLOCK_13, BLOCK_14, BLOCK_15, BLOCK_16, BLOCK_17, BLOCK_18, BLOCK_19, BLOCK_20};

    /**
     * 构造方法
     * @param blocks
     */
    public Block(Square[] blocks) {
            this.squares = blocks;
        }

    public Square[] getSquares() {
        return squares;
    }

    public void setSquares(Square[] blocks) {
        this.squares = blocks;
    }

    /**
     * 左右对称变换方法
     * @return
     */
    public Block leftRightTransformation() {
        for(Square s : this.getSquares()) {
            s.setX(-(s.getX()));
        }
        return this;
    }

    /**
     * 上下对称变换方法
     * @return
     */
    public Block upDownTransformation() {
        for(Square s : this.getSquares()) {
            s.setY(-(s.getY()));
        }
        return this;
    }

    /**
     * 左旋转变换方法
     * 注意y轴正方向向下
     * @return
     */
    public Block leftRevolveTransformation() {
//        for(Square s : this.getSquares()) {
//            int temp = s.getX();
//            s.setX(s.getY());
//            s.setY(-temp);
//        }
        for(int i=0; i<3; i++) {
            this.rightRevolveTransformation();
            this.placedInTheMiddle();
        }
        return this;
    }

    /**
     * 右旋转变换方法
     * @return
     */
    public Block rightRevolveTransformation() {
        //System.out.println("右转方法————————————————————————");
        for(Square s : this.getSquares()) {
            //System.out.println("变换前" + s.getX() + ":" + s.getY());
            int temp = s.getX();
            s.setX(-(s.getY()));
            s.setY(temp);
            //System.out.println("变换后" + s.getX() + ":" + s.getY());
        }
        return this;
    }

    /**
     * 让被选中的方块在选择框中居中显示
     * @return
     */
    public Block placedInTheMiddle() {
        int differX = 0;
        int differY = 0;
        for(Square s : this.getSquares()) {
            differX = differX + (7 - s.getX());
            differY = differY + (9 - s.getY());
        }
        differX =  differX / this.getSquares().length;
        differY =  differY / this.getSquares().length;
//        differX = 8 - this.getSquares()[0].getX();
//        differY = 20 - this.getSquares()[0].getY();
        for(Square s : this.getSquares()) {
            s.setX(s.getX() + differX);
            s.setY(s.getY() + differY);
        }
        return this;
    }


    /**
     * 深克隆方法
     * @return
     */
    @Override
    public Object clone(){
        Block b = null;
//        try {
//            b = (Block) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        b.squares = squares.clone();
        Square[] s = new Square[squares.length];
        for(int i=0; i<squares.length; i++) {
            s[i] = new Square(((Square)squares[i].clone()).getX(), ((Square)squares[i].clone()).getY());
        }
        b = new Block(s);
        return b;
    }
}
