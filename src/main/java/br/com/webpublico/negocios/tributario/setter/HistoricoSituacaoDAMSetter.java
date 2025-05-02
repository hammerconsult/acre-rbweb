package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

public class HistoricoSituacaoDAMSetter <T extends AssistenteBarraProgresso> implements BatchPreparedStatementSetter {

    private final List<DAM> dans;
    private final List<Long> ids;
    private final Long idUsuario;
    private T assistente;

    public HistoricoSituacaoDAMSetter(List<DAM> dans, List<Long> ids, Long idUsuario) {
        this.dans = dans;
        this.ids = ids;
        this.idUsuario = idUsuario;
    }

    public HistoricoSituacaoDAMSetter(List<DAM> dans, List<Long> ids, Long idUsuario, T assistente) {
        this.dans = dans;
        this.ids = ids;
        this.idUsuario = idUsuario;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DAM dam = dans.get(i);
        ps.setLong(1, ids.get(i));
        ps.setLong(2, dam.getId());
        if(idUsuario != null) {
            ps.setLong(3, idUsuario);
        } else {
            ps.setNull(3, Types.NULL);
        }
        ps.setString(4, dam.getSituacao().name());
        ps.setDate(5, new java.sql.Date(new Date().getTime()));
        if (assistente != null) {
            assistente.conta();
        }
    }

    @Override
    public int getBatchSize() {
        return dans.size();
    }
}
