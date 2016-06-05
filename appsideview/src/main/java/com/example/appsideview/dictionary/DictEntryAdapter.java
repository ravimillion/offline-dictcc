package com.example.appsideview.dictionary;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appsideview.R;

public class DictEntryAdapter extends ArrayAdapter<String> {

    private Activity context = null;
    private String TAG = "DictEntryAdapter";
    private String[] keys = null;
    private String[] values = null;


    public DictEntryAdapter(Activity context, String[] keys, String[] values) {
        super(context, R.layout.rowlayout, keys);
        this.keys = keys;
        this.values = values;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.rowlayout, null, true);

        TextView key = (TextView) rowView.findViewById(R.id.key);
        TextView value = (TextView) rowView.findViewById(R.id.value);

        key.setText(keys[position]);
        value.setText(values[position]);
        return rowView;
    }
}