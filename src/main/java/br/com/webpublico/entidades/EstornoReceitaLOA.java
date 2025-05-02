/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoReceitaLoa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Estorno de Receita LOA")
public class EstornoReceitaLOA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    @ManyToOne
    @Etiqueta("Conta de Receita")
    @Obrigatorio
    private ReceitaLOA receitaLOA;
    @Etiqueta("Histórico")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String historico;
    @Etiqueta("Valor(R$)")
    @Monetario
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoReceitaLoa tipoOperacaoReceitaLoa;

    public EstornoReceitaLOA() {
        tipoOperacaoReceitaLoa = TipoOperacaoReceitaLoa.PREVISAO;
        valor = BigDecimal.ZERO;
    }

    public EstornoReceitaLOA(String numero, Date dataEstorno, ReceitaLOA receitaLOA, String historico, BigDecimal valor, TipoOperacaoReceitaLoa tipoOperacaoReceitaLoa) {
        this.numero = numero;
        this.dataEstorno = dataEstorno;
        this.receitaLOA = receitaLOA;
        this.historico = historico;
        this.valor = valor;
        this.tipoOperacaoReceitaLoa = tipoOperacaoReceitaLoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
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

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoOperacaoReceitaLoa getTipoOperacaoReceitaLoa() {
        return tipoOperacaoReceitaLoa;
    }

    public void setTipoOperacaoReceitaLoa(TipoOperacaoReceitaLoa tipoOperacaoReceitaLoa) {
        this.tipoOperacaoReceitaLoa = tipoOperacaoReceitaLoa;
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
        if (!(object instanceof EstornoReceitaLOA)) {
            return false;
        }
        EstornoReceitaLOA other = (EstornoReceitaLOA) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.EstornoReceitaLOA[ id=" + id + " ]";
    }
}
