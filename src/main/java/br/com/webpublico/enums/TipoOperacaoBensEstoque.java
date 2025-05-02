package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoOperacaoBensEstoqueDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 07/03/14
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public enum TipoOperacaoBensEstoque {

    AQUISICAO_BENS_ESTOQUE("Aquisição de bens de estoque", TipoOperacaoBensEstoqueDTO.AQUISICAO_BENS_ESTOQUE),
    INCORPORACAO_BENS_ESTOQUE("Incorporação de bens de estoque", TipoOperacaoBensEstoqueDTO.INCORPORACAO_BENS_ESTOQUE),
    AJUSTE_PERDAS_BENS_ESTOQUE("Ajuste de perdas de bens de estoque", TipoOperacaoBensEstoqueDTO.AJUSTE_PERDAS_BENS_ESTOQUE),
    AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE("Ajuste para redução ao valor de mercado de bens de estoque", TipoOperacaoBensEstoqueDTO.AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE),
    REVERSAO_AJUSTE_PERDAS_BENS_ESTOQUE("Reversão de ajuste de perdas de bens de estoque", TipoOperacaoBensEstoqueDTO.REVERSAO_AJUSTE_PERDAS_BENS_ESTOQUE),
    REVERSAO_AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE("Reversão de ajuste para redução ao valor de mercado de bens de estoque", TipoOperacaoBensEstoqueDTO.REVERSAO_AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE),
    TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA("Transferência de bens de estoque recebida", TipoOperacaoBensEstoqueDTO.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA),
    TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA("Transferência de bens de estoque concedida", TipoOperacaoBensEstoqueDTO.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA),
    REAVALIACAO_BENS_ESTOQUE_AUMENTATIVA("Reavaliação de bens de estoque aumentativa", TipoOperacaoBensEstoqueDTO.REAVALIACAO_BENS_ESTOQUE_AUMENTATIVA),
    REAVALIACAO_BENS_ESTOQUE_DIMINUTIVA("Reavaliação de bens de estoque diminutiva", TipoOperacaoBensEstoqueDTO.REAVALIACAO_BENS_ESTOQUE_DIMINUTIVA),
    BAIXA_BENS_ESTOQUE_POR_CONSUMO("Baixa de bens de estoque por consumo", TipoOperacaoBensEstoqueDTO.BAIXA_BENS_ESTOQUE_POR_CONSUMO),
    BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO("Baixa de bens de estoque por desincorporação", TipoOperacaoBensEstoqueDTO.BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO);
    private String descricao;
    private TipoOperacaoBensEstoqueDTO toDto;

    TipoOperacaoBensEstoque(String descricao, TipoOperacaoBensEstoqueDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<String> getOperacoesAsString(List<TipoOperacaoBensEstoque> operacoes) {
        List<String> toReturn = Lists.newArrayList();
        for (TipoOperacaoBensEstoque tipo : operacoes) {
            toReturn.add(tipo.name());
        }
        return toReturn;
    }

    public static List<TipoOperacaoBensEstoque> getOperacoesDebitoIntegracaoMateriais() {
        List<TipoOperacaoBensEstoque> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO);
        toReturn.add(TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO);
        toReturn.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA);
        return toReturn;
    }

    public static List<TipoOperacaoBensEstoque> getOperacoesCreditoIntegracaoMateriais() {
        List<TipoOperacaoBensEstoque> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensEstoque.INCORPORACAO_BENS_ESTOQUE);
        toReturn.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
        return toReturn;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoBensEstoqueDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
