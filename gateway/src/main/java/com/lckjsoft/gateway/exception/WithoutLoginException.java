package com.lckjsoft.gateway.exception;

import java.io.Serializable;


/**
 * @author uid40330
 */
public class WithoutLoginException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -5395414930731458355L;

    private String msg;

    public WithoutLoginException(){
    }

    public WithoutLoginException(String msg){
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "WithoutLoginException{" +
                "msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
