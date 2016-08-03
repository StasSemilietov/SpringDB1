package com.red.aop.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Steven on 30.07.2016.
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }
    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println(o + "  -------  postProcessAfterInitialization");
        return o;
    }
}
