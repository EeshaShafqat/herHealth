package com.example.herhealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Dashboard3 extends AppCompatActivity {

    private HorizontalBarChart horizontalbarchart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard3);
        horizontalbarchart = findViewById(R.id.horizontalbarchart);
        // Create a list of BarEntry objects with your data points
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 3.5f));  // (y-value, x-value)
        entries.add(new BarEntry(2f, 4.0f));
        entries.add(new BarEntry(3f, 2.5f));
        entries.add(new BarEntry(4f, 5.0f));
        entries.add(new BarEntry(5f, 4.2f));

        BarDataSet dataSet = new BarDataSet(entries, "Lack of Access to Sanitary Items");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(dataSet);
        horizontalbarchart.setData(barData);

        // Customize the HorizontalBarChart
        horizontalbarchart.getDescription().setEnabled(false);
        horizontalbarchart.setDrawValueAboveBar(true);
        horizontalbarchart.setDrawGridBackground(false);

        XAxis xAxis = horizontalbarchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftYAxis = horizontalbarchart.getAxisLeft();
        leftYAxis.setDrawGridLines(false);

        YAxis rightYAxis = horizontalbarchart.getAxisRight();
        rightYAxis.setDrawGridLines(false);

        horizontalbarchart.setFitBars(true);

        // Invalidate the chart to refresh its appearance
        horizontalbarchart.invalidate();


        TextView more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard3.this, Dashboard4.class);
                startActivity(intent);
            }


        });


    }
}