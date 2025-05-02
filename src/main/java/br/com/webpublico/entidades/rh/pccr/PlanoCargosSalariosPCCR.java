package br.com.webpublico.entidades.rh.pccr;

import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 20/06/14
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Plano de Cargos e Salários")
public class PlanoCargosSalariosPCCR implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Inicio Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final Vigência")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo PCS")
    @Enumerated(EnumType.STRING)
    private TipoPCS tipoPCS;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoPCS getTipoPCS() {
        return tipoPCS;
    }

    public void setTipoPCS(TipoPCS tipoPCS) {
        this.tipoPCS = tipoPCS;
    }

    @Override
    public String toString() {
        return descricao + "- " + tipoPCS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanoCargosSalariosPCCR that = (PlanoCargosSalariosPCCR) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

