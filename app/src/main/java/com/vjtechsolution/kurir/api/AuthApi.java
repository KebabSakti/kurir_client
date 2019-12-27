package com.vjtechsolution.kurir.api;

import com.vjtechsolution.kurir.model.Customer;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApi {

    //valdate customer by keyword
    @GET("customer/{keyword}")
    Single<Customer> getCustomer(@Path("keyword") String keyword);

    //register new customer
    @FormUrlEncoded
    @POST("customer")
    Single<Customer> newCustomer(
            @Field("name") String name,
            @Field("sex") String sex,
            @Field("phone") String phone,
            @Field("city") String city
    );

}
