package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
@ManagedBean(name = "ordemGeralMonitoramentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoOrdemGeralMonitoramento", pattern = "/tributario/fiscalizacao/ordem-geral-monitoramento/novo/", viewId = "/faces/tributario/fiscalizacao/ordemgeralmonitoramento/edita.xhtml"),
    @URLMapping(id = "editarOrdemGeralMonitoramento", pattern = "/tributario/fiscalizacao/ordem-geral-monitoramento/editar/#{ordemGeralMonitoramentoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/ordemgeralmonitoramento/edita.xhtml"),
    @URLMapping(id = "listarOrdemGeralMonitoramento", pattern = "/tributario/fiscalizacao/ordem-geral-monitoramento/listar/", viewId = "/faces/tributario/fiscalizacao/ordemgeralmonitoramento/lista.xhtml"),
    @URLMapping(id = "verOrdemGeralMonitoramento", pattern = "/tributario/fiscalizacao/ordem-geral-monitoramento/ver/#{ordemGeralMonitoramentoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/ordemgeralmonitoramento/visualizar.xhtml")
})
public class OrdemGeralMonitoramentoControlador extends PrettyControlador<OrdemGeralMonitoramento> implements Serializable, CRUD {

    @EJB
    private OrdemGeralMonitoramentoFacade ordemGeralMonitoramentoFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade;
    @EJB
    private MoedaFacade moedaFacade;
    private DocumentoOficial documentoOficial;
    private MonitoramentoFiscal monitoramentoFiscalSelecionado;
    private Converter converterServico;
    private CadastroEconomico cadastroEconomico;
    private Servico servico;
    private List<Servico> servicosSelecionados;
    private List<UsuarioSistema> usuariosSistema;
    private ClassificacaoAtividade classificacaoAtividade;
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    private String bairro;
    private String endereco;
    private Date dataInicial;
    private Date dataFinal;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private CadastroEconomico[] cmcs;
    private UsuarioSistema[] arrayListaUsuarios;
    private List<CadastroEconomico> cadastrosEconomicos;
    private ParametroMonitoramentoFiscal parametroMonitoramentoFiscal;

    public OrdemGeralMonitoramentoControlador() {
        super(OrdemGeralMonitoramento.class);
        cadastrosEconomicos = Lists.newArrayList();
    }

    @URLAction(mappingId = "novoOrdemGeralMonitoramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = (OrdemGeralMonitoramento) Web.pegaDaSessao(OrdemGeralMonitoramento.class);
        if (selecionado == null) {
            super.novo();
            servicosSelecionados = Lists.newArrayList();
            selecionado = new OrdemGeralMonitoramento();
            cadastroEconomico = new CadastroEconomico();
        }
        selecionado.setDataCriacao(new Date());
        selecionado.setUsuarioLogado(getSistemaControlador().getUsuarioCorrente());
        selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.INICIADO);
        bairro = null;
        endereco = null;
        recuperarParametroVigente();
    }

    private void recuperarParametroVigente() {
        parametroMonitoramentoFiscal = parametroMonitoramentoFiscalFacade.retornarParametroExercicioCorrente();
    }

    @URLAction(mappingId = "verOrdemGeralMonitoramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarOrdemGeralMonitoramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = (OrdemGeralMonitoramento) Web.pegaDaSessao(OrdemGeralMonitoramento.class);
        if (selecionado == null) {
            super.editar();
            servicosSelecionados = Lists.newArrayList();
            cadastroEconomico = new CadastroEconomico();
        }
        monitoramentoFiscalSelecionado = new MonitoramentoFiscal();
        bairro = null;
        endereco = null;
        recuperarParametroVigente();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/fiscalizacao/ordem-geral-monitoramento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return ordemGeralMonitoramentoFacade;
    }


    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado.setNumero(singletonGeradorCodigo.getProximoCodigo(OrdemGeralMonitoramento.class, "numero"));
                verificarPrazoProcessoOGM();
            }
            verificarSituacaoDesignado();
            selecionado = ordemGeralMonitoramentoFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaEdita();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void redirecionarParaEdita() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public List<UsuarioSistema> getUsuariosSistema() {
        return usuariosSistema;
    }

    public void setUsuariosSistema(List<UsuarioSistema> usuariosSistema) {
        this.usuariosSistema = usuariosSistema;
    }

    public UsuarioSistema[] getArrayListaUsuarios() {
        return arrayListaUsuarios;
    }

    public void setArrayListaUsuarios(UsuarioSistema[] arrayListaUsuarios) {
        this.arrayListaUsuarios = arrayListaUsuarios;
    }

    public MonitoramentoFiscal getMonitoramentoFiscalSelecionado() {
        return monitoramentoFiscalSelecionado;
    }

    public void setMonitoramentoFiscalSelecionado(MonitoramentoFiscal monitoramentoFiscalSelecionado) {
        this.monitoramentoFiscalSelecionado = monitoramentoFiscalSelecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<Servico> getServicosSelecionados() {
        return servicosSelecionados;
    }

    public void setServicosSelecionados(List<Servico> servicosSelecionados) {
        this.servicosSelecionados = servicosSelecionados;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public List<CadastroEconomico> getCadastrosEconomicos() {
        return cadastrosEconomicos;
    }

    public void setCadastrosEconomicos(List<CadastroEconomico> cadastrosEconomicos) {
        this.cadastrosEconomicos = cadastrosEconomicos;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public void setSingletonGeradorCodigo(SingletonGeradorCodigo singletonGeradorCodigo) {
        this.singletonGeradorCodigo = singletonGeradorCodigo;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public void setUsuarioSistemaFacade(UsuarioSistemaFacade usuarioSistemaFacade) {
        this.usuarioSistemaFacade = usuarioSistemaFacade;
    }

    public Converter getConverterServico() {
        if (converterServico == null) {
            converterServico = new ConverterAutoComplete(Servico.class, ordemGeralMonitoramentoFacade.getServicoFacade());
        }
        return converterServico;
    }

    public List<SelectItem> getSituacoesOrdemGeralMonitoramento() {
        return Util.getListSelectItem(SituacaoOrdemGeralMonitoramento.values());
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    public void setEnderecoCorreioFacade(EnderecoCorreioFacade enderecoCorreioFacade) {
        this.enderecoCorreioFacade = enderecoCorreioFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public void setDocumentoOficialFacade(DocumentoOficialFacade documentoOficialFacade) {
        this.documentoOficialFacade = documentoOficialFacade;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public void atualizaForm() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public void adicionarCadastroEconomico() {
        try {
            validarCadastroEconomico();
            MonitoramentoFiscal monitoramentoFiscal = new MonitoramentoFiscal();
            monitoramentoFiscal.setCadastroEconomico(ordemGeralMonitoramentoFacade.getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId()));
            monitoramentoFiscal.setOrdemGeralMonitoramento(selecionado);
            monitoramentoFiscal.alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal.ADICIONADO);
            Util.adicionarObjetoEmLista(selecionado.getMonitoramentosFiscais(), monitoramentoFiscal);
            cadastroEconomico = new CadastroEconomico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void excluirCadastrosEconomicos(MonitoramentoFiscal monitoramentoFiscal) {
        selecionado.getMonitoramentosFiscais().remove(monitoramentoFiscal);
    }

    public void adicionarCadastrosEconomicos() {
        try {
            for (CadastroEconomico cmc : cmcs) {
                MonitoramentoFiscal monitoramentoFiscal = new MonitoramentoFiscal();
                monitoramentoFiscal.setOrdemGeralMonitoramento(selecionado);
                monitoramentoFiscal.setCadastroEconomico(cmc);
                monitoramentoFiscal.alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal.ADICIONADO);
                Util.adicionarObjetoEmLista(selecionado.getMonitoramentosFiscais(), monitoramentoFiscal);
                cadastroEconomico = new CadastroEconomico();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void selecionarCMCs() {
        if (servico != null) {
            adicionarListaServicos();
        }
        this.cadastrosEconomicos = Lists.newArrayList();
        this.cadastrosEconomicos.addAll(this.ordemGeralMonitoramentoFacade.recuperarPorFiltroCmc(servicosSelecionados,
            classificacaoAtividade,
            bairro,
            dataInicial,
            dataFinal,
            valorInicial,
            valorFinal,
            situacaoCadastral,
            endereco));
    }

    public void limparFiltros() {
        this.cadastrosEconomicos = Lists.newArrayList();
        this.bairro = null;
        this.endereco = null;
        this.dataFinal = null;
        this.dataInicial = null;
        this.valorFinal = null;
        this.valorInicial = null;
        this.servico = null;
        this.classificacaoAtividade = null;
        this.situacaoCadastral = null;
        this.servicosSelecionados = Lists.newArrayList();
    }

    private void validarCadastroEconomico() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o C.M.C");
        } else {
            for (MonitoramentoFiscal monitoramentoFiscal : selecionado.getMonitoramentosFiscais()) {
                if (cadastroEconomico.equals(monitoramentoFiscal.getCadastroEconomico())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Cadastro econômico já adicionado!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial!");
        }
        if (selecionado.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final!");
        }
        if (selecionado.getDataInicial() != null && selecionado.getDataInicial().after(selecionado.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data final deve ser maior que data inicial");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a descrição da ordem de monitoramento!");
        }
        if (selecionado.getMonitoramentosFiscais().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione pelo menos um C.M.C");
        }
        ve.lancarException();
    }

    public List<Servico> completarServicos(String parte) {
        return ordemGeralMonitoramentoFacade.getServicoFacade().completaServico(parte.trim());
    }

    public void adicionarListaServicos() {
        try {
            validarAdicaoServicoLista();
            servicosSelecionados.add(servico);
            servico = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicaoServicoLista() {
        ValidacaoException ve = new ValidacaoException();
        if (servico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um serviço!");
        }
        if (servicosSelecionados.contains(servico)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Serviço já adicionado!");
        }
        ve.lancarException();
    }

    public void removerItemListaServicosSelecionados(ActionEvent evento) {
        Servico item = (Servico) evento.getComponent().getAttributes().get("removeItem");
        servicosSelecionados.remove(item);
    }

    public boolean isMostrarListaServicos() {
        return servicosSelecionados != null && !servicosSelecionados.isEmpty();
    }

    public List<SelectItem> getClassificacaoDaAtividade() {
        return Util.getListSelectItem(ClassificacaoAtividade.values());
    }

    public List<String> completarBairro(String parte) {
        return this.enderecoCorreioFacade.listaFiltrandoPorBairro(parte.trim());
    }

    public List<String> completarEndereco(String parte) {
        return this.enderecoCorreioFacade.listaFiltrandoPorLogradouro(parte.trim());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoCadastralCadastroEconomico.values());
    }

    public CadastroEconomico[] getCmcs() {
        return cmcs;
    }

    public void setCmcs(CadastroEconomico[] cmcs) {
        this.cmcs = cmcs;
    }

    public boolean instanciaPJ(Pessoa p) {
        return (p instanceof PessoaJuridica);
    }

    public List<EnderecoCorreio> enderecosDaPessoa(Pessoa p) {
        return enderecoCorreioFacade.recuperaEnderecosPessoa(p);
    }

    public String inscricaoCadastralCMC(MonitoramentoFiscal monitoramentoFiscal) {
        if (monitoramentoFiscal.getId() == null) {
            return monitoramentoFiscal.getCadastroEconomico().getInscricaoCadastral();
        }
        return ordemGeralMonitoramentoFacade.recuperaPessoaCadastroEconomicoMF(monitoramentoFiscal).getInscricaoCadastral();
    }

    public String nomeCMC(MonitoramentoFiscal monitoramentoFiscal) {
        if (monitoramentoFiscal.getId() == null) {
            return monitoramentoFiscal.getCadastroEconomico().getPessoa().getNome();
        }
        return ordemGeralMonitoramentoFacade.recuperaPessoaCadastroEconomicoMF(monitoramentoFiscal).getPessoa().getNome();
    }

    public void designarMonitoramentoFiscal() {
        try {
            validarDesignacao();
            confirmarDesignacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDesignacao() {
        ValidacaoException ve = new ValidacaoException();
        if (arrayListaUsuarios == null || arrayListaUsuarios.length <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o Auditor Fiscal na lista.");
        }
        if (monitoramentoFiscalSelecionado.getDataLevantamentoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial de Monitoramento!");
        }
        if (monitoramentoFiscalSelecionado.getDataLevantamentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final de Monitoramento!");
        }
        if (monitoramentoFiscalSelecionado.getDataLevantamentoInicial() != null &&
            monitoramentoFiscalSelecionado.getDataLevantamentoFinal() != null &&
            ordemGeralMonitoramentoFacade.isCadastroEconomicoNaMesmaData(monitoramentoFiscalSelecionado,
            monitoramentoFiscalSelecionado.getDataLevantamentoInicial(), monitoramentoFiscalSelecionado.getDataLevantamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe processo de monitoramento fiscal para esse C.M.C nesse período!");
        }
        if (monitoramentoFiscalSelecionado.getDataLevantamentoInicial() != null &&
            monitoramentoFiscalSelecionado.getDataLevantamentoFinal() != null &&
            monitoramentoFiscalSelecionado.getDataLevantamentoInicial().after(monitoramentoFiscalSelecionado.getDataLevantamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data final deve ser maior que data inicial");
        }
        ve.lancarException();
    }

    private void confirmarDesignacao() {
        try {
            for (UsuarioSistema arrayListaUsuario : arrayListaUsuarios) {
                FiscalMonitoramento fiscal = new FiscalMonitoramento();
                fiscal.setMonitoramentoFiscal(monitoramentoFiscalSelecionado);
                fiscal.setAuditorFiscal(arrayListaUsuario);
                Util.adicionarObjetoEmLista(monitoramentoFiscalSelecionado.getFiscaisMonitoramento(), fiscal);
            }
            monitoramentoFiscalSelecionado.setDataInicialMonitoramento(monitoramentoFiscalSelecionado.getDataLevantamentoInicial());
            monitoramentoFiscalSelecionado.setDataFinalMonitoramento(monitoramentoFiscalSelecionado.getDataLevantamentoFinal());
            monitoramentoFiscalSelecionado.alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal.DESIGNADO);
            ordemGeralMonitoramentoFacade.salvar(selecionado);
            carregarMesesPorPeriodo(monitoramentoFiscalSelecionado);
            selecionado.getMonitoramentosFiscais().set(selecionado.getMonitoramentosFiscais().indexOf(monitoramentoFiscalSelecionado), monitoramentoFiscalSelecionado);
            dataInicial = null;
            dataFinal = null;
            arrayListaUsuarios = null;
            monitoramentoFiscalSelecionado = null;
            FacesUtil.executaJavaScript("dialogDesignar.hide()");
            verificarSituacaoDesignado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void verificarSituacaoDesignado() {
        boolean designado = true;
        for (MonitoramentoFiscal mf : selecionado.getMonitoramentosFiscais()) {
            if (SituacaoMonitoramentoFiscal.ADICIONADO.equals(mf.getSituacaoMF())) {
                designado = false;
            }
        }
        if (SituacaoOrdemGeralMonitoramento.INICIADO.equals(selecionado.getSituacaoOGM())) {
            if (designado) {
                selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.DESIGNADO);
                ordemGeralMonitoramentoFacade.salvar(selecionado);
            }
        }
    }

    public void selecionarMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        try {
            validarCancelamentoMonitoramento(monitoramentoFiscal);
            monitoramentoFiscalSelecionado = monitoramentoFiscal;
            usuariosSistema = ordemGeralMonitoramentoFacade.getUsuarioSistemaFacade().listaUsuarioTributarioVigentePorTipo(TipoUsuarioTributario.FISCAL_TRIBUTARIO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCancelamentoMonitoramento(MonitoramentoFiscal monitoramentoFiscal){
        ValidacaoException ve = new ValidacaoException();
        if (monitoramentoFiscal.getId() == null){
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de cancelar, é necessário salvar a Ordem Geral de Monitoramento.");
        }
        ve.lancarException();
    }

    public List<UsuarioSistema> completarAuditorFiscal(String parte) {
        return usuarioSistemaFacade.listaFiltrandoUsuarioSistemaPorTipo(parte, TipoUsuarioTributario.FISCAL_TRIBUTARIO);
    }

    public boolean isPermitirCancelamentoMonitoramento(MonitoramentoFiscal monitoramentoFiscal) {
        return (SituacaoMonitoramentoFiscal.DESIGNADO.equals(monitoramentoFiscal.getSituacaoMF()) ||
            SituacaoMonitoramentoFiscal.ADICIONADO.equals(monitoramentoFiscal.getSituacaoMF())) &&
            !selecionado.getSituacaoOGM().equals(SituacaoOrdemGeralMonitoramento.FINALIZADO);
    }

    public boolean isPermitirExclusaoMonitoramento(MonitoramentoFiscal monitoramentoFiscal) {
        return SituacaoMonitoramentoFiscal.ADICIONADO.equals(monitoramentoFiscal.getSituacaoMF()) &&
            !selecionado.getSituacaoOGM().equals(SituacaoOrdemGeralMonitoramento.FINALIZADO);
    }

    public void irParaMonitoramentoGeral(MonitoramentoFiscal monitoramentoFiscal) {
        if (monitoramentoFiscal.getId() != null) {
            Web.navegacao(getUrlAtual(), "/tributario/fiscalizacao/monitoramento-fiscal/ver/" + monitoramentoFiscal.getId() + "/");
        }
    }

    public void cancelarMonitoramentoFiscal() {
        try {
            if (monitoramentoFiscalSelecionado != null) {
                validarCampoCancelamento();
                monitoramentoFiscalSelecionado.alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal.CANCELADO);
                for (MonitoramentoFiscal mf : selecionado.getMonitoramentosFiscais()) {
                    if (mf.getId() != null && mf.getId().equals(monitoramentoFiscalSelecionado.getId())) {
                        selecionado.getMonitoramentosFiscais()
                            .set(selecionado.getMonitoramentosFiscais().indexOf(mf), monitoramentoFiscalSelecionado);
                    }
                }
                monitoramentoFiscalSelecionado = new MonitoramentoFiscal();
                boolean cancelarProgramacao = true;
                for (MonitoramentoFiscal mf : selecionado.getMonitoramentosFiscais()) {
                    if (!SituacaoMonitoramentoFiscal.CANCELADO.equals(mf.getSituacaoMF())) {
                        cancelarProgramacao = false;
                    }
                }
                if (cancelarProgramacao) {
                    selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.FINALIZADO);
                }
                selecionado = ordemGeralMonitoramentoFacade.salva(selecionado);
                RequestContext.getCurrentInstance().execute("dlgCancelamento.hide()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCampoCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (monitoramentoFiscalSelecionado.getMotivoCancelamento().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do cancelamento.");
        }
        ve.lancarException();
    }

    public void iniciarMonitoramentoFiscal() {
        if (parametroMonitoramentoFiscal == null) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possivel localizar o parâmetro de monitoramento fiscal!");
            return;
        }
        gerarDocumentoInicioMonitoramentoFiscal();
        if (SituacaoOrdemGeralMonitoramento.DESIGNADO.equals(selecionado.getSituacaoOGM())) {
            selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.EXECUTANDO);
        }
        for (MonitoramentoFiscal monitoramento : selecionado.getMonitoramentosFiscais()) {
            if (SituacaoMonitoramentoFiscal.DESIGNADO.equals(monitoramento.getSituacaoMF())) {
                monitoramento.alterarSituacaoAdicionandoHistorico(SituacaoMonitoramentoFiscal.INICIADO);
            }
        }
        ordemGeralMonitoramentoFacade.salvar(selecionado);
        FacesUtil.addOperacaoRealizada("Processo da ordem geral de monitoramento iniciado com sucesso!");
    }

    private void gerarDocumentoInicioMonitoramentoFiscal() {
        try {
            selecionado.setDoctoiniciomonitoramento(documentoOficialFacade.
                gerarDocumentoOrdemGeraldeMonitoramento(selecionado, selecionado.getDoctoiniciomonitoramento(), null, parametroMonitoramentoFiscal.getTipoDoctoOrdemGeral(),
                    usuarioSistemaFacade.getSistemaFacade().getUsuarioCorrente()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (UFMException | AtributosNulosException e) {
            descobrirETratarException(e);
        }
    }

    public boolean isLiberadoMonitoramento(){
        return SituacaoOrdemGeralMonitoramento.EXECUTANDO.equals(selecionado.getSituacaoOGM());
    }

    public boolean isLiberarInicioMonitoramento() {
        return SituacaoOrdemGeralMonitoramento.INICIADO.equals(selecionado.getSituacaoOGM());
    }

    public void finalizarMonitoramento() {
        if (SituacaoOrdemGeralMonitoramento.EXECUTANDO.equals(selecionado.getSituacaoOGM())) {
            selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.FINALIZADO);
            ordemGeralMonitoramentoFacade.salvar(selecionado);
        }
    }

    public boolean isLiberarFimMonitoramento() {
        boolean hasMonitoramentoNaoFinalizado = false;
        if (selecionado.getMonitoramentosFiscais() != null && !selecionado.getMonitoramentosFiscais().isEmpty()) {
            for (MonitoramentoFiscal monitoramento : selecionado.getMonitoramentosFiscais()) {
                if (!SituacaoMonitoramentoFiscal.FINALIZADO.equals(monitoramento.getSituacaoMF())) {
                    hasMonitoramentoNaoFinalizado = true;
                }
            }
        }
        return hasMonitoramentoNaoFinalizado;
    }

    private void validarGerarDocumentoOrdemGeralRelatorioFinal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDoctorelatoriofinalogm() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi localizado o Tipo de Documento para Relátorio Final de Ordem de Monitoramento");
        }
        ve.lancarException();
    }

    public void gerarRelatorioFinal() {
        try {
            validarGerarDocumentoOrdemGeralRelatorioFinal();
            FacesUtil.executaJavaScript("dialogRelatorioFinal.show()");
            documentoOficial = documentoOficialFacade.gerarDocumentoOrdemGeralRelatorioFinal(selecionado, selecionado.getDoctorelatoriofinalogm(), parametroMonitoramentoFiscal.getTipoDoctoRelatorioFinal(), usuarioSistemaFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDoctorelatoriofinalogm(documentoOficial);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (UFMException | AtributosNulosException e) {
            descobrirETratarException(e);
        }
    }

    public void emitirRelatorioFinal() {
        if (parametroMonitoramentoFiscal != null) {
            documentoOficialFacade.emiteDocumentoOficial(documentoOficial);
            if (SituacaoOrdemGeralMonitoramento.FINALIZADO.equals(selecionado.getSituacaoOGM())) {
                selecionado.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.CONCLUIDO);
                ordemGeralMonitoramentoFacade.salvar(selecionado);
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi possivel localizar o parâmetro de monitoramento fiscal!");
        }
    }

    public boolean isLibearRelatorioFinal() {
        return SituacaoOrdemGeralMonitoramento.FINALIZADO.equals(selecionado.getSituacaoOGM()) || SituacaoOrdemGeralMonitoramento.CONCLUIDO.equals(selecionado.getSituacaoOGM());
    }

    private void verificarPrazoProcessoOGM() {
        if (parametroMonitoramentoFiscal != null) {
            if (DataUtil.diferencaDiasInteira(selecionado.getDataInicial(), selecionado.getDataFinal()) > parametroMonitoramentoFiscal.getPrazoProcessoOrdem()) {
                ordemGeralMonitoramentoFacade.criarNotificacaoUsuarioLogado(selecionado);
            }
        }
    }

    public void limparCamposDesignarFiscais() {
        this.arrayListaUsuarios = null;
        this.dataInicial = null;
        this.dataFinal = null;
    }

    public void carregarMesesPorPeriodo(MonitoramentoFiscal monitoramentoFiscal) {
        try {
            validarPeriodoLevantamentoUFMMonitoramentoFiscal(monitoramentoFiscal);
            monitoramentoFiscal.getLevantamentosUFM().clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(monitoramentoFiscal.getDataLevantamentoInicial());
            int mesInicio = calendar.get(Calendar.MONTH) + 1;
            int anoInicio = calendar.get(Calendar.YEAR);
            int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));

            calendar.setTime(monitoramentoFiscal.getDataLevantamentoFinal());
            int mesFim = calendar.get(Calendar.MONTH) + 1;
            int anoFim = calendar.get(Calendar.YEAR);
            int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
            while (anoMesInicio <= anoMesFim) {
                if (mesInicio <= 12) {
                    LevantamentoUFMMonitoramentoFiscal levantamentoUFMMonitoramentoFiscal = new LevantamentoUFMMonitoramentoFiscal();
                    levantamentoUFMMonitoramentoFiscal.setValorIndice(moedaFacade.recuperaValorPorExercicio(anoInicio, mesInicio));
                    levantamentoUFMMonitoramentoFiscal.setMonitoramentoFiscal(monitoramentoFiscal);
                    levantamentoUFMMonitoramentoFiscal.setMes(Mes.getMesToInt(mesInicio));
                    levantamentoUFMMonitoramentoFiscal.setAno(anoInicio);

                    monitoramentoFiscal.getLevantamentosUFM().add(levantamentoUFMMonitoramentoFiscal);
                    mesInicio++;
                } else {
                    mesInicio = 1;
                    anoInicio++;
                }
                anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            }
            monitoramentoFiscal.setDataArbitramento(new Date());
            monitoramentoFiscal.setUfmArbitramento(moedaFacade.recuperaValorVigenteUFM());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarPeriodoLevantamentoUFMMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        ValidacaoException ve = new ValidacaoException();
        if (monitoramentoFiscal.getDataLevantamentoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial do levantamento!");
        }
        if (monitoramentoFiscal.getDataLevantamentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final do levantamento!");
        }
        if (monitoramentoFiscal.getDataLevantamentoInicial() != null && monitoramentoFiscal.getDataLevantamentoFinal() != null &&
            monitoramentoFiscal.getDataLevantamentoInicial().compareTo(monitoramentoFiscal.getDataLevantamentoFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data inicial não pode ser anterior a data final.");
        }
        ve.lancarException();
    }

    public boolean hasTodosMonitoramentosNaoIniciados() {
        for (MonitoramentoFiscal monitoramento : selecionado.getMonitoramentosFiscais()) {
            if (!SituacaoMonitoramentoFiscal.ADICIONADO.equals(monitoramento.getSituacaoMF())) {
                return false;
            }
        }
        return true;
    }
}
