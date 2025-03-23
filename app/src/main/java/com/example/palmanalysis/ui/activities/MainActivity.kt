package com.example.palmanalysis.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.palmanalysis.R
import com.example.palmanalysis.databinding.ActivityMainBinding
import com.example.palmanalysis.service.OpenAIService
import com.example.palmanalysis.util.ConfigUtil
import com.example.palmanalysis.viewmodel.PalmAnalysisUiState
import com.example.palmanalysis.viewmodel.PalmAnalysisViewModel
import com.example.palmanalysis.viewmodel.PalmAnalysisViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PalmAnalysisViewModel
    
    private var currentPhotoPath: String? = null
    
    // 갤러리에서 이미지 선택 결과 처리
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("MainActivity", "갤러리에서 이미지 선택됨: $it")
            val file = createTempFileFromUri(it)
            file?.let { imageFile ->
                viewModel.analyzeImage(imageFile)
            } ?: run {
                Toast.makeText(this, "이미지 파일을 처리할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // 카메라로 사진 촬영 결과 처리
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            Log.d("MainActivity", "카메라로 사진 촬영 성공")
            currentPhotoPath?.let { path ->
                val imageFile = File(path)
                viewModel.analyzeImage(imageFile)
            }
        } else {
            Log.d("MainActivity", "카메라로 사진 촬영 취소 또는 실패")
        }
    }
    
    // 권한 요청 결과 처리
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            Log.d("MainActivity", "모든 권한 획득 성공")
        } else {
            Log.d("MainActivity", "일부 권한 획득 실패")
            Toast.makeText(this, "앱 기능을 사용하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // OpenAI API 키 설정 (환경 변수에서 가져옴)
        val apiKey = ConfigUtil.getOpenAIApiKey(this)
        
        // ViewModel 초기화
        val openAIService = OpenAIService(apiKey)
        val factory = PalmAnalysisViewModelFactory(openAIService)
        viewModel = ViewModelProvider(this, factory)[PalmAnalysisViewModel::class.java]
        
        // UI 상태 관찰
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is PalmAnalysisUiState.Initial -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInstructions.visibility = View.VISIBLE
                }
                is PalmAnalysisUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInstructions.visibility = View.GONE
                }
                is PalmAnalysisUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // 결과 화면으로 이동
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra(ResultActivity.EXTRA_RESULT_TEXT, state.result.text)
                    }
                    startActivity(intent)
                }
                is PalmAnalysisUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInstructions.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.analysis_failed, state.message), Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // 권한 확인 및 요청
        checkAndRequestPermissions()
        
        // 버튼 클릭 리스너 설정
        binding.btnGallery.setOnClickListener {
            Log.d("MainActivity", "갤러리 버튼 클릭됨")
            openGallery()
        }
        
        binding.btnCamera.setOnClickListener {
            Log.d("MainActivity", "카메라 버튼 클릭됨")
            openCamera()
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        // 카메라 권한
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        
        // 저장소 권한 (Android 13 이상은 READ_MEDIA_IMAGES, 이전 버전은 READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        
        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun openCamera() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            cameraLauncher.launch(photoURI)
        }
    }
    
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = getExternalFilesDir(null)
            File.createTempFile(imageFileName, ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "이미지 파일 생성 실패", e)
            null
        }
    }
    
    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(cacheDir, "JPEG_${timeStamp}.jpg")
            
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            file
        } catch (e: Exception) {
            Log.e("MainActivity", "URI에서 파일 생성 실패", e)
            null
        }
    }
}
