package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseClassesConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseFaixaDeValoresFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseGruposConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabitesePadroesConstrucaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "habiteseFaixaDeValoresControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHabiteseFaixaDeValores", pattern = "/regularizacao-construcao/habitese-faixa-de-valores/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesefaixadevalores/edita.xhtml"),
    @URLMapping(id = "editarHabiteseFaixaDeValores", pattern = "/regularizacao-construcao/habitese-faixa-de-valores/editar/#{habiteseFaixaDeValoresControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesefaixadevalores/edita.xhtml"),
    @URLMapping(id = "listarHabiteseFaixaDeValores", pattern = "/regularizacao-construcao/habitese-faixa-de-valores/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesefaixadevalores/lista.xhtml"),
    @URLMapping(id = "verHabiteseFaixaDeValores", pattern = "/regularizacao-construcao/habitese-faixa-de-valores/ver/#{habiteseFaixaDeValoresControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesefaixadevalores/visualizar.xhtml")
})

public class HabiteseFaixaDeValoresControlador extends PrettyControlador<HabiteseFaixaDeValores> implements Serializable, CRUD {

    @EJB
    private HabiteseFaixaDeValoresFacade habiteseFaixaDeValoresFacade;
    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;
    private ConverterAutoComplete converterHabiteseClasses;
    @EJB
    private HabiteseGruposConstrucaoFacade habiteseGruposConstrucaoFacade;
    private ConverterAutoComplete converterHabiteseGrupos;
    @EJB
    private HabitesePadroesConstrucaoFacade habitesePadroesConstrucaoFacade;
    private ConverterAutoComplete converterHabitesePadroes;
    private FaixaDeValoresHL faixaDeValoresHL;

    public HabiteseFaixaDeValoresControlador() {
        super(HabiteseFaixaDeValores.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return habiteseFaixaDeValoresFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/habitese-faixa-de-valores/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoHabiteseFaixaDeValores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        faixaDeValoresHL = new FaixaDeValoresHL();
        selecionado.setListaFaixaDeValoresHL(new ArrayList<FaixaDeValoresHL>());
    }

    @URLAction(mappingId = "verHabiteseFaixaDeValores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarHabiteseFaixaDeValores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        faixaDeValoresHL = new FaixaDeValoresHL();
    }

    public FaixaDeValoresHL getFaixaDeValoresHL() {
        return faixaDeValoresHL;
    }

    public void setFaixaDeValoresHL(FaixaDeValoresHL faixaDeValoresHL) {
        this.faixaDeValoresHL = faixaDeValoresHL;
    }

    public Converter getConverterHabiteseClasses() {
        if (converterHabiteseClasses == null) {
            converterHabiteseClasses = new ConverterAutoComplete(HabiteseClassesConstrucao.class, habiteseClassesConstrucaoFacade);
        }
        return converterHabiteseClasses;
    }

    public Converter getConverterHabiteseGrupos() {
        if (converterHabiteseGrupos == null) {
            converterHabiteseGrupos = new ConverterAutoComplete(HabiteseGruposConstrucao.class, habiteseGruposConstrucaoFacade);
        }
        return converterHabiteseGrupos;
    }

    public Converter getConverterHabitesePadroes() {
        if (converterHabitesePadroes == null) {
            converterHabitesePadroes = new ConverterAutoComplete(HabitesePadroesConstrucao.class, habitesePadroesConstrucaoFacade);
        }
        return converterHabitesePadroes;
    }

    private void validarExisteExercicioSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione primeiro um Exercício.");
        }
        ve.lancarException();
    }

    private void validarExisteGrupoSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHabiteseGruposConstrucao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione primeiro um Grupo.");
        }
        ve.lancarException();
    }

    public List<HabiteseClassesConstrucao> completarHabiteseClasses(String parte) {
        try {
            validarExisteExercicioSelecionado();
            return habiteseClassesConstrucaoFacade.listarFiltrando(parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public List<HabiteseGruposConstrucao> completarHabiteseGrupos(String parte) {
        try {
            validarExisteExercicioSelecionado();
            return habiteseGruposConstrucaoFacade.listarFiltrando(parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public List<HabitesePadroesConstrucao> completarHabitesePadroes(String parte) {
        try {
            validarExisteExercicioSelecionado();
            validarExisteGrupoSelecionado();
            return habitesePadroesConstrucaoFacade.listarFiltrandoPorGrupo(selecionado.getHabiteseGruposConstrucao(), parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public void adicionarFaixaDeValoresHL() {
        try {
            validarAdicionarFaixaDeValoresHL();
            faixaDeValoresHL.setHabiteseFaixaDeValores(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getListaFaixaDeValoresHL(), faixaDeValoresHL);
            faixaDeValoresHL = new FaixaDeValoresHL();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarFaixaDeValoresHL() {
        ValidacaoException ve = new ValidacaoException();
        if (faixaDeValoresHL.getHabitesePadroesConstrucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Padrão deve ser preenchido.");
        }
        if (faixaDeValoresHL.getAreaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Área Inicial deve ser preenchido.");
        }
        if (faixaDeValoresHL.getValorUFM() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor em UFM deve ser preenchido.");
        }
        ve.lancarException();
    }

    public void limparFaixaDeValoresHL() {
        faixaDeValoresHL = new FaixaDeValoresHL();
    }

    public void editarFaixaDeValoresHL(FaixaDeValoresHL fdvhl) {
        faixaDeValoresHL = (FaixaDeValoresHL) Util.clonarObjeto(fdvhl);
    }

    public void removerFaixaDeValoresHL(FaixaDeValoresHL fdvhl) {
        selecionado.getListaFaixaDeValoresHL().remove(fdvhl);
    }

}
