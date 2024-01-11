package com.example.GuildWakayama2.ui.notifications;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    public CustomArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        // カスタムしたスタイルを適用
        applyCustomStyle((TextView) view);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        // カスタムしたスタイルを適用
        applyCustomStyle((TextView) view);
        return view;
    }

    private void applyCustomStyle(TextView textView) {
        // ここにカスタムしたスタイルの設定を追加
        // 例えば、textView.setTextColor() や textView.setBackgroundColor() を使用
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.parseColor("#303F9F"));
    }
}
