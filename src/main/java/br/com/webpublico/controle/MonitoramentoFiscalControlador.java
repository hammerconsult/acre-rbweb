package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImpressaoDemonstrativoMonitoramentoFiscal;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author octavio
 */
@ManagedBean(name = "monitoramentoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMonitoramentoFiscal", pattern = "/tributario/fiscalizacao/monitoramento-fiscal/novo/",
        viewId = "/faces/tributario/fiscalizacao/monitoramentofiscal/edita.xhtml"),
    @URLMapping(id = "editarMonitoramentoFiscal", pattern = "/tributario/fiscalizacao/monitoramento-fiscal/editar/#{monitoramentoFiscalControlador.id}/",
        viewId = "/faces/tributario/fiscalizacao/monitoramentofiscal/edita.xhtml"),
    @URLMapping(id = "listarMonitoramentoFiscal", pattern = "/tributario/fiscalizacao/monitoramento-fiscal/listar/",
        viewId = "/faces/tributario/fiscalizacao/monitoramentofiscal/lista.xhtml"),
    @URLMapping(id = "verMonitoramentoFiscal", pattern = "/tributario/fiscalizacao/monitoramento-fiscal/ver/#{monitoramentoFiscalControlador.id}/",
        viewId = "/faces/tributario/fiscalizacao/monitoramentofiscal/visualizar.xhtml")
})
public class MonitoramentoFiscalControlador extends PrettyControlador<MonitoramentoFiscal> implements Serializable, CRUD {

    @EJB
    private MonitoramentoFiscalFacade monitoramentoFiscalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private LevantamentoUFMMonitoramentoFiscal[] levantamentosUFMSelecionados;
    private ParametroMonitoramentoFiscal parametroMonitoramentoFiscal;
    private TipoLevantamentoContabilMonitoramentoFiscal tipoLevantamentoContabilMonitoramentoFiscal;
    private TotalizadorLancamentoContabil totalizador;
    private List<TotalizadorLancamentoContabil> listaTotalizador;
    private Map<MonitoramentoFiscalControlador.ValoresIssPago, BigDecimal> valoresPagos;
    private Map<String, ItemCalculoIss> aliquotasVariadas;
    private Map<Integer, Integer> diaVencimentoPorAno = Maps.newHashMap();
    private RegistroLancamentoContabilMonitoramentoFiscal registro;
    private String alvaraLocalizacao;
    private String alvaraFuncionamento;
    private String alvaraVigilanciaSanitaria;
    private String iptuCadastroImobiliarioDoCadastroEconomico;
    private boolean confirmouDialogEmpresaIrregular;

    public MonitoramentoFiscalControlador() {
        super(MonitoramentoFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return monitoramentoFiscalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/fiscalizacao/monitoramento-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        listaTotalizador = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getDataInicialMonitoramento() != null) {
            if (selecionado.getDataArbitramento() == null || selecionado.getUfmArbitramento() == null
                || selecionado.getLevantamentosUFM() == null || selecionado.getLevantamentosUFM().isEmpty()) {
                carregarMesesPorPeriodo();
            }
        }
        carregarVerificacaoDebitos();
        listaTotalizador = Lists.newArrayList();
    }

    @URLAction(mappingId = "verMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        selecionado = monitoramentoFiscalFacade.salvarRetornando(selecionado);
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        FacesUtil.redirecionamentoInterno(getUrlAtual());
    }

    public void fechouDialogEmpresaIrregular() {
        if (selecionado.getEmpresaIrregular() && !confirmouDialogEmpresaIrregular) {
            selecionado.setIrregularidade(null);
            selecionado.setObservacaoIrregularidade(null);
            selecionado.setEmpresaIrregular(false);
        }
    }

    private void validarEmpresaIrregular() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getIrregularidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a irregularidade");
        }
        ve.lancarException();
    }

    public void confirmarDialogEmpresaIrregular() {
        try {
            validarEmpresaIrregular();
            selecionado.setUsuarioIrregularidade(sistemaFacade.getUsuarioCorrente());
            confirmouDialogEmpresaIrregular = true;
            monitoramentoFiscalFacade.adicionarSituacaoNaEmpresaIrregular(selecionado);
            FacesUtil.executaJavaScript("dialogEmpresaIrregular.hide()");
            FacesUtil.addOperacaoRealizada("Empresa " + (selecionado.getEmpresaIrregular() ? "inserida" : "removida") + " do Rol de Empresas Irregulares!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void mudouEmpresaIrregular() {
        if (selecionado.getEmpresaIrregular()) {
            FacesUtil.executaJavaScript("dialogEmpresaIrregular.show()");
        } else {
            selecionado.setIrregularidade(null);
            selecionado.setObservacaoIrregularidade(null);
            selecionado.setUsuarioIrregularidade(sistemaFacade.getUsuarioCorrente());

            monitoramentoFiscalFacade.adicionarSituacaoNaEmpresaIrregular(selecionado);
            selecionado = monitoramentoFiscalFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("Empresa " + (selecionado.getEmpresaIrregular() ? "inserida" : "removida") + " do Rol de Empresas Irregulares!");
        }
    }

    public List<Irregularidade> completarIrregularidade(String parte) {
        return monitoramentoFiscalFacade.getEmpresaIrregularFacade().getIrregularidadeFacade().buscarEmOrdemAlfabeticaParaTiposPorDescircao(parte.trim(), Irregularidade.Tipo.MONITORAMENTO_FISCAL);
    }

    private void recuperarParametroVigente() {
        parametroMonitoramentoFiscal = parametroMonitoramentoFiscalFacade.retornarParametroExercicioCorrente();
    }

    private void carregarVerificacaoDebitos() {
        alvaraLocalizacao = monitoramentoFiscalFacade.recuperarAlvarasPorTipo(TipoAlvara.LOCALIZACAO, selecionado.getCadastroEconomico());
        alvaraFuncionamento = monitoramentoFiscalFacade.recuperarAlvarasPorTipo(TipoAlvara.FUNCIONAMENTO, selecionado.getCadastroEconomico());
        alvaraVigilanciaSanitaria = monitoramentoFiscalFacade.recuperarAlvarasPorTipo(TipoAlvara.SANITARIO, selecionado.getCadastroEconomico());
        iptuCadastroImobiliarioDoCadastroEconomico = monitoramentoFiscalFacade.buscarIptuCadastroImobiliarioDoCadastroEconomico(selecionado);
    }

    public List<SelectItem> getSituacoesMonitoramentoFiscal() {
        return Util.getListSelectItem(SituacaoMonitoramentoFiscal.values());
    }


    public void iniciarMonitoramentoFiscal() {
        selecionado.setSituacaoMF(SituacaoMonitoramentoFiscal.INICIADO);
        selecionado.setDataInicialMonitoramento(new Date());
        monitoramentoFiscalFacade.criarHistoricoSituacoesMonitoramentoFiscal(selecionado);
        monitoramentoFiscalFacade.salvar(selecionado);

        monitoramentoFiscalFacade.atualizarSituacaoDaOrdemParaExecutando(selecionado.getOrdemGeralMonitoramento());

        FacesUtil.addOperacaoRealizada("Monitoramento Fiscal iniciado com sucesso!");
    }

    public void finalizarMonitoramentoFiscal() {
        selecionado.setSituacaoMF(SituacaoMonitoramentoFiscal.FINALIZADO);
        selecionado.setDataInicialMonitoramento(new Date());
        monitoramentoFiscalFacade.criarHistoricoSituacoesMonitoramentoFiscal(selecionado);
        monitoramentoFiscalFacade.salvar(selecionado);

        FacesUtil.addOperacaoRealizada("Monitoramento fiscal finalizado com sucesso!");
    }

    public String getAlvaraLocalizacao() {
        return alvaraLocalizacao;
    }

    public void setAlvaraLocalizacao(String alvaraLocalizacao) {
        this.alvaraLocalizacao = alvaraLocalizacao;
    }

    public String getAlvaraFuncionamento() {
        return alvaraFuncionamento;
    }

    public void setAlvaraFuncionamento(String alvaraFuncionamento) {
        this.alvaraFuncionamento = alvaraFuncionamento;
    }

    public String getAlvaraVigilanciaSanitaria() {
        return alvaraVigilanciaSanitaria;
    }

    public void setAlvaraVigilanciaSanitaria(String alvaraVigilanciaSanitaria) {
        this.alvaraVigilanciaSanitaria = alvaraVigilanciaSanitaria;
    }

    public String getIptuCadastroImobiliarioDoCadastroEconomico() {
        return iptuCadastroImobiliarioDoCadastroEconomico;
    }

    public void setIptuCadastroImobiliarioDoCadastroEconomico(String iptuCadastroImobiliarioDoCadastroEconomico) {
        this.iptuCadastroImobiliarioDoCadastroEconomico = iptuCadastroImobiliarioDoCadastroEconomico;
    }

    public LevantamentoUFMMonitoramentoFiscal[] getLevantamentosUFMSelecionados() {
        return levantamentosUFMSelecionados;
    }

    public void setLevantamentosUFMSelecionados(LevantamentoUFMMonitoramentoFiscal[] levantamentosUFMSelecionados) {
        this.levantamentosUFMSelecionados = levantamentosUFMSelecionados;
    }

    public TipoLevantamentoContabilMonitoramentoFiscal getTipoLevantamentoContabilMonitoramentoFiscal() {
        return tipoLevantamentoContabilMonitoramentoFiscal;
    }

    public void setTipoLevantamentoContabilMonitoramentoFiscal(TipoLevantamentoContabilMonitoramentoFiscal tipoLevantamentoContabilMonitoramentoFiscal) {
        this.tipoLevantamentoContabilMonitoramentoFiscal = tipoLevantamentoContabilMonitoramentoFiscal;
    }


    private void carregarMesesPorPeriodo() {
        try {
            validarPeriodoLevantamentoUFMMonitoramentoFiscal();
            recuperarParametroVigente();
            selecionado.getLevantamentosUFM().clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selecionado.getDataLevantamentoFinal());
            int mesInicio = calendar.get(Calendar.MONTH) + 1;
            int anoInicio = calendar.get(Calendar.YEAR);
            int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            calendar.setTime(selecionado.getDataLevantamentoFinal());
            int mesFim = calendar.get(Calendar.MONTH) + 1;
            int anoFim = calendar.get(Calendar.YEAR);
            int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
            while (anoMesInicio <= anoMesFim) {
                if (mesInicio <= 12) {
                    LevantamentoUFMMonitoramentoFiscal levantamentoUFM = new LevantamentoUFMMonitoramentoFiscal();
                    levantamentoUFM.setMonitoramentoFiscal(selecionado);
                    levantamentoUFM.setMes(Mes.getMesToInt(mesInicio));
                    levantamentoUFM.setAno(anoInicio);
                    levantamentoUFM.setValorIndice(monitoramentoFiscalFacade.getMoedaFacade().recuperaValorUFMPorExercicio(anoInicio));
                    selecionado.getLevantamentosUFM().add(levantamentoUFM);
                    mesInicio++;
                } else {
                    mesInicio = 1;
                    anoInicio++;
                }
                anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
            }
            Calendar ultimo = Calendar.getInstance();
            ultimo.set(Calendar.MONTH, mesInicio - 1);
            ultimo.set(Calendar.YEAR, anoInicio);
            selecionado.setDataArbitramento(Util.recuperaDataUltimoDiaDoMes(calendar.getTime()));
            selecionado.setUfmArbitramento(monitoramentoFiscalFacade.getMoedaFacade().recuperaValorUFMPorExercicio(anoInicio));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarPeriodoLevantamentoUFMMonitoramentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataLevantamentoInicial() == null && !SituacaoMonitoramentoFiscal.INICIADO.equals(selecionado.getSituacaoMF())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial do levantamento!");
        }
        if (selecionado.getDataLevantamentoFinal() == null && !SituacaoMonitoramentoFiscal.INICIADO.equals(selecionado.getSituacaoMF())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final do levantamento!");
        }
        if (selecionado.getDataLevantamentoInicial() != null && selecionado.getDataLevantamentoFinal() != null &&
            selecionado.getDataLevantamentoInicial().compareTo(selecionado.getDataLevantamentoFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data inicial não pode ser anterior a data final.");
        }
        ve.lancarException();
    }

    public void novoRegistro() {
        registro = new RegistroLancamentoContabilMonitoramentoFiscal();
        registro.setNumeroRegistro(new Long(selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().size() + 1));
        FacesUtil.executaJavaScript("aguarde.hide(); dlgLevantamentoContabil.show();");
    }

    private void validarNovoRegistro() {
        ValidacaoException ve = new ValidacaoException();
        if (getTipoLevantamentoContabilMonitoramentoFiscal() == null || Strings.isNullOrEmpty((getTipoLevantamentoContabilMonitoramentoFiscal().getDescricao()))) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um tipo de registro!");
        }
        ve.lancarException();
    }

    public void carregarMesesLancamentoContabil() {
        try {
            validarNovoRegistro();
            preparaTotalizador();
            this.carregarLancamentoContabil();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void preparaTotalizador() {
        listaTotalizador.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selecionado.getDataLevantamentoInicial());
        int mesInicio = calendar.get(Calendar.MONTH) + 1;
        int anoInicio = calendar.get(Calendar.YEAR);
        int anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));

        calendar.setTime(selecionado.getDataLevantamentoFinal());
        int mesFim = calendar.get(Calendar.MONTH) + 1;
        int anoFim = calendar.get(Calendar.YEAR);
        int anoMesFim = Integer.parseInt(anoFim + "" + StringUtil.preencheString("" + mesFim, 2, '0'));
        while (anoMesInicio <= anoMesFim) {
            if (mesInicio <= 12) {
                this.totalizador = new TotalizadorLancamentoContabil();
                this.totalizador.setAliquota(BigDecimal.ZERO);
                this.totalizador.setMes(Mes.getMesToInt(mesInicio));
                this.totalizador.setAno(anoInicio);
                this.totalizador.setValor(BigDecimal.ZERO);
                this.listaTotalizador.add(this.totalizador);
                this.totalizador.setTributado(true);
                mesInicio++;
            } else {
                mesInicio = 1;
                anoInicio++;
            }
            anoMesInicio = Integer.parseInt(anoInicio + "" + StringUtil.preencheString("" + mesInicio, 2, '0'));
        }
    }

    private void carregarLancamentoContabil() {
        valoresPagos = null;
        aliquotasVariadas = new HashMap<>();
        Collections.sort(listaTotalizador);
        registro.setMonitoramentoFiscal(selecionado);
        registro.setTipo(getTipoLevantamentoContabilMonitoramentoFiscal());
        for (TotalizadorLancamentoContabil totalizador : listaTotalizador) {
            adicionarLancamentoContabilPelaNota(totalizador);
        }
        for (String index : aliquotasVariadas.keySet()) {
            adicionarLancamentoContabilPelaAliquota(index);
        }
        adicionarLancamentoContabilNfse();
        List<LancamentoFiscalMonitoramentoFiscal> duplicados = Lists.newArrayList();
        for (LancamentoFiscalMonitoramentoFiscal lancamento : registro.getLancamentosFiscais()) {
            if (semValorDeclaradoSemAliquotaComMovimento(lancamento)) {
                duplicados.addAll(definirAliquotaParaLancamentosZerados(lancamento));
            }
        }
        if (!duplicados.isEmpty()) {
            for (LancamentoFiscalMonitoramentoFiscal duplicado : duplicados) {
                registro.getLancamentosFiscais().add(duplicado);
            }
        }
        removerValoresDeclaradosSemAliquota();
        ordenarLancamentosContabeis();
        mesclarLancamentosCompativeis();
        definirSequenciaZeraAliquotaRegistrosSemValores();
        Collections.sort(registro.getLancamentosFiscais());
        removerLancamentosZeradosQuandoTemOutroLancamentoComValorMesmoPeriodo();

        selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().add(registro);
    }

    private void removerValoresDeclaradosSemAliquota() {
        List<LancamentoFiscalMonitoramentoFiscal> lancamentosParaExcluir = Lists.newArrayList();

        for (LancamentoFiscalMonitoramentoFiscal lancamento : registro.getLancamentosFiscais()) {
            if (semValorDeclaradoSemAliquotaComMovimento(lancamento)) {
                lancamentosParaExcluir.add(lancamento);
            }
        }
        for (LancamentoFiscalMonitoramentoFiscal excluir : lancamentosParaExcluir) {
            registro.getLancamentosFiscais().remove(excluir);
        }

    }

    private void ordenarLancamentosContabeis() {
        Collections.sort(registro.getLancamentosFiscais(), new Comparator<LancamentoFiscalMonitoramentoFiscal>() {
            @Override
            public int compare(LancamentoFiscalMonitoramentoFiscal o1, LancamentoFiscalMonitoramentoFiscal o2) {
                try {
                    int retorno = o2.getAno().compareTo(o1.getAno());

                    if (retorno == 0) {
                        retorno = o2.getMes().compareTo(o1.getMes());
                    }
                    if (retorno == 0) {
                        retorno = o2.getAliquotaISS().compareTo(o1.getAliquotaISS());
                    }
                    if (retorno == 0) {
                        retorno = o2.getTributado().compareTo(o1.getTributado());
                    }
                    return retorno;
                } catch (Exception e) {
                    logger.trace("Erro ordenando Lançamentos Contábeis", e);
                    return Integer.MAX_VALUE;
                }
            }
        });
    }

    private void mesclarLancamentosCompativeis() {
        List<LancamentoFiscalMonitoramentoFiscal> listaParaSeremRemovidos = Lists.newArrayList();
        int tamanho = registro.getLancamentosFiscais().size() - 1;
        for (int i = 0; i < tamanho; i++) {
            LancamentoFiscalMonitoramentoFiscal lancamento = registro.getLancamentosFiscais().get(i);
            if (i < tamanho) {
                int indiceDoProximo = i + 1;
                LancamentoFiscalMonitoramentoFiscal proximoLancamento = registro.getLancamentosFiscais().get(indiceDoProximo);
                if ((isMesmoMesAnoComDeclaradoApuradoZerado(lancamento, proximoLancamento)
                    || mesmoMesAnoAliquotaZeradaOuIgual(lancamento, proximoLancamento))
                    && ambosTemMesmoTipoTributacao(lancamento, proximoLancamento)) {
                    proximoLancamento.setValorApurado(proximoLancamento.getValorApurado().add(lancamento.getValorApurado()));
                    if (lancamento.getTributado()) {
                        proximoLancamento.setBaseCalculo(proximoLancamento.getValorDeclarado().subtract(proximoLancamento.getValorApurado()));
                        proximoLancamento.setIssApurado(proximoLancamento.getValorApurado().multiply(proximoLancamento.getAliquotaISS().divide(CEM)));
                        proximoLancamento.setValorDeclarado(buscarValorDeclaradoISS(proximoLancamento));
                        proximoLancamento.setIssPago(buscarValorPagoIss(proximoLancamento));
                        arbitrar(proximoLancamento);
                        proximoLancamento.setIssDevido((proximoLancamento.getIssApurado().subtract(proximoLancamento.getIssPago())).multiply(registro.getLancamentosFiscais().get(indiceDoProximo).getIndiceCorrecao()));
                    } else {
                        proximoLancamento.setBaseCalculo(BigDecimal.ZERO);
                        proximoLancamento.setIssApurado(BigDecimal.ZERO);
                        proximoLancamento.setValorDeclarado(proximoLancamento.getValorDeclarado().add(lancamento.getValorDeclarado()));
                        proximoLancamento.setIssPago(BigDecimal.ZERO);
                        proximoLancamento.setIssDevido(BigDecimal.ZERO);
                    }
                    listaParaSeremRemovidos.add(lancamento);

                } else {
                    proximoLancamento = registro.getLancamentosFiscais().get(i);
                }
            }
        }

        for (LancamentoFiscalMonitoramentoFiscal lancamento : listaParaSeremRemovidos) {
            registro.getLancamentosFiscais().remove(lancamento);
        }
    }

    private boolean ambosTemMesmoTipoTributacao(LancamentoFiscalMonitoramentoFiscal lancamento, LancamentoFiscalMonitoramentoFiscal proximoLancamento) {
        return lancamento.getTributado().equals(proximoLancamento.getTributado());
    }

    private boolean mesmoMesAnoAliquotaZeradaOuIgual(LancamentoFiscalMonitoramentoFiscal lancamento, LancamentoFiscalMonitoramentoFiscal proximoLancamento) {
        return ((lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) == 0
            || lancamento.getAliquotaISS().equals(proximoLancamento.getAliquotaISS()))
            && lancamento.getMes().equals(proximoLancamento.getMes())
            && lancamento.getAno().equals(proximoLancamento.getAno()));
    }

    private boolean isMesmoMesAnoComDeclaradoApuradoZerado(LancamentoFiscalMonitoramentoFiscal lancamento, LancamentoFiscalMonitoramentoFiscal proximoLancamento) {
        return lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getValorApurado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getMes().equals(proximoLancamento.getMes())
            && lancamento.getAno().equals(proximoLancamento.getAno());
    }

    private void definirSequenciaZeraAliquotaRegistrosSemValores() {
        Long sequencia = 1L;
        for (LancamentoFiscalMonitoramentoFiscal lancamento : registro.getLancamentosFiscais()) {
            lancamento.setSequencia(sequencia);
            sequencia++;
            if (lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
                && lancamento.getValorApurado().compareTo(BigDecimal.ZERO) == 0
                && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0) {
                lancamento.setAliquotaISS(BigDecimal.ZERO);
            }
            atualizarValoresTabelaLancamentoContabil(lancamento);
        }
    }

    private void removerLancamentosZeradosQuandoTemOutroLancamentoComValorMesmoPeriodo() {
        List<LancamentoFiscalMonitoramentoFiscal> listaParaSeremRemovidos = Lists.newArrayList();
        int tamanho = registro.getLancamentosFiscais().size() - 1;
        for (int i = 0; i < tamanho; i++) {
            LancamentoFiscalMonitoramentoFiscal lancamento = registro.getLancamentosFiscais().get(i);
            if (i < tamanho) {
                int indiceDoProximo = i + 1;
                LancamentoFiscalMonitoramentoFiscal proximoLancamento = registro.getLancamentosFiscais().get(indiceDoProximo);
                if (isMesmoMesAnoComDeclaradoApuradoZerado(lancamento, proximoLancamento)) {
                    listaParaSeremRemovidos.add(lancamento);
                }
            }
        }
        for (LancamentoFiscalMonitoramentoFiscal lancamento : listaParaSeremRemovidos) {
            registro.getLancamentosFiscais().remove(lancamento);
        }
    }

    private void adicionarLancamentoContabilPelaNota(TotalizadorLancamentoContabil totalizador) {
        LancamentoFiscalMonitoramentoFiscal lancamento = new LancamentoFiscalMonitoramentoFiscal();
        lancamento.setRegistroLancContabilMF(registro);
        lancamento.setMes(totalizador.getMes());
        lancamento.setAno(totalizador.getAno());
        lancamento.setSequencia(Long.valueOf(selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().size() + 1));
        lancamento.setIndiceCorrecao(BigDecimal.ONE);
        lancamento.setAliquotaISS(totalizador.getAliquota());
        lancamento.setValorApurado(totalizador.getValor());
        lancamento.setTributado(totalizador.isTributado());
        if (lancamento.getTributado()) {
            lancamento.setValorDeclarado(buscarValorDeclaradoISS(lancamento));
            lancamento.setIssPago(buscarValorPagoIss(lancamento));
            lancamento.setIssApurado(lancamento.getValorApurado().multiply(lancamento.getAliquotaISS().divide(CEM)));
        } else {
            lancamento.setValorDeclarado(totalizador.getValor());
            lancamento.setIssPago(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
        }
        lancamento.setBaseCalculo(lancamento.getValorDeclarado().subtract(lancamento.getValorApurado()));
        arbitrar(lancamento);
        BigDecimal saldoIss = (lancamento.getIssApurado().subtract(lancamento.getIssPago())).multiply(lancamento.getIndiceCorrecao());
        if (saldoIss.compareTo(BigDecimal.ZERO) <= 0) {
            saldoIss = BigDecimal.ZERO;
        }
        lancamento.setIssDevido(saldoIss);
        registro.getLancamentosFiscais().add(lancamento);
    }

    private void adicionarLancamentoContabilNfse() {
        List<TotalizadorLancamentoContabil> totalizadorNfse = monitoramentoFiscalFacade.buscarLancamentosNfse(selecionado);
        for (TotalizadorLancamentoContabil nfse : totalizadorNfse) {
            LancamentoFiscalMonitoramentoFiscal lancamento = new LancamentoFiscalMonitoramentoFiscal();
            lancamento.setRegistroLancContabilMF(registro);
            lancamento.setMes(nfse.getMes());
            lancamento.setAno(nfse.getAno());
            lancamento.setSequencia(Long.valueOf(selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().size() + 1));
            lancamento.setIndiceCorrecao(BigDecimal.ONE);
            lancamento.setAliquotaISS(nfse.getAliquota());
            lancamento.setValorApurado(BigDecimal.ZERO);
            lancamento.setTributado(nfse.isTributado());
            lancamento.setNfse(true);

            lancamento.setIssPago(nfse.getValor());
            lancamento.setValorDeclarado(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
            lancamento.setBaseCalculo(BigDecimal.ZERO);

            lancamento.setIssDevido(BigDecimal.ZERO);
            registro.getLancamentosFiscais().add(lancamento);
        }
    }


    private void adicionarLancamentoContabilPelaAliquota(String index) {
        LancamentoFiscalMonitoramentoFiscal lancamento = new LancamentoFiscalMonitoramentoFiscal();
        lancamento.setRegistroLancContabilMF(registro);
        lancamento.setMes(Mes.getMesToInt(aliquotasVariadas.get(index).getCalculo().getProcessoCalculoISS().getMesReferencia()));
        lancamento.setAno(aliquotasVariadas.get(index).getCalculo().getProcessoCalculoISS().getExercicio().getAno());
        lancamento.setSequencia(Long.valueOf(selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().size() + 1));
        lancamento.setIndiceCorrecao(BigDecimal.ONE);
        lancamento.setAliquotaISS(aliquotasVariadas.get(index).getAliquota());
        lancamento.setSemMovimento(aliquotasVariadas.get(index).getCalculo().getAusenciaMovimento());

        if (!lancamento.getSemMovimento()) {
            lancamento.setValorDeclarado(aliquotasVariadas.get(index).getBaseCalculo());
            lancamento.setIssPago(buscarValorPagoIss(lancamento));
            lancamento.setIssApurado(lancamento.getValorApurado().multiply(lancamento.getAliquotaISS().divide(CEM)));
        } else {
            lancamento.setValorDeclarado(BigDecimal.ZERO);
            lancamento.setIssPago(BigDecimal.ZERO);
            lancamento.setIssApurado(BigDecimal.ZERO);
        }
        lancamento.setBaseCalculo(lancamento.getValorDeclarado().subtract(lancamento.getValorApurado()));
        arbitrar(lancamento);
        lancamento.setIssDevido((lancamento.getIssApurado().subtract(lancamento.getIssPago())).multiply(lancamento.getIndiceCorrecao()));
        registro.getLancamentosFiscais().add(lancamento);
    }

    private BigDecimal buscarValorDeclaradoISS(LancamentoFiscalMonitoramentoFiscal lancamento) {
        List<ItemCalculoIss> itens = monitoramentoFiscalFacade.buscaLancamentoDeISS(lancamento.getMes(), lancamento.getAno(), selecionado.getCadastroEconomico());
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : itens) {
            if (item.getAliquota().compareTo(lancamento.getAliquotaISS()) == 0) {
                total = total.add(item.getBaseCalculo());
            } else {
                String identificador = montaIdentificadorRegistro(item);
                if (aliquotasVariadas.containsKey(identificador)) {
                    aliquotasVariadas.get(identificador).setBaseCalculo(aliquotasVariadas.get(identificador).getBaseCalculo().add(item.getBaseCalculo()));
                    aliquotasVariadas.get(identificador).setFaturamento(aliquotasVariadas.get(identificador).getFaturamento().add(item.getFaturamento()));
                } else {
                    aliquotasVariadas.put(identificador, item);
                }
            }
        }
        return total;
    }


    private List<LancamentoFiscalMonitoramentoFiscal> definirAliquotaParaLancamentosZerados(LancamentoFiscalMonitoramentoFiscal lancamento) {
        List<LancamentoFiscalMonitoramentoFiscal> duplicados = Lists.newArrayList();
        List<BigDecimal> aliquotas = monitoramentoFiscalFacade.buscarAliquotasServicos(selecionado.getCadastroEconomico());
        if (aliquotas.isEmpty()) {
            aliquotas.add(new BigDecimal("5"));
        }

        for (BigDecimal aliquota : aliquotas) {
            if (aliquota != null) {
                if (aliquotas.size() == 1) {
                    lancamento.setAliquotaISS(aliquota);
                    lancamento.setValorDeclarado(buscarValorDeclaradoISS(lancamento));
                    lancamento.setIssPago(buscarValorPagoIss(lancamento));
                } else {
                    duplicados.add(duplicarLancamentoContabilSemEvento(lancamento, aliquota));
                }
            }
        }
        return duplicados;
    }

    public LancamentoFiscalMonitoramentoFiscal duplicarLancamentoContabilSemEvento(LancamentoFiscalMonitoramentoFiscal lancamento, BigDecimal aliquota) {
        LancamentoFiscalMonitoramentoFiscal lancamentoDuplicado = (LancamentoFiscalMonitoramentoFiscal) Util.clonarObjeto(lancamento);
        lancamentoDuplicado.setId(null);
        lancamentoDuplicado.setSequencia(null);
        lancamentoDuplicado.setAliquotaISS(aliquota);
        lancamentoDuplicado.setValorDeclarado(lancamento.getValorDeclarado());
        lancamentoDuplicado.setIssPago(lancamento.getIssPago());
        return lancamentoDuplicado;
    }

    private boolean semValorDeclaradoSemAliquotaComMovimento(LancamentoFiscalMonitoramentoFiscal lancamento) {
        return lancamento.getValorDeclarado().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) == 0
            && lancamento.getIssPago().compareTo(BigDecimal.ZERO) == 0
            && !lancamento.getSemMovimento();
    }

    private String montaIdentificadorRegistro(ItemCalculoIss item) {
        return Util.zerosAEsquerda(item.getAliquota().intValue(), 2)
            + Util.zerosAEsquerda(item.getCalculo().getProcessoCalculoISS().getMesReferencia(), 2)
            + Util.zerosAEsquerda(item.getCalculo().getProcessoCalculoISS().getExercicio().getAno(), 4)
            + (item.getCalculo().getAusenciaMovimento() ? '1' : '0');
    }


    public List<SelectItem> getTiposLevantamentoContabil() {
        recuperarParametroVigente();
        List<SelectItem> retorno = Lists.newArrayList();

        retorno.add(new SelectItem(null, ""));
        if (parametroMonitoramentoFiscal != null) {
            if (parametroMonitoramentoFiscal.getSimplesNacional()) {
                retorno.add(new SelectItem(TipoLevantamentoContabilMonitoramentoFiscal.SIMPLES_NACIONAL,
                    TipoLevantamentoContabilMonitoramentoFiscal.SIMPLES_NACIONAL.getDescricao()));
            }
            if (parametroMonitoramentoFiscal.getNfse()) {
                retorno.add(new SelectItem(TipoLevantamentoContabilMonitoramentoFiscal.NFSE,
                    TipoLevantamentoContabilMonitoramentoFiscal.NFSE.getDescricao()));
            }
            if (parametroMonitoramentoFiscal.getOutro()) {
                retorno.add(new SelectItem(TipoLevantamentoContabilMonitoramentoFiscal.OUTRO,
                    TipoLevantamentoContabilMonitoramentoFiscal.OUTRO.getDescricao()));
            }
        }
        return retorno;
    }

    public void atualizarValoresTabelaLancamentoContabilComUpdate(LancamentoFiscalMonitoramentoFiscal lancamento, Long registro, int var2, int linhaRegistro) {
        atualizarValoresTabelaLancamentoContabil(lancamento);
        updateRodapes(registro, var2);
    }

    private void atualizarValoresTabelaLancamentoContabil(LancamentoFiscalMonitoramentoFiscal lancamentoMF) {
        if (lancamentoMF.getTributado()) {
            lancamentoMF.setBaseCalculo(lancamentoMF.getValorDeclarado().subtract(lancamentoMF.getValorApurado()));
            lancamentoMF.setIssApurado(lancamentoMF.getValorApurado().multiply(lancamentoMF.getAliquotaISS().divide(CEM)));
            if (!lancamentoMF.getNfse()) {
                lancamentoMF.setIssPago(buscarValorPagoIss(lancamentoMF));
            }
            arbitrar(lancamentoMF);
            BigDecimal saldoIss = (lancamentoMF.getIssApurado().subtract(lancamentoMF.getIssPago()));
            if (saldoIss.compareTo(BigDecimal.ZERO) <= 0) {
                saldoIss = BigDecimal.ZERO;
            }
            lancamentoMF.setIssDevido(saldoIss);

            lancamentoMF.setCorrecao(calcularCorrecaoLancamento(lancamentoMF));
            lancamentoMF.setJuros(calcularJurosLancamento(lancamentoMF));
            lancamentoMF.setMulta(calcularMultaLancamento(lancamentoMF));
        }
    }

    private BigDecimal calcularCorrecaoLancamento(LancamentoFiscalMonitoramentoFiscal lancamento) {
        lancamento.setCorrecao(monitoramentoFiscalFacade.calcularCorrecaoLancamento(lancamento, this.selecionado));
        return lancamento.getCorrecao();
    }

    private BigDecimal calcularJurosLancamento(LancamentoFiscalMonitoramentoFiscal lancamento) {
        return monitoramentoFiscalFacade.calcularJurosLancamento(lancamento, this.selecionado, diaVencimentoPorAno);
    }

    private BigDecimal calcularMultaLancamento(LancamentoFiscalMonitoramentoFiscal lancamento) {
        return monitoramentoFiscalFacade.calcularMultaLancamento(lancamento, this.selecionado, this.diaVencimentoPorAno);
    }

    private void arbitrar(LancamentoFiscalMonitoramentoFiscal lancamentoMF) {
        BigDecimal valor = monitoramentoFiscalFacade.getMoedaFacade().recuperaValorVigenteUFM();
        if (valor != null) {
            for (LevantamentoUFMMonitoramentoFiscal levantamentoUfm : selecionado.getLevantamentosUFM()) {
                if (levantamentoUfm.getMes().equals(lancamentoMF.getMes()) && levantamentoUfm.getAno().compareTo(lancamentoMF.getAno()) == 0) {
                    lancamentoMF.setIndiceCorrecao(levantamentoUfm.getValorIndice());
                }
            }
        }
    }

    private BigDecimal buscarValorPagoIss(LancamentoFiscalMonitoramentoFiscal lancamento) {
        if (lancamento.getAliquotaISS().compareTo(BigDecimal.ZERO) > 0 && lancamento.getTributado()) {
            MonitoramentoFiscalControlador.ValoresIssPago valores = new MonitoramentoFiscalControlador.ValoresIssPago(lancamento);
            if (valoresPagos == null) {
                valoresPagos = Maps.newHashMap();
            }
            if (valoresPagos.containsKey(valores)) {
                return valoresPagos.get(valores);
            }
            valoresPagos.put(valores, monitoramentoFiscalFacade.buscaValorPagoISS(lancamento, selecionado.getCadastroEconomico()));
            return valoresPagos.get(valores);
        }
        return lancamento.getIssPago();
    }

    public BigDecimal retornarValorTotalDeclaradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalDeclaradoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalApuradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalApuradoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalBaseCalculoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalBaseCalculoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalIssPagoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalIssPagoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalIssApuradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalIssApuradoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalIssDevidoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalIssDevidoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalCorrecaoMonetariaLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornarValorTotalCorrecaoMonetariaLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalCorrigidoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornarValorTotalCorrigidoLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalJurosMoraLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornarValorTotalJurosMoraLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalMultaMoraLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornarValorTotalMultaMoraLancamento(ano, reg, selecionado);
    }

    public BigDecimal retornarValorTotalTotalLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        return monitoramentoFiscalFacade.retornaValorTotalTotalLancamento(ano, reg, selecionado);
    }

    public BigDecimal calculaTotalLancamento(LancamentoFiscalMonitoramentoFiscal lancamento) {
        return monitoramentoFiscalFacade.calculaTotalLancamento(lancamento);
    }

    public BigDecimal calculaValorCorrigido(LancamentoFiscalMonitoramentoFiscal lancamento) {
        return monitoramentoFiscalFacade.calcularValorCorrigido(lancamento);
    }

    public void duplicaLancamentoContabil(LancamentoFiscalMonitoramentoFiscal lancamento, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        int posicao = selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().indexOf(lancamento);
        LancamentoFiscalMonitoramentoFiscal lancamentoDuplicado = (LancamentoFiscalMonitoramentoFiscal) Util.clonarObjeto(lancamento);
        lancamentoDuplicado.setId(null);
        lancamentoDuplicado.zeraValores();
        reg.getLancamentosFiscais().add(posicao + 1, lancamentoDuplicado);
        reg.getLancamentosPorAno().clear();
        reorganizaSequenciaLancamentoContabil(reg);
    }

    private void reorganizaSequenciaLancamentoContabil(RegistroLancamentoContabilMonitoramentoFiscal reg) {
        Collections.sort(reg.getLancamentosFiscais());
        for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
            lmf.setSequencia(Long.valueOf(reg.getLancamentosFiscais().indexOf(lmf) + 1));
        }
    }

    public void removerLancamentoContabil(LancamentoFiscalMonitoramentoFiscal lancamento, RegistroLancamentoContabilMonitoramentoFiscal reg) {
        int contador = 0;
        for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
            if (lmf.getMes() == lancamento.getMes() && lmf.getAno().compareTo(lancamento.getAno()) == 0) {
                contador++;
            }
        }
        if (contador > 1) {
            reg.getLancamentosFiscais().remove(lancamento);
            reg.getLancamentosPorAno().clear();
            reorganizaSequenciaLancamentoContabil(reg);
        } else {
            FacesUtil.addError("Impossível continuar!", "Só é possível remover os meses duplicados.");
        }
    }

    public void removeRegistro(RegistroLancamentoContabilMonitoramentoFiscal registro) {
        selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().remove(registro);
    }

    public void imprimeDemonstrativo() {
        selecionado = monitoramentoFiscalFacade.salvarRetornando(selecionado);
        new ImpressaoDemonstrativoMonitoramentoFiscal().imprimeViaMonitoramento(selecionado.getId(), sistemaFacade.getLogin());
    }

    public void imprimeDemonstrativo(RegistroLancamentoContabilMonitoramentoFiscal registro) {
        Long idRegistro = registro.getId();
        selecionado = monitoramentoFiscalFacade.salvarRetornando(selecionado);
        selecionado = monitoramentoFiscalFacade.recuperar(selecionado.getId());
        for (RegistroLancamentoContabilMonitoramentoFiscal reg : selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal()) {
            if ((reg.getNumeroRegistro() != null && reg.getNumeroRegistro().equals(registro.getNumeroRegistro())) && reg.getAno().equals(registro.getAno())) {
                idRegistro = reg.getId();
                break;
            }
        }
        new ImpressaoDemonstrativoMonitoramentoFiscal().imprimeViaRegistro(idRegistro, sistemaFacade.getLogin());
    }

    public void limparItensLevantamentoContabil() {
        getTiposLevantamentoContabil().clear();
    }

    private void updateRodapes(Long registro, int var2) {
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalDeclarado");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalApurado");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalBase");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalIssPago");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalIssApurado");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalSaldo");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalCorrecao");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalCorrigido");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalJuros");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotalMulta");
        FacesUtil.atualizarComponente("Formulario:dadosVerificacaoDebitos:tableLevantamentoContabil" + registro + var2 + ":valorTotal");
    }

    public void criarNotificacaoMalaDireta() {
        recuperarParametroVigente();
        if (parametroMonitoramentoFiscal != null && parametroMonitoramentoFiscal.getParametroMalaDireta() != null) {

            MalaDiretaGeral malaDiretaGeral = getMalaDiretaGeral();

            ItemMalaDiretaGeral itemMalaDiretaGeral = getItemMalaDiretaGeral(malaDiretaGeral);

            MalaDiretaMonitoramentoFiscal malaDiretaMonitoramentoFiscal = getMalaDiretaMonitoramentoFiscal(itemMalaDiretaGeral);
            selecionado.setSituacaoMF(SituacaoMonitoramentoFiscal.COMUNICADO);

            monitoramentoFiscalFacade.criarHistoricoSituacoesMonitoramentoFiscal(selecionado);

            criarNotificacoesMalaDireta(malaDiretaMonitoramentoFiscal);

            FacesUtil.addOperacaoRealizada("Mala Direta gerada com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } else {
            FacesUtil.addOperacaoRealizada("Não foi encontrado Parâmetro de Mala Direta cadastrado no Parâmetro do Monitoramento Fiscal!");
        }
    }

    public boolean isDesabilitarNotificacao() {
        if (SituacaoMonitoramentoFiscal.FINALIZADO.equals(selecionado.getSituacaoMF())) {
            return true;
        }
        if (selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal() == null || selecionado.getRegistrosLancamentoContabilMonitoramentoFiscal().isEmpty()) {
            return true;
        }
        return false;
    }

    private MalaDiretaMonitoramentoFiscal getMalaDiretaMonitoramentoFiscal(ItemMalaDiretaGeral itemMalaDiretaGeral) {
        MalaDiretaMonitoramentoFiscal malaDiretaMonitoramentoFiscal = new MalaDiretaMonitoramentoFiscal();
        malaDiretaMonitoramentoFiscal.setMonitoramentoFiscal(selecionado);
        malaDiretaMonitoramentoFiscal.setItemMalaDiretaGeral(itemMalaDiretaGeral);
        Date dataResposta = DataUtil.somaPeriodo(new Date(), parametroMonitoramentoFiscal.getPrazoRespostaComunicado(), TipoPrazo.DIAS);
        Date dataRegularizacao = DataUtil.somaPeriodo(new Date(), parametroMonitoramentoFiscal.getPrazoRegularizaPendencia(), TipoPrazo.DIAS);
        malaDiretaMonitoramentoFiscal.setDataResposta(dataResposta);
        malaDiretaMonitoramentoFiscal.setDataRegularizacao(dataRegularizacao);
        Util.adicionarObjetoEmLista(selecionado.getMalasDiretasMonitoramentoFiscal(), malaDiretaMonitoramentoFiscal);
        return malaDiretaMonitoramentoFiscal;
    }

    private ItemMalaDiretaGeral getItemMalaDiretaGeral(MalaDiretaGeral malaDiretaGeral) {
        ItemMalaDiretaGeral itemMalaDiretaGeral = new ItemMalaDiretaGeral();
        itemMalaDiretaGeral.setMalaDiretaGeral(malaDiretaGeral);
        itemMalaDiretaGeral.setCadastro(selecionado.getCadastroEconomico());
        itemMalaDiretaGeral.setPessoa(selecionado.getCadastroEconomico().getPessoa());
        itemMalaDiretaGeral.setTexto(malaDiretaGeral.getTexto());
        Util.adicionarObjetoEmLista(malaDiretaGeral.getItensMalaDireta(), itemMalaDiretaGeral);

        itemMalaDiretaGeral = monitoramentoFiscalFacade.salvarItemMalaGeral(itemMalaDiretaGeral);
        return itemMalaDiretaGeral;
    }

    private MalaDiretaGeral getMalaDiretaGeral() {
        MalaDiretaGeral malaDiretaGeral = new MalaDiretaGeral();
        malaDiretaGeral.setExercicio(sistemaFacade.getExercicioCorrente());
        malaDiretaGeral.setCabecalho(parametroMonitoramentoFiscal.getParametroMalaDireta().getCabecalho());
        malaDiretaGeral.setTexto(parametroMonitoramentoFiscal.getParametroMalaDireta().getCorpo());
        malaDiretaGeral.setTipoMalaDireta(TipoMalaDireta.NOTIFICACAO);
        malaDiretaGeral.setTipoCadastro(TipoCadastroTributario.ECONOMICO);
        malaDiretaGeral.setGeradoEm(new Date());
        malaDiretaGeral.setConsideraDebitos(false);
        malaDiretaGeral.setEnviaEmail(false);

        malaDiretaGeral = monitoramentoFiscalFacade.salvarMalaGeral(malaDiretaGeral);
        return malaDiretaGeral;
    }

    private void criarNotificacoesMalaDireta(MalaDiretaMonitoramentoFiscal malaDiretaMonitoramentoFiscal) {
        monitoramentoFiscalFacade.criarNotificacaoVencimentoRespostaMalaDireta(malaDiretaMonitoramentoFiscal, selecionado.getOrdemGeralMonitoramento().getAuditorFiscal());
        monitoramentoFiscalFacade.criarNotificacaoVencimentoRegularizacaoMalaDireta(malaDiretaMonitoramentoFiscal, selecionado.getOrdemGeralMonitoramento().getAuditorFiscal());
        for (FiscalMonitoramento fiscalMonitoramento : selecionado.getFiscaisMonitoramento()) {
            monitoramentoFiscalFacade.criarNotificacaoVencimentoRespostaMalaDireta(malaDiretaMonitoramentoFiscal, fiscalMonitoramento.getAuditorFiscal());
            monitoramentoFiscalFacade.criarNotificacaoVencimentoRegularizacaoMalaDireta(malaDiretaMonitoramentoFiscal, fiscalMonitoramento.getAuditorFiscal());
        }

        if (usuarioSistemaFacade.usuarioSistemaDaPessoa(parametroMonitoramentoFiscal.getDiretorDepartamento()) != null) {
            monitoramentoFiscalFacade.criarNotificacaoVencimentoRespostaMalaDireta(malaDiretaMonitoramentoFiscal,
                usuarioSistemaFacade.usuarioSistemaDaPessoa(parametroMonitoramentoFiscal.getDiretorDepartamento()));
            monitoramentoFiscalFacade.criarNotificacaoVencimentoRegularizacaoMalaDireta(malaDiretaMonitoramentoFiscal,
                usuarioSistemaFacade.usuarioSistemaDaPessoa(parametroMonitoramentoFiscal.getDiretorDepartamento()));
        }

        monitoramentoFiscalFacade.salvarRetornando(selecionado);
    }

    public void redirecionarParaMalaDireta() {
        Web.navegacao(getUrlAtual(), "/mala-direta/ver-por-cadastro/" + selecionado.getMalasDiretasMonitoramentoFiscal().get(0).getItemMalaDiretaGeral().getId() + "/");
    }

    public boolean isDesabilitarAnexos() {
        return SituacaoMonitoramentoFiscal.FINALIZADO.equals(selecionado.getSituacaoMF());
    }

    public class ValoresIssPago {
        private Integer mes;
        private Integer ano;
        private BigDecimal aliquota;

        private ValoresIssPago(LancamentoFiscalMonitoramentoFiscal lancamento) {
            this.mes = lancamento.getMes().getNumeroMes();
            this.ano = lancamento.getAno();
            this.aliquota = lancamento.getAliquotaISS();
        }

        public Integer getMes() {
            return mes;
        }

        public void setMes(Integer mes) {
            this.mes = mes;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public BigDecimal getAliquota() {
            return aliquota;
        }

        public void setAliquota(BigDecimal aliquota) {
            this.aliquota = aliquota;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MonitoramentoFiscalControlador.ValoresIssPago that = (MonitoramentoFiscalControlador.ValoresIssPago) o;

            if (aliquota != null ? !aliquota.equals(that.aliquota) : that.aliquota != null) return false;
            if (ano != null ? !ano.equals(that.ano) : that.ano != null) return false;
            if (mes != null ? !mes.equals(that.mes) : that.mes != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mes != null ? mes.hashCode() : 0;
            result = 31 * result + (ano != null ? ano.hashCode() : 0);
            result = 31 * result + (aliquota != null ? aliquota.hashCode() : 0);
            return result;
        }
    }

}



