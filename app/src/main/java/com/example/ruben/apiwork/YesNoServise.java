package com.example.ruben.apiwork;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface YesNoServise {

    @GET(".")
    Single<YesOrNo> getAnswer();//tipy jeneriqov talis enq
}
