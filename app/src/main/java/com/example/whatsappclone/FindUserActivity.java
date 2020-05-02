package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView userList;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager userListLayoutManager;

    ArrayList<UserObject> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        users = new ArrayList<>();

        initializeRecyclerView();
        getContactList();
    }

    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            UserObject contact = new UserObject(name, phone);
            users.add(contact);
            userListAdapter.notifyDataSetChanged();
        }
    }

    private void initializeRecyclerView() {
        userList = findViewById(R.id.userList);
        userList.setNestedScrollingEnabled(false);
        userList.setHasFixedSize(false);
        userListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        userList.setLayoutManager(userListLayoutManager);
        userListAdapter = new UserListAdapter(users);
        userList.setAdapter(userListAdapter);
    }
}
