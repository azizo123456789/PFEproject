package com.example.pfeproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserPointRecyclerViewHolder extends RecyclerView.ViewHolder {
//    @BindView(R.id.point_user)
//    TextView user_point;
//    @BindView(R.id.point_soc_name)
//    TextView soc_name;

    @BindView(R.id.soc_img)
    CircleImageView soc_img;
    @BindView(R.id.soc_name)
    TextView soc_name;
    @BindView(R.id.user_soc_point)
    TextView soc_user_point;


    public UserPointRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdateUserPoint(TotalPoint model, Context ctx) {
        try {
            TotalPoint point = new SessionManager(ctx).retrieveUserEntreprisePoint(model.getIdEntreprise());
            int userRestPointPerEntr = 0;
            if (point != null)
                userRestPointPerEntr = point.getRestpoints();
            soc_user_point.setText(userRestPointPerEntr + " points");
            soc_name.setText(model.getEntreprise().getName());
            if (model.getEntreprise().getImageLink() != null) {
                String replacedImageLink = model.getEntreprise().getImageLink().replace(ApiUrl.hostnameHost, ApiUrl.hostname);
                Picasso.get().load(replacedImageLink).placeholder(R.drawable.avatar_company).into(soc_img);
            }
        } catch (Exception ex) {
            Log.e("TAG", "UpdateUserPoint: Exception = " + ex.getMessage());
        }

    }
}
