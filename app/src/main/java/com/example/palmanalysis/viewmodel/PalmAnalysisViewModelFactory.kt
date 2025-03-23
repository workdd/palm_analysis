package com.example.palmanalysis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.palmanalysis.service.OpenAIService

class PalmAnalysisViewModelFactory(private val openAIService: OpenAIService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PalmAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PalmAnalysisViewModel(openAIService) as T
        }
        throw IllegalArgumentException("알 수 없는 ViewModel 클래스")
    }
}
