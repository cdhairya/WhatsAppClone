package com.example.whatsappclone;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");

        Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        initializeRecyclerView();
    }

    private void sendMessage() {
        EditText etMessage = findViewById(R.id.etMessage);
        if(!etMessage.getText().toString().isEmpty()){
            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();
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
