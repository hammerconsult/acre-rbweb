package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CancelamentoParcelamento;
import br.com.webpublico.entidades.ProcessoParcelamento;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoParcelamento;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository(value = "processoParcelamentoDAO")
public class JdbcProcessoParcelamentoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcProcessoParcelamentoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void updateSituacaoProcessoParcelamento(ProcessoParcelamento processoParcelamento, SituacaoParcelamento situacaoParcelamento) {
        processoParcelamento.setSituacaoParcelamento(situacaoParcelamento);
        String sql = "update ProcessoParcelamento set situacaoParcelamento = ? where id = ?";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setString(1, parcelamento.getSituacaoParcelamento().name());
                    preparedStatement.setLong(2, parcelamento.getId());
                }
            });
    }

    public void updateDadosReativacaoProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        String sql = "update ProcessoParcelamento set usuarioReativacao_id = ?, dataReativacao = ?, enderecoReativacao = ?, motivoReativacao = ?, permitirCancelarParcelamento = ? " +
            " where id = ?";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setLong(1, parcelamento.getUsuarioReativacao().getId());
                    preparedStatement.setTimestamp(2, new java.sql.Timestamp(parcelamento.getDataReativacao().getTime()));
                    preparedStatement.setString(3, parcelamento.getEnderecoReativacao());
                    preparedStatement.setString(4, parcelamento.getMotivoReativacao());
                    preparedStatement.setBoolean(5, false);
                    preparedStatement.setLong(6, parcelamento.getId());
                }
            });
    }

    public void deleteParcelasCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        String sql = "delete from PARCELASCANCELPARCELAMENTO where CANCELAMENTOPARCELAMENTO_ID = ?";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(cancelamentoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<CancelamentoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, CancelamentoParcelamento cancelamento) throws SQLException {
                    preparedStatement.setLong(1, cancelamento.getId());
                }
            });
    }

    public void deleteItensCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        String sql = "delete from ITEMCANCELPARCELAMENTO where CANCELAMENTOPARCELAMENTO_ID = ?";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(cancelamentoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<CancelamentoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, CancelamentoParcelamento cancelamento) throws SQLException {
                    preparedStatement.setLong(1, cancelamento.getId());
                }
            });
    }

    public void deleteCancelamentoParcelamento(ProcessoParcelamento processoParcelamento) {
        String sql = "delete from CANCELAMENTOPARCELAMENTO where PROCESSOPARCELAMENTO_ID = ?";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setLong(1, parcelamento.getId());
                }
            });
    }

    public void updateParcelasProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        String sql = "update situacaoparcelavalordivida set SITUACAOPARCELA = ? where id in ( " +
            "select spvd.id from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where vd.CALCULO_ID = ? " +
            "  and spvd.SITUACAOPARCELA = ?)";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setString(1, SituacaoParcela.EM_ABERTO.name());
                    preparedStatement.setLong(2, parcelamento.getId());
                    preparedStatement.setString(3, SituacaoParcela.PARCELAMENTO_CANCELADO.name());
                }
            });
    }

    public void updateOrigemProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        String sql = "update situacaoparcelavalordivida set SITUACAOPARCELA = ? where id in ( " +
            "select spvd.id from parcelavalordivida pvd " +
            "inner join itemprocessoparcelamento ipp on ipp.PARCELAVALORDIVIDA_ID = pvd.id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where ipp.PROCESSOPARCELAMENTO_ID = ? " +
            "  and spvd.SITUACAOPARCELA in (?, ?))";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setString(1, SituacaoParcela.PARCELADO.name());
                    preparedStatement.setLong(2, parcelamento.getId());
                    preparedStatement.setString(3, SituacaoParcela.EM_ABERTO.name());
                    preparedStatement.setString(4, SituacaoParcela.PAGO_PARCELAMENTO.name());
                }
            });
    }

    public void updateCancelarResidualCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        String sql = "update situacaoparcelavalordivida set SITUACAOPARCELA = 'CANCELAMENTO' where id in ( " +
            "select spvd.id from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where vd.calculo_Id = ?)";

        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(cancelamentoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<CancelamentoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, CancelamentoParcelamento cancelamento) throws SQLException {
                    preparedStatement.setLong(1, cancelamento.getId());
                }
            });
    }

    public void updateAtivarDAMsCanceladosNaoVencidos(ProcessoParcelamento processoParcelamento) {
        String sql = "update DAM set SITUACAO = 'ABERTO' where id in (select idam.dam_id from ItemDam idam" +
            " inner join ParcelaValorDivida pvd on pvd.id = idam.PARCELA_ID" +
            " inner join ValorDivida vd on vd.id = pvd.VALORDIVIDA_ID" +
            " where vd.CALCULO_ID = ?)" +
            " and trunc(VENCIMENTO) >= trunc(current_date) " +
            " and SITUACAO = 'CANCELADO'";
        getJdbcTemplate().batchUpdate(sql, Lists.newArrayList(processoParcelamento), 1,
            new ParameterizedPreparedStatementSetter<ProcessoParcelamento>() {
                @Override
                public void setValues(PreparedStatement preparedStatement, ProcessoParcelamento parcelamento) throws SQLException {
                    preparedStatement.setLong(1, parcelamento.getId());
                }
            });
    }
}
