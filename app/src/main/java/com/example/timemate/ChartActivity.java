package com.example.timemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.timemate.Database.AppDatabase;
import com.example.timemate.Database.OneDay;
import com.example.timemate.Database.OneDayDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView rv_days;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ODData> arrayList;
    private static List<OneDay> rvODItems;
    private static TextView tv_WDT, tv_EDT, tv_SDT;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db = AppDatabase.getAppDatabase(this); // 데이터베이스 가져오기

        rv_days = findViewById(R.id.rv_days);
        rv_days.setHasFixedSize(true); // 얘는 그냥 달아주긴하는데 뭔지 정확히 모름
        layoutManager = new LinearLayoutManager(this);
        rv_days.setLayoutManager(layoutManager);
        rv_days.addItemDecoration(new RecyclerViewDecoration(10));
        arrayList = new ArrayList<>(); // OOData 객체를 담을 어레이 리스트(어댑터로 쏴줄것)
        rvODItems = new ArrayList<>();

        new OneDaySearchAsyncTask(db.oneDayDao()).execute();

        adapter = new OneDayAdapater(arrayList, this);
        rv_days.setAdapter(adapter); // 리사이클러 뷰에 어댑터 연결


        tv_WDT = findViewById(R.id.tv_WDT);
        tv_EDT = findViewById(R.id.tv_EDT);
        tv_SDT = findViewById(R.id.tv_SDT);
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
            long tempW,tempE,tempS;
            OneDay last = oneDays[0];
            tempW = last.getWork_dayTotal();
            tempE = last.getExercise_dayTotal();
            tempS = last.getStudy_dayTotal();
            String tw=String.format("%02dHR %02dMIN",tempW/1000/60/60,tempW/1000/60);
            String te=String.format("%02dHR %02dMIN",tempE/1000/60/60,tempE/1000/60);
            String ts=String.format("%02dHR %02dMIN",tempS/1000/60/60,tempS/1000/60);
            /*Log.d("숫자1", Long.toString(tempW));
            Log.d("숫자1", Long.toString(tempE));
            Log.d("숫자1", Long.toString(tempS));*/
            tv_WDT.setText(tw);
            tv_EDT.setText(te);
            tv_SDT.setText(ts);
        }
    }
}
