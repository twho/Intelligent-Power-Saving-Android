package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael Ho on 2015/5/27.
 * Updated by Michael Ho on 2017/12/21.
 */

// Singleton class
public class ChartUtils implements BuildingConstants {

    private static final ChartUtils ourInstance = new ChartUtils();

    public static ChartUtils getInstance() {
        return ourInstance;
    }

    private ChartUtils() {
    }

    public void setupLineChart(Context context, LineChart lineChart, ArrayList<String> stringArrayList) {
        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.WHITE);
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "Share-Regular.ttf");

        List<Integer> intList = convertStringArrToIntArr(stringArrayList);

        LimitLine ll1 = new LimitLine(Collections.max(intList), "Max Consumption");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTextColor(context.getResources().getColor(R.color.green));
        ll1.setLineColor(context.getResources().getColor(R.color.green));
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(Collections.min(intList), "Min Consumption");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextColor(context.getResources().getColor(R.color.green));
        ll2.setLineColor(context.getResources().getColor(R.color.green));
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(Collections.max(intList)*1.1f);
        leftAxis.setAxisMinimum(Collections.min(intList) == 0 ? -15f : 0f);
        leftAxis.setTextColor(Color.WHITE);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);

        //lineChart.getViewPortHandler().setMaximumScaleY(2f);
        //lineChart.getViewPortHandler().setMaximumScaleX(2f);
        //
        // lineChart.setVisibleXRange(20);
        // lineChart.setVisibleYRange(20f, AxisDependency.LEFT);
        // lineChart.centerViewTo(20, 50, AxisDependency.LEFT);

        setChartData(context, lineChart, stringArrayList);

        lineChart.animateX(1500);

        // get the legend (only possible after setting data) and modify it
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(context.getResources().getColor(R.color.teal));

        // Refresh the drawing
        // lineChart.invalidate();
    }

    private  ArrayList<Integer> convertStringArrToIntArr(ArrayList<String> arrayList) {
        ArrayList<Integer> numArraList = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            numArraList.add(Integer.parseInt(arrayList.get(i)));
        }
        return numArraList;
    }

    private void setChartData(Context context, LineChart lineChart, ArrayList<String> stringArrayList) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < stringArrayList.size(); i++) {
            // Drawable icon is unused, can be set to anything
            values.add(new Entry(i, Float.valueOf(stringArrayList.get(i)), context.getResources().getDrawable(R.drawable.img_splash)));
        }

        LineDataSet set1;

        if (null != lineChart.getData() && lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, context.getString(R.string.chart_y_title));

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(context.getResources().getColor(R.color.teal));
            set1.setValueTextColor(context.getResources().getColor(R.color.teal));
            set1.setCircleColor(context.getResources().getColor(R.color.teal));
            set1.setLineWidth(1f);
            set1.setCircleRadius(4.5f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.background_chart_line);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(context.getResources().getColor(R.color.teal));
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            lineChart.setData(data);
        }
    }
}
