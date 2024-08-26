package es.lamc.appnegytech.dData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private List<String> items;
    private List<Boolean> isEnabledList;

    public CustomSpinnerAdapter(Context context, List<String> items, List<Boolean> isEnabledList) {
        super(context, android.R.layout.simple_spinner_item, items);
        this.items = items;
        this.isEnabledList = isEnabledList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (!isEnabledList.get(position)) {
            view.setEnabled(false);
            ((TextView) view).setTextColor(Color.GRAY); // Cambiar color para indicar deshabilitado
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        if (!isEnabledList.get(position)) {
            view.setEnabled(false);
            ((TextView) view).setTextColor(Color.GRAY); // Cambiar color para indicar deshabilitado
        }
        return view;
    }
}

