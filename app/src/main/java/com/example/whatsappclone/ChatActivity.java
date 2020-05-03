package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.whatsappclone.Chat.ChatObject;
import com.example.whatsappclone.Chat.MessageAdapter;
import com.example.whatsappclone.Chat.MessageObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMsg;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;

    ArrayList<MessageObject> messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeRecyclerView();
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
