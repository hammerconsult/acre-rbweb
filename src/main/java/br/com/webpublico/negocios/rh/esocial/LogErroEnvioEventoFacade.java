package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.DetalheLogErroEnvioEvento;
import br.com.webpublico.entidades.rh.esocial.LogErroEnvioEvento;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Stateless
public class LogErroEnvioEventoFacade extends AbstractFacade<LogErroEnvioEvento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LogErroEnvioEventoFacade() {
        super(LogErroEnvioEvento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DetalheLogErroEnvioEvento> buscarDetalhesLog(ConfiguracaoEmpregadorESocial config) {
        String sql = "select detalha.* from LOGERROENVIOEVENTO log " +
            " inner join DETALHELOGERROENVIOEVENTO detalha on log.ID = detalha.LOGERROENVIOEVENTO_ID" +
            " where log.empregador_id = :config order by log.dataLog desc ";
        Query q = em.createNativeQuery(sql, DetalheLogErroEnvioEvento.class);
        q.setParameter("config", config.getId());
        q.setMaxResults(2000);
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return result;
        }
        return null;
    }

    public List<LogErroEnvioEvento> buscarLogByIdentificador(List<Long> id, ConfiguracaoEmpregadorESocial config) {
        String sql = "select * from LOGERROENVIOEVENTO where IDENTIFICADOR = :id and empregador_id = :idEmpregador";
        Query q = em.createNativeQuery(sql, LogErroEnvioEvento.class);
        q.setParameter("id", id);
        q.setParameter("idEmpregador", config.getId());
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return result;
        }
        return null;
    }

    public void removerLogErroEnvioEvento(List<Long> idsEventoFP, ConfiguracaoEmpregadorESocial config) {
        List<LogErroEnvioEvento> logs = buscarLogByIdentificador(idsEventoFP, config);
        for (LogErroEnvioEvento log : logs) {
            remover(log);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogErroEnvioEvento criarLogErro(String nomeCLasse, Long identificador, ConfiguracaoEmpregadorESocial config, TipoArquivoESocial tipoArquivoESocial, ValidacaoException ve) {
        LogErroEnvioEvento log = new LogErroEnvioEvento();
        log.setDataLog(new Date());
        log.setClasse(nomeCLasse);
        log.setIdentificador(identificador);
        log.setTipoArquivoESocial(tipoArquivoESocial);
        log.setEmpregador(config);

        for (FacesMessage mensagen : ve.getMensagens()) {
            DetalheLogErroEnvioEvento detalhe = new DetalheLogErroEnvioEvento();
            detalhe.setLog(mensagen.getSummary() + " - " + mensagen.getDetail());
            detalhe.setLogErroEnvioEvento(log);
            log.getItemDetalheLog().add(detalhe);
        }
        if (!log.getItemDetalheLog().isEmpty()) {
            return em.merge(log);
        }
        return null;
    }
}
