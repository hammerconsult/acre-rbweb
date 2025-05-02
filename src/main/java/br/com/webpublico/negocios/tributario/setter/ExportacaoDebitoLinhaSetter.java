package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidadesauxiliares.AssistenteExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.VOLinhasExportacaoDebitosIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExportacaoDebitoLinhaSetter implements BatchPreparedStatementSetter {

    private final SingletonGeradorId geradorDeIds;
    private final AssistenteExportacaoDebitosIPTU assistente;

    public ExportacaoDebitoLinhaSetter(SingletonGeradorId geradorDeIds, AssistenteExportacaoDebitosIPTU assistente) {
        this.geradorDeIds = geradorDeIds;
        this.assistente = assistente;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        VOLinhasExportacaoDebitosIPTU linha = assistente.getLinhas().get(i);
        Long idLinha = geradorDeIds.getProximoId();
        ps.setLong(1, idLinha);
        ps.setLong(2, linha.getIndice());
        ps.setLong(3, linha.getIdExportacao());
        ps.setString(4, linha.getLinha());
        assistente.conta();
    }

    @Override
    public int getBatchSize() {
        return assistente.getLinhas().size();
    }
}
