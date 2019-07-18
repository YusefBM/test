package com.github.dperezcabrera.test.architecture.error;

import com.github.dperezcabrera.test.architecture.common.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {DeniedPermissionException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MessageDto deniedPermissionException(DeniedPermissionException ex) {
        log.error("deniedPermissionException ",ex);
        return new MessageDto("Denied permission");
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageDto internalServerError(Exception ex) {
        log.error("internalServerError ",ex);
        return new MessageDto("Internal error");
    }
}
