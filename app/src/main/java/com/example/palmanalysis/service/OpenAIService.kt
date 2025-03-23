package com.example.palmanalysis.service

import android.util.Base64
import com.example.palmanalysis.model.PalmAnalysisResult
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class OpenAIService(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzePalmImage(imageFile: File): PalmAnalysisResult = withContext(Dispatchers.IO) {
        val bytes = imageFile.readBytes()
        val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

        val jsonBody = JSONObject().apply {
            put("model", "gpt-4-vision-preview")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", "다음 이미지는 손바닥 사진입니다. 손금을 해석해 주세요:")
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: throw Exception("응답이 비어있습니다.")
            val jsonResponse = JSONObject(responseBody)
            val resultText = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
            
            PalmAnalysisResult(text = resultText)
        } else {
            throw Exception("OpenAI API 오류: ${response.code} ${response.message}")
        }
    }
}
