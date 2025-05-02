package br.com.webpublico.config;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomObjectMapper extends ObjectMapper {

    private static final Logger log = LoggerFactory.getLogger(CustomObjectMapper.class);


    @PostConstruct
    public void init() {
        SimpleModule module = new SimpleModule("SimpleModule",
            new Version(1, 9, 13, null));
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(Date.class, new DateDeserializer());
        this.registerModule(module);
        super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public class DateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            gen.writeString(sdf.format(value));
        }
    }


    @Override
    public <T> T readValue(JsonParser jp, JavaType valueType) throws IOException {
        try {
            return super.readValue(jp, valueType);
        } catch (Exception e) {
            log.error("error readValue {}", e);
        }
        return null;
    }

    public class DateDeserializer extends JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

            JsonToken t = jsonParser.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return new Date(jsonParser.getLongValue());
            }
            try {
                return converterData(jsonParser, "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                //TODO removido loger de dataparsing
            }
            try {
                Date date = converterData(jsonParser, "yyyy-MM-dd");
                return date;
            } catch (ParseException e) {
                log.error("Erro ao deserializar data {}", e.getMessage() + " campo: " + jsonParser.getCurrentName());
            }
            return null;
        }
    }

    private Date converterData(JsonParser jsonParser, String s) throws ParseException, IOException {
        SimpleDateFormat FORMATTER = new SimpleDateFormat(s);
        Date data = FORMATTER.parse(jsonParser.getText());
        return data;
    }
}
