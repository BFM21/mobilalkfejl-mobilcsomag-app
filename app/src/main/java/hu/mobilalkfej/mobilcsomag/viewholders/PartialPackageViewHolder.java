package hu.mobilalkfej.mobilcsomag.viewholders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import hu.mobilalkfej.mobilcsomag.R;

public class PartialPackageViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout container;
    private TextView name;
    private TextView description;
    private TextView price;

    Drawable originalBackground;
    public PartialPackageViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.titleLabel);
        description = itemView.findViewById(R.id.subtitleLabel);
        price = itemView.findViewById(R.id.priceLabel);
        container = itemView.findViewById(R.id.container);
        originalBackground = itemView.getBackground();
    }

    public TextView getName() {
        return name;
    }

    public TextView getDescription() {
        return description;
    }

    public TextView getPrice() {
        return price;
    }

    public ConstraintLayout getContainer() {
        return container;
    }

    public Drawable getOriginalBackground() {
        return originalBackground;
    }
}
