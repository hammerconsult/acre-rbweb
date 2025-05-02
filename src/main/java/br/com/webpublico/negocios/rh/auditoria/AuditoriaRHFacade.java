package br.com.webpublico.negocios.rh.auditoria;

import br.com.webpublico.entidadesauxiliares.rh.auditoria.AssistenteAuditoriaRH;
import br.com.webpublico.entidadesauxiliares.rh.auditoria.ObjetoAuditoriaRH;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.negocios.jdbc.AuditoriaRHJDBC;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author octavio
 */
@Stateless
public class AuditoriaRHFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaRHFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public AuditoriaRHFacade() {
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<TreeMap<ObjetoAuditoriaRH, Object>> consultar(AssistenteAuditoriaRH assistente) {
        try {
            AuditoriaRHJDBC auditoraRhJDBC = (AuditoriaRHJDBC) Util.getSpringBeanPeloNome("auditoraRhJDBC");
            return auditoraRhJDBC.buscarAuditorias(assistente);
        } catch (Exception e) {
            logger.error("Erro ao consultar: ", e);
        }
        return Lists.newArrayList();
    }
}
