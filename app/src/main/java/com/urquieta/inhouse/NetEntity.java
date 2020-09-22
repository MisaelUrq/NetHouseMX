package com.urquieta.inhouse;

import java.io.Serializable;
import java.util.ArrayList;

public class NetEntity implements Serializable {
    public String entity;
    public ArrayList<NetData> data;
    public NetData year_min_d;
    public NetData year_max_d;

    public NetEntity() {
        entity = null;
        data   = new ArrayList<>();
    }

    public class NetData implements Serializable {
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
