package com.mastercard.mpqr.exception;


import com.mastercard.api.core.exception.ApiException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * General mastercard exception
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class MasterCardException extends RuntimeException {

    String source;
    String reasonCode;
    int httpStatus;

    public MasterCardException(ApiException e) {
        super(e.getMessage());
        source = e.getSource();
        reasonCode = e.getReasonCode();
        httpStatus = e.getHttpStatus();
    }

    public MasterCardException(Exception e) {
        super(e.getMessage());
        reasonCode = null;
        httpStatus = HttpStatus.BAD_REQUEST.value();
    }
}
