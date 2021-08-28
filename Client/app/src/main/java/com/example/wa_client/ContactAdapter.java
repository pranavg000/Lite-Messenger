package com.example.wa_client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Serializable {

    List<Contact> ContactList;
    Context context;

    public ContactAdapter(Context context,List<Contact> ContactList){
        Log.d(TAG, "ContactAdapter: ");
        this.ContactList=ContactList;
        this.context = context;
    }

    public void addContact(Contact contact){
//        Log.d("waclonedebug", "ContactList: "+ getItemCount());
        ContactList.add(contact);
//        Log.d("waclonedebug", "ContactList: after"+ getItemCount());
//        Log.d("waclonedebug", "addContact: "+ hasObservers());
//        Log.d("waclonedebug", "addContact: "+ (getItemCount()-1));
        notifyItemInserted(getItemCount()-1);
//        Log.d("waclonedebug", "addContact: added"+ (getItemCount()-1));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Contact contact = ContactList.get(position);
//        Log.d("waclonedebug", "onBindViewHolder: "+position);

        holder.clientName.setText(contact.getClientName());
        holder.displayMessage.setText(contact.getDisplayMessage());
        holder.lastSeenTime.setText(contact.getLastSeenTime());
        holder.numberUnseenMessages.setText(String.valueOf(contact.getNumberUnseenMessages()));
    }

    @Override
    public int getItemCount() {
        return ContactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView clientName;
        TextView displayMessage;
        TextView lastSeenTime;
        TextView numberUnseenMessages;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    Contact contact = ContactList.get(getAdapterPosition());
                    args.putString("clientId", contact.getClientId());

                    Navigation.findNavController((MainActivity)context,R.id.navHostFragment).navigate(R.id.action_home2_to_chatFragment,args);
                }
            });
            clientName = itemView.findViewById(R.id.chat_name_txt);
            displayMessage = itemView.findViewById(R.id.chat_description);
            lastSeenTime = itemView.findViewById(R.id.chat_date_txt);
            numberUnseenMessages = itemView.findViewById(R.id.chat_notifs_txt);
        }
    }
}
