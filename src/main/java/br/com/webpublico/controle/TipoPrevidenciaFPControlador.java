/*
 * Codigo gerado automaticamente em Thu Sep 15 16:18:48 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.TipoPrevidenciaFP;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoPrevidenciaFPFacade;
import br.com.webpublico.util.ConverterGenerico;
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
import java.util.List;

@ManagedBean(name = "tipoPrevidenciaFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoPrevidenciaFP", pattern = "/tipo-de-previdencia/novo/", viewId = "/faces/rh/administracaodepagamento/tipoprevidenciafp/edita.xhtml"),
    @URLMapping(id = "listaTipoPrevidenciaFP", pattern = "/tipo-de-previdencia/listar/", viewId = "/faces/rh/administracaodepagamento/tipoprevidenciafp/lista.xhtml"),
    @URLMapping(id = "verTipoPrevidenciaFP", pattern = "/tipo-de-previdencia/ver/#{tipoPrevidenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoprevidenciafp/visualizar.xhtml"),
    @URLMapping(id = "editarTipoPrevidenciaFP", pattern = "/tipo-de-previdencia/editar/#{tipoPrevidenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoprevidenciafp/edita.xhtml"),
})
public class TipoPrevidenciaFPControlador extends PrettyControlador<TipoPrevidenciaFP> implements Serializable, CRUD {

    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    private List<EventoFP> grupoEvento;
    private ConverterGenerico converterEvento;
    private EventoFP eventoFPSelecionado;
    private List<EventoFP> listaParaExcluidos;


    public TipoPrevidenciaFPControlador() {
        super(TipoPrevidenciaFP.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-previdencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoPrevidenciaFPFacade;
    }

    @URLAction(mappingId = "novoTipoPrevidenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        getSelecionado().setCodigo(tipoPrevidenciaFPFacade.retornaUltimoCodigoLong());
        eventoFPSelecionado = new EventoFP();
        listaParaExcluidos = new ArrayList<>();
    }

    @URLAction(mappingId = "verTipoPrevidenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoPrevidenciaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        listaParaExcluidos = new ArrayList<>();
    }

    @Override
    public void excluir() {
        if (selecionado.getEventosFP() != null) {
            tipoPrevidenciaFPFacade.atualizaEventos(selecionado);
        }
        super.excluir();
    }

    @Override
    public void salvar() {
        try {
            if (operacao == Operacoes.NOVO) {
                Long novoCodigo = tipoPrevidenciaFPFacade.retornaUltimoCodigoLong();
                if (!novoCodigo.equals(selecionado.getCodigo())) {
                    FacesUtil.addInfo("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                    selecionado.setCodigo(novoCodigo);
                }
            }
            tipoPrevidenciaFPFacade.salvar(selecionado, listaParaExcluidos);
            redireciona();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ConverterGenerico getConverterEvento() {
        if (converterEvento == null) {
            converterEvento = new ConverterGenerico(EventoFP.class, tipoPrevidenciaFPFacade.getEventoFPFacade());
        }
        return converterEvento;
    }

    public List<SelectItem> getEventoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP object : tipoPrevidenciaFPFacade.getEventoFPFacade().listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRegimePrevidenciario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegimePrevidenciario object : TipoRegimePrevidenciario.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public EventoFP getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(EventoFP eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }


    public void adicionarEvento() {
        if (podeAdcionarEvento()) {
            eventoFPSelecionado.setTipoPrevidenciaFP(selecionado);
            selecionado.setEventosFP(Util.adicionarObjetoEmLista(selecionado.getEventosFP(), eventoFPSelecionado));
            eventoFPSelecionado = new EventoFP();
        }
    }

    public Boolean verificaEventoAdicionado() {
        if (selecionado.getEventosFP() != null && !selecionado.getEventosFP().isEmpty()) {
            if (selecionado.getEventosFP().contains(eventoFPSelecionado)) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um evento semelhante ao qual está sendo cadastrado !");
                return false;
            }
        }
        return true;
    }

    public void removeEvento(EventoFP eventoFP) {
        eventoFP.setTipoPrevidenciaFP(null);
        listaParaExcluidos.add(eventoFP);
        selecionado.getEventosFP().remove(eventoFP);
    }

    private boolean podeAdcionarEvento() {
        if (eventoFPSelecionado == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione um Evento de Folha de Pagamento!");
            return false;
        }
        if (!verificaEventoAdicionado()) {
            return false;
        }
        if (tipoPrevidenciaFPFacade.getEventoFPFacade().verificaEventoCadastrados(eventoFPSelecionado.getCodigo())) {
            eventoFPSelecionado.getCodigo().equals(selecionado.getEventosFP());
            FacesUtil.addOperacaoNaoPermitida(" O Evento de Folha de Pagamento selecionado já está sendo usado em um Tipo de Previdência !");
            return false;
        }
        return true;
    }

}
