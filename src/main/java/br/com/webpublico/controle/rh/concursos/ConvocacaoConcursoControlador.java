package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.concursos.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "convocacaoConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-convocacao-concurso", pattern = "/concursos/convocacao/novo/", viewId = "/faces/rh/concursos/convocacao/edita.xhtml"),
    @URLMapping(id = "editar-convocacao-concurso", pattern = "/concursos/convocacao/editar/#{convocacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/convocacao/edita.xhtml"),
    @URLMapping(id = "ver-convocacao-concurso", pattern = "/concursos/convocacao/ver/#{convocacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/convocacao/edita.xhtml"),
    @URLMapping(id = "listar-convocacao-concurso", pattern = "/concursos/convocacao/listar/", viewId = "/faces/rh/concursos/convocacao/lista.xhtml")
})
public class ConvocacaoConcursoControlador extends PrettyControlador<ConvocacaoConcurso> implements Serializable, CRUD {

    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    @EJB
    private ConvocacaoConcursoFacade convocacaoConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    private CargoConcurso cargoSelecionado;
    private Concurso concursoSelecionado;
    private ClassificacaoConcurso classificacaoConcursoSelecionada;

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public ConvocacaoConcursoControlador() {
        super(ConvocacaoConcurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return convocacaoConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/convocacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-convocacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo(){
        super.novo();
    }

    @URLAction(mappingId = "ver-convocacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-convocacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarParametrosIniciais();
    }

    private void carregarParametrosIniciais() {
        selecionado = convocacaoConcursoFacade.recuperar(getId());
        classificacaoConcursoSelecionada = classificacaoConcursoFacade.buscarClassificacaoComClassificados(selecionado.getCargo().getClassificacaoConcurso().getId());
        concursoSelecionado = concursoFacade.recuperarConcursoComCriteriosDeDesempate(selecionado.getCargo().getConcurso().getId());
        cargoSelecionado = selecionado.getCargo();
        Collections.sort(classificacaoConcursoSelecionada.getInscricoes());
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(CargoConcurso.class, cargoConcursoFacade);
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.buscarConcursosComCargosAndInscricoesAndCriteriosDesempate(Long.parseLong(value));
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
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((Concurso) value).getId();
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
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso c : concursoFacade.buscarConcursosParaTelaDeConvocacao()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (concursoSelecionado == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        for (CargoConcurso c : concursoSelecionado.getCargos()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public CargoConcurso getCargoSelecionado() {
        return cargoSelecionado;
    }

    public void setCargoSelecionado(CargoConcurso cargoSelecionado) {
        this.cargoSelecionado = cargoSelecionado;
    }

    public void limparSelecionadoAndCargo() {
        cargoSelecionado = null;
        classificacaoConcursoSelecionada = null;
    }

    private boolean validarCamposObrigatorios() {
        boolean retorno = true;
        if (cargoSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo Concurso deve ser informado.");
            retorno = false;
        }

        if (cargoSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo Cargo deve ser informado.");
            retorno = false;
        }
        return retorno;
    }

    public String converterBooleanParaSimOuNao(Boolean valor) {
        return valor == null ? "Não" : valor ? "Sim" : "Não";
    }

    public String recuperarCorDoTexto(Boolean valor) {
        return valor == null ? "Não" : valor ? "verde" : "vermelho-escuro";
    }

    public void buscarClassificacaoJaExistente() {
        if (concursoSelecionado == null){
            FacesUtil.addCampoObrigatorio("O campo Concurso deve ser informado.");
            return;
        }

        if (cargoSelecionado == null){
            FacesUtil.addCampoObrigatorio("O campo Cargo deve ser informado.");
            return;
        }

        if (cargoSelecionado.getClassificacaoConcurso() == null){
            FacesUtil.addOperacaoNaoRealizada("Não existe uma classificação gerada para este cargo. É necessário gerar ou atualizar a classificação deste cargo para poder continuar");
            return;
        }

        classificacaoConcursoSelecionada = classificacaoConcursoFacade.buscarClassificacaoComClassificados(cargoSelecionado.getClassificacaoConcurso().getId());
        Collections.sort(classificacaoConcursoSelecionada.getInscricoes());
    }

    @Override
    public void salvar() {
        if (classificacaoConcursoSelecionada == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário recuperar uma classificação para poder salvar. Verifique as informações e tente novamente.");
            return;
        }

        selecionado.setCargo(cargoSelecionado);
        selecionado.setConcurso(concursoSelecionado);

        try {
            convocacaoConcursoFacade.salvar(selecionado, classificacaoConcursoSelecionada);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
        redireciona();
    }

    public ClassificacaoConcurso getClassificacaoConcursoSelecionada() {
        return classificacaoConcursoSelecionada;
    }

    public void setClassificacaoConcursoSelecionada(ClassificacaoConcurso classificacaoConcursoSelecionada) {
        this.classificacaoConcursoSelecionada = classificacaoConcursoSelecionada;
    }
}
