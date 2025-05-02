/*
 * Codigo gerado automaticamente em Wed Nov 23 20:17:02 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.ItinerarioViagem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.negocios.ItinerarioViagemFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.enums.Operacoes;
import org.apache.commons.beanutils.BeanUtils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ItinerarioViagemControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ItinerarioViagemFacade itinerarioViagemFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    private ConverterAutoComplete converterCidade;
    private List<ItinerarioViagem> listaItinerarioViagem;
    private Boolean estaAlterando;

    public ItinerarioViagemControlador() {
        metadata = new EntidadeMetaData(ItinerarioViagem.class);
    }

    public ItinerarioViagemFacade getFacade() {
        return itinerarioViagemFacade;
    }

    public Boolean getEstaAlterando() {
        return estaAlterando;
    }

    public void setEstaAlterando(Boolean estaAlterando) {
        this.estaAlterando = estaAlterando;
    }

    @Override
    public AbstractFacade getFacede() {
        return itinerarioViagemFacade;
    }

    public ConverterAutoComplete getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterAutoComplete(Cidade.class, cidadeFacade);
        }
        return converterCidade;
    }

    public List<Cidade> completaCidade(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public List<ItinerarioViagem> getListaItinerarioViagem() {
        return listaItinerarioViagem;
    }

    public void setListaItinerarioViagem(List<ItinerarioViagem> listaItinerarioViagem) {
        this.listaItinerarioViagem = listaItinerarioViagem;
    }

    @Override
    public void novo() {
        novaListaItinerario();
        geraNovoComOrdem();
        estaAlterando = Boolean.FALSE;
    }

    public void adicionarItemListaItinerario() {
        if (this.listaItinerarioViagem != null) {
            if (super.validaCampos()) {
                this.listaItinerarioViagem.add((ItinerarioViagem) selecionado);
                geraNovoComOrdem();
            }
        } else {
            novaListaItinerario();
            adicionarItemListaItinerario();
        }
    }

    private void novaListaItinerario() {
        this.listaItinerarioViagem = new ArrayList<ItinerarioViagem>();
    }

    public void alterarItemLista(ActionEvent evento) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        ItinerarioViagem itinerarioViagem = (ItinerarioViagem) evento.getComponent().getAttributes().get("itinerario");
        selecionado = BeanUtils.cloneBean(itinerarioViagem);
        estaAlterando = Boolean.TRUE;
    }

    public void removerItemLista(ActionEvent evento) {
        ItinerarioViagem itinerarioViagem = (ItinerarioViagem) evento.getComponent().getAttributes().get("itinerario");
        this.listaItinerarioViagem.remove(itinerarioViagem);
        reordenaLista();
    }

    private int recuperaNumeroDaOrdem() {
        return listaItinerarioViagem != null ? listaItinerarioViagem.size() + 1 : 1;
    }

    public void geraNovoComOrdem() {
        super.novo();
        atribuiNovaOrdem();
    }

    private void reordenaLista() {
        int ordem = 1;
        for (ItinerarioViagem itinerarioDaVez : listaItinerarioViagem) {
            itinerarioDaVez.setOrdem(ordem);
            ordem++;
        }
        atribuiNovaOrdem();
    }

    private void atribuiNovaOrdem() {
        ((ItinerarioViagem) selecionado).setOrdem(recuperaNumeroDaOrdem());
    }

    public void confirmarAlteracao() {
        if (validaCampos()) {
            int index = -1;
            for (ItinerarioViagem itinerarioDaVez : listaItinerarioViagem) {
                if (itinerarioDaVez.getOrdem() == ((ItinerarioViagem) selecionado).getOrdem()) {
                    index = listaItinerarioViagem.indexOf(itinerarioDaVez);
                }
            }
            if (index >= 0) {
                listaItinerarioViagem.set(index, (ItinerarioViagem) selecionado);
            }
            geraNovoComOrdem();
            estaAlterando = Boolean.FALSE;
        }
    }

    public void cancelarAlteracao() {
        geraNovoComOrdem();
        estaAlterando = Boolean.FALSE;
    }

    @Override
    public String salvar() {
        if (!listaItinerarioViagem.isEmpty()) {
            try {
                for (ItinerarioViagem itinerarioDaVez : listaItinerarioViagem) {
                    if (operacao == Operacoes.NOVO) {
                        this.getFacede().salvarNovo(itinerarioDaVez);
                    } else {
                        this.getFacede().salvar(itinerarioDaVez);
                    }
                }
                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                return super.getCaminho();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
                return "edita.xhtml";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não existem cidades adicionadas ao itinerário!", ""));
            return "edita.xhtml";
        }
    }
}
