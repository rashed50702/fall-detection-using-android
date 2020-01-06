package com.example.fd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class settingContact extends AppCompatActivity {
    DatabaseHelper myDB;

    EditText caretakerName,caretakerNumber;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Contact Number");

        //database
        myDB = new DatabaseHelper(this);

        //caretaker name and phone number input box
        caretakerName = (EditText) findViewById(R.id.caretakerName);
        caretakerNumber = (EditText) findViewById(R.id.caretakerPhone);
        PhoneNumberUtils.formatNumber(caretakerNumber.getText().toString());

        saveBtn = (Button) findViewById(R.id.contactSaveBtn);

        saveCaretaker();
    }

    public void saveCaretaker(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = caretakerName.getText().toString().trim();
                String phone = caretakerNumber.getText().toString().trim();

                if (name.isEmpty()){
                    caretakerName.setError("Name can't be empty");
                    caretakerName.requestFocus();
                    return;
                }

                if (phone.isEmpty()){
                    caretakerNumber.setError("Phone number can't be empty");
                    caretakerNumber.requestFocus();
                    return;
                }

                boolean isInserted = myDB.insertData(name,phone);

                if (isInserted == true){
                    Toast.makeText(settingContact.this, "New caretaker saved", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(settingContact.this, "Oops! something went wrong.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
