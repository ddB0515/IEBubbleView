package com.iebubble;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class IEBubbleView extends RelativeLayout {

    private static final float STROKE = 1.0f;
    private int sizeWidth, sizeHeight;

    private float mPadding  = 15;
    private float mPaddingLeft = 1;
    private float mPaddingTop = 10;
    private float mPaddingRight = 1;
    private float mPaddingBottom = 10;

    // points needed to calculate/draw curved arrow
    //      P1 *    * P2
    //         |   /
    //         | _/
    //         \/
    //         P3
    private float mPoint1Distance  = 65;
    private float mPoint2Distance = 15;
    private float mPoint3Distance = 25;

    private int   mRadius   = 10;

    private int backgroundColor = Color.WHITE;
    private Paint mPaint;
    private RectF bubbleRect;
    private Position arrowPosition;
    private Orientation orientation;
    private ArrowType arrowType;
    private Point p1, p2, p3;
    private Point topLeft;
    private Point bottomRight;
    private Path path;

    public IEBubbleView(Context context) {
        super(context);
        init(null);
    }

    public IEBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IEBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        setWillNotDraw(false);
        mPadding  = convertDpToPixel(mPadding);
        mPaddingLeft = convertDpToPixel(mPaddingLeft);
        mPaddingTop = convertDpToPixel(mPaddingTop);
        mPaddingRight = convertDpToPixel(mPaddingRight);
        mPaddingBottom = convertDpToPixel(mPaddingBottom);
        mRadius   = convertDpToPixel(mRadius);

        mPoint1Distance = convertDpToPixel(mPoint1Distance);
        mPoint2Distance     = convertDpToPixel(mPoint2Distance);
        mPoint3Distance     = convertDpToPixel(mPoint3Distance);
        arrowPosition = Position.DEFAULT;
        orientation     = Orientation.LEFT;
        arrowType       = ArrowType.NORMAL;

        p1 = new Point();
        p2 = new Point();
        p3 = new Point();
        topLeft = new Point();
        bottomRight = new Point();
        bubbleRect = new RectF();
        path = new Path();

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IEBubbleView);
            try {
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_color)) {
                    backgroundColor = typedArray.getColor(R.styleable.IEBubbleView_ieb_color, backgroundColor);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_position)) {
                    arrowPosition = Position.fromInt(typedArray.getInt(R.styleable.IEBubbleView_ieb_position,
                            Position.DEFAULT.getValue()));
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_orientation)) {
                    orientation = Orientation.fromInt(typedArray.getInt(R.styleable.IEBubbleView_ieb_orientation,
                            Orientation.DEFAULT.getValue()));
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_type)) {
                    arrowType = ArrowType.fromInt(typedArray.getInt(R.styleable.IEBubbleView_ieb_type,
                            ArrowType.DEFAULT.getValue()));
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_radius)) {
                    mRadius = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_radius, mRadius);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_distance)) {
                    mPoint1Distance = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_distance, (int)mPoint1Distance);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_marginLeft)) {
                    mPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_marginLeft, (int)mPaddingLeft);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_marginTop)) {
                    mPaddingTop = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_marginTop, (int)mPaddingTop);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_marginRight)) {
                    mPaddingRight = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_marginRight, (int)mPaddingRight);
                }
                if (typedArray.hasValue(R.styleable.IEBubbleView_ieb_marginBottom)) {
                    mPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.IEBubbleView_ieb_marginBottom, (int)mPaddingBottom);
                }
            } finally {
                typedArray.recycle();
            }
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(backgroundColor);
        mPaint.setStrokeWidth(STROKE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // placeholder points (container)
        topLeft.x = 0;
        topLeft.y = 0;
        bottomRight.x = sizeWidth;
        bottomRight.y = sizeHeight;

        // Bubble rectangle
        bubbleRect.left = topLeft.x + mPaddingLeft;
        bubbleRect.top = topLeft.y + mPaddingTop;
        bubbleRect.right = bottomRight.x - mPaddingRight;
        bubbleRect.bottom = bottomRight.y - mPaddingBottom;

        if (arrowType == ArrowType.SMALL) {
            mPoint2Distance = convertDpToPixel(10.0f);
        }

        switch (arrowPosition) {
            case TOP_LEFT:
                bubbleRect.top += mPadding;
                p1.x = (int) (bubbleRect.left + mPoint1Distance);
                p1.y = (int) bubbleRect.top;
                p2.x = (int) (bubbleRect.left + mPoint1Distance + mPoint2Distance);
                p2.y = (int) bubbleRect.top;
                if (orientation == Orientation.LEFT) {
                    p3.x = (int) (bubbleRect.left + mPoint1Distance - mPoint3Distance / 2);
                    p3.y = (int) bubbleRect.left + convertDpToPixel(10);
                } else {
                    p3.x = (int) (bubbleRect.left + mPoint1Distance + mPoint3Distance);
                    p3.y = (int) bubbleRect.left + convertDpToPixel(10);
                }
                path.reset();
                if (arrowType == ArrowType.SMALL) {
                    if (orientation == Orientation.LEFT){
                        p3.x = p1.x;
                        p3.y = p3.y + convertDpToPixel(5);
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p2.x - convertDpToPixel(5), p2.y - convertDpToPixel(2), p3.x + convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p2.x - convertDpToPixel(2), p3.y, p2.x - convertDpToPixel(2), p2.y, p2.x, p2.y);
                    } else {
                        p3.x = p2.x;
                        p3.y = p3.y + convertDpToPixel(5);
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p1.x + convertDpToPixel(5), p1.y - convertDpToPixel(2), p3.x - convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p1.x + convertDpToPixel(2), p3.y, p1.x + convertDpToPixel(2), p2.y, p1.x, p1.y);
                    }
                } else {
                    if (orientation == Orientation.LEFT) {
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p1.x, p1.y - convertDpToPixel(5), p3.x + mPadding / 4, p1.y - mPadding, p3.x, p3.y);
                        path.cubicTo(p1.x, p3.y, p2.x, p2.y - mPadding / 2, p2.x, p2.y);
                    } else {
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p2.x, p2.y - convertDpToPixel(5), p3.x - mPadding / 4, p1.y - mPadding, p3.x, p3.y);
                        path.cubicTo(p2.x, p3.y, p1.x, p1.y - mPadding / 2, p1.x, p1.y);
                    }
                }
                path.close();
                canvas.drawPath(path, mPaint);
                break;

            case TOP_RIGHT:
                bubbleRect.top += mPadding;
                p1.x = (int) (bubbleRect.right - mPoint1Distance);
                p1.y = (int) bubbleRect.top;
                p2.x = (int) (bubbleRect.right - mPoint1Distance + mPoint2Distance);
                p2.y = (int) bubbleRect.top;
                if (orientation == Orientation.LEFT) {
                    p3.x = (int) (bubbleRect.right - mPoint1Distance - mPoint3Distance / 2);
                    p3.y = (int) bubbleRect.left + convertDpToPixel(10);
                } else {
                    p3.x = (int) (bubbleRect.right - mPoint1Distance + mPoint3Distance);
                    p3.y = (int) bubbleRect.left + convertDpToPixel(10);
                }
                path.reset();
                if (arrowType == ArrowType.SMALL) {
                    if (orientation == Orientation.LEFT){
                        p3.x = p1.x;
                        p3.y = p3.y + convertDpToPixel(5);
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p2.x - convertDpToPixel(5), p2.y - convertDpToPixel(2), p3.x + convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p2.x - convertDpToPixel(2), p3.y, p2.x - convertDpToPixel(2), p2.y, p2.x, p2.y);
                    } else {
                        p3.x = p2.x;
                        p3.y = p3.y + convertDpToPixel(5);
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p1.x + convertDpToPixel(5), p1.y - convertDpToPixel(2), p3.x - convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p1.x + convertDpToPixel(2), p3.y, p1.x + convertDpToPixel(2), p2.y, p1.x, p1.y);
                    }
                } else {
                    if (orientation == Orientation.LEFT) {
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p1.x, p1.y - convertDpToPixel(5), p3.x + mPadding / 4, p1.y - mPadding, p3.x, p3.y);
                        path.cubicTo(p1.x, p3.y, p2.x, p2.y - mPadding / 2, p2.x, p2.y);
                    } else {
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p2.x, p2.y - convertDpToPixel(5), p3.x - mPadding / 4, p1.y - mPadding, p3.x, p3.y);
                        path.cubicTo(p2.x, p3.y, p1.x, p1.y - mPadding / 2, p1.x, p1.y);
                    }
                }
                path.close();
                canvas.drawPath(path, mPaint);
                break;

            case BOTTOM_LEFT:
                bubbleRect.bottom -= mPadding;
                p1.x = (int) (bubbleRect.left + mPoint1Distance);
                p1.y = (int) bubbleRect.bottom;
                p2.x = (int) (bubbleRect.left + mPoint1Distance + mPoint2Distance);
                p2.y = (int) bubbleRect.bottom;
                if (orientation == Orientation.LEFT) {
                    p3.x = (int) (bubbleRect.left + mPoint1Distance - mPoint3Distance / 2);
                    p3.y = (int) bottomRight.y  - convertDpToPixel(10);
                } else {
                    p3.x = (int) (bubbleRect.left + mPoint1Distance + mPoint3Distance);
                    p3.y = (int) bottomRight.y  - convertDpToPixel(10);
                }
                path.reset();
                if (arrowType == ArrowType.SMALL) {
                    if (orientation == Orientation.LEFT){
                        p3.x = p1.x;
                        p3.y = p3.y - convertDpToPixel(5);
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p2.x - convertDpToPixel(5), p2.y - convertDpToPixel(2), p3.x + convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p2.x - convertDpToPixel(2), p3.y, p2.x - convertDpToPixel(2), p2.y, p2.x, p2.y);
                    } else {
                        p3.x = p2.x;
                        p3.y = p3.y - convertDpToPixel(5);
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p1.x + convertDpToPixel(5), p1.y - convertDpToPixel(2), p3.x - convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p1.x + convertDpToPixel(2), p3.y, p1.x + convertDpToPixel(2), p2.y, p1.x, p1.y);
                    }
                } else {
                    if (orientation == Orientation.LEFT) {
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p1.x, p1.y + convertDpToPixel(10), p3.x + mPadding / 4, p1.y + mPadding, p3.x, p3.y);
                        path.cubicTo(p1.x, p3.y, p2.x, p2.y + mPadding / 2, p2.x, p2.y);
                    } else {
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p2.x, p2.y + convertDpToPixel(10), p3.x + mPadding / 4, p1.y + mPadding, p3.x, p3.y);
                        path.cubicTo(p2.x, p3.y, p1.x, p1.y + mPadding / 2, p1.x, p1.y);
                    }
                }
                path.close();
                canvas.drawPath(path, mPaint);
                break;

            case BOTTOM_RIGHT:
                bubbleRect.bottom -= mPadding;
                p1.x = (int) (bubbleRect.right - mPoint1Distance);
                p1.y = (int) bubbleRect.bottom;
                p2.x = (int) (bubbleRect.right - mPoint1Distance + mPoint2Distance);
                p2.y = (int) bubbleRect.bottom;
                if (orientation == Orientation.LEFT) {
                    p3.x = (int) (bubbleRect.right - mPoint1Distance - mPoint3Distance / 2);
                    p3.y = bottomRight.y - convertDpToPixel(10);
                } else {
                    p3.x = (int) (bubbleRect.right - mPoint1Distance + mPoint3Distance);
                    p3.y = bottomRight.y - convertDpToPixel(10);
                }
                path.reset();
                if (arrowType == ArrowType.SMALL) {
                    if (orientation == Orientation.LEFT){
                        p3.x = p1.x;
                        p3.y = p3.y - convertDpToPixel(5);
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p2.x - convertDpToPixel(5), p2.y - convertDpToPixel(2), p3.x + convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p2.x - convertDpToPixel(2), p3.y, p2.x - convertDpToPixel(2), p2.y, p2.x, p2.y);
                    } else {
                        p3.x = p2.x;
                        p3.y = p3.y - convertDpToPixel(5);
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p1.x + convertDpToPixel(5), p1.y - convertDpToPixel(2), p3.x - convertDpToPixel(3), p3.y, p3.x, p3.y);
                        path.cubicTo(p1.x + convertDpToPixel(2), p3.y, p1.x + convertDpToPixel(2), p2.y, p1.x, p1.y);
                    }
                } else {
                    if (orientation == Orientation.LEFT) {
                        path.moveTo(p1.x, p1.y);
                        path.cubicTo(p1.x, p1.y + convertDpToPixel(10), p3.x + mPadding / 4, p1.y + mPadding, p3.x, p3.y);
                        path.cubicTo(p1.x, p3.y, p2.x, p2.y + mPadding / 2, p2.x, p2.y);
                    } else {
                        path.moveTo(p2.x, p2.y);
                        path.cubicTo(p2.x, p2.y + convertDpToPixel(10), p3.x + mPadding / 4, p1.y + mPadding, p3.x, p3.y);
                        path.cubicTo(p2.x, p3.y, p1.x, p1.y + mPadding / 2, p1.x, p1.y);
                    }
                }
                path.close();
                canvas.drawPath(path, mPaint);
                break;
        }
        canvas.drawRoundRect(bubbleRect, mRadius, mRadius, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.sizeWidth = w;
        this.sizeHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    private int convertDpToPixel(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private enum Position {
        TOP_LEFT(0),
        TOP_RIGHT(1),
        BOTTOM_LEFT(2),
        BOTTOM_RIGHT(3);

        private static Position DEFAULT = BOTTOM_RIGHT;
        private int mIntValue;

        Position(int value) {
            this.mIntValue = value;
        }

        @NonNull
        static Position fromInt(int enumValue) {
            for (Position style : values()) {
                if (style.getValue() == enumValue) {
                    return style;
                }
            }
            return DEFAULT;
        }
        private int getValue() {
            return mIntValue;
        }
    }

    private enum Orientation {
        LEFT(0),
        RIGHT(1);

        private static Orientation DEFAULT = LEFT;

        private int mIntValue;

        Orientation(int value) {
            this.mIntValue = value;
        }

        @NonNull
        static Orientation fromInt(int enumValue) {
            for (Orientation style : values()) {
                if (style.getValue() == enumValue) {
                    return style;
                }
            }
            return DEFAULT;
        }
        private int getValue() {
            return mIntValue;
        }
    }

    private enum ArrowType {
        NORMAL(0),
        SMALL(1);

        private static ArrowType DEFAULT = NORMAL;

        private int mIntValue;

        ArrowType(int value) {
            this.mIntValue = value;
        }

        @NonNull
        static ArrowType fromInt(int enumValue) {
            for (ArrowType style : values()) {
                if (style.getValue() == enumValue) {
                    return style;
                }
            }
            return DEFAULT;
        }
        private int getValue() {
            return mIntValue;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

}