package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.Pregao;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PregaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO PREGAO (ID, REALIZADOEM, LICITACAO_ID) VALUES (?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO PREGAO_AUD (ID, REALIZADOEM, LICITACAO_ID, REV, REVTYPE) VALUES (?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE PREGAO SET REALIZADOEM = ?, LICITACAO_ID = ? WHERE ID = ? ";

    private final Pregao pregao;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;

    public PregaoSetter(Pregao pregao, TipoSetterJdbc tipoInsertJdbc) {
        this.pregao = pregao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public PregaoSetter(Pregao pregao, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.pregao = pregao;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, pregao.getId());
                ps.setDate(2, new java.sql.Date(pregao.getRealizadoEm().getTime()));
                ps.setLong(3, pregao.getLicitacao().getId());
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(4, idRevisao);
                    ps.setLong(5, tipoRevisao);
                }
                break;

            case UPDATE:
                ps.setDate(1, new java.sql.Date(pregao.getRealizadoEm().getTime()));
                ps.setLong(2, pregao.getLicitacao().getId());
                ps.setLong(3, pregao.getId());
                break;
            default:
                break;
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
