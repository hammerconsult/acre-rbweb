package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ItemPregaoItemProcesso;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemPregaoItemProcessoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO ITPREITPRO (ID, ITEMPREGAO_ID, ITEMPROCESSODECOMPRA_ID) VALUES (?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO ITPREITPRO_AUD (ID, ITEMPREGAO_ID, ITEMPROCESSODECOMPRA_ID, REV, REVTYPE) VALUES (?, ?, ?, ?, ?) ";

    private ItemPregaoItemProcesso itemPregaoItemProcesso;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;


    public ItemPregaoItemProcessoSetter(ItemPregaoItemProcesso itemPregaoItemProcesso, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoItemProcesso = itemPregaoItemProcesso;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public ItemPregaoItemProcessoSetter(ItemPregaoItemProcesso itemPregaoItemProcesso, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoItemProcesso = itemPregaoItemProcesso;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, itemPregaoItemProcesso.getId());
                ps.setLong(2, itemPregaoItemProcesso.getItemPregao().getId());
                ps.setLong(3, itemPregaoItemProcesso.getItemProcessoDeCompra().getId());
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(4, idRevisao);
                    ps.setLong(5, tipoRevisao);
                }
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
