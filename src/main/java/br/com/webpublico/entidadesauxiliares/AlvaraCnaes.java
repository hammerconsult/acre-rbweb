package br.com.webpublico.entidadesauxiliares;

public class AlvaraCnaes {

    private String codigoCnae;
    private String descricaoDetalhada;
    private String grauDeRisco;
    private String ambito;
    private String licenca;
    private Boolean interesseDoEstado;
    private Boolean sanitario;
    private Boolean exercidaNoLocal;
    private Boolean principal;

    public AlvaraCnaes() {
        this.interesseDoEstado = Boolean.FALSE;
        this.sanitario = Boolean.FALSE;
        this.exercidaNoLocal = Boolean.FALSE;
        this.principal = Boolean.FALSE;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public void setCodigoCnae(String codigoCnae) {
        this.codigoCnae = codigoCnae;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public String getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(String grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public String getLicenca() {
        return licenca;
    }

    public void setLicenca(String licenca) {
        this.licenca = licenca;
    }

    public Boolean getInteresseDoEstado() {
        return interesseDoEstado != null ? interesseDoEstado : Boolean.FALSE;
    }

    public void setInteresseDoEstado(Boolean interesseDoEstado) {
        this.interesseDoEstado = interesseDoEstado;
    }

    public Boolean getSanitario() {
        return sanitario != null ? sanitario : Boolean.FALSE;
    }

    public void setSanitario(Boolean sanitario) {
        this.sanitario = sanitario;
    }

    public Boolean getExercidaNoLocal() {
        return exercidaNoLocal != null ? exercidaNoLocal : Boolean.FALSE;
    }

    public void setExercidaNoLocal(Boolean exercidaNoLocal) {
        this.exercidaNoLocal = exercidaNoLocal;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}
