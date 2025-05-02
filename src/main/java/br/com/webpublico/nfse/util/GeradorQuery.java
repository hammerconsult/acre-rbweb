package br.com.webpublico.nfse.util;

import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.ParametroQuery;
import br.com.webpublico.nfse.domain.dtos.ParametroQueryCampo;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GeradorQuery {

    private EntityManager em;
    private Class classe;
    private String sqlPrincipal;
    private List<ParametroQuery> parametros;
    private String orderBy;
    private String andOr;

    public GeradorQuery(EntityManager em, Class classe, String sqlPrincipal) {
        this.em = em;
        this.classe = classe;
        this.sqlPrincipal = sqlPrincipal;
        this.parametros = Lists.newArrayList();
        this.orderBy = " ";
        this.andOr = " and ";
    }

    public GeradorQuery parametros(List<ParametroQuery> parametros) {
        this.parametros = parametros;
        return this;
    }

    public GeradorQuery orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public GeradorQuery andOr(String andOr) {
        this.andOr = andOr;
        return this;
    }

    public Query count() throws Exception {
        return build(true);
    }

    public Query build() throws Exception {
        return build(false);
    }

    public Query build(boolean count) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(sqlPrincipal);
        Map<String, Object> parameters = montarParametroString(sqlBuilder, parametros);
        String sql = "";
        if (count) {
            sql = " select count(1) as qtd from ( " + sqlBuilder.toString() + ") dados ";
        } else {
            if (orderBy != null) {
                sqlBuilder.append(" ").append(orderBy).toString();
            }
            sql = sqlBuilder.toString();
        }
        Query q = null;
        if (classe != null) {
            q = em.createNativeQuery(sql, classe);
        } else {
            q = em.createNativeQuery(sql);
        }
        adicionarParametrosNaQuery(parameters, q);
        return q;
    }

    public static void adicionarParametrosNaQuery(Map<String, Object> parameters, Query q) {
        for (String parameter : parameters.keySet()) {
            Object value = parameters.get(parameter);
            if (value instanceof Date) {
                value = DataUtil.dataSemHorario((Date) value);
            }
            q.setParameter(parameter, value);
        }
    }

    public static Map<String, Object> montarParametroString(StringBuilder sqlBuilder, List<ParametroQuery> parametros) throws Exception {
        String juncao = " where ";
        AtomicInteger index = new AtomicInteger();
        Map<String, Object> parameters = Maps.newHashMap();
        if (parametros != null && !parametros.isEmpty()) {
            sqlBuilder.append(juncao);
            juncao = " ";
            for (ParametroQuery parametro : parametros) {
                sqlBuilder.append(juncao).append(" ( ");

                String juncaoCampo = " ";
                for (ParametroQueryCampo parametroQueryCampo : parametro.getParametroQueryCampos()) {
                    if (!Strings.isNullOrEmpty(parametroQueryCampo.getLivre())) {
                        sqlBuilder.append(juncaoCampo).append(parametroQueryCampo.getLivre());
                        continue;
                    }

                    sqlBuilder.append(juncaoCampo).append(parametroQueryCampo.getCampo()).append(" ")
                        .append(parametroQueryCampo.getOperador().getOperador()).append(" ");

                    if (parametroQueryCampo.getValor() != null) {
                        parameters.put("index_" + index.get(), parametroQueryCampo.getValor());
                        if (Operador.IN.equals(parametroQueryCampo.getOperador())) {
                            sqlBuilder.append("(:index_" + index.getAndAdd(1) + ")");
                        } else {
                            sqlBuilder.append(":index_" + index.getAndAdd(1));
                        }
                    }

                    juncaoCampo = " " + parametro.getJuncao() + " ";
                }
                sqlBuilder.append(" ) ");
                juncao = " and ";
            }
        } else {
            throw new Exception("Nenhum parâmetro informado.");
        }
        return parameters;
    }
}
