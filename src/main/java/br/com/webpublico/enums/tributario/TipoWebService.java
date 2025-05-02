package br.com.webpublico.enums.tributario;

/**
 * Created by William on 10/01/2017.
 */
public enum TipoWebService {
    PROCURADORIA("Procuradoria"),
    ARQUIVO_CDA("Arquivo CDA"),
    EMAIL("Email"),
    NFSE("NFS-e"),
    E_SOCIAL("E-social"),
    REINF("REINF"),
    REDESIM("Rede SIM"),
    SISOBRA("SisObra Pref"),
    ARQUIVO_BI_TRIBUTARIO("Arquivo BI Tributário"),
    ARQUIVO_BI_CONTABIL("Arquivo BI Contábil"),
    PROTESTO("Protesto de Débitos"),
    SIT("SIT"),
    PONTO("Ponto"),
    DAM("DAM"),
    NOTA_FISCAL_NACIONAL("Nota Fiscal Nacional"),
    SPC("SPC");


    private String descricao;

    TipoWebService(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
