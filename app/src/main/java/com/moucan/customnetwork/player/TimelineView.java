package com.moucan.customnetwork.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.Scroller;

import com.cmcc.base.log.HLog;
import com.cmcc.base.utils.DateTimeHelper;
import com.cmcc.base.utils.HMTimeUtils;
import com.cmcc.base.utils.ResUtils;
import com.cmcc.iot.widgets.ImageLoader;
import com.cmcc.xiaomu.play_common.Constants;
import com.cmcc.xiaomu.play_common.bean.ICameraEventInfo;
import com.cmcc.xiaomu.play_common.bean.ICameraSectionInfo;
import com.cmcc.xiaomu.play_common.bean.ITimelineEventInfo;
import com.cmcc.xiaomu.play_common.bean.ITimelineSectionInfo;
import com.cmcc.xiaomu.play_common.util.SystemUtils;
import com.cmcc.xiaomu.play_common.camera.ICamera;
import com.cmcc.xiaomu.play_common.util.BitmapUtils;
import com.cmcc.xiaomu.play_common.util.DateUtil;
import com.cmcc.xiaomu.play_ui.PlayerUIIniter;
import com.cmcc.xiaomu.play_ui.R;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

public class TimelineView extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mSliderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long mCenterTime = System.currentTimeMillis();
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");

    private final static int mSecondsPerHours = 60 * 60;
    private final static int mSecondsPerMinutes = 60;
    private final String mDurationFormat = new String("%02d:%02d:%02d");

    private final Rect mTextRect = new Rect();
    //可见时间轴表示的总时长（毫秒）
    private long mTotalMillis = 30 * 60 * 1000; //30分钟
    private long mCellMillis = 10 * 60 * 1000; //一个刻度表示的总时长
    private int mCellWidth = 1;//单位刻度在时间轴上的长度（像素）
    //水平横线y坐标
    private int mHorizontalLineY = 0;

    IStaffData mStaffData = new StaffDataManager();
    ICamera mCameraInfo;
    ITimelineViewData mTimelineData = new DataTest();
    OnScrollListener mScrollListener = null;

    private int AUTO_SCROLL_STEP = 6;
    private final static float VELOCITY_THRESHOLD = 200f;
    private static final int AUTO_SCROLL_TIME = 3000;
    private int MIN_FLING_VELOCITY = 75;
    private static int MAX_FLING_VELOCITY = 6000;
    private float VELOCITY_DEFAULT = 1000f;

    private static final long THUMB_INTERVAL = Constants.ONE_MINUTE_MILLISECONDS * 15;

    private float mLastSliderTouchX;
    private final Scroller mScroller = new Scroller(getContext());

    private boolean mCanDrawThumb = false;

    GestureDetector mScrollDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            long disTime = (long) (distanceX * mCellMillis / mCellWidth);
            doScrollTo(disTime);
            postInvalidate();
            if (mInClip) {
                onClipTimeChanged();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > VELOCITY_THRESHOLD) {
                mHandler.removeCallbacks(mScrollCompleteMsg);
                final int delta = (int) (getWidth() * (velocityX - MIN_FLING_VELOCITY) / (VELOCITY_DEFAULT));
                mScroller.startScroll(getScrollX(), 0, delta, 0, AUTO_SCROLL_TIME);
            }
            return true;
        }
    });

    private Handler mHandler;
    private final Runnable mScrollCompleteMsg = new Runnable() {
        @Override
        public void run() {
            if (mScroller != null && mScroller.isFinished()) {
                mScrollListener.onScrollStateChanged(TimelineView.this, OnScrollListener.SCROLL_STATE_COMPLETE);
            }
        }
    };

    private Bitmap mLeftSliderBitmap;
    private Bitmap mRightSliderBitmap;
    private Bitmap mDefalutThumb;
    private int mLeftSliderX;
    private int mRightSliderX;
    private long mLeftSliderTime;
    private long mRightSliderTime;
    private final long mMaxClipTime = 10 * 60 * 1000;
    private final long mMinClipTime = 10 * 1000;
    private final Rect mLeftSliderRect = new Rect();
    private final Rect mRightSliderRect = new Rect();
    private final Rect mLeftSliderTouchRect = new Rect();
    private final Rect mRightSliderTouchRect = new Rect();
    private final Rect mEventTextTimeRect = new Rect();
    private final Rect mEventTimeRect = new Rect();
    private boolean mInClip = false;
    private int mSliderTouchMode = 0; //-1-滑动左滑块，0没有滑动滑块，1-滑动右滑块

    private int mLeftSliderResId = R.drawable.camera_iot_video_clip_drag_icon_left;
    private int mRightSliderResId = R.drawable.camera_iot_video_clip_drag_icon_right;
    int mTextSize = (int) getResources().getDimension(R.dimen.wdp68);
    int mTextColor = Color.WHITE;
    int mHorizontalLineColor = Color.WHITE;
    int mCenterLineColor = Color.WHITE;
    int mTimeLineColor = (0xff737373);//时间刻度画线颜色
    int mSectionColor = 0xffb0b0b0;
    int mSectionHeight = ResUtils.getDimen(R.dimen.wdp5);
    int mEventHeight = ResUtils.getDimen(R.dimen.wdp20);
    int mHighLightEventHeight = ResUtils.getDimen(R.dimen.wdp60);
    int mHorizontalLineHeight = 2;
    int mCenterLineWidth = 2;
    int mTimelineWidth = 2;
    int mTimelineHeight = 22;
    float mTimelinePosPercent = 0.5f;

    public TimelineView(Context context) {
        super(context);
        init(context, null);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setCanDrawThumb(boolean canDrawThumb) {
        mCanDrawThumb = canDrawThumb;
    }

    private void init(Context context, AttributeSet attrs) {
        mHandler = PlayerUIIniter.getMainThreadHandler();

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimelineView);
            mLeftSliderResId = typedArray.getResourceId(R.styleable.TimelineView_leftSlider, mLeftSliderResId);
            mRightSliderResId = typedArray.getResourceId(R.styleable.TimelineView_rightSlider, mRightSliderResId);
            mTextSize = (int) typedArray.getDimension(R.styleable.TimelineView_timelineTextSize, getResources().getDimension(R.dimen.wdp18));
            mTextColor = typedArray.getColor(R.styleable.TimelineView_timelineTextColor, Color.WHITE);
            mHorizontalLineColor = typedArray.getColor(R.styleable.TimelineView_horizontalLineColor, Color.WHITE);
            mCenterLineColor = typedArray.getColor(R.styleable.TimelineView_centerLineColor, Color.WHITE);
            mTimeLineColor = typedArray.getColor(R.styleable.TimelineView_timeLineColor, Color.WHITE);
            mSectionColor = typedArray.getColor(R.styleable.TimelineView_sectionColor, Color.GRAY);
            mSectionHeight = (int) typedArray.getDimension(R.styleable.TimelineView_sectionHeight, mSectionHeight);
            mEventHeight = (int) typedArray.getDimension(R.styleable.TimelineView_eventHeight, mEventHeight);
            mHighLightEventHeight = (int) typedArray.getDimension(R.styleable.TimelineView_highLightEventHeight, mHighLightEventHeight);
            mHorizontalLineHeight = (int) typedArray.getDimension(R.styleable.TimelineView_horizontalLineHeight, mHorizontalLineHeight);
            mCenterLineWidth = (int) typedArray.getDimension(R.styleable.TimelineView_centerLineWidth, mCenterLineWidth);
            mTimelineWidth = (int) typedArray.getDimension(R.styleable.TimelineView_timelineWidth, mTimelineWidth);
            mTimelineHeight = (int) typedArray.getDimension(R.styleable.TimelineView_timelineHeight, mTimelineHeight);
            mTimelinePosPercent = typedArray.getFloat(R.styleable.TimelineView_timelinePosPercent, mTimelinePosPercent);
            typedArray.recycle();
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.MIN_FLING_VELOCITY = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.MAX_FLING_VELOCITY = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.VELOCITY_DEFAULT = VELOCITY_DEFAULT * MAX_FLING_VELOCITY / 6000;// * (SystemUtils.isPhone(context) ? 1 : 2);
        this.AUTO_SCROLL_STEP = (int) (this.AUTO_SCROLL_STEP * displayMetrics.scaledDensity);

        initClipBitmap();
    }

    private void initClipBitmap() {

        if (mLeftSliderBitmap == null || mLeftSliderBitmap.isRecycled()) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = false;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;

            // 得到新的图片
            mLeftSliderBitmap = BitmapFactory.decodeResource(getResources(), mLeftSliderResId, newOpts);
            mRightSliderBitmap = BitmapFactory.decodeResource(getResources(), mRightSliderResId, newOpts);
        }

    }

    public void init(ICamera cameraInfo, ITimelineViewData timelineData, OnScrollListener onScrollListener) {
        mTimelineData = timelineData;
        mScrollListener = onScrollListener;
        mCameraInfo = cameraInfo;
    }

    public void scrollTo(long time) {
        scrollTo(time, false);
    }

    public void scrollTo(long time, boolean needRefreshClip) {
        mCenterTime = time;
        if (needRefreshClip) {
            computeSliderPosition();
        }
        postInvalidate();
    }

    private void doScrollTo(long disTime) {
        long tempTime = mCenterTime + disTime;
        if (tempTime >= System.currentTimeMillis()) {
            mCenterTime = System.currentTimeMillis();
            mScrollListener.onScrollToEnd(true);
            if (mInClip) {
                long duration = Math.abs((mRightSliderTime - mLeftSliderTime) / 2);
                mLeftSliderTime = mCenterTime - duration;
                mRightSliderTime = mCenterTime + duration;
            }
        } else {
            mCenterTime = tempTime;
            mScrollListener.onScroll(TimelineView.this, mCenterTime);
            if (mInClip) {
                mLeftSliderTime += disTime;
                mRightSliderTime += disTime;
            }
        }

    }

    public void computeSliderPosition() {
        if (mInClip) {
            long duration = Math.abs((mRightSliderTime - mLeftSliderTime) / 2);
            mLeftSliderTime = mCenterTime - duration;
            mRightSliderTime = mCenterTime + duration;
            onClipTimeChanged();
        }
//        postInvalidate();
    }

    public long getCenterTime() {
        return mCenterTime;
    }

    private long mOrignalAnimationTime = 0;
    private long mAnimationTargetTime = 90 * 1000;

    public void enterClip(long startTime, long endTime) {
        mLeftSliderTime = startTime;
        mRightSliderTime = endTime;
        mInClip = true;
        postInvalidate();
        mOrignalAnimationTime = mTotalMillis;
        if (Math.abs(mTotalMillis - mAnimationTargetTime) < 15 * 1000) {
            applyAnimationZoom(mTotalMillis, mTotalMillis * 2);
        } else {
            applyAnimationZoom(mTotalMillis, mAnimationTargetTime);
        }
    }

    public void exitClip() {
        mInClip = false;
        postInvalidate();
        applyAnimationZoom(mTotalMillis, mOrignalAnimationTime);
    }

    public boolean inClip() {
        return mInClip;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHorizontalLineY = h - (int) (h * mTimelinePosPercent);
        mCellWidth = (int) (w / (mTotalMillis / (float) mCellMillis));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mScrollCompleteMsg);
    }

    public long getShowTotalMillis() {
        return mTotalMillis;
    }

    public void applyAnimationZoom(final long startTotalMillis, final long targetTotalMillis) {

        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                long newTotalMillis = startTotalMillis + (long) ((targetTotalMillis - startTotalMillis) * interpolatedTime);
                mTotalMillis = newTotalMillis;
                mCellMillis = mStaffData.getCellMillis(mTotalMillis);
                mCellWidth = (int) (getWidth() / (mTotalMillis / (float) mCellMillis));
                postInvalidate();
            }
        };
        animation.setDuration(800);
        animation.setInterpolator(new DecelerateInterpolator());
        startAnimation(animation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastDis = 0;
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mLeftSliderX = getXByTime(mLeftSliderTime);
            mRightSliderX = getXByTime(mRightSliderTime);
        }
        if (event.getPointerCount() == 2) {
            handleScaleTouchEvent(event);
        } else if (mInClip) {
            if (!handleSliderTouchEvent(event)) {
                handleScrollTouchEvent(event);
            }
        } else {
            handleScrollTouchEvent(event);
        }
        return true;
    }

    /**
     * @return false 触摸事件不在滑块上，本方法不处理该事件
     */
    private boolean handleSliderTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastSliderTouchX = event.getX();
            if (mLeftSliderTouchRect.contains(touchX, touchY)) {
                mSliderTouchMode = -1;
            } else if (mRightSliderTouchRect.contains(touchX, touchY)) {
                mSliderTouchMode = 1;
            } else {
                mSliderTouchMode = 0;
            }
        }

        if (mSliderTouchMode != 0) {
            int disX = (int) (event.getX() - mLastSliderTouchX);
            mLastSliderTouchX = event.getX();
            long tmpTime = 0;
            if (mSliderTouchMode == -1) {
                tmpTime = mLeftSliderTime + getDisTimeByX(disX);
                if (tmpTime < mRightSliderTime && (mRightSliderTime - tmpTime) >= mMinClipTime && (mRightSliderTime - tmpTime) <= mMaxClipTime) {
                    int tmpX = getXByTime(tmpTime);
                    if (tmpX >= 1 / 8f * getWidth()) {
                        mLeftSliderTime = tmpTime;
                        onClipTimeChanged();
                    }
                }
            } else if (mSliderTouchMode == 1) {
                tmpTime = mRightSliderTime + getDisTimeByX(disX);
                if (tmpTime > mLeftSliderTime && (tmpTime - mLeftSliderTime) >= mMinClipTime && (tmpTime - mLeftSliderTime) <= mMaxClipTime) {
                    int tmpX = getXByTime(tmpTime);
                    if (tmpX <= 7 / 8f * getWidth()) {
                        mRightSliderTime = tmpTime;
                        onClipTimeChanged();
                    }
                }
            }
            postInvalidate();
        }

        return mSliderTouchMode != 0;
    }

    private void onClipTimeChanged() {
        mScrollListener.onSelectTimeChanged(new ClipDragInfo(mLeftSliderTime, mRightSliderTime - mLeftSliderTime, 0, 0, true));
    }

    //处理滑动事件
    private void handleScrollTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mScrollListener.onScrollStateChanged(this, OnScrollListener.SCROLL_STATE_START);
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mHandler.postDelayed(mScrollCompleteMsg, 20);
        }
        mScrollDetector.onTouchEvent(event);
    }

    float mLastDis;

    //处理缩放事件
    private void handleScaleTouchEvent(MotionEvent event) {
        float dis = distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        if (mLastDis > 0) {
            float scale = dis / mLastDis;
            if (canScale(scale)) {
                computeScale((long) (mTotalMillis / scale), false);
            }
        }
        mLastDis = dis;
    }

    private void computeScale(long newTotalMillis, boolean animationSlider) {
        mTotalMillis = newTotalMillis;
        mCellMillis = mStaffData.getCellMillis(mTotalMillis);
        mCellWidth = (int) (getWidth() / (mTotalMillis / (float) mCellMillis));
        if (mInClip && !animationSlider) {
            mLeftSliderTime = getTimeByX(mLeftSliderX);
            mRightSliderTime = getTimeByX(mRightSliderX);
        }
        postInvalidate();
        if (mInClip) {
            onClipTimeChanged();
        }
    }

    private boolean canScale(float scale) {
        if (mInClip) {
            if (scale < 1.0f && mRightSliderTime - mLeftSliderTime >= mMaxClipTime) {
                if (mRightSliderTime - mLeftSliderTime > mMaxClipTime) {
                    mRightSliderTime = mLeftSliderTime + mMaxClipTime;
                    onClipTimeChanged();
                    postInvalidate();
                }
                return false;
            } else if (scale > 1.0f && mRightSliderTime - mLeftSliderTime <= mMinClipTime) {
                return false;
            }
        }
        if (scale < 1.0f && mTotalMillis < mStaffData.getMaxTotalMillis()) {
            return true;
        } else if (scale > 1.0f && mTotalMillis > mStaffData.getMinTotalMillis()) {
            return true;
        }
        return false;
    }

    public boolean isScrolling = false;
    private boolean scrollToEnd = false;

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (Math.abs(mScroller.getCurrX() - mScroller.getFinalX()) > 1.1) {
                int totalX = mScroller.getFinalX() - mScroller.getStartX();
                float swipe = (totalX - mScroller.getCurrX()) / AUTO_SCROLL_STEP;
                long disTime = -(long) (swipe) * mCellMillis / mCellWidth;
                if (mCenterTime + disTime > HMTimeUtils.getCurrentTime() && !mScroller.isFinished()) {
                    isScrolling = false;
                    scrollToEnd = true;
                } else {
//                    scrollToEnd = false;
                    isScrolling = true;
                    doScrollTo(disTime);
                }
                isScrolling = true;
            } else if (isScrolling) {
                isScrolling = false;
                if (!scrollToEnd) {
                    mScrollListener.onScrollStateChanged(this, OnScrollListener.SCROLL_STATE_COMPLETE);
                }
            }
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#333333"));
        mPaint.setStrokeWidth(mCenterLineWidth);
        //水平横线
        canvas.drawLine(0, mHorizontalLineY, getWidth(), mHorizontalLineY, mPaint);

        drawTimes(canvas);
        drawSections(canvas);
        drawEvents(canvas);
        drawSliders(canvas);
        drawThumbs(canvas);
        drawCutDuration(canvas);

        mPaint.setColor(mCenterLineColor);
        mPaint.setStrokeWidth(mCenterLineWidth);
        //中间竖线
        if (!inClip()) {
            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaint);
            mPaint.setColor(mHorizontalLineColor);
            mPaint.setStrokeWidth(mHorizontalLineHeight);
        }
    }


    //时间标尺
    private void drawTimes(Canvas canvas) {
        long drawStartTime = mCenterTime - mTotalMillis / 2 - mCellMillis;
        drawStartTime = drawStartTime / mCellMillis * mCellMillis; //以刻度单位时间取整
        int startX = getXByTime(drawStartTime);

        int smallScaleWidth = mCellWidth / 5;
        while (startX < getWidth()) {
            mPaint.setStrokeWidth(mTimelineWidth);
            mPaint.setColor(mTimeLineColor);
            canvas.drawLine(startX, mHorizontalLineY, startX, mHorizontalLineY + mTimelineHeight, mPaint);
            for (int i = 1; i < 5; ++i) {
                int tempX = startX + i * smallScaleWidth;
                canvas.drawLine(tempX, mHorizontalLineY, tempX, mHorizontalLineY + mTimelineHeight / 2, mPaint);
            }
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);
            String drawText = getTimeText(drawStartTime);
            float f3 = mPaint.measureText(drawText);
            canvas.drawText(getTimeText(drawStartTime), startX - f3 / 2, mHorizontalLineY + mTimelineHeight + (mTimelineHeight >> 1), mPaint);
            startX += mCellWidth;
            drawStartTime += mCellMillis;
        }
    }

    private void drawEvents(Canvas canvas) {
        List<ICameraEventInfo> eventInfoList = mTimelineData.getEventInfoByTime(getTimeByX(0), getTimeByX(getWidth()));
        if (eventInfoList == null) {
            return;
        }
        mPaint.setColor(Color.RED);
        ITimelineEventInfo highLightEventInfo = null;
        int highLightStartX = 0;
        int highLightEndX = 0;
        int viewWidth = getWidth();
        for (ITimelineEventInfo eventInfo : eventInfoList) {
            long startTime = eventInfo.getStartTime();
            long endTime = eventInfo.getEndTime();
            int startX = getXByTime(startTime);
            int endX = getXByTime(endTime);
            if (endX < 0 || startX > viewWidth) {
                continue;
            }
            if (mCenterTime <= endTime && mCenterTime >= startTime) {
                highLightEventInfo = eventInfo;
                highLightStartX = startX;
                highLightEndX = endX;
                mPaint.setColor(eventInfo.getEventColor());
                canvas.drawRect(startX, mHorizontalLineY - mEventHeight, endX, mHorizontalLineY, mPaint);
            } else {
                mPaint.setColor(eventInfo.getEventColor());
                canvas.drawRect(startX, mHorizontalLineY - mEventHeight, endX, mHorizontalLineY, mPaint);
            }
        }
        if (highLightEventInfo != null) {
            mPaint.setColor(highLightEventInfo.getHighLightColor());
            canvas.drawRect(highLightStartX, mHorizontalLineY - mHighLightEventHeight, highLightEndX, mHorizontalLineY, mPaint);
        }
    }

    private void drawSections(Canvas canvas) {
        List<ICameraSectionInfo> sectionInfoList = mTimelineData.getSectionInfoByTime(getTimeByX(0), getTimeByX(getWidth()));
        if (sectionInfoList == null) {
            return;
        }
        mPaint.setColor(mSectionColor);
        int viewWidth = getWidth();
        for (ITimelineSectionInfo sectionInfo : sectionInfoList) {
            int startX = getXByTime(sectionInfo.getStartTime());
            int endX = getXByTime(sectionInfo.getEndTime());
            if (endX < 0 || startX > viewWidth) {
                continue;
            }
            canvas.drawRect(startX, mHorizontalLineY - mSectionHeight, endX, mHorizontalLineY, mPaint);
        }
    }

    private void drawSliders(Canvas canvas) {
        if (mInClip) {
            int leftSliderEndX = getXByTime(mLeftSliderTime);
            int leftSliderStartX = leftSliderEndX - mLeftSliderBitmap.getWidth();
            mLeftSliderRect.set(leftSliderStartX, 0, leftSliderEndX, getHeight());
            mLeftSliderTouchRect.set(leftSliderStartX, getHeight() * 3 / 4, leftSliderEndX, getHeight());

            int rightSliderStartX = getXByTime(mRightSliderTime);
            int rightSliderEndX = rightSliderStartX + mRightSliderBitmap.getWidth();
            mRightSliderRect.set(rightSliderStartX, 0, rightSliderEndX, getHeight());
            mRightSliderTouchRect.set(rightSliderStartX, getHeight() * 3 / 4, rightSliderEndX, getHeight());

            canvas.drawBitmap(mLeftSliderBitmap, null, mLeftSliderRect, mSliderPaint);
            canvas.drawBitmap(mRightSliderBitmap, null, mRightSliderRect, mSliderPaint);
            mPaint.setColor(0x33ffffff);
            canvas.drawRect(mLeftSliderRect.right, 0, mRightSliderRect.left, getHeight(), mPaint);
        }
    }

    private void drawThumbs(Canvas canvas) {
        if (mCanDrawThumb) {
            long startTime = getTimeByX(0) - THUMB_INTERVAL;
            long endTime = getTimeByX(getWidth());
            if (mDefalutThumb == null) {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                mDefalutThumb = BitmapFactory.decodeResource(getResources(), R.drawable.home_page_icon_loading, newOpts);
            }
            List<ICameraEventInfo> cameraEventInfoList = mTimelineData.getEventInfoByTime(startTime, endTime);
            if (cameraEventInfoList == null || cameraEventInfoList.size() == 0) {
                return;
            }
            while (startTime <= endTime) {
                final ICameraEventInfo nextEventInfoByTime = mTimelineData.findNextEventByTime(startTime);
                if (nextEventInfoByTime == null || nextEventInfoByTime.getStartTime() > endTime || startTime + THUMB_INTERVAL < nextEventInfoByTime.getStartTime()) {
                    HLog.d(VIEW_LOG_TAG, nextEventInfoByTime == null ? "null" : " not null" + "   nextEventInfoByTime.getStartTime()= " + "   endTime = " + endTime);
                } else {
                    Rect thumbRect = new Rect();
                    thumbRect.top = 0;
                    thumbRect.bottom = getContext().getResources().getDimensionPixelOffset(R.dimen.wdp100);
                    //如果是前半段，直接画
                    if (startTime + THUMB_INTERVAL < nextEventInfoByTime.getStartTime()) {
                        startTime = startTime + THUMB_INTERVAL;
                        continue;
                    }
                    if (startTime + THUMB_INTERVAL / 2 < nextEventInfoByTime.getStartTime()) {
                        //处于第二段
                        //找到下一个事件
                        ICameraEventInfo iCameraEventInfo = mTimelineData.findNextEventByTime(startTime + THUMB_INTERVAL);
                        //等于null 去画事件
                        if (iCameraEventInfo != null) {
                            long l = iCameraEventInfo.getStartTime() - nextEventInfoByTime.getStartTime();
                            //判断两个事件的间隔是否是安全
                            int mthumbWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.wdp100) * 16 / 9;
                            int distance = Math.abs(getDisXByTime(l));
                            if (distance < mthumbWidth) {
                                // 两个事件的间隔小于安全距离，则继续下一个事件
                                startTime = startTime + THUMB_INTERVAL;
                                continue;
                            }
                        }
                    }
                    long startTime1 = nextEventInfoByTime.getStartTime();
                    double recordStart = getXByTime(startTime1);
                    thumbRect.left = (int) recordStart;
                    thumbRect.right = (int) recordStart + getContext().getResources().getDimensionPixelOffset(R.dimen.wdp100) * 16 / 9;
                    Drawable drawable = TimelineThumbManager.INSTANCE.getDrawable(nextEventInfoByTime.getEventThumbnailUrl(mCameraInfo));
                    if (drawable == null) {
                        ImageLoader.INSTANCE.downLoadThumb(getContext(), nextEventInfoByTime.getEventThumbnailUrl(mCameraInfo), new ImageLoader.ILoadResourceListener() {
                            @Override
                            public void onResourceReady(Drawable drawable) {
                                if (drawable != null) {
                                    TimelineThumbManager.INSTANCE.addThumb(nextEventInfoByTime.getEventThumbnailUrl(mCameraInfo), drawable);
                                }
                            }
                        });
                        canvas.drawBitmap(mDefalutThumb, null, thumbRect, null);
                    } else {
                        if (drawable instanceof BitmapDrawable) {
                            HLog.d(VIEW_LOG_TAG, nextEventInfoByTime.getStartTime() + "");
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            if (bitmap != null) canvas.drawBitmap(bitmap, null, thumbRect, null);
                        } else {
                            Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable);
                            if (bitmap != null) canvas.drawBitmap(bitmap, null, thumbRect, null);
                            bitmap.recycle();
                        }
                    }
//                    mThumbTimePointList.put(nextEventInfoByTime.getStartTime(), thumbRect);
                    //画时间搓
                    if (mPaint == null) {
                        mPaint = new Paint();
                        mPaint.setColor(Color.WHITE);
                        mPaint.setStrokeWidth(ResUtils.getDimen(R.dimen.wdp5));
                        mPaint.setTextSize(ResUtils.getDimen(R.dimen.wdp20));
                    }
                    String dateString = DateUtil.getDateString(nextEventInfoByTime.getStartTime(), DateUtil.HOUR_MINUTE);
                    String duration = DateTimeHelper.eventsSecondToMS((int) (nextEventInfoByTime.getDuration() / 1000));
                    //   String duration = DateTimeHelper.SecondToHM((nextEventInfoByTime.getDuration() == null ? 0 : (nextEventInfoByTime.getDuration().intValue())));
                    mPaint.getTextBounds(duration, 0, duration.length(), mEventTextTimeRect);
                    mEventTimeRect.bottom = mEventTextTimeRect.height();
                    mEventTimeRect.left = thumbRect.right - mEventTextTimeRect.width();
                    canvas.drawText(duration, mEventTimeRect.left, mEventTimeRect.bottom, mPaint);

                    mPaint.getTextBounds(duration, 0, duration.length(), mEventTextTimeRect);
                    mEventTimeRect.bottom = thumbRect.bottom;
                    mEventTimeRect.left = thumbRect.left;
                    int dimen = ResUtils.getDimen(R.dimen.wdp10);
                    canvas.drawText(dateString, mEventTimeRect.left + dimen, mEventTimeRect.bottom - dimen, mPaint);
                    long l = startTime + (endTime - startTime) / 2;
                    if (l >= nextEventInfoByTime.getStartTime() && l <= nextEventInfoByTime.getEndTime()) {
                        //选中状态
               /*         RectF rectF = new RectF();
                        rectF.left = mEventTimeRect.left+dimen/2;
                        rectF.bottom = mEventTimeRect.bottom-dimen/2;
                        rectF.right = (float) (rectF.left+mEventTextTimeRect.width()+dimen*1.5);
                        rectF.top = (float) (rectF.bottom-dimen*1.5-mEventTextTimeRect.height());*/
                        if (mPaint == null) {
                            mPaint = new Paint();
                            mPaint.setColor(getResources().getColor(R.color.color_dominant));
                            mPaint.setStyle(Paint.Style.STROKE);
                            mPaint.setStrokeWidth(ResUtils.getDimen(R.dimen.wdp4));
                        }
                        thumbRect.top = thumbRect.top + ResUtils.getDimen(R.dimen.wdp2);
                        thumbRect.bottom = thumbRect.bottom - ResUtils.getDimen(R.dimen.wdp2);
                        canvas.drawRect(thumbRect, mPaint);
                    }
                }
            }
        }
    }

    private void drawCutDuration(Canvas canvas) {
        if (mInClip) {
            RectF rectF = new RectF(getWidth() / 2 - ResUtils.getDimen(R.dimen.wdp60), ResUtils.getDimen(R.dimen.wdp27), getWidth() / 2 + ResUtils.getDimen(R.dimen.wdp60), ResUtils.getDimen(R.dimen.wdp67));
            mPaint.setColor(ResUtils.getColor(R.color.a3));
            canvas.drawRoundRect(rectF, ResUtils.getDimen(R.dimen.wdp20), ResUtils.getDimen(R.dimen.wdp20), mPaint);
            mPaint.setColor(ResUtils.getColor(R.color.hemu_item_divider));
            mPaint.setTextSize(ResUtils.getDimen(R.dimen.wdp20));
            Rect textRect = new Rect();
            String s = formatDurationTime((int) ((mRightSliderTime - mLeftSliderTime) / 1000));
            mPaint.getTextBounds(s, 0, s.length(), textRect);
            canvas.drawText(s, getWidth() / 2 - textRect.width() / 2, (rectF.top + rectF.bottom) / 2 + ResUtils.getDimen(R.dimen.wdp6), mPaint);
        }
    }

    private int getDisXByTime(long disTime) {
        return (int) (disTime * mCellWidth / mCellMillis);
    }

    private long getDisTimeByX(int disX) {
        return disX * mCellMillis / mCellWidth;
    }

    private int getXByTime(long time) {
        int x = getWidth() / 2;
        long tmp = ((mCenterTime - time) * mCellWidth / mCellMillis);
        return x - (int) tmp;
    }

    private long getTimeByX(float x) {
        if (x < getWidth() / 2) {
            long disTime = (long) ((getWidth() / 2 - x) * mCellMillis / mCellWidth);
            return (long) (mCenterTime - disTime);
        } else {
            long disTime = (long) ((x - getWidth() / 2) * mCellMillis / mCellWidth);
            return (long) (mCenterTime + disTime);
        }
    }

    private String getTimeText(long time) {
        String text = mDateFormat.format(time);
        return text;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    private String formatDurationTime(int durationSeconds) {
        int hours = durationSeconds / mSecondsPerHours;
        int minutes = (durationSeconds % mSecondsPerHours) / mSecondsPerMinutes;
        int seconds = durationSeconds % mSecondsPerMinutes;
        return String.format(mDurationFormat, hours, minutes, seconds);
    }

    public static abstract interface OnScrollListener {
        public static final int SCROLL_STATE_START = 1;
        public static final int SCROLL_STATE_END = 2;
        public static final int SCROLL_STATE_COMPLETE = 3;
        public static final int SCROLL_STATE_SCROLLING = 4;

        /**
         * 实时回调TimelineView滚动到的时间点
         *
         * @param view
         * @param timeInMillis
         */
        public abstract void onScroll(final TimelineView view, final long timeInMillis);

        /**
         * TimelineView滚动状态发生变化时的回调
         *
         * @param timeline
         * @param status
         */
        public abstract void onScrollStateChanged(TimelineView timeline, int status);

        //
        public abstract void onSelectTimeChanged(TimelineView.ClipDragInfo clipDragInfo);

        public abstract void onScrollToEnd(boolean right);
    }


    public static class ClipDragInfo {
        public long startTime;
        public long during;
        public long showTime;
        public int locationX;
        public boolean showTips = false;
        public boolean showDragImage = true;

        ClipDragInfo(long startTime, long during, long showTime, int locationX, boolean showTips) {
            this.startTime = startTime;
            this.during = during;
            this.showTime = showTime;
            this.locationX = locationX;
            this.showTips = showTips;
        }

        ClipDragInfo(long startTime, int during) {
            this.startTime = startTime;
            this.during = during;
        }
    }

    public long getLeftSliderTime() {
        return mLeftSliderTime;
    }

    public long getRightSliderTime() {
        return mRightSliderTime;
    }

    public long getTimelineCurrentDistance() {
        return Math.abs(mTotalMillis);
    }

    public void updateSliderTime(long time) {
        mLeftSliderTime = time;
        postInvalidate();
    }

    public void updateSliderTime(long startTime, long endTime) {
        mLeftSliderTime = startTime;
        mRightSliderTime = endTime;
        postInvalidate();
    }
}

