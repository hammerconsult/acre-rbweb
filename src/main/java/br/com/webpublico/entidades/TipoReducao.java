package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/10/14
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Tipo de Redução")
public class TipoReducao implements Serializable, ValidadorEntidade, ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Tipo de Redução")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoReducaoValorBem tipoReducaoValorBem;

    @Etiqueta("Grupo Bem")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @OneToOne
    private GrupoBem grupoBem;

    @Positivo(permiteZero = true)
    @Etiqueta("Vida Útil")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private BigDecimal vidaUtilEmAnos;

    @Positivo(permiteZero = true)
    @Etiqueta("Valor Residual")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private BigDecimal valorResidual;

    @Positivo(permiteZero = true)
    @Etiqueta("Taxa de Redução Anual")
    @Transient
    @Tabelavel
    private BigDecimal taxaReducaoAnual;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @Transient
    @Invisivel
    private Long criadoEm;

    public TipoReducao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoReducaoValorBem getTipoReducaoValorBem() {
        return tipoReducaoValorBem;
    }

    public void setTipoReducaoValorBem(TipoReducaoValorBem tipoReducaoValorBem) {
        this.tipoReducaoValorBem = tipoReducaoValorBem;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getVidaUtilEmAnos() {
        return vidaUtilEmAnos;
    }

    public void setVidaUtilEmAnos(BigDecimal vidaUtilEmAnos) {
        this.vidaUtilEmAnos = vidaUtilEmAnos;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public BigDecimal getTaxaReducaoAnual() {
        return taxaReducaoAnual;
    }

    public void setTaxaReducaoAnual(BigDecimal taxaReducaoAnual) {
        this.taxaReducaoAnual = taxaReducaoAnual;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (tipoReducaoValorBem != null && !tipoReducaoValorBem.equals(TipoReducaoValorBem.NAO_APLICAVEL)) {
            if (BigDecimal.ZERO.compareTo(vidaUtilEmAnos) >= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Vuda Útil em Anos não pode ser menor ou igual a zero(0).");
            }
            if (BigDecimal.ZERO.compareTo(taxaReducaoAnual) >= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Taxa de Redução Anual não pode ser menor ou igual a zero(0).");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public BigDecimal processarTaxaDeReducaoAnual() {
        if (this.getVidaUtilEmAnos() != null && this.getVidaUtilEmAnos().compareTo(BigDecimal.ZERO) != 0) {
            return new BigDecimal(100).divide(this.getVidaUtilEmAnos(), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
