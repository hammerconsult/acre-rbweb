/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoProprietario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PropriedadeRuralImobiliaria;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@GrupoDiagrama(nome = "CadastroRural")
@Entity
@Audited
public class PropriedadeRural implements Serializable, PropriedadeRuralImobiliaria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("ImovelRural")
    private CadastroRural imovel;
    @ManyToOne
    private Pessoa pessoa;
    @Etiqueta("Proporção")
    private Double proporcao;
    //private Boolean atual;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Tipo de Proprietário")
    @Enumerated(EnumType.STRING)
    private TipoProprietario tipoProprietario;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroRural getImovel() {
        return imovel;
    }

    public void setImovel(CadastroRural imovel) {
        this.imovel = imovel;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
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

    public Boolean temPropriedade(List<PropriedadeRural> propriedades, PropriedadeRural propriedade) {
        for (PropriedadeRural p : propriedades) {
            if (p.getPessoa().getId().equals(propriedade.getPessoa().getId())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
