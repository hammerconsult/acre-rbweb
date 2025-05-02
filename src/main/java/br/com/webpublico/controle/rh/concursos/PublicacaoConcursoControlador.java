package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.PublicacaoConcurso;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.PublicacaoConcursoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "publicacaoConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-publicacao-concurso", pattern = "/concursos/publicacao/novo/", viewId = "/faces/rh/concursos/publicacao/edita.xhtml"),
        @URLMapping(id = "editar-publicacao-concurso", pattern = "/concursos/publicacao/editar/#{publicacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/publicacao/edita.xhtml"),
        @URLMapping(id = "ver-publicacao-concurso", pattern = "/concursos/publicacao/ver/#{publicacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/publicacao/edita.xhtml"),
        @URLMapping(id = "listar-publicacao-concurso", pattern = "/concursos/publicacao/listar/", viewId = "/faces/rh/concursos/publicacao/lista.xhtml")
})
public class PublicacaoConcursoControlador extends PrettyControlador<Concurso> implements Serializable, CRUD {

    @EJB
    private PublicacaoConcursoFacade publicacaoConcursoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    private PublicacaoConcurso publicacaoSelecionada;

    public PublicacaoConcursoControlador() {
        super(Concurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return publicacaoConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/publicacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-publicacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = null;
    }

    @URLAction(mappingId = "ver-publicacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        setOperacao(Operacoes.VER);
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-publicacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        setOperacao(Operacoes.EDITAR);
        carregarParametrosIniciais();
    }

    private void carregarParametrosIniciais(){
        PublicacaoConcurso publicacaoConcurso = publicacaoConcursoFacade.recuperar(getId());
        selecionado = concursoFacade.recuperarConcursoComPublicacoes(publicacaoConcurso.getConcurso().getId());
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.recuperarConcursoComPublicacoes(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado  = String.valueOf(value);
                    } else {
                        try {
                            return ""+((Concurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Concurso c : concursoFacade.getUltimosConcursosComBancaExaminadora()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public PublicacaoConcurso getPublicacaoSelecionada() {
        return publicacaoSelecionada;
    }

    public void setPublicacaoSelecionada(PublicacaoConcurso publicacaoSelecionada) {
        this.publicacaoSelecionada = publicacaoSelecionada;
    }

    public void novoPublicacao() {
        publicacaoSelecionada = new PublicacaoConcurso();
        publicacaoSelecionada.setConcurso(selecionado);
        publicacaoSelecionada.setCadastradaEm(UtilRH.getDataOperacao());
    }

    public void confirmarPublicacao() {
        if (!Util.validaCampos(publicacaoSelecionada)) {
            return;
        }

        selecionado.setPublicacoes(Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), publicacaoSelecionada));
        cancelarPublicacao();
    }

    public void cancelarPublicacao() {
        publicacaoSelecionada = null;
    }

    public void removerPublicacao(PublicacaoConcurso pc) {
        selecionado.getPublicacoes().remove(pc);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    @Override
    public void salvar() {
        try {
            publicacaoConcursoFacade.salvarConcurso(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }
        redireciona();
    }
}
