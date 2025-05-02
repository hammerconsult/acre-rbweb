package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum PermissaoUsuarioEmpresaNfse implements NfseEnumDTO {
    ROLE_EMPRESA_ADM("Administrador e edição de dados da empresa"),
    ROLE_AIDFE_SOLICITAR("AIDF-e - Solicitar"),
    ROLE_AIDFE_CONSULTAR("AIDF-e - Consultar"),
    ROLE_PLANO_GERAL_CONTAS_COMENTADO_CONSULTAR("Plano Geral de Contas Comentado - Consultar"),
    ROLE_PLANO_GERAL_CONTAS_COMENTADO_EDITAR("Plano Geral de Contas Comentado - Editar"),
    ROLE_DOCUMENTOS_FISCAIS_EMITIR_NFSE("Documentos Fiscais - Emitir Nfs-e"),
    ROLE_DOCUMENTOS_FISCAIS_EXPORTAR_XML("Documentos Fiscais - Exportar XML de Nfs-e"),
    ROLE_DOCUMENTOS_FISCAIS_DECLARAR_SERVICO("Documentos Fiscais - Declarar Serviço"),
    ROLE_DOCUMENTOS_FISCAIS_NOTA_FISCAL_AVULSA("Documentos Fiscais - Nota Fiscal Avulsa"),
    ROLE_RPS_MANUAL("RPS - Manual"),
    ROLE_RPS_IMPORTAR("RPS - Importar"),
    ROLE_RPS_CONSULTAR("RPS - Consultar"),
    ROLE_RPS_LOG_LOTES("RPS - Log dos Lotes"),
    ROLE_ENCERRAMENTO_MENSAL_SERVICOS_PRESTADOS("Encerramento Mensal - Serviços Prestados"),
    ROLE_ENCERRAMENTO_MENSAL_SERVICOS_TOMADOS("Encerramento Mensal - Serviços Tomados"),
    ROLE_ENCERRAMENTO_MENSAL_INSTITUICAO_FINANCEIRA("Encerramento Mensal - Instituição Financeira"),
    ROLE_ENCERRAMENTO_MENSAL_ISSQN_RETIDO("Encerramento Mensal - ISSQN Retido"),
    ROLE_CONTA_CORRENTE_EXTRATO("Conta Corrente - Extrato"),
    ROLE_CONTA_CORRENTE_DEBITOS("Conta Corrente - Débitos"),
    ROLE_CONTA_CORRENTE_GERAR_GUIA("Conta Corrente - Gerar Guia"),
    ROLE_CONTA_CORRENTE_LIVRO_FISCAL("Conta Corrente - Livro Fiscal"),
    ROLE_TOMADORES_CONSULTAR("Tomadores - Consultar"),
    ROLE_TOMADORES_INCLUIR("Tomadores - Incluir"),
    ROLE_CONSTRUCAO_CIVIL_CONSULTAR("Construção Civil - Consultar"),
    ROLE_CONSTRUCAO_CIVIL_INCLUIR("Construção Civil - Incluir"),
    ROLE_RELATORIOS_CONSULTAR_DOCUMENTOS("Relatórios - Consultar Documentos");


    private String descricao;

    PermissaoUsuarioEmpresaNfse(String descricao) {
        this.descricao = descricao;
    }

    public static List<PermissaoUsuarioEmpresaNfse> getTodasMenosAdmin() {
        List<PermissaoUsuarioEmpresaNfse> retorno = Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values());
        retorno.remove(ROLE_EMPRESA_ADM);
        return retorno;
    }

    public String getDescricao() {
        return descricao;
    }
}
