/*
 * Codigo gerado automaticamente em Tue Feb 14 10:59:19 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoAposentadoria;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.configuracao.TempoMinimoAposentadoria;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.GrupoBeneficioEsocial;
import br.com.webpublico.entidades.rh.esocial.TipoBeneficioEsocial;
import br.com.webpublico.entidades.rh.esocial.TipoBeneficioPrevidenciario;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.TipoMotivoDesligamentoESocial;
import br.com.webpublico.enums.rh.estudoatuarial.TipoBeneficioEstudoAtuarial;
import br.com.webpublico.enums.rh.esocial.TipoPlanoSegregacaoMassa;
import br.com.webpublico.enums.rh.sig.TipoAposentadoriaEspecialSIG;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.AvisoPrevioFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoAposentadoriaFacade;
import br.com.webpublico.negocios.rh.esocial.TipoBeneficioEsocialFacade;
import br.com.webpublico.negocios.rh.esocial.TipoBeneficioPrevidenciarioFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static br.com.webpublico.util.DataUtil.getAnosAndDiasFormatadosPorPeriodo;

@ManagedBean(name = "aposentadoriaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAposentadoria", pattern = "/aposentadoria/novo/", viewId = "/faces/rh/administracaodepagamento/aposentadoria/edita.xhtml"),
    @URLMapping(id = "editarAposentadoria", pattern = "/aposentadoria/editar/#{aposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aposentadoria/edita.xhtml"),
    @URLMapping(id = "listarAposentadoria", pattern = "/aposentadoria/listar/", viewId = "/faces/rh/administracaodepagamento/aposentadoria/lista.xhtml"),
    @URLMapping(id = "verAposentadoria", pattern = "/aposentadoria/ver/#{aposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aposentadoria/visualizar.xhtml"),
    @URLMapping(id = "corrigirAposentadoria", pattern = "/aposentadoria/correcao/", viewId = "/faces/rh/administracaodepagamento/aposentadoria/correcao.xhtml")
})
public class AposentadoriaControlador extends PrettyControlador<Aposentadoria> implements Serializable, CRUD {

    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterGenerico converterUnidadeOrganizacional;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConverterGenerico converterMatriculaFP;
    @EJB
    private ContaCorrenteFacade contaCorrenteFacade;
    private ConverterGenerico converterContaCorrente;
    @EJB
    private CBOFacade cboFacade;
    private ConverterGenerico converterCbo;
    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;
    private ConverterGenerico converterNaturezaRendimento;
    @EJB
    private TipoAposentadoriaFacade tipoAposentadoriaFacade;
    private ConverterGenerico converterTipoAposentadoria;
    private ConverterGenerico converterTipoBeneficioPrevidenciario;
    private ConverterGenerico converterTipoBeneficioEsocial;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ItemAposentadoria itemAposentadoria;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private PercentualConverter percentualConverter;
    private InvalidezAposentado invalidezAposentado;
    @EJB
    private CIDFacade CIDFacade;
    private ConverterAutoComplete converterCid;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisica;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private Boolean tipoReajusteParidade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private ConverterAutoComplete converterHO;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    private String mensagem;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private Integer diasTempoEfetivoServico = 0;
    private List<ItemFichaFinanceiraFP> itensFichaFinanceira;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    private ConverterAutoComplete converterProcesso;
    private ConverterGenerico converterEventoFP;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFolhasFacade;
    private ConverterAutoComplete converterRecursoFP;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    private LotacaoFuncional lotacaoFuncional;
    private RecursoDoVinculoFP recursoDoVinculoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Integer indiceAba;
    private ConverterAutoComplete converterInvalidezMedico;
    @EJB
    private TipoBeneficioPrevidenciarioFacade tipoBeneficioPrevidenciarioFacade;
    @EJB
    private TipoBeneficioEsocialFacade tipoBeneficioEsocialFacade;
    private List<CID> cids;
    private InvalidezMedico invalidezMedico;
    private List<InvalidezMedico> listInvalidezMedico;
    private String selecionarMedico = "carregar";
    @EJB
    private ConfiguracaoAposentadoriaFacade configuracaoAposentadoriaFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    private AtoLegal atoLegal;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private AvisoPrevioFacade avisoPrevioFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    private Boolean renderizarSalvar;
    private ExoneracaoRescisao exoneracaoRescisao;

    public AposentadoriaControlador() {
        super(Aposentadoria.class);
        renderizarSalvar = false;
    }

    public List<CID> getCids() {
        return cids;
    }

    public void setCids(List<CID> cids) {
        this.cids = cids;
    }

    public InvalidezMedico getInvalidezMedico() {
        return invalidezMedico;
    }

    public void setInvalidezMedico(InvalidezMedico invalidezMedico) {
        this.invalidezMedico = invalidezMedico;
    }

    public List<InvalidezMedico> getListInvalidezMedico() {
        return listInvalidezMedico;
    }

    public void setListInvalidezMedico(List<InvalidezMedico> listInvalidezMedico) {
        this.listInvalidezMedico = listInvalidezMedico;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    @URLAction(mappingId = "novaAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        indiceAba = 0;
        itemAposentadoria = new ItemAposentadoria();
        Integer indice = (Integer) Web.pegaDaSessao(Integer.class);
        if (indice != null) {
            indiceAba = indice;
        }

        invalidezAposentado = (InvalidezAposentado) Web.pegaDaSessao(InvalidezAposentado.class);
        if (invalidezAposentado == null) {
            invalidezAposentado = new InvalidezAposentado();
        }

        recursoDoVinculoFP = new RecursoDoVinculoFP();
        lotacaoFuncional = new LotacaoFuncional();
        hierarquiaOrganizacional = null;
        //itemAposentadoriaEventoFPs = new LinkedList<>();
        cids = new LinkedList<>();
        listInvalidezMedico = new LinkedList<>();
        invalidezMedico = new InvalidezMedico();
        diasTempoEfetivoServico = 0;

        if (situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.APOSENTADO) == null) {
            FacesUtil.addWarn("Atenção !", "A Situação Funcional para Aposentadoria ainda não foi cadastrada !");
        }

        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.APOSENTADORIA) == null) {
            FacesUtil.addWarn("Atenção !", "O Tipo de Provimento para Aposentados ainda não foi cadastrado !");
        }
        novoAtoLegal();
        exoneracaoRescisao = new ExoneracaoRescisao();
    }

    @URLAction(mappingId = "verAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ordenarItemAposentadoria(selecionado.getItemAposentadorias());
    }

    @URLAction(mappingId = "editarAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

        indiceAba = 0;
        this.selecionado.setHierarquiaOrganizacional(new HierarquiaOrganizacional());
        this.selecionado.getHierarquiaOrganizacional().setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        this.selecionado.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidadeUltima(getAposentadoria().getUnidadeOrganizacional().getId()));
        itemAposentadoria = new ItemAposentadoria();

        invalidezAposentado = (InvalidezAposentado) Web.pegaDaSessao(InvalidezAposentado.class);
        if (invalidezAposentado == null) {
            invalidezAposentado = new InvalidezAposentado();
        }


        cids = new LinkedList<>();
        listInvalidezMedico = new LinkedList<>();
        invalidezMedico = new InvalidezMedico();
        recursoDoVinculoFP = new RecursoDoVinculoFP();
        lotacaoFuncional = new LotacaoFuncional();
        hierarquiaOrganizacional = null;
        FichaFinanceiraFP fichaRecuperada = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(getAposentadoria().getContratoFP());
        if (fichaRecuperada != null) {
            fichaRecuperada = fichaFinanceiraFPFacade.recuperar(fichaRecuperada.getId());
            this.selecionado.setFichaFinanceiraFP(fichaRecuperada);
        }
        sugerirTipoReajuste();
        novoAtoLegal();
        ordenarItemAposentadoria(selecionado.getItemAposentadorias());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarTipoAposentadoria();
            for (InvalidezAposentado ia : selecionado.getInvalidezAposentados()) {
                validarVigenciaInvalidezAposentado(ia);
            }
            //O novo vinculo de aposentado recebe o mesmo numero do vinculo de contrato
            getAposentadoria().setNumero(getAposentadoria().getContratoFP().getNumero());
            getAposentadoria().setMatriculaFP(getAposentadoria().getContratoFP().getMatriculaFP());
            if (isOperacaoNovo()) {
                criarExoneracao();
            } else {
                aposentadoriaFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void salvarExoneracaoRescisao() {
        try {
            validarCamposExoneracaoRescisao();
            validarAfastamentoAndConcessaoFerias();
            Date dataFinalVigenciaContratoFP = getFinalVigenciaContratoFP();
            exoneracaoRescisaoFacade.salvarNovo(exoneracaoRescisao, dataFinalVigenciaContratoFP);
            RequestContext.getCurrentInstance().execute("dlgExoneracaoRescisao.hide()");
            salvarAposentadoriaNovo();
        } catch (SemBasePeriodoAquisitivoException semBaseEx) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/cargo/editar/" + exoneracaoRescisao.getVinculoFP().getContratoFP().getCargo().getId() + "' target='_blank'>Cadastro de Cargos</a></b>";
            FacesUtil.addOperacaoNaoRealizada("O cargo <b>" + exoneracaoRescisao.getVinculoFP().getContratoFP().getCargo() + "</b> não possui bases de período aquisitivo vigentes em <b>" + Util.formatterDiaMesAno.format(UtilRH.getDataOperacao()) + "</b>.<br />Verifique as configurações no " + url);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            FacesUtil.addAtencao("Não foi passivel salvar a exoneração/rescisão: " + e.getMessage());
            descobrirETratarException(e);
        }
    }

    public void salvarAposentadoriaNovo() {
        try {
            aposentadoriaFacade.salvarNovo(selecionado);
            if (renderizarSalvar) {
                FacesUtil.addWarn("Atenção!", "Por favor, verifique os registros de Pensão Alimentícia migrados.");
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void criarExoneracao() {
        if (validarServidorNaoExonerado()) {
            exoneracaoRescisao = new ExoneracaoRescisao();
            exoneracaoRescisao.setVinculoFP(selecionado.getContratoFP());
            exoneracaoRescisao.setDataRescisao(DataUtil.localDateToDate(DataUtil.dateToLocalDate(selecionado.getInicioVigencia()).minusDays(1)));
            exoneracaoRescisao.setMotivoExoneracaoRescisao(selecionado.getTipoAposentadoria().getMotivoExoneracaoRescisao() != null ? selecionado.getTipoAposentadoria().getMotivoExoneracaoRescisao() : null);
            exoneracaoRescisao.setAtoLegal(recuperarAtoLegal());
            RequestContext.getCurrentInstance().execute("dlgExoneracaoRescisao.show()");
        } else {
            RequestContext.getCurrentInstance().execute("dlgExoneracaoExistente.show()");
        }
    }

    private boolean validarServidorNaoExonerado() {
        if (exoneracaoRescisaoFacade.existeExoneracaoRescisaoPorVinculoFP(selecionado.getContratoFP())) {
            Reintegracao reintegracao = exoneracaoRescisaoFacade.getReintegracaoFacade().recuperarUltimaReintegracao(selecionado.getContratoFP());
            if (reintegracao == null || (reintegracao.getDataReintegracao().compareTo(DataUtil.localDateToDate(DataUtil.dateToLocalDate(selecionado.getInicioVigencia()).minusDays(1))) >= 0)) {
                return false;
            }
        }
        return true;
    }


    private AtoLegal recuperarAtoLegal() {
        if (selecionado.getAtosLegais() != null && !selecionado.getAtosLegais().isEmpty()) {
            return selecionado.getAtosLegais().get(selecionado.getAtosLegais().size() - 1).getAtoLegal();
        }
        return null;
    }

    public AposentadoriaFacade getFacade() {
        return aposentadoriaFacade;
    }

    public Date retornaDataMaisUmDia(Date dataAtual) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.DAY_OF_YEAR, +1);
        return c.getTime();
    }

    private Aposentadoria getAposentadoria() {
        return selecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return aposentadoriaFacade;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<ItemFichaFinanceiraFP> getItensFichaFinanceira() {
        return itensFichaFinanceira;
    }

    public void setItensFichaFinanceira(List<ItemFichaFinanceiraFP> itensFichaFinanceira) {
        this.itensFichaFinanceira = itensFichaFinanceira;
    }

    public List<SelectItem> getMatriculaFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (MatriculaFP object : matriculaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatricula()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterGenerico(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }

    public List<SelectItem> getTipoAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAposentadoria object : tipoAposentadoriaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<SelectItem> getRegraAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        if (getAposentadoria().getTipoAposentadoria() != null) {
            for (RegraAposentadoria object : RegraAposentadoria.values()) {
                // Quando o tipo de aposentadoria for igual a TipoAposentadoria.INVALIDEZ
                // incluir apenas as regras RegraAposentadoria.INVALIDEZ e RegraAposentadoria.INVALIDEZ_ART40_2012
                if (object.equals(RegraAposentadoria.INVALIDEZ) || object.equals(RegraAposentadoria.INVALIDEZ_ART40_2012) || object.equals(RegraAposentadoria.INVALIDEZ_ART23_2009)) {
                    if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.INVALIDEZ)) {
                        toReturn.add(new SelectItem(object, object.getDescricao()));
                    }
                } else if (object.equals(RegraAposentadoria.COMPULSORIA)) {
                    if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.COMPULSORIA)) {
                        toReturn.add(new SelectItem(object, object.getDescricao()));
                    }
                } else if (object.equals(RegraAposentadoria.VOLUNTARIA_POR_IDADE)) {
                    if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.IDADE)) {
                        toReturn.add(new SelectItem(object, object.getDescricao()));
                    }
                } else if (!getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.INVALIDEZ)
                    && !getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.COMPULSORIA)
                    && !getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.IDADE)
                    && getAposentadoria().getContratoFP() != null) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, 2004);
                    c.set(Calendar.MONTH, 1);
                    c.set(Calendar.DAY_OF_MONTH, 1);

                    // Para os demais tipos de aposentadoria com data de início de vigência do contratoFP após 01/01/2004
                    // incluir apenas as regras abaixo
                    // ou se 01/01/2004 for maior que a data de inicio de averbação
                    AverbacaoTempoServico averbacaoTempoServico = averbacaoTempoServicoFacade.averbacaoAposentado(((Aposentadoria) selecionado).getContratoFP());
                    if (averbacaoTempoServico == null || averbacaoTempoServico.getId() == null) {
                        averbacaoTempoServico = new AverbacaoTempoServico();
                        averbacaoTempoServico.setInicioVigencia(new Date());
                    }
                    if (averbacaoTempoServico.getInicioVigencia() != null && averbacaoTempoServico.getInicioVigencia().after(c.getTime()) && getAposentadoria().getContratoFP().getInicioVigencia().after(c.getTime())) {
                        if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.TEMPO_DE_CONTRIBUICAO) && ((object.equals(RegraAposentadoria.VOLUNTARIA_INTEGRAL_COMNUM_ART_2005)
                            //                                || object.equals(RegraAposentadoria.COMPULSORIA)
                            || object.equals(RegraAposentadoria.VOLUNTARIA_INTEGRAL)
                            //                                || object.equals(RegraAposentadoria.VOLUNTARIA_POR_IDADE)
                            || object.equals(RegraAposentadoria.VOLUNTARIA_ESPECIAL_MAGISTERIO)
                            || object.equals(RegraAposentadoria.VOLUNTARIA_PROPORCIONAL)))) {
                            toReturn.add(new SelectItem(object, object.getDescricao()));
                        }
                        if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.COMPULSORIA)
                            && (object.equals(RegraAposentadoria.COMPULSORIA))) {
                            toReturn.add(new SelectItem(object, object.getDescricao()));
                        }
                        if (getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.IDADE)
                            && (object.equals(RegraAposentadoria.VOLUNTARIA_POR_IDADE))) {
                            toReturn.add(new SelectItem(object, object.getDescricao()));
                            continue;
                        }
                    } else {
                        toReturn.add(new SelectItem(object, object.getDescricao()));
                    }

                }
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAposentadoria() {
        if (converterTipoAposentadoria == null) {
            converterTipoAposentadoria = new ConverterGenerico(TipoAposentadoria.class, tipoAposentadoriaFacade);
        }
        return converterTipoAposentadoria;
    }

    public ConverterGenerico getConverterTipoBeneficioPrevidenciario() {
        if (converterTipoBeneficioPrevidenciario == null) {
            converterTipoBeneficioPrevidenciario = new ConverterGenerico(TipoBeneficioPrevidenciario.class, tipoBeneficioPrevidenciarioFacade);
        }
        return converterTipoBeneficioPrevidenciario;
    }

    public ConverterGenerico getConverterTipoBeneficioEsocial() {
        if (converterTipoBeneficioEsocial == null) {
            converterTipoBeneficioEsocial = new ConverterGenerico(TipoBeneficioEsocial.class, tipoBeneficioEsocialFacade);
        }
        return converterTipoBeneficioEsocial;
    }

    public List<ContratoFP> completaContratoFP(String s) {
        //buscaContratosMatriculaFPTipoPrevidencia
        return contratoFPFacade.recuperaContratoMatriculaApo(s.trim());
    }

    public ConverterAutoComplete getConverterHO() {
        if (converterHO == null) {
            converterHO = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterHO;
    }

    public List<UnidadeOrganizacional> completaHO(String s) {
        return hierarquiaOrganizacionalFacadeOLD.listaUnidadeVigentePorTipoEntidade(s.trim(), TipoEntidade.FUNDO_PUBLICO, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public ItemAposentadoria getItemAposentadoria() {
        return itemAposentadoria;
    }

    public void setItemAposentadoria(ItemAposentadoria itemAposentadoria) {
        this.itemAposentadoria = itemAposentadoria;
    }

    public void addValorBeneficio() {
        if (itemAposentadoria.getInicioVigencia() != null && itemAposentadoria.getValor() != null) {
            for (ItemAposentadoria i : getAposentadoria().getItemAposentadorias()) {
                if (itemAposentadoria.getEventoFP() != null && itemAposentadoria.getEventoFP().equals(i.getEventoFP())) {
                    if (i.getFinalVigencia() == null) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:finalVigenciaValor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Itens com a mesma verba nao podem estar na mesma vigência."));
                        return;
                    }
                    if (itemAposentadoria.getInicioVigencia().before(i.getFinalVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:finalVigenciaValor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Itens com a mesma verba nao podem estar na mesma vigência."));
                        return;
                    }
                }
            }
            itemAposentadoria.setAposentadoria(((Aposentadoria) selecionado));

            if (validaPerido(itemAposentadoria.getInicioVigencia(), itemAposentadoria.getFinalVigencia())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:finalVigenciaValor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Início de Vigencia maior que o final de Vigência."));
                return;
            }
            if ((getAposentadoria().getTipoReajusteAposentadoria() == TipoReajusteAposentadoria.MEDIA) && (itemAposentadoria.getValor().compareTo(BigDecimal.ZERO) <= 0)) {
                FacesContext.getCurrentInstance().addMessage("Formulario:valor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Para o Tipo de Reajuste Média o valor tem que ser maior que zero."));
                return;
            }
            itemAposentadoria.setDataRegistro(new Date());
            ((Aposentadoria) selecionado).getItemAposentadorias().add(itemAposentadoria);
            itemAposentadoria = new ItemAposentadoria();
            ordenarItemAposentadoria(selecionado.getItemAposentadorias());
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:valor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Há campos obrigatórios sem preenchimento"));
        }

    }

    public void sugerirTipoReajusteAposentadoria() {
        if (selecionado.getRegraAposentadoria() != null) {
            sugerirTipoReajuste();
            sugereValorBeneficio();
        }
    }

    public void sugerirTipoReajuste() {
        Aposentadoria aposentadoria = (Aposentadoria) selecionado;
        if (aposentadoria.getRegraAposentadoria() != null) {
            if (isParidade(aposentadoria)) {
                aposentadoria.setTipoReajusteAposentadoria(TipoReajusteAposentadoria.PARIDADE);
                aposentadoria.setPercentual(BigDecimal.valueOf(Aposentadoria.PERCENTUAL_MAXIMO.longValue()));
            } else {
                aposentadoria.setTipoReajusteAposentadoria(TipoReajusteAposentadoria.MEDIA);
                aposentadoria.setPercentual(BigDecimal.valueOf(Math.max(aposentadoria.getPercentual().longValue(), Aposentadoria.PERCENTUAL_MINIMO.longValue())));
            }
        } else {
            aposentadoria.setTipoReajusteAposentadoria(null);
        }
    }

    private boolean isParidade(Aposentadoria aposentadoria) {
        return (aposentadoria.getRegraAposentadoria() == RegraAposentadoria.VOLUNTARIA_INTEGRAL_COMNUM_ART_2005)
            || (aposentadoria.getRegraAposentadoria() == RegraAposentadoria.VOLUNTARIA_INTEGRAL_COMNUM_TRANSICAO_ART6_2003)
            || (aposentadoria.getRegraAposentadoria() == RegraAposentadoria.VOLUNTARIA_INTEGRAL_ESPECIAL_MAGISTERIO_ART6_2003)
            || (aposentadoria.getRegraAposentadoria() == RegraAposentadoria.INVALIDEZ_ART40_2012)
            || (aposentadoria.getRegraAposentadoria() == RegraAposentadoria.VOLUNTARIA_INTEGRAL);
    }

    public void sugereValorBeneficio() {
        if (getAposentadoria().getContratoFP() != null) {
            if (getAposentadoria().getTipoReajusteAposentadoria() == TipoReajusteAposentadoria.PARIDADE && getAposentadoria().getContratoFP() != null) {
                FichaFinanceiraFP fichaRecuperada = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(getAposentadoria().getContratoFP(), TipoFolhaDePagamento.NORMAL);
                if (fichaRecuperada == null) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:valor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não existe Ficha Financeria para o Servidor selecionado"));
                    return;
                }
                if (fichaRecuperada != null) {
                    fichaRecuperada = fichaFinanceiraFPFacade.recuperar(fichaRecuperada.getId());
                    this.selecionado.setFichaFinanceiraFP(fichaRecuperada);
                    calculaItemFichaFinanceiraTipoEventoFPVantagem();
                    calculaItemAposentadoriaParaTipoReajusteParidade();
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:valor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não existe vencimento base para o Servidor selecionado"));
                    return;
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:regraAposentadoria", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione um Servidor."));
            return;
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Servidor para ser aposentado!");
        }
        if (situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.APOSENTADO) == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Situação Funcional para Aposentadoria ainda não foi cadastrada !");
        }
        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.APOSENTADORIA) == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Provimento para Aposentados ainda não foi cadastrado !");
        }
        if (selecionado.getRecursosDoVinculoFP().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Lotação Orçamentária.");
        }
        if (selecionado.getAtosLegais().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Ato Legal.");
        }
        if (selecionado.getLotacaoFuncionals().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Lotação Administrativa.");
        }
        if (getAposentadoria().getTipoReajusteAposentadoria() == TipoReajusteAposentadoria.MEDIA && getAposentadoria().getItemAposentadorias().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione um Valor Benefício para o Aposentado.");
        }
        if (getAposentadoria().getTipoAposentadoria() != null && getAposentadoria().getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.INVALIDEZ) && getAposentadoria().getInvalidezAposentados().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Para aposentadoria do tipo Invalidez deve ser incluída a invalidez do aposentado.");
        }
        if (getAposentadoria().getTipoReajusteAposentadoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Reajuste da Aposentadoria deve ser informado.");
        } else {
            if (getAposentadoria().getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Percentual deve ser informado.");
            } else {
                if (getAposentadoria().getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.MEDIA)) {
                    if (getAposentadoria().getPercentual().longValue() < Aposentadoria.PERCENTUAL_MINIMO.longValue()) {
                        ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Para Reajuste da Aposentadoria do tipo Média o percentual deve ser no mínimo de " + Aposentadoria.PERCENTUAL_MINIMO.toString() + "%.");
                    }
                } else if (getAposentadoria().getPercentual().longValue() < Aposentadoria.PERCENTUAL_MAXIMO.longValue()) {
                    ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Para Reajuste da Aposentadoria do tipo Paridade o percentual deve ser de " + Aposentadoria.PERCENTUAL_MAXIMO.toString() + "%.");
                }
            }
        }
        ve.lancarException();
    }

    public void validarTipoAposentadoria() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.TEMPO_DE_CONTRIBUICAO) || selecionado.getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.IDADE)) {

            Integer anosContribuicao = recupararTempoPorTipoAposentadoria(selecionado.getTipoAposentadoria());
            ConfiguracaoAposentadoria configuracaoAposentadoria = configuracaoAposentadoriaFacade.buscarConfiguracaoPorRegraAposentadoria(selecionado.getRegraAposentadoria());
            if (configuracaoAposentadoria == null) {
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não foi encontrado configuração de aposentadoria para " + selecionado.getRegraAposentadoria());
                String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/configuracao/aposentadoria/listar/' target='_blank'>Configurações de Aposentadoria</a></b>";
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Verifique as informações em " + url);
            } else if (configuracaoAposentadoria.getTempoMinimoAposentadorias() != null) {
                for (TempoMinimoAposentadoria tempoMinimoAposentadoria : configuracaoAposentadoria.getTempoMinimoAposentadorias()) {
                    if (tempoMinimoAposentadoria.getSexo().equals(selecionado.getContratoFP().getMatriculaFP().getPessoa().getSexo())) {
                        if (anosContribuicao < tempoMinimoAposentadoria.getQuantidadeMinima()) {
                            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, getMensagemPersonalizadaPorSexo(tempoMinimoAposentadoria, selecionado.getTipoAposentadoria()));
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    private Integer recupararTempoPorTipoAposentadoria(TipoAposentadoria tipoAposentadoria) {
        if (tipoAposentadoria.getCodigo().equals(TipoAposentadoria.TEMPO_DE_CONTRIBUICAO)) {
            String tempoContribuicao = tempoContruicao(selecionado.getContratoFP());
            StringTokenizer st = new StringTokenizer(tempoContribuicao);
            if (st.hasMoreTokens()) {
                Integer anosContribuicao = Integer.parseInt(st.nextToken(" ").trim());
                return anosContribuicao;
            }
        }
        if (tipoAposentadoria.getCodigo().equals(TipoAposentadoria.IDADE)) {
            Integer idade = Pessoa.recuperaIdade(selecionado.getContratoFP().getMatriculaFP().getPessoa().getDataNascimento());
            return idade;
        }
        return 0;
    }

    private String getMensagemPersonalizadaPorSexo(TempoMinimoAposentadoria tempoMinimoAposentadoria, TipoAposentadoria tipo) {
        if (tempoMinimoAposentadoria.getSexo().equals(Sexo.MASCULINO)) {
            return "Este servidor deve ter no mínimo " + tempoMinimoAposentadoria.getQuantidadeMinima() + " anos de " + (tipo.getCodigo().equals(TipoAposentadoria.TEMPO_DE_CONTRIBUICAO) ? "tempo de contribuição" : "idade") + " para ser aposentado.";
        } else {
            return "Esta servidora deve ter no mínimo " + tempoMinimoAposentadoria.getQuantidadeMinima() + " anos de " + (tipo.getCodigo().equals(TipoAposentadoria.TEMPO_DE_CONTRIBUICAO) ? "tempo de contribuição" : "idade") + " para ser aposentada.";
        }
    }

    private boolean validaPerido(Date inicioVigencia, Date fimVigencia) {
        if (fimVigencia == null) {
            return false;
        } else {
            if (inicioVigencia.after(fimVigencia)) {
                return true;
            }
        }
        return false;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoDePessoal(parte.trim());
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public List<ContratoFP> getListaContratosAposentado() {
        Aposentadoria aposentadoria = ((Aposentadoria) selecionado);

        if (aposentadoria.getContratoFP() != null) {
            MatriculaFP matricula = aposentadoria.getContratoFP().getMatriculaFP();
            return contratoFPFacade.recuperaContratosPorMatriculaFP(matricula);
        } else {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getTipoReajusteAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoReajusteAposentadoria object : TipoReajusteAposentadoria.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoCalculoAposentadorias() {
        return Util.getListSelectItem(Arrays.asList(TipoCalculoAposentadoria.values()));
    }

    public InvalidezAposentado getInvalidezAposentado() {
        return invalidezAposentado;
    }

    public void setInvalidezAposentado(InvalidezAposentado invalidezAposentado) {
        this.invalidezAposentado = invalidezAposentado;
    }

    public void adicionarInvalidezAposentado() {
        try {

            if (!validaInvalidezAposentado()) {
                return;
            }
            validarVigenciaInvalidezAposentado(invalidezAposentado);

            Aposentadoria aposentadoria = (Aposentadoria) selecionado;
            invalidezAposentado.setAposentadoria(aposentadoria);
            for (CID i : cids) {
                InvalidezCid inv = new InvalidezCid();
                inv.setInvalidezAposentado(invalidezAposentado);
                inv.setCid(i);
                invalidezAposentado.getInvalidezCids().add(inv);
            }

            for (InvalidezMedico medico : listInvalidezMedico) {
                medico.setInvalidezAposentado(invalidezAposentado);
                medico.setId(medico.getId());
                invalidezAposentado.getInvalidezMedicos().add(medico);
            }

            aposentadoria.getInvalidezAposentados().add(invalidezAposentado);

            invalidezAposentado = new InvalidezAposentado();
            cids = new LinkedList<>();
            listInvalidezMedico = new LinkedList<>();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addWarn("Atenção!", e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void removeInvalidezAposentado(ActionEvent e) {
        InvalidezAposentado ia = (InvalidezAposentado) e.getComponent().getAttributes().get("objeto");
        Aposentadoria aposentadoria = (Aposentadoria) selecionado;
        aposentadoria.getInvalidezAposentados().remove(ia);
    }

    public boolean validaInvalidezAposentado() {
        Boolean valida = true;
        if (invalidezAposentado.getInicioVigencia() == null) {
            FacesUtil.addWarn("Atenção!", "Obrigatório informar a data inicial de vigência da invalidez!");
            valida = false;
        }

        if (invalidezAposentado.getFinalVigencia() != null && invalidezAposentado.getInicioVigencia().after(invalidezAposentado.getFinalVigencia())) {
            FacesUtil.addWarn("Atenção!", "A data final de vigência da invalidez deve ser maior que a data inicial!");
            valida = false;
        }
        if (cids == null) {
            FacesUtil.addWarn("Atenção!", "Obrigatório informar o CID!");
            valida = false;
        }
        if (cids != null && cids.isEmpty()) {
            FacesUtil.addWarn("Atenção!", "Obrigatório informar o CID!");
            valida = false;
        }


//        if (invalidezAposentado.getNumeroLaudo().equals("")) {
//            FacesUtil.addWarn("Atenção!", "Obrigatório informar o número do laudo médico!");
//            valida = false;
//        }

        if (listInvalidezMedico == null) {
            FacesUtil.addWarn("Atenção!", "Obrigatório informar o médico!");
            valida = false;
        }

        if (listInvalidezMedico != null && listInvalidezMedico.isEmpty()) {
            FacesUtil.addWarn("Atenção!", "Obrigatório informar o médico!");
            valida = false;
        }

        return valida;
    }


    private void validarVigenciaInvalidezAposentado(InvalidezAposentado invalidezAp) {
        ValidacaoException ve = new ValidacaoException();
        Aposentadoria aposentadoria = (Aposentadoria) selecionado;

        if (aposentadoria.getInicioVigencia() != null && invalidezAp.getInicioVigencia().getTime() < aposentadoria.getInicioVigencia().getTime()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O início de vigência da invalidez não pode ser anterior ao início da vigência da aposentadoria!");
        }

        if (invalidezAp.getFinalVigencia() != null && invalidezAp.getFinalVigencia().getTime() < invalidezAp.getInicioVigencia().getTime()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final de vigência não pode ser menor que a data inicial!");
        }
        ve.lancarException();
    }

    public List<CID> completaCids(String parte) {
        return CIDFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public void navegarAtoLegal() {
        Web.navegacao(getUrlAtual(), "/atolegal/novo/", selecionado, indiceAba);
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, CIDFacade);
        }
        return converterCid;
    }

    public Converter getConverterMedico() {
        if (converterInvalidezMedico == null) {
            converterInvalidezMedico = new ConverterAutoComplete(Medico.class, aposentadoriaFacade.getMedicoFacade());
        }
        return converterInvalidezMedico;
    }

    public List<Medico> completarMedico(String parte) {
        return aposentadoriaFacade.getMedicoFacade().listaFiltrandoMedico(parte);
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public Boolean getTipoReajusteParidade() {
        if (getAposentadoria().getTipoReajusteAposentadoria() == null) {
            tipoReajusteParidade = false;
        } else {
            tipoReajusteParidade = getAposentadoria().getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.PARIDADE);
        }

        return tipoReajusteParidade;
    }

    public String dadosObito() {
        ContratoFP contrato = ((Aposentadoria) selecionado).getContratoFP();
        if (contrato == null) {
            return "Informe um contrato para visualizar as informações do óbito";
        }
        String retorno = "";
        if (contrato != null) {
            RegistroDeObito obito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(contrato.getMatriculaFP().getPessoa());
            if (obito == null) {
                retorno += "O servidor " + contrato + " não possui informações de óbito.";
                return retorno;
            }
            if (obito != null) {
                retorno += "<table>";
                retorno += "<tr>";

                retorno += "<td>";
                retorno += " Data do Óbito: ";
                retorno += "</td>";

                retorno += "<td>";
                retorno += obito.getDataFalecimento() != null ? Util.dateToString(obito.getDataFalecimento()) : " ";
                retorno += "</td>";

                retorno += "</tr>";
                retorno += "<tr>";

                retorno += "<td>";
                retorno += " Fé Pública:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getFePublica() != null && obito.getFePublica() == true ? "Sim" : "Não";
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Nome do Cartório:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += Strings.isNullOrEmpty(obito.getCartorio()) ? "" : obito.getCartorio();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Matrícula da Certidão de Óbito:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getMatriculaCertidao() == null ? "" : obito.getMatriculaCertidao();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Documento de Identificação:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getNumeroObito() == null ? "" : obito.getNumeroObito();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Livro:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getLivro() == null ? "" : obito.getLivro();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Termo:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getTermo() == null ? "" : obito.getTermo();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Folha:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getFolha() == null ? "" : obito.getFolha();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "<tr>";
                retorno += "<td>";
                retorno += "Obs.:";
                retorno += "</td>";
                retorno += "<td>";
                retorno += obito.getObservacao() == null ? "" : obito.getObservacao();
                retorno += "</td>";
                retorno += "</tr>";

                retorno += "</table>";
            }
        }
        return retorno;
    }

    public void verificarCargoConfiancaFuncaoGratificada() {
        carregarContrato();
        if (((Aposentadoria) selecionado).getContratoFP() != null && ((Aposentadoria) selecionado).getContratoFP().getId() != null) {
            if (emCargoConfianca()) {
                mensagem = ((Aposentadoria) selecionado).getContratoFP().getMatriculaFP().getPessoa().getNome() + " possui um acesso a cargo em comissão vigente, é necessário encerra-lo para prosseguir com o cadastro.";
                mostraDialog();
            } else if (emFuncaoGratificada()) {
                mensagem = ((Aposentadoria) selecionado).getContratoFP().getMatriculaFP().getPessoa().getNome() + " possui acesso a função gratificada vigente, é necessário encerra-lo para prosseguir com o cadastro.";
                mostraDialog();
            }
        }
    }

    public void mostraDialog() {
        RequestContext.getCurrentInstance().execute("dlgCargoConfiancaFuncaoGratificada.show()");
    }

    public boolean emCargoConfianca() {
        CargoConfianca cargoConfianca = cargoConfiancaFacade.recuperaCargoConfiancaVigente(((Aposentadoria) selecionado).getContratoFP(), new Date());
        if (cargoConfianca != null && cargoConfianca.getId() != null) {
            return true;
        }
        return false;
    }

    private boolean emFuncaoGratificada() {
        FuncaoGratificada funcaoGratificada = funcaoGratificadaFacade.recuperaFuncaoGratificadaVigente(((Aposentadoria) selecionado).getContratoFP(), new Date());
        if (funcaoGratificada != null && funcaoGratificada.getId() != null) {
            return true;
        }
        return false;
    }

    public String tempoEfetivoServicoPublico(ContratoFP contrato) {
        diasTempoEfetivoServico = 0;
        if (contrato != null) {
            contrato = contratoFPFacade.recuperar(contrato.getId());
            return getQuantidadeDiasTempoEfetivoServicoPublico(contrato);
        } else {
            return diasTempoEfetivoServico.toString();
        }
    }

    public String tempoContruicao(ContratoFP contrato) {
        return getQuantidadeDiasTempoContribuicao(contrato);
    }

    public String getQuantidadeDiasTempoEfetivoServicoPublico(ContratoFP contrato) {

        String anoDia = "0";

        if (contrato != null && contrato.getId() != null) {
            if (contrato.temTipoPrevidenciaRPPS()) {
                DateTime inicio = new DateTime(contrato.getInicioVigencia() != null ? contrato.getInicioVigencia() : new Date());
                DateTime fim = new DateTime(contrato.getFinalVigencia() != null ? contrato.getFinalVigencia() : new Date());
                diasTempoEfetivoServico += Days.daysBetween(inicio, fim).getDays();
                anoDia = getAnosAndDiasFormatadosPorPeriodo(inicio, fim, 0);
            }
        }
        return anoDia;
    }

    public String getQuantidadeDiasTempoContribuicao(ContratoFP contrato) {
        String anoDia = "0";
        if (contrato != null && contrato.getId() != null) {
            List<AverbacaoTempoServico> lista = averbacaoTempoServicoFacade.buscarAverbacaoAposentado(contrato);

            if (lista != null) {
                int quantidadeDiasAverbacao = 0;
                for (AverbacaoTempoServico ats : lista) {
                    DateTime inicio = new DateTime(ats.getInicioVigencia() != null ? ats.getInicioVigencia() : new Date());
                    DateTime fim = new DateTime(ats.getFinalVigencia() != null ? ats.getFinalVigencia() : new Date());
                    quantidadeDiasAverbacao += Days.daysBetween(inicio, fim).getDays();
                }

                anoDia = getAnosAndDiasFormatadosPorPeriodo(DateTime.now(), DateTime.now().plusDays(quantidadeDiasAverbacao), diasTempoEfetivoServico);
            } else {
                anoDia = getAnosAndDiasFormatadosPorPeriodo(DateTime.now(), DateTime.now(), diasTempoEfetivoServico);
            }
        }

        return anoDia;
    }

    public String getAverbacaoTempoServico() {
        if (((Aposentadoria) selecionado).getContratoFP() != null) {
            if (averbacaoTempoServicoFacade.averbacao(((Aposentadoria) selecionado).getContratoFP()) != null) {
                return "Sim";
            }
        }
        return "Não";
    }

    public void calculaItemFichaFinanceiraTipoEventoFPVantagem() {
        itensFichaFinanceira = new ArrayList<>();
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : this.selecionado.getFichaFinanceiraFP().getItemFichaFinanceiraFP()) {
            if (itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) && itemFichaFinanceiraFP.getTipoCalculoFP().equals(TipoCalculoFP.NORMAL)) {
                itensFichaFinanceira.add(itemFichaFinanceiraFP);
            }
        }
    }

    public void calculaItemAposentadoriaParaTipoReajusteParidade() {
        if (getAposentadoria().getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.PARIDADE)) {
            ItemAposentadoria item = null;
            for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : this.selecionado.getFichaFinanceiraFP().getItemFichaFinanceiraFP()) {
                EventoFP evento = (EventoFP) this.eventoFPFacade.recuperar(EventoFP.class, itemFichaFinanceiraFP.getEventoFP().getId());
                if (evento.getVerbaFixa() != null && evento.getVerbaFixa() && evento.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM) && validaItemAposentadoria(itemFichaFinanceiraFP)) {
                    item = new ItemAposentadoria();
                    item.setAposentadoria(getAposentadoria());
                    item.setDataRegistro(new Date());
                    item.setEventoFP(evento);
                    item.setValor(itemFichaFinanceiraFP.getValor());
                    item.setInicioVigencia(selecionado.getInicioVigencia());
                    getAposentadoria().getItemAposentadorias().add(item);
                }
            }
        }
    }

    private boolean validaItemAposentadoria(ItemFichaFinanceiraFP item) {
        boolean retorno = true;
        for (ItemAposentadoria itemAposentadoria1 : getAposentadoria().getItemAposentadorias()) {
            if (itemAposentadoria1.getEventoFP() != null && item.getEventoFP() != null && itemAposentadoria1.getEventoFP().equals(item.getEventoFP())) {
                retorno = false;
            }
        }
        return retorno;
    }

    public String lotacaoFuncional() {
        LotacaoFuncional lot = null;
        HierarquiaOrganizacional ho = null;
        if (this.selecionado.getContratoFP() != null && this.selecionado.getContratoFP().getId() != null) {
            lot = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(this.selecionado.getContratoFP());
        }
        if (lot != null && lot.getUnidadeOrganizacional() != null) {
            ho = hierarquiaOrganizacionalFacadeOLD.hierarquiaDaUnidadeOrg(lot.getUnidadeOrganizacional());
        }

        if (ho != null && ho.getId() != null) {
            return ho.getCodigo() + " - " + lot.toString();
        }

        return "Não foi encontrada a Lotação Funcional";
    }

    public ConverterAutoComplete getConverterProcesso() {
        if (converterProcesso == null) {
            converterProcesso = new ConverterAutoComplete(Processo.class, processoFacade);
        }
        return converterProcesso;
    }

    public List<Processo> completaProcesso(String s) {
        if (s.charAt(0) == '/') {
            s = s.substring(1, s.length());
        }
        String parametros[] = s.trim().split("\\/");
        try {
            Integer numero = Integer.parseInt(parametros[0]);
            Integer ano = null;
            if (parametros.length == 2) {
                ano = Integer.parseInt(parametros[1]);
            }
            return processoFacade.filtraProcesso(numero, ano);
        } catch (NumberFormatException nfe) {
            return new ArrayList<>();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aposentadoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ADMINISTRATIVA, UtilRH.getDataOperacao());
    }

    public void limparTipoReajusteAposentadoria() {
        selecionado.setTipoReajusteAposentadoria(null);
    }

    public void removeValorBeneficio(ActionEvent evento) {
        ItemAposentadoria item = (ItemAposentadoria) evento.getComponent().getAttributes().get("obj");
        selecionado.getItemAposentadorias().remove(item);
    }

    public List<SelectItem> getEventos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (EventoFP object : eventoFPFacade.listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }


    public ConverterGenerico getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterGenerico(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public void migrarInvalidezAposentados() {
        List<Aposentadoria> aposentadoriasInvalidas = aposentadoriaFacade.recuperarAposentadosParaInvalidez();
        for (Aposentadoria apo : aposentadoriasInvalidas) {
            apo = aposentadoriaFacade.recuperar(apo.getId());
            logger.debug("migrando invalidez da(a) " + apo);
            if (apo.getInvalidezAposentados().isEmpty()) {
                apo.setIsentoIR(true);
                InvalidezAposentado invalidez = new InvalidezAposentado();
                invalidez.setDataRegistro(new Date());
                invalidez.setAposentadoria(apo);
                invalidez.setIsentoIRRF(true);
                invalidez.setInicioVigencia(apo.getInicioVigencia());
                invalidez.setNumeroLaudo("0");
                apo.getInvalidezAposentados().add(invalidez);
            }
            aposentadoriaFacade.salvar(apo);
        }
    }

    public void limparItensAposentadoriasParaMigrar() {
        List<Aposentadoria> todosAposentados = aposentadoriaFacade.lista();
        for (Aposentadoria apo : todosAposentados) {
            apo = aposentadoriaFacade.recuperar(apo.getId());
            apo.getItemAposentadorias().clear();
            aposentadoriaFacade.salvar(apo);
        }
    }

    public void migrarEventosAposentadoria() throws SQLException {
        List<Aposentadoria> todosAposentados = aposentadoriaFacade.lista();
        logger.debug("total de aposentadorias recuperadas: " + todosAposentados.size());
        for (Aposentadoria apo : todosAposentados) {
            apo = aposentadoriaFacade.recuperar(apo.getId());
            ObjetoPesquisa objetoPesquisa = criarObjetoPesquisa(apo);
            List<RegistroDB> verbasTurmalina = comparadorDeFolhasFacade.buscaUlimaFichaFinanceiraTurmalinaApenasCredito(objetoPesquisa);
            //logger.debug("Verbas do " + apo + " : " + verbasTurmalina);
            List<ItemAposentadoria> eventosApo = apo.getItemAposentadorias();
            if (!verbasTurmalina.isEmpty()) {
                if (eventosApo.isEmpty()) {
                    gerarEventosAposentadoria(apo, verbasTurmalina);
                } else {
                    gerarEventosAposentadoriaComRegistros(apo, verbasTurmalina, eventosApo);
                }
            } else {
                logger.debug("sem ficha financeiras encontradas no turmalina para " + apo);
            }

        }
    }

    private void gerarEventosAposentadoriaComRegistros(Aposentadoria apo, List<RegistroDB> verbasTurmalina, List<ItemAposentadoria> eventosApo) {
        List<String> duplicados = new ArrayList<>();
        List<ItemAposentadoria> itemAposentadoria = new ArrayList<>();
        for (RegistroDB registroDB : verbasTurmalina) {
            String codigoEvento = registroDB.getCampoByIndex(0).getValor() + "";
            BigDecimal valor = new BigDecimal(registroDB.getCampoByIndex(1).getValor() + "");
            for (ItemAposentadoria eventoApo : eventosApo) {
                if (codigoEvento.equals(eventoApo.getEventoFP().getCodigo())) {
                    duplicados.add(eventoApo.getEventoFP().getCodigo());
                }
            }
        }

        for (RegistroDB registroDB : verbasTurmalina) {
            String codigoEvento = registroDB.getCampoByIndex(0).getValor() + "";
            BigDecimal valor = new BigDecimal(registroDB.getCampoByIndex(1).getValor() + "");
            if (!duplicados.contains(codigoEvento)) {
                itemAposentadoria.add(gerarItemEventoAposentadoria(apo, codigoEvento, valor));
            }
        }
        aposentadoriaFacade.salvar(apo);
        logger.debug("Para o aposentado com registro no banco " + apo + " serao criados os seguintes eventos: " + itemAposentadoria);


    }

    ObjetoPesquisa criarObjetoPesquisa(Aposentadoria apo) {
        ObjetoPesquisa op = new ObjetoPesquisa();
        op.setNumero(Integer.parseInt(apo.getNumero()));
        op.setMatricula(Integer.parseInt(apo.getMatriculaFP().getMatricula()));
        op.setVinculoFP(apo);
        op.setTipoFolhaDePagamentoWeb(TipoFolhaDePagamento.NORMAL);
        return op;
    }

    void gerarEventosAposentadoria(Aposentadoria apo, List<RegistroDB> verbasTurmalina) {
        List<ItemAposentadoria> itemAposentadoriaEventos = new ArrayList<>();
        for (RegistroDB registroDB : verbasTurmalina) {
            String codigoEvento = registroDB.getCampoByIndex(0).getValor() + "";
            BigDecimal valor = new BigDecimal(registroDB.getCampoByIndex(1).getValor() + "");
            itemAposentadoriaEventos.add(gerarItemEventoAposentadoria(apo, codigoEvento, valor));

        }
        aposentadoriaFacade.salvar(apo);
        logger.debug("Para o aposentado " + apo + " serão Criados os seguintes eventos: " + itemAposentadoriaEventos);

    }

    private ItemAposentadoria gerarItemEventoAposentadoria(Aposentadoria apo, String codigoEvento, BigDecimal valor) {
        EventoFP eventoFP = eventoFPFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
        ItemAposentadoria iae = new ItemAposentadoria();
        iae.setEventoFP(eventoFP);
        iae.setInicioVigencia(apo.getInicioVigencia());
        iae.setAposentadoria(apo);
        iae.setDataRegistro(apo.getInicioVigencia());
        iae.setValor(valor);
        apo.getItemAposentadorias().add(iae);
        return iae;
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void addLotacao() {
        if (validaDados()) {
            criarLotacaoFuncional();
            lotacaoFuncional = new LotacaoFuncional();
            hierarquiaOrganizacional = null;
        }
    }

    public void adicionarRecursoDoVinculoFP() {
        try {
            validarDadosRecurso();
            criarRecursoDoVinculoFP();
            recursoDoVinculoFP = new RecursoDoVinculoFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public HierarquiaOrganizacional hierarquiaPai(UnidadeOrganizacional u) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(u, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public void removeLotacaoFuncional(LotacaoFuncional lot) {
        selecionado.getLotacaoFuncionals().remove(lot);

    }

    public void removerRecursoDoVinculoFP(RecursoDoVinculoFP rec) {
        selecionado.getRecursosDoVinculoFP().remove(rec);
        if (!selecionado.getRecursosDoVinculoFP().isEmpty()) {
            abrirVigenciaUltimoRecursoDoVinculo();
        }
    }

    private void abrirVigenciaUltimoRecursoDoVinculo() {
        RecursoDoVinculoFP recursoDoVinculoFP = selecionado.getRecursoDoVinculoFPVigente();
        recursoDoVinculoFP.setFinalVigencia(null);
    }

    private void criarRecursoDoVinculoFP() {
        if (!selecionado.getRecursosDoVinculoFP().isEmpty()) {
            RecursoDoVinculoFP recursoDoVinculoFPVigente = selecionado.getRecursoDoVinculoFPVigente();
            if (recursoDoVinculoFPVigente.getFinalVigencia() == null) {
                recursoDoVinculoFPVigente.setFinalVigencia(DataUtil.getDataDiaAnterior(recursoDoVinculoFP.getInicioVigencia()));
            }
        }
        recursoDoVinculoFP.setVinculoFP(selecionado);
        recursoDoVinculoFP.setDataRegistro(UtilRH.getDataOperacao());
        selecionado.getRecursosDoVinculoFP().add(recursoDoVinculoFP);
    }

    public void sugereDatas() {
        if (selecionado.getInicioVigencia() != null) {
            itemAposentadoria.setInicioVigencia(selecionado.getInicioVigencia());
            lotacaoFuncional.setInicioVigencia(selecionado.getInicioVigencia());
            recursoDoVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
            invalidezAposentado.setInicioVigencia(selecionado.getInicioVigencia());
        }
        contemPensaoAlimenticia();
    }

    private void criarLotacaoFuncional() {
        lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        lotacaoFuncional.setVinculoFP(selecionado);
        lotacaoFuncional.setDataRegistro(UtilRH.getDataOperacao());
        selecionado.getLotacaoFuncionals().add(lotacaoFuncional);
    }

    private boolean validaDados() {
        if (hierarquiaOrganizacional == null) {
            FacesUtil.addError("Atenção", "Selecione um lotação funcional.");
            return false;
        }
        if (lotacaoFuncional.getInicioVigencia() == null) {
            FacesUtil.addError("Atenção", "Preencha o inicio de vigência.");
            return false;
        }
        if (lotacaoFuncional.getFinalVigencia() != null) {
            if (lotacaoFuncional.getInicioVigencia().after(lotacaoFuncional.getFinalVigencia()))
                FacesUtil.addError("Atenção", "Inicio de vigência inferior ao final de vigência.");
            return false;
        }
        for (LotacaoFuncional lot : selecionado.getLotacaoFuncionals()) {
            if (lot.getFinalVigencia() == null) {
                FacesUtil.addError("Atenção", "Existe registro vigente na lista.");
                return false;
            }
            if (lotacaoFuncional.getInicioVigencia().before(lot.getFinalVigencia())) {
                FacesUtil.addError("Atenção", "O inicio de vigência não pode conflitar com as vigências existentes.");
                return false;
            }
        }

        return true;
    }

    private void validarDadosRecurso() {
        ValidacaoException ve = new ValidacaoException();
        if (recursoDoVinculoFP.getRecursoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um lotação orçamentária.");
        }
        if (recursoDoVinculoFP.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Preencha o início de vigência.");
        }
        if (recursoDoVinculoFP.getInicioVigencia() != null && recursoDoVinculoFP.getFinalVigencia() != null) {
            if (recursoDoVinculoFP.getInicioVigencia().after(recursoDoVinculoFP.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Início da vigência deve ser inferior ou igual ao final de vigência.");
            }
        }
        for (RecursoDoVinculoFP rec : selecionado.getRecursosDoVinculoFP()) {
            if (rec.getFinalVigencia() != null && recursoDoVinculoFP.getInicioVigencia().before(rec.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O início de vigência não pode conflitar com as vigências existentes.");
            }
        }

        ve.lancarException();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent ac) {
        try {
            atoLegal = (AtoLegal) ac.getComponent().getAttributes().get("objeto");
            validarAtoLegal();
            AtoLegalAposentadoria atoLegalAposentadoria = new AtoLegalAposentadoria();
            atoLegalAposentadoria.setAtoLegal(atoLegal);
            atoLegalAposentadoria.setAposentadoria(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getAtosLegais(), atoLegalAposentadoria);

            atoLegal = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public String getDescricaoDeficiencia() {
        if (isPossuiDeficiencia()) {
            PessoaFisica pf = selecionado.getContratoFP().getMatriculaFP().getPessoa();
            return "Tipo: " + pf.getTipoDeficiencia().getDescricao() + " - CID: " + (pf.getPessoaFisicaCid() != null ? ((pf.getPessoaFisicaCid().getCid() != null ? pf.getPessoaFisicaCid().getCid() : "") + " - Desde: " + (pf.getPessoaFisicaCid().getInicioVigencia() != null ? Util.dateToString(pf.getPessoaFisicaCid().getInicioVigencia()) : "")) : "");
        }
        return "";
    }

    public boolean isPossuiDeficiencia() {
        if (selecionado == null)
            return false;
        if (selecionado.getContratoFP() == null)
            return false;
        if (selecionado.getContratoFP().getMatriculaFP().getPessoa().getTipoDeficiencia() == null)
            return false;
        return selecionado.getContratoFP().getMatriculaFP().getPessoa().possuiDeficienciaFisica();
    }

    public void adicionarInvalidezMedico() {
        if (isPodeAdicionarInvalidezMedico()) {
            if (listInvalidezMedico.size() <= 2) {
                setListInvalidezMedico(Util.adicionarObjetoEmLista(listInvalidezMedico, invalidezMedico));
                invalidezMedico = new InvalidezMedico();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Limite de 3 médicos alcançado.");
            }
        }
    }

    public boolean isPodeAdicionarInvalidezMedico() {
        boolean valida = true;
        if (!selecionarMedico.equals("carregar")) {
            if (invalidezMedico.getNomeMedico() == null || invalidezMedico.getNomeMedico().trim().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Informe um Nome válido.");
                valida = false;
            }
            if (invalidezMedico.getRegistroCRM() == null || invalidezMedico.getRegistroCRM().trim().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Informe um Registro CRM válido.");
                valida = false;
            }
        } else if (invalidezMedico.getMedico() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe um Médico válido.");
            valida = false;
        }
        return valida;
    }

    public void limparInvalidesMedico() {
        invalidezMedico.setMedico(null);
        invalidezMedico.setNomeMedico(null);
        invalidezMedico.setRegistroCRM(null);
    }

    public void removerInvalidezMedico() {
        for (InvalidezMedico medico : listInvalidezMedico) {
            listInvalidezMedico.remove(medico);
        }
    }


    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public void checarVigenciasDoRegistro(ItemAposentadoria itemAposentadoria) {
        if (itemAposentadoria != null && itemAposentadoria.getInicioVigencia() != null && itemAposentadoria.getFinalVigencia() != null) {
            if (itemAposentadoria.getInicioVigencia().after(itemAposentadoria.getFinalVigencia())) {
                itemAposentadoria.setFinalVigencia(null);
                FacesUtil.addOperacaoNaoPermitida("O final da vigência deve ser superior ao inicio de vigência.");

            }
        }
    }

    public void carregarContrato() {
        if (selecionado != null && selecionado.getContratoFP() != null) {
            selecionado.setContratoFP(contratoFPFacade.recuperar(selecionado.getContratoFP().getId()));
        }
    }

    public String getSelecionarMedico() {
        return selecionarMedico;
    }

    public void setSelecionarMedico(String selecionarMedico) {
        this.selecionarMedico = selecionarMedico;
    }

    @Override
    public void excluir() {
        try {
            if (!aposentadoriaFacade.temAposentadoriaCalculada(selecionado)) {
                FacesUtil.addOperacaoNaoRealizada("Não é possivel excluir Aposentado. Já existe cálculo para ele.");
            } else {
                aposentadoriaFacade.removerProvimentoAposentadoria(selecionado);
                validarDependencias();

                aposentadoriaFacade.remover(selecionado);
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDependencias() throws ValidacaoException {
        validarForeinKeysComRegistro(selecionado);
    }

    public List<SelectItem> getTipoPlanoSegregacaoMassa() {
        return Util.getListSelectItem(TipoPlanoSegregacaoMassa.values());
    }

    public List<SelectItem> getTiposAposentadoriasEspeciaisSIG() {
        return Util.getListSelectItem(TipoAposentadoriaEspecialSIG.values(), false);
    }

    public List<SelectItem> getTiposBeneficioPrevidenciario() {
        return Util.getListSelectItem(tipoBeneficioPrevidenciarioFacade.buscarTipoBeneficioPrevidanciario(), false);
    }

    public List<SelectItem> getTiposBeneficioEsocial() {
        List<TipoBeneficioEsocial> tipos = Lists.newLinkedList();
        tipos.addAll(tipoBeneficioEsocialFacade.buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial.GRUPO_1));
        tipos.addAll(tipoBeneficioEsocialFacade.buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial.GRUPO_2));
        tipos.addAll(tipoBeneficioEsocialFacade.buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial.GRUPO_3));
        tipos.addAll(tipoBeneficioEsocialFacade.buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial.GRUPO_7));
        tipos.addAll(tipoBeneficioEsocialFacade.buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial.GRUPO_8));
        return Util.getListSelectItem(tipos, false);
    }

    public List<SelectItem> getTiposBeneficiosEstudoAtuarial() {
        return Util.getListSelectItem(TipoBeneficioEstudoAtuarial.values());
    }

    private void novoAtoLegal() {
        atoLegal = new AtoLegal();
    }

    public void adicionarAtoLegal() {
        try {
            validarAtoLegal();
            AtoLegalAposentadoria atoLegalAposentadoria = new AtoLegalAposentadoria();
            atoLegalAposentadoria.setAposentadoria(selecionado);
            atoLegalAposentadoria.setAtoLegal(atoLegal);
            Util.adicionarObjetoEmLista(selecionado.getAtosLegais(), atoLegalAposentadoria);

            atoLegal = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerAtoLegal(AtoLegalAposentadoria atoLegal) {
        selecionado.getAtosLegais().remove(atoLegal);
    }

    private void validarAtoLegal() {
        ValidacaoException ve = new ValidacaoException();
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um ato legal para adicionar.");
        }
        if (selecionado.getAtosLegais() != null && !selecionado.getAtosLegais().isEmpty()) {
            for (AtoLegalAposentadoria ato : selecionado.getAtosLegais()) {
                if (ato.getAtoLegal().equals(atoLegal)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Ato legal já adicionado na lista");
                }
            }
        }
        ve.lancarException();
    }

    public void contemPensaoAlimenticia() {
        renderizarSalvar = getAposentadoria().getContratoFP() != null && getAposentadoria().getInicioVigencia() != null &&
            !pensaoAlimenticiaFacade.buscarPensoesVigentesPorInstituidor(getAposentadoria().getContratoFP(), getAposentadoria().getInicioVigencia()).isEmpty();
    }

    public Boolean getRenderizarSalvar() {
        return renderizarSalvar;
    }

    public void setRenderizarSalvar(Boolean renderizarSalvar) {
        this.renderizarSalvar = renderizarSalvar;
    }

    public ExoneracaoRescisao getExoneracaoRescisao() {
        return exoneracaoRescisao;
    }

    public void setExoneracaoRescisao(ExoneracaoRescisao exoneracaoRescisao) {
        this.exoneracaoRescisao = exoneracaoRescisao;
    }

    public void novaContaCorrente() {
        Web.poeNaSessao(getSelecionado().getMatriculaFP().getPessoa());
        Web.navegacao(getUrlAtual(), "/conta-corrente-bancaria/novo/", getSelecionado());
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getContratoFP() != null) {
            for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(selecionado.getContratoFP().getMatriculaFP().getPessoa())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public void cancelarExoneracaoRescisao() {
        exoneracaoRescisao = new ExoneracaoRescisao();
        RequestContext.getCurrentInstance().execute("dlgExoneracaoRescisao.hide()");
    }

    public void validarCamposExoneracaoRescisao() {
        ValidacaoException ve = new ValidacaoException();
        if (exoneracaoRescisao.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Servidor deve ser informado!");
        }
        if (exoneracaoRescisao.getDataRescisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data da Rescisão deve ser informado!");
        }
        if (exoneracaoRescisao.getMotivoExoneracaoRescisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo da Exoneração/Rescisão deve ser informado!");
        } else if (exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Motivo da Exoneração/Rescisão selecionado não possui um 'Motivo Exoneração/Rescisão E-Social' cadastrado!");
        }
        if (exoneracaoRescisao.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Ato Legal deve ser informado!");
        }
        if (situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.EXONERADO_RESCISO) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Situação Funcional para Exoneração/Rescisão ainda não foi cadastrada!");
        }
        if ((tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_CARREIRA) == null)
            || (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_COMISSAO) == null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os Tipos de Provimentos para Exonerações/Rescisões não foram cadastrados!");
        }
        if (exoneracaoRescisao.getVinculoFP().getInicioVigencia().after(exoneracaoRescisao.getDataRescisao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Rescisão não pode ser inferior ao início de vigência do contrato do servidor!");
        }
        if (isAvisoPrevioObrigatorio() && exoneracaoRescisao.getAvisoPrevio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para esse Servidor é obrigatório informar o Aviso Prévio!");
        }
        if (isInformarNumeroCertidaoObito() && Strings.isNullOrEmpty(exoneracaoRescisao.getNumeroCertidaoObito())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para servidor que tem motivo de desligamento por óbito é necessário informar o Número da Certidão de óbito válida!");
        }
        ve.lancarException();
    }

    private void validarAfastamentoAndConcessaoFerias() {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = exoneracaoRescisaoFacade.hasAfastamentoDataExoneracao(exoneracaoRescisao.getVinculoFP(), exoneracaoRescisao.getDataRescisao());
        List<ConcessaoFeriasLicenca> concessao = exoneracaoRescisaoFacade.hasConcessaoDataExoneracao(exoneracaoRescisao.getVinculoFP(), exoneracaoRescisao.getDataRescisao());
        if (afastamentos != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) tem afastamento à vencer na data <b>" + DataUtil.getDataFormatada(afastamentos.get(0).getTermino()) + "</b>" + " não podendo ser exonerado.");
        }
        if (concessao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) tem concessão de férias à vencer na data <b>" + DataUtil.getDataFormatada(concessao.get(0).getDataFinal()) + "</b>" + " não podendo ser exonerado.");
        }
        ve.lancarException();
    }

    public boolean isInformarNumeroCertidaoObito() {
        if (exoneracaoRescisao != null && exoneracaoRescisao.getMotivoExoneracaoRescisao() != null) {
            return TipoMotivoDesligamentoESocial.RESCISAO_OPCAO_EMPREGADO_VIRTUDE_FALECIMENTO_EMPREGADOR_INDIVIDUAL_OU_DOCUMESTICO.equals(exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial())
                || TipoMotivoDesligamentoESocial.RESCISAO_FALECIMENTO_EMPREGADO.equals(exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial());
        }
        return false;
    }

    public boolean isAvisoPrevioObrigatorio() {
        try {
            if (exoneracaoRescisao == null || exoneracaoRescisao.getVinculoFP() == null) {
                return false;
            }
            ConfiguracaoEmpregadorESocial empregador = contratoFPFacade.buscarEmpregadorPorVinculoFP((ContratoFP) exoneracaoRescisao.getVinculoFP());
            if (empregador != null) {
                return empregador.getEmiteAvisoPrevio();
            }
            return false;
        } catch (Exception e) {
            logger.debug("Não foi possível determinar o Aviso Prévio.");
            return false;
        }
    }

    public List<SelectItem> getAvisoPrevio() {
        List<SelectItem> retorno = Lists.newArrayList();
        if (exoneracaoRescisao.getVinculoFP() == null) {
            return retorno;
        }
        List<AvisoPrevio> avisosPrevio = avisoPrevioFacade.getAvisoPrevioPorContratoFP((ContratoFP) exoneracaoRescisao.getVinculoFP());
        if (avisosPrevio == null || avisosPrevio.isEmpty()) {
            return retorno;
        }
        retorno.add(new SelectItem(null, ""));
        for (AvisoPrevio avisoPrevio : avisosPrevio) {
            retorno.add(new SelectItem(avisoPrevio, "Data do Aviso Prévio: " + DataUtil.getDataFormatada(avisoPrevio.getDataAviso()) + "; Data do Desligamento: " +
                DataUtil.getDataFormatada(avisoPrevio.getDataDesligamento()) + " Servidor: " + avisoPrevio.getContratoFP()));
        }
        return retorno;
    }

    public boolean isDesabilitarFGTS() {
        if (exoneracaoRescisao != null && exoneracaoRescisao.getVinculoFP() != null) {
            try {
                if (exoneracaoRescisaoFacade.isContratoFP(exoneracaoRescisao.getVinculoFP().getId())) {
                    ContratoFP c = exoneracaoRescisao.getVinculoFP().getContratoFP();
                    if (TipoRegime.ESTATUTARIO.equals(c.getTipoRegime().getCodigo())) return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public String mensagemFichaFinanceiraAberta() {
        return (exoneracaoRescisao != null && exoneracaoRescisao.getVinculoFP() != null && exoneracaoRescisao.getDataRescisao() != null && exoneracaoRescisaoFacade.buscarFichaFinanceiraParaExclusao(exoneracaoRescisao.getVinculoFP(), exoneracaoRescisao.getDataRescisao()) != null) ? "if (!confirm('ATENÇÃO! Foi encontrada ficha financeira em aberto processada em Folha Normal na competência da exoneração. Essa ficha financeira será excluída.')) return false;" : "aguarde.show()";
    }

    public Date getFinalVigenciaContratoFP() {
        ValidacaoException ex = new ValidacaoException();
        try {
            ConfiguracaoRH configuracaoRH = exoneracaoRescisaoFacade.getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente(sistemaControlador.getDataOperacao());
            validarConfiguracaoVigenteRh(configuracaoRH, ex);
            if (configuracaoRH.getConfiguracaoRescisao().getControleVigenciaFinalViculoFP()) {
                return DataUtil.getDataDiaAnterior(exoneracaoRescisao.getDataRescisao());
            }
            return exoneracaoRescisao.getDataRescisao();
        } catch (ValidacaoException ve) {
            return exoneracaoRescisao.getDataRescisao();
        }
    }

    private void validarConfiguracaoVigenteRh(ConfiguracaoRH configuracaoRH, ValidacaoException ex) {
        if (configuracaoRH == null || configuracaoRH.getConfiguracaoRescisao() == null)
            ex.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível encontrar configuração vigênte, por favor tente novamente, se o erro persistir verifique suas configurações em " + getMontarUrlInfo());

        if (ex.temMensagens())
            throw ex;
    }

    private String getMontarUrlInfo() {
        return "<a href='" + FacesUtil.getRequestContextPath() + "/configuracao/rh/listar/' target='_blank'>Configuração Recursos Humanos</a>";
    }

    private void ordenarItemAposentadoria(List<ItemAposentadoria> itens) {
        Collections.sort(itens, new Comparator<ItemAposentadoria>() {
            @Override
            public int compare(ItemAposentadoria o1, ItemAposentadoria o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }
}
