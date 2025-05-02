package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Leonardo
 */
@Stateless
public class PromocaoFacade extends AbstractFacade<Promocao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PromocaoFacade() {
        super(Promocao.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public PlanoCargosSalariosFacade getPlanoCargosSalariosFacade() {
        return planoCargosSalariosFacade;
    }

    public ProvimentoFPFacade getProvimentoFPFacade() {
        return provimentoFPFacade;
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public void salvarNovo(Promocao entity, AtoLegal ato, EnquadramentoFuncional anterior, EnquadramentoFuncional atual) {
        ProvimentoFP provimento = getNovoProvimentoFPTipoPromocao(ato, atual);
        atual.setProvimentoFP(em.merge(provimento));

        anterior.setFinalVigencia(DataUtil.getDataDiaAnterior(atual.getInicioVigencia()));
        anterior.setDataCadastroFinalVigencia(atual.getDataCadastro());

        entity.setEnquadramentoAnterior(em.merge(anterior));
        entity.setEnquadramentoNovo(em.merge(atual));
        super.salvarNovo(entity);
    }

    public ProvimentoFP getNovoProvimentoFPTipoPromocao(AtoLegal ato, EnquadramentoFuncional atual) {
        ProvimentoFP provimento = new ProvimentoFP();
        provimento.setAtoLegal(ato);
        provimento.setContratoFP(atual.getContratoServidor());
        provimento.setDataProvimento(atual.getDataCadastro());
        provimento.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PROMOCAO));
        provimento.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(atual.getContratoServidor()));
        provimento.setVinculoFP(atual.getContratoServidor());
        return provimento;
    }

    @Override
    public void remover(Promocao entity) {
        EnquadramentoFuncional efAnterior = entity.getEnquadramentoAnterior();
        efAnterior.setDataCadastroFinalVigencia(null);
        efAnterior.setFinalVigencia(null);
        em.merge(efAnterior);

        EnquadramentoFuncional efARemover = entity.getEnquadramentoNovo();

        enquadramentoFuncionalFacade.remover(efARemover);
        provimentoFPFacade.remover(efARemover.getProvimentoFP());

        super.remover(entity);
    }

    public Promocao buscarPromocaoPorContrato(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from Promocao pro "
                + " inner join pro.enquadramentoNovo ef "
                + " inner join ef.contratoServidor con "
                + " where con = :contrato ");
            q.setParameter("contrato", contratoFP);
            return (Promocao) q.getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("nenhuma promocao encontrada para buscarPromocaoPorContrato " + contratoFP + " erro: " + nre.getMessage());
            return null;
        }
    }
}
