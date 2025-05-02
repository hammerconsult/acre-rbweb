package br.com.webpublico.enums;

public enum TipoExecucaoProcesso {

    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação/Inexigibilidade"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço");
    private String descricao;

    TipoExecucaoProcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isDispensa(){
        return this.equals(DISPENSA_LICITACAO_INEXIGIBILIDADE);
    }

    public boolean isAta(){
        return this.equals(ATA_REGISTRO_PRECO);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
