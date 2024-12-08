package com.minyan.nascommon.httpRequest;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @decription 代币扣减参数
 * @author minyan.he
 * @date 2024/12/8 12:04
 */
@Data
public class CurrencyDeductRequest {
  private String userId;
  private BigDecimal deductCurrency;
  private Integer currencyType;
  private String businessId;
  private String behaviorCode;
  private String behaviorDesc;
}
