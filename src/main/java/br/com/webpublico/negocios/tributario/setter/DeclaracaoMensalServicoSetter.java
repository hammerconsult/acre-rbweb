package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DeclaracaoMensalServicoSetter implements BatchPreparedStatementSetter {

    private DeclaracaoMensalServico declaracaoMensalServico;
    private SingletonGeradorId geradorDeIds;

    public DeclaracaoMensalServicoSetter(DeclaracaoMensalServico declaracaoMensalServico, SingletonGeradorId geradorDeIds) {
        this.declaracaoMensalServico = declaracaoMensalServico;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        declaracaoMensalServico.setId(geradorDeIds.getProximoId());
        ps.setLong(1, declaracaoMensalServico.getId());
        ps.setInt(2, declaracaoMensalServico.getCodigo());
        ps.setString(3, declaracaoMensalServico.getTipo().name());
        ps.setLong(4, declaracaoMensalServico.getPrestador().getId());
        if (declaracaoMensalServico.getProcessoCalculo() != null) {
            ps.setLong(5, declaracaoMensalServico.getProcessoCalculo().getId());
        } else {
            ps.setNull(5, Types.NUMERIC);
        }
        ps.setString(6, declaracaoMensalServico.getMes().name());
        ps.setLong(7, declaracaoMensalServico.getExercicio().getId());
        ps.setString(8, declaracaoMensalServico.getSituacao().name());
        ps.setInt(9, declaracaoMensalServico.getQtdNotas());
        ps.setBigDecimal(10, declaracaoMensalServico.getTotalServicos());
        ps.setBigDecimal(11, declaracaoMensalServico.getTotalIss());
        ps.setDate(12, new java.sql.Date(declaracaoMensalServico.getAbertura().getTime()));
        ps.setDate(13, new java.sql.Date(declaracaoMensalServico.getEncerramento().getTime()));
        ps.setString(14, declaracaoMensalServico.getTipoMovimento().name());
        ps.setDate(15, new java.sql.Date(declaracaoMensalServico.getReferencia().getTime()));
        ps.setString(16, declaracaoMensalServico.getUsuarioDeclaracao());
        ps.setString(17, declaracaoMensalServico.getLancadoPor().name());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
