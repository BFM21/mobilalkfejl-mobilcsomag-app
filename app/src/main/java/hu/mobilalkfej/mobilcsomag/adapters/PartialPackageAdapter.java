package hu.mobilalkfej.mobilcsomag.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.PackageType;
import hu.mobilalkfej.mobilcsomag.models.PartialPackage;
import hu.mobilalkfej.mobilcsomag.viewholders.PartialPackageViewHolder;
import hu.mobilalkfej.mobilcsomag.views.CustomPackageActivity;

public class PartialPackageAdapter extends RecyclerView.Adapter<PartialPackageViewHolder> {

    private  ArrayList<PartialPackage> packages;
    private PackageType type;

    private PartialPackage selectedPackage;
    public static int selectedCount = 0;

    public PartialPackageAdapter(ArrayList<PartialPackage> packages, PackageType type){
        this.packages = packages;
        this.type = type;
        this.selectedPackage = null;
    }

    @NonNull
    @Override
    public PartialPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_package_list_item, parent, false);
        return new PartialPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartialPackageViewHolder holder, int position) {
        holder.getName().setText(packages.get(position).getName());
        holder.getDescription().setText(packages.get(position).getDescription());
        holder.getPrice().setText(packages.get(position).getPrice() + " Ft/" + (packages.get(position).isMonthly() ? "hónap": (type == PackageType.CALL ? "perc" : "üzenet")));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PartialPackage item = packages.get(holder.getAdapterPosition());
                for(int i =0; i < packages.size(); i++){
                    if(packages.get(i).isSelected() && !packages.get(i).equals(item)){
                        selectedCount--;
                        CustomPackageActivity.price -= packages.get(i).getPrice();
                        packages.get(i).setSelected(false);
                        notifyItemChanged(i);
                    }
                }


                item.setSelected(!item.isSelected());
                if(item.isSelected()) {
                    selectedCount++;
                    if(item.isMonthly()) {
                        CustomPackageActivity.price += item.getPrice();
                    }
                }else{
                    selectedCount--;
                    if(item.isMonthly()) {
                        CustomPackageActivity.price -= item.getPrice();
                    }
                }
                CustomPackageActivity.updatePrice();
                notifyItemChanged(holder.getAdapterPosition());

            }
        });
        if(!packages.get(position).isSelected()){
            selectedPackage = null;
            holder.getContainer().setBackground(holder.getOriginalBackground());
        }else{
            selectedPackage = packages.get(position);
            holder.getContainer().setBackgroundColor(Color.MAGENTA);
        }
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }


    public PartialPackage getSelectedPackage() {
        return selectedPackage;
    }
}
