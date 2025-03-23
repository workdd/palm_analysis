package com.example.palmanalysis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palmanalysis.model.PalmAnalysisResult
import com.example.palmanalysis.service.OpenAIService
import kotlinx.coroutines.launch
import java.io.File

class PalmAnalysisViewModel(private val openAIService: OpenAIService) : ViewModel() {

    private val _uiState = MutableLiveData<PalmAnalysisUiState>(PalmAnalysisUiState.Initial)
    val uiState: LiveData<PalmAnalysisUiState> = _uiState

    private val _result = MutableLiveData<PalmAnalysisResult>()
    val result: LiveData<PalmAnalysisResult> = _result

    fun analyzeImage(imageFile: File) {
        _uiState.value = PalmAnalysisUiState.Loading
        Log.d("PalmAnalysisViewModel", "이미지 분석 시작: ${imageFile.path}")
        
        viewModelScope.launch {
            try {
                val analysisResult = openAIService.analyzePalmImage(imageFile)
                _result.value = analysisResult
                _uiState.value = PalmAnalysisUiState.Success(analysisResult)
                Log.d("PalmAnalysisViewModel", "이미지 분석 성공")
            } catch (e: Exception) {
                _uiState.value = PalmAnalysisUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
                Log.e("PalmAnalysisViewModel", "이미지 분석 실패", e)
            }
        }
    }
}

sealed class PalmAnalysisUiState {
    object Initial : PalmAnalysisUiState()
    object Loading : PalmAnalysisUiState()
    data class Success(val result: PalmAnalysisResult) : PalmAnalysisUiState()
    data class Error(val message: String) : PalmAnalysisUiState()
}
