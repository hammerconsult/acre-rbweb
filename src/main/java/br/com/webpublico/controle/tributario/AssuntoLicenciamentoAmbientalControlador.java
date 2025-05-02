package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.AssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.CategoriaAssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.SituacaoEmissaoDebitoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.SituacaoEmissaoLicencaLicenciamentoAmbiental;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.tributario.CategoriasAssuntoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.AssuntoLicenciamentoAmbientalFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAssuntoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/assunto/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/assunto/edita.xhtml"),
    @URLMapping(id = "editarAssuntoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/assunto/editar/#{assuntoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/assunto/edita.xhtml"),
    @URLMapping(id = "verAssuntoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/assunto/ver/#{assuntoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/assunto/visualizar.xhtml"),
    @URLMapping(id = "listarAssuntoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/assunto/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/assunto/lista.xhtml")
})
public class AssuntoLicenciamentoAmbientalControlador extends PrettyControlador<AssuntoLicenciamentoAmbiental> implements CRUD, Serializable {

    @EJB
    private AssuntoLicenciamentoAmbientalFacade assuntoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private CategoriaAssuntoLicenciamentoAmbiental categoriaSelecionada;
    private SituacaoEmissaoDebitoLicenciamentoAmbiental situacaoEmissaoDebitoSelecionada;
    private SituacaoEmissaoLicencaLicenciamentoAmbiental situacaoEmissaoLicencaSelecionada;

    @Override
    public AbstractFacade getFacede() {
        return assuntoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/assunto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssuntoLicenciamentoAmbientalControlador() {
        super(AssuntoLicenciamentoAmbiental.class);
    }


    @URLAction(mappingId = "verAssuntoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAssuntoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novaCategoriaSelecionada();
        novaDividaSituacao();
        novaSituacaoLicenca();
    }

    @URLAction(mappingId = "novoAssuntoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        novaCategoriaSelecionada();
        novaDividaSituacao();
        novaSituacaoLicenca();
    }

    public CategoriaAssuntoLicenciamentoAmbiental getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(CategoriaAssuntoLicenciamentoAmbiental categoriaSelecionada) {
        this.categoriaSelecionada = categoriaSelecionada;
    }

    @Override
    protected String getMensagemSucessoAoSalvar() {
        return "O Assunto " + selecionado.getDescricao() + " foi salvo com sucesso.";
    }

    @Override
    protected String getMensagemSucessoAoExcluir() {
        return "O Assunto " + selecionado.getDescricao() + " foi excluído com sucesso.";
    }

    @Override
    public void salvar() {
        try {
            if (operacao.equals(Operacoes.NOVO)) {
                selecionado.setCodigo(Math.toIntExact(singletonGeradorCodigo.getProximoCodigo(AssuntoLicenciamentoAmbiental.class, "codigo")));
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public SituacaoEmissaoDebitoLicenciamentoAmbiental getSituacaoEmissaoDebitoSelecionada() {
        return situacaoEmissaoDebitoSelecionada;
    }

    public void setSituacaoEmissaoDebitoSelecionada(SituacaoEmissaoDebitoLicenciamentoAmbiental situacaoEmissaoDebitoSelecionada) {
        this.situacaoEmissaoDebitoSelecionada = situacaoEmissaoDebitoSelecionada;
    }

    public SituacaoEmissaoLicencaLicenciamentoAmbiental getSituacaoEmissaoLicencaSelecionada() {
        return situacaoEmissaoLicencaSelecionada;
    }

    public void setSituacaoEmissaoLicencaSelecionada(SituacaoEmissaoLicencaLicenciamentoAmbiental situacaoEmissaoLicencaSelecionada) {
        this.situacaoEmissaoLicencaSelecionada = situacaoEmissaoLicencaSelecionada;
    }

    public void novaCategoriaSelecionada() {
        setCategoriaSelecionada(new CategoriaAssuntoLicenciamentoAmbiental(selecionado));
    }

    private void validarCategoria() {
        ValidacaoException ve = new ValidacaoException();
        if (categoriaSelecionada.getCategoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a categoria.");
        }
        if (categoriaSelecionada.getValorUFM() == null || categoriaSelecionada.getValorUFM().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o valor (UFM).");
        }
        if (categoriaSelecionada.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício.");
        } else {
            Exercicio exAdicionando = categoriaSelecionada.getExercicio();
            CategoriasAssuntoLicenciamentoAmbiental categoriaAdicionando = categoriaSelecionada.getCategoria();
            for (CategoriaAssuntoLicenciamentoAmbiental categoria : selecionado.getCategorias()) {
                if (categoria.getCategoria().equals(categoriaAdicionando) && categoria.getExercicio().equals(exAdicionando) && !categoria.equals(categoriaSelecionada)) {
                    ve.adicionarMensagemDeCampoObrigatorio("A " + categoriaAdicionando + " com o exerício " + exAdicionando + " já foi adicionada");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarCategoria() {
        try {
            validarCategoria();
            Util.adicionarObjetoEmLista(selecionado.getCategorias(), categoriaSelecionada);
            novaCategoriaSelecionada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerCategoria(ActionEvent evento) {
        CategoriaAssuntoLicenciamentoAmbiental categoria = (CategoriaAssuntoLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vCategoria");
        selecionado.getCategorias().remove(categoria);
    }

    public void editarCategoria(CategoriaAssuntoLicenciamentoAmbiental categoria) {
        categoriaSelecionada = (CategoriaAssuntoLicenciamentoAmbiental) Util.clonarObjeto(categoria);
    }

    public List<SelectItem> getTodosAssuntos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.addAll(getTodosAssuntosSemCampoVazio());
        return toReturn;
    }

    public List<SelectItem> getTodasCategorias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriasAssuntoLicenciamentoAmbiental categoria : CategoriasAssuntoLicenciamentoAmbiental.values()) {
            toReturn.add(new SelectItem(categoria, categoria.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (StatusLicenciamentoAmbiental status : StatusLicenciamentoAmbiental.getSituacoesAtivas()) {
            toReturn.add(new SelectItem(status, status.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTodosAssuntosSemCampoVazio() {
        return assuntoFacade.lista().stream()
            .map(assunto -> new SelectItem(assunto, assunto.toString()))
            .collect(Collectors.toList());
    }

    private void validarDividaSituacao() {
        ValidacaoException ve = new ValidacaoException();
        if (situacaoEmissaoDebitoSelecionada.getStatus() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a situação.");
        }
        for (SituacaoEmissaoDebitoLicenciamentoAmbiental situacao : selecionado.getSituacoesEmissaoDebito()) {
            if (situacao.getStatus().equals(situacaoEmissaoDebitoSelecionada.getStatus()) && !situacao.equals(situacaoEmissaoDebitoSelecionada)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A situação " + situacao.getStatus().getDescricao() + " já foi adicionada.");
                break;
            }
        }
        ve.lancarException();
    }

    public void novaDividaSituacao() {
        setSituacaoEmissaoDebitoSelecionada(new SituacaoEmissaoDebitoLicenciamentoAmbiental(selecionado));
    }

    public void adicionarDividaSituacao() {
        try {
            validarDividaSituacao();
            Util.adicionarObjetoEmLista(selecionado.getSituacoesEmissaoDebito(), situacaoEmissaoDebitoSelecionada);
            novaDividaSituacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removeDividaSituacao(ActionEvent evento) {
        selecionado.getSituacoesEmissaoDebito().remove((SituacaoEmissaoDebitoLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vDividaSituacao"));
    }

    private void validarSituacaoLicenca() {
        ValidacaoException ve = new ValidacaoException();
        if (situacaoEmissaoLicencaSelecionada.getStatus() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a situação.");
        }
        for (SituacaoEmissaoLicencaLicenciamentoAmbiental situacao : selecionado.getSituacoesEmissaoLicenca()) {
            if (situacao.getStatus().equals(situacaoEmissaoLicencaSelecionada.getStatus()) && !situacao.equals(situacaoEmissaoLicencaSelecionada)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A situação " + situacaoEmissaoLicencaSelecionada.getStatus().getDescricao() + " já foi adicionada.");
                break;
            }
        }
        ve.lancarException();
    }

    public void novaSituacaoLicenca() {
        setSituacaoEmissaoLicencaSelecionada(new SituacaoEmissaoLicencaLicenciamentoAmbiental(selecionado));
    }

    public void adicionarSituacaoLicenca() {
        try {
            validarSituacaoLicenca();
            Util.adicionarObjetoEmLista(selecionado.getSituacoesEmissaoLicenca(), situacaoEmissaoLicencaSelecionada);
            novaSituacaoLicenca();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removeSituacaoLicenca(ActionEvent evento) {
        selecionado.getSituacoesEmissaoLicenca().remove((SituacaoEmissaoLicencaLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vSituacao"));
    }
}
