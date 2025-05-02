package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.TipoRequerenteCredencialRBTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 18/07/13
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public class FiltroGeracaoCredencialRBTrans {

    private Logger logger = LoggerFactory.getLogger(FiltroGeracaoCredencialRBTrans.class);
    private Integer anoValidade;
    private Integer numeroPermissaoInicial;
    private Integer numeroPermissaoFinal;
    private Integer finalPermissaoInicial;
    private Integer finalPermissaoFinal;
    private TipoCredencialRBTrans tipoCredencialRBTrans;
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private TipoRequerenteCredencialRBTrans tipoRequerenteCredencialRBTrans;
    private String nome;
    private String cpf;
    private String cnpj;
    private String cmc;
    private Date dataValidade;

    public FiltroGeracaoCredencialRBTrans() {
        anoValidade = null;
        numeroPermissaoInicial = null;
        numeroPermissaoFinal = null;
        finalPermissaoInicial = null;
        finalPermissaoFinal = null;
        tipoCredencialRBTrans = null;
        tipoPermissaoRBTrans = null;
        tipoRequerenteCredencialRBTrans = null;
        nome = null;
        cpf = null;
        cnpj = null;
        cmc = null;
    }


    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Integer getAnoValidade() {
        return anoValidade;
    }

    public void setAnoValidade(Integer anoValidade) {
        this.anoValidade = anoValidade;
    }

    public TipoCredencialRBTrans getTipoCredencialRBTrans() {
        return tipoCredencialRBTrans;
    }

    public void setTipoCredencialRBTrans(TipoCredencialRBTrans tipoCredencialRBTrans) {
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
    }

    public Integer getNumeroPermissaoInicial() {
        return numeroPermissaoInicial;
    }

    public void setNumeroPermissaoInicial(Integer numeroPermissaoInicial) {
        this.numeroPermissaoInicial = numeroPermissaoInicial;
    }

    public Integer getNumeroPermissaoFinal() {
        return numeroPermissaoFinal;
    }

    public void setNumeroPermissaoFinal(Integer numeroPermissaoFinal) {
        this.numeroPermissaoFinal = numeroPermissaoFinal;
    }

    public Integer getFinalPermissaoInicial() {
        return finalPermissaoInicial;
    }

    public void setFinalPermissaoInicial(Integer finalPermissaoInicial) {
        this.finalPermissaoInicial = finalPermissaoInicial;
    }

    public Integer getFinalPermissaoFinal() {
        return finalPermissaoFinal;
    }

    public void setFinalPermissaoFinal(Integer finalPermissaoFinal) {
        this.finalPermissaoFinal = finalPermissaoFinal;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public TipoRequerenteCredencialRBTrans getTipoRequerenteCredencialRBTrans() {
        return tipoRequerenteCredencialRBTrans;
    }

    public void setTipoRequerenteCredencialRBTrans(TipoRequerenteCredencialRBTrans tipoRequerenteCredencialRBTrans) {
        this.tipoRequerenteCredencialRBTrans = tipoRequerenteCredencialRBTrans;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }
}
