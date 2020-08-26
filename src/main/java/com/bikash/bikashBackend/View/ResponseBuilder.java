package com.bikash.bikashBackend.View;

import com.bikash.bikashBackend.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseBuilder {

    private ResponseBuilder() {
    };

    private static List<ErrorResponseDto> getCustomError(BindingResult result) {
        List<ErrorResponseDto> dtoList = new ArrayList<>();
        result.getFieldErrors().forEach(fieldError -> {
            ErrorResponseDto dto = ErrorResponseDto.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage()).build();
            dtoList.add(dto);
        });
        return dtoList;
    }

    public static Response getFailureResponce(BindingResult result, String message) {
        return Response.builder()
                .message(message)
                .errors(getCustomError(result))
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date().getTime())
                .build();
    }

    public static Response getFailureResponce(HttpStatus status, String message) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .timestamp(new Date().getTime())
                .build();
    }

    public static Response getSuccessResponce(HttpStatus status, String message, Object content) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .content(content)
                .statusCode(status.value())
                .timestamp(new Date().getTime())
                .build();
    }
    public static Response getSuccessResponseForTransactions(HttpStatus status, String message, Long transactionId) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .transactionId(transactionId)
                .statusCode(status.value())
                .timestamp(new Date().getTime())
                .build();
    }

    public static Response getSuccessResponce(HttpStatus status, String message, Object content, int numberOfElement) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .content(content)
                .numberOfElement(numberOfElement)
                .statusCode(status.value())
                .timestamp(new Date().getTime())
                .build();
    }

    public static Response getSuccessResponce(HttpStatus status, String message, Object content, int numberOfElement, long rowCount) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .content(content)
                .numberOfElement(numberOfElement)
                .statusCode(status.value())
                .rowCount(rowCount)
                .timestamp(new Date().getTime())
                .build();
    }

}
