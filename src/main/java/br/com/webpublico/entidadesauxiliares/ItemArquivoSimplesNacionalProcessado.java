package br.com.webpublico.entidadesauxiliares;

public class ItemArquivoSimplesNacionalProcessado {

    private Long id;
    private String cnpj;
    private String razaoSocial;
    private Boolean temPessoa;
    private Boolean temCmc;
    private Boolean temDebitosVencidos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public Boolean getTemPessoa() {
        return temPessoa;
    }

    public void setTemPessoa(Boolean temPessoa) {
        this.temPessoa = temPessoa;
    }

    public Boolean getTemCmc() {
        return temCmc;
    }

    public void setTemCmc(Boolean temCmc) {
        this.temCmc = temCmc;
    }

    public Boolean getTemDebitosVencidos() {
        return temDebitosVencidos;
    }

    public void setTemDebitosVencidos(Boolean temDebitosVencidos) {
        this.temDebitosVencidos = temDebitosVencidos;
    }
}
