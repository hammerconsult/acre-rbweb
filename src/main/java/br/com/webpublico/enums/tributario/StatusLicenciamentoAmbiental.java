package br.com.webpublico.enums.tributario;

import com.google.common.collect.Lists;

import java.util.List;

public enum StatusLicenciamentoAmbiental {
    ABERTO("Aberto"),
    AGUARDANDO_DOCUMENTACAO_REDE_SIM("Aguardando Documentação Rede Sim"),
    ANALISE_DOCUMENTAL("Análise Documental"),
    PENDENCIA_DOCUMENTAL("Pendência Documental"),
    PRODUCAO_RELATORIO_PARECER("Produção de Relatório/Parecer"),
    FINALIZADO_EM_PARTE("Finalizado em Parte"),
    FINALIZADO("Finalizado"),
    CONDICIONANTES("Condicionantes"),
    ENCERRADO("Encerrado"),
    ARQUIVADO("Arquivado"),
    VALIDADO("Validado"),
    REJEITADO("Rejeitado");

    private final String descricao;

    StatusLicenciamentoAmbiental(String descricao) {
        this.descricao = descricao;
    }

    public static List<StatusLicenciamentoAmbiental> getSituacoesAtivas() {
        return Lists.newArrayList(ABERTO,
            AGUARDANDO_DOCUMENTACAO_REDE_SIM,
            ANALISE_DOCUMENTAL,
            CONDICIONANTES,
            FINALIZADO_EM_PARTE,
            FINALIZADO,
            PENDENCIA_DOCUMENTAL,
            VALIDADO,
            REJEITADO,
            PRODUCAO_RELATORIO_PARECER);
    }

    public static List<StatusLicenciamentoAmbiental> getSituacoesRevisao() {
        return Lists.newArrayList(VALIDADO, REJEITADO);
    }

    public static List<String> getSituacoesAtivasName() {
        List<String> retorno = Lists.newArrayList();
        for (StatusLicenciamentoAmbiental status : getSituacoesAtivas()) {
            retorno.add(status.name());
        }
        return retorno;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
