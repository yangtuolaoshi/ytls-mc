package icu.ytlsnb.ytls.integration.ai;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 外服 HTTP 调用封装；仅客户端 {@link icu.ytlsnb.ytls.client.ClientChatHandler} 使用。
 * 后续可改为配置项驱动 URL、超时与鉴权。
 */
public final class AiChatClient {
    private static final String API_URL = "http://localhost:8000/api/agent-person/chat";

    private AiChatClient() {
    }

    public static String call(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("userPrompt", prompt);
        String result = HttpUtil.post(API_URL, JSONUtil.toJsonStr(body));
        if (result == null) {
            return "请求失败";
        }
        JSONObject json = JSONUtil.parseObj(result);
        Integer code = json.getInt("code");
        if (code != 200) {
            return json.getStr("message");
        }
        return json.getStr("data");
    }
}
