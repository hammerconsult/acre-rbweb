package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ItemPregaoLoteItemProcesso;
import br.com.webpublico.enums.administrativo.TipoSetterJdbc;
import com.google.common.base.Strings;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ItemPregaoLoteItemProcessoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO ITEMPREGAOLOTEITEMPROCESSO (ID, ITEMPREGAOLOTEPROCESSO_ID, ITEMPROCESSODECOMPRA_ID, VALOR, JUSTIFICATIVA) VALUES (?, ?, ?, ?, ?) ";
    public static final String SQL_INSERT_AUDITORIA = " INSERT INTO ITEMPREGAOLOTEITEMPROCESSO_AUD (ID, ITEMPREGAOLOTEPROCESSO_ID, ITEMPROCESSODECOMPRA_ID, VALOR, JUSTIFICATIVA, REV, REVTYPE) VALUES (?, ?, ?, ?, ?, ?, ?) ";
    public static final String SQL_UPDATE = " UPDATE ITEMPREGAOLOTEITEMPROCESSO SET ITEMPREGAOLOTEPROCESSO_ID = ?, ITEMPROCESSODECOMPRA_ID = ?, VALOR = ?, JUSTIFICATIVA = ? WHERE ID = ? ";

    private ItemPregaoLoteItemProcesso itemPregaoLoteItemProcesso;
    private TipoSetterJdbc tipoInsertJdbc;
    private Long idRevisao;
    private Long tipoRevisao;


    public ItemPregaoLoteItemProcessoSetter(ItemPregaoLoteItemProcesso itemPregaoLoteItemProcesso, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLoteItemProcesso = itemPregaoLoteItemProcesso;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    public ItemPregaoLoteItemProcessoSetter(ItemPregaoLoteItemProcesso itemPregaoLoteItemProcesso, Long idRevisao, Long tipoRevisao, TipoSetterJdbc tipoInsertJdbc) {
        this.itemPregaoLoteItemProcesso = itemPregaoLoteItemProcesso;
        this.idRevisao = idRevisao;
        this.tipoRevisao = tipoRevisao;
        this.tipoInsertJdbc = tipoInsertJdbc;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        switch (tipoInsertJdbc) {
            case INSERT:
                ps.setLong(1, itemPregaoLoteItemProcesso.getId());
                ps.setLong(2, itemPregaoLoteItemProcesso.getItemPregaoLoteProcesso().getId());
                ps.setLong(3, itemPregaoLoteItemProcesso.getItemProcessoDeCompra().getId());
                ps.setBigDecimal(4, itemPregaoLoteItemProcesso.getValor());
                if (!Strings.isNullOrEmpty(itemPregaoLoteItemProcesso.getJustificativa())) {
                    ps.setString(5, itemPregaoLoteItemProcesso.getJustificativa());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                if (idRevisao != null && tipoRevisao != null) {
                    ps.setLong(6, idRevisao);
                    ps.setLong(7, tipoRevisao);
                }
                break;

            case UPDATE:
                ps.setLong(1, itemPregaoLoteItemProcesso.getItemPregaoLoteProcesso().getId());
                ps.setLong(2, itemPregaoLoteItemProcesso.getItemProcessoDeCompra().getId());
                ps.setBigDecimal(3, itemPregaoLoteItemProcesso.getValor());
                if (!Strings.isNullOrEmpty(itemPregaoLoteItemProcesso.getJustificativa())) {
                    ps.setString(4, itemPregaoLoteItemProcesso.getJustificativa());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                ps.setLong(5, itemPregaoLoteItemProcesso.getId());
            default:
                break;
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
