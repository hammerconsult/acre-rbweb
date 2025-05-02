package br.com.webpublico.enums;

public enum VerificarDebitosDoImovel {

    DEBITOS_VENCIDOS("Permitir, mas avisar se houver Débitos Vencidos", "Atenção! Este imóvel possui débitos vencidos. Deseja continuar?"),
    DEBITOS_VENCIDOS_OU_A_VENCER("Permitir, mas avisar se houver Débitos Vencidos ou À Vencer", "Atenção! Este imóvel possui débitos vencidos ou à vencer. Deseja continuar?"),
    DEBITOS_AJUIZADOS("Permitir, mas me avisar se houver Débitos Ajuizados", "Atenção! Este imóvel possui débitos Ajuizados. Deseja continuar?"),
    DEBITOS_ITBI("Permitir, mas me avisar se houver Lançamento de ITBI", "Atenção! Este imóvel possui lançamento de ITBI em aberto. Deseja continuar?"),
    DEBITOS_NAO_PERMITIDOS("Não permitir se houver Débitos Vencidos", "Não foi possível continuar! Este imóvel possui débitos vencidos. Proceda a quitação dos débitos para continuar."),
    DEBITOS_NAO_PERMITIDOS_VENCIDOS_OU_A_VENCER("Não permitir se houver Débitos Vencidos ou À Vencer", "Não é possível continuar! Este imóvel possui débitos vencidos ou à vencer. Proceda a quitação dos débitos para continuar."),
    DEBITOS_NAO_PERMITIDOS_AJUIZADOS("Não permitir se houver Débitos Ajuizados", "Não é possível continuar! Este imóvel possui débitos ajuizados."),
    DEBITOS_NAO_PERMITIDOS_ITBI("Não permitir se houver Lançamento de ITBI", "Não foi possível continuar! Este imóvel possui lançamento de ITBI em aberto. Proceda a quitação dos débitos para continuar!"),
    PERMITIR_SEM_VERIFICAR("Permitir sem verificar", "");

    private String descricao;
    private String mensagem;

    private VerificarDebitosDoImovel(String descricao, String mensagem){
        this.descricao =  descricao;
        this.mensagem = mensagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMensagem() {
        return mensagem;
    }
}
