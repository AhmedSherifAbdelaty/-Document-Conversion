package com.doc.conversion.exception;

public class InvalidDocumentException extends Exception {

    public InvalidDocumentException(String fileIsEmpty) {
        super(fileIsEmpty);
    }
}
