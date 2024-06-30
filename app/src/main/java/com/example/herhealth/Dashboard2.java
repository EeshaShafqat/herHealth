package com.example.herhealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Dashboard2 extends AppCompatActivity {
    LineChart lineChart;


    private ArrayList<Entry> getData() {
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(0, 50));
        values.add(new Entry(1, 70));
        values.add(new Entry(2, 30));
        // Add more data points as needed
        return values;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        lineChart = findViewById(R.id.linechart);
        // Customize the LineChart as needed
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        LineDataSet dataSet = new LineDataSet(getData(), "MHM");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

        TextView more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard2.this, Dashboard3.class);
                startActivity(intent);
            }


        });






    }
}

