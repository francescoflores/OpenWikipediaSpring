package com.open.openwikipedia.common.Exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomException extends Exception {
    public HttpStatus status;

    public CustomException(HttpStatus status, String message){
        super(message);
        this.status = status;
    }
}
