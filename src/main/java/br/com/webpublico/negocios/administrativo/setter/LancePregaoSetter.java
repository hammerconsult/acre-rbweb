package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.LancePregao;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import com.google.common.base.Strings;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LancePregaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO LANCEPREGAO (ID, RODADAPREGAO_ID, PROPOSTAFORNECEDOR_ID, STATUSLANCEPREGAO, VALOR, PERCENTUALDESCONTO, JUSTIFICATIVACANCELAMENTO) VALUES (?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO LANCEPREGAO_AUD (ID, RODADAPREGAO_ID, PROPOSTAFORNECEDOR_ID, STATUSLANCEPREGAO, VALOR, PERCENTUALDESCONTO, JUSTIFICATIVACANCELAMENTO, REV, REVTYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE LANCEPREGAO SET PROPOSTAFORNECEDOR_ID = ?, STATUSLANCEPREGAO = ?, VALOR = ?, PERCENTUALDESCONTO = ?, JUSTIFICATIVACANCELAMENTO = ? WHERE ID = ? ";
    public static final String SQL_DELETE = " DELETE FROM LANCEPREGAO WHERE ID = ? ";

    private final LancePregao lancePregao;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;

    public LancePregaoSetter(LancePregao lancePregao, TipoSetterJdbc tipoInsertJdbc) {
        this.lancePregao = lancePregao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public LancePregaoSetter(LancePregao lancePregao, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.lancePregao = lancePregao;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, lancePregao.getId());
                if (lancePregao.getRodadaPregao() != null) {
                    ps.setLong(2, lancePregao.getRodadaPregao().getId());
                } else {
                    ps.setNull(2, Types.NUMERIC);
                }
                if (lancePregao.getPropostaFornecedor() != null) {
                    ps.setLong(3, lancePregao.getPropostaFornecedor().getId());
                } else {
                    ps.setNull(3, Types.NUMERIC);
                }
                if (lancePregao.getStatusLancePregao() != null) {
                    ps.setString(4, lancePregao.getStatusLancePregao().name());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                if (lancePregao.getValor() != null) {
                    ps.setBigDecimal(5, lancePregao.getValor());
                } else {
                    ps.setNull(5, Types.DECIMAL);
                }
                if (lancePregao.getPercentualDesconto() != null) {
                    ps.setBigDecimal(6, lancePregao.getPercentualDesconto());
                } else {
                    ps.setNull(6, Types.DECIMAL);
                }

                if (!Strings.isNullOrEmpty(lancePregao.getJustificativaCancelamento())) {
                    ps.setString(7, lancePregao.getJustificativaCancelamento());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                }
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(8, idRevisao);
                    ps.setLong(9, tipoRevisao);
                }
                break;

            case UPDATE:
                ps.setLong(1, lancePregao.getPropostaFornecedor().getId());
                if (lancePregao.getStatusLancePregao() != null) {
                    ps.setString(2, lancePregao.getStatusLancePregao().name());
                } else {
                    ps.setNull(2, Types.VARCHAR);
                }
                if (lancePregao.getValor() != null) {
                    ps.setBigDecimal(3, lancePregao.getValor());
                } else {
                    ps.setNull(3, Types.DECIMAL);
                }
                if (lancePregao.getPercentualDesconto() != null) {
                    ps.setBigDecimal(4, lancePregao.getPercentualDesconto());
                } else {
                    ps.setNull(4, Types.DECIMAL);
                }
                if (!Strings.isNullOrEmpty(lancePregao.getJustificativaCancelamento())) {
                    ps.setString(5, lancePregao.getJustificativaCancelamento());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                ps.setLong(6, lancePregao.getId());
                break;

            case DELETE:
                ps.setLong(1, lancePregao.getId());
            default:
                break;
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
