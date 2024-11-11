package com.minyan.nascapi.aspect;

import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @decription 全局异常处理器
 * @author minyan.he
 * @date 2024/11/11 20:51
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ApiResult<?> businessExceptionHandler(CustomException e) {
    return ApiResult.build(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ApiResult<?> runtimeExceptionHandler(RuntimeException e) {
    return ApiResult.build(CodeEnum.SYSTEM_ERROR);
  }
}
