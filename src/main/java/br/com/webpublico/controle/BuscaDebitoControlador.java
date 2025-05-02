/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValorDividaRetornoBD;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@SessionScoped
@ManagedBean
public class BuscaDebitoControlador implements Serializable {

    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @Limpavel(Limpavel.NULO)
    private List<ParcelaValorDivida> lista;
    private List<OpcaoPagamento> listaOpcaoPagamento;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroInscricao;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroLote;
    @Limpavel(Limpavel.NULO)
    private Pessoa filtroContribuinte;
    private Date filtroData;
    private ConverterAutoComplete converterContribuinte;
    private ValorDivida selecionado;
    private List<Propriedade> proprietarios;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String vencidos;
    private Date inicioVencimento;
    private Date finalVencimento;
    private Date dataAtual;
    @Limpavel(Limpavel.ZERO)
    private int filtroTipoLote;
    private ParcelaValorDivida[] parcelas;

    public BuscaDebitoControlador() {
        parcelas = new ParcelaValorDivida[0];
        proprietarios = new ArrayList<Propriedade>();
        vencidos = "todos";
        dataAtual = new Date();
    }

    public ParcelaValorDivida[] getParcelas() {
        return parcelas;
    }

    public void setParcelas(ParcelaValorDivida[] parcelas) {
        this.parcelas = parcelas;
    }

    public String getVencidos() {
        return vencidos;
    }

    public void setVencidos(String vencidos) {
        this.vencidos = vencidos;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public ValorDivida getSelecionado() {
        return selecionado;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public List<Propriedade> getProprietarios() {
        return proprietarios;
    }

    public void setProprietarios(List<Propriedade> proprietarios) {
        this.proprietarios = proprietarios;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public void setSelecionado(ValorDivida selecionado) {
        this.selecionado = selecionado;
    }

    public Converter getConverterContribuinte() {
        if (converterContribuinte == null) {
            converterContribuinte = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterContribuinte;
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public Date getFiltroData() {
        return filtroData;
    }

    public void setFiltroData(Date filtroData) {
        this.filtroData = filtroData;
    }

    public String getFiltroInscricao() {
        return filtroInscricao;
    }

    public void setFiltroInscricao(String filtroInscricao) {
        this.filtroInscricao = filtroInscricao;
    }

    public String getFiltroLote() {
        return filtroLote;
    }

    public void setFiltroLote(String filtroLote) {
        this.filtroLote = filtroLote;
    }

    public Date getFinalVencimento() {
        return finalVencimento;
    }

    public void setFinalVencimento(Date finalVencimento) {
        this.finalVencimento = finalVencimento;
    }

    public Date getInicioVencimento() {
        return inicioVencimento;
    }

    public void setInicioVencimento(Date inicioVencimento) {
        this.inicioVencimento = inicioVencimento;
    }

    public List<ParcelaValorDivida> getLista() {
        if (lista == null) {
            lista = new ArrayList<ParcelaValorDivida>();
        }
        return lista;
    }

    public void setLista(List<ParcelaValorDivida> lista) {
        this.lista = lista;
    }

    public List<OpcaoPagamento> getListaOpcaoPagamento() {
        if (listaOpcaoPagamento == null) {
            listaOpcaoPagamento = new ArrayList<OpcaoPagamento>();
        }
        return listaOpcaoPagamento;
    }

    public int getFiltroTipoLote() {
        return filtroTipoLote;
    }

    public void setFiltroTipoLote(int filtroTipoLote) {
        this.filtroTipoLote = filtroTipoLote;
    }

    public void setListaOpcaoPagamento(List<OpcaoPagamento> listaOpcaoPagamentos) {
        this.listaOpcaoPagamento = listaOpcaoPagamentos;
    }

    public List<Pessoa> completaContribuinte(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public void filtrar() {
        lista = new ArrayList<ParcelaValorDivida>();
        if ((filtroTipoLote > 0 && filtroTipoLote < 4) && (filtroLote == null || filtroLote.trim().isEmpty())) {
            FacesUtil.addAtencao("Com o Tipo de Lote selecionado é necessário informar o Lote");
        } else if ((filtroInscricao == null || "".equals(filtroInscricao.trim()))
                && (filtroLote == null || "".equals(filtroLote.trim()))
                && (filtroContribuinte == null || filtroContribuinte.getId() == null)) {
            FacesUtil.addAtencao("Necessário selecionar ao menos um filtro");
        } else {
            try {
                lista = consultaDebitoFacade.listaParcelaDebito(filtroContribuinte, filtroInscricao, filtroLote, inicioVencimento, finalVencimento, filtroTipoLote, vencidos);
            } catch (UFMException ex) {
                FacesUtil.addError("Impossível continuar", ex.getMessage());
            }
        }

    }

    public void calculaAcrescimosParcela() {
    }

    public List<SelectItem> getTiposLote() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(0, " "));
        toReturn.add(new SelectItem(1, "Loteamento"));
        toReturn.add(new SelectItem(2, "Município"));
        toReturn.add(new SelectItem(3, "Loteamento/Município"));
        return toReturn;
    }

    public void selecionar(ActionEvent evento) {
        ValorDividaRetornoBD vdRetorno = (ValorDividaRetornoBD) evento.getComponent().getAttributes().get("objeto");
        selecionado = consultaDebitoFacade.recuperar(vdRetorno.getId().longValue());
        proprietarios = consultaDebitoFacade.recuperaPorCadastroImobiliario(((CalculoIPTU) selecionado.getCalculo()).getCadastroImobiliario());
    }

    public void limparCampos() {
        Util.limparCampos(this);
        this.vencidos = "todos";
    }

    public BigDecimal getTotalValorParcela() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaValorDivida p : parcelas) {
            total = total.add(p.getValor());
        }
        return total;
    }

    public BigDecimal getTotalValorMulta() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaValorDivida p : parcelas) {
            total = total.add(p.getValorMulta());
        }
        return total;
    }

    public BigDecimal getTotalValorJuros() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaValorDivida p : parcelas) {
            total = total.add(p.getValorJuros());
        }
        return total;
    }

//    public BigDecimal getTotalValorCorrecao() {
//        BigDecimal total = BigDecimal.ZERO;
//        for (ParcelaValorDivida p : parcelas) {
//            total = total.add(p.getValorCorrecaoMonetaria());
//        }
//        return total;
//    }

    public BigDecimal getTotalValorAcrescimo() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaValorDivida p : parcelas) {
            total = total.add(p.getValorAcrescimo());
        }
        return total;
    }

    public BigDecimal getTotalValor() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaValorDivida p : parcelas) {
            total = total.add(p.getValorTotal());
        }
        return total;
    }
}
