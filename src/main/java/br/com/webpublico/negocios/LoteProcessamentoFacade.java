/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CamposPesquisa;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Peixe
 */
@Stateless
public class LoteProcessamentoFacade extends AbstractFacade<LoteProcessamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;

    public LoteProcessamentoFacade() {
        super(LoteProcessamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoPrevidenciaFPFacade getTipoPrevidenciaFPFacade() {
        return tipoPrevidenciaFPFacade;
    }

    @Override
    public LoteProcessamento recuperar(Object id) {
        LoteProcessamento lote = em.find(LoteProcessamento.class, id);
        if (lote != null) {
            lote.getItensLoteProcessamentos().size();
        }
        return lote;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<VinculoFP> findVinculosByLote(LoteProcessamento lote) {
        lote = recuperar(lote.getId());
        String query = gerarQueryConsulta(lote);

        List<Long> idsVinculo = vinculoFPFacade.findVinculosByConsultDinamicaSql(query);
        List<VinculoFP> vinculos = Lists.newArrayList();
        for (Long id : idsVinculo) {
            vinculos.add(vinculoFPFacade.recuperar(id));
        }
        return vinculos;
    }

    public boolean existeNumeroLoteNoAnoMes(LoteProcessamento loteProcessamento) {
        String sql = " select lp.numerolote from loteprocessamento lp "
            + " where mes = :mes "
            + " and ano = :ano "
            + " and numerolote = :numerolote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", loteProcessamento.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("ano", loteProcessamento.getAno());
        q.setParameter("numerolote", loteProcessamento.getNumeroLote());
        return !q.getResultList().isEmpty();
    }


    public BigDecimal buscarProximoNumeroLote(Integer mes, Integer ano) {
        String sql = " "
            + "  select max(lp.numerolote) +1 as numero "
            + "   from loteprocessamento lp "
            + "  where mes = :mes "
            + "  and ano = :ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setMaxResults(1);
        if (q.getResultList() != null) {
            return (BigDecimal) q.getSingleResult();
        }
        return null;
    }

    public List<VinculoFP> findVinculosByConsulta(String lote) {
        Query q = em.createQuery(lote);
        if (lote.contains("data")) {
            q.setParameter("data", UtilRH.getDataOperacao());
        }
        return q.getResultList();
    }

    public String gerarQueryConsulta(LoteProcessamento lote) {
        Boolean aposentado = Boolean.FALSE;
        Boolean pensionista = Boolean.FALSE;
        String texto = "select distinct vinculo.id from VinculoFP vinculo ";
        texto += " left join contratofp contrato on contrato.id = vinculo.id ";
        String where = "";
        String juncao = " where ";

        List<ItensLoteProcessamento> vinculosMat = findObjetosByTipo(lote, CamposPesquisa.MATRICULA);
        if (!vinculosMat.isEmpty()) {
            where += juncao + " ( ";
            for (ItensLoteProcessamento v : vinculosMat) {
                where += " vinculo.id = " + v.getValor();
                where += " or ";
            }
            where = cortaStringFim(where, 3);
            where += " ) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosCarg = findObjetosByTipo(lote, CamposPesquisa.CARGO);
        if (!vinculosCarg.isEmpty()) {
            texto += "inner join cargo on cargo.id = contrato.cargo_id";
            where += juncao + " ( ";
            for (ItensLoteProcessamento c : vinculosCarg) {
                where += " cargo.id = " + c.getValor();
                where += " or ";
            }
            where = cortaStringFim(where, 3);
            where += " ) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosSitu = findObjetosByTipo(lote, CamposPesquisa.SITUACAO_FUNCIONAL);
        for (ItensLoteProcessamento processamento : vinculosSitu) {

            SituacaoFuncional situacao = situacaoFuncionalFacade.recuperar(Long.valueOf(processamento.getValor()));
            if (situacao != null) {
                if (SituacaoFuncional.APOSENTADO.equals(situacao.getCodigo())) {
                    aposentado = true;
                }
                if (SituacaoFuncional.PENSIONISTA.equals(situacao.getCodigo())) {
                    pensionista = true;
                }
            }

        }

        if (!vinculosSitu.isEmpty()) {
            texto += ((pensionista || aposentado) ? "left " : " ") + " join SituacaoContratoFP situacao on situacao.CONTRATOFP_ID = contrato.id ";
            texto += ((pensionista || aposentado) ? "left " : " ") + " join situacaofuncional on situacao.SITUACAOFUNCIONAL_ID = situacaofuncional.id ";
//            texto += ((pensionista || aposentado) ? "left " : " ") + " join vinculo.situacaoFuncionals situacao ";

            where += juncao + " ( ";
            for (ItensLoteProcessamento situ : vinculosSitu) {
                where += " situacaofuncional.id = " + situ.getValor();
                where += " or ";
            }
            where = cortaStringFim(where, 3);
            where += " ) ";
            where += " and :data between trunc(situacao.inicioVigencia) and coalesce(trunc(situacao.finalVigencia), :data) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosOrgao = findObjetosByTipo(lote, CamposPesquisa.ORGAO);
        if (!vinculosOrgao.isEmpty()) {
            texto += " inner join LotacaoFuncional lotacao on lotacao.VINCULOFP_ID = vinculo.id ";
            texto += " inner join VwHierarquiaAdministrativa vw on vw.SUBORDINADA_ID = lotacao.UNIDADEORGANIZACIONAL_ID ";
//            texto += "join vinculo.lotacaoFuncionals lotacao, VwHierarquiaAdministrativa vw ";

            where += juncao + " ( ";
            HierarquiaOrganizacional ho = null;
            for (ItensLoteProcessamento item : vinculosOrgao) {
                ho = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(Long.valueOf(item.getValor()));
                where += " vw.codigo like '" + ho.getCodigoSemZerosFinais() + "%'";
                //where += " vw.subordinadaId = " + orgao.getValor();
                where += " or ";
            }

            where = cortaStringFim(where, 3);

            where += " ) and ( ";
            where += " vw.SUBORDINADA_ID = lotacao.UNIDADEORGANIZACIONAL_ID ";
            where += " and :data between trunc(lotacao.inicioVigencia) and coalesce(trunc(lotacao.finalVigencia), :data) ";
            where += " and :data between vw.inicioVigencia and coalesce(vw.fimVigencia, :data) ";

            where += " ) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosCat = findObjetosByTipo(lote, CamposPesquisa.CATEGORIA);
        if (!vinculosCat.isEmpty()) {
            texto += " inner join EnquadramentoFuncional enquadramento on enquadramento.CONTRATOSERVIDOR_ID = vinculo.id ";
            texto += " inner join CATEGORIAPCS categoria on enquadramento.CATEGORIAPCS_ID = categoria.id ";
            texto += " inner join CATEGORIAPCS superior on categoria.SUPERIOR_ID = superior.id ";

            where += juncao + " ( ";
            for (ItensLoteProcessamento cat : vinculosCat) {
                where += " superior.id  = " + cat.getValor();
                where += " or ";
            }

            where = cortaStringFim(where, 3);
            where += " ) and ( ";
            where += " :data between trunc(enquadramento.inicioVigencia) and coalesce(trunc(enquadramento.finalVigencia), :data) ";

            where += " ) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosPeriod = findObjetosByTipo(lote, CamposPesquisa.PERIODO);
        if (!vinculosPeriod.isEmpty()) {
            where += juncao + " ( ";
            for (ItensLoteProcessamento period : vinculosPeriod) {
                where += " contrato." + period.getTipo() + " between to_date('" + Util.dateToString(period.getDataInicio()) + "','DD/MM/YYYY') and to_date('" + Util.dateToString(period.getDataFim()) + "','DD/MM/YYYY') ";
                where += " and";
            }
            where = cortaStringFim(where, 3);
            where += " ) ";
            juncao = " and ";
        }
        List<ItensLoteProcessamento> vinculosRecursoFP = findObjetosByTipo(lote, CamposPesquisa.ORGAO_RECURSOFP);
        boolean isVerba = false;
        if (!vinculosRecursoFP.isEmpty()) {
            for(ItensLoteProcessamento item : lote.getItensLoteProcessamentos()){
                if(CamposPesquisa.VERBA.equals(item.getCampo())){
                    isVerba = true;
                    break;
                }
            }
            if (!isVerba) {
                texto += " , RecursoDoVinculoFP recvinc ";
            }

            where += juncao + " ( ";
            for (ItensLoteProcessamento rec : vinculosRecursoFP) {
                where += " recvinc.recursoFP_id = " + rec.getValor();
                where += " or ";
            }

            where = cortaStringFim(where, 3);
            where += " ) and ( ";
            where += " recvinc.vinculoFP_id = vinculo.id ";
            where += " and :data between trunc(recvinc.inicioVigencia) and coalesce(trunc(recvinc.finalVigencia), :data) ";
            where += " ) ";
            juncao = " and ";
        }

        List<ItensLoteProcessamento> vinculosEvento = findObjetosByTipo(lote, CamposPesquisa.VERBA);
        if (!vinculosEvento.isEmpty()) {
            texto += " inner join lancamentofp lancamento on lancamento.VINCULOFP_ID = vinculo.id ";
            if(isVerba) {
                texto += " , RecursoDoVinculoFP recvinc";
            }

            where += juncao + " ( ";
            for (ItensLoteProcessamento evento : vinculosEvento) {
                where += " lancamento.EVENTOFP_ID = " + Long.parseLong(evento.getValor());
                where += " and to_date('" + Util.dateToString(evento.getDataInicio()) + "','dd/MM/yyyy') between trunc(lancamento.MESANOINICIAL) and coalesce (trunc(lancamento.MESANOFINAL), sysdate)";
                where += " or ";
            }
            where = cortaStringFim(where, 3);
            where += " ) ";
        }


        if (aposentado || pensionista) {
            if (aposentado) {
                where += " or vinculo in (select v from VinculoFP v, Aposentadoria apo " +
                    " where apo.id = v.id " +
                    " and to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY') between trunc(v.inicioVigencia) and coalesce(trunc(v.finalVigencia), to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY'))) ";
            }
            if (pensionista) {
                where += " or vinculo in (select v from VinculoFP v, Pensionista pen " +
                    " where pen.id = v.id " +
                    " and to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY') between trunc(v.inicioVigencia) and coalesce(trunc(v.finalVigencia), to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY'))) ";
            }
            where += " ) ";
        } else {
            where = where.replaceAll("where \\(\\( ", "where (");
        }
        String vigencia = " and to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY') between trunc(vinculo.inicioVigencia) and coalesce(trunc(vinculo.finalVigencia), to_date('" + Util.dateToString(UtilRH.getDataOperacao()) + "','DD/MM/YYYY')) ";

        String retorno = texto + where + vigencia;
        retorno = retorno.replaceAll("where \\(  or", "where ");
        return retorno;
    }

    private List<ItensLoteProcessamento> findObjetosByTipo(LoteProcessamento lote, CamposPesquisa campo) {
        List<ItensLoteProcessamento> list = new ArrayList<>();
        for (ItensLoteProcessamento item : lote.getItensLoteProcessamentos()) {
            if (item.getCampo().equals(campo)) {
                list.add(item);
            }
        }
        return list;
    }

    private String cortaStringFim(String s, int quantidade) {
        return s.substring(0, s.length() - quantidade);
    }

    public List<LoteProcessamento> getLotesPorMesAnoAndTipoFolhaDePagamento(Mes mes, Integer ano, TipoFolhaDePagamento tipo) {
        Query q = em.createQuery("select lote from LoteProcessamento  lote where lote.mes = :mes and lote.ano = :ano and lote.tipoFolhaDePagamento = :tipo order by lote.numeroLote desc ");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<LoteProcessamento> listaFiltrandoMesAno(Mes mes, Integer ano, String parte) {
        Query q = em.createQuery("select lote from LoteProcessamento  lote where lote.mes = :mes and lote.ano = :ano and lower(lote.descricao) like :parte");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<LoteProcessamento> filtrandoByMesAno(Integer mes, Integer ano) {
        Query q = em.createQuery("select lote from LoteProcessamento  lote where lote.mes = :mes and lote.ano = :ano");
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public CategoriaPCSFacade getCategoriaPCSFacade() {
        return categoriaPCSFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    public SituacaoFuncionalFacade getSituacaoFuncionalFacade() {
        return situacaoFuncionalFacade;
    }

    public RecursoFPFacade getRecursoFPFacade() {
        return recursoFPFacade;
    }

    public ComparadorDeFollhasFacade getComparadorDeFollhasFacade() {
        return comparadorDeFollhasFacade;
    }
}
