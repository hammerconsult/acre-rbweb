/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 15/06/15
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity
@Etiqueta("Beneficiário da Emenda")
public class BeneficiarioEmenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Emenda")
    private Emenda emenda;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "beneficiarioEmenda", orphanRemoval = true)
    private List<ResponsavelBenefEmenda> responsavelBeneficiarioEmendas;

    public BeneficiarioEmenda() {
        super();
        this.responsavelBeneficiarioEmendas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<ResponsavelBenefEmenda> getResponsavelBeneficiarioEmendas() {
        return responsavelBeneficiarioEmendas;
    }

    public void setResponsavelBeneficiarioEmendas(List<ResponsavelBenefEmenda> responsavelBeneficiarioEmendas) {
        this.responsavelBeneficiarioEmendas = responsavelBeneficiarioEmendas;
    }
}
