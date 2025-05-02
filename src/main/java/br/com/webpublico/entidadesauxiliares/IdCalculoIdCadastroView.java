package br.com.webpublico.entidadesauxiliares;

public class IdCalculoIdCadastroView {

    private Long idCalculo;
    private Long idCadastro;

    public IdCalculoIdCadastroView() {
    }

    public IdCalculoIdCadastroView(Long idCalculo, Long idCadastro) {
        this.idCalculo = idCalculo;
        this.idCadastro = idCadastro;
    }

    public Long getIdCalculo() {
        return idCalculo;
    }

    public void setIdCalculo(Long idCalculo) {
        this.idCalculo = idCalculo;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }
}
