package br.com.webpublico.enums.administrativo;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.webreportdto.dto.administrativo.OperacaoMovimentacaoBemDTO;

/**
 * Created by William on 01/11/2018.
 */
public enum OperacaoMovimentacaoBem {

    NAO_APLICAVEL("Não Aplicável", "", 0, AgrupadorMovimentoBem.NAO_APLICAVEL, OperacaoMovimentacaoBemDTO.NAO_APLICAVEL),

    SOLICITACAO_AQUISICAO_BEM("Solicitação de Aquisição de Bens", "Movimento 1", 1, AgrupadorMovimentoBem.AQUISICAO, OperacaoMovimentacaoBemDTO.SOLICITACAO_AQUISICAO_BEM),
    AQUISICAO_BEM("Aquisição de Bens", "Movimento 1", 1, AgrupadorMovimentoBem.AQUISICAO, OperacaoMovimentacaoBemDTO.AQUISICAO_BEM),
    ESTORNO_AQUISICAO_BEM("Estorno de Aquisição de Bens", "Movimento 1", 1, AgrupadorMovimentoBem.AQUISICAO, OperacaoMovimentacaoBemDTO.ESTORNO_AQUISICAO_BEM),

    INCORPORACAO_BEM("Incorporação de Bens", "Movimento 1", 1, AgrupadorMovimentoBem.INCORPORACAO, OperacaoMovimentacaoBemDTO.INCORPORACAO_BEM),

    IMPLANTACAO_LEVANTAMENTO("Implantação do Levantamento", "Movimento 1", 1, AgrupadorMovimentoBem.LEVANTAMENTO, OperacaoMovimentacaoBemDTO.IMPLANTACAO_LEVANTAMENTO),

    DEPRECIACAO("Depreciação", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.DEPRECIACAO),
    ESTORNO_DEPRECIACAO("Estorno de Depreciação", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.ESTORNO_DEPRECIACAO),
    AMORTIZACAO("Amortização", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.AMORTIZACAO),
    ESTORNO_AMORTIZACAO("Estorno de Amortização", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.ESTORNO_AMORTIZACAO),
    EXAUSTAO("Exaustão", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.EXAUSTAO),
    ESTORNO_EXAUSTAO("Estorno de Exaustão", "Movimento 3", 3, AgrupadorMovimentoBem.REDUCAO_VALOR_BEM, OperacaoMovimentacaoBemDTO.ESTORNO_EXAUSTAO),

    REDUCAO_VALOR_RECUPERAVEL("Redução ao Valor Recuperável", "Movimento 2", 2, AgrupadorMovimentoBem.REDUCAO_VALOR_RECUPERAVEL, OperacaoMovimentacaoBemDTO.REDUCAO_VALOR_RECUPERAVEL),

    AJUSTE_BENS_DEPRECIACAO_AUMENTATIVO("Ajuste de Bens Depreciação - Aumentativo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_DEPRECIACAO_AUMENTATIVO),
    AJUSTE_BENS_DEPRECIACAO_DIMINUTIVO("Ajuste de Bens Depreciação - Diminutivo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_DEPRECIACAO_DIMINUTIVO),
    AJUSTE_BENS_AMORTIZACAO_AUMENTATIVO("Ajuste de Bens Amortização - Aumentativo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_AMORTIZACAO_AUMENTATIVO),
    AJUSTE_BENS_AMORTIZACAO_DIMINUTIVO("Ajuste de Bens Amortização - Diminutivo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_AMORTIZACAO_DIMINUTIVO),
    AJUSTE_BENS_ORIGINAL_AUMENTATIVO("Ajuste de Bens Original- Aumentativo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_ORIGINAL_AUMENTATIVO),
    AJUSTE_BENS_ORIGINAL_DIMINUTIVO("Ajuste de Bens Original- Diminutivo", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_ORIGINAL_DIMINUTIVO),
    AJUSTE_BENS_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste de Bens Original – Aumentativo Empresa Pública", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA),
    AJUSTE_BENS_ORIGINAL_DIMINUTIVO_EMPRESA_PULICA("Ajuste de Bens Original – Diminutivo Empresa Pública", "Movimento 2", 2, AgrupadorMovimentoBem.AJUSTE_BENS, OperacaoMovimentacaoBemDTO.AJUSTE_BENS_ORIGINAL_DIMINUTIVO_EMPRESA_PULICA),

    SOLICITACAO_REAVALIACAO_BEM("Solicitação de Reavaliação de Bens", "Movimento 2", 2, AgrupadorMovimentoBem.REAVALIACAO_BEM, OperacaoMovimentacaoBemDTO.SOLICITACAO_REAVALIACAO_BEM),
    EFETIVACAO_REAVALIACAO_BEM("Efetivação de Reavaliação de Bens", "Movimento 2", 2, AgrupadorMovimentoBem.REAVALIACAO_BEM, OperacaoMovimentacaoBemDTO.EFETIVACAO_REAVALIACAO_BEM),

    SOLICITACAO_TRANSFERENCIA_BEM("Solicitação de Transferência Bem", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA, OperacaoMovimentacaoBemDTO.SOLICITACAO_TRANSFERENCIA_BEM),
    APROVACAO_TRANSFERENCIA_BEM("Aprovação de Transferência Bem", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA, OperacaoMovimentacaoBemDTO.APROVACAO_TRANSFERENCIA_BEM),
    SOLICITACAO_TRANSFERENCIA_BEM_ESTORNO("Solicitação de Transferência Bem Estorno", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA, OperacaoMovimentacaoBemDTO.SOLICITACAO_TRANSFERENCIA_BEM_ESTORNO),
    EFETIVACAO_TRANSFERENCIA_BEM("Efetivação de Transferência Bem", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA, OperacaoMovimentacaoBemDTO.EFETIVACAO_TRANSFERENCIA_BEM),
    EFETIVACAO_TRANSFERENCIA_BEM_ESTORNO("Efetivação de Transferência Bem Estorno", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA, OperacaoMovimentacaoBemDTO.EFETIVACAO_TRANSFERENCIA_BEM_ESTORNO),

    SOLICITACAO_ALIENACAO_BEM("Solicitação de Alienação", "Movimento 1", 1, AgrupadorMovimentoBem.ALIENACAO, OperacaoMovimentacaoBemDTO.SOLICITACAO_ALIENACAO_BEM),
    APROVACAO_ALIENACAO_BEM("Aprovação de Alienação", "Movimento 1", 1, AgrupadorMovimentoBem.ALIENACAO, OperacaoMovimentacaoBemDTO.APROVACAO_ALIENACAO_BEM),
    AVALIACAO_ALIENACAO_BEM("Avaliação de Alienação", "Movimento 1", 1, AgrupadorMovimentoBem.ALIENACAO, OperacaoMovimentacaoBemDTO.AVALIACAO_ALIENACAO_BEM),
    EFETIVACAO_ALIENACAO_BEM("Efetivação de Alienação", "Movimento 1", 1, AgrupadorMovimentoBem.ALIENACAO, OperacaoMovimentacaoBemDTO.EFETIVACAO_ALIENACAO_BEM),

    SOLICITACAO_BAIXA_PATRIMONIAL("Solicitação Baixa Patrimonial", "Movimento 1", 1, AgrupadorMovimentoBem.BAIXA, OperacaoMovimentacaoBemDTO.SOLICITACAO_BAIXA_PATRIMONIAL),
    PARECER_BAIXA_PATRIMONIAL("Parecer Baixa Patrimonial", "Movimento 1", 1, AgrupadorMovimentoBem.BAIXA, OperacaoMovimentacaoBemDTO.PARECER_BAIXA_PATRIMONIAL),
    EFETIVACAO_BAIXA_PATRIMONIAL("Efetivação Baixa Patrimonial", "Movimento 1", 1, AgrupadorMovimentoBem.BAIXA, OperacaoMovimentacaoBemDTO.EFETIVACAO_BAIXA_PATRIMONIAL),

    ABERTURA_INVENTARIO("Abertura de Inventário", "Movimento 1", 1, AgrupadorMovimentoBem.INVENTARIO, OperacaoMovimentacaoBemDTO.ABERTURA_INVENTARIO),
    FECHAMENTO_INVENTARIO("Fechamento de Inventário", "Movimento 1", 1, AgrupadorMovimentoBem.INVENTARIO, OperacaoMovimentacaoBemDTO.FECHAMENTO_INVENTARIO),

    MANUTENCAO_BEM_REMESSA("Manutenção de Bem - Remessa", "Movimento 1", 1, AgrupadorMovimentoBem.MANUTENCAO, OperacaoMovimentacaoBemDTO.MANUTENCAO_BEM_REMESSA),
    MANUTENCAO_BEM_RETORNO("Manutenção de Bem - Retorno", "Movimento 1", 1, AgrupadorMovimentoBem.MANUTENCAO, OperacaoMovimentacaoBemDTO.MANUTENCAO_BEM_RETORNO),

    SOLICITACAO_CESSAO_BEM("Solicitação de Cessão de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.CESSAO, OperacaoMovimentacaoBemDTO.SOLICITACAO_CESSAO_BEM),
    EFETIVACAO_CESSAO_BEM("Efetivação de Cessão de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.CESSAO, OperacaoMovimentacaoBemDTO.EFETIVACAO_CESSAO_BEM),
    SOLICITACAO_PRORROGACAO_CESSAO_BEM("Solicitação de Prorrogação de Cessão de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.CESSAO, OperacaoMovimentacaoBemDTO.SOLICITACAO_PRORROGACAO_CESSAO_BEM),
    AVALIACAO_PRORROGACAO_CESSAO_BEM("Avaliação de Prorrogação de Cessão de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.CESSAO, OperacaoMovimentacaoBemDTO.AVALIACAO_PRORROGACAO_CESSAO_BEM),
    DEVOLUCAO_CESSAO_BEM("Devolução de Cessão de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.CESSAO, OperacaoMovimentacaoBemDTO.DEVOLUCAO_CESSAO_BEM),

    SOLICITACAO_ALTERACAO_CONSERVACAO_BEM("Solicitação de Alteração de Conservação de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.ALTERACAO_CONSERVACAO, OperacaoMovimentacaoBemDTO.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM),
    EFETIVACAO_ALTERACAO_CONSERVACAO_BEM("Efetivação de Alteração de Conservação de Bem", "Movimento 1", 1, AgrupadorMovimentoBem.ALTERACAO_CONSERVACAO, OperacaoMovimentacaoBemDTO.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM),

    SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL("Solicitação de Transferência Grupo Bem", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL, OperacaoMovimentacaoBemDTO.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL),
    EFETIVACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL("Efetivação de Transferência Grupo Bem", "Movimento 1", 1, AgrupadorMovimentoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL, OperacaoMovimentacaoBemDTO.EFETIVACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
    private String descricao;
    private String movimento;
    private Integer operacao;
    private AgrupadorMovimentoBem agrupadorMovimentoBem;
    private OperacaoMovimentacaoBemDTO dto;

    OperacaoMovimentacaoBem(String descricao, String movimento, Integer operacao, AgrupadorMovimentoBem agrupadorMovimentoBem, OperacaoMovimentacaoBemDTO dto) {
        this.descricao = descricao;
        this.movimento = movimento;
        this.operacao = operacao;
        this.agrupadorMovimentoBem = agrupadorMovimentoBem;
        this.dto = dto;
    }

    public OperacaoMovimentacaoBemDTO getDto() {
        return dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMovimento() {
        return movimento;
    }

    public void setMovimento(String movimento) {
        this.movimento = movimento;
    }

    public Integer getOperacao() {
        return operacao;
    }

    public boolean isOperacaoMovimentoUm() {
        return operacao != null && operacao == 1;
    }

    public boolean isOperacaoMovimentoDois() {
        return operacao != null && operacao == 2;
    }

    public boolean isOperacaoMovimentoTres() {
        return operacao != null && operacao == 3;
    }

    public AgrupadorMovimentoBem getAgrupadorMovimentoBem() {
        return agrupadorMovimentoBem;
    }

    public static SituacaoEventoBem getSituacaoPorOperacao(OperacaoMovimentacaoBem operacaoMovimento) {
        switch (operacaoMovimento) {
            case AQUISICAO_BEM:
                return SituacaoEventoBem.AGUARDANDO_LIQUIDACAO;

            case ESTORNO_AQUISICAO_BEM:
                return SituacaoEventoBem.BLOQUEADO;

            case DEPRECIACAO:
            case AMORTIZACAO:
            case EXAUSTAO:
            case APROVACAO_ALIENACAO_BEM:
            case PARECER_BAIXA_PATRIMONIAL:
            case SOLICITACAO_PRORROGACAO_CESSAO_BEM:
            case AVALIACAO_ALIENACAO_BEM:
                return SituacaoEventoBem.AGUARDANDO_EFETIVACAO;

            case SOLICITACAO_REAVALIACAO_BEM:
            case SOLICITACAO_TRANSFERENCIA_BEM:
            case SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL:
            case SOLICITACAO_TRANSFERENCIA_BEM_ESTORNO:
            case SOLICITACAO_ALIENACAO_BEM:
            case SOLICITACAO_BAIXA_PATRIMONIAL:
            case SOLICITACAO_CESSAO_BEM:
            case SOLICITACAO_ALTERACAO_CONSERVACAO_BEM:
            case AJUSTE_BENS_ORIGINAL_AUMENTATIVO:
            case AJUSTE_BENS_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA:
            case AJUSTE_BENS_ORIGINAL_DIMINUTIVO:
            case AJUSTE_BENS_ORIGINAL_DIMINUTIVO_EMPRESA_PULICA:
            case AJUSTE_BENS_DEPRECIACAO_AUMENTATIVO:
            case AJUSTE_BENS_DEPRECIACAO_DIMINUTIVO:
            case AJUSTE_BENS_AMORTIZACAO_AUMENTATIVO:
            case AJUSTE_BENS_AMORTIZACAO_DIMINUTIVO:
                return SituacaoEventoBem.EM_ELABORACAO;

            case EFETIVACAO_ALIENACAO_BEM:
                return SituacaoEventoBem.ALIENADO;

            case EFETIVACAO_BAIXA_PATRIMONIAL:
                return SituacaoEventoBem.BAIXADO;

            case MANUTENCAO_BEM_REMESSA:
                return SituacaoEventoBem.EM_MANUTENCAO;

            case AVALIACAO_PRORROGACAO_CESSAO_BEM:
            case EFETIVACAO_CESSAO_BEM:
                return SituacaoEventoBem.AGUARDANDO_DEVOLUCAO;

            default:
                return SituacaoEventoBem.FINALIZADO;
        }
    }

    @Override
    public String toString() {
        return descricao + " - " + movimento;
    }
}
