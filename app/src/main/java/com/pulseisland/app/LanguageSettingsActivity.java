package com.pulseisland.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<LanguageItem> languages;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        prefs = getSharedPreferences("pulse_island_prefs", Context.MODE_PRIVATE);

        // 如果已经设置过语言，直接跳过
        if (prefs.getBoolean("language_set_done", false)) {
            goToMain();
            return;
        }

        recyclerView = findViewById(R.id.language_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initLanguages();
        adapter = new LanguageAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initLanguages() {
        languages = new ArrayList<>();
        languages.add(new LanguageItem("English", "English", "en"));
        languages.add(new LanguageItem("简体中文", "Simplified Chinese", "zh-rCN"));
        languages.add(new LanguageItem("繁體中文", "Traditional Chinese", "zh-rTW"));
        languages.add(new LanguageItem("日本語", "Japanese", "ja"));
        languages.add(new LanguageItem("한국어", "Korean", "ko"));
        languages.add(new LanguageItem("Tiếng Việt", "Vietnamese", "vi"));
        languages.add(new LanguageItem("ไทย", "Thai", "th"));
        languages.add(new LanguageItem("Bahasa Indonesia", "Indonesian", "in"));
        languages.add(new LanguageItem("Bahasa Melayu", "Malay", "ms"));
        languages.add(new LanguageItem("हिन्दी", "Hindi", "hi"));
        languages.add(new LanguageItem("বাংলা", "Bengali", "bn"));
        languages.add(new LanguageItem("اردو", "Urdu", "ur"));
        languages.add(new LanguageItem("தமிழ்", "Tamil", "ta"));
        languages.add(new LanguageItem("తెలుగు", "Telugu", "te"));
        languages.add(new LanguageItem("العربية", "Arabic", "ar"));
        languages.add(new LanguageItem("فارسی", "Persian", "fa"));
        languages.add(new LanguageItem("Türkçe", "Turkish", "tr"));
        languages.add(new LanguageItem("עברית", "Hebrew", "he"));
        languages.add(new LanguageItem("Қазақша", "Kazakh", "kk"));
        languages.add(new LanguageItem("Français", "French", "fr"));
        languages.add(new LanguageItem("Deutsch", "German", "de"));
        languages.add(new LanguageItem("Español", "Spanish", "es"));
        languages.add(new LanguageItem("Português", "Portuguese", "pt"));
        languages.add(new LanguageItem("Italiano", "Italian", "it"));
        languages.add(new LanguageItem("Русский", "Russian", "ru"));
        languages.add(new LanguageItem("Nederlands", "Dutch", "nl"));
        languages.add(new LanguageItem("Polski", "Polish", "pl"));
        languages.add(new LanguageItem("Svenska", "Swedish", "sv"));
        languages.add(new LanguageItem("Dansk", "Danish", "da"));
        languages.add(new LanguageItem("Norsk", "Norwegian", "no"));
        languages.add(new LanguageItem("Suomi", "Finnish", "fi"));
        languages.add(new LanguageItem("Ελληνικά", "Greek", "el"));
        languages.add(new LanguageItem("Čeština", "Czech", "cs"));
        languages.add(new LanguageItem("Kiswahili", "Swahili", "sw"));
        languages.add(new LanguageItem("አማርኛ", "Amharic", "am"));
        languages.add(new LanguageItem("Hausa", "Hausa", "ha"));
        languages.add(new LanguageItem("Guarani", "Guarani", "gn"));
        languages.add(new LanguageItem("Quechua", "Quechua", "qu"));
        languages.add(new LanguageItem("Māori", "Maori", "mi"));
        languages.add(new LanguageItem("Kreyòl Ayisyen", "Haitian Creole", "ht"));
    }

    private void selectLanguage(LanguageItem language) {
        // 应用语言
        PulseIslandAPI api = new PulseIslandAPI(this);
        api.setLocale(language.code);

        // 标记已设置
        prefs.edit().putBoolean("language_set_done", true).apply();
        prefs.edit().putString("selected_language_name", language.nativeName).apply();

        // 跳转主界面
        goToMain();
    }

    private void goToMain() {
        // 后续替换为主界面Activity
        // startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ========== 语言数据类 ==========
    static class LanguageItem {
        String nativeName;  // 本地语言显示
        String englishName; // 英文名
        St
