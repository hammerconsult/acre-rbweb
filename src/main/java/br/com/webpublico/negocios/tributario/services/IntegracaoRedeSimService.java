package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.EventoRedeSim;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.IntegracaoRedeSimFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class IntegracaoRedeSimService {

    private static final Logger logger = LoggerFactory.getLogger(IntegracaoRedeSimService.class);

    private ObjectMapper mapper = new ObjectMapper();
    private boolean executando = false;
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    private SistemaFacade sistemaFacade;
    private HashSet<String> cnpjsEmProcessamento = new HashSet<>();

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public IntegracaoRedeSimFacade getIntegracaoRedeSimFacade() {
        return integracaoRedeSimFacade;
    }

    @PostConstruct
    private void init() {
        try {
            integracaoRedeSimFacade = (IntegracaoRedeSimFacade) new InitialContext().lookup("java:module/IntegracaoRedeSimFacade");
            sistemaFacade = (SistemaFacade) Util.getFacadeViaLookup("java:module/SistemaFacade");
        } catch (NamingException e) {
            logger.error("Injecao de facades falhou {}", e);
        }
    }

    public void processarEventosRedeSim() {
        if (executando) {
            return;
        }
        try {
            executando = true;
            logger.debug("Integração Redesim. Iniciado.");
            List<EventoRedeSim> eventosNaoProcessados = getProximosEventeRedeSim();
            if (eventosNaoProcessados == null || eventosNaoProcessados.isEmpty()) {
                logger.debug("Integração Redesim. Buscando eventos na RedeSim.");
                integracaoRedeSimFacade.buscarEventosRedeSim();
                eventosNaoProcessados = getProximosEventeRedeSim();
            }
            if (eventosNaoProcessados != null && !eventosNaoProcessados.isEmpty()) {
                for (EventoRedeSim eventoRedeSim : eventosNaoProcessados) {
                    logger.debug("Integração Redesim. Iniciou -> Identificador {}.", eventoRedeSim.getIdentificador());
                    integrarEventoRedeSim(eventoRedeSim, null, sistemaFacade.getUsuarioBancoDeDados(),
                        false, false);
                    logger.debug("Integração Redesim. Finalizou -> Identificador {}.", eventoRedeSim.getIdentificador());
                }
            }
            logger.debug("Integração Redesim. Finalizado.");
        } catch (Exception e) {
            logger.error("Integração Redesim. Erro", e);
        } finally {
            executando = false;
        }
    }

    public void integrarEventoRedeSim(EventoRedeSim eventoRedeSim,
                                      UsuarioSistema usuarioSistema,
                                      String usuarioBancoDados,
                                      boolean criarNovoCadastro,
                                      boolean atualizarEnquadramentoFiscal) throws IOException {
        if (cnpjsEmProcessamento.contains(eventoRedeSim.getCnpj())) {
            throw new ValidacaoException("O CNPJ está sendo integrado com a REDESIM.");
        }
        try {
            cnpjsEmProcessamento.add(eventoRedeSim.getCnpj());
            integracaoRedeSimFacade.processarEventoRedeSim(eventoRedeSim, usuarioSistema, usuarioBancoDados,
                criarNovoCadastro, atualizarEnquadramentoFiscal);
            integracaoRedeSimFacade.gerarBcmAndConfirmarResposta(eventoRedeSim);
            integracaoRedeSimFacade.alterarParaProcessado(eventoRedeSim);
        } finally {
            cnpjsEmProcessamento.remove(eventoRedeSim.getCnpj());
        }
    }

    public List<EventoRedeSim> getProximosEventeRedeSim() {
        logger.debug("Integração RedeSim. Buscando próximos eventos não processados.");
        return integracaoRedeSimFacade.buscarEventosNaoProcessados(0, 50, null, null, null);
    }

    public void buscarEventosRedeSim() {
        integracaoRedeSimFacade.buscarEventosRedeSim();
    }
}
