package courier.uy.core.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
public @interface AdminAuthorization {

}
