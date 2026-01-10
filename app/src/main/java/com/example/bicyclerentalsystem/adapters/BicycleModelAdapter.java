package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.BicycleModel;

import java.util.ArrayList;
import java.util.List;

public class BicycleModelAdapter extends RecyclerView.Adapter<BicycleModelAdapter.ViewHolder> {

    private List<BicycleModel> bicycleModels;

    public BicycleModelAdapter() {
        this.bicycleModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bicycle_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BicycleModel model = bicycleModels.get(position);
        holder.textModel.setText(model.getModel());
        holder.textType.setText(model.getType());
        holder.textCount.setText(String.valueOf(model.getCount()));
    }

    @Override
    public int getItemCount() {
        return bicycleModels.size();
    }

    public void setBicycleModels(List<BicycleModel> bicycleModels) {
        this.bicycleModels = bicycleModels;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textModel;
        TextView textType;
        TextView textCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textModel = itemView.findViewById(R.id.text_model);
            textType = itemView.findViewById(R.id.text_type);
            textCount = itemView.findViewById(R.id.text_count);
        }
    }
}
