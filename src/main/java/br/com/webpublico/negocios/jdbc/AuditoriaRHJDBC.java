package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidadesauxiliares.rh.auditoria.AssistenteAuditoriaRH;
import br.com.webpublico.entidadesauxiliares.rh.auditoria.ObjetoAuditoriaRH;
import br.com.webpublico.enums.TipoRevisaoAuditoria;
import br.com.webpublico.enums.rh.TipoAuditoriaRH;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.ColunaAuditoriaRH;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository(value = "auditoraRhJDBC")
public class AuditoriaRHJDBC extends JdbcDaoSupport implements Serializable {

    @PersistenceContext
    protected transient EntityManager em;

    @Autowired
    public AuditoriaRHJDBC(DataSource ds) {
        setDataSource(ds);
    }

    public List<TreeMap<ObjetoAuditoriaRH, Object>> buscarAuditorias(final AssistenteAuditoriaRH assistente) {
        final Class classe = assistente.getTipoAuditoriaRH().getClasse();
        final Map<Long, Object> mapaEntidade = Maps.newHashMap();

        String tabela = Util.getNomeTabela(classe);
        String sql = montarConsulta(assistente, classe, tabela);
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameter = new MapSqlParameterSource();
        List<Long> tipos = Lists.newArrayList();
        montarNamedParameters(assistente, parameter, tipos);

        return named.query(sql, parameter, new ResultSetExtractor<List<TreeMap<ObjetoAuditoriaRH, Object>>>() {
            @Override
            public List<TreeMap<ObjetoAuditoriaRH, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<TreeMap<ObjetoAuditoriaRH, Object>> lista = Lists.newArrayList();

                while (rs.next()) {
                    TreeMap<ObjetoAuditoriaRH, Object> mapa = Maps.newTreeMap();
                    adicionarColunasComuns(rs, mapa, assistente);
                    adicionarColunasEspecificas(rs, mapa, classe);

                    lista.add(mapa);
                }
                return lista;
            }

            private void adicionarColunasEspecificas(ResultSet rs, TreeMap<ObjetoAuditoriaRH, Object> mapa, Class<?> classe) throws SQLException {
                for (Field atributo : classe.getDeclaredFields()) {
                    atributo.setAccessible(true);
                    if (atributo.isAnnotationPresent(ColunaAuditoriaRH.class)) {
                        ObjetoAuditoriaRH key = new ObjetoAuditoriaRH(Persistencia.getNomeDoCampo(atributo), (atributo.getAnnotation(ColunaAuditoriaRH.class).posicao() + 5));
                        Object objeto;
                        String nomeAtributo = atributo.getName();
                        if (atributo.getType().isAnnotationPresent(Entity.class)) {
                            nomeAtributo = atributo.getName() + "_id";
                        }

                        if (rs.getObject(nomeAtributo) != null) {
                            objeto = preencherObjeto(rs, atributo, nomeAtributo, mapaEntidade);
                            formatarObjeto(mapa, atributo, key, objeto);
                        }
                    }
                }
            }
        });
    }

    private void montarNamedParameters(AssistenteAuditoriaRH assistente, MapSqlParameterSource parameter, List<Long> tipos) {
        tipos.add(assistente.getTipoRevisao().getTipo());
        parameter.addValue("tipos", Lists.newArrayList(tipos));

        if (assistente.getVinculoFP() != null && assistente.getIdAuditoria() == null) {
            parameter.addValue("matricula", assistente.getVinculoFP().getMatriculaFP().getMatricula());
        }
        parameter.addValue("idAuditoria", assistente.getIdAuditoria());


        List<Long> valores = Lists.newArrayList();
        if (assistente.getIdAuditoria() != null) {
            for (TipoRevisaoAuditoria value : TipoRevisaoAuditoria.values()) {
                valores.add(value.getTipo());
            }
        } else {
            valores.add(assistente.getTipoRevisao().getTipo());
        }

        parameter.addValue("subTipos", Lists.newArrayList(valores));

        if (assistente.getUsuarioSistema() != null) {
            if (assistente.getTipoAuditoriaRH().getNomeColunaUsuario() != null && !assistente.getTipoRevisao().equals(TipoRevisaoAuditoria.EXCLUSAO)) {
                parameter.addValue("loginUsuario", assistente.getUsuarioSistema().getId());
            } else {
                parameter.addValue("loginUsuario", assistente.getUsuarioSistema().getLogin());
            }
        }
        if (assistente.getDataInicial() != null) {
            parameter.addValue("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        }
        if (assistente.getDataFinal() != null) {
            parameter.addValue("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        }
    }

    private void adicionarColunasComuns(ResultSet resultSet, Map<ObjetoAuditoriaRH, Object> mapa, AssistenteAuditoriaRH assistente) throws SQLException {
        mapa.put(new ObjetoAuditoriaRH("Id Auditoria", 1), resultSet.getObject(1));
        mapa.put(new ObjetoAuditoriaRH("Id Revisão", 2), resultSet.getObject(2));

        if (assistente.getTipoAuditoriaRH().getNomeColunaUsuario() == null) {
            mapa.put(new ObjetoAuditoriaRH("Usuário", 3), resultSet.getObject(3));
        } else {
            mapa.put(new ObjetoAuditoriaRH("Usuário", 3), resultSet.getObject(6) != null ? resultSet.getObject(6) : (resultSet.getObject(3) != null && assistente.getTipoRevisao().equals(TipoRevisaoAuditoria.EXCLUSAO) ? resultSet.getObject(3) : "WebPúblico (Integração de Dados)"));
        }
        Timestamp timestamp = Timestamp.valueOf(resultSet.getObject(4).toString());
        mapa.put(new ObjetoAuditoriaRH("Data Revisão", 4), DataUtil.getDataFormatadaDiaHora(timestamp));
        for (TipoRevisaoAuditoria tipo : TipoRevisaoAuditoria.values()) {
            if (resultSet.getObject(5).equals(new BigDecimal(tipo.getTipo()))) {
                mapa.put(new ObjetoAuditoriaRH("Tipo Revisão", 5), tipo.getDescricao());
            }
        }
        if (!TipoRevisaoAuditoria.EXCLUSAO.equals(assistente.getTipoRevisao()) && TipoAuditoriaRH.ENQUADRAMENTO_FUNCIONAL.equals(assistente.getTipoAuditoriaRH())) {
            Integer posicao = assistente.getTipoAuditoriaRH().getNomeColunaUsuario() == null ? 6 : 7;
            mapa.put(new ObjetoAuditoriaRH("Plano de Cargos", 9), resultSet.getObject(posicao));
            mapa.put(new ObjetoAuditoriaRH("Categoria", 10), resultSet.getObject(posicao + 1));
            mapa.put(new ObjetoAuditoriaRH("Progressão", 11), resultSet.getObject(posicao + 2));
            mapa.put(new ObjetoAuditoriaRH("Tipo Provimento", 13), resultSet.getObject(posicao + 3));
            BigDecimal valor = (BigDecimal) resultSet.getObject(posicao + 4);
            mapa.put(new ObjetoAuditoriaRH("Valor", 14), valor != null ? "R$ " + Util.reaisToString(valor) : "");
        }
    }

    private Object preencherObjeto(ResultSet resultSet, Field atributo, String nomeAtributo, Map<Long, Object> mapaEntidade) throws SQLException {
        Object objeto;
        if (atributo.getType().isAnnotationPresent(Entity.class)) {
            objeto = resultSet.getObject(nomeAtributo) != null ?
                buscarEntidade(atributo.getType(), ((BigDecimal) resultSet.getObject(nomeAtributo)).longValue(), mapaEntidade) : "";
        } else {
            objeto = resultSet.getObject(nomeAtributo) != null ? resultSet.getObject(nomeAtributo) : "";
        }
        return objeto;
    }

    private void formatarObjeto(Map<ObjetoAuditoriaRH, Object> mapa, Field atributo, ObjetoAuditoriaRH key, Object objeto) {
        if (!Strings.isNullOrEmpty(objeto.toString().trim())) {
            String valorFormatado;
            valorFormatado = formatarValores(objeto, atributo);
            mapa.put(key, valorFormatado);
        }
    }

    private String formatarValores(Object valor, Field atributo) {
        try {
            if (valor == null) {
                return "";
            }
            if (atributo.getType().equals(Date.class)) {
                Temporal t = atributo.getAnnotation(Temporal.class);
                if (t.value() == TemporalType.TIME) {
                    return DataUtil.getDataFormatadaDiaHora((Date) valor);
                }
                if (t.value() == TemporalType.DATE) {
                    return DataUtil.getDataFormatada((Date) valor);
                }
            }
            if (atributo.getType().equals(BigDecimal.class)) {
                if (atributo.getAnnotation(ColunaAuditoriaRH.class).monetario()) {
                    return Util.formataValorSemCifrao((BigDecimal) valor);
                }
            }
            if (atributo.isAnnotationPresent(Enumerated.class)) {
                String nomeDaClasse = atributo.getType().toString();
                nomeDaClasse = nomeDaClasse.replace("class ", "");
                Class<?> classe = Class.forName(nomeDaClasse);
                return Enum.valueOf((Class<? extends Enum>) classe, valor.toString()).toString();
            }
        } catch (Exception ex) {
            logger.error("erro ao formatar atributos", ex);
        }
        return valor.toString();
    }

    private Object buscarEntidade(Class classe, Long id, Map<Long, Object> mapaEntidade) {
        try {
            if (!mapaEntidade.containsKey(id)) {
                mapaEntidade.put(id, em.find(classe, id).toString());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar referência do objeto: ", e);
        }
        return mapaEntidade.get(id) != null ? mapaEntidade.get(id) : "";
    }

    private String montarConsulta(AssistenteAuditoriaRH assistente, Class classe, String tabela) {
        String sql = " select distinct auditoria.id,         " +
            "                          revisao.id,           " +
            "                          revisao.usuario,      " +
            "                          revisao.datahora,     " +
            "                          auditoria.revtype,    ";
        if (assistente.getTipoAuditoriaRH().getNomeColunaUsuario() != null) {
            sql += " usuario.login, ";
        }
        if (!TipoRevisaoAuditoria.EXCLUSAO.equals(assistente.getTipoRevisao()) && TipoAuditoriaRH.ENQUADRAMENTO_FUNCIONAL.equals(assistente.getTipoAuditoriaRH())) {
            sql += "plano.DESCRICAO," +
                " catPai.DESCRICAO, " +
                " progPai.DESCRICAO, " +
                " tipo.DESCRICAO, " +
                " (select max(enqPCS.VENCIMENTOBASE) " +
                "                 from EnquadramentoPCS enqPCS " +
                "                 where enqPCS.PROGRESSAOPCS_ID = prog.id " +
                "                   and enqPCS.CATEGORIAPCS_ID = cat.id " +
                "                   and auditoria.INICIOVIGENCIA between enqPCS.INICIOVIGENCIA and coalesce(enqPCS.FINALVIGENCIA, auditoria.INICIOVIGENCIA)),";
        }
        sql += montarParametros(classe) + " from       " +
            tabela + "_aud auditoria" +
            " inner join revisaoauditoria revisao on auditoria.rev = revisao.id ";

        if (!TipoRevisaoAuditoria.EXCLUSAO.equals(assistente.getTipoRevisao()) && (TipoAuditoriaRH.CONCESSAO_FERIAS.equals(assistente.getTipoAuditoriaRH())
            || TipoAuditoriaRH.CONCESSAO_LICENCA.equals(assistente.getTipoAuditoriaRH()))) {
            sql += "  inner join PERIODOAQUISITIVOFL pa on auditoria.PERIODOAQUISITIVOFL_ID = pa.ID " +
                "     inner join BASECARGO base on pa.BASECARGO_ID = base.id " +
                "     inner join BASEPERIODOAQUISITIVO basepa on base.BASEPERIODOAQUISITIVO_ID = basepa.ID ";
        }
        if (!TipoRevisaoAuditoria.EXCLUSAO.equals(assistente.getTipoRevisao()) && TipoAuditoriaRH.ENQUADRAMENTO_FUNCIONAL.equals(assistente.getTipoAuditoriaRH())) {
            sql += " inner join categoriaPCS cat on auditoria.CATEGORIAPCS_ID = cat.ID " +
                " inner join planoCargosSalarios plano on cat.PLANOCARGOSSALARIOS_ID = plano.ID " +
                " inner join categoriaPCS catPai on cat.SUPERIOR_ID = catPai.id " +
                " inner join progressaoPCS prog on auditoria.PROGRESSAOPCS_ID = prog.ID " +
                " inner join progressaoPCS progPai on prog.SUPERIOR_ID = progPai.id " +
                " left join provimentoFP prov on auditoria.PROVIMENTOFP_ID = prov.ID " +
                " left join tipoProvimento tipo on prov.TIPOPROVIMENTO_ID = tipo.ID ";
        }

        if (assistente.getTipoAuditoriaRH().getNomeColunaUsuario() != null) {
            sql += " left join usuariosistema usuario on auditoria." + assistente.getTipoAuditoriaRH().getNomeColunaUsuario() + " = usuario.id";
        }
        sql += " where auditoria.id in ( " +
            " select aud.id from " + tabela + "_aud aud " +
            montarInner(assistente);

        sql += "where aud.revtype in (:tipos) ";

        if (TipoRevisaoAuditoria.EXCLUSAO.equals(assistente.getTipoRevisao())) {
            sql += " or (aud.revtype in (0, 1) and rev.DATAHORA = " +
                " (select max(rev2.dataHora) from " + tabela + "_aud aud2 inner join revisaoauditoria rev2 on rev2.id = aud2.rev " +
                "  where aud2.id = aud.id and aud2.revtype in (0, 1) ))";
        }

        if (assistente.getVinculoFP() != null && assistente.getIdAuditoria() == null) {
            sql += " and mfp.matricula = :matricula ";
        }
        if (assistente.getIdAuditoria() != null) {
            sql += " and aud.id = :idAuditoria ";
        }
        if (TipoAuditoriaRH.CONCESSAO_FERIAS.equals(assistente.getTipoAuditoriaRH())) {
            sql += " and basepa.TIPOPERIODOAQUISITIVO = 'FERIAS'";
        }
        if (TipoAuditoriaRH.CONCESSAO_LICENCA.equals(assistente.getTipoAuditoriaRH())) {
            sql += " and basepa.TIPOPERIODOAQUISITIVO = 'LICENCA'";
        }
        sql += ")";
        if (assistente.getTipoRevisao() != null) {
            sql += " and auditoria.revtype in (:subTipos) ";
        }

        sql += montarCondicoes(assistente) + " order by revisao.datahora ";

        return sql;
    }

    private String montarParametros(Class classe) {
        StringBuilder sql = new StringBuilder();
        List<Field> atributos = Persistencia.getAtributos(classe);
        String juncao = "";
        for (Field atributo : atributos) {
            if (atributo.isAnnotationPresent(ColunaAuditoriaRH.class)) {
                String nomeDoCampo = atributo.getType().isAnnotationPresent(Entity.class) ? atributo.getName() + "_id" : atributo.getName();
                sql.append(juncao);
                sql.append(" auditoria.").append(nomeDoCampo);
                juncao = ", ";
            }
        }
        return sql.toString();
    }

    private String montarInner(AssistenteAuditoriaRH assistente) {
        String sql = " inner join revisaoauditoria rev on rev.id = aud.rev ";
        if (assistente.getTipoAuditoriaRH().getHasJoinVinculo()) {
            if (assistente.getTipoAuditoriaRH().getHasVinculoOutraTabela()) {
                sql += "  inner join " + assistente.getTipoAuditoriaRH().getNomeOutraTabela() + " outraTabela" +
                    "     on  " + assistente.getTipoAuditoriaRH().getJoinVinculoOutraTabela() + " = outratabela.id " +
                    " inner join vinculofp vfp on vfp.id = outraTabela." + assistente.getTipoAuditoriaRH().getNomeColunaVinculo() +
                    " inner join matriculafp mfp on vfp.matriculafp_id = mfp.id ";
                if (TipoAuditoriaRH.CONCESSAO_FERIAS.equals(assistente.getTipoAuditoriaRH()) || TipoAuditoriaRH.CONCESSAO_LICENCA.equals(assistente.getTipoAuditoriaRH())) {
                    sql += " inner join BASECARGO base on outraTabela.BASECARGO_ID = base.id " +
                        "    inner join BASEPERIODOAQUISITIVO basepa on base.BASEPERIODOAQUISITIVO_ID = basepa.ID ";
                }
            } else {
                sql += " inner join vinculofp vfp on vfp.id = aud." + assistente.getTipoAuditoriaRH().getNomeColunaVinculo() +
                    " inner join matriculafp mfp on vfp.matriculafp_id = mfp.id ";
            }
        }
        return sql;
    }

    private String montarCondicoes(AssistenteAuditoriaRH assistente) {
        String sql = "";
        if (assistente.getUsuarioSistema() != null && assistente.getIdAuditoria() == null) {
            if (assistente.getTipoAuditoriaRH().getNomeColunaUsuario() != null && !assistente.getTipoRevisao().equals(TipoRevisaoAuditoria.EXCLUSAO)) {
                sql = " and auditoria." + assistente.getTipoAuditoriaRH().getNomeColunaUsuario() + " = :loginUsuario ";
            } else {
                sql = " and revisao.usuario = :loginUsuario ";
            }
        }
        if (assistente.getDataInicial() != null && assistente.getIdAuditoria() == null) {
            sql += " and trunc(revisao.datahora) >= to_date(:dataInicial, 'dd/MM/yyyy') ";
        }
        if (assistente.getDataFinal() != null && assistente.getIdAuditoria() == null) {
            sql += " and trunc(revisao.datahora) <= to_date(:dataFinal, 'dd/MM/yyyy') ";
        }
        return sql;
    }
}
