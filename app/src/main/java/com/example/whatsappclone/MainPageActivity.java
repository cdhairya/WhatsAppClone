package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whatsappclone.Chat.ChatListAdapter;
import com.example.whatsappclone.Chat.ChatObject;
import com.example.whatsappclone.User.UserListAdapter;
import com.example.whatsappclone.User.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    private RecyclerView listViewChat;
    private RecyclerView.Adapter chatListAdapter;
    private RecyclerView.LayoutManager chatListLayoutManager;

    ArrayList<ChatObject> chatList;

    Button btnLogout, btnFindUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        chatList = new ArrayList<>();

        btnLogout = findViewById(R.id.btnLogout);
        btnFindUser = findViewById(R.id.btnFindUser);

        btnFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindUserActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }

    private void getUserChatList(){
        DatabaseReference UserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        UserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                        ChatObject Chat = new ChatObject(childSnapshot.getKey());
                        boolean exists = false;
                        for(ChatObject ChatIterator: chatList){
                            if(ChatIterator.getChatId().equals(Chat.getChatId()))
                                exists = true;
                        }
                        if(exists)
                            continue;
                        chatList.add(Chat);
                        chatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecyclerView() {
        listViewChat = findViewById(R.id.listViewChat);
        listViewChat.setNestedScrollingEnabled(false);
        listViewChat.setHasFixedSize(false);
        chatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        listViewChat.setLayoutManager(chatListLayoutManager);
        chatListAdapter = new ChatListAdapter(chatList);
        listViewChat.setAdapter(chatListAdapter);
    }
    private void getPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},1);
    }
}
