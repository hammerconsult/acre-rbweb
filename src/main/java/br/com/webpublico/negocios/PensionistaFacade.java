/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFundamentacao;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author boy
 */
@Stateless
public class PensionistaFacade extends AbstractFacade<Pensionista> {

    @EJB
    TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    ProvimentoFPFacade provimentoFPFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PensionistaFacade() {
        super(Pensionista.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvar(Pensao entity) {
        insereNovosProvimentos(entity);
        em.merge(entity);
    }

    public void salvarNovo(Pensao entity) {
        insereNovosProvimentos(entity);
        em.persist(entity);
    }

    public void insereNovosProvimentos(Pensao pensao) {
        ProvimentoFP provimentoFP;
        ContratoFP contratoFP;
        for (Pensionista p : pensao.getListaDePensionistas()) {
            if (p.getId() == null) {
                contratoFP = p.getPensao().getContratoFP();
                provimentoFP = new ProvimentoFP();
                provimentoFP.setAtoLegal(pensao.getPensaoAtoLegal().get(0).getAtoLegal());
                provimentoFP.setVinculoFP(contratoFP);
                provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
                provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PENSIONISTAS));
                provimentoFP.setDataProvimento(p.getInicioVigencia());
                provimentoFP.setObservacao("Provimento para Pensionista");
                em.persist(provimentoFP);
                p.setProvimentoFP(provimentoFP);
            }
        }
        for (Pensionista p : pensao.getListaDePensionistas()) {
            for (InvalidezPensao i : p.getListaInvalidez()) {
                if (i.getId() == null) {
                    contratoFP = i.getPensionista().getPensao().getContratoFP();
                    provimentoFP = new ProvimentoFP();
                    provimentoFP.setAtoLegal(pensao.getPensaoAtoLegal().get(0).getAtoLegal());
                    provimentoFP.setVinculoFP(contratoFP);
                    provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
                    provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PENSIONISTAS));
                    provimentoFP.setDataProvimento(i.getInicioVigencia());
                    provimentoFP.setObservacao("Provimento para Invalidez");
                    em.persist(provimentoFP);
                    i.setProvimentoFP(provimentoFP);
                }
            }
        }
    }

    public void fecharVigenciaPensionista(PessoaFisica pf, Date finalVigencia) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("  from Pensionista pensionista");
        queryString.append(" where pensionista.matriculaFP.pessoa = :pessoa");
        queryString.append("   and :data >= pensionista.inicioVigencia");
        queryString.append("   and :data <= coalesce(pensionista.finalVigencia, :data)");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("pessoa", pf);
        q.setParameter("data", finalVigencia);

        for (Pensionista pensionista : (List<Pensionista>) q.getResultList()) {
            pensionista.setFinalVigencia(finalVigencia);
            em.merge(pensionista);
        }
    }

    public ItemValorPensionista recuperaItemValorPensionistaVigente(Pensionista pensionista) {
        try {
            String hql = "select item from ItemValorPensionista item" +
                " where item.pensionista = :pensionista " +
                " and :dataVigencia between item.inicioVigencia and coalesce(item.finalVigencia, :dataVigencia)";
            Query q = em.createQuery(hql);
            q.setParameter("dataVigencia", UtilRH.getDataOperacao());
            q.setParameter("pensionista", pensionista);
            return (ItemValorPensionista) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Pensionista recuperar(Object id) {
        Pensionista p = em.find(Pensionista.class, id);
        p.getItensValorPensionista().size();
        p.getListaInvalidez().size();
        return p;
    }

    public Pensionista recuperarParaArquivoAtuarial(Object id) {
        Pensionista p = em.find(Pensionista.class, id);
        p.getLotacaoFuncionals().size();
        p.getListaInvalidez().size();
        return p;
    }

    public List<Pensionista> recuperaPensionistasVigente(Date dataOperacao) {
        Query q = em.createQuery(" select distinct p from Pensionista p "
            + " where :data between p.inicioVigencia and coalesce(p.finalVigencia, :data) ");
        q.setParameter("data", DataUtil.dataSemHorario(dataOperacao));
        return q.getResultList();
    }

    public Pensao recuperarPensaoDoPensionista(Pensionista pensionista) {
        Query q = em.createQuery(" select distinct p.pensao from Pensionista p "
            + " where p = :pensionista");
        q.setParameter("pensionista", pensionista);
        return (Pensao) q.getResultList().get(0);
    }

    public List<Long> buscarPensionistasArquivoAtuarial(Date dataReferencia) {
        String hql = "  select p.id from  Pensionista p"
            + " inner join p.fichasFinanceiraFP ffp "
            + " inner join ffp.folhaDePagamento fp  "
            + "      where fp.mes = :mes            "
            + "        and fp.ano = :ano            "
            + "        and p.id = p.id              ";

        Query q = em.createQuery(hql);
        q.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(dataReferencia)));
        q.setParameter("ano", DataUtil.getAno(dataReferencia));
        return q.getResultList();
    }

    public List<Long> buscarPensionistaComRegistroObito(Date dataReferencia) {
        String sql = "  select distinct vinculo.id from vinculofp vinculo " +
            " inner join PENSIONISTA pensionista on vinculo.id = pensionista.id " +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.id " +
            " inner join pessoafisica pf on mat.PESSOA_ID =pf.id " +
            " inner join REGISTRODEOBITO obito on obito.PESSOAFISICA_ID = pf.id " +
            " where extract(year from obito.DATAFALECIMENTO) = :ano " +
            " and extract (month from obito.DATAFALECIMENTO) = :mes";
        Query q = em.createNativeQuery(sql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("mes", date.getMonthOfYear());
        q.setParameter("ano", date.getYear());
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    public Pensionista recuperarPensionistaComItens(Object id) {
        Pensionista p = em.find(Pensionista.class, id);
        p.getItensValorPensionista().size();
        return p;
    }

    public List<VinculoFP> buscarPenionistasNomeadosNoPeriodoPorTipoReajuste(Date inicio, Date fim) {
        Query q = this.em.createQuery("select pen from Pensionista  pen join pen.pensao p " +
            "   where pen.inicioVigencia between :inicio and :fim " +
            "   and pen.tipoFundamentacao in (:tipoFundamentacao) ");
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("tipoFundamentacao", Lists.newArrayList(TipoFundamentacao.ART_40_CF_I, TipoFundamentacao.ART_40_CF_II));
        return q.getResultList();
    }

    public List<VinculoFP> buscarPenionistasNomeadosAte(Date inicio, Date dataOperacao) {
        Query q = this.em.createQuery("select pen from Pensionista  pen join pen.pensao p " +
            "   where to_date(to_char(pen.inicioVigencia,'MM/yyyy'),'MM/yyyy') <= to_date(to_char(:inicio,'MM/yyyy'),'MM/yyyy') and to_date(to_char(:dataOperacao,'MM/yyyy'),'MM/yyyy') between to_date(to_char(pen.inicioVigencia,'MM/yyyy'),'MM/yyyy') and to_date(to_char(coalesce(pen.finalVigencia, :dataOperacao),'MM/yyyy'),'MM/yyyy')" +
            "   and pen.tipoFundamentacao in (:tipoFundamentacao) " +
            "  ");
        q.setParameter("inicio", inicio);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("tipoFundamentacao", Lists.newArrayList(TipoFundamentacao.ART_40_CF_I, TipoFundamentacao.ART_40_CF_II));
        return q.getResultList();
    }

    public Collection<? extends ItemValorPrevidencia> buscarItemValorPensionistaPorReajuste(ReajusteMediaAposentadoria reajuste) {
        Query q = em.createQuery("select item from Pensionista pen join pen.itensValorPensionista item where item.reajusteRecebido = :reajuste");
        q.setParameter("reajuste", reajuste);
        return q.getResultList();
    }

    public List<Pensionista> buscarPensionistasPorReajuste(ReajusteMediaAposentadoria reajuste) {
        Query q = em.createQuery("select p from Pensionista p join p.itensValorPensionista item where item.reajusteRecebido = :reajuste");
        q.setParameter("reajuste", reajuste);
        return q.getResultList();
    }

    public List<ItemValorPensionista> buscarItensComReajusteDoExercicio(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        try {
            Query q = em.createQuery("select item from ItemValorPensionista item where item.reajusteRecebido in (select reajuste from ReajusteMediaAposentadoria reajuste where reajuste.exercicio = :ex and reajuste.exercicioReferencia = :exReferencia)");
            q.setParameter("ex", exercicioAplicacao);
            q.setParameter("exReferencia", exercicioReferencia);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhum Item Valor Pensionista encontrado com Reajuste em " + exercicioAplicacao.getAno());
        }
    }

    public List<ItemValorPrevidencia> buscarLancamentosVigentesAtivosENaoAutomaticos(VinculoFP vinculo, int mes, int ano) {
        Query q = em.createQuery("from ItemValorPensionista item "
            + " where item.pensionista = :vinculo and item.eventoFP.ativo = true and coalesce(item.eventoFP.automatico,false) = false "
            + " and (to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(coalesce(item.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy')) ");
        q.setParameter("vinculo", vinculo);
        Date date = Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano);
        q.setParameter("dataParam", date);
        List<ItemValorPrevidencia> itensPensionista = q.getResultList();
        return itensPensionista;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal buscarValorPensionistaPerPensionista(Pensionista pensionista, Date data) {
        Query q = this.em.createQuery("select item.valor from ItemValorPensionista  item inner join item.pensionista  pensi " +
            "   where pensi.id = :id and to_date(:data,'dd/mm/yyyy') between item.inicioVigencia and coalesce(item.finalVigencia,to_date(:data,'dd/mm/yyyy'))");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("id", pensionista.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0);
    }

    public Boolean pensionistaPertencePencao(Long pensaoId, Long matriculaId){
        String sql = "select p.ID from pensao p " +
            " inner join pensionista pen on p.ID = pen.PENSAO_ID " +
            " inner join vinculofp v on pen.ID = v.ID " +
            " where p.ID = :pensaoId and v.MATRICULAFP_ID = :matriculaId";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pensaoId", pensaoId);
        q.setParameter("matriculaId", matriculaId);
        List resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }
}




