package com.education.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.education.common.model.JwtToken;
import com.education.common.utils.ObjectUtils;
import com.education.common.utils.RequestUtils;
import com.education.common.utils.Result;
import com.education.common.utils.ResultCode;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2018/12/22 19:43
 */
public abstract class BaseInterceptor implements HandlerInterceptor {


    private static final String contentType = "application/json; charset=utf-8";
    private static final String contentTypeForIE = "text/html; charset=utf-8";
    private boolean forIE = false;


    protected void renderJson(HttpServletResponse response, Result result) {
        String dataJson = JSONObject.toJSONString(result);
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType(forIE ? contentTypeForIE : contentType);
            writer = response.getWriter();
            writer.write(dataJson);
            writer.flush();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验token 是否合法
     * @param jwtToken
     * @param request
     * @param response
     * @return
     */
    protected boolean checkToken(JwtToken jwtToken, HttpServletRequest request, HttpServletResponse response) {
        //获取token
        String token = request.getHeader("token");
        String userId = jwtToken.parseTokenToString(token);
        if (ObjectUtils.isEmpty(token) || ObjectUtils.isEmpty(userId)) { //token不存在 或者token失效
            renderJson(response, Result.fail(ResultCode.UN_AUTH_ERROR_CODE, "用户未认证"));
            return false;
        }
        return true;
    }


    /**
     * 获取json 参数值
     * @param request
     * @return
     */
    protected String readData(HttpServletRequest request) {
       return RequestUtils.readData(request);
    }
}
