package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Saldo da Receita ORC")
public class SaldoReceitaORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @ManyToOne
    private Conta contaReceita;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    private BigDecimal previsaoInicial;
    private BigDecimal previsaoAdicional;
    private BigDecimal anulacaoPrevisao;
    private BigDecimal arrecadacao;
    @Transient
    private Long criadoEm;

    public SaldoReceitaORC() {
        criadoEm = System.nanoTime();
        this.setAnulacaoPrevisao(BigDecimal.ZERO);
        this.setArrecadacao(BigDecimal.ZERO);
        this.setPrevisaoInicial(BigDecimal.ZERO);
        this.setPrevisaoAdicional(BigDecimal.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getPrecisaoAdicional() {
        return previsaoAdicional;
    }

    public void setPrevisaoAdicional(BigDecimal previsaoAdicional) {
        this.previsaoAdicional = previsaoAdicional;
    }

    public BigDecimal getPrevisaoInicial() {
        return previsaoInicial;
    }

    public void setPrevisaoInicial(BigDecimal previsaoInicial) {
        this.previsaoInicial = previsaoInicial;
    }

    public BigDecimal getAnulacaoPrevisao() {
        return anulacaoPrevisao;
    }

    public void setAnulacaoPrevisao(BigDecimal anulacaoPrevisao) {
        this.anulacaoPrevisao = anulacaoPrevisao;
    }

    public BigDecimal getArrecadacao() {
        return arrecadacao;
    }

    public void setArrecadacao(BigDecimal arrecadacao) {
        this.arrecadacao = arrecadacao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.SaldoReceitaORC[ id=" + id + " ]";
    }
}
