package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sysmar
 * Date: 14/11/13
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public class PesquisaDividas implements Serializable {

    private Date dataLancamento;
    private Date dataVencimento;
    private Integer exercicio;
    private Integer numero;
    private TipoDividaDiversa tipoDivida;
    private String cadastro;
    private TipoCadastroTributario tipoCadastroTributario;
    private SituacaoCalculoDividaDiversa situacaoCalculoDividaDiversa;

    public PesquisaDividas() {
        dataLancamento = null;
        dataVencimento = null;
        exercicio = null;
        numero = null;
        tipoDivida = null;
        cadastro = "";
        tipoCadastroTributario = TipoCadastroTributario.ECONOMICO;
        situacaoCalculoDividaDiversa = SituacaoCalculoDividaDiversa.EFETIVADO;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoDividaDiversa getTipoDivida() {
        return tipoDivida;
    }

    public void setTipoDivida(TipoDividaDiversa tipoDivida) {
        this.tipoDivida = tipoDivida;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public SituacaoCalculoDividaDiversa getSituacaoCalculoDividaDiversa() {
        return situacaoCalculoDividaDiversa;
    }

    public void setSituacaoCalculoDividaDiversa(SituacaoCalculoDividaDiversa situacaoCalculoDividaDiversa) {
        this.situacaoCalculoDividaDiversa = situacaoCalculoDividaDiversa;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}
