package com.example.herhealth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Dashboard4 extends AppCompatActivity {
    BarChart barchart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard4);
        barchart = findViewById(R.id.barchart);

        // Create a list of BarEntry objects with your data points
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 25));
        entries.add(new BarEntry(1, 15));
        entries.add(new BarEntry(2, 30));
        entries.add(new BarEntry(3, 20));

        // Create a BarDataSet with the entries
        BarDataSet dataSet = new BarDataSet(entries, "Categories");

        // Set colors for the bars
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a BarData object with the dataset
        BarData barData = new BarData(dataSet);

        // Set up data formatting for percentages
        barData.setValueFormatter(new PercentFormatter());
        barData.setValueTextColor(getResources().getColor(android.R.color.black));

        barchart.setData(barData);

        // Customize the BarChart appearance
        Description description = new Description();
        description.setText(""); // Remove description
        barchart.setDescription(description);
        barchart.setDrawValueAboveBar(true);
        barchart.setDrawBarShadow(false);
        barchart.setDrawGridBackground(false);
        barchart.setFitBars(true);

        // Set labels for the x-axis
        String[] labels = new String[]{"Mental Health", "Menstruation", "Pregnancy", "Gynaecological"};
        XAxis xAxis = barchart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barchart.invalidate();


    }
}