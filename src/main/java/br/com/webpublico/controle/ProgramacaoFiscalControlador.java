package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DateSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author fabio
 */
@ManagedBean(name = "programacaoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editarProgramacaoFiscal", pattern = "/tributario/fiscalizacao/programacao/editar/#{programacaoFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacao/programacao/edita.xhtml"),
    @URLMapping(id = "listarProgramacaoFiscal", pattern = "/tributario/fiscalizacao/programacao/listar/", viewId = "/faces/tributario/fiscalizacao/programacao/lista.xhtml"),
    @URLMapping(id = "verProgramacaoFiscal", pattern = "/tributario/fiscalizacao/programacao/ver/#{programacaoFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacao/programacao/visualizar.xhtml"),
    @URLMapping(id = "novaProgramacaoFiscal", pattern = "/tributario/fiscalizacao/programacao/novo/", viewId = "/faces/tributario/fiscalizacao/programacao/edita.xhtml")
})
public class ProgramacaoFiscalControlador extends PrettyControlador<ProgramacaoFiscal> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ProgramacaoFiscalControlador.class);


    @EJB
    private ProgramacaoFiscalFacade programacaoFiscalFacade;
    private UsuarioSistema usuarioSistema;
    private AcaoFiscal acaoFiscal;
    private AcaoFiscal acaoFiscalSelecionado;
    private ConverterAutoComplete converterCadastroEconomico;
    private ClassificacaoAtividade classificacaoAtividade;
    private String bairro;
    private String endereco;
    private ConverterAutoComplete converterBairro;
    private Converter converterServico;
    private Date dataInicial;
    private Date dataFinal;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private List<CadastroEconomico> listaCadastroEconomico;
    private CadastroEconomico[] listaCmc;
    private List<UsuarioSistema> listaUsuarioSistema;
    private UsuarioSistema[] arrayListaUsuarios;
    private DocumentoOficial documentoOficial;
    private CadastroEconomico cadastroEconomico;
    private String conteudo;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroFacade;
    private String loginSupervisor;
    private String senhaSupervisor;
    private SituacaoCadastralCadastroEconomico situacao;
    private boolean desabilitaEditorDocumento;
    private List<Servico> servicosSelecionados;
    private Servico servico;
    private AssistenteDetentorArquivoComposicao assistenteArquivo;

    public ProgramacaoFiscalControlador() {
        super(ProgramacaoFiscal.class);
    }

    public List<Servico> getServicosSelecionados() {
        return servicosSelecionados;
    }

    public void setServicosSelecionados(List<Servico> servicosSelecionados) {
        this.servicosSelecionados = servicosSelecionados;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Converter getConverterServico() {
        if (converterServico == null) {
            converterServico = new ConverterAutoComplete(Servico.class, programacaoFiscalFacade.getServicoFacade());
        }
        return converterServico;
    }

    public String getLoginSupervisor() {
        return loginSupervisor;
    }

    public void setLoginSupervisor(String loginSupervisor) {
        this.loginSupervisor = loginSupervisor;
    }

    public SituacaoCadastralCadastroEconomico getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralCadastroEconomico situacao) {
        this.situacao = situacao;
    }

    public String getSenhaSupervisor() {
        return senhaSupervisor;
    }

    public void setSenhaSupervisor(String senhaSupervisor) {
        this.senhaSupervisor = senhaSupervisor;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<UsuarioSistema> getListaUsuarioSistema() {
        return listaUsuarioSistema;
    }

    public void setListaUsuarioSistema(List<UsuarioSistema> listaUsuarioSistema) {
        this.listaUsuarioSistema = listaUsuarioSistema;
    }

    public AcaoFiscal getAcaoFiscalSelecionado() {
        return acaoFiscalSelecionado;
    }

    public void setAcaoFiscalSelecionado(AcaoFiscal acaoFiscalSelecionado) {
        this.acaoFiscalSelecionado = acaoFiscalSelecionado;
    }

    public UsuarioSistema[] getArrayListaUsuarios() {
        return arrayListaUsuarios;
    }

    public void setArrayListaUsuarios(UsuarioSistema[] arrayListaUsuarios) {
        this.arrayListaUsuarios = arrayListaUsuarios;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteArquivo() {
        return assistenteArquivo;
    }

    public void setAssistenteArquivo(AssistenteDetentorArquivoComposicao assistenteArquivo) {
        this.assistenteArquivo = assistenteArquivo;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/fiscalizacao/programacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return programacaoFiscalFacade;
    }

    @URLAction(mappingId = "novaProgramacaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        servicosSelecionados = new ArrayList<>();
        selecionado = new ProgramacaoFiscal();
        selecionado.setSituacao(TipoSituacaoProgramacaoFiscal.PROGRAMADO);
        selecionado.setDataCriacao(new Date());
        usuarioSistema = getSistemaControlador().getUsuarioCorrente();
        acaoFiscal = new AcaoFiscal();
        bairro = null;
        endereco = null;
        documentoOficial = new DocumentoOficial();
        cadastroEconomico = new CadastroEconomico();
        acaoFiscalSelecionado = new AcaoFiscal();
        assistenteArquivo = new AssistenteDetentorArquivoComposicao(selecionado, programacaoFiscalFacade.getSistemaFacade().getDataOperacao());
    }

    private Long geraNumeroSequencia() {
        return programacaoFiscalFacade.ultimoNumeroMaisUm();
    }

    public List<ProgramacaoFiscal> getLista() {
        return this.programacaoFiscalFacade.lista();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "verProgramacaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarProgramacaoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        servicosSelecionados = new ArrayList<>();
        selecionado = this.programacaoFiscalFacade.recuperar(getId());
        usuarioSistema = getSistemaControlador().getUsuarioCorrente();
        acaoFiscal = new AcaoFiscal();
        documentoOficial = new DocumentoOficial();
        cadastroEconomico = new CadastroEconomico();
        acaoFiscalSelecionado = new AcaoFiscal();
        assistenteArquivo = new AssistenteDetentorArquivoComposicao(selecionado, programacaoFiscalFacade.getSistemaFacade().getDataOperacao());
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, programacaoFiscalFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date datafinal) {
        this.dataFinal = datafinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date datainicial) {
        this.dataInicial = datainicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorfinal) {
        this.valorFinal = valorfinal;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorinicial) {
        this.valorInicial = valorinicial;
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

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public List<CadastroEconomico> getListaCadastroEconomico() {
        return listaCadastroEconomico;
    }

    public void setListaCadastroEconomico(List<CadastroEconomico> listaCadastroEconomico) {
        this.listaCadastroEconomico = listaCadastroEconomico;
    }

    public CadastroEconomico[] getListaCmc() {
        return listaCmc;
    }

    public void setListaCmc(CadastroEconomico[] listaCmc) {
        this.listaCmc = listaCmc;
    }

    public List<Servico> completaServicos(String parte) {
        return programacaoFiscalFacade.getServicoFacade().completaServico(parte.trim());
    }

    public List<String> completaBairro(String parte) {
        return this.enderecoCorreioFacade.listaFiltrandoPorBairro(parte.trim());
    }

    public List<String> completaEndereco(String parte) {
        return this.enderecoCorreioFacade.listaFiltrandoPorLogradouro(parte.trim());
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(EnderecoCorreio.class, enderecoCorreioFacade);
        }
        return converterBairro;
    }

    public List<SelectItem> getClassificacaoDaAtividade() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (ClassificacaoAtividade ca : ClassificacaoAtividade.values()) {
            retorno.add(new SelectItem(ca, ca.getDescricao()));
        }
        return retorno;
    }

    public void salvar() {
        if (validaCampos() && validaProgramcao()) {
            try {
                if (selecionado.getId() == null) {
                    selecionado.setNumero(geraNumeroSequencia());
                    this.adicionaPeriodoAcoesFiscais();
                }
                selecionado = programacaoFiscalFacade.salva(selecionado);
                FacesUtil.addInfo("Salvo com sucesso!", "");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void excluirSelecionado() {
        try {
            this.programacaoFiscalFacade.remover(selecionado);
        } catch (Exception e) {
            FacesUtil.addAtencao("O registro não pode ser excluído porque possui dependências.");
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getDataInicio() == null && selecionado.getDataFinal() == null) {
            FacesUtil.addCampoObrigatorio("Informe o período.");
            retorno = false;
        } else if (selecionado.getDataInicio() != null && selecionado.getDataFinal() != null && selecionado.getDataInicio().compareTo(selecionado.getDataFinal()) > 0) {
            FacesUtil.addAtencao("A data inicial não pode ser posterior a data inicial!");
            retorno = false;
        } else if (selecionado.getDataInicio() == null && selecionado.getDataFinal() != null) {
            FacesUtil.addCampoObrigatorio("Informe a data inicial.");
            retorno = false;
        } else if (selecionado.getDataInicio() != null && selecionado.getDataFinal() == null) {
            FacesUtil.addCampoObrigatorio("Informe a data final.");
            retorno = false;
        }
        if (selecionado.getDataCriacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe a data de criação.");
            retorno = false;
        }
        if (selecionado.getDescricao().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Informe a descrição.");
            retorno = false;
        } else if (selecionado.getDescricao().length() > 250) {
            FacesUtil.addCampoObrigatorio("Informe a descrição com no máximo 250 caracteres.");
            retorno = false;
        }
        if (selecionado.getAcoesFiscais().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Informe ao menos um C.M.C. para a Programação Fiscal.");
            retorno = false;
        }
        return retorno;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return programacaoFiscalFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public void addAcaoFiscal() {
        if (validaCamposAcaoFiscal() && validaProgramcao()) {
            SituacoesAcaoFiscal situacao = new SituacoesAcaoFiscal();
            situacao.setAcaoFiscal(acaoFiscal);
            situacao.setData(new Date());
            situacao.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.PROGRAMADO);
            acaoFiscal.setCadastroEconomico(programacaoFiscalFacade.getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId()));
            acaoFiscal.setAno(programacaoFiscalFacade.getSistemaFacade().getExercicioCorrente().getAno());
            acaoFiscal.setSituacaoAcaoFiscal(situacao.getSituacaoAcaoFiscal());
            acaoFiscal.getSituacoesAcaoFiscal().add(situacao);
            acaoFiscal.setProgramacaoFiscal(selecionado);
            acaoFiscal.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.PROGRAMADO);
            acaoFiscal.setDataInicial(new Date());
            acaoFiscal.setDataFinal(acaoFiscalSelecionado.getDataFinal());
            selecionado.getAcoesFiscais().add(acaoFiscal);
            acaoFiscal = new AcaoFiscal();
            cadastroEconomico = new CadastroEconomico();
        }
    }

    public void selecionarCMCs() {
        if (validaProgramcao()) {
            if (servico != null) {
                adicionaListaServicos();
            }

            this.listaCadastroEconomico = new ArrayList<>();
            this.listaCadastroEconomico.addAll(this.programacaoFiscalFacade.recuperarPorFiltroCmc(servicosSelecionados,
                classificacaoAtividade,
                bairro,
                dataInicial,
                dataFinal,
                valorInicial,
                valorFinal,
                situacao,
                endereco));
        }
    }

    public void limparFiltros() {
        this.listaCadastroEconomico = new ArrayList<>();
        this.bairro = null;
        this.endereco = null;
        this.dataFinal = null;
        this.dataInicial = null;
        this.valorFinal = null;
        this.valorInicial = null;
        this.servico = null;
        this.servicosSelecionados = new ArrayList<>();
    }

    public void designarAcaoFiscal() {
        loginSupervisor = "";
        senhaSupervisor = "";
        if (acaoFiscalSelecionado != null) {
            if (validaDesignacao()) {
                if (programacaoFiscalFacade.getAcaoFiscalFacade().verificaFiscalizacaoParaMesmoCadastroEconomico(acaoFiscalSelecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("Esse Cadastro Econômico já possuí uma fiscalização designada nesse período.");
                } else if (refiscalizacao()) {
                    RequestContext.getCurrentInstance().execute("dialogConfirmaRefiscalizacao.show()");
                } else {
                    confirmaDesignacao();
                }
            }
        }
    }

    public void confirmaDesignacao() {
        for (int i = 0; i < arrayListaUsuarios.length; i++) {
            FiscalDesignado fiscal = new FiscalDesignado();
            fiscal.setAcaoFiscal(acaoFiscalSelecionado);
            fiscal.setUsuarioSistema(arrayListaUsuarios[i]);
            acaoFiscalSelecionado.getFiscalDesignados().add(fiscal);
        }
        SituacoesAcaoFiscal sit = new SituacoesAcaoFiscal();
        sit.setAcaoFiscal(acaoFiscalSelecionado);
        sit.setData(new Date());
        sit.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.DESIGNADO);
        acaoFiscalSelecionado.setSituacaoAcaoFiscal(sit.getSituacaoAcaoFiscal());
        acaoFiscalSelecionado.getSituacoesAcaoFiscal().add(sit);
        acaoFiscalSelecionado = programacaoFiscalFacade.getAcaoFiscalFacade().salvarRetornando(acaoFiscalSelecionado);
        carregarMesesPorPeriodo(acaoFiscalSelecionado);
        Util.adicionarObjetoEmLista(selecionado.getAcoesFiscais(), acaoFiscalSelecionado);
        acaoFiscalSelecionado = new AcaoFiscal();
        RequestContext.getCurrentInstance().execute("dialogDesignar.hide()");
        this.programacaoFiscalFacade.salvar(selecionado);
    }

    public boolean validaPeriodoLevantamentoContabil(AcaoFiscal acaoFiscal) {
        boolean retorno = true;
        if (acaoFiscal.getDataLevantamentoInicial() == null || acaoFiscal.getDataLevantamentoFinal() == null) {
            FacesUtil.addCampoObrigatorio("Informe o período correto.");
            retorno = false;
        } else if (acaoFiscal.getDataLevantamentoInicial().compareTo(acaoFiscal.getDataLevantamentoFinal()) > 0) {
            FacesUtil.addAtencao("A data inicial não pode ser posterior a data final.");
            retorno = false;
        }
        return retorno;
    }

    public void carregarMesesPorPeriodo(AcaoFiscal acaoFiscal) {
        LevantamentoContabil levantamentoContabil = new LevantamentoContabil();
        if (validaPeriodoLevantamentoContabil(acaoFiscal)) {
            acaoFiscal.getLevantamentosContabeis().clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(acaoFiscal.getDataLevantamentoInicial());
            int mesInicio = calendar.get(Calendar.MONTH) + 1;
            int anoInicio = calendar.get(Calendar.YEAR);
            int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));

            calendar.setTime(acaoFiscal.getDataLevantamentoFinal());
            int mesFim = calendar.get(Calendar.MONTH) + 1;
            int anoFim = calendar.get(Calendar.YEAR);
            int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
            while (anoMesInicio <= anoMesFim) {
                if (mesInicio <= 12) {
                    levantamentoContabil = new LevantamentoContabil();

                    levantamentoContabil.setValorIndice(moedaFacade.recuperaValorPorExercicio(anoInicio, mesInicio));

                    levantamentoContabil.setAcaoFiscal(acaoFiscal);
                    levantamentoContabil.setMes(Mes.getMesToInt(mesInicio));
                    levantamentoContabil.setAno(anoInicio);

                    acaoFiscal.getLevantamentosContabeis().add(levantamentoContabil);
                    mesInicio++;
                } else {
                    mesInicio = 1;
                    anoInicio++;
                }
                anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            }
            acaoFiscal.setDataArbitramento(new Date());
            acaoFiscal.setUfmArbitramento(moedaFacade.recuperaValorVigenteUFM());
        }
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.SUSPENSA);
        return situacoes;
    }

    public void atualizaForm() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public boolean validaDesignacao() {
        boolean retorno = true;
        if (arrayListaUsuarios == null || arrayListaUsuarios.length <= 0) {
            FacesUtil.addCampoObrigatorio("Selecione o Auditor Fiscal na lista.");
            retorno = false;
        }
        if (acaoFiscalSelecionado.getDataInicial() == null) {
            FacesUtil.addCampoObrigatorio("Informe a data do prazo do Auditor Fiscal.");
            retorno = false;
        }
        if (acaoFiscalSelecionado.getDataLevantamentoInicial() == null || acaoFiscalSelecionado.getDataLevantamentoFinal() == null) {
            FacesUtil.addCampoObrigatorio("Informe o período de fiscalização.");
            retorno = false;
        } else if (acaoFiscalSelecionado.getDataLevantamentoInicial().compareTo(acaoFiscalSelecionado.getDataLevantamentoFinal()) > 0) {
            FacesUtil.addAtencao("A data inicial do período de fiscalização não pode ser posterior a data final.");
            retorno = false;
        }
        return retorno;
    }

    public DocumentoOficial gerarDocumento(TipoDoctoAcaoFiscal tipoDoctoAcaoFiscal) {
        if (acaoFiscalSelecionado != null) {
            DoctoAcaoFiscal docto = programacaoFiscalFacade.buscarDocumentoFiscalPorTipo(acaoFiscalSelecionado, tipoDoctoAcaoFiscal);
            if (docto == null || docto.getDocumentoOficial() == null || docto.getDocumentoOficial().getDataEmissao() == null) {
                docto = new DoctoAcaoFiscal();
            }
            acaoFiscalSelecionado.setCadastroEconomico(programacaoFiscalFacade.getAcaoFiscalFacade().buscarCadastroEconomicoDaAcaoFiscal(acaoFiscalSelecionado));
            if (acaoFiscalSelecionado.getOrdemServico() == null || acaoFiscalSelecionado.getOrdemServico() == 0) {
                acaoFiscalSelecionado.setOrdemServico(getNumeroOrdemServico());
                acaoFiscalSelecionado.setAno(getSistemaControlador().getExercicioCorrenteAsInteger());
                acaoFiscalSelecionado = programacaoFiscalFacade.getAcaoFiscalFacade().salvaRetornando(acaoFiscalSelecionado);
            }

            TipoDoctoOficial tipo = programacaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarTipoDoctoOrdemPorExercicio(getSistemaControlador().getExercicioCorrente());
            docto.setAcaoFiscal(acaoFiscalSelecionado);
            docto.setTipoDoctoAcaoFiscal(tipoDoctoAcaoFiscal);
            docto.setAtivo(true);
            try {
                Exercicio e = getSistemaControlador().getExercicioCorrente();
                ParametroFiscalizacao param = programacaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(e);

                DocumentoOficial doc = programacaoFiscalFacade.getDocumentoOficialFacade().geraDocumentoFiscalizacao(acaoFiscalSelecionado,
                    docto.getDocumentoOficial(),
                    acaoFiscalSelecionado.getCadastroEconomico(), null,
                    tipo, getSistemaControlador(), param);

                docto.setDocumentoOficial(doc);
            } catch (Exception ex) {
                FacesUtil.addFatal("Não foi possível continuar!", ex.getMessage());
                logger.error("Erro: ", ex);
            }
            acaoFiscalSelecionado.getDoctosAcaoFiscal().add(programacaoFiscalFacade.salvaDocto(docto));
            programacaoFiscalFacade.getAcaoFiscalFacade().salvar(acaoFiscalSelecionado);

            selecionado.getAcoesFiscais().set(selecionado.getAcoesFiscais().indexOf(acaoFiscalSelecionado), acaoFiscalSelecionado);
            acaoFiscalSelecionado = new AcaoFiscal();
            return docto.getDocumentoOficial();
        }
        return null;
    }

    public Long getNumeroOrdemServico() {
        Long numero = programacaoFiscalFacade.getNumeroOrdemServicoMaisUm();
        if (numero < Calendar.getInstance().get(Calendar.YEAR)) {
            numero = Long.parseLong(Calendar.getInstance().get(Calendar.YEAR) + StringUtil.preencheString(numero.toBinaryString(numero), 5, '0'));
        }
        return numero;
    }

    public void cancelarAcaoFiscal() {
        if (acaoFiscalSelecionado != null) {
            if (validaCampoCancelamento()) {
                SituacoesAcaoFiscal sit = new SituacoesAcaoFiscal();
                sit.setData(new Date());
                sit.setAcaoFiscal(acaoFiscalSelecionado);
                sit.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CANCELADO);
                acaoFiscalSelecionado.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CANCELADO);
                acaoFiscalSelecionado.getSituacoesAcaoFiscal().add(sit);
                for (AcaoFiscal acao : selecionado.getAcoesFiscais()) {
                    if (acao.getId().equals(acaoFiscalSelecionado.getId())) {
                        selecionado.getAcoesFiscais()
                            .set(selecionado.getAcoesFiscais().indexOf(acao), acaoFiscalSelecionado);
                        // comparando os ids da lista de ações pq muda a referencia qndo seleciona o camarada na lista
                    }
                }
                acaoFiscalSelecionado = new AcaoFiscal();
                boolean cancelaProgramacao = true;
                for (AcaoFiscal acao : selecionado.getAcoesFiscais()) {
                    if (!SituacaoAcaoFiscal.CANCELADO.equals(acao.getSituacaoAcaoFiscal())) {
                        cancelaProgramacao = false;
                    }
                }
                if (cancelaProgramacao) {
                    selecionado.setSituacao(TipoSituacaoProgramacaoFiscal.CANCELADO);
                }
                selecionado = programacaoFiscalFacade.salva(selecionado);
                RequestContext.getCurrentInstance().execute("dlgCancelamento.hide()");
            }
        }
    }

    public boolean validaCampoCancelamento() {
        boolean retorno = true;
        if (acaoFiscalSelecionado.getConclusao().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Informe o motivo do cancelamento.");
            retorno = false;
        } else if (acaoFiscalSelecionado.getConclusao().length() > 250) {
            FacesUtil.addCampoObrigatorio("Informe o motivo do cancelamento com no máximo 250 caracteres.");
            retorno = false;
        }
        return retorno;
    }

    public void selecionarAcaoFiscal(AcaoFiscal acao) {
        acaoFiscalSelecionado = programacaoFiscalFacade.getAcaoFiscalFacade().recuperar(acao.getId());
        if (acaoFiscalSelecionado.getDataInicial() == null) {
            acaoFiscalSelecionado.setDataInicial(new Date());
        }
        calculaDataFinalPrazo(null);
        listaUsuarioSistema = Lists.newArrayList();
        listaUsuarioSistema = programacaoFiscalFacade.getUsuarioSistemaFacade().listaUsuarioTributarioVigentePorTipo(TipoUsuarioTributario.FISCAL_TRIBUTARIO);
    }

    public void calculaDataFinalPrazo(DateSelectEvent event) {
        if (event != null) {
            acaoFiscalSelecionado.setDataInicial((Date) event.getDate());
        }
        if (acaoFiscalSelecionado.getDataInicial() != null) {
            ParametroFiscalizacao parametroFiscalizacao = programacaoFiscalFacade.getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(getSistemaControlador().getExercicioCorrente());
            Calendar dataFim = Calendar.getInstance();
            dataFim.setTime(acaoFiscalSelecionado.getDataInicial());
            if (parametroFiscalizacao.getPrazoFiscalizacao() != null) {
                dataFim.add(Calendar.DAY_OF_YEAR, parametroFiscalizacao.getPrazoFiscalizacao());
            }
            acaoFiscalSelecionado.setDataFinal(dataFim.getTime());
        }
    }

    private boolean validaCamposAcaoFiscal() {
        boolean retorno = true;
        if (acaoFiscal == null || cadastroEconomico == null) {
            FacesUtil.addCampoObrigatorio("Informe o C.M.C.");
            retorno = false;
        } else if (selecionado.getAcoesFiscais().size() > 0) {
            for (AcaoFiscal af : selecionado.getAcoesFiscais()) {
                if (af.getId() != null) {
                    af.setCadastroEconomico(programacaoFiscalFacade.getAcaoFiscalFacade().buscarCadastroEconomicoDaAcaoFiscal(af));
                }
//                if (af.getCadastroEconomico().equals(cadastroEconomico)
//                        && !af.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CANCELADO)
//                        && !af.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CONCLUIDO)) {
//                    FacesUtil.addAtencao("Cadastro Econômico já adicionado.");
//                    retorno = false;
//                }
            }
        }
        if (acaoFiscal.getDataInicial() == null && acaoFiscal.getDataFinal() != null) {
            FacesUtil.addCampoObrigatorio("Informe a data inicial.");
            retorno = false;
        }
        if (acaoFiscal.getDataFinal() == null && acaoFiscal.getDataInicial() != null) {
            FacesUtil.addCampoObrigatorio("Informe a data final.");
            retorno = false;
        }
        if (acaoFiscal.getDataFinal() != null && acaoFiscal.getDataInicial() != null && acaoFiscal.getDataInicial().compareTo(acaoFiscal.getDataFinal()) > 0) {
            FacesUtil.addAtencao("A data inicial não pode ser posterior a data inicial.");
            retorno = false;
        }
        return retorno;
    }

    public void addAllAcaoFiscal() {
        for (int i = 0; i < this.listaCmc.length; i++) {
            boolean existeAcaoFiscal = false;
            SituacoesAcaoFiscal situacao = new SituacoesAcaoFiscal();
            situacao.setAcaoFiscal(acaoFiscal);
            situacao.setData(new Date());
            situacao.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.PROGRAMADO);
            this.acaoFiscal.getSituacoesAcaoFiscal().add(situacao);
            acaoFiscal.setAno(programacaoFiscalFacade.getSistemaFacade().getExercicioCorrente().getAno());
            acaoFiscal.setProgramacaoFiscal(selecionado);
            acaoFiscal.setCadastroEconomico(listaCmc[i]);
            acaoFiscal.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.PROGRAMADO);
            acaoFiscal.setDataInicial(new Date());
            acaoFiscal.setDataFinal(acaoFiscalSelecionado.getDataFinal());
            if (selecionado.getAcoesFiscais().size() > 0) {
                for (AcaoFiscal af : selecionado.getAcoesFiscais()) {
                    if (af.getCadastroEconomico().equals(acaoFiscal.getCadastroEconomico())) {
                        existeAcaoFiscal = true;
                    }
                }
            }
            if (!existeAcaoFiscal) {
                selecionado.getAcoesFiscais().add(acaoFiscal);
                acaoFiscal = new AcaoFiscal();
            }
        }
    }

    private void adicionaPeriodoAcoesFiscais() {
        for (AcaoFiscal acaoFiscal : this.selecionado.getAcoesFiscais()) {
            acaoFiscal.setDataInicial(this.selecionado.getDataInicio());
            acaoFiscal.setDataFinal(this.selecionado.getDataFinal());
        }
    }

    public void gerarDocumentoOS(AcaoFiscal acao) {
        selecionarAcaoFiscal(acao);
        documentoOficial = gerarDocumento(TipoDoctoAcaoFiscal.ORDEMSERVICO);
        desabilitaEditor();
    }

    public void desabilitaEditor() {
        desabilitaEditorDocumento = false;
        if (documentoOficial != null) {
            try {
                if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getImprimirDiretoPDF()) {
                    desabilitaEditorDocumento = true;
                }
            } catch (Exception e) {
                desabilitaEditorDocumento = false;
            }
        }

    }

    public void imprimirDocumentoOS() {
        programacaoFiscalFacade.getDocumentoOficialFacade().salvar(documentoOficial);
        programacaoFiscalFacade.getDocumentoOficialFacade().emiteDocumentoOficial(documentoOficial);
    }

    public String inscricaoCadastralCMC(AcaoFiscal acaoFiscal) {
        if (acaoFiscal.getId() == null) {
            return acaoFiscal.getCadastroEconomico().getInscricaoCadastral();
        }
        return programacaoFiscalFacade.getAcaoFiscalFacade().buscarCadastroEconomicoDaAcaoFiscal(acaoFiscal).getInscricaoCadastral();
    }

    public String nomeCMC(AcaoFiscal acaoFiscal) {
        if (acaoFiscal.getId() == null) {
            return acaoFiscal.getCadastroEconomico().getPessoa().getNome();
        }
        return programacaoFiscalFacade.getAcaoFiscalFacade().buscarCadastroEconomicoDaAcaoFiscal(acaoFiscal).getPessoa().getNome();
    }

    public void excluirAcaoFiscal(ActionEvent evento) {
        AcaoFiscal item = (AcaoFiscal) evento.getComponent().getAttributes().get("removeItem");
        selecionado.getAcoesFiscais().remove(item);
    }

    public boolean refiscalizacao() {
        if (acaoFiscalSelecionado.getDataLevantamentoInicial() != null && acaoFiscalSelecionado.getDataLevantamentoFinal() != null) {
            if (programacaoFiscalFacade.getAcaoFiscalFacade().verificaRefiscalizacao(acaoFiscalSelecionado)) {
                return true;
            }
        }
        return false;
    }

    public void validaUsuarioSupervisor() {
        this.senhaSupervisor = Seguranca.md5(this.senhaSupervisor);
        boolean usuarioValido = programacaoFiscalFacade.getAcaoFiscalFacade().validaUsuarioSupervisor(loginSupervisor, senhaSupervisor);
        if (usuarioValido) {
            RequestContext.getCurrentInstance().execute("dialogConfirmaRefiscalizacao.hide()");
            RequestContext.getCurrentInstance().execute("dialogLoginSupervisor.hide()");
            confirmaDesignacao();
        } else {
            FacesUtil.addError("Usuário inválido.", "");
        }
    }

    public boolean isDesabilitaEditorDocumento() {
        return desabilitaEditorDocumento;
    }

    public void setDesabilitaEditorDocumento(boolean desabilitaEditorDocumento) {
        this.desabilitaEditorDocumento = desabilitaEditorDocumento;
    }

    public boolean instanciaPJ(Pessoa p) {
        return (p instanceof PessoaJuridica);
    }

    public SituacaoCadastroEconomico ultimaSituacaoCadastro(CadastroEconomico cmc) {
        return cmc.getSituacaoAtual();
    }

    public List<EnderecoCorreio> enderecosDaPessoa(Pessoa p) {
        return enderecoCorreioFacade.recuperaEnderecosPessoa(p);
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, ""));
        for (SituacaoCadastralCadastroEconomico situacao : SituacaoCadastralCadastroEconomico.values()) {
            itens.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getSituacoesProgramacao() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, ""));
        for (TipoSituacaoProgramacaoFiscal situacao : TipoSituacaoProgramacaoFiscal.values()) {
            itens.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return itens;
    }

    private boolean validaProgramcao() {
        boolean valida = true;
        if (selecionado.getDenunciaEspontanea() && selecionado.getAcoesFiscais().size() > 1) {
            valida = false;
            FacesUtil.addAtencao("Ao informar que uma programação é um Denuncia Espontânea a mesma só pode incorporar um único C.M.C.");
        }

        return valida;
    }

    public void adicionaListaServicos() {
        if (validaAdicaoServicoLista()) {
            servicosSelecionados.add(servico);
            servico = null;
        }
    }

    public boolean validaAdicaoServicoLista() {
        boolean valida = true;
        if (servicosSelecionados.contains(servico)) {
            FacesUtil.addWarn("Atenção!", "O Serviço já foi selecionado.");
            valida = false;
        }

        if (servico == null) {
            valida = false;
        }
        return valida;
    }

    public void removeItemListaServicosSelecionados(ActionEvent evento) {
        Servico item = (Servico) evento.getComponent().getAttributes().get("removeItem");
        servicosSelecionados.remove(item);
    }

    public boolean getMostraListaServicos() {
        return servicosSelecionados != null && !servicosSelecionados.isEmpty();
    }

    public boolean desabilitaCancelamento(AcaoFiscal acao) {
        boolean desabilita = acao.getId() == null ||
            SituacaoAcaoFiscal.CANCELADO.equals(acao.getSituacaoAcaoFiscal()) ||
            SituacaoAcaoFiscal.PROGRAMADO.equals(acao.getSituacaoAcaoFiscal()) ||
            SituacaoAcaoFiscal.REABERTO.equals(acao.getSituacaoAcaoFiscal()) ||
            SituacaoAcaoFiscal.CONCLUIDO.equals(acao.getSituacaoAcaoFiscal());

        if (!desabilita) {
            if (SituacaoAcaoFiscal.EXECUTANDO.equals(acao.getSituacaoAcaoFiscal())) {
                desabilita = (!acao.getLancamentosContabeisAtivos().isEmpty());
            }
        }

        return desabilita;
    }
}
