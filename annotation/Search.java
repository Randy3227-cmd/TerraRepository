package quickcrud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Search {
    String[] value(); // The name of the value that we will search
    String[] comparison() default {}; // A list that we will use to search from the table
    String logic() default "AND";
}
