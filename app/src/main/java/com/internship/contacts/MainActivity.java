package com.internship.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<>();
    ContactsAdapter contactsadapter;
    RecyclerView.ItemDecoration divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        checkPermission();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsadapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this
                    ,new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{
            getContactList();
        }
    }

    private void getContactList() {

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //sort in ascending order
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection, new  String[]{id}, null
                );
                if(phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsadapter = new ContactsAdapter(this, arrayList);
        recyclerView.setAdapter(contactsadapter);
        recyclerView.addItemDecoration(divider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0]
        == PackageManager.PERMISSION_GRANTED){
            getContactList();
        }else {
            Toast.makeText(MainActivity.this, "Permission Denied"
            ,Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}