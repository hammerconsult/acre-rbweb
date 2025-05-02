package br.com.webpublico.handlers;

import br.com.webpublico.exception.ValidacaoException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.List;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final String HEADER_MESSAGE_ERROR = "X-message-error";

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
            || clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) {
        List<String> messages = clientHttpResponse.getHeaders().get(HEADER_MESSAGE_ERROR);
        throw new ValidacaoException(StringUtils.join(messages, ", "));
    }
}
