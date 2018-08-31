package xyz.harrychen.trivialnews.support.api

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.gson.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.harrychen.trivialnews.support.API_BASE_URL
import xyz.harrychen.trivialnews.support.utils.ApiException
import java.lang.reflect.Type

interface BaseApi {
    companion object {

        private val newsApiResponseInterceptor by lazy {
            { chain: Interceptor.Chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val body = response.body()!!.string()

                val json = Parser().parse(body) as JsonObject

                val errorCode = json.int("error_code")!!
                if (response.code() == 200 && errorCode == 0) {
                    val newBodyString = json.obj("result")!!.toJsonString()
                    val newResponse = ResponseBody.create(response.body()!!.contentType(), newBodyString)
                    response.newBuilder().body(newResponse).build()

                } else {
                    val errorMessage = json.string("error_message")!!
                    val reason = json.string("reason")!!
                    throw ApiException(errorCode, errorMessage, reason)
                }
            }
        }

        private var httpClient = OkHttpClient.Builder()
                .addInterceptor(newsApiResponseInterceptor).build()


        fun setToken(token: String) {
            httpClient = OkHttpClient
                    .Builder()
                    .addInterceptor { chain: Interceptor.Chain ->
                        val request = chain.request()
                        val newRequest = request.newBuilder().header("Authorization", "Bearer $token").build()
                        chain.proceed(newRequest)
                    }
                    .addInterceptor(newsApiResponseInterceptor)
                    .build()
            RETROFIT = buildRetrofit()
        }

        private var RETROFIT: Retrofit? = null

        private val GSON by lazy {
            GsonBuilder()
                    .registerTypeAdapter(DateTime::class.java,
                            { json: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
                                val dateString = json!!.asString
                                ISODateTimeFormat.dateTimeParser().parseDateTime(dateString)
                            })
                    .registerTypeAdapter(DateTime::class.java,
                            { src: DateTime?, _: Type?, _: JsonSerializationContext? ->
                                JsonPrimitive(ISODateTimeFormat.dateTime().print(src))
                            })
                    .create()
        }

        private fun buildRetrofit(): Retrofit {
            val builder = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GSON))
                    .baseUrl(API_BASE_URL)
                    .client(httpClient)
            return builder.build()
        }


        fun getRetrofit(): Retrofit {
            if (RETROFIT == null) {
                RETROFIT = getRetrofit()
            }
            return RETROFIT!!
        }

    }
}