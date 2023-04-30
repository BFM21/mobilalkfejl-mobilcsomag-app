package hu.mobilalkfej.mobilcsomag.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hu.mobilalkfej.mobilcsomag.R;

public class MobilePackageViewHolder extends RecyclerView.ViewHolder {
    private TextView name;
    private TextView description;

    public MobilePackageViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.titleLabel);
        description = itemView.findViewById(R.id.subtitleLabel);

    }

    public TextView getName() {
        return name;
    }

    public TextView getDescription() {
        return description;
    }
}
