package com.bikash.bikashBackend.annotation;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@org.springframework.data.annotation.QueryAnnotation
@java.lang.annotation.Documented
public @interface NativeQuery {
    String value() default "";

    String countQuery() default "";

    String countProjection() default "";

    boolean nativeQuery() default true;//for direct query

    String name() default "";

    String countName() default "";
}
