/*
 * Codigo gerado automaticamente em Mon Oct 31 10:00:39 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class FonteDespesaORCFacade extends AbstractFacade<FonteDespesaORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    public FonteDespesaORCFacade() {
        super(FonteDespesaORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<FonteDespesaORC> listaPorDespesaORC(DespesaORC desp) {

        String hql = "from FonteDespesaORC f where f.despesaORC = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", desp);
        return q.getResultList();
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte, DespesaORC desp) {
        if (desp == null) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" select  fdo.*  ");
        sql.append(" from  FONTEDESPESAORC fdo");
        sql.append(" INNER JOIN DESPESAORC do on do.ID = fdo.DESPESAORC_ID and do.ID=:desp  ");
        sql.append(" INNER JOIN PROVISAOPPAFONTE ppf on ppf.ID = fdo.PROVISAOPPAFONTE_ID ");
        sql.append(" INNER join CONTA co on co.ID = ppf.DESTINACAODERECURSOS_ID AND ((upper(co.CODIGO) LIKE :parte) or (upper(co.DESCRICAO) LIKE :parte))");
        sql.append(" order by co.codigo");
        Query q = getEntityManager().createNativeQuery(sql.toString(), FonteDespesaORC.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("desp", desp.getId());
        return q.getResultList();
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {

        StringBuilder sql = new StringBuilder();
        sql.append(" select  fdo.*  ");
        sql.append(" from  FONTEDESPESAORC fdo");
        sql.append(" INNER JOIN PROVISAOPPAFONTE ppf on ppf.ID = fdo.PROVISAOPPAFONTE_ID ");
        sql.append(" INNER join CONTA co on co.ID = ppf.DESTINACAODERECURSOS_ID AND ((upper(co.CODIGO) LIKE :parte) or (upper(co.DESCRICAO) LIKE :parte))");
        // logger.log(Level.INFO, sql.toString().replace(":parte", parte).replace(":desp", desp.getId()+""));
        Query q = getEntityManager().createNativeQuery(sql.toString(), FonteDespesaORC.class);
        q.setParameter("parte", "%" + parte.toUpperCase() + "%");

        return q.getResultList();
    }

    public SaldoFonteDespesaORC recuperaUltimoSaldo(FonteDespesaORC fonte) {
        String sql = "SELECT sa.* FROM saldofontedespesaorc sa WHERE sa.datasaldo = "
            + "(SELECT coalesce(max(sfd.datasaldo), sysdate) FROM saldofontedespesaorc sfd "
            + "WHERE sfd.datasaldo <= sysdate "
            + "AND sfd.fontedespesaorc_id = :param) "
            + "AND sa.fontedespesaorc_id = :param";
        Query q = em.createNativeQuery(sql, SaldoFonteDespesaORC.class);
        q.setParameter("param", fonte.getId());
        if (q.getResultList().isEmpty()) {
            return new SaldoFonteDespesaORC();
        }
        return (SaldoFonteDespesaORC) q.getSingleResult();
    }

    public BigDecimal saldoRealPorFonte(FonteDespesaORC fonte, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        return saldoFonteDespesaORCFacade.saldoRealPorFonte(fonte, data, unidadeOrganizacional);
    }

    public List<FonteDespesaORC> listaPorProvisaoPPAFonte(ProvisaoPPAFonte ppf) {
        String sql = "SELECT * FROM FONTEDESPESAORC WHERE PROVISAOPPAFONTE_ID = :param";
        Query q = getEntityManager().createNativeQuery(sql, FonteDespesaORC.class);
        q.setParameter("param", ppf);
        return q.getResultList();
    }

    //Recuperar as mascaras!!!!!
    public List<FonteDespesaORC> buscarFontesDespesaOrcPorGrupoOrcamentario(GrupoOrcamentario go, Exercicio e, Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select fonte.* from fonteDespesaORC fonte ")
            .append(" inner join despesaORC despesa on despesa.id = fonte.despesaorc_id and despesa.exercicio_id = ").append(e.getId())
            .append(" inner join provisaoppadespesa ppd on ppd.id = despesa.provisaoppadespesa_id ")
            .append(" inner join conta cdesp on cdesp.id = ppd.contadedespesa_id ")
            .append(" inner join PROVISAOPPAFONTE ppf on ppf.id = fonte.provisaoppafonte_id ")
            .append(" inner join conta co on co.id = ppf.destinacaoderecursos_id ")
            .append(" inner join contadedestinacao cd on cd.id = co.id ")
            .append(" inner join fontederecursos fr on fr.id = cd.fontederecursos_id ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppd.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN programappa PROG ON PROG.ID = A.programa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PPD.UNIDADEORGANIZACIONAL_ID ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VWsup ON VWsup.SUBORDINADA_ID = vw.superior_ID ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VWEnt ON VWEnt.SUBORDINADA_ID = vwSup.superior_ID ")
            .append("  left join entidade ent on VWEnt.entidade_id = ent.id ")
            .append(" where fonte.grupoOrcamentario_id is null ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') between VWsup.iniciovigencia and coalesce(VWsup.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') between VWEnt.iniciovigencia and coalesce(VWEnt.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ");
        if (go.getEntidade() != null) {
            sql.append(" and ent.id = :entidade ");
        }
        if (go.getOrgao() != null) {
            sql.append(" and vw.superior_id = :orgao ");
        }
        if (go.getUnidade() != null) {
            sql.append(" and vw.subordinada_id = :unidade ");
        }
        if (go.getFuncao() != null) {
            sql.append(" and f.id = :funcao ");
        }
        if (go.getSubFuncao() != null) {
            sql.append(" and SF.id = :subFuncao ");
        }
        if (go.getProgramaPPA() != null) {
            sql.append(" and prog.id = :programa ");
        }
        if (go.getAcaoPPA() != null) {
            sql.append(" and a.id = :acao ");
        }
        if (go.getSubAcaoPPA() != null) {
            sql.append(" and SUB.id = :subAcao ");
        }
        if (go.getFonteDeRecursos() != null) {
            sql.append(" and fr.id = :fonte");
        }
        if (go.getNaturezaDespesa() != null) {
            sql.append(" and cdesp.codigo like :natureza");
        }

        Query q = em.createNativeQuery(sql.toString(), FonteDespesaORC.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (go.getEntidade() != null) {
            q.setParameter("entidade", entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(go.getEntidade()).getId());
        }
        if (go.getOrgao() != null) {
            q.setParameter("orgao", go.getOrgao().getId());
        }
        if (go.getUnidade() != null) {
            q.setParameter("unidade", go.getUnidade().getId());
        }
        if (go.getFuncao() != null) {
            q.setParameter("funcao", go.getFuncao().getId());
        }
        if (go.getSubFuncao() != null) {
            q.setParameter("subFuncao", go.getSubFuncao().getId());
        }
        if (go.getProgramaPPA() != null) {
            q.setParameter("programa", go.getProgramaPPA().getId());
        }
        if (go.getAcaoPPA() != null) {
            q.setParameter("acao", go.getAcaoPPA().getId());
        }
        if (go.getSubAcaoPPA() != null) {
            q.setParameter("subAcao", go.getSubAcaoPPA().getId());
        }
        if (go.getFonteDeRecursos() != null) {
            q.setParameter("fonte", go.getFonteDeRecursos().getId());
        }
        if (go.getNaturezaDespesa() != null) {
            q.setParameter("natureza", "%" + go.getNaturezaDespesa().getCodigoSemZerosAoFinal() + "%");
        }
        if (q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        } else {
            return q.getResultList();
        }
    }

    public List<FonteDespesaORC> recuperaPorGrupoOrcamentario(GrupoOrcamentario go) {
        String sql = "SELECT * FROM FonteDespesaORC WHERE grupoOrcamentario_id = :param";
        Query q = em.createNativeQuery(sql, FonteDespesaORC.class);
        q.setParameter("param", go.getId());
        List<FonteDespesaORC> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<FonteDespesaORC>();
        }
        return lista;
    }

    public List<FonteDespesaORC> listaFontesPorLicitacao(Licitacao lic) {
        String sql = "SELECT DISTINCT fonte.* FROM licitacao lic "
            + "INNER JOIN processodecompra pdc ON pdc.id = lic.processodecompra_id "
            + "INNER JOIN loteprocessodecompra lpdc ON lpdc.processodecompra_id = pdc.id "
            + "INNER JOIN itemprocessodecompra ipdc ON ipdc.loteprocessodecompra_id = lpdc.id "
            + "INNER JOIN itemsolicitacao itemsol ON itemsol.id = ipdc.itemsolicitacaomaterial_id "
            + "INNER JOIN lotesolicitacaomaterial lsm ON lsm.id = itemsol.lotesolicitacaomaterial_id "
            + "INNER JOIN solicitacaomaterial solmat ON solmat.id = lsm.solicitacaomaterial_id "
            + "INNER JOIN dotsolmat dsm ON dsm.solicitacaomaterial_id = solmat.id "
            + "INNER JOIN fontedespesaorc fonte ON fonte.id = dsm.fontedespesaorc_id "
            + "WHERE lic.id = :lic";
        Query q = em.createNativeQuery(sql, FonteDespesaORC.class);
        q.setParameter("lic", lic.getId());
        List<FonteDespesaORC> fontes = q.getResultList();
        if (fontes != null) {
            return fontes;
        } else {
            return new ArrayList<FonteDespesaORC>();
        }
    }

    public List<SaldoFonteDespesaORC> recuperarSaldoGrupoOrc(GrupoOrcamentario grupoOrcamentario, Date data) {
        String sql = "select saldo.* from saldofontedespesaorc saldo " +
            "INNER JOIN FONTEDESPESAORC f oN f.id = saldo.FONTEDESPESAORC_ID AND f.GRUPOORCAMENTARIO_ID = :param " +
            "inner join (" +
            "SELECT MAX(sfd.datasaldo) as data, f.id as id " +
            "  FROM saldofontedespesaorc sfd " +
            "  INNER JOIN FONTEDESPESAORC f ON f.id       = sfd.FONTEDESPESAORC_ID " +
            "  AND f.GRUPOORCAMENTARIO_ID                 = :param" +
            "  WHERE to_date(sfd.datasaldo,'dd/mm/yyyy') <= to_date(:data,'dd/mm/yyyy') " +
            "  AND sfd.fontedespesaorc_id                 = f.id " +
            "  group by f.id " +
            "  ) dados on dados.data = saldo.datasaldo and dados.id = f.id";
        Query q = getEntityManager().createNativeQuery(sql, SaldoFonteDespesaORC.class);
        q.setParameter("param", grupoOrcamentario.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return (ArrayList<SaldoFonteDespesaORC>) q.getResultList();
    }

    public BigDecimal recuperarSaldoCalculadoGrupoOrc(GrupoOrcamentario grupoOrcamentario, Date data) {
        List<SaldoFonteDespesaORC> saldoFonteDespesaORCs = recuperarSaldoGrupoOrc(grupoOrcamentario, data);
        BigDecimal saldoAtual = recuperarSaldoAtualPorGrupoOrc(grupoOrcamentario);
        if (saldoFonteDespesaORCs != null) {
            BigDecimal retorno = BigDecimal.ZERO;
            for (SaldoFonteDespesaORC saldo : saldoFonteDespesaORCs) {
                retorno = retorno.add(saldo.getSaldoAtual());
            }
            return retorno;
        }
        return BigDecimal.ZERO;
    }


    public BigDecimal recuperarSaldoAtualPorGrupoOrc(GrupoOrcamentario grupoOrcamentario) {

        BigDecimal valor = BigDecimal.ZERO;

        String sql = "select coalesce(sum(s.dotacao + s.alteracao),0) as valor from saldofontedespesaorc s " +
            " inner join fontedespesaorc fd on s.fontedespesaorc_id = fd.id " +
            " inner join grupoorcamentario g on fd.grupoorcamentario_id = g.id " +
            " inner join (select max(saldo.datasaldo) as data, saldo.fontedespesaorc_id from saldofontedespesaorc saldo " +
            " group by saldo.fontedespesaorc_id) maiorsaldo on s.datasaldo = maiorsaldo.data " +
            "      and s.fontedespesaorc_id = maiorsaldo.fontedespesaorc_id " +
            " where g.id = :idGrupo ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idGrupo", grupoOrcamentario.getId());

        if (!consulta.getResultList().isEmpty()
            || consulta.getResultList() != null) {
            valor = (BigDecimal) consulta.getSingleResult();
        }
        return valor;
    }

    public FonteDespesaORC recuperaPorContaFonteUnidade(Conta conta, UnidadeOrganizacional unidadeOrganizacional, FonteDeRecursos fonteDeRecursos, SubAcaoPPA subAcaoPPA, AcaoPPA acaoPPA) {
        String sql = " select b.* from FONTEDESPESAORC B " +
            " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id " +
            " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id " +
            " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID " +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID " +
            " inner join subacaoppa sub on d.subacaoppa_id = sub.id" +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id" +
            " where fr.id = :fonte_id " +
            " and e.id = :conta_id " +
            " and d.UNIDADEORGANIZACIONAL_ID = :unidade " +
            " and sub.id = :sub_id" +
            " and acao.id = :acao_id";
        Query q = getEntityManager().createNativeQuery(sql, FonteDespesaORC.class);
        q.setParameter("fonte_id", fonteDeRecursos.getId());
        q.setParameter("conta_id", conta.getId());
        q.setParameter("sub_id", subAcaoPPA.getId());
        q.setParameter("acao_id", acaoPPA.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        return (FonteDespesaORC) q.getSingleResult();
    }

    public List<FonteDespesaORC> buscarFontesDespORCPorContaDeDestinacaoEPermiteSuprimentoDeFundo(String parte, DespesaORC desp) {
        if (desp == null || desp.getId() == null) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" select  fdo.*  ");
        sql.append(" from  FONTEDESPESAORC fdo");
        sql.append(" INNER JOIN DESPESAORC do on do.ID = fdo.DESPESAORC_ID and do.ID=:desp  ");
        sql.append(" INNER JOIN PROVISAOPPAFONTE ppf on ppf.ID = fdo.PROVISAOPPAFONTE_ID ");
        sql.append(" INNER join CONTA co on co.ID = ppf.DESTINACAODERECURSOS_ID ");
        sql.append(" INNER join ContaDeDestinacao cdd on co.ID = cdd.ID ");
        sql.append(" INNER JOIN FONTEDERECURSOS fdr on fdr.ID = cdd.FONTEDERECURSOS_ID ");
        sql.append(" where (upper(co.CODIGO) LIKE :parte or upper(co.DESCRICAO) LIKE :parte) ");
        sql.append("   and fdr.permitirSuprimentoDeFundo = 1 ");
        sql.append(" order by co.codigo");
        Query q = getEntityManager().createNativeQuery(sql.toString(), FonteDespesaORC.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("desp", desp.getId());
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FonteDespesaORC salvarRetornandoComNovaTransacao(FonteDespesaORC fonte) {
        return em.merge(fonte);
    }
}
