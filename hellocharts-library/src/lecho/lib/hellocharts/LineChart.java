package lecho.lib.hellocharts;

import lehco.lib.hellocharts.model.LineChartData;
import lehco.lib.hellocharts.model.LineSeries;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;

public class LineChart extends View {
	protected LineChartData mData;
	protected Paint mPaint = new Paint();
	protected float minXValue = Float.MAX_VALUE;
	protected float maxXValue = Float.MIN_VALUE;
	protected float minYValue = Float.MAX_VALUE;
	protected float maxYValue = Float.MIN_VALUE;
	protected Path mPath = new Path();

	public LineChart(Context context) {
		super(context);
	}

	public LineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mBitmap);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		float availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		float availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		float xMultiplier = availableWidth / (maxXValue - minXValue);
		float yMultiplier = availableHeight / (maxYValue - minYValue);

		for (LineSeries lineSeries : mData.series) {
			int valueIndex = 0;
			for (Float valueX : mData.domain) {
				if (valueIndex == 0) {
					mPath.moveTo(getPaddingLeft() + (valueX - minXValue) * xMultiplier, getHeight()
							- getPaddingBottom() - (lineSeries.values.get(valueIndex) - minYValue) * yMultiplier);
				} else {
					mPath.lineTo((valueX - minXValue) * xMultiplier, getHeight() - getPaddingBottom()
							- (lineSeries.values.get(valueIndex) - minYValue) * yMultiplier);
				}
				++valueIndex;
			}
		}
		mCanvas.drawPath(mPath, mPaint);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		mPath.reset();
	}

	public void setData(LineChartData data) {
		mData = data;
		calculateRanges();
		postInvalidate();
	}

	private void calculateRanges() {
		for (Float value : mData.domain) {
			if (value < minXValue) {
				minXValue = value;
			} else if (value > maxXValue) {
				maxXValue = value;
			}
		}
		for (LineSeries lineSeries : mData.series) {
			for (Float value : lineSeries.values) {
				if (value < minYValue) {
					minYValue = value;
				} else if (value > maxYValue) {
					maxYValue = value;
				}
			}
		}
	}

}
