/*
 * Codigo gerado automaticamente em Wed Nov 30 14:35:11 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoContabilAdministrativo;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "empenhoControlador")
@ViewScoped
@URLMappings(mappings = {
//        Mapeamento Empenho Normal
    @URLMapping(id = "novo-empenho", pattern = "/empenho/novo/", viewId = "/faces/financeiro/orcamentario/empenho/edita.xhtml"),
    @URLMapping(id = "editar-empenho", pattern = "/empenho/editar/#{empenhoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenho/edita.xhtml"),
    @URLMapping(id = "ver-empenho", pattern = "/empenho/ver/#{empenhoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenho/visualizar.xhtml"),
    @URLMapping(id = "listar-empenho", pattern = "/empenho/listar/", viewId = "/faces/financeiro/orcamentario/empenho/lista.xhtml"),

//        Mapeamento Empenho Resto a Pagar
    @URLMapping(id = "novo-empenho-resto", pattern = "/empenho/resto-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/empenho/edita.xhtml"),
    @URLMapping(id = "editar-empenho-resto", pattern = "/empenho/resto-a-pagar/editar/#{empenhoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenho/edita.xhtml"),
    @URLMapping(id = "ver-empenho-resto", pattern = "/empenho/resto-a-pagar/ver/#{empenhoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenho/visualizar.xhtml"),
    @URLMapping(id = "listarRestoPagar-empenho-resto", pattern = "/empenho/resto-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/empenho/listarRestoPagar.xhtml"),

    @URLMapping(id = "consultar-liquidacoes-empenho", pattern = "/empenho/#{empenhoControlador.id}/liquidacoes/", viewId = "/faces/financeiro/orcamentario/empenho/consultar-liquidacoes-do-empenho.xhtml")
})

public class EmpenhoControlador extends PrettyControlador<Empenho> implements Serializable, CRUD {

    @EJB
    protected EmpenhoFacade facade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private MoneyConverter moneyConverter;
    private DesdobramentoEmpenho desdobramento;
    private EmpenhoObrigacaoPagar empenhoObrigacaoPagar;
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    private List<TipoContaDespesa> tiposContaDespesa;
    private Object objetoPesquisaGenerica;
    private String classePesquisaGenerica;
    private List<SolicitacaoEmpenho> solicitacoesPendentes;
    private List<DesdobramentoObrigacaoPagar> detalhamentosObrigacaoPagar;
    private Integer indiceAba;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORC;
    private ConfigContaDespTipoPessoa configContaDespTipoPessoa;
    private FormaEntregaExecucao formaEntregaExecucao;
    private IntegracaoContabilAdministrativo integracaoContabilAdministrativo;
    private Boolean pertenceEmenda;
    private EmendaEmpenho emendaEmpenho;
    private Empenho empenhoDeRP;

    public EmpenhoControlador() {
        super(Empenho.class);
    }

    public EmpenhoFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (isCategoriaOrcamentariaNormal()) {
            return "/empenho/";
        } else {
            return "/empenho/resto-a-pagar/";
        }
    }

    public String getTituloEmpenho() {
        if (isCategoriaOrcamentariaNormal()) {
            return "Empenho";
        } else {
            return "Empenho de Resto a Pagar";
        }
    }

    public String getTituloLiquidacao() {
        if (isCategoriaOrcamentariaNormal()) {
            return "Liquidação";
        } else {
            return "Liquidação de Resto a Pagar";
        }
    }

    public String getTituloPagamento() {
        if (isCategoriaOrcamentariaNormal()) {
            return "Pagamento";
        } else {
            return "Pagamento de Resto a Pagar";
        }
    }

    private void redirecionarParaLista() {
        cleanScoped();
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public void redirecionarParaVer(Empenho empenho) {
        selecionado = empenho;
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        cleanScoped();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @URLAction(mappingId = "ver-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("O Empenho selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Empenho Normal.");
            redireciona();
        }
    }

    @URLAction(mappingId = "editar-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("O Empenho selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Empenho Normal.");
            redireciona();
        }
    }

    @URLAction(mappingId = "novo-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEmpenhoResto() {
        super.novo();
        parametrosIniciaisRestoPagar();
    }

    @URLAction(mappingId = "ver-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEmpenhoResto() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarEditarVer();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("O Empenho selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Restos a Pagar.");
            redireciona();
        } else {
            vwHierarquiaDespesaORC = facade.getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getEmpenho().getDespesaORC().getId());
        }
    }

    @URLAction(mappingId = "editar-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEmpenhoResto() {
        super.editar();
        recuperarEditarVer();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("O Empenho selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Restos a Pagar.");
            redireciona();
        } else {
            vwHierarquiaDespesaORC = facade.getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getEmpenho().getDespesaORC().getId());
        }
    }

    @URLAction(mappingId = "consultar-liquidacoes-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consultarLiquidacoesEmpenho() {
        super.editar();
        carregarLiquidacoesDoEmpenho();
    }

    public void buscarDetalhamentoFonte() {
        if (selecionado.getFonteDespesaORC() != null) {
            Conta contaDestinacao = selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            selecionado.setContaDeDestinacao((ContaDeDestinacao) contaDestinacao);
            FonteDeRecursos fonte = getFonteRecursoSelecionada();
            selecionado.setFonteDeRecursos(fonte);
            TipoFonteRecurso tipoFonteRecurso = fonte.getTipoFonteRecurso();
            if (selecionado.getSolicitacaoEmpenho() != null && TipoFonteRecurso.OPERACAO_CREDITO.equals(tipoFonteRecurso)) {
                DividaPublicaSolicitacaoEmpenho solicitacaoDividaPublica = buscarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(selecionado.getSolicitacaoEmpenho());
                if (solicitacaoDividaPublica != null && solicitacaoDividaPublica.getOperacaoDeCredito() != null) {
                    selecionado.setOperacaoDeCredito(solicitacaoDividaPublica.getOperacaoDeCredito());
                }
            }
            atualizarExtensaoDaFonte(selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getExtensaoFonteRecurso());
        } else {
            atualizarExtensaoDaFonte(null);
        }
    }

    private void atualizarExtensaoDaFonte(ExtensaoFonteRecurso extensaoFonteRecurso) {
        if (isOperacaoNovo()) {
            selecionado.setExtensaoFonteRecurso(extensaoFonteRecurso);
        }
    }

    private void parametrosIniciaisComuns() {
        selecionado.setLiquidacoes(new ArrayList<Liquidacao>());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataEmpenho(facade.getSistemaFacade().getDataOperacao());
        selecionado.setModalidadeLicitacao(ModalidadeLicitacaoEmpenho.SEM_LICITACAO);
        selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
        selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        classePesquisaGenerica = null;
        indiceAba = 0;
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    private void parametrosIniciais() {
        parametrosIniciaisComuns();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
        if (tiposContaDespesa == null) {
            tiposContaDespesa = Lists.newArrayList();
        }
        if (solicitacoesPendentes == null) {
            recuperarSolicitacoesPendentes();
        }
    }

    private void parametrosIniciaisRestoPagar() {
        parametrosIniciaisComuns();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
        selecionado.setTipoRestosProcessados(TipoRestosProcessado.PROCESSADOS);
    }

    public boolean isCategoriaOrcamentariaNormal() {
        return CategoriaOrcamentaria.NORMAL.equals(selecionado.getCategoriaOrcamentaria());
    }

    private void carregarLiquidacoesDoEmpenho() {
        for (Liquidacao liq : selecionado.getLiquidacoes()) {
            liq = facade.getLiquidacaoFacade().recuperar(liq.getId());
            selecionado.setLiquidacoes(Util.adicionarObjetoEmLista(selecionado.getLiquidacoes(), liq));
        }
    }

    public void recuperarEditarVer() {
        indiceAba = 0;
        buscarTipoContaDespesa();
        if (selecionado.getModalidadeLicitacao() == null) {
            selecionado.setModalidadeLicitacao(ModalidadeLicitacaoEmpenho.SEM_LICITACAO);
        }
        if (selecionado.getUsuarioSistema() == null) {
            selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        }
        if (selecionado.getSaldoDisponivel() == null) {
            selecionado.setSaldoDisponivel(BigDecimal.ZERO);
        }
        buscarExecucaoSolicitacaoEmpenho();
        pertenceEmenda = (selecionado.getEmendas() != null && !selecionado.getEmendas().isEmpty());
        buscarEmpenhoDeRPDoEmpenhoDeOrigem();
    }

    public List<SelectItem> getExtensoesFonteRecurso() {
        return Util.getListSelectItem(facade.getExtensaoFonteRecursoFacade().listaDecrescente());
    }

    @Override
    public void salvar() {
        try {
            validarEmpenho();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarEmpenho(selecionado);
                FacesUtil.addOperacaoRealizada("Empenho:  " + selecionado.toString() + " foi salvo com sucesso.");
                redirecionarParaVer(selecionado);
            } else {
                facade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Empenho " + selecionado.toString() + " foi alterado com sucesso.");
                redirecionarParaLista();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            selecionado.setMovimentoDespesaORC(null);
            logger.debug("salvarNovo {}", e);
            FacesUtil.executaJavaScript("aguarde.hide()");
            facade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getUnidadeOrganizacional());
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            selecionado.setMovimentoDespesaORC(null);
            logger.debug("salvarNovo {}", ex);
            FacesUtil.executaJavaScript("aguarde.hide()");
            facade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getUnidadeOrganizacional());
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        validarEmpenhoRestoPagar(ve);
        validarCampos(ve);
        validarTipoContaDespesaComTipoClassePessoa(ve);
        validarPessoaDuplicada(ve);
        validarEmendas(ve);
        if (isOperacaoNovo()) {
            validarDesdobramentos(ve);
            validarIntegracao(ve);
        }
        ve.lancarException();
    }

    private void validarDesdobramentos(ValidacaoException ve) {
        if (selecionado.getDesdobramentos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário ter pelo menos um detalhamento");
        }
        if (selecionado.isEmpenhoDeObrigacaoPagar() && facade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getBuscarEventoEmpDiferenteObrig()) {
            if (selecionado.getValor().compareTo(selecionado.getValorTotalDetalhamento()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do empenho deve maior ou igual ao total dos detalhamentos.");
            }
        } else {
            if (selecionado.getValor().compareTo(selecionado.getValorTotalDetalhamento()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do empenho deve ser igual ao total dos detalhamentos.");
            }
        }
        ve.lancarException();
        for (DesdobramentoEmpenho desdobramentoEmpenho : selecionado.getDesdobramentos()) {
            if (!selecionado.getTipoContaDespesa().equals(((ContaDespesa) desdobramentoEmpenho.getConta()).getTipoContaDespesa())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de despesa da conta de despesa " + desdobramentoEmpenho.getConta().toString() + " do detalhamento deve ser igual ao tipo de despesa selecionado " + selecionado.getTipoContaDespesa().getDescricao());
            }
            if (getGeradoPorSolicitacao()
                && (selecionado.isEmpenhoIntegracaoContrato() || selecionado.isEmpenhoIntegracaoExecucaoProcesso())
                && selecionado.getSolicitacaoEmpenho().getContaDespesaDesdobrada() != null
                && !desdobramentoEmpenho.getConta().equals(selecionado.getSolicitacaoEmpenho().getContaDespesaDesdobrada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Empenho de integração com o ata/contrato, o desdobramento deve ser o mesmo da integração: " + selecionado.getSolicitacaoEmpenho().getContaDespesaDesdobrada() + ".");
            }
        }
        ve.lancarException();
    }

    private void validarPessoaDuplicada(ValidacaoException ve) {
        if (selecionado.getFornecedor().getCpf_Cnpj() == null || selecionado.getFornecedor().getCpf_Cnpj().trim().equals("")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar um empenho para uma pessoa com CPF/CNPJ vazio.");
        }
        ve.lancarException();
        if (selecionado.getFornecedor() instanceof PessoaJuridica) {
            if (facade.getPessoaFacade().existeMaisPessoaComCnpjAtiva((PessoaJuridica) selecionado.getFornecedor())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa jurídica " + selecionado.getFornecedor().getCpf_Cnpj() + " - " + selecionado.getFornecedor().getNome() + " possui mais de um cadastro ativo. Por favor, encaminhe um email para webpublico@riobranco.ac.gov.br, solicitando a correção do cadastro em duplicidade.");
            }
        } else if (facade.getPessoaFacade().existeMaisPessoaComCpfAtiva((PessoaFisica) selecionado.getFornecedor())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa física " + selecionado.getFornecedor().getCpf_Cnpj() + " - " + selecionado.getFornecedor().getNome() + " possui mais de um cadastro ativo. Por favor, encaminhe um email para webpublico@riobranco.ac.gov.br, solicitando a correção do cadastro em duplicidade.");
        }
        ve.lancarException();
    }

    private void validarEmpenhoRestoPagar(ValidacaoException ve) {
        if (selecionado.isEmpenhoRestoPagar() && selecionado.getEmpenho() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Empenho dever ser informado.");
        }
        ve.lancarException();
    }

    private void validarCampos(ValidacaoException ve) {
        selecionado.realizarValidacoes();
        if (selecionado.getEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Evento Contábil deve ser informado.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor dever ser maior que zero(0).");
        }
        if (selecionado.getTipoContaDespesa().isPessoaEncargos() && selecionado.getSubTipoDespesa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Subtipo de Despesa dever ser informado.");
        }
        if (selecionado.getValorLiquidoEmpenho().compareTo(BigDecimal.ZERO) > 0
            && selecionado.isEmpenhoDeObrigacaoPagar()
            && TipoEmpenho.ORDINARIO.equals(selecionado.getTipoEmpenho())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de empenho não pode ser ordinário, pois o mesmo possui valor de obrigação a pagar e valor do empenho.");
        }
        ve.lancarException();
    }

    private void validarTipoContaDespesaComTipoClassePessoa(ValidacaoException ve) {
        TipoContaDespesa tipoContaDespesa = selecionado.getTipoContaDespesa();

        if (TipoContaDespesa.retornaTipoContaGrupoBem().contains(tipoContaDespesa)) {
            if (!TipoClasseCredor.FORNECEDOR_NACIONAL.equals(selecionado.getClasseCredor().getTipoClasseCredor())
                && !TipoClasseCredor.PRESTADOR_SERVICO.equals(selecionado.getClasseCredor().getTipoClasseCredor())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Quando o tipo de despesa for igual a " + tipoContaDespesa.toString() + " a classe de pessoa deve pertencer ao tipo <b>Fornecedor Nacional ou Prestador de Serviço </b>.");
            }
        }
        if (tipoContaDespesa.isBemImovel()) {
            if (!TipoClasseCredor.INDENIZACAO.equals(selecionado.getClasseCredor().getTipoClasseCredor())
                && !TipoClasseCredor.FORNECEDOR_NACIONAL.equals(selecionado.getClasseCredor().getTipoClasseCredor())
                && !TipoClasseCredor.PRESTADOR_SERVICO.equals(selecionado.getClasseCredor().getTipoClasseCredor())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Quando o tipo de despesa for igual a " + tipoContaDespesa.toString() + " a classe de pessoa deve pertencer ao tipo <b>Fornecedor Nacional, Prestador de Serviço ou Indenização </b>.");
            }
        }
        ve.lancarException();
    }

    public void buscarTiposDeConta() {
        recuperarConfiguracaoContaDespesaTipoPessoa();
        selecionado.setTipoContaDespesa(null);
        selecionado.setSubTipoDespesa(null);
        selecionado.setFonteDespesaORC(null);
        selecionado.getDesdobramentos().removeAll(selecionado.getDesdobramentos());
        buscarTipoContaDespesa();
    }

    public void definirEventoContabil() {
        try {
            if (selecionado.getDespesaORC() != null) {
                selecionado.setEventoContabil(null);
                EventoContabil eventoContabil = facade.buscarEventoContabil(selecionado);
                if (eventoContabil != null) {
                    selecionado.setEventoContabil(eventoContabil);
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void adicionarObrigacaoPagar() {
        try {
            validarAddObrigacaoPagar();
            empenhoObrigacaoPagar.setEmpenho(selecionado);
            empenhoObrigacaoPagar.setObrigacaoAPagar(facade.getObrigacaoAPagarFacade().recuperar(empenhoObrigacaoPagar.getObrigacaoAPagar().getId()));
            Util.adicionarObjetoEmLista(selecionado.getObrigacoesPagar(), empenhoObrigacaoPagar);
            buscarDetalhamentoObrigacaoPagar();
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
            definirEventoContabil();
            novaObrigacaoPagar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerObrigacaoPagar(EmpenhoObrigacaoPagar obj) {
        selecionado.getObrigacoesPagar().remove(obj);
        if (selecionado.getObrigacoesPagar().isEmpty()) {
            selecionado.getDesdobramentos().clear();
            cancelarDesdobramento();
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
            definirEventoContabil();
        }
    }

    public void novaObrigacaoPagar() {
        try {
            validarNovaObrigacaoPagar();
            empenhoObrigacaoPagar = new EmpenhoObrigacaoPagar();
            selecionado.getDesdobramentos().clear();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tabGeralObrigacao:obrigacao-pagar_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovaObrigacaoPagar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo elemento de despesa deve ser informado.");
        }
        if (selecionado.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo fonte de recurso deve ser informado.");
        }
        if (selecionado.getFornecedor() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo pessoa deve ser informado.");
        }
        if (selecionado.getClasseCredor() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo classe deve ser informado.");
        }
        ve.lancarException();
    }

    public void cancelarObrigacaoPagar() {
        empenhoObrigacaoPagar = null;
        selecionado.getDesdobramentos().clear();
        selecionado.getObrigacoesPagar().clear();
        cancelarDesdobramento();
    }

    public boolean desabilitarRemoverObrigacaoPagar() {
        return !selecionado.getDesdobramentos().isEmpty();
    }

    private void validarAddObrigacaoPagar() {
        ValidacaoException ve = new ValidacaoException();
        if (empenhoObrigacaoPagar.getObrigacaoAPagar() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo obrigação a pagar deve ser informado.");
        }
        for (EmpenhoObrigacaoPagar empOB : selecionado.getObrigacoesPagar()) {
            if (empOB.getObrigacaoAPagar().equals(empenhoObrigacaoPagar.getObrigacaoAPagar())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A obrigação a pagar: " + empenhoObrigacaoPagar.getObrigacaoAPagar() + " já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void novoDesdobramento() {
        desdobramento = new DesdobramentoEmpenho();
        if (selecionado.getPropostaConcessaoDiaria() != null) {
            desdobramento.setValor(selecionado.getPropostaConcessaoDiaria().getValor());
        }
    }

    public void novoDesdobramentoObrigacao() {
        if (selecionado.getObrigacoesPagar().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório ter ao menos uma obrigação adicionada na lista de obrigações.");
            return;
        }
        novoDesdobramento();
        buscarDetalhamentoObrigacaoPagar();
    }

    private void buscarDetalhamentoObrigacaoPagar() {
        if (selecionado.isEmpenhoDeObrigacaoPagar()) {
            detalhamentosObrigacaoPagar = Lists.newArrayList();
            for (EmpenhoObrigacaoPagar empenhoObrigacaoPagar : selecionado.getObrigacoesPagar()) {
                ObrigacaoAPagar obrigacaoAPagar = facade.getObrigacaoAPagarFacade().recuperar(empenhoObrigacaoPagar.getObrigacaoAPagar().getId());
                detalhamentosObrigacaoPagar.addAll(facade.getDesdobramentoObrigacaoAPagarFacade().buscarDesdobramentoObrigacaoPagarPorObrigacao(obrigacaoAPagar));
            }
        }
    }

    public void adicionarDetalhamento() {
        try {
            if (selecionado.isEmpenhoDeObrigacaoPagar()) {
                adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoEmpenho();
                validarDetalhamento();
                desdobramento.setEmpenho(selecionado);
                desdobramento.setSaldo(desdobramento.getValor());
                Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
                selecionado.setValor(selecionado.getValorTotalDetalhamento());
                cancelarDesdobramento();
            }
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnAddDetalhamento')");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDetalhamento() {
        ValidacaoException ve = new ValidacaoException();
        validarConta(ve);
        ve.lancarException();
        validarMesmoDetalhamentoEmLista();
        validarSaldoDisponivelPorDetalhamento();
        ve.lancarException();
    }

    private void validarConta(ValidacaoException ve) {
        if (desdobramento.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado");
        }
        if (desdobramento.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");
        }
    }

    private void validarSaldoDisponivelPorDetalhamento() {
        ObrigacaoAPagar obrigacaoAPagar = facade.getObrigacaoAPagarFacade().recuperar(desdobramentoObrigacaoPagar.getObrigacaoAPagar().getId());
        obrigacaoAPagar.validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramentoObrigacaoPagar.getConta());
    }

    private void validarMesmoDetalhamentoEmLista() {
        selecionado.validarMesmoDetalhamentoEmListaPoObrigacaoPagar(desdobramento, desdobramentoObrigacaoPagar.getObrigacaoAPagar());
    }

    private void adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramentoObrigacaoPagar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        if (desdobramentoObrigacaoPagar.getConta() != null) {
            desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoObrigacaoPagar);
            desdobramento.setConta(desdobramentoObrigacaoPagar.getConta());
        }
    }

    public List<DesdobramentoObrigacaoPagar> completarContaDespesaPorObrigacaoPagar(String parte) {
        return facade.getDesdobramentoObrigacaoAPagarFacade().buscarDesdobramentoObrigacaoPagar(
            parte.trim(),
            selecionado.getExercicio(),
            getIdsObrigacaoPagar());
    }

    public List<Conta> completarDesdobramento(String parte) {
        return facade.getContaFacade().buscarContasFilhasDespesaORCPorTipo(
            parte.trim(),
            selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
            selecionado.getExercicio(),
            selecionado.getTipoContaDespesa(),
            false);
    }

    public void adicionarDesdobramento() {
        try {
            validarDesdobramento();
            desdobramento.setEmpenho(selecionado);
            desdobramento.setSaldo(desdobramento.getValor());
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            selecionado.setValor(selecionado.getValorTotalDetalhamento());
            cancelarDesdobramento();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo elemento de despesa deve ser informado.");
        }
        if (selecionado.getTipoContaDespesa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo tipo conta de despesa deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarDesdobramento() {
        validarNovoDesdobramento();
        ValidacaoException ve = new ValidacaoException();
        desdobramento.realizarValidacoes();
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        for (DesdobramentoEmpenho desd : selecionado.getDesdobramentos()) {
            if (!desd.equals(desdobramento) && desd.getConta().equals(desdobramento.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de despesa já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    private List<Long> getIdsObrigacaoPagar() {
        List<Long> toReturn = new ArrayList<>();
        for (EmpenhoObrigacaoPagar liqOp : selecionado.getObrigacoesPagar()) {
            toReturn.add(liqOp.getObrigacaoAPagar().getId());
        }
        return toReturn;
    }

    public boolean isMesmaContaDespesaPorObrigacaoPagar(DesdobramentoObrigacaoPagar desdObrigacao) {
        return desdobramentoObrigacaoPagar != null
            && desdObrigacao.getConta().equals(desdobramentoObrigacaoPagar.getConta())
            && desdObrigacao.getObrigacaoAPagar().equals(desdobramentoObrigacaoPagar.getObrigacaoAPagar());

    }

    public void definirValorDetalhamento() {
        if (desdobramentoObrigacaoPagar != null) {
            for (DesdobramentoObrigacaoPagar desdObrigacao : detalhamentosObrigacaoPagar) {
                if (desdObrigacao.getConta().equals(desdobramentoObrigacaoPagar.getConta())
                    && desdObrigacao.getObrigacaoAPagar().equals(desdobramentoObrigacaoPagar.getObrigacaoAPagar())) {
                    desdobramento.setValor(desdObrigacao.getSaldo());
                    desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoObrigacaoPagar);
                }
            }
        }
    }

    public void cancelarDesdobramento() {
        desdobramento = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:btnNovoDesdobramento')");
    }

    public void cancelarDesdobramentoObrigacao() {
        desdobramento = null;
        desdobramentoObrigacaoPagar = null;
        selecionado.setValor(BigDecimal.ZERO);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:btnNovaConta')");
    }

    public void removerDesdobramento(DesdobramentoEmpenho obj) {
        try {
            validarDesdobramentoIntegracao();
            selecionado.getDesdobramentos().remove(obj);
            selecionado.setValor(selecionado.getValorTotalDetalhamento());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarDesdobramentoIntegracao() {
        ValidacaoException ve = new ValidacaoException();
        if (getGeradoPorSolicitacao()
            && (selecionado.isEmpenhoIntegracaoContrato() || selecionado.isEmpenhoIntegracaoExecucaoProcesso())
            && (selecionado.getTipoContaDespesa().isBemMovel() || selecionado.getTipoContaDespesa().isEstoque())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido alterar o desdobramento quando o empenho é de integração com o ata/contrato de " + selecionado.getTipoContaDespesa().getDescricao() + ".");
        }
        ve.lancarException();
    }

    public void editarDesdobramento(DesdobramentoEmpenho obj) {
        try {
            validarDesdobramentoIntegracao();
            this.desdobramento = (DesdobramentoEmpenho) Util.clonarObjeto(obj);
            assert this.desdobramento != null;
            if (this.desdobramento.getDesdobramentoObrigacaoPagar() != null) {
                desdobramentoObrigacaoPagar = desdobramento.getDesdobramentoObrigacaoPagar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public Exercicio getExercicioPorAno(String ano) {
        return facade.getExercicioFacade().getExercicioPorAno(Integer.parseInt(ano));
    }

    public List<SubTipoDespesa> getSubTipoContas() {

        List<SubTipoDespesa> toReturn = new ArrayList<>();
        if (selecionado != null) {
            TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
            if (tipo.isPessoaEncargos()) {
                toReturn.add(SubTipoDespesa.RGPS);
                toReturn.add(SubTipoDespesa.RPPS);

            } else if (tipo.isDividaPublica() || tipo.isPrecatorio()) {
                toReturn.add(SubTipoDespesa.JUROS);
                toReturn.add(SubTipoDespesa.OUTROS_ENCARGOS);
                toReturn.add(SubTipoDespesa.VALOR_PRINCIPAL);

            } else {
                for (SubTipoDespesa std : SubTipoDespesa.values()) {
                    toReturn.add(std);
                }
            }
        }
        return toReturn;
    }

    private void recuperarSolicitacoesPendentes() {
        solicitacoesPendentes = facade.getSolicitacaoEmpenhoFacade().buscarSolicitacoesPendentesPorUnidade(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public void empenharSolicitacao(ActionEvent evt) {
        try {
            selecionado = new Empenho();
            parametrosIniciais();

            SolicitacaoEmpenho se = (SolicitacaoEmpenho) evt.getComponent().getAttributes().get("empenhar");
            if (se != null) {
                se.setEmpenho(selecionado);
                selecionado.setSolicitacaoEmpenho(se);
                selecionado.setComplementoHistorico(se.getComplementoHistorico());
                selecionado.setFornecedor(se.getFornecedor());
                selecionado.setClasseCredor(se.getClasseCredor());
                selecionado.setHistoricoContabil(se.getHistoricoContabil());
                selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
                selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
                selecionado.setValor(se.getValor());
                selecionado.setSaldo(se.getValor());
                if (se.getFonteDespesaORC() != null && selecionado.getExercicio().equals(se.getFonteDespesaORC().getDespesaORC().getExercicio())) {
                    selecionado.setFonteDespesaORC(se.getFonteDespesaORC());
                    adicionarDespesaOrc(se);
                }
                buscarTipoContaDespesa();
                definirVariaveisAoEmpenhoPorSolicitacao(se, selecionado);
                definirEventoContabil();
                buscarDetalhamentoFonte();
                solicitacoesPendentes.remove(se);
                recuperarFormaEntregaExecucao(se);
                adicionarDocumentoPorSolicitacaoEmpenho(se);
                direcionarParaAbaEmpenho();
                buscarExecucaoSolicitacaoEmpenho();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            indiceAba = 5;
        } catch (Exception e) {
            logger.debug("EmpenharSolicitacaoEmpenho {}", e);
        }
    }

    private void adicionarDocumentoPorSolicitacaoEmpenho(SolicitacaoEmpenho se) {
        selecionado.setDesdobramentos(Lists.newArrayList());
        if (se.getContaDespesaDesdobrada() != null) {
            novoDesdobramento();
            desdobramento.setEmpenho(selecionado);
            desdobramento.setValor(se.getValor());
            desdobramento.setConta(se.getContaDespesaDesdobrada());
            desdobramento.setSaldo(desdobramento.getValor());
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            selecionado.setValor(selecionado.getValorTotalDetalhamento());
            cancelarDesdobramento();
        }
    }

    public Boolean getBloquearNovoDesdobramento() {
        return selecionado.getDespesaORC() == null || isOperacaoEditar() || (facade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getBloquearUmDesdobramento() && !selecionado.getDesdobramentos().isEmpty());
    }

    private void recuperarFormaEntregaExecucao(SolicitacaoEmpenho se) {
        if (selecionado.isEmpenhoIntegracaoContrato()) {
            formaEntregaExecucao = facade.getExecucaoContratoFacade().buscarFormaEntregaExecucao(se);
        } else if (selecionado.isEmpenhoIntegracaoExecucaoProcesso()) {
            formaEntregaExecucao = facade.getExecucaoProcessoFacade().buscarFormaEntregaExecucao(se);
        }
    }

    public Boolean bloquearClassePessoaPelaSolicitacaoEmpenho() {
        return selecionado.getSolicitacaoEmpenho() != null &&
            selecionado.getSolicitacaoEmpenho().getClasseCredor() != null &&
            selecionado.getClasseCredor() != null;
    }

    private void variaveisExecucaoProcesso(SolicitacaoEmpenho se) {
        if (se != null && se.getOrigemSolicitacaoEmpenho() != null && se.getOrigemSolicitacaoEmpenho().isOrigemExecucaoProcesso()) {
            ExecucaoProcessoEmpenho execProcEmp = facade.getExecucaoProcessoFacade().buscarExecucaoProcessoEmpenhoPorSolicitacaoEmpenho(se);
            if (execProcEmp == null) {
                FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar a integração com a execução do processo de " + se.getOrigemSolicitacaoEmpenho().getDescricao() + ".");
                return;
            }
            selecionado.setExecucaoProcesso(execProcEmp.getExecucaoProcesso());
            selecionado.setModalidadeLicitacao(ModalidadeLicitacaoEmpenho.getModalidadeLicitacaoEmpenhoPelaModalidadeLicitacao(
                execProcEmp.getExecucaoProcesso().getModalidadeProcesso(),
                execProcEmp.getExecucaoProcesso().getNaturezaProcesso()));
        }
    }

    private void variaveisContrato(SolicitacaoEmpenho se) {
        if (se != null
            && se.getContrato() != null) {
            selecionado.setModalidadeLicitacao(ModalidadeLicitacaoEmpenho.getModalidadeLicitacaoEmpenhoPelaModalidadeLicitacao(
                se.getContrato().getModalidadeLicitacao(),
                se.getContrato().getTipoNaturezaDoProcedimentoLicitacao()));

            selecionado.setContrato(se.getContrato());
        }
    }

    private void definirVariaveisAoEmpenhoPorSolicitacao(SolicitacaoEmpenho se, Empenho e) {
        variaveisContrato(se);
        variaveisExecucaoProcesso(se);
        variaveisTipoContaDespesa();
        variaveisDiaria(se, e);
        variaveisConvenioDespesa(se, e);
        variaveisReconhecimentoDivida(se, e);
    }

    private void variaveisReconhecimentoDivida(SolicitacaoEmpenho se, Empenho e) {
        if (se.getReconhecimentoDivida() != null) {
            e.setReconhecimentoDivida(se.getReconhecimentoDivida());
        }
    }

    private void adicionarDespesaOrc(SolicitacaoEmpenho se) {
        if (se.getDespesaORC() != null) {
            selecionado.setDespesaORC(se.getDespesaORC());
        }
    }

    private void variaveisDiaria(SolicitacaoEmpenho se, Empenho e) {
        if (selecionado.getTipoContaDespesa() != null) {
            if (TipoContaDespesa.retornaTipoContaConcessaoDiaria().contains(selecionado.getTipoContaDespesa())) {
                DiariaCivilSolicitacaoEmpenho diariaCivilSolicitacaoEmpenho = facade.getSolicitacaoEmpenhoFacade().recuperarDiariaCivilSolicitacaoEmpenhoApartirSolicitacaoEmpenho(se);
                if (diariaCivilSolicitacaoEmpenho != null) {
                    e.setPropostaConcessaoDiaria(diariaCivilSolicitacaoEmpenho.getPropostaConcessaoDiaria());

                    if (diariaCivilSolicitacaoEmpenho.getPropostaConcessaoDiaria() != null
                        && diariaCivilSolicitacaoEmpenho.getPropostaConcessaoDiaria().getDesdobramentos() != null
                        && !diariaCivilSolicitacaoEmpenho.getPropostaConcessaoDiaria().getDesdobramentos().isEmpty()) {
                        for (DesdobramentoPropostaConcessao desdobramentoPC : diariaCivilSolicitacaoEmpenho.getPropostaConcessaoDiaria().getDesdobramentos()) {
                            DesdobramentoEmpenho d = new DesdobramentoEmpenho();
                            d.setConta(desdobramentoPC.getConta());
                            d.setValor(desdobramentoPC.getValor());
                            d.setSaldo(desdobramentoPC.getSaldo());
                            d.setEmpenho(selecionado);
                            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), d);
                            selecionado.setValor(selecionado.getValorTotalDetalhamento());
                        }
                    }
                }
            }
        }
    }

    private void variaveisDividaPublica(SolicitacaoEmpenho sol, Empenho emp) {

        if (selecionado.getTipoContaDespesa() != null) {
            if (selecionado.getTipoContaDespesa().isDividaPublica() || selecionado.getTipoContaDespesa().isPrecatorio()) {
                DividaPublicaSolicitacaoEmpenho solicitacaoDividaPublica = buscarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(sol);
                if (solicitacaoDividaPublica != null) {
                    if (solicitacaoDividaPublica.getDividaPublica() != null) {
                        emp.setDividaPublica(solicitacaoDividaPublica.getDividaPublica());
                    }
                    if (solicitacaoDividaPublica.getParcelaDividaPublica() != null) {
                        emp.setParcelaDividaPublica(solicitacaoDividaPublica.getParcelaDividaPublica());
                    }
                    if (solicitacaoDividaPublica.getOperacaoDeCredito() != null) {
                        emp.setOperacaoDeCredito(solicitacaoDividaPublica.getOperacaoDeCredito());
                    }
                    emp.setSubTipoDespesa(solicitacaoDividaPublica.getSubTipoDespesa());
                    emp.setSolicitacaoEmpenho(solicitacaoDividaPublica.getSolicitacaoEmpenho());
                }
            }
        }
    }

    private DividaPublicaSolicitacaoEmpenho buscarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        return facade.getSolicitacaoEmpenhoFacade().recuperarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(solicitacaoEmpenho);
    }

    private void variaveisConvenioDespesa(SolicitacaoEmpenho sol, Empenho emp) {
        if (selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isConvenioDespesa()) {
            ConvenioDespSolicEmpenho convenioDespSolicEmpenho = facade.getSolicitacaoEmpenhoFacade().recuperarConvenioDespesaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(sol);
            if (convenioDespSolicEmpenho != null) {
                emp.setConvenioDespesa(convenioDespSolicEmpenho.getConvenioDespesa());
                emp.setSubTipoDespesa(convenioDespSolicEmpenho.getSubTipoDespesa());
                emp.setSolicitacaoEmpenho(convenioDespSolicEmpenho.getSolicitacaoEmpenho());
            }
        }
    }

    private void variaveisTipoContaDespesa() {
        if (selecionado.getTipoContaDespesa() == null) {
            ContaDespesa contaDespesaSelecionada = getContaDespesaSelecionada();
            if (contaDespesaSelecionada != null) {
                selecionado.setTipoContaDespesa(contaDespesaSelecionada.getTipoContaDespesa());
                if (selecionado.getTipoContaDespesa().isNaoAplicavel()) {
                    try {
                        selecionado.setTipoContaDespesa(getTipoContas().get(0));
                        alterarTipoDespesaSeForSuprimentoDeFundo();
                    } catch (Exception e) {
                        logger.debug(e.getMessage());
                    }
                }
            }
        }
    }

    private void alterarTipoDespesaSeForSuprimentoDeFundo() {
        if (selecionado.getClasseCredor() != null && selecionado.getClasseCredor().getTipoClasseCredor().equals(TipoClasseCredor.SUPRIMENTO_FUNDO)) {
            for (TipoContaDespesa tipoContaDespesa : getTipoContas()) {
                if (tipoContaDespesa.isSuprimentoFundo()) {
                    selecionado.setTipoContaDespesa(tipoContaDespesa);
                    break;
                }
            }
        }
    }

    public BigDecimal getCalculaValorLiquidado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!selecionado.getLiquidacoes().isEmpty()) {
            for (Liquidacao li : selecionado.getLiquidacoes()) {
                valor = valor.add(li.getValor());
            }
            return valor.subtract(getCalculaEstornosLiquidacoes());
        }
        return valor;
    }

    public BigDecimal getCalculaValorPago() {
        BigDecimal valor = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            List<Pagamento> pagamentos = facade.getPagamentoFacade().listaPorEmpenho(selecionado);
            for (Pagamento p : pagamentos) {
                valor = valor.add(p.getValor());
            }
        }
        return valor.subtract(getCalculaEstornosPagamentos());
    }

    public BigDecimal getCalculaSaldoPagar() {
        Empenho e = ((Empenho) selecionado);
        return e.getSaldo().subtract(getCalculaValorPago());
    }

    public BigDecimal getCalculaEstornosEmpenhos() {
        BigDecimal estornoEmpenho = new BigDecimal(BigInteger.ZERO);
        for (EmpenhoEstorno ee : ((Empenho) selecionado).getEmpenhoEstornos()) {
            estornoEmpenho = estornoEmpenho.add(ee.getValor());
        }
        return estornoEmpenho;
    }

    public BigDecimal getCalculaLiquidacoes() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (Liquidacao liq : getListaLiquidacoes()) {
            total = total.add(liq.getValor());
        }
        return total;
    }

    public BigDecimal getCalculaPagamentos() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (Pagamento pag : getPagamentos()) {
            total = total.add(pag.getValor());
        }
        return total;
    }

    private List<Liquidacao> getListaLiquidacoes() {
        if (selecionado.getId() != null) {
            return facade.buscarLiquidacoesPorEmpenho(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public BigDecimal getCalculaEstornosLiquidacoes() {
        BigDecimal estornoLiqui = new BigDecimal(BigInteger.ZERO);
        if (selecionado.getId() != null) {
            for (LiquidacaoEstorno le : facade.getLiquidacaoEstornoFacade().listaPorEmpenho(selecionado)) {
                estornoLiqui = estornoLiqui.add(le.getValor());
            }
        }
        return estornoLiqui;
    }

    public BigDecimal getCalculaEstornosPagamentos() {
        BigDecimal estornoPagto = new BigDecimal(BigInteger.ZERO);
        if (selecionado.getId() != null) {
            for (PagamentoEstorno pe : facade.getPagamentoEstornoFacade().listaPorEmpenho(selecionado)) {
                estornoPagto = estornoPagto.add(pe.getValor());
            }
        }
        return estornoPagto;
    }

    public BigDecimal calculaSaldoAtual() {
        BigDecimal saldoAtual = BigDecimal.ZERO;
        if (selecionado.getId() == null) {
            saldoAtual = getSaldoFonteDespesaORC().subtract(selecionado.getValor());
        } else {
            saldoAtual = selecionado.getSaldoDisponivel().subtract(selecionado.getValor());
        }
        return saldoAtual;
    }

    public BigDecimal getSaldoCotaOrcamentaria() {
        BigDecimal saldoAtual = BigDecimal.ZERO;
        try {
            if (selecionado != null && selecionado.getFonteDespesaORC() != null) {
                Calendar data = Calendar.getInstance();
                data.setTime(selecionado.getDataEmpenho());
                int mes = (data.get(Calendar.MONTH)) + 1;
                saldoAtual = facade.getCotaOrcamentariaFacade().buscarValorDaCotaPorFonteDespesaORC(selecionado.getFonteDespesaORC(), mes);
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar recuperar o Saldo dispónivel da Cota Orçamentária.");
        }
        return saldoAtual;
    }

    public BigDecimal calculaSaldoAnterior() {
        BigDecimal saldoAnterior = BigDecimal.ZERO;
        if (selecionado.getId() == null) {
            saldoAnterior = getSaldoFonteDespesaORC();
        } else {
            saldoAnterior = selecionado.getSaldoDisponivel();
        }
        return saldoAnterior;
    }

    private BigDecimal getSaldoFonteDespesaORC() {
        try {
            return facade.getSaldoFonteDespesaORC(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public void validaSaldoFonteDespesa(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        BigDecimal saldo = BigDecimal.ZERO;
        BigDecimal valor = (BigDecimal) value;
        try {
            saldo = facade.getSaldoFonteDespesaORC(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Erro ao recuperar o saldo da Cota Orçamentária: " + ex.getMessage());
        }

        if (selecionado.getFonteDespesaORC() != null) {
            if (valor.compareTo(saldo) > 0) {
                message.setSummary(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao());
                message.setDetail(" O saldo disponível na Fonte de Despesa é: " + Util.formataValor(saldo));
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (selecionado.getDataEmpenho() != null) {
                Calendar data = Calendar.getInstance();
                data.setTime(selecionado.getDataEmpenho());
                int mes = (data.get(Calendar.MONTH)) + 1;
                try {
                    if (!facade.getCotaOrcamentariaFacade().validarValorUtilizadoCotaOrcamentaria(selecionado.getFonteDespesaORC(), mes, valor)) {
                        CotaOrcamentaria cotaOrcamentaria = facade.getCotaOrcamentariaFacade().listaPorFonteDespesaOrcEIndice(selecionado.getFonteDespesaORC(), mes);
                        String msgCotaOrcamentaria = " Saldo indisponível para o Grupo Orçamentário ";
                        if (cotaOrcamentaria != null) {
                            msgCotaOrcamentaria += cotaOrcamentaria.getGrupoCotaORC().getGrupoOrcamentario() + " e Fonte: " + selecionado.getFonteDespesaORC().getDescricaoFonteDeRecurso();
                        }
                        message.setSummary(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao());
                        message.setDetail(msgCotaOrcamentaria);
                        message.setSeverity(FacesMessage.SEVERITY_ERROR);
                        throw new ValidatorException(message);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
                }
            }
        }
    }

    public void definirSubTipoDespesaPorTipoDespesa() {
        if (selecionado.getSolicitacaoEmpenho() != null) {
            variaveisDividaPublica(selecionado.getSolicitacaoEmpenho(), selecionado);
        }
        TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
        if (!tipo.isPessoaEncargos() && !tipo.isDividaPublica() && !tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
            definirEventoContabil();
        } else if (tipo.isPessoaEncargos() || tipo.isDividaPublica() || tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(null);
        }
    }

    public void definirSubTipoDespesa(AjaxBehaviorEvent event) {
        SubTipoDespesa std = (SubTipoDespesa) ((UIOutput) event.getSource()).getValue();
        selecionado.setSubTipoDespesa(std);
        definirEventoContabil();
    }

    public void definirNullParaClasseCredor() {
        selecionado.setClasseCredor(null);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:classeCredor')");
    }

    public Boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    public Boolean bloquearNovaObrigacao() {
        return isOperacaoEditar() || (facade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getBloquearUmDesdobramento() && !selecionado.getObrigacoesPagar().isEmpty());
    }

    public Boolean bloquearValor() {
        return isOperacaoEditar() || getGeradoPorSolicitacao() || (selecionado.getObrigacoesPagar().isEmpty() && !selecionado.getDesdobramentos().isEmpty());
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent ac) {
        objetoPesquisaGenerica = ac.getComponent().getAttributes().get("objeto");
        if (objetoPesquisaGenerica.getClass().equals(PessoaFisica.class) || objetoPesquisaGenerica.getClass().equals(PessoaJuridica.class)) {
            selecionado.setFornecedor((Pessoa) objetoPesquisaGenerica);
            objetoPesquisaGenerica = null;
        } else {
            for (Field f : Persistencia.getAtributos(selecionado.getClass())) {
                f.setAccessible(true);
                if (f.getType().equals(objetoPesquisaGenerica.getClass())) {
                    try {
                        f.set(selecionado, objetoPesquisaGenerica);
                    } catch (Exception e) {
                        logger.debug(e.getMessage());
                    }
                }
            }
        }
    }

    public Boolean renderizaSubTipoDespesa() {
        return selecionado.getTipoContaDespesa() != null && TipoContaDespesa.retornaTipoContaDividaPublica().contains(selecionado.getTipoContaDespesa());
    }

    public Boolean mostrarPanelIntegracaoConvenioDespesa() {
        return (selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isConvenioDespesa()) || selecionado.getConvenioDespesa() != null;
    }

    public Boolean mostrarPainelIntegracaoConvenioReceita() {
        return getFonteRecursoSelecionada() != null && TipoFonteRecurso.CONVENIO_RECEITA.equals(getFonteRecursoSelecionada().getTipoFonteRecurso());
    }

    public Boolean mostraPainelIntegracaoOperacaoCredito() {
        return getFonteRecursoSelecionada() != null && TipoFonteRecurso.OPERACAO_CREDITO.equals(getFonteRecursoSelecionada().getTipoFonteRecurso());
    }

    public Boolean mostrarPainelIntegracaoDividaPublica() {
        return selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isDividaPublica();
    }

    public Boolean mostraPainelIntegracaoPrecatorios() {
        return selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isPrecatorio();
    }

    public Boolean mostraPainelIntegracaoFolhaPagamento() {
        return selecionado.getFolhaDePagamento() != null;
    }

    public Boolean mostraPainelIntegracaoSuprimentoFundo() {
        return selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isSuprimentoFundo();
    }

    public Boolean mostraPainelIntegracaoDiariaCivil() {
        return selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isDiariaCivil();
    }

    public Boolean mostraPainelIntegracaoDiariaCampo() {
        return selecionado.getTipoContaDespesa() != null && selecionado.getTipoContaDespesa().isDiariaCampo();
    }

    private ContaDespesa getContaDespesaSelecionada() {
        try {
            return (ContaDespesa) selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
        } catch (Exception e) {
            return null;
        }
    }

    public FonteDeRecursos getFonteRecursoSelecionada() {
        try {
            return ((ContaDeDestinacao) selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos();
        } catch (Exception exception) {
            return null;
        }
    }

    private void validarIntegracao(ValidacaoException ve) throws ExcecaoNegocioGenerica {
        TipoContaDespesa tipoContaDespesa = selecionado.getTipoContaDespesa();
        FonteDeRecursos fonte = getFonteRecursoSelecionada();
        TipoFonteRecurso tipoFonteRecurso = fonte.getTipoFonteRecurso();
        if (fonte.getTipoFonteRecurso() == null) {
            throw new ExcecaoNegocioGenerica("O atributo tipo de fonte de recurso da ronte de recurso selecionada não foi encontrado.");
        }
        validarIntegracaoDividaPublica(ve, tipoContaDespesa, tipoFonteRecurso);
        validarIntegracaoConvenios(ve, tipoContaDespesa, tipoFonteRecurso);
        validarIntegracaoConcessaoDiarias(ve, tipoContaDespesa);
        validarIntegracaoContrato(ve);
        validarIntegracaoAtributosComuns(tipoContaDespesa, ve);
        ve.lancarException();
    }

    private void validarIntegracaoConcessaoDiarias(ValidacaoException ve, TipoContaDespesa tipoContaDespesa) {
        if (tipoContaDespesa.isPropostaConcessaoDiaria()) {
            if (selecionado.getPropostaConcessaoDiaria() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + tipoContaDespesa.getDescricao() + " da aba de Integração deve ser informado.");
            }
            ve.lancarException();
            if (selecionado.getValor().compareTo(selecionado.getPropostaConcessaoDiaria().getValor()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do empenho <b>(" + Util.formataValor(selecionado.getValor()) + ")</b> deve ser igual ao valor da " + tipoContaDespesa.getDescricao() + " <b>(" + Util.formataValor(selecionado.getPropostaConcessaoDiaria().getValor()) + ")</b>.");
            }
            ve.lancarException();
        }
    }

    private void validarIntegracaoConvenios(ValidacaoException ve, TipoContaDespesa tipoContaDespesa, TipoFonteRecurso tipoFonteRecurso) {
        validaIntegracaoConveioReceita(tipoFonteRecurso, ve);
        validarIntegracaoConveioDespesa(tipoContaDespesa, ve);
    }

    private void validarIntegracaoDividaPublica(ValidacaoException ve, TipoContaDespesa tipoContaDespesa, TipoFonteRecurso tipoFonteRecurso) {
        validarIntegracaoPrecatorios(tipoContaDespesa, ve);
        validarIntegracaoDividaPublica(tipoContaDespesa, ve);
        validarIntegracaoOperacaoCredito(tipoFonteRecurso, ve);
    }

    private void validarIntegracaoContrato(ValidacaoException ve) {
        validarTipoContaBens(ve);
        validarSolicitacoesComContrato(ve);
        if (selecionado.getContrato() != null && selecionado.getSolicitacaoEmpenho() != null) {
            validarIntegracaoAtributosComunsPessoa(selecionado.getFornecedor(), selecionado.getContrato().getContratado(), " do contrato");
            if (selecionado.getSolicitacaoEmpenho().getValor().compareTo(selecionado.getValor()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do empenho é diferente do valor da solicitação." +
                    " <b> Valor da Solicitação: " + Util.formataValor(selecionado.getSolicitacaoEmpenho().getValor()) + ". </b>");
            }
        }
        ve.lancarException();
        for (DesdobramentoEmpenho desdobramentoEmpenho : selecionado.getDesdobramentos()) {
            if ((selecionado.getTipoContaDespesa().isEstoque() || selecionado.getTipoContaDespesa().isServicoTerceiro()) &&
                !facade.hasConfiguracaoExcecaoVigente(selecionado.getDataEmpenho(), desdobramentoEmpenho.getConta()) &&
                selecionado.getSolicitacaoEmpenho() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Empenho de " + selecionado.getTipoContaDespesa().getDescricao() + " para o desdobramento " + desdobramentoEmpenho.getConta() + " é necessário vinculo com Contrato.");
            }
        }
        ve.lancarException();
    }

    private void validarTipoContaBens(ValidacaoException ve) {
        if ((selecionado.getTipoContaDespesa().isBemMovel() ||
            selecionado.getTipoContaDespesa().isBemIntangivel()) &&
            !selecionado.isEmpenhoIntegracaoReconhecimentoDivida() &&
            selecionado.getExecucaoProcesso() == null &&
            selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato é obrigatório quando o tipo de despesa for  " + selecionado.getTipoContaDespesa().getDescricao() + " .");
        }
    }

    private void validarSolicitacoesComContrato(ValidacaoException ve) {
        if (selecionado.getContrato() != null && selecionado.getSolicitacaoEmpenho() == null) {
            List<SolicitacaoEmpenho> solicitacoes = facade.getSolicitacaoEmpenhoFacade().buscarSolicitacoesPendentesPorUnidadeAndContrato(selecionado.getUnidadeOrganizacional(), selecionado.getContrato());
            if (!solicitacoes.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) solicitação(ões) de empenho para este contrato. Selecione uma solicitação.");
            }
        }
        ve.lancarException();
    }

    private void validarIntegracaoOperacaoCredito(TipoFonteRecurso tipoFonteRecurso, ValidacaoException ve) {
        if (TipoFonteRecurso.OPERACAO_CREDITO.equals(tipoFonteRecurso)) {
            if (selecionado.getOperacaoDeCredito() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar uma Operação de Crédito na aba Integração.");
            }
            ve.lancarException();
            NaturezaDividaPublica naturezaDividaPublica = selecionado.getOperacaoDeCredito().getNaturezaDividaPublica();
            if (!NaturezaDividaPublica.OPERACAO_CREDITO.equals(naturezaDividaPublica)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Por favor informar uma Dívida Pública na aba Integração, com a natureza da dívida: " + NaturezaDividaPublica.OPERACAO_CREDITO.getDescricao() + ".");
            }
            ve.lancarException();
        }
    }

    private void validarIntegracaoPrecatorios(TipoContaDespesa tipoContaDespesaSelecionado, ValidacaoException ve) {
        if (TipoContaDespesa.PRECATORIO.equals(tipoContaDespesaSelecionado)) {
            if (selecionado.getDividaPublica() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar uma Dívida Pública na aba Integração.");
            }
            ve.lancarException();
            NaturezaDividaPublica naturezaDividaPublica = selecionado.getDividaPublica().getNaturezaDividaPublica();
            if (!NaturezaDividaPublica.PRECATORIO.equals(naturezaDividaPublica)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Por favor informar uma Dívida Pública na aba Integração, com a Natureza da Dívida Pública como : " + NaturezaDividaPublica.PRECATORIO.getDescricao() + ".");
            }
            ve.lancarException();
            validarFonteDaDividaPublica(ve, TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, selecionado.getDividaPublica());
        }
    }

    private void validarIntegracaoDividaPublica(TipoContaDespesa tipoContaDespesa, ValidacaoException ve) {
        if (tipoContaDespesa.isDividaPublica()) {
            if (selecionado.getDividaPublica() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar uma Dívida Pública na aba Integração.");
            }
            ve.lancarException();
            NaturezaDividaPublica naturezaDividaPublica = selecionado.getDividaPublica().getNaturezaDividaPublica();
            if (NaturezaDividaPublica.PRECATORIO.equals(naturezaDividaPublica)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Por favor informar uma Dívida Pública na aba Integração, com a Natureza diferente de " + NaturezaDividaPublica.PRECATORIO.getDescricao() + ".");
            }
            ve.lancarException();
            validarFonteDaDividaPublica(ve, TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, selecionado.getDividaPublica());
        }
    }

    private void validarFonteDaDividaPublica(ValidacaoException ve, TipoValidacao tipoValidacao, DividaPublica dividaPublica) {
        if (!facade.getDividaPublicaFacade().hasFonteConfiguradaNaDividaPublica(dividaPublica, tipoValidacao, facade.getSistemaFacade().getExercicioCorrente(), selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Fonte de Recurso selecionada não está configurada no cadastro da dívida pública " + dividaPublica.getNumero() + " para o tipo de movimento '" + tipoValidacao.getDescricao() + "'.");
        }
        ve.lancarException();
    }

    private void validaIntegracaoConveioReceita(TipoFonteRecurso tipoFonteRecurso, ValidacaoException ve) throws ExcecaoNegocioGenerica {
        if (TipoFonteRecurso.CONVENIO_RECEITA.equals(tipoFonteRecurso) && selecionado.getConvenioReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar um Convênio de Receita na aba Integração.");
        }
        ve.lancarException();
    }

    private void validarIntegracaoConveioDespesa(TipoContaDespesa tipoContaDespesa, ValidacaoException ve) {
        if (tipoContaDespesa.isConvenioDespesa() && selecionado.getConvenioDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" Por favor informar um Convênio de Despesa na aba Integração.");
        }
        if (selecionado.getValor() != null && selecionado.getConvenioDespesa() != null) {
            BigDecimal valorJaEmpenhado = facade.getConvenioDespesaFacade().buscarValorEmpenhadoPorConvenioDeDespesa(selecionado.getConvenioDespesa());
            BigDecimal valorConvenioSelecionado = selecionado.getConvenioDespesa().getValorConvenio() != null ? selecionado.getConvenioDespesa().getValorConvenio() : BigDecimal.ZERO;
            BigDecimal valorTotalConvenio = valorJaEmpenhado.add(valorConvenioSelecionado);
            if (selecionado.getValor().compareTo(valorTotalConvenio) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do empenho <b>" + Util.formataValor(selecionado.getValor()) +
                    "</b> deve ser menor ou igual ao valor disponível do convênio de <b>" + Util.formataValor(valorConvenioSelecionado.subtract(valorJaEmpenhado)) + "</b>.");
            }
        }
    }

    private void validarIntegracaoAtributosComunsPessoa(Pessoa pessoaEmpenho, Pessoa pessoaVinculo, String mensagemValidacao) {
        ValidacaoException ve = new ValidacaoException();
        if (pessoaEmpenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi informada a pessoa no empenho.");
        }
        if (pessoaVinculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi informada a pessoa na(o) " + mensagemValidacao + ".");
        }
        ve.lancarException();
        if (!pessoaEmpenho.equals(pessoaVinculo)) {
            throw new ExcecaoNegocioGenerica("A Pessoa " + pessoaEmpenho.getNomeCpfCnpj() + " do empenho é diferente da Pessoa " + pessoaVinculo.getNomeCpfCnpj() + mensagemValidacao + ".");
        }
        ve.lancarException();
    }

    private void validarIntegracaoAtributosComunsValor(BigDecimal valorEmpenho, BigDecimal valorVinculo, String mensagemValidacao, ValidacaoException ve) {
        if (valorVinculo == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da integração não foi informado.");
        }
        ve.lancarException();
        if (valorVinculo != null && valorVinculo.compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da integração deve ser maior que zero(0) para a(o) " + mensagemValidacao + ".");
        }
        if (valorEmpenho.compareTo(valorVinculo) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor de " + Util.formataValor(valorEmpenho) + " do " + getTituloEmpenho() + " é maior que de " + Util.formataValor(valorVinculo) + mensagemValidacao + ".");
        }
        ve.lancarException();
    }

    private void validarIntegracaoAtributosComunsClasseCredor(ClasseCredor classeEmpenho, ClasseCredor classeVinculo, String mensagemValidacao, ValidacaoException ve) {
        if (classeVinculo == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informada a classe na() " + mensagemValidacao + ".");
        }
        ve.lancarException();
        if (!classeEmpenho.equals(classeVinculo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Classe " + classeEmpenho.toString() + " do " + getTituloEmpenho() + " é diferente da Classe " + classeVinculo.toString() + mensagemValidacao + ".");
        }
        ve.lancarException();
    }


    private void validarIntegracaoAtributosComunsDespesaORC(DespesaORC despesaOrcEmpenho, DespesaORC despesaOrcVinculo, String mensagemValidacao, ValidacaoException ve) {
        if (despesaOrcVinculo == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informada o elemento de despesa na() " + mensagemValidacao + ".");
        }
        ve.lancarException();
        if (!despesaOrcEmpenho.equals(despesaOrcVinculo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Despesa " + despesaOrcEmpenho.toString() + " do " + getTituloEmpenho() + " é diferente da Despesa " + despesaOrcVinculo.toString() + mensagemValidacao + ".");
        }
        ve.lancarException();
    }

    private void validarIntegracaoAtributosComunsFonteRecursos(FonteDespesaORC fonteEmpenho, FonteDespesaORC fonteVinculo, String mensagemValidacao, ValidacaoException ve) {
        if (fonteVinculo == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informada a fonte de recurso na() " + mensagemValidacao + ".");
        }
        ve.lancarException();
        if (!fonteEmpenho.equals(fonteVinculo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Fonte de Recurso " + fonteEmpenho.toString() + " do " + getTituloEmpenho() + " é diferente da Fonte " + fonteVinculo.toString() + mensagemValidacao + ".");
        }
        ve.lancarException();
    }

    private void validarIntegracaoAtributosComunsDiarias(TipoContaDespesa tipoContaDespesa, ValidacaoException ve) {
        String mensagemfinal = !tipoContaDespesa.isSuprimentoFundo() ? " da " + tipoContaDespesa.getDescricao() : " do " + tipoContaDespesa.getDescricao();
        validarIntegracaoAtributosComunsPessoa(selecionado.getFornecedor(), selecionado.getPropostaConcessaoDiaria().getPessoaFisica(), mensagemfinal);
        validarIntegracaoAtributosComunsClasseCredor(selecionado.getClasseCredor(), selecionado.getPropostaConcessaoDiaria().getClasseCredor(), mensagemfinal, ve);
        validarIntegracaoAtributosComunsFonteRecursos(selecionado.getFonteDespesaORC(), selecionado.getPropostaConcessaoDiaria().getFonteDespesaORC(), mensagemfinal, ve);
        validarIntegracaoAtributosComunsDespesaORC(selecionado.getDespesaORC(), selecionado.getPropostaConcessaoDiaria().getDespesaORC(), mensagemfinal, ve);
        validarIntegracaoAtributosComunsValor(selecionado.getValor(), selecionado.getPropostaConcessaoDiaria().getValor(), mensagemfinal, ve);
        ve.lancarException();
    }

    private void validarIntegracaoAtributosComunsDividaPublica(ValidacaoException ve) {
        String mensagemfinal = " da Dívida Pública ";
        if (selecionado.getTipoContaDespesa().isPrecatorio()) {
            mensagemfinal = " do Precatório";
        }
        validarIntegracaoAtributosComunsPessoa(selecionado.getFornecedor(), selecionado.getDividaPublica().getPessoa(), mensagemfinal);
        validarIntegracaoAtributosComunsClasseCredor(selecionado.getClasseCredor(), selecionado.getDividaPublica().getClasseCredorPorSubTipoDespesa(selecionado.getSubTipoDespesa()), mensagemfinal, ve);
    }

    private void validarIntegracaoAtributosComunsContrato(ValidacaoException ve) {
        String mensagemfinal = " do Contrato.";
        if (selecionado.getContrato() != null) {
            Contrato contrato = facade.getContratoFacade().recuperar(selecionado.getContrato().getId());
            validarIntegracaoAtributosComunsPessoa(selecionado.getFornecedor(), contrato.getContratado(), mensagemfinal);
            validarIntegracaoAtributosComunsValor(selecionado.getValor(), contrato.getValorTotalExecucao(), mensagemfinal, ve);
        }
    }

    private void validarIntegracaoAtributosComunsConvenioDespesa(ValidacaoException ve) {
        String mensagemfinal = " do Convênio de Despesa";
        if (selecionado.getConvenioDespesa().getEntidadeBeneficiaria() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Convênio de Despesa selecionado não possui Entidade Beneficiária;");
        }
        ve.lancarException();
        validarIntegracaoAtributosComunsPessoa(selecionado.getFornecedor(), selecionado.getConvenioDespesa().getEntidadeBeneficiaria().getPessoaJuridica(), mensagemfinal);
        validarIntegracaoAtributosComunsClasseCredor(selecionado.getClasseCredor(), selecionado.getConvenioDespesa().getClasseCredor(), mensagemfinal, ve);
        validarIntegracaoAtributosComunsValor(selecionado.getValor(), selecionado.getConvenioDespesa().getValorConvenio(), mensagemfinal, ve);
    }


    private void validarIntegracaoAtributosComuns(TipoContaDespesa tipoContaDespesa, ValidacaoException ve) {
        if (TipoContaDespesa.retornaTipoContaConcessaoDiaria().contains(tipoContaDespesa)) {
            validarIntegracaoAtributosComunsDiarias(tipoContaDespesa, ve);
        }
        if (tipoContaDespesa.isDividaPublica() || tipoContaDespesa.isPrecatorio()) {
            validarIntegracaoAtributosComunsDividaPublica(ve);
        }
        if (tipoContaDespesa.isConvenioDespesa()) {
            validarIntegracaoAtributosComunsConvenioDespesa(ve);
        }
        if (selecionado.getContrato() != null) {
            validarIntegracaoAtributosComunsContrato(ve);
        }
    }

    private void recuperarConfiguracaoContaDespesaTipoPessoa() {
        try {
            if (selecionado.getDespesaORC() != null && selecionado.getDespesaORC().getContaDeDespesa() != null) {
                configContaDespTipoPessoa = facade.getConfigContaDespTipoPessoaFacade().recuperarConfiguracaoContaDespesaTipoPessoa(
                    selecionado.getDespesaORC().getContaDeDespesa(),
                    facade.getSistemaFacade().getExercicioCorrente());
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada("Erro ao recuperar a configuração para conta de despesa: " + e.getMessage());
        }
    }

    public void definirContrato(SelectEvent evt) {
        Contrato contrato = (Contrato) evt.getObject();
        selecionado.setContrato(facade.getContratoFacade().recarregar(contrato));
        selecionado.setFornecedor(contrato.getContratado());
        selecionado.setValor(contrato.getValorTotal());
        selecionado.setSaldo(contrato.getValorTotal());
    }

    public void copiarHistoricoUsuario() {
        if (selecionado.getHistoricoContabil() != null) {
            selecionado.setComplementoHistorico(selecionado.getHistoricoContabil().toStringAutoComplete());
            selecionado.setHistoricoContabil(null);
        }
    }

    public void navegarParaHistoricoContabil() {
        HistoricoContabilControlador historicoContabilControlador = (HistoricoContabilControlador) Util.getControladorPeloNome("historicoContabilControlador");
        FacesUtil.redirecionamentoInterno(historicoContabilControlador.getCaminhoPadrao() + "novo/");
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotaEmpenho(" and emp.id = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria(), getFraseDocumento());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), CategoriaOrcamentaria.RESTO.equals(selecionado.getCategoriaOrcamentaria()) ? ModuloTipoDoctoOficial.NOTA_RESTO_EMPENHO : ModuloTipoDoctoOficial.NOTA_EMPENHO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar nota de empenho: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFraseDocumento() {
        String retorno = "NOTA DE EMPENHO";
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            retorno = "NOTA DE RESTOS A PAGAR";
            if (selecionado.getTransportado() != null && selecionado.getTransportado()) {
                if (TipoRestosProcessado.NAO_PROCESSADOS.equals(selecionado.getTipoRestosProcessados())) {
                    retorno += " NÃO-PROCESSADOS INSCRITOS";
                } else {
                    retorno += " PROCESSADOS";
                }
            }
        }
        return retorno;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<FonteDespesaORC> buscarFontes(String filtro) {
        return facade.getFonteDespesaORCFacade().completaFonteDespesaORC(filtro, selecionado.getDespesaORC());
    }

    public List<Empenho> completarEmpenho(String parte) {
        return facade.buscarEmpenhoAnterioresAoExercicio(
            parte.trim(),
            facade.getSistemaFacade().getExercicioCorrente().getAno(),
            facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<ObrigacaoAPagar> completarObrigacaoPagar(String parte) {
        return facade.getObrigacaoAPagarFacade().buscarObrigacaoPagar(
            parte.trim(),
            selecionado.getUnidadeOrganizacional(),
            selecionado.getDespesaORC(),
            selecionado.getFonteDespesaORC(),
            selecionado.getFornecedor(),
            selecionado.getClasseCredor());
    }

    private void buscarTipoContaDespesa() {
        if (getContaDespesaSelecionada() != null) {
            selecionado.setContaDespesa(getContaDespesaSelecionada());
            selecionado.setCodigoContaTCE(selecionado.getContaDespesa().getCodigoTCEOrCodigoSemPonto());
            TipoContaDespesa tipo = getContaDespesaSelecionada().getTipoContaDespesa();
            if (tipo != null && tipo.isNaoAplicavel()) {
                tiposContaDespesa = facade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta(getContaDespesaSelecionada());
            }
        }
    }

    public List<TipoContaDespesa> getTipoContas() {
        if (tiposContaDespesa == null) {
            buscarTipoContaDespesa();
        }
        List<TipoContaDespesa> toReturn = new ArrayList<>();
        if (getContaDespesaSelecionada() != null) {
            TipoContaDespesa tipo = getContaDespesaSelecionada().getTipoContaDespesa();
            if (!tipo.isNaoAplicavel()) {
                selecionado.setTipoContaDespesa(tipo);
                toReturn.add(tipo);
            } else {
                if (getGeradoPorSolicitacao()) {
                    tiposContaDespesa.removeIf(TipoContaDespesa.SUPRIMENTO_FUNDO::equals);
                }
                for (TipoContaDespesa tp : tiposContaDespesa) {
                    if (!tp.isNaoAplicavel()) {
                        toReturn.add(tp);
                    }
                }
            }
        }
        return toReturn;
    }

    public boolean renderizarTipoDespesa() {
        return selecionado.getDespesaORC() != null && getContaDespesaSelecionada() != null && tiposContaDespesa != null && !tiposContaDespesa.isEmpty();
    }

    public List<HistoricoContabil> completarHistoricoContabil(String parte) {
        return facade.getHistoricoContabilFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public List<DividaPublica> completarDividaPublica(String parte) {
        if (selecionado.getTipoContaDespesa().equals(TipoContaDespesa.PRECATORIO)) {
            return facade.getDividaPublicaFacade().completaDividaPublicaPorNatureza(parte.trim(), NaturezaDividaPublica.PRECATORIO);
        } else {
            return facade.getDividaPublicaFacade().listaFiltrandoDividaPublicaPorUnidade(
                parte.trim(),
                facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        }
    }

    public List<DividaPublica> completarOperacaoCredito(String parte) {
        return facade.getDividaPublicaFacade().completaDividaPublicaPorNatureza(parte.trim(), NaturezaDividaPublica.OPERACAO_CREDITO);
    }

    public List<ParcelaDividaPublica> completarParcelaDividaPublica() {
        if (selecionado.getDividaPublica() != null) {
            return facade.getDividaPublicaFacade().recuperaParcelamentoPorDividaPublica(selecionado.getDividaPublica());
        } else {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " Selecione uma dívida pública para listar as parcelas.");
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getTipoEmpenho() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (formaEntregaExecucao != null) {
            toReturn.addAll(Util.getListSelectItemSemCampoVazio(TipoEmpenho.getTipoEmpenhoPorFormaEntregaExecucaoContrato(formaEntregaExecucao).toArray()));
        } else {
            toReturn.add(new SelectItem(null, ""));
            for (TipoEmpenho object : TipoEmpenho.values()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRestosProcessado() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRestosProcessado object : TipoRestosProcessado.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRestosInscritos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRestosInscritos object : TipoRestosInscritos.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<LiquidacaoEstorno> getLiquidacaoEstornos() {
        if (selecionado.getId() != null) {
            return facade.getLiquidacaoEstornoFacade().listaPorEmpenho(selecionado);
        }
        return new ArrayList<>();
    }

    public List<PagamentoEstorno> getPagamentoEstornos() {
        if (selecionado.getId() != null) {
            return facade.getPagamentoEstornoFacade().listaPorEmpenho(selecionado);
        }
        return new ArrayList<>();
    }

    public List<Pagamento> getPagamentos() {
        if (selecionado.getId() != null) {
            return facade.getPagamentoFacade().listaPorEmpenho(selecionado);
        }
        return new ArrayList<>();
    }

    public List<SelectItem> getClassesCredor() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (selecionado.getTipoContaDespesa() != null && selecionado.getSubTipoDespesa() != null && selecionado.getFornecedor() != null) {
            ConfiguracaoTipoContaDespesaClasseCredor configClasseCredor = facade.getConfiguracaoTipoContaDespesaClasseCredorFacade().buscarConfiguracaoVigente(
                selecionado.getTipoContaDespesa(),
                selecionado.getSubTipoDespesa(),
                selecionado.getDataEmpenho());
            List<ClasseCredor> classesDoFornecedor = facade.getClasseCredorFacade().buscarClassesPorPessoa("", selecionado.getFornecedor());
            if (configClasseCredor != null) {
                for (TipoContaDespesaClasseCredor tipoContaDespesaClasseCredor : configClasseCredor.getListaDeClasseCredor()) {
                    for (ClasseCredor classeCredor : classesDoFornecedor) {
                        if (classeCredor.equals(tipoContaDespesaClasseCredor.getClasseCredor())) {
                            list.add(new SelectItem(tipoContaDespesaClasseCredor.getClasseCredor()));
                        }
                    }
                }
            } else {
                for (ClasseCredor classeCredor : classesDoFornecedor) {
                    list.add(new SelectItem(classeCredor));
                }
            }
        }
        return list;
    }

    public List<SelectItem> getListaModalidadeLicitacao() {
        List<SelectItem> retorno = Lists.newArrayList(new SelectItem(ModalidadeLicitacaoEmpenho.SEM_LICITACAO, ModalidadeLicitacaoEmpenho.SEM_LICITACAO.getDescricao()));

        if (getGeradoPorSolicitacao() && (selecionado.isEmpenhoIntegracaoContrato() || selecionado.isEmpenhoIntegracaoExecucaoProcesso())) {
            retorno.clear();
            if (selecionado.getContrato() != null && selecionado.getContrato().getModalidadeLicitacao().isDispensaLicitacao()) {
                retorno.add(new SelectItem(ModalidadeLicitacaoEmpenho.DISPENSA_LICITACAO, ModalidadeLicitacaoEmpenho.DISPENSA_LICITACAO.getDescricao()));
                retorno.add(new SelectItem(ModalidadeLicitacaoEmpenho.DISPENSA_EXCETO_VALOR, ModalidadeLicitacaoEmpenho.DISPENSA_EXCETO_VALOR.getDescricao()));
            } else {
                retorno = Arrays.stream(ModalidadeLicitacaoEmpenho.values())
                    .map(mod -> new SelectItem(mod, mod.getDescricao()))
                    .collect(Collectors.toList());
            }
        }
        return retorno;
    }

    public List<PropostaConcessaoDiaria> completarSuprimentoFundo(String parte) {
        return facade.getPropostaConcessaoDiariaFacade().listaFiltrandoDiariasPorUnidadePessoaTipoESituacao(parte.trim(), facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), TipoProposta.SUPRIMENTO_FUNDO, SituacaoDiaria.DEFERIDO);
    }

    public List<Contrato> completarContrato(String parte) {
        return ordenarContratos(facade.getContratoFacade().buscarFiltrandoContratoPorModalidadeAndUnidade(
            parte.trim(),
            facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            selecionado.getModalidadeLicitacao()));
    }

    public List<PropostaConcessaoDiaria> completarConcessaoDiaria(String parte) {
        return facade.getPropostaConcessaoDiariaFacade().listaFiltrandoDiariasPorUnidadePessoaTipoESituacao(parte.trim(), facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), TipoProposta.CONCESSAO_DIARIA, SituacaoDiaria.DEFERIDO);
    }

    public List<PropostaConcessaoDiaria> completarConcessaoDiariaCampo(String parte) {
        return facade.getPropostaConcessaoDiariaFacade().listaFiltrandoDiariasPorUnidadePessoaETipo(parte.trim(), facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), TipoProposta.CONCESSAO_DIARIACAMPO);
    }

    public List<ConvenioDespesa> completarConvenioDespesa(String parte) {
        return facade.getConvenioDespesaFacade().listaFiltrando(parte.trim(), "numConvenio", "objeto");
    }

    public List<ConvenioReceita> completarConvenioReceita(String parte) {
        return facade.getConvenioReceitaFacade().buscarFiltrandoConvenioReceita(parte.trim());
    }

    public List<Pessoa> completarFornecedor(String parte) {
        if (configContaDespTipoPessoa != null) {
            if (TipoPessoaPermitido.PESSOAFISICA.equals(configContaDespTipoPessoa.getTipoPessoa())) {
                return facade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);

            } else if (TipoPessoaPermitido.PESSOAJURIDICA.equals(configContaDespTipoPessoa.getTipoPessoa())) {
                return facade.getPessoaFacade().listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);

            }
        }
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<EmpenhoEstorno> getEmpenhoEstornos() {
        if (selecionado.getId() != null) {
            return selecionado.getEmpenhoEstornos();
        }
        return new ArrayList<>();
    }

    public List<SolicitacaoEmpenho> getSolicitacoesPendentes() {
        if (solicitacoesPendentes == null) {
            recuperarSolicitacoesPendentes();
        }
        return solicitacoesPendentes;
    }

    public void setSolicitacoesPendentes(List<SolicitacaoEmpenho> solicitacoesPendentes) {
        this.solicitacoesPendentes = solicitacoesPendentes;
    }

    private void direcionarParaAbaEmpenho() {
        indiceAba = 0;
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tipoEmpenho')");
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public String getClassePesquisaGenerica() {
        return classePesquisaGenerica;
    }

    public void setClassePesquisaGenerica(String classePesquisaGenerica) {
        this.classePesquisaGenerica = classePesquisaGenerica;
    }

    public Object getObjetoPesquisaGenerica() {
        return objetoPesquisaGenerica;
    }

    public void setObjetoPesquisaGenerica(Object objetoPesquisaGenerica) {
        this.objetoPesquisaGenerica = objetoPesquisaGenerica;
    }

    public VwHierarquiaDespesaORC getVwHierarquiaDespesaORC() {
        return vwHierarquiaDespesaORC;
    }

    public void setVwHierarquiaDespesaORC(VwHierarquiaDespesaORC vwHierarquiaDespesaORC) {
        this.vwHierarquiaDespesaORC = vwHierarquiaDespesaORC;
    }

    public DesdobramentoEmpenho getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoEmpenho desdobramento) {
        this.desdobramento = desdobramento;
    }

    public void setarObjetoPesquisa(String classe) {
        classePesquisaGenerica = classe;
    }

    public void setaEmpenho(SelectEvent evt) {
        Empenho e = (Empenho) evt.getObject();
        vwHierarquiaDespesaORC = facade.getDespesaORCFacade().recuperaStrDespesaPorId(e.getDespesaORC().getId());
    }

    public boolean getPossuiSolicitacao() {
        return selecionado.getId() == null && selecionado.isEmpenhoNormal() && !solicitacoesPendentes.isEmpty();
    }

    public String getMensagemSolicitacaoEmpenho() {
        if (solicitacoesPendentes.size() == 1) {
            return "Existe " + solicitacoesPendentes.size() + " solicitação de empenho pendente. Utilize a aba 'Solicitações' para visualizá-la.";
        } else {
            return "Existem " + solicitacoesPendentes.size() + " solicitações de empenho pendentes. Utilize a aba 'Solicitações' para visualizá-las.";
        }
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }


    public List<DesdobramentoObrigacaoPagar> getDetalhamentosObrigacaoPagar() {
        return detalhamentosObrigacaoPagar;
    }

    public void setDetalhamentosObrigacaoPagar(List<DesdobramentoObrigacaoPagar> detalhamentosObrigacaoPagar) {
        this.detalhamentosObrigacaoPagar = detalhamentosObrigacaoPagar;
    }

    public boolean getGeradoPorSolicitacao() {
        return selecionado.getSolicitacaoEmpenho() != null;
    }

    public boolean getGeradoPorSolicitacaoModalidadeDispensa() {
        return getGeradoPorSolicitacao()
            && ((selecionado.isEmpenhoIntegracaoContrato() && !selecionado.getContrato().getModalidadeLicitacao().isDispensaLicitacao())
            || selecionado.isEmpenhoIntegracaoExecucaoProcesso());
    }

    public boolean isSolicitacaoDeExercicioAnterior() {
        return selecionado.getSolicitacaoEmpenho() != null && selecionado.getSolicitacaoEmpenho().getDespesaORC() != null && !selecionado.getSolicitacaoEmpenho().getDespesaORC().getExercicio().equals(facade.getSistemaFacade().getExercicioCorrente());
    }

    public EmpenhoObrigacaoPagar getEmpenhoObrigacaoPagar() {
        return empenhoObrigacaoPagar;
    }

    public void setEmpenhoObrigacaoPagar(EmpenhoObrigacaoPagar empenhoObrigacaoPagar) {
        this.empenhoObrigacaoPagar = empenhoObrigacaoPagar;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    private List<Contrato> ordenarContratos(List<Contrato> contratos) {
        Collections.sort(contratos, new Comparator<Contrato>() {
            @Override
            public int compare(Contrato o1, Contrato o2) {
                Integer i1 = Integer.valueOf(o1.getNumeroTermo());
                Integer i2 = Integer.valueOf(o2.getNumeroTermo());
                return i1.compareTo(i2);
            }
        });
        return contratos;
    }

    public boolean canEditarFonteRecursos() {
        if (isOperacaoEditar()) {
            return false;
        }
        if (selecionado.getSolicitacaoEmpenho() == null) {
            return true;
        }
        if (selecionado.getEmendas() != null && !selecionado.getEmendas().isEmpty()) {
            return false;
        }
        return (getGeradoPorSolicitacao() && isSolicitacaoDeExercicioAnterior()) || isOrigemRestituicao();
    }

    private boolean isOrigemRestituicao() {
        return selecionado.getSolicitacaoEmpenho() != null &&
            OrigemSolicitacaoEmpenho.RESTITUICAO.equals(selecionado.getSolicitacaoEmpenho().getOrigemSolicitacaoEmpenho());
    }

    private void buscarExecucaoSolicitacaoEmpenho() {
        integracaoContabilAdministrativo = buscarIntegracaoContabilAdministrativo();
    }

    public void redirecionarParaOrigemSolicitacaoEmpenho() {
        if (integracaoContabilAdministrativo != null) {
            FacesUtil.redirecionamentoInterno(integracaoContabilAdministrativo.getLinkRedirecionarExecucaoSolicitacaoEmp());
        }
    }

    private IntegracaoContabilAdministrativo buscarIntegracaoContabilAdministrativo() {
        IntegracaoContabilAdministrativo integracao = new IntegracaoContabilAdministrativo();
        integracao.setEmpenho(selecionado);
        integracao.setSolicitacaoEmpenho(selecionado.getSolicitacaoEmpenho());
        try {
            if (selecionado.getSolicitacaoEmpenho() != null) {
                Object[] ec = facade.recuperarIdENumeroExecucaoContrato(selecionado.getSolicitacaoEmpenho());
                if (ec != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Execução de Contrato nº " + ec[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/execucao-contrato-adm/ver/" + ec[0] + "/");
                    return integracao;
                }

                Object[] exAta = facade.recuperarIdENumeroExecucaoProcesso(selecionado.getSolicitacaoEmpenho());
                if (exAta != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Execução da " + TipoExecucaoProcesso.valueOf((String) exAta[2]).getDescricao() + "  nº " + exAta[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/execucao-sem-contrato/ver/" + exAta[0] + "/");
                    return integracao;

                }

                Object[] dp = facade.recuperarIdENumeroDividaPublica(selecionado.getSolicitacaoEmpenho());
                if (dp != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Dívida Pública nº " + dp[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/divida-publica/ver/" + dp[0] + "/");
                    return integracao;
                }

                Object[] pcdDiaria = facade.recuperarIdENumeroPropostaConcessaoDiaria(selecionado.getSolicitacaoEmpenho(), false);
                if (pcdDiaria != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Concessão de Diária nº " + pcdDiaria[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/diaria/ver/" + pcdDiaria[0] + "/");
                    return integracao;
                }

                Object[] pcdSuprimentoFundo = facade.recuperarIdENumeroPropostaConcessaoDiaria(selecionado.getSolicitacaoEmpenho(), true);
                if (pcdSuprimentoFundo != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Suprimento de Fundos nº " + pcdSuprimentoFundo[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/suprimento-fundos/ver/" + pcdSuprimentoFundo[0] + "/");
                    return integracao;
                }

                Object[] cd = facade.recuperarIdENumeroConvenioDespesa(selecionado.getSolicitacaoEmpenho());
                if (cd != null) {
                    integracao.setExecucaoSolicitacaoEmpenho("Convênio de Despesa nº " + cd[1]);
                    integracao.setLinkRedirecionarExecucaoSolicitacaoEmp("/convenio-despesa/ver/" + cd[0] + "/");
                    return integracao;
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar integração contábil/administrativo.", e);
        }
        return null;
    }

    public IntegracaoContabilAdministrativo getIntegracaoContabilAdministrativo() {
        return integracaoContabilAdministrativo;
    }

    public void setIntegracaoContabilAdministrativo(IntegracaoContabilAdministrativo integracaoContabilAdministrativo) {
        this.integracaoContabilAdministrativo = integracaoContabilAdministrativo;
    }

    public void redirecionarParaReconhecimentoDivida() {
        if (selecionado.getReconhecimentoDivida() != null) {
            FacesUtil.redirecionamentoInterno("/reconhecimento-divida/ver/" + selecionado.getReconhecimentoDivida().getId() + "/");
        }
    }

    public Boolean getPertenceEmenda() {
        return pertenceEmenda == null ? Boolean.FALSE : pertenceEmenda;
    }

    public void setPertenceEmenda(Boolean pertenceEmenda) {
        this.pertenceEmenda = pertenceEmenda;
    }

    public EmendaEmpenho getEmendaEmpenho() {
        return emendaEmpenho;
    }

    public void setEmendaEmpenho(EmendaEmpenho emendaEmpenho) {
        this.emendaEmpenho = emendaEmpenho;
    }

    public void limparEmendaEmpenho() {
        emendaEmpenho = null;
    }

    public void novoEmendaEmpenho() {
        emendaEmpenho = new EmendaEmpenho();
        emendaEmpenho.setEmpenho(selecionado);
    }

    public void adicionarEmendaEmpenho() {
        try {
            validarEmendaEmpenho();
            pertenceEmenda = true;
            Util.adicionarObjetoEmLista(selecionado.getEmendas(), emendaEmpenho);
            limparEmendaEmpenho();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarEmendaEmpenho(EmendaEmpenho ee) {
        emendaEmpenho = (EmendaEmpenho) Util.clonarObjeto(ee);
    }

    public void removerEmendaEmpenho(EmendaEmpenho ee) {
        selecionado.getEmendas().remove(ee);
    }

    public List<Emenda> completarEmendas(String parte) {
        if (selecionado.getExercicio() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getFonteDespesaORC() != null) {
            return facade.getEmendaFacade().buscarEmendasAprovadasPorExercicioFonteDespesaORCEUnidade(parte, selecionado.getExercicio(), selecionado.getFonteDespesaORC(), selecionado.getUnidadeOrganizacional());
        }
        return Lists.newArrayList();
    }

    private void validarEmendaEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        if (emendaEmpenho.getEmenda() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Emenda deve ser informado.");
        }
        if (emendaEmpenho.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser informado.");
        }
        ve.lancarException();
        for (EmendaEmpenho emenda : selecionado.getEmendas()) {
            if (!emenda.equals(emendaEmpenho) && emenda.getEmenda().equals(emendaEmpenho.getEmenda())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Emenda selecionada já foi adicionada.");
            }
        }
        ve.lancarException();
    }

    private void validarEmendas(ValidacaoException ve) {
        if (pertenceEmenda) {
            if (selecionado.getValorTotalEmendas().compareTo(selecionado.getValor()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total das emendas deve ser o mesmo que o valor do empenho.");
            }
            for (EmendaEmpenho ee : selecionado.getEmendas()) {
                boolean achouSuplementacaoComMesmaDotacao = false;
                ee.setEmenda(facade.getEmendaFacade().recuperar(ee.getEmenda().getId()));
                for (SuplementacaoEmenda suplementacaoEmenda : ee.getEmenda().getSuplementacaoEmendas()) {
                    if (suplementacaoEmenda.getConta().equals(selecionado.getFonteDespesaORC().getContaDeDespesa()) &&
                        suplementacaoEmenda.getContaAsDestinacao().equals(selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()) &&
                        suplementacaoEmenda.getAcaoPPA().equals(selecionado.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA())) {
                        achouSuplementacaoComMesmaDotacao = true;
                        break;
                    }
                }
                if (!achouSuplementacaoComMesmaDotacao) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A dotação do empenho deve ser a mesma da(s) emenda(s).");
                }
            }
        }
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();
        converterFonteDespesaORC = null;
        moneyConverter = null;
        desdobramento = null;
        empenhoObrigacaoPagar = null;
        desdobramentoObrigacaoPagar = null;
        tiposContaDespesa = null;
        objetoPesquisaGenerica = null;
        classePesquisaGenerica = null;
        solicitacoesPendentes = null;
        detalhamentosObrigacaoPagar = null;
        indiceAba = null;
        vwHierarquiaDespesaORC = null;
        configContaDespTipoPessoa = null;
        formaEntregaExecucao = null;
        integracaoContabilAdministrativo = null;
        pertenceEmenda = null;
        emendaEmpenho = null;
    }

    public Empenho getEmpenhoDeRP() {
        return empenhoDeRP;
    }

    public void setEmpenhoDeRP(Empenho empenhoDeRP) {
        this.empenhoDeRP = empenhoDeRP;
    }

    private void buscarEmpenhoDeRPDoEmpenhoDeOrigem() {
        if (!isOperacaoNovo()) {
            empenhoDeRP = facade.buscarEmpenhoNovoPorEmpenho(selecionado);
        }
    }

    public boolean renderizarCodigoCO() {
        return (pertenceEmenda != null && pertenceEmenda) || facade.getCodigoCOFacade().hasPagamentoComSubContaObrigaCodigoCOPorEmpenho(selecionado);
    }

    public List<CodigoCO> completarCodigosCOs(String parte) {
        return facade.getCodigoCOFacade().buscarCodigosCOsPorFonteDeRecursosFiltrandoPorCodigoEDescricao(parte, selecionado.getFonteDeRecursos());
    }
}
