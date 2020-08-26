package com.bikash.bikashBackend.annotation;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@org.springframework.stereotype.Controller
@org.springframework.web.bind.annotation.ResponseBody
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
public @interface ApiController {
    @org.springframework.core.annotation.AliasFor(annotation = org.springframework.stereotype.Controller.class)
    String value() default "";
}