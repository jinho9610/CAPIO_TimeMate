package com.example.timemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemate.Database.AppDatabase;
import com.example.timemate.Database.OneDay;
import com.example.timemate.Database.OneDayDao;
import com.example.timemate.Database.TotalData;
import com.example.timemate.Database.TotalDataDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv_days;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ODData> arrayList;
    private static List<OneDay> rvODItems;
    private static TextView tv_WDT, tv_EDT, tv_SDT, tv_week_work, tv_week_exercise, tv_week_study, tv_total_work, tv_total_exercise, tv_total_study, tv_month, tv_totalInfo;
    private Animation fab_open_day, fab_open_week, fab_open_month, fab_open_time, fab_close, panelup_cal, paneldown_cal;
    private Boolean isFabOpen = false, isCalVisible = false;
    private FloatingActionButton fab_main, fab_day, fab_month, fab_time;
    private LinearLayout LLO_chart_day, LLO_chart_month, LLO_calendar, LLO_chart_total;
    private CalendarView calendar;
    private static ProgressBar pb_month, pb_day;
    private static int cnt_month = -1, cnt_day = -1;
    static Context context;

    private static int temp_month;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db = AppDatabase.getAppDatabase(this); // 데이터베이스 가져오기

        rv_days = findViewById(R.id.rv_days);
        calendar = findViewById(R.id.calendar);
        tv_WDT = findViewById(R.id.tv_WDT);
        tv_EDT = findViewById(R.id.tv_EDT);
        tv_SDT = findViewById(R.id.tv_SDT);
        tv_week_work = findViewById(R.id.tv_week_work);
        tv_week_exercise = findViewById(R.id.tv_week_exercise);
        tv_week_study = findViewById(R.id.tv_week_study);
        tv_total_work = findViewById(R.id.tv_total_work);
        tv_total_exercise = findViewById(R.id.tv_total_exercise);
        tv_total_study = findViewById(R.id.tv_total_study);
        tv_month = findViewById(R.id.tv_month);
        tv_totalInfo = findViewById(R.id.tv_totalInfo);
        // 애니메이션
        fab_main = findViewById(R.id.fab_main);
        fab_day = findViewById(R.id.fab_day);
        fab_month = findViewById(R.id.fab_month);
        fab_time = findViewById(R.id.fab_time);
        // 리니어 레이아웃
        LLO_chart_day = findViewById(R.id.LLO_chart_day);
        LLO_chart_month = findViewById(R.id.LLO_chart_month);
        LLO_calendar = findViewById(R.id.LLO_calendar);
        LLO_chart_total = findViewById(R.id.LLO_chart_total);

        context = getApplicationContext();
        // 프로그레스 바
        pb_month = findViewById(R.id.pb_month);
        pb_day = findViewById(R.id.pb_day);

        rv_days.setHasFixedSize(true); // 얘는 그냥 달아주긴하는데 뭔지 정확히 모름
        layoutManager = new LinearLayoutManager(this);
        rv_days.setLayoutManager(layoutManager);
        rv_days.addItemDecoration(new RecyclerViewDecoration(10));
        arrayList = new ArrayList<>(); // OOData 객체를 담을 어레이 리스트(어댑터로 쏴줄것)
        rvODItems = new ArrayList<>();

        new DayAsyncTask(db.oneDayDao()).execute();
        new MonthAsyncTask(db.oneDayDao()).execute();
        new TotalAsyncTask(db.totalDataDao()).execute();

        adapter = new OneDayAdapater(arrayList, this);
        rv_days.setAdapter(adapter); // 리사이클러 뷰에 어댑터 연결

        // 애니메이션 연결
        fab_open_day = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_day);
        fab_open_week = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_week);
        fab_open_month = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_month);
        fab_open_time = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_time);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        panelup_cal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.panelup_cal);
        paneldown_cal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.paneldown_cal);

        fab_main.setOnClickListener(this);
        fab_day.setOnClickListener(this);
        fab_month.setOnClickListener(this);
        fab_time.setOnClickListener(this);

        // 패널업 애니메이션 리스너
        panelup_cal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LLO_calendar.setVisibility(View.VISIBLE);
                LLO_calendar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // 패널다운 애니메이션 리스너
        paneldown_cal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LLO_calendar.setVisibility(View.INVISIBLE);
                LLO_calendar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // 달력에서 특정 날짜를 선택하면 그 날짜를 가지고 일일 통계의 리싸이클러 뷰를 재구성한다.
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                new CalDayAsyncTask(db.oneDayDao()).execute(year, month + 1, dayOfMonth);
                LLO_calendar.startAnimation(paneldown_cal);
                isCalVisible = false;
                LLO_chart_month.setVisibility(View.INVISIBLE);
                LLO_chart_total.setVisibility(View.INVISIBLE);
                LLO_chart_day.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_main:
                anim();
                break;
            case R.id.fab_day:
                if (isCalVisible) {
                    fab_main.setImageResource(R.drawable.fltbtn);
                    fab_day.setImageResource(R.drawable.day);
                    fab_month.setImageResource(R.drawable.month);
                    fab_time.setImageResource(R.drawable.clk);
                    LLO_calendar.startAnimation(paneldown_cal);
                    isCalVisible = false;
                    anim();

                    break;
                } else {
                    fab_main.setImageResource(R.drawable.fltbtn2);
                    fab_day.setImageResource(R.drawable.day2);
                    fab_month.setImageResource(R.drawable.month2);
                    fab_time.setImageResource(R.drawable.clk2);
                    LLO_calendar.startAnimation(panelup_cal);
                    isCalVisible = true;
                    anim();
                    break;
                }
            case R.id.fab_month:
                LLO_chart_day.setVisibility(View.INVISIBLE);
                LLO_chart_total.setVisibility(View.INVISIBLE);
                LLO_chart_month.setVisibility(View.VISIBLE);
                if (isCalVisible) {
                    LLO_calendar.startAnimation(paneldown_cal);
                    isCalVisible = false;
                }
                // pbMonth();
                break;
            case R.id.fab_time:
                LLO_chart_day.setVisibility(View.INVISIBLE);
                LLO_chart_month.setVisibility(View.INVISIBLE);
                LLO_chart_total.setVisibility(View.VISIBLE);
                if (isCalVisible) {
                    LLO_calendar.startAnimation(paneldown_cal);
                    isCalVisible = false;
                }
                break;
        }
    }

    // 애니메이션 적용
    public void anim() {
        if (isFabOpen) {
            fab_day.startAnimation(fab_close);
            fab_month.startAnimation(fab_close);
            fab_time.startAnimation(fab_close);
            fab_day.setClickable(false);
            fab_month.setClickable(false);
            fab_time.setClickable(false);
            isFabOpen = false;
        } else {
            fab_day.startAnimation(fab_open_day);
            fab_month.startAnimation(fab_open_month);
            fab_time.startAnimation(fab_open_time);
            fab_day.setClickable(true);
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
            Log.e("오늘날짜", Integer.toString(year));
            Log.e("오늘날짜", Integer.toString(month));
            Log.e("오늘날짜", Integer.toString(day));

            if (mOneDayDao.getAllItemsByDayInfo(year, month, day).size() != 0) { // 오늘 저장된 것이 하나라도 있다면
                rvODItems = mOneDayDao.getAllItemsByDayInfo(year, month, day); // 오늘 정보만 가져온다
                Log.e("오늘저장된게있는경우", Integer.toString(rvODItems.size()));
            } else { // 오늘 저장된 놈이 하나도 없는 상태라면, 저장된 데이터가 있는 가장 최근의 날 정보들 보여주자
                for (int i = 1; ; i++) {
                    Calendar cal2 = new GregorianCalendar();
                    cal2.add(Calendar.DAY_OF_MONTH, -i);
                    int y = cal2.get(Calendar.YEAR); // 연
                    int m = cal2.get(Calendar.MONTH) + 1; // 월
                    int d = cal2.get(Calendar.DATE); // 일
                    if (mOneDayDao.getAllItemsByDayInfo(y, m, d).size() != 0) {
                        rvODItems = mOneDayDao.getAllItemsByDayInfo(y, m, d);
                        Log.e("오늘저장된게없는경우 Y", Integer.toString(y));
                        Log.e("오늘저장된게없는경우 M", Integer.toString(m));
                        Log.e("오늘저장된게없는경우 D", Integer.toString(d));
                        break;
                    } else continue;
                }
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
            final long tempW, tempE, tempS;
            OneDay last = oneDays[0];
            tempW = last.getWork_dayTotal();
            tempE = last.getExercise_dayTotal();
            tempS = last.getStudy_dayTotal();
            String tw = String.format("%02dHR %02dMIN", tempW / 1000 / 60 / 60, (tempW / 1000 / 60) % 60);
            String te = String.format("%02dHR %02dMIN", tempE / 1000 / 60 / 60, (tempE / 1000 / 60) % 60);
            String ts = String.format("%02dHR %02dMIN", tempS / 1000 / 60 / 60, (tempS / 1000 / 60) % 60);
            tv_WDT.setText(tw);
            tv_EDT.setText(te);
            tv_SDT.setText(ts);

            final Timer t = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    cnt_day++;
                    Log.d("dddd", "" + cnt_day);
                    pb_day.setProgress(cnt_day);

                    if (cnt_day == ((tempW + tempE + tempS) / 1000 / 60 % 60) || cnt_day == 60)
                        t.cancel();
                }
            };
            t.schedule(tt, 0, 20);
            cnt_day = -1;
        }
    }


    public static class CalDayAsyncTask extends AsyncTask<Integer, OneDay, Void> {
        private OneDayDao mOneDayDao;
        private boolean empty = false;

        public CalDayAsyncTask(OneDayDao oneDayDao) {
            this.mOneDayDao = oneDayDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            Log.d("integers", Integer.toString(integers[0]));
            Log.d("integers", Integer.toString(integers[1]));
            Log.d("integers", Integer.toString(integers[2]));
            Log.d("size", Integer.toString(mOneDayDao.getAllItemsByDayInfo(integers[0], integers[1], integers[2]).size()));
            if (!arrayList.isEmpty()) arrayList.clear();
            if (!rvODItems.isEmpty()) rvODItems.clear();

            if (!mOneDayDao.getAllItemsByDayInfo(integers[0], integers[1], integers[2]).isEmpty()) {
                rvODItems = mOneDayDao.getAllItemsByDayInfo(integers[0], integers[1], integers[2]);
                ArrayList<ODData> temp = new ArrayList<>();
                for (OneDay oneDay : rvODItems) {
                    temp.add(new ODData(oneDay.getCategory(), oneDay.getTime()));
                }
                arrayList.addAll(temp);
                Collections.reverse(arrayList);
                Log.d("integers", Integer.toString(rvODItems.size()));
                publishProgress(rvODItems.get(rvODItems.size() - 1));
                Log.d("arraylist사이즈", Integer.toString(arrayList.size()));
            } else {
                empty = true;
                // onProgressUpdate를 호출하기 위한 더미 객체 보냄
                publishProgress(new OneDay(0, 0, 0, 0, 1, 1, 1, 1, 1));
            }

            Log.d("사이즈", "" + mOneDayDao.getAllItemsByDayInfo(integers[0], integers[1], integers[2]).size());

            return null;
        }

        protected void onProgressUpdate(OneDay... oneDays) {
            Log.d("비었냐", "" + empty);
            if (empty)
                Toast.makeText(context, "해당 날짜에는 기록이 없어요", Toast.LENGTH_SHORT).show();
            else {
                adapter.notifyDataSetChanged();

                final long tempW, tempE, tempS;
                OneDay last = oneDays[0];
                tempW = last.getWork_dayTotal();
                tempE = last.getExercise_dayTotal();
                tempS = last.getStudy_dayTotal();
                String tw = String.format("%02dHR %02dMIN", tempW / 1000 / 60 / 60, (tempW / 1000 / 60) % 60);
                String te = String.format("%02dHR %02dMIN", tempE / 1000 / 60 / 60, (tempE / 1000 / 60) % 60);
                String ts = String.format("%02dHR %02dMIN", tempS / 1000 / 60 / 60, (tempS / 1000 / 60) % 60);
                tv_WDT.setText(tw);
                tv_EDT.setText(te);
                tv_SDT.setText(ts);

                final Timer t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        cnt_day++;
                        Log.d("dddd", "" + cnt_day);
                        pb_day.setProgress(cnt_day);

                        if (cnt_day == ((tempW + tempE + tempS) / 1000 / 60 % 60) || cnt_day == 60) t.cancel();
                    }
                };
                t.schedule(tt, 0, 20);
                cnt_day = -1;
            }
        }
    }

    public static class MonthAsyncTask extends AsyncTask<OneDay, List<Long>, Void> {
        private OneDayDao mOneDayDao;

        public MonthAsyncTask(OneDayDao oneDayDao) {
            this.mOneDayDao = oneDayDao;
        }

        @Override
        protected Void doInBackground(OneDay... oneDays) {
            List<OneDay> temp = mOneDayDao.getAllItems();
            OneDay last = temp.get(temp.size() - 1);
            int tempDay = last.getDay(); // 가장 최근에 기록된 녀석의 날짜를 가져온다
            Log.d("가장최근날짜", "" + last.getDay());
            List<Long> tempInt = new ArrayList();
            int cursor = temp.size() - 1;
            long a = 0, b = 0, c = 0;
            for (int i = cursor; i >= 0; i--) {
                Log.d("해당 커서", "" + i);
                Log.d("해당 월", "" + (temp.get(i).getMonth()));
                Log.d("해당 날짜", "" + temp.get(i).getDay());
                if (i == cursor) {
                    a += temp.get(i).getWork_dayTotal();
                    b += temp.get(i).getExercise_dayTotal();
                    c += temp.get(i).getStudy_dayTotal();
                } else if ((temp.get(i + 1).getDay() != temp.get(i).getDay()) && (temp.get(i + 1).getMonth() == temp.get(i).getMonth())) { // 날짜가 같다면 굳이 연산하지 않는다
                    a += temp.get(i).getWork_dayTotal();
                    b += temp.get(i).getExercise_dayTotal();
                    c += temp.get(i).getStudy_dayTotal();
                } else if (temp.get(i + 1).getMonth() != temp.get(i).getMonth())
                    break;
                else continue;
            }
            tempInt.add(a);
            tempInt.add(b);
            tempInt.add(c);
            publishProgress(tempInt);
            return null;
        }

        protected void onProgressUpdate(List<Long>... lists) {
            final long a = lists[0].get(0);
            final long b = lists[0].get(1);
            final long c = lists[0].get(2);
            final long d = a + b + c;

            String aa = String.format("%02dHR %02dMIN", a / 1000 / 60 / 60, (a / 1000 / 60) % 60);
            String bb = String.format("%02dHR %02dMIN", b / 1000 / 60 / 60, (b / 1000 / 60) % 60);
            String cc = String.format("%02dHR %02dMIN", c / 1000 / 60 / 60, (c / 1000 / 60) % 60);
            String dd = String.format("%02dHOUR %02dMIN", d / 1000 / 60 / 60, (d / 1000 / 60) % 60);

            tv_week_work.setText(aa);
            tv_week_exercise.setText(bb);
            tv_week_study.setText(cc);
            tv_month.setText(dd);

            pb_month.setProgress(((int) ((a + b + c) / 1000 / 60 / 60)) * 2);
            //temp_month=((int) ((a + b + c) / 1000 / 60 / 60)) * 2;
            /*final Timer t = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    cnt_month++;
                    Log.d("dddd", "" + cnt_month);
                    pb_month.setProgress(cnt_month);

                    if (cnt_month == ((a+b+c) / 1000 / 60 % 60)) t.cancel();
                }
            };
            t.schedule(tt, 0, 20);
            cnt_month = -1;*/
        }
    }

    public static class TotalAsyncTask extends AsyncTask<TotalData, TotalData, Void> {
        private TotalDataDao mTotalDataDao;

        public TotalAsyncTask(TotalDataDao totalDataDao) {
            this.mTotalDataDao = totalDataDao;
        }

        @Override
        protected Void doInBackground(TotalData... totalDatas) {
            TotalData temp = mTotalDataDao.getAllItems().get(0);
            publishProgress(temp);

            return null;
        }

        protected void onProgressUpdate(TotalData... totalDatas) {
            long a = totalDatas[0].getWork_Total();
            long b = totalDatas[0].getExercise_Total();
            long c = totalDatas[0].getStudy_Total();
            long d = a + b + c;

            String aa = String.format("%02dHR %02dMIN", a / 1000 / 60 / 60, (a / 1000 / 60) % 60);
            String bb = String.format("%02dHR %02dMIN", b / 1000 / 60 / 60, (b / 1000 / 60) % 60);
            String cc = String.format("%02dHR %02dMIN", c / 1000 / 60 / 60, (c / 1000 / 60) % 60);
            String dd = String.format("%02dHOUR %02dMIN", d / 1000 / 60 / 60, (d / 1000 / 60) % 60);

            tv_total_work.setText(aa);
            tv_total_exercise.setText(bb);
            tv_total_study.setText(cc);
            tv_totalInfo.setText(dd);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            fab_day.startAnimation(fab_close);
            fab_month.startAnimation(fab_close);
            fab_time.startAnimation(fab_close);
            fab_day.setClickable(false);
            fab_month.setClickable(false);
            fab_time.setClickable(false);
            isFabOpen = false;
        } else if (isCalVisible && isFabOpen) {
            fab_day.startAnimation(fab_close);
            fab_month.startAnimation(fab_close);
            fab_time.startAnimation(fab_close);
            fab_day.setClickable(false);
            fab_month.setClickable(false);
            fab_time.setClickable(false);
            isFabOpen = false;
        } else if (isCalVisible && !isFabOpen) {
            LLO_calendar.startAnimation(paneldown_cal);
            isCalVisible = false;
        } else {
            finish();
        }
    }

   /* public void pbMonth() {
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                // tempTT는 ms단위임
                cnt_month++;
                pb_month.setProgress(cnt_month);

                    if (cnt_month == temp_month) t.cancel();

            }
        };
        t.schedule(tt, 0, 20);
        cnt_month = -1;
    }*/
}

