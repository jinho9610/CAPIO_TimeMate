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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timemate.Database.AppDatabase;
import com.example.timemate.Database.OneDay;
import com.example.timemate.Database.OneDayDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv_days;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ODData> arrayList;
    private static List<OneDay> rvODItems;
    private static TextView tv_WDT, tv_EDT, tv_SDT, tv_week_work, tv_week_exercise, tv_week_study;
    private Animation fab_open_day, fab_open_week, fab_open_month, fab_open_time, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab_main, fab_day, fab_week, fab_month, fab_time;
    private LinearLayout LLO_chart_day, LLO_chart_week, LLO_calendar;

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
        tv_week_work = findViewById(R.id.tv_week_work);
        tv_week_exercise = findViewById(R.id.tv_week_exercise);
        tv_week_study = findViewById(R.id.tv_week_study);

        fab_main = findViewById(R.id.fab_main);
        fab_day = findViewById(R.id.fab_day);
        fab_week = findViewById(R.id.fab_week);
        fab_month = findViewById(R.id.fab_month);
        fab_time = findViewById(R.id.fab_time);

        LLO_chart_day = findViewById(R.id.LLO_chart_day);
        LLO_chart_week = findViewById(R.id.LLO_chart_week);
        LLO_calendar=findViewById(R.id.LLO_calendar);


        rv_days.setHasFixedSize(true); // 얘는 그냥 달아주긴하는데 뭔지 정확히 모름
        layoutManager = new LinearLayoutManager(this);
        rv_days.setLayoutManager(layoutManager);
        rv_days.addItemDecoration(new RecyclerViewDecoration(10));
        arrayList = new ArrayList<>(); // OOData 객체를 담을 어레이 리스트(어댑터로 쏴줄것)
        rvODItems = new ArrayList<>();

        new DayAsyncTask(db.oneDayDao()).execute();
        new WeekAsyncTask(db.oneDayDao()).execute();

        adapter = new OneDayAdapater(arrayList, this);
        rv_days.setAdapter(adapter); // 리사이클러 뷰에 어댑터 연결

        // 애니메이션 연결
        fab_open_day = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_day);
        fab_open_week = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_week);
        fab_open_month = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_month);
        fab_open_time = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_time);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab_main.setOnClickListener(this);
        fab_day.setOnClickListener(this);
        fab_week.setOnClickListener(this);
        fab_month.setOnClickListener(this);
        fab_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_main:
                anim();
                break;
            case R.id.fab_day:
                LLO_chart_day.setVisibility(View.VISIBLE);
                LLO_chart_week.setVisibility(View.INVISIBLE);
                LLO_calendar.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_week:
                LLO_chart_day.setVisibility(View.INVISIBLE);
                LLO_chart_week.setVisibility(View.VISIBLE);
                LLO_calendar.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_month :
                LLO_calendar.setVisibility(View.VISIBLE);
                anim();
                break;
        }
    }

    // 애니메이션 적용
    public void anim() {
        if (isFabOpen) {
            fab_day.startAnimation(fab_close);
            fab_week.startAnimation(fab_close);
            fab_month.startAnimation(fab_close);
            fab_time.startAnimation(fab_close);
            fab_day.setClickable(false);
            fab_week.setClickable(false);
            fab_month.setClickable(false);
            fab_time.setClickable(false);
            isFabOpen = false;
        } else {
            fab_day.startAnimation(fab_open_day);
            fab_week.startAnimation(fab_open_week);
            fab_month.startAnimation(fab_open_month);
            fab_time.startAnimation(fab_open_time);
            fab_day.setClickable(true);
            fab_week.setClickable(true);
            fab_month.setClickable(true);
            fab_time.setClickable(true);
            isFabOpen = true;
        }
    }

    // 데이터 가져오기
    public static class DayAsyncTask extends AsyncTask<OneDay, OneDay, Void> {
        private OneDayDao mOneDayDao;

        public DayAsyncTask(OneDayDao oneDayDao) {
            this.mOneDayDao = oneDayDao;
        }

        @Override // 백그라운드 작업
        protected Void doInBackground(OneDay... oneDays) {

            if (!arrayList.isEmpty()) arrayList.clear();
            if (!rvODItems.isEmpty()) rvODItems.clear();

            Calendar cal = new GregorianCalendar();
            int year = cal.get(Calendar.YEAR); // 연
            int month = cal.get(Calendar.MONTH) + 1; // 월
            int day = cal.get(Calendar.DATE); // 일
            Log.e("오늘날짜",Integer.toString(year));
            Log.e("오늘날짜",Integer.toString(month));
            Log.e("오늘날짜",Integer.toString(day));

            if (mOneDayDao.getAllItemsByDayInfo(year, month, day).size()!=0) { // 오늘 저장된 것이 하나라도 있다면
                rvODItems = mOneDayDao.getAllItemsByDayInfo(year, month, day); // 오늘 정보만 가져온다
                Log.e("오늘저장된게있는경우",Integer.toString(rvODItems.size()));
            } else { // 오늘 저장된 놈이 하나도 없는 상태라면 그냥 전날것들 보여주자
                Calendar cal2 = new GregorianCalendar();
                cal2.add(Calendar.DAY_OF_MONTH, -1);
                int y = cal2.get(Calendar.YEAR); // 연
                int m = cal2.get(Calendar.MONTH) + 1; // 월
                int d = cal2.get(Calendar.DATE); // 일
                rvODItems = mOneDayDao.getAllItemsByDayInfo(y, m, d);
                Log.e("오늘저장된게없는경우 Y",Integer.toString(y));
                Log.e("오늘저장된게없는경우 M",Integer.toString(m));
                Log.e("오늘저장된게없는경우 D",Integer.toString(d));
            }

            for (OneDay oneDay : rvODItems) {
                ODData temp = new ODData(oneDay.getCategory(), oneDay.getTime());
                arrayList.add(temp);
            }


            // 가장 최근 것이 rv에서 가장 위로 올라오도록 리스트를 뒤집어준다.
            Collections.reverse(arrayList);

            // onProgressUpdate로 가장 최근 저장된 OneDay 객체를 보낸다
            publishProgress(rvODItems.get(rvODItems.size() - 1));


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
            tv_WDT.setText(tw);
            tv_EDT.setText(te);
            tv_SDT.setText(ts);
        }
    }

    public static class WeekAsyncTask extends AsyncTask<OneDay, List<Long>, Void> {
        private OneDayDao mOneDayDao;

        public WeekAsyncTask(OneDayDao oneDayDao) {
            this.mOneDayDao = oneDayDao;
        }

        @Override
        protected Void doInBackground(OneDay... oneDays) {
            List<OneDay> temp = mOneDayDao.getAllItems();
            OneDay last = temp.get(temp.size() - 1);
            int tempDate = last.getDate();
            List<Long> tempInt = new ArrayList();
            int cursor = temp.size() - 1;
            long a = 0, b = 0, c = 0;
            for (int i = tempDate; i > 0; i--) {
                if (i == tempDate) {
                    a += temp.get(cursor).getWork_dayTotal();
                    b += temp.get(cursor).getExercise_dayTotal();
                    c += temp.get(cursor).getStudy_dayTotal();
                    cursor--;
                } else if (cursor > -1 && temp.get(cursor + 1).getDay() != temp.get(cursor).getDay()) {
                    a += temp.get(cursor).getWork_dayTotal();
                    b += temp.get(cursor).getExercise_dayTotal();
                    c += temp.get(cursor).getStudy_dayTotal();
                    cursor--;
                } else break;
            }
            tempInt.add(a);
            tempInt.add(b);
            tempInt.add(c);
            publishProgress(tempInt);
            return null;
        }

        protected void onProgressUpdate(List<Long>... lists) {
            long a = lists[0].get(0);
            long b = lists[0].get(1);
            long c = lists[0].get(2);

            String aa = String.format("%02dHR %02dMIN", a / 1000 / 60 / 60, a / 1000 / 60);
            String bb = String.format("%02dHR %02dMIN", b / 1000 / 60 / 60, b / 1000 / 60);
            String cc = String.format("%02dHR %02dMIN", c / 1000 / 60 / 60, c / 1000 / 60);

            tv_week_work.setText(aa);
            tv_week_exercise.setText(bb);
            tv_week_study.setText(cc);
        }
    }

}

