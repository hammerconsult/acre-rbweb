package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ItemPregaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO ITEMPREGAO (ID, PREGAO_ID, ITEMPREGAOLANCEVENCEDOR_ID, TIPOSTATUSITEMPREGAO, STATUSFORNECEDORVENCEDOR) VALUES (?, ?, ?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO ITEMPREGAO_AUD (ID, PREGAO_ID, ITEMPREGAOLANCEVENCEDOR_ID, TIPOSTATUSITEMPREGAO, STATUSFORNECEDORVENCEDOR, REV, REVTYPE) VALUES (?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE ITEMPREGAO SET ITEMPREGAOLANCEVENCEDOR_ID = ?, TIPOSTATUSITEMPREGAO = ?, STATUSFORNECEDORVENCEDOR = ? WHERE ID = ? ";

    private final ItemPregao itemPregao;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;

    public ItemPregaoSetter(ItemPregao itemPregao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregao = itemPregao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public ItemPregaoSetter(ItemPregao itemPregao, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregao = itemPregao;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, itemPregao.getId());
                ps.setLong(2, itemPregao.getPregao().getId());
                if (itemPregao.getItemPregaoLanceVencedor() != null) {
                    ps.setLong(3, itemPregao.getItemPregaoLanceVencedor().getId());
                } else {
                    ps.setNull(3, Types.NUMERIC);
                }
                if (itemPregao.getTipoStatusItemPregao() != null) {
                    ps.setString(4, itemPregao.getTipoStatusItemPregao().name());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                if (itemPregao.getStatusFornecedorVencedor() != null) {
                    ps.setString(5, itemPregao.getStatusFornecedorVencedor().name());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(6, idRevisao);
                    ps.setLong(7, tipoRevisao);
                }
                break;

            case UPDATE:
                    if (itemPregao.getItemPregaoLanceVencedor() != null) {
                        ps.setLong(1, itemPregao.getItemPregaoLanceVencedor().getId());
                    } else {
                        ps.setNull(1, Types.NUMERIC);
                    }
                    if (itemPregao.getTipoStatusItemPregao() != null) {
                        ps.setString(2, itemPregao.getTipoStatusItemPregao().name());
                    } else {
                        ps.setNull(2, Types.VARCHAR);
                    }
                    if (itemPregao.getStatusFornecedorVencedor() != null) {
                        ps.setString(3, itemPregao.getStatusFornecedorVencedor().name());
                    } else {
                        ps.setNull(3, Types.VARCHAR);
                    }
                    ps.setLong(4, itemPregao.getId());
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
