package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemDAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemDAMSetter <T extends AssistenteBarraProgresso> implements BatchPreparedStatementSetter {

    private final List<ItemDAM> itensDAM;
    private final List<Long> ids;
    private T assistente;

    public ItemDAMSetter(List<ItemDAM> itensDAM, List<Long> ids) {
        this.itensDAM = itensDAM;
        this.ids = ids;
    }

    public ItemDAMSetter(List<ItemDAM> itensDAM, List<Long> ids, T assistente) {
        this.itensDAM = itensDAM;
        this.ids = ids;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemDAM itemDAM = itensDAM.get(i);
        itemDAM.setId(ids.get(i));
        ps.setLong(1, itemDAM.getId());
        ps.setLong(2, itemDAM.getDAM().getId());
        ps.setBigDecimal(3, itemDAM.getDesconto());
        ps.setBigDecimal(4, itemDAM.getCorrecaoMonetaria());
        ps.setBigDecimal(5, itemDAM.getJuros());
        ps.setBigDecimal(6, itemDAM.getMulta());
        ps.setBigDecimal(7, itemDAM.getOutrosAcrescimos());
        ps.setBigDecimal(8, itemDAM.getValorOriginalDevido());
        ps.setLong(9, itemDAM.getParcela().getId());
        ps.setBigDecimal(10, itemDAM.getHonorarios());
        if (assistente != null) {
            assistente.conta();
        }
    }

    @Override
    public int getBatchSize() {
        return itensDAM.size();
    }
}
