/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
public class FebrabanHeaderArquivo implements Serializable {

    private Agencia agencia;
    private Banco banco;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaBancariaEntidade contaBancariaEntidade;
    private PessoaJuridica pessoaJuridica;
    private Entidade entidade;
    private Integer sequencia;
    private String lote;

    public FebrabanHeaderArquivo() {
    }

    public FebrabanHeaderArquivo(Agencia agencia, Banco banco, UnidadeOrganizacional unidadeOrganizacional, ContaBancariaEntidade contaBancariaEntidade, PessoaJuridica pessoaJuridica, Entidade entidade) {
        this.agencia = agencia;
        this.banco = banco;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaBancariaEntidade = contaBancariaEntidade;
        this.pessoaJuridica = pessoaJuridica;
        this.entidade = entidade;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getHeaderArquivo() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(getBanco().getNumeroBanco(), 3, "0"));

        lote = "0000";
        sb.append(lote);
        sb.append("0");
        sb.append(StringUtils.leftPad(" ", 9, " "));
        sb.append("2");
        sb.append(StringUtil.cortaOuCompletaEsquerda(pessoaJuridica.getCnpj().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(contaBancariaEntidade.getCodigoDoConvenio() != null ? contaBancariaEntidade.getCodigoDoConvenio() : " ", 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(agencia.getNumeroAgencia(), 5, "0"));
        sb.append(agencia.getDigitoVerificador() != null ? agencia.getDigitoVerificador() : " ");
        sb.append(StringUtil.cortaOuCompletaDireita(contaBancariaEntidade.getNumeroConta(), 12, "0"));
        sb.append(contaBancariaEntidade.getDigitoVerificador());
        sb.append(" ");
        sb.append(StringUtil.cortaOuCompletaDireita(pessoaJuridica.getRazaoSocial(), 30, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(banco.getDescricao(), 30, " "));
        sb.append(StringUtils.leftPad(" ", 10, " "));
        sb.append("1");
        sb.append(Util.dateToString(new Date()).replaceAll("/", ""));
        sb.append(Util.hourToString(new Date()).replaceAll(":", ""));
        sb.append(StringUtil.cortaOuCompletaEsquerda(this.sequencia.toString(), 6, "0"));
        sb.append("084");
        sb.append("00000");
        sb.append(StringUtil.cortaOuCompletaEsquerda("", 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda("", 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda("", 29, " "));
        sb.append("\n");
        return sb.toString();
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }
}
