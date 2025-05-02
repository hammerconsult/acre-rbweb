/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
public class PontoTaxi implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Número")
    private Integer numero;
    @Obrigatorio
    @Etiqueta("Localização")
    private String localizacao;
    @Obrigatorio
    @Etiqueta("Logradouro De")
    private String logradouroDe;
    @Obrigatorio
    @Etiqueta("Bairro De")
    private String bairroDe;
    @Obrigatorio
    @Etiqueta("Logradouro Até")
    private String logradouroAte;
    @Obrigatorio
    @Etiqueta("Bairro Até")
    private String bairroAte;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataDeCadastro;
    @Obrigatorio
    @Etiqueta("Ativo")
    private Boolean ativo;
    @Obrigatorio
    @Etiqueta("Total de Vagas")
    private Integer totalDeVagas;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    @Invisivel
    @Transient
    private Long criadoEm;

    public PontoTaxi() {
        totalDeVagas = 0;
        ativo = false;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataDeCadastro() {
        return dataDeCadastro;
    }

    public void setDataDeCadastro(Date dataDeCadastro) {
        this.dataDeCadastro = dataDeCadastro;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getBairroAte() {
        return bairroAte;
    }

    public void setBairroAte(String bairroAte) {
        this.bairroAte = bairroAte;
    }

    public String getBairroDe() {
        return bairroDe;
    }

    public void setBairroDe(String bairroDe) {
        this.bairroDe = bairroDe;
    }

    public String getLogradouroAte() {
        return logradouroAte;
    }

    public void setLogradouroAte(String logradouroAte) {
        this.logradouroAte = logradouroAte;
    }

    public String getLogradouroDe() {
        return logradouroDe;
    }

    public void setLogradouroDe(String logradouroDe) {
        this.logradouroDe = logradouroDe;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getTotalDeVagas() {
        return totalDeVagas;
    }

    public void setTotalDeVagas(Integer totalDeVagas) {
        this.totalDeVagas = totalDeVagas;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return numero + " - " + getLocalizacao();
    }

}
