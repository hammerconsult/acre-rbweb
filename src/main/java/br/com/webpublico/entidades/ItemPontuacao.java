/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
public class ItemPontuacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pontuacao pontuacao;
    @JoinTable(name = "IPont_VPos")
    @ManyToMany
    private List<ValorPossivel> valores;
    private BigDecimal pontos;
    private BigDecimal pontoPredial;

    public ItemPontuacao() {
        pontos = BigDecimal.ZERO;
        pontoPredial = BigDecimal.ZERO;
        valores = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPontoPredial() {
        return pontoPredial;
    }

    public void setPontoPredial(BigDecimal pontoPredial) {
        this.pontoPredial = pontoPredial;
    }

    public BigDecimal getPontos() {
        return pontos;
    }

    public void setPontos(BigDecimal pontos) {
        this.pontos = pontos;
    }

    public Pontuacao getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Pontuacao pontuacao) {
        this.pontuacao = pontuacao;
    }

    public List<ValorPossivel> getValores() {
        return valores;
    }

    public void setValores(List<ValorPossivel> valores) {
        this.valores = valores;
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
        if (!(object instanceof ItemPontuacao)) {
            return false;
        }
        ItemPontuacao other = (ItemPontuacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemPontuacao[id=" + id + "]";
    }
}
