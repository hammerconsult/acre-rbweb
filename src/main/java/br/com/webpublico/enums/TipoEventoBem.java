package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoEventoBemDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoEventoBem implements EnumComDescricao {

    RECLASSIFICACAOBEM("Reclassificação", "", TipoEventoBemDTO.RECLASSIFICACAOBEM),
    ITEMEFETIVACAOBAIXAPATRIMONIAL("Baixa", "Desincorporação", TipoEventoBemDTO.ITEMEFETIVACAOBAIXAPATRIMONIAL),
    ITEMEFETIVACAOBAIXAALIENACAO("Baixa", "Alienação", TipoEventoBemDTO.ITEMEFETIVACAOBAIXAALIENACAO),// utilizado somente para mostrar no extrato de bens móveis
    AVARIABEM("Ajuste de Perdas", "Redução ao Valor Recuperável", TipoEventoBemDTO.AVARIABEM),
    ESTORNOAVARIABEM("Estorno de Avaria", "Estorno da Redução ao Valor Recuperável", TipoEventoBemDTO.ESTORNOAVARIABEM),
    ITEMAQUISICAO("Efetivação de Aquisição", "Aquisição", TipoEventoBemDTO.ITEMAQUISICAO),
    ITEM_SOLICITACAO_AQUISICAO("Solicitação de Aquisição", "Solicitação de Aquisição", TipoEventoBemDTO.ITEM_SOLICITACAO_AQUISICAO),
    ITEMAQUISICAO_ESTORNO("Estorno de Aquisição", "Estorno de Aquisição", TipoEventoBemDTO.ITEMAQUISICAO_ESTORNO),
    EFETIVACAOLEVANTAMENTOBEM("Efetivação do Levantamento", "Implantação do Levantamento", TipoEventoBemDTO.EFETIVACAOLEVANTAMENTOBEM),
    INCORPORACAOBEM("Incorporação", "Incorporação", TipoEventoBemDTO.INCORPORACAOBEM),
    CESSAO("Solicitação de Cessão", "Solicitação de Cessão", TipoEventoBemDTO.CESSAO),
    EFETIVACAOCESSAO("Cessão", "Cessão", TipoEventoBemDTO.EFETIVACAOCESSAO),
    SOLICITACAO_PRORROGACAO_CESSAO("Solicitação de Prorrogação de Cessão", "Solicitação de Prorrogação de Cessão", TipoEventoBemDTO.SOLICITACAO_PRORROGACAO_CESSAO),
    AVALIACAO_PRORROGACAO_CESSAO("Avaliação de Prorrogação de Cessão", "Avaliação de Prorrogação de Cessão", TipoEventoBemDTO.AVALIACAO_PRORROGACAO_CESSAO),
    CESSAODEVOLUCAO("Devolução de Bem Cedido", "Devolução de Bem Cedido", TipoEventoBemDTO.CESSAODEVOLUCAO),
    REDUCAOVALORBEM("Redução do Valor", "Redução ao Valor Recuperável", TipoEventoBemDTO.REDUCAOVALORBEM),
    AMORTIZACAO("Amortização", "Amortização", TipoEventoBemDTO.AMORTIZACAO),
    DEPRECIACAO("Depreciação", "Depreciação", TipoEventoBemDTO.DEPRECIACAO),
    EXAUSTAO("Exaustão", "Exaustão", TipoEventoBemDTO.EXAUSTAO),
    ESTORNOREDUCAOVALORBEM("Estorno da Redução do Valor", "Estorno da Redução do Valor", TipoEventoBemDTO.ESTORNOREDUCAOVALORBEM),
    APURACAOVALORLIQUIDOAMORTIZACAO("Apuração do Valor Líquido Amortização", "Apuração do Valor Líquido Amortização", TipoEventoBemDTO.APURACAOVALORLIQUIDOAMORTIZACAO),
    APURACAOVALORLIQUIDODEPRECIACAO("Apuração do Valor Líquido Depreciação", "Apuração do Valor Líquido Depreciação", TipoEventoBemDTO.APURACAOVALORLIQUIDODEPRECIACAO),
    APURACAOVALORLIQUIDOEXAUSTAO("Apuração do Valor Líquido Exaustão", "Apuração do Valor Líquido Exaustão", TipoEventoBemDTO.APURACAOVALORLIQUIDOEXAUSTAO),
    APURACAOVALORLIQUIDOAJUSTE("Apuração do Valor Líquido Ajuste", "Apuração do Valor Líquido Ajuste", TipoEventoBemDTO.APURACAOVALORLIQUIDOAJUSTE),
    ITEMLOTESOLICITACAOALIENACAO("Solicitação de Alienação", "Solicitação de Alienação", TipoEventoBemDTO.ITEMLOTESOLICITACAOALIENACAO),
    ITEMAPROVACAOALIENACAO("Aprovação de Alienação", "Aprovação de Alienação", TipoEventoBemDTO.ITEMAPROVACAOALIENACAO),
    ITEMPARECERBAIXAPATRIMONIAL("Parecer da Baixa", "Parecer da Baixa", TipoEventoBemDTO.ITEMPARECERBAIXAPATRIMONIAL),
    AVALIACAO_ALIENACAO("Avaliação de Alienação", "Avaliação de Alienação", TipoEventoBemDTO.AVALIACAO_ALIENACAO),
    ITEMSOLICITACAOBAIXAPATRIMONIAL("Solicitação de Baixa", "Solicitação de Baixa", TipoEventoBemDTO.ITEMSOLICITACAOBAIXAPATRIMONIAL),
    LEILAOALIENACAOLOTEBEM("Efetivação de  Alienação", "Efetivação de  Alienação", TipoEventoBemDTO.LEILAOALIENACAOLOTEBEM),
    TRANSFERENCIABEM("Solicitação de Transferência", "Solicitação de Transferência", TipoEventoBemDTO.TRANSFERENCIABEM),
    APROVACAO_TRANSFERENCIA_BEM("Aprovação de Transferência", "Aprovação de Transferência", TipoEventoBemDTO.APROVACAO_TRANSFERENCIA_BEM),
    ITEMESTORNOTRANSFERENCIA("Solicitação de Estorno de Transferência", "Solicitação de Estorno de Transferência", TipoEventoBemDTO.ITEMESTORNOTRANSFERENCIA),
    TRANSFERENCIABEMCONCEDIDA("Efetivação de Transferência Concedida", "Transferência Concedida", TipoEventoBemDTO.TRANSFERENCIABEMCONCEDIDA),
    TRANSFERENCIADEPRECIACAOCONCEDIDA("Transferência Depreciação Concedida", "Transferência Depreciação Concedida", TipoEventoBemDTO.TRANSFERENCIADEPRECIACAOCONCEDIDA),
    TRANSFERENCIAAMORTIZACAOCONCEDIDA("Transferência Amortização Concedida", "Transferência Amortização Concedida", TipoEventoBemDTO.TRANSFERENCIAAMORTIZACAOCONCEDIDA),
    TRANSFERENCIAEXAUSTAOCONCEDIDA("Transferência Exaustão Concedida", "Transferência Exaustão Concedida", TipoEventoBemDTO.TRANSFERENCIAEXAUSTAOCONCEDIDA),
    TRANSFERENCIAREDUCAOCONCEDIDA("Transferência Redução Concedida", "Transferência Redução Concedida", TipoEventoBemDTO.TRANSFERENCIAREDUCAOCONCEDIDA),
    EFETIVACAOTRANSFERENCIABEM("Efetivação de Transferência Recebida", "Transferência Recebida", TipoEventoBemDTO.EFETIVACAOTRANSFERENCIABEM),
    TRANSFERENCIADEPRECIACAORECEBIDA("Transferência Depreciação Recebida", "Transferência Depreciação Recebida", TipoEventoBemDTO.TRANSFERENCIADEPRECIACAORECEBIDA),
    TRANSFERENCIAAMORTIZACAORECEBIDA("Transferência Amortização Recebida", "Transferência Amortização Recebida", TipoEventoBemDTO.TRANSFERENCIAAMORTIZACAORECEBIDA),
    TRANSFERENCIAREDUCAORECEBIDA("Transferência Redução Recebida", "Transferência Redução Recebida", TipoEventoBemDTO.TRANSFERENCIAREDUCAORECEBIDA),
    TRANSFERENCIAEXAUSTAORECEBIDA("Transferência Exaustão Recebida", "Transferência Exaustão Recebida", TipoEventoBemDTO.TRANSFERENCIAEXAUSTAORECEBIDA),
    ESTORNOTRANSFERENCIABEMCONCEDIDA("Estorno Transferência Concedida", "Estorno Transferência Concedida", TipoEventoBemDTO.ESTORNOTRANSFERENCIABEMCONCEDIDA),
    ESTORNOTRANSFERENCIADEPRECIACAOCONCEDIDA("Estorno Transferência Depreciação Concedida", "Estorno Transferência Depreciação Concedida", TipoEventoBemDTO.ESTORNOTRANSFERENCIADEPRECIACAOCONCEDIDA),
    ESTORNOTRANSFERENCIAAMORTIZACAOCONCEDIDA("Estorno Transferência Amortização Concedida", "Estorno Transferência Amortização Concedida", TipoEventoBemDTO.ESTORNOTRANSFERENCIAAMORTIZACAOCONCEDIDA),
    ESTORNOTRANSFERENCIAEXAUSTAOCONCEDIDA("Estorno Transferência Exaustão Concedida", "Estorno Transferência Exaustão Concedid", TipoEventoBemDTO.ESTORNOTRANSFERENCIAEXAUSTAOCONCEDIDA),
    ESTORNOTRANSFERENCIAREDUCAOCONCEDIDA("Estorno Transferência Redução Concedida", "Estorno Transferência Redução Concedida", TipoEventoBemDTO.ESTORNOTRANSFERENCIAREDUCAOCONCEDIDA),
    ITEMEFETIVACAOESTORNOTRANSFERENCIA("Efetivação de Estorno de Transferência", "Estorno de Transferência Recebida", TipoEventoBemDTO.ITEMEFETIVACAOESTORNOTRANSFERENCIA),
    ESTORNOTRANSFERENCIADEPRECIACAORECEBIDA("Estorno Transferência Depreciação Recebida", "Estorno Transferência Depreciação Recebida", TipoEventoBemDTO.ESTORNOTRANSFERENCIADEPRECIACAORECEBIDA),
    ESTORNOTRANSFERENCIAAMORTIZACAORECEBIDA("Estorno Transferência Amortização Recebida", "Estorno Transferência Amortização Recebida", TipoEventoBemDTO.ESTORNOTRANSFERENCIAAMORTIZACAORECEBIDA),
    ESTORNOTRANSFERENCIAEXAUSTAORECEBIDA("Estorno Transferência Exaustão Recebida", "Estorno Transferência Exaustão Recebida", TipoEventoBemDTO.ESTORNOTRANSFERENCIAEXAUSTAORECEBIDA),
    ESTORNOTRANSFERENCIAREDUCAORECEBIDA("Estorno Transferência Redução Recebida", "Estorno Transferência Redução Recebida", TipoEventoBemDTO.ESTORNOTRANSFERENCIAREDUCAORECEBIDA),
    SOLICITACAO_ALTERACAO_CONSERVACAO_BEM("Solicitação de Alteração de Conservação", "", TipoEventoBemDTO.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM),
    EFETIVACAO_ALTERACAO_CONSERVACAO_BEM("Efetivação de Alteração de Conservação", "", TipoEventoBemDTO.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM),
    GANHOPATRIMONIAL("Ganho Patrimônial", "Ganhos com Alienação", TipoEventoBemDTO.GANHOPATRIMONIAL),
    PERDAPATRIMONIAL("Perda Patrimônial", "Perdas com Alienação", TipoEventoBemDTO.PERDAPATRIMONIAL),
    GANHOPERDAPATRIMONIAL("Ganho/Perda Patrimônial", "Ganho/Perda Patrimônial", TipoEventoBemDTO.GANHOPERDAPATRIMONIAL),
    EFETIVACAODEPRECIACAOBEM("Depreciação do Levantamento", "Depreciação do Levantamento", TipoEventoBemDTO.EFETIVACAODEPRECIACAOBEM),
    ESTORNOEFETIVACAOLEVMOVEL("Estorno da Efetivação do Levantamento", "Estorno da Efetivação do Levantamento", TipoEventoBemDTO.ESTORNOEFETIVACAOLEVMOVEL),
    LOTEREAVALICAOBEM("Solicitação de Reavaliação de Bem", "Solicitação de Reavaliação de Bem", TipoEventoBemDTO.LOTEREAVALICAOBEM),
    EFETIVACAOREAVALICAOBEM("Reavaliação", "Reavaliação", TipoEventoBemDTO.EFETIVACAOREAVALICAOBEM),
    EFETIVACAOLEVANTAMENTOIMOVEL("Efetivação de Bem Imóvel", "Efetivação de Levantamento Imóvel", TipoEventoBemDTO.EFETIVACAOLEVANTAMENTOIMOVEL),
    ITEMINVENTARIOBENS("Abertura de Inventário de Bens Móveis", "Abertura de Inventário de Bens Móveis", TipoEventoBemDTO.ITEMINVENTARIOBENS),
    FECHAMENTOINVENTARIOBEM("Fechamento de Inventário de Bens Móveis", "Fechamento de Inventário de Bens Móveis", TipoEventoBemDTO.FECHAMENTOINVENTARIOBEM),
    MANUTENCAOBEMMOVEISENTRADA("Manutenção de Bens Móveis - Remessa", "Manutenção de Bens Móveis - Remessa", TipoEventoBemDTO.MANUTENCAOBEMMOVEISENTRADA),
    MANUTENCAOBEMMOVEISRETORNO("Manutenção de Bens Móveis - Retorno", "Manutenção de Bens Móveis - Retorno", TipoEventoBemDTO.MANUTENCAOBEMMOVEISRETORNO),
    SOLICITACAO_AJUSTE_BEM_MOVEL_ORIGINAL("Solicitação de Ajuste de Bens Móveis Original", "Solicitação de Ajuste de Bens Móveis Original", TipoEventoBemDTO.SOLICITACAO_AJUSTE_BEM_MOVEL_ORIGINAL),
    SOLICITACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO("Solicitação de Ajuste de Bens Móveis Depreciação", "Solicitação de Ajuste de Bens Móveis Depreciação", TipoEventoBemDTO.SOLICITACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO),
    SOLICITACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO("Solicitação de Ajuste de Bens Móveis Amortização", "Solicitação de Ajuste de Bens Móveis Amortização", TipoEventoBemDTO.SOLICITACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO("Efetivação de Ajuste de Bens Móveis Original- Aumentativo", "Ajuste de Bens Móveis Original - Aumentativo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA("Efetivação de Ajuste de Bens Móveis Original- Aumentativo - Empresa Pública", "Ajuste de Bens Móveis Original - Aumentativo - Empresa Pública", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA),
    EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO("Efetivação de Ajuste de Bens Móveis Original - Diminutivo", "Ajuste de Bens Móveis Original - Diminutivo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA("Efetivação de Ajuste de Bens Móveis Original - Diminutivo - Empresa Pública", "Ajuste de Bens Móveis Original - Diminutivo- Empresa Pública", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA),
    EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO("Efetivação de Ajuste de Bens Móveis Depreciação - Aumentativo", "Ajuste de Bens Móveis Depreciação - Aumentativo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO("Efetivação de Ajuste de Bens Móveis Depreciação - Diminutivo", "Ajuste de Bens Móveis Depreciação - Diminutivo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO("Efetivação de Ajuste de Bens Móveis Amortização - Aumentativo", "Ajuste de Bens Móveis Amortização - Aumentativo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO),
    EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO("Efetivação de Ajuste de Bens Móveis Amortização - Diminutivo", "Ajuste de Bens Móveis Amortização - Diminutivo", TipoEventoBemDTO.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO),
    SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL("Solicitação de Transferência Grupo Patrimonial", "Solicitação de Transferência Grupo Patrimonial", TipoEventoBemDTO.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_CONCEDIDA("Transferência Grupo Patrimonial Original Concedida", "EfetivaçãoTransferência Grupo Patrimonial Original Concedida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_CONCEDIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA("Transferência Grupo Patrimonial Original Recebida", "Transferência Grupo Patrimonial Original Recebida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_CONCEDIDA("Transferência Grupo Patrimonial Depreciação Concedida", "Transferência Grupo Patrimonial Depreciação Concedida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_CONCEDIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_RECEBIDA("Transferência Grupo Patrimonial Depreciação Recebida", "Transferência Grupo Patrimonial Depreciação Recebida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_RECEBIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_CONCEDIDA("Transferência Grupo Patrimonial Amortização Concedida", "Transferência Grupo Patrimonial Amortização Concedida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_CONCEDIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_RECEBIDA("Transferência Grupo Patrimonial Amortização Recebida", "Transferência Grupo Patrimonial Amortização Recebida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_RECEBIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_CONCEDIDA("Transferência Grupo Patrimonial Ajuste Concedida", "Transferência Grupo Patrimonial Ajuste Concedida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_CONCEDIDA),
    TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_RECEBIDA("Transferência Grupo Patrimonial Ajuste Recebida", "Transferência Grupo Patrimonial Ajuste Recebida", TipoEventoBemDTO.TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_RECEBIDA);

    /* Descrição apresentada no historico*/
    private String descricao;
    /* Descrição apresentada no extrato*/
    private String descricaoContabil;
    private TipoEventoBemDTO toDto;

    TipoEventoBem(String descricao, String descricaoContabil, TipoEventoBemDTO toDto) {
        this.descricao = descricao;
        this.descricaoContabil = descricaoContabil;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoContabil() {
        return descricaoContabil;
    }

    public TipoEventoBemDTO getToDto() {
        return toDto;
    }

    public static List<TipoEventoBem> retornaTipoEventoIngressado() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.INCORPORACAOBEM);
        retorno.add(TipoEventoBem.ITEMAQUISICAO);
        retorno.add(TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM);
        return retorno;
    }

    public static List<TipoEventoBem> retornaTipoEventoTransferencia() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.TRANSFERENCIABEMCONCEDIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAAMORTIZACAOCONCEDIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAEXAUSTAOCONCEDIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAREDUCAOCONCEDIDA);

        retorno.add(TipoEventoBem.EFETIVACAOTRANSFERENCIABEM);
        retorno.add(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAAMORTIZACAORECEBIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAEXAUSTAORECEBIDA);
        retorno.add(TipoEventoBem.TRANSFERENCIAREDUCAORECEBIDA);
        return retorno;
    }

    public static List<TipoEventoBem> retornaTipoEventoReducaoValorBem() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.DEPRECIACAO);
        retorno.add(TipoEventoBem.ESTORNOREDUCAOVALORBEM);
        retorno.add(TipoEventoBem.AMORTIZACAO);
        retorno.add(TipoEventoBem.EXAUSTAO);
        return retorno;
    }

    public static List<TipoEventoBem> getTiposEventosCreditoOriginal() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.ITEMAQUISICAO_ESTORNO);
        retorno.add(TipoEventoBem.TRANSFERENCIABEMCONCEDIDA);
        retorno.add(TipoEventoBem.EFETIVACAOREAVALICAOBEM);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA);
        return retorno;
    }

    public static List<TipoEventoBem> getTiposEventosDebitoOriginal() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.ITEMAQUISICAO);
        retorno.add(TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM);
        retorno.add(TipoEventoBem.INCORPORACAOBEM);
        retorno.add(TipoEventoBem.EFETIVACAOTRANSFERENCIABEM);
        retorno.add(TipoEventoBem.EFETIVACAOREAVALICAOBEM);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA);
        return retorno;
    }

    public static List<TipoEventoBem> getTiposEventosCreditoAjuste() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.DEPRECIACAO);
        retorno.add(TipoEventoBem.AMORTIZACAO);
        retorno.add(TipoEventoBem.EFETIVACAODEPRECIACAOBEM);
        retorno.add(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO);
        return retorno;
    }

    public static List<TipoEventoBem> getTiposEventosDebitoAjuste() {
        List<TipoEventoBem> retorno = Lists.newArrayList();
        retorno.add(TipoEventoBem.ESTORNOREDUCAOVALORBEM);
        retorno.add(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO);
        retorno.add(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO);
        return retorno;
    }

    public boolean isSolicitacaoAlteracaoConservacao(){
        return SOLICITACAO_ALTERACAO_CONSERVACAO_BEM.equals(this);
    }
}
