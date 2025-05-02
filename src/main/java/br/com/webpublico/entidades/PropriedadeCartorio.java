/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoProprietario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PropriedadeRuralImobiliaria;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
public class PropriedadeCartorio implements Serializable, PropriedadeRuralImobiliaria {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    @Etiqueta("Imóvel")
    private CadastroImobiliario imovel;
    @ManyToOne
    private Pessoa pessoa;
    @Etiqueta("Proporção")
    private Double proporcao;
    private Boolean atual;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Tipo de Propietário")
    @Enumerated(EnumType.STRING)
    private TipoProprietario tipoProprietario;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Transient
    private Long criadoEm;

    public PropriedadeCartorio() {
        this.dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CadastroImobiliario getImovel() {
        return imovel;
    }

    public void setImovel(CadastroImobiliario imovel) {
        this.imovel = imovel;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Double getProporcao() {
        return proporcao;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public TipoProprietario getTipoProprietario() {
        return tipoProprietario;
    }

    public void setTipoProprietario(TipoProprietario tipoProprietario) {
        this.tipoProprietario = tipoProprietario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAtual() {
        return atual;
    }

    public void setAtual(Boolean atual) {
        this.atual = atual;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
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
        return pessoa.getNomeCpfCnpj();
    }
}
