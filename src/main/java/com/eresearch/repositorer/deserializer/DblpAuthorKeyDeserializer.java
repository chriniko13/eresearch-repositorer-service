package com.eresearch.repositorer.deserializer;

import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class DblpAuthorKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return new DblpAuthor(key);
    }
}
