package hu.mobilalkfej.mobilcsomag.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.viewholders.MobilePackageViewHolder;
import hu.mobilalkfej.mobilcsomag.views.PackageActivity;
import hu.mobilalkfej.mobilcsomag.views.ProfileActivity;

public class MobilePackageAdapter extends RecyclerView.Adapter<MobilePackageViewHolder> {

    private ArrayList<MobilePackage> packages;
    private Context context;


    public MobilePackageAdapter(Context context, ArrayList<MobilePackage> packages){
        this.packages = packages;
        this.context = context;
    }

    @NonNull
    @Override
    public MobilePackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_list_item, parent, false);


        return new MobilePackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MobilePackageViewHolder holder, int position) {
        holder.getName().setText(packages.get(position).getName());
        holder.getDescription().setText(packages.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PackageActivity.class);
                intent.putExtra("id", packages.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }
}
