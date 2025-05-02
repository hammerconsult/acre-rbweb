package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassePessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PessoaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        String cpf = rs.getString("cpf");
        String cnpj = rs.getString("cnpj");
        Pessoa pessoa = null;
        if (cpf != null) {
            pessoa = new PessoaFisica();
            ((PessoaFisica) pessoa).setCpf(cpf);
        } else if (cnpj != null) {
            pessoa = new PessoaJuridica();
            ((PessoaJuridica) pessoa).setCnpj(cnpj);
        }

        pessoa.setId(rs.getLong("ID"));
        pessoa.setDataRegistro(rs.getDate("DATAREGISTRO"));
        pessoa.setEmail(rs.getString("EMAIL"));
        pessoa.setHomePage(rs.getString("HOMEPAGE"));
        ContaCorrenteContribuinte contaCorrenteContribuinte = new ContaCorrenteContribuinte();
        contaCorrenteContribuinte.setId(rs.getLong("CONTACORRENTECONTRIBUINTE_ID"));
        pessoa.setContaCorrenteContribuinte(contaCorrenteContribuinte.getId() != 0 ? contaCorrenteContribuinte : null);
        Nacionalidade nacionalidade = new Nacionalidade();
        nacionalidade.setId(rs.getLong("NACIONALIDADE_ID"));
        pessoa.setNacionalidade(nacionalidade.getId() != 0 ? nacionalidade : null);
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.valueOf(rs.getString("SITUACAOCADASTRALPESSOA")));
        pessoa.setMigracaoChave(rs.getString("MIGRACAOCHAVE"));
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("UNIDADEORGANIZACIONAL_ID"));
        pessoa.setUnidadeOrganizacional(unidadeOrganizacional.getId() != 0 ? unidadeOrganizacional : null);
        pessoa.setMotivo(rs.getString("MOTIVO"));
        pessoa.setBloqueado(rs.getBoolean("BLOQUEADO"));
        pessoa.setClassePessoa(ClassePessoa.valueOf(rs.getString("CLASSEPESSOA")));
        UnidadeExterna unidadeExterna = new UnidadeExterna();
        unidadeExterna.setId(rs.getLong("UNIDADEEXTERNA_ID"));
        pessoa.setUnidadeExterna(unidadeExterna.getId() != 0 ? unidadeExterna : null);
        pessoa.setObservacao(rs.getString("OBSERVACAO"));
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setId(rs.getLong("ENDERECOPRINCIPAL_ID"));
        pessoa.setEnderecoPrincipal(enderecoCorreio.getId() != 0 ? enderecoCorreio : null);
        Profissao profissao = new Profissao();
        profissao.setId(rs.getLong("PROFISSAO_ID"));
        pessoa.setProfissao(profissao.getId() != 0 ? profissao : null);
        Telefone telefone = new Telefone();
        telefone.setId(rs.getLong("TELEFONEPRINCIPAL_ID"));
        pessoa.setTelefonePrincipal(telefone.getId() != 0 ? telefone : null);
        ContaCorrenteBancPessoa contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        contaCorrenteBancPessoa.setId(rs.getLong("CONTACORRENTEPRINCIPAL_ID"));
        pessoa.setContaCorrentePrincipal(contaCorrenteBancPessoa.getId() != 0 ? contaCorrenteBancPessoa : null);
        DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
        detentorArquivoComposicao.setId(rs.getLong("DETENTORARQUIVOCOMPOSICAO_ID"));
        pessoa.setDetentorArquivoComposicao(detentorArquivoComposicao.getId() != 0 ? detentorArquivoComposicao : null);
        Arquivo arquivo = new Arquivo();
        arquivo.setId(rs.getLong("ARQUIVO_ID"));
        pessoa.setArquivo(arquivo.getId() != 0 ? arquivo : null);
        pessoa.setCodigoCnaeBI(rs.getString("CODIGOCNAEBI"));
        pessoa.setReceitaBrutaCPRB(rs.getBoolean("RECEITABRUTACPRB"));
        return pessoa;
    }
}
