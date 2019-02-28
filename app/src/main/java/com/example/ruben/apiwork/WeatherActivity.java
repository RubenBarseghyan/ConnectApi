package com.example.ruben.apiwork;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.SingleScheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {
    private TextView citytextView;
    private TextView weatherTextView;
    private TextView dateTextView;
    private ImageView imageView;

    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        citytextView = findViewById(R.id.cityTextView);
        weatherTextView= findViewById(R.id.weatherTextView);
        dateTextView = findViewById(R.id.dateTextView);
        imageView = findViewById(R.id.weatherImageView);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");


        compositeDisposable = new CompositeDisposable();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.metaweather.com/api/")//verjin slashy partadir e
                .addConverterFactory(GsonConverterFactory.create())//asum e vor ekaccy javai object sarqi
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//retrofiyin asume nq inch dzevov req ani
                .build();




    //sarqum enq servisy
    final WeatherService weatherService = retrofit.create(WeatherService.class);
    weatherService.searchCities("san francisco")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<List<CitySearch>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onSuccess(List<CitySearch> citySearches) {
                    if(!citySearches.isEmpty()){
                        Toast.makeText(WeatherActivity.this, ""+citySearches.get(0).getWoeid(), Toast.LENGTH_SHORT).show();
                    }
                    weatherService.getCityInfo(citySearches.get(0).getWoeid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<CityInfo>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    compositeDisposable.add(d);
                                }

                                @Override
                                public void onSuccess(CityInfo cityInfo) {
                                    Consolidated_weather weather = cityInfo.getConsolidated_weather()[0];
                                    weatherTextView.setText(weather.getWeather_state_name());
                                    citytextView.setText(cityInfo.getTitle());
                                    dateTextView.setText(weather.getApplicable_date());

                                    Glide.with(WeatherActivity.this)
                                            .asBitmap()
                                            .load(Uri.parse("https://www.metaweather.com/"+
                                            "static/img/weather/png/64"+weather.getWeather_state_abbr()+".png"))
                                            .into(imageView);
                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });



                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(WeatherActivity.this, "bad", Toast.LENGTH_SHORT).show();
                }
            });

}
    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
