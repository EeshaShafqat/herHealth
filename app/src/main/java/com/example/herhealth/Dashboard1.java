package com.example.herhealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Dashboard1 extends AppCompatActivity {
    BarChart barchart;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard1);

        //Assign Variables
        barchart = findViewById(R.id.barchart);
        pieChart = findViewById(R.id.piechart);

        //initialize variables
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        //use for loop
        for (int i = 0; i < 10; i++){
           //Convert to Float
            float value = (float) (i*10.0);
            //initialize bar chart entry
            BarEntry barEntry = new BarEntry(i,value);
            //Initialize pie chart entry
            PieEntry pieEntry = new PieEntry(i,value);
            //add to array list
            barEntries.add(barEntry);
            pieEntries.add(pieEntry);
        }
        //initialize bar data set
        BarDataSet barDataSet = new BarDataSet(barEntries,"MHM");
        //add more entries to bar data set
        barDataSet.setBarBorderWidth(0.9f);
        //set Colors
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //Hide draw Value
        barDataSet.setDrawValues(false);
        //set bar data
        barchart.setData(new BarData(barDataSet));
        barchart.getDescription().setText("Ages");
        barchart.getDescription().setTextColor(Color.BLUE);

        //initialize pie data set
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"Age");
        //set Colors
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //Set pie Data
        pieChart.setData(new PieData(pieDataSet));
        //Set Animation
        pieChart.animateXY(5000,5000);
        //Hide Description
        pieChart.getDescription().setEnabled(false);



        TextView more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard1.this, Dashboard2.class);
                startActivity(intent);
            }


        });




    }
}