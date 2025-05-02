/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author gustavo
 */
@Entity

@Audited
@Etiqueta("pontos Comerciais")
public class PontoComercial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Localização")
    private Localizacao localizacao;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Tipo de Ponto Comercial")
    private TipoPontoComercial tipoPontoComercial;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Box")
    private String numeroBox;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Área (m²)")
    private BigDecimal area;
    @ManyToOne
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastroImobiliario;
    private BigDecimal valorMetroQuadrado;
    private Boolean ativo;
    @Transient
    private Boolean disponivel;

    public PontoComercial() {
        this.ativo = Boolean.TRUE;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public BigDecimal getArea() {
        return area != null ? area.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public String getNumeroBox() {
        return numeroBox;
    }

    public void setNumeroBox(String numeroBox) {
        this.numeroBox = numeroBox;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPontoComercial getTipoPontoComercial() {
        return tipoPontoComercial;
    }

    public void setTipoPontoComercial(TipoPontoComercial tipoPontoComercial) {
        this.tipoPontoComercial = tipoPontoComercial;
    }

    public BigDecimal getValorMetroQuadrado() {
        return valorMetroQuadrado;
    }

    public void setValorMetroQuadrado(BigDecimal valorMetroQuadrado) {
        this.valorMetroQuadrado = valorMetroQuadrado;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDisponivel() {
        return disponivel != null ? disponivel : true;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getDescricaoCompleta() {
        String retorno = "BOX Nº " + this.getNumeroBox();
        if (this.getLocalizacao() != null && this.getLocalizacao().getId() != null) {
            retorno += " - " + this.getLocalizacao().getToStringAutoComplete();
        }
        return retorno;
    }

    @Override
    public String toString() {
        return "Box " + numeroBox + (localizacao != null ? ", " + localizacao.getDescricao() : "");
    }
}
