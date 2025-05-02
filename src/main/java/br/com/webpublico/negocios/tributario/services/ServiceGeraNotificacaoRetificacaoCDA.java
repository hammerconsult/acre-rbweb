package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ServiceGeraNotificacaoRetificacaoCDA {

    @PersistenceContext
    protected transient EntityManager em;

    public void geraNotificacaoDeRetificacao(Pessoa pessoa) {
        Notificacao not = new Notificacao();
        not.setGravidade(Notificacao.Gravidade.INFORMACAO);
        not.setLink("/certida-divida-ativa/retificacao/" + pessoa.getId() + "/");
        not.setDescricao("Os dados da pessoa de CPF/CNPJ " + pessoa.getCpf_Cnpj() + " foram alterados, as certidões de dívida atíva devem ser retificadas. Acesse o link para efetuar a retificação.");
        not.setTitulo("Retificação de CDA");
        not.setTipoNotificacao(TipoNotificacao.COMUNICACAO_SOFTPLAN);
        NotificacaoService.getService().notificar(not);
    }



}
