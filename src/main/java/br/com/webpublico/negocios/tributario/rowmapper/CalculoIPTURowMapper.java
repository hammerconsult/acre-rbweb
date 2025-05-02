/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Wellington
 */
public class CalculoIPTURowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        CalculoIPTU calculoIPTU = new CalculoIPTU();
        calculoIPTU.setId(rs.getLong("ID"));
        calculoIPTU.setCadastroImobiliario(new CadastroImobiliario(rs.getLong("CADASTRO_ID"), null));
        calculoIPTU.setCadastro(calculoIPTU.getCadastroImobiliario());
        calculoIPTU.setConsistente(rs.getBoolean("CONSISTENTE"));
        calculoIPTU.setConstrucao(new Construcao(rs.getLong("CONSTRUCAO_ID")));
        calculoIPTU.setIsencaoCadastroImobiliario(new IsencaoCadastroImobiliario(rs.getLong("ISENCAOCADASTROIMOBILIARIO_ID")));
        calculoIPTU.setDataCalculo(rs.getDate("DATACALCULO"));
        calculoIPTU.setIsento(rs.getBoolean("ISENTO"));
        calculoIPTU.setProcessoCalculoIPTU(new ProcessoCalculoIPTU());
        calculoIPTU.getProcessoCalculoIPTU().setId(rs.getLong("PROCESSOCALCULOIPTU_ID"));
        calculoIPTU.setSimulacao(rs.getBoolean("SIMULACAO"));
        calculoIPTU.setSubDivida(rs.getLong("SUBDIVIDA"));
        calculoIPTU.setValorEfetivo(rs.getBigDecimal("VALOREFETIVO"));
        calculoIPTU.setValorReal(rs.getBigDecimal("VALORREAL"));
        return calculoIPTU;
    }

}
