package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTipoContaDespesaClasseCredorFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
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

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configuracaoTipoContaDespesaClasseCredorControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-configuracao-tipocontadespesa-classecredor", pattern = "/configuracao-tipocontadespesa-classecredor/novo/", viewId = "/faces/financeiro/orcamentario/config-tipocontadespesa-classecredor/edita.xhtml"),
        @URLMapping(id = "editar-configuracao-tipocontadespesa-classecredor", pattern = "/configuracao-tipocontadespesa-classecredor/editar/#{configuracaoTipoContaDespesaClasseCredorControlador.id}/", viewId = "/faces/financeiro/orcamentario/config-tipocontadespesa-classecredor/edita.xhtml"),
        @URLMapping(id = "ver-configuracao-tipocontadespesa-classecredor", pattern = "/configuracao-tipocontadespesa-classecredor/ver/#{configuracaoTipoContaDespesaClasseCredorControlador.id}/", viewId = "/faces/financeiro/orcamentario/config-tipocontadespesa-classecredor/visualizar.xhtml"),
        @URLMapping(id = "listar-configuracao-tipocontadespesa-classecredor", pattern = "/configuracao-tipocontadespesa-classecredor/listar/", viewId = "/faces/financeiro/orcamentario/config-tipocontadespesa-classecredor/lista.xhtml")
})
public class ConfiguracaoTipoContaDespesaClasseCredorControlador extends PrettyControlador<ConfiguracaoTipoContaDespesaClasseCredor> implements Serializable, CRUD {
    @EJB
    private ConfiguracaoTipoContaDespesaClasseCredorFacade configuracaoTipoContaDespesaClasseCredorFacade;
    private ConverterAutoComplete converterClasseCredor;
    private ClasseCredor classeCredor;

    public ConfiguracaoTipoContaDespesaClasseCredorControlador() {
        super(ConfiguracaoTipoContaDespesaClasseCredor.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-tipocontadespesa-classecredor/";
    }

    @Override
    @URLAction(mappingId = "novo-configuracao-tipocontadespesa-classecredor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void definirSubTipoDespesaPorTipoDespesa() {
        TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
        if (!tipo.isPessoaEncargos() && !tipo.isDividaPublica() && !tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);

        } else if (tipo.isPessoaEncargos()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.RGPS);

        } else {
            selecionado.setSubTipoDespesa(SubTipoDespesa.VALOR_PRINCIPAL);
        }
    }

    public List<SubTipoDespesa> getSubTipoContas() {
        List<SubTipoDespesa> toReturn = new ArrayList<>();
        if (selecionado != null && selecionado.getTipoContaDespesa() != null) {
            TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
            if (tipo.isPessoaEncargos()) {
                toReturn.add(SubTipoDespesa.RGPS);
                toReturn.add(SubTipoDespesa.RPPS);
            } else if (tipo.isDividaPublica() || tipo.isPrecatorio()) {
                toReturn.add(SubTipoDespesa.JUROS);
                toReturn.add(SubTipoDespesa.OUTROS_ENCARGOS);
                toReturn.add(SubTipoDespesa.VALOR_PRINCIPAL);
            } else {
                toReturn.add(SubTipoDespesa.NAO_APLICAVEL);
            }
        }
        return toReturn;
    }

    @Override
    @URLAction(mappingId = "ver-configuracao-tipocontadespesa-classecredor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-configuracao-tipocontadespesa-classecredor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {

        validarCampos();

        super.salvar();
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getListaDeClasseCredor().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório ter pelo menos uma <b>Classe de Pessoa</b> adicionada na lista.");
        }
        ve.lancarException();
        if (configuracaoTipoContaDespesaClasseCredorFacade.hasConfiguracaoVigente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração vigente utilizando o Tipo de Conta de Despesa <b>" + selecionado.getTipoContaDespesa().toString() +
                "</b> e Subtipo de Despesa <b>" + selecionado.getSubTipoDespesa().getDescricao() + "</b>");
        }
        ve.lancarException();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoTipoContaDespesaClasseCredorFacade;
    }

    //    GETTER E SETTER
    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public List<SelectItem> getTipoContaDespesa() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoContaDespesa tipoContaDespesa : TipoContaDespesa.values()) {
            retorno.add(new SelectItem(tipoContaDespesa, tipoContaDespesa.toString()));
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, configuracaoTipoContaDespesaClasseCredorFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public SelectItem[] tiposClasseCredorView() {
        SelectItem[] opcoes = new SelectItem[TipoClasseCredor.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoClasseCredor tipo : TipoClasseCredor.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposSituacao() {
        SelectItem[] opcoes = new SelectItem[SituacaoCadastralContabil.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (SituacaoCadastralContabil tipo : SituacaoCadastralContabil.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }
//    METODOS UTILIZADOS NA VIEW

    public List<ClasseCredor> listaClasseCredor(String parte) {
        return configuracaoTipoContaDespesaClasseCredorFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
    }

    public void adicionarClasseCredor() {
        if (podeAdicionarClasseCredor(selecionado, classeCredor)) {
            this.selecionado.setListaDeClasseCredor(Util.adicionarObjetoEmLista(selecionado.getListaDeClasseCredor(), new TipoContaDespesaClasseCredor(selecionado, classeCredor)));
            classeCredor = null;
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A Classe Credor <b> " + classeCredor.toString() + "</b> já foi adicionada na lista.");
        }
    }

    private Boolean podeAdicionarClasseCredor(ConfiguracaoTipoContaDespesaClasseCredor selecionado, ClasseCredor classeCredor) {
        for (TipoContaDespesaClasseCredor tipoContaDespesaClasseCredor : selecionado.getListaDeClasseCredor()) {
            if (tipoContaDespesaClasseCredor.getClasseCredor().equals(classeCredor)) {
                return false;
            }
        }
        return true;
    }

    public void removerClasseCredor(TipoContaDespesaClasseCredor tipoContaDespesaClasseCredor) {
        this.selecionado.getListaDeClasseCredor().remove(tipoContaDespesaClasseCredor);
    }
}
