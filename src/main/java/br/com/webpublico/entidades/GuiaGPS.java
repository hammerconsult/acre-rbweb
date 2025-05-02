package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by RenatoRomanini on 11/05/2015.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class GuiaGPS extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código da Receita Tributo")
    private String codigoReceitaTributo;
    @Obrigatorio
    @Etiqueta("Código Identficador do Tributo")
    private String codigoIdentificacaoTributo;
    @Obrigatorio
    @Etiqueta("Competência")
    @Temporal(TemporalType.DATE)
    private Date periodoCompetencia;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor Previsto no INSS")
    private BigDecimal valorPrevistoINSS;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor Outras Entidades")
    private BigDecimal valorOutrasEntidades;
    @Obrigatorio
    @Monetario
    @Etiqueta("Atualização Monetária")
    private BigDecimal atualizacaoMonetaria;

    public GuiaGPS() {
        this.valorPrevistoINSS = BigDecimal.ZERO;
        this.valorOutrasEntidades = BigDecimal.ZERO;
        this.atualizacaoMonetaria = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoReceitaTributo() {
        return codigoReceitaTributo;
    }

    public void setCodigoReceitaTributo(String codigoReceitaTributo) {
        this.codigoReceitaTributo = codigoReceitaTributo;
    }

    public String getCodigoIdentificacaoTributo() {
        return codigoIdentificacaoTributo;
    }

    public void setCodigoIdentificacaoTributo(String codigoIdentificacaoTributo) {
        this.codigoIdentificacaoTributo = codigoIdentificacaoTributo;
    }

    public Date getPeriodoCompetencia() {
        return periodoCompetencia;
    }

    public void setPeriodoCompetencia(Date periodoCompetencia) {
        this.periodoCompetencia = periodoCompetencia;
    }

    public BigDecimal getValorPrevistoINSS() {
        return valorPrevistoINSS;
    }

    public void setValorPrevistoINSS(BigDecimal valorPrevistoINSS) {
        this.valorPrevistoINSS = valorPrevistoINSS;
    }

    public BigDecimal getValorOutrasEntidades() {
        return valorOutrasEntidades;
    }

    public void setValorOutrasEntidades(BigDecimal valorOutrasEntidades) {
        this.valorOutrasEntidades = valorOutrasEntidades;
    }

    public BigDecimal getAtualizacaoMonetaria() {
        return atualizacaoMonetaria;
    }

    public void setAtualizacaoMonetaria(BigDecimal atualizacaoMonetaria) {
        this.atualizacaoMonetaria = atualizacaoMonetaria;
    }
}
