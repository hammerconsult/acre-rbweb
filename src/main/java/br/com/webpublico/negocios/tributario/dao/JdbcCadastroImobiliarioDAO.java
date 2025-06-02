package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.Testada;
import br.com.webpublico.negocios.tributario.rowmapper.CadastroImobiliarioRowMapper;
import br.com.webpublico.negocios.tributario.rowmapper.TestadaRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository(value = "cadastroImobiliarioJDBCDAO")
public class JdbcCadastroImobiliarioDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    public JdbcCadastroImobiliarioDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<CadastroImobiliario> lista(String filtro, Boolean somenteAtivos) {
        return lista(filtro, somenteAtivos, 10);
    }

    public List<CadastroImobiliario> lista(String filtro, Boolean ativo, Integer maximoResultados) {
        String sql = " select distinct ci.id," +
            "             ci.codigo," +
            "             ci.inscricaoCadastral," +
            "             ci.lote_id," +
            "             ci.areaTotalConstruida," +
            "             ci.ativo," +
            "             (select prop.id " +
            "                from Propriedade prop " +
            "              where prop.imovel_id = ci.id " +
            "                and prop.proporcao = (select max(s_prop.proporcao) from Propriedade s_prop where s_prop.imovel_id = prop.imovel_id) " +
            "                and (prop.finalVigencia is null or trunc(prop.finalVigencia) <= sysdate) " +
            "                and rownum = 1) pessoa_id," +
            "              coalesce(ci.autonoma,1) as autonoma " +
            "        from CadastroImobiliario ci " +
            "       where lower(ci.inscricaoCadastral) like '%" + filtro.toLowerCase().trim() + "%'";
        if (ativo != null)
            sql += " and coalesce(ci.ativo, 1) = " + (ativo ? "1" : "0");
        if (maximoResultados != null) {
            sql += " and rownum <= " + maximoResultados;
        }
        List<CadastroImobiliario> lista = getJdbcTemplate().query(sql, new Object[]{}, new CadastroImobiliarioRowMapper());
        return lista;
    }

    public Testada recuperaTestadaPrincipalLote(Lote lote) {
        String sql = "select testada.id testada_id,\n" +
            "       face.id face_id,\n" +
            "       logradourobairro.id logradourobairro_id,\n" +
            "       logradourobairro.cep logradourobairro_cep,\n" +
            "       bairro.id bairro_id,\n" +
            "       bairro.descricao bairro_descricao,\n" +
            "       tipologradouro.id tipologradouro_id,\n" +
            "       tipologradouro.descricao tipologradouro_descricao,\n" +
            "       logradouro.id logradouro_id,\n" +
            "       logradouro.nome logradouro_nome\n" +
            "   from lote \n" +
            "  inner join testada on testada.lote_id = lote.id\n" +
            "  inner join face on testada.face_id = face.id\n" +
            "  inner join logradourobairro on face.logradourobairro_id = logradourobairro.id\n" +
            "  inner join bairro on logradourobairro.bairro_id = bairro.id \n" +
            "  inner join logradouro on logradourobairro.logradouro_id = logradouro.id\n" +
            "  inner join tipologradouro on logradouro.tipologradouro_id = tipologradouro.id\n" +
            "where testada.principal = 1 and lote.id = " + lote.getId();
        List<Testada> lista = getJdbcTemplate().query(sql, new Object[]{}, new TestadaRowMapper());
        if (lista != null && lista.size() > 0) {
            return lista.get(0);
        }
        return new Testada();
    }

    public void executarSql(String sql) {
        getJdbcTemplate().execute(sql);
    }
}
