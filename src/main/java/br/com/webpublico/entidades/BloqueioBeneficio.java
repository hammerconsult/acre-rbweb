package br.com.webpublico.entidades;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 22/08/14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Bloqueio de Benefício")
public class BloqueioBeneficio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Servidor")
    @Tabelavel
    @ManyToOne
    private ContratoFP contratoFP;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    //@Pesquisavel
    @Enumerated(EnumType.STRING)
    //@Tabelavel
    @Etiqueta("Tipo de Bloqueio")
    private TipoBloqueio tipoBloqueio;
    @Tabelavel
    @Pesquisavel
    private Boolean bloqueado;
    private String observacao;
    @Transient
    private Long criadoEm = System.nanoTime();
    @Transient
    private Long idContrato;

    public long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(long idContrato) {
        this.idContrato = idContrato;
    }


    public BloqueioBeneficio() {
        bloqueado = false;
    }

    public BloqueioBeneficio(Long idContrato, TipoBloqueio tipoBloqueio) {
        this.tipoBloqueio = tipoBloqueio;
        this.idContrato = idContrato;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public TipoBloqueio getTipoBloqueio() {
        return tipoBloqueio;
    }

    public void setTipoBloqueio(TipoBloqueio tipoBloqueio) {
        this.tipoBloqueio = tipoBloqueio;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public void valida() {
        if (inicioVigencia == null) {
            throw new ExcecaoNegocioGenerica("Preencha o inicio de vigência");
        }
        if (inicioVigencia != null && finalVigencia != null) {
            if (inicioVigencia.after(finalVigencia)) {
                throw new ExcecaoNegocioGenerica("O inicio de vigência deve ser menor que o final de vigência");
            }
        }
    }
}
