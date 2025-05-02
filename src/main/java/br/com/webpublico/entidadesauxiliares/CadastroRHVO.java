package br.com.webpublico.entidadesauxiliares;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class CadastroRHVO {

    private String matricula;
    private String codigoPrestadorServico;
    private String categoriaTrabalhador;
    public CadastroRHVO() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodigoPrestadorServico() {
        return codigoPrestadorServico;
    }

    public void setCodigoPrestadorServico(String codigoPrestadorServico) {
        this.codigoPrestadorServico = codigoPrestadorServico;
    }

    public String getCategoriaTrabalhador() {
        return categoriaTrabalhador;
    }

    public void setCategoriaTrabalhador(String categoriaTrabalhador) {
        this.categoriaTrabalhador = categoriaTrabalhador;
    }
}
