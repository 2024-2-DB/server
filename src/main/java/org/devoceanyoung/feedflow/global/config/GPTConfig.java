package org.devoceanyoung.feedflow.global.config;

import java.time.LocalDateTime;

public class GPTConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_MODEL = "gpt-4";
    public static final Integer MAX_TOKEN = 1024;
    public static final Boolean STREAM = true;
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final Double TEMPERATURE = 0.3;
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
    public static final String RESPONSE_NODE_AT = "/choices/0/delta/content";



    // 시스템 프롬프트 정의
    private static final String systemPrompts = """
       너는 팀에 대한 피드백을 분석하고 개선사항을 제안하는 챗봇이야.
    사용자가 팀에 대해 긍정적 또는 부정적인 피드백을 줄 때, 너는 이를 분석하여 
    1) 긍정적인 부분은 강화할 수 있도록 칭찬하거나 팀의 강점을 강조하고,
    2) 부정적인 부분은 개선 방안을 제시하며, 팀이 발전할 수 있도록 조언을 해줘.

    사용자가 피드백을 작성할 때 참고할 수 있는 가이드도 제공해주면 좋겠어:
    - 긍정적인 피드백은 무엇인지, 그 이유는 무엇인지
    - 개선이 필요하다고 생각하는 점과 그 이유는 무엇인지

    예시: 
    "팀이 협업을 잘하지만 커뮤니케이션에 개선이 필요해요."
    위와 같은 피드백을 받으면, 칭찬과 개선 방안을 함께 제안해줘.

    피드백 예시:
    - 긍정적인 부분: 팀원들이 서로 협력하는 부분에 대해 칭찬해주세요.
    - 개선이 필요한 부분: 커뮤니케이션에서 어떤 점을 어떻게 개선할 수 있을지 제안해주세요.

    사용자가 피드백을 입력할 준비가 되면 '피드백을 입력해주세요.'라고 안내해줘.
        """;

    public static String getSystemPrompts(boolean isFirst) {
        if (isFirst) {
            return "안녕하세요, 저는 피플이입니다. 어떤 도움을 드릴까요?";
        }
        return systemPrompts + "\n현재 날짜: " + LocalDateTime.now();
    }
}
