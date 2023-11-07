package com.acodigo.godkantsv.adapters_and_items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.acodigo.godkantsv.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class adapter_questions extends RecyclerView.Adapter<adapter_questions.MyViewHolder> {

    final Integer integerSelectedIndex;

    final ArrayList<String> arrayListQuestionsNumbers;
    final ArrayList<String> arrayListQuestionsStatus;

    final Context context;

    onItemClickListener itemClickListener;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public adapter_questions(Context context, Integer integerSelectedIndex, ArrayList<String> arrayListQuestionsNumbers, ArrayList<String> arrayListQuestionsStatus) {
        this.context = context;
        this.integerSelectedIndex = integerSelectedIndex;
        this.arrayListQuestionsNumbers = arrayListQuestionsNumbers;
        this.arrayListQuestionsStatus = arrayListQuestionsStatus;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new MyViewHolder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewQuestionNumber.setText(arrayListQuestionsNumbers.get(position));

        if (arrayListQuestionsStatus.get(position).equals("0")) {
            holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_gray_dark);
        } else if (arrayListQuestionsStatus.get(position).equals("1")) {
            holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_primary);
        } else {
            holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_yellow_dark);
        }

        if (integerSelectedIndex == position) {
            if (arrayListQuestionsStatus.get(position).equals("0")) {
                holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_gray_dark_border_selected);
            } else if (arrayListQuestionsStatus.get(position).equals("1")) {
                holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_primary_border_selected);
            } else {
                holder.relativeLayoutQuestionNumber.setBackgroundResource(R.drawable.shape_dynamic_square_yellow_dark_border_selected);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListQuestionsNumbers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout relativeLayoutQuestionNumber;
        final TextView textViewQuestionNumber;

        public MyViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            textViewQuestionNumber = itemView.findViewById(R.id.textViewQuestionNumber);
            relativeLayoutQuestionNumber = itemView.findViewById(R.id.relativeLayoutQuestionNumber);

            relativeLayoutQuestionNumber.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        itemClickListener = listener;
    }
}