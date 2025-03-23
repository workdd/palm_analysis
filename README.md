# 손금 분석 앱 (Kotlin 버전)

이 프로젝트는 손바닥 사진을 분석하여 손금을 해석해주는 안드로이드 앱입니다. OpenAI의 GPT-4 Vision API를 사용하여 손금 분석을 수행합니다.

## 주요 기능

- 갤러리에서 이미지 선택 또는 카메라로 사진 촬영
- OpenAI API를 사용한 손금 분석
- 분석 결과 표시

## 프로젝트 구조

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/palmanalysis/
│   │   │   ├── model/
│   │   │   │   └── PalmAnalysisResult.kt
│   │   │   ├── service/
│   │   │   │   └── OpenAIService.kt
│   │   │   ├── ui/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   └── ResultActivity.kt
│   │   │   │   └── fragments/
│   │   │   └── viewmodel/
│   │   │       ├── PalmAnalysisViewModel.kt
│   │   │       └── PalmAnalysisViewModelFactory.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── activity_result.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       └── file_paths.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

## 설정 방법

1. OpenAI API 키 설정:
   - `MainActivity.kt` 파일에서 `YOUR_OPENAI_API_KEY_HERE` 부분을 실제 OpenAI API 키로 교체하세요.

2. 앱 빌드 및 실행:
   - Android Studio에서 프로젝트를 열고 빌드 후 실행하세요.

## 사용된 기술

- Kotlin
- Android Jetpack (ViewModel, LiveData)
- Coroutines
- OkHttp
- ViewBinding

## 권한 요구사항

- 카메라 접근 권한
- 갤러리 접근 권한
- 인터넷 접근 권한
