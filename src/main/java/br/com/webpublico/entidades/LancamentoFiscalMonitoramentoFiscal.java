package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta("Lançamento Fiscal do Monitoramento Fiscal")
@Table(name = "LANCAMENTOFISCALMONFISCAL")
@Entity
@Audited
public class LancamentoFiscalMonitoramentoFiscal extends SuperEntidade implements Comparable<LancamentoFiscalMonitoramentoFiscal> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RegistroLancamentoContabilMonitoramentoFiscal registroLancContabilMF;
    private Long sequencia;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private Integer ano;
    private BigDecimal valorDeclarado;
    private BigDecimal valorApurado;
    private BigDecimal baseCalculo;
    private BigDecimal aliquotaISS;
    private BigDecimal issPago;
    private BigDecimal issApurado;
    private BigDecimal issDevido;
    private BigDecimal indiceCorrecao;
    private BigDecimal correcao;
    private BigDecimal juros;
    private BigDecimal multa;
    private Boolean tributado;
    private Boolean semMovimento;
    private Boolean nfse;
    private Boolean arbitrado = false;

    public LancamentoFiscalMonitoramentoFiscal() {
        zeraValores();
        this.nfse = false;
    }

    public void zeraValores() {
        criadoEm = System.nanoTime();
        valorDeclarado = BigDecimal.ZERO;
        valorApurado = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
        aliquotaISS = BigDecimal.ZERO;
        issApurado = BigDecimal.ZERO;
        issPago = BigDecimal.ZERO;
        issDevido = BigDecimal.ZERO;
        indiceCorrecao = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        tributado = true;
        semMovimento = false;
    }

    public BigDecimal getJuros() {
        if (juros == null) {
            return BigDecimal.ZERO;
        }
        return juros.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        if (multa == null) {
            return BigDecimal.ZERO;
        }
        return multa.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroLancamentoContabilMonitoramentoFiscal getRegistroLancContabilMF() {
        return registroLancContabilMF;
    }

    public void setRegistroLancContabilMF(RegistroLancamentoContabilMonitoramentoFiscal registroLancContabilMF) {
        this.registroLancContabilMF = registroLancContabilMF;
    }

    public BigDecimal getAliquotaISS() {
        if (aliquotaISS == null) {
            return BigDecimal.ZERO;
        }
        return aliquotaISS.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setAliquotaISS(BigDecimal aliquotaISS) {
        this.aliquotaISS = aliquotaISS;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getBaseCalculo() {
        if (baseCalculo == null) {
            return BigDecimal.ZERO;
        }
        return baseCalculo.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getIssApurado() {
        if (issApurado == null) {
            return BigDecimal.ZERO;
        }
        return issApurado.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setIssApurado(BigDecimal issApurado) {
        this.issApurado = issApurado;
    }

    public BigDecimal getIssDevido() {
        if (issDevido == null) {
            return BigDecimal.ZERO;
        }
        return issDevido.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setIssDevido(BigDecimal issDevido) {
        this.issDevido = issDevido;
    }

    public BigDecimal getValorCorrigido() {
        return getCorrecao().add(getIssDevido());
    }

    public BigDecimal getIssPago() {
        if (issPago == null) {
            return BigDecimal.ZERO;
        }
        return issPago.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setIssPago(BigDecimal issPago) {
        this.issPago = issPago;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public BigDecimal getValorApurado() {
        if (valorApurado == null) {
            return BigDecimal.ZERO;
        }
        return valorApurado.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setValorApurado(BigDecimal valorApurado) {
        this.valorApurado = valorApurado;
    }

    public BigDecimal getValorDeclarado() {
        if (valorDeclarado == null) {
            return BigDecimal.ZERO;
        }
        return valorDeclarado.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public BigDecimal getIndiceCorrecao() {
        if (indiceCorrecao == null) {
            return BigDecimal.ZERO;
        }
        return indiceCorrecao;
    }

    public void setIndiceCorrecao(BigDecimal indiceCorrecao) {
        this.indiceCorrecao = indiceCorrecao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public BigDecimal getCorrecao() {
        if (correcao == null) {
            return BigDecimal.ZERO;
        }
        return correcao.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Ano..: " + this.getAno() + " mes..: " + this.getMes() + " aliquota..: " + this.getAliquotaISS() + " valor..: " + this.getValorApurado() + " ID..: " + this.id + " Tributado: " + this.tributado;
    }

    @Override
    public int compareTo(LancamentoFiscalMonitoramentoFiscal o) {
        try {
            int retorno = this.getAno().compareTo(o.getAno());

            if (retorno == 0) {
                retorno = this.getMes().compareTo(o.getMes());
            }
            if (retorno == 0) {
                retorno = this.getAliquotaISS().compareTo(o.getAliquotaISS());
            }
            if (retorno == 0) {
                retorno = this.getTributado().compareTo(o.getTributado());
            }
            return retorno;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public boolean getValorDeclaradoInvalido() {
        return (this.getValorDeclarado().compareTo(BigDecimal.ZERO) <= 0)
            && (this.getIssPago().compareTo(BigDecimal.ZERO) > 0);
    }

    public Boolean getTributado() {
        return tributado != null ? tributado : true;
    }

    public void setTributado(Boolean tributado) {
        this.tributado = tributado;
    }

    public Boolean getSemMovimento() {
        return semMovimento != null ? semMovimento : false;
    }

    public void setSemMovimento(Boolean semMovimento) {
        this.semMovimento = semMovimento;
    }

    public Boolean getNfse() {
        return nfse != null ? nfse : false;
    }

    public void setNfse(Boolean nfse) {
        this.nfse = nfse;
    }

    public boolean isArbitrado() {
        return arbitrado;
    }

    public void setArbitrado(boolean arbitrado) {
        this.arbitrado = arbitrado;
    }
}
