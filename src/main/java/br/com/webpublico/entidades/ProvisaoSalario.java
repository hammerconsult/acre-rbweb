/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoProvSalario;
import br.com.webpublico.enums.TipoProvisaoSalario;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@Audited
@Entity
@Etiqueta("Provisão de Salário")

public class ProvisaoSalario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataProvisao;
    @Etiqueta("Tipo Lançamento")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoLancamento tipoLancamento;
    @Obrigatorio
    @Etiqueta("Tipo Operação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoOperacaoProvSalario tipoOperacaoProvSalario;
    @Obrigatorio
    @Etiqueta("Tipo Provisão")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoProvisaoSalario tipoProvisaoSalario;
    @Obrigatorio
    @Etiqueta("Valor")
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Histórico")
    private String historico;
    @ManyToOne
    private Exercicio exercicio;

    public ProvisaoSalario() {
        this.dataProvisao = new Date();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
    }

    public ProvisaoSalario(String numero, Date dataProvisao, TipoLancamento tipoLancamento, TipoOperacaoProvSalario tipoOperacaoProvSalario, TipoProvisaoSalario tipoProvisaoSalario, UnidadeOrganizacional unidadeOrganizacional, String historico, BigDecimal valor, Exercicio exercicio) {
        this.numero = numero;
        this.dataProvisao = dataProvisao;
        this.tipoLancamento = tipoLancamento;
        this.tipoOperacaoProvSalario = tipoOperacaoProvSalario;
        this.tipoProvisaoSalario = tipoProvisaoSalario;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.historico = historico;
        this.valor = valor;
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataProvisao() {
        return dataProvisao;
    }

    public void setDataProvisao(Date dataProvisao) {
        this.dataProvisao = dataProvisao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacaoProvSalario getTipoOperacaoProvSalario() {
        return tipoOperacaoProvSalario;
    }

    public void setTipoOperacaoProvSalario(TipoOperacaoProvSalario tipoOperacaoProvSalario) {
        this.tipoOperacaoProvSalario = tipoOperacaoProvSalario;
    }

    public TipoProvisaoSalario getTipoProvisaoSalario() {
        return tipoProvisaoSalario;
    }

    public void setTipoProvisaoSalario(TipoProvisaoSalario tipoProvisaoSalario) {
        this.tipoProvisaoSalario = tipoProvisaoSalario;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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
        if (!(object instanceof ProvisaoSalario)) {
            return false;
        }
        ProvisaoSalario other = (ProvisaoSalario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProvSalarioFeriasLicenca[ id=" + id + " ]";
    }
}
