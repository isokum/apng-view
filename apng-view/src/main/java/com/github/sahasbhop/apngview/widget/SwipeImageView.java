package com.github.sahasbhop.apngview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.github.sahasbhop.flog.FLog;

import static com.github.sahasbhop.apngview.ApngImageLoader.enableDebugLog;

/**
 * Created by sokum on 15. 9. 22..
 *
 * 비율에 맞게 이미지크기를 조정하기 위해서 사용
 *
 */
public class SwipeImageView extends ImageView {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private PointF mStartPoint = new PointF();

    private float mDesignWidth = 0;
    private float mDesignHeight = 0;
    private float mTouchMargin = 0;

    private int mImageCount = 0;
    private int mLastImageIndex = 1;


    private OnRotateListener mOnImageRotateListener = null;

    public SwipeImageView(Context context) {
        super(context);
    }

    public SwipeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if ( mImageCount != 0 ) {
            mTouchMargin = getMeasuredWidth() / 2 / mImageCount;
        }
    }

    public void setImageCount(int rotateCount) {
        this.mImageCount = rotateCount;

        if ( mImageCount != 0 ) {
            this.mTouchMargin = getMeasuredWidth() / 2 / mImageCount;
        }
    }

    public void setCurrentImageIndex(int imageIndex) {
        this.mLastImageIndex = imageIndex;
    }

    public int getCurrentImageIndex() {
        return mLastImageIndex;
    }

    public int moveNextImageIndex() {

        int currentImageIndex = mLastImageIndex + 1;

        if ( currentImageIndex > mImageCount) {
            currentImageIndex = 1;
        }

        mLastImageIndex = currentImageIndex;

        if (enableDebugLog) FLog.d("auto rotate = " + mLastImageIndex);

        return mLastImageIndex;
    }

    public void setOnImageRotateListener(OnRotateListener listener) {
        this.mOnImageRotateListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        try {
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    mStartPoint.set(ev.getX(), ev.getY());
                    return true;
                }
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    float offsetX = ev.getX() - mStartPoint.x;

                    if ( Math.abs(offsetX) >= mTouchMargin ) {
                        mStartPoint.x = ev.getX();
                        int rotateOffset = (int) Math.round(offsetX / mTouchMargin);
                        int direction = rotateOffset > 0 ? 1 : -1;

                        mLastImageIndex = (mLastImageIndex + rotateOffset);

                        if ( mLastImageIndex <= 0 ) {
                            mLastImageIndex = mImageCount;
                        } else if ( mLastImageIndex > mImageCount) {
                            mLastImageIndex = 1;
                        }

                        if (mOnImageRotateListener != null) {
                            mOnImageRotateListener.onRotate(direction, mLastImageIndex);
                        }

                        if (enableDebugLog) FLog.d("touch distance = " + offsetX + ", offset = " + rotateOffset + ", rotate = " + mLastImageIndex);
                    }

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    mStartPoint.set(0,0);
                    break;
                }
            }
            return super.dispatchTouchEvent(ev);
        } catch (IllegalStateException ex) {
        } catch (IllegalArgumentException ex) {
        }

        return false;
    }

    public static interface OnRotateListener {
        public void onRotate(int direction, int offest);
    }
}
