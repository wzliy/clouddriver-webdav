package com.zlin.clouddriver.webdav.exceptions;

public class WebdavException extends RuntimeException{
    public WebdavException() {
        super();
    }

    public WebdavException(String message) {
        super(message);
    }

    public WebdavException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebdavException(Throwable cause) {
        super(cause);
    }
}
