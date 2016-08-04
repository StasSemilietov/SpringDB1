package com.red.aop.exception;

import org.springframework.dao.DataAccessException;

/**
 * Created by Steven on 03.08.2016.
 */
public class MyException extends DataAccessException {

    /**
     *
     */
    private static final long serialVersionUID = 5394203711238298583L;

    public MyException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }

}