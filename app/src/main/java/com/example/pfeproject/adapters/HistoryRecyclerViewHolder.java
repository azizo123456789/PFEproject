package com.example.pfeproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryRecyclerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cmd_price)
    TextView cmd_price;
    @BindView(R.id.cmd_order)
    TextView cmd_order;
    @BindView(R.id.cmd_state)
    ImageView cmd_state;
    @BindView(R.id.cmd_point)
    TextView cmd_point;
    @BindView(R.id.view)
    View view;


    public HistoryRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdateHistoryData(Command cmd, Context ctx) {
        cmd_order.setText("Order #" + cmd.getId());
        this.cmd_price.setText(cmd.getPrice()+" DT");
        switch (cmd.getStatus()) {
            case Types.Confirmed:
                cmd_state.setImageResource(R.drawable.ic_check);
                cmd_state.setColorFilter(ContextCompat.getColor(ctx, R.color.cmd_confirmed),
                        PorterDuff.Mode.SRC_IN);
                view.setBackgroundColor(Color.parseColor("#00af91"));
                break;
            case Types.InProgress:
                cmd_state.setImageResource(R.drawable.ic_clock_or_check);
                cmd_state.setColorFilter(ContextCompat.getColor(ctx, R.color.cmd_in_progress),
                        PorterDuff.Mode.SRC_IN);
                view.setBackgroundColor(Color.parseColor("#5f41e0"));
                break;
            case Types.Cancled:
                cmd_state.setImageResource(R.drawable.ic_close);
                cmd_state.setColorFilter(ContextCompat.getColor(ctx, R.color.cmd_cancled),
                        PorterDuff.Mode.SRC_IN);
                view.setBackgroundColor(Color.parseColor("#f05454"));
                break;

        }

        int totalPoint = new SessionManager(ctx).retrieveCommandTotalPointById(cmd.getId());
        cmd_point.setText(totalPoint == 0 ? "" : String.valueOf(totalPoint)+" points");
    }
}
