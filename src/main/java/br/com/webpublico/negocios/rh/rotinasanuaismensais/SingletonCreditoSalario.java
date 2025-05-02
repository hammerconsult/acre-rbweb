package br.com.webpublico.negocios.rh.rotinasanuaismensais;

import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.negocios.CreditoSalarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;


@Stateless
public class SingletonCreditoSalario implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SingletonCreditoSalario.class);
    private Integer ultimoSequencial;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CreditoSalarioFacade creditoSalarioFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @PostConstruct
    private void init() {
        ultimoSequencial = buscarUltimoSequencial();
    }

    private Integer buscarUltimoSequencial() {
        Query q = em.createQuery("select max(m.sequencial) from ItemCreditoSalario m");
        q.setMaxResults(1);
        try {
            Integer retorno = 0;
            if (!q.getResultList().isEmpty()) {
                retorno = (Integer) q.getResultList().get(0);
            }
            if (retorno == null) {
                ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(new Date());
                if (configuracaoRH.getConfiguracaoCreditoSalario() == null || (configuracaoRH.getConfiguracaoCreditoSalario() != null && configuracaoRH.getConfiguracaoCreditoSalario().getSequencialInicial() == null)) {
                    return 0;
                }
                return configuracaoRH.getConfiguracaoCreditoSalario().getSequencialInicial();
            }
            return retorno;
        } catch (NoResultException nre) {
            logger.error("Erro: ", nre);
        }
        return 0;
    }

    //@Lock(LockType.WRITE)
    public Integer getProximoSequencial() {
        return ultimoSequencial + 1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Integer getProximoCodigo() {
        return buscarUltimoSequencial() + 1;
    }

    //@Lock(LockType.WRITE)
    public void delete(CreditoSalario creditoSalario) {
        try {
            em.remove(creditoSalario);
        } catch (Exception e) {
            logger.error("Erro ao delete crédito e salário ", e);
        }
    }

    //@Lock(LockType.WRITE)
    public void recarregarSequencial() {
        ultimoSequencial = buscarUltimoSequencial();
    }

    /*@Lock(LockType.WRITE)
    public String gravaMatricula(CreditoSalario matricula) {
        if (matricula == null) {
            //System.out.println("A MatriculaFP não pode ser null");
        }
        //É registro novo, então muda a matricula do cabra
        if (matricula.getId() == null) {
            matricula.setMatricula(String.valueOf(getProximaMatricula()));
            creditoSalarioFacade.salvarNovo(matricula);
            ultimoSequencial++;
        }
        return String.valueOf(ultimoSequencial);
    }*/
}
