/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoEventoContabilDTO;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @author major Favor deixar em ordem alfabetica.
 */
public enum TipoEventoContabil {

    ABERTURA("Abertura de Exercício", TipoEventoContabilDTO.ABERTURA),
    OBRIGACAO_APAGAR("Obrigação a Pagar", TipoEventoContabilDTO.OBRIGACAO_APAGAR),
    AJUSTE_ATIVO_DISPONIVEL("Ajuste em Ativo Disponível", TipoEventoContabilDTO.AJUSTE_ATIVO_DISPONIVEL),
    AJUSTE_DEPOSITO("Ajuste em Depósitos", TipoEventoContabilDTO.AJUSTE_DEPOSITO),
    AJUSTE_CONTABIL_MANUAL("Ajuste Contábil Manual", TipoEventoContabilDTO.AJUSTE_CONTABIL_MANUAL),
    APURACAO("Apuração de Exercício", TipoEventoContabilDTO.APURACAO),
    ATO_POTENCIAL("Ato Potencial", TipoEventoContabilDTO.ATO_POTENCIAL),
    BENS_ESTOQUE("Bens de Estoque", TipoEventoContabilDTO.BENS_ESTOQUE),
    BENS_IMOVEIS("Bens Imóveis", TipoEventoContabilDTO.BENS_IMOVEIS),
    BENS_INTANGIVEIS("Bens Intangíveis", TipoEventoContabilDTO.BENS_INTANGIVEIS),
    BENS_MOVEIS("Bens móveis", TipoEventoContabilDTO.BENS_MOVEIS),
    RESPONSABILIDADE_VTB("Responsabilidade por Valores, Títulos e Bens", TipoEventoContabilDTO.RESPONSABILIDADE_VTB),
    CREDITO_ADICIONAL("Crédito Adicional", TipoEventoContabilDTO.CREDITO_ADICIONAL),
    CREDITO_INICIAL("Crédito Inicial", TipoEventoContabilDTO.CREDITO_INICIAL),
    CREDITO_RECEBER("Créditos a Receber", TipoEventoContabilDTO.CREDITO_RECEBER),
    DESPESA_EXTRA_ORCAMENTARIA("Despesa Extra Orçamentária", TipoEventoContabilDTO.DESPESA_EXTRA_ORCAMENTARIA),
    DIARIA_CAMPO("Diárias de Campo", TipoEventoContabilDTO.DIARIA_CAMPO),
    DIARIAS_CIVIL("Diárias Cívil", TipoEventoContabilDTO.DIARIAS_CIVIL),
    DIVIDA_ATIVA("Dívida Ativa", TipoEventoContabilDTO.DIVIDA_ATIVA),
    DIVIDA_PUBLICA("Dívida Pública", TipoEventoContabilDTO.DIVIDA_PUBLICA),
    ENCERRAMENTO("Encerramento de Exercício", TipoEventoContabilDTO.ENCERRAMENTO),
    EMPENHO_DESPESA("Empenho de Despesa", TipoEventoContabilDTO.EMPENHO_DESPESA),
    EXECUCAO_DESPESA("Execução da Despesa", TipoEventoContabilDTO.EXECUCAO_DESPESA),
    EXECUCAO_RECEITA("Execução da Receita", TipoEventoContabilDTO.EXECUCAO_RECEITA),
    EXECUCAO_RECURSO("Execução do Recurso", TipoEventoContabilDTO.EXECUCAO_RECURSO),
    INVESTIMENTO("Investimento", TipoEventoContabilDTO.INVESTIMENTO),
    LIBERACAO_FINANCEIRA("Liberação Financeira", TipoEventoContabilDTO.LIBERACAO_FINANCEIRA),
    LIQUIDACAO_DESPESA("Liquidação de Despesa", TipoEventoContabilDTO.LIQUIDACAO_DESPESA),
    LIQUIDACAO_RESTO_PAGAR("Liquidação de Restos a Pagar", TipoEventoContabilDTO.LIQUIDACAO_RESTO_PAGAR),
    NAO_APLICAVEL("Não Aplicavel", TipoEventoContabilDTO.NAO_APLICAVEL),
    PAGAMENTO_DESPESA("Pagamento de Despesa", TipoEventoContabilDTO.PAGAMENTO_DESPESA),
    PAGAMENTO_RESTO_PAGAR("Pagamento de Restos a Pagar", TipoEventoContabilDTO.PAGAMENTO_RESTO_PAGAR),
    PATRIMONIO_LIQUIDO("Patrimônio Líquido", TipoEventoContabilDTO.PATRIMONIO_LIQUIDO),
    PREVISAO_ADICIONAL_RECEITA("Previsão adicional da Receita", TipoEventoContabilDTO.PREVISAO_ADICIONAL_RECEITA),
    PREVISAO_INICIAL_RECEITA("Previsão inicial da Receita", TipoEventoContabilDTO.PREVISAO_INICIAL_RECEITA),
    RESTO_PAGAR("Inscrição de Resto a Pagar", TipoEventoContabilDTO.RESTO_PAGAR),
    CANCELAMENTO_RESTO_PAGAR("Cancelamento de Resto a Pagar", TipoEventoContabilDTO.CANCELAMENTO_RESTO_PAGAR),
    PROVISAO_MATEMATICA_PREVIDENCIARIA("Provisão Matemática Previdenciária", TipoEventoContabilDTO.PROVISAO_MATEMATICA_PREVIDENCIARIA),
    RECEITA_EXTRA_ORCAMENTARIA("Receita Extra Orçamentária", TipoEventoContabilDTO.RECEITA_EXTRA_ORCAMENTARIA),
    RECEITA_REALIZADA("Receita Realizada", TipoEventoContabilDTO.RECEITA_REALIZADA),
    SUPRIMENTO_FUNDO("Suprimento de Fundos", TipoEventoContabilDTO.SUPRIMENTO_FUNDO),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira", TipoEventoContabilDTO.TRANSFERENCIA_FINANCEIRA),
    TRANSFERENCIA_MESMA_UNIDADE("Transferência Mesma Unidade", TipoEventoContabilDTO.TRANSFERENCIA_MESMA_UNIDADE),
    TRANSFERENCIA_BENS_MOVEIS("Transferência de Bens Móveis", TipoEventoContabilDTO.TRANSFERENCIA_BENS_MOVEIS),
    TRANSFERENCIA_BENS_IMOVEIS("Transferência de Bens Imóveis", TipoEventoContabilDTO.TRANSFERENCIA_BENS_IMOVEIS),
    TRANSFERENCIA_BENS_INTANGIVEIS("Transferência de Bens Intangíveis", TipoEventoContabilDTO.TRANSFERENCIA_BENS_INTANGIVEIS),
    TRANSFERENCIA_BENS_ESTOQUE("Transferência de Bens de Estoque", TipoEventoContabilDTO.TRANSFERENCIA_BENS_ESTOQUE),
    TRANSFERENCIA_RESULTADO("Transferência de Resultado", TipoEventoContabilDTO.TRANSFERENCIA_RESULTADO);

    private String descricao;
    private TipoEventoContabilDTO toDto;

    TipoEventoContabil(String descricao, TipoEventoContabilDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoEventoContabilDTO getToDto() {
        return toDto;
    }

    public static List<TipoEventoContabil> getTiposEventoContabilRetirandoInicialLOA() {
        List<TipoEventoContabil> retorno = Lists.newArrayList();
        retorno.addAll(Arrays.asList(TipoEventoContabil.values()));
        retorno.remove(TipoEventoContabil.CREDITO_INICIAL);
        retorno.remove(TipoEventoContabil.PREVISAO_INICIAL_RECEITA);
        return retorno;
    }

    public static List<TipoEventoContabil> getTiposEventoContabilInicialLOA() {
        List<TipoEventoContabil> retorno = Lists.newArrayList();
        retorno.add(TipoEventoContabil.CREDITO_INICIAL);
        retorno.add(TipoEventoContabil.PREVISAO_INICIAL_RECEITA);
        return retorno;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
