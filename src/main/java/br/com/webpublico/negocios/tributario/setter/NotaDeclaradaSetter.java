package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.nfse.domain.NotaDeclarada;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class NotaDeclaradaSetter implements BatchPreparedStatementSetter {
    private List<NotaDeclarada> notas;
    private SingletonGeradorId geradorDeIds;


    public NotaDeclaradaSetter(List<NotaDeclarada> notas, SingletonGeradorId gerador) {
        this.notas = notas;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        NotaDeclarada nota = notas.get(i);
        nota.setId(geradorDeIds.getProximoId());

        ps.setLong(1, nota.getId());
        ps.setLong(2, nota.getDeclaracaoPrestacaoServico().getId());
        ps.setLong(3, nota.getDeclaracaoMensalServico().getId());

    }

    @Override
    public int getBatchSize() {
        return notas.size();
    }
}
