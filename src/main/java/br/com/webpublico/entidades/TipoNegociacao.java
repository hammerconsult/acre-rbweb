package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.envers.Audited;

/**
 *
 * @author fabinho
 */
@CorEntidade("#00ff00")
@Entity
@Audited
@GrupoDiagrama(nome = "Tributacao")
@Etiqueta("Tipo de Negociação")

public class TipoNegociacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel (ALINHAMENTO= Tabelavel.ALINHAMENTO.DIREITA, TIPOCAMPO= Tabelavel.TIPOCAMPO.NUMERO)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("% de Alíquota")
    private BigDecimal aliquota;
    private Boolean ativo;
    private Boolean financiado;

    public TipoNegociacao() {
        financiado = Boolean.FALSE;
        ativo = Boolean.TRUE;
    }

    public TipoNegociacao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFinanciado() {
        return financiado;
    }

    public void setFinanciado(Boolean financiado) {
        this.financiado = financiado;
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
        if (!(object instanceof TipoNegociacao)) {
            return false;
        }
        TipoNegociacao other = (TipoNegociacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
