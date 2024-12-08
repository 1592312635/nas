package com.minyan.nascommon.httpRequest;

import lombok.Data;

/**
 * @decription 代币订单确认参数
 * @author minyan.he
 * @date 2024/12/8 13:44
 */
@Data
public class CurrencyConfirmRequest {
  private String userId;
  private String orderNo;
  private Integer currencyType;
  private Integer confirmTag;
}
