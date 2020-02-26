package com.example.timemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.timemate.Database.AppDatabase;
import com.example.timemate.Database.OneDay;
import com.example.timemate.Database.OneDayDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView rv_days;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ODData> arrayList;
    private static List<OneDay> rvODItems;
    private static TextView tv_WDT, tv_EDT, tv_SDT;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab_main, fab_day, fab_week, fab_month, fab_time;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db = AppDatabase.getAppDatabase(this); // 데이터베이스 가져오기

        rv_days = findViewById(R.id.rv_days);
        tv_WDT = findViewById(R.id.tv_WDT);
        tv_EDT = findViewById(R.id.tv_EDT);
        tv_SDT = findViewById(R.id.tv_SDT);

        fab_main = findViewById(R.id.fab_main);
        fab_day = findViewById(R.id.fab_day);
        fab_week = findViewById(R.id.fab_week);
        fab_month = findViewById(R.id.fab_month);


        rv_days.setHasFixedSize(true); // 얘는 그냥 달아주긴하는데 뭔지 정확히 모름
        layoutManager = new LinearLayoutManager(this);
        rv_days.setLayoutManager(layoutManager);
        rv_days.addItemDecoration(new RecyclerViewDecoration(10));
        arrayList = new ArrayList<>(); // OOData 객체를 담을 어레이 리스트(어댑터로 쏴줄것)
        rvODItems = new ArrayList<>();

        new OneDaySearchAsyncTask(db.oneDayDao()).execute();

        adapter = new OneDayAdapater(arrayList, this);
        rv_days.setAdapter(adapter); // 리사이클러 뷰에 어댑터 연결

        // 애니메이션 연결
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab_main.setOnClickListener(this);
        fab_day.setOnClickListener(this);
        fab_week.setOnClickListener(this);
        fab_month.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fab_main :
                anim();
                break;
        }
    }
    // 애니메이션 적용
    public void anim() {
        if(isFabOpen) {
            fab_day.startAnimation(fab_close);
            fab_week.startAnimation(fab_close);
            fab_month.startAnimation(fab_close);
            fab_day.setClickable(false);
            fab_week.setClickable(false);
            fab_month.setClickable(false);
            isFabOpen=false;
        }
        else {
            fab_day.startAnimation(fab_open);
            fab_week.startAnimation(fab_open);
            fab_month.startAnimation(fab_open);
            fab_day.setClickable(true);
            fab_week.setClickable(true);
            fab_month.setClickable(true);
            isFabOpen=true;
        }
    }

    // 데이터 가져오기
    public static class OneDaySearchAsyncTask extends AsyncTask<OneDay, OneDay, Void> {
        private OneDayDao mOneDayDao;

        public OneDaySearchAsyncTask(OneDayDao oneDayDao) {
            this.mOneDayDao = oneDayDao;
        }

        @Override // 백그라운드 작업
        protected Void doInBackground(OneDay... oneDays) {

            if (!arrayList.isEmpty()) arrayList.clear();
            if (!rvODItems.isEmpty()) rvODItems.clear();

            if (mOneDayDao.getAllItems() != null) {
                rvODItems = mOneDayDao.getAllItems();

                for (OneDay oneDay : rvODItems) {
                    ODData temp = new ODData(oneDay.getCategory(), oneDay.getTime());
                    arrayList.add(temp);
                }

                Collections.reverse(arrayList);

                publishProgress(rvODItems.get(rvODItems.size() - 1));
            }
            return null;
        }

        protected void onProgressUpdate(OneDay... oneDays) {
            long tempW, tempE, tempS;
            OneDay last = oneDays[0];
            tempW = last.getWork_dayTotal();
            tempE = last.getExercise_dayTotal();
            tempS = last.getStudy_dayTotal();
            String tw = String.format("%02dHR %02dMIN", tempW / 1000 / 60 / 60, tempW / 1000 / 60);
            String te = String.format("%02dHR %02dMIN", tempE / 1000 / 60 / 60, tempE / 1000 / 60);
            String ts = String.format("%02dHR %02dMIN", tempS / 1000 / 60 / 60, tempS / 1000 / 60);
            /*Log.d("숫자1", Long.toString(tempW));
            Log.d("숫자1", Long.toString(tempE));
            Log.d("숫자1", Long.toString(tempS));*/
            tv_WDT.setText(tw);
            tv_EDT.setText(te);
            tv_SDT.setText(ts);
        }
    }
}
