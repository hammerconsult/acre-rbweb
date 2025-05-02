package br.com.webpublico.entidades.rh.pccr;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Enquadramento Funcional PCCR")
public class EnquadramentoFuncionalPCCR implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private ReferenciaProgressaoPCCR referenciaProgressaoPCCR;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private CategoriaPCCR categoriaPCCR;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private ContratoFP contratoServidor;
    @Monetario
    @Transient
    private BigDecimal vencimentoBase;
    @Transient
    private Long criadoEm = System.nanoTime();

    public EnquadramentoFuncionalPCCR() {
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

    public ReferenciaProgressaoPCCR getReferenciaProgressaoPCCR() {
        return referenciaProgressaoPCCR;
    }

    public void setReferenciaProgressaoPCCR(ReferenciaProgressaoPCCR referenciaProgressaoPCCR) {
        this.referenciaProgressaoPCCR = referenciaProgressaoPCCR;
    }

    public CategoriaPCCR getCategoriaPCCR() {
        return categoriaPCCR;
    }

    public void setCategoriaPCCR(CategoriaPCCR categoriaPCS) {
        this.categoriaPCCR = categoriaPCS;
    }

    public ContratoFP getContratoServidor() {
        return contratoServidor;
    }

    public void setContratoServidor(ContratoFP contratoServidor) {
        this.contratoServidor = contratoServidor;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return contratoServidor + " - " + referenciaProgressaoPCCR + " - " + categoriaPCCR;
    }

    public void valida() {
        if (inicioVigencia == null) {
            throw new ExcecaoNegocioGenerica("Preencha o início de vigência corretamente.");
        }
        if (contratoServidor == null) {
            throw new ExcecaoNegocioGenerica("Preencha o servidor corretamente.");
        }
        if (referenciaProgressaoPCCR == null) {
            throw new ExcecaoNegocioGenerica("Selecione a referência salarial.");
        }
        if (categoriaPCCR == null) {
            throw new ExcecaoNegocioGenerica("Selecione a categoria do servidor.");
        }
        if (inicioVigencia != null && finalVigencia != null) {
            if (inicioVigencia.after(finalVigencia)) {
                throw new ExcecaoNegocioGenerica("O início de vigência deve ser menor que o final de vigência");
            }
        }
    }
}
