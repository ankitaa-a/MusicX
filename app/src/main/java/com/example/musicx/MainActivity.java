package com.example.musicx;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusic;
    ArrayList<AudioModel> songslist = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        noMusic = findViewById(R.id.nosongstext);

        if(checkPermission() == false){
            requestPermission();
            return;
        }
        String[] projection ={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC +"!=0";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songdata = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songdata.getPath()).exists()){
                songslist.add(songdata);
            }
        }
        if(songslist.size()==0){
            noMusic.setVisibility(View.VISIBLE);

        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songslist,getApplicationContext()));
        }



    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this,READ_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }
    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"PERMISSION REQUIRED",Toast.LENGTH_SHORT);

        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE}, 123);
        }
    }

    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songslist,getApplicationContext()));
        }
    }



}