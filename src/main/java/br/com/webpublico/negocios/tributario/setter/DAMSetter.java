package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.DAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DAMSetter <T extends AssistenteBarraProgresso> implements BatchPreparedStatementSetter {
    private final static Logger logger = LoggerFactory.getLogger(DAMSetter.class);

    private final List<DAM> dans;
    private final List<Long> ids;
    private T assistente;

    public DAMSetter(List<DAM> dans, List<Long> ids) {
        logger.info("Inserindo {} dans.", ids.size());
        this.dans = dans;
        this.ids = ids;
    }

    public DAMSetter(List<DAM> dans, List<Long> ids, T assistente) {
        this.dans = dans;
        this.ids = ids;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DAM dam = dans.get(i);
        dam.setId(ids.get(i));
        ps.setLong(1, dam.getId());
        ps.setString(2, dam.getNumeroDAM());
        ps.setString(3, dam.getCodigoBarras());
        ps.setDate(4, new java.sql.Date(dam.getVencimento().getTime()));
        ps.setTimestamp(5, new java.sql.Timestamp(dam.getEmissao().getTime()));
        ps.setBigDecimal(6, dam.getValorOriginal());
        ps.setBigDecimal(7, dam.getJuros());
        ps.setBigDecimal(8, dam.getMulta());
        ps.setBigDecimal(9, dam.getCorrecaoMonetaria());
        ps.setBigDecimal(10, dam.getDesconto());
        ps.setString(11, dam.getSituacao().name());
        ps.setString(12, dam.getTipo().name());
        ps.setLong(13, dam.getNumero());
        ps.setLong(14, dam.getExercicio().getId());
        ps.setBigDecimal(15, dam.getHonorarios());
        ps.setInt(16, dam.getSequencia());
        if (assistente != null) {
            assistente.conta();
        }
    }

    @Override
    public int getBatchSize() {
        return dans.size();
    }
}
