package com.minyan.nascapi.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.service.HttpService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.httpRequest.CurrencySendRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @decription 远程调用请求
 * @author minyan.he
 * @date 2024/12/1 11:00
 */
@Service
public class HttpServiceImpl implements HttpService {
  private static final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);
  private static final String SEND_CURRENCY_URL = "/account/send";
  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public Boolean sendCurrency(CurrencySendRequest sendRequest) {
    // 设置HTTP头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 创建HTTP实体
    HttpEntity<CurrencySendRequest> entity = new HttpEntity<>(sendRequest, headers);

    // 发起远程调用
    try {
      String response = restTemplate.postForObject(SEND_CURRENCY_URL, entity, String.class);
      JSONObject jsonResponse = JSON.parseObject(response);
      if (!CodeEnum.SUCCESS.getCode().equals(jsonResponse.getString("code"))
          || !jsonResponse.getBoolean("data")) {
        logger.error("[HttpServiceImpl][sendCurrency]远程调用代币发放失败，响应：{}", response);
        return false;
      }
    } catch (Exception e) {
      logger.error("[HttpServiceImpl][sendCurrency]远程调用代币发放异常，异常信息：{}", e.getMessage());
      return false;
    }
    return true;
  }
}
