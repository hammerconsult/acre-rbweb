package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ConsultaEntidade;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteIntegracaoSit;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "cadastroImobiliarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCadastroImobiliario", pattern = "/cadastro-imobiliario/novo/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/edita.xhtml"),
    @URLMapping(id = "editarCadastroImobiliario", pattern = "/cadastro-imobiliario/editar/#{cadastroImobiliarioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/edita.xhtml"),
    @URLMapping(id = "listarCadastroImobiliario", pattern = "/cadastro-imobiliario/listar/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/lista.xhtml"),
    @URLMapping(id = "verCadastroImobiliario", pattern = "/cadastro-imobiliario/ver/#{cadastroImobiliarioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/visualizar.xhtml"),
    @URLMapping(id = "verCadastroImobiliarioRetroativo", pattern = "/cadastro-imobiliario/ver-retroativo/#{cadastroImobiliarioControlador.id}/revisao/#{cadastroImobiliarioControlador.idRevisao}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/visualizar.xhtml"),
    @URLMapping(id = "verCadastroImobiliarioInscricao", pattern = "/cadastro-imobiliario/ver-inscricao/#{cadastroImobiliarioControlador.inscricao}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/visualizar.xhtml"),
})
public class CadastroImobiliarioControlador extends PrettyControlador<CadastroImobiliario> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CadastroImobiliarioControlador.class);

    private final int SINCRONIA_DATA = 0;
    private final int SINCRONIA_QUADRA = 1;
    private final int SINCRONIA_LOTE = 2;
    private final int SINCRONIA_FILTRADOS = 3;
    private final int SINCRONIA_BLOCO = 4;
    private final int SINCRONIA_INSCRICAO = 5;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ProcessoIsencaoIPTUFacade processoIsencaoIPTUFacade;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private BloqueioTransferenciaProprietarioFacade bloqueioTransferenciaProprietarioFacade;
    private Converter converterSetor;
    private transient Converter converterEnglobado;
    private ConverterAutoComplete converterLote;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterQuadra;
    private ConverterAutoComplete converterLogradouro;
    private ConverterAutoComplete converterProcesso;
    private ConverterGenerico converterCidade;
    private ConverterGenerico converterCartorio;
    private ConverterGenerico converterAtoLegal;
    private String inscricao;
    private ConfiguracaoTributario configuracaoTributario;
    private ConfiguracaoWebService urlIntegracaoSit;
    private IntegracaoSit integracaoSit;
    private List<Historico> historicos;
    private Historico historico;
    private Construcao construcaoSelecionada;
    private Date hojeInformado;
    private Future<AssistenteIntegracaoSit> futureSit;
    private AssistenteIntegracaoSit assistenteSit;
    private int tipoSincroniaSit;
    private String setorSincronia, quadraSincronia, loteSincronia, blocoSincronia, inscricaoSincronia;
    private Long idRevisao;
    private Date dataRetroativa;
    private List<IntegracaoSit> integracoes;
    private Date dataAlteracaoFiltroHistoricoLegado = null;
    private String motivoFiltroHistoricoLegado = null;
    private String usuarioFiltroHistoricoLegado = null;
    private HistoricoLegadoBCI hsitoricoLegadoBCISelecionado;
    private HistoricoLegadoBCIDataModel historicoLegadoBCIDataModel;
    private AbstractReport abstractReport;
    private Propriedade propriedadeSelecionada;
    private PropriedadeCartorio propriedadeCartorioSelecionada;
    private Compromissario compromissarioSelecionado;
    private List<HistoricoLegadoBCI> historicosLegados;
    private ValidacaoException ve;
    private List<BloqueioTransferenciaProprietario> bloqueioTransferenciaProprietariosList;
    private boolean imovelPenhorado;
    private boolean imovelBloqueado;
    private Setor setor;
    private Quadra quadra;
    private Lote lote;
    private Zona zona;

    public CadastroImobiliarioControlador() {
        super(CadastroImobiliario.class);
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracaoTributario == null) {
            configuracaoTributario = cadastroImobiliarioFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
        return configuracaoTributario;
    }

    public ConfiguracaoWebService getUrlSIT() {
        if (urlIntegracaoSit == null) {
            urlIntegracaoSit = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.SIT);
        }
        return urlIntegracaoSit;
    }

    public List<BloqueioTransferenciaProprietario> getBloqueioTransferenciaProprietariosList() {
        if (bloqueioTransferenciaProprietariosList == null) {
            bloqueioTransferenciaProprietariosList = Lists.newArrayList();
        }
        return bloqueioTransferenciaProprietariosList;
    }

    public void setBloqueioTransferenciaProprietariosList(List<BloqueioTransferenciaProprietario> bloqueioTransferenciaProprietariosList) {
        this.bloqueioTransferenciaProprietariosList = bloqueioTransferenciaProprietariosList;
    }

    public boolean isImovelPenhorado() {
        return imovelPenhorado;
    }

    public boolean isImovelBloqueado() {
        return imovelBloqueado;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public int getTipoSincroniaSit() {
        return tipoSincroniaSit;
    }

    public void setTipoSincroniaSit(int tipoSincroniaSit) {
        this.tipoSincroniaSit = tipoSincroniaSit;
    }

    public String getQuadraSincronia() {
        return quadraSincronia;
    }

    public String getBlocoSincronia() {
        return blocoSincronia;
    }

    public void setBlocoSincronia(String blocoSincronia) {
        this.blocoSincronia = blocoSincronia;
    }

    public void setQuadraSincronia(String quadraSincronia) {
        this.quadraSincronia = quadraSincronia;
    }

    public String getSetorSincronia() {
        return setorSincronia;
    }

    public void setSetorSincronia(String setorSincronia) {
        this.setorSincronia = setorSincronia;
    }

    public String getLoteSincronia() {
        return loteSincronia;
    }

    public void setLoteSincronia(String loteSincronia) {
        this.loteSincronia = loteSincronia;
    }

    public String getInscricaoSincronia() {
        return inscricaoSincronia;
    }

    public void setInscricaoSincronia(String inscricaoSincronia) {
        this.inscricaoSincronia = inscricaoSincronia;
    }

    public int getSINCRONIA_DATA() {
        return SINCRONIA_DATA;
    }

    public int getSINCRONIA_QUADRA() {
        return SINCRONIA_QUADRA;
    }

    public int getSINCRONIA_LOTE() {
        return SINCRONIA_LOTE;
    }

    public int getSINCRONIA_FILTRADOS() {
        return SINCRONIA_FILTRADOS;
    }

    public int getSINCRONIA_BLOCO() {
        return SINCRONIA_BLOCO;
    }

    public int getSINCRONIA_INSCRICAO() {
        return SINCRONIA_INSCRICAO;
    }

    public HistoricoLegadoBCI getHsitoricoLegadoBCISelecionado() {
        return hsitoricoLegadoBCISelecionado;
    }

    public void setHsitoricoLegadoBCISelecionado(HistoricoLegadoBCI hsitoricoLegadoBCISelecionado) {
        this.hsitoricoLegadoBCISelecionado = hsitoricoLegadoBCISelecionado;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public Construcao getConstrucaoSelecionada() {
        return construcaoSelecionada;
    }

    public void setConstrucaoSelecionada(Construcao construcaoSelecionada) {
        this.construcaoSelecionada = construcaoSelecionada;
    }

    public Propriedade getPropriedadeSelecionada() {
        return propriedadeSelecionada;
    }

    public void setPropriedadeSelecionada(Propriedade propriedadeSelecionada) {
        this.propriedadeSelecionada = propriedadeSelecionada;
    }

    public PropriedadeCartorio getPropriedadeCartorioSelecionada() {
        return propriedadeCartorioSelecionada;
    }

    public void setPropriedadeCartorioSelecionada(PropriedadeCartorio propriedadeCartorioSelecionada) {
        this.propriedadeCartorioSelecionada = propriedadeCartorioSelecionada;
    }

    public Compromissario getCompromissarioSelecionado() {
        return compromissarioSelecionado;
    }

    public void setCompromissarioSelecionado(Compromissario compromissarioSelecionado) {
        this.compromissarioSelecionado = compromissarioSelecionado;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public List<HistoricoLegadoBCI> getHistoricosLegados() {
        if (historicosLegados == null) {
            historicosLegados = Lists.newArrayList();
        }
        return historicosLegados;
    }

    public void setHistoricosLegados(List<HistoricoLegadoBCI> historicosLegados) {
        this.historicosLegados = historicosLegados;
    }


    //------------------------------CONVERTERS----------------------------------
    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, cadastroImobiliarioFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Converter getConverterProcesso() {
        if (converterProcesso == null) {
            converterProcesso = new ConverterAutoComplete(Processo.class, cadastroImobiliarioFacade.getProcessoFacade());
        }
        return converterProcesso;
    }

    public ConverterGenerico getConverterCartorio() {
        if (converterCartorio == null) {
            converterCartorio = new ConverterGenerico(Cartorio.class, cadastroImobiliarioFacade.getCartorioFacade());
        }
        return converterCartorio;
    }

    public Converter getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, cadastroImobiliarioFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public Converter getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, cadastroImobiliarioFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, cadastroImobiliarioFacade.getSetorFacade());
        }
        return converterSetor;
    }

    //------------------------------------ COMPLETE METHODS-----------------------------------
    public List<Pessoa> completaPessoa(String parte) {
        return cadastroImobiliarioFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<Quadra> completaQuadra(String parte) {
        if (setor != null && setor.getId() != null) {
            return cadastroImobiliarioFacade.getQuadraFacade().listaFiltrandoPorSetor(parte.trim(), setor, 2);
        }
        return new ArrayList<>();
    }

    public List<Lote> completaLote(String parte) {
        if (quadra != null && quadra.getId() != null) {
            return cadastroImobiliarioFacade.getLoteFacade().listaPorQuadra(parte.trim(), quadra);
        }
        return Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "novoCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setObservacoes(new Texto());
        selecionado.setLote(new Lote());
        selecionado.setTipoMatricula(TipoMatricula.AVERBACAO_DA_ESCRITURA);
        selecionado.popularAtributos(cadastroImobiliarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.CADASTRO_IMOBILIARIO));
    }

    @Override
    @URLAction(mappingId = "verCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarInfos();

    }

    @URLAction(mappingId = "verCadastroImobiliarioInscricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verInscricao() {
        selecionarPorInscricao(inscricao);
        carregarInfos();
    }

    @URLAction(mappingId = "verCadastroImobiliarioRetroativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRetroativo() {
        super.ver();
        RevisaoAuditoria rev = (RevisaoAuditoria) cadastroImobiliarioFacade.getAuditoriaFacade().recuperar(RevisaoAuditoria.class, idRevisao);
        dataRetroativa = rev.getDataHora();
        selecionarPorRevisao(getId(), getIdRevisao());
    }

    @Override
    @URLAction(mappingId = "editarCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarInfos();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    public void selecionarPorRevisao(Long id, Long idRevisao) {
        try {
            CadastroImobiliario bciRecuperado = cadastroImobiliarioFacade.recuperarPorRevisao(id, idRevisao);
            logger.debug("BCI [{}]", bciRecuperado);
            selecionado = bciRecuperado;
            carregarInfos();
        } catch (Exception e) {
            logger.error("Erro ao selecionar pela revisão ", e);
        }

    }

    public void selecionarPorInscricao(String inscricao) {
        setSelecionado(cadastroImobiliarioFacade.recuperaPorInscricao(inscricao));
    }

    public void selecionaLote(SelectEvent evento) {
        if (lote != null && lote.getId() != null) {
            Lote l = cadastroImobiliarioFacade.getLoteFacade().recuperar(lote.getId());
            selecionado.setLote(l);
            selecionado.getLote().setTestadas(l.getTestadas());
            setQuadra(selecionado.getLote().getQuadra());
            setSetor(selecionado.getLote().getSetor());
            calculaInscricao();
        }
    }

    private void carregarInfos() {
        selecionado.popularAtributos(cadastroImobiliarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.CADASTRO_IMOBILIARIO));
        selecionado.getLote().popularAtributos(cadastroImobiliarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.LOTE));
        inicializaHistorico();
        setQuadra(selecionado.getLote().getQuadra());
        setSetor(selecionado.getLote().getSetor());
        setLote(selecionado.getLote());
        setZona(selecionado.getLote().getZona());
        setPropriedadeSelecionada(null);
        setPropriedadeCartorioSelecionada(null);
        setCompromissarioSelecionado(null);
        setConstrucaoSelecionada(null);
        integracaoSit = cadastroImobiliarioFacade.buscarUltimaSincronizacaoSit(selecionado);
        for (Construcao construcao : selecionado.getConstrucoes()) {
            construcao.popularAtributos(cadastroImobiliarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.CONSTRUCAO));
        }
        if (selecionado.getTipoMatricula() == null && operacao.equals(Operacoes.EDITAR)) {
            selecionado.setTipoMatricula(TipoMatricula.AVERBACAO_DA_ESCRITURA);
        }
        if (selecionado.getObservacoes() == null && (isOperacaoEditar() || isOperacaoNovo())) {
            selecionado.setObservacoes(new Texto());
        }
        if (isOperacaoVer()) {
            historicos = cadastroImobiliarioFacade.getHistoricoFacade().recuperaPorCadastro(selecionado, new Date());
        }
        imovelPenhorado = cadastroImobiliarioFacade.imovelComPenhora(selecionado);
        if (isImovelPenhorado()) {
            imovelBloqueado = cadastroImobiliarioFacade.imovelComPenhoraComBloqueio(selecionado);
        } else {
            imovelBloqueado = false;
        }
    }

    private void inicializaHistorico() {
        try {
            historico = new Historico();
            historico.setDigitador(cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente());
            historico.setSequencia(1L);
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
    }

    @Override
    public void salvar() {
        try {
            verificaAreaTotal();
            validarSalvar();
            if (selecionado != null && selecionado.getId() != null) {
                FacesUtil.executaJavaScript("dlgHistorico.show()");
            } else {
                getFacede().salvar(selecionado);
                FacesUtil.addInfo("Salvo com sucesso!", "");
                FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao aslvar {}", ex);
        }
    }

    public void redireciona() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().clear();
        FacesUtil.navegaEmbora(selecionado, this.getCaminhoPadrao());
    }

    @Override
    public void cancelar() {
        Web.getEsperaRetorno();
        redireciona();
    }

    public void salvarHistorico() throws IOException {
        if (validaHistorico()) {
            try {
                historico.setCadastro(selecionado);
                selecionado.getHistoricos().add(historico);
                getFacede().salvarCadastro(selecionado);
                FacesUtil.addInfo("Salvo com sucesso!", "");
                FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    public boolean validaHistorico() {
        boolean retorno = true;
        if (historico.getDataRegistro() != null && historico.getDataSolicitacao() != null && (historico.getDataSolicitacao().compareTo(historico.getDataRegistro()) > 0)) {
            FacesUtil.addError("Não foi possível continuar - A data de solicitação é maior que a data de registro.", "");
            retorno = false;
        }
        if (historico.getDataRegistro() == null) {
            FacesUtil.addError("A data de registro deve ser informada.", "");
            retorno = false;
        }
        if (historico.getDigitador() == null) {
            FacesUtil.addError("O nome do digitador(Você) deve ser informado.", "");
            retorno = false;
        }
        if (historico.getSolicitante() == null || historico.getSolicitante().isEmpty()) {
            FacesUtil.addWarn("O nome do solicitante deve ser informado.", "");
            retorno = false;
        }
        if (historico.getDataSolicitacao() == null) {
            FacesUtil.addError("A data de solicitação deve ser informada.", "");
            retorno = false;
        }
        if (Util.getDataHoraMinutoSegundoZerado(historico.getDataSolicitacao()).after(Util.getDataHoraMinutoSegundoZerado(SistemaFacade.getDataCorrente()))) {
            FacesUtil.addError("A data de solicitação não pode ser maior que a data atual.", "");
            retorno = false;
        }
        if (historico.getMotivo() == null || historico.getMotivo().isEmpty()) {
            FacesUtil.addError("O motivo deve ser informado.", "");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public CadastroImobiliarioFacade getFacede() {
        return cadastroImobiliarioFacade;
    }

    public List<SelectItem> getCartorio() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, " "));
        for (Cartorio object : cadastroImobiliarioFacade.getCartorioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getPessoaJuridica().getRazaoSocial()));
        }
        return toReturn;
    }

    public List<SelectItem> getSetores() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Setor obj : cadastroImobiliarioFacade.getSetorFacade().listaSetoresOrdenadosPorCodigo()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void novoProprietario() {
        try {
            if (isOperacaoNovo()) {
                validaPropretario();
                propriedadeSelecionada.setImovel(selecionado);
                propriedadeSelecionada.setAtual(true);
                propriedadeSelecionada.setDataRegistro(new Date());
                selecionado.getPropriedade().add(propriedadeSelecionada);
                setPropriedadeSelecionada(new Propriedade());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao adicionar proprietário {}", ex);
        }
    }

    public void novoProprietarioCartorio() {
        try {
            validaPropretarioCartorio();
            propriedadeCartorioSelecionada.setImovel(selecionado);
            propriedadeCartorioSelecionada.setAtual(true);
            propriedadeCartorioSelecionada.setDataRegistro(new Date());
            selecionado.getPropriedadeCartorio().add(propriedadeCartorioSelecionada);
            setPropriedadeCartorioSelecionada(new PropriedadeCartorio());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao adicionar proprietário do cartório {}", ex);
        }
    }

    public List<Propriedade> getPropriedades() {
        List<Propriedade> propriedadesAtuais = selecionado.getPropriedadeVigente(dataRetroativa);
        ordenarProprietarios(propriedadesAtuais);
        return propriedadesAtuais;
    }

    public List<Propriedade> getPropriedadesHistorico() {
        List<Propriedade> propriedadesAnteriores = selecionado.getPropriedadeHistorico(dataRetroativa);
        ordenarProprietarios(propriedadesAnteriores);
        return propriedadesAnteriores;
    }

    private void ordenarProprietarios(List<Propriedade> propriedadesAnteriores) {
        propriedadesAnteriores.sort((o1, o2) -> ComparisonChain.start().compare(o1.getInicioVigencia(), o2.getInicioVigencia()).result());
    }

    public List<Compromissario> getCompromissarios() {
        return selecionado.getCompromissarioVigente(dataRetroativa);
    }

    public List<Compromissario> getCompromissariosHistorico() {
        return selecionado.getCompromissariosHistorico(dataRetroativa);
    }

    public List<PropriedadeCartorio> getPropriedadesCartorio() {
        return selecionado.getPropriedadeCartorio();
    }

    public void removeConstrucao(Construcao construcao) {
        if (!isOperacaoEditar()) {
            selecionado.getConstrucoes().remove(construcao);
        }
    }

    public void removeProprietarioCartorio(PropriedadeCartorio p) {
        selecionado.getPropriedadeCartorio().remove(p);
    }

    public void removeProprietario(Propriedade p) {
        if (isOperacaoEditar()) {
            FacesUtil.addAtencao("Não foi possível remover um proprietário ao alterar um Cadastro Imobiliário");
        } else {
            selecionado.getPropriedade().remove(p);
        }
    }

    public List<EnderecoCorreio> enderecosPropriedade(Pessoa pessoa) {
        if (pessoa != null) {
            Pessoa proprietario = cadastroImobiliarioFacade.getPessoaFacade().recuperarSimples(pessoa.getId());
            if (proprietario.getEnderecos() != null) {
                return proprietario.getEnderecos();
            }
        }
        return new ArrayList<>();
    }

    public Double getSomaAreas() {
        double d = 0D;
        if (selecionado != null) {
            for (Construcao c : selecionado.getConstrucoesAtivas()) {
                d = d + c.getAreaConstruida();
            }
        }
        return d;
    }

    public List<AtoLegal> completaAtoLegalIsencao(String parte) {
        return cadastroImobiliarioFacade.getAtoLegalFacade().listaPorPropositoAtoLegal(PropositoAtoLegal.TRIBUTARIO, parte.trim());
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : cadastroImobiliarioFacade.getUfFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterAtoLegalIsencao() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterGenerico(AtoLegal.class, cadastroImobiliarioFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public Converter getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, cadastroImobiliarioFacade.getLoteFacade());
        }
        return converterLote;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, cadastroImobiliarioFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    //TODO MELHORAR....
    public Converter getConverterEnglobado() {
        if (converterEnglobado == null) {
            converterEnglobado = new Converter() {
                @Override
                public Object getAsObject(FacesContext context, UIComponent component, String value) {
                    Construcao retorno = null;
                    try {
                        int hash = Integer.parseInt(value);
                        for (Construcao co : selecionado.getConstrucoesAtivas()) {
                            if (co.hashCode() == (hash)) {
                                retorno = co;
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    return retorno;
                }

                @Override
                public String getAsString(FacesContext context, UIComponent component, Object value) {
                    String retorno = null;
                    if (value instanceof Construcao) {
                        retorno = String.valueOf(value.hashCode());
                    }
                    return retorno;
                }
            };
        }
        return converterEnglobado;
    }

    public List<SelectItem> getConstrucoesDesteImovel() {
        List<SelectItem> aRetornar = new ArrayList<>();
        aRetornar.add(new SelectItem(null, " "));
        for (Construcao construcaoItem : selecionado.getConstrucoesAtivas()) {
            if (!construcaoItem.equals(construcaoSelecionada)) {
                aRetornar.add(new SelectItem(construcaoItem, construcaoItem.getDescricao()));
            }
        }
        return aRetornar;
    }

    public List<SelectItem> getTipoProprietarios() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProprietario object : TipoProprietario.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<Processo> completaProcessos(String parte) {
        try {
            return cadastroImobiliarioFacade.getProcessoFacade().filtraAutoCompleteProcesso(Integer.parseInt(parte.trim()), Integer.parseInt(parte.trim()));
        } catch (java.lang.NumberFormatException e) {
            return cadastroImobiliarioFacade.getProcessoFacade().filtraAutoCompleteProcesso(null, null);
        }
    }

    public void salvarSomenteArquivos() {
        Boolean possuiaArquivo = Boolean.FALSE;
        if (selecionado.getDetentorArquivoComposicao() == null) {
            try {
                possuiaArquivo = getFacede().removerSomenteArquivo(selecionado);
                if (possuiaArquivo) {
                    FacesUtil.addInfo("Lista de anexos limpa com sucesso!", "");
                    FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
                } else {
                    FacesUtil.addAtencao("Nenhum arquivo para ser salvo!");
                }
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        } else if (!selecionado.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            try {
                getFacede().salvarDetentor(selecionado);
                FacesUtil.addInfo("Arquivos salvos com sucesso!", "");
                FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        } else {
            FacesUtil.addAtencao("Nenhum arquivo para ser salvo!");
        }
    }

    public Double getSomaProporcoes() {
        try {
            BigDecimal total = BigDecimal.ZERO;
            if (getPropriedades() != null) {
                for (Propriedade p : getPropriedades()) {
                    total = total.add(BigDecimal.valueOf(p.getProporcao()).setScale(15, RoundingMode.HALF_UP));
                }
            }
            return total.doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Double getSomaProporcoesCartorio() {
        BigDecimal total = BigDecimal.ZERO;
        if (getPropriedadesCartorio() != null) {
            for (PropriedadeCartorio p : getPropriedadesCartorio()) {
                total = total.add(BigDecimal.valueOf(p.getProporcao()).setScale(15, RoundingMode.HALF_UP));
            }
        }
        return total.doubleValue();
    }

    private void validarSalvar() {
        ve = new ValidacaoException();
        if (!ValidaAtributosGenericos.validaAtributosdGenericos(selecionado.getAtributos())) {
            ve.adicionarMensagemDeCampoObrigatorio("Atributos não informados.");
        }
        if (selecionado.getCodigo() == null || selecionado.getCodigo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O código do imóvel deve ser informado.");
        }
        if (!cadastroImobiliarioFacade.validaCodigo(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Inscrição cadastral já existe.");
        }
        if (construcaoSelecionada != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Há uma construção sendo alterada, salve ou cancele para continuar.");
        }
        if (selecionado.getInscricaoCadastral() == null || selecionado.getInscricaoCadastral().isEmpty() || "".equals(selecionado.getInscricaoCadastral())) {
            ve.adicionarMensagemDeCampoObrigatorio("A Inscrição cadastral deve ser informada.");
        }
        if (selecionado.getPropriedade().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O imóvel deve conter no mínimo 01 proprietário.");
        } else if (isOperacaoNovo()) {
            double somaTotal = 0L;
            for (Propriedade propriedade : selecionado.getPropriedade()) {
                somaTotal += propriedade.getProporcao();
            }
            if (somaTotal != 100) {
                ve.adicionarMensagemDeCampoObrigatorio("A soma dos proprietários devem ser de 100%.");
            }
        }
        if (selecionado.getLote() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O lote do imóvel deve ser informado.");
        }
        if ((getMostraUnidadeExterna() || getMostraUnidadeOrganizacional() && (selecionado.getIdentificacaoPatrimonial() == null || selecionado.getIdentificacaoPatrimonial().isEmpty()))) {
            ve.adicionarMensagemDeCampoObrigatorio("A identificação do imovel deve ser informado.");
        }
        ve.lancarException();
    }

    private void validaConstrucoes() {
        ve = new ValidacaoException();
        if (construcaoSelecionada.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("A construção a ser adicionada deve conter uma descrição.");
        }
        if (construcaoSelecionada.getCodigo() == null || construcaoSelecionada.getCodigo() < 1) {
            ve.adicionarMensagemDeCampoObrigatorio("A construção a ser adicionada deve conter um código. Ex. 001, 002, 003.");
        }
        if (construcaoSelecionada.getAreaConstruida() == null || construcaoSelecionada.getAreaConstruida() <= 0.0) {
            ve.adicionarMensagemDeCampoObrigatorio("A construção a ser adicionada deve conter uma área.");
        }
        if (!ValidaAtributosGenericos.validaAtributosdGenericos(construcaoSelecionada.getAtributos())) {
            ve.adicionarMensagemDeCampoObrigatorio("Atributos não informados.");
        }
        ve.lancarException();
    }

    public void gravaConstrucao() {
        try {
            if (isOperacaoNovo()) {
                validaConstrucoes();
                construcaoSelecionada.popularCaracteristicas();
                if (selecionado.getConstrucoes().contains(construcaoSelecionada)) {
                    selecionado.getConstrucoes().set(selecionado.getConstrucoes().indexOf(construcaoSelecionada), construcaoSelecionada);
                } else {
                    selecionado.getConstrucoes().add(construcaoSelecionada);
                }
                setConstrucaoSelecionada(null);
                RequestContext.getCurrentInstance().execute("dialogoConstrucao.hide()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao adicionar compromissário {}", ex);
        }
    }

    public void novaConstrucao() {
        construcaoSelecionada = new Construcao();
        construcaoSelecionada.popularAtributos(cadastroImobiliarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.CONSTRUCAO));
        construcaoSelecionada.setImovel(selecionado);
    }

    public void novaPropriedadeCartorio() {
        propriedadeCartorioSelecionada = new PropriedadeCartorio();
        propriedadeCartorioSelecionada.setImovel(selecionado);
    }

    public void novaPropriedade() {
        propriedadeSelecionada = new Propriedade();
        propriedadeSelecionada.setImovel(selecionado);
    }

    public void novoCompromissario() {
        compromissarioSelecionado = new Compromissario();
        compromissarioSelecionado.setCadastroImobiliario(selecionado);
    }

    @URLAction(mappingId = "listarCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
        hojeInformado = new Date();
        tipoSincroniaSit = SINCRONIA_DATA;
    }

    public List<Historico> getListaHistoricos() {
        return historicos;
    }

    public Penhora getUltimaPenhora() {
        return cadastroImobiliarioFacade.retornaPenhoraDoImovel(selecionado);
    }

    private void verificaAreaTotal() {
        if (selecionado.getAreaTotalConstruida() != null && selecionado.getAreaTotalConstruida().setScale(4, RoundingMode.HALF_EVEN).compareTo(BigDecimal.valueOf(getSomaAreas()).setScale(4, RoundingMode.HALF_EVEN)) != 0) {
            // caso a área seja diferente o usuário deve ser informado, mas mesmo assim continuar
            FacesUtil.addError("Atenção!", "O valor informado na área total da construção é diferente da área total somada pelo lançamento das construções");
        }
    }

    public void carregaHistoricoLegado() {
        try {
            //setHistoricosLegados(cadastroImobiliarioFacade.getHistoricoLegadoFacade().listaHistoricoBCINOVO(selecionado));
        } catch (Exception e) {
            logger.error("Erro ao carregar os historicos legados {}", e);
        }
    }

    public List<FaceServico> servicosUrbanos(Face face) {
        return cadastroImobiliarioFacade.getServicosFace(face);
    }

    private void validaPropretario() {
        ve = new ValidacaoException();
        if (propriedadeSelecionada.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o proprietário.");
        }
        if (propriedadeSelecionada.getProporcao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a proporção.");
        } else if (propriedadeSelecionada.getProporcao() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A proporção deve ser maior que zero (0).");
        }
        if (propriedadeSelecionada.getTipoProprietario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de proprietario.");
        }
        if (propriedadeSelecionada.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o inicio da vigência.");
        }
        BigDecimal valida = BigDecimal.valueOf(getSomaProporcoes());
        valida = valida.add(BigDecimal.valueOf(propriedadeSelecionada.getProporcao()));
        if (valida.compareTo(new BigDecimal(100)) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A soma das porcentagens dos proprietários não devem exceder 100%.");
        }
        ve.lancarException();
    }

    private void validaPropretarioCartorio() {
        ve = new ValidacaoException();
        if (propriedadeCartorioSelecionada.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Proprietário.");
        }
        if (propriedadeCartorioSelecionada.getProporcao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Proporção.");
        }
        if (propriedadeCartorioSelecionada.getTipoProprietario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Proprietario.");
        }
        if (propriedadeCartorioSelecionada.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Inicio da vigência.");
        }
        BigDecimal valida = BigDecimal.valueOf(getSomaProporcoesCartorio());
        valida = valida.add(BigDecimal.valueOf(propriedadeCartorioSelecionada.getProporcao()));
        if (valida.compareTo(new BigDecimal(100)) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A soma das porcentagens dos proprietários não devem exceder 100%.");
        }
        ve.lancarException();
    }

    public void atualizaAreaTotalConstruida() {
        List<Construcao> listaConstrucao = selecionado.getConstrucoesAtivas();
        Double areaTotalConstruida = 0D;
        if (listaConstrucao != null) {
            for (Construcao constucao : listaConstrucao) {
                areaTotalConstruida += constucao.getAreaConstruida();
            }
            selecionado.setAreaTotalConstruida(new BigDecimal(areaTotalConstruida));
        } else {
            selecionado.setAreaTotalConstruida(null);
        }
    }

    public void calculaInscricao() {
        if (selecionado.getLote() != null) {
            ConfiguracaoTributario config = cadastroImobiliarioFacade.getConfiguracaoTributarioFacade().retornaUltimo();
            StringBuilder sb = new StringBuilder();
            selecionado.setAutonoma(cadastroImobiliarioFacade.calcularAutonomaPeloLote(selecionado.getLote()));
            String codigoDistrito;
            if (selecionado.getLote().getSetor().getDistrito() != null) {
                codigoDistrito = selecionado.getLote().getSetor().getDistrito().getCodigo();
            } else {
                codigoDistrito = "1";
            }
            sb.append(StringUtil.preencheString(codigoDistrito, config.getNumDigitosDistrito(), '0'))
                .append(StringUtil.preencheString(selecionado.getLote().getSetor().getCodigo(), config.getNumDigitosSetor(), '0'))
                .append(StringUtil.preencheString(selecionado.getLote().getQuadra().getCodigo(), config.getNumDigitosQuadra(), '0'))
                .append(StringUtil.preencheString(selecionado.getLote().getCodigoLote(), config.getNumDigitosLote(), '0'))
                .append(StringUtil.preencheString(selecionado.getAutonoma().toString(), config.getNumDigitosUnidade(), '0'));
            selecionado.setInscricaoCadastral(sb.toString());
            selecionado.setCodigo(sb.toString());
        }
    }

    private void validaExclusao() {
        ve = new ValidacaoException();
        if (imovelBloqueado) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O imóvel encontra-se bloqueado por penhora, não pode ser excluído.");
        }
        ve.lancarException();
    }

    public void atribuiTipoDeMatricula() {
        if (selecionado.getTipoMatricula().equals(TipoMatricula.AVERBACAO_DA_ESCRITURA)) {
            selecionado.setDataTitulo(null);
            selecionado.setNumeroDoTitulo(null);
        } else {
            selecionado.setTipoMatricula(TipoMatricula.TITULO_DEFINITIVO_DE_POSSE);
            selecionado.setDataRegistro(null);
            selecionado.setMatriculaRegistro(null);
        }
    }

    public TipoMatricula getTipoDeAverbacao() {
        return TipoMatricula.AVERBACAO_DA_ESCRITURA;
    }

    public TipoMatricula getTipoDePosse() {
        return TipoMatricula.TITULO_DEFINITIVO_DE_POSSE;
    }

    public boolean getRenderizaAverbacao() {
        return selecionado.getTipoMatricula() != null && TipoMatricula.AVERBACAO_DA_ESCRITURA.equals(selecionado.getTipoMatricula());
    }

    public boolean getRenderizaPanelMatricula() {
        return selecionado.getTipoMatricula() != null;
    }

    @Override
    public void excluir() {
        try {
            validaExclusao();
            getFacede().remover(selecionado);
            FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
            FacesUtil.addInfo("Registro excluído com sucesso!", "");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void descobrirETratarException(Exception e) {
        try {
            Util.getRootCauseDataBaseException(e);
        } catch (SQLIntegrityConstraintViolationException sql) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Este cadastro imobiliário ou suas construções possuem dependencias, não podem ser excluídos");
        } catch (SQLException sql) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Este cadastro imobiliário ou suas construções possuem dependencias, não podem ser excluídos");
        } catch (Exception throwable) {
            logger.error(throwable.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "");
        }
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public List<CadastroImobiliario> completaCadastroImobiliarioAtivo(String parte) {
        return getFacede().listaFiltrando(parte.trim(), true, "inscricaoCadastral");
    }

    public List<UnidadeExterna> completaUnidadeExterna(String parte) {
        return getFacede().getUnidadeExternaFacade().listaFiltrandoPessoaJuridicaEsferaGoverno(parte.trim());
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        return getFacede().getUnidadeOrganizacionalFacade().listaTodosPorFiltro(parte.trim());
    }

    public boolean getMostraUnidadeExterna() {
        if (selecionado != null) {
            if (selecionado.getLote() != null && selecionado.getLote().getId() != null) {
                if (selecionado.getLote().getAtributos() != null && !selecionado.getLote().getAtributos().isEmpty()) {
                    ConfiguracaoPatrimonioLote configuracaoPatrimonioLote = getConfiguracaoTributario().getConfiguracaoPatrimonioLote();
                    if (configuracaoPatrimonioLote != null) {
                        ValorPossivel valor = selecionado.getLote().getAtributos().get(configuracaoPatrimonioLote.getAtributo()).getValorDiscreto();
                        for (ItemConfiguracaoPatrimonioLote item : configuracaoPatrimonioLote.getItensConfiguracaoPatrimonioLote()) {
                            if (item != null && item.getValorPossivel() != null && valor != null && item.getValorPossivel().getId().equals(valor.getId()) && (PatrimonioDoLote.ESTADUAL.equals(item.getPatrimonioDoLote()) || PatrimonioDoLote.FEDERAL.equals(item.getPatrimonioDoLote()))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean getMostraUnidadeOrganizacional() {
        if (selecionado != null) {
            if (selecionado.getLote() != null && selecionado.getLote().getId() != null)
                if (!selecionado.getLote().getAtributos().isEmpty()) {
                    ConfiguracaoPatrimonioLote configuracaoPatrimonioLote = getConfiguracaoTributario().getConfiguracaoPatrimonioLote();
                    if (configuracaoPatrimonioLote != null) {
                        ValorPossivel valor = selecionado.getLote().getAtributos().get(configuracaoPatrimonioLote.getAtributo()).getValorDiscreto();
                        for (ItemConfiguracaoPatrimonioLote item : configuracaoPatrimonioLote.getItensConfiguracaoPatrimonioLote()) {
                            if (item != null && item.getValorPossivel() != null && valor != null && item.getValorPossivel().getId().equals(valor.getId()) && PatrimonioDoLote.MUNICIPAL.equals(item.getPatrimonioDoLote())) {
                                return true;
                            }
                        }
                    }
                }
        }
        return false;
    }

    public Date getHojeInformado() {
        return hojeInformado;
    }

    public void setHojeInformado(Date hojeInformado) {
        this.hojeInformado = hojeInformado;
    }

    public void sincronizarDadosComSIT() {
        try {
            UsuarioSistema usuarioCorrente = cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente();
            assistenteSit = new AssistenteIntegracaoSit(Lists.newArrayList(selecionado.getInscricaoCadastral()));
            assistenteSit.setLogin(cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
            assistenteSit.integrar(getUrlSIT().getUrl());
            cadastroImobiliarioFacade.atualizaValoresDosCadastros(Lists.newArrayList(selecionado));
            FacesUtil.executaJavaScript("iniciaSit();");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            notificarFalhaAoSincronizar(e);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Verifique a disponibilidade do Integrador");
            notificarFalhaAoSincronizar(e);
        }

    }

    private void notificarFalhaAoSincronizar(Exception e) {
        logger.error("Não foi possível sincronizar com o SIT o imóvel {}", selecionado);
        logger.debug("Erro na sincronização", e);
    }

    public AssistenteIntegracaoSit getAssistenteSit() {
        return assistenteSit;
    }

    public void sincronizarDadosComSITPorPeriodo() {
        try {
            UsuarioSistema usuarioCorrente = cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente();
            if (tipoSincroniaSit == SINCRONIA_DATA) {
                DateTime hoje = new DateTime(hojeInformado);
                DateTime ontem = hoje.minusDays(1);
                assistenteSit = new AssistenteIntegracaoSit(ontem.toDate(), hoje.toDate());
            } else if (tipoSincroniaSit == SINCRONIA_QUADRA) {
                assistenteSit = new AssistenteIntegracaoSit(setorSincronia, quadraSincronia);
            } else if (tipoSincroniaSit == SINCRONIA_LOTE) {
                assistenteSit = new AssistenteIntegracaoSit(setorSincronia, quadraSincronia, loteSincronia);
            } else if (tipoSincroniaSit == SINCRONIA_BLOCO) {
                assistenteSit = new AssistenteIntegracaoSit(blocoSincronia);
            } else if (tipoSincroniaSit == SINCRONIA_INSCRICAO) {
                assistenteSit = new AssistenteIntegracaoSit(Lists.newArrayList(inscricaoSincronia));
            }
            assistenteSit.setLogin(cadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
            assistenteSit.integrar(getUrlSIT().getUrl());
            FacesUtil.executaJavaScript("iniciaSit();");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Verifique a disponibilidade do Integrador");
            logger.error("Erro ao integrar sit. ", e);
        }
    }

    public void consultarAssisteSit() {
        if (assistenteSit != null) {
            assistenteSit.consultar();
            if (assistenteSit.isDone()) {
                assistenteSit.setExecutando(false);
                FacesUtil.executaJavaScript("terminaSit();");
                FacesUtil.atualizarComponente("formBarra");
                assistenteSit = null;
            }
        }
    }

    public void sincronizarDadosComSITPorFiltrados(ConsultaEntidade consultaEntidade) {
        try {
            List<String> inscricoes = cadastroImobiliarioFacade.buscarInscricoesCadastraisDosListadosParaIntegracaoSit(consultaEntidade);
            assistenteSit = new AssistenteIntegracaoSit(inscricoes);
            assistenteSit.integrar(getUrlSIT().getUrl());
            FacesUtil.executaJavaScript("iniciaSit();");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro ao integrar sit por filtrados. ", e);
        }
    }

    public void atualizarBotoesSitDoListar() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public String getDescricaoSincronizacao() {
        if (integracaoSit != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if (integracaoSit.getSucesso()) {
                return sdf.format(integracaoSit.getDataIntegracao()) + " com sucesso.";
            } else {
                return sdf.format(integracaoSit.getDataIntegracao()) + " sem sucesso.";
            }
        }
        return "";
    }

    public IntegracaoSit getIntegracaoSit() {
        return integracaoSit;
    }

    public void alterarTabs(TabChangeEvent evt) {
        String tab = evt.getTab().getId();
        switch (tab) {
            case "tabAlvaraConstrucaoHabitese": {
                selecionado.setAlvarasContrucao(cadastroImobiliarioFacade.getAlvaraConstrucaoFacade()
                    .buscarAlvarasConstrucaoPorCadastroImobiliario(selecionado));
                selecionado.setHabiteses(cadastroImobiliarioFacade.getHabiteseConstrucaoFacade()
                    .buscarHabitesConstrucaoPorCadastroImobiliario(selecionado));
                break;
            }
            case "tabHistoricoLegado": {
                carregaHistoricoLegado();
                break;
            }
            case "tabMapa": {
                verInscricaoMapa();
                break;
            }
            case "tabBloqueioTransferenciaProprietario": {
                setBloqueioTransferenciaProprietariosList(bloqueioTransferenciaProprietarioFacade
                    .buscarBloqueiosPorCadastro(getSelecionado()));
                break;
            }
        }
    }

    public void verInscricaoMapa() {
        int setor = Integer.parseInt(selecionado.getLote().getSetor().getCodigo());
        int quadra = selecionado.getLote().getQuadra().getCodigoToInteger();
        int lote = Integer.parseInt(selecionado.getLote().getCodigoLote());
        String inscricaoMapa = "1-" + setor + "-" + quadra + "-" + lote;
        FacesUtil.executaJavaScript("verIncricao('" + inscricaoMapa + "')");
    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public Date getDataRetroativa() {
        return dataRetroativa;
    }

    public void setDataRetroativa(Date dataRetroativa) {
        this.dataRetroativa = dataRetroativa;
    }

    public void verCadastroPelaDataRetroativa() {
        if (dataRetroativa == null) {
            FacesUtil.addCampoObrigatorio("A data retroativa deve ser informada!");
        } else {
            Long idRevisao = cadastroImobiliarioFacade.ultimaRevisaoNaData(dataRetroativa, selecionado.getId());
            if (idRevisao != null) {
                FacesUtil.redirecionamentoInterno("/cadastro-imobiliario/ver-retroativo/" + selecionado.getId() + "/" + idRevisao + "/");
            } else {
                FacesUtil.addCampoObrigatorio("Nenhuma alteração encontrada para a data informada!");
            }
        }
    }

    public List<IntegracaoSit> getIntegracoes() {
        return integracoes;
    }

    public void setIntegracoes(List<IntegracaoSit> integracoes) {
        this.integracoes = integracoes;
    }

    public void carregarHistoricoSit() {
        integracoes = cadastroImobiliarioFacade.buscarIntegracoesSitComUsuarioDaAuditoria(selecionado);
    }

    public Date getDataAlteracaoFiltroHistoricoLegado() {
        return dataAlteracaoFiltroHistoricoLegado;
    }

    public void setDataAlteracaoFiltroHistoricoLegado(Date dataAlteracaoFiltroHistoricoLegado) {
        this.dataAlteracaoFiltroHistoricoLegado = dataAlteracaoFiltroHistoricoLegado;
    }

    public String getMotivoFiltroHistoricoLegado() {
        return motivoFiltroHistoricoLegado;
    }

    public void setMotivoFiltroHistoricoLegado(String motivoFiltroHistoricoLegado) {
        this.motivoFiltroHistoricoLegado = motivoFiltroHistoricoLegado;
    }

    public String getUsuarioFiltroHistoricoLegado() {
        return usuarioFiltroHistoricoLegado;
    }

    public void setUsuarioFiltroHistoricoLegado(String usuarioFiltroHistoricoLegado) {
        this.usuarioFiltroHistoricoLegado = usuarioFiltroHistoricoLegado;
    }


    public class HistoricoLegadoBCIDataModel extends LazyDataModel<HistoricoLegadoBCI> implements Serializable {

        @Override
        public List<HistoricoLegadoBCI> load(int inicio, int maximo, String s, SortOrder sortOrder, Map<String, String> map) {
            String select = "select hlbci";
            String from = " from HistoricoLegadoBCI hlbci";
            String where = montarWhere();
            String orderBy = " order by hlbci.dtaimvhst desc";
            Map<String, Object> parametros = montarParametros();
            Map<String, Object> dados = recuperadorFacade.getResultadoDe("select count(hlbci)", select, from, where, orderBy, parametros, inicio, maximo);
            if (dados != null) {
                setRowCount(Integer.parseInt("" + dados.get("COUNT")));
            }

            List<HistoricoLegadoBCI> retorno = (List<HistoricoLegadoBCI>) dados.get("DADOS");
            return retorno;
        }

        public void posSit() {
            if (futureSit != null && futureSit.isDone()) {
                assistenteSit.setExecutando(false);
                FacesUtil.executaJavaScript("terminaSit();");
                if (!assistenteSit.getLogs().isEmpty()) {
                    for (String log : assistenteSit.getLogs()) {
                        FacesUtil.addOperacaoNaoRealizada(log);
                    }
                }
            }
        }

        private Map<String, Object> montarParametros() {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cadastroImobiliarioId", selecionado.getId());
            if (dataAlteracaoFiltroHistoricoLegado != null) {
                parametros.put("dataAlteracao", dataAlteracaoFiltroHistoricoLegado);
            }
            if (motivoFiltroHistoricoLegado != null && !motivoFiltroHistoricoLegado.trim().isEmpty()) {
                parametros.put("motivo", "%" + motivoFiltroHistoricoLegado.toUpperCase() + "%");
            }
            if (usuarioFiltroHistoricoLegado != null && !usuarioFiltroHistoricoLegado.trim().isEmpty()) {
                parametros.put("usuario", "%" + usuarioFiltroHistoricoLegado.toUpperCase() + "%");
            }
            return parametros;
        }

        private String montarWhere() {

            String condicao = " where hlbci.cadastroImobiliario.id = :cadastroImobiliarioId";
            if (dataAlteracaoFiltroHistoricoLegado != null) {
                condicao += " and to_char(hlbci.dtaimvhst,'dd/MM/yyyy') = to_char(:dataAlteracao,'dd/MM/yyyy') ";
            }
            if (motivoFiltroHistoricoLegado != null && !motivoFiltroHistoricoLegado.trim().isEmpty()) {
                condicao += " and (upper(hlbci.mtvaltimv1) like :motivo ";
                condicao += " or upper(hlbci.mtvaltimv2) like :motivo ";
                condicao += " or upper(hlbci.mtvaltimv3) like :motivo) ";
            }
            if (usuarioFiltroHistoricoLegado != null && !usuarioFiltroHistoricoLegado.trim().isEmpty()) {
                condicao += " and upper(hlbci.usrhstimv) like :usuario ";
            }
            return condicao;
        }

        @Override
        public void setRowIndex(int rowIndex) {
            if (rowIndex == -1 || getPageSize() == 0) {
                super.setRowIndex(-1);
            } else {
                super.setRowIndex(rowIndex % getPageSize());
            }
        }
    }

    public HistoricoLegadoBCIDataModel getHistoricoLegadoBCIDataModel() {
        if (historicoLegadoBCIDataModel == null) {
            historicoLegadoBCIDataModel = new HistoricoLegadoBCIDataModel();
        }

        return historicoLegadoBCIDataModel;
    }

    public void setHistoricoLegadoBCIDataModel(HistoricoLegadoBCIDataModel historicoLegadoBCIDataModel) {
        this.historicoLegadoBCIDataModel = historicoLegadoBCIDataModel;
    }

    public boolean isencaoEfetivada(IsencaoCadastroImobiliario isencaoCadastroImobiliario) {
        return processoIsencaoIPTUFacade.isencaoEfetivada(isencaoCadastroImobiliario);
    }

    public void adicionarCompromissario() {
        try {
            validarCompromissario();
            for (Compromissario compromissario : selecionado.getCompromissarioVigente()) {
                compromissario.encerraVigencia();
            }
            Util.adicionarObjetoEmLista(selecionado.getListaCompromissarios(), compromissarioSelecionado);
            setCompromissarioSelecionado(new Compromissario());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao adicionar compromissário {}", ex);
        }
    }

    public void editarCompromissario(Compromissario compromissario) {
        setCompromissarioSelecionado(compromissario);
    }

    public void editarPropriedadeCartorio(PropriedadeCartorio pc) {
        setPropriedadeCartorioSelecionada(pc);
    }

    public void editarPropriedade(Propriedade p) {
        setPropriedadeSelecionada(p);
        selecionado.getPropriedade().remove(p);
    }

    public void removerCompromissario(Compromissario compromissario) {
        selecionado.getListaCompromissarios().remove(compromissario);
    }

    private void validarCompromissario() {
        ve = new ValidacaoException();
        if (compromissarioSelecionado.getCompromissario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Compromissário.");
        }
        if (compromissarioSelecionado.getProporcao() == null || compromissarioSelecionado.getProporcao() == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Proporção.");
        }
        if (compromissarioSelecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Inicio de Vigência.");
        }
        if (compromissarioSelecionado.getInicioVigencia() != null && compromissarioSelecionado.getFimVigencia() != null && compromissarioSelecionado.getFimVigencia().before(compromissarioSelecionado.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de fim de vigência não pode ser anterior à data de inicio de vigência.");
        }
        ve.lancarException();
    }

    public void gerarRelatorioHistoricoLegadoBCI(HistoricoLegadoBCI historicoLegadoBCI) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            abstractReport = AbstractReport.getAbstractReport();
            dto.adicionarParametro("USUARIO", abstractReport.usuarioLogado().getNome());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("complementoQuery", " where leg.id = " + historicoLegadoBCI.getId());
            dto.adicionarParametro("SECRETARIA", "Sistema Integrado de Administração Tributária");
            dto.adicionarParametro("MODULO", ModuloFAQ.TRIBUTARIO.getDescricao());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("tributario/historico-legado-bci/");
            ReportService.getInstance().gerarRelatorio(abstractReport.usuarioLogado(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException ex) {
            ReportService.getInstance().abrirDialogConfirmar(ex);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("error gerarRelatorioHistoricoLegadoBCI {} " + e);
        }
    }

    public void atualizarCoordenadasDoSit(){
        if (!Strings.isNullOrEmpty(selecionado.getCoordenadasSIT())) {
            cadastroImobiliarioFacade.atualizarCoordenadasDoSit(selecionado);
        }
    }

    private String getNomeRelatorio() {
        return "B.C.I - Boletim de Cadastramento Imobiliário do Histórico Legado";
    }
}


