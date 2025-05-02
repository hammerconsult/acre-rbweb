package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DeclaracaoMensalServicoUpdateSetter implements BatchPreparedStatementSetter {

    private DeclaracaoMensalServico declaracaoMensalServico;

    public DeclaracaoMensalServicoUpdateSetter(DeclaracaoMensalServico declaracaoMensalServico) {
        this.declaracaoMensalServico = declaracaoMensalServico;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setInt(1, declaracaoMensalServico.getCodigo());
        ps.setString(2, declaracaoMensalServico.getTipo().name());
        ps.setLong(3, declaracaoMensalServico.getPrestador().getId());
        if (declaracaoMensalServico.getProcessoCalculo() != null) {
            ps.setLong(4, declaracaoMensalServico.getProcessoCalculo().getId());
        } else {
            ps.setNull(4, Types.NUMERIC);
        }
        ps.setString(5, declaracaoMensalServico.getMes().name());
        ps.setLong(6, declaracaoMensalServico.getExercicio().getId());
        ps.setString(7, declaracaoMensalServico.getSituacao().name());
        ps.setInt(8, declaracaoMensalServico.getQtdNotas());
        ps.setBigDecimal(9, declaracaoMensalServico.getTotalServicos());
        ps.setBigDecimal(10, declaracaoMensalServico.getTotalIss());
        ps.setDate(11, new java.sql.Date(declaracaoMensalServico.getAbertura().getTime()));
        ps.setDate(12, new java.sql.Date(declaracaoMensalServico.getEncerramento().getTime()));
        ps.setString(13, declaracaoMensalServico.getTipoMovimento().name());
        ps.setDate(14, new java.sql.Date(declaracaoMensalServico.getReferencia().getTime()));
        ps.setLong(15, declaracaoMensalServico.getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
