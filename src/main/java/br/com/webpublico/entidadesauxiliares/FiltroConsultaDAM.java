package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 20/03/14
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class FiltroConsultaDAM implements Serializable {

    private Pessoa pessoa;
    private Cadastro cadastro;
    private TipoCadastroTributario tipoCadastroTributario;
    private DAM.Situacao situacaoDAM;
    private Long numeroDAM;
    private Exercicio anoDAM;
    private List<Divida> dividasSeleciondas;
    private Divida divida;
    private String codigoLote;
    private String[] codigoBarra;
    private Date dataMovimento;
    private Date dataPagamento;

    public FiltroConsultaDAM() {
        dividasSeleciondas = Lists.newArrayList();
        codigoBarra = new String[8];
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public DAM.Situacao getSituacaoDAM() {
        return situacaoDAM;
    }

    public void setSituacaoDAM(DAM.Situacao situacaoDAM) {
        this.situacaoDAM = situacaoDAM;
    }

    public Long getNumeroDAM() {
        return numeroDAM;
    }

    public void setNumeroDAM(Long numeroDAM) {
        this.numeroDAM = numeroDAM;
    }

    public Exercicio getAnoDAM() {
        return anoDAM;
    }

    public void setAnoDAM(Exercicio anoDAM) {
        this.anoDAM = anoDAM;
    }

    public List<Divida> getDividasSeleciondas() {
        return dividasSeleciondas;
    }

    public void setDividasSeleciondas(List<Divida> dividasSeleciondas) {
        this.dividasSeleciondas = dividasSeleciondas;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getCodigoBarraCompleto() {
        String codigoCompleto = "";
        for (String codigo : codigoBarra) {
            codigoCompleto += codigo;
        }
        return codigoCompleto;
    }

    public String[] getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String[] codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    private boolean validaDivida() {
        boolean valida = true;
        if (divida == null || divida.getId() == null) {
            FacesUtil.addError("Atenção", "Selecione uma dívida para adicionar");
            valida = false;
        } else if (dividasSeleciondas.contains(divida)) {
            FacesUtil.addError("Atenção", "Essa Dívida já foi selecionada.");
            valida = false;
        }
        return valida;
    }

    public void addDivida() {
        if (validaDivida()) {
            dividasSeleciondas.add(divida);
            divida = new Divida();
        }
    }

    public void removeDivida(Divida divida) {
        if (dividasSeleciondas.contains(divida)) {
            dividasSeleciondas.remove(divida);
        }
    }

    public List<Long> getIdsDividasSelecionadas() {
        List<Long> retorno = Lists.newArrayList();
        if (dividasSeleciondas != null && dividasSeleciondas.size() > 0) {
            for (Divida div : dividasSeleciondas) {
                retorno.add(div.getId());
            }
        }
        return retorno;
    }

}
