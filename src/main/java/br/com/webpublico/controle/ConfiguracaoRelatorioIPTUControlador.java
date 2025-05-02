package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoRelatorioIPTU;
import br.com.webpublico.entidades.EventoCalculo;
import br.com.webpublico.entidades.EventoRelatorioIPTU;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoRelatorioIPTUFacade;
import br.com.webpublico.negocios.EventoCalculoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "configuracaoRelatorioIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoConfiguracaoRelatorioIPTU",
                pattern = "/configuracao-relatorio-iptu/configurar/",
                viewId = "/faces/tributario/configuracao/configuracaorelatorioiptu/edita.xhtml")
})
public class ConfiguracaoRelatorioIPTUControlador extends PrettyControlador<ConfiguracaoRelatorioIPTU> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoRelatorioIPTUFacade configuracaoRelatorioIPTUFacade;
    @EJB
    private EventoCalculoFacade eventoCalculoFacade;
    private ConverterAutoComplete converterEvento;
    private EventoCalculo eventoCalculo;

    public ConfiguracaoRelatorioIPTUControlador() {
        super(ConfiguracaoRelatorioIPTU.class);
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            //redireciona();
        }
    }

    @URLAction(mappingId = "novoConfiguracaoRelatorioIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
//        super.novo();
        selecionado = configuracaoRelatorioIPTUFacade.recuperaUltimo();
        if (selecionado == null) {
            eventoCalculo = new EventoCalculo();
            selecionado = new ConfiguracaoRelatorioIPTU();
        }
        else {this.setId(selecionado.getId());
        }
    }

    @Override
    public void ver() {
        super.ver();
        eventoCalculo = new EventoCalculo();
    }

    @Override
    public void editar() {
        super.editar();
        eventoCalculo = new EventoCalculo();

    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-relatorio-iptu/";
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoRelatorioIPTUFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<EventoCalculo> completaEvento(String parte) {
        List<EventoCalculo> eventos = eventoCalculoFacade.listaEventosVigentes(parte.trim());
        return eventos;
    }

    public Converter getConverterEvento() {
        if (converterEvento == null) {
            converterEvento = new ConverterAutoComplete(EventoCalculo.class, eventoCalculoFacade);
        }
        return converterEvento;
    }

    public void addEvento() {
        EventoRelatorioIPTU eventoRelatorioIPTU = new EventoRelatorioIPTU();
        eventoRelatorioIPTU.setEventoCalculo(eventoCalculo);
        eventoRelatorioIPTU.setConfiguracaoRelatorioIPTU(selecionado);
        selecionado.getEventos().add(eventoRelatorioIPTU);
        eventoCalculo = new EventoCalculo();
    }

    public void removerEvento(EventoRelatorioIPTU evento) {
        selecionado.getEventos().remove(evento);
    }
    public void cancelar() {
//        Web.getEsperaRetorno();
//        redireciona();
    }
}
