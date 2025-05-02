/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta(value = "Beneficiário de Pensão Judicial Tramitada e Julgada", genero = "M")
public class Beneficiario extends VinculoFP implements Serializable {

    @Tabelavel
    @ManyToOne
    @Etiqueta("Pessoa Física")
    private PessoaFisica pessoaFisicaServidor;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Instituidor")
    private ContratoFP contratoFP;
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @OneToMany(mappedBy = "beneficiario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Indenizações")
    private List<ItemBeneficiario> itensBeneficiarios;

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<ItemBeneficiario> getItensBeneficiarios() {
        return itensBeneficiarios;
    }

    public void setItensBeneficiarios(List<ItemBeneficiario> itensBeneficiarios) {
        this.itensBeneficiarios = itensBeneficiarios;
    }

    public PessoaFisica getPessoaFisicaServidor() {
        return pessoaFisicaServidor;
    }

    public void setPessoaFisicaServidor(PessoaFisica pessoaFisicaServidor) {
        this.pessoaFisicaServidor = pessoaFisicaServidor;
    }


    @Override
    public String toString() {
        return (getMatriculaFP() != null ? getMatriculaFP().getMatricula() : "") + "/" + getNumero() + " - " + (getMatriculaFP() != null ? getMatriculaFP().getPessoa().getNome() : "");
    }

}
