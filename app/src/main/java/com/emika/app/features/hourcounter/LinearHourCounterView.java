package com.emika.app.features.hourcounter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.emika.app.R;

/**
 * @author Andrey Kudryavtsev on 2019-10-08.
 */
public class LinearHourCounterView extends View {

    public static final double MAX_PROGRESS = 12.0;
    private static final String TAG = "FinanceProgressView";
    private static final float START_ANGLE = 90f;
    private static final int MAX_ANGLE = 360;

    private String mProgress;
    private int mInactiveColor;
    private int mColor;
    private int mTextSize;
    private int mStrokeWidth;

    private Paint mInactiveCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mProgressRect = new RectF();
    private Rect mTextBounds = new Rect();

    public LinearHourCounterView(Context context) {
        super(context);
        init(context, null);
    }

    public LinearHourCounterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfHeight = getHeight();
        int progressEndX = (int) (getWidth()  * Double.parseDouble(mProgress) / MAX_PROGRESS);
//        canvas.translate(mStrokeWidth / 2, mStrokeWidth / 2);
        updateProgressRect();
//        canvas.drawLine(progressEndX, halfHeight, getWidth(), halfHeight, mInactiveCirclePaint);
        canvas.drawLine(0, halfHeight/2, progressEndX - mTextBounds.width() - mTextBounds.width()/3, halfHeight/2, mCirclePaint);

        mProgress = mProgress.replace(',', '.');
//        canvas.drawArc(mProgressRect, START_ANGLE, (float) (Double.parseDouble(mProgress) * MAX_ANGLE / MAX_PROGRESS), false, mCirclePaint);
        drawText(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getTextBounds(formatString(String.valueOf((int) MAX_PROGRESS)));
        int requestedSize = mTextBounds.height();
        final int suggestedMinimumSize = Math.max(getSuggestedMinimumHeight(), getSuggestedMinimumWidth());
        requestedSize = Math.max(suggestedMinimumSize, requestedSize);
        final int resolvedWidth = resolveSize(requestedSize + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        final int resolvedHeight = resolveSize(requestedSize + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        final int resolvedSize = Math.min(resolvedHeight, resolvedWidth);
        setMeasuredDimension(widthMeasureSpec, resolvedHeight);
    }



    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mProgress = (int) Double.parseDouble(mProgress);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mProgress = String.valueOf(savedState.mProgress);
    }


    private void drawText(Canvas canvas) {
        final String progressString = formatString(String.valueOf(mProgress));
        getTextBounds(progressString);

        float x = mProgressRect.width() - mTextBounds.width() - getPaddingRight();
        float y = mTextBounds.height();
//        float x = mProgressRect.width() - mTextBounds.width() - getPaddingRight();
//        float y = mTextBounds.height() - getPaddingTop() - getPaddingBottom();
        canvas.drawText(progressString, x, y, mTextPaint);
    }

    private void updateProgressRect() {
        mProgressRect.left = getPaddingLeft();
        mProgressRect.top = getPaddingTop() + mTextBounds.height() ;
        mProgressRect.right = getWidth() - mStrokeWidth - getPaddingRight();
        mProgressRect.bottom = getHeight() - mStrokeWidth - getPaddingBottom();
    }

    private void getTextBounds(@NonNull String progressString) {
        mTextPaint.getTextBounds(progressString, 0, progressString.length(), mTextBounds);
    }

    private String formatString(String progress) {
        return String.format(getResources().getString(R.string.progress_template), progress);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureInactiveCirclePaint();
        configureCirclePaint();
        configureTextPaint();
    }

    private void configureInactiveCirclePaint() {
        mInactiveCirclePaint.setStrokeWidth(mStrokeWidth);
        mInactiveCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mInactiveCirclePaint.setColor(mInactiveColor);
    }

    private void configureTextPaint() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
    }

    private void configureCirclePaint() {
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(mColor);
    }

    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.hour,
                R.attr.hourCounter, 0);
        try {
            mProgress = String.valueOf(typedArray.getInteger(R.styleable.hour_progress, 0));
            mInactiveColor = typedArray.getColor(R.styleable.hour_inactiveColor,
                    ContextCompat.getColor(getContext(), R.color.colorPrimary));
            mColor = typedArray.getColor(R.styleable.hour_color, ContextCompat.getColor(getContext(), R.color.colorAccent));
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.hour_textSize,
                    getResources().getDimensionPixelSize(R.dimen.defaultTextSize));
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.hour_strokeWidth,
                    getResources().getDimensionPixelSize(R.dimen.defaultStrokeWidth));
//            Log.d(TAG, "Progress = " + mProgress + ", " + "Color = " + mColor + ", textSize = " + mTextSize + ", strokeWidth = " + mStrokeWidth);
        } finally {
            typedArray.recycle();
        }
    }

    public String getProgress() {
        return mProgress;
    }

    public void setProgress(String progress) {
        mProgress = progress;
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    static class SavedState extends BaseSavedState {

        private int mProgress;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
            mProgress = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
        }

        private static final class StateCreator implements Creator<SavedState> {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }
    }
}