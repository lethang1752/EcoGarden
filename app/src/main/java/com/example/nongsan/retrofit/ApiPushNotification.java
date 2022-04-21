package com.example.nongsan.retrofit;

import com.example.nongsan.model.NotiResponse;
import com.example.nongsan.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "Content_Type:application/json",
                    "Authorization:key=AAAArDnSZ4c:APA91bGAFglR42EygaQMvMH8fRz7rzX1EI19re1Ozv4SQwXjh2-xN9kKrRAbWN2asAlsLR1NkYWel8ooHwWXZrKZ1RltLD3FaTzQ340dl41xNNT4NrX4C8bsBD2_m4L4eWVpBfda6IMI"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
