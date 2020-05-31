package com.example.submarinehunter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.ViewHolder> {

    private List<Record> list;

    public AdapterRecord(List<Record> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record r = list.get(position);

        Timestamp ts = new Timestamp(r.tsm);
        Date date = new Date(ts.getTime());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");

        holder.n.setText((position + 1) + "");
        holder.result.setText(r.result + "");
        holder.time.setText(dateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView n;
        TextView time;
        TextView result;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            n = itemView.findViewById(R.id.record_n);
            result = itemView.findViewById(R.id.record_result);
            time = itemView.findViewById(R.id.record_time);
        }
    }
}
