package com.example.andriodassignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String[] universityNames = intent.getStringArrayExtra("universityNames");
        Map<String, String> uniWikiMap = new HashMap<>();

        uniWikiMap.put("اليرموك", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D9%8A%D8%B1%D9%85%D9%88%D9%83");
        uniWikiMap.put("الأردنية", "https://ar.wikipedia.org/wiki/%D8%A7%D9%84%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D8%A3%D8%B1%D8%AF%D9%86%D9%8A%D8%A9");
        uniWikiMap.put("العلوم والتكنولوجيا", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D8%B9%D9%84%D9%88%D9%85_%D9%88%D8%A7%D9%84%D8%AA%D9%83%D9%86%D9%88%D9%84%D9%88%D8%AC%D9%8A%D8%A7_(%D8%A7%D9%84%D8%A3%D8%B1%D8%AF%D9%86)");
        uniWikiMap.put("البلقاء التطبيقية", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D8%A8%D9%84%D9%82%D8%A7%D8%A1_%D8%A7%D9%84%D8%AA%D8%B7%D8%A8%D9%8A%D9%82%D9%8A%D8%A9");
        uniWikiMap.put("الهاشمية", "https://ar.wikipedia.org/wiki/%D8%A7%D9%84%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D9%87%D8%A7%D8%B4%D9%85%D9%8A%D8%A9");
        uniWikiMap.put("مؤتة", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D9%85%D8%A4%D8%AA%D8%A9");
        uniWikiMap.put("آل البيت", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A2%D9%84_%D8%A7%D9%84%D8%A8%D9%8A%D8%AA");
        uniWikiMap.put("الحسين بن طلال", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D8%AD%D8%B3%D9%8A%D9%86_%D8%A8%D9%86_%D8%B7%D9%84%D8%A7%D9%84");
        uniWikiMap.put("الطفيلة التقنية", "https://ar.wikipedia.org/wiki/%D8%AC%D8%A7%D9%85%D8%B9%D8%A9_%D8%A7%D9%84%D8%B7%D9%81%D9%8A%D9%84%D8%A9_%D8%A7%D9%84%D8%AA%D9%82%D9%86%D9%8A%D8%A9");



        if(universityNames.length == 0){
            universityNames = new String[]{"لا توجد جامعة توافق معاييرك"};
        }

        ListView listView = findViewById(R.id.result_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, universityNames);
        listView.setAdapter(adapter);

        String[] finalUniversityNames = universityNames;
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = finalUniversityNames[position];
            String url = uniWikiMap.get(selectedItem);
            Intent broweserIntent = new Intent(Intent.ACTION_VIEW);
            broweserIntent.setData(Uri.parse(url));
            startActivity(broweserIntent);
        });
    }
}
