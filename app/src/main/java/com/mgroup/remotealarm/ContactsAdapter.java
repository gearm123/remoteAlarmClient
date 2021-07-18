package com.mgroup.remotealarm;

import android.content.Context;
import android.graphics.Color;
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

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements Filterable {
        Context mContext;
    private List<Contact> contactListFiltered;
    private List<Contact> mContacts;

    public ContactsAdapter(List v) {
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
        public Button mEnableButton;
        public Button mdisableButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            mNumberTextVIew = (TextView) itemView.findViewById(R.id.contact_number);
            mEnableButton = (Button) itemView.findViewById(R.id.enable);
            mdisableButton = (Button) itemView.findViewById(R.id.disable);
        }
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.contact_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        final Contact contact = contactListFiltered.get(position);

        // Set item views based on your views and data model
        final TextView name = holder.nameTextView;
        final  Button enableB = holder.mEnableButton;
        final  Button disableB = holder.mdisableButton;
        name.setText(contact.getName());
        if(Utilities.isFilteredContact(mContext,contact.getNumber())){
            name.setTextColor(Color.GREEN);
            enableB.setVisibility(View.GONE);
            disableB.setVisibility(View.VISIBLE);
        }
        TextView number = holder.mNumberTextVIew;
        number.setText(contact.getNumber());
       enableB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableB.setVisibility(View.GONE);
                disableB.setVisibility(View.VISIBLE);
                name.setTextColor(Color.GREEN);
                Utilities.addContacttoFilterList(mContext,contact.getNumber());
            }
        });

        disableB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableB.setVisibility(View.VISIBLE);
                disableB.setVisibility(View.GONE);
                name.setTextColor(Color.WHITE);
                Utilities.removeFilteredContact(mContext,contact.getNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    }
