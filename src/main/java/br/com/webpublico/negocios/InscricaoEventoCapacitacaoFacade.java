package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author AndreGustavo
 * on 30/10/2014.
 */
@Stateless
public class InscricaoEventoCapacitacaoFacade extends AbstractFacade<InscricaoCapacitacao> {

    private static final Logger logger = LoggerFactory.getLogger(InscricaoEventoCapacitacaoFacade.class);

    @EJB
    private EventoCapacitacaoFacade eventoCapacitacaoFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public InscricaoEventoCapacitacaoFacade() {
        super(InscricaoCapacitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviaEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    public void salvar(List<InscricaoCapacitacao> inscricoesCapacitacao, List<InscricaoCapacitacao> inscricoesCapacitacaoExcluidas) {
        for (InscricaoCapacitacao inscricaoCapacitacao : inscricoesCapacitacao) {
            em.merge(inscricaoCapacitacao);
        }

        for (InscricaoCapacitacao inscricaoCapacitacao : inscricoesCapacitacaoExcluidas) {
            em.remove(em.merge(inscricaoCapacitacao));
        }
    }

    public void salvarNota(List<NotaModulo> notaModulos) {
        for (NotaModulo notaModulo : notaModulos) {
            if (notaModulo.getId() == null) {
                em.persist(notaModulo);
            } else {
                em.merge(notaModulo);
            }
        }
    }

    public void salvar(List<PresencaModulo> presencaModulos) {
        for (PresencaModulo presencaModulo : presencaModulos) {
            if (presencaModulo.getId() == null) {
                em.persist(presencaModulo);
            } else {
                em.merge(presencaModulo);
            }
        }
    }

    public List<PresencaModulo> recuperarPresencaPorDia(Date data, ModuloCapacitacao moduloCapacitacao) {
        Query sql = em.createNativeQuery(" SELECT * FROM PRESENCAMODULO "
            + " WHERE DIA = :DATA AND MODULOCAPACITACAO_ID = :moduloCapacitacao ", PresencaModulo.class);
        sql.setParameter("DATA", data);
        sql.setParameter("moduloCapacitacao", moduloCapacitacao.getId());

        return sql.getResultList();
    }

    public List<NotaModulo> recuperarNota(ModuloCapacitacao moduloCapacitacao) {
        Query sql = em.createNativeQuery(" SELECT * FROM NOTAMODULO "
            + " WHERE MODULOCAPACITACAO_ID = :moduloCapacitacao ", NotaModulo.class);
        sql.setParameter("moduloCapacitacao", moduloCapacitacao.getId());

        return sql.getResultList();
    }

    public List<MatriculaFP> buscarInscricoesEventoCapacitacao(Capacitacao capacitacao) {
        Query sql = em.createNativeQuery(" SELECT mat.* FROM InscricaoCapacitacao inscricao inner join matriculaFP mat on mat.id = inscricao.matriculafp_id " +
            "                       inner join capacitacao cap on cap.id = inscricao.capacitacao_id "
            + "                     WHERE cap.id = :capacitacao ", MatriculaFP.class);
        sql.setParameter("capacitacao", capacitacao.getId());
        return sql.getResultList();
    }


    public EventoCapacitacaoFacade getEventoCapacitacaoFacade() {
        return eventoCapacitacaoFacade;
    }

    public MatriculaFPFacade getMatriculaFPFacade() {
        return matriculaFPFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

}
