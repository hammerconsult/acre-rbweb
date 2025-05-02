/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

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
@Etiqueta("Responsável Beneficiário Emenda")
public class ResponsavelBenefEmenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Beneficiário Emenda")
    private BeneficiarioEmenda beneficiarioEmenda;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa")
    private Pessoa pessoa;

    public ResponsavelBenefEmenda() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BeneficiarioEmenda getBeneficiarioEmenda() {
        return beneficiarioEmenda;
    }

    public void setBeneficiarioEmenda(BeneficiarioEmenda beneficiarioEmenda) {
        this.beneficiarioEmenda = beneficiarioEmenda;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}
