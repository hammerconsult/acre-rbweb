package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBemContabil;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

public class ReducaoValorBemContabilSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_REDUCAO_VALOR_BEM_CONTABIL = "INSERT INTO REDUCAOVALORBEMCONTABIL (ID, LOTEREDUCAOVALORBEM_ID, BEM_ID, BENSMOVEIS_ID, GRUPOPATRIMONIAL_ID, VALOR, DATALANCAMENTO, SITUACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<ReducaoValorBemContabil> reducoes;
    private final SingletonGeradorId geradorDeIds;

    public ReducaoValorBemContabilSetter(List<ReducaoValorBemContabil> reducoes, SingletonGeradorId geradorDeIds) {
        this.reducoes = reducoes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ReducaoValorBemContabil reducao = reducoes.get(i);
        reducao.setId(geradorDeIds.getProximoId());
        ps.setLong(1, reducao.getId());
        ps.setLong(2, reducao.getLoteReducaoValorBem().getId());
        ps.setLong(3, reducao.getBem().getId());
        ps.setNull(4, Types.NULL);
        ps.setLong(5, reducao.getGrupoPatrimonial().getId());
        ps.setBigDecimal(6, reducao.getValor());
        ps.setTimestamp(7, new Timestamp(reducao.getDataLancamento().getTime()));
        ps.setString(8, reducao.getSituacao().name());
    }

    @Override
    public int getBatchSize() {
        return reducoes.size();
    }
}
