package com.example.ruben.apiwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ImageView gifView;
    private TextView textView;
    private Button reloadBtn;
    private Retrofit retrofit;
    private ProgressDialog pd;



    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");

        textView=findViewById(R.id.textView);
        reloadBtn=findViewById(R.id.reloadBtn);
        gifView = findViewById(R.id.gifView);

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                YesNoServise servise = retrofit.create(YesNoServise.class);
                servise.getAnswer()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<YesOrNo>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);


                            }

                            @Override
                            public void onSuccess(YesOrNo yesOrNo) {
                                textView.setText(yesOrNo.getAnswer());
                                pd.dismiss();
                                Glide.with(gifView).asGif()
                                        .load(Uri.parse(yesOrNo.getImage()))
                                        .into(gifView);

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this, "Something get Wrong", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }
                        });


            }
        });
        compositeDisposable = new CompositeDisposable();
      retrofit = new Retrofit.Builder()
                .baseUrl("https://yesno.wtf/api/")//verjin slashy partadir e
                .addConverterFactory(GsonConverterFactory.create())//asum e vor ekaccy javai object sarqi
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//retrofiyin asume nq inch dzevov req ani
                .build();

      findViewById(R.id.weatherBtn).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
              startActivity(intent);
          }
      });


    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
