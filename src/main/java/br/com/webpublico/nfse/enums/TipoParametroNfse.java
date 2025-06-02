package br.com.webpublico.nfse.enums;

import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.util.List;

public enum TipoParametroNfse {

    OBRIGA_USO_AIDF("Obriga o uso de AIDF?", BooleanCancelamento.toStringArray()),
    OBRIGA_SOLICITAR_RPS("Obriga o solicitar envio de RPS?", BooleanCancelamento.toStringArray()),
    PERMITE_EMISSAO_RETROATIVA("Permite emissão retroativa?", BooleanCancelamento.toStringArray()),
    PERMITE_EMISSAO_RETROATIVA_ULTIMA_EMITIDA("Permite emissão retroativa antes da ultima nfse emitida?", BooleanCancelamento.toStringArray()),
    PERGUNTA_RPS_NA_EMISSAO_NFSE("Pergunta o número do RPS na emissão da nfse?", BooleanCancelamento.toStringArray()),
    VALIDA_DADOS_DEDUCAO_EMISSAO_NFSE("Valida dados da dedução na emissão da nfse?", BooleanCancelamento.toStringArray()),
    URL_APLICACAO_NFSE("Url de aplicação de nfse", null),
    URL_APLICACAO_NFSE_HOMOLOGACAO("Url de aplicação de nfse (Homologação)", null),
    URL_REST_NFSE("Url para comunicação Rest da nfse", null),
    URL_REST_NFSE_HOMOLOGACAO("Url para comunicação Rest da nfse (Homologação)", null),
    URL_AUTENTICACAO_NFSE("Url de autênticação de nfse", null),
    URL_AUTENTICACAO_NFSE_HOMOLOGACAO("Url de autênticação de nfse (Homologação)", null),
    TEXTO_CABECALHO_GUIA_RECOLHIMENTO("Texto do cabeçalho da Guia", null),
    VALIDA_DMS_ABERTA_MESES_ANTERIORES("Validar DMS aberta de mês anterior", BooleanCancelamento.toStringArray()),
    TIPOS_RPS_PERMITIDOS("Tipos de RPS", null),
    DIA_LIMITE_CANCELAMENTO_NOTA("Dia limite para cancelamento da nota", null),
    PERMITE_SELECIONAR_SERVICO_NAO_AUTORIZADO("Permite selecionar serviço não autorizado", BooleanCancelamento.toStringArray()),
    ENCERRA_COMPETENCIA_ANTERIOR_RPS("Encerra competência anterior no envio do RPS", BooleanCancelamento.toStringArray()),
    ISENTA_SERVICO_INCORPORACAO("Isenta serviço por incorporação", BooleanCancelamento.toStringArray()),
    PERMITE_EMISSAO_NFSE_ISS_FIXO_VENCIDO("Emite nfse para Fixo vencido?", BooleanCancelamento.toStringArray()),
    PERMITE_SERVICO_DECLARADO_PRESTADO("Permite declarar serviço prestado?", BooleanCancelamento.toStringArray()),
    PERMITE_ALTERAR_OPCOES_ENVIO_EMAIL_PERFIL_EMPRESA("Permite que a empresa altere o tipo de envio de email?", BooleanCancelamento.toStringArray()),
    ENVIA_EMAIL_HOMOLOGACAO("Envia email em homologação?", BooleanCancelamento.toStringArray()),
    MENSAGEM_LOGIN("Mensagem antes do login", null),
    MENSAGEM_TELA_AUTENTICACAO("Mensagem na tela de autênticação", null),
    BLOQUEIA_LOGIN("Bloqueia todos logins?", BooleanCancelamento.toStringArray()),
    PERMITE_EMISSAO_NFS_AVULSA("Permite emissão de Nota Avulsa para PF?", BooleanCancelamento.toStringArray()),
    PERMITE_ISENCAO_ACRESCIMOS_INSTITUICAO_FINANCEIRA("Permite isenção de acréscimos para IF?", BooleanCancelamento.toStringArray()),
    HABILITA_PRIMEIRO_ACESSO("Habilita Primeiro acesso?",BooleanCancelamento.toStringArray()),
    QUANTIDADE_SOLICITACAO_CANCELAMENTO("Quantidade máxima de solicitações de cancelamento por nota", null),
    MENSAGEM_PRIMEIRO_ACESSO_DESABILITADO("Mensagem do primeiro acesso",null),
    DIA_LIMITE_CANCELAMENTO_ENCERRAMENTO_MENSAL("Dia limite para cancelamento de encerramento mensal", null),
    PERMITE_CANCELAMENTO_FORA_PRAZO("Permite o cancelamento de Nfs-e fora do prazo?", BooleanCancelamento.toStringArray()),
    TEXTO_BLOQUEIO_CANCELAMENTO_FORA_PRAZO("Texto para bloqueio de cancelamento de Nfs-e fora do prazo", null),
    URL_APLICACAO_NOTA_PREMIADA("Url de aplicação da Nota Premiada", null),
    UTILIZA_BANCO_CACHE("Utiliza Banco de Cache", BooleanCancelamento.toStringArray()),
    PRAZO_AVISO_NOVA_MENSAGEM("Prazo em dias para aviso de nova mensagem", null),
    GRUPOS_COSIF_OBRIGATORIOS("Grupos Cosif obrigatórios para o PGCC", null);

    private List<String> valoresPossiveis;
    private String descricao;

    TipoParametroNfse(String descricao, List<String> valoresPossiveis) {
        this.valoresPossiveis = valoresPossiveis;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getValoresPossiveis() {
        return valoresPossiveis;
    }

    public List<SelectItem> getValoresPossiveisSelectItem() {
        return Util.getListSelectItem(valoresPossiveis != null ? valoresPossiveis : Lists.newArrayList());
    }

    public enum FluxoCancelamento {
        FLUXO_01, FLUXO_02, FLUXO_03;

        static List<String> toStringArray() {
            List<String> retorno = Lists.newArrayList();
            for (int i = 0; i < values().length; i++) {
                FluxoCancelamento value = values()[i];
                retorno.add(value.name());
            }
            return retorno;
        }
    }

    public enum BooleanCancelamento {
        TRUE, FALSE;

        static List<String> toStringArray() {
            List<String> retorno = Lists.newArrayList();
            for (int i = 0; i < values().length; i++) {
                BooleanCancelamento value = values()[i];
                retorno.add(value.name());
            }
            return retorno;
        }
    }

}
