package com.example.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotesDao {

    @Query("select * from notes")
    List<Notes> getAll();

    @Query("SELECT * FROM notes where id LIKE :id")
    Notes findById(String id);

    @Query("SELECT COUNT(*) from notes")
    int countNotes();

    @Insert
    void insert(Notes n);

    @Delete
    void delete(Notes n);
}
