/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class Laudo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Número Laudo")
    private String numeroLaudo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Inicio Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Final Vigência(Validade Laudo)")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @OneToMany(mappedBy = "laudo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLaudo> itensLaudos;

    public Laudo() {
        itensLaudos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getNumeroLaudo() {
        return numeroLaudo;
    }

    public void setNumeroLaudo(String numeroLaudo) {
        this.numeroLaudo = numeroLaudo;
    }

    public List<ItemLaudo> getItensLaudos() {
        return itensLaudos;
    }

    public void setItensLaudos(List<ItemLaudo> itensLaudos) {
        this.itensLaudos = itensLaudos;
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
        if (!(object instanceof Laudo)) {
            return false;
        }
        Laudo other = (Laudo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        return "Número : " + numeroLaudo + " - Início Vigência : " + sf.format(inicioVigencia) + " - Final Vigência(Validade) :" + sf.format(fimVigencia);
    }
}
