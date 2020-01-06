package com.example.fd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CaretakerAdapter extends ArrayAdapter<Caretaker> {

    Context cContext;
    int resourceL;
    List<Caretaker> caretakerList;

    SQLiteDatabase myDB;

    public CaretakerAdapter(@NonNull Context cContext, int resourceL, @NonNull List<Caretaker> caretakerList, SQLiteDatabase myDB) {
        super(cContext, resourceL, caretakerList);

        this.cContext = cContext;
        this.resourceL = resourceL;
        this.caretakerList = caretakerList;
        this.myDB = myDB;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        LayoutInflater inflater = LayoutInflater.from(cContext);

        View view = inflater.inflate(resourceL, null);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);

        final Caretaker caretaker = caretakerList.get(position);

        textViewName.setText(caretaker.getName());
        textViewPhone.setText(caretaker.getPhone());

        Button deleteBtn = view.findViewById(R.id.caretakerDeleteBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(cContext);
//                builder.setTitle("Are you sure to delete!");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String sql = "DELETE FROM caretaker_table WHERE id = ?";
//                        myDB.execSQL(sql, new Integer[]{caretaker.getId()});
//                    }
//                });
                deleteCaretaker(caretaker);


            }
        });
        return view;
    }

    private void deleteCaretaker(final Caretaker caretaker){
        AlertDialog.Builder builder = new AlertDialog.Builder(cContext);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "DELETE FROM caretaker_table WHERE id = ?";
                myDB.execSQL(sql, new Integer[] {caretaker.getId()});
                reloadCaretaker();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialog) {
                Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.rgb(62, 194, 134));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0,0,20,0);

                negButton.setLayoutParams(params);
            }
        });
        alertDialog.show();
    }

    private void reloadCaretaker(){
        Cursor cursorCaretakers = myDB.rawQuery("SELECT * FROM caretaker_table", null);

        if (cursorCaretakers.moveToFirst()){
            caretakerList.clear();
            do{
                caretakerList.add(new Caretaker(
                        cursorCaretakers.getInt(0),
                        cursorCaretakers.getString(1),
                        cursorCaretakers.getString(2)
                ));

            }while (cursorCaretakers.moveToNext());

            notifyDataSetChanged();
        }
    }
}
