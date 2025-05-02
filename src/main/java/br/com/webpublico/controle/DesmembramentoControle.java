package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.MotivoInativacaoImovel;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author gustavo
 */
//
///
@ManagedBean(name = "desmembramentoControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDesmembramentodeLote", pattern = "/desmembramento/novo/", viewId = "/faces/tributario/cadastromunicipal/desmembramento/edita.xhtml"),
    @URLMapping(id = "listarDesmembramento", pattern = "/desmembramento/listar/", viewId = "/faces/tributario/cadastromunicipal/desmembramento/lista.xhtml"),
    @URLMapping(id = "verDesmembramento", pattern = "/desmembramento/ver/#{desmembramentoControle.id}/", viewId = "/faces/tributario/cadastromunicipal/desmembramento/visualizar.xhtml"),
    @URLMapping(id = "editarDesmembramento", pattern = "/desmembramento/editar/#{desmembramentoControle.id}/", viewId = "/faces/tributario/cadastromunicipal/desmembramento/edita.xhtml")
})
public class DesmembramentoControle extends PrettyControlador<Desmembramento> implements Serializable, CRUD {

    private CadastroImobiliario original;
    private List<CadastroImobiliario> resultantes;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private TestadaFacade testadaFacade;
    @EJB
    private ConstrucaoFacade construcaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DesmembramentoFacade desmembramentoFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    private Logradouro logradouro;
    private List<Testada> testadasOriginario, testadasDisponiveis;
    private List<Construcao> construcoesDisponiveis;
    private Lote novoLote;
    private CadastroImobiliario novoCadastroImobiliario;
    private Testada novaTestada;
    private Construcao novaConstrucao;
    private ConverterGenerico converterTestada, converterConstrucao;
    private BigDecimal tamanhoTestada;
    private final String idMsgValidacao = "formulario";
    private Historico historico;
    private ConfiguracaoTributario configuracaoTributario;
    private List<TestadasAdiconadas> listaTestadasAdiconadas;
    private List<Testada> listaTestadaRecuperada;

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public DesmembramentoControle() {
        super(Desmembramento.class);
    }

    @URLAction(mappingId = "verDesmembramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarDesmembramento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public final void limparDependencias() {
        novoCadastroImobiliario = new CadastroImobiliario();
        novoCadastroImobiliario.setConstrucoes(new ArrayList<Construcao>());
        novoLote = new Lote();
        novoLote.setOriginarios(new ArrayList<LoteOriginario>());
        novoLote.setTestadas(new ArrayList<Testada>());
        novaTestada = new Testada();
        novaConstrucao = new Construcao();
    }

    public BigDecimal getTamanhoTestada() {
        return tamanhoTestada;
    }

    public void setTamanhoTestada(BigDecimal tamanhoTestada) {
        this.tamanhoTestada = tamanhoTestada;
    }

    public Construcao getNovaConstrucao() {

        return novaConstrucao;
    }

    public void setNovaConstrucao(Construcao novaConstrucao) {
        this.novaConstrucao = novaConstrucao;
    }

    public Testada getNovaTestada() {
        return novaTestada;
    }

    public void setNovaTestada(Testada novaTestada) {
        this.novaTestada = novaTestada;
    }

    public List<Testada> getTestadasOriginario() {
        return testadasOriginario;
    }

    public void setTestadasOriginario(List<Testada> testadasOriginario) {
        this.testadasOriginario = testadasOriginario;
    }

    public CadastroImobiliario getNovoCadastroImobiliario() {
        return novoCadastroImobiliario;
    }

    public void setNovoCadastroImobiliario(CadastroImobiliario novoCadastroImobiliario) {
        this.novoCadastroImobiliario = novoCadastroImobiliario;
    }

    public Lote getNovoLote() {
        return novoLote;
    }

    public void setNovoLote(Lote novoLote) {
        this.novoLote = novoLote;
    }

    public CadastroImobiliario getOriginal() {
        return original;
    }

    public void setOriginal(CadastroImobiliario original) {
        this.original = original;
    }

    public List<CadastroImobiliario> getResultantes() {
        return resultantes;
    }

    public void setResultantes(List<CadastroImobiliario> resultantes) {
        this.resultantes = resultantes;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public DesmembramentoFacade getDesmembramentoFacade() {
        return desmembramentoFacade;
    }

    public void setDesmembramentoFacade(DesmembramentoFacade desmembramentoFacade) {
        this.desmembramentoFacade = desmembramentoFacade;
    }

    public Converter getConverterConstrucao() {
        if (converterConstrucao == null) {
            converterConstrucao = new ConverterGenerico(Construcao.class, construcaoFacade);
        }
        return converterConstrucao;
    }

    public Converter getConverterTestada() {
        if (converterTestada == null) {
            converterTestada = new ConverterGenerico(Testada.class, testadaFacade);
        }
        return converterTestada;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public List<Construcao> recuperaConstrucoes(CadastroImobiliario bci) {
        return cadastroImobiliarioFacade.recuperarConstrucoes(bci);
    }

    public Converter getConverterCadastroImobiliario() {
        if (this.converterCadastroImobiliario == null) {
            this.converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public void recuperarTestadas() {
        listaTestadaRecuperada = testadaFacade.listaTestadasDoLote(original.getLote());
    }

    public List<Testada> recuperaTestadas(Lote lote) {
        return testadaFacade.listaTestadasDoLote(lote);
    }

    public List<Construcao> getConstrucoesDisponiveis() {
        return construcoesDisponiveis;
    }

    public void setConstrucoesDisponiveis(List<Construcao> construcoesDisponiveis) {
        this.construcoesDisponiveis = construcoesDisponiveis;
    }

    public void handleSelect() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        original = cadastroImobiliarioFacade.recuperar(original.getId());
        recuperarTestadas();
        consultaParcela.addParameter(ConsultaParcela.Campo.BCI_ID, ConsultaParcela.Operador.IGUAL, original.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO.name());
        List<ResultadoParcela> resultadoConsulta = consultaParcela.executaConsulta().getResultados();
        if (resultadoConsulta != null && !resultadoConsulta.isEmpty()) {
            original = new CadastroImobiliario();
            testadasOriginario = new ArrayList();
            testadasDisponiveis = new ArrayList();
            construcoesDisponiveis = new ArrayList<>();
            resultantes = new ArrayList<CadastroImobiliario>();
            limparDependencias();
            RequestContext.getCurrentInstance().execute("dlgDebitosFracasso.show()");
            return;
        }
        testadasOriginario = recuperaTestadas(original.getLote());
        for (Testada t : testadasOriginario) {
            if (t.getPrincipal()) {
                if (t.getFace() != null) {
                    logradouro = t.getFace().getLogradouroBairro().getLogradouro();
                }
            }
        }

        testadasDisponiveis = testadasOriginario;
        tamanhoTestada = BigDecimal.ZERO;

        if (original.getConstrucoes().isEmpty()) {
            construcoesDisponiveis = new ArrayList<>();
        } else {
            construcoesDisponiveis = original.getConstrucoes();
        }

        resultantes = new ArrayList<CadastroImobiliario>();
        lancaMensagemCasoTenhaPenhora();
        limparDependencias();
    }

    public List<SelectItem> getTestadas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Testada t : testadasDisponiveis) {
            toReturn.add(new SelectItem(t, t.getFace().getLogradouroBairro().getLogradouro().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getContrucoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));

        for (Construcao c : construcoesDisponiveis) {
            toReturn.add(new SelectItem(c, c.getDescricao()));
        }

        return toReturn;
    }

    public boolean existeTestadaNaLista() {
        for (Testada testada : novoLote.getTestadas()) {
            if (novaTestada.getFace().equals(testada.getFace())) {
                return true;
            }
        }
        return false;

    }

    public void adicionaTestada() {
        if (!validaTestada()) {
            return;
        }
        Testada testadaTemp = new Testada();
        testadaTemp.setLote(novoLote);
        testadaTemp.setTamanho(tamanhoTestada);
        testadaTemp.setFace(novaTestada.getFace());
        testadaTemp.setPrincipal(novaTestada.getPrincipal());
        novoLote.getTestadas().add(testadaTemp);
        novaTestada = new Testada();
        tamanhoTestada = BigDecimal.ZERO;
    }

    private boolean validaTestada() {
        boolean resultado = true;

        if (novaTestada == null) {
            FacesUtil.addError("Não foi possível continuar.", "Selecione uma Testada");
            resultado = false;
        }
        if (tamanhoTestada.compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addError("Não foi possível continuar.", "O tamanho da testada deve ser maior que 0(zero)");
            resultado = false;
        }
        if (existeTestadaNaLista()) {
            FacesUtil.addError("Não foi possível continuar.", "A testada selecionada já encontra-se registrada na lista!");
            resultado = false;
        }
        return resultado;
    }

    public void adicionaConstrucao() {
        Construcao construcaoTemp = new Construcao();
        construcaoTemp.setAnoConstrucao(novaConstrucao.getAnoConstrucao());
        construcaoTemp.setAreaConstruida(novaConstrucao.getAreaConstruida());
        construcaoTemp.setDataRegistro(new Date());
        construcaoTemp.setDescricao(novaConstrucao.getDescricao());
        construcaoTemp.setEnglobado(novaConstrucao.getEnglobado());
        construcaoTemp.setImovel(novoCadastroImobiliario);

        construcaoTemp.setAtributos(new HashMap<Atributo, ValorAtributo>());
        for (Atributo at : novaConstrucao.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = novaConstrucao.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            construcaoTemp.getAtributos().put(at, novoValorAtributo);
        }
        novoCadastroImobiliario.getConstrucoes().add(construcaoTemp);
        construcoesDisponiveis.remove(novaConstrucao);
        novaConstrucao = new Construcao();
    }

    public double getTamanhoMaximoTestada() {
        return 10000.0;
    }

    public void adicionaNovoResultante() {
        if (!validaNovoLote()) {
            return;
        }
        if (getInativaCadastroOriginal()) {
            original = cadastroImobiliarioFacade.recuperar(original.getId());
            resultantes.add(cadastroImobiliarioFacade.geraDesmembramentoIncorporadoNovo(original, novoLote, novoCadastroImobiliario));
        } else {
            resultantes.add(cadastroImobiliarioFacade.geraNovoPorDesmembramento(original, novoLote, novoCadastroImobiliario));
        }
        limparDependencias();
    }

    private boolean validaNovoLote() {
        boolean resultado = true;
        if (novoCadastroImobiliario.getInscricaoCadastral() == null || novoCadastroImobiliario.getInscricaoCadastral().trim().length() == 0) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Campo Nova Inscricao Cadastra Obrigatório");
            resultado = false;
        }
        if (novoCadastroImobiliario.getCodigo() == null || novoCadastroImobiliario.getCodigo().trim().length() == 0) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Campo Código do novo Cadastro Imobiliário Obrigatório");
            resultado = false;
        }
        if (novoLote.getAreaLote() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Campo Área do novo Lote Obrigatório");
            resultado = false;
        }

        if (original != null && original.getId() != null) {
            Double areaTotalDesmembrada = (getAreaTotalResultantes() + (novoLote.getAreaLote() != null ? novoLote.getAreaLote() : 0)) - original.getLote().getAreaLote();
            if (areaTotalDesmembrada > 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A área informada para este lote ultrapassa o total em " + areaTotalDesmembrada);
                resultado = false;
            }
        }

        if (novoLote.getCodigoLote() == null || novoLote.getCodigoLote().trim().length() == 0) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Campo Codigo do Novo Lote é Obrigatório");
            resultado = false;
        }
        if (novoLote.getTestadas() == null || novoLote.getTestadas().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Ao menos UMA Testada deve ser Adicionada ao Novo Lote");
            resultado = false;
        }
        //System.out.println("novoCadastroImobiliario " + novoCadastroImobiliario);
        if (novoCadastroImobiliario != null && existeBCIRegistrado(novoCadastroImobiliario.getInscricaoCadastral())) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A inscrição cadastral informada para o novo cadastro imobiliário já encontra-se registrada");
            resultado = false;
        }
        if (existeBCINoResultante()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A inscrição cadastral informada já encontra-se registrada na lista de novos lotes adicionados!");
            resultado = false;
        }
        return resultado;
    }

    public boolean validaTamanhoTestadaDesmembramento() {
        boolean retorno = true;
        if (tamanhoTestadasDosResultates().compareTo(tamanhoTestadasLoteOriginal()) > 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A soma dos tamanhos das testadas dos novos lotes ultrapassou o tamanho das testadas do lote originário!");
            retorno = false;
        }
        if (getInativaCadastroOriginal()) {
            if (tamanhoTestadasDosResultates().compareTo(tamanhoTestadasLoteOriginal()) < 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A soma dos tamanhos das testadas dos novos lotes não alcançou o tamanho das testadas do lote originário!");
                retorno = false;
            }
        }
        return retorno;
    }

    public void validacaoSalvar() {
        if (configuracaoTributario.getId() == null) {
            return;
        } else if (getInativaCadastroOriginal()) {
            if (!validaDesmembramento()) {
                return;
            }
        }
        if (!validaTamanhoTestadaDesmembramento()) {
            return;
        }
        if (temPenhora()) {
            return;
        }
        RequestContext.getCurrentInstance().execute("dlgHistorico.show()");
    }


    public boolean temPenhora() {
        if (cadastroImobiliarioFacade.imovelComPenhora(original)) {
            lancaMensagemCasoTenhaPenhora();
            return true;
        }
        return false;
    }

    @Override
    public void salvar() {
        if (original != null && original.getId() != null && !resultantes.isEmpty()) {
            if (getInativaCadastroOriginal()) {
                RequestContext.getCurrentInstance().execute("concluir.show()");
            } else {
                validacaoSalvar();
            }
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não há dados suficientes para processar este comando!");
        }
    }

    public void salvarDesmembramento() {
        if (!validaHistorico()) {
            return;
        }
        BigDecimal areaLoteOriginario = new BigDecimal(original.getLote().getAreaLote());
        if (!getInativaCadastroOriginal()) {
            original = cadastroImobiliarioFacade.geraDesmembramentoIncorporadoOrigem(original, resultantes);
        } else {
            original.setAtivo(Boolean.FALSE);
            original.setDataInativacao(new Date());
            original.setMotivoInativacao(MotivoInativacaoImovel.DESMEMBRAMENTO);
            original.setHistoricos(new ArrayList<Historico>());
            original.getHistoricos().add(historico);
        }
        geraHistorico(original);
        salvar(original);

        Desmembramento desmembramento = new Desmembramento();
        desmembramento.setOriginal(original);


        desmembramento.setUsuarioResponsavel(sistemaFacade.getUsuarioCorrente());
        desmembramento.setDataDesmembramento(sistemaFacade.getDataOperacao());

        for (CadastroImobiliario ci : resultantes) {
            ItemDesmembramento item = new ItemDesmembramento();
            item.setDesmembramento(desmembramento);
            item.setOriginario(ci);
            desmembramento.getItens().add(item);
            geraHistorico(ci);
        }

        desmembramentoFacade.salvar(desmembramento);
        RequestContext.getCurrentInstance().execute("dlgHistorico.hide()");
        RequestContext.getCurrentInstance().execute("dlgHistoricoSucesso.show()");
        FacesUtil.addInfo("Operação Realizada!", "Registro salvo com sucesso");
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public void geraHistorico(CadastroImobiliario c) {
        Historico h = new Historico();
        h.setCadastro(c);
        h.setSolicitante(historico.getSolicitante());
        h.setDataRegistro(historico.getDataRegistro());
        h.setDataSolicitacao(historico.getDataSolicitacao());
        h.setDigitador(historico.getDigitador());
        h.setMotivo(historico.getMotivo());
        c.getHistoricos().add(h);
    }

    private boolean validaHistorico() {
        boolean resultado = true;
        if (historico.getDigitador() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o digitador");
        }
        if (historico.getSolicitante() == null || historico.getSolicitante().isEmpty()) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe o solicitante");
        }
        if (historico.getDataRegistro() != null && historico.getDataSolicitacao() != null && (historico.getDataSolicitacao().compareTo(historico.getDataRegistro()) == 1)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar - A data de solicitação é maior que a data de registro.", ""));
            resultado = false;
        }
        return resultado;
    }

    private boolean validaDesmembramento() {
        boolean resultado = true;
        if (cadastroImobiliarioFacade.imovelComPenhora(original)) {
            lancaMensagemCasoTenhaPenhora();
            resultado = false;
        }
        if (original == null || original.getId() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Selecione o cadastro imobiliário originário");
            resultado = false;
        }
        if (original.getLote() == null || original.getLote().getId() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O cadastro imobiliário selecionado não possui um lote");
            resultado = false;
        }
        int naoAlocadas = construcoesDisponiveis.size();
        if (naoAlocadas > 0) {
            FacesContext.getCurrentInstance().addMessage(idMsgValidacao, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Gravar Desmembramento", naoAlocadas > 1 ? ("Existem " + naoAlocadas + " construções ainda não alocadas") : ("Existe uma construção ainda não alocada")));
            resultado = false;
        }
        double diferencaAreaLote = original.getLote().getAreaLote().doubleValue() - getAreaTotalResultantes();
        if (diferencaAreaLote != 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Área Total dos Lotes Lançados diferente da Área do Lote Original");
            resultado = false;
        }
        String inscricoesDuplicadas = existeBCIRegistradoNoResultante();
        if (!inscricoesDuplicadas.isEmpty()) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A(s) Inscrição(ões) [" + inscricoesDuplicadas + "] para os novos lotes já encontram registradas!");
        }
        return resultado;
    }
//
//    private String validaTodasTestadas() {
//        StringBuilder sbResultado = new StringBuilder("");
//        for (Testada testada : testadasOriginario) {
//            if ((testada.getTamanho().subtract(tamanhoTestadas.get(testada))).signum() != 0) {
//                sbResultado.append(testada.getFace().getLogradouro()).append(", ");
//            }
//        }
//        if (sbResultado.length() > 0) {
//            sbResultado.delete(sbResultado.length() - 2, sbResultado.length() - 1);
//        }
//
//        return sbResultado.toString();
//    }

    public void salvar(CadastroImobiliario ci) {
        try {
            cadastroImobiliarioFacade.salvar(ci);
        } catch (Exception e) {
            RequestContext.getCurrentInstance().execute("dlgHistoricoFracasso.show()");
            FacesContext.getCurrentInstance().addMessage(idMsgValidacao, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossivel continuar", "Erro: " + e.getMessage()));
        }
    }

    public void removeResultante(CadastroImobiliario imobiliario) {
        resultantes.remove(imobiliario);
    }

    public Double getAreaTotalResultantes() {
        Double resultado = 0.0;
        for (CadastroImobiliario resultante : resultantes) {
            resultado += resultante.getLote().getAreaLote() != null ? resultante.getLote().getAreaLote() : 0.0;
        }
        return resultado;
    }

    private void lancaMensagemCasoTenhaPenhora() {
        if (cadastroImobiliarioFacade.imovelComPenhora(original)) {
            String mensagem = "O imóvel informado encontra-se penhorado ";
            Penhora p = cadastroImobiliarioFacade.retornaPenhoraDoImovel(original);
            if (p != null && p.getId() != null) {
                mensagem += "conforme, Processo nº. " + p.getNumeroProcesso() + ", em data de " + new SimpleDateFormat("dd/MM/yyyy").format(p.getDataPenhora()) + ", pelo Autor " + p.getAutor().getNome();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar", mensagem));
        }
        selecionado = null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/desmembramento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return getDesmembramentoFacade();
    }

    @URLAction(mappingId = "novoDesmembramentodeLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        listaTestadaRecuperada = new ArrayList<>();
        construcoesDisponiveis = new ArrayList<>();
        original = new CadastroImobiliario();
        this.configuracaoTributario = this.configuracaoTributarioFacade.retornaUltimo();
        novoCadastroImobiliario = new CadastroImobiliario();
        original.setLote(new Lote());
        original.getLote().setQuadra(new Quadra());
        original.getLote().setSetor(new Setor());
        original.getLote().getQuadra().setLoteamento(new Loteamento());
        original.getLote().setTestadas(new ArrayList<Testada>());
        original.setConstrucoes(new ArrayList<Construcao>());
        original.setHistoricos(new ArrayList<Historico>());
        historico = new Historico(1L);
        novaConstrucao = new Construcao();
        for (Testada testada : original.getLote().getTestadas()) {
            testada.setFace(new Face());
        }
        testadasOriginario = new ArrayList<>();
        testadasDisponiveis = new ArrayList<>();
        construcoesDisponiveis = new ArrayList<>();
        tamanhoTestada = BigDecimal.ZERO;
        resultantes = new ArrayList<>();
        limparDependencias();
        historico.setDigitador(sistemaFacade.getUsuarioCorrente());
    }

    public void removerConstrucao(Construcao c) {
        novoCadastroImobiliario.getConstrucoes().remove(c);
        handleSelect();
    }

    public Boolean getInativaCadastroOriginal() {
        if (configuracaoTributario != null && configuracaoTributario.getId() != null) {
            return !configuracaoTributario.getUsaCadOriginalDesmembramento();
        }
        return false;
    }

    public boolean existeBCIRegistrado(String inscricaoCadastral) {
        //System.out.println("meotdo");
        //System.out.println("inscricao cadastral " + inscricaoCadastral);
        CadastroImobiliario bci;
        if (!inscricaoCadastral.equals("")) {
            bci = cadastroImobiliarioFacade.recuperaPorInscricao(inscricaoCadastral);
        } else {
            bci = null;
        }
        //System.out.println("bci " + bci);
        return bci != null && bci.getId() != null;
    }

    public boolean existeBCINoResultante() {
        for (CadastroImobiliario cadastroImobiliario : resultantes) {
            if (cadastroImobiliario.getInscricaoCadastral().equals(novoCadastroImobiliario.getInscricaoCadastral())) {
                return true;
            }
        }
        return false;
    }

    public String existeBCIRegistradoNoResultante() {
        String retorno = "";
        String concatena = "";
        for (CadastroImobiliario cadastroImobiliario : resultantes) {
            if (existeBCIRegistrado(cadastroImobiliario.getInscricaoCadastral())) {
                retorno = retorno + concatena + cadastroImobiliario.getInscricaoCadastral();
                concatena = ", ";
            }
        }
        return retorno;
    }

    public void removerTestada(Testada testada) {
        novoLote.getTestadas().remove(testada);
    }

    public BigDecimal tamanhoTestadasDosResultates() {
        BigDecimal total = BigDecimal.ZERO;
        for (CadastroImobiliario cadastroImobiliario : resultantes) {
            Lote lote = cadastroImobiliario.getLote();
            for (Testada testada : lote.getTestadas()) {
                total = total.add(testada.getTamanho());
            }
        }
        return total;
    }

    public BigDecimal tamanhoTestadasEmMemoria() {
        BigDecimal total = BigDecimal.ZERO;
        for (Testada testada : novoLote.getTestadas()) {
            total = total.add(testada.getTamanho());
        }
        return total;
    }

    public BigDecimal tamanhoTestadasLoteOriginal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Testada testada : listaTestadaRecuperada) {
            total = total.add(testada.getTamanho());
        }
        return total;
    }

    public class TestadasAdiconadas {

        private BigDecimal tamanho;
        private Logradouro logradouro;
        private String inscricaocadastral;

        public BigDecimal getTamanho() {
            return tamanho;
        }

        public void setTamanho(BigDecimal tamanho) {
            this.tamanho = tamanho;
        }

        public Logradouro getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(Logradouro logradouro) {
            this.logradouro = logradouro;
        }

        public String getInscricaocadastral() {
            return inscricaocadastral;
        }

        public void setInscricaocadastral(String inscricaocadastral) {
            this.inscricaocadastral = inscricaocadastral;
        }
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }
}
