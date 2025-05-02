package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.negocios.tributario.services.ServiceDAM;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

public class ListenerAlteraSituacaoParcela {
    @PostPersist
    public void alteraSituacao(SituacaoParcelaValorDivida situacao) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        ServiceDAM serviceDAM = (ServiceDAM) ap.getBean("serviceDAM");
        serviceDAM.alterarSituacaoDamsDaParcela(situacao);
    }

    @PrePersist
    public void geraReferencia(SituacaoParcelaValorDivida situacao) {
        situacao.geraReferencia();
    }
}
