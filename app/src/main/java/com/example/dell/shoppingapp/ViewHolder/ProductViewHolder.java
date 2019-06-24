package com.example.dell.shoppingapp.ViewHolder;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.shoppingapp.Interface.ItemClickListener;
import com.example.dell.shoppingapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtproductName,txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.product_image);
        txtproductName = itemView.findViewById(R.id.product_name);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        txtProductDescription = itemView.findViewById(R.id.product_description);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
