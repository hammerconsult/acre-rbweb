package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.RodadaPregao;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import com.google.common.base.Strings;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RodadaPregaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO RODADAPREGAO (ID, ITEMPREGAO_ID, NUMERO, JUSTIFICATIVAEXCLUSAO, OBSERVACAO) VALUES (?, ?, ?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO RODADAPREGAO_AUD (ID, ITEMPREGAO_ID, NUMERO, JUSTIFICATIVAEXCLUSAO, OBSERVACAO, REV, REVTYPE) VALUES (?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE RODADAPREGAO SET NUMERO = ?, JUSTIFICATIVAEXCLUSAO = ?, OBSERVACAO = ? WHERE ID = ? ";
    public static final String SQL_DELETE = " DELETE FROM RODADAPREGAO WHERE ID = ? ";

    private final RodadaPregao rodadaPregao;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;

    public RodadaPregaoSetter(RodadaPregao rodadaPregao, TipoSetterJdbc tipoInsertJdbc) {
        this.rodadaPregao = rodadaPregao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public RodadaPregaoSetter(RodadaPregao rodadaPregao, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.rodadaPregao = rodadaPregao;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, rodadaPregao.getId());
                if (rodadaPregao.getItemPregao() != null) {
                    ps.setLong(2, rodadaPregao.getItemPregao().getId());
                } else {
                    ps.setNull(2, Types.NUMERIC);
                }
                if (rodadaPregao.getNumero() != null) {
                    ps.setInt(3, rodadaPregao.getNumero());
                } else {
                    ps.setNull(3, Types.NUMERIC);
                }
                if (!Strings.isNullOrEmpty(rodadaPregao.getJustificativaExclusao())) {
                    ps.setString(4, rodadaPregao.getJustificativaExclusao());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                if (!Strings.isNullOrEmpty(rodadaPregao.getObservacao())) {
                    ps.setString(5, rodadaPregao.getObservacao());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(6, idRevisao);
                    ps.setLong(7, tipoRevisao);
                }
                break;

            case UPDATE:
                ps.setInt(1, rodadaPregao.getNumero());
                if (!Strings.isNullOrEmpty(rodadaPregao.getJustificativaExclusao())) {
                    ps.setString(2, rodadaPregao.getJustificativaExclusao());
                } else {
                    ps.setNull(2, Types.VARCHAR);
                }
                if (!Strings.isNullOrEmpty(rodadaPregao.getObservacao())) {
                    ps.setString(3, rodadaPregao.getObservacao());
                } else {
                    ps.setNull(3, Types.VARCHAR);
                }
                ps.setLong(4, rodadaPregao.getId());
                break;

            case DELETE:
                ps.setLong(1, rodadaPregao.getId());

            default:
                break;
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
