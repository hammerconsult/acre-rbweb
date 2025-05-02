package br.com.webpublico.pncp.service;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.pncp.dto.EventoPncpDto;
import br.com.webpublico.pncp.entidade.EventoPncp;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class EventoPncpService extends PncpService {

    private final String urlEntidadePncp = "/api/v1/evento";

    public void enviar(EventoPncp evento, ValidacaoException ve) {
        try {
            EventoPncpDto dto = new EventoPncpDto(evento);
            restTemplate.postForEntity(getBaseUrl(ve) + urlEntidadePncp, dto, String.class);
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }
}
