package br.com.webpublico.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utilize esse RestTemplate caso queira que as anotações do com.fasterxml.jackson.* sejam levadas em conta na serialização e deserialização do json
 */
public class RestTemplateAnotacoes extends RestTemplate {

    public RestTemplateAnotacoes() {
        super();
        for (HttpMessageConverter<?> messageConverter : new ArrayList<>(this.getMessageConverters())) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                this.getMessageConverters().set(this.getMessageConverters().indexOf(messageConverter), new JSONMapping(new ObjectMapper()));
            }
        }
    }

    private class JSONMapping extends MappingJackson2HttpMessageConverter {

        private ObjectMapper objectMapper;

        public JSONMapping(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            objectMapper.writeValue(outputMessage.getBody(), object);
        }

        @Override
        protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            try {
                return this.objectMapper.readerFor(clazz).readValue(inputMessage.getBody());
            } catch (JsonProcessingException ex) {
                throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
    }
}
