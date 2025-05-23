/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.beust.jcommander.internal.Lists;

import java.util.List;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Certidao")
public enum ModuloTipoDoctoOficial {
    SOLICITACAO("Solicitação", true),
    CERTIDADIVIDAATIVA("Dívida Ativa - Certidão", true),
    PETICAODIVIDAATIVA("Dívida Ativa - Petição", true),
    TERMODIVIDAATIVA("Dívida Ativa - Termo de Inscrição", true),
    FISCALIZACAO("Fiscalização de ISSQN", true),
    TERMOADVERTENCIA("Fiscalização de Secretaria - Termo de Advertência", true),
    AUTOINFRACAO("Fiscalização de Secretaria - Auto de Infração", true),
    PARECER_FISCALIZACAO("Fiscalização de Secretaria - Parecer", true),
    TERMO_GERAIS("Fiscalização de Secretaria - Termos Gerais", true),
    RBTRANS("RBTrans", true),
    FISCALIZACAORBTRANS("RBTrans - Fiscalização", true),
    RBTRANS_CERTIFICADO_OTT("RBTrans - Certificado Anual de Credenciamento da OTT", false),
    RBTRANS_CERTIFICADO_CONDUTOR_OTT("RBTrans - Certificado de Autorização de Condutor da OTT", false),
    PROTOCOLO("Protocolo", true),
    CONTRATO_RENDAS_PATRIMONIAIS("Contrato de Rendas Patrimoniais", true),
    CONTRATO_CEASA("Contrato de CEASA", true),
    ISENCAO_IPTU("Isenção de IPTU", true),
    TERMO_PARCELAMENTO("Termo de Confissão de Dívida de Parcelamento de Débitos", true),
    COBRANCAADMINISTRATIVA("Cobrança Administrativa", true),
    SOLICITACAO_SEPULTAMENTO("Solicitação de Sepultamento", true),
    DECLARACAO_BENEFICIOS_EVENTUAIS("Declaração de Benefícios Eventuais", true),
    DECLARACAO_SOLICITANTE_BENEFICIARIO("Declaração do Solicitante do Benefício Auxilio Funeral", true),
    REQUISICAO_FUNERAL("Requisição Funeral", true),
    CERTIDAO_BAIXA_ATIVIDADE("Certidão de Baixa de Atividade do C.M.C.", true),
    SUBVENCAO("Subvenção", false),
    MONITORAMENTO_FISCAL("Monitoramento Fiscal",true),
    BLOQUEIO_OUTORGA("Bloqueio de Outorga", false),
    RBTRANS_CERTIFICADO_RENOVACAO_OTT("RBTrans - Certificado de Renovação de Autorização da OTT", false),
    ALVARA_CONSTRUCAO("Alvará de Construção", true),
    ALVARA_IMEDIATO("Alvará Imediato", true),
    HABITESE_CONSTRUCAO("Habite-se de Construção", true),
    PROCESSO_PROTESTO("Processo de Protesto", true),
    NOTA_EMPENHO("Nota de Empenho", false),
    NOTA_RESTO_EMPENHO("Nota de Resto de Empenho", false),
    NOTA_ESTORNO_EMPENHO("Nota de Estorno de Empenho", false),
    NOTA_RESTO_ESTORNO_EMPENHO("Nota de Resto de Estorno de Empenho", false),
    NOTA_LIQUIDACAO("Nota de Liquidação", false),
    NOTA_RESTO_LIQUIDACAO("Nota de Resto da Liquidação", false),
    NOTA_ESTORNO_LIQUIDACAO("Nota de Estorno da Liquidação", false),
    NOTA_RESTO_ESTORNO_LIQUIDACAO("Nota de Resto de Estorno da Liquidação", false),
    NOTA_PAGAMENTO("Nota de Pagamento", false),
    NOTA_RESTO_PAGAMENTO("Nota de Resto de Pagamento", false),
    NOTA_ESTORNO_PAGAMENTO("Nota de Estorno do Pagamento", false),
    NOTA_RESTO_ESTORNO_PAGAMENTO("Nota de Resto de Estorno do Pagamento", false),
    NOTA_RECEITA_EXTRA("Nota de Receita Extraorçamentária", false),
    NOTA_RECEITA_EXTRA_PAGAMENTO("Nota de Receita Extraorçamentária com Retenção de Pagamento", false),
    NOTA_PAGAMENTO_EXTRA("Nota de Despesa Extraorçamentária", false),
    NOTA_RECEITA_EXTRA_ESTORNO("Nota de Estorno de Receita Extraorçamentária", false),
    NOTA_PAGAMENTO_EXTRA_ESTORNO("Nota de Estorno de Despesa Extraorçamentária", false),
    RECIBO_REINF("Recibo REINF", false),
    NOTA_OBRIGACAO_A_PAGAR_ESTORNO("Nota de Estorno de Obrigação a Pagar", false),
    NOTA_OBRIGACAO_A_PAGAR("Nota de Obrigação a Pagar", false),
    SOLICITACAO_MATERIAL("Solicitação de Compra", false),
    LICENCA_ETR("Licença de ETR", true),
    TR("Termo de Referência - TR", false),
    DFD("Documento de Formalização de Demanda - DFD", false),
    CERTIDAO_MARCA_FOGO("Certidão de Marca a Fogo", true),
    DOCUMENTO_LICENCIAMENTO_AMBIENTAL("Documento do Licenciamento Ambiental", false);

    private String descricao;
    private boolean precisaTagsTipoCadastro;

    private ModuloTipoDoctoOficial(String descricao, boolean precisaTagsTipoCadastro) {
        this.descricao = descricao;
        this.precisaTagsTipoCadastro = precisaTagsTipoCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isPrecisaTagsTipoCadastro() {
        return precisaTagsTipoCadastro;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<ModuloTipoDoctoOficial> getNotas() {
        return Lists.newArrayList(
            NOTA_EMPENHO,
            NOTA_RESTO_EMPENHO,
            NOTA_ESTORNO_EMPENHO,
            NOTA_RESTO_ESTORNO_EMPENHO,
            NOTA_LIQUIDACAO,
            NOTA_RESTO_LIQUIDACAO,
            NOTA_ESTORNO_LIQUIDACAO,
            NOTA_RESTO_ESTORNO_LIQUIDACAO,
            NOTA_RECEITA_EXTRA,
            NOTA_RECEITA_EXTRA_PAGAMENTO,
            NOTA_PAGAMENTO_EXTRA,
            NOTA_RECEITA_EXTRA_ESTORNO,
            NOTA_OBRIGACAO_A_PAGAR_ESTORNO,
            NOTA_OBRIGACAO_A_PAGAR);
    }
}
