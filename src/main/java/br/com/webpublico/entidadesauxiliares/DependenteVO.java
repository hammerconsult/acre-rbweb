package br.com.webpublico.entidadesauxiliares;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class DependenteVO {

    private String responsavel;
    private String grauParentesco;
    private String pessoaDependente;

    public DependenteVO() {
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getGrauParentesco() {
        return grauParentesco;
    }

    public void setGrauParentesco(String grauParentesco) {
        this.grauParentesco = grauParentesco;
    }

    public String getPessoaDependente() {
        return pessoaDependente;
    }

    public void setPessoaDependente(String pessoaDependente) {
        this.pessoaDependente = pessoaDependente;
    }
}
