/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Item da Regra Deducao Dias Direito Ferias")
public class ItemRegraDeducaoDDF implements Serializable, ValidadorEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade Inicial")
    private Double quantidadeInicial;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade Final")
    private Double quantidadeFinal;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Dias de Desconto")
    private Double diasDeDesconto;

    @ManyToOne
    private RegraDeducaoDDF regraDeducaoDDF;

    @Invisivel
    @Transient
    private Long criadoEm;

    public ItemRegraDeducaoDDF() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDiasDeDesconto() {
        return diasDeDesconto;
    }

    public void setDiasDeDesconto(Double diasDeDesconto) {
        this.diasDeDesconto = diasDeDesconto;
    }

    public Double getQuantidadeFinal() {
        return quantidadeFinal;
    }

    public void setQuantidadeFinal(Double quantidadeFinal) {
        this.quantidadeFinal = quantidadeFinal;
    }

    public Double getQuantidadeInicial() {
        return quantidadeInicial;
    }

    public void setQuantidadeInicial(Double quantidadeInicial) {
        this.quantidadeInicial = quantidadeInicial;
    }

    public RegraDeducaoDDF getRegraDeducaoDDF() {
        return regraDeducaoDDF;
    }

    public void setRegraDeducaoDDF(RegraDeducaoDDF regraDeducaoDDF) {
        this.regraDeducaoDDF = regraDeducaoDDF;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemRegraDeducaoDDF[ id=" + id + " ]";
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
