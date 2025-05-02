package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaIsencaoIPTU;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaIsencaoIPTUFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabio
 */
@ManagedBean(name = "categoriaIsencaoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "cadastrarCategoriaIsencaoIPTUTributario",
    pattern = "/tributario/categoriaisencaoiptu/novo/",
    viewId = "/faces/tributario/iptu/isencaoimpostos/categoriaisencao/edita.xhtml"),
    @URLMapping(id = "alterarCategoriaIsencaoIPTUTributario",
    pattern = "/tributario/categoriaisencaoiptu/editar/#{categoriaIsencaoIPTUControlador.id}/",
    viewId = "/faces/tributario/iptu/isencaoimpostos/categoriaisencao/edita.xhtml"),
    @URLMapping(id = "listaCategoriaIsencaoIPTUTributario",
    pattern = "/tributario/categoriaisencaoiptu/listar/",
    viewId = "/faces/tributario/iptu/isencaoimpostos/categoriaisencao/lista.xhtml"),
    @URLMapping(id = "visualizarCategoriaIsencaoIPTUTributario",
    pattern = "/tributario/categoriaisencaoiptu/ver/#{categoriaIsencaoIPTUControlador.id}/",
    viewId = "/faces/tributario/iptu/isencaoimpostos/categoriaisencao/visualizar.xhtml")
})
public class CategoriaIsencaoIPTUControlador extends PrettyControlador<CategoriaIsencaoIPTU> implements Serializable, CRUD {

    @EJB
    private CategoriaIsencaoIPTUFacade categoriaIsencaoIPTUFacade;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete converterTipoDoctoOficial;

    public CategoriaIsencaoIPTUControlador() {
        super(CategoriaIsencaoIPTU.class);
    }

    @Override
    public CategoriaIsencaoIPTU getSelecionado() {
        return selecionado;
    }

    @Override
    public void setSelecionado(CategoriaIsencaoIPTU selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    @URLAction(mappingId = "cadastrarCategoriaIsencaoIPTUTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        this.selecionado = new CategoriaIsencaoIPTU();
        this.selecionado.setCodigo(this.categoriaIsencaoIPTUFacade.ultimoCodigoMaisUm());
        this.selecionado.setPercentual(new BigDecimal("100"));
    }

    public List<SelectItem> getTiposDeCategoriasDeIsencao() {
        return Util.getListSelectItem(TipoCategoriaIsencaoIPTU.values());
    }

    public List<SelectItem> montarTiposImovel() {
        return Util.getListSelectItem(TipoImovel.values());
    }

    public List<CategoriaIsencaoIPTU> getLista() {
        return this.categoriaIsencaoIPTUFacade.lista();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado = categoriaIsencaoIPTUFacade.salvarRetornando(selecionado);
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void excluir() {
        try {
            this.categoriaIsencaoIPTUFacade.remover(selecionado);
            FacesUtil.addOperacaoRealizada("Excluído com sucesso!");
            redireciona();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Este registro não pode ser excluído porque possui dependências.");
        }
    }

    private void validarCampos() {
        ValidacaoException ve  = new ValidacaoException();
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um código maior que zero.");
        } else if (selecionado.getId() == null && categoriaIsencaoIPTUFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(categoriaIsencaoIPTUFacade.ultimoCodigoMaisUm());
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código informado já está em uso em outro registro. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente.");
        } else if (selecionado.getId() != null && !categoriaIsencaoIPTUFacade.existeCodigoCategoriaIsencaoIPTU(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código informado já existe.");
        }
        if (this.selecionado.getDescricao() == null || "".equals(this.selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição.");
        }
        if (this.selecionado.getExercicioInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício Inicial.");
        }
        if (this.selecionado.getExercicioFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício Final.");
        }
        if (this.selecionado.getExercicioInicial() != null && this.selecionado.getExercicioFinal() != null) {
            if (this.selecionado.getExercicioInicial().getAno() > this.selecionado.getExercicioFinal().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício Inicial não pode ser maior que o Exercício Final.");
            }
        }
        if (this.selecionado.getTipoLancamentoIsencaoIPTU() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Lançamento.");
        }
        if (this.selecionado.getPercentual() == null || this.selecionado.getPercentual().compareTo(BigDecimal.ZERO) <= 0 || this.selecionado.getPercentual().compareTo(CEM) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Percentual de Isenção entre 1% e 100%.");
        }
        ve.lancarException();
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(categoriaIsencaoIPTUFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getTipoLancamentoIsencaoIPTUs() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, " "));
        for (TipoLancamentoIsencaoIPTU e : TipoLancamentoIsencaoIPTU.values()) {
            lista.add(new SelectItem(e, e.getDescricao()));
        }
        return lista;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return categoriaIsencaoIPTUFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo(parte.trim(), ModuloTipoDoctoOficial.ISENCAO_IPTU);
    }

    public Converter getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, categoriaIsencaoIPTUFacade.getTipoDoctoOficialFacade());
        }
        return converterTipoDoctoOficial;
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaIsencaoIPTUFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/categoriaisencaoiptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    @URLAction(mappingId = "alterarCategoriaIsencaoIPTUTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        selecionado = categoriaIsencaoIPTUFacade.recuperar(getId());
        super.editar();
    }

    @Override
    @URLAction(mappingId = "visualizarCategoriaIsencaoIPTUTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
