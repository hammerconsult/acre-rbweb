package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteParcelamento;
import br.com.webpublico.entidadesauxiliares.AssistenteSimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaParaParcelamento;
import br.com.webpublico.entidadesauxiliares.VOPessoa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.ProcessoParcelamentoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorHonorarios;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@ManagedBean(name = "processoParcelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParcelamento",
        pattern = "/parcelamento/novo/",
        viewId = "/faces/tributario/contacorrente/parcelamento/edita.xhtml"),
    @URLMapping(id = "verParcelamento",
        pattern = "/parcelamento/ver/#{processoParcelamentoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/parcelamento/visualizar.xhtml"),
    @URLMapping(id = "listarParcelamento",
        pattern = "/parcelamento/listar/",
        viewId = "/faces/tributario/contacorrente/parcelamento/lista.xhtml"),
})
public class ProcessoParcelamentoControlador extends PrettyControlador<ProcessoParcelamento> implements Serializable, CRUD {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private ResultadoParcela[] selecionadas;
    private Testada testadaPrincipal;
    private SituacaoCadastroEconomico ultimaSituacaoCadastroEconomico;
    private String motivoEstorno;
    private List<ResultadoParcela> parcelasOriginadas;
    private TipoCadastroTributario tipoCadastro;
    private List<EnderecoCorreio> enderecos;
    private List<Telefone> telefones;
    private List<SociedadeCadastroEconomico> socios;
    private List<Propriedade> propriedadesBCI;
    private List<PropriedadeRural> propriedadesBCR;
    private EnderecoCorreio localizacao;
    private Lote lote;
    private Map<String, Valores> valoresVisualiza;
    private boolean permiteReativarParcelamento = false;
    private Map<Long, String> ajuizamentoPorValorDivida;
    private Map<ValorDivida, String> referenciaPorValorDivida;

    private Boolean situacaoCadastroImobiliario;
    private SituacaoCadastralCadastroEconomico situacaoCadastroEconomico;
    private Boolean situacaoCadastroRural;
    private SituacaoCadastralPessoa situacaoCadastroPessoa;

    private AssistenteParcelamento assistente;
    private CompletableFuture<AssistenteParcelamento> futureParcelamento;
    private boolean parcelamentoValido = false;
    private List<ComunicacaoSoftPlan> comunicacaoSoftPlans;
    private Boolean autorizacaoCancelarParcelamento;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private AssistenteSimulacaoParcelamento assistenteSimulacaoParcelamento;
    private PessoaFisica prorietarioSelecionado;

    public ProcessoParcelamentoControlador() {
        super(ProcessoParcelamento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return processoParcelamentoFacade;
    }

    public boolean isAutorizacaoCancelarParcelamento() {
        if (autorizacaoCancelarParcelamento == null) {
            autorizacaoCancelarParcelamento = processoParcelamentoFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(), AutorizacaoTributario.PERMITIR_CANCELAR_PARCELAMENTO);
        }
        return autorizacaoCancelarParcelamento;
    }

    public boolean isParcelamentoValido() {
        return parcelamentoValido;
    }

    public ProcessoParcelamentoFacade getFacade() {
        return processoParcelamentoFacade;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<PropriedadeRural> getPropriedadesBCR() {
        return propriedadesBCR;
    }

    public void setPropriedadesBCR(List<PropriedadeRural> propriedadesBCR) {
        this.propriedadesBCR = propriedadesBCR;
    }

    public List<Propriedade> getPropriedadesBCI() {
        return propriedadesBCI;
    }

    public void setPropriedadesBCI(List<Propriedade> propriedadesBCI) {
        this.propriedadesBCI = propriedadesBCI;
    }

    public List<SociedadeCadastroEconomico> getSocios() {
        return socios;
    }

    public void setSocios(List<SociedadeCadastroEconomico> socios) {
        this.socios = socios;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecos = enderecos;
    }

    public EnderecoCorreio getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(EnderecoCorreio localizacao) {
        this.localizacao = localizacao;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Testada getTestadaPrincipal() {
        return testadaPrincipal;
    }

    public void setTestadaPrincipal(Testada testadaPrincipal) {
        this.testadaPrincipal = testadaPrincipal;
    }

    public SituacaoCadastroEconomico getUltimaSituacaoCadastroEconomico() {
        return ultimaSituacaoCadastroEconomico;
    }

    public void setUltimaSituacaoCadastroEconomico(SituacaoCadastroEconomico ultimaSituacaoCadastroEconomico) {
        this.ultimaSituacaoCadastroEconomico = ultimaSituacaoCadastroEconomico;
    }

    public SituacaoCadastralPessoa getSituacaoCadastroPessoa() {
        return situacaoCadastroPessoa;
    }

    public void setSituacaoCadastroPessoa(SituacaoCadastralPessoa situacaoCadastroPessoa) {
        this.situacaoCadastroPessoa = situacaoCadastroPessoa;
    }

    public Boolean getSituacaoCadastroRural() {
        return situacaoCadastroRural;
    }

    public void setSituacaoCadastroRural(Boolean situacaoCadastroRural) {
        this.situacaoCadastroRural = situacaoCadastroRural;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(SituacaoCadastralCadastroEconomico situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public Boolean getSituacaoCadastroImobiliario() {
        return situacaoCadastroImobiliario != null ? situacaoCadastroImobiliario : true;
    }

    public void setSituacaoCadastroImobiliario(Boolean situacaoCadastroImobiliario) {
        this.situacaoCadastroImobiliario = situacaoCadastroImobiliario;
    }

    public AssistenteParcelamento getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteParcelamento assistente) {
        this.assistente = assistente;
    }

    public boolean isPermiteReativarParcelamento() {
        return permiteReativarParcelamento;
    }

    public Map<String, Valores> getValoresVisualiza() {
        return valoresVisualiza;
    }

    public List<ResultadoParcela> getParcelasOriginadas() {
        ordenarParcelas(parcelasOriginadas);
        return parcelasOriginadas;
    }

    public AssistenteSimulacaoParcelamento getAssistenteSimulacaoParcelamento() {
        return assistenteSimulacaoParcelamento;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parcelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void editar() {
        super.editar();
        ConfiguracaoAcrescimos configuracaoAcrescimos = processoParcelamentoFacade
            .buscarConfiguracaoDeAcrescimosVigente(selecionado.getParamParcelamento()
                .getDividaParcelamento());
        assistenteSimulacaoParcelamento = new AssistenteSimulacaoParcelamento(processoParcelamentoFacade,
            processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(),
            selecionado, configuracaoAcrescimos, new Date());
        inicializarParcelasValorDividas();
        montarParcelasOriginadas();
        recuperarParametro();
        tipoCadastro = selecionado.getParamParcelamento().getTipoCadastroTributario();
        recuperarCadastros();
    }

    @Override
    @URLAction(mappingId = "novoParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarAtributosDoSelecionado();
        assistenteSimulacaoParcelamento = new AssistenteSimulacaoParcelamento(processoParcelamentoFacade,
            processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(), selecionado, null, new Date());

        selecionado.setQuantidadeMaximaParcelas(0);
        comunicacaoSoftPlans = Lists.newArrayList();
        lote = null;
        testadaPrincipal = null;
        ultimaSituacaoCadastroEconomico = null;
        selecionado.setNumeroParcelas(1);
        selecionado.setUsuarioResponsavel(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente());
        atribuirPrimeiroVencimento();

        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                FacesUtil.addError("Erro!", "Não foi possível recuperar os dados da sessão!");
            }
        }

        limparSituacaoCadastro();
    }

    @Override
    @URLAction(mappingId = "verParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        calcularValoresVisualiza();
        if (selecionado.getFaixaDesconto() == null && selecionado.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            criarFaixarDescontoParaDemonstrar();
        }
        selecionado.definirNumeroProcesso();
        permiteReativarParcelamento = selecionado.isCancelado() && processoParcelamentoFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(), AutorizacaoTributario.PERMITIR_REATIVAR_PARCELAMENTO);
        comunicacaoSoftPlans = Lists.newArrayList();
        if (selecionado != null && selecionado.getId() != null) {
            comunicacaoSoftPlans = processoParcelamentoFacade.recuperarComunicacoesParcelamento(selecionado);
        }
    }

    private void ordenarParcelas(List<ResultadoParcela> originadas) {
        if (originadas != null) {
            Collections.sort(originadas, new Comparator<ResultadoParcela>() {
                @Override
                public int compare(ResultadoParcela o1, ResultadoParcela o2) {
                    return Util.zerosAEsquerda(o1.getParcela(), 6).compareTo(Util.zerosAEsquerda(o2.getParcela(), 6));
                }
            });
        }
    }

    public List<CadastroImobiliario> buscarCadastrosImobiliarios(String parte) {
        return processoParcelamentoFacade.buscarCadastrosImobiliarios(parte, getSituacaoCadastroImobiliario());
    }

    public List<CadastroRural> buscarCadastroRurais(String parte) {
        return processoParcelamentoFacade.buscarCadastrosRurais(parte, getSituacaoCadastroRural());
    }

    private void calcularValoresVisualiza() {
        valoresVisualiza = Maps.newHashMap();
        Valores valorOriginal = new Valores();
        Valores valorOriginado = new Valores();

        for (ItemProcessoParcelamento item : selecionado.getItensProcessoParcelamento()) {
            valorOriginal.calcular(item, assistenteSimulacaoParcelamento.getConfiguracaoAcrescimos());
        }
        for (ResultadoParcela parcela : parcelasOriginadas) {
            valorOriginado.calcular(parcela, assistenteSimulacaoParcelamento.getConfiguracaoAcrescimos(), selecionado.getParamParcelamento());
        }
        valoresVisualiza.put("originado", valorOriginado);
        montarParcelasOriginais();
        assistenteSimulacaoParcelamento.setValores(valorOriginal);
    }

    private void criarFaixarDescontoParaDemonstrar() {
        processoParcelamentoFacade.criarFaixarDescontoParaDemonstrar(selecionado);
    }

    private void montarParcelasOriginais() {
        assistenteSimulacaoParcelamento.setParcelas(processoParcelamentoFacade
            .buscarParcelasOriginaisParcelamento(ajuizamentoPorValorDivida, selecionado));
    }

    private void montarParcelasOriginadas() {
        this.parcelasOriginadas = processoParcelamentoFacade.recuperarParcelasOriginadasSemAcrescimos(selecionado);
    }

    private void atribuirPrimeiroVencimento() {
        assistenteSimulacaoParcelamento.atribuirPrimeiroVencimento();
    }

    private void limparSituacaoCadastro() {
        situacaoCadastroImobiliario = true;
        situacaoCadastroEconomico = SituacaoCadastralCadastroEconomico.ATIVA_REGULAR;
        situacaoCadastroRural = true;
        situacaoCadastroPessoa = SituacaoCadastralPessoa.ATIVO;
    }

    private void inicializarAtributosDoSelecionado() {
        selecionado.setExercicio(processoParcelamentoFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataProcessoParcelamento(new Date());
        selecionado.setItensProcessoParcelamento(new ArrayList<ItemProcessoParcelamento>());
        selecionado.setSituacaoParcelamento(SituacaoParcelamento.ABERTO);
        selecionado.setParamParcelamento(null);
    }

    public void inicializarParcelasValorDividas() {
        assistenteSimulacaoParcelamento.setParcelas(Lists.newArrayList());
    }

    public void selecionarParametro() {
        selecionado.setItensProcessoParcelamento(new ArrayList<ItemProcessoParcelamento>());
        if (selecionado.getParamParcelamento() != null && validarParametro()) {
            selecionado.setParamParcelamento(processoParcelamentoFacade.getParametroParcelamentoFacade().recuperar(selecionado.getParamParcelamento().getId()));
            try {
                assistenteSimulacaoParcelamento.setValorMinimoParcela(getFacade().getMoedaFacade()
                    .converterToReal(selecionado.getParamParcelamento().getValorMinimoParcelaUfm()));
            } catch (UFMException e) {
                logger.error(e.getMessage());
            }
            selecionado.getParamParcelamento().setFaixas(processoParcelamentoFacade.getTodasFaixaProcesso(selecionado.getParamParcelamento()));
            selecionado.setPercentualEntrada(selecionado.getParamParcelamento().getValorPercentualEntrada());
            assistenteSimulacaoParcelamento.setConfiguracaoAcrescimos(processoParcelamentoFacade
                .buscarConfiguracaoDeAcrescimosVigente(selecionado.getParamParcelamento().getDividaParcelamento()));
        }
        reinicializarTodosOsValores();
    }

    private boolean validarParametro() {
        boolean valida = true;
        if (selecionado.getParamParcelamento().getVigenciaFim() != null && selecionado.getDataProcessoParcelamento().after(selecionado.getParamParcelamento().getVigenciaFim())) {
            FacesUtil.addError("Parâmetro inválido!", "O Parâmetro selecionado está fora de vigência. Data Inicial: " + new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getParamParcelamento().getVigenciaInicio()) + ", Data Final: " + new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getParamParcelamento().getVigenciaFim()));
            selecionado.setParamParcelamento(null);
            valida = false;
        }
        if (selecionado.getDataProcessoParcelamento().before(selecionado.getParamParcelamento().getVigenciaInicio())) {
            FacesUtil.addError("Parâmetro inválido!", "O Parâmetro selecionado ainda não está vigente. Data Inicial: " + new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getParamParcelamento().getVigenciaInicio()) + ", Data Final: " + new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getParamParcelamento().getVigenciaFim()));
            selecionado.setParamParcelamento(null);
            valida = false;
        }
        return valida;
    }

    public void recuperarParcelasValorDividas() {
        assistenteSimulacaoParcelamento.setValorEntradaInformado(null);
        assistenteSimulacaoParcelamento.setValorEntradaInicial(BigDecimal.ZERO);
        selecionado.setValorEntrada(BigDecimal.ZERO);
        assistenteSimulacaoParcelamento.setValorMinimoEntrada(assistenteSimulacaoParcelamento.getValorMinimoParcela());
        atribuirPrimeiroVencimento();

        selecionado.setNumeroParcelas(1);
        inicializarParcelasValorDividas();
        selecionado.setItensProcessoParcelamento(new ArrayList<ItemProcessoParcelamento>());
        recuperarParcelasConformeTipoCadastro();
    }

    private void recuperarParcelasConformeTipoCadastro() {
        try {
            executarConsulta();
            if (assistenteSimulacaoParcelamento.getParcelas().isEmpty()) {
                FacesUtil.addError("Atenção", "Nenhum débito encontrado de acordo com o parâmetro utilizado!");
                selecionado.setValorEntrada(BigDecimal.ZERO);
                selecionado.setValorParcela(BigDecimal.ZERO);
            } else {
                assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
                assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
                validarValorDeEntrada();
            }

        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
        }
    }

    private void executarConsulta() {
        selecionado.setParamParcelamento(processoParcelamentoFacade.getParametroParcelamentoFacade().recuperar(selecionado.getParamParcelamento().getId()));
        assistenteSimulacaoParcelamento.setParcelas(Lists.newArrayList());
        for (ParamParcelamentoDivida p : selecionado.getParamParcelamento().getDividas()) {
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.DIFERENTE, 1);
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, p.getDivida().getId());
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            if (SituacaoDebito.DIVIDA_ATIVA.equals(selecionado.getParamParcelamento().getSituacaoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.DIFERENTE, 1);
            } else if (SituacaoDebito.AJUIZADA.equals(selecionado.getParamParcelamento().getSituacaoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
            } else if (SituacaoDebito.EXERCICIO.equals(selecionado.getParamParcelamento().getSituacaoDebito())) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.DIFERENTE, 1);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.DIFERENTE, 1);
            }
            if (selecionado.getCadastro() != null) {
                consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastro().getId());
            } else if (selecionado.getPessoa() != null) {
                consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());
            }
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, p.getExercicioInicial().getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, p.getExercicioFinal().getAno());

            assistenteSimulacaoParcelamento.getParcelas().addAll(Lists.newArrayList(new HashSet<>(consulta.executaConsulta().getResultados())));

            Collections.sort(assistenteSimulacaoParcelamento.getParcelas(), new OrdenaResultadoParcelaParaParcelamento());
        }

        if (SituacaoDebito.AJUIZADA.equals(selecionado.getParamParcelamento().getSituacaoDebito())) {
            for (ResultadoParcela parcela : assistenteSimulacaoParcelamento.getParcelas()) {
                parcela.setNumeroProcessoAjuizamento(processoParcelamentoFacade.getNumeroAjuizamento(ajuizamentoPorValorDivida, parcela));
            }
        }
    }

    public void calcularValoresAposAlteracaoQuantidadeParcelas() {
        assistenteSimulacaoParcelamento.calcularValoresAposAlteracaoQuantidadeParcelas();
    }

    public void calcularValores() {
        assistenteSimulacaoParcelamento.calcularValores();
    }

    private void recuperarParametro() {
        try {
            if (selecionado.getParamParcelamento() != null) {
                selecionado.setParamParcelamento(processoParcelamentoFacade.getParametroParcelamentoFacade().recuperar(selecionado.getParamParcelamento().getId()));
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar parametro do parcelamento. ", e);
        }
    }

    public void validarValorDeEntrada() {
        assistenteSimulacaoParcelamento.validarValorDeEntrada();
    }

    public String montarDescricaoValorParametroEntrada() {
        return assistenteSimulacaoParcelamento.montarDescricaoValorParametroEntrada();
    }

    public void avaliarProcessoParcelamento() {
        try {
            validarPessoaParcelamento();
            validarParcelamento();
            if (canReparcelar()) {
                efetivarProcessoParcelamento();
            }
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide(); dialogoConfirmaParcelamento.hide();");
        }
    }

    private void validarPessoaParcelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa() == null && selecionado.getCadastro() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar uma pessoa ou um cadastro para o parcelamento");
        }
        if (selecionado.getPessoa() != null || selecionado.getCadastro() != null) {
            if (!permiteParcelamentoComCpfCnpjInvalido()) {
                VOPessoa pessoa = processoParcelamentoFacade.getPessoaDoProcessoParcelamento(selecionado);
                if (!Util.valida_CpfCnpj(pessoa.getCpfCnpj())) {
                    String tipoDocumento = "CNPJ";
                    if (TipoPessoa.FISICA.getSigla().equals(pessoa.getTipoPessoa())) {
                        tipoDocumento = "CPF";
                    }
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O " + tipoDocumento + " do Contribuinte " + pessoa.getNome() +
                        " é inválido. Para concluir o Processo de Parcelamento é necessário a correção/atualização das informações cadastrais.");
                }
            }
            if (ve.getMensagens().isEmpty()) {
                BloqueioParcelamento bloqueioParcelamento = processoParcelamentoFacade.hasBloqueioParcelamento(selecionado);
                if (bloqueioParcelamento != null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte " + bloqueioParcelamento.getPessoa() + " selecionado possui um bloqueio de parcelamento (" + bloqueioParcelamento.getCodigoExercicio() + ") vigente!" +
                        "<br><b>Motivo:</b> " + bloqueioParcelamento.getMotivo());
                }
            }
        }
        if (ve.getMensagens().isEmpty()) {
            parcelamentoValido = true;
        }
        ve.lancarException();
    }

    private void validarParcelamento() {
        ValidacaoException ve = new ValidacaoException();
        for (ResultadoParcela resultadoParcela : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
            if (resultadoParcela.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Uma das parcelas a ser originada está com o valor zerado, isso não é permitido, " +
                    "verifique os dados informados e tente novamente!");
                break;
            }
        }
        for (ItemProcessoParcelamento item : selecionado.getItensProcessoParcelamento()) {
            if (!processoParcelamentoFacade.getConcorrenciaParcela().isDisponivel(item.getParcelaValorDivida(), SituacaoParcela.EM_ABERTO)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Parcela " + item.getParcelaValorDivida().getSequenciaParcela() + " do exercício de " +
                    item.getParcelaValorDivida().getValorDivida().getExercicio() + " no valor de " + Util.formataNumero(item.getTotal()) +
                    " já foi utilizada em outro processo e não está disponível para parcelamento," + " atualize os débitos do contribuínte " +
                    "pressionando o botao Buscar Débitos");
                break;
            }
        }
        ve.lancarException();
    }

    private boolean permiteParcelamentoComCpfCnpjInvalido() {
        return selecionado != null && selecionado.getParamParcelamento() != null && TipoSimNao.SIM.equals(selecionado.getParamParcelamento().getParcelamentoCpfCnpjInvalido());
    }

    public void efetivarProcessoParcelamento() {
        try {
            adicionarItens();
            bloquearParcelas();
            selecionado.setValoresOriginais(assistenteSimulacaoParcelamento.getValores());
            selecionado = processoParcelamentoFacade.salvarParcelamento(selecionado);
            selecionado = processoParcelamentoFacade.recuperar(selecionado.getId());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistente = criarAssistenteParcelamento();
            assistenteBarraProgresso.setUsuarioSistema(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente());
            futureParcelamento = AsyncExecutor.getInstance()
                .execute(assistenteBarraProgresso,
                    () -> processoParcelamentoFacade.gerarParcelasParcelamento(assistenteBarraProgresso, assistente));
            FacesUtil.executaJavaScript("pollEfetivar.start()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao Efetivar Processo de Parcelamento. Detalhes: " + e.getMessage());
            logger.error("Erro ao efetivar processo de parcelamento. ", e);
        }
    }

    public void acompanharProcesso() {
        FacesUtil.executaJavaScript("aguarde.show()");
        if (futureParcelamento != null && futureParcelamento.isDone()) {
            futureParcelamento.handle((result, exception) -> {
                FacesUtil.executaJavaScript("pollEfetivar.stop()");
                FacesUtil.executaJavaScript("aguarde.hide()");
                if (exception != null) {
                    processoParcelamentoFacade.remover(selecionado);
                    FacesUtil.addOperacaoNaoRealizada("Não foi possivel efetivar o parcelamento, tente novamente!");
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
                } else {
                    FacesUtil.executaJavaScript("closeDialog(dlgEfetivar)");
                    FacesUtil.executaJavaScript("rcFinalizarEfetivacao()");
                }
                return null;
            });
        }
        if (futureParcelamento == null || futureParcelamento.isCancelled()) {
            FacesUtil.executaJavaScript("pollEfetivar.stop()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("closeDialog(dlgEfetivar)");
        }
    }

    public void finalizarProcesso() {
        try {
            assistente = futureParcelamento.get();
            if (assistente != null) {
                assistente.setExecutando(false);
                FacesUtil.addOperacaoRealizada("Processo de Parcelamento finalizado com sucesso!");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            }
        } catch (Exception e) {
            logger.error("Erro ao finalizar processo de parcelamento. ", e);
        } finally {
            futureParcelamento = null;
        }
    }

    private AssistenteParcelamento criarAssistenteParcelamento() {
        AssistenteParcelamento assistente = new AssistenteParcelamento();
        assistente.setSelecionado(selecionado);
        assistente.setParcelas(assistenteSimulacaoParcelamento.getParcelasGeradas());
        assistente.setExercicio(processoParcelamentoFacade.buscarExercicioCorrente());
        assistente.setUsuarioSistema(processoParcelamentoFacade.buscarUsuarioCorrente());
        assistente.setIpUsuario(processoParcelamentoFacade.obterIpUsuario());
        assistente.setExecutando(true);
        return assistente;
    }

    private void bloquearParcelas() {
        for (ItemProcessoParcelamento item : selecionado.getItensProcessoParcelamento()) {
            processoParcelamentoFacade.getConcorrenciaParcela().lock(item.getParcelaValorDivida());
        }
    }

    public void redirecionarCancelamento() {
        if (selecionado.getSituacaoParcelamento().isAtivo()) {
            FacesUtil.redirecionamentoInterno("/parcelamento/cancelamento/novo/" + selecionado.getId() + "/");
        } else if (SituacaoParcelamento.CANCELADO.equals(selecionado.getSituacaoParcelamento()) && selecionado.getCancelamentoParcelamento() != null) {
            FacesUtil.redirecionamentoInterno("/parcelamento/cancelamento/ver/" + selecionado.getCancelamentoParcelamento().getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Esse parcelamento não possui informações do seu processo de cancelamento!");
        }
    }

    public void voltar() {
        super.cancelar();
    }

    public List<SelectItem> buscarParametros() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, " "));
        if (tipoCadastro != null) {
            for (ParamParcelamento param : processoParcelamentoFacade.getParametroParcelamentoFacade().listaVigentesPorTipoCadastro(tipoCadastro)) {
                retorno.add(new SelectItem(param, param.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(retorno);
    }

    public List<SelectItem> buscarTiposCadastro() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tlf : TipoCadastroTributario.values()) {
            retorno.add(new SelectItem(tlf, tlf.getDescricaoLonga()));
        }
        return retorno;
    }

    public List<SelectItem> buscarSituacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem(true, "Ativo"));
        retorno.add(new SelectItem(false, "Inativo"));
        return retorno;
    }

    public List<SelectItem> buscarSituacoesCadastroEconomico() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> buscarSituacoesPessoa() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (SituacaoCadastralPessoa sit : SituacaoCadastralPessoa.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public boolean isCadastroEconomico() {
        if (selecionado.getCadastro() instanceof CadastroEconomico) {
            lote = null;
            testadaPrincipal = null;
            ultimaSituacaoCadastroEconomico = processoParcelamentoFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) selecionado.getCadastro());
            return true;
        }
        return false;
    }

    public boolean isCadastroImobiliario() {
        if (selecionado.getCadastro() instanceof CadastroImobiliario) {
            if (selecionado.getCadastro().getId() != null && ((CadastroImobiliario) selecionado.getCadastro()).getLote() != null) {
                lote = processoParcelamentoFacade.getLoteFacade().recuperar(((CadastroImobiliario) selecionado.getCadastro()).getLote().getId());
                if (lote != null) {
                    testadaPrincipal = processoParcelamentoFacade.getLoteFacade().recuperarTestadaPrincipal(lote);
                } else {
                    testadaPrincipal = new Testada();
                }
                return true;
            }
        }
        return false;
    }

    public boolean isCadastroRural() {
        if (selecionado.getCadastro() instanceof CadastroRural) {
            lote = null;
            testadaPrincipal = null;
            ultimaSituacaoCadastroEconomico = null;
            return true;
        }
        return false;
    }

    public String recuperarSituacaoParcela(ItemProcessoParcelamento item) {
        if (referenciaPorValorDivida == null) {
            referenciaPorValorDivida = Maps.newHashMap();
        }
        if (referenciaPorValorDivida.containsKey(item.getParcelaValorDivida().getValorDivida())) {
            return referenciaPorValorDivida.get(item.getParcelaValorDivida().getValorDivida());
        }

        String referencia = processoParcelamentoFacade.buscarSituacaoParcelaPorParcelaAndParcelamento(item.getParcelaValorDivida(), selecionado.getNumeroCompostoComAno());
        referenciaPorValorDivida.put(item.getParcelaValorDivida().getValorDivida(), referencia);
        return referencia;
    }

    public void emitirDemonstrativo() {
        try {
            byte[] bytes = processoParcelamentoFacade.gerarBytesDemonstrativo(selecionado,
                assistenteSimulacaoParcelamento.getParcelas(),
                parcelasOriginadas != null ? parcelasOriginadas : assistenteSimulacaoParcelamento.getParcelasGeradas(),
                assistenteSimulacaoParcelamento.getValores());
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            abstractReport.setGeraNoDialog(Boolean.TRUE);
            abstractReport.escreveNoResponse("DemonstrativoParcelamento", bytes);
        } catch (Exception ex) {
            logger.error("Erro ao emitir demonstrativo. ", ex);
        }
    }

    public boolean canReparcelar() {
        Integer qtdReparcelamentos = 0;
        for (ResultadoParcela parcela : assistenteSimulacaoParcelamento.getParcelas()) {
            Integer parcelamentosEfetuados = processoParcelamentoFacade.getNumeroReparcelamento(parcela.getIdParcela());
            if (parcelamentosEfetuados > qtdReparcelamentos) {
                qtdReparcelamentos = parcelamentosEfetuados;
            }
            if (parcelamentosEfetuados == (selecionado.getParamParcelamento().getQuantidadeReparcelamento() + 1)) {
                selecionado.setNumeroReparcelamento(qtdReparcelamentos + 1);
                selecionado.setNumeroParcelas(1);
                calcularValores();
                FacesUtil.executaJavaScript("dialogoReparcelamento.show()");
                return false;
            } else if (parcelamentosEfetuados > selecionado.getParamParcelamento().getQuantidadeReparcelamento()) {
                selecionado.setNumeroReparcelamento(qtdReparcelamentos + 1);
                FacesUtil.addAtencao("A parcela número " + parcela.getParcela()
                    + " com referência '" + parcela.getReferencia() + "' no valor de R$ " + Util.formataValor(parcela.getValorTotal())
                    + ", superou o teto máximo de reparcelamentos, essa operação não pode ser concluída.");
                return false;
            }
        }
        selecionado.setNumeroReparcelamento(qtdReparcelamentos);
        return true;
    }

    public BigDecimal getDescontoImposto() {
        return assistenteSimulacaoParcelamento.getValores().descontoImposto;
    }

    public BigDecimal getDescontoTaxa() {
        return assistenteSimulacaoParcelamento.getValores().descontoTaxa;
    }

    public BigDecimal getImposto() {
        return assistenteSimulacaoParcelamento.getValores().imposto;
    }

    public BigDecimal getImpostoComDesconto() {
        return getImposto().subtract(getDescontoImposto());
    }

    public BigDecimal getTaxa() {
        return assistenteSimulacaoParcelamento.getValores().taxa;
    }

    public BigDecimal getTaxaComDesconto() {
        return getTaxa().subtract(getDescontoTaxa());
    }

    public BigDecimal getDescontoCorrecao() {
        return assistenteSimulacaoParcelamento.getValores().descontoCorrecao;
    }

    public BigDecimal getDescontoHonorarios() {
        return assistenteSimulacaoParcelamento.getValores().descontoHonorarios;
    }

    public BigDecimal getDescontoTotal() {
        return getDescontoImposto().add(getDescontoTaxa()).add(getDescontoJuros()).add(getDescontoMulta()).add(getDescontoCorrecao()).add(getDescontoHonorarios());
    }

    public BigDecimal getTotal() {
        return assistenteSimulacaoParcelamento.getValores() != null && assistenteSimulacaoParcelamento.getValores().total != null ?
            assistenteSimulacaoParcelamento.getValores().total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getCorrecao() {
        return assistenteSimulacaoParcelamento.getValores().correcao;
    }

    public BigDecimal getCorrecaoComDesconto() {
        return getCorrecao().subtract(getDescontoCorrecao());
    }

    public BigDecimal getHonorarios() {
        return assistenteSimulacaoParcelamento.getValores().honorarios;
    }

    public BigDecimal getHonorariosComDesconto() {
        return getHonorarios().subtract(getDescontoHonorarios());
    }

    public BigDecimal getDescontoMulta() {
        return assistenteSimulacaoParcelamento.getValores().descontoMulta;
    }

    public BigDecimal getMulta() {
        return assistenteSimulacaoParcelamento.getValores().multa;
    }

    public BigDecimal getMultaComDesconto() {
        return getMulta().subtract(getDescontoMulta());
    }

    public BigDecimal getDescontoJuros() {
        return assistenteSimulacaoParcelamento.getValores().descontoJuros;
    }

    public BigDecimal getJuros() {
        return assistenteSimulacaoParcelamento.getValores().juros;
    }

    public void adicionarItens() {
        selecionado.getItensProcessoParcelamento().clear();
        for (ResultadoParcela parcela : assistenteSimulacaoParcelamento.getParcelas()) {
            getFacade().addItem(selecionado, parcela);
        }
    }

    public void limparCadastroPessoa() {
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
    }

    public void limparCadastroPessoaSituacao() {
        limparSituacaoCadastro();
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
        parcelamentoValido = false;
    }

    public void poeNaSessao() {
        Web.poeTodosFieldsNaSessao(this);
    }

    public void carregarCadastro() {
        try {
            validarPessoaParcelamento();
            prorietarioSelecionado = null;
            if (selecionado.getPessoa() != null && selecionado.getPessoa() instanceof PessoaFisica) {
                PessoaFisica pf = selecionado.getPessoa().getAsPessoaFisica();
                if (!getFacade().getPessoaFacade().cadastroAtualizado(pf)) {
                    montarMensagemDialogAtualizacaoCadastroPF(pf);
                    abrirDialogAtualizacaoCadastralPF();
                    return;
                }
            }
            recuperarCadastros();
            if (selecionado.getCadastro() instanceof CadastroImobiliario) {
                boolean possuiProprietarioComCadastroDesatualizado = false;
                for (Propriedade propriedade : propriedadesBCI) {
                    if (propriedade.getPessoa().isPessoaFisica() && !getFacade().getPessoaFacade().cadastroAtualizado(propriedade.getPessoa().getAsPessoaFisica())) {
                        possuiProprietarioComCadastroDesatualizado = true;
                        break;
                    }
                }
                if (possuiProprietarioComCadastroDesatualizado) {
                    if (propriedadesBCI.size() == 1 && propriedadesBCI.get(0).getPessoa().isPessoaFisica()) {
                        PessoaFisica pf = propriedadesBCI.get(0).getPessoa().getAsPessoaFisica();
                        prorietarioSelecionado = pf;
                        montarMensagemDialogAtualizacaoCadastroPF(pf);
                        abrirDialogAtualizacaoCadastralPF();
                    } else {
                        FacesUtil.atualizarComponente("formDialogoAtualizacaoPFCadastro");
                        FacesUtil.executaJavaScript("dialogoProprietariosCI.show();");
                    }
                }
            }
            reinicializarTodosOsValores();
        } catch (ValidacaoException ve) {
            selecionado.setPessoa(null);
            selecionado.setCadastro(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<Propriedade> proprietariosPessoaFisica() {
        if (propriedadesBCI == null) return Lists.newArrayList();
        return propriedadesBCI.stream().filter(p -> p.getPessoa().isPessoaFisica()).collect(Collectors.toList());
    }

    private void recuperarCadastros() {
        if (selecionado.getCadastro() instanceof CadastroImobiliario) {
            CadastroImobiliario ci = getFacade().getCadastroImobiliarioFacade().recuperarSemCalcular(selecionado.getCadastro().getId());
            lote = ci.getLote();
            propriedadesBCI = ci.getPropriedadeVigente();
            selecionado.setCadastro(ci);
        }
        if (selecionado.getCadastro() instanceof CadastroEconomico) {
            CadastroEconomico ce = getFacade().getCadastroEconomicoFacade().recuperar(selecionado.getCadastro().getId());
            socios = ce.getSociedadeCadastroEconomico();
            localizacao = ce.getLocalizacao().converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
            selecionado.setCadastro(ce);
        }
        if (selecionado.getCadastro() instanceof CadastroRural) {
            CadastroRural cr = getFacade().getCadastroRuralFacade().recuperar(selecionado.getCadastro().getId());
            propriedadesBCR = cr.getPropriedadesAtuais();
            selecionado.setCadastro(cr);
        }
        if (selecionado.getCadastro() == null && selecionado.getPessoa() != null) {
            Pessoa recuperar = getFacade().getPessoaFacade().recuperar(selecionado.getPessoa().getId());
            enderecos = recuperar.getEnderecos();
            telefones = recuperar.getTelefones();
            selecionado.setPessoa(recuperar);
        }
    }

    private void reinicializarTodosOsValores() {
        assistenteSimulacaoParcelamento.setParcelas(Lists.newArrayList());
        assistenteSimulacaoParcelamento.setValores(new Valores());
        selecionado.setQuantidadeMaximaParcelas(0);
        selecionado.setValorEntrada(BigDecimal.ZERO);
        selecionado.setNumeroParcelas(1);
        FacesUtil.atualizarComponente("Formulario");
    }

    public List<SituacaoCadastralCadastroEconomico> buscarSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = Lists.newArrayList();
        situacoes.add(getSituacaoCadastroEconomico());
        return situacoes;
    }

    public List<SituacaoCadastralPessoa> buscarSituacoesDisponiveisPessoa() {
        List<SituacaoCadastralPessoa> situacoes = Lists.newArrayList();
        situacoes.add(getSituacaoCadastroPessoa());
        return situacoes;
    }

    public String getNomeClasse() {
        if (tipoCadastro != null) {
            switch (tipoCadastro) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {
            selecionado.setCadastro((Cadastro) obj);
        } else if (obj instanceof Pessoa) {
            selecionado.setPessoa((Pessoa) obj);
        }
        carregarCadastro();
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (tipoCadastro != null) {
            switch (tipoCadastro) {
                case IMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case ECONOMICO:
                    PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                    List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
                    situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
                    situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
                    componente.setSituacao(situacao);
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case PESSOA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public void gerarTermoAssinado() {
        try {
            Arquivo arquivo = processoParcelamentoFacade.gerarTermoAssinado(selecionado);
            byte[] bytes = arquivoFacade.montarArquivoParaDownload(arquivo);
            AbstractReport.poeRelatorioNaSessao(bytes, arquivo.getNome());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void gerarTermo(boolean novo) {
        try {
            selecionado.setTermo(processoParcelamentoFacade.gerarTermoParcelamento(selecionado, null, novo ? null : selecionado.getTermo()));
            processoParcelamentoFacade.salvar(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao gerar termo do parcelamento. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar Termo do Parcelamento. Detalhes: " + e.getMessage());
        }
    }

    public void salvar() {
        processoParcelamentoFacade.salvar(selecionado);
        FacesUtil.addInfo("Processo de Parcelamento salvo com sucesso!", "");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public static class Valores {
        public BigDecimal juros, jurosParaDesconto;
        public BigDecimal descontoJuros;
        public BigDecimal multa, multaParaDesconto;
        public BigDecimal descontoMulta;
        public BigDecimal correcao, correcaoParaDesconto;
        public BigDecimal descontoCorrecao;
        public BigDecimal honorarios, honorariosParaDesconto;
        public BigDecimal descontoHonorarios;
        public BigDecimal total;
        public BigDecimal imposto, impostoParaDesconto;
        public BigDecimal taxa, taxaParaDesconto;
        public BigDecimal descontoImposto;
        public BigDecimal descontoTaxa;
        public BigDecimal desconto;
        private ConfiguracaoAcrescimos configuracaoAcrescimos;

        public Valores() {
            juros = BigDecimal.ZERO;
            multa = BigDecimal.ZERO;
            correcao = BigDecimal.ZERO;
            honorarios = BigDecimal.ZERO;
            imposto = BigDecimal.ZERO;
            taxa = BigDecimal.ZERO;
            jurosParaDesconto = BigDecimal.ZERO;
            multaParaDesconto = BigDecimal.ZERO;
            correcaoParaDesconto = BigDecimal.ZERO;
            honorariosParaDesconto = BigDecimal.ZERO;
            impostoParaDesconto = BigDecimal.ZERO;
            taxaParaDesconto = BigDecimal.ZERO;
            descontoJuros = BigDecimal.ZERO;
            descontoMulta = BigDecimal.ZERO;
            descontoCorrecao = BigDecimal.ZERO;
            descontoHonorarios = BigDecimal.ZERO;
            descontoImposto = BigDecimal.ZERO;
            descontoTaxa = BigDecimal.ZERO;
            desconto = BigDecimal.ZERO;
            total = BigDecimal.ZERO;
        }

        public Valores(ProcessoParcelamento parcelamento) {
            juros = parcelamento.getValorTotalJuros();
            multa = parcelamento.getValorTotalMulta();
            correcao = parcelamento.getValorTotalCorrecao();
            honorarios = parcelamento.getValorTotalHonorarios();
            imposto = parcelamento.getValorTotalImposto();
            taxa = parcelamento.getValorTotalTaxa();
            desconto = parcelamento.getValorDesconto();
            total = parcelamento.getTotalGeral();
            descontoJuros = BigDecimal.ZERO;
            descontoMulta = BigDecimal.ZERO;
            descontoCorrecao = BigDecimal.ZERO;
            descontoHonorarios = BigDecimal.ZERO;
            descontoImposto = BigDecimal.ZERO;
            descontoTaxa = BigDecimal.ZERO;
        }


        public void calcular(ResultadoParcela parcela, ConfiguracaoAcrescimos configuracaoAcrescimos, ParamParcelamento paramParcelamento) {
            this.configuracaoAcrescimos = configuracaoAcrescimos;
            juros = juros.add(parcela.getValorJuros());
            multa = multa.add(parcela.getValorMulta());
            correcao = correcao.add(parcela.getValorCorrecao());
            imposto = imposto.add(parcela.getValorImposto());
            taxa = taxa.add(parcela.getValorTaxa());
            desconto = desconto.add(parcela.getValorDesconto());
            honorarios = honorarios.add(parcela.getValorHonorarios());
            if (parcela.getTotal() != null && parcela.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                total = total.add(parcela.getTotal());
            } else {
                total = (juros.add(multa.add(correcao.add(honorarios.add(imposto.add(taxa)))))).setScale(2, RoundingMode.HALF_UP);
            }
            if ((paramParcelamento.getInicioDesconto() == null || paramParcelamento.getInicioDesconto().before(parcela.getVencimento()))
                && ((paramParcelamento.getFinalDesconto() == null ||
                paramParcelamento.getFinalDesconto().getTime() >= (parcela.getVencimento().getTime())))) {
                jurosParaDesconto = jurosParaDesconto.add(parcela.getValorJuros());
                multaParaDesconto = multaParaDesconto.add(parcela.getValorMulta());
                correcaoParaDesconto = correcaoParaDesconto.add(parcela.getValorCorrecao());
                honorariosParaDesconto = honorariosParaDesconto.add(parcela.getValorHonorarios());
                impostoParaDesconto = impostoParaDesconto.add(parcela.getValorImposto());
                taxaParaDesconto = taxaParaDesconto.add(parcela.getValorTaxa());
            }
        }

        public void calcular(ItemProcessoParcelamento item, ConfiguracaoAcrescimos configuracaoAcrescimos) {
            this.configuracaoAcrescimos = configuracaoAcrescimos;
            juros = juros.add(item.getJuros());
            multa = multa.add(item.getMulta());
            correcao = correcao.add(item.getCorrecao());

            imposto = imposto.add(item.getImposto());
            taxa = taxa.add(item.getTaxa());
            if (item.getHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                calcularHonorarios();
            }
            total = juros.add(multa.add(correcao.add(honorarios.add(imposto.add(taxa)))));
        }

        public void calcularHonorarios() {
            honorarios = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), new Date(), juros.add(multa.add(correcao.add(imposto.add(taxa)))));
        }

        public void aplicarDescontos(ParamParcelamentoTributo paramDesconto) {
            descontoImposto = descontoImposto.add(calcularPercentualDesconto(paramDesconto.getPercDescontoImposto(), impostoParaDesconto));
            descontoTaxa = descontoTaxa.add(calcularPercentualDesconto(paramDesconto.getPercDescontoTaxa(), taxaParaDesconto));
            descontoMulta = descontoMulta.add(calcularPercentualDesconto(paramDesconto.getPercentualMulta(), multaParaDesconto));
            descontoJuros = descontoJuros.add(calcularPercentualDesconto(paramDesconto.getPercentualJuros(), jurosParaDesconto));
            descontoCorrecao = descontoCorrecao.add(calcularPercentualDesconto(paramDesconto.getPercentualCorrecaoMonetaria(), correcaoParaDesconto));
            descontoHonorarios = descontoHonorarios.add(calcularPercentualDesconto(paramDesconto.getPercentualHonorarios(), honorariosParaDesconto));
        }

        private BigDecimal calcularPercentualDesconto(BigDecimal percentual, BigDecimal aplicavelSobre) {
            BigDecimal valorAplicavel = BigDecimal.ZERO;
            if (percentual != null && percentual.compareTo(BigDecimal.ZERO) > 0) {
                valorAplicavel = percentual.multiply(aplicavelSobre);
                valorAplicavel = valorAplicavel.divide(CEM);
            }

            return valorAplicavel;
        }

        public BigDecimal getJuros() {
            return juros;
        }

        public BigDecimal getDescontoJuros() {
            return descontoJuros;
        }

        public BigDecimal getMulta() {
            return multa;
        }

        public BigDecimal getDescontoMulta() {
            return descontoMulta;
        }

        public BigDecimal getCorrecao() {
            return correcao;
        }

        public BigDecimal getTotal() {
            return total != null ? total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        public BigDecimal getTotalComDesconto() {
            return total.subtract(desconto);
        }

        public BigDecimal getDescontoCorrecao() {
            return descontoCorrecao;
        }

        public BigDecimal getDescontoHonorarios() {
            return descontoHonorarios;
        }

        public BigDecimal getImposto() {
            return imposto;
        }

        public BigDecimal getTaxa() {
            return taxa;
        }

        public BigDecimal getDescontoImposto() {
            return descontoImposto;
        }

        public BigDecimal getDescontoTaxa() {
            return descontoTaxa;
        }

        public BigDecimal getHonorarios() {
            return honorarios != null ? honorarios.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        public void setHonorarios(BigDecimal honorarios) {
            this.honorarios = honorarios;
        }

        public BigDecimal getDesconto() {
            return desconto != null ? desconto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        public BigDecimal getHonorariosAtualizados() {
            if (honorarios.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valor = juros;
                valor = valor.add(multa);
                valor = valor.add(correcao);
                valor = valor.add(imposto);
                valor = valor.add(taxa);
                return CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), new Date(), valor);
            }
            return BigDecimal.ZERO;
        }

        @Override
        public String toString() {
            return "Valores{" +
                "juros=" + juros +
                ", descontoJuros=" + descontoJuros +
                ", multa=" + multa +
                ", descontoMulta=" + descontoMulta +
                ", correcao=" + correcao +
                ", descontoCorrecao=" + descontoCorrecao +
                ", honorarios=" + honorarios +
                ", descontoHonorarios=" + descontoHonorarios +
                ", total=" + total +
                ", imposto=" + imposto +
                ", taxa=" + taxa +
                ", descontoImposto=" + descontoImposto +
                ", descontoTaxa=" + descontoTaxa +
                '}';
        }
    }

    public static class ComparaDescontoPorData implements Comparator<ParamParcelamentoTributo> {
        @Override
        public int compare(ParamParcelamentoTributo o1, ParamParcelamentoTributo o2) {
            return o1.getVencimentoFinalParcela().compareTo(o1.getVencimentoFinalParcela());
        }
    }

    public ResultadoParcela[] getSelecionadas() {
        return selecionadas;
    }

    public void setSelecionadas(ResultadoParcela[] selecionadas) {
        this.selecionadas = selecionadas;
    }

    public void removerParcelas() {
        if (selecionadas == null || selecionadas.length == 0) {
            FacesUtil.addOperacaoNaoPermitida("Selecione ao menos uma parcela para remover");
            return;
        }
        if (selecionadas.length == assistenteSimulacaoParcelamento.getParcelas().size()) {
            FacesUtil.addOperacaoNaoPermitida("Você não pode remover todas as parcelas desse parcelamento, marque apenas as que não serão parceladas");
            return;
        }
        for (ResultadoParcela selecionada : selecionadas) {
            assistenteSimulacaoParcelamento.getParcelas().remove(selecionada);
        }

        assistenteSimulacaoParcelamento.setValorEntradaInformado(BigDecimal.ZERO);
        assistenteSimulacaoParcelamento.setValorEntradaInicial(BigDecimal.ZERO);
        assistenteSimulacaoParcelamento.setValorMinimoEntrada(assistenteSimulacaoParcelamento.getValorMinimoParcela());
        selecionado.setValorEntrada(BigDecimal.ZERO);
        selecionado.setNumeroParcelas(1);

        assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
        assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
    }

    public boolean canCancelar() {
        if (parcelasOriginadas != null) {
            for (ResultadoParcela parcela : parcelasOriginadas) {
                if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean bloquearEmissaoDamDaParcela(ResultadoParcela parcela) {
        Divida divida = processoParcelamentoFacade.recuperarDivida(parcela.getIdDivida());
        int anoVencimentoParcela = DataUtil.getAno(parcela.getVencimento());
        return (PermissaoEmissaoDAM.HABILITA_BLOQUEIA_EXERCICIOS_POSTERIORES.equals(divida.getPermissaoEmissaoDAM())
            && anoVencimentoParcela > processoParcelamentoFacade.getSistemaFacade().getExercicioCorrente().getAno())
            || PermissaoEmissaoDAM.BLOQUEIA.equals(divida.getPermissaoEmissaoDAM());
    }

    public void imprimirDam() {
        try {
            List<DAM> dams = Lists.newArrayList();
            ordenarParcelas(getParcelasOriginadas());
            for (ResultadoParcela parcela : getParcelasOriginadas()) {
                if (!bloquearEmissaoDamDaParcela(parcela)) {
                    DAM dam = processoParcelamentoFacade.getDamFacade()
                        .gerarDAMUnicoViaApi(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(),
                            parcela);
                    if (dam != null) {
                        dams.add(dam);
                    }
                }
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void comunicarProcuradoria() {
        try {
            processoParcelamentoFacade.comunicarProcuradoria(selecionado, parcelasOriginadas);
            FacesUtil.addOperacaoRealizada("Parcelamento enviado a procuradoria.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public boolean isValorMenorQueEntrada() {
        return assistenteSimulacaoParcelamento.isValorMenorQueEntrada();
    }

    public List<ComunicacaoSoftPlan> getComunicacaoSoftPlans() {
        return comunicacaoSoftPlans;
    }

    public void setComunicacaoSoftPlans(List<ComunicacaoSoftPlan> comunicacaoSoftPlans) {
        this.comunicacaoSoftPlans = comunicacaoSoftPlans;
    }

    public void permitirCancelarParcelamentos() {
        if (!selecionado.getPermitirCancelarParcelamento()) {
            selecionado.setPermitirCancelarParcelamento(true);
        } else {
            selecionado.setPermitirCancelarParcelamento(false);
        }

        if (selecionado.getFaixaDesconto() != null && selecionado.getFaixaDesconto().getId() == null)
            selecionado.setFaixaDesconto(null);

        getFacade().salvarPermissaoCancelamento(selecionado);
    }

    public boolean isVerificarSePermiteCancelarProcesso() {
        return selecionado.getPermitirCancelarParcelamento() || selecionado.isCancelado();
    }

    public boolean isParcelamentoAberto() {
        for (ResultadoParcela parcela : parcelasOriginadas) {
            if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean verificarPermissaoCancelarParcelamento() {
        if (isAutorizacaoCancelarParcelamento()) {
            for (ResultadoParcela parcela : parcelasOriginadas) {
                if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) || SituacaoParcela.PAGO.equals(parcela.getSituacaoEnumValue()) || SituacaoParcela.BAIXADO.equals(parcela.getSituacaoEnumValue())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public BigDecimal getValorTotalImpostoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorImposto());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorTaxa());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalJurosOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorJuros());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalMultaOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorMulta());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorCorrecao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorHonorarios());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalSemDescontoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorTotalSemDesconto());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDescontoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteSimulacaoParcelamento.getParcelasGeradas() != null) {
            for (ResultadoParcela originada : assistenteSimulacaoParcelamento.getParcelasGeradas()) {
                total = total.add(originada.getValorDesconto());
            }
        }
        return total;
    }

    public void validarPrimeiroVencimento() {
        if (selecionado.getVencimentoPrimeiraParcela() != null && processoParcelamentoFacade.isDiaNaoUtil(selecionado.getVencimentoPrimeiraParcela())) {
            FacesUtil.addOperacaoNaoPermitida("O Primeiro Vencimento deve ser em um dia útil.");
            while (processoParcelamentoFacade.isDiaNaoUtil(selecionado.getVencimentoPrimeiraParcela())) {
                selecionado.setVencimentoPrimeiraParcela(DataUtil.adicionaDias(selecionado.getVencimentoPrimeiraParcela(), 1));
            }
        }
        validarValorDeEntrada();
    }

    public void reativarParcelamentoCancelado() {
        selecionado.setUsuarioReativacao(processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataReativacao(processoParcelamentoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setEnderecoReativacao(processoParcelamentoFacade.getSistemaFacade().getIp());
        selecionado.setMotivoReativacao("");
        FacesUtil.executaJavaScript("reativarParcelamento.show()");
    }

    public void efetivarReativacaoParcelamento() {
        processoParcelamentoFacade.reativarProcessoParcelamento(selecionado, selecionado.getCancelamentoParcelamento());
        FacesUtil.addInfo("Processo de Parcelamento reativado com sucesso!", "");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    @Override
    public void limparCampoPessoaFisica() {
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
    }

    @Override
    public Long idPfParaAtualizacaoCadastral() {
        return prorietarioSelecionado != null ? prorietarioSelecionado.getId() : selecionado.getPessoa().getId();
    }

    public void fecharDialogSelecaoProprietarioCI() {
        FacesUtil.executaJavaScript("dialogoProprietariosCI.hide();");
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
        FacesUtil.atualizarComponente("Formulario");
    }

    public void selecionouProprietario(Propriedade prop) {
        FacesUtil.executaJavaScript("dialogoProprietariosCI.hide();");
        if (!getFacade().getPessoaFacade().cadastroAtualizado(prop.getPessoa().getAsPessoaFisica())) {
            prorietarioSelecionado = prop.getPessoa().getAsPessoaFisica();
            montarMensagemDialogAtualizacaoCadastroPF(prop.getPessoa().getAsPessoaFisica());
            abrirDialogAtualizacaoCadastralPF();
        }
    }
}
