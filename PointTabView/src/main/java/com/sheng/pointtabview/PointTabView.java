package com.sheng.pointtabview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/21.
 */

public class PointTabView extends View {

    private final boolean DEBUG = true;
    //    private String[] datas = {"标题一", "标题二", "标题三", "标题四", "标题五", "标题六"};
    private List<String> datas = new ArrayList<>();

    {
        datas.add("标题一");
        datas.add("标题二");
        datas.add("标题三");
        datas.add("标题四");
        datas.add("标题五");
        datas.add("标题六");
    }

    //默认值
    private final int DEFAULT_TEXT_COLOR = 0xFF000000;
    private final int DEFAULT_MIN_TEXT_SIZE = 10;
    private final int DEFAULT_MAX_TEXT_SIZE = 15;
    private final int DEFAULT_POINT_COLOR = 0xFFFF4081;
    private final int DEFAULT_MIN_POINT_SIZE = 5;
    private final int DEFAULT_MAX_POINT_SIZE = 10;
    private final int DEFAULT_RING_STROKE_WIDTH = 2;
    private final int DEFAULT_LINE_COLOR = 0xFFFF4081;
    private final int DEFAULT_LINE_SIZE = 2;
    private final int DEFAULT_SPACE = 5;

    //属性值
    private int textColor = DEFAULT_TEXT_COLOR;
    private int minTextSize = DEFAULT_MIN_TEXT_SIZE;
    private int maxTextSize = DEFAULT_MAX_TEXT_SIZE;
    private int pointColor = DEFAULT_POINT_COLOR;
    private int minPointSize = DEFAULT_MIN_POINT_SIZE;
    private int maxPointSize = DEFAULT_MAX_POINT_SIZE;
    private int ringStrokeWidth = DEFAULT_RING_STROKE_WIDTH;
    private int lineColor = DEFAULT_LINE_COLOR;
    private int lineSize = DEFAULT_LINE_SIZE;
    private int space = DEFAULT_SPACE;

    //画笔
    private Paint linePaint;
    private Paint textPaint;
    private Paint circlePaint;
    private Paint ringPaint;

    //监听
    private ItemChangerListener listener;

    //最大文字高度
    private float textHeight;
    //item间距
    private float itemSpace;
    //边缘item与边界距离 - 停止时的距离
    private float bSpace;
    //线总长度
    private float lineLength;
    //计算中心区域 - 圆形和文字缩放区域，超过这个区域维持最小
    private float centerL;
    private float centerR;
    //记录大小圆半径
    private float minR;
    private float maxR;
    //两圆半径差
    private float dr;
    //文字顶部y坐标
    private float textY;
    //记录大小文字size差值
    private float dts;
    //滑动偏移量
    private float offset;
    //线与圆 - y坐标
    private float lineY = 0;
    //控件中心点
    private float cx;
    //点击区域
    private List<ClickBean> clickList;
    //页码
    private int position = 0;
    private int oldPosition = 0;
    //显示的item数量
    private int currentCount = 3;
    //记录触摸坐标
    private float dx;
    private float mx;
    //允许触摸
//    private boolean touchFlag = true;
    //触摸滑动中
    private boolean isScroll = false;
    //滚动动画
    private ValueAnimator animator;
    //按下的时间戳
    private long touchTime;

    public PointTabView(Context context) {
        this(context, null);
    }

    public PointTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointTabView, defStyleAttr, 0);

        textColor = typedArray.getColor(R.styleable.PointTabView_textColor, DEFAULT_TEXT_COLOR);
        minTextSize = typedArray.getDimensionPixelSize(R.styleable.PointTabView_minTextSize, DEFAULT_MIN_TEXT_SIZE);
        maxTextSize = typedArray.getDimensionPixelSize(R.styleable.PointTabView_maxTextSize, DEFAULT_MAX_TEXT_SIZE);
        pointColor = typedArray.getColor(R.styleable.PointTabView_pointColor, DEFAULT_POINT_COLOR);
        minPointSize = typedArray.getDimensionPixelSize(R.styleable.PointTabView_minPointSize, DEFAULT_MIN_POINT_SIZE);
        maxPointSize = typedArray.getDimensionPixelSize(R.styleable.PointTabView_maxPointSize, DEFAULT_MAX_POINT_SIZE);
        ringStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.PointTabView_ringStrokeWidth, DEFAULT_RING_STROKE_WIDTH);
        lineColor = typedArray.getColor(R.styleable.PointTabView_lineColor, DEFAULT_LINE_COLOR);
        lineSize = typedArray.getDimensionPixelSize(R.styleable.PointTabView_lineSize, DEFAULT_LINE_SIZE);
        space = typedArray.getDimensionPixelSize(R.styleable.PointTabView_space, DEFAULT_SPACE);

        log("textColor = " + textColor + "");
        log("minTextSize = " + minTextSize + "");
        log("maxTextSize = " + maxTextSize + "");
        log("pointColor = " + pointColor + "");
        log("minPointSize = " + minPointSize + "");
        log("maxPointSize = " + maxPointSize + "");
        log("lineColor = " + lineColor + "");
        log("lineSize = " + lineSize + "");


        initPaint();
        initText();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineSize);
        linePaint.setStrokeCap(Paint.Cap.BUTT);
        linePaint.setColor(lineColor);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(maxTextSize);
        textPaint.setColor(textColor);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(pointColor);

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(pointColor);
        ringPaint.setStyle(Paint.Style.STROKE);//空心
        ringPaint.setStrokeWidth(ringStrokeWidth);
    }

    private void initText() {
        Paint.FontMetricsInt font = textPaint.getFontMetricsInt();
        textHeight = font.bottom - font.ascent;
        log("textHeight = " + textHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            int size = (int) (getPaddingTop() + getPaddingBottom() + maxPointSize + ringStrokeWidth + space + textHeight + 0.5);
            log("size = " + size);
            int hms = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, hms);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w / 2;
        //线圆所在的y坐标
        lineY = getPaddingTop() + ringStrokeWidth + maxPointSize / 2;
        //动态可变参数
        initParameter();
    }

    //计算可动态改变的相关参数
    private void initParameter() {
        //item之间的间距
        itemSpace = (getWidth() - getPaddingLeft() - getPaddingRight()) / currentCount;
        log("itemSpace = " + itemSpace);
        //边缘距离-设定为item间距的一半
        bSpace = itemSpace / 2;
        //线的总长度
        lineLength = (datas.size() - 1) * itemSpace;
        log("lineLength = " + lineLength);
        //计算中心区域 - 圆形和文字缩放区域，超过这个区域维持最小
//        centerL = cx - bSpace;
//        centerR = cx + bSpace;
        centerL = cx - itemSpace;
        centerR = cx + itemSpace;
        //记录大小圆半径
        minR = minPointSize / 2;
        maxR = maxPointSize / 2;
        dr = maxR - minR;
        //text顶部坐标
        textY = getPaddingTop() + maxPointSize + space;
        //记录textSize差值
        dts = maxTextSize - minTextSize;
        //重新计算当前偏移量 - position从0开始
        offset = position * itemSpace;
        //规划可点击范围
        clickList = new ArrayList<>();
        int clickRange = (int) (0.5 + itemSpace / 6);
        int moveValue = (1 - currentCount) / 2;
        for (int i = 0; i < currentCount; i++) {
            float px = bSpace + itemSpace * i;
            Rect rect = new Rect((int) (px - clickRange), getPaddingTop(), (int) (px + clickRange), getHeight() - getPaddingBottom());
            ClickBean bean = new ClickBean(rect, moveValue);
            clickList.add(bean);
            moveValue++;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawLine(cx, lineY, cx + 500, lineY, linePaint);
        //画线
        float lx_1 = cx - offset;
        float lx_2 = lx_1 + lineLength;
//        log("lx_1 = " + lx_1 + "，lx_2 = " + lx_2 + "，lineY = " + lineY);
        canvas.drawLine(lx_1, lineY, lx_2, lineY, linePaint);
        //画圆 - 画字
        for (int i = 0; i < datas.size(); i++) {
            float circleX = lx_1 + itemSpace * i;
            canvas.drawCircle(circleX, lineY, minR, circlePaint);
            //当圆处于中心区域时
            if (circleX > centerL && circleX < centerR) {
                float temp = Math.abs(cx - circleX);
                float scale = temp / itemSpace;
                canvas.drawCircle(circleX, lineY, maxR - dr * scale, ringPaint);
                textPaint.setTextSize(maxTextSize - dts * scale);
                Paint.FontMetricsInt font = textPaint.getFontMetricsInt();
                canvas.drawText(datas.get(i), circleX, textY - font.top, textPaint);
            } else {
                textPaint.setTextSize(minTextSize);
                Paint.FontMetricsInt font = textPaint.getFontMetricsInt();
                canvas.drawText(datas.get(i), circleX, textY - font.top, textPaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animator != null && animator.isRunning())
            animator.cancel();
//        if (!touchFlag) {//动画播放中不允许滑动
//            log("不允许 action = " + event.getAction());
//            return true;
//        }
        log("action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = event.getX();
                mx = dx;
                touchTime = System.currentTimeMillis();
                isScroll = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = mx - event.getX();//TODO - 迎合vp，是否需要相反
                mx = event.getX();
                offset += deltaX;
                //滑动边界判断
                if (offset < 0)
                    offset = 0;
                else if (offset > lineLength)
                    offset = lineLength;
                //判断更改position
                int tempPosition = (int) (offset / itemSpace);//TODO - 增加判断范围
                if (offset % itemSpace > bSpace)
                    tempPosition++;
                if (position != tempPosition) {
                    position = tempPosition;
                    if (listener != null)
                        listener.onItemScroll(position);
                    log("position = " + tempPosition);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isScroll = false;
                float ux = event.getX();
                long upTime = System.currentTimeMillis();
                if (Math.abs(ux - dx) < 5 && upTime - touchTime < 200) {//判断为点击
                    log("点击");
                    for (int i = 0; i < clickList.size(); i++) {
                        ClickBean bean = clickList.get(i);
                        boolean contains = bean.rect.contains((int) ux, (int) event.getY());
                        if (contains) {
                            log("在区域内");
                            int temp = position + bean.moveTo;
                            //判断区域上是否有item
                            if (temp >= 0 && temp < datas.size()) {
                                log("该区域可以切换position");
                                position = temp;
//                                returnPosition();
                            }
                        }
                    }
                }
                if (oldPosition != position)
                    returnPosition();
                oldPosition = position;
                //定位到position
                scrollToPosition(position);
                break;
        }
        return true;
    }

    //滑动到指定position
    private void scrollToPosition(int position) {
        if (isScroll)
            return;
        this.position = position;
        scroll(offset, position * itemSpace);
    }

    private void scroll(float v1, float v2) {
        animator = ValueAnimator.ofFloat(v1, v2);
        animator.setDuration(200);
        animator.addUpdateListener(updateListener);
        animator.addListener(animatorListener);
        animator.setInterpolator(new ScrollInterpolator());
        animator.start();
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            offset = value;
            invalidate();
        }

    };

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {
//            touchFlag = false;
        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {
//            touchFlag = true;
        }

        @Override
        public void onAnimationStart(Animator animation) {
//            touchFlag = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
//            touchFlag = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
//            touchFlag = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    //定位到
    public void setPosition(int position) {
        this.position = position;
        initParameter();
        invalidate();
        //TODO - 可添加动画
    }

    //前台显示的item个数
    public void setCurrentCount(int currentCount) {
        currentCount = Math.abs(currentCount);
        if (currentCount % 2 == 0)
            currentCount++;
        this.currentCount = currentCount;
        initParameter();
        invalidate();
    }

    public void setData(List<String> datas) {
        this.datas = datas;
        initParameter();
        invalidate();
    }

    public void bindViewPager(ViewPager vp) {
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int state = 0;
            float oldOffset = -1;
            boolean flag = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!isScroll && flag && state != 0) {
                    if (!(oldOffset == -1 || Math.abs(positionOffset - oldOffset) > 0.5)) {
                        float df = positionOffset - oldOffset;
                        log("df = " + df);
                        if (df > 0) {//item往左
                            offset += itemSpace * df;
                        } else {//往右
                            offset += itemSpace * df;
                        }
                        invalidate();
                    }
                    oldOffset = positionOffset;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (isScroll)
                    return;
                if (position < datas.size())
                    scrollToPosition(position);
                oldOffset = -1;
                flag = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                this.state = state;
                if (state == 0) {
                    flag = true;
//                    if (isScroll)
//                        return;
                    //加上，快速左右滑动的时候会有闪烁，不加速，会有一点点偏差
//                    offset = itemSpace * position;
//                    invalidate();
                }
            }
        });
    }

    public void setItemChangerListener(ItemChangerListener listener) {
        this.listener = listener;
    }

    private void returnPosition() {
        if (listener != null)
            listener.onItemChanger(position);
    }

    private void log(String log) {
        if (DEBUG)
            Log.i("测试", log);
    }

    public interface ItemChangerListener {
        void onItemScroll(int position);

        void onItemChanger(int position);
    }

    class ScrollInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
//        Log.i("加速器", "input = " + input);
//        (x-1)^2
            return 1 - (input - 1) * (input - 1);
        }
    }

    private class ClickBean {
        public Rect rect;
        public int moveTo;

        public ClickBean(Rect rect, int moveTo) {
            this.rect = rect;
            this.moveTo = moveTo;
        }
    }
}
