package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    String s1[], s2[], s3[], s4[];
    int icon;
    Context context;

    // note attributes
    public MyAdapter(Context ct, String titles[], String dates[], String notes[], String priorities[], int image) {
        context = ct;
        s1 = titles;
        s2 = dates;
        s3 = notes;
        s4 = priorities;
        icon = image;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // creating the notes code
        holder.myTitle.setText(s1[position]);
        holder.myDate.setText(s2[position]);
        holder.myNote.setText(s3[position]);
        holder.myPriority.setText(s4[position]);
        holder.myIcon.setImageResource(icon);

        // clickable notes code
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClickActivity.class);
                intent.putExtra("Title", s1[position]);
                intent.putExtra("Date", s2[position]);
                intent.putExtra("Note", s3[position]);
                intent.putExtra("Priority", s4[position]);
                intent.putExtra("Icon", icon);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return s1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myTitle, myDate, myNote, myPriority;
        ImageView myIcon;
        ConstraintLayout cardLayout;

        // linkage to XML files
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myTitle = itemView.findViewById(R.id.myTitle);
            myDate = itemView.findViewById(R.id.myDate);
            myNote = itemView.findViewById(R.id.myNote);
            myPriority = itemView.findViewById(R.id.myPriority);
            myIcon = itemView.findViewById(R.id.myIcon);
            cardLayout = itemView.findViewById(R.id.cardLayout);
        }
    }
}
