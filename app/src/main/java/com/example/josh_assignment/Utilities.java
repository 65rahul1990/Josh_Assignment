package com.example.josh_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;

/**
 * Created by panxshaz on 1/Aug/16.
 */
public class Utilities {


  ///Deletes it along with all the contents
  private void deleteRecursive(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory())
      for (File child : fileOrDirectory.listFiles())
        deleteRecursive(child);
    fileOrDirectory.delete();
  }
  public static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

//  public static Bitmap blurBitmapDrawable(int drawableId, Context context) {
//    ///This is to get the bitmap
//    ///Refer: http://stackoverflow.com/questions/15255611/how-to-convert-a-drawable-image-from-resources-to-a-bitmap
//    Bitmap bitmap = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), drawableId);
//    return blurBitmap(bitmap, context);
//  }

//  ///Refer: http://stacktips.com/tutorials/android/how-to-create-bitmap-blur-effect-in-android-using-renderscript
//  public static Bitmap blurBitmap(Bitmap bitmap, Context context){
//    //Let's create an empty bitmap with the same size of the bitmap we want to blur
//    Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//    //Instantiate a new Renderscript
//    RenderScript rs = RenderScript.create(context);
//    //Create an Intrinsic Blur Script using the Renderscript
//    ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//    //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
//    Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
//    Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
//    //Set the radius of the blur
//    blurScript.setRadius(25.f);
//    //Perform the Renderscript
//    blurScript.setInput(allIn);
//    blurScript.forEach(allOut);
//    //Copy the final bitmap created by the out Allocation to the outBitmap
//    allOut.copyTo(outBitmap);
//    //recycle the original bitmap
//    bitmap.recycle();
//    //After finishing everything, we destroy the Renderscript.
//    rs.destroy();
//    return outBitmap;
//  }

  public static String saveImage(Bitmap finalBitmap, String filename, Context context) {
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/SkoolSlate/Chats");
    myDir.mkdirs();
    long timeMillis = System.currentTimeMillis();
    String fname = "wallpaper-"+ timeMillis +".jpg";
    File file = new File (myDir, fname);
    if (file.exists ()) file.delete ();
    try {
      FileOutputStream out = new FileOutputStream(file);
      finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
      if (BuildConfig.DEBUG) {
//        MISToast.show(context, "File saved successfully", false);
      }
      String filePath = file.getPath();
      addImageToGallery(file.getPath(),context);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return file.getAbsolutePath();
  }

  public static void addImageToGallery(final String filePath, final Context context) {
    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    values.put(MediaStore.MediaColumns.DATA, filePath);
    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
  }
  //  ======================== Date Utilities =================================
  public static String utcToItc(String date){
    SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    String formattedDate = null;
    utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      Date myDate = utcDateFormat.parse(date);
//      MISLog.printDebug(""+ myDate);
      utcDateFormat.setTimeZone(TimeZone.getDefault());
      formattedDate = utcDateFormat.format(myDate);
      return formattedDate;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return formattedDate;
  }

  public static Date getStartingTimeOfDayForDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    //calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(date.getTime());
    int d = calendar.get(Calendar.DAY_OF_MONTH);
    int m = calendar.get(Calendar.MONTH);
    int y = calendar.get(Calendar.YEAR);
    int s = 0;//calendar.get(Calendar.SECOND);
    int min = 0;//calendar.get(Calendar.MINUTE);
    int h = 0;//calendar.get(Calendar.HOUR_OF_DAY);
    return getDateFromComponents(y, m, d, h, min, s);
  }

  public static Date getNextDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DATE, 1);
    date = c.getTime();
    return date;
  }

  public static Date getDateFromComponents(int y, int m, int d, int h, int min, int s) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, d);
    calendar.set(Calendar.MONTH, m);
    calendar.set(Calendar.YEAR, y);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, s);
    calendar.set(Calendar.MINUTE, min);
    calendar.set(Calendar.HOUR_OF_DAY, h);
    Date date = calendar.getTime();
    return date;
  }
  public enum DateField {
    Year /* = Calendar.YEAR */,
    Month /* = Calendar.MONTH */,
    Day /* = Calendar.DAY_OF_MONTH */
  }
  public static Date getDate(final int year, final int month, final int day) {
    Calendar currentCalender = Calendar.getInstance();
    currentCalender.setTime(new Date());
    currentCalender.set(year, month, day);
    return currentCalender.getTime();
  }
  ///After/Before current date
  public static Date getDateAfter(int value, DateField dateField) {
    Calendar currentCalender = Calendar.getInstance();
    currentCalender.setTime(new Date());
    int field = Calendar.DAY_OF_MONTH;
    switch (dateField) {
      case Year:
        field = Calendar.YEAR; break;
      case Month:
        field = Calendar.MONTH; break;
      case Day:
        field = Calendar.DAY_OF_MONTH; break;
    }
    currentCalender.add(field, value);
    return currentCalender.getTime();
  }
  public static Date stringToDate(String dateStr) {
    if (dateStr == null) {
      if (BuildConfig.DEBUG) {
        return new Date();
      }
      return null;
    }
    Date date = null;
    try {
      DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
      df.setTimeZone(TimeZone.getDefault());
      date = df.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return date;
  }

  public static Date stringToUTCDate(String dateStr) {
    if (dateStr == null) {
//      if (BuildConfig.DEBUG) {
//        return new Date();
//      }
      return null;
    }
    Date date = null;
    try {
      DateFormat df = new SimpleDateFormat("yyyyMMdd");
     // df.setTimeZone(TimeZone.getTimeZone("UTC"));
      date = df.parse(dateStr);
    } catch (ParseException e) {
//      MISLog.printDebug("Could Not parse dateStr : " + dateStr);
      e.printStackTrace();
    }
    return date;
  }


  public static String dateToStringForDisplay(Date date) {
    return dateToUTCString(date, "dd-MMM-yyyy h:mm a");
  }

  public static Boolean isDateToday(Date date) {
    Date startTimeOfDay = getStartingTimeOfDayForDate(date);
    Date startingTimeOfToday = getStartingTimeOfDayForDate(new Date());
    return startTimeOfDay == startingTimeOfToday;
  }

  public static String dateToStringForTimeTable(Date date) {
    return dateToString(date, "h:mm a");
  }

  public static String dateToString(Date date, String format) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setTimeZone(TimeZone.getDefault());
    return sdf.format(date);
  }
  public static String dateToUTCString(Date date, String format) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  ///Provide negative value for days for past dates
  public static Date dateAfterDay(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, days);
    return cal.getTime();
  }

  ///Provide negative value for days for past dates
  public static Date dateAfterToday(int days) {
    return dateAfterDay(new Date(), days);
  }

  public static String listToString(ArrayList<String> list){
    return TextUtils.join(",", list);
  }

  public static List<String> stringToList(String string){
    List<String> items = null;

    if (string != null){
      items = Arrays.asList(string.split("\\s*,\\s*"));
    }
    return items;
  }
  public static void handleRequestFailure(Context context) {
    if (context != null) {
      Toast.makeText(context, R.string.internet_error, Toast.LENGTH_SHORT).show();
    }
  }

  public static boolean handleResponseJsonErrorBody(ResponseBody jsonError, Context context) {
    if (jsonError == null) {
      ///no need to process
      return true;
    }
    String errorMsg = null;
    boolean allGood = true;
    try {
      JSONObject jObjError = new JSONObject(jsonError.string());
      errorMsg = jObjError.getString("message");
    } catch (Exception e) {
      errorMsg = e.getMessage();
      allGood = false;
    } finally {
      if (context != null && errorMsg != null) {
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
      }
    }
    return allGood;
  }

  public static Date atEndOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }

  public static Date atStartOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static float dpFromPx(final Context context, final float px) {
    return px / context.getResources().getDisplayMetrics().density;
  }

  public static float pxFromDp(final Context context, final float dp) {
    return dp * context.getResources().getDisplayMetrics().density;
  }

}
