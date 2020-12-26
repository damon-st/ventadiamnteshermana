package com.damon.ventadiamante.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAKK3kBc8:APA91bHJZwumNqCMn_sUj4jZFujYXte3JvTLOaAj-YCFf53zlpOiZURlOPJAFKkNxyJY8MLv_AxcAu0W4jsElSHfwLs2obwx6RDZsp_8J6ty2I_sVNXRPMl4S9GLtkN6G6jzIlgSKaBs"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
