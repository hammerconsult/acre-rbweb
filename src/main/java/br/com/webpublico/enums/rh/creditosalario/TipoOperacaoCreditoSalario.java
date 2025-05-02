package br.com.webpublico.enums.rh.creditosalario;

public enum TipoOperacaoCreditoSalario {

    C("Lançamento a Crédito"),
    D("Lançamento a Débito"),
    E("Extrato para Conciliação"),
    G("Extrato para Gestão de Caixa"),
    I("Informações de Títulos Capturados do Próprio Banco"),
    R("Arquivo Remessa"),
    T("Arquivo Retorno");

    private String descricao;

    TipoOperacaoCreditoSalario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
