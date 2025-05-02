package br.com.webpublico.ws.model;

/**
 * Created by HardRock on 14/03/2017.
 */
public class WsIPTU {

    private String cpfCnpj;
    private String matriculaImovel;
    private Integer ano;

    public WsIPTU() {
    }

    public WsIPTU(String cpfCnpj, String matriculaImovel, Integer ano) {
        this.cpfCnpj = cpfCnpj;
        this.matriculaImovel = matriculaImovel;
        this.ano = ano;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getMatriculaImovel() {
        return matriculaImovel;
    }

    public void setMatriculaImovel(String matriculaImovel) {
        this.matriculaImovel = matriculaImovel;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
