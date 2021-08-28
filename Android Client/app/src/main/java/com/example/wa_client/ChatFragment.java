package com.example.wa_client;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "clientId";
    private Toolbar appBar;
    private Button sendButton;
    private EditText chatbox;
    private RecyclerView recyclerView;
    private Contact contact;

    // TODO: Rename and change types of parameters
    private String clientId;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String clientId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, clientId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clientId = getArguments().getString(ARG_PARAM1);
            contact = ((MainActivity)getActivity()).getContact(clientId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: chatFragment");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        appBar = view.findViewById(R.id.toolbar2);
        appBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(),R.id.navHostFragment).navigateUp();
            }
        });

        recyclerView = view.findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ((MainActivity)getActivity()).setRecyclerViewChatFragment(recyclerView,clientId);

        chatbox = view.findViewById(R.id.edittext_chatbox);

        sendButton = view.findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called");
                ((MainActivity)getActivity()).sendMessage(clientId,chatbox.getText().toString(),recyclerView);
                contact.setDisplayMessage(chatbox.getText().toString());
                chatbox.getText().clear();
                recyclerView.scrollToPosition(((MainActivity)getActivity()).clientIdToMessageListAdapter.get(clientId).getItemCount()-1);
            }
        });

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "onLayoutChange:"+top+" "+bottom+" "+oldTop+" "+oldBottom);
                if(oldBottom!=bottom||oldTop!=top)
                    recyclerView.scrollToPosition(((MainActivity)getActivity()).clientIdToMessageListAdapter.get(clientId).getItemCount()-1);
            }
        });

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "onLayoutChange:"+top+" "+bottom+" "+oldTop+" "+oldBottom);
                if(oldBottom!=bottom||oldTop!=top)
                    recyclerView.scrollToPosition(((MainActivity)getActivity()).clientIdToMessageListAdapter.get(clientId).getItemCount()-1);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).currentChat = clientId;
        if(contact.getNumberUnseenMessages() > 0){
            ArrayList<Message> messages = ((MainActivity)getActivity()).clientIdToMessages.get(clientId);
            long latestMessageTimestamp = messages.get(messages.size()-1).getTimeStamp();
            // Sending read receipt
            ((MainActivity)getActivity()).mService.sendMessageService.submit(new SendRequestTask(Request.RequestType.MessageRead, contact.getClientId(), String.valueOf(latestMessageTimestamp), ((MainActivity)getActivity()).currentClientId));
            contact.setNumberUnseenMessages(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).currentChat = null;
    }
}