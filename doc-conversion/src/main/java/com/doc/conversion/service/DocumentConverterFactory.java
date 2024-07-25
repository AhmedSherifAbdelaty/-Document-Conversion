package com.doc.conversion.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DocumentConverterFactory {
    private final Map<String, DocumentConverter> converters = new HashMap<>();
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void setAllConverters() {
        converters.put("PDF" , applicationContext.getBean(WordToPDFConverter.class));
    }




    public  DocumentConverter getConverter(String documentType) {
        return converters.get(documentType.toUpperCase());
    }
}
