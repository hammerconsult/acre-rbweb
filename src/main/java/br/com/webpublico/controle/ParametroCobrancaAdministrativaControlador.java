package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: julio
 * Date: 09/08/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "parametroCobrancaAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroCobrancaAdministrativa", pattern = "/parametrocobrancaAdministrativa/novo/", viewId = "/faces/tributario/contacorrente/parametrocobrancaadministrativa/edita.xhtml"),
    @URLMapping(id = "editarParametroCobrancaAdministrativa", pattern = "/parametrocobrancaAdministrativa/editar/#{parametroCobrancaAdministrativaControlador.id}/", viewId = "/faces/tributario/contacorrente/parametrocobrancaadministrativa/edita.xhtml"),
    @URLMapping(id = "listarParametroCobrancaAdministrativa", pattern = "/parametrocobrancaAdministrativa/listar/", viewId = "/faces/tributario/contacorrente/parametrocobrancaadministrativa/lista.xhtml"),
    @URLMapping(id = "verParametroCobrancaAdministrativa", pattern = "/parametrocobrancaAdministrativa/ver/#{parametroCobrancaAdministrativaControlador.id}/", viewId = "/faces/tributario/contacorrente/parametrocobrancaadministrativa/visualizar.xhtml")
})
public class ParametroCobrancaAdministrativaControlador extends PrettyControlador<ParametrosCobrancaAdministrativa> implements Serializable, CRUD {


    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ParametroCobrancaAdministrativaFacade facade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterTipoDoctoFicial;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete converterPessoa;


    public ParametroCobrancaAdministrativaControlador() {
        super(ParametrosCobrancaAdministrativa.class);
    }

    @Override
    @URLAction(mappingId = "novoParametroCobrancaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verParametroCobrancaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroCobrancaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarParametro();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametrocobrancaAdministrativa/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    private void validarParametro() {
        ValidacaoException ve = new ValidacaoException();
        ParametrosCobrancaAdministrativa parametro = facade.parametrosCobrancaAdministrativaPorExercicio(selecionado.getExercicio());
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício!");
        }
        if (parametro != null && !selecionado.equals(parametro)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Parâmetro cadastrado para o exercício informado!");
        }
        if (selecionado.getDiretorDepartamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o diretor do departamento!");
        }
        if (selecionado.getChefeDaDivisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o chefe da divisao!");
        }
        if (Strings.isNullOrEmpty(selecionado.getCargo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cargo!");
        }
        if (Strings.isNullOrEmpty(selecionado.getPortaria())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a portaria!");
        }
        ve.lancarException();
    }

    public ConverterExercicio converteExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterTipoDoctOficial() {
        if (converterTipoDoctoFicial == null) {
            converterTipoDoctoFicial = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDoctoFicial;
    }

    public void setConverterExercicio(ConverterExercicio converterExercicio) {
        this.converterExercicio = converterExercicio;
    }

    public void setConverterTipoDoctoFicial(ConverterAutoComplete converterTipoDoctoFicial) {
        this.converterTipoDoctoFicial = converterTipoDoctoFicial;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroImobiliario(String parte) {
        return facade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROIMOBILIARIO, ModuloTipoDoctoOficial.COBRANCAADMINISTRATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaFisica(String parte) {
        return facade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAFISICA, ModuloTipoDoctoOficial.COBRANCAADMINISTRATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaJuridica(String parte) {
        return facade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAJURIDICA, ModuloTipoDoctoOficial.COBRANCAADMINISTRATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroEconomico(String parte) {
        return facade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROECONOMICO, ModuloTipoDoctoOficial.COBRANCAADMINISTRATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroRural(String parte) {
        return facade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTRORURAL, ModuloTipoDoctoOficial.COBRANCAADMINISTRATIVA);
    }

    public List<PessoaFisica> completarPessoasComContratosFP(String parte) {
        return contratoFPFacade.listaPessoasComContratos(parte.trim());
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFisicaFacade);
        }
        return converterPessoa;
    }

    public void novoTipoDoctoOficial() {
        Web.navegacao(selecionado, getCaminhoOrigem(), "/tipo-de-documento-oficial/novo/", "");

    }

    public void visualizarDocumentoOficial(TipoDoctoOficial tipoDoctoOficial) {
        Web.navegacao(selecionado, getCaminhoOrigem(), "/tipo-de-documento-oficial/ver/" + tipoDoctoOficial.getId() + "/", "");
    }
}
