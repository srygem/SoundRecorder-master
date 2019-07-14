package com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class devInfoDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="devInfo.db";
    private static final String TABLE_NAME="devInfo";
    public devInfoDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db=this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"(CAS TEXT,ID INTEGER PRIMARY KEY AUTOINCREMENT,BATTERY TEXT,NETWORK TEXT,CPU TEXT,RAM TEXT,BAND TEXT,STATUS BOOLEAN,TIME TEXT,API TEXT,DIFF TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String cas,Integer battery,String netType,String cpu,String ram,String band,String status,String time,String api,String diff){
    SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("CAS",cas);
        cv.put("BATTERY",battery);
        cv.put("NETWORK",netType);
        cv.put("CPU",cpu);
        cv.put("RAM",ram);
        cv.put("BAND",band);
        cv.put("STATUS",status);
        cv.put("TIME",time);
        cv.put("API",api);
        cv.put("DIFF",diff);
        long result=db.insert(TABLE_NAME,null,cv);
        if (result==-1){
            return false;
        }
        else
            return true;
    }

    public boolean exportDatabase() {
       // DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, "MyCSVFile");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */
                SQLiteDatabase db = this.getReadableDatabase(); //open the database for reading

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */
                Cursor curCSV = db.rawQuery("select * from devInfo", null);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
               // printWriter.println("FIRST TABLE OF THE DATABASE");
               // printWriter.println("BATTERY,NETWORK,CPU,RAM,BAND,STATUS");
                while(curCSV.moveToNext())
                {
                    Integer battery = curCSV.getInt(curCSV.getColumnIndex("BATTERY"));
                    String network = curCSV.getString(curCSV.getColumnIndex("NETWORK"));
                    String cpu = curCSV.getString(curCSV.getColumnIndex("CPU"));
                    String ram = curCSV.getString(curCSV.getColumnIndex("RAM"));
                    String band = curCSV.getString(curCSV.getColumnIndex("BAND"));
                    String status = curCSV.getString(curCSV.getColumnIndex("STATUS"));
                    String time=curCSV.getString(curCSV.getColumnIndex("TIME"));
                    String api=curCSV.getString(curCSV.getColumnIndex("API"));



                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record = status+" 1:"+battery/100+" 2:"+Integer.parseInt(network)/10+" 3:"+Integer.parseInt(cpu)/100+" 4:"+Integer.parseInt(ram)/100+" 5:"+band+" ";
                    printWriter.print(record); //write the record in the .csv file
                }

                curCSV.close();
                db.close();
            }

            catch(Exception exc) {
                //if there are any exceptions, return false
                System.out.println( exc.getMessage());
                return false;
            }
            finally {
                if(printWriter != null) printWriter.close();
            }

            //If there are no errors, return true.
            return true;
        }
    }
}
