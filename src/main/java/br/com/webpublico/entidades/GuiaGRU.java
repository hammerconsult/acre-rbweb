package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class GuiaGRU extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código do Recolhimento")
    private String codigoRecolhimento;
    @Obrigatorio
    @Etiqueta("Número de Referência")
    private String numeroReferencia;
    @Obrigatorio
    @Etiqueta("Competência")
    @Temporal(TemporalType.DATE)
    private Date competencia;
    @Obrigatorio
    @Etiqueta("Vencimento")
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @Obrigatorio
    @Etiqueta("UG/Gestão")
    private String ugGestao;
    @Obrigatorio
    @Etiqueta("Valor Principal")
    private BigDecimal valorPrincipal;
    @Etiqueta("Código de Barras")
    private String codigoBarra;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoRecolhimento() {
        return codigoRecolhimento;
    }

    public void setCodigoRecolhimento(String codigoRecolhimento) {
        this.codigoRecolhimento = codigoRecolhimento;
    }

    public String getNumeroReferencia() {
        return numeroReferencia;
    }

    public void setNumeroReferencia(String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getUgGestao() {
        return ugGestao;
    }

    public void setUgGestao(String ugGestao) {
        this.ugGestao = ugGestao;
    }

    public BigDecimal getValorPrincipal() {
        return valorPrincipal;
    }

    public void setValorPrincipal(BigDecimal valorPrincipal) {
        this.valorPrincipal = valorPrincipal;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }
}
