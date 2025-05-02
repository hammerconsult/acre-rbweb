package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoPessoaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setId(rs.getLong("ENDERECO_ID"));
        enderecoCorreio.setBairro(rs.getString("BAIRRO"));
        enderecoCorreio.setLogradouro(rs.getString("LOGRADOURO"));
        enderecoCorreio.setCep(rs.getString("CEP"));
        enderecoCorreio.setComplemento(rs.getString("COMPLEMENTO"));
        enderecoCorreio.setLocalidade(rs.getString("LOCALIDADE"));
        enderecoCorreio.setNumero(rs.getString("NUMERO"));
        enderecoCorreio.setTipoEndereco(rs.getString("TIPOENDERECO") != null ? TipoEndereco.valueOf(rs.getString("TIPOENDERECO")) : null);
        enderecoCorreio.setTipoLogradouro(rs.getString("TIPOLOGRADOURO") != null ? TipoLogradouroEnderecoCorreio.valueOf("TIPOLOGRADOURO") : null);
        enderecoCorreio.setUf(rs.getString("UF"));
        return enderecoCorreio;
    }
}
