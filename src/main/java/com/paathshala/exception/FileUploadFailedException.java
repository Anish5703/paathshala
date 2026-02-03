package com.paathshala.exception;

import java.io.IOException;

public class FileUploadFailedException extends  RuntimeException {
    public FileUploadFailedException(String message)
    {
        super(message);
    }
}
