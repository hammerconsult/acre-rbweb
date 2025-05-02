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
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author reidocrime
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Ações Beneficiário")
public class AcoesBeneficiario extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EntidadeBeneficiaria entidadeBeneficiaria;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    public AcoesBeneficiario() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EntidadeBeneficiaria getEntidadeBeneficiaria() {
        return entidadeBeneficiaria;
    }

    public void setEntidadeBeneficiaria(EntidadeBeneficiaria entidadeBeneficiaria) {
        this.entidadeBeneficiaria = entidadeBeneficiaria;
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
}
