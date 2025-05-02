package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoCadastroAlteracaoContratual implements EnumComDescricao {

    CONTRATO("Contrato"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço");
    private String descricao;

    TipoCadastroAlteracaoContratual(String descricao) {
        this.descricao = descricao;
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isAta() {
        return ATA_REGISTRO_PRECO.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoCurta() {
        return CONTRATO.equals(this) ? descricao : descricao.substring(0, 3);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
