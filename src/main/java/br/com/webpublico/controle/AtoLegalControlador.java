/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AtoLegalValorOperacaoCreditoESuplementacaoVO;
import br.com.webpublico.entidadesauxiliares.OrgaoAtoLegalVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.url.QueryString;
import org.apache.commons.collections.CollectionUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author terminal3
 */
@ManagedBean(name = "atoLegalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAtoLegal", pattern = "/atolegal/novo/", viewId = "/faces/tributario/cadastromunicipal/atolegal/edita.xhtml"),
    @URLMapping(id = "editarAtoLegal", pattern = "/atolegal/editar/#{atoLegalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atolegal/edita.xhtml"),
    @URLMapping(id = "listarAtoLegal", pattern = "/atolegal/listar/", viewId = "/faces/tributario/cadastromunicipal/atolegal/lista.xhtml"),
    @URLMapping(id = "verAtoLegal", pattern = "/atolegal/ver/#{atoLegalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atolegal/visualizar.xhtml"),
    @URLMapping(id = "importarAtoLegal", pattern = "/atolegal/importar/", viewId = "/faces/tributario/cadastromunicipal/atolegal/importar.xhtml"),
})
public class AtoLegalControlador extends PrettyControlador<AtoLegal> implements Serializable, CRUD {

    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterGenerico converterEsferaGoverno;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterUnidadeExterna;
    private ConverterGenerico converterExercicio;
    private ConverterAutoComplete converterVeiculoDePublicacao;
    private ConverterAutoComplete converterPessoaFisica;
    private AtoLegalORC atoLegalORC;
    private BigDecimal totalCredito;
    private BigDecimal totalDeducao;
    private boolean validaTotais;
    private BigDecimal resto;
    private Comissao comissaoSelecionada;
    private MembroComissao membroComissaoSelecionado;
    private MembroComissao membroExoneracao;
    private AtoDeComissao atoDeComissao;
    private AtoLegal superior;
    private MoneyConverter moneyConverter;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private AtoLegalRepublicacao atoLegalRepublicacao;
    private List<AtoLegal> atosNaoCadastrados;
    private List<AtoLegal> atosSalvos;
    private List<OrgaoAtoLegalVO> orgaosPorTipo;
    private OrgaoAtoLegalVO orgaoAtoLegalVO;
    private String errosAoImportar;
    private AssistenteBarraProgressoImportarAto assistenteImportarAto;
    private Future<AssistenteBarraProgressoImportarAto> future;

    public AtoLegalControlador() {
        super(AtoLegal.class);
    }

    public List getLista() {
        return atoLegalFacade.lista();
    }

    public AtoLegalFacade getFacade() {
        return atoLegalFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return atoLegalFacade;
    }

    public Comissao getComissaoSelecionada() {
        return comissaoSelecionada;
    }

    public void setComissaoSelecionada(Comissao comissaoSelecionada) {
        this.comissaoSelecionada = comissaoSelecionada;
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Arquivo a = new Arquivo();
            a.setMimeType(event.getFile().getContentType());
            a.setNome(event.getFile().getFileName());
            a.setDescricao(event.getFile().getFileName());
            a.setTamanho(event.getFile().getSize());
            a.setInputStream(event.getFile().getInputstream());
            a = atoLegalFacade.getArquivoFacade().novoArquivoMemoria(a);

            selecionado.setArquivo(a);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void cancelarArquivo() {
        selecionado.setArquivo(null);
    }

    public BigDecimal getResto() {
        return resto;
    }

    public void setResto(BigDecimal resto) {
        this.resto = resto;
    }

    public AtoLegalORC getAtoLegalORC() {
        return atoLegalORC;
    }

    public void setAtoLegalORC(AtoLegalORC atoLegalORC) {
        this.atoLegalORC = atoLegalORC;
    }

    public AtoDeComissao getAtoDeComissao() {
        return atoDeComissao;
    }

    public void setAtoDeComissao(AtoDeComissao atoDeComissao) {
        this.atoDeComissao = atoDeComissao;
    }

    public MembroComissao getMembroComissaoSelecionado() {
        return membroComissaoSelecionado;
    }

    public void setMembroComissaoSelecionado(MembroComissao membroComissaoSelecionado) {
        this.membroComissaoSelecionado = membroComissaoSelecionado;
    }

    public MembroComissao getMembroExoneracao() {
        return membroExoneracao;
    }

    public void setMembroExoneracao(MembroComissao membroExoneracao) {
        this.membroExoneracao = membroExoneracao;
    }

    @URLAction(mappingId = "novoAtoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
        getParametrosURL();
    }

    private void getParametrosURL() {
        QueryString qs = PrettyContext.getCurrentInstance().getRequestQueryString();
        String valor = qs.getParameter("propositoAtoLegal");
        if (valor == null) {
            return;
        }
        PropositoAtoLegal pal = PropositoAtoLegal.valueOf(valor);
        selecionado.setPropositoAtoLegal(pal);
    }

    @URLAction(mappingId = "importarAtoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void importarAtos() {
        super.novo();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        totalCredito = BigDecimal.ZERO;
        totalDeducao = BigDecimal.ZERO;
        resto = BigDecimal.ZERO;
        adicionarOrgaosPorTipo();
    }

    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    private void inicializar() {
        selecionado.setDataEmissao(atoLegalFacade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(atoLegalFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setTipoAtoLegalUnidade(TipoAtoLegalUnidade.INTERNA);
        atoLegalORC = new AtoLegalORC();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        totalCredito = new BigDecimal(BigInteger.ZERO);
        totalDeducao = new BigDecimal(BigInteger.ZERO);
        resto = new BigDecimal(BigInteger.ZERO);
        atoLegalORC = atoLegalFacade.recuperaAtolegaOrc(selecionado);
        selecionado.setDataPublicacao(atoLegalFacade.getSistemaFacade().getDataOperacao());
    }

    @Override
    public void salvar() {
        try {
            atoLegalFacade.validarAtoLegal(selecionado, null, true, true);
            validaAtoLegalORC();
            validarValoresDasOperacoesDeCreditoAndSuplementacao();
            if (selecionado.getNome() != null) {
                selecionado.setNome(selecionado.getNome().replace("\u0002", ""));
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Ocorreu erro ao salvar o ato legal: " + ex.getMessage());
        }
    }

    public boolean isValidarSituacao() {
        return PropositoAtoLegal.ATO_DE_CARGO.equals(selecionado.getPropositoAtoLegal()) || PropositoAtoLegal.ATO_DE_PESSOAL.equals(selecionado.getPropositoAtoLegal());
    }

    private void validarValoresDasOperacoesDeCreditoAndSuplementacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isPropositoAtoLegalAlteracaoOrcamentaria()) {
            calculaLancamento();
            if (!validaTotais && atoLegalORC.getSuperAvit().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Corrigir os valores das Operações de Crédito e Suplementações para continuar.");
            }
        }
        ve.lancarException();
    }

    public boolean renderizaPainelUnidades() {
        return PropositoAtoLegal.ATO_DE_CARGO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.ATO_DE_PESSOAL.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.CONCESSAO_BENEFICIO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.COMISSAO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.OUTROS.equals(selecionado.getPropositoAtoLegal());
    }

    public boolean isTipoAtoLegalLegislativoOuControleExternoOuPortaria() {
        return TipoAtoLegal.CONTROLE_EXTERNO.equals(selecionado.getTipoAtoLegal()) ||
            TipoAtoLegal.PORTARIA.equals(selecionado.getTipoAtoLegal()) ||
            TipoAtoLegal.LEGISLATIVO.equals(selecionado.getTipoAtoLegal());
    }

    public boolean renderizaObrigatoriedadeDataPublicao() {
        if (selecionado.getPropositoAtoLegal() != null) {
            if (selecionado.getPropositoAtoLegal().equals(PropositoAtoLegal.ATO_DE_CARGO)
                || selecionado.getPropositoAtoLegal().equals(PropositoAtoLegal.ATO_DE_PESSOAL)
                || selecionado.getPropositoAtoLegal().equals(PropositoAtoLegal.OUTROS)
                || selecionado.getPropositoAtoLegal().equals(PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO)) {
                return true;
            }
        }
        return false;
    }

    @URLAction(mappingId = "verAtoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarObjetosAoVerEditar();
    }

    @URLAction(mappingId = "editarAtoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarObjetosAoVerEditar();
    }

    private void inicializarObjetosAoVerEditar() {
        carregarObjetosDoControlador();
        calculaLancamento();
    }

    private void carregarObjetosDoControlador() {
        hierarquiaOrganizacional = getHierarquiaOrganizacionalPorUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
        atoLegalORC = atoLegalFacade.recuperaAtolegaOrc(selecionado);
    }

    private HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        try {
            return atoLegalFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(unidadeOrganizacional.getId());
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public List<SelectItem> getEsferaGoverno() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaGoverno object : atoLegalFacade.getEsferaGovernoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public ConverterGenerico getConverterEsferaGoverno() {
        if (converterEsferaGoverno == null) {
            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, atoLegalFacade.getEsferaGovernoFacade());
        }
        return converterEsferaGoverno;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, atoLegalFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterUnidadeExterna() {
        if (converterUnidadeExterna == null) {
            converterUnidadeExterna = new ConverterAutoComplete(UnidadeExterna.class, atoLegalFacade.getUnidadeExternaFacade());
        }
        return converterUnidadeExterna;
    }

    public void setaCamposAutoCompleteNull() {
        hierarquiaOrganizacional = null;
        selecionado.setUnidadeOrganizacional(null);
        selecionado.setUnidadeExterna(null);
    }

    public List<SelectItem> getPropositoAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PropositoAtoLegal object : PropositoAtoLegal.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTipoAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAtoLegal object : TipoAtoLegal.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getSituacoesAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoAtoLegal obj : SituacaoAtoLegal.values()) {
            toReturn.add(new SelectItem(obj, obj.getCodigo() + " - " + obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAtoLegalUnidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAtoLegalUnidade object : TipoAtoLegalUnidade.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getNaturezaDoCargo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (NaturezaDoCargo object : NaturezaDoCargo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTipoDeComissao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoComissao object : TipoComissao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getAtruibuicaoComissao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtribuicaoComissao object : AtribuicaoComissao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public boolean isValidaTotais() {
        return validaTotais;
    }

    public void setValidaTotais(boolean validaTotais) {
        this.validaTotais = validaTotais;
    }

    public void validaAtoLegalORC() {
        boolean isAlteracaoOrcamento = PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(selecionado.getPropositoAtoLegal());
        boolean isOrcamento = PropositoAtoLegal.ORCAMENTO.equals(selecionado.getPropositoAtoLegal());

        boolean isLei = TipoAtoLegal.LEI_ORDINARIA.equals(selecionado.getTipoAtoLegal());
        boolean isProjeto = TipoAtoLegal.EMENDA.equals(selecionado.getTipoAtoLegal());
        boolean isProjetoLei = TipoAtoLegal.PROJETO_DE_LEI.equals(selecionado.getTipoAtoLegal());
        boolean isDecreto = TipoAtoLegal.DECRETO.equals(selecionado.getTipoAtoLegal());
        boolean isMedidaProvisao = TipoAtoLegal.MEDIDA_PROVISORIA.equals(selecionado.getTipoAtoLegal());
        boolean isResolucao = TipoAtoLegal.RESOLUCAO.equals(selecionado.getTipoAtoLegal());
        if ((isAlteracaoOrcamento || isOrcamento) && (isLei || isDecreto || isMedidaProvisao || isProjeto || isProjetoLei || isResolucao)) {
            atoLegalORC.setAtoLegal(selecionado);
            selecionado.setAtoLegalORC(atoLegalORC);
        } else {
            atoLegalORC.setAtoLegal(null);
            selecionado.setAtoLegalORC(null);
        }
    }

    public void processarPropositoSelecionado() {
        try {
            if (selecionado.isPropositoAtoLegalComissao() && selecionado.getAtoDeComissao() == null) {
                selecionado.setAtoDeComissao(new AtoDeComissao(selecionado));
            }
            definirEsferaParaAtoLegalOrcamentario();
            definirDataDeValidade();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void selecionarComissao(Comissao comissao) {
        comissaoSelecionada = comissao;
    }

    public void selecionarComissaoAndNovoMembro(Comissao comissao) {
        selecionarComissao(comissao);
        novoMembro();
    }

    private void novoMembro() {
        membroComissaoSelecionado = new MembroComissao(comissaoSelecionada);
    }

    public void removerComissao(Comissao comissao) {
        selecionado.getAtoDeComissao().removerComissao(comissao);
    }

    public void instanciarRepublicacao() {
        atoLegalRepublicacao = new AtoLegalRepublicacao();
    }

    public void cancelarRepublicacao() {
        atoLegalRepublicacao = null;
    }

    public void removerRepublicacao(AtoLegalRepublicacao republicacao) {
        selecionado.getRepublicacoes().remove(republicacao);
    }

    public void editarRepublicacao(AtoLegalRepublicacao republicacao) {
        atoLegalRepublicacao = (AtoLegalRepublicacao) Util.clonarObjeto(republicacao);
    }

    public void adicionarRepublicacao() {
        try {
            validarRepublicacao();
            atoLegalRepublicacao.setAtoLegal(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getRepublicacoes(), atoLegalRepublicacao);
            cancelarRepublicacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRepublicacao() {
        ValidacaoException ve = new ValidacaoException();
        if (atoLegalRepublicacao.getNumeroRepublicacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número da Republicação deve ser informado.");
        }
        if (atoLegalRepublicacao.getDataRepublicacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Republicação deve ser informado.");
        }
        ve.lancarException();
    }

    public AtoLegalRepublicacao getAtoLegalRepublicacao() {
        return atoLegalRepublicacao;
    }

    public void setAtoLegalRepublicacao(AtoLegalRepublicacao atoLegalRepublicacao) {
        this.atoLegalRepublicacao = atoLegalRepublicacao;
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = totalCredito;
    }

    public BigDecimal getTotalDeducao() {
        return totalDeducao;
    }

    public void setTotalDeducao(BigDecimal totalDeducao) {
        this.totalDeducao = totalDeducao;
    }

    public AtoLegal getSuperior() {
        return superior;
    }

    public void setSuperior(AtoLegal superior) {
        this.superior = superior;
    }

    public void calculaLancamento() {
        totalCredito = BigDecimal.ZERO;
        totalDeducao = BigDecimal.ZERO;
        resto = BigDecimal.ZERO;
        totalCredito = totalCredito.add(atoLegalORC.getCreditoEspecial());
        totalCredito = totalCredito.add(atoLegalORC.getCreditoExtraordinario());
        totalCredito = totalCredito.add(atoLegalORC.getCreditoSuplementar());

        totalDeducao = totalDeducao.add(atoLegalORC.getAnulacao());
        totalDeducao = totalDeducao.add(atoLegalORC.getExcessoArecadacao());
        totalDeducao = totalDeducao.add(atoLegalORC.getOperacaoDeCredito());
        totalDeducao = totalDeducao.add(atoLegalORC.getSuperAvit());

        resto = totalCredito.subtract(totalDeducao);
        validaTotais = resto.compareTo(BigDecimal.ZERO) == 0;
    }

    public void cancelarComissaoSelecionada() {
        comissaoSelecionada = null;
    }

    public void confirmarComissao() {
        if (!Util.validaCampos(comissaoSelecionada)) return;
        try {
            validarComissaoSelecionada();
            selecionado.getAtoDeComissao().setComissoes(Util.adicionarObjetoEmLista(selecionado.getAtoDeComissao().getComissoes(), comissaoSelecionada));
            cancelarComissaoSelecionada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    public ConverterAutoComplete getConverterVeiculoDePublicacao() {
        if (converterVeiculoDePublicacao == null) {
            converterVeiculoDePublicacao = new ConverterAutoComplete(VeiculoDePublicacao.class, atoLegalFacade.getVeiculoDePublicacaoFacade());
        }
        return converterVeiculoDePublicacao;
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, atoLegalFacade.getPessoaFisicaFacade());
        }
        return converterPessoaFisica;
    }

    public List<VeiculoDePublicacao> completaVeiculoDePublicacao(String parte) {
        return atoLegalFacade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        //return atoLegalFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ADMINISTRATIVA");
        return atoLegalFacade.getHierarquiaOrganizacionalFacade().listaPorUsuarioEIndiceDoNo(atoLegalFacade.getSistemaFacade().getUsuarioCorrente(), parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), atoLegalFacade.getSistemaFacade().getDataOperacao(), 2);
    }

    public List<UnidadeExterna> completaUnidadeExterna(String parte) {
        return atoLegalFacade.getUnidadeExternaFacade().listaFiltrandoPessoaJuridicaEsferaGoverno(parte.trim());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return atoLegalFacade.getPessoaFisicaFacade().listaFiltrando(parte.trim(), "nome", "cpf");
    }

    public void novaComissao() {
        comissaoSelecionada = new Comissao(selecionado.getAtoDeComissao());
        comissaoSelecionada.setCodigo(atoLegalFacade.getComissaoFacade().buscarProximoCodigoDeComissao());
    }

    public void cancelarMembroComissaoSelecionadoAndComissaoSelecionada() {
        membroComissaoSelecionado = null;
        cancelarComissaoSelecionada();
    }

    public void confirmarMembro() {
        if (!Util.validaCampos(membroComissaoSelecionado)) return;
        try {
            validarMembroComissao();
            comissaoSelecionada.setMembroComissao(Util.adicionarObjetoEmLista(comissaoSelecionada.getMembroComissao(), membroComissaoSelecionado));
            novoMembro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarMembroComissao() {
        ValidacaoException ve = new ValidacaoException();
        validarMembroJaAdicionado(ve);
        validarDataExoneradoEm(ve);
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarDataExoneradoEm(ValidacaoException ve) {
        if (membroComissaoSelecionado.temExoneradoEm()) {
            if (membroComissaoSelecionado.getExoneradoEm().after(atoLegalFacade.getSistemaFacade().getDataOperacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data/hora da exoneração não pode ser posterior a data atual. " + UtilRH.getDataOperacao());
            }
        }
    }

    private void validarMembroJaAdicionado(ValidacaoException ve) {
        if (comissaoSelecionada.temMembroAdicionado(membroComissaoSelecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Membro já foi adicionado na lista!");
        }
    }

    public void removerMembro(MembroComissao membro) {
        comissaoSelecionada.remover(membro);
    }

    public void validarComissaoSelecionada() {
        ValidacaoException ve = new ValidacaoException();
        validarCodigoComissaoSelecionada(ve);
        validarDatasComissaoSelecionada(ve);
//        if (possuiMembros()) {
//            atribuiComissaoAoMembro();
//        } else {
//            FacesUtil.addOperacaoNaoPermitida("A comissão deve possuir pelo menos um Membro.");
//            return false;
//        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarDatasComissaoSelecionada(ValidacaoException ve) {
        if (comissaoSelecionada.temFinalVigencia()) {
            if (comissaoSelecionada.getInicioVigencia().after(comissaoSelecionada.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Final de Vigência deve ser posterior o Início de Vigência!");
            }
        }
    }

    private void validarCodigoComissaoSelecionada(ValidacaoException ve) {
        if (selecionado.getAtoDeComissao().temComissaoAdicionadaPorCodigo(comissaoSelecionada)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma comissão cadastrado com esse código!");
        }
    }

    public boolean possuiMembros() {
        return !CollectionUtils.isEmpty(comissaoSelecionada.getMembroComissao());
    }

    public void atribuiComissaoAoMembro() {
        for (MembroComissao membro : comissaoSelecionada.getMembroComissao()) {
            membro.setComissao(comissaoSelecionada);
        }
    }

    public boolean validarCodigoNegativo() {
        if (comissaoSelecionada.getCodigo() < 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Código informado deve ser positivo.");
            comissaoSelecionada.setCodigo(null);
            return false;
        }
        return true;
    }

    public List<SelectItem> getSuperiores() {
        List<SelectItem> dados = new ArrayList<SelectItem>();
        dados.add(new SelectItem(null, ""));
        for (AtoLegal ato : atoLegalFacade.listaFiltrandoAtoLegalAtoLegalDecreto()) {
            dados.add(new SelectItem(ato, ato.toString()));
        }
        return dados;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void copiaValoreAtoPai() {
        AtoLegal selec = (AtoLegal) selecionado;
        if (selec != null && selec.getId() != null) {
            if (selec.getSuperior() != null) {
                AtoLegalORC sup = selec.getSuperior().getAtoLegalORC();
                if (sup != null) {
                    atoLegalORC.setCreditoEspecial(sup.getCreditoEspecial());
                    atoLegalORC.setCreditoExtraordinario(sup.getCreditoExtraordinario());
                    atoLegalORC.setCreditoSuplementar(sup.getCreditoSuplementar());
                    atoLegalORC.setExcessoArecadacao(sup.getExcessoArecadacao());
                    atoLegalORC.setOperacaoDeCredito(sup.getOperacaoDeCredito());
                    atoLegalORC.setSuperAvit(sup.getSuperAvit());
                    atoLegalORC.setAnulacao(sup.getAnulacao());
                    calculaLancamento();
                }
            }
        }
    }

    public void validaDataDecreto(FacesContext context, UIComponent component, Object value) {
        AtoLegal al = selecionado;
        if (al == null) {
            return;
        }
        if (al.getPropositoAtoLegal() != null) {
            if (!PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(al.getPropositoAtoLegal())) {
                FacesMessage message = new FacesMessage();
                Date dataLei = (Date) value;
                if (TipoAtoLegal.DECRETO.equals(al.getTipoAtoLegal()) && al.getSuperior() != null) {
                    if (dataLei.before(al.getSuperior().getDataPublicacao())) {
                        message.setDetail("Data não pode ser menor que da lei vinculada a este ato legal.");
                        message.setSummary("Operação não Permitida!");
                        message.setSeverity(FacesMessage.SEVERITY_ERROR);
                        throw new ValidatorException(message);
                    }
                }
                // boolean logico1 = (al.getPropositoAtoLegal().equals(PropositoAtoLegal.ALTERACAO_ORCAMENTARIA));
                boolean logico = !TipoAtoLegal.EMENDA.equals(al.getTipoAtoLegal())
                    || !TipoAtoLegal.PROJETO_DE_LEI.equals(al.getTipoAtoLegal()) || !TipoAtoLegal.DECRETO.equals(al.getTipoAtoLegal());
                if (!PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(al.getPropositoAtoLegal())) {
                    if (logico) {
                        if (value == null) {
                            message.setDetail(" O Campo Data da Publicação deve ser informado.");
                            message.setSummary("Campo Obrigatório! ");
                            message.setSeverity(FacesMessage.SEVERITY_ERROR);
                            throw new ValidatorException(message);
                        }
                    }
                }
            }
        }
    }

    public void validaNumero(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        AtoLegal al = selecionado;
        FacesMessage message = new FacesMessage();
        //boolean logico = (al.getPropositoAtoLegal().equals(PropositoAtoLegal.ALTERACAO_ORCAMENTARIA));
        if (al.getTipoAtoLegal() == null) {
            return;
        }
        boolean logico = !al.getTipoAtoLegal().equals(TipoAtoLegal.EMENDA)
            || !al.getTipoAtoLegal().equals(TipoAtoLegal.PROJETO_DE_LEI) || !al.getTipoAtoLegal().equals(TipoAtoLegal.DECRETO);
        if (!al.getPropositoAtoLegal().equals(PropositoAtoLegal.ALTERACAO_ORCAMENTARIA)) {
            if (logico) {
                if (value == null) {
                    message.setDetail(" O campo Número do ato legal deve ser informado.");
                    message.setSummary(" Campo Obrigatório! ");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(message);
                }
            }
        }
    }


    @Override
    public String getCaminhoPadrao() {
        return "/atolegal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void selecionarMembro(MembroComissao membro) {
        membroComissaoSelecionado = (MembroComissao) Util.clonarObjeto(membro);
    }

    public void selecionarMembroExoneracao(MembroComissao membro) {
        this.membroExoneracao = membro;
        this.membroExoneracao.setExoneradoEm(UtilRH.getDataOperacao());
    }

    public void cancelarExoneracao() {
        this.membroExoneracao.setExoneradoEm(null);
    }

    public void confirmarExoneracaoMembro() {
        if (membroExoneracao.getExoneradoEm() != null) {
            List<Licitacao> licitacoesQuePossuemMembro = atoLegalFacade.getLicitacaoFacade().recuperarLicitacoesQuePossuamOMembroDeComissao(membroExoneracao);
            if (licitacoesQuePossuemMembro != null) {
                for (Licitacao licitacao : licitacoesQuePossuemMembro) {
                    // Se a licitação estiver 'Aberta', ou seja não estiver homologada o membro exonerado deverá ser removido.
                    licitacao.setListaDeStatusLicitacao(atoLegalFacade.getLicitacaoFacade().recuperarListaDeStatus(licitacao));
                    if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.HOMOLOGADA)) {
                        atoLegalFacade.getLicitacaoFacade().removerMembroComissaoDaLicitacao(membroExoneracao, licitacao);
                    }
                }
            }
        }
    }

    @Override
    public List<AtoLegal> completarEstaEntidade(String parte) {
        return atoLegalFacade.buscarAtosLegaisPorExercicio(parte.trim(), atoLegalFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void navegarParaPessoaFisica() {
        FacesUtil.redirecionamentoInterno("/pessoa-fisica/novo/");
    }

    public List<AtoLegal> completaSelecionado(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public List<AtoLegal> completaAtoLegalPorNomeNumero(String parte) {
        return atoLegalFacade.buscarAtoLegalPorNomeNumero(parte.trim());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.buscarAtosLegais(parte);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    private void definirEsferaParaAtoLegalOrcamentario() {
        if (selecionado.isPropositoAtoLegalAlteracaoOrcamentaria()
            && selecionado.isTipoAtoLegalAlteracaoOrcamentaria()) {
            selecionado.setEsferaGoverno(getEsferaGovernoMunicipal());
        }
    }

    private EsferaGoverno getEsferaGovernoMunicipal() {
        EsferaGoverno esferaGoverno;
        esferaGoverno = atoLegalFacade.getEsferaGovernoFacade().findEsferaGovernoByNome("Municipal");
        if (esferaGoverno != null) {
            return esferaGoverno;
        }
        return null;
    }

    private Integer getAnoDaLoa() {
        try {
            return atoLegalFacade.getLoaFacade().getAnoDaLoa();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage() + " Dessa forma, não foi possível definir a data de validação. Por favor, informe a mesma de forma manual.");
        }
    }

    private void definirDataDeValidade() {
        if (selecionado.isPropositoAtoLegalAlteracaoOrcamentaria()
            && selecionado.isTipoAtoLegalAlteracaoOrcamentaria()) {
            selecionado.setDataValidade(DataUtil.getDataUltimoDiaAnoFromExercicioCorrente());
        }
    }

    public void novoOrgaoAtoLegalVO() {
        orgaoAtoLegalVO = new OrgaoAtoLegalVO();
    }

    public void editarOrgaoAtoLegalVO(OrgaoAtoLegalVO orgao) {
        orgaoAtoLegalVO = (OrgaoAtoLegalVO) Util.clonarObjeto(orgao);
    }

    public void removerOrgaoAtoLegalVO(OrgaoAtoLegalVO orgao) {
        orgaosPorTipo.remove(orgao);
    }

    public void cancelarOrgaoAtoLegalVO() {
        orgaoAtoLegalVO = null;
    }

    public List<SelectItem> getTiposDeUnidades() {
        return Util.getListSelectItem(TipoOrgaoAtoLegal.values());
    }

    public void adicionarOrgaoAtoLegalVO() {
        try {
            validarAdicionarOrgao();
            Util.adicionarObjetoEmLista(orgaosPorTipo, orgaoAtoLegalVO);
            cancelarOrgaoAtoLegalVO();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarAdicionarOrgao() {
        ValidacaoException ve = new ValidacaoException();
        if (orgaoAtoLegalVO.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo deve ser informado.");
        }
        if (orgaoAtoLegalVO.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade deve ser informado.");
        }
        ve.lancarException();
        if (orgaosPorTipo.stream().anyMatch(opt -> opt.getTipo().equals(orgaoAtoLegalVO.getTipo()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo selecionado já foi adicionado.");
        }
        ve.lancarException();
    }

    public List<AtoLegal> getAtosNaoCadastrados() {
        return atosNaoCadastrados;
    }

    public void setAtosNaoCadastrados(List<AtoLegal> atosNaoCadastrados) {
        this.atosNaoCadastrados = atosNaoCadastrados;
    }

    public List<OrgaoAtoLegalVO> getOrgaosPorTipo() {
        return orgaosPorTipo;
    }

    public void setOrgaosPorTipo(List<OrgaoAtoLegalVO> orgaosPorTipo) {
        this.orgaosPorTipo = orgaosPorTipo;
    }

    public OrgaoAtoLegalVO getOrgaoAtoLegalVO() {
        return orgaoAtoLegalVO;
    }

    public void setOrgaoAtoLegalVO(OrgaoAtoLegalVO orgaoAtoLegalVO) {
        this.orgaoAtoLegalVO = orgaoAtoLegalVO;
    }

    public String getErrosAoImportar() {
        return errosAoImportar;
    }

    public void setErrosAoImportar(String errosAoImportar) {
        this.errosAoImportar = errosAoImportar;
    }

    public void removerAtoNaoCadastrado(AtoLegal ato) {
        atosNaoCadastrados.remove(ato);
    }

    public AssistenteBarraProgressoImportarAto getAssistenteImportarAto() {
        return assistenteImportarAto;
    }

    public void setAssistenteImportarAto(AssistenteBarraProgressoImportarAto assistenteImportarAto) {
        this.assistenteImportarAto = assistenteImportarAto;
    }

    public List<AtoLegal> getAtosSalvos() {
        return atosSalvos;
    }

    public void setAtosSalvos(List<AtoLegal> atosSalvos) {
        this.atosSalvos = atosSalvos;
    }

    public void redirecinarParaTelaImportacao() {
        redireciona(getCaminhoPadrao() + "importar/");
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void salvarAtosLegaisImportados() {
        atosSalvos = Lists.newArrayList();
        errosAoImportar = "";
        for (AtoLegal ato : atosNaoCadastrados) {
            try {
                selecionado = ato;
                atoLegalFacade.salvarNovo(selecionado);
                atosSalvos.add(selecionado);
            } catch (ValidacaoException ve) {
                String menssagens = "<br/>Erro: ";
                for (FacesMessage message : ve.getMensagens()) {
                    menssagens += message.getDetail() + "<br/>";
                }
                menssagens += "<br/>";
                errosAoImportar += selecionado.toString() + menssagens;
            } catch (Exception ex) {
                logger.error("Erro ao importar arquivo de atos legais.", ex);
                errosAoImportar += selecionado.toString() + "<br/>Erro: " + ex.getMessage() + "<br/><br/>";
            }
        }
        FacesUtil.addOperacaoRealizada("Operação realizada com sucesso!");
        if (!errosAoImportar.isEmpty()) {
            FacesUtil.addWarn("Atenção", "Um ou mais Atos não puderam ser salvos, confira a aba de erro para ver os motivos.");
        }
        atosNaoCadastrados = Lists.newArrayList();
    }

    private void adicionarOrgaosPorTipo() {
        orgaosPorTipo = Lists.newArrayList();
        for (TipoOrgaoAtoLegal tipo : TipoOrgaoAtoLegal.values()) {
            String parte;
            switch (tipo) {
                case GABINETE_DO_PREFEITO:
                    parte = "Gabinete do Prefeito";
                    break;

                case CASA_CIVIL:
                    parte = "Secretaria Municipal da Casa Civil";
                    break;

                default:
                    parte = tipo.name();
                    break;
            }

            List<HierarquiaOrganizacional> hierarquias = completaUnidadeOrganizacional(parte);
            if (!hierarquias.isEmpty()) {
                orgaosPorTipo.add(new OrgaoAtoLegalVO(tipo, hierarquias.get(0)));
            }
        }
    }

    public void importar(FileUploadEvent evento) {
        try {
            atosSalvos = null;
            assistenteImportarAto = new AssistenteBarraProgressoImportarAto();
            assistenteImportarAto.setOrgaosPorTipo(orgaosPorTipo);
            assistenteImportarAto.setValorOperacoesCredito(new AtoLegalValorOperacaoCreditoESuplementacaoVO(totalCredito, totalDeducao, resto));
            assistenteImportarAto.setEvento(evento);
            assistenteImportarAto.setDataAtual(atoLegalFacade.getSistemaFacade().getDataOperacao());
            future = atoLegalFacade.importarAtos(assistenteImportarAto);
            FacesUtil.executaJavaScript("openDialog(dialogImportacao)");
            FacesUtil.executaJavaScript("pollImportacao.start()");
        } catch (Exception ex) {
            logger.error("Erro ao importar arquivo de atos legais.", ex);
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }

    public void acompanharImportacao() {
        if (future != null && future.isDone()) {
            errosAoImportar = assistenteImportarAto.getErrosAoImportar().toString();
            atosNaoCadastrados = assistenteImportarAto.getAtosNaoCadastrados();
            FacesUtil.executaJavaScript("pollImportacao.stop()");
            FacesUtil.executaJavaScript("closeDialog(dialogImportacao)");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addOperacaoRealizada("Importação finalizada com sucesso.");
            if (errosAoImportar != null && !errosAoImportar.isEmpty()) {
                FacesUtil.addWarn("Atenção", "Um ou mais Atos não puderam ser recuperados, confira a aba de erro para ver os motivos.");
            }
            assistenteImportarAto = null;
            future = null;
        }
    }
}
