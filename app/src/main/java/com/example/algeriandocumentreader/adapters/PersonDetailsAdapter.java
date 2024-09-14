package com.example.algeriandocumentreader.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.algeriandocumentreader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonDetailsAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> keys;
    private final List<Object> values;

    public PersonDetailsAdapter(Context context, Map<String, Object> data) {
        this.context = context;
        this.keys = new ArrayList<>(data.keySet());
        this.values = new ArrayList<>(data.values());
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        // Find the key TextView and set its value
        TextView keyTextView = convertView.findViewById(R.id.tv_key);
        keyTextView.setText(keys.get(position));

        // Get references for the value views
        TextView valueTextView = convertView.findViewById(R.id.tv_value);
        ImageView imageView = convertView.findViewById(R.id.image_value);

        // Reset visibility
        valueTextView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        // Check if the value is a String or a Bitmap and set the appropriate view
        if (values.get(position) instanceof String) {
            valueTextView.setVisibility(View.VISIBLE);  // Show the TextView
            valueTextView.setText((String) values.get(position));
        } else if (values.get(position) instanceof Bitmap) {
            imageView.setVisibility(View.VISIBLE);  // Show the ImageView
            imageView.setImageBitmap((Bitmap) values.get(position));
        }

        return convertView;
    }


}

