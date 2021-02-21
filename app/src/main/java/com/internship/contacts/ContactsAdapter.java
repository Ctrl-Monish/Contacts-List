package com.internship.contacts;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    Activity activity;
    ArrayList<ContactModel> arrayList;
    ArrayList<ContactModel> arrayListAll;
    Context context;


    public ContactsAdapter(Activity activity, ArrayList<ContactModel> arrayList, Context context) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.arrayListAll = new ArrayList<>(arrayList);
        this.context = context;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ContactModel contact = arrayList.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getNumber());
        holder.number2.setText(contact.getSecondNumber());

        if(holder.number2.getText().toString().isEmpty()) {
            holder.number2.setVisibility(View.GONE);
        }else holder.number2.setVisibility(View.VISIBLE);


        holder.number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.name.getText().toString() + "'s number copied", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Number", holder.number.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
            }
        });
        holder.number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.name.getText().toString() + "'s 2nd number copied", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Number", holder.number2.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {

        //runs on a background thread automatically
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ContactModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(arrayListAll);
            }else{
                for(ContactModel contactModel: arrayListAll){
                    if(contactModel.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(contactModel);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //runs on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((Collection<? extends ContactModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, number, number2, number3;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            number = itemView.findViewById(R.id.contact_number);
            number2 = itemView.findViewById(R.id.contact_number2);
        }
    }
}
