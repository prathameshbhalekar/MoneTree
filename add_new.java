package com.example.savingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class add_new extends AppCompatActivity {
    Button add;
    EditText item;
    EditText total_amt;
    EditText days;
    TextView priceh;
    TextView itemh;
    TextView targeth;
    TextView s;
    Vibrator myVib;
    EditText query;
    Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        item=findViewById(R.id.item);
        priceh=findViewById(R.id.priceh);
        itemh=findViewById(R.id.itemh);
        s=findViewById(R.id.s);
        targeth=findViewById(R.id.targeth);
        query=findViewById(R.id.query);
        search=findViewById(R.id.search);
        total_amt=findViewById(R.id.amt);
        days=findViewById(R.id.days);
        Intent intent = getIntent();
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                if(total_amt.getText().toString().equals("")||days.getText().toString().equals("")||item.getText().toString().equals("")){
                    targeth.setVisibility(View.INVISIBLE);
                    priceh.setVisibility(View.INVISIBLE);
                    itemh.setVisibility(View.INVISIBLE);
                    if(days.getText().toString().equals(""))     {
                        targeth.setVisibility(View.VISIBLE);
                    }
                    if(item.getText().toString().equals(""))     {
                        itemh.setVisibility(View.VISIBLE);
                    }
                    if(total_amt.getText().toString().equals(""))     {
                        priceh.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    targeth.setVisibility(View.INVISIBLE);
                    priceh.setVisibility(View.INVISIBLE);
                    itemh.setVisibility(View.INVISIBLE);
                    Intent i=new Intent(add_new.this,MainActivity.class);
                    int []array =new int[2];
                    array[0]=Integer.parseInt(total_amt.getText().toString());
                    array[1]=Integer.parseInt(days.getText().toString());
                    i.putExtra("int_data",array);
                    String it=item.getText().toString();
                    i.putExtra("string_data",it);
                    startActivity(i);}
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                s.setVisibility(View.INVISIBLE);
                String search=query.getText().toString();
                if(search.equals("")){
                    s.setVisibility(View.VISIBLE);
                }
                else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.in/s?k="+search)));
                    query.setText("");
                    item.setText(search);
                }
            }
        });
    }



}
