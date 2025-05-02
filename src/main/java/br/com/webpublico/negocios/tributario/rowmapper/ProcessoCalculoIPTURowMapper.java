/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.ConfiguracaoEventoIPTU;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.enums.ReferenciaImposto;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.UtilizacaoImovel;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Wellington
 */
public class ProcessoCalculoIPTURowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ProcessoCalculoIPTU processoCalculoIPTU = new ProcessoCalculoIPTU();
        processoCalculoIPTU.setId(rs.getLong("ID"));
        processoCalculoIPTU.setCadastroInical(rs.getString("CADASTROINICAL"));
        processoCalculoIPTU.setCadastroFinal(rs.getString("CADASTROFINAL"));
        processoCalculoIPTU.setConfiguracaoEventoIPTU(new ConfiguracaoEventoIPTU());
        processoCalculoIPTU.getConfiguracaoEventoIPTU().setId(rs.getLong("CONFIGURACAOEVENTOIPTU_ID"));
        processoCalculoIPTU.getConfiguracaoEventoIPTU().setCodigo(rs.getInt("CFG_CODIGO"));
        processoCalculoIPTU.getConfiguracaoEventoIPTU().setDescricao(rs.getString("CFG_DESCRICAO"));
        processoCalculoIPTU.setDescontoSobreValorVenalConstrucao(rs.getBigDecimal("DESCONTOVALOVENALCONSTRUCAO"));
        processoCalculoIPTU.setDescontoSobreValorVenalTerreno(rs.getBigDecimal("DESCONTOVALOVENALTERRENO"));
        processoCalculoIPTU.setLog(rs.getString("LOG"));
        if (rs.getString("REFERENCIAIMPOSTO") != null) {
            processoCalculoIPTU.setReferenciaImposto(ReferenciaImposto.valueOf(rs.getString("REFERENCIAIMPOSTO")));
        }
        if (rs.getString("TIPOIMOVEL") != null) {
            processoCalculoIPTU.setTipoImovel(TipoImovel.valueOf(rs.getString("TIPOIMOVEL")));
        }
        if (rs.getString("UTILIZACAOIMOVEL") != null) {
            processoCalculoIPTU.setUtilizacaoImovel(UtilizacaoImovel.valueOf(rs.getString("UTILIZACAOIMOVEL")));
        }
        processoCalculoIPTU.setValorMinimoImpostoPredial(rs.getBigDecimal("VALORMINIMOIMPOSTOPREDIAL"));
        processoCalculoIPTU.setValorMinimoImpostoTerritorial(rs.getBigDecimal("VALORMINIMOIMPOSTOTERRITORIAL"));
        processoCalculoIPTU.setDescricao(rs.getString("DESCRICAO"));
        return processoCalculoIPTU;
    }
}
