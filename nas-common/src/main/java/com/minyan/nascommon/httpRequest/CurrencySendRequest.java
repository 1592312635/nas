package com.minyan.nascommon.httpRequest;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @decription 代币发放请求参数
 * @author minyan.he
 * @date 2024/12/1 11:02
 */
@Data
public class CurrencySendRequest {
  private String userId;
  private BigDecimal addCurrency;
  private Integer currencyType;
  private String businessId;
  private String behaviorCode;
  private String behaviorDesc;
}
