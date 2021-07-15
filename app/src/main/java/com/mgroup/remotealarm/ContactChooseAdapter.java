package com.mgroup.remotealarm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactChooseAdapter extends
        RecyclerView.Adapter<ContactChooseAdapter.ViewHolder> implements Filterable {

    Context mContext;
    private List<Contact> contactListFiltered;
    private List<Contact> mContacts;

    public ContactChooseAdapter(List v) {
        mContacts = v;
        this.contactListFiltered = v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = mContacts;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : mContacts) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView mNumberTextVIew;
        public Button wakeButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            mNumberTextVIew = (TextView) itemView.findViewById(R.id.contact_number);
            wakeButton =  (Button) itemView.findViewById(R.id.wake_friend);
        }
    }

    @Override
    public ContactChooseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.contact_item_choose, parent, false);

        // Return a new holder instance
        ContactChooseAdapter.ViewHolder viewHolder = new ContactChooseAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactChooseAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        final Contact contact = contactListFiltered.get(position);

        // Set item views based on your views and data model
        final TextView name = holder.nameTextView;
        name.setText(contact.getName());
        TextView number = holder.mNumberTextVIew;
        number.setText(contact.getNumber());
        final Button wake = holder.wakeButton;
        wake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("remote_alarm","waking up:"+contact.getName()+" with number "+contact.getNumber());
                WakeUpThread wake = new WakeUpThread(mContext, contact.getName(),contact.getNumber());
                wake.start();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }
}
