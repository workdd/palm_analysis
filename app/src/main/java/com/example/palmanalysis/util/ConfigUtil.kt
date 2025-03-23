package com.example.palmanalysis.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log

/**
 * 설정 정보를 관리하는 유틸리티 클래스
 * API 키와 같은 민감한 정보를 안전하게 관리합니다.
 */
object ConfigUtil {
    private const val TAG = "ConfigUtil"
    private const val META_DATA_API_KEY = "com.example.palmanalysis.OPENAI_API_KEY"
    
    /**
     * AndroidManifest.xml의 meta-data에서 OpenAI API 키를 가져옵니다.
     * 이 방법을 사용하면 API 키를 코드에 하드코딩하지 않고 환경변수처럼 관리할 수 있습니다.
     */
    fun getOpenAIApiKey(context: Context): String {
        return try {
            // 매니페스트에서 메타데이터 가져오기
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, 
                PackageManager.GET_META_DATA
            )
            
            val apiKey = appInfo.metaData?.getString(META_DATA_API_KEY)
            
            if (apiKey.isNullOrBlank()) {
                Log.w(TAG, "API 키가 설정되지 않았습니다. 환경변수 OPENAI_API_KEY를 확인하세요.")
                // 환경변수에서 가져오기 시도 (개발 환경에서만 작동)
                System.getenv("OPENAI_API_KEY") ?: ""
            } else {
                apiKey
            }
        } catch (e: Exception) {
            Log.e(TAG, "API 키를 가져오는 중 오류 발생", e)
            // 환경변수에서 가져오기 시도 (개발 환경에서만 작동)
            System.getenv("OPENAI_API_KEY") ?: ""
        }
    }
}
