package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CadastroRural;
import br.com.webpublico.negocios.tributario.rowmapper.CadastroRuralRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository(value = "cadastroRuralDAO")
public class JdbcCadastroRuralDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcCadastroRuralDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<CadastroRural> lista(String filtro) {
        return lista(filtro, true);
    }

    public List<CadastroRural> lista(String filtro, Boolean ativo) {
        String sql = " select * from(select distinct cr.id, " +
            "       cr.codigo, " +
            "       cr.localizacaolote, " +
            "       cr.nomepropriedade, " +
            "       cr.numeroincra " +
            "   from cadastrorural cr " +
            "  left join propriedade cr_prop on cr_prop.imovel_id = cr.id " +
            "  left join pessoa p on cr_prop.pessoa_id = p.id " +
            "  left join pessoafisica pf on p.id = pf.id " +
            "  left join pessoajuridica pj on pj.id = p.id " +
            "where (cr_prop.id is null or cr_prop.finalvigencia is null or trunc(cr_prop.finalvigencia) <= sysdate) ";
        if (ativo != null) {
            sql += " and coalesce(cr.ativo, 1) = " + (ativo ? "1" : "0");
        }

        sql += " and (lower(cr.nomePropriedade) like '%" + filtro + "%' " +
            "  or lower(cr.localizacaoLote) like '%" + filtro + "%' " +
            "  or lower(cr.numeroincra) like '%" + filtro + "%' " +
            "  or cr.codigo like '%" + filtro + "%' " +
            "  or lower(coalesce(pf.nome, pj.razaosocial)) like '%" + filtro + "%' " +
            "  or lower(coalesce(pf.cpf, pj.cnpj)) like '%" + filtro + "%')) cadastrorural " +
            " where rownum <= 10 ";

        List<CadastroRural> lista = getJdbcTemplate().query(sql, new Object[]{}, new CadastroRuralRowMapper());
        return lista;
    }
}
