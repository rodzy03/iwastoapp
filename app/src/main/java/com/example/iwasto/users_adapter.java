package com.example.iwasto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class users_adapter extends RecyclerView.Adapter<users_adapter.AllcurrentHolder>{

    private Context context;
    private List<users_constructor> users_constructor;
    public Integer count = 0;


    public users_adapter(Context context, List<users_constructor> users_constructor)
    {
        this.context = context;
        this.users_constructor = users_constructor;

    }


    @Override
    public AllcurrentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_reliefs_history_templates, null);
        AllcurrentHolder holder = new AllcurrentHolder(view);
        int width = parent.getMeasuredWidth();
        holder.setIsRecyclable(false);
        view.setMinimumWidth(width);
        return holder;
    }
    @Override
    public void onBindViewHolder(final AllcurrentHolder holder, int position) {

        final users_constructor _users_constructor = users_constructor.get(position);

        holder.fullname.setText("Name: "+ _users_constructor.full_name());
        holder.major_area.setText("Major Area : "+ _users_constructor.major_area());
        holder.barangay.setText("Barangay : "+ _users_constructor.barangay());


    }

    Integer rowindex = 0;
    @Override
    public int getItemCount() {
        return users_constructor.size();
    }
    public class AllcurrentHolder extends RecyclerView.ViewHolder{

        TextView fullname, major_area, barangay;

        public AllcurrentHolder(final View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.username);
            major_area = itemView.findViewById(R.id.major_area);
            barangay = itemView.findViewById(R.id.barangay);
        }
    }

}
