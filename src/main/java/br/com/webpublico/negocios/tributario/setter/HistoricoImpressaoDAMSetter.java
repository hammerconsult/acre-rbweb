package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.HistoricoImpressaoDAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class HistoricoImpressaoDAMSetter <T extends AssistenteBarraProgresso> implements BatchPreparedStatementSetter {
    private final List<HistoricoImpressaoDAM> historicosImpressaoDAM;
    private final List<Long> ids;
    private T assistente;

    public HistoricoImpressaoDAMSetter(List<HistoricoImpressaoDAM> historicosImpressaoDAM, List<Long> ids) {
        this.historicosImpressaoDAM = historicosImpressaoDAM;
        this.ids = ids;
    }

    public HistoricoImpressaoDAMSetter(List<HistoricoImpressaoDAM> historicosImpressaoDAM, List<Long> ids, T assistente) {
        this.historicosImpressaoDAM = historicosImpressaoDAM;
        this.ids = ids;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        HistoricoImpressaoDAM historicoImpressaoDAM = historicosImpressaoDAM.get(i);
        historicoImpressaoDAM.setId(ids.get(i));
        ps.setLong(1, historicoImpressaoDAM.getId());
        ps.setLong(2, historicoImpressaoDAM.getDam().getId());
        if (historicoImpressaoDAM.getUsuarioSistema() != null) {
            ps.setLong(3, historicoImpressaoDAM.getUsuarioSistema().getId());
        } else {
            ps.setNull(3, Types.NULL);
        }
        ps.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
        ps.setString(5, historicoImpressaoDAM.getTipoImpressao().name());
        ps.setLong(6, historicoImpressaoDAM.getParcela().getId());
        if (assistente != null) {
            assistente.conta();
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
