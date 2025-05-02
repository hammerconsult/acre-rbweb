package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class IsencaoCalculoIPTUSetter implements BatchPreparedStatementSetter {

    SingletonGeradorId geradorDeIds;
    private List<IsencaoCadastroImobiliario> isencoes;

    public IsencaoCalculoIPTUSetter(List<IsencaoCadastroImobiliario> isencoes, SingletonGeradorId geradorDeIds) {
        this.isencoes = isencoes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        IsencaoCadastroImobiliario isencao = isencoes.get(i);
        isencao.setId(geradorDeIds.getProximoId());
        ps.setLong(1, isencao.getId());
        ps.setLong(2, isencao.getProcessoIsencaoIPTU().getId());
        ps.setDate(3, new java.sql.Date(isencao.getInicioVigencia().getTime()));
        if (isencao.getFinalVigencia() != null) {
            ps.setDate(4, new java.sql.Date(isencao.getFinalVigencia().getTime()));
        }else{
            ps.setNull(4, Types.DATE);
        }
        ps.setBoolean(5, isencao.getLancaImposto());
        ps.setBoolean(6, isencao.getLancaTaxa());
        ps.setLong(7, isencao.getSequencia());
        ps.setString(8, isencao.getTipoLancamentoIsencao().name());
        ps.setLong(9, isencao.getCadastroImobiliario().getId());
        ps.setString(10, isencao.getSituacao().name());
        ps.setBigDecimal(11, isencao.getPercentual());
    }

    @Override
    public int getBatchSize() {
        return isencoes.size();
    }
}
