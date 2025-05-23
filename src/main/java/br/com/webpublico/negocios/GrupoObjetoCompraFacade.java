/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Romanini
 */
@Stateless
public class GrupoObjetoCompraFacade extends AbstractFacade<GrupoObjetoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GrupoObjetoCompraFacade() {
        super(GrupoObjetoCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public String geraCodigoNovo() {
        String codigoRetorno;
        String hql = "from GrupoObjetoCompra a where a.codigo = (select max(b.codigo) from GrupoObjetoCompra b)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoObjetoCompra) q.getSingleResult()).getCodigo();
            String quebrado[] = codigoInteiro.split("\\.");
            int primeiro = Integer.parseInt(quebrado[0]);
            primeiro++;
            if (primeiro < 10) {
                codigoRetorno = "0" + String.valueOf(primeiro);
            } else {
                codigoRetorno = String.valueOf(primeiro);
            }

        } else {
            codigoRetorno = "01";
        }
        return codigoRetorno;
    }

    public String geraCodigoFilho(GrupoObjetoCompra grupoObjetoCompra) {
        String codigoRetorno = null;
        String codigoPai = grupoObjetoCompra.getCodigo();

        String hql = "from GrupoObjetoCompra a where a.codigo = (select max(b.codigo) from GrupoObjetoCompra b where b.superior = :superior)";
        Query q = em.createQuery(hql).setParameter("superior", grupoObjetoCompra);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoObjetoCompra) q.getSingleResult()).getCodigo();
            String quebrado[] = codigoInteiro.split("\\.");
            int ultimo = Integer.parseInt(quebrado[quebrado.length - 1]);
            ultimo++;

            if (ultimo < 10) {
                String temp = ".0000" + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            }
            if (ultimo < 100 && ultimo > 9) {
                String temp = ".000" + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            }
            if (ultimo < 1000 && ultimo > 99) {
                String temp = ".00" + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            }
            if (ultimo < 10000 && ultimo > 999) {
                String temp = ".0" + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            }
            if (ultimo < 100000 && ultimo > 9999) {
                String temp = "." + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            }

        } else {
            codigoRetorno = codigoPai + ".00001";
        }

        return codigoRetorno;
    }

    public List<GrupoObjetoCompra> getFilhosDe(GrupoObjetoCompra gb) {
        Query q = em.createQuery("from GrupoObjetoCompra g where g.superior = :superior");
        q.setParameter("superior", gb);
        return q.getResultList();
    }

    public List<ObjetoCompra> getObjetosDeCompra(GrupoObjetoCompra gb) {
        Query q = em.createQuery("from ObjetoCompra o where o.grupoObjetoCompra = :grupo");
        q.setParameter("grupo", gb);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> completaGruposAnalitico(String parte) {
        String hql = "select folha.* from grupoobjetocompra folha "
            + " where not exists (select 1 from grupoobjetocompra filho where filho.superior_id = folha.id)"
            + "    and (lower(folha.codigo) like :filtro "
            + "      OR lower(folha.descricao) like :filtro) ";

        Query q = getEntityManager().createNativeQuery(hql, GrupoObjetoCompra.class);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPermanentePorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "       SELECT filho.* " +
            "              FROM grupoobjetocompra filho " +
            "             WHERE filho.codigo like '1.%'" +
            "               AND TRIM(length(FILHO.CODIGO)) = 19 " +
            "               AND (lower(filho.descricao) like :codigoOrDescricao" +
            "                     OR filho.codigo like :codigoOrDescricao)";

        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPermanenteImovelPorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "       SELECT filho.* " +
            "              FROM grupoobjetocompra filho " +
            "             WHERE filho.codigo like '04.%'" +
            "               AND TRIM(length(FILHO.CODIGO)) = 20 " +
            "               AND (lower(filho.descricao) like :codigoOrDescricao" +
            "                     OR filho.codigo like :codigoOrDescricao)";

        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraConsumoPorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "       SELECT filho.* " +
            "              FROM grupoobjetocompra filho " +
            "             WHERE filho.codigo like '2.%'" +
            "               AND TRIM(length(FILHO.CODIGO)) = 19 " +
            "               AND (lower(filho.descricao) like :codigoOrDescricao" +
            "                     OR filho.codigo like :codigoOrDescricao)";

        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGruposAssociadoObjetoCompra(String parte, TipoObjetoCompra tipo) {
        String sql = "select distinct goc.* from grupoobjetocompra goc" +
            "          inner join objetocompra oc on oc.grupoobjetocompra_id = goc.id " +
            "        where (lower(goc.codigo) like :parte or lower(goc.descricao) like :parte) " +
            "         and goc.superior_id is not null ";
        sql += tipo != null ? " and oc.tipoobjetocompra = :tipoOc " : "";
        sql += "         order by goc.codigo ";
        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        if (tipo != null) {
            q.setParameter("tipoOc", tipo.name());
        }
        return q.getResultList();
    }


    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraSemAgrupador(String parte) {
        String sql = "select distinct * from ( " +
            "            select distinct goc.* from itemcontrato ic " +
            "               left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "               left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "               left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "               left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "               left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id " +
            "               left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id " +
            "               left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id " +
            "               left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id " +
            "               left join objetocompra obj on obj.id = its.objetocompra_id " +
            "               left join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id " +
            "               where not exists(select 1 from agrupadorgocgrupo gp where goc.id = gp.grupoobjetocompra_id) " +
            "              union all " +
            "            select distinct goc.* from itemcontrato ic " +
            "               inner join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id " +
            "               inner join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id " +
            "               inner join objetocompra obj on ise.objetocompra_id = obj.id " +
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id " +
            "               where not exists(select 1 from agrupadorgocgrupo gp where goc.id = gp.grupoobjetocompra_id) " +
            "          union all " +
            "            select distinct goc.* from itemcontrato ic " +
            "               inner join itemcontratovigente icv on ic.id = icv.itemcontrato_id " +
            "               inner join itemcotacao itcot on itcot.id = icv.itemcotacao_id " +
            "               inner join objetocompra obj on obj.id = itcot.objetocompra_id " +
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id " +
            "               where not exists(select 1 from agrupadorgocgrupo gp where goc.id = gp.grupoobjetocompra_id) " +
            "              )grupo ";
        sql += !Strings.isNullOrEmpty(parte) ? " where (lower(grupo.descricao) like :parte or grupo.codigo like :parte or replace(grupo.codigo, '.', '') like :parte) " : " ";
        sql += "           order by replace(replace(grupo.codigo, '.', ''), '04', '4') ";
        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        if (!Strings.isNullOrEmpty(parte)) {
            q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        }
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraServicoPorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "       SELECT filho.* " +
            "              FROM grupoobjetocompra filho " +
            "             WHERE filho.codigo like '3.%'" +
            "               AND TRIM(length(FILHO.CODIGO)) = 19 " +
            "               AND (lower(filho.descricao) like :codigoOrDescricao" +
            "                     OR filho.codigo like :codigoOrDescricao)";

        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public GrupoObjetoCompra getGrupoPermanenteNivelDois(GrupoObjetoCompra grupoFilho) {
        String sql = "WITH pai (ID, DESCRICAO, CODIGO, SUPERIOR_ID, NIVEL, IDNIVELDOIS) " +
            "  AS (SELECT grupopai.id, " +
            "             grupopai.descricao, " +
            "             grupopai.codigo, " +
            "             grupopai.superior_id, " +
            "             1 AS nivel, " +
            "             -1 " +
            "        FROM grupoobjetocompra grupopai " +
            "       WHERE grupopai.superior_id IS NULL " +
            "         AND grupopai.codigo = '1'" +
            "   UNION ALL " +
            "      SELECT grupofilho.id, " +
            "             grupofilho.descricao, " +
            "             grupofilho.codigo, " +
            "             grupofilho.superior_id, " +
            "             pai.nivel + 1 AS nivel, " +
            "             CASE " +
            "               WHEN pai.nivel = 2 THEN pai.id " +
            "               ELSE pai.idniveldois " +
            "             END " +
            "        FROM grupoobjetocompra grupofilho " +
            "  INNER JOIN pai ON pai.id = grupofilho.superior_id) " +
            "  select super.*  " +
            "  from pai sub  " +
            "  inner join grupoobjetocompra super on super.id = sub.idniveldois " +
            "  where sub.id = :filho_id";

        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("filho_id", grupoFilho.getId());

        try {
            return (GrupoObjetoCompra) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Object[]> recuperarGrupos(String grupoObjetoCompra, Integer nivelGrupoObjetoCompra) {
        String query = "with pai_com_nivel (ID, DESCRICAO, CODIGO, SUPERIOR_ID, NIVEL) " +
            "  as ( " +
            "      select grupo_pai.id, " +
            "             grupo_pai.descricao, " +
            "             grupo_pai.codigo, " +
            "             grupo_pai.superior_id, " +
            "             1 as nivel " +
            "        from grupoobjetocompra grupo_pai " +
            "       where grupo_pai.superior_id is null " +
            "       union all " +
            "      select grupo_filho.id, " +
            "             grupo_filho.descricao, " +
            "             grupo_filho.codigo, " +
            "             grupo_filho.superior_id, " +
            "             pai.nivel+1 as nivel " +
            "        from grupoobjetocompra grupo_filho " +
            "  inner join pai_com_nivel pai on pai.id = grupo_filho.superior_id " +
            "  ),  " +
            "  filho (ID, DESCRICAO, CODIGO, SUPERIOR_ID) " +
            "  as ( " +
            "      select grupo_filho.id, " +
            "             grupo_filho.descricao, " +
            "             grupo_filho.codigo, " +
            "             grupo_filho.superior_id " +
            "        from grupoobjetocompra grupo_filho " +
            "       where (grupo_filho.codigo = :grupo_objeto" +
            "              or lower(grupo_filho.descricao) = :grupo_objeto)" +
            "       union all " +
            "      select grupo_pai.id, " +
            "             grupo_pai.descricao, " +
            "             grupo_pai.codigo, " +
            "             grupo_pai.superior_id " +
            "        from grupoobjetocompra grupo_pai " +
            "  inner join filho f on grupo_pai.id = f.superior_id " +
            "  ) select grupo.id as id_grupo," +
            "           grupo.codigo as codigo_grupo, " +
            "           grupo.descricao as descricao_grupo, " +
            "           p.nivel as nivel_grupo " +
            "      from filho grupo " +
            "inner join pai_com_nivel p on grupo.id = p.id " +
            "     where p.nivel <= :nivel " +
            "  order by grupo.id";

        Query q = em.createNativeQuery(query);
        q.setParameter("grupo_objeto", grupoObjetoCompra.toLowerCase());
        q.setParameter("nivel", nivelGrupoObjetoCompra);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }

        return null;
    }

    public List<Object[]> recuperarObjetos(Long idDoGrupo) {
        String query = "with pai (ID, DESCRICAO, CODIGO, SUPERIOR_ID)  " +
            "                  as (  " +
            "                      select grupo_pai.id,  " +
            "                             grupo_pai.descricao,  " +
            "                             grupo_pai.codigo,  " +
            "                             grupo_pai.superior_id" +
            "                        from grupoobjetocompra grupo_pai  " +
            "                       where grupo_pai.id =  :id_grupo_objeto" +
            "                       union all  " +
            "                      select grupo_filho.id,  " +
            "                             grupo_filho.descricao,  " +
            "                             grupo_filho.codigo,  " +
            "                             grupo_filho.superior_id" +
            "                        from grupoobjetocompra grupo_filho  " +
            "                  inner join pai pai on pai.id = grupo_filho.superior_id)" +
            "                  select obj.codigo," +
            "                         obj.descricao" +
            "                  from objetocompra obj" +
            "                  inner join pai p on obj.grupoobjetocompra_id = p.id" +
            "                  order by 1";

        Query q = em.createNativeQuery(query);
        q.setParameter("id_grupo_objeto", idDoGrupo);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }

        return null;
    }

    public List<GrupoObjetoCompra> buscarFiltrandoGrupoObjetoCompraPorCodigoOrDescricao(String parte) {
        String sql = " " +
            "    SELECT grupo.* " +
            "      FROM grupoobjetocompra grupo " +
            "    WHERE ((LOWER(grupo.descricao) like :descricao) " +
            "              OR (grupo.codigo like :codigo) " +
            "              OR (replace(grupo.codigo,'.','') like :codigo )) ";
        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("descricao", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("codigo", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPorCodigoOrDescricaoNaoAgrupado(String parte) {
        String sql = " " +
            "    SELECT grupo.* " +
            "      FROM grupoobjetocompra grupo " +
            "    WHERE ((LOWER(grupo.descricao) like :descricao) " +
            "              OR (grupo.codigo like :codigo) " +
            "              OR (replace(grupo.codigo,'.','') like :codigo )) " +
            "   and not exists(select 1 from agrupadorgocgrupo gp where grupo.id = gp.grupoobjetocompra_id)";
        Query q = em.createNativeQuery(sql, GrupoObjetoCompra.class);
        q.setParameter("descricao", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("codigo", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrupoObjetoCompra> buscarFiltrandoGrupoObjetoCompraPorTipoObjetoCompra(String codigoOrDescricao, TipoObjetoCompra tipoObjetoCompra) {
        if (tipoObjetoCompra != null) {
            switch (tipoObjetoCompra) {
                case PERMANENTE_MOVEL:
                    return buscarGrupoObjetoCompraPermanentePorCodigoOrDescricao(codigoOrDescricao);
                case PERMANENTE_IMOVEL:
                    return buscarGrupoObjetoCompraPermanenteImovelPorCodigoOrDescricao(codigoOrDescricao);
                case CONSUMO:
                    return buscarGrupoObjetoCompraConsumoPorCodigoOrDescricao(codigoOrDescricao);
                case SERVICO:
                    return buscarGrupoObjetoCompraServicoPorCodigoOrDescricao(codigoOrDescricao);
            }
        }
        return new ArrayList<>();
    }

    public List<GrupoObjetoCompra> listaOrdenadaPorIndiceDoNo(String filtro, TipoObjetoCompra tipoObjetoCompra, Boolean buscarGrupoObjetoCompraNaoAgrupado) {
        String codigoPrefixo = "";

        if (tipoObjetoCompra != null) {
            switch (tipoObjetoCompra) {
                case CONSUMO:
                    codigoPrefixo = "2%";
                    break;
                case PERMANENTE_MOVEL:
                    codigoPrefixo = "1%";
                    break;
                case PERMANENTE_IMOVEL:
                    codigoPrefixo = "04%";
                    break;
                case SERVICO:
                    codigoPrefixo = "3%";
                    break;
                default:
                    codigoPrefixo = "2%";
                    break;
            }
        }

        List<String> listaGrupoObjetoCompraSuperiores = new ArrayList<>();

        if (filtro != null) {
            if (verificaNumeros(filtro)) {
                filtro = adicionarPonto(filtro, listaGrupoObjetoCompraSuperiores);
            } else if (verificaNumerosPontos(filtro)) {
                extrairGrupos(filtro, listaGrupoObjetoCompraSuperiores);
            } else {
                obterCodigosGrupoObjetoCompraFiltrandoPorDescricao(filtro, listaGrupoObjetoCompraSuperiores, codigoPrefixo, tipoObjetoCompra);
            }
        }

        String sql = " select grupo.* from grupoobjetocompra grupo ";

        if (tipoObjetoCompra != null) {
            sql += " where grupo.codigo like '" + codigoPrefixo + "' ";

            if (tipoObjetoCompra.isObraInstalacoes()) {
                sql += " and trim(length(grupo.codigo)) = 20 ";
            }
        }

        if (filtro != null && !filtro.trim().isEmpty()) {
            if (tipoObjetoCompra == null) {
                sql += " where (";

                if (!listaGrupoObjetoCompraSuperiores.isEmpty()) {
                    sql += gerarCondicaoInComOr("grupo.codigo", listaGrupoObjetoCompraSuperiores) + " or ";
                }

                sql += " grupo.codigo like :codigo) ";

                if (buscarGrupoObjetoCompraNaoAgrupado) {
                    sql += " and not exists(select 1 from agrupadorgocgrupo gp where grupo.id = gp.grupoobjetocompra_id) ";
                }
            } else {
                sql += " and (";

                if (!listaGrupoObjetoCompraSuperiores.isEmpty()) {
                    sql += gerarCondicaoInComOr("grupo.codigo", listaGrupoObjetoCompraSuperiores) + " or ";
                }

                sql += " grupo.codigo like :codigo) ";
            }
        } else {
            if (tipoObjetoCompra == null && buscarGrupoObjetoCompraNaoAgrupado) {
                sql += " where not exists(select 1 from agrupadorgocgrupo gp where grupo.id = gp.grupoobjetocompra_id) ";
            }
        }
        sql += " order by grupo.codigo asc ";

        Query q = getEntityManager().createNativeQuery(sql, GrupoObjetoCompra.class);

        if (filtro != null && !filtro.trim().isEmpty()) {
            q.setParameter("codigo", filtro + "%");
        }
        return q.getResultList();
    }


    private String gerarCondicaoInComOr(String nomeColuna, List<String> grupos) {
        String condicaoSql = "";
        List<List<String>> gruposParticionados = Lists.partition(grupos, 1000);

        for (int i = 0; i < gruposParticionados.size(); i++) {
            if (i > 0) {
                condicaoSql += " or ";
            }

            condicaoSql += nomeColuna + " in (";
            for (String value : gruposParticionados.get(i)) {
                condicaoSql += "'" + value + "',";
            }
            condicaoSql = condicaoSql.substring(0, condicaoSql.length() - 1) + ")";
        }
        return condicaoSql;
    }

    private void obterCodigosGrupoObjetoCompraFiltrandoPorDescricao(String filtro, List<String> listaGrupoObjetoCompraSuperiores, String codigoPrefixo, TipoObjetoCompra tipoObjetoCompra) {
        String sql = " select goc.codigo from grupoobjetocompra goc ";
            if(tipoObjetoCompra != null) {
                sql += " where goc.codigo like '" + codigoPrefixo + "'  and ";
            } else {
                sql += " where ";
            }
            sql += " upper(goc.descricao) like :descricao " +
                   " and trim(length(goc.codigo)) = :tamanhoCodigo ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("descricao",  "%" + filtro.trim().toUpperCase() + "%");
        if (tipoObjetoCompra.isObraInstalacoes()) {
            q.setParameter("tamanhoCodigo", 20);
        } else {
            q.setParameter("tamanhoCodigo", 19);
        }

        List<String> resultado = q.getResultList();

        if (!resultado.isEmpty()) {
            for (String obj : resultado) {
                extrairGrupos(obj, listaGrupoObjetoCompraSuperiores);
            }
        }
    }

    private static void extrairGrupos(String codigo, List<String> listaGrupoObjetoCompraSuperiores) {
        String[] partes = codigo.split("\\.");
        StringBuilder parteAtual = new StringBuilder();
        for (String parte : partes) {
            parteAtual.append(parte);
            listaGrupoObjetoCompraSuperiores.add(parteAtual.toString());
            if (!parteAtual.toString().equals(codigo)) {
                parteAtual.append(".");
            }
        }
    }

    public static String adicionarPonto(String texto, List<String> listaGrupoObjetoCompraSuperiores){
        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        resultado.append(texto.charAt(0));
        resultado.append(".");

        for(int i = 1; i<texto.length(); i++){
            resultado.append(texto.charAt(i));
            contador++;

            if(contador == 5){
                if(i != texto.length() - 1) {
                    resultado.append(".");
                    contador = 0;
                }
            }
        }
        extrairGrupos(resultado.toString(), listaGrupoObjetoCompraSuperiores);
        return resultado.toString();
    }

    public static boolean verificaNumerosPontos(String str) {
        Pattern pattern = Pattern.compile("^[0-9.]+$");
        return pattern.matcher(str).matches();
    }

    public static boolean verificaNumeros(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        return pattern.matcher(str).matches();
    }
}
