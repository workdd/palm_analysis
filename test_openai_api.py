import base64
import requests
import json
import sys
import os

def analyze_palm_image(image_path, api_key):
    """
    OpenAI API를 사용하여 손바닥 이미지를 분석합니다.
    
    Args:
        image_path (str): 이미지 파일 경로
        api_key (str): OpenAI API 키
        
    Returns:
        str: 분석 결과 텍스트
    """
    # 이미지 파일을 Base64로 인코딩
    with open(image_path, "rb") as image_file:
        base64_image = base64.b64encode(image_file.read()).decode('utf-8')
    
    # API 요청 헤더
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }
    
    # API 요청 본문
    payload = {
        "model": "gpt-4-vision-preview",
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": "다음 이미지는 손바닥 사진입니다. 손금을 해석해 주세요:"
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}"
                        }
                    }
                ]
            }
        ]
    }
    
    # API 요청 보내기
    response = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers=headers,
        json=payload
    )
    
    # 응답 처리
    if response.status_code == 200:
        result = response.json()
        return result["choices"][0]["message"]["content"]
    else:
        print(f"오류: {response.status_code}")
        print(response.text)
        return f"OpenAI API 오류: {response.status_code}"

def main():
    """메인 함수"""
    if len(sys.argv) < 2:
        print("사용법: python test_openai_api.py <이미지_파일_경로>")
        return
    
    # OpenAI API 키 가져오기
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        print("OPENAI_API_KEY 환경 변수를 설정해주세요.")
        return
    
    # 이미지 파일 경로
    image_path = sys.argv[1]
    if not os.path.exists(image_path):
        print(f"파일을 찾을 수 없습니다: {image_path}")
        return
    
    print("손바닥 이미지 분석 중...")
    result = analyze_palm_image(image_path, api_key)
    print("\n===== 분석 결과 =====\n")
    print(result)

if __name__ == "__main__":
    main()
