package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/08/14
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class ItemProcessoFiscalizacao implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ProcessoFiscalizacao processoFiscalizacao;
    @ManyToOne
    private TarefaAuditorFiscal tarefaAuditorFiscal;
    @ManyToOne
    @JoinColumn(name = "INFRACAO_ID")
    private InfracaoFiscalizacaoSecretaria infracaoFiscalizacaoSecretaria;
    @ManyToOne
    @JoinColumn(name = "PENALIDADE_ID")
    private PenalidadeFiscalizacaoSecretaria penalidadeFiscalizacaoSecretaria;
    private BigDecimal valor;
    private BigDecimal quantidade;
    @Transient
    private Long criadoEm;
    private String observacao;

    public ItemProcessoFiscalizacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TarefaAuditorFiscal getTarefaAuditorFiscal() {
        return tarefaAuditorFiscal;
    }

    public void setTarefaAuditorFiscal(TarefaAuditorFiscal tarefaAuditorFiscal) {
        this.tarefaAuditorFiscal = tarefaAuditorFiscal;
    }

    public InfracaoFiscalizacaoSecretaria getInfracaoFiscalizacaoSecretaria() {
        return infracaoFiscalizacaoSecretaria;
    }

    public void setInfracaoFiscalizacaoSecretaria(InfracaoFiscalizacaoSecretaria infracaoFiscalizacaoSecretaria) {
        this.infracaoFiscalizacaoSecretaria = infracaoFiscalizacaoSecretaria;
    }

    public PenalidadeFiscalizacaoSecretaria getPenalidadeFiscalizacaoSecretaria() {
        return penalidadeFiscalizacaoSecretaria;
    }

    public void setPenalidadeFiscalizacaoSecretaria(PenalidadeFiscalizacaoSecretaria penalidadeFiscalizacaoSecretaria) {
        this.penalidadeFiscalizacaoSecretaria = penalidadeFiscalizacaoSecretaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal valorTotalUFM() {
        return this.getValor().multiply(this.getQuantidade()).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal valorTotalUFMConvertido(BigDecimal valorUFMConvertido) {
        return this.getValor().multiply(this.getQuantidade()).multiply(valorUFMConvertido).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal valorReais(BigDecimal valorUFMConvertido) {
        if (PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM.equals(penalidadeFiscalizacaoSecretaria.getTipoCobranca())) {
            return this.getValor().multiply(valorUFMConvertido).setScale(2, RoundingMode.HALF_EVEN);
        }
        return this.getValor();
    }

    public BigDecimal valorTotalReais(BigDecimal valorUFMConvertido) {
        if (PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM.equals(penalidadeFiscalizacaoSecretaria.getTipoCobranca())) {
            return this.getValor().multiply(this.getQuantidade()).multiply(valorUFMConvertido).setScale(2, RoundingMode.HALF_EVEN);
        }
        return this.getValor().multiply(this.getQuantidade()).setScale(2, RoundingMode.HALF_EVEN);
    }

    public ProcessoFiscalizacao getProcessoFiscalizacao() {
        return processoFiscalizacao;
    }

    public void setProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao) {
        this.processoFiscalizacao = processoFiscalizacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
