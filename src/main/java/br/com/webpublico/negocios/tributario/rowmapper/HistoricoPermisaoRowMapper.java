package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidadesauxiliares.VOHistoricoPermissao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 14/01/14
 * Time: 18:30
 */
public class HistoricoPermisaoRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        VOHistoricoPermissao historico = new VOHistoricoPermissao();

        historico.setDataAlteracao(resultSet.getString("dataAlteracao"));
        historico.setMotivoAlteracao(resultSet.getString("motivoAlteracao"));
        historico.setNomePermissionario(resultSet.getString("nomePermissionario"));
        historico.setCpfPermissionario(resultSet.getString("cpfPermissionario"));
        historico.setCmcPermissionario(resultSet.getString("cmcPermissionario"));
        historico.setEnderecoPermissionario(resultSet.getString("enderecoPermissionario"));
        historico.setCepPermissionario(resultSet.getString("cepPermissionario"));
        historico.setBairroPermissionario(resultSet.getString("bairroPermissionario"));
        historico.setNomeAuxiliar1(resultSet.getString("nomeAuxiliar1"));
        historico.setCpfAuxiliar1(resultSet.getString("cpfAuxiliar1"));
        historico.setCmcAuxiliar1(resultSet.getString("cmcAuxiliar1"));
        historico.setEnderecoAuxiliar1(resultSet.getString("enderecoAuxiliar1"));
        historico.setCepAuxiliar1(resultSet.getString("cepAuxiliar1"));
        historico.setBairroAuxiliar1(resultSet.getString("bairroAuxiliar1"));
        historico.setNomeAuxiliar2(resultSet.getString("nomeAuxiliar2"));
        historico.setCpfAuxiliar2(resultSet.getString("cpfAuxiliar2"));
        historico.setCmcAuxiliar2(resultSet.getString("cmcAuxiliar2"));
        historico.setEnderecoAuxiliar2(resultSet.getString("enderecoAuxiliar2"));
        historico.setCepAuxiliar2(resultSet.getString("cepAuxiliar2"));
        historico.setBairroAuxiliar2(resultSet.getString("bairroAuxiliar2"));
        historico.setEspecieVeiculo(resultSet.getString("especieVeiculo"));
        historico.setTipoCombustivel(resultSet.getString("tipoCombustivel"));
        historico.setMarcaVeiculo(resultSet.getString("marcaVeiculo"));
        historico.setModeloVeiculo(resultSet.getString("modeloVeiculo"));
        historico.setCorVeiculo(resultSet.getString("corVeiculo"));
        historico.setAnoFabricacaoVeiculo(resultSet.getString("anoFabricacaoVeiculo"));
        historico.setAnoModeloVeiculo(resultSet.getString("anoModeloVeiculo"));

        return historico;
    }
}
