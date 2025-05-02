package br.com.webpublico.controlerelatorio;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PublicoAlvoPreferencial;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProgramaAlimentacaoFacade;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-prog-alimentacao", pattern = "/programa-alimentacao/novo/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/programa-alimentacao/edita.xhtml"),
    @URLMapping(id = "editar-prog-alimentacao", pattern = "/programa-alimentacao/editar/#{programaAlimentacaoControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/programa-alimentacao/edita.xhtml"),
    @URLMapping(id = "listar-prog-alimentacao", pattern = "/programa-alimentacao/listar/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/programa-alimentacao/lista.xhtml"),
    @URLMapping(id = "ver-prog-alimentacao", pattern = "/programa-alimentacao/ver/#{programaAlimentacaoControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/programa-alimentacao/visualizar.xhtml"),
})
public class ProgramaAlimentacaoControlador extends PrettyControlador<ProgramaAlimentacao> implements Serializable, CRUD {

    @EJB
    private ProgramaAlimentacaoFacade facade;
    private ProgramaAlimentacaoLocalEstoque programaLocalEstoque;
    private LocalEstoque localEstoquePai;

    public ProgramaAlimentacaoControlador() {
        super(ProgramaAlimentacao.class);
    }

    @Override
    @URLAction(mappingId = "nova-prog-alimentacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        novoLocalEstoque();
        if(selecionado.getComposicaoNutricional() == null){
            selecionado.setComposicaoNutricional(new ComposicaoNutricional());
        }
    }

    @Override
    @URLAction(mappingId = "ver-prog-alimentacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @Override
    @URLAction(mappingId = "editar-prog-alimentacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        novoLocalEstoque();
        atribuirLocalEstoquePaiAssociadoAoPrograma();
        if (selecionado.getComposicaoNutricional() == null) {
            selecionado.setComposicaoNutricional(new ComposicaoNutricional());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programa-alimentacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    @Override
    protected boolean validaRegrasParaSalvar() {
        validarCamposInformacaoNutricional();
        ValidacaoException ve = new ValidacaoException();
        if (Util.isListNullOrEmpty(selecionado.getLocaisEstoque())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione local de estoque ao programa para continuar.");
        }
        ve.lancarException();
        return super.validaRegrasParaSalvar();
    }

    public void atribuirLocalEstoquePaiAssociadoAoPrograma() {
         if (!Util.isListNullOrEmpty(selecionado.getLocaisEstoque())) {
            LocalEstoque localEstoqueFilho = selecionado.getLocaisEstoque().get(0).getLocalEstoque();
            if (localEstoqueFilho.getSuperior() == null) {
                localEstoquePai = localEstoqueFilho;
                return;
            }
            localEstoquePai = localEstoqueFilho.getSuperior();
        } else {
            localEstoquePai = null;
        }
    }

    public List<ConvenioReceita> completarConvernioReceita(String parte) {
        return facade.getConvenioReceitaFacade().buscarFiltrandoConvenioReceita(parte.trim());
    }

    public List<LocalEstoque> completarLocalEstoque(String parte) {
        if (hasLocalEstoquePai()) {
            return facade.getLocalEstoqueFacade().recuperarFilhos(localEstoquePai);
        }
        return facade.getLocalEstoqueFacade().completarLocalEstoqueAbertos(parte.trim());
    }

    public void novoLocalEstoque() {
        programaLocalEstoque = new ProgramaAlimentacaoLocalEstoque();
        programaLocalEstoque.setProgramaAlimentacao(selecionado);
    }

    public void removerLocalEstoque(ProgramaAlimentacaoLocalEstoque progLocalEstoque) {
        selecionado.getLocaisEstoque().remove(progLocalEstoque);
        atribuirLocalEstoquePaiAssociadoAoPrograma();
    }

    public void adicionarLocalEstoque() {
        try {
            validarLocalEstoque();
            Util.adicionarObjetoEmLista(selecionado.getLocaisEstoque(), programaLocalEstoque);
            novoLocalEstoque();
            atribuirLocalEstoquePaiAssociadoAoPrograma();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean hasLocalEstoquePai() {
        return localEstoquePai != null;
    }

    private void validarLocalEstoque() {
        Util.validarCampos(programaLocalEstoque);
        ValidacaoException ve = new ValidacaoException();
        for (ProgramaAlimentacaoLocalEstoque le : selecionado.getLocaisEstoque()) {
            if (le.getLocalEstoque().equals(programaLocalEstoque.getLocalEstoque())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O local de estoque já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public ProgramaAlimentacaoLocalEstoque getProgramaLocalEstoque() {
        return programaLocalEstoque;
    }

    public void setProgramaLocalEstoque(ProgramaAlimentacaoLocalEstoque programaLocalEstoque) {
        this.programaLocalEstoque = programaLocalEstoque;
    }

    public List<SelectItem> getPublicos() {
        return Util.getListSelectItem(PublicoAlvoPreferencial.values(), false);
    }

    public TipoMascaraUnidadeMedida getTipoMascaraDefault(){
        return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
    }

    public void validarCamposInformacaoNutricional() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getComposicaoNutricional() != null) {
            if (selecionado.getComposicaoNutricional().getEnergiaKCAL() != null) {
                if (selecionado.getComposicaoNutricional().getEnergiaKCAL().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Energia (Kcal) deve ser maior que ZERO.");
                }
            }
        }
        ve.lancarException();
    }
}
