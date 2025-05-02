/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Base do Período Aquisitivo")
public class BasePeriodoAquisitivo extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Duração")
    @Obrigatorio
    private Integer duracao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Dias de Direito")
    private Integer diasDeDireito;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Período Aquisitivo")
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;

    public BasePeriodoAquisitivo() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDiasDeDireito() {
        return diasDeDireito;
    }

    public void setDiasDeDireito(Integer diasDeDireito) {
        this.diasDeDireito = diasDeDireito;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isTipoLicenca() {
        return TipoPeriodoAquisitivo.LICENCA.equals(tipoPeriodoAquisitivo);
    }

    public boolean isTipoFerias() {
        return TipoPeriodoAquisitivo.FERIAS.equals(tipoPeriodoAquisitivo);
    }
}
