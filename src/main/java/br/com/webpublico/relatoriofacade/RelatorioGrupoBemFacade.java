package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.GrupoBemItensRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 09/04/2015.
 */
@Stateless
public class RelatorioGrupoBemFacade implements Serializable {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    public RelatorioGrupoBemFacade() {
    }

    public List<GrupoBemItensRelatorio> montarConsultaRelatorioBensMoveis(String apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select bens.numero as NUMERO, ")
            .append("   bens.dataBensMoveis as dataBem, ")
            .append("   bens.tipolancamento as LANCAMENTO, ")
            .append("   bens.historico as HISTORICO, ")
            .append("   bens.valor as valor, ")
            .append("   bens.tipoGrupo as tipoGrupo, ")
            .append("   bens.tipooperacaobemestoque as operacao, ")
            .append("   ingresso.descricao as INGRESSO, ")
            .append("   baixa.descricao as baixa, ")
            .append("   grupo.codigo || ' - ' || grupo.descricao as GRUPO, ")
            .append("   ev.codigo || ' - ' ||ev.descricao as evento, ")
            .append("   vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("   vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ")
            .append(" from bensmoveis bens ")
            .append(" inner JOIN vwhierarquiaorcamentaria vw on bens.UNIDADEORGANIZACIONAL_ID = vw.subordinada_id ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  LEFT JOIN TIPOINGRESSO ingresso on ingresso.id = bens.tipoIngresso_id ")
            .append("  left join tipobaixabens baixa on baixa.id = bens.tipobaixabens_id ")
            .append("  left join grupobem grupo on grupo.id = bens.grupobem_id ")
            .append("  left join eventocontabil ev on bens.eventocontabil_id = ev.id ")
            .append(" where trunc(bens.dataBensMoveis) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(bens.dataBensMoveis)) ")
            .append("   and trunc(bens.databensmoveis) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(bens.databensmoveis)) ")
            .append(concatenaParametros(parametros, 1, " and "));
        if (apresentacao.equals("UNIDADE_GESTORA")) {
            sql.append("order by ug.codigo, bens.dataBensMoveis, bens.numero ");
        } else if (apresentacao.equals("ORGAO") || apresentacao.equals("UNIDADE")) {
            sql.append(" order by vw.codigo, bens.dataBensMoveis, bens.numero ");
        } else {
            sql.append(" order by bens.dataBensMoveis, bens.numero ");
        }

        Query q = em.createNativeQuery(sql.toString());
        q = adicionaParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<GrupoBemItensRelatorio> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                GrupoBemItensRelatorio item = new GrupoBemItensRelatorio();
                item.setNumero((String) obj[0]);
                item.setDataBem((Date) obj[1]);
                item.setTipoLancamento((String) obj[2]);
                item.setHistorico((String) obj[3]);
                item.setValor((BigDecimal) obj[4]);
                item.setTipoGrupo((String) obj[5]);
                item.setOperacao((String) obj[6]);
                item.setIngresso((String) obj[7]);
                item.setTipoBaixa((String) obj[8]);
                item.setGrupo((String) obj[9]);
                item.setEvento((String) obj[10]);
                item.setUnidade((String) obj[11]);
                item.setOrgao((String) obj[12]);
                item.setUnidadeGestora((String) obj[13]);
                retorno.add(item);
            }
            return retorno;
        }
    }


    public List<GrupoBemItensRelatorio> recuperaRelatorioBensImoveis(String apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ")
            .append("  bens.numero as numero, ")
            .append("  bens.databem as databem, ")
            .append("  bens.tipolancamento as lancamento, ")
            .append("  bens.historico as historico, ")
            .append("  bens.valor as valor, ")
            .append("  bens.tipogrupo as tipoGrupo, ")
            .append("  bens.tipooperacaobemestoque as operacao, ")
            .append("  ingresso.descricao as ingresso, ")
            .append("  baixa.descricao as baixa, ")
            .append("  grupo.codigo || ' - ' || grupo.descricao as grupo, ")
            .append("  ev.codigo || ' - ' || ev.descricao as evento, ")
            .append("  vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("  vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ")
            .append("   from bensimoveis bens ")
            .append("   inner join vwhierarquiaorcamentaria vw on bens.unidadeorganizacional_id = vw.subordinada_id ")
            .append("   inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("   left join tipoingresso ingresso on ingresso.id = bens.tipoingresso_id ")
            .append("   left join tipobaixabens baixa on baixa.id = bens.tipobaixabens_id ")
            .append("   inner join grupobem grupo on grupo.id = bens.grupobem_id ")
            .append("   inner join eventocontabil ev on bens.eventocontabil_id = ev.id ")
            .append("   where trunc(bens.databem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(bens.databem)) ")
            .append("     and trunc(bens.databem) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(bens.databem)) ")
            .append("     and bens.tipooperacaobemestoque not in (:operacaoRetirada)")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(concatenaParametros(parametros, 2, " and "));
        if (apresentacao.equals("UNIDADE_GESTORA")) {
            sql.append("order by ug.codigo, bens.databem, bens.numero ");
        } else if (apresentacao.equals("ORGAO") || apresentacao.equals("UNIDADE")) {
            sql.append(" order by vw.codigo, bens.databem, bens.numero ");
        } else {
            sql.append(" order by bens.databem, bens.numero ");
        }
        Query q = em.createNativeQuery(sql.toString());
        q = adicionaParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<GrupoBemItensRelatorio> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                GrupoBemItensRelatorio item = new GrupoBemItensRelatorio();
                item.setNumero((String) obj[0]);
                item.setDataBem((Date) obj[1]);
                item.setTipoLancamento((String) obj[2]);
                item.setHistorico((String) obj[3]);
                item.setValor((BigDecimal) obj[4]);
                item.setTipoGrupo((String) obj[5]);
                item.setOperacao((String) obj[6]);
                item.setIngresso((String) obj[7]);
                item.setTipoBaixa((String) obj[8]);
                item.setGrupo((String) obj[9]);
                item.setEvento((String) obj[10]);
                item.setUnidade((String) obj[11]);
                item.setOrgao((String) obj[12]);
                item.setUnidadeGestora((String) obj[13]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    private Query adicionaParametros(List<ParametrosRelatorios> parametros, Query q) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        return q;
    }

    private String concatenaParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }
}
