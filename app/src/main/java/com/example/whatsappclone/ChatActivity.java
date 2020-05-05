package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whatsappclone.Chat.ChatObject;
import com.example.whatsappclone.Chat.MediaAdapter;
import com.example.whatsappclone.Chat.MessageAdapter;
import com.example.whatsappclone.Chat.MessageObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMsg, recyclerViewMedia;
    private RecyclerView.Adapter chatAdapter, mediaAdapter;
    private RecyclerView.LayoutManager chatLayoutManager, mediaLayoutManager;

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
        Button addMedia = findViewById(R.id.addMedia);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }

    private void getChatMessages() {
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String text = "", creatorID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();
                    if(dataSnapshot.child("media").getChildrenCount() > 0)
                        for(DataSnapshot mediaSnapshot: dataSnapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapshot.getValue().toString());

                    MessageObject myMessage = new MessageObject(dataSnapshot.getKey(),creatorID,text, mediaUrlList);
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

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText etMessage;

    private void sendMessage() {
        etMessage = findViewById(R.id.etMessage);
            String messageId = chatDB.push().getKey();
            final DatabaseReference newMessageDB = chatDB.child(messageId);
            final Map newMessageMap = new HashMap<>();
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            if(!etMessage.getText().toString().isEmpty())
                newMessageMap.put("text",etMessage.getText().toString());


            if(!mediaUriList.isEmpty()){
                for(String mediaUri : mediaUriList){
                    String mediaId = newMessageDB.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(messageId).child(mediaId);
                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                    totalMediaUploaded++;
                                    if(totalMediaUploaded == mediaUriList.size())
                                        updateDatabaseWithNewMessage(newMessageDB, newMessageMap);
                                }
                            });
                        }
                    });
                }
            }
            else {
                if(!etMessage.getText().toString().isEmpty()){
                    updateDatabaseWithNewMessage(newMessageDB, newMessageMap);
                }
            }
    }

    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        etMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        mediaAdapter.notifyDataSetChanged();
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        recyclerViewMsg = findViewById(R.id.recyclerViewMsg);
        recyclerViewMsg.setNestedScrollingEnabled(false);
        recyclerViewMsg.setHasFixedSize(false);
        chatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerViewMsg.setLayoutManager(chatLayoutManager);
        chatAdapter = new MessageAdapter(messageList);
        recyclerViewMsg.setAdapter(chatAdapter);
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        recyclerViewMedia = findViewById(R.id.recyclerViewMedia);
        recyclerViewMedia.setNestedScrollingEnabled(false);
        recyclerViewMedia.setHasFixedSize(false);
        mediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerViewMedia.setLayoutManager(mediaLayoutManager);
        mediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        recyclerViewMedia.setAdapter(mediaAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture(s)"),PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null) mediaUriList.add(data.getData().toString());
                else {
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }
}
