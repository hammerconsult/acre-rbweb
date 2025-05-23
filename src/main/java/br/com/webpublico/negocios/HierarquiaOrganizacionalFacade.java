/*
 * Codigo gerado automaticamente em Fri May 13 15:37:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.entidadesauxiliares.PrevistoRealizadoDespesaUnidade;
import br.com.webpublico.enums.TipoGestor;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.exception.SQLGrammarException;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class HierarquiaOrganizacionalFacade extends AbstractFacade<HierarquiaOrganizacional> {

    private static final Logger logger = LoggerFactory.getLogger(HierarquiaOrganizacionalFacade.class);

    @EJB
    ConfiguracaoHierarquiaOrganizacionalFacade configuracaoHierarquiaOrganizacionalFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public HierarquiaOrganizacionalFacade() {
        super(HierarquiaOrganizacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }


    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalTipo(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();
        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE to_date(:data,'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
            sql.append(" and (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')  ");
            sql.append(" or VW.CODIGO like :str ");
            sql.append(" or replace(VW.CODIGO, '.', '') like :str ) ");
            sql.append(" order by vw.codigo");
        } else {
            sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE to_date(:data,'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
            sql.append(" and (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')  ");
            sql.append(" or VW.CODIGO like :str ");
            sql.append(" or replace(VW.CODIGO, '.', '') like :str ) ");
            sql.append(" order by vw.codigo ");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro.toUpperCase());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalAdministrativaVigenteOuNaoVigente(String parte, Date data, boolean apenasVigentes) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')  ");
        sql.append(" or VW.CODIGO like :str ");
        sql.append(" or replace(VW.CODIGO, '.', '') like :str ) ");
        if (apenasVigentes) {
            sql.append(" and to_date(:data,'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
        }
        sql.append(" order by vw.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro.toUpperCase());
        if (apenasVigentes) {
            q.setParameter("data", DataUtil.getDataFormatada(data));
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> listaTodasHierarquiaHorganizacionalTipo(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();
        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE to_date(:data,'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
            sql.append(" and (upper(vw.descricao) like :str  ");
            sql.append(" or VW.CODIGO like :str ");
            sql.append(" or replace(VW.CODIGO,'.','') like :str )");
            sql.append(" order by vw.codigo");
        } else {
            sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE to_date(:data,'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
            sql.append(" and (upper(vw.descricao) like :str  ");
            sql.append(" or VW.CODIGO like :str ");
            sql.append(" or replace(VW.CODIGO,'.','') like :str) ");
            sql.append(" order by vw.codigo");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro.toUpperCase());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalAdministrativa(String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str) ");
        sql.append(" and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/mm/yyyy')) >= to_date(:data, 'dd/mm/yyyy') ");
        sql.append(" order by h.codigo asc ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(20);
        return new ArrayList<>(new HashSet<HierarquiaOrganizacional>(q.getResultList()));
    }

    public List<HierarquiaOrganizacional> getListaDeHierarquiaAdministrativaOrdenadaPorCodigo(Date dataCorrente) throws ExcecaoNegocioGenerica {
        if (dataCorrente == null) {
            throw new ExcecaoNegocioGenerica("Data esta null");
        }

        StringBuilder sql = new StringBuilder();

        sql.append("  select ho.* " +
            "        from hierarquiaOrganizacional ho " +
            "     where  ( to_date(:data,'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data,'dd/mm/yyyy')) )"
            + "  and ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'"
            + "  order by ho.codigo asc ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataCorrente));

        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica(" A Hierarquia do tipo Administrativa não contem registros para a data de " + DataUtil.getDataFormatada(dataCorrente) + "!");
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdministrativaComResponsavelVigente(Date dataCorrente) throws ExcecaoNegocioGenerica {
        if (dataCorrente == null) {
            throw new ExcecaoNegocioGenerica("Data esta null");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("  select distinct hoadm.* from hierarquiaorganizacional hoadm ")
            .append(" inner join unidadeorganizacional uoadm on uoadm.id  = hoadm.subordinada_id ")
            .append("  and hoadm.tipohierarquiaorganizacional = :tipoHierarquia ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hoadm.iniciovigencia) and coalesce(trunc(hoadm.fimvigencia),to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append(" inner join hierarquiaorgresp hresp on hresp.hierarquiaorgadm_id = hoadm.id ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hresp.datainicio) and coalesce(trunc(hresp.datafim), to_date(:dataOperacao, 'dd/MM/yyyy')) ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataCorrente));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica(" A Hierarquia do tipo Administrativa não contem registros para a data de " + DataUtil.getDataFormatada(dataCorrente) + "!");
        }
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> buscarHierarquiaUsuarioPorTipo(String parte, UsuarioSistema usuario, Date dataOperacao, TipoHierarquiaOrganizacional tipoHierarquia) {
        return buscarHierarquiaUsuarioPorTipo(parte, usuario, dataOperacao, tipoHierarquia, null);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaUsuarioPorTipo(String parte, UsuarioSistema usuario, Date dataOperacao, TipoHierarquiaOrganizacional tipoHierarquia, Integer nivel) {
        String sql = " select distinct ho.* " +
            " from hierarquiaorganizacional ho " +
            " inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = ho.subordinada_id " +
            " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id  " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia " +
            "           and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "       and (lower(ho.descricao) like :str or ho.codigo like :str or replace(ho.codigo, '.', '') like :str)  " +
            "       and usu.id = :idUsuario " +
            "       and ho.tipohierarquiaorganizacional = :tipoHierarquia" +
                    (nivel != null ? " and ho.nivelnaentidade = :nivel " : "") +
            " order by ho.codigo ";

        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("tipoHierarquia", tipoHierarquia.name());
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

        if (nivel != null) {
            q.setParameter("nivel", nivel);
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHierarquiaUsuarioCorrentePorTipo(String parte, UsuarioSistema usu, Exercicio ex, Date data, String tipoHierarquiaOrganizacional) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select distinct horc.* FROM vwhierarquiaadministrativa VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " INNER JOIN HIERARQUIAORGRESP RESP on hadm.id = resp.hierarquiaorgadm_id  "
            + " inner join vwhierarquiaorcamentaria vworc on resp.hierarquiaorgorc_id = vworc.id "
            + " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = VWORC.SUBORDINADA_ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON HORC.SUBORDINADA_ID = U.ID "
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID "
            + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(hadm.iniciovigencia) and coalesce(trunc(hadm.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(HORC.iniciovigencia) and coalesce(trunc(HORC.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " AND ((replace(vworc.codigo,'.','') LIKE :parteCod) or vworc.codigo LIKE :parteCod OR (lower(vworc.descricao) LIKE :parteDesc))"
            + " and usu.id = :usu "
            + " AND HORC.TIPOHIERARQUIAORGANIZACIONAL = '").append(tipoHierarquiaOrganizacional).append("' order by horc.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        q.setParameter("parteCod", "%" + parte + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHierarquiaUsuarioCorrentePorNivel(String parte, UsuarioSistema usu, Exercicio ex, Date data, String tipoHierarquiaOrganizacional, Integer nivel) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT distinct horc.* FROM vwhierarquiaadministrativa VW "
                + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
                + " INNER JOIN HIERARQUIAORGRESP RESP on hadm.id = resp.hierarquiaorgadm_id "
                + " inner join vwhierarquiaorcamentaria vworc on resp.hierarquiaorgorc_id = vworc.id "
                + " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = VWORC.SUBORDINADA_ID "
                + " INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON HORC.SUBORDINADA_ID = U.ID "
                + " INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID AND horc.tipohierarquiaorganizacional = '" + tipoHierarquiaOrganizacional.toUpperCase() + "'"
                + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id "
                + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
                + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
                + " and to_date(:data, 'dd/MM/yyyy') between hadm.iniciovigencia and coalesce(hadm.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
                + " and to_date(:data, 'dd/MM/yyyy') between HORC.iniciovigencia and coalesce(HORC.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
                + " and (lower(vworc.descricao) like :str "
                + "     or vworc.CODIGO like :str "
                + "     or replace(vworc.codigo,'.','') like :str) "
                + " and usu.id = :usu "
                + " and horc.indicedono = :nivel "
                + " order by horc.codigo ");
            Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
            q.setParameter("usu", usu.getId());
            q.setParameter("nivel", nivel);
            q.setParameter("str", "%" + parte.toLowerCase() + "%");
            q.setParameter("data", DataUtil.getDataFormatada(data));
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<HierarquiaOrganizacional> listaHierarquiaOrgaoUsuarioCorrentePorNivel(String parte, UsuarioSistema usu, Exercicio ex, Date data, String tipoHierarquiaOrganizacional, Integer nivel) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct horg.* FROM vwhierarquiaadministrativa VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " inner join vwhierarquiaorcamentaria vworc on hadm.hierarquiaorc_id = vworc.id "
            + " inner join vwhierarquiaorcamentaria vworg on vworc.SUPERIOR_ID = vworg.subordinada_id "
            + " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = VWORC.SUBORDINADA_ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON HORC.SUBORDINADA_ID = U.ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HORG ON HORC.SUPERIOR_ID = HORG.SUBORDINADA_ID "
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID AND horG.tipohierarquiaorganizacional = '" + tipoHierarquiaOrganizacional.toUpperCase() + "'"
            + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworg.iniciovigencia and coalesce(vworg.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(HORC.iniciovigencia) and coalesce(trunc(HORC.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(HORG.iniciovigencia) and coalesce(trunc(HORG.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " and (lower(vworg.descricao) like :str or vworg.CODIGO like :str ) "
            + " and usu.id = :usu  "
            + " and horg.indicedono = :nivel "
            + " order by horg.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("nivel", nivel);
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHierarquiaUsuarioCorrenteIntervaloCodigo(String codigoUnidadeInicio, String codigoUnidadeFim, String codigoOrgaoInicio, String codigoOrgaoFim, UsuarioSistema usu, Exercicio ex, Date data, String tipoHierarquiaOrganizacional) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select distinct horc.* FROM vwhierarquiaadministrativa VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " INNER JOIN HIERARQUIAORGRESP RESP on hadm.id = resp.hierarquiaorgadm_id  "
            + " inner join vwhierarquiaorcamentaria vworc on resp.hierarquiaorgorc_id = vworc.id "
            + " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = VWORC.SUBORDINADA_ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON HORC.SUBORDINADA_ID = U.ID "
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID "
            + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(hadm.iniciovigencia) and coalesce(trunc(hadm.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(HORC.iniciovigencia) and coalesce(trunc(HORC.fimvigencia), to_date(:data, 'dd/MM/yyyy'))"
            + " and usu.id = :usu "
            + " and horc.NIVELNAENTIDADE = 3");
        if (!codigoUnidadeInicio.equals("") && !codigoUnidadeFim.equals("")) {
            sql.append(" and SUBSTR(vworc.CODIGO, 8, 3) between \'").append(codigoUnidadeInicio).append("\' and \'").append(codigoUnidadeFim).append("\' ");
        }
        if (!codigoOrgaoInicio.equals("") && !codigoOrgaoFim.equals("")) {
            sql.append(" and SUBSTR(vworc.CODIGO, 4, 3) between \'").append(codigoOrgaoInicio).append("\' and \'").append(codigoOrgaoFim).append("\' ");
        }
        sql.append(" AND HORC.TIPOHIERARQUIAORGANIZACIONAL = '").append(tipoHierarquiaOrganizacional).append("' order by horc.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }


    public ConfiguracaoHierarquiaOrganizacionalFacade getConfiguracaoHierarquiaOrganizacionalFacade() {
        return configuracaoHierarquiaOrganizacionalFacade;
    }

    public int niveisHierarquia(TipoHierarquiaOrganizacional tipo) {
        ConfiguracaoHierarquiaOrganizacional conf = configuracaoHierarquiaOrganizacionalFacade.configuracaoVigente(tipo);
        if (conf != null) {
            return conf.getMascara().split("\\.").length;
        } else {
            throw new ExcecaoNegocioGenerica("Não existe nenhuma Mascara cadastrada para o Tipo " + tipo.getDescricao());
        }
    }

    public HierarquiaOrganizacional getPaiDe(HierarquiaOrganizacional ho, Date hoje) {
        if (ho.getSuperior() == null) {
            return null;
        }
        String hql = " select ho from HierarquiaOrganizacional ho "
            + " where coalesce(trunc(ho.fimVigencia), to_date(:hoje,'dd/MM/yyyy')) >= to_date(:hoje,'dd/MM/yyyy') "
            + " and ho.subordinada = :subordinada "
            + " and (ho.tipoHierarquiaOrganizacional=:tipoHO or ho.superior is null)";
        Query q = em.createQuery(hql, HierarquiaOrganizacional.class);

        q.setParameter("subordinada", ho.getSuperior());
        q.setParameter("tipoHO", ho.getTipoHierarquiaOrganizacional());
        q.setParameter("hoje", DataUtil.getDataFormatada(hoje));
        q.setMaxResults(1);
        try {
            HierarquiaOrganizacional h = (HierarquiaOrganizacional) q.getSingleResult();
            h.getHierarquiaOrganizacionalCorrespondentes().size();
            h.getHierarquiaOrganizacionalResponsavels().size();
            return h;
        } catch (NoResultException e) {
            if (ho.getIndiceDoNo() != 1) {
                throw new ExcecaoNegocioGenerica("Hierarquia Organizacional " + ho.getCodigo() + " " + ho.getSubordinada().getDescricao() + " não possui superior.");
            }
            return null;
        }
    }

    @Override
    public HierarquiaOrganizacional recuperar(Object id) {
        HierarquiaOrganizacional ho = super.recuperar(((HierarquiaOrganizacional) id).getId());
        ho.getHierarquiaOrganizacionalCorrespondentes().size();
        ho.getHierarquiaOrganizacionalResponsavels().size();
        return ho;
    }

    public HierarquiaOrganizacional recuperarPeloId(Long id) {
        HierarquiaOrganizacional ho = em.find(HierarquiaOrganizacional.class, id);
        ho.getHierarquiaOrganizacionalCorrespondentes().size();
        ho.getHierarquiaOrganizacionalResponsavels().size();
        return ho;
    }

    public HierarquiaOrganizacional recuperar(Long id) {
        HierarquiaOrganizacional ho = super.recuperar(id);
        return ho;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionaisPorTipoVigente(Date fim, TipoHierarquiaOrganizacional tipoHO) {
        String SQL = " SELECT  * FROM hierarquiaorganizacional "
            + " WHERE to_date(:fim, 'dd/mm/yyyy') between trunc(iniciovigencia) and coalesce(trunc(fimvigencia), to_date(:fim, 'dd/mm/yyyy'))"
            + " AND tipohierarquiaorganizacional =:tipo ORDER BY codigo";
        Query q = getEntityManager().createNativeQuery(SQL, HierarquiaOrganizacional.class);
        q.setParameter("fim", DataUtil.getDataFormatada(fim));
        q.setParameter("tipo", tipoHO.name());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaOrdenadaPorIndiceDoNo(TipoHierarquiaOrganizacional tipo, Date data) throws ExcecaoNegocioGenerica {
        StringBuilder sql = new StringBuilder();
        if (tipo == null) {
            throw new ExcecaoNegocioGenerica("Tipo da Hierarquia esta vazio! Selecione o tipo de Hierarquia Organizacional");
        } else if (data == null) {
            throw new ExcecaoNegocioGenerica("Data não informada!");
        }
        sql.append(" select ho.* from HIERARQUIAORGANIZACIONAL ho "
            + " where ho.TIPOHIERARQUIAORGANIZACIONAL =:tipo "
            + " and to_date(:data,'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:data,'dd/mm/yyyy'))"
            + " order by ho.codigo asc ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipo", tipo.toString());
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("A Hierarquia do " + tipo.getDescricao() + " não contem registros para a data de " + DataUtil.getDataFormatada(data) + "!");
        }
        return q.getResultList();

    }

    public boolean isUnidadeVigenteEmLotacaoOrVinculoOrFolha(UnidadeOrganizacional unid, Date dataEncerramentoHierarquia) {
        String sql = "select distinct unidade.* " +
            "from vinculofp vinculo " +
            "         inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.ID " +
            "where to_date(:dataOperacao, 'dd/MM/yyyy') between vinculo.INICIOVIGENCIA " +
            "    and coalesce(vinculo.FINALVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  and unidade.id = :unidId " +
            "union all " +
            "select distinct unidade.* " +
            "from vinculofp vinculo " +
            "         inner join LOTACAOFUNCIONAL lotacao on vinculo.ID = lotacao.VINCULOFP_ID " +
            "         inner join UNIDADEORGANIZACIONAL unidade on lotacao.UNIDADEORGANIZACIONAL_ID = unidade.ID " +
            "where to_date(:dataOperacao, 'dd/MM/yyyy') between lotacao.INICIOVIGENCIA " +
            "    and coalesce(lotacao.FINALVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  and unidade.id = :unidId " +
            "union all " +
            "select distinct unidade.* " +
            "from vinculofp vinculo " +
            "         inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.ID " +
            "         inner join FICHAFINANCEIRAFP ficha on unidade.ID = ficha.UNIDADEORGANIZACIONAL_ID " +
            "         inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "where to_date('01' || '/' || to_char(folha.MES + 1) || '/' || to_char(folha.ANO), 'dd/MM/yyyy') > " +
            "      to_date(:dataEncerramentoHierarquia, 'dd/MM/yyyy') " +
            "  and unidade.id = :unidId";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("unidId", unid.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(SistemaFacade.getDataCorrente()));
        q.setParameter("dataEncerramentoHierarquia", DataUtil.getDataFormatada(dataEncerramentoHierarquia));
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public List<HierarquiaOrganizacional> getHierarquiasFilhasDe(UnidadeOrganizacional uo, Date data, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        String sql = "SELECT h.* FROM HierarquiaOrganizacional h  "
            + " WHERE to_date(:dataAtual, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia),  to_date(:dataAtual, 'dd/mm/yyyy'))"
            + " AND h.superior_id = :superior "
            + " AND h.tipoHierarquiaOrganizacional = :tipo"
            + " ORDER BY h.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("superior", uo.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(data));
        q.setParameter("tipo", tipoHierarquiaOrganizacional.toString());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasFilhasVigentesOuNaoVigentes(UnidadeOrganizacional uo, Date data, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, Boolean apenasVigentes) {
        String sql = "SELECT h.* FROM HierarquiaOrganizacional h  "
            + " WHERE  h.superior_id = :superior "
            + " AND h.tipoHierarquiaOrganizacional = :tipo";
        if (apenasVigentes) {
            sql += " AND to_date(:dataAtual, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia),  to_date(:dataAtual, 'dd/mm/yyyy'))";
        }
        sql += " ORDER BY h.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("superior", uo.getId());
        if (apenasVigentes) {
            q.setParameter("dataAtual", DataUtil.getDataFormatada(data));
        }
        q.setParameter("tipo", tipoHierarquiaOrganizacional.toString());
        return q.getResultList();
    }

    /**
     * Método utilizado para filtrar hierarquias da view utilizando a vigência e
     * não mais o exercicio.
     *
     * @param parte
     * @param data  data que será filtrada a hierarquia
     * @return
     */
    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalTipo(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT h.* FROM HIERARQUIAORGANIZACIONAL h ");
        sql.append(" WHERE to_date(:data, 'dd/mm/yyyy') between trunc(h.INICIOVIGENCIA) and coalesce(trunc(h.FIMVIGENCIA), to_date(:data, 'dd/mm/yyyy')) and h.tipoHierarquiaOrganizacional = :tipo ");
        sql.append(" and (upper(h.descricao) like :str or h.CODIGO like :str) ");
        sql.append("  order by h.codigo");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipo", tipo);
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalEntidadeTipo(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
        } else {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
        }
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" inner join UNIDADEORGANIZACIONAL uni on uni.id = h.subordinada_id ");
        sql.append(" where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
        sql.append(" and uni.entidade_id is not null and (upper(translate(vw.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','AAAAAAAAEEEEIIOOOOOOUUUUCC'))) like :str  ");
        sql.append(" or (VW.CODIGO like :str)  order by h.codigo");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(20);
        return q.getResultList();
    }

    public HierarquiaOrganizacional getRaizHierarquia(Date data) {
        String sql = "SELECT ho.* FROM VWHIERARQUIAADMINISTRATIVA vw " +
            " INNER JOIN HIERARQUIAORGANIZACIONAL ho ON ho.id = vw.id " +
            " WHERE vw.SUPERIOR_ID IS null " +
            " AND to_date(:data, 'dd/MM/yyyy') between vw.INICIOVIGENCIA AND coalesce(vw.FIMVIGENCIA, to_date(:data, 'dd/MM/yyyy')) " +
            " ORDER BY ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(1);
        try {
            if (!q.getResultList().isEmpty()) {
                HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) q.getSingleResult();
                return hierarquiaOrganizacional;
            }
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhuma Hierarquia encontrada para esta vigência" + e);
        }
        return null;
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalTipo(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
            sql.append(" and (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) ");
            sql.append("  like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ");
            sql.append(" or VW.CODIGO like :str or replace(VW.CODIGO,'.','') like :str)  order by VW.codigo");
        } else {
            sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
            sql.append(" and (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) ");
            sql.append("  like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ");
            sql.append(" or VW.CODIGO like :str or replace(VW.CODIGO,'.','') like :str)  order by VW.codigo");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("str", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("data", Util.formatterDiaMesAno.format(data));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalAdministrativaDoUsuario(String parte,
                                                                                                String nivelEstrutura,
                                                                                                Date data,
                                                                                                UsuarioSistema usuarioCorrente,
                                                                                                HierarquiaOrganizacional hoOrc,
                                                                                                Boolean gestorPatrimonio) {
        String sql = "   SELECT * FROM ( " +
            "   SELECT DISTINCT H.* " +
            "      FROM VWHIERARQUIAADMINISTRATIVA VWADM " +
            "     INNER JOIN HIERARQUIAORGRESP REL_ADM_ORC ON REL_ADM_ORC.HIERARQUIAORGADM_ID = VWADM.ID " +
            "     INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.ID = REL_ADM_ORC.HIERARQUIAORGORC_ID " +
            "     INNER JOIN HIERARQUIAORGANIZACIONAL H ON H.ID = VWADM.ID " +
            "     INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = H.SUBORDINADA_ID  " +
            "     INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON UU.UNIDADEORGANIZACIONAL_ID = U.ID " +
            "   WHERE to_date(:DATA, 'dd/mm/yyyy') BETWEEN VWADM.INICIOVIGENCIA   AND COALESCE(VWADM.FIMVIGENCIA, to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND to_date(:DATA, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA   AND COALESCE(VWORC.FIMVIGENCIA, to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND to_date(:DATA, 'dd/mm/yyyy') BETWEEN trunc(REL_ADM_ORC.DATAINICIO) AND COALESCE(trunc(REL_ADM_ORC.DATAFIM), to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND (UPPER(translate(VWADM.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) LIKE translate(:STR,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "         OR UPPER(VWADM.DESCRICAO) LIKE :STR " +
            "         OR VWADM.CODIGO LIKE :STR " +
            "         or (REPLACE(VWADM.CODIGO, '.','') like :STR)) ";
        if (gestorPatrimonio != null) {
            sql += "     AND UU.GESTORPATRIMONIO = :GESTOR_PATRIMONIO ";
        }
        sql += "  AND UU.USUARIOSISTEMA_ID = :USUARIO_ID ";
        if (nivelEstrutura != null) {
            sql += " AND H.INDICEDONO = :NIVEL_ESTRUTURA ";
        }
        if (hoOrc != null && hoOrc.getId() != null) {
            sql += " AND VWORC.SUBORDINADA_ID = :ID_SUBORDINADA_ORC ";
        }
        sql += "   ) H " +
            "   ORDER BY H.CODIGO ";

        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("STR", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("DATA", DataUtil.getDataFormatada(data));
        if (gestorPatrimonio != null) {
            q.setParameter("GESTOR_PATRIMONIO", gestorPatrimonio);
        }
        q.setParameter("USUARIO_ID", usuarioCorrente.getId());
        if (nivelEstrutura != null) {
            q.setParameter("NIVEL_ESTRUTURA", nivelEstrutura);
        }
        if (hoOrc != null && hoOrc.getId() != null) {
            q.setParameter("ID_SUBORDINADA_ORC", hoOrc.getSubordinada().getId());
        }
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(String parte,
                                                                                                               String nivelEstrutura,
                                                                                                               Date data,
                                                                                                               UsuarioSistema usuarioCorrente, HierarquiaOrganizacional hoAdm) {
        String sql = " SELECT * FROM (  " +
            "   SELECT DISTINCT HORC.* " +
            "      FROM VWHIERARQUIAORCAMENTARIA VWORC " +
            "     INNER JOIN HIERARQUIAORGRESP REL_ADM_ORC ON REL_ADM_ORC.HIERARQUIAORGORC_ID = VWORC.ID " +
            "     INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM ON VWADM.ID = REL_ADM_ORC.HIERARQUIAORGADM_ID " +
            "     INNER JOIN HIERARQUIAORGANIZACIONAL HORC ON HORC.ID = VWORC.ID " +
            "     INNER JOIN HIERARQUIAORGANIZACIONAL HADM ON HADM.ID = VWADM.ID " +
            "     INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = HADM.SUBORDINADA_ID  " +
            "     INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON UU.UNIDADEORGANIZACIONAL_ID = U.ID " +
            "   WHERE to_date(:DATA, 'dd/mm/yyyy')BETWEEN VWADM.INICIOVIGENCIA   AND COALESCE(VWADM.FIMVIGENCIA, to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND to_date(:DATA, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA   AND COALESCE(VWORC.FIMVIGENCIA, to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND to_date(:DATA, 'dd/mm/yyyy') BETWEEN trunc(REL_ADM_ORC.DATAINICIO) AND COALESCE(trunc(REL_ADM_ORC.DATAFIM), to_date(:DATA, 'dd/mm/yyyy')) " +
            "     AND (lower(translate(VWORC.DESCRICAO,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) LIKE :STR " +
            "           OR lower(VWORC.DESCRICAO) LIKE :STR " +
            "           OR VWORC.CODIGO LIKE :STR" +
            "           OR (REPLACE(VWORC.CODIGO, '.','') like :STR)) " +
            "     AND UU.GESTORPATRIMONIO = :GESTOR " +
            "     AND UU.USUARIOSISTEMA_ID = :USUARIO_ID " +
            (nivelEstrutura == null ? "" : " AND HADM.INDICEDONO = :NIVEL_ESTRUTURA ") +
            (hoAdm != null && hoAdm.getId() != null ? " AND VWADM.SUBORDINADA_ID = :ID_SUBORDINADA_ADM " : "") +
            " ) H " +
            "   ORDER BY H.CODIGO ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);

        q.setParameter("STR", "%" + StringUtil.removeAcentuacao(parte).trim().toLowerCase() + "%");
        q.setParameter("DATA", DataUtil.getDataFormatada(data));
        q.setParameter("GESTOR", Boolean.TRUE);
        q.setParameter("USUARIO_ID", usuarioCorrente.getId());
        if (nivelEstrutura != null) {
            q.setParameter("NIVEL_ESTRUTURA", nivelEstrutura);
        }
        if (hoAdm != null && hoAdm.getId() != null) {
            q.setParameter("ID_SUBORDINADA_ADM", hoAdm.getSubordinada().getId());
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndNivelAndDataAndUsuarioCorrenteAndGestorLicitacao(String codigoOrDescricao,
                                                                                                                                                              String nivelEstrutura, Date data, UsuarioSistema usuarioCorrente) {
        String sql = "  select  dados.* from(" +
            " SELECT DISTINCT h.* " +
            "    FROM vwhierarquiaadministrativa vw " +
            "   INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            "   INNER JOIN unidadeorganizacional u on u.id = h.subordinada_id " +
            "   INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id " +
            " WHERE :data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, :data)" +
            "   AND (upper(vw.descricao) LIKE :codigoOrDescricao OR vw.codigo LIKE :codigoOrDescricao OR replace(vw.codigo, '.', '') LIKE :codigoOrDescricao) " +
            "   AND uu.gestorlicitacao = 1 " +
            "   AND uu.usuariosistema_id = :usuario_id" +
            (nivelEstrutura == null ? "" : " AND h.indicedono = :nivel_estrutura") +
            " ) dados " +
            " ORDER BY dados.codigo ";

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.trim().toUpperCase() + "%");
        q.setParameter("data", data, TemporalType.DATE);
        q.setParameter("usuario_id", usuarioCorrente.getId());

        if (nivelEstrutura != null) {
            q.setParameter("nivel_estrutura", nivelEstrutura);
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndDataComLocalDeEstoqueOndeUsuarioEhGestor(String codigoOrDescricao,
                                                                                                                                                      Date data,
                                                                                                                                                      UsuarioSistema usuarioCorrente) {
        String sql = " select dados.* from(" +
            " select distinct h.* " +
            "    from vwhierarquiaadministrativa vw " +
            "   inner join hierarquiaorganizacional h on h.id = vw.id " +
            "   inner join unidadeorganizacional u on u.id = h.subordinada_id " +
            "   inner join localestoque le on le.unidadeorganizacional_id = u.id" +
            "   inner join gestorlocalestoque gle on gle.localestoque_id = le.id" +
            " where :dataOperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, :dataOperacao)" +
            "   and gle.usuariosistema_id = :id_usuario" +
            "   and :dataOperacao between gle.iniciovigencia and coalesce(gle.fimvigencia, :dataOperacao)" +
            "   and (upper(vw.descricao) like :codigo_descricao or vw.codigo like :codigo_descricao)) dados " +
            " order by dados.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("codigo_descricao", "%" + codigoOrDescricao.trim().toUpperCase() + "%");
        q.setParameter("dataOperacao", data, TemporalType.DATE);
        q.setParameter("id_usuario", usuarioCorrente.getId());
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(UsuarioSistema usuarioSistema, Date dataReferencia) {
        String sql = "    SELECT distinct " +
            "              HIERARQUIAORC.* " +
            "      FROM HIERARQUIAORGRESP " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA   VWORC   ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
            "INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = VWADM.SUBORDINADA_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAORC ON HIERARQUIAORC.ID = VWORC.ID" +
            "     WHERE to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA,to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA,to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN trunc(HIERARQUIAORGRESP.DATAINICIO) AND COALESCE(trunc(HIERARQUIAORGRESP.DATAFIM), to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND uu.gestorpatrimonio = 1 " +
            "       AND uu.usuariosistema_id = :usuario_id" +
            "  ORDER BY HIERARQUIAORC.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarFiltrandoHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(UsuarioSistema usuarioSistema, Date dataReferencia, String parametro) {
        String sql = "    SELECT distinct " +
            "              HIERARQUIAORC.* " +
            "      FROM HIERARQUIAORGRESP " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA   VWORC   ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
            "INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = VWADM.SUBORDINADA_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAORC ON HIERARQUIAORC.ID = VWORC.ID" +
            "     WHERE to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA, to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN trunc(HIERARQUIAORGRESP.DATAINICIO) AND COALESCE(trunc(HIERARQUIAORGRESP.DATAFIM), to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND (upper(VWORC.descricao) LIKE :codigoOrDescricao OR VWORC.codigo LIKE :codigoOrDescricao or replace(vworc.codigo, '.', '') like :codigoOrDescricao) " +
            "       AND uu.gestorpatrimonio = 1 " +
            "       AND uu.usuariosistema_id = :usuario_id" +
            "  ORDER BY HIERARQUIAORC.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("codigoOrDescricao", "%" + parametro.trim().toUpperCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio(String parteDigitada, String nivelEstrutura, UsuarioSistema usuarioSistema, Date dataDaOperacao) {
        String sql = "with dados(id, superior_id, subordinada_id, codigo, descricao ) as (" +
            "  select vw.id, vw.superior_id, vw.subordinada_id, vw.codigo, vw.descricao " +
            "  from vwhierarquiaadministrativa vw" +
            "  inner join hierarquiaorganizacional ho on ho.id = vw.id" +
            "  where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia   " +
            "  and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "  AND nivelestrutura(ho.codigo, '.') = 2 " +
            "  and substr(vw.codigo,1,6) in (select substr(ho.codigo,1,6)" +
            "                                  from usuariosistema us" +
            "                                 inner join usuariounidadeorganizacio uu on uu.usuariosistema_id = us.id" +
            "                                 inner join hierarquiaorganizacional ho on ho.subordinada_id = uu.unidadeorganizacional_id" +
            "                                 where us.id = :usuarioId " +
            "                                   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "                                   and uu.gestorpatrimonio = 1)" +
            "  " +
            "  UNION ALL " +
            "  " +
            "  select vw.id, vw.superior_id, vw.subordinada_id, vw.codigo, vw.descricao " +
            "  from vwhierarquiaadministrativa vw " +
            "  inner join dados dd on dd.subordinada_id = vw.superior_id" +
            "  where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia   " +
            "  and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            ")" +
            " select distinct ho.* " +
            "   from dados dd" +
            " inner join hierarquiaorganizacional ho on ho.id = dd.id" +
            " where ((replace(dd.codigo,'.','') LIKE :filtro) or dd.codigo LIKE :filtro OR (UPPER(dd.descricao) LIKE :filtro))" +
            "    and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            (nivelEstrutura == null ? "" : " and ho.indicedono = :nivelEstrutura") +
            " order by ho.codigo asc";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataDaOperacao));
        q.setParameter("usuarioId", usuarioSistema.getId());
        q.setParameter("filtro", "%" + parteDigitada.toUpperCase().trim() + "%");

        if (nivelEstrutura != null) {
            q.setParameter("nivelEstrutura", nivelEstrutura);
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraPorNivel(String parte, String nivel, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        String nomeView = "";

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            nomeView = "VWHIERARQUIAORCAMENTARIA";
        } else {
            nomeView = "VWHIERARQUIAADMINISTRATIVA";
        }
        sql.append(" SELECT DISTINCT h.* FROM " + nomeView + " VW ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data) ");
        sql.append("  and h.INDICEDONO = :nivel ");
        sql.append(" and ((replace(VW.CODIGO, '.', '') like :str) ");
        sql.append("   or VW.CODIGO like :str ");
        sql.append("   or upper(vw.descricao) like :str) ");
        sql.append("order by h.codigo");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", nivel);
        q.setParameter("data", data);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaPorUsuarioEIndiceDoNo(UsuarioSistema usuarioSistema, String parte, String tipo, Date data, Integer indiceDoNo) {
        StringBuilder sql = new StringBuilder();

        String nomeView = "";

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            nomeView = "VWHIERARQUIAORCAMENTARIA";
        } else {
            nomeView = "VWHIERARQUIAADMINISTRATIVA";
        }
        sql.append(" select distinct h.* from " + nomeView + " vw ");
        sql.append(" inner join hierarquiaorganizacional h on h.id= vw.id ");
        sql.append(" inner join unidadeorganizacional u on u.id = vw.subordinada_id ");
        sql.append(" inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = vw.subordinada_id ");
        sql.append(" inner join usuariosistema usu on usuund.usuariosistema_id = usu.id ");
        sql.append(" where :data between vw.iniciovigencia  and coalesce(vw.fimvigencia, :data) ");
        sql.append(" and h.indicedono = :indiceDoNo ");
        sql.append("AND ((replace(vw.codigo,'.','') LIKE :parte) or vw.codigo LIKE :parte OR (upper(vw.descricao) LIKE :parte))");
        sql.append(" and usu.id = :usuario ");
        sql.append(" order by h.codigo ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("parte", parametro);
        q.setParameter("indiceDoNo", indiceDoNo);
        q.setParameter("data", data);
        q.setParameter("usuario", usuarioSistema);
        q.setMaxResults(10);
        return q.getResultList();
    }

    //@comRegistros - filtra Orgão/Entidade/Funda que possua objetofrota vinculado
    private List<HierarquiaOrganizacional> listaOrgaoEntidadeFundoFrotas(UsuarioSistema usuarioSistema,
                                                                         String parte,
                                                                         Date data,
                                                                         Boolean comRegistros) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select distinct h.* from VWHIERARQUIAADMINISTRATIVA vw ");
        sql.append(" inner join hierarquiaorganizacional h on h.id= vw.id ");
        sql.append(" inner join unidadeorganizacional u on u.id = vw.subordinada_id ");
        sql.append(" inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = vw.subordinada_id ");
        sql.append(" inner join usuariosistema usu on usuund.usuariosistema_id = usu.id ");
        sql.append(" where :data between vw.iniciovigencia  and coalesce(vw.fimvigencia, :data) ");
        sql.append(" and h.indicedono = :indiceDoNo ");
        sql.append("AND ((replace(vw.codigo,'.','') LIKE :parte) or vw.codigo LIKE :parte OR (upper(vw.descricao) LIKE :parte))");
        sql.append(" and usu.id = :usuario ");
        if (comRegistros) {
            sql.append(" and exists(select 1 from objetoFrota obj where obj.unidadeOrganizacional_id = vw.subordinada_id) ");
        }
        sql.append(" order by h.codigo ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("parte", parametro);
        q.setParameter("indiceDoNo", 2);
        q.setParameter("data", data);
        q.setParameter("usuario", usuarioSistema);
        q.setMaxResults(50);
        return q.getResultList();
    }

    //@comRegistros - filtra Orgão/Entidade/Funda que possua objetofrota vinculado
    public List<HierarquiaOrganizacional> buscarOrgaoEntidadeFundoFrotasComRegistros(String parte,
                                                                                     Date data,
                                                                                     Boolean comRegistros) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select distinct h.* from VWHIERARQUIAADMINISTRATIVA vw ");
        sql.append(" inner join hierarquiaorganizacional h on h.id= vw.id ");
        sql.append(" where :data between vw.iniciovigencia  and coalesce(vw.fimvigencia, :data) ");
        sql.append(" and h.indicedono = :indiceDoNo ");
        sql.append("AND ((replace(vw.codigo,'.','') LIKE :parte) or vw.codigo LIKE :parte OR (upper(vw.descricao) LIKE :parte))");
        if (comRegistros) {
            sql.append(" and exists(select 1 from objetoFrota obj where obj.unidadeOrganizacional_id = vw.subordinada_id) ");
        }
        sql.append(" order by h.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("parte", parametro);
        q.setParameter("indiceDoNo", 2);
        q.setParameter("data", data);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaOrgaoEntidadeFundoFrotas(UsuarioSistema usuarioSistema, String parte, Date data) {
        return listaOrgaoEntidadeFundoFrotas(usuarioSistema, parte, data, Boolean.FALSE);
    }

    public List<HierarquiaOrganizacional> listaHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalPai,
                                                                                                            UsuarioSistema usuarioSistema, Date data, String parte) {
        String sql = " " +
            "   with dados (id, subordinada_id, iniciovigencia, fimvigencia, codigo, descricao) as ( " +
            "       select vwpai.id, vwpai.subordinada_id, vwpai.iniciovigencia, vwpai.fimvigencia, " +
            "              vwpai.codigo, vwpai.descricao " +
            "       from vwhierarquiaadministrativa vwpai " +
            "       where vwpai.id = :hierarquiapai_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vwpai.iniciovigencia and coalesce(vwpai.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   union all " +
            "       select vwfilho.id, vwfilho.subordinada_id, vwfilho.iniciovigencia, vwfilho.fimvigencia, " +
            "              vwfilho.codigo, vwfilho.descricao " +
            "       from vwhierarquiaadministrativa vwfilho " +
            "       inner join dados on vwfilho.superior_id = dados.subordinada_id  " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vwfilho.iniciovigencia and coalesce(vwfilho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))) " +
            "   select distinct h.* " +
            "   from dados " +
            "       inner join hierarquiaorganizacional h on h.id= dados.id " +
            "       inner join unidadeorganizacional u on u.id = dados.subordinada_id " +
            "       inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = dados.subordinada_id " +
            "       inner join usuariosistema usu on usuund.usuariosistema_id = usu.id " +
            "   where to_date(:dataOperacao, 'dd/MM/yyyy') between dados.iniciovigencia  and coalesce(dados.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "       and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "       and ((replace(dados.codigo,'.','') like :parte) or dados.codigo like :parte or (upper(dados.descricao) like :parte)) " +
            "       and usu.id = :usuario_id " +
            "       and dados.id <> :hierarquiapai_id " +
            "  order by h.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("hierarquiapai_id", hierarquiaOrganizacionalPai.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("parte", parametro);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalPai,
                                                                                                             Date data) {
        String sql = " with dados (id, subordinada_id, iniciovigencia, fimvigencia, codigo, descricao) as ( " +
            " select vwpai.id, vwpai.subordinada_id, vwpai.iniciovigencia, vwpai.fimvigencia, " +
            "        vwpai.codigo, vwpai.descricao " +
            "    from vwhierarquiaadministrativa vwpai " +
            " where vwpai.id = :hierarquiapai_id " +
            "   and to_date(:data, 'dd/mm/yyyy') between vwpai.iniciovigencia and coalesce(vwpai.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " union all " +
            " select vwfilho.id, vwfilho.subordinada_id, vwfilho.iniciovigencia, vwfilho.fimvigencia, " +
            "       vwfilho.codigo, vwfilho.descricao " +
            "   from vwhierarquiaadministrativa vwfilho " +
            "  inner join dados on vwfilho.superior_id = dados.subordinada_id " +
            "   and to_date(:data, 'dd/mm/yyyy') between vwfilho.iniciovigencia and coalesce(vwfilho.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " )select distinct h.* " +
            "    from dados " +
            "   inner join hierarquiaorganizacional h on h.id= dados.id " +
            "   inner join unidadeorganizacional u on u.id = dados.subordinada_id " +
            " where to_date(:data, 'dd/mm/yyyy') between dados.iniciovigencia and coalesce(dados.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "   and to_date(:data, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/mm/yyyy')) " +
            "   and dados.id <> :hierarquiapai_id " +
            " order by h.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("hierarquiapai_id", hierarquiaOrganizacionalPai.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativaVigenteOuNaoVigente(HierarquiaOrganizacional hierarquiaOrganizacionalPai,
                                                                                                                                Date data, Boolean apenasVigente) {
        String sql = " with dados (id, subordinada_id, iniciovigencia, fimvigencia, codigo, descricao) as ( " +
            " select vwpai.id, vwpai.subordinada_id, vwpai.iniciovigencia, vwpai.fimvigencia, " +
            "        vwpai.codigo, vwpai.descricao " +
            "    from vwhierarquiaadministrativa vwpai " +
            " where vwpai.id = :hierarquiapai_id ";
        if (apenasVigente) {
            sql += "   and to_date(:data, 'dd/mm/yyyy') between vwpai.iniciovigencia and coalesce(vwpai.fimvigencia, to_date(:data, 'dd/mm/yyyy')) ";
        }
        sql += " union all " +
            " select vwfilho.id, vwfilho.subordinada_id, vwfilho.iniciovigencia, vwfilho.fimvigencia, " +
            "       vwfilho.codigo, vwfilho.descricao " +
            "   from vwhierarquiaadministrativa vwfilho " +
            "  inner join dados on vwfilho.superior_id = dados.subordinada_id ";
        if (apenasVigente) {
            sql += "   and to_date(:data, 'dd/mm/yyyy') between vwfilho.iniciovigencia and coalesce(vwfilho.fimvigencia, to_date(:data, 'dd/mm/yyyy')) ";
        }
        sql += " )select distinct h.* " +
            "    from dados " +
            "   inner join hierarquiaorganizacional h on h.id= dados.id " +
            "   inner join unidadeorganizacional u on u.id = dados.subordinada_id " +
            " where dados.id <> :hierarquiapai_id ";
        if (apenasVigente) {
            sql += "   and to_date(:data, 'dd/mm/yyyy') between dados.iniciovigencia and coalesce(dados.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
                "   and to_date(:data, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/mm/yyyy')) ";
        }
        sql += " order by h.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("hierarquiapai_id", hierarquiaOrganizacionalPai.getId());
        if (apenasVigente) {
            q.setParameter("data", DataUtil.getDataFormatada(data));
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraPorNivelTrazendoTodas(String parte, String nivel, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        String nomeView = "";

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            nomeView = "VWHIERARQUIAORCAMENTARIA";
        } else {
            nomeView = "VWHIERARQUIAADMINISTRATIVA";
        }
        sql.append(" SELECT DISTINCT h.* FROM " + nomeView + " VW ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data) ");
        sql.append("and h.INDICEDONO = :nivel and ( VW.CODIGO like :str or upper(vw.descricao) like :str)  order by h.codigo");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", nivel);
        q.setParameter("data", data);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaTodasPorNivel(String parte, String nivel, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        String nomeView = "";

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            nomeView = "VWHIERARQUIAORCAMENTARIA";
        } else {
            nomeView = "VWHIERARQUIAADMINISTRATIVA";
        }
        sql.append(" SELECT DISTINCT h.* FROM " + nomeView + " VW ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data) ");
        sql.append("and h.INDICEDONO = :nivel and ( VW.CODIGO like :str or upper(vw.descricao) like :str)  order by h.codigo");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", nivel);
        q.setParameter("data", data != null ? data : sistemaFacade.getDataOperacao());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraNivelDoisESemRaiz(String parte, String tipo, Date data) {
        return filtraNivelDoisESemRaiz(parte, tipo, data, true);
    }

    public List<HierarquiaOrganizacional> filtraNivelDoisESemRaiz(String parte, String tipo, Date data, boolean semRaiz) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" inner join UNIDADEORGANIZACIONAL uni on uni.id = h.subordinada_id ");
            sql.append(" WHERE to_date(:data) between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, to_date(:data)) ");
            sql.append(" and h.INDICEDONO = :nivel and uni.entidade_id is not null and ( VW.CODIGO like :str or upper(vw.descricao) like :str)  order by h.codigo");
        } else {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data)");
            sql.append(" and (h.INDICEDONO = :nivel or h.INDICEDONO = :nivelUm) ").append(semRaiz ? "AND h.superior_id  is not null " : "");
            sql.append(" and (replace(VW.CODIGO, '.','') like :str ");
            sql.append("     or VW.CODIGO like :str ");
            sql.append("     or upper(vw.descricao) like :str) ");
            sql.append(" order by h.codigo");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", "2");
        q.setParameter("nivelUm", "1");
        q.setParameter("data", data, TemporalType.DATE);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraNivelDoisEComRaiz(String parte, String tipo, Date data) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" inner join UNIDADEORGANIZACIONAL uni on uni.id = h.subordinada_id ");
            sql.append(" WHERE to_date(:data) between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, to_date(:data)) ");
            sql.append(" and h.INDICEDONO = :nivel and uni.entidade_id is not null and ( VW.CODIGO like :str or upper(vw.descricao) like :str)  order by h.codigo");
        } else {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data)");
            sql.append(" and (h.INDICEDONO = :nivel or h.INDICEDONO = :nivelUm) ");
            sql.append("and (replace(VW.CODIGO, '.','') like :str ");
            sql.append("     or VW.CODIGO like :str ");
            sql.append("     or upper(vw.descricao) like :str) ");
            sql.append(" order by h.codigo");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", "2");
        q.setParameter("nivelUm", "1");
        q.setParameter("data", data, TemporalType.DATE);
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraNivelDoisEComRaizAndUsuario(String parte, Date data) {
        String sql = "select distinct h.* from VWHIERARQUIAADMINISTRATIVA VW " +
            "                             inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID " +
            " where :data between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, :data) " +
            "  and (h.INDICEDONO = :nivel or h.INDICEDONO = :nivelUm) " +
            "  and (replace(VW.CODIGO, '.','') like :str " +
            "     or VW.CODIGO like :str " +
            "     or upper(vw.descricao) like :str) and " +
            "      (substr(h.codigo,1,3) in (select distinct substr(hierarquia.codigo,1,3) " +
            "       from hierarquiaorganizacional hierarquia " +
            "                inner join usuariounidadeorganizacio uu " +
            "                           on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "       where u.id = :usuario " +
            "         and hierarquia.tipohierarquiaorganizacional = :administrativa " +
            "         and hierarquia.INDICEDONO = :nivelUm " +
            "         and :data between trunc(hierarquia.INICIOVIGENCIA) and coalesce(trunc(hierarquia.FIMVIGENCIA), :data)) OR " +
            "      h.SUBORDINADA_ID in (select distinct uu.UNIDADEORGANIZACIONAL_ID " +
            "                                                                      from usuariounidadeorganizacio uu " +
            "                                                                               inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                                                      where u.id = :usuario)) " +
            "order by h.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", "2");
        q.setParameter("nivelUm", "1");
        q.setParameter("data", data, TemporalType.DATE);
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraPorNivelSemView(String parte, String nivel, String tipo, Date data) {

        StringBuilder sql = new StringBuilder();

        if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA.name())) {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE to_date(:data, 'dd/mm/yyyy') between vw.INICIOVIGENCIA  and coalesce(vw.FIMVIGENCIA, to_date(:data, 'dd/mm/yyyy')) ");
            sql.append("and ho.INDICEDONO = :nivel and ( VW.CODIGO like :str or upper(vw.descricao) like :str)  order by h.codigo");
        } else {
            sql.append(" SELECT ho.* FROM HIERARQUIAORGANIZACIONAL ho ");
            sql.append(" INNER JOIN unidadeorganizacional uni ON uni.id = ho.subordinada_id AND ((upper(uni.descricao) LIKE upper(:str) ) or (ho.codigo like upper(:str)))");
            sql.append(" where to_date(:data, 'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data, 'dd/mm/yyyy')) and ho.INDICEDONO = :nivel "
                + " and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' ORDER BY ho.codigo ");

        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("nivel", nivel);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(String tipo, UnidadeOrganizacional o, Date data) {
        if (o == null || o.getId() == null || data == null) return null;
        String sql = " select h.* from hierarquiaorganizacional h " +
        " where trunc(:data) between trunc(h.iniciovigencia)  and coalesce(trunc(h.fimvigencia), trunc(:data))" +
        " and h.subordinada_id = :unidade and h.tipoHierarquiaOrganizacional = :tipo order by h.codigo";
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.dataSemHorario(data));
        q.setParameter("unidade", o.getId());
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        List<HierarquiaOrganizacional> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }

    }

    public HierarquiaOrganizacional getHierarquiaDaUnidadeSemConsiderarVigencia(String tipo, UnidadeOrganizacional o) {
        String sql = " select h.* from hierarquiaorganizacional h " +
        " where h.subordinada_id = :unidade and h.tipoHierarquiaOrganizacional = :tipo order by h.codigo ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("unidade", o.getId());
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        List<HierarquiaOrganizacional> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public HierarquiaOrganizacional buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional tipo, Long idUnidade, Date data) {
        StringBuilder sql = new StringBuilder();

        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipo)) {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" where trunc(:data) between trunc(vw.iniciovigencia)  and coalesce(trunc(vw.fimvigencia), trunc(:data))");
            sql.append(" and vw.subordinada_id = :unidade  order by h.codigo");
        } else {
            sql.append(" SELECT DISTINCT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" where trunc(:data) between trunc(vw.iniciovigencia)  and coalesce(trunc(vw.fimvigencia), trunc(:data))");
            sql.append(" and vw.subordinada_id = :unidade  order by h.codigo");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("data", data != null ? DataUtil.dataSemHorario(data) : DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        q.setParameter("unidade", idUnidade);
        q.setMaxResults(1);
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public HierarquiaOrganizacional validaVigenciaHIerarquiaAdministrativaOrcamentaria(UnidadeOrganizacional undAdm, UnidadeOrganizacional undOrc, Date dataMovimento) throws ExcecaoNegocioGenerica {
        if (undAdm == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada a Unidade Organizacional Administrativa.");
        }
        if (undOrc == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada a Unidade Organizacional Orçamentaria.");
        }
        if (dataMovimento == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada a data do Movimento.");
        }
        String msgErro;
        String stringData = DataUtil.getDataFormatada(dataMovimento);
        StringBuilder sql = new StringBuilder();
        sql.append(" select hoadm.* ")
            .append(" from hierarquiaorganizacional hoadm ")
            .append("   inner join unidadeorganizacional uoadm on uoadm.id  = hoadm.subordinada_id ")
            .append("       and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdm ")
            .append("       and to_date(:dataMvoimento, 'dd/MM/yyyy') between trunc(hoadm.iniciovigencia) and coalesce(trunc(hoadm.fimvigencia),to_date(:dataMvoimento, 'dd/MM/yyyy')) ")
            .append("   inner join hierarquiaorgresp hresp on hresp.hierarquiaorgadm_id = hoadm.id ")
            .append("       and to_date(:dataMvoimento, 'dd/MM/yyyy') between trunc(hresp.datainicio) and coalesce(trunc(hresp.datafim), to_date(:dataMvoimento, 'dd/MM/yyyy')) ")
            .append("   inner join hierarquiaorganizacional horc on hresp.hierarquiaorgorc_id = horc.id ")
            .append(" where hoadm.subordinada_id = :undAdm ")
            .append("   and horc.subordinada_id = :undOrc ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setMaxResults(1);
        try {
            q.setParameter("undAdm", undAdm.getId());
            q.setParameter("undOrc", undOrc.getId());
            q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
            q.setParameter("dataMvoimento", DataUtil.getDataFormatada(dataMovimento));
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException nr) {
            msgErro = "Não foi encontrada nenhuma Hierarquia com a Unidade Administrativa " + undAdm + "; Unidade Orçamentaria " + undOrc + "; Data do Movimento " + stringData + ". Erro:" + nr.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (NonUniqueResultException nu) {
            msgErro = "Foi encontrada mais de uma Hierarquia com a Unidade Administrativa " + undAdm + "; Unidade Orçamentaria " + undOrc + "; Data do Movimento " + stringData + ". Erro:" + nu.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    public HierarquiaOrganizacional recuperaHierarquiaOrganizacional(Entidade entidade) {
        String sql = "SELECT DISTINCT ho.* FROM VWhierarquiaAdministrativa vw "
            + "INNER JOIN HierarquiaOrganizacional ho ON ho.id = vw.id "
            + "INNER JOIN UnidadeOrganizacional uo ON vw.subordinada_id = uo.id "
            + "INNER JOIN Entidade e ON e.id = uo.entidade_id "
            + "WHERE sysdate BETWEEN vw.INICIOVIGENCIA "
            + "AND coalesce(vw.FIMVIGENCIA, sysdate) "
            + "AND e.id = :entidade";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("entidade", entidade.getId());

        List<HierarquiaOrganizacional> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new HierarquiaOrganizacional();
        }
        return lista.get(0);

    }

    public HierarquiaOrganizacional recuperaHierarquiaOrganizacionalPelaUnidade(Long idUnidade) {
        String sql = "SELECT DISTINCT ho.* FROM VWhierarquiaAdministrativa vw "
            + "INNER JOIN HierarquiaOrganizacional ho ON ho.id = vw.id "
            + "WHERE :data BETWEEN vw.INICIOVIGENCIA AND coalesce(vw.FIMVIGENCIA, :data) "
            + "AND ho.subordinada_id = :id";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", SistemaFacade.getDataCorrente(), TemporalType.DATE);
        q.setParameter("id", idUnidade);

        List<HierarquiaOrganizacional> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new HierarquiaOrganizacional();
        }
        HierarquiaOrganizacional ho = lista.get(0);
        Hibernate.initialize(ho.getHierarquiaOrganizacionalResponsavels());
        return ho;
    }

    public HierarquiaOrganizacional recuperaHierarquiaOrganizacionalPelaUnidadeUltima(Long idUnidade) {
        String sql = " select distinct ho.* " +
            " from hierarquiaorganizacional ho " +
            " where ho.subordinada_id = :id " +
            " and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            " order by ho.iniciovigencia desc, ho.fimvigencia desc ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("id", idUnidade);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(1);

        List<HierarquiaOrganizacional> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new HierarquiaOrganizacional();
        }
        return lista.get(0);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(Date data, UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo) {
        if (unidade == null) return null;
        return getHierarquiaOrganizacionalPorUnidade(data, unidade.getId(), tipo);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(Date data, Long idUnidade, TipoHierarquiaOrganizacional tipo) {
        if (idUnidade == null) {
            return null;
        }
        String hql = "from  HierarquiaOrganizacional h where "
            + "h.subordinada.id=:unidadeParametro "
            + "and :data between h.inicioVigencia and coalesce(h.fimVigencia, :data) "
            + "and (h.tipoHierarquiaOrganizacional=:tipoHO "
            + "or h.tipoHierarquiaOrganizacional is null) order by h.codigo";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("unidadeParametro", idUnidade);
        q.setParameter("tipoHO", tipo);
        q.setParameter("data", data, TemporalType.DATE);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getSingleResult();
        }
        return null;
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalPorIdDeUnidades(Date data, UsuarioSistema usuarioSistema, String parte, TipoHierarquiaOrganizacional tipo) {
        String hql = " select distinct hOrc.* from HIERARQUIAORGRESP hieresp " +
            " inner join HierarquiaOrganizacional h on hieresp.hierarquiaorgadm_id = h.id " +
            " INNER JOIN unidadeorganizacional und ON h.subordinada_id = und.id " +
            " inner join HierarquiaOrganizacional hOrc on hieresp.hierarquiaorgorc_id = hOrc.id " +
            " INNER JOIN unidadeorganizacional undOrc ON hOrc.subordinada_id = undOrc.id " +
            " WHERE h.subordinada_id IN (select usuUni.unidadeOrganizacional_id from usuariounidadeorganizacio usuUni where usuUni.usuarioSistema_id = :usuario) " +
            " AND to_date(:data, 'dd/mm/yyyy') BETWEEN trunc(h.inicioVigencia) AND COALESCE(trunc(h.fimVigencia), to_date(:data, 'dd/mm/yyyy')) " +
            " AND to_date(:data, 'dd/mm/yyyy') BETWEEN trunc(hOrc.inicioVigencia) AND COALESCE(trunc(hOrc.fimVigencia), to_date(:data, 'dd/mm/yyyy')) " +
            " AND (hOrc.tipohierarquiaorganizacional LIKE '" + tipo.name() + "'" +
            " OR hOrc.tipoHierarquiaOrganizacional IS NULL) " +
            " AND (hOrc.codigo LIKE :parametro " +
            " OR upper(hOrc.descricao) LIKE :parametro) " +
            " ORDER BY hOrc.codigo ";

        Query q = getEntityManager().createNativeQuery(hql, HierarquiaOrganizacional.class);
        q.setParameter("parametro", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("usuario", usuarioSistema.getId());
        if (!q.getResultList().isEmpty()) {
            return (List<HierarquiaOrganizacional>) q.getResultList();
        }
        return null;
    }

    @Deprecated
    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo) {
        if (unidade == null) {
            return null;
        }
        String hql = "from  HierarquiaOrganizacional h where "
            + "h.subordinada.id=:unidadeParametro "
            + "and (h.tipoHierarquiaOrganizacional=:tipoHO "
            + "or h.tipoHierarquiaOrganizacional is null) order by h.codigo";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("unidadeParametro", unidade.getId());
        q.setParameter("tipoHO", tipo);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getSingleResult();
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalVigentePorUnidade(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo, Date data) {
        if (unidade == null) {
            return null;
        }
        String hql = " select h.* from  HierarquiaOrganizacional h " +
            "  where h.subordinada_id = :unidadeParametro "
            + " and (h.tipoHierarquiaOrganizacional = :tipoHO "
            + "   or h.tipoHierarquiaOrganizacional is null) "
            + " and to_date(:data, 'dd/MM/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/MM/yyyy')) "
            + " order by h.codigo desc ";


        Query q = getEntityManager().createNativeQuery(hql, HierarquiaOrganizacional.class);
        q.setParameter("unidadeParametro", unidade.getId());
        q.setParameter("tipoHO", tipo.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getSingleResult();
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativaDaOrcamentaria(UnidadeOrganizacional unidade, Date data) {
        if (unidade == null) {
            return null;
        }
        String sql = " select distinct hadm.* from vwhierarquiaadministrativa vw " +
            " inner join hierarquiaorganizacional hadm on hadm.id=vw.id " +
            " inner join hierarquiaorgcorre resp on hadm.id = resp.hierarquiaorgadm_id " +
            " inner join vwhierarquiaorcamentaria vworc on resp.hierarquiaorgorc_id = vworc.id " +
            " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " and to_date(:data, 'dd/MM/yyyy') between trunc(hadm.iniciovigencia) and coalesce(trunc(hadm.fimvigencia), to_date(:data, 'dd/MM/yyyy')) " +
            " and vworc.subordinada_id = :idUnidade ";

        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(1);
        List<HierarquiaOrganizacional> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public void persistePrevisoesHOContaDestinacao(UnidadeOrganizacional uni) {
        uni = em.merge(uni);
    }

    public List<PrevisaoHOContaDestinacao> recuperarPrevisaoHOContaDestinacao(UnidadeOrganizacional id, Exercicio exercicio) throws Exception {
        try {
            Query q = getEntityManager().createNativeQuery("select pre.* from unidadeorganizacional uo " +
                "inner join previsaohocontadestinacao pre on uo.id = pre.unidadeorganizacional_id " +
                "where pre.exercicio_id = :exercicio " +
                "and uo.id = :paramId", PrevisaoHOContaDestinacao.class);
            q.setParameter("paramId", id);
            q.setParameter("exercicio", exercicio.getId());

            return q.getResultList();
        } catch (SQLGrammarException e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro : " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro : " + ex.getMessage());
        } catch (Exception ep) {
            ep.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro:  " + ep.getMessage());
        }
    }

    public List<PrevistoRealizadoDespesaUnidade> buscarCriarPrevistoRealizadoPorUnidadeExercicio(UnidadeOrganizacional unidade, Exercicio exercicio, ContaDeDestinacao contaDeDestinacao) {
        try {

            String sql = "select  " +
                " to_number(pre.exercicio_id) as exercicio,  " +
                " to_number(c.id) as conta, " +
                " pre.valor as valor_previsto, " +
                " (select sum(pf.valor) from ProvisaoPPAFonte pf" +
                " inner join ProvisaoPPAdespesa p on pf.provisaoppadespesa_id = p.id  " +
                " inner join subacaoppa sub on p.subacaoppa_id = sub.id " +
                " where sub.exercicio_id = :exercicio " +
                " and p.unidadeorganizacional_id = :unidade" +
                " and pf.destinacaoDeRecursos_id = c.id " +
                " ) as despesa " +
                " from unidadeorganizacional uo " +
                " inner join previsaohocontadestinacao pre on uo.id = pre.unidadeorganizacional_id  " +
                " inner join conta c on pre.conta_id = c.id " +
                " where pre.exercicio_id = :exercicio " +
                " and uo.id = :unidade ";
            if (contaDeDestinacao != null) {
                sql += " and c.id = :contaDestinacao";
            }
            Query q = getEntityManager().createNativeQuery(sql);
            q.setParameter("unidade", unidade.getId());
            q.setParameter("exercicio", exercicio.getId());
            if (contaDeDestinacao != null) {
                q.setParameter("contaDestinacao", contaDeDestinacao.getId());
            }
            List resultList = q.getResultList();
            List<PrevistoRealizadoDespesaUnidade> retorno = Lists.newArrayList();
            for (Object o : resultList) {
                Object[] objeto = (Object[]) o;
                Exercicio ex = em.find(Exercicio.class, ((BigDecimal) objeto[0]).longValue());
                Conta c = em.find(Conta.class, ((BigDecimal) objeto[1]).longValue());
                BigDecimal previsto = (BigDecimal) objeto[2];
                BigDecimal realizado = (BigDecimal) objeto[3];
                PrevistoRealizadoDespesaUnidade previstoRealizadoDespesaUnidade = new PrevistoRealizadoDespesaUnidade(ex, c, previsto, realizado);
                retorno.add(previstoRealizadoDespesaUnidade);
            }
            return retorno;
        } catch (Exception ep) {
            ep.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro:  " + ep.getMessage());
        }
    }

    public List<PrevisaoHOContaDestinacao> recuperarPrevisaoHOContaDestinacaoPorExercicio(UnidadeOrganizacional id, Exercicio exercicio) throws Exception {
        try {
            Query q = getEntityManager().createNativeQuery("select pr.* from UnidadeOrganizacional u " +
                "inner join previsaoHOContaDestinacao pr on u.id = pr.unidadeorganizacional_id " +
                "inner join exercicio e on pr.exercicio_id = e.id " +
                "where u.id = :paramId " +
                "and e.id = :idExercicio", PrevisaoHOContaDestinacao.class);
            q.setParameter("paramId", id);
            q.setParameter("idExercicio", exercicio.getId());
            return q.getResultList();
        } catch (Exception ep) {
            ep.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro:  " + ep.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> consultaSuperioresPorVigencia(HierarquiaOrganizacional ho, Date dataMovimento) throws ExcecaoNegocioGenerica {

        try {
            String sql = " SELECT * FROM (SELECT h.* FROM hierarquiaorganizacional h "
                + " WHERE to_date(:data, 'dd/mm/yyyy') BETWEEN trunc(h.iniciovigencia) AND coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/mm/yyyy')) "
                + " AND h.tipohierarquiaorganizacional = 'ORCAMENTARIA' "
                + " START WITH h.id =:hierarquia "
                + " CONNECT BY H.SUBORDINADA_ID = PRIOR H.SUPERIOR_ID)X "
                + " ORDER BY x.codigo DESC";
            Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);

            q.setParameter("hierarquia", ho.getId());
            q.setParameter("data", DataUtil.getDataFormatada(dataMovimento));


            return q.getResultList();
        } catch (SQLGrammarException g) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar superiores! " + g.getMessage());
        } catch (IllegalArgumentException i) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar superiores! " + i.getMessage());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar superiores! " + e.getMessage());
        }
    }

    private BigDecimal acumulaPrevisaHO(List<PrevisaoHOContaDestinacao> lista) {
        BigDecimal acumulador = BigDecimal.ZERO;
        for (PrevisaoHOContaDestinacao previsaoHOContaDestinacao : lista) {
            acumulador = acumulador.add(previsaoHOContaDestinacao.getValor());
        }
        return acumulador;
    }

    public void calculaSomatoriaValorEstimado(HierarquiaOrganizacional horg, Date data, List<PrevisaoHOContaDestinacao> lista) throws ExcecaoNegocioGenerica {
        List<HierarquiaOrganizacional> listaHo = consultaSuperioresPorVigencia(horg, data);
        BigDecimal acumulador = acumulaPrevisaHO(lista);
        for (int x = 0; x < listaHo.size(); x++) {
            HierarquiaOrganizacional var = listaHo.get(x);
            if (x != 0) {
                //System.out.println("Valor somado" + var.getValorEstimado().add(acumulador) + "   em " + var);
                var.setValorEstimado(var.getValorEstimado().add(acumulador));
                getEntityManager().merge(var);

            }
        }
        //System.out.println("Saida");
    }

    public void calculaSubtracaoValorEstimado(HierarquiaOrganizacional horg, Date data, List<PrevisaoHOContaDestinacao> lista) throws ExcecaoNegocioGenerica {
        List<HierarquiaOrganizacional> listaHo = consultaSuperioresPorVigencia(horg, data);
        BigDecimal acumulador = acumulaPrevisaHO(lista);
        for (int x = 0; x < listaHo.size(); x++) {
            HierarquiaOrganizacional var = listaHo.get(x);
            if (x != 0) {
                //System.out.println("Valor subtraido " + var.getValorEstimado().add(acumulador) + "   em " + var);
                var.setValorEstimado(var.getValorEstimado().subtract(acumulador));
                getEntityManager().merge(var);

            }
        }
    }

    public List<PrevisaoHOContaDestinacao> listaPrevisoesPorUnidade(UnidadeOrganizacional uni, Exercicio e) {
        String sql = "SELECT PREV.*"
            + " FROM PREVISAOHOCONTADESTINACAO PREV"
            + " INNER JOIN CONTA ON PREV.CONTA_ID = CONTA.ID"
            + " WHERE PREV.UNIDADEORGANIZACIONAL_ID = :unidade"
            + "   AND PREV.EXERCICIO_ID = :ex";
        Query q = em.createNativeQuery(sql, PrevisaoHOContaDestinacao.class);
        q.setParameter("unidade", uni.getId());
        q.setParameter("ex", e.getId());
        return q.getResultList();
    }

    public Boolean validaSeAFonteExiste(UnidadeOrganizacional uni, Conta c, Exercicio e) {
        String sql = "SELECT PREV.*"
            + " FROM PREVISAOHOCONTADESTINACAO PREV"
            + " INNER JOIN CONTA ON PREV.CONTA_ID = CONTA.ID"
            + " WHERE PREV.UNIDADEORGANIZACIONAL_ID = :unidade"
            + "   AND CONTA.ID = :conta"
            + "   AND PREV.EXERCICIO_ID = :ex";
        Query q = em.createNativeQuery(sql, PrevisaoHOContaDestinacao.class);
        q.setParameter("unidade", uni.getId());
        q.setParameter("conta", c.getId());
        q.setParameter("ex", e.getId());
        //new Util().imprimeSQL(sql, q);
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<HierarquiaOrganizacional> listaIntervaloCodigoUnidadeOrgaoPorTipo(String codigoUnidadeInicio, String codigoUnidadeFim, String codigoOrgaoInicio, String codigoOrgaoFim, String nivel, Exercicio exerc, String tipoHierarquia) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select * from hierarquiaorganizacional ho  ");
        sql.append(" where ho.indicedono = :nivel ");
        if (!codigoUnidadeInicio.equals("") && !codigoUnidadeFim.equals("")) {
            sql.append(" and SUBSTR(HO.CODIGO, 8, 3) between \'").append(codigoUnidadeInicio).append("\' and \'").append(codigoUnidadeFim).append("\' ");
        }
        if (!codigoOrgaoInicio.equals("") && !codigoOrgaoFim.equals("")) {
            sql.append(" and SUBSTR(HO.CODIGO, 4, 3) between \'").append(codigoOrgaoInicio).append("\' and \'").append(codigoOrgaoFim).append("\' ");
        }
        sql.append(" and ho.exercicio_id = :exerc ");
        sql.append(" and HO.TIPOHIERARQUIAORGANIZACIONAL = :tipoHierarquia ORDER BY ho.codigo ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("exerc", exerc.getId());
        q.setParameter("nivel", nivel);
        q.setParameter("tipoHierarquia", tipoHierarquia);

        return q.getResultList();
    }

    public void validaRemocaoPrevisaoHOContaDestinacao(PrevisaoHOContaDestinacao prev) throws ExcecaoNegocioGenerica {
        try {
            Query q = getEntityManager().createQuery("from ProvisaoPPAFonte ppf inner join ppf.provisaoPPADespesa as ppd "
                + " inner join ppd.unidadeOrganizacional as und  "
                + " inner join ppf.destinacaoDeRecursos as conta  "
                + " where und=:unidade and conta=:conta");
            q.setParameter("conta", prev.getConta());
            q.setParameter("unidade", prev.getUnidadeOrganizacional());

            if (!q.getResultList().isEmpty()) {
                throw new ExcecaoNegocioGenerica(" A previsão já está vinculada a uma conta de destinação de recursos.");
            }

        } catch (SQLGrammarException sql) {
            sql.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro de sql " + sql.getMessage() + "  SQL: " + sql.getSQL());
        } catch (IllegalArgumentException il) {
            il.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro argumetno invalido " + il.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> listaSimplesFiltrandoPorDataTipo(Date dataVigente, TipoHierarquiaOrganizacional tipo) throws ExcecaoNegocioGenerica {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT H.* FROM HIERARQUIAORGANIZACIONAL H ");
            sql.append(" WHERE H.TIPOHIERARQUIAORGANIZACIONAL = :tipo ");
            sql.append(" AND to_date(:dataVigente,'dd/MM/yyyy') ");
            sql.append(" BETWEEN trunc(H.INICIOVIGENCIA) ");
            sql.append(" and coalesce(trunc(H.FIMVIGENCIA), to_date(:dataVigente,'dd/MM/yyyy')) ");
            sql.append(" order by codigo ");
            Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
            q.setParameter("dataVigente", DataUtil.getDataFormatada(dataVigente));
            q.setParameter("tipo", tipo.name());
            if (q.getResultList().isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não existe nenhuma Hierarquia Vigente na Data " + Util.dateToString(dataVigente));
            }

            return q.getResultList();

        } catch (SQLGrammarException sql) {
            throw new ExcecaoNegocioGenerica("Erro de sql " + sql.getMessage() + "  SQL: " + sql.getSQL());
        } catch (IllegalArgumentException il) {
            throw new ExcecaoNegocioGenerica("Erro argumetno invalido " + il.getMessage());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }

    public List<HierarquiaOrganizacional> listaFiltrandoPorOrgaoAndTipoAdministrativa(String parte) {
        String sql = "SELECT ho.* FROM hierarquiaorganizacional ho "
            + "INNER JOIN VWHIERARQUIAADMINISTRATIVA vw ON vw.id = ho.id "
            + " INNER JOIN unidadeorganizacional uni on uni.id = ho.subordinada_id "
            + "WHERE ho.tipohierarquiaorganizacional = :tipo "
            + "AND to_date(:data, 'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data, 'dd/mm/yyyy')) "
            + "AND ho.indicedono = 2 " +
            "  AND (replace(ho.codigo, '.', '') LIKE :parte or ho.codigo LIKE :parte or lower(uni.descricao) LIKE :parte or lower(ho.descricao) LIKE :parte) ORDER BY ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);

        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (hierarquias.isEmpty()) {
            return new ArrayList<>();
        } else {
            return hierarquias;
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaPorOrgaoAndTipoAdministrativaAndUsuario(String parte) {
        String sql = "select ho.* " +
            " from hierarquiaorganizacional ho " +
            "         inner join usuariounidadeorganizacio usu on ho.subordinada_id = usu.unidadeorganizacional_id " +
            " where ho.tipohierarquiaorganizacional = :tipo " +
            "  and to_date(:data, 'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data, 'dd/mm/yyyy')) " +
            "  and ho.indicedono = 2 " +
            "  and (replace(ho.codigo, '.', '') like :parte or ho.codigo like :parte or lower(ho.descricao) like :parte or lower(ho.descricao) like :parte) " +
            "  and usu.usuariosistema_id = :idUsuario " +
            " order by ho.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (!Util.isListNullOrEmpty(hierarquias)) {
            return hierarquias;
        }
        return new ArrayList<>();
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativaPorOrgaoAndTipoAdministrativa(String codigoOrgao) {
        String sql = "SELECT ho.* FROM hierarquiaorganizacional ho "
            + "INNER JOIN VWHIERARQUIAADMINISTRATIVA vw ON vw.id = ho.id "
            + " INNER JOIN unidadeorganizacional uni on uni.id = ho.subordinada_id "
            + "WHERE ho.tipohierarquiaorganizacional = :tipo "
            + "AND sysdate between ho.inicioVigencia and coalesce(ho.fimVigencia,sysdate) "
            + " and ho.INDICEDONO = 2 AND (ho.codigo LIKE :parte)";

        String hql = "SELECT ho FROM HierarquiaOrganizacional ho, VwHierarquiaAdministrativa  vw "
            + "  "
            + "WHERE ho.id= vw.id and :data between ho.inicioVigencia and coalesce(ho.fimVigencia,:data) "
            + " and ho.indiceDoNo = 2 AND SUBSTR(ho.codigoNo,1,3) = :codigo";

        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigoOrgao);
        q.setParameter("data", sistemaFacade.getDataOperacao());
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (hierarquias.isEmpty()) {
            return null;
        }
        return hierarquias.get(0);
    }

    public List<HierarquiaOrganizacional> listaFiltrandoPorOrgaoAndTipoOrcamentaria(String parte, Date data) {
        String sql = "SELECT ho.* FROM hierarquiaorganizacional ho "
            + "INNER JOIN VWHIERARQUIAORCAMENTARIA vw ON vw.id = ho.id "
            + " INNER JOIN unidadeorganizacional uni on uni.id = ho.subordinada_id "
            + "WHERE ho.tipohierarquiaorganizacional = :tipo "
            + "AND to_date(:data, 'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data, 'dd/mm/yyyy')) "
            + "AND ho.indicedono = 2 AND (ho.codigo LIKE :parte or lower(uni.descricao) LIKE :parte)" +
            " order by ho.codigo";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);

        q.setParameter("tipo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (hierarquias.isEmpty()) {
            return new ArrayList<>();
        } else {
            return hierarquias;
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrcamentariasCriadosNoPeriodoPorIndice(Integer indice, Date dataInicial, Date dataFinal) {
        String sql = "SELECT ho.* FROM hierarquiaorganizacional ho "
            + " WHERE ho.tipohierarquiaorganizacional = :tipo "
            + " AND trunc(ho.inicioVigencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') "
            + " AND ho.indicedono = :indice "
            + " order by ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);

        q.setParameter("tipo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("indice", indice);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (hierarquias.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return hierarquias;
        }
    }


    public List<HierarquiaOrganizacional> getListaHierarquiasAdministrativasVigentes(Date data) {
        Query q = em.createQuery(" select ho from VwHierarquiaAdministrativa vw, HierarquiaOrganizacional ho" +
            "  "
            + " where ho.id = vw.id and " +
            "  to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vw.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "   and to_date(to_char(coalesce(vw.fimVigencia,:data),'mm/yyyy'),'mm/yyyy') ");

        q.setParameter("data", data);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getListaHierarquiasAdministrativasTodas() {
        Query q = em.createQuery(" select ho from HierarquiaOrganizacional ho" +
            " where ho.tipoHierarquiaOrganizacional = :tipo ");

        q.setParameter("tipo" +
            "", TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        return q.getResultList();
    }

    public Long getCountDe(String toSelect, String from, String where, Map<String, Object> parametros) {
        toSelect = toSelect.replace("select ", "");
        String hql = "select count(" + toSelect + ")";
        hql += from;
        hql += where;

        Query q = em.createQuery(hql);
        for (String param : parametros.keySet()) {
            q.setParameter(param, parametros.get(param));
        }

        return (Long) q.getSingleResult();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasFiltrandoVigentesPorPeriodoAndTipo(String parte, Date inicio, Date fim, TipoHierarquiaOrganizacional tipo) {
        String sql = "";

        sql = TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(tipo) ? "select ho.* from vwhierarquiaadministrativa vw " : "select ho.* from vwhierarquiaorcamentaria vw ";

        sql += " inner join hierarquiaorganizacional ho on ho.id = vw.id" +
            "      where (vw.iniciovigencia >= :inicio and vw.iniciovigencia <= :fim" +
            "         or (vw.fimVigencia    >= :inicio and vw.fimVigencia    <= :fim)" +
            "         or (vw.inicioVigencia is null    and vw.fimvigencia    >= :inicio)" +
            "         or (vw.fimvigencia    is null    and vw.iniciovigencia >= :inicio and vw.iniciovigencia <= :fim)" +
            "         or (vw.inicioVigencia <= :inicio and (vw.fimvigencia   >= :inicio and vw.fimvigencia    <= :fim))" +
            "         or (vw.inicioVigencia <= :inicio and vw.fimvigencia    >= :fim)" +
            "         or (vw.inicioVigencia <= :inicio and vw.fimVigencia    is null)) " +
            "        and (vw.codigo like :param or lower(vw.descricao) like :param) " +
            "        and ho.indicedono > 1 " +
            "   order by vw.codigo asc";

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("inicio", inicio, TemporalType.DATE);
        q.setParameter("fim", fim, TemporalType.DATE);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasVigentesNoPeriodoNoNivel(Date inicio, Date fim, TipoHierarquiaOrganizacional tipo, Integer... niveis) {
        String sql = "";
        String sNiveis = niveis.length > 0 ? " and (" : "";

        for (Integer i : niveis) {
            sNiveis += " ho.indicedono = :nivel" + i + " or";
        }
        sNiveis = niveis.length > 0 ? sNiveis.substring(0, sNiveis.length() - 2) : "";
        sNiveis = niveis.length > 0 ? sNiveis + ")" : "";

        sql = "select ho.* from hierarquiaorganizacional ho";

        sql += "      where tipohierarquiaorganizacional = 'ADMINISTRATIVA'" +
            "        and (ho.iniciovigencia >= :inicio and ho.iniciovigencia <= :fim" +
            "         or (ho.fimVigencia    >= :inicio and ho.fimVigencia    <= :fim)" +
            "         or (ho.inicioVigencia is null    and ho.fimvigencia    >= :inicio)" +
            "         or (ho.fimvigencia    is null    and ho.iniciovigencia >= :inicio and ho.iniciovigencia <= :fim)" +
            "         or (ho.inicioVigencia <= :inicio and (ho.fimvigencia   >= :inicio and ho.fimvigencia    <= :fim))" +
            "         or (ho.inicioVigencia <= :inicio and ho.fimvigencia    >= :fim)" +
            "         or (ho.inicioVigencia <= :inicio and ho.fimVigencia    is null)) " + sNiveis + " order by ho.codigo asc";

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("inicio", inicio, TemporalType.DATE);
        q.setParameter("fim", fim, TemporalType.DATE);
        for (Integer i : niveis) {
            q.setParameter("nivel" + i, i);
        }

        return q.getResultList();

    }

    public boolean existeHierarquiaOrganizacionalVigenteComCodigo(HierarquiaOrganizacional ho, Date dataOperacao) {
        String hql = "select ho from HierarquiaOrganizacional ho" +
            "      where ho.codigo  = :codigo" +
            "        and ho.tipoHierarquiaOrganizacional = :tipo" +
            "        and :dataOperacao between ho.inicioVigencia  and coalesce(ho.fimVigencia, :dataOperacao)";
        Query q = em.createQuery(hql);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("tipo", ho.getTipoHierarquiaOrganizacional());
        q.setParameter("codigo", ho.getCodigo());
        q.setMaxResults(1);
        try {
            HierarquiaOrganizacional resultado = (HierarquiaOrganizacional) q.getSingleResult();
            if (resultado.equals(ho)) {
                return false;
            } else {
                return true;
            }
        } catch (NoResultException nre) {
            return false;
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrgaoVigentesSiprev() {
        Query createQuery = em.createNativeQuery("select distinct ho.* from HierarquiaOrganizacional ho inner join UnidadeOrganizacional un on un.id = ho.subordinada_id inner join Entidade e on e.id = un.entidade_id where " +
            " ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' and :data between ho.inicioVigencia " +
            " and coalesce(ho.fimVigencia, :data)", HierarquiaOrganizacional.class);
        createQuery.setParameter("data", UtilRH.getDataOperacao(), TemporalType.DATE);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return createQuery.getResultList();
    }

    public ResponsavelUnidade recuperarResponsavelPorUnidadeOrganizacional(UnidadeOrganizacional unidade) {
        String hql = "select responsavel from ResponsavelUnidade responsavel  where responsavel.unidadeOrganizacional = :unidade";
        Query q = em.createQuery(hql);
        q.setParameter("unidade", unidade);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ResponsavelUnidade) q.getResultList();
        }
        return null;
    }

    public List<HierarquiaOrganizacional> completarUnidadesOndeUsuarioEhGestorMateriais(UsuarioSistema usuario, Date dataOperacao, String parte) {
        String sql = " SELECT h.*" +
            "       FROM hierarquiaorganizacional h" +
            " INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.id = h.id " +
            " INNER JOIN unidadeorganizacional uo on uo.id = h.subordinada_id " +
            " INNER JOIN USUARIOUNIDADEORGANIZACIO UUO ON UUO.UNIDADEORGANIZACIONAL_ID = UO.ID" +
            " WHERE :data_operacao BETWEEN VW.INICIOVIGENCIA and COALESCE(VW.FIMVIGENCIA, :data_operacao)" +
            "   AND UUO.USUARIOSISTEMA_ID = :usuario_id" +
            "   AND UUO.GESTORMATERIAIS = 1" +
            "   AND (VW.CODIGO LIKE :parte" +
            "        OR LOWER(VW.DESCRICAO) LIKE :parte)" +
            " order by vw.codigo ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data_operacao", dataOperacao, TemporalType.DATE);
        q.setParameter("usuario_id", usuario.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public List<UnidadeOrganizacional> buscarUnidadesOndeUsuarioEhGestorPatrimonio(UsuarioSistema usuario, Date dataOperacao, UnidadeOrganizacional uo) {
        String sql = "SELECT UO.*" +
            "       FROM UNIDADEORGANIZACIONAL UO" +
            " INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = UO.ID" +
            " INNER JOIN USUARIOUNIDADEORGANIZACIO UUO ON UUO.UNIDADEORGANIZACIONAL_ID = UO.ID" +
            " WHERE :data_operacao BETWEEN VW.INICIOVIGENCIA and COALESCE(VW.FIMVIGENCIA, :data_operacao)" +
            "   AND UUO.USUARIOSISTEMA_ID = :usuario_id" +
            "   AND UUO.GESTORPATRIMONIO = 1" +
            "   AND UO.ID = :unidade " +
            " order by vw.codigo ";

        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("data_operacao", dataOperacao, TemporalType.DATE);
        q.setParameter("usuario_id", usuario.getId());
        q.setParameter("unidade", uo.getId());

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa, Date dataReferencia) {
        String sql = "    SELECT HIERARQUIAORC.* " +
            "      FROM HIERARQUIAORGRESP " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC     ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
            "INNER JOIN UNIDADEORGANIZACIONAL ORCAMENTARIA ON ORCAMENTARIA.ID =  VWORC.SUBORDINADA_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAORC ON HIERARQUIAORC.ID = VWORC.ID" +
            "     WHERE to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN trunc(VWADM.INICIOVIGENCIA) AND COALESCE(trunc(VWADM.FIMVIGENCIA),to_date(:dataAtual, 'dd/MM/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN trunc(VWORC.INICIOVIGENCIA) AND COALESCE(trunc(VWORC.FIMVIGENCIA),to_date(:dataAtual, 'dd/MM/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN trunc(HIERARQUIAORGRESP.DATAINICIO) AND COALESCE(trunc(HIERARQUIAORGRESP.DATAFIM),to_date(:dataAtual, 'dd/MM/yyyy'))" +
            "       AND VWADM.SUBORDINADA_ID = :unidadeadministrativa" +
            "  ORDER BY HIERARQUIAORC.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("unidadeadministrativa", unidadeAdministrativa.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> retornarHierarquiAdministrativaPresenteNoUsuario( UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrc, Date dataReferencia) {
        String sql = " SELECT DISTINCT HIERARQUIAADM.* " +
                " FROM HIERARQUIAORGRESP " +
                " INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
                " INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAADM ON HIERARQUIAADM.ID = VWADM.ID " +
                " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
                " INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON VWADM.SUBORDINADA_ID = UU.UNIDADEORGANIZACIONAL_ID " +
                "     WHERE to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA, to_date(:dataAtual, 'dd/MM/yyyy')) " +
                "       AND to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN HIERARQUIAADM.INICIOVIGENCIA AND COALESCE(HIERARQUIAADM.FIMVIGENCIA, to_date(:dataAtual, 'dd/MM/yyyy')) " +
                "       AND to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, to_date(:dataAtual, 'dd/MM/yyyy')) " +
                "       AND to_date(:dataAtual, 'dd/MM/yyyy') BETWEEN HIERARQUIAORGRESP.DATAINICIO AND COALESCE(HIERARQUIAORGRESP.DATAFIM, to_date(:dataAtual, 'dd/MM/yyyy')) " +
                "       AND VWORC.SUBORDINADA_ID = :unidadeOrc " +
                " AND UU.USUARIOSISTEMA_ID = :usuarioSistema " +
                "  ORDER BY HIERARQUIAADM.CODIGO ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("unidadeOrc", unidadeOrc.getId());
        q.setParameter("usuarioSistema", usuarioSistema.getId());

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdministrativaPorUnidadeOrcamentaria(UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrcamentaria, Date dataReferencia) {
        String sql = "        SELECT DISTINCT HIERARQUIAADM.*  " +
            "      FROM HIERARQUIAORGCORRE  " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGCORRE.HIERARQUIAORGADM_ID  " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC     ON VWORC.ID = HIERARQUIAORGCORRE.HIERARQUIAORGORC_ID  " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAADM ON HIERARQUIAADM.ID = VWADM.ID " +
            "INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON VWADM.SUBORDINADA_ID = UU.UNIDADEORGANIZACIONAL_ID " +
            "     WHERE :dataAtual BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA,:dataAtual)  " +
            "       AND :dataAtual BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA,:dataAtual)  " +
            "       AND :dataAtual BETWEEN HIERARQUIAORGCORRE.DATAINICIO AND COALESCE(HIERARQUIAORGCORRE.DATAFIM,:dataAtual) " +
            "       AND VWORC.SUBORDINADA_ID = :unidadeorcamentaria " +
            "       AND UU.USUARIOSISTEMA_ID = :idUsuario " +
            "  ORDER BY HIERARQUIAADM.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("unidadeorcamentaria", unidadeOrcamentaria.getId());
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("dataAtual", dataReferencia, TemporalType.DATE);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> retornaTodasHierarquiasOrcamentarias() {
        String sql = "SELECT * FROM HIERARQUIAORGANIZACIONAL "
            + " WHERE TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA'";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        return q.getResultList();
    }

    public void atualizaViewHierarquiaOrcamentaria() {
        Connection conn = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            String refresh = "{call DBMS_snapshot.refresh('VWHIERARQUIAORCAMENTARIA')}";
            CallableStatement call = conn.prepareCall(refresh);
            call.execute();
        } catch (SQLException e) {
            logger.debug("Erro ao atualizar VWHIERARQUIAORCAMENTARIA ", e);
            logger.error("Não foi possível atualizar a VWHIERARQUIAORCAMENTARIA, verifique o log em DEBBUG");
        } catch (Exception ex) {
            logger.debug("Erro ao atualizar VWHIERARQUIAORCAMENTARIA ", ex);
            logger.error("Não foi possível atualizar a VWHIERARQUIAORCAMENTARIA, verifique o log em DEBBUG");
            throw new ExcecaoNegocioGenerica("Erro ao atualizar a View, " + ex.getMessage());
        } finally {
            AbstractReport.fecharConnection(conn);
        }
    }

    public List<HierarquiaOrganizacional> getHIerarquiaFilhosDeX(String parte, HierarquiaOrganizacional ho, Date vigencia) {
        String hql = " select ho from VwHierarquiaAdministrativa vw, HierarquiaOrganizacional  ho "
            + " where ho.id = vw.id"
            + " and ho.id <> :superior and ho.codigo like :codigo " +
            " and :vigencia between ho.inicioVigencia  and coalesce(ho.fimVigencia,:vigencia) " +
            " AND (ho.codigo LIKE :parametro " +
            " or upper(vw.descricao) LIKE :parametro) " +
            " order by ho.codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("superior", ho.getId());
        q.setParameter("codigo", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", vigencia);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public HierarquiaOrganizacional retornarHierarquiaOrcamentariaPelaUnidadeOrcamentaria(UnidadeOrganizacional orcamentaria, Date data) {
        String sql = "select ho.* from hierarquiaorganizacional ho " +
            "where :dataAtual between ho.iniciovigencia and coalesce(ho.fimvigencia,:dataAtual) " +
            "and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "and ho.subordinada_id = :orcamentaria ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("dataAtual", data);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("orcamentaria", orcamentaria.getId());
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException no) {
            return null;
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaPaiAndFilhoUsuarioPorTipoGestor(TipoGestor tipoGestor, String parteDigitada, String nivelEstrutura, UsuarioSistema usuarioSistema, Date dataDaOperacao) {
        String sql = "with dados(id, superior_id, subordinada_id, codigo, descricao ) as (" +
            "  select vw.id, vw.superior_id, vw.subordinada_id, vw.codigo, vw.descricao " +
            "  from vwhierarquiaadministrativa vw" +
            "  inner join hierarquiaorganizacional ho on ho.id = vw.id" +
            "  where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia   " +
            "  and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "  AND nivelestrutura(ho.codigo, '.') = 2 " +
            "  and substr(vw.codigo,1,6) in (select substr(ho.codigo,1,6)" +
            "                                  from usuariosistema us" +
            "                                 inner join usuariounidadeorganizacio uu on uu.usuariosistema_id = us.id" +
            "                                 inner join hierarquiaorganizacional ho on ho.subordinada_id = uu.unidadeorganizacional_id" +
            "                                 where us.id = :usuarioId " +
            "                                   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "                                   and uu." +TipoGestor.getNomeColunaGestor(tipoGestor) + " = :gestor)" +
            "  UNION ALL " +
            "  select vw.id, vw.superior_id, vw.subordinada_id, vw.codigo, vw.descricao " +
            "  from vwhierarquiaadministrativa vw " +
            "  inner join dados dd on dd.subordinada_id = vw.superior_id" +
            "  where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia   " +
            "  and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            ")" +
            " select distinct ho.* " +
            "   from dados dd" +
            " inner join hierarquiaorganizacional ho on ho.id = dd.id" +
            " where ((replace(dd.codigo,'.','') LIKE :filtro) or dd.codigo LIKE :filtro OR (UPPER(dd.descricao) LIKE :filtro))" +
            "    and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            (nivelEstrutura == null ? "" : " and ho.indicedono = :nivelEstrutura") +
            " order by ho.codigo ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataDaOperacao));
        q.setParameter("usuarioId", usuarioSistema.getId());
        q.setParameter("gestor", Boolean.TRUE);
        q.setParameter("filtro", "%" + parteDigitada.toUpperCase().trim() + "%");
        if (nivelEstrutura != null) {
            q.setParameter("nivelEstrutura", nivelEstrutura);
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorMaterial(String parte, String nivelEstrutura, Date data, UsuarioSistema usuarioCorrente) {
        String sql = "  SELECT h.* " +
            "       FROM vwhierarquiaadministrativa vw " +
            " INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            " INNER JOIN unidadeorganizacional u on u.id = h.subordinada_id " +
            " INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id " +
            "      WHERE :data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, :data)" +
            "      AND ((replace(vw.codigo,'.','') LIKE :str) or vw.codigo LIKE :str OR (lower(vw.descricao) LIKE :str))" +
            "        AND uu.gestorMateriais = 1 " +
            "        AND uu.usuariosistema_id = :usuario_id" +
            (nivelEstrutura == null ? "" : " AND h.indicedono = :nivel_estrutura") +
            "   ORDER BY vw.codigo";

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", data, TemporalType.DATE);
        q.setParameter("usuario_id", usuarioCorrente.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);

        if (nivelEstrutura != null) {
            q.setParameter("nivel_estrutura", nivelEstrutura);
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdmiministrativaLocalEstoueGestorMaterial(String parte, HierarquiaOrganizacional hierarquia) {
        String sql = "  SELECT distinct h.* " +
            "       FROM vwhierarquiaadministrativa vw " +
            " INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            " INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = h.subordinada_id" +
            " inner join localestoque le on le.unidadeorganizacional_id = h.subordinada_id   " +
            " where to_date(:data, 'dd/MM/yyyy') between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), to_date(:data, 'dd/MM/yyyy'))" +
            "   and h.codigo like :codigoOrgao " +
            "      AND ((replace(vw.codigo,'.','') LIKE :str) or vw.codigo LIKE :str OR (lower(vw.descricao) LIKE :str))" +
            "        AND uu.gestorMateriais = 1 " +
            "        AND uu.usuariosistema_id = :usuario_id " +
            "   ORDER BY h.codigo";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("usuario_id", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("codigoOrgao", hierarquia.getCodigoSemZerosFinais() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalAdministrativaFilhasOndeUsuarioEhGestorMaterial(String parte, String nivelEstrutura, Date data, UsuarioSistema usuarioCorrente, HierarquiaOrganizacional pai) {
        String sql = "  SELECT h.* " +
            "       FROM vwhierarquiaadministrativa vw " +
            " INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            " INNER JOIN unidadeorganizacional u on u.id = h.subordinada_id " +
            " INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id " +
            "      WHERE :data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, :data)" +
            "        AND (upper(vw.descricao) LIKE :str " +
            "         OR vw.codigo LIKE :str) " +
            "        AND h.codigo LIKE :codigo " +
            "        AND uu.gestorMateriais = 1 " +
            "        AND uu.usuariosistema_id = :usuario_id" +
            (nivelEstrutura == null ? "" : " AND h.indicedono = :nivel_estrutura") +
            "   ORDER BY vw.codigo";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("str", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("codigo", pai.getCodigo().substring(0, 6) + "%");
        q.setParameter("data", data, TemporalType.DATE);
        q.setParameter("usuario_id", usuarioCorrente.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (nivelEstrutura != null) {
            q.setParameter("nivel_estrutura", nivelEstrutura);
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalOrcamentariaPorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "  select  dados.* from (" +
            " SELECT DISTINCT h.* " +
            "    FROM vwhierarquiaorcamentaria vw " +
            "   INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            " WHERE :data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, :data)" +
            "   and (upper(translate(VW.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) " +
            "   like translate(:codigoOrDescricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "   or VW.CODIGO like :codigoOrDescricao or replace(VW.CODIGO,'.','') like :codigoOrDescricao)  order by VW.codigo" +
            ") dados " +
            " ORDER BY dados.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.trim().toUpperCase() + "%");
        q.setParameter("data", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacional recuperarHierarquiaPorCodigoTipoData(String codigo, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, Date data) {
        String sql = "SELECT DISTINCT ho.*  from HierarquiaOrganizacional ho " +
            "   WHERE ho.tipohierarquiaorganizacional = :tipo" +
            " and ho.codigo = :codigo" +
            " and to_date(:data, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:data, 'dd/MM/yyyy'))";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("tipo", tipoHierarquiaOrganizacional.name());
        q.setParameter("codigo", codigo);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(1);
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade " + tipoHierarquiaOrganizacional + " para o código " + codigo + " e data " + DataUtil.getDataFormatada(data) + ".");
        }
    }

    public HierarquiaOrganizacional buscarOrgaoAdministrativoPorUnidadeAndVigencia(UnidadeOrganizacional unidadeOrganizacional, Date dataVigencia) {
        String sql = " with hierarquia (codigo, nivel, id, subordinada_id, superior_id) as ( " +
            " select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel, vw.id, vw.subordinada_id, vw.superior_id from vwhierarquiaadministrativa vw " +
            " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " and vw.subordinada_id = :subordinada_id  " +
            " union all " +
            " select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel, vw.id, vw.subordinada_id, vw.superior_id from vwhierarquiaadministrativa vw " +
            " inner join hierarquia filho on filho.superior_id = vw.subordinada_id " +
            " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " )select distinct hie.* from hierarquia " +
            " inner join HIERARQUIAORGANIZACIONAL hie on hie.id = hierarquia.id " +
            " where hierarquia.nivel = 2 " +
            " and to_date(:data, 'dd/MM/yyyy') between trunc(hie.iniciovigencia) and coalesce(trunc(hie.fimvigencia), to_date(:data, 'dd/MM/yyyy')) " +
            " order by hie.codigo ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("subordinada_id", unidadeOrganizacional.getId());
        q.setMaxResults(1);
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o Órgão para a unidade " + unidadeOrganizacional.getDescricao() + " na data " + DataUtil.getDataFormatada(dataVigencia) + ".");
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasPorTipoAndUnidadeAndVigencia(TipoHierarquiaOrganizacional tipo, UnidadeOrganizacional unidadeOrganizacional, Date dataVigencia) {
        String sql = " with hierarquia (codigo, nivel, id, subordinada_id, superior_id) as ( " +
            " select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel, vw.id, vw.subordinada_id, vw.superior_id from " + buscarTipo(tipo) + " vw " +
            " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " and vw.subordinada_id = :subordinada_id  " +
            " union all " +
            " select vw.codigo, nivelestrutura(vw.codigo, '.') as nivel, vw.id, vw.subordinada_id, vw.superior_id from " + buscarTipo(tipo) + " vw " +
            " inner join hierarquia filho on filho.superior_id = vw.subordinada_id " +
            " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " )select distinct hie.* from hierarquia " +
            " inner join HIERARQUIAORGANIZACIONAL hie on hie.id = hierarquia.id " +
            " where to_date(:data, 'dd/MM/yyyy') between trunc(hie.iniciovigencia) and coalesce(trunc(hie.fimvigencia), to_date(:data, 'dd/MM/yyyy')) " +
            " order by hie.codigo ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("subordinada_id", unidadeOrganizacional.getId());
        try {
            return (List<HierarquiaOrganizacional>) q.getResultList();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar hierarquia para a unidade " + unidadeOrganizacional.getDescricao() + " na data " + DataUtil.getDataFormatada(dataVigencia) + ".");
        }
    }

    private String buscarTipo(TipoHierarquiaOrganizacional tipo) {
        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipo)) {
            return " vwhierarquiaorcamentaria ";
        } else {
            return " vwhierarquiaadministrativa ";
        }
    }

    public void validarUnidadesIguais(UnidadeOrganizacional uni1, UnidadeOrganizacional uni2, ValidacaoException ve, String mensagem) {
        if (!uni1.equals(uni2)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem + " Feche todas as abas do navegador e tente novamente. Caso o problema persistir, entre em contato com o suporte.");
        }
    }

    public boolean isGestorOrcamento(UsuarioSistema usuario, UnidadeOrganizacional unidadeOrcamentaria, Date dataOperacao) {
        return usuarioSistemaFacade.isGestorOrcamento(usuario, unidadeOrcamentaria, dataOperacao);
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorMaterial(String parte, Date data, UsuarioSistema usuarioCorrente) {
        String sql = "  select distinct orc.* " +
            "       from VWHIERARQUIAORCAMENTARIA VW" +
            "  inner join HIERARQUIAORGANIZACIONAL orc on orc.id = vw.id" +
            "  inner join USUARIOUNIDADEORGANIZACORC UU on UU.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            "  inner join HIERARQUIAORGRESP RESP on VW.id = RESP.HIERARQUIAORGORC_ID" +
            "  inner join HIERARQUIAORGANIZACIONAL HIE on RESP.HIERARQUIAORGADM_ID = HIE.id" +
            "  inner join USUARIOUNIDADEORGANIZACIO usu on hie.subordinada_id = USU.UNIDADEORGANIZACIONAL_ID" +
            "      where to_date(:data, 'dd/mm/yyyy') between VW.INICIOVIGENCIA and coalesce(VW.FIMVIGENCIA, to_date(:data, 'dd/mm/yyyy')) " +
            "        and to_date(:data, 'dd/mm/yyyy') between trunc(HIE.INICIOVIGENCIA) and coalesce(trunc(HIE.FIMVIGENCIA), to_date(:data, 'dd/mm/yyyy')) " +
            "        and to_date(:data, 'dd/mm/yyyy') between trunc(RESP.DATAINICIO) and coalesce(trunc(RESP.DATAFIM), to_date(:data, 'dd/mm/yyyy')) " +
            "        and USU.GESTORMATERIAIS = 1" +
            "        and UU.USUARIOSISTEMA_ID = :usuario_id" +
            "        AND (upper(vw.descricao) LIKE :str OR vw.codigo LIKE :str or replace(vw.codigo, '.', '') like :str)" +
            "   ORDER BY orc.codigo";

        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);

        q.setParameter("str", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("usuario_id", usuarioCorrente.getId());

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalFilhasDe(Date dataOperacao,
                                                                                 TipoHierarquiaOrganizacional tipoHierarquia,
                                                                                 UnidadeOrganizacional superior,
                                                                                 String filtro) {
        String sql = "with dados ( " +
            "     id, codigo, nivelnaentidade, tipohierarquiaorganizacional, subordinada_id, superior_id, " +
            "     valorestimado, indicedono, codigono, exercicio_id, tipohierarquia, hierarquiaorc_id, iniciovigencia, fimvigencia, descricao)" +
            " as (select id, codigo, nivelnaentidade, tipohierarquiaorganizacional, subordinada_id, superior_id, valorestimado, indicedono, " +
            "            codigono, exercicio_id, tipohierarquia, hierarquiaorc_id, iniciovigencia, fimvigencia, descricao" +
            "        from HierarquiaOrganizacional h " +
            "     WHERE to_date(:data, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/mm/yyyy')) " +
            "        and h.superior_id = :id_superior " +
            "        and h.tipoHierarquiaOrganizacional = :tipo " +
            "     union all " +
            "     select filho.id, filho.codigo, filho.nivelnaentidade, filho.tipohierarquiaorganizacional, filho.subordinada_id, filho.superior_id, filho.valorestimado, " +
            "            filho.indicedono, filho.codigono, filho.exercicio_id, filho.tipohierarquia, filho.hierarquiaorc_id, filho.iniciovigencia," +
            "            filho.fimvigencia, filho.descricao" +
            "        from dados d " +
            "       inner join hierarquiaorganizacional filho on d.subordinada_id = filho.superior_id" +
            "     where to_date(:data, 'dd/mm/yyyy') between trunc(filho.iniciovigencia) and coalesce(trunc(filho.fimvigencia), to_date(:data, 'dd/mm/yyyy')) " +
            "        and filho.tipoHierarquiaOrganizacional = :tipo" +
            "        and (lower(filho.descricao) like :filtro or filho.codigo like :filtro)) " +
            " select * from dados " +
            " order by codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipo", tipoHierarquia.name());
        q.setParameter("id_superior", superior.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> recuperarHierarquiaOrganizacionalPorCapacitacao(String filtro, Capacitacao capacitacao) {
        String sql = " SELECT DISTINCT HO.* FROM HIERARQUIAORGANIZACIONAL HO "
            + " INNER JOIN VINCULOFP VINCULO ON VINCULO.UNIDADEORGANIZACIONAL_ID = HO.SUBORDINADA_ID "
            + " INNER JOIN MATRICULAFP MATRICULA ON MATRICULA.ID = VINCULO.MATRICULAFP_ID "
            + " INNER JOIN INSCRICAOCAPACITACAO INSC ON INSC.MATRICULAFP_ID = VINCULO.MATRICULAFP_ID "
            + " INNER JOIN CAPACITACAO CAPACITACAO ON CAPACITACAO.ID = INSC.CAPACITACAO_ID "
            + " WHERE TO_DATE(:data, 'dd/MM/yyyy') BETWEEN trunc(VINCULO.INICIOVIGENCIA) AND COALESCE(trunc(VINCULO.FINALVIGENCIA), TO_DATE(:data, 'dd/MM/yyyy'))"
            + " AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN trunc(HO.INICIOVIGENCIA) AND COALESCE(trunc(HO.FIMVIGENCIA), TO_DATE(:data, 'dd/MM/yyyy')) "
            + " AND (lower(HO.DESCRICAO) LIKE :filtro OR HO.CODIGO LIKE :filtro) "
            + " AND HO.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA' ";
        if (capacitacao != null) {
            sql += " AND CAPACITACAO.ID = :capacitacao";
        }
        sql += " ORDER BY HO.CODIGO ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        if (capacitacao != null) {
            q.setParameter("capacitacao", capacitacao.getId());
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> recuperarUnidadePorCapacitacao(String filtro, Capacitacao capacitacao) {
        String sql = " SELECT "
            + "    ho.* "
            + "FROM "
            + "    vinculofp vinculo "
            + "    INNER JOIN lotacaofuncional lotacao ON lotacao.vinculofp_id = vinculo.id "
            + "    INNER JOIN inscricaocapacitacao insc ON insc.matriculafp_id = vinculo.matriculafp_id "
            + "    INNER JOIN capacitacao capacitacao ON capacitacao.id = insc.capacitacao_id "
            + "    INNER JOIN unidadeorganizacional uo ON uo.id = lotacao.unidadeorganizacional_id "
            + "    INNER JOIN hierarquiaorganizacional ho ON ho.subordinada_id = uo.id "
            + "WHERE "
            + "    TO_DATE(:data, 'dd/MM/yyyy') BETWEEN trunc(lotacao.iniciovigencia) AND coalesce(trunc(lotacao.finalvigencia), TO_DATE(:data, 'dd/MM/yyyy')) "
            + "    AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN trunc(ho.iniciovigencia) AND coalesce(trunc(ho.fimvigencia), TO_DATE(:data, 'dd/MM/yyyy')) "
            + "    AND (lower(HO.DESCRICAO) LIKE :filtro OR HO.CODIGO LIKE :filtro) ";
        if (capacitacao != null) {
            sql += " AND CAPACITACAO.ID = :capacitacao";
        }
        sql += " ORDER BY ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        if (capacitacao != null) {
            q.setParameter("capacitacao", capacitacao.getId());
        }

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaFiltrandoTipoAdministrativaAndHierarquiaSemRaiz(String parte) {
        String sql = " SELECT ho.* FROM hierarquiaorganizacional ho "
            + "         INNER JOIN VWHIERARQUIAADMINISTRATIVA vw ON vw.id = ho.id "
            + "         INNER JOIN unidadeorganizacional uni on uni.id = ho.subordinada_id "
            + "        WHERE ho.tipohierarquiaorganizacional = :tipo "
            + "         AND to_date(:data, 'dd/mm/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:data, 'dd/mm/yyyy')) "
            + "         AND to_date(:data, 'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimVigencia, to_date(:data, 'dd/mm/yyyy')) "
            + "         AND ho.superior_id is not null " +
            "           and (upper(translate(ho.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) " +
            "               like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "               or upper(ho.descricao) like :parte " +
            "               or ho.CODIGO like :parte or replace(ho.CODIGO,'.','') like :parte) " +
            "          ORDER BY ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        return hierarquias;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public HierarquiaOrganizacional buscarOrgaoDaUnidade(UnidadeOrganizacional lotacao, Date data) {
        HierarquiaOrganizacional orgao = null;
        try {
            orgao = buscarOrgaoAdministrativoPorUnidadeAndVigencia(lotacao, data);
        } catch (ExcecaoNegocioGenerica e) {
        }
        if (orgao == null) {
            orgao = getHierarquiaOrganizacionalPorUnidade(data, lotacao, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }
        return orgao;
    }

    public String buscarCodigoDescricaoHierarquia(String tipoHierarquia, UnidadeOrganizacional unidade, Date dataMovimento) {
        HierarquiaOrganizacional hierarquia = getHierarquiaDaUnidade(tipoHierarquia, unidade, dataMovimento);
        if (hierarquia != null) {
            return hierarquia.getCodigo() + " - " + hierarquia.getDescricao();
        }
        return unidade.getDescricao();
    }

    public String getDescricaoHierarquia(String tipoHierarquia, UnidadeOrganizacional unidade, Date dataMovimento) {
        HierarquiaOrganizacional hierarquia = getHierarquiaDaUnidade(tipoHierarquia, unidade, dataMovimento);
        if (hierarquia != null) {
            return hierarquia.getDescricao();
        }
        return unidade.getDescricao();
    }


    public String getSiglaHierarquia(TipoHierarquiaOrganizacional tipoHierarquia, UnidadeOrganizacional unidade) {
        String sql = " " +
            "   select regexp_replace(h.descricao, '.* - ', '') from hierarquiaorganizacional h " +
            "    where trunc(:data) between trunc(h.iniciovigencia)  and coalesce(trunc(h.fimvigencia), trunc(:data))" +
            "    and h.subordinada_id = :unidade " +
            "    and h.tipoHierarquiaOrganizacional = :tipo ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("data", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        q.setParameter("unidade", unidade.getId());
        q.setParameter("tipo", tipoHierarquia.name());
        List lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return (String) q.getSingleResult();
        }
        return unidade.getDescricao();
    }


    public List<HierarquiaOrganizacional> buscarFiltrandoHierarquiaOndeUsuarioEhGestorPatrimonio(UsuarioSistema usuarioSistema, Date dataReferencia, String parte) {
        String sql = "    SELECT distinct " +
            "              HIERARQUIAORC.* " +
            "      FROM HIERARQUIAORGRESP " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA   VWORC   ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
            "INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = VWADM.SUBORDINADA_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAORC ON HIERARQUIAORC.ID = VWORC.ID" +
            "     WHERE to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA,to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN trunc(HIERARQUIAORGRESP.DATAINICIO) AND COALESCE(trunc(HIERARQUIAORGRESP.DATAFIM), to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND uu.gestorpatrimonio = 1 " +
            "       AND uu.usuariosistema_id = :usuario_id" +
            "       AND (lower(vworc.descricao) LIKE :filtro OR vworc.codigo LIKE :filtro)  " +
            "  ORDER BY HIERARQUIAORC.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasUsuarioCorrenteProtocolo(UsuarioSistema usu, String parte) {
        String sql;
        sql = "select hadm.* from usuariosistema usuSis "
            + " inner join usuariounidadeorganizacio usuuni on ususis.id = usuuni.usuariosistema_id "
            + " inner join unidadeorganizacional uo on usuuni.unidadeorganizacional_id = uo.id "
            + " inner join hierarquiaorganizacional hadm on hadm.subordinada_id = uo.id "
            + " where ususis.id = :usuario_id "
            + "  and usuuni.gestorProtocolo = :gestorProtocolo "
            + "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hadm.iniciovigencia) and coalesce(trunc(hadm.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + "  and hadm.tipohierarquiaorganizacional = :tipo "
            + " and ((replace(hadm.codigo,'.','') LIKE :parte) or hadm.codigo LIKE :parte OR (upper(hadm.descricao) LIKE :parte)) "
            + " order by hadm.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("gestorProtocolo", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacionalDTO> buscarHierarquiaOndeUsuarioEhGestorProtocolo(UsuarioSistema usuarioSistema, Date dataReferencia) {
        String sql = "SELECT distinct HIERARQUIAADM.id, HIERARQUIAADM.codigo, HIERARQUIAADM.DESCRICAO, HIERARQUIAADM.SUBORDINADA_ID " +
            "      FROM HIERARQUIAORGANIZACIONAL HIERARQUIAADM " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM ON VWADM.ID = HIERARQUIAADM.ID " +
            "INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = VWADM.SUBORDINADA_ID " +
            "     WHERE to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA, to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       and to_date(:dataAtual, 'dd/mm/yyyy') BETWEEN trunc(HIERARQUIAADM.INICIOVIGENCIA) AND COALESCE(trunc(HIERARQUIAADM.FIMVIGENCIA), to_date(:dataAtual, 'dd/mm/yyyy')) " +
            "       AND uu.gestorprotocolo = 1  " +
            "       AND uu.usuariosistema_id = :usuario_id" +
            "  ORDER BY HIERARQUIAADM.CODIGO";

        Query q = em.createNativeQuery(sql);
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(dataReferencia));
        List<HierarquiaOrganizacionalDTO> hierarquias = Lists.newArrayList();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            HierarquiaOrganizacionalDTO dto = new HierarquiaOrganizacionalDTO();
            dto.setId(((Number) o[0]).longValue());
            dto.setCodigo((String) o[1]);
            dto.setDescricao((String) o[2]);
            dto.setSubordinadaId(((Number) o[3]).longValue());
            hierarquias.add(dto);
        }
        return hierarquias;
    }

    public HierarquiaOrganizacional recuperarSecretariaAdministrativaVigente(HierarquiaOrganizacional hierarquiaOrganizacional, Date dataOperacao) {
        String sql = "SELECT DISTINCT h.* " +
            " FROM " +
            " VWHIERARQUIAADMINISTRATIVA VW" +
            " inner join HierarquiaOrganizacional h on h.id = vw.id " +
            " WHERE VW.CODIGO = SUBSTRB(:codigo,1,5) || '.00.00000.000.00' " +
            " and trunc(:dataOperacao) BETWEEN trunc(VW.INICIOVIGENCIA) and coalesce(trunc(VW.FIMVIGENCIA), trunc(:dataOperacao))";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("codigo", hierarquiaOrganizacional.getCodigo());
        q.setParameter("dataOperacao", DataUtil.dataSemHorario(dataOperacao));
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException no) {
            return null;
        }
    }

    public HierarquiaOrganizacional recuperarAdministrativaDaOrcamentariaVigente(HierarquiaOrganizacional orcamentaria, Date dataOperacao) {
        String sql = "select hoadm.*" +
            " from HIERARQUIAORGCORRE corr " +
            " inner join hierarquiaorganizacional hoadm on corr.hierarquiaorgadm_id = hoadm.id " +
            " where corr.hierarquiaorgorc_id = :id " +
            " AND trunc(:dataOperacao) BETWEEN trunc(corr.DATAINICIO) AND COALESCE(trunc(corr.DATAFIM), trunc(:dataOperacao))" +
            " AND trunc(:dataOperacao) BETWEEN trunc(hoadm.INICIOVIGENCIA) AND COALESCE(trunc(hoadm.FIMVIGENCIA), trunc(:dataOperacao))";
        Query consulta = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        consulta.setParameter("id", orcamentaria.getId());
        consulta.setParameter("dataOperacao", DataUtil.dataSemHorario(dataOperacao));
        try {
            return (HierarquiaOrganizacional) consulta.getSingleResult();
        } catch (NoResultException no) {
            return null;
        }

    }

    public List<HierarquiaOrganizacional> buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(String parte, UnidadeOrganizacional unidadeAdministrativa, Date dataReferencia) {
        String sql = "    SELECT HIERARQUIAORC.* " +
            "      FROM HIERARQUIAORGRESP " +
            "INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM   ON VWADM.ID = HIERARQUIAORGRESP.HIERARQUIAORGADM_ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC     ON VWORC.ID = HIERARQUIAORGRESP.HIERARQUIAORGORC_ID " +
            "INNER JOIN UNIDADEORGANIZACIONAL ORCAMENTARIA ON ORCAMENTARIA.ID =  VWORC.SUBORDINADA_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HIERARQUIAORC ON HIERARQUIAORC.ID = VWORC.ID" +
            "     WHERE :dataAtual BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA,:dataAtual) " +
            "       AND :dataAtual BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA,:dataAtual) " +
            "       AND :dataAtual BETWEEN HIERARQUIAORGRESP.DATAINICIO AND COALESCE(HIERARQUIAORGRESP.DATAFIM,:dataAtual)" +
            "       AND VWADM.SUBORDINADA_ID = :unidadeadministrativa" +
            "       AND ((replace(VWORC.codigo,'.','') LIKE :parte) or VWORC.codigo LIKE :parte OR (upper(VWORC.descricao) LIKE :parte)) " +
            "  ORDER BY HIERARQUIAORC.CODIGO";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("unidadeadministrativa", unidadeAdministrativa.getId());
        q.setParameter("parte", "%" + parte.trim().toUpperCase().trim() + "%");
        q.setParameter("dataAtual", dataReferencia, TemporalType.DATE);
        return q.getResultList();
    }

    public boolean estaNoOrgao(UnidadeOrganizacional unidadeOrganizacional, HierarquiaOrganizacional orgao) {
        HierarquiaOrganizacional ho = getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        while (ho.getSuperior() != null) {
            if (ho.getCodigo().equals(orgao.getCodigo())) {
                return true;
            }
            ho = getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), ho.getSuperior(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }
        return false;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaUsuarioPorTipoData(String parte, UsuarioSistema usuario, Date dataOperacao, TipoHierarquiaOrganizacional tipoHierarquia) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ho.*  ")
            .append(" from hierarquiaorganizacional ho  ");
        if (TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(tipoHierarquia)) {
            sql.append("   inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = ho.subordinada_id ");
        } else {
            sql.append("   inner join usuariounidadeorganizacorc usuund on usuund.unidadeorganizacional_id = ho.subordinada_id ");
        }
        sql.append("   inner join usuariosistema usu on usuund.usuariosistema_id = usu.id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and (lower(ho.descricao) like :str or ho.codigo like :str or replace(ho.codigo, '.', '') like :str)  ")
            .append("   and usu.id = :idUsuario ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia");
        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipoHierarquia)) {
            sql.append(" and ho.indicedono = :indice ");
        }
        sql.append(" order by ho.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("tipoHierarquia", tipoHierarquia.name());
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipoHierarquia)) {
            q.setParameter("indice", Integer.valueOf("3"));
        }
        return q.getResultList();
    }

    public HierarquiaOrganizacional recuperarHierarquiaOrganizacionalPorUnidadeId(Long idUnidade, TipoHierarquiaOrganizacional tipoHierarquia, Date dataOperacao) {
        String sql = " select ho.* FROM hierarquiaorganizacional ho "
            + "         where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) "
            + "          and ho.subordinada_id = :idUnidade " +
            "            and ho.tipohierarquiaorganizacional = :tipoHierarquia ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        if (dataOperacao != null) {
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        } else {
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        }
        q.setParameter("idUnidade", idUnidade);
        q.setParameter("tipoHierarquia", tipoHierarquia.name());
        q.setMaxResults(1);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getResultList().get(0);
        }
        return null;
    }

    public List<HierarquiaOrganizacional> recuperaHierarquiaOrgaoVigentes(Date dataOperacao) {
        HierarquiaOrganizacional ho = null;
        Query createQuery = em.createQuery("select ho from HierarquiaOrganizacional ho where ho.nivelNaEntidade = 2 " +
            " and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' and :data between ho.inicioVigencia " +
            " and coalesce(ho.fimVigencia, :data)");
        createQuery.setParameter("data", dataOperacao, TemporalType.DATE);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return createQuery.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasPorFolha(FolhaDePagamento folha) {
        Query q = em.createNativeQuery("select distinct ho.* from FichaFinanceiraFP ficha  " +
                "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
                "              inner join UnidadeOrganizacional rec on rec.ID = ficha.unidadeorganizacional_id  " +
                "              inner join HierarquiaOrganizacional ho on rec.ID = ho.subordinada_id  " +
                " where folha.id = :id and ho.tipohierarquiaorganizacional = :tipoAdm" +
                " and to_date( (folha.mes+1)||''||folha.ano,'mm/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date( (folha.mes+1)||''||folha.ano,'mm/yyyy'))" +
                "  order by ho.codigo "
            , HierarquiaOrganizacional.class);
        q.setParameter("id", folha.getId());
        q.setParameter("tipoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaUsuarioPorTipo(UsuarioSistema usuario, Date dataOperacao,
                                                                         TipoHierarquiaOrganizacional tipoHierarquia,
                                                                         Integer... niveis) {

        String sNiveis = niveis.length > 0 ? " and (" : "";

        for (Integer i : niveis) {
            sNiveis += " ho.indicedono = :nivel" + i + " or";
        }

        sNiveis = niveis.length > 0 ? sNiveis.substring(0, sNiveis.length() - 2) : "";
        sNiveis = niveis.length > 0 ? sNiveis + ")" : "";

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ho.*  ")
            .append(" from hierarquiaorganizacional ho  ")
            .append("   inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = ho.subordinada_id ")
            .append("   inner join usuariosistema usu on usuund.usuariosistema_id = usu.id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and usu.id = :idUsuario ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia")
            .append(sNiveis)
            .append(" order by ho.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("tipoHierarquia", tipoHierarquia.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

        for (Integer i : niveis) {
            q.setParameter("nivel" + i, i);
        }
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarUnidadesOrcamentariasRequisicaoCompra(RequisicaoDeCompra requisicaoCompra) {
        String sql = " select distinct vw.* from requisicaodecompra req  " +
            " left join requisicaocompraexecucao reqEx on reqEx.requisicaocompra_id = req.id " +
            " left join execucaocontratoempenho exEmp on exEmp.id = reqEx.execucaocontratoempenho_id " +
            " left join execucaoprocessoempenho exEmpProc on  exEmpProc.id = reqEx.execucaoprocessoempenho_id " +
            " left join solicitacaoempenhorecdiv exrd on exrd.id = reqEx.execucaoreconhecimentodiv_id " +
            " left join reconhecimentodivida rd on rd.id = exrd.reconhecimentodivida_id " +
            " left join solicitacaoempenho sol on sol.reconhecimentodivida_id = rd.id " +
            " inner join empenho emp on emp.id = coalesce(exEmp.empenho_id, exEmpProc.empenho_id, sol.empenho_id) " +
            " inner join hierarquiaorganizacional vw on vw.subordinada_id = emp.unidadeorganizacional_id  " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and vw.tipohierarquiaorganizacional = :tipoHo" +
            " and req.id = :idRequisicao ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(requisicaoCompra.getDataRequisicao()));
        q.setParameter("tipoHo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDoEmpregadorEsocial(Entidade empregador, Date dataOperacao) {
        String sql = " select hierarquia.* " +
            " from VWHIERARQUIAADMINISTRATIVA ho " +
            " inner join hierarquiaorganizacional hierarquia ON hierarquia.id = ho.id " +
            " inner join ITEMEMPREGADORESOCIAL item on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " inner join CONFIGEMPREGADORESOCIAL conf on item.CONFIGEMPREGADORESOCIAL_ID = conf.ID " +
            " where conf.ENTIDADE_ID = :idEmpregador " +
            "   and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idEmpregador", empregador.getId());
        q.setParameter("dataOperacao", dataOperacao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> agruparHierarquiasFilhas(List<HierarquiaOrganizacional> hos) {
        if (hos.isEmpty()) {
            return Lists.newArrayList();
        }
        HashMap<String, HierarquiaOrganizacional> codigoHos = new HashMap<>();
        hos.forEach(ho -> {
            codigoHos.put(HierarquiaOrganizacionalUtil.removeZerosFinalCodigo(ho.getCodigo()), ho);
        });
        List<String> codigosSemZerosFinal = codigoHos.keySet()
            .stream().sorted(Comparator.comparingInt(String::length))
            .collect(Collectors.toList());
        List<String> codigosFiltrados = Lists.newArrayList(codigosSemZerosFinal);
        List<String> codigosRemovidos = Lists.newArrayList();
        codigosSemZerosFinal.forEach((codigo) -> {
            List<String> removidos =  codigosFiltrados.stream()
                .filter(cod -> cod.startsWith(codigo)).collect(Collectors.toList());
            codigosFiltrados.removeAll(removidos);
            if(!codigosRemovidos.contains(codigo)) {
                codigosFiltrados.add(codigo);
            }
            codigosRemovidos.addAll(removidos);
        });

        return codigosFiltrados.stream().map(codigoHos::get).collect(Collectors.toList());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(List<Long> hoIds) {
        String sql = " select ho.* " +
            " from hierarquiaorganizacional ho " +
            " where ho.id in :hoIds " +
            " order by ho.codigo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("hoIds", hoIds);
        List resultado = q.getResultList();
        if (resultado != null) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> buscarUnidadeAdministrativaPorUsuario(UsuarioSistema usuario, String parte, Date dataOperacao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ho.*  ")
            .append(" from hierarquiaorganizacional ho  ")
            .append("   inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = ho.subordinada_id ")
            .append("   inner join usuariosistema usu on usuund.usuariosistema_id = usu.id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and (lower(ho.descricao) like :str or ho.codigo like :str or replace(ho.codigo, '.', '') like :str)  ")
            .append("   and usu.id = :idUsuario ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia")
            .append(" order by ho.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarUnidadeOrcamentariaPorUsuario(UsuarioSistema usuario, String parte, Date dataOperacao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ho.*  ")
            .append(" from hierarquiaorganizacional ho  ")
            .append("   inner join usuariounidadeorganizacorc usuundorc on usuundorc.unidadeorganizacional_id = ho.subordinada_id ")
            .append("   inner join usuariosistema usu on usuundorc.usuariosistema_id = usu.id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and (lower(ho.descricao) like :str or ho.codigo like :str or replace(ho.codigo, '.', '') like :str)  ")
            .append("   and usu.id = :idUsuario ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia")
            .append(" order by ho.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("str", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionaisAdmnistrativasComFilhasDoUsuarioFiltrando(String parte, Date data, UsuarioSistema usuarioSistema) {
        String sql = " select distinct h.* " +
            " from vwhierarquiaadministrativa vw " +
            "         inner join hierarquiaorganizacional h on h.id = vw.id " +
            " where to_date(:data, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "  and vw.subordinada_id in (select distinct ho.subordinada_id " +
            "                            from unidadeorganizacional uo " +
            "                                     inner join hierarquiaorganizacional ho " +
            "                                                on uo.id = ho.subordinada_id and " +
            "                                                   ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "                                                    and to_date(:data, 'dd/mm/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "                            start with ho.id in (select distinct hierarquia.id " +
            "                                                 from hierarquiaorganizacional hierarquia " +
            "                                                          inner join usuariounidadeorganizacio uu " +
            "                                                                     on uu.unidadeorganizacional_id = hierarquia.subordinada_id " +
            "                                                          inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            "                                                 where u.id = :idUsuario " +
            "                                                   and hierarquia.tipohierarquiaorganizacional = :tipoHierarquia " +
            "                                                   and to_date(:data, 'dd/mm/yyyy') between hierarquia.iniciovigencia and coalesce(hierarquia.fimvigencia, to_date(:data, 'dd/mm/yyyy'))) " +
            "                            connect by prior ho.subordinada_id = ho.superior_id) " +
            "  and (upper(translate(vw.descricao, 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc')) like " +
            "       translate(:str, 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc') " +
            "    or vw.codigo like :str " +
            "    or replace(vw.codigo, '.', '') like :str) " +
            "order by h.codigo";

        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro.toUpperCase());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasAdministrativasFilhasPorNivel(HierarquiaOrganizacional hoSuperior, Date dataOperacao, Integer nivel) {
        String sql = " select ho.* " +
            " from unidadeorganizacional uo " +
            "         inner join hierarquiaorganizacional ho " +
            "                    on uo.id = ho.subordinada_id and ho.tipohierarquiaorganizacional = :tipo " +
            "                        and " +
            "                       to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where ho.id <> :hoSuperior " +
            " start with ho.id = :hoSuperior " +
            " connect by prior ho.subordinada_id = ho.superior_id and ho.INDICEDONO = :nivel " +
            " order by ho.codigo";
        Query q = getEntityManager().createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("hoSuperior", hoSuperior.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("nivel", nivel);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return q.getResultList();
    }
}
