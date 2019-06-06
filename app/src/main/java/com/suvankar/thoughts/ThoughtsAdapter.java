package com.suvankar.thoughts;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ThoughtsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ThoughtModel> thoughtList;
    List<String> colorList;

    public ThoughtsAdapter(List<ThoughtModel> thoughtList, List<String> colorList) {
        this.thoughtList = thoughtList;
        this.colorList = colorList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.thought_list_item, viewGroup, false);
        return new ThoughtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ThoughtViewHolder holder = (ThoughtViewHolder) viewHolder;
        holder.bind(i);
        holder.thoughtCard
                .setCardBackgroundColor(Color.parseColor(colorList.get((int) (Math.random() * 5))));
    }

    @Override
    public int getItemCount() {
        return thoughtList.size();
    }

    class ThoughtViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView thoughtCard;
        TextView thoughtText, timeStamp;

        public ThoughtViewHolder(@NonNull View itemView) {
            super(itemView);
            thoughtCard = itemView.findViewById(R.id.thought_card);
            thoughtText = itemView.findViewById(R.id.thought);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            thoughtText.setText(thoughtList.get(position).getText());
            timeStamp.setText(thoughtList.get(position).getTime());
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            String thought = thoughtList.get(position).getText();
//            MainActivity.saveThought(true, position);
        }
    }
}
