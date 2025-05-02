package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ItemPregaoLanceVencedorSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO ITEMPREGAOLANCEVENCEDOR (ID, ITEMPREGAO_ID, LANCEPREGAO_ID, STATUS, VALOR, PERCENTUALDESCONTO) VALUES (?, ?, ?, ?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO ITEMPREGAOLANCEVENCEDOR_AUD (ID, ITEMPREGAO_ID, LANCEPREGAO_ID, STATUS, VALOR, PERCENTUALDESCONTO, REV, REVTYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE ITEMPREGAOLANCEVENCEDOR SET ITEMPREGAO_ID = ?, LANCEPREGAO_ID = ?, STATUS = ?, VALOR = ?, PERCENTUALDESCONTO = ? WHERE ID = ? ";
    public static final String SQL_DELETE = " DELETE FROM ITEMPREGAOLANCEVENCEDOR WHERE ID = ? ";

    private final ItemPregaoLanceVencedor itemPregaoLanceVencedor;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;

    public ItemPregaoLanceVencedorSetter(ItemPregaoLanceVencedor itemPregaoLanceVencedor, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLanceVencedor = itemPregaoLanceVencedor;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public ItemPregaoLanceVencedorSetter(ItemPregaoLanceVencedor itemPregaoLanceVencedor, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLanceVencedor = itemPregaoLanceVencedor;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, itemPregaoLanceVencedor.getId());
                if (itemPregaoLanceVencedor.getItemPregao() != null) {
                    ps.setLong(2, itemPregaoLanceVencedor.getItemPregao().getId());
                } else {
                    ps.setNull(2, Types.NUMERIC);
                }
                if (itemPregaoLanceVencedor.getLancePregao() != null) {
                    ps.setLong(3, itemPregaoLanceVencedor.getLancePregao().getId());
                } else {
                    ps.setNull(3, Types.NUMERIC);
                }
                if (itemPregaoLanceVencedor.getStatus() != null) {
                    ps.setString(4, itemPregaoLanceVencedor.getStatus().name());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                if (itemPregaoLanceVencedor.getValor() != null) {
                    ps.setBigDecimal(5, itemPregaoLanceVencedor.getValor());
                } else {
                    ps.setNull(5, Types.DECIMAL);
                }
                if (itemPregaoLanceVencedor.getPercentualDesconto() != null) {
                    ps.setBigDecimal(6, itemPregaoLanceVencedor.getPercentualDesconto());
                } else {
                    ps.setNull(6, Types.DECIMAL);
                }
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(7, idRevisao);
                    ps.setLong(8, tipoRevisao);
                }
                break;

            case UPDATE:
                if (itemPregaoLanceVencedor.getItemPregao() != null) {
                    ps.setLong(1, itemPregaoLanceVencedor.getItemPregao().getId());
                } else {
                    ps.setNull(1, Types.NUMERIC);
                }
                if (itemPregaoLanceVencedor.getLancePregao() != null) {
                    ps.setLong(2, itemPregaoLanceVencedor.getLancePregao().getId());
                } else {
                    ps.setNull(2, Types.NUMERIC);
                }
                if (itemPregaoLanceVencedor.getStatus() != null) {
                    ps.setString(3, itemPregaoLanceVencedor.getStatus().name());
                } else {
                    ps.setNull(3, Types.VARCHAR);
                }
                if (itemPregaoLanceVencedor.getValor() != null) {
                    ps.setBigDecimal(4, itemPregaoLanceVencedor.getValor());
                } else {
                    ps.setNull(4, Types.DECIMAL);
                }
                if (itemPregaoLanceVencedor.getPercentualDesconto() != null) {
                    ps.setBigDecimal(5, itemPregaoLanceVencedor.getPercentualDesconto());
                } else {
                    ps.setNull(5, Types.DECIMAL);
                }
                ps.setLong(6, itemPregaoLanceVencedor.getId());
                break;

            case DELETE:
                ps.setLong(1, itemPregaoLanceVencedor.getId());

            default:
                break;
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
