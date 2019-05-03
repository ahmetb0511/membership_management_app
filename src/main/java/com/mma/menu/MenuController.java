package com.mma.menu;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@Menu(Item.Users)
@RequestMapping
public @interface MenuController {

	@AliasFor(annotation = Menu.class, attribute = "value")
	Item item();
	
	@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};
	
}
