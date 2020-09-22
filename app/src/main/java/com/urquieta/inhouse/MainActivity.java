package com.urquieta.inhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, GraphEntity.class);
                i.putExtra("entity", (NetEntity)parent.getItemAtPosition(position));
                startActivity(i);
            }
        });
        NetEntityAdapter ls_adapter = new NetEntityAdapter(this, entities);

        list.setAdapter(ls_adapter);
    }
}
