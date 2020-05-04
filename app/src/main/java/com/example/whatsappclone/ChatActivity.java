package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whatsappclone.Chat.ChatObject;
import com.example.whatsappclone.Chat.MessageAdapter;
import com.example.whatsappclone.Chat.MessageObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMsg;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;

    ArrayList<MessageObject> messageList;
    String chatID;
    DatabaseReference chatDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");

        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        initializeRecyclerView();
        getChatMessages();
    }

    private void getChatMessages() {
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String text = "", creatorID = "";
                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();

                    MessageObject myMessage = new MessageObject(dataSnapshot.getKey(),creatorID,text);
                    messageList.add(myMessage);
                    chatLayoutManager.scrollToPosition(messageList.size() - 1);
                    chatAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        EditText etMessage = findViewById(R.id.etMessage);
        if(!etMessage.getText().toString().isEmpty()){
            DatabaseReference newMessageDB = chatDB.push();
            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text",etMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            newMessageDB.updateChildren(newMessageMap);
        }
        etMessage.setText(null);
    }

    private void initializeRecyclerView() {
        messageList = new ArrayList<>();
        recyclerViewMsg = findViewById(R.id.recyclerViewMsg);
        recyclerViewMsg.setNestedScrollingEnabled(false);
        recyclerViewMsg.setHasFixedSize(false);
        chatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerViewMsg.setLayoutManager(chatLayoutManager);
        chatAdapter = new MessageAdapter(messageList);
        recyclerViewMsg.setAdapter(chatAdapter);
    }
}
