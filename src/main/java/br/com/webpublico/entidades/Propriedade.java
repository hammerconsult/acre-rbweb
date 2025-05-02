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
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
public class Propriedade implements Serializable, PropriedadeRuralImobiliaria {

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
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Tipo de Propietário")
    @Enumerated(EnumType.STRING)
    private TipoProprietario tipoProprietario;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Transient
    private Long criadoEm;
    private Boolean veioPorITBI;

    public Propriedade() {
        this.dataRegistro = new Date();
        criadoEm = System.nanoTime();
        veioPorITBI = false;
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
        return proporcao != null ? proporcao : 0.0;
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

    public Boolean getVeioPorITBI() {
        return veioPorITBI;
    }

    public void setVeioPorITBI(Boolean veioPorITBI) {
        this.veioPorITBI = veioPorITBI;
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

    public void encerraVigencia() {
        DateTime dateTime = new DateTime();
        dateTime.minusDays(1);
        setFinalVigencia(dateTime.toDate());
    }

    public Boolean temPropriedade(List<Propriedade> propriedades, Propriedade propriedade) {
        for (Propriedade p : propriedades) {
            if (p.getPessoa().getId().equals(propriedade.getPessoa().getId())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
