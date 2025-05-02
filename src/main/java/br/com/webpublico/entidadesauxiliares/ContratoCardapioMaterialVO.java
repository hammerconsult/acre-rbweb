package br.com.webpublico.entidadesauxiliares;

public class ContratoCardapioMaterialVO {

    private Long codigoMaterial;
    private String descricaoMaterial;
    private Long numeroItem;
    private Long numeroLote;

    private Boolean pertenceRefeicao = false;

    public ContratoCardapioMaterialVO() {
    }

    public Long getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(Long codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public String getDescricaoMaterial() {
        return descricaoMaterial;
    }

    public void setDescricaoMaterial(String descricaoMaterial) {
        this.descricaoMaterial = descricaoMaterial;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Long getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Long numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Boolean getPertenceRefeicao() {
        return pertenceRefeicao;
    }

    public void setPertenceRefeicao(Boolean pertenceRefeicao) {
        this.pertenceRefeicao = pertenceRefeicao;
    }

    public String getMaterial() {
        return codigoMaterial + " - " + descricaoMaterial;
    }
}
