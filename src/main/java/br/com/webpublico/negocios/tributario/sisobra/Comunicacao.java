package br.com.webpublico.negocios.tributario.sisobra;

import br.com.webpublico.tributario.dto.sisobra.LoteAlvaraHabitese;
import br.com.webpublico.tributario.dto.sisobra.RetornoLoteAlvaraHabitese;
import br.com.webpublico.util.RestTemplateAnotacoes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Comunicacao {

    private String url;
    private RestTemplate restTemplate;

    public Comunicacao(String url) {
        this.url = url;
        this.restTemplate = new RestTemplateAnotacoes();
    }

    public ResponseEntity<RetornoLoteAlvaraHabitese> enviarLoteAlvaraHabitese(LoteAlvaraHabitese loteAlvaraHabitese) {
        return restTemplate.postForEntity(url + "/enviarLoteAlvaraHabitese", loteAlvaraHabitese, RetornoLoteAlvaraHabitese.class);
    }
}
