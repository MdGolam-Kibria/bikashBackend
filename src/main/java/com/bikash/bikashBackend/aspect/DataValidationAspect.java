package com.bikash.bikashBackend.aspect;


import com.bikash.bikashBackend.View.Response;
import com.bikash.bikashBackend.View.ResponseBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Aspect
@Configuration
public class DataValidationAspect {
   private Logger logger = LoggerFactory.getLogger(DataValidationAspect.class.getName());

    @Around("@annotation(com.bikash.bikashBackend.annotation.ValidateData) && args(..)")
    public Response validateData(ProceedingJoinPoint joinPoint){
        Object[] signatures = joinPoint.getArgs();
        BindingResult result = null;
        for (int i = 0; i < signatures.length; i++) {
            if(signatures[i] instanceof BindingResult){
                result = (BindingResult) signatures[i];
                break;
            }
        }
        if(result.hasErrors()){
            return ResponseBuilder.getFailureResponce(result, "Bean Binding error");
        }
        try {
            return (Response) joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage());
            return ResponseBuilder.getFailureResponce(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
