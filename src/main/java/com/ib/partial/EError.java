package com.ib.partial;

public interface EError {

    void error(Exception e);

    void error(String str);

    void error(int id, int errorCode, String errorMsg);
}
