package fi.metropolia.movesense.api

import fi.metropolia.movesense.BuildConfig
import fi.metropolia.movesense.model.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object UserApi {
    private const val BASE_URL = BuildConfig.userApiBaseUrl

    private val logging: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    interface Service {
        @POST("/account/login")
        fun loginUser(@Body loginRequest: LoginRequest?): Call<LoginResponse?>?

        @POST("/account/register")
        fun registerUser(@Body registerRequest: RegisterRequest?): Call<RegisterResponse>

        @GET("/organization/details")
        fun getOrganizations(): Call<List<OrganizationResponse>>

       @GET("/account/details")
       fun getUserDetails(@Header("authorization") token: String?): Call<DetailResponse>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
    val service: Service = retrofit.create(Service::class.java)
}