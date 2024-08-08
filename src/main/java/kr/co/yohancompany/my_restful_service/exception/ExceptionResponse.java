package kr.co.yohancompany.my_restful_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private Date timestamp;
    private String message;
    private String details;

}
