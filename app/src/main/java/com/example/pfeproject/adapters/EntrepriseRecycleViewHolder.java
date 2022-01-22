package com.example.pfeproject.adapters;

import android.view.View;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Entreprise;
import com.example.pfeproject.utils.ApiUrl;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EntrepriseRecycleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.soc_name)
    TextView socName;
    @BindView(R.id.soc_img)
    CircleImageView socImg;

    public EntrepriseRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdateEntreprise(Entreprise e){
        socName.setText(e.getName());
        if (e.getImageLink() != null) {
            String replacedImageLink = e.getImageLink().replace(ApiUrl.hostnameHost, ApiUrl.hostname);
            Picasso.get().load(replacedImageLink).placeholder(R.drawable.avatar_icon).into(socImg);
        }
    }
}
