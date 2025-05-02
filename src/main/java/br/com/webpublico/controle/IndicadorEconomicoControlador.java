/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.IndicadorEconomico;
import br.com.webpublico.entidades.ValorIndicadorEconomico;
import br.com.webpublico.enums.PeriodicidadeIndicador;
import br.com.webpublico.enums.TipoIndicador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IndicadorEconomicoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author java
 */
@ManagedBean(name = "indicadorEconomicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoIndicadorEconomico", pattern = "/indicador-economico/novo/", viewId = "/faces/tributario/cadastromunicipal/indicadoreconomico/edita.xhtml"),
    @URLMapping(id = "editarIndicadorEconomico", pattern = "/indicador-economico/editar/#{indicadorEconomicoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/indicadoreconomico/edita.xhtml"),
    @URLMapping(id = "listarIndicadorEconomico", pattern = "/indicador-economico/listar/", viewId = "/faces/tributario/cadastromunicipal/indicadoreconomico/lista.xhtml"),
    @URLMapping(id = "verIndicadorEconomico", pattern = "/indicador-economico/ver/#{indicadorEconomicoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/indicadoreconomico/visualizar.xhtml")
})
public class IndicadorEconomicoControlador extends PrettyControlador<IndicadorEconomico> implements Serializable, CRUD {

    @EJB
    private IndicadorEconomicoFacade facade;
    private ValorIndicadorEconomico valorIndicadorEconomico;
    private Boolean bloquearIndicador;
    private Date dataMensal;
    private Date dataDiaria;
    private Date dataAnual;

    public IndicadorEconomicoControlador() {
        super(IndicadorEconomico.class);
    }

    @URLAction(mappingId = "novoIndicadorEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.inicializaListaValorIndicador();
        bloquearIndicador = false;
    }

    @URLAction(mappingId = "verIndicadorEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarIndicadorEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        bloquearIndicador = true;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Date getDataMensal() {
        if (this.dataMensal == null) {
            return new Date();
        }
        return dataMensal;
    }

    public void setDataMensal(Date dataMensal) {
        this.dataMensal = dataMensal;
    }

    public Date getDataDiaria() {
        return this.dataDiaria;
    }

    public void setDataDiaria(Date dataDiaria) {
        this.dataDiaria = dataDiaria;
    }

    public Date getDataAnual() {
        if (this.dataAnual == null) {
            return new Date();
        }
        return dataAnual;
    }

    public void setDataAnual(Date dataAnual) {
        this.dataAnual = dataAnual;
    }

    public List<SelectItem> getTipoIndicadores() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", ""));
        for (TipoIndicador indicador : TipoIndicador.values()) {
            lista.add(new SelectItem(indicador, indicador.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", ""));
        for (PeriodicidadeIndicador pi : PeriodicidadeIndicador.values()) {
            lista.add(new SelectItem(pi, pi.getDescricao()));
        }
        return lista;
    }

    public void adicionar() {
        if (validaAdicionarValorIndicador()) {
            bloquearIndicador = true;
            Calendar c = Calendar.getInstance();
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.MENSAL)) {
                if (dataMensal != null) {
                    c.setTime(dataMensal);
                    valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), Util.criaDataPrimeiroDiaDoMesFP(c.get(Calendar.MONTH), c.get(Calendar.YEAR)), Util.recuperaDataUltimoDiaDoMes(c.getTime()), selecionado);
                } else {
                    FacesUtil.addOperacaoNaoPermitida("A Data Inicial não está preenchida corretamente com Mês e Ano. Exemplo: mm/aaaa");
                    return;
                }
            }
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.DIARIA)) {
                if (dataDiaria != null) {
                    valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), dataDiaria, dataDiaria, selecionado);
                } else {
                    FacesUtil.addOperacaoNaoPermitida("A Data Inicial não está preenchida corretamente com Dia, Mês e Ano. Exemplo: dd/mm/aaaa");
                    return;
                }
            }
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.ANUAL)) {
                if (dataAnual != null) {
                    c.setTime(dataAnual);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    Date primeiroDiaDoAno = c.getTime();
                    c.set(Calendar.DAY_OF_MONTH, 31);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    Date ultimoDiaDoAno = c.getTime();
                    valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), primeiroDiaDoAno, ultimoDiaDoAno, selecionado);
                } else {
                    FacesUtil.addOperacaoNaoPermitida("A Data Inicial não está preenchida corretamente com Ano. Exemplo: " + calendarioAtual().get(Calendar.YEAR));
                    return;
                }
            }
            selecionado.getListaDeValorIndicador().add(valorIndicadorEconomico);
        }
    }

    private boolean validaAdicionarValorIndicador() {
        if (selecionado.getTipoIndicador() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Tipo de Indicador deve ser informado.");
            return false;
        } else if (selecionado.getPeriodicidadeIndicador() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Periodicidade deve ser informado.");
            return false;
        }
        return true;
    }


    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/indicador-economico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public String excluirValorIndicador(ValorIndicadorEconomico valor) {
        selecionado.getListaDeValorIndicador().remove(valor);
        dataAnual = null;
        dataDiaria = null;
        dataMensal = null;
        return null;
    }

    public boolean validaCampos() {
        Boolean controle = true;
        controle = Util.validaCampos(selecionado);
        if (selecionado.getListaDeValorIndicador().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum valor foi adicionado para o Indicador Econômico.");
            controle = false;
        } else if (!validarPeriodicidade(selecionado.getListaDeValorIndicador().get(0))) {
            controle = false;
        }
        return controle;
    }

    public boolean validarPeriodicidade(ValorIndicadorEconomico novoRegistro) {
        if (selecionado.getListaDeValorIndicador().size() > 1) {
            int size = selecionado.getListaDeValorIndicador().size();
            ValorIndicadorEconomico ultimoRegistroLista = selecionado.getListaDeValorIndicador().get(size - 1);
            if (novoRegistro.getValor() < 0 && selecionado.getTipoIndicador().equals(TipoIndicador.MOEDA)) {
                FacesUtil.addOperacaoNaoPermitida("O valor não pode ser negativo quando o tipo de Indicador for Moeda.");
                return false;
            }
            Calendar dataInicialUltimoRegistro = Calendar.getInstance();
            Calendar dataFinalUltimoRegistro = Calendar.getInstance();

            Calendar dataInicialNovoRegistro = Calendar.getInstance();
            Calendar dataFinalNovoRegistro = Calendar.getInstance();

            dataInicialUltimoRegistro.setTime(ultimoRegistroLista.getInicioVigencia());
            dataFinalUltimoRegistro.setTime(ultimoRegistroLista.getFimVigencia());

            dataInicialNovoRegistro.setTime(novoRegistro.getInicioVigencia());
            dataFinalNovoRegistro.setTime(novoRegistro.getFimVigencia());


            if (dataInicialNovoRegistro.after(dataFinalNovoRegistro)) {
                FacesUtil.addOperacaoNaoPermitida("O novo registro não pode ter a data inicial maior que a data final.");
                return false;
            }

            if (dataFinalNovoRegistro.before(dataFinalUltimoRegistro)) {
                FacesUtil.addOperacaoNaoPermitida("O novo registro não pode ter a data final de Vigência menor que a data final da ultima Vigência");
                return false;
            }
            if (dataInicialNovoRegistro.before(dataFinalUltimoRegistro)) {
                FacesUtil.addOperacaoNaoPermitida(" O novo registro não pode ter data inicial de Vigência menor que a data final da ultima Vigência");
                return false;
            }
            if (dataInicialUltimoRegistro.after(dataFinalUltimoRegistro)) {
                FacesUtil.addOperacaoNaoPermitida("O novo registro esta com data Inicial maior que a data Final");
                return false;
            }


        } else if (selecionado.getListaDeValorIndicador().size() == 1) {
            Calendar dataInicialUltimoRegistro = Calendar.getInstance();
            Calendar dataFinalUltimoRegistro = Calendar.getInstance();
            dataInicialUltimoRegistro.setTime(selecionado.getListaDeValorIndicador().get(0).getInicioVigencia());
            dataFinalUltimoRegistro.setTime(selecionado.getListaDeValorIndicador().get(0).getFimVigencia());
            if (selecionado.getListaDeValorIndicador().get(0).getValor() < 0 && selecionado.getTipoIndicador().equals(TipoIndicador.MOEDA)) {
                FacesUtil.addOperacaoNaoPermitida("O valor não pode ser negativo quando o tipo de Indicador for Moeda!");
                return false;
            }
            if (dataInicialUltimoRegistro.after(dataFinalUltimoRegistro)) {
                FacesUtil.addOperacaoNaoPermitida("O novo registro não pode ter a data inicial maior que a data final.");
                return false;
            }
        }
        return true;
    }

    public String adicionarNovoRegistro(ValorIndicadorEconomico novoRegistro) {
        if (validarPeriodicidade(novoRegistro)) {
            Calendar c = Calendar.getInstance();
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.MENSAL)) {
                c.setTime(novoRegistro.getFimVigencia());
                valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), Util.criaDataPrimeiroDiaDoMesFP(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)), Util.recuperaDataUltimoDiaMesSeguinte(c.getTime()), selecionado);
            }
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.DIARIA)) {
                c.setTime(novoRegistro.getInicioVigencia());
                c.add(Calendar.DAY_OF_MONTH, 1);
                valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), c.getTime(), c.getTime(), selecionado);
            }
            if (selecionado.getPeriodicidadeIndicador().equals(PeriodicidadeIndicador.ANUAL)) {
                c.setTime(novoRegistro.getFimVigencia());
                c.add(Calendar.YEAR, 1);
                c.set(Calendar.MONTH, Calendar.JANUARY);
                c.set(Calendar.DAY_OF_MONTH, 1);
                Date primeiroDiaDoAno = c.getTime();
                c.set(Calendar.MONTH, Calendar.DECEMBER);
                c.set(Calendar.DAY_OF_MONTH, 31);
                Date ultimoDiaDoAno = c.getTime();
                valorIndicadorEconomico = new ValorIndicadorEconomico(new Double(0.0), primeiroDiaDoAno, ultimoDiaDoAno, selecionado);
            }
            selecionado.getListaDeValorIndicador().add(0, valorIndicadorEconomico);
        }
        return null;
    }

    public Boolean antesDoPrimeiroDaLista(ValorIndicadorEconomico vie) {
        ValorIndicadorEconomico get = selecionado.getListaDeValorIndicador().get(0);
        if (vie.equals(get)) {
            return false;
        }
        return true;
    }

    public Boolean mensal() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        return selecionado.getPeriodicidadeIndicador() == PeriodicidadeIndicador.MENSAL;
    }

    public Boolean diaria() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        return selecionado.getPeriodicidadeIndicador() == PeriodicidadeIndicador.DIARIA;
    }

    public Boolean anual() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        return selecionado.getPeriodicidadeIndicador() == PeriodicidadeIndicador.ANUAL;
    }

    public void limpaCamposData() {
        dataAnual = null;
        dataDiaria = null;
        dataMensal = null;
        selecionado.inicializaListaValorIndicador();
        selecionado.setPeriodicidadeIndicador(null);
        selecionado.setTipoIndicador(null);
        bloquearIndicador = false;
    }

    public Calendar calendarioAtual() {
        Calendar c = Calendar.getInstance();
        return c;
    }


    public ValorIndicadorEconomico getValorIndicadorEconomico() {
        return valorIndicadorEconomico;
    }

    public void setValorIndicadorEconomico(ValorIndicadorEconomico valorIndicadorEconomico) {
        this.valorIndicadorEconomico = valorIndicadorEconomico;
    }

    public Boolean getBloquearEdicao() {
        return bloquearIndicador;
    }

    public void setBloquearEdicao(Boolean bloquearEdicao) {
        this.bloquearIndicador = bloquearEdicao;
    }
}
