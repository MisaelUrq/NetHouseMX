package com.urquieta.inhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphEntity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_entity);
        Intent i = getIntent();
        NetEntity e = (NetEntity) i.getSerializableExtra("entity");
        TextView text = findViewById(R.id.element_title);
        if (text != null) {
            text.setText(e.entity);
        }

        LineChartView chart = findViewById(R.id.chart);
        List years = new ArrayList();
        List percentages = new ArrayList();

        int index = 0;
        for (NetEntity.NetData d : e.data) {
            years.add(new AxisValue(index).setLabel(String.format("%d", d.year)));
            percentages.add(new PointValue(index++, d.percentage));
        }

        Line line  = new Line(percentages).setColor(Color.parseColor("#34CAB0"));
        List lines = new ArrayList();
        lines.add(line);

        LineChartData line_data = new LineChartData();
        line_data.setLines(lines);

        Axis axis_bottom = new Axis();
        axis_bottom.setValues(years);
        axis_bottom.setTextSize(20);
        axis_bottom.setTextColor(Color.parseColor("#34CAB0"));
        line_data.setAxisXBottom(axis_bottom);

        Axis axis_y     = new Axis();
        // axis_y.setValues(percentages);
        axis_y.setName("Porcentaje");
        axis_y.setTextColor(Color.parseColor("#34CAB0"));
        axis_y.setTextSize(18);
        line_data.setAxisYLeft(axis_y);

        chart.setLineChartData(line_data);
        Viewport vp = new Viewport(chart.getMaximumViewport());
        vp.top = 100;
        chart.setMaximumViewport(vp);
        chart.setCurrentViewport(vp);
    }
}
