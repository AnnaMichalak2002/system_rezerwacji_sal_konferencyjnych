package edu.zut.projekt.exception;

import edu.zut.projekt.model.ErrorResponse;
import edu.zut.projekt.model.ResponseHeader;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ApiErrorResponseFactory {

    public ErrorResponse build(String code, String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setResponseHeader(buildResponseHeader());
        errorResponse.setCode(code);
        errorResponse.setMessage(message);
        return errorResponse;
    }

    private ResponseHeader buildResponseHeader() {
        ResponseHeader header = new ResponseHeader();
        header.setRequestId(UUID.randomUUID());
        header.setSendDate(OffsetDateTime.now());
        return header;
    }
}