package com.example.alphachat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.alphachat.R;
import com.example.alphachat.model.Mechina;
import java.util.List;

public class MechinaAdapter extends RecyclerView.Adapter<MechinaAdapter.MechinaViewHolder> {

    private final List<Mechina> displayList;

    public MechinaAdapter(List<Mechina> displayList) {
        this.displayList = displayList;
    }

    @NonNull
    @Override
    public MechinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechina, parent, false);
        return new MechinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MechinaViewHolder holder, int position) {
        Mechina item = displayList.get(position);

        holder.nameText.setText(item.name);
        holder.detailsText.setText(item.region + " | " + item.gender + " | " + item.type);

        // Glide intercepts the URL thread, checks local memory cache, or downloads asynchronously
        Glide.with(holder.itemView.getContext())
                .load(item.image)
                .placeholder(R.drawable.icon_search)
                .error(R.drawable.icon_search)
                .into(holder.mechinaImage);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class MechinaViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, detailsText;
        ImageView mechinaImage;

        public MechinaViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.mechina_name_text);
            detailsText = itemView.findViewById(R.id.mechina_details_text);
            mechinaImage = itemView.findViewById(R.id.mechina_image_view);

            // Forces Hebrew right-to-left layout alignment dynamically across older versions
            nameText.setTextDirection(View.TEXT_DIRECTION_RTL);
            detailsText.setTextDirection(View.TEXT_DIRECTION_RTL);
        }
    }
}