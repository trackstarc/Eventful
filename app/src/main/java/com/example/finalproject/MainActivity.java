package com.example.finalproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button b_newNote; // Joodi
    int icon = R.drawable.icon;
    RecyclerView recyclerView;

    // database things; judy
    private NotesDatabase database;
    private NotesDao dao;
    private List<Notes> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up notification channels for 3 priorities
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("10001", "High Importance", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationChannel = new NotificationChannel("10002", "Normal Importance", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationChannel = new NotificationChannel("10003", "Low Importance", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        database = Room.databaseBuilder(getApplicationContext(),
                NotesDatabase.class,
                "notes").allowMainThreadQueries().build();
        dao = database.dao();

        // Ced's code for creation of actual "note" made up of different attributes collected from database
        recyclerView = findViewById(R.id.recyclerView);

        int count = database.dao().countNotes();
        Notes note;

        List<String> t = new ArrayList<>();
        List<String> d = new ArrayList<>();
        List<String> n = new ArrayList<>();
        List<String> p = new ArrayList<>();

        //judy: adding a single sample note
        t.add("Sample Title");
        d.add("Sample Date");
        n.add("Sample Note");
        p.add("Sample Priority");

        if (count > 0) {
            notesList = database.dao().getAll();

            // loop and add each note element.
            for (int i = 0; i < count; i++) {
                note = notesList.get(i);
                t.add(note.getTitle());
                d.add(note.getDate());
                n.add(note.getNote());
                p.add(note.getPriority());
            }
        }

        // displays note cards inside of a recycler view
        MyAdapter myAdapter = new MyAdapter(this, t.toArray(new String[t.size()]),
                d.toArray(new String[d.size()]),
                n.toArray(new String[n.size()]),
                p.toArray(new String[p.size()]),
                icon);

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Joodi's code
        b_newNote = (Button) findViewById(R.id.newNoteButton); //get id of new note button "+"
        b_newNote.setOnClickListener(view -> addNote());
    }

    public void addNote()
    {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }
}
