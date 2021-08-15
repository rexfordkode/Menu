package com.example.moftech.smartmenu.Remote;

import com.example.moftech.smartmenu.Model.DataMessage;
import com.example.moftech.smartmenu.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAhH9kZkE:APA91bGnkYYN0I1FNaHmG4b7UdskjLmtOBAFPnjiKUSQ22GEf2WEdhgw8olrvJVc-K6WvSgBpFkYj2oHbMMlXM5PKcovpFcbE0XACaNW1InUDzuMk3GUnT1rSk98nBHLJKbnHrxqOc4S"

            }


    )
@POST ("fcm/send")

    Call<MyResponse>sendNotficaition (@Body DataMessage body);
}
