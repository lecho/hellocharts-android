package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import lecho.lib.hellocharts.formatter.PieChartValueFormatter;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Default renderer for PieChart. PieChart doesn't use viewport concept so it a little different than others chart
 * types.
 */
public class PieChartRenderer extends AbstractChartRenderer {
    private static final float MAX_WIDTH_HEIGHT = 100f;
    private static final int DEFAULT_START_ROTATION = 45;
    private static final float DEFAULT_LABEL_INSIDE_RADIUS_FACTOR = 0.7f;
    private static final float DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR = 1.0f;
    private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 8;
    private static final int MODE_DRAW = 0;
    private static final int MODE_HIGHLIGHT = 1;
    private int rotation = DEFAULT_START_ROTATION;
    private PieChartDataProvider dataProvider;
    private Paint slicePaint = new Paint();
    private float maxSum;
    private RectF originCircleOval = new RectF();
    private RectF drawCircleOval = new RectF();
    private PointF sliceVector = new PointF();
    private int touchAdditional;
    private float circleFillRatio = 1.0f;

    // Center circle related attributes
    private boolean hasCenterCircle;
    private float centerCircleScale;
    private Paint centerCirclePaint = new Paint();
    // Text1
    private Paint centerCircleText1Paint = new Paint();
    private FontMetricsInt centerCircleText1FontMetrics = new FontMetricsInt();
    // Text2
    private Paint centerCircleText2Paint = new Paint();
    private FontMetricsInt centerCircleText2FontMetrics = new FontMetricsInt();
    // Separation lines
    private Paint separationLinesPaint = new Paint();

    private boolean hasLabelsOutside;
    private boolean hasLabels;
    private boolean hasLabelsOnlyForSelected;
    private PieChartValueFormatter valueFormatter;
    private Viewport tempMaximumViewport = new Viewport();

    private Bitmap softwareBitmap;
    private Canvas softwareCanvas = new Canvas();

    public PieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
        super(context, chart);
        this.dataProvider = dataProvider;
        touchAdditional = ChartUtils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);

        slicePaint.setAntiAlias(true);
        slicePaint.setStyle(Paint.Style.FILL);

        centerCirclePaint.setAntiAlias(true);
        centerCirclePaint.setStyle(Paint.Style.FILL);
        centerCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        centerCircleText1Paint.setAntiAlias(true);
        centerCircleText1Paint.setTextAlign(Align.CENTER);

        centerCircleText2Paint.setAntiAlias(true);
        centerCircleText2Paint.setTextAlign(Align.CENTER);

        separationLinesPaint.setAntiAlias(true);
        separationLinesPaint.setStyle(Paint.Style.STROKE);
        separationLinesPaint.setStrokeCap(Paint.Cap.ROUND);
        separationLinesPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        separationLinesPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void onChartSizeChanged() {
        calculateCircleOval();

        if (computator.getChartWidth() > 0 && computator.getChartHeight() > 0) {
            softwareBitmap = Bitmap.createBitmap(computator.getChartWidth(), computator.getChartHeight(),
                    Bitmap.Config.ARGB_8888);
            softwareCanvas.setBitmap(softwareBitmap);
        }
    }

    @Override
    public void onChartDataChanged() {
        super.onChartDataChanged();
        final PieChartData data = dataProvider.getPieChartData();
        hasLabelsOutside = data.hasLabelsOutside();
        hasLabels = data.hasLabels();
        hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected();
        valueFormatter = data.getFormatter();
        hasCenterCircle = data.hasCenterCircle();
        centerCircleScale = data.getCenterCircleScale();
        centerCirclePaint.setColor(data.getCenterCircleColor());
        if (null != data.getCenterText1Typeface()) {
            centerCircleText1Paint.setTypeface(data.getCenterText1Typeface());
        }
        centerCircleText1Paint.setTextSize(ChartUtils.sp2px(scaledDensity, data.getCenterText1FontSize()));
        centerCircleText1Paint.setColor(data.getCenterText1Color());
        centerCircleText1Paint.getFontMetricsInt(centerCircleText1FontMetrics);
        if (null != data.getCenterText2Typeface()) {
            centerCircleText2Paint.setTypeface(data.getCenterText2Typeface());
        }
        centerCircleText2Paint.setTextSize(ChartUtils.sp2px(scaledDensity, data.getCenterText2FontSize()));
        centerCircleText2Paint.setColor(data.getCenterText2Color());
        centerCircleText2Paint.getFontMetricsInt(centerCircleText2FontMetrics);

        onChartViewportChanged();
    }

    @Override
    public void onChartViewportChanged() {
        if (isViewportCalculationEnabled) {
            calculateMaxViewport();
            computator.setMaxViewport(tempMaximumViewport);
            computator.setCurrentViewport(computator.getMaximumViewport());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // softwareBitmap can be null if chart is rendered in layout editor. In that case use default canvas and not
        // softwareCanvas.
        final Canvas drawCanvas;
        if (null != softwareBitmap) {
            drawCanvas = softwareCanvas;
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } else {
            drawCanvas = canvas;
        }

        drawSlices(drawCanvas);
        drawSeparationLines(drawCanvas);
        if (hasCenterCircle) {
            drawCenterCircle(drawCanvas);
        }
        drawLabels(drawCanvas);

        if (null != softwareBitmap) {
            canvas.drawBitmap(softwareBitmap, 0, 0, null);
        }
    }

    @Override
    public void drawUnclipped(Canvas canvas) {
    }

    @Override
    public boolean checkTouch(float touchX, float touchY) {
        selectedValue.clear();
        final PieChartData data = dataProvider.getPieChartData();
        final float centerX = originCircleOval.centerX();
        final float centerY = originCircleOval.centerY();
        final float circleRadius = originCircleOval.width() / 2f;

        sliceVector.set(touchX - centerX, touchY - centerY);
        // Check if touch is on circle area, if not return false;
        if (sliceVector.length() > circleRadius + touchAdditional) {
            return false;
        }
        // Check if touch is not in center circle, if yes return false;
        if (data.hasCenterCircle() && sliceVector.length() < circleRadius * data.getCenterCircleScale()) {
            return false;
        }

        // Get touchAngle and align touch 0 degrees with chart 0 degrees, that why I subtracting start angle,
        // adding 360
        // and modulo 360 translates i.e -20 degrees to 340 degrees.
        final float touchAngle = (pointToAngle(touchX, touchY, centerX, centerY) - rotation + 360f) % 360f;
        final float sliceScale = 360f / maxSum;
        float lastAngle = 0f; // No start angle here, see above
        int sliceIndex = 0;
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;
            if (touchAngle >= lastAngle) {
                selectedValue.set(sliceIndex, sliceIndex, SelectedValueType.NONE);
            }
            lastAngle += angle;
            ++sliceIndex;
        }
        return isTouched();
    }

    /**
     * Draw center circle with text if {@link PieChartData#hasCenterCircle()} is set true.
     */
    private void drawCenterCircle(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        final float circleRadius = originCircleOval.width() / 2f;
        final float centerRadius = circleRadius * data.getCenterCircleScale();
        final float centerX = originCircleOval.centerX();
        final float centerY = originCircleOval.centerY();

        canvas.drawCircle(centerX, centerY, centerRadius, centerCirclePaint);

        // Draw center text1 and text2 if not empty.
        if (!TextUtils.isEmpty(data.getCenterText1())) {

            final int text1Height = Math.abs(centerCircleText1FontMetrics.ascent);

            if (!TextUtils.isEmpty(data.getCenterText2())) {
                // Draw text 2 only if text 1 is not empty.
                final int text2Height = Math.abs(centerCircleText2FontMetrics.ascent);
                canvas.drawText(data.getCenterText1(), centerX, centerY - text1Height * 0.2f, centerCircleText1Paint);
                canvas.drawText(data.getCenterText2(), centerX, centerY + text2Height, centerCircleText2Paint);
            } else {
                canvas.drawText(data.getCenterText1(), centerX, centerY + text1Height / 4, centerCircleText1Paint);
            }
        }
    }

    /**
     * Draw all slices for this PieChart, if mode == {@link #MODE_HIGHLIGHT} currently selected slices will be redrawn
     * and
     * highlighted.
     *
     * @param canvas
     */
    private void drawSlices(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        final float sliceScale = 360f / maxSum;
        float lastAngle = rotation;
        int sliceIndex = 0;
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;
            if (isTouched() && selectedValue.getFirstIndex() == sliceIndex) {
                drawSlice(canvas, sliceValue, lastAngle, angle, MODE_HIGHLIGHT);
            } else {
                drawSlice(canvas, sliceValue, lastAngle, angle, MODE_DRAW);
            }
            lastAngle += angle;
            ++sliceIndex;
        }
    }

    private void drawSeparationLines(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        if (data.getValues().size() < 2) {
            //No need for separation lines for 0 or 1 slices.
            return;
        }
        final int sliceSpacing = ChartUtils.dp2px(density, data.getSlicesSpacing());
        if (sliceSpacing < 1) {
            //No need for separation lines
            return;
        }
        final float sliceScale = 360f / maxSum;
        float lastAngle = rotation;
        final float circleRadius = originCircleOval.width() / 2f;
        separationLinesPaint.setStrokeWidth(sliceSpacing);
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;

            sliceVector.set((float) (Math.cos(Math.toRadians(lastAngle))),
                    (float) (Math.sin(Math.toRadians(lastAngle))));
            normalizeVector(sliceVector);

            float x1 = sliceVector.x * (circleRadius + touchAdditional) + originCircleOval.centerX();
            float y1 = sliceVector.y * (circleRadius + touchAdditional) + originCircleOval.centerY();

            canvas.drawLine(originCircleOval.centerX(), originCircleOval.centerY(), x1, y1, separationLinesPaint);

            lastAngle += angle;
        }
    }

    public void drawLabels(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        final float sliceScale = 360f / maxSum;
        float lastAngle = rotation;
        int sliceIndex = 0;
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;
            if (isTouched()) {
                if (hasLabels) {
                    drawLabel(canvas, sliceValue, lastAngle, angle);
                } else if (hasLabelsOnlyForSelected && selectedValue.getFirstIndex() == sliceIndex) {
                    drawLabel(canvas, sliceValue, lastAngle, angle);
                }
            } else {
                if (hasLabels) {
                    drawLabel(canvas, sliceValue, lastAngle, angle);
                }
            }
            lastAngle += angle;
            ++sliceIndex;
        }
    }

    /**
     * Method draws single slice from lastAngle to lastAngle+angle, if mode = {@link #MODE_HIGHLIGHT} slice will be
     * darken
     * and will have bigger radius.
     */
    private void drawSlice(Canvas canvas, SliceValue sliceValue, float lastAngle, float angle, int mode) {
        sliceVector.set((float) (Math.cos(Math.toRadians(lastAngle + angle / 2))),
                (float) (Math.sin(Math.toRadians(lastAngle + angle / 2))));
        normalizeVector(sliceVector);
        drawCircleOval.set(originCircleOval);
        if (MODE_HIGHLIGHT == mode) {
            // Add additional touch feedback by setting bigger radius for that slice and darken color.
            drawCircleOval.inset(-touchAdditional, -touchAdditional);
            slicePaint.setColor(sliceValue.getDarkenColor());
            canvas.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint);
        } else {
            slicePaint.setColor(sliceValue.getColor());
            canvas.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint);
        }
    }

    private void drawLabel(Canvas canvas, SliceValue sliceValue, float lastAngle, float angle) {
        sliceVector.set((float) (Math.cos(Math.toRadians(lastAngle + angle / 2))),
                (float) (Math.sin(Math.toRadians(lastAngle + angle / 2))));
        normalizeVector(sliceVector);

        final int numChars = valueFormatter.formatChartValue(labelBuffer, sliceValue);

        if (numChars == 0) {
            // No need to draw empty label
            return;
        }

        final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
        final int labelHeight = Math.abs(fontMetrics.ascent);

        final float centerX = originCircleOval.centerX();
        final float centerY = originCircleOval.centerY();
        final float circleRadius = originCircleOval.width() / 2f;
        final float labelRadius;

        if (hasLabelsOutside) {
            labelRadius = circleRadius * DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR;
        } else {
            if (hasCenterCircle) {
                labelRadius = circleRadius - (circleRadius - (circleRadius * centerCircleScale)) / 2;
            } else {
                labelRadius = circleRadius * DEFAULT_LABEL_INSIDE_RADIUS_FACTOR;
            }
        }

        final float rawX = labelRadius * sliceVector.x + centerX;
        final float rawY = labelRadius * sliceVector.y + centerY;

        float left;
        float right;
        float top;
        float bottom;

        if (hasLabelsOutside) {
            if (rawX > centerX) {
                // Right half.
                left = rawX + labelMargin;
                right = rawX + labelWidth + labelMargin * 3;
            } else {
                left = rawX - labelWidth - labelMargin * 3;
                right = rawX - labelMargin;
            }

            if (rawY > centerY) {
                // Lower half.
                top = rawY + labelMargin;
                bottom = rawY + labelHeight + labelMargin * 3;
            } else {
                top = rawY - labelHeight - labelMargin * 3;
                bottom = rawY - labelMargin;
            }
        } else {
            left = rawX - labelWidth / 2 - labelMargin;
            right = rawX + labelWidth / 2 + labelMargin;
            top = rawY - labelHeight / 2 - labelMargin;
            bottom = rawY + labelHeight / 2 + labelMargin;
        }

        labelBackgroundRect.set(left, top, right, bottom);
        drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars,
                sliceValue.getDarkenColor());
    }

    private void normalizeVector(PointF point) {
        final float abs = point.length();
        point.set(point.x / abs, point.y / abs);
    }

    /**
     * Calculates angle of touched point.
     */
    private float pointToAngle(float x, float y, float centerX, float centerY) {
        double diffX = x - centerX;
        double diffY = y - centerY;
        // Pass -diffX to get clockwise degrees order.
        double radian = Math.atan2(-diffX, diffY);

        float angle = ((float) Math.toDegrees(radian) + 360) % 360;
        // Add 90 because atan2 returns 0 degrees at 6 o'clock.
        angle += 90f;
        return angle;
    }

    /**
     * Calculates rectangle(square) that will constraint chart circle.
     */
    private void calculateCircleOval() {
        Rect contentRect = computator.getContentRectMinusAllMargins();
        final float circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f);
        final float centerX = contentRect.centerX();
        final float centerY = contentRect.centerY();
        final float left = centerX - circleRadius + touchAdditional;
        final float top = centerY - circleRadius + touchAdditional;
        final float right = centerX + circleRadius - touchAdditional;
        final float bottom = centerY + circleRadius - touchAdditional;
        originCircleOval.set(left, top, right, bottom);
        final float inest = 0.5f * originCircleOval.width() * (1.0f - circleFillRatio);
        originCircleOval.inset(inest, inest);
    }

    /**
     * Viewport is not really important for PieChart, this kind of chart doesn't relay on viewport but uses pixels
     * coordinates instead. This method also calculates sum of all SliceValues.
     */
    private void calculateMaxViewport() {
        tempMaximumViewport.set(0, MAX_WIDTH_HEIGHT, MAX_WIDTH_HEIGHT, 0);
        maxSum = 0.0f;
        for (SliceValue sliceValue : dataProvider.getPieChartData().getValues()) {
            maxSum += Math.abs(sliceValue.getValue());
        }
    }

    public RectF getCircleOval() {
        return originCircleOval;
    }

    public void setCircleOval(RectF orginCircleOval) {
        this.originCircleOval = orginCircleOval;
    }

    public int getChartRotation() {
        return rotation;
    }

    public void setChartRotation(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        this.rotation = rotation;
    }

    /**
     * Returns SliceValue that is under given angle, selectedValue (if not null) will be hold slice index.
     */
    public SliceValue getValueForAngle(int angle, SelectedValue selectedValue) {
        final PieChartData data = dataProvider.getPieChartData();
        final float touchAngle = (angle - rotation + 360f) % 360f;
        final float sliceScale = 360f / maxSum;
        float lastAngle = 0f;
        int sliceIndex = 0;
        for (SliceValue sliceValue : data.getValues()) {
            final float tempAngle = Math.abs(sliceValue.getValue()) * sliceScale;
            if (touchAngle >= lastAngle) {
                if (null != selectedValue) {
                    selectedValue.set(sliceIndex, sliceIndex, SelectedValueType.NONE);
                }
                return sliceValue;
            }
            lastAngle += tempAngle;
            ++sliceIndex;
        }
        return null;
    }

    /**
     * @see #setCircleFillRatio(float)
     */
    public float getCircleFillRatio() {
        return this.circleFillRatio;
    }

    /**
     * Set how much of view area should be taken by chart circle. Value should be between 0 and 1. Default is 1 so
     * circle will have radius equals min(View.width, View.height).
     */
    public void setCircleFillRatio(float fillRatio) {
        if (fillRatio < 0) {
            fillRatio = 0;
        } else if (fillRatio > 1) {
            fillRatio = 1;
        }

        this.circleFillRatio = fillRatio;
        calculateCircleOval();
    }

}
