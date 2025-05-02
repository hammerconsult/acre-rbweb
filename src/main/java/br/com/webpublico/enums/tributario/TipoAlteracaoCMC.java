package br.com.webpublico.enums.tributario;

public enum TipoAlteracaoCMC {
    CMC,
    CNAE,
    ENDERECO;

    TipoAlteracaoCMC() {
    }

    public boolean isCnae() {
        return CNAE.equals(this);
    }

    public boolean isEndereco() {
        return ENDERECO.equals(this);
    }
}
