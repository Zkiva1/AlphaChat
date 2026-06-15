package com.example.alphachat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alphachat.MechinaActivity;
import com.example.alphachat.R;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.utils.AndroidUtil;

import java.util.List;

/**
 * Adapter for displaying a list of Mechinot (academies) in a RecyclerView.
 *
 * This adapter manages {@link Mechina} objects and binds them to the {@code item_mechina}
 * layout. It handles image loading via Glide and navigates to {@link MechinaActivity}
 * when an item is clicked.
 */
public class MechinaAdapter extends RecyclerView.Adapter<MechinaAdapter.MechinaViewHolder> {

    /** The list of academies to be displayed in the RecyclerView. */
    private final List<Mechina> displayList;

    /**
     * Constructs a new MechinaAdapter with a specific list of academies.
     *
     * @param displayList The {@link List} of {@link Mechina} objects to display.
     */
    public MechinaAdapter(List<Mechina> displayList) {
        this.displayList = displayList;
    }

    /**
     * Called by RecyclerView to create a new ViewHolder.
     *
     * @param parent The {@link ViewGroup} into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@link MechinaViewHolder} that holds the {@code item_mechina} view.
     */
    @NonNull
    @Override
    public MechinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechina, parent, false);
        return new MechinaViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * Binds academy details to the UI components and sets up a click listener to
     * launch the details activity.
     *
     * @param holder The {@link MechinaViewHolder} to update.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MechinaViewHolder holder, int position) {
        Mechina item = displayList.get(position);

        holder.nameText.setText(item.getName());
        holder.detailsText.setText(item.getRegion() + " | " + item.getGender() + " | " + item.getType());

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.icon_search)
                .error(R.drawable.icon_search)
                .into(holder.mechinaImage);

        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, MechinaActivity.class);
            AndroidUtil.passMechinaModelAsIntent(intent, item);
            context.startActivity(intent);
        });

    }

    /**
     * Called by RecyclerView to get the number of items in the list.
     *
     * @return The total number of items in {@code displayList}.
     */
    @Override
    public int getItemCount() {
        return displayList.size();
    }

    /**
     * ViewHolder class for academy items.
     *
     * Holds references to the views within the {@code item_mechina} layout.
     */
    static class MechinaViewHolder extends RecyclerView.ViewHolder {
        /** Displays the name of the Mechina. Binds to {@code mechina_name_text}. */
        TextView nameText;
        /** Displays academy details (region, gender, type). Binds to {@code mechina_details_text}. */
        TextView detailsText;
        /** Displays the academy's representative image. Binds to {@code mechina_image_view}. */
        ImageView mechinaImage;

        /**
         * Constructs a new MechinaViewHolder.
         *
         * @param itemView The root view of the item layout.
         */
        public MechinaViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.mechina_name_text);
            detailsText = itemView.findViewById(R.id.mechina_details_text);
            mechinaImage = itemView.findViewById(R.id.mechina_image_view);

            nameText.setTextDirection(View.TEXT_DIRECTION_RTL);
            detailsText.setTextDirection(View.TEXT_DIRECTION_RTL);
        }
    }
}