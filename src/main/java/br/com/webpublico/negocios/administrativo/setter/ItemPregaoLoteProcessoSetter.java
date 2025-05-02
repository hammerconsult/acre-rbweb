package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ItemPregaoLoteProcesso;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemPregaoLoteProcessoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO ITPRELOTPRO (ID, ITEMPREGAO_ID, LOTEPROCESSODECOMPRA_ID) VALUES (?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO ITPRELOTPRO_AUD (ID, ITEMPREGAO_ID, LOTEPROCESSODECOMPRA_ID, REV, REVTYPE) VALUES (?, ?, ?, ?, ?) ";

    private ItemPregaoLoteProcesso itemPregaoLoteProcesso;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;


    public ItemPregaoLoteProcessoSetter(ItemPregaoLoteProcesso itemPregaoLoteProcesso, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLoteProcesso = itemPregaoLoteProcesso;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public ItemPregaoLoteProcessoSetter(ItemPregaoLoteProcesso itemPregaoLoteProcesso, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLoteProcesso = itemPregaoLoteProcesso;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, itemPregaoLoteProcesso.getId());
                ps.setLong(2, itemPregaoLoteProcesso.getItemPregao().getId());
                ps.setLong(3, itemPregaoLoteProcesso.getLoteProcessoDeCompra().getId());
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
