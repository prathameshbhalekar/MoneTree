package com.example.savingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //Initial declaration~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ProgressBar p;
    TextView top;
    TextView amt;
    TextView amt_per_day;
    TextView day;
    ImageView check;
    ImageView hl;
    TextView name;
    Button next;
    Button Afirst;
    Button delete;
    TextView e;
    SharedPreferences prefs = null;
    SharedPreferences date=null;
    Button back;
    FloatingActionButton add;
    Button addMoney;
    TextView getAmt;
    int totalSlides=0;
    int currentSlide=0;
    TextView fill;
    private Vibrator myVib;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_example,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.creds:Toast.makeText(getApplicationContext(),"Prathmesh Bhalekar",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+"prathameshbhalekar13@gmail.com")));
                break;
            case R.id.tips:Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.thebalance.com/how-to-set-and-reach-savings-goals-2386115"));
            startActivity(i);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        //Assigning View~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        delete=findViewById(R.id.delete);
        p = findViewById(R.id.progress_bar);
        top = findViewById(R.id.top);
        amt = findViewById(R.id.amt_left);
        amt_per_day = findViewById(R.id.amt_per_day);
        day = findViewById(R.id.days_left);
        name=findViewById(R.id.name);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        fill=findViewById(R.id.fill);
        check=findViewById(R.id.imageView);
        hl=findViewById(R.id.highlight);
        addMoney=findViewById(R.id.add_button);
        getAmt=findViewById(R.id.add_number);
        back.setEnabled(false);
        check.setVisibility(View.INVISIBLE);
        Afirst=findViewById(R.id.addfirst);
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        Afirst.setVisibility(View.VISIBLE);
        addMoney.setVisibility(View.INVISIBLE);
        fill.setVisibility(View.INVISIBLE);
        getAmt.setVisibility(View.INVISIBLE);
        hl.setVisibility(View.INVISIBLE);

        //Initializing DB~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        db dbHelper = new db(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteDatabase rdb = dbHelper.getReadableDatabase();
        db2 dbHelper2 = new db2(this);

        //Day~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        SQLiteDatabase datedb = dbHelper2.getWritableDatabase();
        SQLiteDatabase daterdb = dbHelper2.getReadableDatabase();
        String[] projection1 = {"date"};
        Date k = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(k);
        Cursor c = daterdb.query("data1", projection1, null, null, null, null, null);
        if(c.getCount()!=0){
            c.moveToLast();
            String lastDate=c.getString(0);
            System.out.println("date is old date "+lastDate);
            String[]old=lastDate.split("-");
            String[]newd=date.split("-");
            int newdate=Integer.parseInt(newd[0]);
            int olddate=Integer.parseInt(old[0]);
            if(newdate!=olddate){
                if(newdate==1){
                    shift(1);
                }
                else{
                shift(newdate-olddate);}
            }
        }
        System.out.println("date is "+date);
        ContentValues v1 = new ContentValues();
        v1.put("date",date);
        long value1=datedb.insert("data1",null,v1);
        System.out.println("value is"+value1);


        //Add Button~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
             int[] data = i.getIntArrayExtra("int_data");
            String item = i.getStringExtra("string_data");
            ContentValues v = new ContentValues();
            if(data!=null){
            v.put("id", Integer.toString(totalSlides));
            totalSlides++;

            v.put("name", item);
            v.put("days", Integer.toString(data[1]));
            v.put("target", Integer.toString(data[0]));
            v.put("completed", "0");
            long value = db.insert("data", null, v);}
            currentSlide=0;
            Toast.makeText(getApplicationContext(),"Make space for your new "+item+"!!",Toast.LENGTH_SHORT).show();

        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                Intent intent = new Intent(MainActivity.this, add_new.class);
                startActivity(intent);
            }
        });
        //First Run~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Intent intent = new Intent(MainActivity.this, add_new.class);
            startActivity(intent);
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        //Displaying Info~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        String[] projection = {"id", "name", "days", "target", "completed"};
        Cursor f = rdb.query("data", projection, null, null, null, null, null);
        f.moveToFirst();

        if(f.isLast()){
            next.setEnabled(false);
        }
        if(f.isFirst()){
            back.setEnabled(false);
        }
        back.setEnabled(false);
        String Name = f.getString(1);
        int days = Integer.parseInt(f.getString(2));
        int target = Integer.parseInt(f.getString(3));
        int completed = Integer.parseInt(f.getString(4));
        addMoney.setEnabled(true);
        amt.setText(Integer.toString(target - completed));
        if(completed>=target){
            addMoney.setEnabled(false);
            amt.setText("-");
        }

        amt_per_day.setText(Integer.toString((target - completed) / days));
        day.setText(Integer.toString(days));
        top.setText(completed + "/" + target);
        p.setProgress(completed*100/target);
        name.setText(Name);
        if(completed>=target){
            check.setVisibility(View.VISIBLE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                db dbHelper = new db(MainActivity.this);
                SQLiteDatabase rdb = dbHelper.getReadableDatabase();
                String[] projection = {"id", "name", "days", "target", "completed"};
                Cursor c = rdb.query("data", projection, null, null, null, null, null);
                c.moveToLast();
                currentSlide++;
                c.moveToPosition(currentSlide);
                String Name = c.getString(1);
                int days = Integer.parseInt(c.getString(2));
                int target = Integer.parseInt(c.getString(3));
                int completed = Integer.parseInt(c.getString(4));
                amt.setText(Integer.toString(target - completed));
                amt_per_day.setText(Integer.toString((target - completed) / days));
                day.setText(Integer.toString(days));
                top.setText(completed + "/" + target);
                name.setText(Name);
                Afirst.setVisibility(View.VISIBLE);
                addMoney.setVisibility(View.INVISIBLE);
                getAmt.setVisibility(View.INVISIBLE);
                fill.setVisibility(View.INVISIBLE);
                hl.setVisibility(View.INVISIBLE);
                check.setVisibility(View.INVISIBLE);
                if(completed>=target){
                    check.setVisibility(View.VISIBLE);
                    amt.setText("-");
                }
                p.setProgress(completed*100/target);
                addMoney.setEnabled(true);
                if(completed>=target){
                    addMoney.setEnabled(false);
                }
                back.setEnabled(true);
                if(c.isLast()){
                    next.setEnabled(false);
                }


            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                db dbHelper = new db(MainActivity.this);
                SQLiteDatabase rdb = dbHelper.getReadableDatabase();
                String[] projection = {"id", "name", "days", "target", "completed"};
                Cursor c = rdb.query("data", projection, null, null, null, null, null);
                c.moveToLast();
                currentSlide--;
                c.moveToPosition(currentSlide);
                String Name = c.getString(1);
                int days = Integer.parseInt(c.getString(2));
                int target = Integer.parseInt(c.getString(3));
                int completed = Integer.parseInt(c.getString(4));
                amt.setText(Integer.toString(target - completed));
                amt_per_day.setText(Integer.toString((target - completed) / days));
                day.setText(Integer.toString(days));
                top.setText(completed + "/" + target);
                next.setEnabled(true);
                Afirst.setVisibility(View.VISIBLE);
                addMoney.setVisibility(View.INVISIBLE);
                getAmt.setVisibility(View.INVISIBLE);
                hl.setVisibility(View.INVISIBLE);
                fill.setVisibility(View.INVISIBLE);
                addMoney.setEnabled(true);
                name.setText(Name);
                check.setVisibility(View.INVISIBLE);
                if(completed>=target){
                    check.setVisibility(View.VISIBLE);
                    amt.setText("-");
                }
                p.setProgress(completed*100/target);
                if(completed>=target){
                    addMoney.setEnabled(false);
                }
                if(c.isFirst()){
                    back.setEnabled(false);
                }

            }
        });
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                if(getAmt.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(),"Please Enter Amount!",Toast.LENGTH_SHORT).show();
                    fill.setVisibility(View.VISIBLE);
                    myVib.vibrate(50);

                }
                else{db dbHelper = new db(MainActivity.this);
                SQLiteDatabase rdb = dbHelper.getReadableDatabase();
                fill.setVisibility(View.INVISIBLE);
                String[] projection = {"id", "name", "days", "target", "completed"};
                Cursor c = rdb.query("data", projection, null, null, null, null, null);
                c.moveToPosition(currentSlide);
                String name=c.getString(1);
                int completed=Integer.parseInt(c.getString(4));
                completed=completed+Integer.parseInt(getAmt.getText().toString());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String[] u={name};
                int target=Integer.parseInt(c.getString(3));
                int days=Integer.parseInt(c.getString(2));
                ContentValues cv=new ContentValues();
                cv.put("completed",completed);
                db.update("data",cv,"name=?",u);
                amt.setText(Integer.toString(target - completed));
                amt_per_day.setText(Integer.toString((target - completed) / days));
                day.setText(Integer.toString(days));
                top.setText(completed + "/" + target);
                p.setProgress(completed*100/target);
                getAmt.setText("");
                check.setVisibility(View.INVISIBLE);
                if(completed>=target){
                    check.setVisibility(View.VISIBLE);
                    amt.setText("-");
                    myVib.vibrate(2000);

                }
                addMoney.setEnabled(true);
                if(completed>=target){
                    addMoney.setEnabled(false);
                }
                Afirst.setVisibility(View.VISIBLE);
                addMoney.setVisibility(View.INVISIBLE);
                getAmt.setVisibility(View.INVISIBLE);
                hl.setVisibility(View.INVISIBLE);}

            }
        });
        Afirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(50);
                Afirst.setVisibility(View.INVISIBLE);
                addMoney.setVisibility(View.VISIBLE);
                getAmt.setVisibility(View.VISIBLE);
                hl.setVisibility(View.VISIBLE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        }
        void shift(int days){
            db dbHelper = new db(MainActivity.this);
            SQLiteDatabase rdb = dbHelper.getReadableDatabase();
            String[] projection = {"id", "name", "days", "target", "completed"};
            Cursor c = rdb.query("data", projection, null, null, null, null, null);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            c.moveToFirst();
            while(!c.isLast()){
                int date=Integer.parseInt(c.getString(2));
                ContentValues v=new ContentValues();
                int target=Integer.parseInt(c.getString(3));
                int completed=Integer.parseInt(c.getString(3));
                if(date>0&&completed<target){
                    date=date-days;
                }
                String name=c.getString(1);
                v.put("days",date);
                String[]s={name};
                db.update("data",v,"name=?",s);
                c.moveToNext();
            }
            int date=Integer.parseInt(c.getString(2));
            ContentValues v=new ContentValues();
            date=date-days;
            String name=c.getString(1);
            v.put("days",date);
            String[]s={name};
            db.update("data",v,"name=?",s);
//            onCreate(null);
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);

        }
        void delete(){
            String s=name.getText().toString();
            db dbHelper = new db(MainActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String f[]= {s};
            db.delete("data","name=?",f);
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);


        }







    }



