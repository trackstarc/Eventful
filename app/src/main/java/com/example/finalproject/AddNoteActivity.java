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
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    private Button addBtn, dateBtn;
    private EditText titleET, noteET, priorityET;
    private Calendar myCalendar = Calendar.getInstance();
    private long delay = 0;
    private Date datein = Calendar.getInstance().getTime();

    /******   judy adding database connection (input)      ******/
    private NotesDatabase database;
    private NotesDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTitle("EventFul Add Note");

        database = Room.databaseBuilder(getApplicationContext(),
                NotesDatabase.class,
                "notes").allowMainThreadQueries().build();
        dao = database.dao();

        titleET = findViewById(R.id.titleEditText);
        noteET = findViewById(R.id.noteEditText);
        priorityET = findViewById(R.id.numberEditText);
        addBtn = findViewById(R.id.addRecord);
        dateBtn = findViewById(R.id.dateButton);

        dateBtn.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                datein = myCalendar.getTime();
                Date currentTime = Calendar.getInstance().getTime();
                delay = datein.getTime() - currentTime.getTime();

            };

            new DatePickerDialog(
                    AddNoteActivity.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        addBtn.setOnClickListener(this::addClick);

    }

   public void addClick(View v) {
       if (v.getId() == R.id.addRecord) {
           String title = titleET.getText().toString();
           String note = noteET.getText().toString();
           String myFormat = "MM/dd/yy";
           SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
           String date = sdf.format(datein);
           String priority = priorityET.getText().toString();

           Notes newNoteObj = new Notes();

           if (title.isEmpty())
               title = "New Note ";

           newNoteObj.setTitle(title);
           newNoteObj.setDate(date);
           newNoteObj.setNote(note);
           newNoteObj.setPriority(priority);

           database.dao().insert(newNoteObj);

           // gets id of note just inserted from database
           List<Notes> DB = database.dao().getAll();
           int id = -1;
           for(Notes i:DB){
               if(i.getTitle().equals(title) &&
               i.getDate().equals(date) &&
               i.getNote().equals(note) &&
               i.getPriority().equals(priority))
                   id = i.getId();
           }

           String NOTIFICATION_CHANNEL_ID;
           // switch for whatever priority chosen
           try {
               int intpriority = Integer.parseInt(priority);
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
                   AddNoteActivity.this, NOTIFICATION_CHANNEL_ID)
                   .setContentTitle(title)
                   .setContentText(note/* + Integer.toString(id)*/)
                   .setSmallIcon(R.drawable.ic_launcher_foreground)
                   .setAutoCancel(true)
                   .setPriority(NotificationCompat.PRIORITY_MAX) // for old versions
                   .setChannelId(NOTIFICATION_CHANNEL_ID);// for old versions

           scheduleNotification(builder.build(), delay, id);


           Intent main = new Intent(AddNoteActivity.this, ListActivity.class)
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
}
