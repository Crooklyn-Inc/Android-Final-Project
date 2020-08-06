/**
 * This is the copyrighted content for course
 * of mobile programming at Algonquin College
 *
 * @author Olga Zimina
 * @version 1.0.0
 * @created Jul 25, 2020
 */

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

/**
 * Dao Layer
 */
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

    /**
     * Constructor to create current DAO instance
     *
     * @param context current context
     */
    public MessageDao(Context context) {
        dbHelper = new MessageDatabaseHelper(context);
    }

    /**
     * Database connection initializer
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Database connection destructor
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Method allowing to search all records in database
     *
     * @return List of messages of type MessageDTO
     */
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

    /**
     * Return one record from Database based on record ID
     *
     * @param id ID of the record
     *
     * @return MessageDTO one message
     */
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

    /**
     * This method creates a database record and saves it
     *
     * @param messageDTO message to be saved in database
     *
     * @return saved message as MessageDTO
     */
    public MessageDTO create(MessageDTO messageDTO) {
        ContentValues values = new ContentValues();
        values.put(MessageTable.BAND, messageDTO.getBand());
        values.put(MessageTable.SONG, messageDTO.getSong());
        values.put(MessageTable.LYRICS, messageDTO.getLyrics());

        long insertId = database.insert(MessageTable.TABLE_NAME, null, values);

        messageDTO.setId(insertId);
        return messageDTO;
    }

    /**
     * This method deletes a record from database, searched by message ID
     *
     * @param messageDTO message to be deleted
     */
    public void delete(MessageDTO messageDTO) {
        long id = messageDTO.getId();
        database.delete(MessageTable.TABLE_NAME, MessageTable.ID + " = " + id, null);
    }

    /**
     * Converts Cursor message into MessageDTO message to be saved in database
     *
     * @param cursor data to be converted in type of MessageDTO
     *
     * @return message as MessageDTO
     */
    private MessageDTO convertToMessage(Cursor cursor) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(cursor.getLong(0));
        messageDTO.setBand(cursor.getString(1));
        messageDTO.setSong(cursor.getString(2));
        messageDTO.setLyrics(cursor.getString(3));
        return messageDTO;
    }

    /**
     * Print message to log, using logger and message from Cursor
     *
     * @param cursor data to be logged
     */
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
