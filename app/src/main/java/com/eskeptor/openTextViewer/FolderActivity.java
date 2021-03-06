package com.eskeptor.openTextViewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;
import com.eskeptor.openTextViewer.datatype.FolderObject;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import util.TestLog;

/*
 * Created by eskeptor on 17. 2. 2.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

/**
 * Folder Activity Class
 */
public class FolderActivity extends AppCompatActivity
{
    private ArrayList<FolderObject> mFolders;           // ArrarCist to list folders
    private FolderAdaptor mFolderAdaptor;               // Adapter to be used for arra yList to list folders
    private View mContextView;
    private Context mContextThis;                       // Context
    private EditText mEditText;                         // EditText for adding names when creating folders
    private int mFoldersLength;                         // Number of folders
    private String mNewFolderName;                      // New Folder Name
    private ListView mFolderList;                       // Folder List

    private RefreshList mHandler;                       // Handler for refreshing folder lists
    private Thread mListThread;

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_folderAdd) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.folder_dialog_title_create);
            View layout = LayoutInflater.from(mContextThis).inflate(R.layout.dialog_folder_create, null);
            mEditText = layout.findViewById(R.id.dialog_folder_input);
            builder.setView(layout);
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE) {
                        mNewFolderName = mEditText.getText().toString();
                        if (!mNewFolderName.equals("")) {
                            File file = new File(Constant.APP_INTERNAL_URL + File.separator + mNewFolderName);
                            if (file.exists()) {
                                Snackbar.make(mContextView, R.string.folder_dialog_toast_exist, Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                if (file.mkdir()) {
                                    if (mListThread != null) {
                                        mListThread.interrupt();
                                    }
                                    mListThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            File file = new File(Constant.APP_INTERNAL_URL);
                                            File files[] = file.listFiles(new FileFilter() {
                                                @Override
                                                public boolean accept(File pathname) {
                                                    return pathname.isDirectory();
                                                }
                                            });
                                            mFoldersLength = files.length;
                                            mFolders.clear();

                                            // General folder connection
                                            for (int i = 0; i < mFoldersLength; i++) {
                                                mFolders.add(new FolderObject(files[i].getName(), files[i].listFiles(new FileFilter() {
                                                    @Override
                                                    public boolean accept(File _pathname) {
                                                        return _pathname.isFile() && (_pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION)
                                                                || _pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION));
                                                    }
                                                }).length, checkFolderType(files[i]), mContextThis));
                                            }

                                            // File browser connection
                                            mFolders.add(new FolderObject(getResources().getString(R.string.folder_externalBrowser), -1, FolderObject.FolderType.External, null));
                                            mHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_LIST);
                                        }
                                    });
                                    mListThread.start();
                                }
                            }
                        }
                    }
                    dialog.dismiss();
                }
            };
            builder.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
            builder.setPositiveButton(R.string.folder_dialog_button_create, clickListener);

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        mContextThis = getApplicationContext();
        mContextView = findViewById(R.id.activity_folder);

        setTitle(R.string.folder_title);
        mFolderList = findViewById(R.id.folder_list);
        mFolders = new ArrayList<>();

        mHandler = new RefreshList(this);
        mListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Constant.APP_INTERNAL_URL);
                File files[] = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                mFoldersLength = files.length;

                mFolders.clear();
                for (int i = 0; i < mFoldersLength; i++) {
                    mFolders.add(new FolderObject(files[i].getName(), files[i].listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isFile() && (pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION)
                                    || pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION));
                        }
                    }).length, checkFolderType(files[i]), mContextThis));
                }

                // 파일 브라우저 연결
                mFolders.add(new FolderObject(getResources().getString(R.string.folder_externalBrowser), -1, FolderObject.FolderType.External, null));
                mHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_LIST);
            }
        });
        mListThread.start();

        SharedPreferences sharedPref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        int font = sharedPref.getInt(Constant.APP_FONT, Constant.FontType.Default.getValue());
        if (font == Constant.FontType.BaeDal_JUA.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"))
                    .addBold(Typekit.createFromAsset(mContextThis, "fonts/bmjua.ttf"));
        } else if (font == Constant.FontType.KOPUB_Dotum.getValue()) {
            Typekit.getInstance().addNormal(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"))
                    .addBold(Typekit.createFromAsset(mContextThis, "fonts/kopub_dotum_medium.ttf"));
        } else {
            Typekit.getInstance().addNormal(Typeface.DEFAULT).addBold(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFolderList = null;
        mFolderAdaptor = null;
        if (!mFolders.isEmpty()) {
            mFolders.clear();
        }
        mFolders = null;
        mContextThis = null;
        mEditText = null;
        mNewFolderName = null;
        mContextView = null;
        mHandler = null;
        if (mListThread != null) {
            mListThread.interrupt();
        }
        mListThread = null;
    }

    /**
     * Check the type of folder
     * @param file Folder
     * @return Folder Type
     */
    private FolderObject.FolderType checkFolderType(final File file) {
        if (file.getName().equals(Constant.FOLDER_DEFAULT_NAME) || file.getName().equals(Constant.FOLDER_WIDGET_NAME)) {
            return FolderObject.FolderType.Default;
        }
        return FolderObject.FolderType.Custom;
    }

    /**
     * How to delete folders
     * @param index Index of folders to delete
     */
    private void deleteFolder(final int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.folder_dialog_title_delete);
        dialog.setMessage(R.string.folder_dialog_message_question_delete);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    File file = new File(mFolders.get(index).mFolderPath);
                    if (file.exists()) {
                        if (!file.getName().equals(Constant.FOLDER_DEFAULT_NAME) && !file.getName().equals(Constant.FOLDER_WIDGET_NAME)) {
                            for (File inFile : file.listFiles()) {
                               if (inFile.delete()) {
                                   TestLog.Tag("Folder").Logging(TestLog.LogType.ERROR, inFile.getName() + " 제거완료");
                               } else {
                                   TestLog.Tag("Folder").Logging(TestLog.LogType.ERROR, inFile.getName() + " 제거실패");
                               }
                            }
                            if (file.delete()) {
                                Snackbar.make(mContextView, R.string.folder_dialog_toast_delete, Snackbar.LENGTH_LONG).show();
                                if (mListThread != null) {
                                    mListThread.interrupt();
                                }
                                mListThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        File file = new File(Constant.APP_INTERNAL_URL);
                                        File files[] = file.listFiles(new FileFilter() {
                                            @Override
                                            public boolean accept(File pathname) {
                                                return pathname.isDirectory();
                                            }
                                        });
                                        mFoldersLength = files.length;

                                        mFolders.clear();
                                        for (int i = 0; i < mFoldersLength; i++) {
                                            mFolders.add(new FolderObject(files[i].getName(), files[i].listFiles(new FileFilter() {
                                                @Override
                                                public boolean accept(File pathname) {
                                                    return pathname.isFile() && (pathname.getName().endsWith(Constant.FILE_TEXT_EXTENSION)
                                                            || pathname.getName().endsWith(Constant.FILE_IMAGE_EXTENSION));
                                                }
                                            }).length, checkFolderType(files[i]), mContextThis));
                                        }

                                        // 파일 브라우저 연결
                                        mFolders.add(new FolderObject(getResources().getString(R.string.folder_externalBrowser), -1, FolderObject.FolderType.External, null));
                                        mHandler.sendEmptyMessage(Constant.HANDLER_REFRESH_LIST);
                                    }
                                });
                                mListThread.start();
                            }
                        } else {
                            Snackbar.make(mContextView, R.string.folder_toast_remove_defaultfolder, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(mContextView, R.string.error_folder_not_exist, Snackbar.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        };
        dialog.setNegativeButton(R.string.folder_dialog_button_cancel, clickListener);
        dialog.setPositiveButton(R.string.folder_dialog_button_delete, clickListener);
        dialog.show();
    }

    private void handleMessage(Message message) {
        int what = message.what;
        switch (what) {
            case Constant.HANDLER_REFRESH_LIST: {
                if (mFolderAdaptor == null) {
                    mFolderAdaptor = new FolderAdaptor(FolderActivity.this, mFolders);
                    mFolderList.setAdapter(mFolderAdaptor);
                    mFolderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
                            if (_position == mFoldersLength) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setClass(mContextThis, FileBrowserActivity.class);
                                intent.setType("text/plain");
                                intent.putExtra(Constant.INTENT_EXTRA_BROWSER_TYPE, FileBrowserActivity.BrowserType.OpenExternal.getValue());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constant.INTENT_EXTRA_CURRENT_FOLDERURL, mFolders.get(_position).mFolderPath);
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                        }
                    });
                    mFolderList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> _parent, View _view, int _position, long _id) {

                            if (_position == mFoldersLength) {
                                return false;
                            } else {
                                deleteFolder(_position);
                                return true;
                            }
                        }
                    });
                }
                mFolderAdaptor.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * Handler class for refreshing list of folders
     */
    static class RefreshList extends Handler {
        private final WeakReference<FolderActivity> mActivity;
        RefreshList(FolderActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FolderActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
