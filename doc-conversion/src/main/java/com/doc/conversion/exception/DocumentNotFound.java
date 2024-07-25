package com.doc.conversion.exception;

public class DocumentNotFound extends Exception {

    public DocumentNotFound(String fileIsEmpty) {
        super(fileIsEmpty);
    }
}
