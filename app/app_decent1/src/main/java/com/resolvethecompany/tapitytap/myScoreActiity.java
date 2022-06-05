package com.resolvethecompany.tapitytap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class myScoreActiity extends AppCompatActivity {

    // Array of strings...
    ArrayList<String> mobileArray;

    //ArrayAdapter set up
    ArrayAdapter<String> adapter;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score_actiity);

        mobileArray = new ArrayList<String>();
        Collections.reverse(mobileArray);

        adapter = new ArrayAdapter<String>(this,
                R.layout.listitem,
                R.id.scoreItem_TextView,
                mobileArray);

        listView = (ListView) findViewById(R.id.myScores_ListView);
        listView.setAdapter(adapter);

        SharedPreferences uID =
                this.getSharedPreferences("uID",
                        0);

        Log.d("key_test",
                String.valueOf(uID.getInt("key", 0)));

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersID");

        // Read from the database
        myRef.child(String.valueOf(uID.getInt("key", 0)))
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        //String value = dataSnapshot.getValue(String.class);
                        Log.d("snapshot_test_pre", "" + myRef);
                        for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                            String scoreValues = snapShot.getValue(String.class);
                            mobileArray.add(scoreValues);
                            Log.d("snapshot_test_mid", "" +
                                    scoreValues + " " + mobileArray);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d("snapshot_test_passed", "" + mobileArray);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d("final_log_test", "" + mobileArray);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent backToGame = new
                Intent(myScoreActiity.this,
                MainActivity.class);
        startActivity(backToGame);
        return;
    }
}