package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoVencimentoRevisaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteEfetivacaoIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "revisaoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-revisao-iptu", pattern = "/revisao-calculo-de-iptu/novo/", viewId = "/faces/tributario/iptu/revisao/edita.xhtml"),
    @URLMapping(id = "editar-revisao-iptu", pattern = "/revisao-calculo-de-iptu/editar/#{revisaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/revisao/edita.xhtml"),
    @URLMapping(id = "ver-revisao-iptu", pattern = "/revisao-calculo-de-iptu/ver/#{revisaoIPTUControlador.id}/", viewId = "/faces/tributario/iptu/revisao/visualizar.xhtml"),
    @URLMapping(id = "listar-revisao-iptu", pattern = "/revisao-calculo-de-iptu/listar/", viewId = "/faces/tributario/iptu/revisao/lista.xhtml")
})
public class RevisaoIPTUControlador extends PrettyControlador<RevisaoCalculoIPTU> implements Serializable, CRUD {

    Future<AssistenteCalculadorIPTU> futureCalculo;
    Future<List<ValorDivida>> futureValorDivida;
    @EJB
    private NovoCalculoIPTUFacade calculoIPTUFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaRevisaoIPTU geraValorDivida;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private List<ResultadoParcela> resultados;
    private ProcessoCalculoIPTU processoCalculo;
    private List<ValorDivida> valoresDivida;
    private AssistenteCalculadorIPTU assistente;
    private Boolean permiteRevisar;
    private ConsultaDebitoControlador.TotalTabelaParcelas total;
    private BigDecimal totalNovasParcelas;
    private Future<Map<String, List>> futureResultados;
    private List<String> resultadosParcelas;
    private Map<String, List> mapaResultados;
    private List<DAM> dams;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private List<CadastroImobiliario> cadastroParaRevisao;
    private List<CadastroImobiliario> cadastroNaoRevisado;
    private Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroParaRevisao;
    private Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroNaoRevisado;
    private RevisaoCalculoIPTU revisaoPorLote;
    private List<RevisaoCalculoIPTU> revisoes;
    private ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU;
    private ProcessoRevisao processoRevisao;
    private boolean teste = false;
    private CadastroImobiliario cadastroImobiliario;

    public RevisaoIPTUControlador() {
        super(RevisaoCalculoIPTU.class);
    }

    public Map<String, List> getMapaResultados() {
        return mapaResultados;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;

    }

    public Map<CadastroImobiliario, List<ResultadoParcela>> getMapaCadastroParaRevisao() {
        return mapaCadastroParaRevisao;
    }

    public void setMapaCadastroParaRevisao(Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroParaRevisao) {
        this.mapaCadastroParaRevisao = mapaCadastroParaRevisao;
    }

    public Map<CadastroImobiliario, List<ResultadoParcela>> getMapaCadastroNaoRevisado() {
        return mapaCadastroNaoRevisado;
    }

    public void setMapaCadastroNaoRevisado(Map<CadastroImobiliario, List<ResultadoParcela>> mapaCadastroNaoRevisado) {
        this.mapaCadastroNaoRevisado = mapaCadastroNaoRevisado;
    }

    public List<String> getResultadosParcelas() {
        return resultadosParcelas;
    }

    public ProcessoRevisaoCalculoIPTU getProcessoRevisaoCalculoIPTU() {
        return processoRevisaoCalculoIPTU;
    }

    public void setProcessoRevisaoCalculoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU) {
        this.processoRevisaoCalculoIPTU = processoRevisaoCalculoIPTU;
    }

    public BigDecimal getTotalNovasParcelas() {
        return totalNovasParcelas;
    }

    public Boolean getPermiteRevisar() {
        return permiteRevisar;
    }

    public ConsultaDebitoControlador.TotalTabelaParcelas getTotal() {
        return total;
    }

    public List<ValorDivida> getValoresDivida() {
        return valoresDivida;
    }

    public AssistenteCalculadorIPTU getAssistente() {
        return assistente;
    }

    public ProcessoCalculoIPTU getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoIPTU processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public List<CadastroImobiliario> getCadastroParaRevisao() {
        return cadastroParaRevisao;
    }

    public void setCadastroParaRevisao(List<CadastroImobiliario> cadastroParaRevisao) {
        this.cadastroParaRevisao = cadastroParaRevisao;
    }

    public List<CadastroImobiliario> getCadastroNaoRevisado() {
        return cadastroNaoRevisado;
    }

    public void setCadastroNaoRevisado(List<CadastroImobiliario> cadastroNaoRevisado) {
        this.cadastroNaoRevisado = cadastroNaoRevisado;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public List<ResultadoParcela> getResultados() {
        return resultados;
    }

    public boolean isTeste() {
        return teste;
    }

    public void setTeste(boolean teste) {
        this.teste = teste;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/revisao-calculo-de-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoIPTUFacade;
    }

    @Override
    @URLAction(mappingId = "nova-revisao-iptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        resultados = Lists.newArrayList();
        selecionado.setUsuario(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataCriacao(sistemaFacade.getDataOperacao());
        selecionado.setCodigo(calculoIPTUFacade.getSingletonGeradorCodigo().getProximoCodigo(RevisaoCalculoIPTU.class, "codigo"));
    }

    @Override
    @URLAction(mappingId = "editar-revisao-iptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        selecionado = calculoIPTUFacade.recuperaRevisao(getId());
    }

    @Override
    @URLAction(mappingId = "ver-revisao-iptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        futureResultados = calculoIPTUFacade.consultaParcelas(selecionado);
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public void filtrar() {
        try {

            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            boolean permiteSobrescreverPago = usuarioSistemaFacade.validaAutorizacaoUsuario(usuarioCorrente, AutorizacaoTributario.PERMITIR_LANCAMENTO_IPTU_PAGO);

            validarFiltros();
            total = new ConsultaDebitoControlador.TotalTabelaParcelas();
            permiteRevisar = true;
            SituacaoParcela sit = null;
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastro().getId());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, selecionado.getExercicio().getAno());
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, configuracaoTributarioFacade.retornaUltimo().getDividaIPTU().getId());

            List<SituacaoParcela> situacaoes = Lists.newArrayList(SituacaoParcela.EM_ABERTO,
                SituacaoParcela.AGUARDANDO_REVISAO,
                SituacaoParcela.PAGO,
                SituacaoParcela.PAGO_REFIS,
                SituacaoParcela.PAGO_SUBVENCAO,
                SituacaoParcela.BAIXADO,
                SituacaoParcela.BAIXADO_OUTRA_OPCAO,
                SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA,
                SituacaoParcela.COMPENSACAO,
                SituacaoParcela.SUSPENSAO
            );

            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacaoes);
            resultados = consulta.executaConsulta().getResultados();
            if (!resultados.isEmpty()) {
                for (ResultadoParcela resultado : resultados) {
                    total.soma(resultado);
                    if (permiteSobrescreverPago && SituacaoParcela.PAGO.name().equals(resultado.getSituacaoNameEnum())
                        || permiteSobrescreverPago && SituacaoParcela.BAIXADO_OUTRA_OPCAO.name().equals(resultado.getSituacaoNameEnum())) {
                        continue;
                    }
                    if (!SituacaoParcela.EM_ABERTO.name().equals(resultado.getSituacaoNameEnum())
                        && !SituacaoParcela.AGUARDANDO_REVISAO.name().equals(resultado.getSituacaoNameEnum())) {
                        permiteRevisar = false;
                        sit = SituacaoParcela.fromDto(resultado.getSituacaoEnumValue());
                    }
                }
                if (permiteRevisar) {
                    FacesUtil.addOperacaoRealizada("Todas as parcelas do I.P.T.U. estão disponíveis para revisão, pressione o botão de 'Revisão de I.P.T.U.' para visualizar as novas parcelas e efetivar!");
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Há uma ou mais parcelas de I.P.T.U do exercício informado com situação de " + sit.getDescricao() + " para revisar o I.P.T.U todos os débitos devem estar EM ABERTO!");
                }
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não há débitos de I.P.T.U. lançados para o cadastro no exercício informado, a revisão de I.P.T.U.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor selecionar o cadastro imobiliário!");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor selecionar o exercício!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setCadastro((CadastroImobiliario) obj);
    }

    public void reverIPTU() {
        processoCalculo = new ProcessoCalculoIPTU();
        valoresDivida = Lists.newArrayList();
        Long idValorDivida = resultados.get(0).getIdValorDivida();
        ValorDivida valorDivida = calculoIPTUFacade.recuperarValorDivida(idValorDivida);
        selecionado.setValorDivida(valorDivida);
        CalculoIPTU calculo = calculoIPTUFacade.recuperaIPTU(valorDivida.getCalculo().getId());
        processoCalculo.setConfiguracaoEventoIPTU(calculo.getProcessoCalculoIPTU().getConfiguracaoEventoIPTU());
        processoCalculo.setDivida(calculoIPTUFacade.getConfiguracaoTributarioFacade().retornaUltimo().getDividaIPTU());
        processoCalculo.setConfiguracaoEventoIPTU(calculoIPTUFacade.getConfiguracaoEventoFacade().recuperar(processoCalculo.getConfiguracaoEventoIPTU().getId()));
        processoCalculo.setCadastroInicial(selecionado.getCadastro().getInscricaoCadastral());
        processoCalculo.setCadastroFinal(selecionado.getCadastro().getInscricaoCadastral());
        processoCalculo.setExercicio(selecionado.getExercicio());
        processoCalculo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        assistente = new AssistenteCalculadorIPTU(processoCalculo, 1);
        assistente.setPersisteCalculo(false);
        List<Propriedade> propriedades = calculoIPTUFacade.getProcessoIsencaoIPTUFacade().getCadastroImobiliarioFacade().recuperarProprietariosVigentes(selecionado.getCadastro());
        selecionado.getCadastro().setIdMaiorProprietario(propriedades.get(0).getPessoa().getId());
        List<CadastroImobiliario> cadastros = Lists.newArrayList(selecionado.getCadastro());
        futureCalculo = calculoIPTUFacade.calcularIPTU(cadastros, assistente);
        FacesUtil.executaJavaScript("iniciaTimer()");
    }

    public void verificaTerminoCalculo() {
        if (futureCalculo != null && futureCalculo.isDone()) {
            try {
                totalNovasParcelas = BigDecimal.ZERO;
                processoCalculo = futureCalculo.get().getProcessoCalculoIPTU();
                AssistenteEfetivacaoIPTU assistenteEfetivacaoIPTU = new AssistenteEfetivacaoIPTU();
                assistenteEfetivacaoIPTU.inicializar(processoCalculo.getCalculos().size(),
                    AssistenteEfetivacaoIPTU.TipoProcesso.EFETIVACAO);
                valoresDivida = geraValorDivida.geraDebito(assistenteEfetivacaoIPTU,
                    processoCalculo.getCalculosIPTU(), sistemaFacade.getExercicioCorrente(),
                    TipoVencimentoRevisaoIPTU.VENCIMENTO_ATUAL);
                for (ValorDivida vd : valoresDivida) {
                    for (ParcelaValorDivida pvd : vd.getParcelaValorDividas()) {
                        if (!pvd.getOpcaoPagamento().getPromocional()) {
                            totalNovasParcelas = totalNovasParcelas.add(pvd.getValor());
                        }
                    }
                }
                futureCalculo = null;
                FacesUtil.executaJavaScript("terminarTimer()");
                FacesUtil.atualizarComponente("formularioEfetivacao");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void verificaConsulta() throws ExecutionException, InterruptedException {
        if (futureResultados != null && futureResultados.isDone()) {
            mapaResultados = futureResultados.get();
            resultadosParcelas = Lists.newArrayList(mapaResultados.keySet());
            dams = Lists.newArrayList();
            FacesUtil.executaJavaScript("terminarTimer();");
        }
    }

    public void efetivar() throws Exception {
        if (valoresDivida == null || valoresDivida.isEmpty()) {
            FacesUtil.addAtencao("Não foi possível gerar parcelas para esse cadastro, verifique suas isenções ou configurações do IPTU");
            return;
        }
        for (ValorDivida vd : valoresDivida) {
            if (vd.getParcelaValorDividas() == null || vd.getParcelaValorDividas().isEmpty()) {
                FacesUtil.addAtencao("Não foi possível gerar parcelas para esse cadastro, verifique suas isenções ou configurações do IPTU");
                return;
            }
        }
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        processoCalculo.setDescricao("Revisão de Cálculo de I.P.T.U. " + (selecionado.getProcesso() != null ? selecionado.getProcesso() : ""));
        processoCalculo.setNumeroProtocolo(selecionado.getNumeroProtocolo());
        processoCalculo.setAnoProtocolo(selecionado.getAnoProtocolo());
        calculoDAO.gerarProcessoCalculo(processoCalculo);
        selecionado.setProcesso(selecionado.getNumeroProtocolo() + "/" + selecionado.getAnoProtocolo());
        calculoDAO.persisteTudo(processoCalculo.getCalculosIPTU());
        selecionado.setProcessoCalculo(processoCalculo);
        selecionado = calculoIPTUFacade.efetivarRevisao(selecionado);
        processoCalculo = calculoIPTUFacade.recuperarComDependencias(processoCalculo.getId());
        CalculoIPTU calculo = selecionado.getProcessoCalculo().getCalculosIPTU().get(0);
        for (ValorDivida vd : valoresDivida) {
            vd.setCalculo(calculo);
            geraValorDivida.salvaValorDivida(vd);

        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void imprimirDAM(DAM dam) {
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        try {
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dam);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void recuperaDams() {
        CalculoIPTU calculo = calculoIPTUFacade.recuperarCalculo(selecionado);
        DAM dam = damFacade.recuperaDAMpeloCalculo(calculo);
        if (dam == null) {
            damFacade.geraDAM(calculo);
        }
        if (dams == null || dams.isEmpty()) {
            for (ResultadoParcela resultado : (List<ResultadoParcela>) mapaResultados.get("Originadas")) {
                DAM dans = damFacade.recuperaDAMPeloIdParcela(resultado.getIdParcela());
                dams.add(dans);
            }
        }
    }

    public List<DAM> getDams() {
        return dams;
    }

    public boolean podeBuscarCadastros() {
        if (processoRevisaoCalculoIPTU.getInscricaoInicial().trim().equals("") || processoRevisaoCalculoIPTU.getInscricaoFinal().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("Informe a Inscrição Cadastral Inicial e Inscrição Cadastral Final para Revisão de Cálculo de IPTU.");
            return false;
        }
        return true;
    }

    public List<CadastroImobiliario> getCadastros() {
        List<CadastroImobiliario> cadastros = Lists.newArrayList();
        for (Map.Entry<CadastroImobiliario, List<ResultadoParcela>> mapa : mapaCadastroParaRevisao.entrySet()) {
            cadastros.add(mapa.getKey());
        }
        return cadastros;
    }


    public void verificaTerminoCalculoPorLote() {
        if (futureCalculo != null && futureCalculo.isDone()) {
            FacesUtil.executaJavaScript("terminarTimer()");
        }
    }

    public void geraDebitos() {
        try {
            processoCalculo = futureCalculo.get().getProcessoCalculoIPTU();
            AssistenteEfetivacaoIPTU assistenteEfetivacaoIPTU = new AssistenteEfetivacaoIPTU();
            assistenteEfetivacaoIPTU.inicializar(processoCalculo.getCalculos().size(), AssistenteEfetivacaoIPTU.TipoProcesso.EFETIVACAO);
            valoresDivida = geraValorDivida.geraDebito(assistenteEfetivacaoIPTU,
                processoCalculo.getCalculosIPTU(), sistemaFacade.getExercicioCorrente(),
                TipoVencimentoRevisaoIPTU.VENCIMENTO_ATUAL);
            totalNovasParcelas = BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Erro ao gerar débitos na revisão de iptu. {}", e.getMessage());
            logger.debug("Detalhes do erro ao gerar débitos na revisão de iptu.", e);
        }
        try {
            efetivarRevisaoPorLote(processoCalculo);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public boolean temCadastro() {
        return !cadastroParaRevisao.isEmpty();
    }

    public void efetivarRevisaoPorLote(ProcessoCalculoIPTU processo) throws Exception {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        for (CalculoIPTU cal : processo.getCalculosIPTU()) {
            cal.setReferencia(processo.getDescricao());
        }
        calculoDAO.gerarProcessoCalculo(processo);
        calculoDAO.persisteTudo(processo.getCalculosIPTU());


        for (ValorDivida vd : valoresDivida) {
            geraValorDivida.salvaValorDivida(vd);
        }

        ProcessoCalculoIPTU processoCalculoIPTU = calculoIPTUFacade.recuperarComDependencias(processo.getId());
        for (RevisaoCalculoIPTU revisao : revisoes) {
            processoRevisao = new ProcessoRevisao();
            revisao.setProcessoCalculo(processoCalculoIPTU);
            revisao.setExercicio(sistemaFacade.getExercicioCorrente());
            revisao.setUsuario(sistemaFacade.getUsuarioCorrente());
            revisao.setDataCriacao(new Date());
            revisao.setCodigo(calculoIPTUFacade.getSingletonGeradorCodigo().getProximoCodigo(RevisaoCalculoIPTU.class, "codigo"));
            revisao = calculoIPTUFacade.efetivaRevisaoLote(revisao);
            processoRevisao.setRevisaoCalculoIPTU(revisao);
            processoRevisao.setProcessoRevisaoCalculoIPTU(processoRevisaoCalculoIPTU);
            processoRevisaoCalculoIPTU.getProcessoRevisao().add(processoRevisao);
        }
        processoRevisaoCalculoIPTU.setUsuario(sistemaFacade.getUsuarioCorrente());
        processoRevisaoCalculoIPTU.setDataProcesso(new Date());
        processoRevisaoCalculoIPTU.setCodigo(calculoIPTUFacade.getSingletonGeradorCodigo().getProximoCodigo(ProcessoRevisaoCalculoIPTU.class, "codigo"));

        processoRevisaoCalculoIPTU = calculoIPTUFacade.merge(processoRevisaoCalculoIPTU);
        cancelarDebitos(revisoes);
        FacesUtil.executaJavaScript("statusDialog.hide()");
        FacesUtil.executaJavaScript("dialogo.hide()");
        FacesUtil.addOperacaoRealizada("Revisão de Cálculo de IPTU realizada com sucesso.");
        visualizar(processoRevisaoCalculoIPTU);
    }

    private void cancelarDebitos(List<RevisaoCalculoIPTU> revisao) {
        for (RevisaoCalculoIPTU revisaoCalculoIPTU : revisao) {
            calculoIPTUFacade.cancelarParcelas(revisaoCalculoIPTU);
        }
    }

    public void visualizar(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU) {
        FacesUtil.redirecionamentoInterno("/revisao-calculo-de-iptu-lote/ver/" + processoRevisaoCalculoIPTU.getId() + "/");
    }

    public void listar() {
        FacesUtil.redirecionamentoInterno("/revisao-calculo-de-iptu-lote/listar/");
    }

    public void verRevisaoIptu(ProcessoRevisao processoRevisao) {
        FacesUtil.redirecionamentoInterno("/revisao-calculo-de-iptu/ver/" + processoRevisao.getRevisaoCalculoIPTU().getId() + "/");
    }

    public void copiarInscricaoInicialParaFinal() {
        processoRevisaoCalculoIPTU.setInscricaoFinal(processoRevisaoCalculoIPTU.getInscricaoInicial());
    }

    public String endereco(CadastroImobiliario cadastro) {
        String endereco = calculoIPTUFacade.getProcessoIsencaoIPTUFacade().getCadastroImobiliarioFacade().recuperaEnderecoCadastro(cadastro.getId());
        if (endereco.contains("Loteamento")) {
            return endereco.substring(0, endereco.indexOf("Loteamento") - 2);
        }
        return endereco;
    }

    public void carregarDebitos(RevisaoCalculoIPTU revisao) {
        revisaoPorLote = revisao;
        futureResultados = calculoIPTUFacade.consultaParcelas(revisao);
        cadastroImobiliario = revisao.getCadastro();
        FacesUtil.executaJavaScript("iniciaTimer()");
        FacesUtil.atualizarComponente("debitos");
    }

    public void navegacaoNovo() {
        FacesUtil.redirecionamentoInterno("/revisao-calculo-de-iptu-lote/novo/");
    }

    public void verificaConsultaNoDialog() throws ExecutionException, InterruptedException {
        if (futureResultados != null && futureResultados.isDone()) {
            mapaResultados = futureResultados.get();
            resultadosParcelas = Lists.newArrayList(mapaResultados.keySet());
            dams = Lists.newArrayList();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("debitos.show()");
            FacesUtil.executaJavaScript("terminarTimer();");
        }
    }

    public void recuperaDamsProcessoPorLote() {
        CalculoIPTU calculo = calculoIPTUFacade.recuperarCalculo(revisaoPorLote);
        DAM dam = damFacade.recuperaDAMpeloCalculo(calculo);

        if (dam == null) {
            damFacade.geraDAM(calculo);
        }
        if (dams == null || dams.isEmpty()) {
            for (ResultadoParcela resultado : (List<ResultadoParcela>) mapaResultados.get("Originadas")) {
                DAM dans = damFacade.recuperaDAMPeloIdParcela(resultado.getIdParcela());
                dams.add(dans);
            }
        }
    }


}

