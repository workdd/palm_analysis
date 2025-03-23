# 손금 분석 앱 사용 설명서

이 앱은 손바닥 사진을 분석하여 손금을 해석해주는 안드로이드 앱입니다. 아래 내용을 통해 앱을 테스트하고 사용하는 방법을 알아보세요.

## 테스트 방법 (안드로이드 스튜디오 없이)

현재 안드로이드 스튜디오 없이 앱의 핵심 기능을 테스트할 수 있는 Python 스크립트가 준비되어 있습니다.

### 1. 가상 환경 활성화하기

```bash
source palm_analysis_venv/bin/activate
```

### 2. OpenAI API 키 설정하기

```bash
export OPENAI_API_KEY="여기에_실제_API_키를_입력하세요"
```

### 3. 손바닥 이미지 준비하기

손바닥 이미지를 준비하고 해당 이미지 경로를 기억해두세요.

### 4. 스크립트 실행하기

```bash
python test_openai_api.py 이미지_파일_경로
```

예시:
```bash
python test_openai_api.py ~/Desktop/palm_image.jpg
```

## 환경 변수 설정 방법

이제 앱은 환경 변수에서 OpenAI API 키를 가져오도록 수정되었습니다. 앱을 실행하기 전에 다음과 같이 환경 변수를 설정해야 합니다:

### 안드로이드 스튜디오에서 환경 변수 설정하기

1. 안드로이드 스튜디오에서 실행 구성(Run Configuration)을 엽니다.
2. '편집 구성(Edit Configurations)' 메뉴를 선택합니다.
3. 환경 변수(Environment Variables) 섹션에서 다음을 추가합니다:
   ```
   OPENAI_API_KEY=여기에_실제_API_키를_입력하세요
   ```

### 터미널에서 환경 변수 설정하기

터미널에서 앱을 빌드하거나 실행할 때는 다음과 같이 환경 변수를 설정합니다:

```bash
export OPENAI_API_KEY="여기에_실제_API_키를_입력하세요"
```

## 앱 설치 및 실행 방법 (안드로이드 스튜디오 사용)

안드로이드 스튜디오를 설치하면 전체 앱을 빌드하고 실행할 수 있습니다.

1. 안드로이드 스튜디오 설치하기
2. 프로젝트 열기: `/Users/manchann/Desktop/programming/palm_analysis`
3. OpenAI API 키 설정하기: `MainActivity.kt` 파일에서 `YOUR_OPENAI_API_KEY_HERE` 부분을 실제 API 키로 교체
4. 앱 빌드 및 실행하기

## 앱 주요 기능

- 갤러리에서 이미지 선택 또는 카메라로 사진 촬영
- OpenAI API를 사용한 손금 분석
- 분석 결과 표시

## 주의사항

- OpenAI API 사용에는 요금이 부과될 수 있습니다.
- 개인 정보 보호를 위해 API 키를 안전하게 관리하세요.
- 실제 앱 배포 시에는 API 키를 코드에 직접 포함하지 마세요.
