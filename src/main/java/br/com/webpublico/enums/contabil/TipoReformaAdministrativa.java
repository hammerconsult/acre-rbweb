package br.com.webpublico.enums.contabil;

public enum TipoReformaAdministrativa {
    CRIAR_NOVO("Criar novo"),
    CRIAR_NOVO_FILHO("Criar novo filho"),
    ALTERAR_EXISTENTE("Alterar existente"),
    ENCERRAR("Encerrar vigência"),
    NAO_ALTERAR("Não alterar");

    private String descricao;

    TipoReformaAdministrativa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Boolean isCriarNovo(){
        return TipoReformaAdministrativa.CRIAR_NOVO.equals(this) || TipoReformaAdministrativa.CRIAR_NOVO_FILHO.equals(this);
    }

    public Boolean isAlterarExistente(){
        return TipoReformaAdministrativa.ALTERAR_EXISTENTE.equals(this);
    }

    public Boolean isEncerrar(){
        return TipoReformaAdministrativa.ENCERRAR.equals(this);
    }
    public Boolean isNaoAlterar(){
        return TipoReformaAdministrativa.NAO_ALTERAR.equals(this);
    }
}
