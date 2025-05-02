/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.FormaCalculoMultaFiscalizacao;
import br.com.webpublico.enums.IncidenciaMultaFiscalizacao;
import br.com.webpublico.enums.TipoCalculoMultaFiscalizacao;
import br.com.webpublico.enums.TipoMultaFiscalizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author fabio
 */
@Entity

@Audited
@Etiqueta ("Multa do Processo de Fiscalização")
@GrupoDiagrama(nome="Fiscalizacao")
public class MultaFiscalizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta ("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta ("Descrição")
    private String artigo; //este campo é uma descrição da multa e não o artigo
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta ("Incidência")
    private IncidenciaMultaFiscalizacao incidenciaMultaFiscalizacao;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta ("Tipo")
    private TipoMultaFiscalizacao tipoMultaFiscalizacao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta ("Tipo de Cálculo")
    private TipoCalculoMultaFiscalizacao tipoCalculoMultaFiscalizacao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta ("Forma de Cálculo")
    private FormaCalculoMultaFiscalizacao formaCalculoMultaFiscalizacao;
    private BigDecimal valorMulta;
    private BigDecimal aliquotaMulta;
    @ManyToOne
    private IndiceEconomico indiceEconomico;
    @ManyToOne
    @Tabelavel
    @Etiqueta ("Receita")
    private Tributo tributo;
    private String embasamento;
    @Enumerated(EnumType.STRING)
    private BaseCalculo baseCalculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAliquotaMulta() {
        return aliquotaMulta;
    }

    public void setAliquotaMulta(BigDecimal aliquotaMulta) {
        this.aliquotaMulta = aliquotaMulta;
    }

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getEmbasamento() {
        return embasamento;
    }

    public void setEmbasamento(String embasamento) {
        this.embasamento = embasamento;
    }

    public FormaCalculoMultaFiscalizacao getFormaCalculoMultaFiscalizacao() {
        return formaCalculoMultaFiscalizacao;
    }

    public void setFormaCalculoMultaFiscalizacao(FormaCalculoMultaFiscalizacao formaCalculoMultaFiscalizacao) {
        this.formaCalculoMultaFiscalizacao = formaCalculoMultaFiscalizacao;
    }

    public IncidenciaMultaFiscalizacao getIncidenciaMultaFiscalizacao() {
        return incidenciaMultaFiscalizacao;
    }

    public void setIncidenciaMultaFiscalizacao(IncidenciaMultaFiscalizacao incidenciaMultaFiscalizacao) {
        this.incidenciaMultaFiscalizacao = incidenciaMultaFiscalizacao;
    }

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public TipoCalculoMultaFiscalizacao getTipoCalculoMultaFiscalizacao() {
        return tipoCalculoMultaFiscalizacao;
    }

    public void setTipoCalculoMultaFiscalizacao(TipoCalculoMultaFiscalizacao tipoCalculoMultaFiscalizacao) {
        this.tipoCalculoMultaFiscalizacao = tipoCalculoMultaFiscalizacao;
    }

    public TipoMultaFiscalizacao getTipoMultaFiscalizacao() {
        return tipoMultaFiscalizacao;
    }

    public void setTipoMultaFiscalizacao(TipoMultaFiscalizacao tipoMultaFiscalizacao) {
        this.tipoMultaFiscalizacao = tipoMultaFiscalizacao;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BaseCalculo getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BaseCalculo baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getDescricao() {
        if (artigo.length() > 90) {
            return artigo.substring(0,90)+"...";
        }
        return artigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MultaFiscalizacao)) {
            return false;
        }
        MultaFiscalizacao other = (MultaFiscalizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.MultaFiscalizacao[ id=" + id + " ]";
    }

    public enum BaseCalculo {
        LANCAMENTO("Lançamento"),
        MULTA("Multa");
        private String descricao;

        BaseCalculo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
