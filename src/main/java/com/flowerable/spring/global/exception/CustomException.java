package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}