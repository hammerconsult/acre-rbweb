package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.Ocorrencia;
import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.interfaces.DetentorDeOcorrencia;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PessoaFisicaRFBSetter implements BatchPreparedStatementSetter {
    private List<PessoaFisicaRFB> pessoas;
    private SingletonGeradorId geradorDeIds;

    public PessoaFisicaRFBSetter(List<PessoaFisicaRFB> pessoas,
                                 SingletonGeradorId gerador) {
        this.pessoas = pessoas;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        PessoaFisicaRFB pessoaFisicaRFB = pessoas.get(i);
        pessoaFisicaRFB.setId(geradorDeIds.getProximoId());

        ps.setLong(1, pessoaFisicaRFB.getId());
        ps.setString(2, pessoaFisicaRFB.getCpf());
        ps.setString(3, pessoaFisicaRFB.getNome());
        ps.setString(4, pessoaFisicaRFB.getNomeMae());
        ps.setDate(5, DateUtils.toSQLDate(pessoaFisicaRFB.getDataNascimento()));
        ps.setString(6, pessoaFisicaRFB.getBairro());
        ps.setString(7, pessoaFisicaRFB.getTipoLogradouro());
        ps.setString(8, pessoaFisicaRFB.getLogradouro());
        ps.setString(9, pessoaFisicaRFB.getNumero());
        ps.setString(10, pessoaFisicaRFB.getComplemento());
        ps.setString(11, pessoaFisicaRFB.getCep());
        ps.setString(12, pessoaFisicaRFB.getMunicipio());
        ps.setString(13, pessoaFisicaRFB.getDdd());
        ps.setString(14, pessoaFisicaRFB.getTelefone());
        ps.setString(15, pessoaFisicaRFB.getEmail());
        ps.setString(16, pessoaFisicaRFB.getSituacao().name());
    }

    @Override
    public int getBatchSize() {
        return pessoas.size();
    }
}
