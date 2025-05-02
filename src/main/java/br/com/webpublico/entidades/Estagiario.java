/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.TipoNaturezaEstagio;
import br.com.webpublico.enums.rh.TipoNivelEstagio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta(value = "Estagiário", genero = "M")
public class Estagiario extends ContratoFP implements Serializable {

    @Etiqueta("Estagiário")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Agente Pagador")
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Formação")
    private String formacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo da Natureza do Estágio")
    private TipoNaturezaEstagio tipoNaturezaEstagio;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo do Nível do Estágio")
    private TipoNivelEstagio tipoNivelEstagio;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data prevista de término")
    @Obrigatorio
    private Date dataPrevistaTermino;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Instituição de Ensino")
    private PessoaJuridica instituicaoEnsino;

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public TipoNaturezaEstagio getTipoNaturezaEstagio() {
        return tipoNaturezaEstagio;
    }

    public void setTipoNaturezaEstagio(TipoNaturezaEstagio tipoNaturezaEstagio) {
        this.tipoNaturezaEstagio = tipoNaturezaEstagio;
    }

    public TipoNivelEstagio getTipoNivelEstagio() {
        return tipoNivelEstagio;
    }

    public void setTipoNivelEstagio(TipoNivelEstagio tipoNivelEstagio) {
        this.tipoNivelEstagio = tipoNivelEstagio;
    }

    public Date getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public void setDataPrevistaTermino(Date dataPrevistaTermino) {
        this.dataPrevistaTermino = dataPrevistaTermino;
    }

    public PessoaJuridica getInstituicaoEnsino() {
        return instituicaoEnsino;
    }

    public void setInstituicaoEnsino(PessoaJuridica instituicaoEnsino) {
        this.instituicaoEnsino = instituicaoEnsino;
    }

    @Override
    public String toString() {
        return pessoaFisica.getNome();
    }

    @Override
    public ContratoFP getContratoFP() {
        return null;
    }
}
