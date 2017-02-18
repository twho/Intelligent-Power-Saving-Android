package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by MichaelHo on 2015/5/27.
 */
public class AChartUtilities {
    private Context context;

    public AChartUtilities(Context context) {
        this.context = context;
    }

    public View getBarChart(String chartTitle, String XTitle, String YTitle, String[][] xy) {

        XYSeries Series = new XYSeries(YTitle);

        XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
        Dataset.addSeries(Series);

        XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        Renderer.addSeriesRenderer(yRenderer);

        Renderer.setMarginsColor(Color.WHITE);              // set background color
        Renderer.setTextTypeface(null, Typeface.BOLD);      // set text style
        Renderer.setShowGrid(true);                         // set grid
        Renderer.setGridColor(Color.GRAY);                  // set grid color
        Renderer.setChartTitle(chartTitle);                 // set title text
        Renderer.setLabelsColor(Color.BLACK);               // set title text color
        Renderer.setChartTitleTextSize(20);                 // set title text size
        Renderer.setAxesColor(Color.BLACK);                 // set axis color
        Renderer.setBarSpacing(0.5);                        // set bar gap
        //Renderer.setXTitle(XTitle);						// set x axis text
        //Renderer.setYTitle(YTitle);						// set y axis text
        Renderer.setXLabelsColor(Color.BLACK);              // set x axis text color
        Renderer.setYLabelsColor(0, Color.BLACK);           // set y axis text color
        Renderer.setXLabelsAlign(Paint.Align.CENTER);       // set x text align
        Renderer.setYLabelsAlign(Paint.Align.CENTER);       // set y text align
        Renderer.setXLabelsAngle(0);                        // set x text tilt

        Renderer.setXLabels(0);
        Renderer.setYAxisMin(0);                            // set y min

        yRenderer.setColor(Color.BLUE);                     // set series color
        yRenderer.setDisplayChartValues(true);              // show series value

        Series.add(0, 0);
        Renderer.addXTextLabel(0, "");
        for (int r = 0; r < xy.length; r++) {
            Renderer.addXTextLabel(r + 1, xy[r][0]);
            Series.add(r + 1, Integer.parseInt(xy[r][1]));
        }
        Series.add(11, 0);
        Renderer.addXTextLabel(xy.length + 1, "");
        View view = ChartFactory.getBarChartView(context, Dataset, Renderer, BarChart.Type.DEFAULT);
        return view;
    }

}
