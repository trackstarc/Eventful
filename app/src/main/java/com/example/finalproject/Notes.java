package com.example.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Notes {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title", index = true)
    public String title;

    @ColumnInfo(name = "note", index = true)
    public String note;

    @ColumnInfo(name = "date", index = true)
    public String date;

    @ColumnInfo(name = "priority", index = true)
    public String priority;

    // get set id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // get set title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // get set note
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // get set date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // get set prio
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
