package com.mastercard.mpqr.web;

import com.mastercard.mpqr.exception.MasterCardException;
import com.mastercard.mpqr.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * General Exception handler
 */
@ControllerAdvice
public class ExceptionController {

    /**
     * Catches MasterCardException and transform it to ErrorResponse
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MasterCardException.class)
    public ResponseEntity<?> handleSQLException(MasterCardException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getSource(), e.getReasonCode(), e.getMessage());
        Integer status = e.getHttpStatus() == 0 ? HttpStatus.BAD_REQUEST.value() : e.getHttpStatus();
        return ResponseEntity.status(status).body(errorResponse);
    }

}
