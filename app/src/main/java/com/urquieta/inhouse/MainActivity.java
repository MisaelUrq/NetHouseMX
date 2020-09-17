package com.urquieta.inhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CSVData csv_data;

    private class CSVData {

        public ArrayList<String>            headers;
        public boolean                      valid;
        public ArrayList<ArrayList<String>> rows;

        public CSVData() {
            valid      = false;
            rows       = new ArrayList<>();
        }

        public void LoadResFile(Context ctx, int id) {
            InputStream stream = ctx.getResources().openRawResource(id);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader    buf_reader = new BufferedReader(reader);
            String line;
            try {
                line = buf_reader.readLine();
                if (line != null) {
                    this.headers = this.ParseCSVLines(line);
                    while ((line = buf_reader.readLine()) != null) {
                        ArrayList<String> row = this.ParseCSVLines(line);
                        this.rows.add(row);
                    }
                    this.valid = true;
                }

            } catch (IOException e) {
                return;
            }
        }

        public ArrayList<String> ParseCSVLines(String line) {
            String[] ls = line.split(",");
            ArrayList<String> result = new ArrayList<>();
            String r = "";

            boolean quotes_active = false;
            for (String l : ls) {
                if (quotes_active) {
                    r += l;
                    if (l.endsWith("\"")) {
                        quotes_active = false;
                        result.add(r.replaceAll("\"", ""));
                        r = "";
                    }
                } else {
                    if (l.startsWith("\"")) {
                        quotes_active = true;
                        r += l;
                    } else {
                        result.add(l);
                    }
                }
            }

            return result;
        }
    }

    public class NetEntity {
        public String entity;
        public ArrayList<NetData> data;
        public NetData year_min_d;
        public NetData year_max_d;

        public NetEntity() {
            entity = null;
            data   = new ArrayList<>();
        }

        public class NetData {
            public int    year;
            public float  percentage;
            public long   homes_with_net;
            public long   homes_total;
        }

        public String GetYearRange() {
            return String.format("%d - %d", year_min_d.year, year_max_d.year);
        }

        public void CalculateDataRanges() {
            int year_min = Integer.MAX_VALUE;
            int year_max = Integer.MIN_VALUE;

            for (NetData d : data) {
                if (d.year < year_min) {
                    year_min = d.year;
                    year_min_d = d;
                }
                if (d.year > year_max) {
                    year_max = d.year;
                    year_max_d = d;
                }
            }
        }

        public String GetHomesNetRange() {
            return String.format("%d - %d", year_min_d.homes_with_net, year_max_d.homes_with_net);
        }

        public String GetPercentageRange() {
            return String.format("%.2f - %.2f", year_min_d.percentage, year_max_d.percentage);
        }

        public String GetTotalHomesRange() {
            return String.format("%d - %d", year_min_d.homes_total, year_max_d.homes_total);
        }

        public void AddArrayData(ArrayList<String> rs) {
            if (entity == null) {
                entity = rs.get(0);
                NetData d = new NetData();
                d.year           = Integer.parseInt(rs.get(1));
                d.percentage     = Float.parseFloat(rs.get(2));
                d.homes_with_net = Long.parseLong(rs.get(3));
                d.homes_total    = Long.parseLong(rs.get(4));
                data.add(d);
            } else if (entity.compareTo(rs.get(0)) == 0){
                NetData d = new NetData();
                d.year           = Integer.parseInt(rs.get(1));
                d.percentage     = Float.parseFloat(rs.get(2));
                d.homes_with_net = Long.parseLong(rs.get(3));
                d.homes_total    = Long.parseLong(rs.get(4));
                data.add(d);
            }
        }
    }

    public class NetEntityAdapter extends ArrayAdapter<NetEntity> {

        public NetEntityAdapter(Context context, ArrayList<NetEntity> ns) {
            super(context, 0, ns);
        }

        @Override
        public View getView(int p, View v, ViewGroup vg) {
            NetEntity n = getItem(p);
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.activity_ls_view, vg, false);
            }
            n.CalculateDataRanges();
            ((TextView) v.findViewById(R.id.element_view)).setText(n.entity);
            ((TextView) v.findViewById(R.id.element_years)).setText("\tRango de años: " + n.GetYearRange());
            ((TextView) v.findViewById(R.id.element_total_homes)).setText("\tTotal de casas: " + n.GetTotalHomesRange());
            ((TextView) v.findViewById(R.id.element_homes_percentage)).setText("\tPorcentaje de casas con internet: " + n.GetPercentageRange());
            return v;
        }
    }

    public class CSVRowAdapter extends ArrayAdapter<ArrayList<String>> {

        public CSVRowAdapter(Context context, ArrayList<ArrayList<String>> rs) {
            super(context, 0, rs);
        }

        @Override
        public View getView(int p, View v, ViewGroup vg) {
            ArrayList<String> rs = getItem(p);
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.activity_ls_view, vg, false);
            }
            ((TextView) v.findViewById(R.id.element_view)).setText(rs.get(0));
            return v;
        }
    }

    ArrayList<NetEntity> entities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        csv_data = new CSVData();
        csv_data.LoadResFile(this, R.raw.internet_data);
        entities = new ArrayList<>();

        for (ArrayList<String> r : csv_data.rows) {
            String e_name = r.get(0);
            boolean entity_found = false;
            for (NetEntity e : entities) {
                if (e.entity.compareTo(e_name) == 0) {
                    e.AddArrayData(r);
                    entity_found = true;
                    break;
                }
            }
            if (!entity_found) {
                NetEntity e = new NetEntity();
                e.AddArrayData(r);
                entities.add(e);
            }
        }

        ListView list = findViewById(R.id.internet_house_ls);
        NetEntityAdapter ls_adapter = new NetEntityAdapter(this, entities);
        // ArrayAdapter<ArrayList<String>> ls_adapter = new ArrayAdapter<>(this, R.layout.activity_ls_view, R.id.element_view, csv_data.rows);

        list.setAdapter(ls_adapter);
    }
}
