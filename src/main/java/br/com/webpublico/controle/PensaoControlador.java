/*
 * Codigo gerado automaticamente em Tue Feb 14 09:57:29 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.RegraAposentadoria;
import br.com.webpublico.entidades.rh.PensaoAtoLegal;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@ManagedBean(name = "pensaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPensao", pattern = "/pensao/novo/", viewId = "/faces/rh/administracaodepagamento/pensao/edita.xhtml"),
    @URLMapping(id = "editarPensao", pattern = "/pensao/editar/#{pensaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pensao/edita.xhtml"),
    @URLMapping(id = "listarPensao", pattern = "/pensao/listar/", viewId = "/faces/rh/administracaodepagamento/pensao/lista.xhtml"),
    @URLMapping(id = "verPensao", pattern = "/pensao/ver/#{pensaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pensao/visualizar.xhtml"),
    @URLMapping(id = "migrarPensao", pattern = "/pensao/correcao/", viewId = "/faces/rh/administracaodepagamento/pensao/correcao.xhtml")
})
public class PensaoControlador extends PrettyControlador<Pensao> implements Serializable, CRUD {

    private final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private PensaoFacade pensaoFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private ValorReferenciaFPFacade valorReferenciaFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHO;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoaFisica;
    private HierarquiaOrganizacional ho;
    private ConverterAutoComplete converterMatricula;
    private ItemValorPensionista itemValorPensionista;
    private List<ItemValorPensionista> listaValoresPensionistas;
    @EJB
    private CIDFacade cidFacade;
    private ConverterAutoComplete converterCid;
    private boolean painelCID;
    @EJB
    private MedicoFacade medicoFacade;
    private ConverterAutoComplete converterMedico;
    @EJB
    private GrauParentescoPensionistaFacade grauParentescoPensionistaFacade;
    private ConverterAutoComplete converterGrauParentescoPensionista;
    private Boolean mostraPainelDados;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private RegistroDeObito registroDeObito;
    private Double totalDescontos = 0.0;
    private Double totalVantagens = 0.0;
    private BigDecimal percentualExcedente;
    private BigDecimal percentualRPPS;
    private BigDecimal valorTetoRPPS;
    private BigDecimal valorTetoRPPSHistorico;
    private BigDecimal parcelaExcedenteTeto;
    private BigDecimal valorBrutoPensao = new BigDecimal("0");
    private boolean bloqueiaCampos = false;
    private BigDecimal valorRestanteDaPensao = new BigDecimal("0");
    private String mensagemErroDialogValorPensionista = "";
    private BigDecimal baseRPPS = new BigDecimal("0");
    private BigDecimal valorRPPS = new BigDecimal("0");
    private BigDecimal valorRPPSDeficiente = new BigDecimal("0");
    private BigDecimal percentual = new BigDecimal("0");
    private BigDecimal rppsParcelaExcedenteTeto = new BigDecimal("0");
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ComparadorDeFollhasFacade comparadorFolhasFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    private InvalidezMedico invalidezMedico;
    private List<InvalidezMedico> listInvalidezMedico;
    private String selecionarMedico = "carregar";
    private RegistroDeObito obito;


    private LotacaoFuncional lotacaoFuncional;
    private RecursoDoVinculoFP recursoDoVinculoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Aposentadoria aposentadoria;
    private int activeIndexPensionista = 0;
    private AtoLegal atoLegal;
    private ArquivoRegistroDeObito arquivoRegistroDeObito;


    public PensaoControlador() {
        super(Pensao.class);
        mostraPainelDados = false;
        invalidezMedico = new InvalidezMedico();
        listInvalidezMedico = Lists.newArrayList();
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public List<InvalidezMedico> getListInvalidezMedico() {
        return listInvalidezMedico;
    }

    public void setListInvalidezMedico(List<InvalidezMedico> listInvalidezMedico) {
        this.listInvalidezMedico = listInvalidezMedico;
    }

    public String getSelecionarMedico() {
        return selecionarMedico;
    }

    public void setSelecionarMedico(String selecionarMedico) {
        this.selecionarMedico = selecionarMedico;
    }

    public InvalidezMedico getInvalidezMedico() {
        return invalidezMedico;
    }

    public void setInvalidezMedico(InvalidezMedico invalidezMedico) {
        this.invalidezMedico = invalidezMedico;
    }

    public int getActiveIndexPensionista() {
        return activeIndexPensionista;
    }

    public void setActiveIndexPensionista(int activeIndexPensionista) {
        this.activeIndexPensionista = activeIndexPensionista;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, cidFacade);
        }
        return converterCid;
    }

    public Double getTotalDescontos() {
        return totalDescontos;
    }

    public Double getTotalVantagens() {
        return totalVantagens;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public AbstractFacade getFacede() {
        return pensaoFacade;
    }

    public ItemValorPensionista getItemValorPensionista() {
        return itemValorPensionista;
    }

    public void setItemValorPensionista(ItemValorPensionista itemValorPensionista) {
        this.itemValorPensionista = itemValorPensionista;
    }

    public HierarquiaOrganizacional getHo() {
        return ho;
    }

    public void setHo(HierarquiaOrganizacional ho) {
        this.ho = ho;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public List<ItemValorPensionista> getListaValoresPensionistas() {
        return listaValoresPensionistas;
    }

    public void setListaValoresPensionistas(List<ItemValorPensionista> listaValoresPensionistas) {
        this.listaValoresPensionistas = listaValoresPensionistas;
    }

    public boolean isPainelCID() {
        return painelCID;
    }

    public void setPainelCID(boolean painelCID) {
        this.painelCID = painelCID;
    }

    public Converter getConverterMatricula() {
        if (converterMatricula == null) {
            converterMatricula = new ConverterAutoComplete(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatricula;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<MatriculaFP> completaMatricula(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoaPensionista(parte.trim());
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        return contratoFPFacade.buscarContratosComRegistroDeObitoNaoInstituidor(parte.trim());
    }

    public List<PessoaFisica> completaResponsavel(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public boolean isBloqueiaCampos() {
        return bloqueiaCampos;
    }

    public BigDecimal getValorRestanteDaPensao() {
        return valorRestanteDaPensao;
    }

    public String getMensagemErroDialogValorPensionista() {
        return mensagemErroDialogValorPensionista;
    }

    public BigDecimal getRppsParcelaExcedenteTeto() {
        return rppsParcelaExcedenteTeto;
    }

    public void setRppsParcelaExcedenteTeto(BigDecimal rppsParcelaExcedenteTeto) {
        this.rppsParcelaExcedenteTeto = rppsParcelaExcedenteTeto;
    }

    public Integer recuperaQuantidadeDePensionistas(List<Pensionista> lista) {
        Integer total = 0;
        List<PessoaFisica> listaPessoa = new ArrayList<>();
        for (Pensionista obj : lista) {
            if (!listaPessoa.contains(obj.getMatriculaFP().getPessoa())) {
                if (obj.getFinalVigencia() != null
                    && DataUtil.isVigente(obj.getInicioVigencia(), obj.getFinalVigencia())) {
                    listaPessoa.add(obj.getMatriculaFP().getPessoa());
                    total++;
                }
                if (obj.getFinalVigencia() == null) {
                    listaPessoa.add(obj.getMatriculaFP().getPessoa());
                    total++;
                }
            }
        }
        return total;
    }

    public void validarPensaoAndPensionistas() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.validaCampos(selecionado)) throw ve;
        validarProvimentoTipoPensionistas(ve);
        validarPensionistaAdicionado(ve);
        lancarValidacaoException(ve);
        validarRemuneracaoInstituidor(ve);
        validarReferenciaParaPercentualExcedenteCodigoSete(ve);
        validarQuantidadeCotasAndQuantidadePencionistas(ve);
        validarPensionistasAdicionados(ve);
        validarAtoLegal(ve);
        validarLotacoes(ve);
    }

    private void validarLotacoes(ValidacaoException ve) {
        if (lotacaoFuncional != null && lotacaoFuncional.getId() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de salvar, confirme ou cancele as alterações na lotação administrativa");
        }
        if (recursoDoVinculoFP != null && recursoDoVinculoFP.getId() != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de salvar, confirme ou cancele as alterações na lotação orçamentária");
        }
        ve.lancarException();
    }

    private void validarAtoLegal(ValidacaoException ve) {
        if (selecionado.getPensaoAtoLegal() == null || selecionado.getPensaoAtoLegal().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um Ato Legal.");
        }
        ve.lancarException();
    }

    public void validarPensionistasAdicionados(ValidacaoException ve) {
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            validarCamposObrigatoriosPensionista(ve, pensionista);
            lancarValidacaoException(ve);
            validarFinalVigenciaPensionista(ve, pensionista);
            validarDatasInicioAndFinalVigenciaPensionista(ve, pensionista);
            validarPensionistaDuplicado(ve, pensionista);
            validarPensionistaDiferenteDoInstituidor(ve, pensionista);
            validarInvalidez(ve, pensionista);
            lancarValidacaoException(ve);
        }
    }

    public void validarFinalVigenciaPensionista(ValidacaoException ve, Pensionista pensionista) {
        if (pensionista.isTipoPensaoTemporaria() && pensionista.getFinalVigencia() == null) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Para pensão Temporária, a data Final de Vigência deve ser inserida automatimente pelo sistema. Informe novament. <b>Informe novamente a matrícula!</b>");
        }
    }

    public void validarInvalidez(ValidacaoException ve, Pensionista pensionista) {
        if ((pensionista.isTipoPensaoTemporariaInvalidez() || pensionista.isTipoPensaoVitaliciaInvalidez())) {
            if (!pensionista.temInvalidezAdicionada()) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Ao selecionar o Tipo de Pensão = Temporária - Vitalícia de Invalidez. Deve-se informar ao menos uma invalidez!");
            }
            if (pensionista.temInvalidezAdicionada()) {
                Integer contaVigentes = 0;
                for (InvalidezPensao invalidez : pensionista.getListaInvalidez()) {
                    if (Util.validaVigencia(invalidez.getInicioVigencia(), invalidez.getFinalVigencia())) {
                        contaVigentes++;
                    }
                }
                if (contaVigentes < 1) {
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Ao selecionar o Tipo de Pensão = Temporária - Vitalícia de Invalidez. Deve-se informar ao menos uma invalidez vigente!");
                }
            }
        }
    }

    public void validarRemuneracaoInstituidor(ValidacaoException ve) {
        if (selecionado.getRemuneracaoInstituidor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor da Remuneração do Instituidor deve ser maior que zero(0)!");
        }
    }

    public void validarReferenciaParaPercentualExcedenteCodigoSete(ValidacaoException ve) {
        if (percentualExcedente.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "É necessário cadastrar uma Referência para o Percentual Excedente - (Código 7) em Referência FP!");
        }
    }

    public void validarPensionistaDiferenteDoInstituidor(ValidacaoException ve, Pensionista pensionista) {
        if (pensionista != null && pensionista.getMatriculaFP() != null) {
            if (pensionista.getMatriculaFP().getPessoa().equals(selecionado.getContratoFP().getMatriculaFP().getPessoa())) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O Pensionista deve ser diferente do Instituidor!");
            }
        }
    }

    public void validarPensionistaDuplicado(ValidacaoException ve, Pensionista pensionista) {
        if (verificaMatriculaVigenteNaLista(pensionista)) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Já existe um pensionista vigente com a matrícula: " + pensionista.getMatriculaFP());
        }
    }

    public void validarDatasInicioAndFinalVigenciaPensionista(ValidacaoException ve, Pensionista pensionista) {
        if (pensionista.getInicioVigencia() != null && pensionista.getFinalVigencia() != null) {
            if (pensionista.getInicioVigencia().after(pensionista.getFinalVigencia())) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data Início de Vigência não pode ser posterior que a data Final de Vigência!");
            }
        }
        if (registroDeObito != null && registroDeObito.getDataFalecimento().after(pensionista.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data Início de Vigência do pensionista deve ser posterior a data de falecimento do servidor!");
        }
    }

    public void validarCamposObrigatoriosPensionista(ValidacaoException ve, Pensionista pensionista) {
        if (pensionista.getTipoPensao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo da Pensão deve ser informado!");
        }
        if (pensionista.getTipoFundamentacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo da Fundamentação deve ser informado!");
        }
        if (pensionista.getMatriculaFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Matrícula do Pensionista deve ser informado!");
        }
        if (pensionista.getGrauParentescoPensionista() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grau de Parentesco do pensionista deve ser informado!");
        }
        if (pensionista.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado!");
        }
        if (!pensionista.temLotacaoFuncional()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos uma lotação administrativa!");
        }
        if (!pensionista.temRecurso()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos uma lotação orçamentária!");
        }

        if (!temValores()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos um Valor!");
        }
    }

    public boolean temValores() {
        boolean valida = true;
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            if (pensionista.getItensValorPensionista().isEmpty()) {
                valida = false;
            }
        }
        return valida;
    }

    public void validarQuantidadeCotasAndQuantidadePencionistas(ValidacaoException ve) {
        Integer totalPensionistas = contaPensionistasVigentes();
        if (buscarFimVigenciaPensionista()) {
            if (selecionado.getNumeroCotas() != null) {
                if (getSelecionado().getNumeroCotas() < 0 && getSelecionado().getNumeroCotas() > totalPensionistas) {
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, " A quantidade de Cotas deve ser igual ou inferior a quantidade de Pensionistas!");
                }
            }
        }
    }

    public void validarPensionistaAdicionado(ValidacaoException ve) {
        if (selecionado.getListaDePensionistas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório adicionar pelo menos um pensionista!");
        }
    }

    public void validarProvimentoTipoPensionistas(ValidacaoException ve) {
        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PENSIONISTAS) == null) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O Tipo de Provimento para Pensionistas ainda não foi cadastrado!");
        }
    }

    private void lancarValidacaoException(ValidacaoException ve) {
        if (ve.temMensagens()) throw ve;
    }

    public void removerItemPensao(Pensionista pensionista) {
        try {
            if (pensionista.getId() == null) {
                selecionado.getListaDePensionistas().remove(pensionista);
            } else {
                if ((selecionado.getListaDePensionistas() != null) && selecionado.getListaDePensionistas().contains(pensionista) && !pensaoFacade.buscarFichaPensionista(pensionista)) {
                    selecionado.getListaDePensionistas().remove(pensionista);
                    bloquearCampos();
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Impossível remover pensionista, o mesmo possui lançamentos na ficha financeira.");
                    FacesUtil.addOperacaoNaoPermitida("Caso queira remover esse pensionista da folha de pagamento, informe o final de vigência para o mesmo.");
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void removerItemValorPensionista(Pensionista pensionista, ItemValorPensionista item) {
        if (pensionista.getItensValorPensionista().contains(item)) {
            pensionista.getItensValorPensionista().remove(item);
        }
        recuperaValorRestante();
    }

    public void removerInvalidez(Pensionista pensionista, InvalidezPensao invalidezPensao) {
        if (pensionista.getListaInvalidez().contains(invalidezPensao)) {
            pensionista.getListaInvalidez().remove(invalidezPensao);
        }
    }

    public List<SelectItem> getListaTiposPensao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPensao obj : TipoPensao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTiposFundamentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFundamentacao obj : TipoFundamentacao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoReajusteAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoReajusteAposentadoria obj : TipoReajusteAposentadoria.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public void definirTipoReajusteAposentadoria(Pensionista pensionista) {
        if (TipoFundamentacao.ART_40_CF_I.equals(pensionista.getTipoFundamentacao()) || TipoFundamentacao.ART_40_CF_II.equals(pensionista.getTipoFundamentacao())) {
            pensionista.setTipoReajusteAposentadoria(TipoReajusteAposentadoria.MEDIA);
        } else {
            pensionista.setTipoReajusteAposentadoria(TipoReajusteAposentadoria.PARIDADE);
        }
    }

    public Boolean itemVigente(Date inicial, Date fim) {
        return Util.validaVigencia(inicial, fim);
    }

    public void adicionarInvalidez(Pensionista pensionista) {
        try {
            InvalidezPensao invalidezPensao = pensionista.getInvalidezPensao();
            validarInvalidez(pensionista);
            invalidezPensao.setPensionista(pensionista);
            adicionarCids(pensionista, invalidezPensao);
            Util.adicionarObjetoEmLista(pensionista.getListaInvalidez(), invalidezPensao);
            invalidezPensao.getInvalidezMedicos().clear();
            adicionarInvalidezMedico(invalidezPensao);
            pensionista.setInvalidezPensao(null);
            pensionista.setCids(new ArrayList<CID>());
            listInvalidezMedico.clear();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void adicionarInvalidezMedico(InvalidezPensao invalidezPensao) {
        for (InvalidezMedico medico : listInvalidezMedico) {
            medico.setInvalidezPensao(invalidezPensao);
            medico.setId(medico.getId());
            invalidezPensao.getInvalidezMedicos().add(medico);
        }
    }

    private void adicionarCids(Pensionista pensionista, InvalidezPensao invalidezPensao) {
        for (CID cid : pensionista.getCids()) {
            if (invalidezPensao.getCids().isEmpty()) {
                adicionarCid(invalidezPensao, cid);
            } else {
                boolean hasCidAdicionado = false;
                for (InvalidezPensaoCid invalidezPensaoCid : invalidezPensao.getCids()) {
                    if (invalidezPensaoCid.getCid().equals(cid)) {
                        hasCidAdicionado = true;
                        break;
                    }
                }
                if (!hasCidAdicionado) {
                    adicionarCid(invalidezPensao, cid);
                }
            }
        }
    }

    private void adicionarCid(InvalidezPensao invalidezPensao, CID cid) {
        InvalidezPensaoCid invalidezPensaoCid = new InvalidezPensaoCid();
        invalidezPensaoCid.setInvalidezPensao(invalidezPensao);
        invalidezPensaoCid.setCid(cid);
        Util.adicionarObjetoEmLista(invalidezPensao.getCids(), invalidezPensaoCid);
    }

    private void validarInvalidez(Pensionista pensionista) {
        InvalidezPensao invalidezPensao = pensionista.getInvalidezPensao();
        ValidacaoException ve = new ValidacaoException();
        if (invalidezPensao.getInicioVigencia() != null && invalidezPensao.getFinalVigencia() != null) {
            if (invalidezPensao.getInicioVigencia().after(invalidezPensao.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O início de Vigência da Invalidez não pode ser maior que o Final da Vigência da Invalidez");
            }
        }

        if (getSelecionado().getContratoFP() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nao é possivel inserir uma invalidez sem o instituidor");
        }
        if (invalidezPensao.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Início de Vigência é Obrigatório");
        }
        if (pensionista.getCids() == null || pensionista.getCids().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo CID é Obrigatório");
        }
        if (listInvalidezMedico.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um Médico.");
        }
        ve.lancarException();
    }

    private Boolean verificaMatriculaVigenteNaLista(Pensionista item) {
        if (item != null && item.getMatriculaFP() != null) {
            for (Pensionista obj : selecionado.getListaDePensionistas()) {
                boolean vigente = false;
                if (!obj.equals(item) && item.getId() == null) {
                    if (item.getMatriculaFP().equals(obj.getMatriculaFP())) {
                        if (obj.getFinalVigencia() != null) {
                            vigente = obj.getInicioVigencia().compareTo(item.getInicioVigencia()) >= 0
                                || obj.getFinalVigencia().compareTo(item.getInicioVigencia()) >= 0;
                        } else {
                            vigente = obj.getInicioVigencia().compareTo(item.getInicioVigencia()) <= 0;
                        }
                    }
                }
                if (vigente) {
                    return true;
                }
            }
        }
        return false;
    }

    public void atribuiValoresUsandoMatricula(Pensionista pensionista) {
        MatriculaFP matriculaFP = pensionista.getMatriculaFP();
        if (operacao.equals(Operacoes.NOVO) || !pensionistaFacade.pensionistaPertencePencao(selecionado.getId(), pensionista.getMatriculaFP().getId())) {
            novoContrato(pensionista, matriculaFP);
        }
        recuperaTipoPensionista(matriculaFP);
        setaDataFinalVigencia(pensionista);
    }

    public void novoContrato(Pensionista pensionista, MatriculaFP matriculaFP) {
        pensionista.setNumero(vinculoFPFacade.retornaCodigo(matriculaFP));
    }

    public void recuperaTipoPensionista(MatriculaFP m) {

        if (m != null) {
            String retorno = "";
            if (contratoFPFacade.recuperaTodosContratosPorMatriculaFP(m).isEmpty()) {
                retorno = TipoPensionista.PENSIONISTA.getDescricao();
            }

            if (!contratoFPFacade.recuperaTodosContratosPorMatriculaFP(m).isEmpty()) {
                retorno = TipoPensionista.PENSIONISTA_SERVIDOR.getDescricao();
            }

            List<ContratoFP> toReturn = new ArrayList<>();
            for (ContratoFP cont : contratoFPFacade.recuperaTodosContratosPorMatriculaFP(m)) {
                if (aposentadoriaFacade.isAposentado(cont)) {
                    toReturn.add(cont);
                }
            }

            if (toReturn.size() < contratoFPFacade.recuperaTodosContratosPorMatriculaFP(m).size() && toReturn.size() > 0) {
                retorno = TipoPensionista.PENSIONISTA_APOSENTADO_SERVIDOR.getDescricao();
            }

            if (toReturn.size() == contratoFPFacade.recuperaTodosContratosPorMatriculaFP(m).size() && toReturn.size() > 0) {
                retorno = TipoPensionista.PENSIONISTA_APOSENTADO.getDescricao();
            }

            selecionado.setTipoPensionista(retorno);

        }
    }

    public List<CID> completaCids(String parte) {
        return cidFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pensao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaPensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        operacao = Operacoes.NOVO;
        ho = new HierarquiaOrganizacional();
        ho.setExercicio(sistemaControlador.getExercicioCorrente());
        itemValorPensionista = new ItemValorPensionista();
        recursoDoVinculoFP = new RecursoDoVinculoFP();
        listaValoresPensionistas = new ArrayList<>();
        getSelecionado().setTipoPensionista("");
        painelCID = false;
        getSelecionado().setModoRateio(ModoRateio.AUTOMATICO);
        bloqueiaCampos = false;
        valorRPPS = new BigDecimal("0");
        valorRPPSDeficiente = new BigDecimal("0");
        getPercentualRPPS();
        tetoRPPS();

        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PENSIONISTAS) == null) {
            FacesUtil.addAtencao("O Tipo de Provimento para Pensionistas ainda não foi cadastrado !");
        }
        GrauParentescoPensionista grau = (GrauParentescoPensionista) Web.pegaDaSessao(GrauParentescoPensionista.class);
    }

    @URLAction(mappingId = "editarPensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setSelecionado(this.selecionado);
        selecionado.setActiveIndex(1);
        listaValoresPensionistas = new ArrayList<>();
        lotacaoFuncional = new LotacaoFuncional();
        recursoDoVinculoFP = new RecursoDoVinculoFP();
        getPercentualRPPS();
        tetoRPPS();
        getPercentualExcedente();
        getParcelaExcedenteTeto();
        bloquearCampos();
        verificaAposentadoria(selecionado.getContratoFP());
        verificaObitoInstituidor(selecionado.getContratoFP());
        setarTipoReajusteAposentadoria();
    }

    @URLAction(mappingId = "verPensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        setarTipoReajusteAposentadoria();
    }

    private void setarTipoReajusteAposentadoria() {
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            definirTipoReajusteAposentadoria(pensionista);
        }
    }

    @Override
    public void salvar() {
        try {
            validarPensaoAndPensionistas();
            super.salvar();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception e) {
            logger.error("erro", e);
            FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao Salvar a Pensão.");
        }
    }

    public Integer contaPensionistasVigentes() {
        Integer total = 0;

        for (Pensionista item : selecionado.getListaDePensionistas()) {
            if (item.getInicioVigencia() != null && Util.validaVigencia(item.getInicioVigencia(), item.getFinalVigencia())) {
                total += 1;
            }
        }

        return total;
    }

    public String recuperaValorVigentePensionista(Pensionista p) {
        String retorno = "Adicionar Valores";
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        ItemValorPensionista item = getValorVigentePensionista(p);
        if (item != null && item.getValor() != null) {
            retorno = "" + df.format(item.getValor());
        }
        return retorno;
    }

    public String recuperaValorRPPSPorCota(Pensionista p) {
        String retorno = "";
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        if (p != null) {
            if (getPensao().getNumeroCotas() == null || p.getTipoPensao() == null) {
                return retorno;
            }

            if ((p.getTipoPensao().equals(TipoPensao.TEMPORARIA_INVALIDEZ) || p.getTipoPensao().equals(TipoPensao.VITALICIA_INVALIDEZ))) {
                retorno = df.format(valorRPPSDeficiente.divide(new BigDecimal(getPensao().getNumeroCotas()), BigDecimal.ROUND_HALF_EVEN));
            } else {
                retorno = df.format(valorRPPS.divide(new BigDecimal(getPensao().getNumeroCotas()), BigDecimal.ROUND_HALF_EVEN));
            }
        }
        return retorno;
    }

    public ItemValorPensionista getValorVigentePensionista(Pensionista p) {
        List<ItemValorPensionista> lista = new ArrayList<>();
        lista.addAll(p.getItensValorPensionista());
        if (!lista.isEmpty()) {
            for (ItemValorPensionista valorPens : lista) {
                if (Util.validaVigencia(valorPens.getInicioVigencia(), valorPens.getFinalVigencia())) {
                    return valorPens;
                }
            }
        }
        return null;
    }

    public Boolean getMostraPainelDados() {
        return mostraPainelDados;
    }

    public void setMostraPainelDados(Boolean mostraPainelDados) {
        this.mostraPainelDados = mostraPainelDados;
    }

    public void carregarInstituidor(ActionEvent ev) {
        if (selecionado.getContratoFP() == null) {
            return;
        }
        if (!isInstituidorPermitido(selecionado.getContratoFP())) {
            selecionado.setContratoFP(null);
            FacesUtil.executaJavaScript("limparCamposDoAutoComplete('" + ev.getComponent().getClientId() + "')");
            return;
        }

        ContratoFP c = selecionado.getContratoFP();
        registroDeObito = null;
        arquivoRegistroDeObito = null;
        c = contratoFPFacade.recuperar(c.getId());
        verificaObitoInstituidor(c);
        selecionado.setContratoFP(c);
        verificaAposentadoria(c);
        FacesUtil.atualizarComponente(":Formulario");
    }

    private boolean isInstituidorPermitido(ContratoFP contratoFP) {
        boolean permitido = true;
        if (contratoFPFacade.isContratoComRegistroDeObito(contratoFP.getId())) {
            FacesUtil.addOperacaoNaoPermitida("O servidor " + contratoFP + " não tem registro de óbito cadastrado no sistema!");
            permitido = false;
        }
        if (contratoFPFacade.isContratoInstituidor(contratoFP.getId())) {
            FacesUtil.addOperacaoNaoPermitida("O servidor " + contratoFP + " já é um instituidor de pensão!");
            permitido = false;
        }
        return permitido;
    }

    public void verificaAposentadoria(ContratoFP c) {
        if (aposentadoriaFacade.isAposentado(c)) {
            Aposentadoria ap = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(c);
            aposentadoria = ap;
            if (ap.getRegraAposentadoria().getDecisaoJudicial()) {
                mostraPainelDados = true;
            }
        }
    }

    public void verificaObitoInstituidor(ContratoFP c) {
        if (c != null) {
            if (registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(c.getMatriculaFP().getPessoa())) {
                registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(c.getMatriculaFP().getPessoa());
                if (registroDeObito.getArquivoRegistroDeObitos() != null && !registroDeObito.getArquivoRegistroDeObitos().isEmpty()) {
                    arquivoRegistroDeObito = registroDeObito.getArquivoRegistroDeObitos().get(0);
                }
            }
        }
    }

    public void associaItemValorPesionista(Pensionista p) {
        listaValoresPensionistas = new ArrayList<>();
        listaValoresPensionistas = p.getItensValorPensionista();
        itemValorPensionista = new ItemValorPensionista();
        itemValorPensionista.setPensionista(p);
        itemValorPensionista.setInicioVigencia(p.getInicioVigencia());
        if (getSelecionado().getModoRateio() == null) {
            getSelecionado().setModoRateio(ModoRateio.MANUAL);
        }
        if (getSelecionado().getModoRateio().equals(ModoRateio.AUTOMATICO)) {
            itemValorPensionista.setValor(valorPorPensionista());
        }
        recuperaValorRestante();
        mensagemErroDialogValorPensionista = "";
        percentual = new BigDecimal("0");
    }

    public void adicionarItemValorPensionista(Pensionista pensionista) {
        try {
            ItemValorPensionista itemValorPensionistaSelecionado = pensionista.getItemValorPensionista();

            validarItemValorPensao(itemValorPensionistaSelecionado, pensionista);

            itemValorPensionistaSelecionado.setPensionista(pensionista);
            itemValorPensionistaSelecionado.setDataRegistro(sistemaControlador.getDataOperacao());
            pensionista.setItensValorPensionista(Util.adicionarObjetoEmLista(pensionista.getItensValorPensionista(), itemValorPensionistaSelecionado));
            pensionista.setItemValorPensionista(null);
            percentual = new BigDecimal("0");

            recuperaValorRestante();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao confirmar valor! " + ex.getMessage());
        }
    }

    public void validarDadosPensao(ValidacaoException ve) {
        if (!Util.validaCampos(selecionado)) throw ve;
        validarRemuneracaoInstituidor(ve);
    }

    public void validarItemValorPensao(ItemValorPensionista itemValorPensionistaSelecionado, Pensionista
        pensionista) {
        ValidacaoException ve = new ValidacaoException();
        validarDadosPensao(ve);
        lancarValidacaoException(ve);

        validarInicioVigenciaItemValorPensionista(itemValorPensionistaSelecionado, ve);
        validarVerbaItemValorPensionista(itemValorPensionistaSelecionado, ve);
        validarValorDoItemValorPensionista(itemValorPensionistaSelecionado, ve);
        lancarValidacaoException(ve);
        validarFinalVigenciaItemValorPensionista(itemValorPensionistaSelecionado, ve);
        validarVigenciaItemValorPensionista(itemValorPensionistaSelecionado, pensionista, ve);
        valdiarValorItemComValorRestantePensao(itemValorPensionistaSelecionado);
        lancarValidacaoException(ve);
    }

    public void valdiarValorItemComValorRestantePensao(ItemValorPensionista itemValorPensionistaSelecionado) {
        if (selecionado.getModoRateio() != null && selecionado.isModoRateioManual()) {
            if (itemValorPensionistaSelecionado.getValor().compareTo(valorRestanteDaPensao) < 0) {
                mensagemErroDialogValorPensionista = "O valor não pode ser maior que o valor restante da pensão.";
            }
            if (temPensionistaSemValorVigente() && valorRestanteDaPensao.compareTo(itemValorPensionistaSelecionado.getValor()) == 0) {
                mensagemErroDialogValorPensionista = "Você não pode atribuir o valor total restante pois existem outros pensionistas sem valor vigente.";
            }
        }
    }

    public void validarVigenciaItemValorPensionista(ItemValorPensionista
                                                        itemValorPensionistaSelecionado, Pensionista pensionista, ValidacaoException ve) {
        List<ItemValorPensionista> itensMesmoEventoFPDoItemSelecionado = new ArrayList<>();
        for (ItemValorPensionista itemValor : pensionista.getItensValorPensionista()) {
            if (itemValor.getEventoFP().equals(itemValorPensionistaSelecionado.getEventoFP())) {
                itensMesmoEventoFPDoItemSelecionado.add(itemValor);
            }
        }
        if (!DataUtil.isVigenciaValida(itemValorPensionistaSelecionado, itensMesmoEventoFPDoItemSelecionado)) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Foi encontratdo um valor vigente para a verba " + itemValorPensionistaSelecionado.getEventoFP());
        }
    }

    public void validarFinalVigenciaItemValorPensionista(ItemValorPensionista
                                                             itemValorPensionistaSelecionado, ValidacaoException ve) {
        if (itemValorPensionistaSelecionado.getFinalVigencia() != null && itemValorPensionistaSelecionado.getInicioVigencia() != null) {
            if (itemValorPensionistaSelecionado.getInicioVigencia().after(itemValorPensionistaSelecionado.getFinalVigencia())) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data Início de Vigência deve ser anterior a data Final de Vigência!");
            }
        }
    }

    public void validarVerbaItemValorPensionista(ItemValorPensionista
                                                     itemValorPensionistaSelecionado, ValidacaoException ve) {
        if (itemValorPensionistaSelecionado.getEventoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Verba deve ser informado!");
        }
    }

    public void validarInicioVigenciaItemValorPensionista(ItemValorPensionista
                                                              itemValorPensionistaSelecionado, ValidacaoException ve) {
        if (itemValorPensionistaSelecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado!");
        }
    }

    public void validarValorDoItemValorPensionista(ItemValorPensionista
                                                       itemValorPensionistaSelecionado, ValidacaoException ve) {
        if (itemValorPensionistaSelecionado.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser informado!");
            return;
        }
        if (itemValorPensionistaSelecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O campo Valor de ser maior que zero(0)!");
        }
    }

    public boolean temPensionistaSemValorVigente() {
        for (Pensionista p : getSelecionado().getListaDePensionistas()) {
            if (itemValorPensionista.getPensionista() != p && getValorVigentePensionista(p) == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isServidorRPPSComObito() {
        Pensao pensao = (Pensao) getSelecionado();
        return (contratoFPFacade.isContratoPrevidenciaRPPS(pensao.getContratoFP())
            && registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(pensao.getContratoFP().getMatriculaFP().getPessoa()));
    }

    public boolean isAposentado() {
        Pensao pensao = (Pensao) getSelecionado();
        return aposentadoriaFacade.isAposentado(pensao.getContratoFP()) && !isServidorRPPSComObito();
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        Pensao pensao = (Pensao) getSelecionado();
        return fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(pensao.getContratoFP());
    }

    public Date getDataDeObito() {
        Pensao pensao = selecionado;
        if (pensao.getContratoFP() != null && obito == null) {
            obito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pensao.getContratoFP().getMatriculaFP().getPessoa());
        }
        return obito != null ? obito.getDataFalecimento() : null;
    }

    public RegraAposentadoria getRegraAposentadoria() {
        Pensao pensao = (Pensao) getSelecionado();
        Aposentadoria aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(pensao.getContratoFP());
        if (aposentadoria != null) {
            return aposentadoria.getRegraAposentadoria();
        } else {
            return null;
        }
    }

    public List<ItemFichaFinanceiraFP> getListaItemFichaFinanceiraFP() {
        Pensao pensao = (Pensao) getSelecionado();
        FichaFinanceiraFP ficha;

        Aposentadoria aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(pensao.getContratoFP());
        if (aposentadoria != null) {
            ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(aposentadoria);

            if (ficha == null) {
                ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(pensao.getContratoFP());
            }
        } else {
            ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(pensao.getContratoFP());
        }

        if (ficha != null) {
            ficha = fichaFinanceiraFPFacade.recuperar(ficha.getId());
            atualizaValores(ficha.getItemFichaFinanceiraFP());
            return ficha.getItemFichaFinanceiraFP();
        } else {
            return new ArrayList<>();
        }
    }

    public ConverterAutoComplete getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(Medico.class, medicoFacade);
        }
        return converterMedico;
    }

    public List<Medico> completaMedico(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte.trim());
    }

    public List<GrauParentescoPensionista> completaGrauDeParentesco(String parte) {
        Pensionista p = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(),
            "#{pensionista}", Pensionista.class);

        if (p.getTipoPensao() == null) {
            FacesUtil.addAtencao("Selecione um Tipo de Pensão");
            return null;
        }
        return grauParentescoPensionistaFacade.filtrarGrauDeParentesco(parte.trim(), p.getTipoPensao());
    }

    public Pensao getPensao() {
        return getSelecionado();
    }

    public String recuperaPercentualCota(Pensionista p) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        if (p != null && selecionado != null && getPensao().getModoRateio() != null && selecionado.getNumeroCotas() != null) {
            if (getPensao().getModoRateio().equals(ModoRateio.AUTOMATICO)) {
                Double d = getPensao().getNumeroCotas().doubleValue();
                Double retorno = new Double(100 / d);
                return nf.format(retorno) + "%";
            } else {
                String valorVigente = recuperaValorVigentePensionista(p);
                try {
                    valorVigente = valorVigente.replaceAll("\\.", "").replaceAll(",", ".");
                    Double returnHand = (new Double(valorVigente) * 100) / valorBrutoPensao.doubleValue();
                    return nf.format(returnHand);
                } catch (NumberFormatException number) {
                    //System.out.println("Erro ao tentar converter valor '" + valorVigente + "'");
                    return "";
                }
            }
        }
        return "";
    }

    public ConverterAutoComplete getConverterGrauParentescoPensionista() {
        if (converterGrauParentescoPensionista == null) {
            converterGrauParentescoPensionista = new ConverterAutoComplete(GrauParentescoPensionista.class, grauParentescoPensionistaFacade);
        }
        return converterGrauParentescoPensionista;
    }

    public RegistroDeObito getRegistroDeObito() {
        return registroDeObito;
    }

    public void setRegistroDeObito(RegistroDeObito registroDeObito) {
        this.registroDeObito = registroDeObito;
    }

    public boolean isArquivoNull() {
        if (this.registroDeObito != null) {
            return (this.arquivoRegistroDeObito != null);
        } else {
            return false;
        }
    }

    private void atualizaValores(List<ItemFichaFinanceiraFP> itemFichaFinanceiraFP) {
        totalDescontos = 0.0;
        totalVantagens = 0.0;
        for (ItemFichaFinanceiraFP itens : itemFichaFinanceiraFP) {
            if (itens.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                totalDescontos += itens.getValor().doubleValue();
            }
            if (itens.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                totalVantagens += itens.getValor().doubleValue();
            }
        }
    }

    public Double getTotal() {
        return totalVantagens - totalDescontos;
    }

    public String cortaString(GrauParentescoPensionista grau) {
        if (grau != null) {
            String retorno = grau.toString();
            if (retorno.length() < 20) {
                return retorno;
            }
            return retorno.substring(0, 20) + "...";
        }
        return "";
    }

    public void setaDataFinalVigencia(Pensionista pensionista) {
        //seta o final de vigência de acordo com data limite do grau de parentesco
        if (pensionista != null) {
            if (pensionista.getMatriculaFP() != null && pensionista.getMatriculaFP().getId() != null
                && pensionista.getGrauParentescoPensionista() != null && pensionista.getGrauParentescoPensionista().getIdadeLimite() != null) {
                if (pensionista.getMatriculaFP().getPessoa().getDataNascimento() != null) {
                    Calendar dataNascimento = Calendar.getInstance();
                    dataNascimento.setTime(pensionista.getMatriculaFP().getPessoa().getDataNascimento());

                    int ano = dataNascimento.get(Calendar.YEAR) + pensionista.getGrauParentescoPensionista().getIdadeLimite();

                    dataNascimento.set(ano, dataNascimento.get(Calendar.MONTH), dataNascimento.get(Calendar.DAY_OF_MONTH));
                    if (dataNascimento.getTime().after(new Date())) {
                        pensionista.setFinalVigencia(dataNascimento.getTime());
                    } else {
                        FacesUtil.addAtencao("O(A) Pensionista " + pensionista.getMatriculaFP().getPessoa().getNome()
                            + " já completou " + pensionista.getGrauParentescoPensionista().getIdadeLimite() + " anos.");
                        limpaPensionista();
                    }
                } else {
                    FacesUtil.addAtencao("O(A) Pensionista " + pensionista.getMatriculaFP().getPessoa().getNome() + " não possui data de nascimento cadastrada.");
                    limpaPensionista();
                }
            }
        }
    }

    public void limpaPensionista() {
        getSelecionado().setTipoPensionista("");
    }

    public void criarAndAdicionarPensionista() {
        try {
            Pensionista p = new Pensionista();
            p.setPensao(selecionado);
            selecionado.setListaDePensionistas(Util.adicionarObjetoEmLista(selecionado.getListaDePensionistas(), p));
            selecionado.setActiveIndex(1);
            if (registroDeObito != null) {
                p.setInicioVigencia(registroDeObito.getDataFalecimento());
            }
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao criar novo Pensionista! " + ex.getMessage());
            logger.debug("Erro ao criar novo Pensionista! " + ex.getMessage());
        }
    }


    public boolean buscarFimVigenciaPensionista() {
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            if (pensionista.getFinalVigencia() == null) {
                return false;
            }
        }
        return true;
    }

    public ReferenciaFP recuperaReferenciaFP(TipoReferenciaFP tipo, Integer codigo) {
        return referenciaFPFacade.listaFiltrandoPorTipoECodigo(tipo, codigo);
    }

    public ValorReferenciaFP tetoRPPS() {
        ValorReferenciaFP v = valorReferenciaFP(TipoReferenciaFP.VALOR_VALOR, new Integer(13), new Date());
        if (v != null) {
            valorTetoRPPS = v.getValor();
            return v;
        }
        return new ValorReferenciaFP();
    }

    public ValorReferenciaFP tetoRPPSHistorico() {
        ValorReferenciaFP v = valorReferenciaFP(TipoReferenciaFP.VALOR_VALOR, new Integer(13), getDataDeObito() != null ? getDataDeObito() : UtilRH.getDataOperacao());
        if (v != null) {
            valorTetoRPPSHistorico = v.getValor();
            return v;
        }
        return new ValorReferenciaFP();

    }

    public BigDecimal getBasePrevidenciaDataObito() {
        if (valorTetoRPPSHistorico != null && valorBrutoPensao != null) {

            if (valorBrutoPensao.compareTo(valorTetoRPPSHistorico) > 0) {
                return valorBrutoPensao.subtract(valorTetoRPPSHistorico);
            }
        }
        return new BigDecimal("0");

    }

    public BigDecimal getBasePrevidenciaDeficienteDataObito() {
        if (valorTetoRPPSHistorico != null && valorBrutoPensao != null) {
            if (valorBrutoPensao.compareTo(valorTetoRPPSHistorico.multiply(new BigDecimal("2"))) > 0) {
                return valorBrutoPensao.subtract(valorTetoRPPSHistorico.multiply(new BigDecimal("2")));
            }
        }
        return new BigDecimal("0");

    }

    public BigDecimal getBasePrevidencia() {
        if (valorTetoRPPS != null && valorBrutoPensao != null) {

            if (valorBrutoPensao.compareTo(valorTetoRPPS) > 0) {
                return valorBrutoPensao.subtract(valorTetoRPPS);
            }
        }
        return new BigDecimal("0");

    }

    public BigDecimal getBasePrevidenciaDeficiente() {
        if (valorTetoRPPS != null && valorBrutoPensao != null) {
            if (valorBrutoPensao.compareTo(valorTetoRPPS.multiply(new BigDecimal("2"))) > 0) {
                return valorBrutoPensao.subtract(valorTetoRPPS.multiply(new BigDecimal("2")));
            }
        }
        return new BigDecimal("0");

    }

    public BigDecimal getPercentualExcedente() {
        ValorReferenciaFP v = valorReferenciaFP(TipoReferenciaFP.VALOR_PERCENTUAL, new Integer(7), new Date());
        if (v != null && v.getId() != null) {
            percentualExcedente = v.getValor();
        } else {
            percentualExcedente = BigDecimal.ZERO;
        }
        return percentualExcedente;
    }

    public BigDecimal getPercentualRPPS() {
        ValorReferenciaFP v = valorReferenciaFP(TipoReferenciaFP.VALOR_PERCENTUAL, new Integer(1), new Date());
        if (v != null && v.getId() != null) {
            percentualRPPS = v.getValor();
        } else {
            percentualRPPS = BigDecimal.ZERO;
        }
        return percentualRPPS;
    }

    public ValorReferenciaFP valorReferenciaFP(TipoReferenciaFP tipo, Integer codigo, Date data) {
        ReferenciaFP referenciaFP = recuperaReferenciaFP(tipo, codigo);
        if (referenciaFP != null) {
            ValorReferenciaFP valorReferenciaFP = valorReferenciaFPFacade.recuperaValorReferenciaFPVigente(referenciaFP, data);
            if (valorReferenciaFP != null) {
                return valorReferenciaFP;
            }
            FacesUtil.addAtencao("O valor da Referência " + referenciaFP.getCodigo() + " - " + referenciaFP.getDescricao() + " não foi encontrado.");
        }
        FacesUtil.addAtencao("Não foi encontrada nenhuma referência para o percentual excedente");
        return null;
    }

    public List<ModoRateio> modoRateio() {
        return Arrays.asList(ModoRateio.values());
    }

    public BigDecimal getParcelaExcedenteTeto() {
        parcelaExcedenteTeto = new BigDecimal("0");
        valorBrutoPensao = new BigDecimal("0");
        if (getSelecionado().getRemuneracaoInstituidor() != null && getSelecionado().getRemuneracaoInstituidor().compareTo(BigDecimal.ZERO) > 0
            && valorTetoRPPS != null && valorTetoRPPSHistorico != null && valorTetoRPPS.compareTo(BigDecimal.ZERO) > 0) {
            if (getSelecionado().getRemuneracaoInstituidor().compareTo(valorTetoRPPS) > 0) {

                BigDecimal calculoPensaoRPPS = getSelecionado().getRemuneracaoInstituidor().subtract(valorTetoRPPS);
                parcelaExcedenteTeto = calculoPensaoRPPS.multiply(percentualExcedente.divide(new BigDecimal("100")));

                BigDecimal calculoPensao = getSelecionado().getRemuneracaoInstituidor().subtract(valorTetoRPPSHistorico);
                BigDecimal baseBeneficio = calculoPensao.multiply(percentualExcedente.divide(new BigDecimal("100")));

                baseRPPS = parcelaExcedenteTeto;
                rppsParcelaExcedenteTeto = parcelaExcedenteTeto.multiply(new BigDecimal("11").divide(new BigDecimal("100")));
                valorRPPS = baseRPPS.multiply(percentualRPPS.divide(new BigDecimal("100")));

                valorBrutoPensao = valorTetoRPPSHistorico.add(baseBeneficio);
                if (valorBrutoPensao.compareTo(valorTetoRPPS.multiply(new BigDecimal("2"))) > 0) {
                    BigDecimal valorBrutoRppsDeficiente = new BigDecimal("0");
                    BigDecimal tetoDobrado = valorTetoRPPS.multiply(new BigDecimal("2"));
                    //valor rpps deficiente: (valorBrutoPensao - 2*valor do Teto) * 11%
                    valorBrutoRppsDeficiente = (((valorBrutoPensao).subtract(tetoDobrado))).multiply(percentualRPPS.divide(new BigDecimal("100")));
                    valorRPPSDeficiente = valorBrutoRppsDeficiente;
                } else {
                    valorRPPSDeficiente = new BigDecimal("0");
                }
            } else {
                parcelaExcedenteTeto = getSelecionado().getRemuneracaoInstituidor();
                rppsParcelaExcedenteTeto = getSelecionado().getRemuneracaoInstituidor();
                valorBrutoPensao = getSelecionado().getRemuneracaoInstituidor();
            }
        }
        return parcelaExcedenteTeto;
    }

    public BigDecimal getValorBrutoPensao() {
        return valorBrutoPensao;
    }

    public void atualizaValorParcelaExcedenteTeto() {
        getParcelaExcedenteTeto();
        valorRestanteDaPensao = selecionado.getRemuneracaoInstituidor();

    }

    public BigDecimal valorPorPensionista() {
        if (getSelecionado().getModoRateio() == null || getSelecionado().getModoRateio().equals(ModoRateio.AUTOMATICO)) {
            if (valorBrutoPensao != null && getSelecionado().getNumeroCotas() != null && getSelecionado().getNumeroCotas() > 0) {
                return valorBrutoPensao.divide(new BigDecimal(getSelecionado().getNumeroCotas()), MathContext.DECIMAL128).setScale(2, RoundingMode.DOWN);
            }
        }
        return BigDecimal.ZERO;
    }

    private void verificarPensionistaAndValoresVigentesParaAtualizar(BigDecimal valorRateio) {
        ItemValorPensionista itemParaClonar = new ItemValorPensionista();
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            if (pensionista.getItensValorPensionista() != null && !pensionista.getItensValorPensionista().isEmpty() &&
                sistemaFacade.getDataOperacao().compareTo(pensionista.getInicioVigencia()) >= 0 &&
                (pensionista.getFinalVigencia() == null ||
                    sistemaFacade.getDataOperacao().compareTo(pensionista.getFinalVigencia()) <= 0)) {
                itemParaClonar = getItemValorPensionistaVigenteOrMaiorDataVigenciaFinal(pensionista);
                atribuirValoresPensionistas(itemParaClonar, valorRateio, pensionista);
            }
        }
    }

    private ItemValorPensionista getItemValorPensionistaVigenteOrMaiorDataVigenciaFinal(Pensionista pensionista) {
        for (ItemValorPensionista valorPensionista : pensionista.getItensValorPensionista()) {
            if (valorPensionista.getFinalVigencia() == null) {
                return valorPensionista;
            }
        }
        ItemValorPensionista itemValorPensionista = pensionista.getItensValorPensionista().get(0);
        for (ItemValorPensionista valorPensionista : pensionista.getItensValorPensionista()) {
            if (valorPensionista.getFinalVigencia().compareTo(itemValorPensionista.getFimVigencia()) >= 0) {
                itemValorPensionista = valorPensionista;
            }
        }
        return itemValorPensionista;
    }

    private void atribuirValoresPensionistas(ItemValorPensionista itemParaClonar, BigDecimal
        valorRateio, Pensionista pensionista) {
        if (itemParaClonar.getId() == null || DataUtil.dataSemHorario(itemParaClonar.getInicioVigencia()).compareTo(DataUtil.dataSemHorario(sistemaFacade.getDataOperacao())) == 0) {
            itemParaClonar.setValor(valorRateio);
        } else {
            ItemValorPensionista novoItem = (ItemValorPensionista) Util.clonarEmNiveis(itemParaClonar, 1);
            novoItem.setId(null);
            novoItem.setFinalVigencia(null);
            novoItem.setInicioVigencia(sistemaFacade.getDataOperacao());
            novoItem.setValor(valorRateio);
            pensionista.getItensValorPensionista().add(novoItem);
            if (itemParaClonar.getFinalVigencia() == null || itemParaClonar.getFinalVigencia().compareTo(DataUtil.dataSemHorario(sistemaFacade.getDataOperacao())) >= 0) {
                itemParaClonar.setFinalVigencia(DataUtil.getDataDiaAnterior(novoItem.getInicioVigencia()));
            }
        }
    }

    private void bloquearCampos() {
        if (getSelecionado().getListaDePensionistas().isEmpty()) {
            bloqueiaCampos = false;
        } else {
            bloqueiaCampos = true;
        }
    }

    public void recuperaValorRestante() {
        BigDecimal valorTotal = new BigDecimal("0");
        for (Pensionista p : selecionado.getListaDePensionistas()) {
            if (getValorVigentePensionista(p) != null && getValorVigentePensionista(p).getValor() != null) {
                valorTotal = valorTotal.add(getValorVigentePensionista(p).getValor());
            }
        }
        valorRestanteDaPensao = valorBrutoPensao.subtract(valorTotal);
    }

    public BigDecimal getBaseRPPS() {
        return baseRPPS;
    }

    public void setBaseRPPS(BigDecimal baseRPPS) {
        this.baseRPPS = baseRPPS;
    }

    public BigDecimal getValorRPPS() {
        return valorRPPS;
    }

    public void setValorRPPS(BigDecimal valorRPPS) {
        this.valorRPPS = valorRPPS;
    }

    public BigDecimal getValorRPPSDeficiente() {
        return valorRPPSDeficiente;
    }

    public void setValorRPPSDeficiente(BigDecimal valorRPPSDeficiente) {
        this.valorRPPSDeficiente = valorRPPSDeficiente;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public void aplicarPercentualValor(Pensionista pensionista) {
        ItemValorPensionista itemValorPensionista = pensionista.getItemValorPensionista();
        if (itemValorPensionista != null && pensionista.getPercentual() != null && pensionista.getPercentual().compareTo(BigDecimal.ZERO) > 0) {
            itemValorPensionista.setValor(valorBrutoPensao.multiply(pensionista.getPercentual().divide(new BigDecimal("100"))));
            aplicarValorPercentual(pensionista);
        }
    }

    public void aplicarValorPercentual(Pensionista pensionista) {
        ItemValorPensionista itemValorPensionista = pensionista.getItemValorPensionista();
        if (itemValorPensionista != null) {
            try {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                pensionista.setPercentual(BigDecimal.valueOf((new Double(itemValorPensionista.getValor().doubleValue()) * 100) / valorBrutoPensao.doubleValue()));
                String convert = nf.format(pensionista.getPercentual());
                convert = convert.replaceAll(",", ".");
                pensionista.setPercentual(new BigDecimal(convert));
            } catch (NumberFormatException nb) {
                logger.debug("Não foi possível converter o valor!-ERRO: " + nb.getMessage());
            }
        }
    }

    public List<HierarquiaOrganizacional> completaHO(String s) {
        return hierarquiaOrganizacionalFacade.listaHierarquiaVigentePorTipoEntidade(s.trim(), TipoEntidade.FUNDO_PUBLICO, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public ConverterAutoComplete getConverterHO() {
        if (converterHO == null) {
            converterHO = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHO;
    }

    public String lotacaoFuncional() {
        LotacaoFuncional lot = null;
        HierarquiaOrganizacional ho = null;
        if (getSelecionado().getContratoFP() != null && this.getSelecionado().getContratoFP().getId() != null && selecionado != null && selecionado.getContratoFP() != null) {
            lot = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(selecionado.getContratoFP());
        }
        if (lot != null && lot.getUnidadeOrganizacional() != null) {
            ho = hierarquiaOrganizacionalFacadeOLD.getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(), lot.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }

        if (ho != null && ho.getId() != null) {
            return ho.getCodigo() + " - " + lot.toString();
        }

        return "Não foi encontrada a Lotação Funcional";
    }

    public void limparValoresPensionista() {
        List<Pensionista> pensionistas = pensionistaFacade.lista();
        logger.debug("total de pensionistas encontrados: " + pensionistas.size());
        for (Pensionista p : pensionistas) {
            p = pensionistaFacade.recuperar(p.getId());
            p.getItensValorPensionista().clear();
            pensionistaFacade.salvar(p);
        }
    }

    public void migrarEAssociarEventoPensionista() throws SQLException {
        List<Pensionista> pensionistas = pensionistaFacade.lista();
        logger.debug("total de pensionistas encontrados: " + pensionistas.size());
        for (Pensionista p : pensionistas) {
            p = pensionistaFacade.recuperar(p.getId());
            if (p.getItensValorPensionista().isEmpty()) {
                recuperarEAssociarEventoPensionista(p);
            } else {
                for (ItemValorPensionista itemValorPensionista : p.getItensValorPensionista()) {
                    //TODO trocar este evento pelo evento que esterá configurado na tela de configuracao do RH.
                    EventoFP eventoFP = eventoFPFacade.recuperaEvento("600", TipoExecucaoEP.FOLHA);
                    itemValorPensionista.setEventoFP(eventoFP);
                    logger.debug(p + " fixando evento " + eventoFP.getCodigo() + " valor : " + itemValorPensionista.getValor());
                    if (itemValorPensionista.getValor() == null) {
                        p.getItensValorPensionista().clear();
                        recuperarEAssociarEventoPensionista(p);
                    }
                }
            }
        }
    }

    private void recuperarEAssociarEventoPensionista(Pensionista p) throws SQLException {
        ObjetoPesquisa objetoPesquisa = new ObjetoPesquisa();
        objetoPesquisa.setMatricula(Integer.parseInt(p.getMatriculaFP().getMatricula()));
        objetoPesquisa.setNumero(Integer.parseInt(p.getNumero()));
        objetoPesquisa.setTipoFolhaDePagamentoWeb(TipoFolhaDePagamento.NORMAL);
        List<RegistroDB> eventosPensionistaTurmalina = comparadorFolhasFacade.buscaUlimaFichaFinanceiraTurmalinaApenasCredito(objetoPesquisa);
        for (RegistroDB registroDB : eventosPensionistaTurmalina) {
            String codigoEvento = registroDB.getCampoByIndex(0).getValor() + "";
            BigDecimal valor = new BigDecimal(registroDB.getCampoByIndex(1).getValor() + "");
            logger.debug(p + " adicionando evento " + codigoEvento + " e valor: " + valor);
            gerarItemValorPensionista(p, codigoEvento, valor);
        }
        pensionistaFacade.salvar(p);

    }

    private void gerarItemValorPensionista(Pensionista p, String codigoEvento, BigDecimal valor) {
        ItemValorPensionista ivp = new ItemValorPensionista();
        EventoFP eventoFP = eventoFPFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
        ivp.setEventoFP(eventoFP);
        ivp.setInicioVigencia(p.getInicioVigencia());
        ivp.setDataRegistro(p.getInicioVigencia());
        ivp.setValor(valor);
        ivp.setPensionista(p);
        p.getItensValorPensionista().add(ivp);
    }

    public void corrigirEnquadramentosPensionista() {
        pensaoFacade.correcaoMigracaoPensionistaCC();
    }

    public void gerarInvalidezPensionista() {
        criarInvalidezPensionista();
    }

    public void criarInvalidezPensionista() {
        List<Pensionista> pensionistas = pensaoFacade.recuperarPensionistasParaInvalidez();
        logger.debug("total de pensionistas encontrados: " + pensionistas.size());
        for (Pensionista p : pensionistas) {
            p = pensionistaFacade.recuperar(p.getId());
            if (p.getListaInvalidez().isEmpty()) {
                p.setIsentoIR(true);
                gerarInvalidez(p);
            }
        }
    }

    private void gerarInvalidez(Pensionista p) {
        p.setTipoPensao(TipoPensao.VITALICIA_INVALIDEZ);
        InvalidezPensao ivp = new InvalidezPensao();
        ivp.setInicioVigencia(p.getInicioVigencia());
        ivp.setPensionista(p);
        ivp.setIsentoIRRF(true);
        ivp.setFinalVigencia(p.getFinalVigencia());
        ivp.setDataRegistro(new Date());
        p.getListaInvalidez().add(ivp);
        pensionistaFacade.salvar(p);
    }

    public SituacaoFuncional getSituacaoFuncional() {
        SituacaoContratoFP st = situacaoFuncionalFacade.recuperaUltimaSituacaoFuncional(getSelecionado().getContratoFP());
        return st != null ? st.getSituacaoFuncional() : null;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaUltimoEnquadramentoFuncionalComValor(getSelecionado().getContratoFP());
        return enquadramentoFuncional != null ? enquadramentoFuncional : null;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public ExoneracaoRescisao getExoneracao() {
        ExoneracaoRescisao exoneracaoRescisao = exoneracaoFacade.recuperaExoneracaoRecisao2(getSelecionado().getContratoFP());
        return exoneracaoRescisao != null ? exoneracaoRescisao : null;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacadeOLD.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ADMINISTRATIVA, UtilRH.getDataOperacao());
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void removeLotacaoFuncional(Pensionista pensionista, LotacaoFuncional lot) {
        if (pensionista.getLotacaoFuncionals().contains(lot)) {
            pensionista.getLotacaoFuncionals().remove(lot);
        }
    }

    public void removeRecursoDoVinculoFP(Pensionista pensionista, RecursoDoVinculoFP rec) {
        if (pensionista.getRecursosDoVinculoFP().contains(rec)) {
            pensionista.getRecursosDoVinculoFP().remove(rec);
        }
    }

    public void editarRecursoDoVinculoFP(Pensionista pensionista, RecursoDoVinculoFP rec) {
        this.setRecursoDoVinculoFP(rec);
        pensionista.setRecursoDoVinculoFP(this.recursoDoVinculoFP);
        pensionista.setActiveIndex(2);
    }

    public void editarItemValorPensionista(Pensionista pensionista, ItemValorPensionista item) {
        if (pensionista.getItensValorPensionista().contains(item)) {
            pensionista.setItemValorPensionista(item);
            pensionista.setActiveIndex(0);
        }
    }

    public void editarInvalidez(Pensionista pensionista, InvalidezPensao item) {
        pensionista.setActiveIndex(3);
        if (pensionista.getListaInvalidez().contains(item)) {
            pensionista.setInvalidezPensao(item);
            for (InvalidezPensaoCid invalidezPensaoCid : item.getCids()) {
                Util.adicionarObjetoEmLista(pensionista.getCids(), invalidezPensaoCid.getCid());
            }
            if (pensionista.getInvalidezPensao() != null) {
                for (InvalidezMedico medico : pensionista.getInvalidezPensao().getInvalidezMedicos()) {
                    Util.adicionarObjetoEmLista(listInvalidezMedico, medico);
                }
            }
            pensionista.getListaInvalidez().remove(item);
        }
    }

    public HierarquiaOrganizacional hierarquiaPai(UnidadeOrganizacional u) {
        return hierarquiaOrganizacionalFacadeOLD.getHierarquiaOrganizacionalPorUnidade(u, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public void addLotacao(Pensionista pensionista) {
        try {
            validaDadosLotacao(pensionista, pensionista.getLotacaoFuncional());
            criarLotacaoFuncional(pensionista);

            cancelarLotacao(pensionista);
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void criarNovaLotacao(Pensionista pensionista) {
        pensionista.setLotacaoFuncional(new LotacaoFuncional());
        pensionista.getLotacaoFuncional().setInicioVigencia(pensionista.getInicioVigencia());
        pensionista.setActiveIndex(0);
    }

    public void cancelarLotacao(Pensionista pensionista) {
        lotacaoFuncional = null;
        pensionista.setLotacaoFuncional(null);
    }

    public void criarNovoValorPensionsita(Pensionista pensionista) {
        pensionista.setItemValorPensionista(new ItemValorPensionista());
        pensionista.getItemValorPensionista().setInicioVigencia(pensionista.getInicioVigencia());
        pensionista.setActiveIndex(2);
        configurarValorDaPensao(pensionista);
    }

    private void configurarValorDaPensao(Pensionista pensionista) {
        ItemValorPensionista itemValorPensionista = pensionista.getItemValorPensionista();
        if (itemValorPensionista != null) {
            if (ModoRateio.AUTOMATICO.equals(selecionado.getModoRateio())) {
                if (valorBrutoPensao != null && valorBrutoPensao.compareTo(BigDecimal.ZERO) != 0) {
                    itemValorPensionista.setValor(valorPorPensionista());
                    aplicarValorPercentual(pensionista);
                }
            }
        }
    }

    public void cancelarValorPensionista(Pensionista pensionista) {
        pensionista.setItemValorPensionista(null);
    }

    public void criarNovaInvalidez(Pensionista pensionista) {
        pensionista.setInvalidezPensao(new InvalidezPensao());
        pensionista.getInvalidezPensao().setInicioVigencia(pensionista.getInicioVigencia());
        pensionista.setActiveIndex(3);
    }

    public void cancelarInvalidez(Pensionista pensionista) {
        pensionista.setInvalidezPensao(null);
    }

    public void criarNovoRecursoDoVinculo(Pensionista pensionista) {
        pensionista.setRecursoDoVinculoFP(new RecursoDoVinculoFP());
        pensionista.getRecursoDoVinculoFP().setInicioVigencia(pensionista.getInicioVigencia());
        pensionista.setActiveIndex(1);
    }

    public void cancelarRecursoDoVinculo(Pensionista pensionista) {
        recursoDoVinculoFP = null;
        pensionista.setRecursoDoVinculoFP(null);
    }

    public void editarLotacaoFuncional(Pensionista pensionista, LotacaoFuncional lotacao) {
        this.setLotacaoFuncional(lotacao);
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.hierarquiaDaUnidadeOrg(lotacao.getUnidadeOrganizacional());
        pensionista.setLotacaoFuncional(this.lotacaoFuncional);
        pensionista.setActiveIndex(1);
    }

    public void addRecursoDoVinculoFP(Pensionista pensionista) {
        try {
            validaDadosRecurso(pensionista, pensionista.getRecursoDoVinculoFP());
            criarRecursoDoVinculoFP(pensionista);
            cancelarRecursoDoVinculo(pensionista);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validaDadosLotacao(Pensionista pensionista, LotacaoFuncional lotacaoFuncional) {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo lotação funcional é obrigatório.");
        }
        if (lotacaoFuncional.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo início de vigência é obrigatório.");
        }
        if (lotacaoFuncional.getFinalVigencia() != null) {
            if (lotacaoFuncional.getInicioVigencia().after(lotacaoFuncional.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Início de vigência inferior ao final de vigência.");
            }
        }
        for (LotacaoFuncional lot : pensionista.getLotacaoFuncionals()) {
            if (lot.getFinalVigencia() == null && !lotacaoFuncional.getInicioVigencia().after(lot.getInicioVigencia())
                && !pensionista.getLotacaoFuncionals().contains(lotacaoFuncional)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe registro vigente na lista.");
            }
            if (lot.getFinalVigencia() != null && lotacaoFuncional.getInicioVigencia().before(lot.getFinalVigencia())
                && !pensionista.getLotacaoFuncionals().contains(lotacaoFuncional)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O início de vigência não pode conflitar com as vigências existentes.");
            }
        }
        ve.lancarException();
    }

    private void validaDadosRecurso(Pensionista pensionista, RecursoDoVinculoFP recursoDoVinculoFP) {
        ValidacaoException ve = new ValidacaoException();
        if (recursoDoVinculoFP.getRecursoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("o campo lotação orçamentária é obrigatório.");
        }
        if (recursoDoVinculoFP.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo início de vigência é obrigatório.");
        }
        if (recursoDoVinculoFP.getFinalVigencia() != null) {
            if (recursoDoVinculoFP.getInicioVigencia().after(recursoDoVinculoFP.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Início de vigência inferior ao final de vigência.");
            }
        }
        for (RecursoDoVinculoFP rec : pensionista.getRecursosDoVinculoFP()) {
            if (rec.getFinalVigencia() == null && !recursoDoVinculoFP.getInicioVigencia().after(rec.getInicioVigencia())
                && !pensionista.getRecursosDoVinculoFP().contains(recursoDoVinculoFP)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe registro vigente na lista.");
            }
            if (rec.getFinalVigencia() != null && recursoDoVinculoFP.getInicioVigencia().before(rec.getFinalVigencia())
                && !pensionista.getRecursosDoVinculoFP().contains(recursoDoVinculoFP)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O inicio de vigência não pode conflitar com as vigências existentes.");
            }
        }
        ve.lancarException();
    }

    private void criarLotacaoFuncional(Pensionista pensionista) {
        LotacaoFuncional lot = pensionista.getLotacaoFuncional();
        lot.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        pensionista.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        lot.setVinculoFP(pensionista);
        lot.setDataRegistro(UtilRH.getDataOperacao());
        Util.adicionarObjetoEmLista(pensionista.getLotacaoFuncionals(), lot);

    }


    private void criarRecursoDoVinculoFP(Pensionista pensionista) {
        pensionista.getRecursoDoVinculoFP().setVinculoFP(pensionista);
        pensionista.getRecursoDoVinculoFP().setDataRegistro(UtilRH.getDataOperacao());
        Util.adicionarObjetoEmLista(pensionista.getRecursosDoVinculoFP(), pensionista.getRecursoDoVinculoFP());
    }

    public String getDescricaoDeficiencia(Pensionista pensionista) {
        if (isPossuiDeficiencia(pensionista)) {
            PessoaFisica pf = pensionista.getMatriculaFP().getPessoa();
            return "Tipo: " + pf.getTipoDeficiencia().getDescricao() + " - CID: " + pf.getPessoaFisicaCid().getCid() + " - Desde: " + (pf.getPessoaFisicaCid().getInicioVigencia() != null ? Util.dateToString(pf.getPessoaFisicaCid().getInicioVigencia()) : "");
        }
        return "";
    }

    public boolean isPossuiDeficiencia(Pensionista pensionista) {
        if (selecionado == null)
            return false;
        if (pensionista == null)
            return false;
        if (pensionista.getMatriculaFP() == null)
            return false;
        if (pensionista.getMatriculaFP().getPessoa().getTipoDeficiencia() == null)
            return false;
        return pensionista.getMatriculaFP().getPessoa().possuiDeficienciaFisica();
    }

    public void atualizarValoresPensionistas() {
        verificarPensionistaAndValoresVigentesParaAtualizar(valorPorPensionista());
        FacesUtil.atualizarComponente("Formulario:tab-view-geral");
    }

    public void removerPensaoAtoLegal(PensaoAtoLegal pensaoAtoLegal) {
        selecionado.getPensaoAtoLegal().remove(pensaoAtoLegal);
    }

    public void adicionarPensaoAtoLegal() {
        try {
            validarAtoLegal();
            PensaoAtoLegal pensaoAtoLegal = new PensaoAtoLegal();
            pensaoAtoLegal.setAtoLegal(atoLegal);
            pensaoAtoLegal.setPensao(selecionado);
            selecionado.getPensaoAtoLegal().add(pensaoAtoLegal);
            atoLegal = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAtoLegal() {
        ValidacaoException ve = new ValidacaoException();
        for (PensaoAtoLegal pensaoAtoLegal : selecionado.getPensaoAtoLegal()) {
            if (pensaoAtoLegal.getAtoLegal().equals(atoLegal)) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Ato legal já está adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public List<Medico> completarMedico(String parte) {
        return aposentadoriaFacade.getMedicoFacade().listaFiltrandoMedico(parte);
    }

    public void adicionarInvalidezMedico() {
        try {
            validarInvalidezMedico();
            if (listInvalidezMedico.size() <= 2) {
                setListInvalidezMedico(Util.adicionarObjetoEmLista(listInvalidezMedico, invalidezMedico));
                invalidezMedico = new InvalidezMedico();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Limite de 3 médicos alcançado.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarInvalidezMedico() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionarMedico.equals("carregar")) {
            if (Strings.isNullOrEmpty(invalidezMedico.getNomeMedico())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe um Nome válido.");
            }
            if (Strings.isNullOrEmpty(invalidezMedico.getRegistroCRM())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe um Registro CRM válido.");
            }
        } else if (invalidezMedico.getMedico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Médico válido.");
        }
        for (InvalidezMedico medico : listInvalidezMedico) {
            if (medico.getMedico() != null && medico.getMedico().equals(invalidezMedico.getMedico())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Médico já adicionado.");
            }
        }
        ve.lancarException();
    }

    public void limparInvalidesMedico() {
        invalidezMedico.setMedico(null);
        invalidezMedico.setNomeMedico(null);
        invalidezMedico.setRegistroCRM(null);
    }

    public void removerInvalidezMedico(InvalidezMedico invalidezMedico) {
        listInvalidezMedico.remove(invalidezMedico);
    }

    public Integer getNumeroCotasVigentes() {
        Integer numeroCotas = 0;
        for (Pensionista pensionista : selecionado.getListaDePensionistas()) {
            Date inicioVigenciaPensionista = pensionista.getInicioVigencia();
            Date finalVigenciaPensionista = pensionista.getFinalVigencia();
            Date dataOperacao = DataUtil.primeiroDiaMesHorarioZerado(sistemaControlador.getDataOperacao()).getTime();
            if (inicioVigenciaPensionista != null) {
                inicioVigenciaPensionista = DataUtil.primeiroDiaMesHorarioZerado(pensionista.getInicioVigencia()).getTime();
            }
            if (finalVigenciaPensionista != null) {
                finalVigenciaPensionista = DataUtil.primeiroDiaMesHorarioZerado(finalVigenciaPensionista).getTime();
            }
            if (finalVigenciaPensionista == null && pensionista.getMatriculaFP() != null) {
                numeroCotas++;
            }
            if (inicioVigenciaPensionista != null && finalVigenciaPensionista != null && (finalVigenciaPensionista.compareTo(dataOperacao)) >= 0
                && inicioVigenciaPensionista.compareTo(dataOperacao) <= 0) {
                numeroCotas++;
            }
        }
        return numeroCotas;
    }

    public ArquivoRegistroDeObito getArquivoRegistroDeObito() {
        return arquivoRegistroDeObito;
    }

    public void setArquivoRegistroDeObito(ArquivoRegistroDeObito arquivoRegistroDeObito) {
        this.arquivoRegistroDeObito = arquivoRegistroDeObito;
    }

    public List<SelectItem> getTiposDependenciasEstudoAtuarial() {
        return Util.getListSelectItem(TipoDependenciaEstudoAtuarial.values());
    }

    public void novaContaCorrente(Pensionista pensionista) {
        if (pensionista != null && pensionista.getMatriculaFP() != null) {
            Web.poeNaSessao(pensionista.getMatriculaFP().getPessoa());
            Web.navegacao(getUrlAtual(), "/conta-corrente-bancaria/novo/", pensionista);
        }
    }

    public List<SelectItem> contasCorrentesBancarias(Pensionista pensionista) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (pensionista != null && pensionista.getMatriculaFP() != null) {
            for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(pensionista.getMatriculaFP().getPessoa())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }
}
