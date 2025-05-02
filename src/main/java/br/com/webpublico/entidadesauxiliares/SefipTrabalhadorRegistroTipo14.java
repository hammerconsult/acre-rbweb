/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CategoriaSEFIP;
import br.com.webpublico.entidades.PessoaFisica;

import java.util.Date;

/**
 * @author andre
 */
public class SefipTrabalhadorRegistroTipo14 {

    private CategoriaSEFIP categoriaSEFIP;
    private PessoaFisica pessoaFisica;
    private Date dataAdmissao;

    public SefipTrabalhadorRegistroTipo14() {
    }

    public SefipTrabalhadorRegistroTipo14(CategoriaSEFIP categoriaSEFIP, PessoaFisica pessoaFisica, Date dataAdmissao) {
        this.categoriaSEFIP = categoriaSEFIP;
        this.pessoaFisica = pessoaFisica;
        this.dataAdmissao = dataAdmissao;
    }

    public CategoriaSEFIP getCategoriaSEFIP() {
        return categoriaSEFIP;
    }

    public void setCategoriaSEFIP(CategoriaSEFIP categoriaSEFIP) {
        this.categoriaSEFIP = categoriaSEFIP;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }
}
