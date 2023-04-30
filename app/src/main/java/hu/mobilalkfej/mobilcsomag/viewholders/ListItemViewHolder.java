package hu.mobilalkfej.mobilcsomag.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hu.mobilalkfej.mobilcsomag.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView description;
    private ImageView icon;
    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.menuItemTitle);
        description = itemView.findViewById(R.id.menuItemSubtitle);
        icon = itemView.findViewById(R.id.menuItemIcon);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }
}
