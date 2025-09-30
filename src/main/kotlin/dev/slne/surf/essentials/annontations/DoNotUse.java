package dev.slne.surf.essentials.annontations;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;

@TypeQualifierDefault({ElementType.METHOD, ElementType.PACKAGE, ElementType.MODULE, ElementType.TYPE})
public @interface DoNotUse {
    String value() default "";
}
