package br.com.webpublico.enums;

public enum OrigemSaldoExecucaoProcesso {

    ATA_REGISTRO_PRECO("Ata Registro de Preço", 1),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação/Inexigibilidade", 1),
    ADITIVO("Aditivo", 2),
    APOSTILAMENTO("Apostilamento", 3);
    private String descricao;
    private Integer ordem;

    OrigemSaldoExecucaoProcesso(String descricao, Integer ordem) {
        this.descricao = descricao;
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public boolean isAta() {
        return ATA_REGISTRO_PRECO.equals(this);
    }

    public boolean isDispensa() {
        return DISPENSA_LICITACAO_INEXIGIBILIDADE.equals(this);
    }

    public boolean isAditivo() {
        return ADITIVO.equals(this);
    }

    public boolean isApostilamento() {
        return APOSTILAMENTO.equals(this);
    }

    public boolean isAtaOrDispensa(){
        return isAta() || isDispensa();
    }

    public boolean isAditovoOrApostilamento() {
        return isAditivo() || isApostilamento();
    }
}
