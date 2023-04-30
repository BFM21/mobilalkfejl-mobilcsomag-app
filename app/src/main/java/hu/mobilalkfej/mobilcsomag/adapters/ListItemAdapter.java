package hu.mobilalkfej.mobilcsomag.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.utils.UIUpdateCallback;
import hu.mobilalkfej.mobilcsomag.viewholders.ListItemViewHolder;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    private ArrayList<MobilePackage> items;
    private Context context;
    private Drawable icon;

    private UIUpdateCallback uiUpdateCallback;
    public ListItemAdapter(Context context, ArrayList<MobilePackage> items, UIUpdateCallback uiUpdateCallback) {
        this.uiUpdateCallback = uiUpdateCallback;
        this.context = context;
        this.items = items;
        this.icon = context.getDrawable(R.drawable.baseline_delete_24);
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_text_list_item, parent, false);


        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        holder.getTitle().setText(items.get(position).getName());
        holder.getDescription().setText(items.get(position).getDescription());
        holder.getIcon().setImageDrawable(icon);
        holder.getIcon().setColorFilter(context.getColor(R.color.red));
        holder.getIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MobilePackageDAOImpl(context).deletePackage(PersistedSettings.getInstance().getCustomPackages().get(holder.getAdapterPosition()), new FirestoreCallback() {
                    @Override
                    public void onCreate(boolean value) {
                        PersistedSettings.getInstance().getCustomPackages().remove(holder.getAdapterPosition());
                        uiUpdateCallback.onCallback();
                    }

                    @Override
                    public void onCallback(ArrayList<MobilePackage> packages) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
