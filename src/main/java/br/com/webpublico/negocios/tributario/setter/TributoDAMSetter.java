package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.TributoDAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TributoDAMSetter <T extends AssistenteBarraProgresso> implements BatchPreparedStatementSetter {

    private final List<TributoDAM> tributosDAM;
    private final List<Long> ids;
    private T assistente;

    public TributoDAMSetter(List<TributoDAM> tributosDAM, List<Long> ids) {
        this.tributosDAM = tributosDAM;
        this.ids = ids;
    }

    public TributoDAMSetter(List<TributoDAM> tributosDAM, List<Long> ids, T assistente) {
        this.tributosDAM = tributosDAM;
        this.ids = ids;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        TributoDAM tributoDAM = tributosDAM.get(i);
        tributoDAM.setId(ids.get(i));
        ps.setLong(1, tributoDAM.getId());
        ps.setLong(2, tributoDAM.getTributo().getId());
        ps.setLong(3, tributoDAM.getItemDAM().getId());
        ps.setBigDecimal(4, tributoDAM.getValorOriginal());
        ps.setBigDecimal(5, tributoDAM.getDesconto());
        if (assistente != null) {
            assistente.conta();
        }
    }

    @Override
    public int getBatchSize() {
        return tributosDAM.size();
    }
}
