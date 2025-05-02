package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.EnquadramentoFiscal;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SituacaoCadastroEconomico;
import br.com.webpublico.negocios.tributario.rowmapper.CadastroEconomicoRowMapper;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository(value = "cadastroEconomicoDAO")
public class JdbcCadastroEconomicoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcCadastroEconomicoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<CadastroEconomico> listaCadastroEconomico(String filtro) {
        String sql = " select * from ( " +
            "   select ce.id, " +
            "             ce.inscricaoCadastral, " +
            "             coalesce(pf.nome, pj.razaoSocial) nomePessoa," +
            "             coalesce(pf.cpf, pj.cnpj) cpfCnpjPessoa," +
            "             case when pf.id is not null then 'F' else 'J' end tipoPessoa   " +
            "    from CadastroEconomico ce " +
            "   inner join pessoa p on ce.pessoa_id = p.id " +
            "   left join pessoafisica pf on pf.id = p.id" +
            "   left join pessoajuridica pj on pj.id = p.id " +
            " where ce.inscricaoCadastral like '%" + filtro.toLowerCase() + "%' " +
            "       or lower(coalesce(pf.nome, pj.razaoSocial)) like '%" + filtro.toLowerCase() + "%' " +
            "       or lower(coalesce(pf.cpf, pj.cnpj)) like '%" + filtro.toLowerCase() + "%' ) cadastroeconomico " +
            " where rownum <= 10 ";
        ////System.out.println("sql.: " + sql);
        List<CadastroEconomico> lista = getJdbcTemplate().query(sql, new Object[]{}, new CadastroEconomicoRowMapper());
        return lista;
    }

    public CadastroEconomico recuperarPorCmc(String cmc) {
        String sql = "   select ce.id, " +
            "             ce.inscricaoCadastral, " +
            "             coalesce(pf.nome, pj.razaoSocial) nomePessoa," +
            "             coalesce(pf.cpf, pj.cnpj) cpfCnpjPessoa," +
            "             case when pf.id is not null then 'F' else 'J' end tipoPessoa   " +
            "    from CadastroEconomico ce " +
            "   inner join pessoa p on ce.pessoa_id = p.id " +
            "   left join pessoafisica pf on pf.id = p.id" +
            "   left join pessoajuridica pj on pj.id = p.id " +
            " where ce.inscricaocadastral = ? ";
        List<CadastroEconomico> query = getJdbcTemplate().query(sql, new Object[]{cmc}, new CadastroEconomicoRowMapper());
        return query != null && !query.isEmpty() ? query.get(0) : null;
    }

    public void updateNumeroUltimaNotaFiscal(CadastroEconomico cadastroEconomico) {
        String sql = " update cadastroeconomico set numeroultimanotafiscal = ? where inscricaocadastral = ? ";
        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(cadastroEconomico), 1,
            new ParameterizedPreparedStatementSetter<CadastroEconomico>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, CadastroEconomico ce) throws SQLException {
                    preparedStatement.setLong(1, ce.getNumeroUltimaNotaFiscal());
                    preparedStatement.setString(2, ce.getInscricaoCadastral());
                }
            });
    }

    public void updateEmailUsuario(String email, Long id) {
        String sql = " update usuarioweb set email = ? where id = ? ";
        getJdbcTemplate().update(sql, email, id);
    }

    public void updateEnquadramentoFiscal(EnquadramentoFiscal enquadramentoVigente) {
        String sql = " update EnquadramentoFiscal set tipoNotaFiscalServico = ?, substitutoTributario = ?, regimeTributario = ?, tipoIssqn = ? where id = ? ";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(enquadramentoVigente), 1,
            new ParameterizedPreparedStatementSetter<EnquadramentoFiscal>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, EnquadramentoFiscal enquadramento) throws SQLException {
                    preparedStatement.setString(1, enquadramento.getTipoNotaFiscalServico().name());
                    preparedStatement.setBoolean(2, enquadramento.getSubstitutoTributario());
                    preparedStatement.setString(3, enquadramento.getRegimeTributario().name());
                    preparedStatement.setString(4, enquadramento.getTipoIssqn().name());
                    preparedStatement.setLong(5, enquadramento.getId());
                }
            });
    }

    public CadastroEconomico inserir(CadastroEconomico ce) {
        ce.setId(geradorDeIds.getProximoId());
        String sql = "INSERT INTO Cadastro " +
            "(ID) " +
            "VALUES (" + ce.getId() + ")";
        getJdbcTemplate().update(sql);

        sql = "INSERT INTO CadastroEconomico " +
            "(ID, INSCRICAOCADASTRAL, PESSOA_ID) " +
            "VALUES (" + ce.getId() + ", '" + ce.getInscricaoCadastral() + "', " + ce.getPessoa().getId() + ")";
        getJdbcTemplate().update(sql);

        SituacaoCadastroEconomico situacaoAtual = ce.getSituacaoAtual();
        situacaoAtual.setId(geradorDeIds.getProximoId());
        sql = "INSERT INTO SituacaoCadastroEconomico " +
            "(ID, situacaoCadastral, dataAlteracao, dataRegistro) " +
            "VALUES (" + situacaoAtual.getId() + ", '" + situacaoAtual.getSituacaoCadastral().name() + "', current_date, current_date)";
        getJdbcTemplate().update(sql);
        sql = "INSERT INTO CE_SITUACAOCADASTRAL " +
            "(CADASTROECONOMICO_ID, SITUACAOCADASTROECONOMICO_ID) " +
            "VALUES (" + ce.getId() + ", " + situacaoAtual.getId() + ")";
        getJdbcTemplate().update(sql);


        return ce;
    }
}
