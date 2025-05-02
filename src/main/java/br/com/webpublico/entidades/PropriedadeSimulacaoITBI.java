/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@Entity
@Audited
public class PropriedadeSimulacaoITBI implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private CalculoITBI calculoITBI;
    @Etiqueta("Proporção")
    private Double proporcao;

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Double getProporcao() {
        return proporcao != null ? proporcao : 0.0;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return pessoa.getNomeCpfCnpj();
    }

    public Boolean temPropriedade(List<PropriedadeSimulacaoITBI> propriedades, PropriedadeSimulacaoITBI propriedade) {
        for (PropriedadeSimulacaoITBI p : propriedades) {
            if (p.getPessoa().getId().equals(propriedade.getPessoa().getId())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
