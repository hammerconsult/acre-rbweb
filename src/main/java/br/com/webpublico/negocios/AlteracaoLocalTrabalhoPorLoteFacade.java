package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author octavio
 */
@Stateless
public class AlteracaoLocalTrabalhoPorLoteFacade extends AbstractFacade<AlteracaoLocalTrabalhoPorLote> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public AlteracaoLocalTrabalhoPorLoteFacade() {
        super(AlteracaoLocalTrabalhoPorLote.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public void criarProvimento(VinculoFP vinculoFP, AtoLegal atoLegal) {
        ProvimentoFP provimentoFPTransferencia = contratoFPFacade.adicionarProvimentoNoContrato(vinculoFP.getContratoFP(), UtilRH.getDataOperacao(), atoLegal, TipoProvimento.TRANSFERENCIA);
        vinculoFP = provimentoFPTransferencia.getVinculoFP().getContratoFP();
        LotacaoFuncional lotacaoFuncionalVigente = vinculoFP.getLotacaoFuncionalVigente();
        if (lotacaoFuncionalVigente != null) {
            lotacaoFuncionalVigente.setProvimentoFP(provimentoFPTransferencia);
        }
        em.merge(vinculoFP);
    }

    public List<AlteracaoVinculoLotacao> recuperarContratoVigentePorLotacao(HierarquiaOrganizacional unidade, AlteracaoLocalTrabalhoPorLote alteracao) {
        String sql = " select dados.id, dados.matricula, dados.nome from ( " +
            " select distinct vfp.id, " +
            " pf.nome as nome, " +
            " mfp.matricula " +
            " || '/' " +
            " || vfp.numero " +
            " || ' - ' " +
            " || pf.nome " +
            " || ' - ' " +
            " || uo.descricao as matricula " +
            " from lotacaofuncional lf " +
            " inner join vinculofp vfp on vfp.id = lf.vinculofp_id " +
            " inner join matriculafp mfp on mfp.id = vfp.matriculafp_id " +
            " inner join pessoafisica pf on pf.id = mfp.pessoa_id " +
            " inner join unidadeorganizacional uo on uo.id = lf.unidadeorganizacional_id " +
            " inner join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = uo.id " +
            " where :dataVigencia between lf.inicioVigencia and coalesce(lf.finalVigencia, :dataVigencia) " +
            " and :dataVigencia between vfp.inicioVigencia and coalesce(vfp.finalVigencia, :dataVigencia) " +
            " and :dataVigencia between vw.INICIOVIGENCIA and coalesce (vw.FIMVIGENCIA, :dataVigencia) " +
            " and vw.codigo like :codigo) dados " +
            " order by dados.nome ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("codigo", unidade.getCodigoSemZerosFinais() + "%");

        List<Object[]> resultado = q.getResultList();
        List<AlteracaoVinculoLotacao> item = Lists.newArrayList();

        for (Object[] obj : resultado) {
            AlteracaoVinculoLotacao alteracaoVinculoLotacao = new AlteracaoVinculoLotacao();
            alteracaoVinculoLotacao.setAlterLocTrabalhoLotacao(alteracao);
            alteracaoVinculoLotacao.setVinculoFP(new VinculoFP(((Number) obj[0]).longValue(), obj[1].toString()));
            item.add(alteracaoVinculoLotacao);
        }
        return item;
    }

    @Override
    public AlteracaoLocalTrabalhoPorLote recuperar(Object id) {
        AlteracaoLocalTrabalhoPorLote alteracao = em.find(AlteracaoLocalTrabalhoPorLote.class, id);
        alteracao.getAlteracoesVinculoLotacao().size();
        return alteracao;
    }

    public List<AlteracaoLocalTrabalhoPorLote> buscarAlteracoesLocalTrabalhoPorLotePorVinculo(VinculoFP vinculoFP) {
        String sql = " select distinct lote.* from ALTERACAOLOCALTRABPORLOTE lote " +
            " inner join ALTERACAOVINCULOLOTACAO itemVinculo on lote.ID = itemVinculo.ALTERLOCTRABALHOLOTACAO_ID " +
            " where itemVinculo.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, AlteracaoLocalTrabalhoPorLote.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
