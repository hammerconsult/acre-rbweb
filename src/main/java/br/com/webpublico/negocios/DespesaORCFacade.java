/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Stateless
public class DespesaORCFacade extends AbstractFacade<DespesaORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public DespesaORCFacade() {
        super(DespesaORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VwHierarquiaDespesaORC> descricaoDoCodigo(Date data, TipoContaDespesa tipo, UnidadeOrganizacional unidade, Exercicio exercicio, String codigo, Boolean filtrarTipoPelasFilhas) {

        String hql = "  SELECT V.* FROM vwhierarquiadespesaorc v";
        if (filtrarTipoPelasFilhas != null && filtrarTipoPelasFilhas) {
            hql += " left JOIN CONTA filha on v.conta_id = filha.superior_id "
                + " left join CONTADESPESA cd on cd.id = filha.id ";
        }
        hql += "     WHERE  to_date(:data,'dd/MM/yyyy') between v.inicioVigencia and coalesce(v.fimVigencia, to_date(:data, 'dd/MM/yyyy')) ";
        if (tipo != null) {
            if (filtrarTipoPelasFilhas != null && filtrarTipoPelasFilhas) {
                hql += "         AND (v.tipocontadespesa = :tipo or cd.tipocontadespesa = :tipo) ";
            } else {
                hql += "         AND v.tipocontadespesa = :tipo ";
            }
        }
        if (unidade != null) {
            hql += "         AND v.UNIDADE_ID = :unidade ";
        }
        if (exercicio != null) {
            hql += "         AND v.EXERCICIO = :exe ";
        }
        if (codigo != null) {
            if (!codigo.trim().isEmpty()) {
                hql += "   AND (REPLACE(V.CONTA,'.','') LIKE :codigo OR (REPLACE(V.CODIGOREDUZIDO, '.', '') LIKE :codigo))";
            }
        }
        if (filtrarTipoPelasFilhas != null && filtrarTipoPelasFilhas) {
            hql += "   group by v.ID, v.ORGAO, v.UNIDADE, v.SUBACAO, v.CONTA, v.EXERCICIO, v.UNIDADE_ID, v.TIPOCONTADESPESA, v.INICIOVIGENCIA, v.FIMVIGENCIA, v.CODIGOREDUZIDO, v.conta_id ";
        }
        hql += "    ORDER BY V.ORGAO, V.UNIDADE, V.SUBACAO, V.CONTA ";

        Query q = em.createNativeQuery(hql, VwHierarquiaDespesaORC.class);
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        if (tipo != null) {
            q.setParameter("tipo", tipo.name());
        }
        if (unidade != null) {
            q.setParameter("unidade", unidade.getId());
        }
        if (exercicio != null) {
            q.setParameter("exe", exercicio.getId());
        }
        if (codigo != null) {
            if (!codigo.trim().isEmpty()) {
                q.setParameter("codigo", "%" + codigo.replace(".", "") + "%");
            }
        }
        List lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new ArrayList<VwHierarquiaDespesaORC>();
        }
        return lista;

    }

    public VwHierarquiaDespesaORC recuperaStrDespesa(String codReduzido, TipoContaDespesa tipo, UnidadeOrganizacional unidade) {
        String sql = "  SELECT D.ID AS id,"
            + "    hoUo.CODIGO"
            + "    ||' - '"
            + "    ||UOORG.DESCRICAO AS ORGAO,"
            + "    HO.CODIGO"
            + "    ||' - '"
            + "    || UOUNI.DESCRICAO AS UNIDADE,"
            + "    F.CODIGO"
            + "    ||'.'"
            + "    ||SF.CODIGO"
            + "    ||'.'"
            + "    ||P.CODIGO"
            + "    ||'.'"
            + "    ||TPA.CODIGO"
            + "    ||''"
            + "    ||A.CODIGO"
            + "    ||'.'"
            + "    ||SUB.CODIGO"
            + "    ||' - '"
            + "    ||SUB.DESCRICAO AS SUBACAO,"
            + "    CON.CODIGO"
            + "    ||' - '"
            + "    ||con.DESCRICAO AS CONTA,"
            + "    D.EXERCICIO_ID  AS EXERCICIO"
            + "  FROM DESPESAORC D"
            + "  INNER JOIN PROVISAOPPADESPESA PD  ON PD.ID = D.PROVISAOPPADESPESA_ID"
            + "  INNER JOIN SUBACAOPPA SUB  ON SUB.ID = PD.SUBACAOPPA_ID"
            + "  INNER JOIN ACAOPPA A  ON A.ID = SUB.ACAOPPA_ID"
            + "  INNER JOIN TIPOACAOPPA TPA  ON TPA.ID = A.TIPOACAOPPA_ID"
            + "  INNER JOIN PROGRAMAPPA P  ON P.ID = A.PROGRAMA_ID"
            + "  INNER JOIN FUNCAO F  ON F.ID = A.FUNCAO_ID"
            + "  INNER JOIN SUBFUNCAO SF  ON SF.ID = A.SUBFUNCAO_ID"
            + "  INNER JOIN UNIDADEORGANIZACIONAL UOUNI  ON UOUNI.ID = PD.UNIDADEORGANIZACIONAL_ID"
            + "  INNER JOIN HIERARQUIAORGANIZACIONAL HO  ON HO.SUBORDINADA_ID                = UOUNI.ID"
            + "         AND HO.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA'"
            + "         AND HO.EXERCICIO_ID                 = D.EXERCICIO_ID"
            + "  INNER JOIN UNIDADEORGANIZACIONAL UOORG  ON UOORG.ID = HO.SUPERIOR_ID"
            + "  INNER JOIN HIERARQUIAORGANIZACIONAL HOUO  ON HOUO.SUBORDINADA_ID                = UOORG.ID"
            + "         AND HOUO.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA'"
            + "         AND HOUO.EXERCICIO_ID                 = D.EXERCICIO_ID"
            + "  INNER JOIN CONTA CON  ON CON.ID = PD.CONTADEDESPESA_ID"
            + "  INNER JOIN CONTADESPESA CD ON CON.ID = CD.ID"
            + "       WHERE 1=1";

        if (tipo != null) {
            sql += " AND cd.tipocontadespesa = :tipo";
        }
        if (unidade != null) {
            sql += " AND UOUNI.id = :unidade";
        }
        sql += " AND (REPLACE(CON.CODIGO, '.', '') LIKE :param";

        sql += " OR d.codigoreduzido = :param)";

        Query q = getEntityManager().createNativeQuery(sql, VwHierarquiaDespesaORC.class);
        q.setParameter("param", " " + codReduzido.trim().replace(".", "") + "%");
        if (tipo != null) {
            q.setParameter("tipo", tipo.name());
        }
        if (unidade != null) {
            q.setParameter("unidade", unidade.getId());
        }
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new VwHierarquiaDespesaORC();
        }
        return (VwHierarquiaDespesaORC) q.getSingleResult();
    }

    public VwHierarquiaDespesaORC recuperaStrDespesaPorId(Long cod) {
        String sql = "SELECT v.* FROM VWHIERARQUIADESPESAORC v INNER JOIN DESPESAORC desp ON desp.ID = v.ID AND desp.id =:param";
        Query q = getEntityManager().createNativeQuery(sql, VwHierarquiaDespesaORC.class);
        q.setParameter("param", cod);
        List<VwHierarquiaDespesaORC> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return new VwHierarquiaDespesaORC();
        }
        return resultado.get(0);
    }

    public List<DespesaORC> listaPorProvisaoDespesa(ProvisaoPPADespesa ppd) {
        String sql = "SELECT * FROM DESPESAORC WHERE PROVISAOPPADESPESA_ID = :param";
        Query q = getEntityManager().createNativeQuery(sql, DespesaORC.class);
        q.setParameter("param", ppd);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<DespesaORC>();
        }
        return q.getResultList();
    }

    public VwHierarquiaDespesaORC recuperaVwDespesaOrc(DespesaORC desp, Date dataMovimento) throws ExcecaoNegocioGenerica {

        VwHierarquiaDespesaORC vw = null;
        try {
            if (desp != null) {
                if (desp.getId() != null) {
                    String sql = "select distinct(v) from VwHierarquiaDespesaORC v where v.exercicio=:exerc and v.id=:idOrc and to_date(:data,'dd/MM/yyyy') BETWEEN v.inicioVigencia and coalesce(v.fimVigencia,to_date(:data,'dd/MM/yyyy'))";
                    Query q = getEntityManager().createQuery(sql);
                    q.setParameter("exerc", desp.getExercicio().getId() + "");
                    q.setParameter("idOrc", desp.getId());
                    q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataMovimento));
                    vw = (VwHierarquiaDespesaORC) q.getSingleResult();
                } else {
                    throw new ExcecaoNegocioGenerica("DespesaORC NULL ");
                }
            } else {
                throw new ExcecaoNegocioGenerica("DespesaORC NULL ");
            }
        } catch (NoResultException nr) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma despesa orçamentaria para com o ID" + desp.getId());
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Ocorreu um problema com a arvore de despesa, existe mais de uma despesa orçamentaria. Entre em contato com o suporte!");
        } catch (IllegalArgumentException il) {
            il.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro de parametros na consultas para recuperar a Arvaro de despesa");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
        return vw;
    }

    public List<VwHierarquiaDespesaORC> descricaoDoCodigoComponenteConsulta(Date data, List<UnidadeOrganizacional> unidades, Exercicio exercicio, String codigo, List<TipoContaDespesa> tiposContaDespesa) {

        String hql = "  SELECT V.* FROM vwhierarquiadespesaorc v"
            + "     WHERE  to_date(:data,'dd/MM/yyyy') between v.inicioVigencia and coalesce(v.fimVigencia, to_date(:data, 'dd/MM/yyyy')) ";
        if (tiposContaDespesa != null) {
            hql += "         and v.conta_id in ( select co.superior_id from conta co" +
                "                    inner join contadespesa cd on co.id = cd.id" +
                "                    inner join planodecontas pc on co.planodecontas_id = pc.id " +
                "                    inner join planodecontasexercicio pce on pc.id = pce.planodedespesas_id " +
                "                    inner join exercicio e on pce.exercicio_id = e.id and e.id = :exe" +
                "                    where cd.tipocontadespesa in (:tiposContaDespesa) )";
        }
        if (unidades != null) {
            if (!unidades.isEmpty()) {
                hql += "         AND v.UNIDADE_ID in :unidades";
            }
        }
        if (exercicio != null) {
            hql += "         AND v.EXERCICIO = :exe ";
        }
        if (codigo != null) {
            if (!codigo.trim().isEmpty()) {
                hql += "   AND (lower(REPLACE(V.CONTA,'.','')) LIKE :codigo OR V.codigoreduzido LIKE :codigo)";
            }
        }
        hql += "    ORDER BY V.ORGAO, V.UNIDADE, substr(v.subacao,13,20), V.CONTA ";
        Query q = em.createNativeQuery(hql, VwHierarquiaDespesaORC.class);
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        if (unidades != null) {
            if (!unidades.isEmpty()) {
                q.setParameter("unidades", unidades);
            }
        }
        if (tiposContaDespesa != null) {
            if (!tiposContaDespesa.isEmpty()) {
                q.setParameter("tiposContaDespesa", recuperarListaName(tiposContaDespesa));
            }
        }
        if (exercicio != null) {
            q.setParameter("exe", exercicio.getId());
        }
        if (codigo != null) {
            if (!codigo.trim().isEmpty()) {
                q.setParameter("codigo", "%" + codigo.replace(".", "").toLowerCase() + "%");
            }
        }
        return q.getResultList();
    }


    public List<VwHierarquiaDespesaORC> buscarVwHierarquiaDespesaOrcPorConfiguracao(Date data, List<UnidadeOrganizacional> unidades, Exercicio exercicio,
                                                                                    String codigo, List<Long> idsConfig, String filtroElemento) {

        String sql = "  " +
            "   SELECT v.* " +
            "      FROM vwhierarquiadespesaorc v " +
            "   WHERE  to_date(:dataOperacao,'dd/MM/yyyy') between v.inicioVigencia and coalesce(v.fimVigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and v.conta_id in " +
            "           ( " +
            "             select co.id " +
            "             from configtipoobjetocompra config " +
            "               inner join tipoobjetocompracontadesp configConta on configConta.configtipoobjetocompra_id = config.id" +
            "               inner join conta co on co.id = configConta.contadespesa_id " +
            "               inner join contadespesa cd on co.id = cd.id " +
            "               inner join planodecontas pc on co.planodecontas_id = pc.id " +
            "               inner join planodecontasexercicio pce on pc.id = pce.planodedespesas_id " +
            "               inner join exercicio e on pce.exercicio_id = e.id and e.id = :exe " +
            "             where config.id in (:idsConfiguracao) " +
            "           )";
        if (!Util.isListNullOrEmpty(unidades)) {
            sql += "         AND v.UNIDADE_ID in (:unidades)";
        }
        if (exercicio != null) {
            sql += "         AND v.EXERCICIO = :exe ";
        }
        sql += getAndCodigo(codigo, "V");
        sql += getAndCodigo(filtroElemento, "V");
        sql += "    ORDER BY V.ORGAO, V.UNIDADE, substr(v.subacao,13,20), V.CONTA ";
        Query q = em.createNativeQuery(sql, VwHierarquiaDespesaORC.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("idsConfiguracao", idsConfig);
        if (!Util.isListNullOrEmpty(unidades)) {
            q.setParameter("unidades", Util.montarIdsList(unidades));
        }
        if (exercicio != null) {
            q.setParameter("exe", exercicio.getId());
        }
        return q.getResultList();
    }

    private String getAndCodigo(String codigo, String alias) {
        if (!Strings.isNullOrEmpty(codigo)) {
            return " and (lower(replace(" + alias + ".conta,'.','')) like '%" + codigo.replace(".", "").toLowerCase().trim() +
                "%' or " + alias + ".codigoreduzido like '%" + codigo.replace(".", "").toLowerCase().trim() + "%')";
        }
        return "";
    }

    public List<VwHierarquiaDespesaORC> buscarVwHierarquiaDespesaOrc(List<UnidadeOrganizacional> unidades, String codigo, String filtroElemento) {
        String sql = "  " +
            "   select v.* from vwhierarquiadespesaorc v " +
            "    where to_date(:dataOperacao,'dd/MM/yyyy') between v.inicioVigencia and coalesce(v.fimVigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and v.conta_id in ( " +
            "             select co.id " +
            "             from conta co " +
            "               inner join contadespesa cd on co.id = cd.id " +
            "               inner join planodecontas pc on co.planodecontas_id = pc.id " +
            "               inner join planodecontasexercicio pce on pc.id = pce.planodedespesas_id " +
            "               inner join exercicio e on pce.exercicio_id = e.id and e.id = :idExercicio)";
        if (unidades != null) {
            if (!unidades.isEmpty()) {
                sql += "  and v.unidade_id in :unidades";
            }
        }
        sql += getAndCodigo(codigo, "V");
        sql += getAndCodigo(filtroElemento, "V");
        sql += "    order by V.ORGAO, V.UNIDADE, substr(v.subacao,13,20), V.CONTA ";
        Query q = em.createNativeQuery(sql, VwHierarquiaDespesaORC.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idExercicio", sistemaFacade.getExercicioCorrente().getId());
        if (unidades != null) {
            if (!unidades.isEmpty()) {
                q.setParameter("unidades", unidades);
            }
        }
        return q.getResultList();
    }

    private List<String> recuperarListaName(List<TipoContaDespesa> tipos) {
        List<String> retorno = new ArrayList<>();
        for (TipoContaDespesa or : tipos) {
            retorno.add(or.name());
        }
        return retorno;
    }

    private List<Long> getIdsUnidade(List<UnidadeOrganizacional> unidades) {
        List<Long> ids = new ArrayList<>();
        for (UnidadeOrganizacional unidade : unidades) {
            ids.add(unidade.getId());
        }
        return ids;
    }

    public List<DespesaORC> listaContaDespesaDaDiaria(String parte, TipoContaDespesa tipoContaDespesa, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT DESP.* FROM DESPESAORC DESP "
            + " INNER JOIN PROVISAOPPADESPESA PPD ON PPD.ID = DESP.PROVISAOPPADESPESA_ID "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = PPD.CONTADEDESPESA_ID "
            + " INNER JOIN CONTA C ON C.ID = CD.ID "
            + " WHERE DESP.EXERCICIO_ID = :exercicio "
            + " AND PPD.UNIDADEORGANIZACIONAL_ID = :unidade "
            + " AND (REPLACE(C.CODIGO, '.', '') LIKE :codigo OR LOWER(C.DESCRICAO) LIKE :descricao "
            + " OR REPLACE(DESP.CODIGO, '.', '') LIKE :codigo OR REPLACE(DESP.CODIGOREDUZIDO, '.', '') LIKE :codigo) "
            + " and c.id in ( "
            + " SELECT DISTINCT C.SUPERIOR_ID FROM PLANODECONTASEXERCICIO PCE "
            + " INNER JOIN PLANODECONTAS PC ON PCE.PLANODEDESPESAS_ID = PC.ID "
            + " INNER JOIN CONTA C ON PC.ID = C.PLANODECONTAS_ID "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " WHERE PCE.EXERCICIO_ID = :exercicio "
            + " AND CD.TIPOCONTADESPESA = :tipoConta) ";
        Query consulta = em.createNativeQuery(sql, DespesaORC.class);
        consulta.setParameter("codigo", "%" + parte.trim().replace(".", "") + "%");
        consulta.setParameter("descricao", parte.toLowerCase());
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("tipoConta", tipoContaDespesa.name());
        consulta.setMaxResults(10);
        if (consulta.getResultList().isEmpty()) {
            return new ArrayList<DespesaORC>();
        } else {
            return (ArrayList<DespesaORC>) consulta.getResultList();
        }
    }

    public List<DespesaORC> buscarDespesasOrcPorExercicioAndUnidade(String filtro, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional, AcaoPPA acaoPPA) {
        String sql = "SELECT DESP.* FROM DESPESAORC DESP "
            + " INNER JOIN PROVISAOPPADESPESA PPD ON PPD.ID = DESP.PROVISAOPPADESPESA_ID "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = PPD.CONTADEDESPESA_ID "
            + " INNER JOIN SUBACAOPPA SUB ON PPD.SUBACAOPPA_ID = SUB.id "
            + " INNER JOIN ACAOPPA ACAO ON SUB.ACAOPPA_ID = ACAO.id "
            + " INNER JOIN CONTA C ON C.ID = CD.ID "
            + " WHERE DESP.EXERCICIO_ID = :exercicio "
            + " AND PPD.UNIDADEORGANIZACIONAL_ID = :unidade "
            + " AND (REPLACE(C.CODIGO, '.', '') LIKE :codigo OR LOWER(C.DESCRICAO) LIKE :descricao "
            + " OR REPLACE(DESP.CODIGO, '.', '') LIKE :codigo OR REPLACE(DESP.CODIGOREDUZIDO, '.', '') LIKE :codigo) "
            + " and c.id in ( "
            + " SELECT DISTINCT C.SUPERIOR_ID FROM PLANODECONTASEXERCICIO PCE "
            + " INNER JOIN PLANODECONTAS PC ON PCE.PLANODEDESPESAS_ID = PC.ID "
            + " INNER JOIN CONTA C ON PC.ID = C.PLANODECONTAS_ID "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " WHERE PCE.EXERCICIO_ID = :exercicio ) ";
        if (acaoPPA != null) {
            sql += " AND ACAO.ID = :acaoPPA ";
        }
        Query consulta = em.createNativeQuery(sql, DespesaORC.class);
        consulta.setParameter("codigo", "%" + filtro.trim().replace(".", "") + "%");
        consulta.setParameter("descricao", "%" + filtro.toLowerCase() + "%");
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        if (acaoPPA != null) {
            consulta.setParameter("acaoPPA", acaoPPA.getId());
        }
        consulta.setMaxResults(10);
        if (consulta.getResultList().isEmpty()) {
            return new ArrayList<DespesaORC>();
        } else {
            return (ArrayList<DespesaORC>) consulta.getResultList();
        }
    }

    public List<DespesaORC> buscarTodasDespesasOrcPorExercicioAndUnidadeComMovimentacao(Exercicio exercicio,
                                                                                        HierarquiaOrganizacional hierarquiaOrganizacional,
                                                                                        Date dataInicial,
                                                                                        Date dataFinal, Boolean buscarMovimentacao) {
        String sql = buscarMovimentacao ? "SELECT distinct DESP.* FROM DESPESAORC DESP "
            + " INNER JOIN PROVISAOPPADESPESA PPD ON PPD.ID = DESP.PROVISAOPPADESPESA_ID "
            + " INNER JOIN FONTEDESPESAORC FD ON DESP.ID = FD.DESPESAORC_ID "
            + " INNER JOIN MOVIMENTODESPESAORC MOV on FD.ID = MOV.FONTEDESPESAORC_ID "
            + " WHERE DESP.EXERCICIO_ID = :exercicio "
            + " and trunc(mov.DATAMOVIMENTO) between to_date(:DATAINICIAL,'dd/MM/yyyy') and to_date(:DATAFINAL,'dd/MM/yyyy')"
            :
            "SELECT distinct DESP.* FROM DESPESAORC DESP "
                + " INNER JOIN PROVISAOPPADESPESA PPD ON PPD.ID = DESP.PROVISAOPPADESPESA_ID "
                + " WHERE DESP.EXERCICIO_ID = :exercicio ";
        if (hierarquiaOrganizacional != null) {
            sql += " AND PPD.UNIDADEORGANIZACIONAL_ID = :unidade ";
        }
        Query consulta = em.createNativeQuery(sql, DespesaORC.class);
        consulta.setParameter("exercicio", exercicio.getId());
        if(buscarMovimentacao) {
            consulta.setParameter("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
            consulta.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        }
        if (hierarquiaOrganizacional != null) {
            consulta.setParameter("unidade", hierarquiaOrganizacional.getSubordinada().getId());
        }
        if (consulta.getResultList().isEmpty()) {
            return Lists.newArrayList();
        } else {
            List<DespesaORC> resultList = consulta.getResultList();
            for (DespesaORC despesaORC : resultList) {
                Hibernate.initialize(despesaORC.getFonteDespesaORCs());
            }
            return resultList;
        }
    }
}
