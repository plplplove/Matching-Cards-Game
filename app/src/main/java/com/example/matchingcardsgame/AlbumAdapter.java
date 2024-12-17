package com.example.matchingcardsgame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Integer> albumImages;

    public AlbumAdapter(List<Integer> albumImages) {
        this.albumImages = albumImages;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.albumImage.setImageResource(albumImages.get(position));
    }

    @Override
    public int getItemCount() {
        return albumImages.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImage;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.albumImage);
        }
    }
}