package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class DfeNacionalPessoa extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private String cnpj;
    private String cpf;
    private String im;
    private String xNome;
    private String xFant;
    private String xLgr;
    private String nro;
    private String xCpl;
    private String xBairro;
    private String cMun;
    private String cidade;
    private String uf;
    private String cep;
    private String fone;
    private String email;
    private String nif;
    private String cNaoNIF;
    private String caepf;
    private String opSimpNac;
    private String regApTribSN;
    private String regEspTrib;

    @Override
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

    public String getCpf() {
        return cpf;
    }

    public String getCpfCnpj() {
        return cpf != null ? cpf : cnpj;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getxNome() {
        return xNome;
    }

    public void setxNome(String xNome) {
        this.xNome = xNome;
    }

    public String getxFant() {
        return xFant;
    }

    public void setxFant(String xFant) {
        this.xFant = xFant;
    }

    public String getxLgr() {
        return xLgr;
    }

    public void setxLgr(String xLgr) {
        this.xLgr = xLgr;
    }

    public String getNro() {
        return nro;
    }

    public void setNro(String nro) {
        this.nro = nro;
    }

    public String getxCpl() {
        return xCpl;
    }

    public void setxCpl(String xCpl) {
        this.xCpl = xCpl;
    }

    public String getxBairro() {
        return xBairro;
    }

    public void setxBairro(String xBairro) {
        this.xBairro = xBairro;
    }

    public String getcMun() {
        return cMun;
    }

    public void setcMun(String cMun) {
        this.cMun = cMun;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getcNaoNIF() {
        return cNaoNIF;
    }

    public void setcNaoNIF(String cNaoNIF) {
        this.cNaoNIF = cNaoNIF;
    }

    public String getCaepf() {
        return caepf;
    }

    public void setCaepf(String caepf) {
        this.caepf = caepf;
    }

    public String getOpSimpNac() {
        return opSimpNac;
    }

    public void setOpSimpNac(String opSimpNac) {
        this.opSimpNac = opSimpNac;
    }

    public String getRegApTribSN() {
        return regApTribSN;
    }

    public void setRegApTribSN(String regApTribSN) {
        this.regApTribSN = regApTribSN;
    }

    public String getRegEspTrib() {
        return regEspTrib;
    }

    public void setRegEspTrib(String regEspTrib) {
        this.regEspTrib = regEspTrib;
    }
}
