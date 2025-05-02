package br.com.webpublico.negocios.tributario.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository(value = "calculoDAO")
public class JdbcCalculoDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    public JdbcCalculoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void transferirCalculoPessoa(Long idVelho, Long idNovo, Long idCalculo) {
        String delete = "delete from calculopessoa where calculo_id = ? and pessoa_id = ?";
        getJdbcTemplate().update(delete, idCalculo, idVelho);

        String insert = "insert into calculopessoa (id, calculo_id, pessoa_id) values (hibernate_sequence.nextval, ?, ?)";
        getJdbcTemplate().update(insert, idCalculo, idNovo);
    }

    public void transferirPropriedade(Long idPropriedade, Long idPessoa) {
        String update = "update propriedade set pessoa_id = ? where id = ?";
        getJdbcTemplate().update(update, idPessoa, idPropriedade);
    }

    public void transferirPropriedadeRural(Long idPropriedade, Long idPessoa) {
        String update = "update propriedaderural set pessoa_id = ? where id = ?";
        getJdbcTemplate().update(update, idPessoa, idPropriedade);
    }

    public void transferirPessoaCMC(Long idCadastroEconomico, Long idPessoa) {
        String update = "update cadastroeconomico set pessoa_id = ? where id = ?";
        getJdbcTemplate().update(update, idPessoa, idCadastroEconomico);
    }

    public void atualizarSituacaoITBI(Long idProcessoCalculo, String situacao) {
        String sql = " update processocalculoitbi "
            + " set situacao = ? "
            + " where id = ? ";
        getJdbcTemplate().update(sql, situacao, idProcessoCalculo);
    }
}
