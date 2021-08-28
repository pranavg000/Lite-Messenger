package com.example.wa_client;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.ContentValues.TAG;

public class ChatList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FloatingActionButton newChat;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatList() {
        // Required empty public constructor
    }

    public static ChatList newInstance(String param1, String param2) {
        ChatList fragment = new ChatList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate: args");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: "+ savedInstanceState);
            return view;
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ((MainActivity)getActivity()).setRecyclerviewChatList(recyclerView);
        newChat = view.findViewById(R.id.addNewChat);
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_newchat, null);
                builder.setView(dialogView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String newChatClientName = ((EditText)(dialogView.findViewById(R.id.clientName))).getText().toString();
                                String newChatClientId = ((EditText)(dialogView.findViewById(R.id.clientId))).getText().toString();
                                ((MainActivity)getActivity()).addTempContact(newChatClientId, newChatClientName);
//                              GlobalVariables.mainActivity.addNewContact(new Contact(newChatClientName,newChatClientId));
                                ((MainActivity)getActivity()).sendNewChatRequest(newChatClientId);
//                                GlobalVariables.sendMessageService.submit(new SendRequestTask(Request.RequestType.NewChat, newChatClientId, ""));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        Log.d(TAG, "onCreateView: chatlist");
        return view;
    }


}