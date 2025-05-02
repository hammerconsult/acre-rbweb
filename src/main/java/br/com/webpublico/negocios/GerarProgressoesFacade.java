package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ProgressaoAutomatica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Peixe
 * Date: 16/09/13
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class GerarProgressoesFacade extends AbstractFacade<ProgressaoAuto> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;


    public GerarProgressoesFacade() {
        super(ProgressaoAuto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BigDecimal> buscarIdsContratosProgressao(VinculoFP vinculoFP, PlanoCargosSalarios planoCargosSalarios,CategoriaPCS categoriaPCS,
                                                         ProgressaoPCS progressaoPCS, List<HierarquiaOrganizacional> hierarquias) {
        List<BigDecimal> listaContratos = Lists.newArrayList();
        if (vinculoFP == null) {
            Query q = getQueryPorPrazo(hierarquias);
            Query queryFiltro = null;
            if (planoCargosSalarios != null) {
                queryFiltro = getQueryPorFiltro(hierarquias, planoCargosSalarios, categoriaPCS, progressaoPCS);
            }
            listaContratos = planoCargosSalarios != null ? (List<BigDecimal>) queryFiltro.getResultList() : (List<BigDecimal>) q.getResultList();
        } else {
            listaContratos.add(BigDecimal.valueOf(vinculoFP.getId()));
        }
        return listaContratos;
    }

    public Query getQueryPorPrazo(List<HierarquiaOrganizacional> hierarquias) {
        String hql = "select distinct v.id " +
            "from vinculofp v " +
            "         inner join contratofp contrato on v.id = contrato.id " +
            "         inner join EnquadramentoFuncional enquadramento on contrato.id = enquadramento.contratoServidor_id " +
            "         inner join SITUACAOCONTRATOFP stc on stc.contratofp_id = v.id " +
            "         inner join TIPOREGIME regime on contrato.TIPOREGIME_ID = regime.ID " +
            "         inner join situacaofuncional st on stc.SITUACAOFUNCIONAL_ID = st.ID " +
            "         inner join LOTACAOFUNCIONAL lotacao on v.ID = lotacao.VINCULOFP_ID " +
            " " +
            "         inner join CATEGORIAPCS cat on enquadramento.CATEGORIAPCS_ID = cat.ID " +
            "         inner join PROGRESSAOPCS prog on enquadramento.PROGRESSAOPCS_ID = prog.ID " +
            "         inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID " +
            "         inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID " +
            "         left join MESESPROGRESSAO mesesProgPlano " +
            "                   on mesesProgPlano.PLANOCARGOSSALARIOS_ID = progPai.PLANOCARGOSSALARIOS_ID " +
            "                       and " +
            "                      current_date between mesesProgPlano.INICIOVIGENCIA and coalesce(mesesProgPlano.FINALVIGENCIA, current_date) " +
            " " +
            "         left join MesesProgressaoProgressaoPCS mesesProgProg on mesesProgProg.progressaopcs_id = progPai.id " +
            "    and current_date between mesesProgProg.INICIOVIGENCIA and coalesce(mesesProgProg.FINALVIGENCIA, current_date) " +
            " " +
            "         left join MesesProgressaoCategoria mesesPromoCat on mesesPromoCat.categoriapcs_id = catPai.id " +
            "    and current_date between mesesPromoCat.INICIOVIGENCIA and coalesce(mesesPromoCat.FINALVIGENCIA, current_date) " +
            " " +
            "         join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = lotacao.UNIDADEORGANIZACIONAL_ID " +
            " where current_date between stc.inicioVigencia and coalesce(stc.finalVigencia, current_date) " +
            "  and current_date between v.inicioVigencia and coalesce(v.finalVigencia, current_date) " +
            "  and current_date between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia, current_date) " +
            "  and current_date between enquadramento.inicioVigencia and coalesce(enquadramento.finalVigencia, current_date) " +
            montarCondicaoHierarquias(hierarquias) +
            "  and ( " +
            "            months_between(coalesce(enquadramento.finalVigencia, current_date), enquadramento.inicioVigencia) >= " +
            "            mesesProgPlano.MESES or " +
            "            months_between(coalesce(enquadramento.finalVigencia, current_date), enquadramento.inicioVigencia) >= " +
            "            mesesPromoCat.MESES or " +
            "            months_between(coalesce(enquadramento.finalVigencia, current_date), enquadramento.inicioVigencia) >= " +
            "            mesesProgProg.MESES) " +
            "  and st.codigo in (1, 2, 3, 4) " +
            "  and regime.codigo = 2 ";

        Query q = em.createNativeQuery(hql);
        return q;
    }

    private Query getQueryPorFiltro(List<HierarquiaOrganizacional> hierarquias, PlanoCargosSalarios planoCargosSalarios, CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS) {
        String hql = " " +
            "select distinct v.id " +
            "from vinculofp v " +
            "         inner join contratofp contrato on v.id = contrato.id " +
            "         inner join EnquadramentoFuncional enquadramento on contrato.id = enquadramento.contratoServidor_id " +
            "         inner join LOTACAOFUNCIONAL lotacao on v.ID = lotacao.VINCULOFP_ID " +
            " " +
            "         inner join CATEGORIAPCS cat on enquadramento.CATEGORIAPCS_ID = cat.ID " +
            "         inner join PROGRESSAOPCS prog on enquadramento.PROGRESSAOPCS_ID = prog.ID " +
            "         inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID " +
            "         inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID " +
            "         join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = lotacao.UNIDADEORGANIZACIONAL_ID " +
            "where current_date between v.inicioVigencia and coalesce(v.finalVigencia, current_date) " +
            "  and current_date between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia, current_date) " +
            "  and current_date between enquadramento.inicioVigencia and coalesce(enquadramento.finalVigencia, current_date) " +
            "  and catPai.PLANOCARGOSSALARIOS_ID = :plano ";
        if (categoriaPCS != null) {
            hql += "  and catPai.id = :cat ";
        }
        if (progressaoPCS != null) {
            hql += "  and progPai.id = :prog ";
        }
        hql +=  montarCondicaoHierarquias(hierarquias);

        Query q = em.createNativeQuery(hql);

        q.setParameter("plano", planoCargosSalarios.getId());
        if (categoriaPCS != null) {
            q.setParameter("cat", categoriaPCS.getId());
        }
        if (progressaoPCS != null) {
            q.setParameter("prog", progressaoPCS.getId());
        }
        return q;
    }

    private String montarCondicaoHierarquias(List<HierarquiaOrganizacional> hierarquias){
        String condicao = " and (";
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            condicao += " vw.codigo like '" + hierarquia.getCodigoSemZerosFinais() + "%'";
            condicao += " or";
        }
        condicao = condicao.substring(0, condicao.length() - 2);
        condicao += ") ";
        return condicao;
    }


    public void salvarProgressoesAutomaticas(ProgressaoAuto selecionado, AtoLegal ato, List<ProgressaoAutomatica> list) {
        em.persist(selecionado);

        for (ProgressaoAutomatica progressaoAutomatica : list) {
            if (progressaoAutomatica.getSeraSalvo()) {
                progressaoAutomatica.getAntigoEnquadramentoFuncional().setFinalVigencia(DataUtil.removerDias(progressaoAutomatica.getNovoEnquadramentoFuncional().getInicioVigencia(), 1));
                em.merge(progressaoAutomatica.getAntigoEnquadramentoFuncional());
                try {
                    ProvimentoFP provimento = new ProvimentoFP();
                    provimento.setAtoLegal(ato);
                    ContratoFP contratoFP = progressaoAutomatica.getNovoEnquadramentoFuncional().getContratoServidor();
                    provimento.setContratoFP(contratoFP);
                    provimento.setDataProvimento(progressaoAutomatica.getNovoEnquadramentoFuncional().getInicioVigencia());
                    provimento.setObservacao("Provimento gerado através da rotina de progressão automática em " + DataUtil.getDataFormatadaDiaHora(UtilRH.getDataOperacao()));
                    provimento.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PROVIMENTO_PROGRESSAO));
                    provimento.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
                    provimento.setVinculoFP(contratoFP);

                    em.persist(provimento);
                    progressaoAutomatica.getNovoEnquadramentoFuncional().setProvimentoFP(provimento);
                    em.persist(progressaoAutomatica.getNovoEnquadramentoFuncional());
                    ItemProgressaoAuto item = new ItemProgressaoAuto();
                    item.setEnquadramentoAntigo(progressaoAutomatica.getAntigoEnquadramentoFuncional());
                    item.setEnquadramentoNovo(progressaoAutomatica.getNovoEnquadramentoFuncional());
                    item.setInconsistente(progressaoAutomatica.getTemInconsistencia());
                    item.setProvimentoProgressao(provimento);
                    item.setRegularizado(true);
                    item.setProgressaoAuto(selecionado);
                    item.setUltimaProgressao(progressaoAutomatica.getChegouUltimaLetra());
                    item.setEnquadramentoConsidProgAut(progressaoAutomatica.getEnquadramentoConsidProgAut());
                    item.setQuantidadeMesesProgressao(progressaoAutomatica.getQuantidadeMesesProgressao());
                    em.persist(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            em.merge(selecionado);

        }
    }

    public Date getDataRegularizado(EnquadramentoFuncional enquadramentoFuncional) {
        if (enquadramentoFuncional != null && enquadramentoFuncional.getId() != null) {
            String hql = "select max(i.progressaoAuto.dataCadastro) from ItemProgressaoAuto i where i.enquadramentoNovo = :e and i.regularizado = true";
            Query q = em.createQuery(hql);
            q.setParameter("e", enquadramentoFuncional);
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        }
        return null;
    }

    @Override
    public ProgressaoAuto recuperar(Object id) {
        ProgressaoAuto progressaoAuto = em.find(ProgressaoAuto.class, id);
        progressaoAuto.getItemProgressaoAutos().size();
        progressaoAuto.getUnidades().size();
        return progressaoAuto;
    }

    @Override
    public void remover(ProgressaoAuto entity) {

        for (ItemProgressaoAuto item : entity.getItemProgressaoAutos()) {
            ProvimentoFP prov = item.getProvimentoProgressao();
            EnquadramentoFuncional antigo = item.getEnquadramentoAntigo();
            EnquadramentoFuncional novo = item.getEnquadramentoNovo();

            item.setProvimentoProgressao(null);

            ItemProgressaoAuto i = em.find(ItemProgressaoAuto.class, item.getId());
            em.remove(i);

            novo = em.find(EnquadramentoFuncional.class, novo.getId());
            em.remove(novo);

            prov = em.find(ProvimentoFP.class, prov.getId());
            em.remove(prov);
            EnquadramentoFuncionalHist efHist = antigo.getEnquadramentoFuncionalHist();
            if (efHist != null) {
                EnquadramentoFuncional.copiaPropriedadesDoHistoricoParaOrginial(antigo, efHist);
            } else {
                antigo.setFinalVigencia(null);
            }
            em.merge(antigo);
        }
        ProgressaoAuto progressaoAuto = em.find(ProgressaoAuto.class, entity.getId());
        em.remove(progressaoAuto);

    }
}
