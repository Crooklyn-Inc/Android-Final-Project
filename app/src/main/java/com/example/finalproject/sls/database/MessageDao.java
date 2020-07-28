package com.example.finalproject.sls.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.finalproject.sls.data.MessageDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageDao {
    private SQLiteDatabase        database;
    private MessageDatabaseHelper dbHelper;
    private String[]              allColumns = {
        MessageTable.ID,
        MessageTable.BAND,
        MessageTable.SONG,
        MessageTable.LYRICS
    };

    private static final String TAG = MessageDao.class.getSimpleName();

    public MessageDao(Context context) {
        dbHelper = new MessageDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<MessageDTO> findAll() {
        List<MessageDTO> messageList = new ArrayList<>();

        Cursor cursor = database.query(MessageTable.TABLE_NAME,
            allColumns, null, null, null, null, null);

        printCursor(cursor);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MessageDTO message = convertToMessage(cursor);
            messageList.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messageList;
    }

    public MessageDTO findById(long id) {
        Cursor cursor = database.query(MessageTable.TABLE_NAME,
            allColumns, MessageTable.ID + " = " + id, null,
            null, null, null);
        printCursor(cursor);

        MessageDTO message = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            message = convertToMessage(cursor);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return message;
    }

    public MessageDTO create(MessageDTO messageDTO) {
        ContentValues values = new ContentValues();
        values.put(MessageTable.BAND, messageDTO.getBand());
        values.put(MessageTable.SONG, messageDTO.getSong());
        values.put(MessageTable.LYRICS, messageDTO.getLyrics());

        long insertId = database.insert(MessageTable.TABLE_NAME, null, values);

        messageDTO.setId(insertId);
        return messageDTO;
    }

    public void delete(MessageDTO messageDTO) {
        long id = messageDTO.getId();
        database.delete(MessageTable.TABLE_NAME, MessageTable.ID + " = " + id, null);
    }


    private MessageDTO convertToMessage(Cursor cursor) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(cursor.getLong(0));
        messageDTO.setBand(cursor.getString(1));
        messageDTO.setSong(cursor.getString(2));
        messageDTO.setLyrics(cursor.getString(3));
        return messageDTO;
    }

    private void printCursor(Cursor cursor) {

        StringBuilder logMsg = new StringBuilder();

        logMsg.append("DB version:").append(database.getVersion()).append("\n");
        logMsg.append("Columns number:").append(cursor.getColumnCount()).append("\n");
        logMsg.append("Columns names:").append(Arrays.toString(cursor.getColumnNames())).append("\n");
        logMsg.append("Rows number:").append(cursor.getCount()).append("\n");
        logMsg.append("Rows:").append("\n");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            logMsg.append("\t{")
                .append(cursor.getLong(cursor.getColumnIndex(MessageTable.ID))).append(",")
                .append(cursor.getString(cursor.getColumnIndex(MessageTable.BAND))).append(",")
                .append(cursor.getString(cursor.getColumnIndex(MessageTable.SONG))).append(",")
                .append(cursor.getString(cursor.getColumnIndex(MessageTable.LYRICS)))
                .append("}\n");
            cursor.moveToNext();
        }
        cursor.moveToFirst();

        Log.d(TAG, logMsg.toString());
    }
}
