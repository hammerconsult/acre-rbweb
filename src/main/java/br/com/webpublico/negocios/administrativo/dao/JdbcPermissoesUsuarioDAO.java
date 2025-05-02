package br.com.webpublico.negocios.administrativo.dao;

import br.com.webpublico.entidadesauxiliares.VOUsuarioUnidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository("permissoesUsuarioDAO")
public class JdbcPermissoesUsuarioDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPermissoesUsuarioDAO(DataSource ds) {
        setDataSource(ds);
    }

    public List<VOUsuarioUnidade> buscarUsuariosUnidade(Date dataOperacao, Long idGrupo, Long idUsuario) {
        String sql = " select uu.id as id, uu.unidadeorganizacional_id as idunidade, uu.usuariosistema_id as idusuario, uu.grupousuario_id as idgrupo, " +
            "                 uu.gestorprotocolo as gestorprotocolo, uu.gestormateriais as gestormateriais, uu.gestorlicitacao as gestorlicitacao, " +
            "                 uu.gestorpatrimonio as gestorpatrimonio, uu.gestororcamento as gestororcamento, uu.gestorfinanceiro as gestorfinanceiro, " +
            "                 ho.id as idho, ho.subordinada_id as subirdinadaid, ho.codigo as codigoho, ho.descricao as descricaoho " +
            " from usuariounidadeorganizacio uu " +
            " inner join hierarquiaorganizacional ho on uu.unidadeorganizacional_id = ho.subordinada_id" +
            " and (ho.tipohierarquiaorganizacional = :tipoHo or ho.tipohierarquiaorganizacional is null) " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia  and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            " where uu.grupousuario_id = :idGrupoUsuario " +
            " and uu.usuariosistema_id = :idUsuario " +
            " order by ho.codigo ";

        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameter = new MapSqlParameterSource();

        parameter.addValue("tipoHo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        parameter.addValue("idGrupoUsuario", idGrupo);
        parameter.addValue("idUsuario", idUsuario);
        parameter.addValue("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

        return named.query(sql, parameter, new ResultSetExtractor<List<VOUsuarioUnidade>>() {
            @Override
            public List<VOUsuarioUnidade> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<VOUsuarioUnidade> usuarioUnidades = Lists.newArrayList();
                while (rs.next()) {
                    VOUsuarioUnidade usuarioUnidade = new VOUsuarioUnidade();
                    usuarioUnidade.setId(rs.getLong("id"));
                    usuarioUnidade.setIdUnidade(rs.getLong("idunidade"));
                    usuarioUnidade.setIdUsuario(rs.getLong("idusuario"));
                    usuarioUnidade.setIdGrupo(rs.getLong("idgrupo"));
                    usuarioUnidade.setGestorProtocolo(rs.getBoolean("gestorprotocolo"));
                    usuarioUnidade.setGestorMateriais(rs.getBoolean("gestormateriais"));
                    usuarioUnidade.setGestorLicitacao(rs.getBoolean("gestorlicitacao"));
                    usuarioUnidade.setGestorPatrimonio(rs.getBoolean("gestorpatrimonio"));
                    usuarioUnidade.setGestorOrcamento(rs.getBoolean("gestororcamento"));
                    usuarioUnidade.setGestorFinanceiro(rs.getBoolean("gestorfinanceiro"));
                    usuarioUnidade.setIdHo(rs.getLong("idho"));
                    usuarioUnidade.setSubordinadaId(rs.getLong("subirdinadaid"));
                    usuarioUnidade.setCodigoHo(rs.getString("codigoho"));
                    usuarioUnidade.setDescricaoHo(rs.getString("descricaoho"));

                    usuarioUnidades.add(usuarioUnidade);
                }
                return usuarioUnidades;
            }
        });
    }

    public List<VOUsuarioUnidade> buscarOrcamentariasUsuario(Date dataOperacao, Long idUsuario) {
        String sql = " select orc.id as id, orc.unidadeorganizacional_id as idunidade, orc.usuariosistema_id as idusuario, " +
            " ho.id as idho, ho.subordinada_id as subirdinadaid, ho.codigo as codigoho, ho.descricao as descricaoho " +
            " from usuariounidadeorganizacorc orc " +
            " inner join hierarquiaorganizacional ho on orc.unidadeorganizacional_id = ho.subordinada_id " +
            " where (ho.tipohierarquiaorganizacional = :tipoHo or ho.tipohierarquiaorganizacional is null) " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia  and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            " and orc.usuariosistema_id = :idUsuario" +
            " and ho.superior_id is not null " +
            " order by ho.codigo ";

        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameter = new MapSqlParameterSource();

        parameter.addValue("tipoHo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        parameter.addValue("idUsuario", idUsuario);
        parameter.addValue("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

        return named.query(sql, parameter, new ResultSetExtractor<List<VOUsuarioUnidade>>() {
            @Override
            public List<VOUsuarioUnidade> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<VOUsuarioUnidade> orcamentarias = Lists.newArrayList();
                while (rs.next()) {
                    VOUsuarioUnidade orcamentaria = new VOUsuarioUnidade();
                    orcamentaria.setId(rs.getLong("id"));
                    orcamentaria.setIdUnidade(rs.getLong("idunidade"));
                    orcamentaria.setIdUsuario(rs.getLong("idusuario"));
                    orcamentaria.setIdHo(rs.getLong("idho"));
                    orcamentaria.setSubordinadaId(rs.getLong("subirdinadaid"));
                    orcamentaria.setCodigoHo(rs.getString("codigoho"));
                    orcamentaria.setDescricaoHo(rs.getString("descricaoho"));

                    orcamentarias.add(orcamentaria);
                }
                return orcamentarias;
            }
        });
    }

    public List<VOUsuarioUnidade> buscarUnidadesDisponiveis(Date dataOperacao, TipoHierarquiaOrganizacional tipo) {
        String sql = "select ho.id as id, ho.subordinada_id as idsubordinada, ho.codigo as codigoho, ho.descricao as descricaoho" +
            " from hierarquiaorganizacional ho " +
            " inner join unidadeorganizacional uo on ho.subordinada_id = uo.id " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and (ho.tipohierarquiaorganizacional = :tipoHO " +
            " or ho.tipohierarquiaorganizacional is null) " +
            " and superior_id is not null " +
            " order by ho.codigo ";

        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameter = new MapSqlParameterSource();

        parameter.addValue("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        parameter.addValue("tipoHO", tipo.name());

        List<VOUsuarioUnidade> usuarioUnidades = preencherUnidades(sql, named, parameter);
        return usuarioUnidades != null ? usuarioUnidades : Lists.<VOUsuarioUnidade>newArrayList();
    }

    private List<VOUsuarioUnidade> preencherUnidades(String sql, NamedParameterJdbcTemplate named, MapSqlParameterSource parameter) {
        return named.query(sql, parameter, new ResultSetExtractor<List<VOUsuarioUnidade>>() {
            List<VOUsuarioUnidade> usuarioUnidades = Lists.newArrayList();

            @Override
            public List<VOUsuarioUnidade> extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    VOUsuarioUnidade usuarioUnidade = new VOUsuarioUnidade();
                    usuarioUnidade.setIdHo(rs.getLong("id"));
                    usuarioUnidade.setSubordinadaId(rs.getLong("idsubordinada"));
                    usuarioUnidade.setCodigoHo(rs.getString("codigoho"));
                    usuarioUnidade.setDescricaoHo(rs.getString("descricaoho"));

                    usuarioUnidades.add(usuarioUnidade);
                }
                return usuarioUnidades;
            }
        });
    }
}
