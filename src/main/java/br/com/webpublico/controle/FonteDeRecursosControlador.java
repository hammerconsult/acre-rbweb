/*
 * Codigo gerado automaticamente em Wed May 18 13:42:33 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoFonteRecursos;
import br.com.webpublico.enums.SituacaoFonteRecurso;
import br.com.webpublico.enums.TipoFonteRecurso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "fonteDeRecursosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-fonte-de-recursos", pattern = "/fonte-de-recursos/novo/", viewId = "/faces/financeiro/planodecontas/fontederecursos/edita.xhtml"),
    @URLMapping(id = "editar-fonte-de-recursos", pattern = "/fonte-de-recursos/editar/#{fonteDeRecursosControlador.id}/", viewId = "/faces/financeiro/planodecontas/fontederecursos/edita.xhtml"),
    @URLMapping(id = "ver-fonte-de-recursos", pattern = "/fonte-de-recursos/ver/#{fonteDeRecursosControlador.id}/", viewId = "/faces/financeiro/planodecontas/fontederecursos/visualizar.xhtml"),
    @URLMapping(id = "listar-fonte-de-recursos", pattern = "/fonte-de-recursos/listar/", viewId = "/faces/financeiro/planodecontas/fontederecursos/lista.xhtml")
})
public class FonteDeRecursosControlador extends PrettyControlador<FonteDeRecursos> implements Serializable, CRUD {

    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Exercicio exercicio;
    private FonteDeRecursosEquivalente fonteEquivalente;
    private FonteCodigoCO fonteCodigoCO;

    public FonteDeRecursosControlador() {
        super(FonteDeRecursos.class);
    }

    @URLAction(mappingId = "novo-fonte-de-recursos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        exercicio = fonteDeRecursosFacade.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 1);
    }

    @URLAction(mappingId = "ver-fonte-de-recursos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-fonte-de-recursos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        exercicio = fonteDeRecursosFacade.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 1);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fonte-de-recursos/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<FonteDeRecursos> completarPorExercicio(String parte) {
        if (exercicio != null) {
            return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), exercicio);
        }
        return Lists.newArrayList();
    }

    public List<FonteDeRecursos> completarFontesDoExercicioLogado(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<CodigoCO> completarCodigosCOs(String parte) {
        return fonteDeRecursosFacade.getCodigoCOFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public void cancelarFonteEquivalente() {
        fonteEquivalente = null;
    }

    public void novoFonteEquivalente() {
        fonteEquivalente = new FonteDeRecursosEquivalente();
        fonteEquivalente.setFonteDeRecursosDestino(selecionado);
    }

    public void adicionarFonteEquivalente() {
        try {
            validarFonteEquivalente();
            Util.adicionarObjetoEmLista(selecionado.getFontesEquivalentes(), fonteEquivalente);
            cancelarFonteEquivalente();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarFonteEquivalente(FonteDeRecursosEquivalente fonteEquivalente) {
        this.fonteEquivalente = (FonteDeRecursosEquivalente) Util.clonarObjeto(fonteEquivalente);
    }

    public void removerFonteEquivalente(FonteDeRecursosEquivalente fonteEquivalente) {
        selecionado.getFontesEquivalentes().remove(fonteEquivalente);
    }

    private void validarFonteEquivalente() {
        ValidacaoException ve = new ValidacaoException();
        if (fonteEquivalente.getFonteDeRecursosOrigem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recursos deve ser informado!");
        }
        ve.lancarException();
        for (FonteDeRecursosEquivalente fonteDeRecursosEquivalente : selecionado.getFontesEquivalentes()) {
            if (!fonteDeRecursosEquivalente.equals(fonteEquivalente) && fonteDeRecursosEquivalente.getFonteDeRecursosOrigem().equals(fonteEquivalente.getFonteDeRecursosOrigem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Fonte de Recursos selecionada já está adicionada!");
                break;
            }
        }
        ve.lancarException();

    }

    public Boolean hasLoaEfetivada() {
        return fonteDeRecursosFacade.getLoaFacade().hasLoaEfetivadaNoExercicio(getSistemaControlador().getExercicioCorrente());
    }

    @Override
    public List<FonteDeRecursos> completarEstaEntidade(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public FonteDeRecursosFacade getFacade() {
        return fonteDeRecursosFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return fonteDeRecursosFacade;
    }

    public List<SelectItem> getListaClassificacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ClassificacaoFonteRecursos cfr : ClassificacaoFonteRecursos.values()) {
            toReturn.add(new SelectItem(cfr, cfr.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDeFonte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoFonteRecurso tipo : TipoFonteRecurso.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoFonteRecurso sit : SituacaoFonteRecurso.values()) {
            if (sit.equals(SituacaoFonteRecurso.ATIVO) || sit.equals(SituacaoFonteRecurso.INATIVO)) {
                toReturn.add(new SelectItem(sit, sit.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<FonteDeRecursos> listaFontesPorExercicio() {
        return fonteDeRecursosFacade.listaPorExercicio(sistemaControlador.getExercicioCorrente());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<FonteDeRecursos> buscarFonteDeRecursosPorExercicioCorrente(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    @Override
    protected boolean validaRegrasParaSalvar() {
        return true;
    }
    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public FonteDeRecursosEquivalente getFonteEquivalente() {
        return fonteEquivalente;
    }

    public void setFonteEquivalente(FonteDeRecursosEquivalente fonteEquivalente) {
        this.fonteEquivalente = fonteEquivalente;
    }

    public FonteCodigoCO getFonteCodigoCO() {
        return fonteCodigoCO;
    }

    public void setFonteCodigoCO(FonteCodigoCO fonteCodigoCO) {
        this.fonteCodigoCO = fonteCodigoCO;
    }

    public void cancelarFonteCodigoCO() {
        fonteCodigoCO = null;
    }

    public void novoFonteCodigoCO() {
        fonteCodigoCO = new FonteCodigoCO();
        fonteCodigoCO.setFonteDeRecursos(selecionado);
    }

    public void adicionarFonteCodigoCO() {
        try {
            validarFonteCodigoCO();
            Util.adicionarObjetoEmLista(selecionado.getCodigosCOs(), fonteCodigoCO);
            cancelarFonteCodigoCO();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerFonteCodigoCO(FonteCodigoCO fonteCodigoCO) {
        selecionado.getCodigosCOs().remove(fonteCodigoCO);
    }

    private void validarFonteCodigoCO() {
        ValidacaoException ve = new ValidacaoException();
        if (fonteCodigoCO.getCodigoCO() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código CO deve ser informado!");
        }
        ve.lancarException();
        if (selecionado.getCodigosCOs().contains(fonteCodigoCO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código CO selecionado já foi adicionado.");
        }
        ve.lancarException();
    }
}
