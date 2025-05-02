package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BloqueioTransferenciaProprietario;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class BloqueioTransferenciaProprietarioFacade extends AbstractFacade<BloqueioTransferenciaProprietario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public BloqueioTransferenciaProprietarioFacade() {
        super(BloqueioTransferenciaProprietario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void preSave(BloqueioTransferenciaProprietario entidade) {
        entidade.realizarValidacoes();
        if (entidade.getId() == null) {
            entidade.setCodigo(singletonGeradorCodigo.getProximoCodigo(BloqueioTransferenciaProprietario.class, "codigo"));
        }
    }

    public List<BloqueioTransferenciaProprietario> buscarBloqueiosPorCadastro(CadastroImobiliario cadastroImobiliario) {
        return em.createQuery(" from BloqueioTransferenciaProprietario " +
                " where cadastroImobiliario = :cadastroImobiliario " +
                " order by codigo ")
            .setParameter("cadastroImobiliario", cadastroImobiliario)
            .getResultList();
    }

    public BloqueioTransferenciaProprietario buscarBloqueioPorCadastroDataReferencia(CadastroImobiliario cadastroImobiliario,
                                                                                     Date dataReferencia) {
        List resultList = em.createNativeQuery(" select * from BloqueioTransferenciaProprietario b " +
                " where b.cadastroimobiliario_id = :cadastroImobiliarioId " +
                "   and trunc(:dataReferencia) between trunc(b.dataInicial) " +
                "   and trunc(coalesce(b.dataFinal, :dataReferencia)) ", BloqueioTransferenciaProprietario.class)
            .setParameter("cadastroImobiliarioId", cadastroImobiliario.getId())
            .setParameter("dataReferencia", dataReferencia)
            .getResultList();
        return !resultList.isEmpty() ? (BloqueioTransferenciaProprietario) resultList.get(0) : null;
    }

    public void validarBloqueioTransferenciaProprietario(CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario != null) {
            BloqueioTransferenciaProprietario bloqueioTransferenciaProprietario =
                buscarBloqueioPorCadastroDataReferencia(cadastroImobiliario, getSistemaFacade().getDataOperacao());
            if (bloqueioTransferenciaProprietario != null) {
                throw new ValidacaoException("O cadastro imobiliário " + cadastroImobiliario +
                    " possui um bloqueio de transferência de proprietário. Motivo do bloqueio: " +
                    bloqueioTransferenciaProprietario.getMotivo());
            }
        }
    }

}
