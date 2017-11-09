package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

/**
 * Created by MichaelHo on 2015/5/27.
 */
public class AChartUtils implements BuildingConstants {

    private static final AChartUtils ourInstance = new AChartUtils();

    public static AChartUtils getInstance() {
        return ourInstance;
    }

    private AChartUtils() {}

    public View getBarChart(Context context, String chartTitle, String XTitle, String YTitle, ArrayList<String> seriesValue) {

        XYSeries Series = new XYSeries(YTitle);

        XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
        Dataset.addSeries(Series);

        XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        Renderer.addSeriesRenderer(yRenderer);

        // set background color
        Renderer.setMarginsColor(context.getResources().getColor(R.color.colorPrimaryDark));
        // set text style
        Renderer.setTextTypeface(null, Typeface.NORMAL);
        // set grid
        Renderer.setShowGrid(true);
        Renderer.setGridColor(Color.GRAY);
        // set text
        Renderer.setChartTitle(chartTitle);
        Renderer.setXTitle(XTitle);

        // set text size
        Renderer.setChartTitleTextSize(context.getResources().getDimension(R.dimen.fragment_building_title_text_size));
        Renderer.setAxisTitleTextSize(context.getResources().getDimension(R.dimen.fragment_building_content_text_size));
        Renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.fragment_building_content_text_size));
        // set text color
        Renderer.setLabelsColor(Color.WHITE);
        Renderer.setXLabelsColor(Color.WHITE);
        Renderer.setYLabelsColor(0, Color.WHITE);

        // set axis color
        Renderer.setAxesColor(context.getResources().getColor(R.color.colorPrimaryDark));
        // set bar gap
        Renderer.setBarSpacing(0.5);

        // set x axis
        Renderer.setXLabelsAlign(Paint.Align.CENTER);
        Renderer.setXLabelsAngle(0);
        Renderer.setXLabels(0);
        Renderer.setXLabelsPadding(context.getResources().getDimension(R.dimen.activity_main_space));

        // set y axis
        Renderer.setYLabelsAlign(Paint.Align.CENTER);
        Renderer.setYAxisMin(0);
        Renderer.setYAxisMax(350);
        Renderer.setYLabelsPadding(context.getResources().getDimension(R.dimen.activity_main_space));

        // set series color
        yRenderer.setColor(context.getResources().getColor(R.color.teal));
        // show series value
        yRenderer.setDisplayChartValues(true);

        Series.add(0, 0);
        Renderer.addXTextLabel(0, "");
        for (int r = 0; r < seriesValue.size(); r++) {
            Series.add(r + 1, Double.parseDouble(seriesValue.get(r)));
            Renderer.addXTextLabel(r + 1, TIME_HOURS[r]);
        }
        Series.add(seriesValue.size(), 0);
        Renderer.addXTextLabel(seriesValue.size() + 1, "");


        View view = ChartFactory.getBarChartView(context, Dataset, Renderer, BarChart.Type.DEFAULT);
        return view;
    }

}
