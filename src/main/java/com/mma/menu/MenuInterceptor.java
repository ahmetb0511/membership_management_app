package com.mma.menu;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MenuInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Item item = null;
			
			Menu menu = handlerMethod.getMethodAnnotation(Menu.class);
			if (menu == null) {
				menu = handlerMethod.getBean().getClass().getAnnotation(Menu.class);
			}
			
			if (menu != null) {
				item = menu.value();
			}
			
			if (item == null) {
				MenuController menuController = handlerMethod.getBean().getClass().getAnnotation(MenuController.class);
				if (menuController != null) {
					item = menuController.item();
				}
			}
			
			if ((item != null) && (modelAndView != null)) {
				modelAndView.addObject("menuItem", item.toString().toLowerCase());
			}
		}
	}
	
}
