package com.jatora.tfg_the_climb_within;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TutorialPagerAdapter extends RecyclerView.Adapter<TutorialPagerAdapter.TutorialViewHolder> {

    private String[] titles;
    private int[] images;
    private String[] hints;

    public TutorialPagerAdapter(String[] titles, int[] images, String[] hints) {
        this.titles = titles;
        this.images = images;
        this.hints = hints;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial_slide, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        holder.imageView.setImageResource(images[position]);
        holder.hint.setText(hints[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView hint;

        public TutorialViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            hint = itemView.findViewById(R.id.hint);
        }
    }
}
