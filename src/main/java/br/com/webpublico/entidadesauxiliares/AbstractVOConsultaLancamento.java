package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 25/06/15
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */

public abstract class AbstractVOConsultaLancamento implements Serializable {

    private ResultadoParcela resultadoParcela;
    private String cpfOrCnpj;
    private String nomeOrRazaoSocial;
    private String tipoLogradouro;
    private String logradouro;
    private String numeroEndereco;
    private String complemento;
    private String bairro;
    private String cep;
    private String enderecoCompleto;

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public String getCpfOrCnpj() {
        return cpfOrCnpj;
    }

    public void setCpfOrCnpj(String cpfOrCnpj) {
        this.cpfOrCnpj = cpfOrCnpj;
    }

    public String getNomeOrRazaoSocial() {
        return nomeOrRazaoSocial;
    }

    public void setNomeOrRazaoSocial(String nomeOrRazaoSocial) {
        this.nomeOrRazaoSocial = nomeOrRazaoSocial;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEnderecoCompleto() {
        StringBuilder toReturn = new StringBuilder();
        if (tipoLogradouro != null && !tipoLogradouro.trim().isEmpty()) {
            toReturn.append(",").append(tipoLogradouro.trim());
        }
        if (logradouro != null && !logradouro.trim().isEmpty()) {
            toReturn.append(",").append(logradouro.trim());
        }
        if (numeroEndereco != null && !numeroEndereco.trim().isEmpty()) {
            toReturn.append(",").append(numeroEndereco.trim());
        }
        if (complemento != null && !complemento.trim().isEmpty()) {
            toReturn.append(",").append(complemento.trim());
        }
        if (bairro != null && !bairro.trim().isEmpty()) {
            toReturn.append(",").append(bairro.trim());
        }
        if (cep != null && !cep.trim().isEmpty()) {
            toReturn.append(",").append(cep.trim());
        }
        return toReturn.toString().replaceFirst(",", "");
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }
}
