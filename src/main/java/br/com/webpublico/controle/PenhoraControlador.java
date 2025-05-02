/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.PenhoraFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author claudio
 */


@ManagedBean(name = "penhoraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listaPenhora", pattern = "/tributario/cadastro-imobiliario/penhora/listar/", viewId = "/faces/tributario/cadastromunicipal/penhora/lista.xhtml"),
        @URLMapping(id = "novoPenhora", pattern = "/tributario/cadastro-imobiliario/penhora/novo/", viewId = "/faces/tributario/cadastromunicipal/penhora/edita.xhtml"),
        @URLMapping(id = "baixaPenhora", pattern = "/tributario/cadastro-imobiliario/penhora/baixa/#{penhoraControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/penhora/baixar.xhtml")
})
public class PenhoraControlador extends PrettyControlador<Penhora> implements Serializable, CRUD {

    @EJB
    private PenhoraFacade penhoraFacade;
    @Limpavel(Limpavel.NULO)
    private List<Penhora> listaPenhoras;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterAutor;
    @Limpavel(Limpavel.NULO)
    private CadastroImobiliario filtroCadastroImobiliario;
    @Limpavel(Limpavel.ZERO)
    private int filtroTipoPenhora;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroNumeroProcesso;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroNumeroProtocolo;
    @Limpavel(Limpavel.NULO)
    private Pessoa filtroAutor;
    private List<FileUploadEvent> fileUploadEvents;

    public CadastroImobiliario getFiltroCadastroImobiliario() {
        return filtroCadastroImobiliario;
    }

    public void setFiltroCadastroImobiliario(CadastroImobiliario filtroCadastroImobiliario) {
        this.filtroCadastroImobiliario = filtroCadastroImobiliario;
    }

    public String getFiltroNumeroProcesso() {
        return filtroNumeroProcesso;
    }

    public void setFiltroNumeroProcesso(String filtroNumeroProcesso) {
        this.filtroNumeroProcesso = filtroNumeroProcesso;
    }

    public String getFiltroNumeroProtocolo() {
        return filtroNumeroProtocolo;
    }

    public void setFiltroNumeroProtocolo(String filtroNumeroProtocolo) {
        this.filtroNumeroProtocolo = filtroNumeroProtocolo;
    }

    public Pessoa getFiltroAutor() {
        return filtroAutor;
    }

    public void setFiltroAutor(Pessoa filtroAutor) {
        this.filtroAutor = filtroAutor;
    }

    public int getFiltroTipoPenhora() {
        return filtroTipoPenhora;
    }

    public void setFiltroTipoPenhora(int filtroTipoPenhora) {
        this.filtroTipoPenhora = filtroTipoPenhora;
    }

    public PenhoraControlador() {
        super(Penhora.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return penhoraFacade;
    }

    public Converter getConverterAutor() {
        if (converterAutor == null) {
            converterAutor = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterAutor;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<Penhora> getListaPenhoras() {
        if (listaPenhoras == null) {
            listaPenhoras = new ArrayList<Penhora>();
        }
        return listaPenhoras;
    }

    public void setListaPenhoras(List<Penhora> listaPenhoras) {
        this.listaPenhoras = listaPenhoras;
    }


    public PenhoraFacade getPenhoraFacade() {
        if (penhoraFacade == null) {
            penhoraFacade = new PenhoraFacade();
        }
        return penhoraFacade;
    }

    public void setPenhoraFacade(PenhoraFacade penhoraFacade) {
        this.penhoraFacade = penhoraFacade;
    }

    @Override
    public void salvar() {
        try {
            if (!validaCampos()) {
                return;
            }
            penhoraFacade.salvar(selecionado, fileUploadEvents);
            FacesUtil.addInfo("Salvo com sucesso!", "");
            selecionado = null;
            listaPenhoras = null;
            redireciona();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastro-imobiliario/penhora/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoPenhora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        fileUploadEvents = new ArrayList<>();
    }

    public boolean imovelComPenhora() {
        return cadastroImobiliarioFacade.imovelComPenhora(selecionado.getCadastroImobiliario());

    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (imovelComPenhora()) {
            FacesUtil.addError("Não foi possivel continuar.", " Esse imóvel já se encontra penhorado!");
            retorno = false;
        }

        if (selecionado.getCadastroImobiliario() == null || selecionado.getCadastroImobiliario().getId() == null) {
            FacesUtil.addError("Campo Obrigatório.", "Informe o cadastro imobiliário.");
            retorno = false;
        }
        if ("".equals(selecionado.getNumeroProcesso()) || selecionado.getNumeroProcesso().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório.", "Informe o número do processo.");
            retorno = false;
        }
        if (selecionado.getAutor() == null || selecionado.getAutor().getId() == null) {
            FacesUtil.addError("Campo Obrigatório.", "Informe o autor.");
            retorno = false;
        }
        if (selecionado.getDataPenhora() == null) {
            FacesUtil.addError("Campo Obrigatório.", "Informe a data da penhora.");
            retorno = false;
        }
        if ("".equals(selecionado.getNumeroProtocolo()) || selecionado.getNumeroProtocolo().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório.", "Informe o número do protocolo.");
            retorno = false;
        }
        if (selecionado.getDataProtocolo() == null) {
            FacesUtil.addError("Campo Obrigatório.", "Informe a data do protocolo.");
            retorno = false;
        }
        if ("".equals(selecionado.getMotivo()) || selecionado.getMotivo().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório.", "Informe o motivo.");
            retorno = false;
        }
        if (selecionado.getBloqueio() == null) {
            FacesUtil.addError("Campo Obrigatório.", "Selecione o status.");
            retorno = false;
        }

        return retorno;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<Pessoa> completaAutor(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    @URLAction(mappingId = "baixaPenhora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = penhoraFacade.recuperar(selecionado.getId());
    }

    public String baixarPenhora() {
        boolean retorno = true;
        if (selecionado.getDataBaixa() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "Informe a data da baixa."));
            retorno = false;
        }
        if (selecionado.getNumeroProcessoBaixa().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "Informe o número do processo da baixa."));
            retorno = false;
        }
        if (selecionado.getDataBaixa() != null && selecionado.getDataPenhora().compareTo(selecionado.getDataBaixa()) > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "A data da baixa não pode ser menor que a data da penhora."));
            retorno = false;
        }
        if (retorno) {
            penhoraFacade.salvar(selecionado);
            FacesUtil.addInfo("Baixa realizada com sucesso!", "");
            return "lista";
        } else {
            return "";
        }

    }

    public List<SelectItem> getTipoDePenhora() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(0, " "));
        toReturn.add(new SelectItem(1, "Baixadas e Não Baixadas"));
        toReturn.add(new SelectItem(2, "Baixadas"));
        toReturn.add(new SelectItem(3, "Não Baixadas"));
        return toReturn;
    }

    @URLAction(mappingId = "listaPenhora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
    }

    public void filtrar() {
        listaPenhoras.clear();
        listaPenhoras = penhoraFacade.listaPorFiltro(filtroCadastroImobiliario, filtroTipoPenhora, filtroNumeroProcesso, filtroNumeroProtocolo, filtroAutor);
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public void setFileUploadEvents(List<FileUploadEvent> fileUploadEvents) {
        this.fileUploadEvents = fileUploadEvents;
    }

    public void uploadArquivos(FileUploadEvent event) {
        fileUploadEvents.add(event);
    }

    public void removerArquivo(ActionEvent evento) {
        FileUploadEvent e = (FileUploadEvent) evento.getComponent().getAttributes().get("arquivos");
        fileUploadEvents.remove(e);
    }

    public void removeFileUpload(ActionEvent event) {
        ArquivoPenhora arq = (ArquivoPenhora) event.getComponent().getAttributes().get("remove");
        selecionado.getArquivos().remove(arq);
    }
    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }
}
