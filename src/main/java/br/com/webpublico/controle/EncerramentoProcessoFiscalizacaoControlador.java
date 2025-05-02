/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProcessoFiscalizacao;
import br.com.webpublico.entidades.SituacaoProcessoFiscal;
import br.com.webpublico.enums.StatusProcessoFiscalizacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EncerramentoProcessoFiscalizacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */


@ManagedBean(name = "encerramentoProcessoFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEncerramentoProcessoFiscalizacao", pattern = "/tributario/fiscalizacaosecretaria/encerramento/", viewId = "/faces/tributario/fiscalizacaosecretaria/autoinfracao/lista.xhtml"),
        @URLMapping(id = "verEncerramentoProcessoFiscalizacao", pattern = "/tributario/fiscalizacaosecretaria/autoinfracao/ver/#{encerramentoProcessoFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/autoinfracao/visualizar.xhtml"),
})
public class EncerramentoProcessoFiscalizacaoControlador extends PrettyControlador<ProcessoFiscalizacao> implements Serializable, CRUD {

    @EJB
    private EncerramentoProcessoFiscalizacaoFacade encerramentoAutoInfracaoFiscalizacaoFacade;
    private SistemaControlador sistemaControlador;
    private List<ProcessoFiscalizacao> listaProcessos;
    private ProcessoFiscalizacao filtroProcessoInicial;
    private ProcessoFiscalizacao filtroProcessoFinal;
    private Date filtroDataProcessoInicial;
    private Date filtroDataProcessoFinal;
    private Date filtroDataPagamentoInicial;
    private Date filtroDataPagamentoFinal;
    private StatusProcessoFiscalizacao filtroStatusProcessoFiscalizacao;
    private ConverterAutoComplete converterProcessoFiscalizacao;
    @ManagedProperty(name = "processoFiscalizacaoControlador", value = "#{processoFiscalizacaoControlador}")
    private ProcessoFiscalizacaoControlador processoFiscalizacaoControlador;
    private ProcessoFiscalizacao[] processosSelecionados;


    public EncerramentoProcessoFiscalizacaoControlador() {
        super(ProcessoFiscalizacao.class);
    }

    public StatusProcessoFiscalizacao getFiltroStatusProcessoFiscalizacao() {
        return filtroStatusProcessoFiscalizacao;
    }

    public void setFiltroStatusProcessoFiscalizacao(StatusProcessoFiscalizacao filtroStatusProcessoFiscalizacao) {
        this.filtroStatusProcessoFiscalizacao = filtroStatusProcessoFiscalizacao;
    }

    public ProcessoFiscalizacao[] getProcessosSelecionados() {
        return processosSelecionados;
    }

    public void setProcessosSelecionados(ProcessoFiscalizacao[] processosSelecionados) {
        this.processosSelecionados = processosSelecionados;
    }

    public ProcessoFiscalizacaoControlador getProcessoFiscalizacaoControlador() {
        return processoFiscalizacaoControlador;
    }

    public void setProcessoFiscalizacaoControlador(ProcessoFiscalizacaoControlador processoFiscalizacaoControlador) {
        this.processoFiscalizacaoControlador = processoFiscalizacaoControlador;
    }

    public List<ProcessoFiscalizacao> completaProcessoFiscalizacao(String parte) {
        return this.encerramentoAutoInfracaoFiscalizacaoFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public Converter getConverterProcessoFiscalizacao() {
        if (this.converterProcessoFiscalizacao == null) {
            this.converterProcessoFiscalizacao = new ConverterAutoComplete(ProcessoFiscalizacao.class, encerramentoAutoInfracaoFiscalizacaoFacade);
        }
        return this.converterProcessoFiscalizacao;
    }

    public Date getFiltroDataPagamentoFinal() {
        return filtroDataPagamentoFinal;
    }

    public void setFiltroDataPagamentoFinal(Date filtroDataPagamentoFinal) {
        this.filtroDataPagamentoFinal = filtroDataPagamentoFinal;
    }

    public Date getFiltroDataPagamentoInicial() {
        return filtroDataPagamentoInicial;
    }

    public void setFiltroDataPagamentoInicial(Date filtroDataPagamentoInicial) {
        this.filtroDataPagamentoInicial = filtroDataPagamentoInicial;
    }

    public Date getFiltroDataProcessoFinal() {
        return filtroDataProcessoFinal;
    }

    public void setFiltroDataProcessoFinal(Date filtroDataProcessoFinal) {
        this.filtroDataProcessoFinal = filtroDataProcessoFinal;
    }

    public Date getFiltroDataProcessoInicial() {
        return filtroDataProcessoInicial;
    }

    public void setFiltroDataProcessoInicial(Date filtroDataProcessoInicial) {
        this.filtroDataProcessoInicial = filtroDataProcessoInicial;
    }

    public ProcessoFiscalizacao getFiltroProcessoFinal() {
        return filtroProcessoFinal;
    }

    public void setFiltroProcessoFinal(ProcessoFiscalizacao filtroProcessoFinal) {
        this.filtroProcessoFinal = filtroProcessoFinal;
    }

    public ProcessoFiscalizacao getFiltroProcessoInicial() {
        return filtroProcessoInicial;
    }

    public void setFiltroProcessoInicial(ProcessoFiscalizacao filtroProcessoInicial) {
        this.filtroProcessoInicial = filtroProcessoInicial;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ProcessoFiscalizacao getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ProcessoFiscalizacao selecionado) {
        this.selecionado = selecionado;
    }

    public List<ProcessoFiscalizacao> getLista() {
        return this.encerramentoAutoInfracaoFiscalizacaoFacade.lista();
    }

    public List<ProcessoFiscalizacao> getListaProcessos() {
        if (listaProcessos == null) {
            listaProcessos = new ArrayList<ProcessoFiscalizacao>();
        }
        return listaProcessos;
    }

    @URLAction(mappingId = "verEncerramentoProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        selecionado = encerramentoAutoInfracaoFiscalizacaoFacade.recuperar(this.getId());
    }

    @Override
    public AbstractFacade getFacede() {
        return encerramentoAutoInfracaoFiscalizacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/fiscalizacaosecretaria/encerramento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoEncerramentoProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        } else {
            selecionado = new ProcessoFiscalizacao();
            listaProcessos = new ArrayList<ProcessoFiscalizacao>();
            filtroProcessoInicial = new ProcessoFiscalizacao();
            filtroProcessoFinal = new ProcessoFiscalizacao();
            filtroDataProcessoInicial = null;
            filtroDataProcessoFinal = null;
            filtroDataPagamentoInicial = null;
            filtroDataPagamentoFinal = null;
            processosSelecionados = null;
        }
    }

    public void salvar() {
        try {
            if (selecionado.getId() == null) {
                this.encerramentoAutoInfracaoFiscalizacaoFacade.salvarNovo(selecionado);
            } else {
                this.encerramentoAutoInfracaoFiscalizacaoFacade.salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
            redireciona();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema: ", e.getMessage().toString()));
            redireciona();
        }
    }

    public void excluirSelecionado() {
        this.encerramentoAutoInfracaoFiscalizacaoFacade.remover(selecionado);
    }

    public void filtrar() {
        validaFiltro();
        listaProcessos = encerramentoAutoInfracaoFiscalizacaoFacade.filtrar(filtroProcessoInicial, filtroProcessoFinal, filtroDataProcessoInicial, filtroDataProcessoFinal, filtroDataPagamentoInicial, filtroDataPagamentoFinal, filtroStatusProcessoFiscalizacao);
    }

    private void validaFiltro() {
        if (filtroProcessoFinal != null && filtroProcessoFinal.getId() != null && filtroProcessoInicial != null && filtroProcessoInicial.getId() != null) {
            if (filtroProcessoInicial.getId().compareTo(filtroProcessoFinal.getId()) > 0) {
                inverteProcesso();
            }
        }
        if (filtroDataProcessoInicial != null && filtroDataProcessoFinal != null) {
            if (filtroDataProcessoInicial.compareTo(filtroDataProcessoFinal) > 0) {
                inverteDataProcesso();
            }
        }
        if (filtroDataPagamentoInicial != null && filtroDataPagamentoFinal != null) {
            if (filtroDataPagamentoInicial.compareTo(filtroDataPagamentoFinal) > 0) {
                inverteDataPagamento();
            }
        }
    }

    private void inverteProcesso() {
        ProcessoFiscalizacao aux = filtroProcessoInicial;
        filtroProcessoInicial = filtroProcessoFinal;
        filtroProcessoFinal = aux;
    }

    private void inverteDataProcesso() {
        Date aux = filtroDataProcessoInicial;
        filtroDataProcessoInicial = filtroDataProcessoFinal;
        filtroDataProcessoFinal = aux;
    }

    private void inverteDataPagamento() {
        Date aux = filtroDataPagamentoInicial;
        filtroDataPagamentoInicial = filtroDataPagamentoFinal;
        filtroDataPagamentoFinal = aux;
    }

    public String encerrarProcessosSelecionados() {
        if (processosSelecionados.length > 0) {
            for (ProcessoFiscalizacao pf : processosSelecionados) {
                selecionado = processoFiscalizacaoControlador.getProcessoFiscalizacaoFacade().recuperar(pf.getId());
                encerrarProcesso();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso. ", "Processo(s) Encerrado(s)."));
            novo();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção. ", "Selecione os processos que deseja encerrar."));
        }
        return "lista.xhtml";
    }

    public String encerrarProcessoUnico() {
        encerrarProcesso();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso. ", "Processo Encerrado."));
        novo();
        return "lista.xhtml";
    }

    public void encerrarProcesso() {
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setProcessoFiscalizacao(selecionado);
        spf.setData(new Date());
        spf.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.ENCERRADO);
        selecionado.getSituacoesProcessoFiscalizacao().add(spf);
        encerramentoAutoInfracaoFiscalizacaoFacade.salvar(selecionado);
        listaProcessos = new ArrayList<ProcessoFiscalizacao>();
    }

    public List<SelectItem> situacaoProcesso() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (StatusProcessoFiscalizacao status : StatusProcessoFiscalizacao.values()) {
            lista.add(new SelectItem(status, status.getDescricao()));
        }
        return lista;
    }

    public void verProcesso(ProcessoFiscalizacao processo) {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/processo-de-fiscalizacao/encerramento/" + processo.getId() + "/");
    }
}
