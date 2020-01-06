package com.example.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CaretakersList extends AppCompatActivity {
    SQLiteDatabase myDB;

    List<Caretaker> caretakerList;
    ListView listView;
    CaretakerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caretakers_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Caretakers List");



        listView = (ListView) findViewById(R.id.caretakersListView);
        caretakerList = new ArrayList<>();

        //database
        myDB = openOrCreateDatabase(DatabaseHelper.DATABASE_NAME, MODE_PRIVATE, null);

        loadCareTakersFromDatabase();
    }

    private void loadCareTakersFromDatabase(){

        Cursor cursorCaretakers = myDB.rawQuery("SELECT * FROM caretaker_table", null);

        if (cursorCaretakers.moveToFirst()){
            do{
                caretakerList.add(new Caretaker(
                        cursorCaretakers.getInt(0),
                        cursorCaretakers.getString(1),
                        cursorCaretakers.getString(2)
                ));

            }while (cursorCaretakers.moveToNext());

            adapter = new CaretakerAdapter(this, R.layout.caretakerlist, caretakerList, myDB);

            listView.setAdapter(adapter);
        }else{
            ListView listView = (ListView)findViewById(R.id.caretakersListView);
            TextView emptyTextView = (TextView)findViewById(R.id.emptyView);
            listView.setEmptyView(emptyTextView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
