package com.eskeptor.openTextViewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.NumberPicker;


/**
 * Created by eskeptor on 17. 2. 8.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class SettingsActivity extends AppCompatPreferenceActivity{
    private static int activeScene;
    private static AlertDialog.Builder dialog;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static Help helpAct;
    private static FontSettings fontSettingAct;
    private static Settings settingsAct;

    public static class Help extends PreferenceFragment
    {
        private Preference.OnPreferenceClickListener clickListener;
        private Preference main_list;
        private Preference main_memoCreate;
        private Preference main_delete;
        private Preference main_openfolder;
        private Preference folder_list;
        private Preference folder_default;
        private Preference folder_create;
        private Preference folder_delete;
        private Preference folder_external;
        private Preference memo_saveLength;
        private Preference memo_fontColor;
        private Preference memo_edit;
        private Preference etc_abouthelp;
        private Preference etc_backup;
        private Preference etc_filecolor;
        private Preference etc_permission;
        private Preference etc_ad;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings_help);

            main_list = findPreference("settings_key_main_list");
            main_memoCreate = findPreference("settings_key_main_memocreate");
            main_delete = findPreference("settings_key_main_delete");
            main_openfolder = findPreference("settings_key_main_openfolder");
            folder_list = findPreference("settings_key_folder_list");
            folder_default = findPreference("settings_key_folder_default");
            folder_create = findPreference("settings_key_folder_create");
            folder_delete = findPreference("settings_key_folder_delete");
            folder_external = findPreference("settings_key_foler_external");
            memo_saveLength = findPreference("settings_key_memo_savelengthlimited");
            memo_fontColor = findPreference("settings_key_memo_fontcolor");
            memo_edit = findPreference("settings_key_memo_edit");
            etc_abouthelp = findPreference("settings_key_etc_abouthelp");
            etc_backup = findPreference("settings_key_etc_backup");
            etc_filecolor = findPreference("settings_key_etc_filecolor");
            etc_permission = findPreference("settings_key_etc_permission");
            etc_ad = findPreference("settings_key_etc_ad");

            clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setClass(getActivity(), HelpContentsActivity.class);
                    intent.putExtra(Constant.INTENT_EXTRA_HELP_INDEX, preference.getKey());
                    startActivity(intent);
                    getActivity().overridePendingTransition(0,0);
                    return false;
                }
            };

            main_list.setOnPreferenceClickListener(clickListener);
            main_memoCreate.setOnPreferenceClickListener(clickListener);
            main_delete.setOnPreferenceClickListener(clickListener);
            main_openfolder.setOnPreferenceClickListener(clickListener);
            folder_external.setOnPreferenceClickListener(clickListener);
            folder_delete.setOnPreferenceClickListener(clickListener);
            folder_create.setOnPreferenceClickListener(clickListener);
            folder_default.setOnPreferenceClickListener(clickListener);
            folder_list.setOnPreferenceClickListener(clickListener);
            memo_fontColor.setOnPreferenceClickListener(clickListener);
            memo_saveLength.setOnPreferenceClickListener(clickListener);
            memo_edit.setOnPreferenceClickListener(clickListener);
            etc_abouthelp.setOnPreferenceClickListener(clickListener);
            etc_backup.setOnPreferenceClickListener(clickListener);
            etc_filecolor.setOnPreferenceClickListener(clickListener);
            etc_permission.setOnPreferenceClickListener(clickListener);
            etc_ad.setOnPreferenceClickListener(clickListener);
        }
    }

    public static class FontSettings extends PreferenceFragment
    {
        private Preference.OnPreferenceClickListener clickListener;
        private Preference textSize;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings_font);

            textSize = findPreference("settings_key_font_fontsize");
            textSize.setSummary(Float.toString(pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE)));
            textSize.setDefaultValue(Float.toString(pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE)));
            clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(preference.getKey().equals("settings_key_font_fontsize"))
                    {
                        dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getResources().getString(R.string.settings_font_fontsize));
                        dialog.setMessage(getResources().getString(R.string.settings_dialog_font_context));
                        NumberPicker picker = new NumberPicker(getActivity());
                        picker.setMaxValue(30);
                        picker.setMinValue(8);
                        picker.setValue((int)pref.getFloat("FontSize", Constant.SETTINGS_DEFAULT_VALUE_TEXT_SIZE));
                        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                if(oldVal != newVal)
                                {
                                    editor.putFloat("FontSize", newVal);
                                    textSize.setSummary(Integer.toString(newVal));
                                }
                            }
                        });
                        dialog.setView(picker);
                        dialog.setPositiveButton(getResources().getString(R.string.settings_dialog_info_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case AlertDialog.BUTTON_POSITIVE:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    return false;
                }
            };
            textSize.setOnPreferenceClickListener(clickListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            editor.commit();
        }
    }

    public static class Settings extends PreferenceFragment
    {
        private Preference.OnPreferenceClickListener clickListener;
        private CheckBoxPreference.OnPreferenceClickListener checkClickListener;
        private Preference info;
        private Preference font;
        private Preference version;
        private Preference bugreport;
        private Preference help;
        private CheckBoxPreference admob;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            info = findPreference("settings_key_info");
            version = findPreference("settings_key_version");
            font = findPreference("settings_key_font");
            bugreport = findPreference("settings_key_bugreport");
            help = findPreference("settings_key_help");
            admob = (CheckBoxPreference)findPreference("settings_key_admob");
            clickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(preference.getKey().equals("settings_key_info"))
                    {
                        dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getResources().getString(R.string.settings_dialog_info_title));
                        String copyright = getResources().getString(R.string.settings_dialog_info_copyright);
                        String license = getResources().getString(R.string.settings_dialog_info_license);
                        String icons = getResources().getString(R.string.settings_dialog_info_icon);
                        String homepage = getResources().getString(R.string.settings_dialog_info_homepage);
                        String line = "\n";

                        dialog.setMessage(copyright + line + license + line + icons + line + homepage);
                        dialog.setPositiveButton(R.string.settings_dialog_info_ok, null);
                        dialog.show();
                    }
                    else if(preference.getKey().equals("settings_key_font"))
                    {
                        getFragmentManager().beginTransaction().replace(android.R.id.content, fontSettingAct).commit();
                        activeScene = Constant.SETTINGS_ACTIVESCREEN_FONT;
                    }
                    else if(preference.getKey().equals("settings_key_bugreport"))
                    {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        String[] address = {Constant.APP_DEV_MAILADDRESS};
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, address);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(intent);
                    }
                    else if(preference.getKey().equals("settings_key_help"))
                    {
                        getFragmentManager().beginTransaction().replace(android.R.id.content, helpAct).commit();
                        activeScene = Constant.SETTINGS_ACTIVESCREEN_HELP;
                    }
                    return false;
                }
            };

            checkClickListener = new CheckBoxPreference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(preference.getKey().equals("settings_key_admob"))
                    {
                        dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.settings_general_admob);
                        dialog.setMessage(R.string.settings_general_admob_dialog_context);
                        dialog.setPositiveButton(R.string.settings_dialog_info_ok, null);
                        dialog.show();
                    }
                    return false;
                }
            };

            info.setOnPreferenceClickListener(clickListener);
            font.setOnPreferenceClickListener(clickListener);
            bugreport.setOnPreferenceClickListener(clickListener);
            help.setOnPreferenceClickListener(clickListener);
            version.setSummary(BuildConfig.VERSION_NAME);

            admob.setOnPreferenceClickListener(checkClickListener);
            admob.setChecked(pref.getBoolean(Constant.APP_ADMOB_VISIBLE, true));
        }

        @Override
        public void onPause() {
            super.onPause();
            editor.putBoolean(Constant.APP_ADMOB_VISIBLE, admob.isChecked());
            editor.commit();
        }
    }

    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.settings_title);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || FontSettings.class.getName().equals(fragmentName)
                || Settings.class.getName().equals(fragmentName)
                || Help.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        helpAct = new Help();
        fontSettingAct = new FontSettings();
        settingsAct = new Settings();

        getFragmentManager().beginTransaction().replace(android.R.id.content, settingsAct).commit();
        activeScene = Constant.SETTINGS_ACTIVESCREEN_MAIN;

        pref = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();
    }

    @Override
    public void onBackPressed() {
        Log.e("Debug", "activeScene : " + Integer.toString(activeScene));
        if(activeScene == Constant.SETTINGS_ACTIVESCREEN_MAIN)
        {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_top);
            Log.e("Debug", "Change activeScene : " + Integer.toString(activeScene));
        }
        else
        {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();
            activeScene = Constant.SETTINGS_ACTIVESCREEN_MAIN;
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            Log.e("Debug", "Change activeScene : " + Integer.toString(activeScene));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helpAct = null;
        fontSettingAct = null;
        settingsAct = null;
        dialog = null;
        pref = null;
        editor = null;
    }
}
