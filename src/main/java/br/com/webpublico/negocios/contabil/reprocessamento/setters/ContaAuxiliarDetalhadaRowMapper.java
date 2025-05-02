package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.SubSistema;
import br.com.webpublico.enums.TipoContaContabil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContaAuxiliarDetalhadaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ContaAuxiliarDetalhada contaAuxiliarDetalhada = new ContaAuxiliarDetalhada();
        contaAuxiliarDetalhada.setId(rs.getLong("ID"));
        contaAuxiliarDetalhada.setAtiva(rs.getBoolean("ATIVA"));
        contaAuxiliarDetalhada.setCodigo(rs.getString("CODIGO"));
        contaAuxiliarDetalhada.setDataRegistro(rs.getDate("DATAREGISTRO"));
        contaAuxiliarDetalhada.setDescricao(rs.getString("DESCRICAO"));
        contaAuxiliarDetalhada.setFuncao(rs.getString("FUNCAO"));
        contaAuxiliarDetalhada.setPermitirDesdobramento(rs.getBoolean("PERMITIRDESDOBRAMENTO"));
        contaAuxiliarDetalhada.setRubrica(rs.getString("RUBRICA"));
        contaAuxiliarDetalhada.setCodigoCO(rs.getString("CODIGOCO"));
        if (rs.getString("TIPOCONTACONTABIL") != null) {
            contaAuxiliarDetalhada.setTipoContaContabil(TipoContaContabil.valueOf(rs.getString("TIPOCONTACONTABIL")));
        }
        PlanoDeContas planoDeContas = new PlanoDeContas();
        planoDeContas.setId(rs.getLong("PLANODECONTAS_ID"));
        contaAuxiliarDetalhada.setPlanoDeContas(planoDeContas);
        Conta superior = new Conta();
        superior.setId(rs.getLong("SUPERIOR_ID"));
        contaAuxiliarDetalhada.setSuperior(superior);
        Conta contaDeDestinacao = new Conta();
        contaDeDestinacao.setId(rs.getLong("CONTADEDESTINACAO_ID"));
        contaAuxiliarDetalhada.setContaDeDestinacao(contaDeDestinacao);
        contaAuxiliarDetalhada.setdType(rs.getString("DTYPE"));
        Exercicio exercicio = new Exercicio();
        exercicio.setId(rs.getLong("EXERCICIO_ID"));
        contaAuxiliarDetalhada.setExercicio(exercicio);
        if (rs.getString("CATEGORIA") != null) {
            contaAuxiliarDetalhada.setCategoria(CategoriaConta.valueOf(rs.getString("CATEGORIA")));
        }
        contaAuxiliarDetalhada.setCodigoTCE(rs.getString("CODIGOTCE"));
        contaAuxiliarDetalhada.setCodigoSICONFI(rs.getString("CODIGOSICONFI"));
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setId(rs.getLong("CONTACONTABIL_ID"));
        contaAuxiliarDetalhada.setContaContabil(contaContabil);
        Conta conta = new Conta();
        conta.setId(rs.getLong("CONTA_ID"));
        contaAuxiliarDetalhada.setConta(conta);
        Exercicio exercicioAtual = new Exercicio();
        exercicioAtual.setId(rs.getLong("EXERCICIOATUAL_ID"));
        contaAuxiliarDetalhada.setExercicioAtual(exercicioAtual);
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("unidadeOrganizacional_id"));
        contaAuxiliarDetalhada.setUnidadeOrganizacional(unidadeOrganizacional);
        if (rs.getString("subSistema") != null) {
            contaAuxiliarDetalhada.setSubSistema(SubSistema.valueOf(rs.getString("subSistema")));
        }
        contaAuxiliarDetalhada.setDividaConsolidada(rs.getInt("dividaConsolidada"));
        contaAuxiliarDetalhada.setEs(rs.getInt("es"));
        contaAuxiliarDetalhada.setClassificacaoFuncional(rs.getString("classificacaoFuncional"));
        contaAuxiliarDetalhada.setHashContaAuxiliarDetalhada(rs.getInt("hashContaAuxiliarDetalhada"));
        TipoContaAuxiliar tipoContaAuxiliar = new TipoContaAuxiliar();
        tipoContaAuxiliar.setId(rs.getLong("tipoContaAuxiliar_id"));
        contaAuxiliarDetalhada.setTipoContaAuxiliar(tipoContaAuxiliar);
        Exercicio exercicioOrigem = new Exercicio();
        exercicioOrigem.setId(rs.getLong("EXERCICIOORIGEM_ID"));
        contaAuxiliarDetalhada.setExercicioOrigem(exercicioOrigem);
        return contaAuxiliarDetalhada;
    }
}
