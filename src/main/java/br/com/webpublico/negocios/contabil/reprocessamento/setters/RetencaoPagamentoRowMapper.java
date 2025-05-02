package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoConsignacao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RetencaoPagamentoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        RetencaoPgto retencaoPgto = new RetencaoPgto();
        retencaoPgto.setId(rs.getLong("ID"));
        retencaoPgto.setNumero(rs.getString("NUMERO"));
        retencaoPgto.setValor(rs.getBigDecimal("VALOR"));
        retencaoPgto.setDataRetencao(rs.getDate("DATARETENCAO"));
        retencaoPgto.setSaldo(rs.getBigDecimal("SALDO"));
        retencaoPgto.setComplementoHistorico(rs.getString("COMPLEMENTOHISTORICO"));
        SubConta subConta = new SubConta();
        subConta.setId(rs.getLong("SUBCONTA_ID"));
        retencaoPgto.setSubConta(subConta.getId() != 0 ? subConta : null);
        FonteDeRecursos fonteDeRecursos = new FonteDeRecursos();
        fonteDeRecursos.setId(rs.getLong("FONTEDERECURSOS_ID"));
        retencaoPgto.setFonteDeRecursos(fonteDeRecursos.getId() != 0 ? fonteDeRecursos : null);
        ContaExtraorcamentaria contaExtraorcamentaria = new ContaExtraorcamentaria();
        contaExtraorcamentaria.setId(rs.getLong("CONTAEXTRAORCAMENTARIA_ID"));
        retencaoPgto.setContaExtraorcamentaria(contaExtraorcamentaria.getId() != 0 ? contaExtraorcamentaria : null);
        UsuarioSistema usuarioSistema = new UsuarioSistema();
        usuarioSistema.setId(rs.getLong("USUARIOSISTEMA_ID"));
        retencaoPgto.setUsuarioSistema(usuarioSistema.getId() != 0 ? usuarioSistema : null);
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getLong("PAGAMENTO_ID"));
        retencaoPgto.setPagamento(pagamento.getId() != 0 ? pagamento : null);
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("UNIDADEORGANIZACIONAL_ID"));
        retencaoPgto.setUnidadeOrganizacional(unidadeOrganizacional.getId() != 0 ? unidadeOrganizacional : null);
        retencaoPgto.setEfetivado(rs.getBoolean("EFETIVADO"));
        retencaoPgto.setEstornado(rs.getBoolean("ESTORNADO"));
        Pessoa pessoa = createNewPessoa();
        pessoa.setId(rs.getLong("PESSOA_ID"));
        retencaoPgto.setPessoa(pessoa.getId() != 0 ? pessoa : null);
        ClasseCredor classeCredor = new ClasseCredor();
        classeCredor.setId(rs.getLong("CLASSECREDOR_ID"));
        retencaoPgto.setClasseCredor(classeCredor.getId() != 0 ? classeCredor : null);
        retencaoPgto.setTipoConsignacao(TipoConsignacao.valueOf(rs.getString("TIPOCONSIGNACAO")));
        PagamentoEstorno pagamentoEstorno = new PagamentoEstorno();
        pagamentoEstorno.setId(rs.getLong("PAGAMENTOESTORNO_ID"));
        retencaoPgto.setPagamentoEstorno(pagamentoEstorno.getId() != 0 ? pagamentoEstorno : null);
        return retencaoPgto;
    }

    public Pessoa createNewPessoa(){
        Pessoa pessoa = new Pessoa() {
            @Override
            public String getNome() {
                return null;
            }

            @Override
            public String getNomeTratamento() {
                return null;
            }

            @Override
            public String getNomeFantasia() {
                return null;
            }

            @Override
            public String getCpf_Cnpj() {
                return null;
            }

            @Override
            public String getRg_InscricaoEstadual() {
                return null;
            }

            @Override
            public String getOrgaoExpedidor() {
                return null;
            }

            @Override
            public int compareTo(Pessoa o) {
                return 0;
            }
        };
        return pessoa;
    }
}
