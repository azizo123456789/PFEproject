package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Publicity;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PubRecycleViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.pub_label)
    TextView pub_label;
    @BindView(R.id.pub_id)
    TextView pub_id;

    @BindView(R.id.card_pub)
    MaterialCardView cardView;

    public PubRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdatePubLabel(Context ctx, Publicity pub) {
        this.pub_label.setText(ctx.getString(R.string.placeholder_pub) +" "+ pub.getPointToEarn() +" points");
        pub_id.setText(pub.getId());
    }

    public void UpdateCardColor(int color) {
        this.cardView.setCardBackgroundColor(color);
    }

}
