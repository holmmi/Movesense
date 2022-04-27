package fi.metropolia.movesense.api

import fi.metropolia.movesense.model.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object UserApi {
    private const val URL = "https://senseone-movesense-backend.azurewebsites.net/"
    private val logging: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    interface Service {
        @POST("account/login/")
        fun loginUser(@Body loginRequest: LoginRequest?): Call<LoginResponse?>?

        @POST("account/register/")
        fun registerUser(@Body registerRequest: RegisterRequest?): Call<RegisterResponse>

        @GET("organization/details/")
        fun getOrganizations(): Call<List<OrganizationResponse>>

       // @GET("details")
       // fun userDetails(@Body detailRequest: DetailRequest?): Call<DetailResponse>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
    val service: Service = retrofit.create(Service::class.java)
}