package com.example.songhua.openfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.tv);
        btn.setOnClickListener(new View.OnClickListener() {
             @Override
              public void onClick(View v) {
                                 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                 //intent.setType(“image/*”);//选择图片
                                 //intent.setType(“audio/*”); //选择音频
                                 //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                                 //intent.setType(“video/*;image/*”);//同时选择视频和图片
                                 intent.setType("*/*");//无类型限制
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(intent, 1);
                            }
        });
    }
    String path;
     @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 if (resultCode == Activity.RESULT_OK) {
                         Uri uri = data.getData();
                        if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                                 path = uri.getPath();
                                 tv.setText(path);
                               Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                                return;

                           }
                     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                              path = getPath(this, uri);
                            tv.setText(path);
                         Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
                      } else {//4.4以下下系统调用方法
                            path = getRealPathFromURI(uri);
                            tv.setText(path);
                             Toast.makeText(MainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
                          }
                 }
           }

    public String getRealPathFromURI(Uri contentUri) {
               String res = null;
            String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
               if(null!=cursor&&cursor.moveToFirst()){;
                     int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        res = cursor.getString(column_index);
                     cursor.close();
                               }
           return res;
         }

    /**
     79      * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     80      */
     @SuppressLint("NewApi")
     public String getPath(final Context context, final Uri uri) {

             final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

               // DocumentProvider
              if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                     // ExternalStorageProvider
                      if (isExternalStorageDocument(uri)) {
                              final String docId = DocumentsContract.getDocumentId(uri);
                          final String[] split = docId.split(":");
                           final String type = split[0];

                           if ("primary".equalsIgnoreCase(type)) {
                                     return Environment.getExternalStorageDirectory() + "/" + split[1];
                              }
                      }
                 // DownloadsProvider
                  else if (isDownloadsDocument(uri)) {

                              final String id = DocumentsContract.getDocumentId(uri);
                           final Uri contentUri = ContentUris.withAppendedId(
                                             Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                             return getDataColumn(context, contentUri, null, null);
                       }
                    // MediaProvider
                     else if (isMediaDocument(uri)) {
                          final String docId = DocumentsContract.getDocumentId(uri);
                              final String[] split = docId.split(":");
                             final String type = split[0];

                            Uri contentUri = null;
                             if ("image".equals(type)) {
                                   contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                               } else if ("video".equals(type)) {
                                  contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                               } else if ("audio".equals(type)) {
                                   contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                              }

                          final String selection = "_id=?";
                           final String[] selectionArgs = new String[]{split[1]};

                           return getDataColumn(context, contentUri, selection, selectionArgs);
                      }
                 }
           // MediaStore (and general)
           else if ("content".equalsIgnoreCase(uri.getScheme())) {
                   return getDataColumn(context, uri, null, null);
               }
            // File
          else if ("file".equalsIgnoreCase(uri.getScheme())) {
                      return uri.getPath();
                }
            return null;
         }

/**
 140      * Get the value of the data column for this Uri. This is useful for
 141      * MediaStore Uris, and other file-based ContentProviders.
 142      *
 143      * @param context       The context.
 144      * @param uri           The Uri to query.
 145      * @param selection     (Optional) Filter used in the query.
 146      * @param selectionArgs (Optional) Selection arguments used in the query.
 147      * @return The value of the _data column, which is typically a file path.
 148      */
     public String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

            Cursor cursor = null;
              final String column = "_data";
               final String[] projection = {column};

             try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                                   null);
                   if (cursor != null && cursor.moveToFirst()) {
                             final int column_index = cursor.getColumnIndexOrThrow(column);
                            return cursor.getString(column_index);
                          }
                  } finally {
                      if (cursor != null)
                             cursor.close();
                 }
             return null;
       }


    /**
     171      * @param uri The Uri to check.
     172      * @return Whether the Uri authority is ExternalStorageProvider.
     173      */
    public boolean isExternalStorageDocument(Uri uri) {
          return "com.android.externalstorage.documents".equals(uri.getAuthority());
         }

        /**
 179      * @param uri The Uri to check.
 180      * @return Whether the Uri authority is DownloadsProvider.
 181      */
        public boolean isDownloadsDocument(Uri uri) {
                 return "com.android.providers.downloads.documents".equals(uri.getAuthority());
      }

        /**
 187      * @param uri The Uri to check.
 188      * @return Whether the Uri authority is MediaProvider.
 189      */
            public boolean isMediaDocument(Uri uri) {
               return "com.android.providers.media.documents".equals(uri.getAuthority());
            }

}
