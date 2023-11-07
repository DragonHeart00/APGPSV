package com.acodigo.godkantsv.adapters_and_items;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.acodigo.godkantsv.R;

import java.util.List;
import java.util.Objects;

public class adapter_folders extends ArrayAdapter<item_folders> {

    private final Context context;

    public adapter_folders(Context context, int resourceId, List<item_folders> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private static class ViewHolder {
        ImageView imageViewIcon, imageViewNew, imageViewLock;
        TextView textViewTitle, textViewDescription, textViewDash, textViewResult;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        item_folders rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = Objects.requireNonNull(mInflater).inflate(R.layout.item_folder, null);
            holder = new ViewHolder();
            holder.imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            holder.imageViewNew = convertView.findViewById(R.id.imageViewNew);
            holder.imageViewLock = convertView.findViewById(R.id.imageViewLock);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewDescription = convertView.findViewById(R.id.textViewDescription);
            holder.textViewDash = convertView.findViewById(R.id.textViewDash);
            holder.textViewResult = convertView.findViewById(R.id.textViewResult);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewTitle.setText(Objects.requireNonNull(rowItem).getTitle());
        holder.textViewDescription.setText(Objects.requireNonNull(rowItem).getDescription());

        if (Objects.requireNonNull(rowItem).isLock()) {
            holder.imageViewIcon.setBackgroundResource(R.drawable.shape_static_circle_gray_light);
            holder.imageViewNew.setVisibility(View.GONE);
            holder.imageViewLock.setVisibility(View.VISIBLE);
            holder.textViewDescription.setVisibility(View.GONE);
            holder.textViewDash.setVisibility(View.GONE);
            holder.textViewResult.setVisibility(View.GONE);

            convertView.setEnabled(false);
        } else {
            holder.imageViewIcon.setBackgroundResource(R.drawable.shape_dynamic_circle_primary);
            holder.imageViewLock.setVisibility(View.GONE);
            holder.textViewDescription.setVisibility(View.VISIBLE);

            if (Objects.requireNonNull(rowItem).isOpen()) {
                holder.imageViewNew.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewNew.setVisibility(View.GONE);
            }

            if (Objects.requireNonNull(rowItem).isDone()) {
                try {
                    if (Objects.requireNonNull(rowItem).isSuccess()) {
                        holder.textViewResult.setText(R.string.succeed);
                        holder.textViewResult.setTextColor(ContextCompat.getColor(context, R.color.colorGreenDark));
                    } else {
                        holder.textViewResult.setText(R.string.failed);
                        holder.textViewResult.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark));
                    }
                } finally {
                    holder.textViewDash.setVisibility(View.VISIBLE);
                    holder.textViewResult.setVisibility(View.VISIBLE);
                }
            } else {
                holder.textViewDash.setVisibility(View.GONE);
                holder.textViewResult.setVisibility(View.GONE);
            }

            convertView.setEnabled(true);
        }

        return convertView;
    }
}