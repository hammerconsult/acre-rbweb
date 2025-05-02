package br.com.webpublico.controlerelatorio.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.*;

public abstract class AbstractRelatorioItemDemonstrativo implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRelatorioItemDemonstrativo.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;
    @EJB
    private ProjecaoAtuarialFacade projecaoAtuarialFacade;
    @EJB
    private ConfiguracaoCabecalhoFacade configuracaoCabecalhoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PortalTransparenciaFacade portalTransparenciaFacade;
    @EJB
    private NotaExplicativaRGFFacade notaExplicativaRGFFacade;
    protected Exercicio exercicio;
    protected List<ItemDemonstrativoComponente> itens;
    protected RelatoriosItemDemonst relatoriosItemDemonst;
    protected ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    protected PortalTransparencia anexoPortalTransparenciaAnterior;
    protected BimestreAnexosLei bimestre;
    protected PortalTipoAnexo portalTipoAnexo;
    protected Mes mes;
    protected EsferaDoPoder esferaDoPoder;
    protected String notaExplicativa;
    protected TipoAdministracao tipoAdministracao;
    protected TipoEntidade natureza;
    protected UnidadeGestora unidadeGestora;
    protected String filtros;
    protected List<HierarquiaOrganizacional> unidades;
    private String conteudoNotaExplicativa;

    public void limparCampos() {
        exercicio = getSistemaFacade().getExercicioCorrente();
        esferaDoPoder = null;
        tipoAdministracao = null;
        natureza = null;
        unidadeGestora = null;
        unidades = Lists.newArrayList();
        filtros = "";
        atualizarMesComTipoAnexo();
        limparItens();
        buscarNotaPorMes();
    }

    public void buscarNotaEAnexoPorMes() {
        buscarNotaPorMes();
        buscarAnexoAnterior();
    }

    public void buscarNotaPorMes() {
        if (portalTipoAnexo != null && PortalTransparenciaTipo.RGF.equals(portalTipoAnexo.getTipo())) {
            notaExplicativa = (esferaDoPoder != null
                ? notaExplicativaRGFFacade.buscarNotaPorMesAndEsferaDoPoder(mes, exercicio, getAnexoRgfNota(), esferaDoPoder)
                : notaExplicativaRGFFacade.buscarNotaPorMes(mes, exercicio, getAnexoRgfNota())
            ).getNotaExplicativa();
        }
    }

    private AnexoRGF getAnexoRgfNota() {
        switch (portalTipoAnexo) {
            case ANEXO1_RGF:
                return AnexoRGF.ANEXO_1;

            case ANEXO2_RGF:
                return AnexoRGF.ANEXO_2;

            case ANEXO3_RGF:
                return AnexoRGF.ANEXO_3;

            case ANEXO4_RGF:
                return AnexoRGF.ANEXO_4;

            case ANEXO5_RGF:
                return AnexoRGF.ANEXO_5;

            case ANEXO6_RGF:
                return AnexoRGF.ANEXO_6;

            default:
                return null;
        }
    }

    private void atualizarMesComTipoAnexo() {
        if (portalTipoAnexo != null) {
            switch (portalTipoAnexo.getTipo()) {
                case RREO:
                    atualizarMesComBimestre();
                    break;

                case RGF:
                    mes = Mes.ABRIL;
                    break;

                case LEI_4320:
                    mes = Mes.JANEIRO;
                    break;

                default:
                    mes = null;
                    break;
            }
        }
    }

    public AbstractRelatorioItemDemonstrativo() {
        itens = Lists.newArrayList();
    }

    public void limparItens() {
        itens = Lists.newArrayList();
        buscarAnexoAnterior();
    }

    protected void validarExercicio() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    protected void salvarArquivoPortalTransparencia() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        montarDtoSemApi(dto);
        dto.setApi(getApi());
        byte[] bytes = ReportService.getInstance().gerarRelatorioSincrono(dto);
        PortalTransparencia anexo = getAnexoPortalTransparencia();
        definirLocaleAndPerfilApp(dto);
        atribuirConfiguracaoCabecalho(dto);
        montarArquivoPortalTransparencia(getNomeArquivo(), anexo, bytes);
        FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
    }

    public void definirLocaleAndPerfilApp(RelatorioDTO dto) {
        dto.adicionarParametro(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        try {
            dto.adicionarParametro("PERFIL_APP", sistemaFacade.getPerfilAplicacao().name());
        } catch (Exception e) {
            logger.trace("Erro ao definir locale e perfil da app no report {}", e);
        }
    }

    protected void atribuirConfiguracaoCabecalho(RelatorioDTO dto) {
        try {
            dto.adicionarParametro("CONFIGURACAO_CABECALHO", configuracaoCabecalhoFacade.buscarConfiguracaoCabecalhoPorUnidade(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente()));
        } catch (Exception e) {
            logger.error("Erro ao atribuir CONFIGURACAO_CABECALHO {}", e);
        }
    }

    protected void montarArquivoPortalTransparencia(String nomeRelatorio, PortalTransparencia anexo, byte[] bytes) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(nomeRelatorio);
        arquivo.setMimeType("application/pdf");
        arquivo.setNome(nomeRelatorio);
        anexo.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes)));
        portalTransparenciaFacade.salvarPortalTransparenciaArquivo(anexo);
    }

    public Exercicio getExercicio() {
        if (exercicio == null) {
            exercicio = sistemaFacade.getExercicioCorrente();
        }
        return exercicio;
    }

    protected PortalTransparencia getAnexoPortalTransparencia() {
        atualizarMesComBimestre();
        PortalTransparencia anexoPortalTransparencia = new PortalTransparencia();
        anexoPortalTransparencia.setExercicio(exercicio);
        anexoPortalTransparencia.setDataCadastro(new Date());
        anexoPortalTransparencia.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        anexoPortalTransparencia.setNome(portalTipoAnexo.getDescricao());
        anexoPortalTransparencia.setMes(mes);
        anexoPortalTransparencia.setTipo(portalTipoAnexo.getTipo());
        anexoPortalTransparencia.setTipoAnexo(portalTipoAnexo);
        anexoPortalTransparencia.setObservacoes(anexoPortalTransparenciaAnterior != null ? anexoPortalTransparenciaAnterior.getObservacoes() : null);
        return anexoPortalTransparencia;
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    public List<SelectItem> getMesesComExercicio() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.addAll(Util.getListSelectItemSemCampoVazio(Mes.values(), false));
        retorno.add(new SelectItem(null, "Exercício"));
        return retorno;
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Consolidado"));
        toReturn.add(new SelectItem(EsferaDoPoder.EXECUTIVO, EsferaDoPoder.EXECUTIVO.getDescricao()));
        toReturn.add(new SelectItem(EsferaDoPoder.LEGISLATIVO, EsferaDoPoder.LEGISLATIVO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposAdministracoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao ta : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposEsferas() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Consolidado"));
        for (EsferaDoPoder edp : EsferaDoPoder.values()) {
            if (!edp.equals(EsferaDoPoder.JUDICIARIO)) {
                toReturn.add(new SelectItem(edp, edp.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposEntidades() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEntidade te : TipoEntidade.values()) {
            toReturn.add(new SelectItem(te, te.getTipo()));
        }
        return toReturn;
    }

    private void atualizarMesComBimestre() {
        if (bimestre != null) {
            mes = bimestre.getMesFinal();
        }
    }

    public void buscarAnexoAnterior() {
        atualizarMesComBimestre();
        anexoPortalTransparenciaAnterior = portalTransparenciaFacade.buscarAnexoPorExercicioTipoTipoAnexoEMes(exercicio, portalTipoAnexo, mes);
    }

    public void removerAnexoPortalTransparencia(PortalTransparencia anexo) {
        portalTransparenciaFacade.remover(anexo);
    }

    public void gerarRelatorio(String tipo) {
        try {
            validarExercicio();
            acoesExtrasAoGerarOuSalvar();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipo));
            montarDtoSemApi(dto);
            dto.setApi(getApi());
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarAnexoPortalTransparencia() {
        try {
            validarExercicio();
            acoesExtrasAoGerarOuSalvar();
            FacesUtil.executaJavaScript("dialogConfirm.hide()");
            if (anexoPortalTransparenciaAnterior != null) {
                removerAnexoPortalTransparencia(anexoPortalTransparenciaAnterior);
            }
            salvarArquivoPortalTransparencia();
            buscarAnexoAnterior();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar relatório " + portalTipoAnexo.getDescricao() + ": ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        buscarAnexoAnterior();
        if (anexoPortalTransparenciaAnterior != null) {
            FacesUtil.executaJavaScript("dialogConfirm.show()");
        } else {
            salvarAnexoPortalTransparencia();
        }
    }

    public void acoesExtrasAoGerarOuSalvar() {
    }

    public abstract void montarDtoSemApi(RelatorioDTO dto);

    public abstract String getApi();

    public abstract String getNomeArquivo();

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public RelatoriosItemDemonstFacade getRelatoriosItemDemonstFacade() {
        return relatoriosItemDemonstFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ReferenciaAnualFacade getReferenciaAnualFacade() {
        return referenciaAnualFacade;
    }

    public ProjecaoAtuarialFacade getProjecaoAtuarialFacade() {
        return projecaoAtuarialFacade;
    }

    public PortalTransparencia getAnexoPortalTransparenciaAnterior() {
        return anexoPortalTransparenciaAnterior;
    }

    public void setAnexoPortalTransparenciaAnterior(PortalTransparencia anexoPortalTransparenciaAnterior) {
        this.anexoPortalTransparenciaAnterior = anexoPortalTransparenciaAnterior;
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }

    public PortalTipoAnexo getPortalTipoAnexo() {
        return portalTipoAnexo;
    }

    public void setPortalTipoAnexo(PortalTipoAnexo portalTipoAnexo) {
        this.portalTipoAnexo = portalTipoAnexo;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public TipoEntidade getNatureza() {
        return natureza;
    }

    public void setNatureza(TipoEntidade natureza) {
        this.natureza = natureza;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    protected void adicionarItemDemonstrativoFiltrosCampoACampo(RelatorioDTO dto) {
        dto.adicionarParametro("itemFiltrosRelatorioTemId", itemDemonstrativoFiltros.getRelatorio().getId() != null);
        dto.adicionarParametro("itemFiltrosRelatorioId", itemDemonstrativoFiltros.getRelatorio().getId());
        dto.adicionarParametro("itemFiltrosRelatorioUsaConta", itemDemonstrativoFiltros.getRelatorio().getUsaConta());
        dto.adicionarParametro("itemFiltrosRelatorioUsaPrograma", itemDemonstrativoFiltros.getRelatorio().getUsaPrograma());
        dto.adicionarParametro("itemFiltrosRelatorioUsaAcao", itemDemonstrativoFiltros.getRelatorio().getUsaAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubAcao", itemDemonstrativoFiltros.getRelatorio().getUsaSubAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaSubFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaUnidadeOrg", itemDemonstrativoFiltros.getRelatorio().getUsaUnidadeOrganizacional());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFonteRecurso", itemDemonstrativoFiltros.getRelatorio().getUsaFonteRecurso());
        dto.adicionarParametro("itemFiltrosRelatorioUsaTipoDespesa", itemDemonstrativoFiltros.getRelatorio().getUsaTipoDespesa());
        dto.adicionarParametro("itemFiltrosRelatorioUsaContaFinanceira", itemDemonstrativoFiltros.getRelatorio().getUsaContaFinanceira());
        dto.adicionarParametro("itemFiltrosDataFinal", itemDemonstrativoFiltros.getDataFinal());
        dto.adicionarParametro("itemFiltrosDataInicial", itemDemonstrativoFiltros.getDataInicial());
        dto.adicionarParametro("itemFiltrosDataReferencia", itemDemonstrativoFiltros.getDataReferencia());
        dto.adicionarParametro("itemFiltrosIdsUnidades", itemDemonstrativoFiltros.getIdsUnidades());
        dto.adicionarParametro("itemFiltrosTemApresentacao", itemDemonstrativoFiltros.getApresentacaoRelatorio() != null);
        if (itemDemonstrativoFiltros.getApresentacaoRelatorio() != null) {
            dto.adicionarParametro("itemFiltrosApresentacaoRelatorioDTO", itemDemonstrativoFiltros.getApresentacaoRelatorio().getToDto());
        }
        dto.adicionarParametro("itemFiltrosTemExercicio", itemDemonstrativoFiltros.getExercicio() != null);
        if (itemDemonstrativoFiltros.getExercicio() != null) {
            dto.adicionarParametro("itemFiltrosExercicioId", itemDemonstrativoFiltros.getExercicio().getId());
            dto.adicionarParametro("itemFiltrosExercicioAno", itemDemonstrativoFiltros.getExercicio().getAno());
        }
        dto.adicionarParametro("itemFiltrosPesquisouUg", itemDemonstrativoFiltros.getUnidadeGestora() != null);
        if (itemDemonstrativoFiltros.getUnidadeGestora() != null) {
            dto.adicionarParametro("itemFiltrosUnidadeGestoraId", itemDemonstrativoFiltros.getUnidadeGestora().getId());
        }
        dto.adicionarParametro("itemFiltrosTemParametrosRelatorio", (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()));
        if (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()) {
            dto.adicionarParametro("itemFiltrosParametrosRelatorios", ParametrosRelatorios.parametrosToDto(itemDemonstrativoFiltros.getParametros()));
        }
    }

    public void salvarNotaExplicativaRGF() {
        AnexoRGF anexoRGF = getAnexoRgfNota();
        NotaExplicativaRGF notaExplicativaRGF = (esferaDoPoder != null
            ? notaExplicativaRGFFacade.buscarNotaPorMesAndEsferaDoPoder(mes, exercicio, anexoRGF, esferaDoPoder)
            : notaExplicativaRGFFacade.buscarNotaPorMes(mes, exercicio, anexoRGF));
        notaExplicativaRGF.setNotaExplicativa(notaExplicativa);
        notaExplicativaRGF.setAnexoRGF(anexoRGF);
        notaExplicativaRGF.setEsferaDoPoder(esferaDoPoder);
        notaExplicativaRGF.setExercicio(exercicio);
        notaExplicativaRGFFacade.salvar(notaExplicativaRGF);
    }

    public String getDescricaoSegundoEsferaDoPoder() {
        if (esferaDoPoder == null) {
            return "Município de Rio Branco - AC";
        } else if (esferaDoPoder.equals(EsferaDoPoder.LEGISLATIVO)) {
            return "Câmara Municipal de Rio Branco - AC";
        } else if (esferaDoPoder.equals(EsferaDoPoder.EXECUTIVO)) {
            return "Prefeitura Municipal de Rio Branco - AC";
        }
        return "";
    }

    public String getDescricaoMes() {
        return mes != null ? " a " + mes.getDescricao() : " ao Exercício";
    }

    public String getDataInicial() {
        return "01/01/" + exercicio.getAno();
    }

    public String getDataFinal() {
        return mes != null ? Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno() : "31/12/" + exercicio.getAno();
    }

    public String atualizaFiltrosGerais() {
        return filtros = filtros.isEmpty() ? " " : filtros.substring(0, filtros.length() - 1);
    }

    public Date getDataExercicioAndMes() {
        int numeroMes = mes != null ? mes.getNumeroMes() : 12;
        int anoExercicio = exercicio != null ? exercicio.getAno() : sistemaFacade.getExercicioCorrente().getAno();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, Util.getDiasMes(numeroMes, anoExercicio));
        c.set(Calendar.MONTH, numeroMes);
        c.set(Calendar.YEAR, anoExercicio);
        return c.getTime();
    }

    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional lista : unidades) {
                idsUnidades.add(lista.getSubordinada().getId());
                codigosUnidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            if (!filtros.contains("Unidade(s)")) {
                filtros += " Unidade(s): " + codigosUnidades;
            }

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = relatoriosItemDemonstFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getExercicio(), getDataExercicioAndMes(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idsUnidades.add(lista.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 0, false));
        }
        return parametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {
        if (tipoAdministracao != null) {
            listaParametros.add(new ParametrosRelatorios(" vw.TIPOADMINISTRACAO ", ":tipoAdministracao", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false));
            filtros += " Administração: " + tipoAdministracao.getDescricao() + " -";
        }
        if (natureza != null) {
            listaParametros.add(new ParametrosRelatorios(" VW.TIPOUNIDADE ", ":tipoUnidade", null, OperacaoRelatorio.IGUAL, natureza.name(), null, 1, false));
            filtros += " Natureza: " + natureza.getTipo() + " -";
        }
        if (esferaDoPoder != null) {
            listaParametros.add(new ParametrosRelatorios(" VW.ESFERADOPODER ", ":tipoEsfera", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
            filtros += " Poder: " + esferaDoPoder.getDescricao() + " -";
        }
        return listaParametros;
    }
}
