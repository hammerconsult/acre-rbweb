/*
 * Codigo gerado automaticamente em Fri Feb 17 11:15:20 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.TipoNaturezaEstagio;
import br.com.webpublico.enums.rh.TipoNivelEstagio;
import br.com.webpublico.enums.rh.TipoVinculoSicap;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "estagiarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEstagiario", pattern = "/estagiario/novo/", viewId = "/faces/rh/administracaodepagamento/estagiario/edita.xhtml"),
    @URLMapping(id = "editarEstagiario", pattern = "/estagiario/editar/#{estagiarioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/estagiario/edita.xhtml"),
    @URLMapping(id = "listarEstagiario", pattern = "/estagiario/listar/", viewId = "/faces/rh/administracaodepagamento/estagiario/lista.xhtml"),
    @URLMapping(id = "verEstagiario", pattern = "/estagiario/ver/#{estagiarioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/estagiario/visualizar.xhtml")
})
public class EstagiarioControlador extends PrettyControlador<Estagiario> implements Serializable, CRUD {

    @EJB
    private EstagiarioFacade estagiarioFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConverterGenerico converterMatriculaFP;
    @EJB
    private CBOFacade cboFacade;
    private ConverterAutoComplete converterCbo;
    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;
    private ConverterAutoComplete converterNaturezaRendimento;
    @EJB
    private CargoFacade cargoFacade;
    private ConverterGenerico converterCargo;
    @EJB
    private JornadaDeTrabalhoFacade jornadaDeTrabalhoFacade;
    private ConverterGenerico converterJornadaDeTrabalho;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    private ConverterGenerico converterTipoRegime;
    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;
    private ConverterGenerico converterOcorrenciaSEFIP;
    @EJB
    private VinculoEmpregaticioFacade vinculoEmpregaticioFacade;
    private ConverterGenerico converterVinculoEmpregaticio;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ConverterGenerico converterHorarioEstagiario;
    private LotacaoFuncional lotacaoFuncional;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    private ConverterGenerico converterHorarioDeTrabalho;
    private MoneyConverter moneyConverter;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ConverterGenerico converterContaCorrenteBancaria;
    private ConverterAutoComplete converterPessoaFisica;
    private PessoaFisica pessoaFisica;
    private MatriculaFP matriculaSelecionado;
    private String numeroSelecionado;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private HorarioContratoFP horarioContratoFP;
    private List<HorarioContratoFP> horarioContratoFPs;
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    private HierarquiaOrganizacional hierarquiaOrganizacionalEnquadramento;
    @EJB
    private BaseCargoFacade baseCargoFacade;
    private SindicatoVinculoFP sindicatoVinculoFP;
    @EJB
    private SindicatoFacade sindicatoFacade;
    private ConverterGenerico converterSindicato;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private ConverterAutoComplete converterSituacaoFuncional;
    private ConverterGenerico converterConselho;
    @EJB
    private ConselhoClasseOrdemFacade conselhoClasseOrdemFacade;
    @EJB
    private UFFacade uFFacade;
    private ConverterGenerico converterUf;
    private AssociacaoVinculoFP associacaoVinculoFP;
    @EJB
    private AssociacaoFacade associacaoFacade;
    private ConverterGenerico converterAssociacao;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalselecionada;
    private HierarquiaOrganizacional hierarquiaOrganizacionalselecionadaLotacao;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private SituacaoContratoFP situacaoContratoFP;
    private Boolean edicaoCargo;
    private Boolean novoCargo;
    private ContratoFPCargo contratoFPCargoSelecionado;


    public EstagiarioControlador() {
        super(Estagiario.class);
    }

    public EstagiarioFacade getFacade() {
        return estagiarioFacade;
    }

    public Boolean getEdicaoCargo() {
        return edicaoCargo;
    }

    public void setEdicaoCargo(Boolean edicaoCargo) {
        this.edicaoCargo = edicaoCargo;
    }

    private Boolean isEdicaoCargo() {
        return edicaoCargo;
    }

    public Boolean getNovoCargo() {
        return novoCargo;
    }

    public void setNovoCargo(Boolean novoCargo) {
        this.novoCargo = novoCargo;
    }

    public ContratoFPCargo getContratoFPCargoSelecionado() {
        return contratoFPCargoSelecionado;
    }

    public void setContratoFPCargoSelecionado(ContratoFPCargo contratoFPCargoSelecionado) {
        this.contratoFPCargoSelecionado = contratoFPCargoSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return estagiarioFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setaDatas(DateSelectEvent date) {
        Date e = date.getDate();
        previdenciaVinculoFP.setInicioVigencia(e);
        opcaoValeTransporteFP.setInicioVigencia(e);
        horarioContratoFP.setInicioVigencia(e);
        lotacaoFuncional.setInicioVigencia(e);
        sindicatoVinculoFP.setInicioVigencia(e);
        associacaoVinculoFP.setInicioVigencia(e);
    }

    public List<SelectItem> getTipoRegime() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegime object : tipoRegimeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void marcaFGTS() {
        if (selecionado.getTipoRegime() != null && selecionado.getTipoRegime().getCodigo() == 1) {
            ((Estagiario) selecionado).setFgts(Boolean.TRUE);
        } else {
            ((Estagiario) selecionado).setFgts(Boolean.FALSE);
        }

    }

    public ConverterGenerico getConverterTipoRegime() {
        if (converterTipoRegime == null) {
            converterTipoRegime = new ConverterGenerico(TipoRegime.class, tipoRegimeFacade);
        }
        return converterTipoRegime;
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(PrevidenciaVinculoFP previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFP() {
        return opcaoValeTransporteFP;
    }

    public void setOpcaoValeTransporteFP(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = opcaoValeTransporteFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalEnquadramento() {
        return hierarquiaOrganizacionalEnquadramento;
    }

    public void setHierarquiaOrganizacionalEnquadramento(HierarquiaOrganizacional hierarquiaOrganizacionalEnquadramento) {
        this.hierarquiaOrganizacionalEnquadramento = hierarquiaOrganizacionalEnquadramento;
    }

    public SituacaoContratoFP getSituacaoContratoFP() {
        return situacaoContratoFP;
    }

    public void setSituacaoContratoFP(SituacaoContratoFP situacaoContratoFP) {
        this.situacaoContratoFP = situacaoContratoFP;
    }

    private Calendar getFinalVigenciaDoContrato(Date admitidoEm, BaseCargo bc) {
        Calendar c = Calendar.getInstance();
        c.setTime(admitidoEm);
        c.add(Calendar.MONTH, bc.getBasePeriodoAquisitivo().getDuracao());
        return c;
    }

    @Override
    public void salvar() {
        Estagiario cfp = ((Estagiario) selecionado);

        String novoCodigo = "";

        if (cfp.getNumero() != null && cfp.getMatriculaFP() != null && operacao == Operacoes.NOVO) {
            if (!contratoFPFacade.verificaSeExite(cfp.getNumero(), cfp.getMatriculaFP())) {
                novoCodigo = contratoFPFacade.retornaCodigo(cfp.getMatriculaFP());
            }
        }
        if (cfp.getNumero() != null && operacao == Operacoes.EDITAR) {
            if (!contratoFPFacade.verificaCodigoEditar(((Estagiario) selecionado)) && contratoFPFacade.verificaSeExite(cfp.getNumero(), cfp.getMatriculaFP())) {
                novoCodigo = contratoFPFacade.retornaCodigo(cfp.getMatriculaFP());
            }
        }

        if (operacao == Operacoes.NOVO) {

            List<PeriodoAquisitivoFL> listaPeriodoAquisitivoFL = new ArrayList<>();
            for (BaseCargo bc : baseCargoFacade.buscaBasesCargosVigentes()) {

                Calendar c = getFinalVigenciaDoContrato(cfp.getDataAdmissao(), bc);

                PeriodoAquisitivoFL periodo = new PeriodoAquisitivoFL(
                    bc.getBasePeriodoAquisitivo().getDiasDeDireito(),
                    bc, cfp.getInicioVigencia(), c.getTime(), cfp);

                listaPeriodoAquisitivoFL.add(periodo);
            }
            cfp.setPeriodosAquisitivosFLs(listaPeriodoAquisitivoFL);
        }

        if (validaCampos()) {
            String mensagem = "";

            if (!"".equals(novoCodigo)) {
                mensagem = "O código do contrato foi alterado de " + cfp.getNumero() + " para " + novoCodigo
                    + " pois o código " + cfp.getNumero() + " já está sendo usado em um outro contrato ";
                ((Estagiario) selecionado).setNumero(novoCodigo);
            }
            try {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }

                if (isSessao()) {
                    return;
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Ocorreu um erro: ", e.getMessage()));
            }
            redireciona();
        }
    }

    @URLAction(mappingId = "novoEstagiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Estagiario cfp = ((Estagiario) selecionado);
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        opcaoValeTransporteFP = new OpcaoValeTransporteFP();
        lotacaoFuncional = new LotacaoFuncional();
        sindicatoVinculoFP = new SindicatoVinculoFP();
        associacaoVinculoFP = new AssociacaoVinculoFP();
        horarioContratoFP = new HorarioContratoFP();
        horarioContratoFPs = new ArrayList<>();
//        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        situacaoContratoFP = new SituacaoContratoFP();
        pessoaFisica = null;
        cfp.setLotacaoFuncionals(new ArrayList<LotacaoFuncional>());
        cfp.setOpcaoValeTransporteFPs(new ArrayList<OpcaoValeTransporteFP>());
        cfp.setPrevidenciaVinculoFPs(new ArrayList<PrevidenciaVinculoFP>());
        cfp.setSindicatosVinculosFPs(new ArrayList<SindicatoVinculoFP>());
        cfp.setAssociacaosVinculosFPs(new ArrayList<AssociacaoVinculoFP>());
        cfp.setSituacaoFuncionals(new ArrayList<SituacaoContratoFP>());
        cfp.setInicioVigencia(SistemaFacade.getDataCorrente());
        previdenciaVinculoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        opcaoValeTransporteFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        horarioContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        lotacaoFuncional.setInicioVigencia(SistemaFacade.getDataCorrente());
        sindicatoVinculoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        associacaoVinculoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        cfp.setDataAdmissao(SistemaFacade.getDataCorrente());
        situacaoContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
        sindicatoVinculoFP.setVinculoFP(cfp);
        associacaoVinculoFP.setVinculoFP(cfp);
        cfp.setSefip(Boolean.FALSE);
    }

    public void numero(ValueChangeEvent e) {
        Estagiario est = ((Estagiario) selecionado);
        est.setMatriculaFP((MatriculaFP) e.getNewValue());
        est.setPessoaFisica(est.getMatriculaFP().getPessoa());

        if ((est.getId() == null) && (est.getMatriculaFP() != null)) {
            est.setNumero(contratoFPFacade.retornaCodigo(est.getMatriculaFP()));
        } else if (!operacao.equals(Operacoes.NOVO)) {
            if (matriculaSelecionado.equals(est.getMatriculaFP())) {
                est.setNumero(numeroSelecionado);
            } else {
                est.setNumero(contratoFPFacade.retornaCodigo(est.getMatriculaFP()));
            }
        }
        selecionado = est;
    }

    @URLAction(mappingId = "verEstagiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = estagiarioFacade.recuperar(selecionado.getId());
    }

    @URLAction(mappingId = "editarEstagiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        horarioContratoFPs = new ArrayList<>();
        horarioContratoFP = new HorarioContratoFP();
        opcaoValeTransporteFP = new OpcaoValeTransporteFP();
        previdenciaVinculoFP = new PrevidenciaVinculoFP();
        for (LotacaoFuncional lf : selecionado.getLotacaoFuncionals()) {
            if (!horarioContratoFPs.contains(lf.getHorarioContratoFP())) {
                horarioContratoFPs.add(lf.getHorarioContratoFP());
            }
        }
        lotacaoFuncional = new LotacaoFuncional();
        sindicatoVinculoFP = new SindicatoVinculoFP();
        situacaoContratoFP = new SituacaoContratoFP();
        associacaoVinculoFP = new AssociacaoVinculoFP();

        numeroSelecionado = selecionado.getNumero();
        matriculaSelecionado = selecionado.getMatriculaFP();
    }

    public Boolean validaCampos() {
        Boolean retorno = true;
        Estagiario cfp = ((Estagiario) selecionado);

        retorno = Util.validaCampos(selecionado);

        if (cfp.getFinalVigencia() != null) {
            if (cfp.getFinalVigencia().before(cfp.getInicioVigencia())) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Atenção!", "Fim da Vigência não pode ser menor que Início da Vigência!"));
                retorno = false;
            }
        }

        if (cfp.getLotacaoFuncionals().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "Obrigatório informar os dados referentes ao Local de Trabalho"));
            retorno = false;
        }

        if (cfp.getDataAdmissao() == null) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe a data de admissão !"));
            retorno = false;
        }

        if (horarioContratoFPs.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe ao menos um horário de Trabalho !"));
            retorno = false;
        }

        if (cfp.getJornadaDeTrabalho() == null) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe a jornada de trabalho !"));
            retorno = false;
        }

        if (cfp.getMatriculaFP() == null) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe a Matricula do Servidor"));
            retorno = false;
        }

        if (cfp.getTipoVinculoSicap() == null) {
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Informe o Tipo de Vínculo SICAP na aba Informações das Declarações"));
            retorno = false;
        }
        return retorno;
    }

    public ConverterGenerico getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterGenerico(OcorrenciaSEFIP.class, ocorrenciaSEFIPFacade);
        }
        return converterOcorrenciaSEFIP;
    }

    public List<PrevidenciaVinculoFP> getListaPrevidencia() {
        return ((Estagiario) selecionado).getPrevidenciaVinculoFPs();
    }

    public List<SelectItem> getMatriculaFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (MatriculaFP object : matriculaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatricula()));
        }
        return toReturn;
    }

    public Converter getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterGenerico(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }

    public List<SelectItem> getCargo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Cargo object : cargoFacade.recuperaCargosVigentes(TipoPCS.QUADRO_EFETIVO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterGenerico(Cargo.class, cargoFacade);
        }
        return converterCargo;
    }

    public List<SelectItem> getJornadaDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (JornadaDeTrabalho object : jornadaDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterJornadaDeTrabalho() {
        if (converterJornadaDeTrabalho == null) {
            converterJornadaDeTrabalho = new ConverterGenerico(JornadaDeTrabalho.class, jornadaDeTrabalhoFacade);
        }
        return converterJornadaDeTrabalho;
    }

    public ConverterGenerico getconverterHorarioContratoFP() {
        if (converterHorarioEstagiario == null) {
            converterHorarioEstagiario = new ConverterGenerico(HorarioContratoFP.class, horarioContratoFPFacade);
        }
        return converterHorarioEstagiario;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : horarioDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getConselhos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ConselhoClasseOrdem object : conselhoClasseOrdemFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getUfs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : uFFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, horarioDeTrabalhoFacade);
        }
        return converterHorarioDeTrabalho;
    }

    public ConverterGenerico getConverterConselho() {
        if (converterConselho == null) {
            converterConselho = new ConverterGenerico(ConselhoClasseOrdem.class, conselhoClasseOrdemFacade);
        }
        return converterConselho;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, uFFacade);
        }
        return converterUf;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void addLotacaoFuncional() {
        Estagiario c = (Estagiario) selecionado;
        lotacaoFuncional.setDataRegistro(new Date());
        if (hierarquiaOrganizacionalEnquadramento != null) {
            for (LotacaoFuncional p : ((Estagiario) selecionado).getLotacaoFuncionals()) {
                if (lotacaoFuncional.getHorarioContratoFP().equals(p.getHorarioContratoFP())) {
                    if (p.getFinalVigencia() != null && lotacaoFuncional.getInicioVigencia() != null) {
                        if (p.getFinalVigencia().after(lotacaoFuncional.getInicioVigencia())) {
                            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!"));
                            return;
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a vigência da lista contem registro(s) com vigência aberta!"));
                        return;
                    }
                }
            }

            lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacionalEnquadramento.getSubordinada());
            lotacaoFuncional.setVinculoFP(c);


            ((Estagiario) selecionado).getLotacaoFuncionals().add(lotacaoFuncional);
            lotacaoFuncional = new LotacaoFuncional();
            lotacaoFuncional.setInicioVigencia(((Estagiario) selecionado).getInicioVigencia());
            hierarquiaOrganizacionalEnquadramento = new HierarquiaOrganizacional();
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:unidadeOrg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione uma unidade para filtrar!"));
        }
    }

    public void associa(HorarioContratoFP e) {
        lotacaoFuncional.setHorarioContratoFP(e);
        hierarquiaOrganizacionalEnquadramento = new HierarquiaOrganizacional();
    }

    public void addHorarioContrato() {
        if (horarioContratoFP.getInicioVigencia() != null && horarioContratoFP.getHorarioDeTrabalho() != null) {
            Estagiario c = (Estagiario) selecionado;
            for (HorarioContratoFP p : horarioContratoFPs) {
                if (p.getFinalVigencia() != null) {
                    if (p.getFinalVigencia().after(horarioContratoFP.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data final da vigência anterior não pode ser maior que a data inicial da nova vigência!"));
                        return;
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a vigência da lista contem registro(s) com vigência aberta!"));
                    return;
                }
            }
            horarioContratoFPs.add(horarioContratoFP);
            horarioContratoFP = new HorarioContratoFP();
            horarioContratoFP.setInicioVigencia(((Estagiario) selecionado).getInicioVigencia());
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Há Campos Obrigatórios sem preenchimento!"));
        }
    }

    public void removeLotacaoFuncionalFP(ActionEvent e) {
        Estagiario c = ((Estagiario) selecionado);
        lotacaoFuncional = (LotacaoFuncional) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(c, lotacaoFuncional.getInicioVigencia())) {
            c.getLotacaoFuncionals().remove(lotacaoFuncional);
            lotacaoFuncional = new LotacaoFuncional();
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não é possível excluir um objeto vigente!"));
        }
    }

    public void removePrevidencia(ActionEvent e) {
        Estagiario c = ((Estagiario) selecionado);
        PrevidenciaVinculoFP previ = (PrevidenciaVinculoFP) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(c, previ.getInicioVigencia())) {
            c.getPrevidenciaVinculoFPs().remove(previ);
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaPrev", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não é possível excluir, já existe folha calculada para a vigência!"));
        }
    }

    public void removeHorarioCotratoFP(ActionEvent e) {
        Estagiario c = ((Estagiario) selecionado);
        horarioContratoFP = (HorarioContratoFP) e.getComponent().getAttributes().get("objeto");
        if (contratoFPFacade.podeExcluir(c, horarioContratoFP.getInicioVigencia())) {
            horarioContratoFPs.remove(horarioContratoFP);
            horarioContratoFP = new HorarioContratoFP();
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaHorario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não é possível excluir, já existe folha calculada para a vigência!"));
        }
    }

    public List<MatriculaFP> completaMatriculaFP(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacional(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalselecionada() {
        return hierarquiaOrganizacionalselecionada;
    }

    public void setHierarquiaOrganizacionalselecionada(HierarquiaOrganizacional hierarquiaOrganizacionalselecionada) {
        this.hierarquiaOrganizacionalselecionada = hierarquiaOrganizacionalselecionada;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalselecionadaLotacao() {
        return hierarquiaOrganizacionalselecionadaLotacao;
    }

    public void setHierarquiaOrganizacionalselecionadaLotacao(HierarquiaOrganizacional hierarquiaOrganizacionalselecionadaLotacao) {
        this.hierarquiaOrganizacionalselecionadaLotacao = hierarquiaOrganizacionalselecionadaLotacao;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public ConverterGenerico getConverterConta() {
        if (converterContaCorrenteBancaria == null) {
            converterContaCorrenteBancaria = new ConverterGenerico(ContaCorrenteBancaria.class, contaCorrenteBancariaFacade);
        }
        return converterContaCorrenteBancaria;
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null) {
            if (selecionado.getPessoaFisica() != null) {
                for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(selecionado.getPessoaFisica())) {
                    toReturn.add(new SelectItem(object, object.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<ContaCorrenteBancaria> completaContaCorrente(String parte) {
        return contaCorrenteBancariaFacade.listaContasPorPessoaFisica(selecionado.getPessoaFisica());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public Converter getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterPessoaJuridica;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<PessoaFisica> completaPessoaFisicas(String s) {
        return pessoaFisicaFacade.listaFiltrando(s.trim(), "nome", "cpf");
    }

    public List<PessoaJuridica> completaPFs(String s) {
        return pessoaJuridicaFacade.listaFiltrando(s.trim(), "razaoSocial", "cnpj");
    }

    public void recuperarMatriculas(SelectEvent item) {
        pessoaFisica = (PessoaFisica) item.getObject();
    }

    public List<SelectItem> getMatriculasFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null) {
            if (selecionado.getPessoaFisica() != null) {
                try {
                    for (MatriculaFP object : matriculaFPFacade.recuperaMatriculasPorPessoa(selecionado.getPessoaFisica())) {
                        toReturn.add(new SelectItem(object, object.toString()));
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }

        return toReturn;
    }

    public void atualizaUnidade(SelectEvent e) {
        //System.out.println("Unidade: " + (HierarquiaOrganizacional) e.getObject());
        hierarquiaOrganizacionalselecionadaLotacao = (HierarquiaOrganizacional) e.getObject();
        //System.out.println("Unidade: " + hierarquiaOrganizacionalselecionada);
    }

    public List<UnidadeOrganizacional> completaUnidadeLotacao(String parte) {
        return unidadeOrganizacionalFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioEstagiario(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public List<HorarioContratoFP> getHorarioContratoFPs() {
        return horarioContratoFPs;
    }

    public void setHorarioEstagiarios(List<HorarioContratoFP> horarioEstagiarios) {
        this.horarioContratoFPs = horarioEstagiarios;
    }

    public void addPrevidencia() {
        Estagiario c = ((Estagiario) selecionado);
        if (previdenciaVinculoFP.getInicioVigencia() != null) {
            previdenciaVinculoFP.setContratoFP(c);

            for (PrevidenciaVinculoFP p : c.getPrevidenciaVinculoFPs()) {
                if (p.getFinalVigencia() != null) {
                    if (p.getFinalVigencia().after(previdenciaVinculoFP.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaPrev", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!"));
                        return;
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaPrev", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a vigência da lista contem registro(s) com vigência aberta!"));
                    return;
                }
            }
            ((Estagiario) selecionado).getPrevidenciaVinculoFPs().add(previdenciaVinculoFP);
            previdenciaVinculoFP = new PrevidenciaVinculoFP();
            previdenciaVinculoFP.setInicioVigencia(((Estagiario) selecionado).getInicioVigencia());
        }
    }

    public MatriculaFP getMatriculaSelecionado() {
        return matriculaSelecionado;
    }

    public void setMatriculaSelecionado(MatriculaFP idMatriculaSelecionado) {
        this.matriculaSelecionado = idMatriculaSelecionado;
    }

    public String getNumeroSelecionado() {
        return numeroSelecionado;
    }

    public void setNumeroSelecionado(String numeroSelecionado) {
        this.numeroSelecionado = numeroSelecionado;
    }

    public BaseCargoFacade getBaseCargoFacade() {
        return baseCargoFacade;
    }

    public void setBaseCargoFacade(BaseCargoFacade baseCargoFacade) {
        this.baseCargoFacade = baseCargoFacade;
    }

    public CBOFacade getCboFacade() {
        return cboFacade;
    }

    public void setCboFacade(CBOFacade cboFacade) {
        this.cboFacade = cboFacade;
    }

    public SindicatoVinculoFP getSindicatoVinculoFP() {
        return sindicatoVinculoFP;
    }

    public void setSindicatoVinculoFP(SindicatoVinculoFP sindicatoVinculoFP) {
        this.sindicatoVinculoFP = sindicatoVinculoFP;
    }

    public AssociacaoVinculoFP getAssociacaoVinculoFP() {
        return associacaoVinculoFP;
    }

    public void setAssociacaoVinculoFP(AssociacaoVinculoFP associacaoVinculoFP) {
        this.associacaoVinculoFP = associacaoVinculoFP;
    }

    public Converter getConverterCbo() {
        if (converterCbo == null) {
            converterCbo = new ConverterAutoComplete(CBO.class, cboFacade);
        }
        return converterCbo;
    }

    public List<CBO> completaCbo(String parte) {
        if (contratoFPCargoSelecionado.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar um cargo no campo anterior para poder listar os CBOs.");
            return null;
        }
        return cboFacade.listarFiltrandoPorCargo(parte.trim(), contratoFPCargoSelecionado.getCargo());
    }

    public ConverterGenerico getConverterSindicato() {
        if (converterSindicato == null) {
            converterSindicato = new ConverterGenerico(Sindicato.class, sindicatoFacade);
        }
        return converterSindicato;
    }

    private void adicionaSindicato() {
        if (sindicatoVinculoFP.getInicioVigencia() != null) {
            Estagiario c = (Estagiario) selecionado;
            for (SindicatoVinculoFP p : c.getSindicatosVinculosFPs()) {
                if (p.getFinalVigencia() != null) {
                    if (p.getFinalVigencia().after(sindicatoVinculoFP.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaSindicatoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!"));
                        return;
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaSindicatoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a vigência da lista contem registro(s) com vigência aberta!"));
                    return;
                }
            }
            c.getSindicatosVinculosFPs().add(sindicatoVinculoFP);
            sindicatoVinculoFP = new SindicatoVinculoFP();
            sindicatoVinculoFP.setVinculoFP(c);
        }
    }

    public List<SelectItem> getSindicato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Sindicato object : sindicatoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterAssociacao() {
        if (converterAssociacao == null) {
            converterAssociacao = new ConverterGenerico(Associacao.class, associacaoFacade);
        }
        return converterAssociacao;
    }

    public void adicionarAssociacaoVinculoFP() {
        if (associacaoVinculoFP.getInicioVigencia() != null && associacaoVinculoFP.getFinalVigencia() != null) {
            if (associacaoVinculoFP.getInicioVigencia().after(associacaoVinculoFP.getFinalVigencia())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:finalVigenciaAssociacaoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data Final da vigência não pode ser inferior a data inicial da vigência !"));
            } else if (associacaoVinculoFP.getAssociacao() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario:associacao", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione uma associação para poder incluir !"));
            } else {
                adicionarAssociacao();
            }
        } else {
            if (associacaoVinculoFP.getInicioVigencia() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaAssociacaoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Insira pelo menos o inicio da vigência!"));
            } else if (associacaoVinculoFP.getAssociacao() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario:associacao", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione uma associação para poder incluir !"));
            } else {
                adicionarAssociacao();
            }
        }
    }

    private void adicionarAssociacao() {
        if (associacaoVinculoFP.getInicioVigencia() != null) {
            Estagiario c = (Estagiario) selecionado;
            for (AssociacaoVinculoFP p : c.getAssociacaosVinculosFPs()) {
                if (p.getFinalVigencia() != null) {
                    if (p.getFinalVigencia().after(associacaoVinculoFP.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaAssociacaoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!"));
                        return;
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:inicioVigenciaAssociacaoVinculoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a vigência da lista contem registro(s) com vigência aberta!"));
                    return;
                }
            }
            associacaoVinculoFP.setVinculoFP(c);
            c.getAssociacaosVinculosFPs().add(associacaoVinculoFP);
            associacaoVinculoFP = new AssociacaoVinculoFP();
        }
    }

    public List<SelectItem> getAssociacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Associacao object : associacaoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterNaturezaRendimento() {
        if (converterNaturezaRendimento == null) {
            converterNaturezaRendimento = new ConverterAutoComplete(NaturezaRendimento.class, naturezaRendimentoFacade);
        }
        return converterNaturezaRendimento;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<NaturezaRendimento> completaNaturezaRendimento(String parte) {
        return naturezaRendimentoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterSituacaoFuncional() {
        if (converterSituacaoFuncional == null) {
            converterSituacaoFuncional = new ConverterAutoComplete(SituacaoFuncional.class, situacaoFuncionalFacade);
        }
        return converterSituacaoFuncional;
    }

    public List<SituacaoFuncional> completaSituacoesFuncionais(String parte) {
        return situacaoFuncionalFacade.listaFiltrando(parte.trim(), "descricao");
    }


    public void removeSituacaoFuncional(ActionEvent e) {
        situacaoContratoFP = (SituacaoContratoFP) e.getComponent().getAttributes().get("objeto");
        ContratoFP cfp = (ContratoFP) selecionado;
//        situacaoContratoFP = situacaoFuncionalFacade.recuperarSituacao(situacaoContratoFP.getId());
        cfp.getSituacaoFuncionals().remove(situacaoContratoFP);
        instanciaSituacaoFuncional();
    }

    private void instanciaSituacaoFuncional() {
//        situacaoFuncional = new SituacaoFuncional();
//        situacaoFuncional.setCodigo(situacaoFuncionalFacade.retornaUltimoCodigoLong());
//        situacaoFuncional.setDescricao("");
//        situacaoFuncional.setInicioVigencia(SistemaFacade.getDataCorrente());

        situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setInicioVigencia(SistemaFacade.getDataCorrente());
    }

    @Override
    public Estagiario getSelecionado() {
        return (Estagiario) selecionado;
    }

    public List<PessoaFisica> getDependentes() {
        if (selecionado != null) {
            if (selecionado.getPessoaFisica() != null) {
                return dependenteFacade.listaDependentesPorResponsavel(selecionado.getPessoaFisica());
            }
        }
        return null;
    }

    public String getRetornaDescricao() {
        if (selecionado != null) {
            if (((Estagiario) selecionado).getNaturezaRendimento() != null) {
                return ((Estagiario) selecionado).getNaturezaRendimento().getDescricao();
            }
        }
        return "";
    }


    public List<LotacaoFuncional> lotacoesFuncionais(HorarioContratoFP horario) {
        List<LotacaoFuncional> lotacoes = new ArrayList<>();
        for (LotacaoFuncional lotacao : selecionado.getLotacaoFuncionals()) {
            if (lotacao.getHorarioContratoFP().equals(horario)) {
                lotacoes.add(lotacao);
            }
        }
        return lotacoes;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estagiario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void ativouAba(ActionEvent ev) {
        RequestContext.getCurrentInstance().update("Formulario:panelMatricula");
        RequestContext.getCurrentInstance().update("Formulario:contaCorrente");
        RequestContext.getCurrentInstance().update("Formulario:tabViewGeral:panelDependentesServidor");
    }

    public void removeAssociacaoVinculoFP(ActionEvent ev) {
        AssociacaoVinculoFP av = (AssociacaoVinculoFP) ev.getComponent().getAttributes().get("objeto");
        selecionado.getAssociacaosVinculosFPs().remove(av);
    }

    public void criarNovoDependente(ActionEvent ev) {
        String requestContext = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String pessoaFisicaId = "" + selecionado.getPessoaFisica().getId();
        String url = "window.open('" + requestContext + "/dependente/novo/?sessao=true&responsavel=" + pessoaFisicaId + "', '_blank'); ativarOuvidor(ativouAba);";
        RequestContext.getCurrentInstance().execute(url);
    }

    public void criarNovaMatricula(ActionEvent ev) {
        String requestContext = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String url = "window.open('" + requestContext + "/matriculafp/novo/?sessao=true', '_blank'); ativarOuvidor(ativouAba);";
        RequestContext.getCurrentInstance().execute(url);
    }

    public List<SelectItem> getNaturezaEstagio() {
        return Util.getListSelectItem(TipoNaturezaEstagio.values());
    }

    public List<SelectItem> getNivelEstagio() {
        return Util.getListSelectItem(TipoNivelEstagio.values());
    }

    public List<SelectItem> getCategoriaTrabalhador() {
        return Util.getListSelectItem(contratoFPFacade.getCategoriaTrabalhadorFacade().lista());
    }


    public void novoContratoFPCargo() {
        novoCargo = true;
        edicaoCargo = false;
        this.contratoFPCargoSelecionado = new ContratoFPCargo();
        if (!CollectionUtils.isEmpty(selecionado.getCargos())) {
            Date fimVigencia;
            Collections.sort(selecionado.getCargos());
            fimVigencia = selecionado.getCargos().get(selecionado.getCargos().size() - 1).getFimVigencia();
            if (fimVigencia != null) {
                this.contratoFPCargoSelecionado.setInicioVigencia(DataUtil.adicionaDias(fimVigencia, 1));
            }
            return;
        }

        this.contratoFPCargoSelecionado.setInicioVigencia(selecionado.getInicioVigencia());
    }

    public boolean existeFolhadePagamentoParaOCargo() {
        return folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(contratoFPCargoSelecionado.getContratoFP(), contratoFPCargoSelecionado.getInicioVigencia());
    }

    public void confirmarContratoFPCargo() {
        try {
            Util.validarCampos(contratoFPCargoSelecionado);
            if (isEdicaoCargo()) {
                if (!isValidarEdicaoCargo())
                    return;

                ContratoFPCargo ultimo = buscarCargoWithMaiorFimVigencia(selecionado.getCargos());
                if (ultimo != null)
                    ajustarFimVigencia(ultimo, this.contratoFPCargoSelecionado);

            } else {

                ContratoFPCargo vigente = buscarCargoVigente();

                if (vigente != null)
                    vigente.setFimVigencia(DataUtil.adicionaDias(this.contratoFPCargoSelecionado.getInicioVigencia(), -1));
            }

            atribuirToSelecionado();
            addCargo();
            cancelarContratoFPCargo();
            ordenarCargos();
            atualizarTabelaCargos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atualizarTabelaCargos() {
        FacesUtil.atualizarComponente("Formulario:tabViewGeral:panel-cargos");
    }

    private void cancelarContratoFPCargo() {
        this.contratoFPCargoSelecionado = null;
        this.novoCargo = false;
        this.edicaoCargo = false;
    }

    private void atribuirToSelecionado() {
        this.contratoFPCargoSelecionado.setContratoFP(selecionado);
        selecionado.setCargo(this.contratoFPCargoSelecionado.getCargo());
        selecionado.setCbo(this.contratoFPCargoSelecionado.getCbo());
    }

    private boolean isValidarEdicaoCargo() {
        if (isEdicaoCargo()) {
            ContratoFPCargo ultimo = buscarCargoWithMaiorFimVigencia(selecionado.getCargos());
            if (ultimo != null) {
                Date inicio = this.contratoFPCargoSelecionado.getInicioVigencia();
                Date fim = ultimo.getFimVigencia();
                if (fim != null) {
                    if (inicio.compareTo(fim) <= 0) {
                        FacesUtil.addOperacaoNaoPermitida("O Fim de vigência não pode ser menor que o início da vigência do cargo " + ultimo.getCargo().getDescricao().toLowerCase() + ".");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ContratoFPCargo buscarCargoWithMaiorFimVigencia(List<ContratoFPCargo> cargos) {
        ContratoFPCargo ultimo = null;
        ordenarCargos();
        if (cargos.size() == 1) {
            if (cargos.get(0).isFimVigenciaNull())
                return null;
        }

        if (isCargosEmpty())
            return null;
        if (selecionado.getCargos().size() > 1) {
            ultimo = cargos.get(cargos.size() - 2);
        }
        return ultimo;
    }

    private void ordenarCargos() {
        if (!isCargosEmpty())
            Collections.sort(selecionado.getCargos());
    }

    private boolean isCargosEmpty() {
        return (selecionado.getCargos() == null) || selecionado.getCargos().isEmpty();
    }

    private void ajustarFimVigencia(ContratoFPCargo ultimo, ContratoFPCargo cargo) {
        ultimo.setFimVigencia(DataUtil.adicionaDias(cargo.getInicioVigencia(), -1));
        cargo.setFimVigencia(null);
    }

    private ContratoFPCargo buscarCargoVigente() {
        if (isCargosEmpty())
            return null;

        for (ContratoFPCargo cargo : selecionado.getCargos()) {
            if (cargo.isFimVigenciaNull())
                return cargo;
        }
        return null;
    }

    private void addCargo() {
        selecionado.setCargos(Util.adicionarObjetoEmLista(selecionado.getCargos(), this.contratoFPCargoSelecionado));
    }

    public void editarContratoFPCargo(ContratoFPCargo cc) {
        this.contratoFPCargoSelecionado = (ContratoFPCargo) Util.clonarObjeto(cc);
        this.edicaoCargo = true;
        atualizarTabelaCargos();
    }

    public boolean isExcluirOrEditarCargo(ContratoFPCargo cargo) {
        boolean retorno = true;
        if (isOperacaoNovo()) {
            retorno = false;
        } else {
            retorno = folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(cargo.getContratoFP(), cargo.getInicioVigencia());
        }
        if (!retorno && !cargo.isFimVigenciaNull()) {
            retorno = true;
        }
        return retorno;
    }

    public void removerCargo(ContratoFPCargo cc) {
        if (!isOperacaoNovo()) {
            if (folhaDePagamentoFacade.existeFolhaProcessadaParaContratoDepoisDe(cc.getContratoFP(), cc.getInicioVigencia())) {
                String infoString = Util.formatterDiaMesAno.format(cc.getInicioVigencia());
                FacesUtil.addOperacaoNaoPermitida("Já existem dados financeiros para este registro após seu início de vigência <b>'" + infoString + "'</b>.");
                return;
            }
            definirCargo(cc);
        } else {
            definirCargo(cc);
        }
        atualizarTabelaCargos();
    }

    private void definirCargo(ContratoFPCargo cc) {
        this.contratoFPCargoSelecionado = null;
        this.edicaoCargo = true;
        selecionado.getCargos().remove(cc);
        atualizarTabelaCargos();
    }

    public List<SelectItem> tiposVinculoSicap() {
        return Util.getListSelectItem(TipoVinculoSicap.values(), false);
    }

}
