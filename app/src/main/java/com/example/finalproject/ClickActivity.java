package com.example.finalproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Note Card Edit Page with pre-filled info
public class ClickActivity extends AppCompatActivity {

    // note properties
    TextView title, note, priority;
    String  s1, s2, s3, s4;

    Button date; // implements a date picker

    // for confirming changes to note or deleting note
    Button change, del;

    private NotesDatabase database;
    private NotesDao dao;

    private Calendar myCalendar = Calendar.getInstance();
    private long delay = 0;
    private Date datein = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);

        title = findViewById(R.id.titleText);
        date = findViewById(R.id.dateButton);
        note = findViewById(R.id.noteText);
        priority = findViewById(R.id.priorityText);

        database = Room.databaseBuilder(getApplicationContext(),
                NotesDatabase.class,
                "notes").allowMainThreadQueries().build();
        dao = database.dao();

        date.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                datein = myCalendar.getTime();
                Date currentTime = Calendar.getInstance().getTime();
                delay = datein.getTime() - currentTime.getTime();

            };

            new DatePickerDialog(
                    ClickActivity.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        getData();
        setData();

        change = findViewById(R.id.editButton);
        change.setOnClickListener(this::editNote);

        del = findViewById(R.id.delButton);
        del.setOnClickListener(this::delNote);
    }



    private void editNote(View v) {
        if (v.getId() == R.id.editButton) {

            String t = title.getText().toString();
            String n = note.getText().toString();
            String d = date.getText().toString();
            String p = priority.getText().toString();
            Notes newNoteObj = new Notes();
            Notes oldNoteObj = new Notes();

            if (t.isEmpty())
                t = "New Note ";

            // gets id of old note from database
            List<Notes> DB = database.dao().getAll();
            int id = -1;
            for (Notes i : DB) {
                if (i.getTitle().equals(s1) &&
                        i.getDate().equals(s2) &&
                        i.getNote().equals(s3) &&
                        i.getPriority().equals(s4))
                    id = i.getId();
            }

            oldNoteObj.setId(id);
            if (id != -1) { database.dao().delete(oldNoteObj); }

            newNoteObj.setTitle(t);
            newNoteObj.setDate(d);
            newNoteObj.setNote(n);
            newNoteObj.setPriority(p);
            database.dao().insert(newNoteObj);

            // gets id of note just inserted from database
            List<Notes> NewDB = database.dao().getAll();
            id = -1;
            for (Notes i : NewDB) {
                if (i.getTitle().equals(t) &&
                        i.getDate().equals(d) &&
                        i.getNote().equals(n) &&
                        i.getPriority().equals(p))
                    id = i.getId();
            }

            String NOTIFICATION_CHANNEL_ID;
            // switch for whatever priority chosen
            try {
                int intpriority = Integer.parseInt(p);
                if (intpriority == 3) {//high
                    NOTIFICATION_CHANNEL_ID = "10001";
                } else if (intpriority == 2) {//norm
                    NOTIFICATION_CHANNEL_ID = "10002";
                } else {//low
                    NOTIFICATION_CHANNEL_ID = "10003";
                }
            } catch (NumberFormatException e) {
                NOTIFICATION_CHANNEL_ID = "10003";
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    ClickActivity.this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(t)
                    .setContentText(n/* + Integer.toString(id)*/)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX) // for old versions
                    .setChannelId(NOTIFICATION_CHANNEL_ID); // for old versions

            scheduleNotification(builder.build(), delay, id);

            Intent main = new Intent(ClickActivity.this, ListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
    }

    private void delNote(View v) {
        if (v.getId() == R.id.delButton) {

            Notes oldNoteObj = new Notes();

            // gets id of old note from database
            List<Notes> DB = database.dao().getAll();
            int id = -1;
            for (Notes i : DB) {
                if (i.getTitle().equals(s1) &&
                        i.getDate().equals(s2) &&
                        i.getNote().equals(s3) &&
                        i.getPriority().equals(s4))
                    id = i.getId();
            }

            oldNoteObj.setId(id);
            if (id != -1) { database.dao().delete(oldNoteObj); }

            // cancels notification
            Intent intent = new Intent(this, NotificationUtil.class);
            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(this, id, intent,
                            PendingIntent.FLAG_NO_CREATE);
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }

            Intent main = new Intent(ClickActivity.this, ListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
    }

    private void scheduleNotification(Notification notification, long delay, int id) {
        Intent notificationIntent = new Intent(this, NotificationUtil.class);
        notificationIntent.putExtra(NotificationUtil.NOTIFICATION_ID, id);//unique id
        notificationIntent.putExtra(NotificationUtil.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    // gets note card info for fill in
    private void getData() {
        if (getIntent().hasExtra("Title") &&
                getIntent().hasExtra("Date") &&
                getIntent().hasExtra("Note") &&
                getIntent().hasExtra("Priority")) {
            s1 = getIntent().getStringExtra("Title");
            s2 = getIntent().getStringExtra("Date");
            s3 = getIntent().getStringExtra("Note");
            s4 = getIntent().getStringExtra("Priority");
        }

        else {
            Toast.makeText(this, "No Data.", Toast.LENGTH_SHORT).show();
        }
    }

    // fills note card info for editing
    private void setData() {
        title.setText(s1);
        date.setText(s2);
        note.setText(s3);
        priority.setText(s4);
    }
}
