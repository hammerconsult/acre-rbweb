/*
 * Codigo gerado automaticamente em Fri Mar 04 09:37:47 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.PeriodoAquisitivoExcluido;
import br.com.webpublico.entidades.rh.esocial.CargoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.ItemVaga;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.estudoatuarial.TipoEspecificacaoCargo;
import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.enums.rh.esocial.TipoContagemEspecialEsocial;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.*;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "cargoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCargo", pattern = "/cargo/novo/", viewId = "/faces/rh/administracaodepagamento/cargo/edita.xhtml"),
    @URLMapping(id = "editarCargo", pattern = "/cargo/editar/#{cargoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cargo/edita.xhtml"),
    @URLMapping(id = "listarCargo", pattern = "/cargo/listar/", viewId = "/faces/rh/administracaodepagamento/cargo/lista.xhtml"),
    @URLMapping(id = "listarCargoPorVaga", pattern = "/vagas-ocupadas/listar/", viewId = "/faces/rh/administracaodepagamento/consultavagasporunidade/lista.xhtml"),
    @URLMapping(id = "verCargo", pattern = "/cargo/ver/#{cargoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cargo/visualizar.xhtml")
})
public class CargoControlador extends PrettyControlador<Cargo> implements Serializable, CRUD {

    List<ItemVaga> itemVagas;
    @EJB
    SistemaFacade sistemaFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private NivelEscolaridadeFacade nivelEscolaridadeFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private BaseCargo baseCargo;
    private BasePeriodoAquisitivo basePeriodoAquisitivo;
    private ConverterAutoComplete converterBasePeriodoAquisitivo;
    private NaturezaAtividadeFP naturezaAtividadeFP;
    private Vaga vaga;
    private ConverterAutoComplete converterAtoLegal;
    private Cargo cargoSelecionado;
    private ConverterAutoComplete converterCargo;
    //    private TipoPCS tipoPCSSelecionado;
    private ConverterGenerico converterEspecialidades;
    private ConverterAutoComplete converterNivelEscolaridade;
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;
    private CBO cboSelecionado;
    private AreaFormacao areaFormacaoSelecionada;
    private Habilidade habilidadeSelecionada;
    private ConverterAutoComplete converterEventoFP;
    private CargoEventoFP cargoEventoFPSelecionado;
    private ConfiguracaoCargoEventoFP configuracaoCargoEventoFP;
    private CargoAreaFormacao cargoAreaFormacaoSelecionado;
    private CargoHabilidade cargoHabilidadeSelecionado;
    private ItemCargoSindicato itemCargoSindicatoSelecionado;
    private ItemCargoSindicato itemCargoSindicatoOrigem;
    @EJB
    private CBOFacade cboFacade;
    private ActionListener adicionarItemCargoSindicato;
    private CargoEmpregadorESocial cargoEmpregadorESocial;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private List<OcorrenciaESocialDTO> ocorrencias;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private UnidadeCargo unidadeCargo;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Map<UnidadeCargo, String> mapCacheHierarquia;

    public CargoControlador() {
        super(Cargo.class);
        ocorrencias = Lists.newArrayList();
        unidadeCargo = new UnidadeCargo();
        mapCacheHierarquia = Maps.newHashMap();
    }

    public static void sort(List<Cargo> cargos) {
        Collections.sort(cargos, new Comparator<Cargo>() {
            @Override
            public int compare(Cargo o1, Cargo o2) {
                Integer codigo1 = Integer.valueOf(o1.getCodigoDoCargo());
                Integer codigo2 = Integer.valueOf(o2.getCodigoDoCargo());
                return codigo1.compareTo(codigo2);
            }
        });
    }

    public CargoHabilidade getCargoHabilidadeSelecionado() {
        return cargoHabilidadeSelecionado;
    }

    public void setCargoHabilidadeSelecionado(CargoHabilidade cargoHabilidadeSelecionado) {
        this.cargoHabilidadeSelecionado = cargoHabilidadeSelecionado;
    }

    public CargoAreaFormacao getCargoAreaFormacaoSelecionado() {
        return cargoAreaFormacaoSelecionado;
    }

    public void setCargoAreaFormacaoSelecionado(CargoAreaFormacao cargoAreaFormacaoSelecionado) {
        this.cargoAreaFormacaoSelecionado = cargoAreaFormacaoSelecionado;
    }

    public CargoEventoFP getCargoEventoFPSelecionado() {
        return cargoEventoFPSelecionado;
    }

    public void setCargoEventoFPSelecionado(CargoEventoFP cargoEventoFPSelecionado) {
        this.cargoEventoFPSelecionado = cargoEventoFPSelecionado;
    }

    public CBO getCboSelecionado() {
        return cboSelecionado;
    }

    public void setCboSelecionado(CBO cboSelecionado) {
        this.cboSelecionado = cboSelecionado;
    }

    public CargoFacade getFacade() {
        return cargoFacade;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    @Override
    public AbstractFacade getFacede() {
        return cargoFacade;
    }

    public ConfiguracaoCargoEventoFP getConfiguracaoCargoEventoFP() {
        return configuracaoCargoEventoFP;
    }

    public void setConfiguracaoCargoEventoFP(ConfiguracaoCargoEventoFP configuracaoCargoEventoFP) {
        this.configuracaoCargoEventoFP = configuracaoCargoEventoFP;
    }

    public UnidadeCargo getUnidadeCargo() {
        return unidadeCargo;
    }

    public void setUnidadeCargo(UnidadeCargo unidadeCargo) {
        this.unidadeCargo = unidadeCargo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @URLAction(mappingId = "novoCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoQuadroEfetivo() {
        novo();
    }

    @URLAction(mappingId = "listarCargoPorVaga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listarVagas() {
        itemVagas = new ArrayList<>();
        carregarItemVagas();
    }

    private void carregarItemVagas() {
        List<ItemVaga> listaItemVaga = new ArrayList<>();

        if (cargoSelecionado == null) {
            for (Cargo cargo : cargoFacade.filtraCargosVigentes(sistemaFacade.getDataOperacao())) {
                for (ItemVaga item : cargoFacade.recuperaVagasOcupadasPorHierarquia(cargo)) {
                    item.setCargo(cargo);
                    listaItemVaga.add(item);
                }
            }
        } else {
            for (ItemVaga item : cargoFacade.recuperaVagasOcupadasPorHierarquia(cargoSelecionado)) {
                item.setCargo(cargoSelecionado);
                listaItemVaga.add(item);
            }
        }
        itemVagas = listaItemVaga;
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoCargoAcumulavelSIPREV(TipoCargoAcumulavel.NAO_ACUMULAVEL);
        selecionado.setTipoContagemSIPREV(TipoContagemEspecial.NAO);
        selecionado.setBaseCargos(new ArrayList<BaseCargo>());
        selecionado.setTipoPCS(TipoPCS.QUADRO_EFETIVO);
        naturezaAtividadeFP = new NaturezaAtividadeFP();
        selecionado.setPermiteAcumulo(Boolean.FALSE);
        inicializarPeriodoExcludente();
        novoEmpregador();
    }

    private void inicializarPeriodoExcludente() {
        if (selecionado.getPeriodoAquisitivoExcluido() == null) {
            selecionado.setPeriodoAquisitivoExcluido(new PeriodoAquisitivoExcluido());
        }
    }

    @Override
    @URLAction(mappingId = "editarCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionar();
    }

    @Override
    @URLAction(mappingId = "verCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionar();
    }

    public void selecionar() {
        Cargo c = cargoFacade.recuperar(getId());
        selecionado = c;
        naturezaAtividadeFP = new NaturezaAtividadeFP();
        calcularVacancia();
        inicializarPeriodoExcludente();
        novoEmpregador();
        getEventoPorIdentificador(selecionado.getId());
    }

    public void adicionarEmpregador() {
        try {
            verificarRegistroAdicionado(this.cargoEmpregadorESocial);
            cargoEmpregadorESocial.setCargo(selecionado);
            selecionado.getEmpregadores().add(cargoEmpregadorESocial);
            novoEmpregador();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
            logger.error("Erro: ", val);
        }

    }

    private void verificarRegistroAdicionado(CargoEmpregadorESocial cargoEmpregadorESocial) {
        ValidacaoException val = new ValidacaoException();
        for (CargoEmpregadorESocial empregadorESocial : selecionado.getEmpregadores()) {
            if (empregadorESocial.getEmpregador().equals(cargoEmpregadorESocial.getEmpregador())) {
                val.adicionarMensagemDeOperacaoNaoPermitida("Empregador já adicionado na listagem");
            }
        }
        val.lancarException();
    }

    public void novoEmpregador() {
        cargoEmpregadorESocial = new CargoEmpregadorESocial();
    }

    public void calcularVacancia() {
        selecionado.calcularVagasCadastradas();
        selecionado.setVagasOcupadas(cargoFacade.buscarTotalVagasOcupadasPorCargo(selecionado));
    }

    private boolean validarVaga() {
        boolean validou = true;
        if (vaga == null || vaga.getNumeroVagas() == null || vaga.getNumeroVagas() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório não informado.", "Por favor, informe o número de vagas."));
            validou = false;
        }

        if (vaga.getAtoLegal() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório não informado.", "Por favor, informe o ato legal."));
            validou = false;
        }

        return validou;
    }

    public void addVaga() {
        if (!validarVaga()) {
            return;
        }

        vaga.setDataCriacao(new Date());
        vaga.setDataRegistro(new Date());
        vaga.setCargo(selecionado);
        selecionado.getVagas().add(vaga);

        vaga = new Vaga();
    }

    public void removeVaga(ActionEvent event) {
        Vaga v = (Vaga) event.getComponent().getAttributes().get("obj");
        selecionado.getVagas().remove(v);
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public List<ItemVaga> getItemVagas() {
        return itemVagas;
    }

    public void setItemVagas(List<ItemVaga> itemVagas) {
        this.itemVagas = itemVagas;
    }

    public boolean validarBaseCargo() {
        boolean valida = true;

        if (baseCargo == null || baseCargo.getBasePeriodoAquisitivo() == null) {
            FacesUtil.addCampoObrigatorio("O campo Base Periodo Aquisitivo deve ser informado.");
            valida = false;
        }
        if (baseCargo == null || baseCargo.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("O campo Início de Vigência deve ser informado.");
            valida = false;
        }

        if (baseCargo != null) {
            if (baseCargo.getInicioVigencia() != null && baseCargo.getFinalVigencia() != null) {
                if (baseCargo.getFinalVigencia().before(baseCargo.getInicioVigencia())) {
                    FacesUtil.addWarn("Atenção", "A data final de vigência não pode ser anterior à data de início de vigência.");
                    valida = false;
                }
            }

            for (BaseCargo bc : selecionado.getBaseCargos()) {
                if (baseCargo.getBasePeriodoAquisitivo() != null) {
                    if (baseCargo.getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().equals(bc.getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) {
                        if (DataUtil.isVigenciasIguais(baseCargo.getInicioVigencia(), baseCargo.getFinalVigencia(), bc.getInicioVigencia(), bc.getFinalVigencia())) {
                            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Já existe um mesmo objeto salvo com mesma vigencia e com o mesmo tipo de período");
                            valida = false;
                        }
                    }
                }
            }
        }
        return valida;
    }

    public void adicionaBaseCargo() {
        if (validarBaseCargo()) {
            baseCargo.setCargo(((Cargo) selecionado));
            ((Cargo) selecionado).getBaseCargos().add(baseCargo);
            baseCargo = new BaseCargo();
            baseCargo.setInicioVigencia(SistemaFacade.getDataCorrente());
            baseCargo = new BaseCargo();
        }
    }

    public List<SelectItem> getTiposPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoPCS tipo : TipoPCS.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoesCarreira() {
        return Util.getListSelectItem(SituacaoAtoLegal.values(), false);
    }

    public List<SelectItem> getTiposCargo() {
        return Util.getListSelectItem(TipoDeCargo.values());
    }

    public List<SelectItem> getTipoPeriodoAquisitivos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPeriodoAquisitivo tipo : TipoPeriodoAquisitivo.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposContagemEspecial() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoContagemEspecial obj : TipoContagemEspecial.values()) {
            toReturn.add(new SelectItem(obj, obj.getCodigo() + " - " + obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposContagemEspecialEsocial() {
        return Util.getListSelectItem(TipoContagemEspecialEsocial.values(), false);
    }

    public List<SelectItem> getTiposCargoAcumulavel() {
        return Util.getListSelectItemSemCampoVazio(TipoCargoAcumulavel.values(), false);
    }

    public Converter getConverterNivelEscolaridade() {
        if (converterNivelEscolaridade == null) {
            converterNivelEscolaridade = new ConverterAutoComplete(NivelEscolaridade.class, nivelEscolaridadeFacade);
        }
        return converterNivelEscolaridade;
    }

    public void removeBaseCargo(ActionEvent evento) {
        BaseCargo e = (BaseCargo) evento.getComponent().getAttributes().get("baseCargo");
        if (cargoFacade.verificaDependencia(e)) {
            ((Cargo) selecionado).getBaseCargos().remove(e);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossivel Remover", "Este objeto já esta Vinculado ao Periodo Aquisitivo Férias Licença. Crie um novo objeto"));
        }
    }

    public void alterarBaseCargo(BaseCargo bc) {
        baseCargo = bc;
        if (selecionado.getBaseCargos().contains(bc)) {
            selecionado.getBaseCargos().remove(bc);
        }
    }

    public List<BasePeriodoAquisitivo> completaBasesPeriodoAquisitivo(String s) {
        if (tipoPeriodoAquisitivo != null) {
            return basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoTipoDescricao(tipoPeriodoAquisitivo, s.trim());
        } else {
            return basePeriodoAquisitivoFacade.listaFiltrando(s.trim(), "descricao");
        }

    }

    public List<ClassificacaoCargo> completarClassificacoesCargo(String filtro) {
        return cargoFacade.buscarClassificacoesCargo(filtro);
    }

    public List<AtoLegal> completaAtoLegal(String s) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(s.trim(), PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<AtoLegal> completaAtoLegalCargo(String s) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(s.trim(), PropositoAtoLegal.ATO_DE_CARGO, "numero", "nome");
    }

    public Converter getConverterBasePeriodo() {
        if (converterBasePeriodoAquisitivo == null) {
            converterBasePeriodoAquisitivo = new ConverterAutoComplete(BasePeriodoAquisitivo.class, basePeriodoAquisitivoFacade);
        }
        return converterBasePeriodoAquisitivo;
    }

    public List<NivelEscolaridade> completaNivelEscolaridade(String parte) {
        return nivelEscolaridadeFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public BaseCargo getBaseCargo() {
        return baseCargo;
    }

    public void setBaseCargo(BaseCargo baseCargo) {
        this.baseCargo = baseCargo;
    }

    public BasePeriodoAquisitivo getBasePeriodoAquisitivo() {
        return basePeriodoAquisitivo;
    }

    public CargoEmpregadorESocial getCargoEmpregadorESocial() {
        return cargoEmpregadorESocial;
    }

    public void setCargoEmpregadorESocial(CargoEmpregadorESocial cargoEmpregadorESocial) {
        this.cargoEmpregadorESocial = cargoEmpregadorESocial;
    }

    public void setBasePeriodoAquisitivo(BasePeriodoAquisitivo basePeriodoAquisitivo) {
        this.basePeriodoAquisitivo = basePeriodoAquisitivo;
    }

    public Boolean validaCodigoAlfaNumerico(String codigo) {
        return codigo.matches("[a-zA-Z0-9]*");
    }

    public List<SelectItem> getTipoNatureza() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoNaturezaAtividadeFP tipo : TipoNaturezaAtividadeFP.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEmpregadores() {
        return Util.getListSelectItem(configuracaoEmpregadorESocialFacade.lista());
    }

    public NaturezaAtividadeFP getNaturezaAtividadeFP() {
        return naturezaAtividadeFP;
    }

    public void setNaturezaAtividadeFP(NaturezaAtividadeFP naturezaAtividadeFP) {
        this.naturezaAtividadeFP = naturezaAtividadeFP;
    }

    public void atribuiAtoLegal(SelectEvent evt) {
        selecionado.setAtoLegal((AtoLegal) evt.getObject());
    }

    public Cargo getCargoSelecionado() {
        return cargoSelecionado;
    }

    public void setCargoSelecionado(Cargo cargoSelecionado) {
        this.cargoSelecionado = cargoSelecionado;
    }

    public List<SelectItem> getTipoMagisterio() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoMagisterio tipo : TipoMagisterio.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<ItemVaga> getListaVagasPorCargo() {
        List<ItemVaga> listaItemVaga = new ArrayList<>();

        if (cargoSelecionado == null) {
            for (Cargo cargo : cargoFacade.filtraCargosVigentes(sistemaFacade.getDataOperacao())) {
                for (ItemVaga item : cargoFacade.recuperaVagasOcupadasPorHierarquia(cargo)) {
                    item.setCargo(cargo);
                    listaItemVaga.add(item);
                }
            }
        } else {
            for (ItemVaga item : cargoFacade.recuperaVagasOcupadasPorHierarquia(cargoSelecionado)) {
                item.setCargo(cargoSelecionado);
                listaItemVaga.add(item);
            }
        }
        return listaItemVaga;
    }

    public List<Cargo> completaCargo(String parte) {
        return cargoFacade.listaFiltrando(selecionado.getTipoPCS(), parte.trim(), "descricao", "codigoDoCargo");
    }

    public List<Cargo> completaCargos(String parte) {
        return cargoFacade.listaCargosVigentes(parte.trim());
    }

    public Converter getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(Cargo.class, cargoFacade);
        }
        return converterCargo;
    }

    public void setaCargoSelecionadoNull() {
        cargoSelecionado = null;
    }

    @Override
    public void salvar() {
        try {
            if (!selecionado.getAtivo()) {
                cargoFacade.salvarCargoInativo(selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            } else {
                validaRegrasEspecificas();
                if (Strings.isNullOrEmpty(selecionado.getCodigoDoCargo())) {
                    selecionado.setCodigoDoCargo(cargoFacade.getSingletonGeradorCodigo().getProximoCodigo(Cargo.class, "codigoDoCargo").toString());
                }
                super.salvar();
            }
        } catch (ValidacaoException val) {
            if (val.temMensagens()) {
                FacesUtil.printAllFacesMessages(val.getMensagens());
            } else {
                FacesUtil.addOperacaoNaoRealizada(val.getMessage());
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addErrorGenerico(e);
        }


    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException val = new ValidacaoException();
        PeriodoAquisitivoExcluido periodo = selecionado.getPeriodoAquisitivoExcluido();
        if (!Operacoes.NOVO.equals(operacao)) {
            if (Strings.isNullOrEmpty(selecionado.getCodigoDoCargo())) {
                val.adicionarMensagemDeCampoObrigatorio("O campo Código do Cargo deve ser informado.");
            }
        }
        if (selecionado.getEmpregadores().isEmpty()) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Informe o Empregador do Cargo.");
        }
        if (periodo != null) {
            if (periodo.getInicioVigencia() != null && periodo.getFimVigencia() != null) {
                if (DataUtil.dataInicialEhPosteriorOuIgualAdataFinal(new DateTime(periodo.getInicioVigencia()), new DateTime(periodo.getFinalVigencia()))) {
                    val.adicionarMensagemDeOperacaoNaoPermitida("O campo de início de vigência do período excludente é maior que o final de vigência.");
                }
            }
        }
        if (itemCargoSindicatoSelecionado != null) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Antes de salvar, confirme ou cancele as alterações na Entidade Sindical!");
        }
        if(selecionado.getEmpregadores() == null || selecionado.getEmpregadores().isEmpty()){
            val.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um Empregador para o cargo.");
        }
        if (selecionado.getUnidadesCargo().isEmpty()) {
            val.adicionarMensagemDeCampoObrigatorio("Adicione ao menos uma Hierarquia Organizacional ao Cargo.");
        }
        if (selecionado.getBaseCargos() == null || selecionado.getBaseCargos().isEmpty()) {
            val.adicionarMensagemDeCampoObrigatorio("Adicione ao menos uma Base Periodo Aquisitivo ao Cargo.");
        }
        if (selecionado.getClassificacaoCargo() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Classificação do Cargo na aba Sicap deve ser informado.");
        }
        val.lancarException();
        return true;
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }

    public Cargo esteSelecionado() {
        return (Cargo) selecionado;
    }

    private void verificarSeCboJaFoiAdicionado() {
        try {
            if (esteSelecionado().getCbos().contains(cboSelecionado)) {
                throw new RuntimeException("O CBO que você está tentando adicionar já está na lista de CBOs deste cargo.");
            }
        } catch (NullPointerException npe) {
        }
    }

    private void verificarSeCboIsNull() {
        if (cboSelecionado == null) {
            throw new RuntimeException("Por favor, informe um CBO para adicionar.");
        }
    }

    private void validarAdicaoDeCBO() throws RuntimeException {
        verificarSeCboIsNull();
        verificarSeCboJaFoiAdicionado();
    }

    public void adicionarCBO() {
        try {
            validarAdicaoDeCBO();
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada.", e.getMessage());
            return;
        }

        try {
            esteSelecionado().getCbos().add(cboSelecionado);
        } catch (NullPointerException npe) {
            esteSelecionado().setCbos(new ArrayList<CBO>());
            esteSelecionado().getCbos().add(cboSelecionado);
        }
        cboSelecionado = null;
    }

    public void removerCBODoCargo(CBO cbo) {
        try {
            validarRemocaoCBO(cbo);
            esteSelecionado().getCbos().remove(cbo);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRemocaoCBO(CBO cbo) {
        ValidacaoException ve = new ValidacaoException();
        if (cargoFacade.cboVinculadoCargoContratoVigente(cbo, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CBO está registrado para um contrato vigente.");
        }
        ve.lancarException();
    }

    public void removerEmpregador(CargoEmpregadorESocial cargoEmpregadorESocial) {
        if (selecionado.getEmpregadores().contains(cargoEmpregadorESocial)) {
            selecionado.getEmpregadores().remove(cargoEmpregadorESocial);
        }
    }

    public void removerSindicato(ItemCargoSindicato item) {
        itemCargoSindicatoOrigem = item;
        selecionado.getItensCargoSindicato().remove(item);
    }

    public void novoEvento() {
        this.cargoEventoFPSelecionado = new CargoEventoFP(selecionado);
    }

    public void novoEventoParaFolha() {
        this.configuracaoCargoEventoFP = new ConfiguracaoCargoEventoFP();
    }

    public void novoSindicato() {
        this.itemCargoSindicatoSelecionado = new ItemCargoSindicato(selecionado, null);
        this.itemCargoSindicatoOrigem = null;
    }

    public void selecionarEvento(CargoEventoFP evento) {
        cargoEventoFPSelecionado = evento;
        if (selecionado.getCargoEventoFP().contains(evento)) {
            selecionado.getCargoEventoFP().remove(evento);
        }
    }


    public void editarCargoEventoFP(ConfiguracaoCargoEventoFP evento) {
        configuracaoCargoEventoFP = evento;
        if (selecionado.getItemConfiguracaoCargoEventoFP().contains(evento)) {
            selecionado.getItemConfiguracaoCargoEventoFP().remove(evento);
        }
    }

    public void adicionaEvento() {
        if (!validarEvento()) {
            return;
        }
        selecionado.getCargoEventoFP().add(cargoEventoFPSelecionado);
        cancelarEvento();
    }

    private void validarConfiguracaoCargoEventoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoCargoEventoFP.getEventoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Evento.");
        }
        if (selecionado.getItemConfiguracaoCargoEventoFP() != null && configuracaoCargoEventoFP.getEventoFP() != null) {
            for (ConfiguracaoCargoEventoFP cargoEventoFP : selecionado.getItemConfiguracaoCargoEventoFP()) {
                if (cargoEventoFP.getEventoFP().equals(configuracaoCargoEventoFP.getEventoFP())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Evento já adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionaConfiguracaoCargoEventoFP() {
        try {
            validarConfiguracaoCargoEventoFP();
            configuracaoCargoEventoFP.setCargo(selecionado);
            selecionado.getItemConfiguracaoCargoEventoFP().add(configuracaoCargoEventoFP);
            cancelarConfiguracaoCargoEvento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void selecionarSindicato(ItemCargoSindicato evento) {
        itemCargoSindicatoOrigem = evento;
        itemCargoSindicatoSelecionado = (ItemCargoSindicato) Util.clonarObjeto(evento);
        if (selecionado.getItensCargoSindicato().contains(evento)) {
            selecionado.getItensCargoSindicato().remove(evento);
        }
    }

    public void cancelarEvento() {
        this.cargoEventoFPSelecionado = null;
    }

    public void cancelarConfiguracaoCargoEvento() {
        this.configuracaoCargoEventoFP = null;
    }

    public boolean validarEvento() {
        boolean retorno = true;
        if (cargoEventoFPSelecionado.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("O campo início de vigência é obrigatório.");
            retorno = false;
        }

        if (cargoEventoFPSelecionado.getEventoFP() == null) {
            FacesUtil.addCampoObrigatorio("O campo evento é obrigatório.");
            return false;
        }

        if (!validarPeriodoVigenciaEvento()) {
            retorno = false;
        }
        return retorno;
    }

    private boolean validarPeriodoVigenciaEvento() {
        DateTime dateInicio = new DateTime(cargoEventoFPSelecionado.getInicioVigencia());
        DateTime dateFim = new DateTime(cargoEventoFPSelecionado.getFinalVigencia());

        if (!validarDataInicioAnteriorDataFinal()) {
            return false;
        }
        if (!validarDatas(dateInicio, dateFim)) {
            return false;
        }
        return true;
    }

    private boolean validarDatas(DateTime dateInicio, DateTime dateFim) {
        for (CargoEventoFP cargoEventoFP : selecionado.getCargoEventoFP()) {
            DateTime dataValidarInicio = new DateTime(cargoEventoFP.getInicioVigencia());
            DateTime dataValidarFim = new DateTime(cargoEventoFP.getFinalVigencia());

            if (!existeEventoComFinalVigenciaNull()) {
                if (cargoEventoFPSelecionado.getFinalVigencia() != null) {
                    if (new Interval(dateInicio, dateFim).contains(dataValidarInicio)) {
                        FacesUtil.addError("Atenção", "Já existe um registro lançado que se enquadra no mesmo período. a data " + Util.dateToString(dataValidarInicio.toDate()) + " está entre " + Util.dateToString(dateInicio.toDate()) + " e " + Util.dateToString(dateFim.toDate()));
                        return false;
                    }
                    if (new Interval(dateInicio, dateFim).contains(dataValidarFim)) {
                        FacesUtil.addError("Atenção", "Já existe um registro lançado que se enquadra no mesmo período. a data " + Util.dateToString(dataValidarFim.toDate()) + " está entre " + Util.dateToString(dateInicio.toDate()) + " e " + Util.dateToString(dateFim.toDate()));
                        return false;
                    }
                    if (dateInicio.equals(dataValidarInicio) || dateInicio.equals(dataValidarFim) || dateFim.equals(dataValidarInicio) || dateFim.equals(dataValidarFim)) {
                        FacesUtil.addError("Atenção", "Existe um registro lançado que se enquadra na mesma data");
                        return false;
                    }
                    if (dataValidarInicio.isBefore(dateInicio) && dataValidarFim.isAfter(dateFim)) {
                        FacesUtil.addError("Atenção", "Já existe um período lançado dentro do mesmo período.");
                        return false;
                    }
                } else {
                    if (dateInicio.equals(dataValidarInicio) || dateInicio.equals(dataValidarFim)) {
                        FacesUtil.addError("Atenção", "Existe um registro lançado que se enquadra na mesma data");
                        return false;
                    }
                    if (dateInicio.isAfter(dataValidarInicio) && dateInicio.isBefore(dataValidarFim)) {
                        FacesUtil.addError("Atenção", "Já existe um período lançado dentro do mesmo período.");
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean validarDataInicioAnteriorDataFinal() {
        if (cargoEventoFPSelecionado.getFinalVigencia() != null) {
            if (cargoEventoFPSelecionado.getInicioVigencia().after(cargoEventoFPSelecionado.getFinalVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("A data de início de vigência deve ser anterior à data de final de vigência.");
                return false;
            }
        }
        return true;
    }

    private boolean existeEventoComFinalVigenciaNull() {
        for (CargoEventoFP cargoEventoFP : selecionado.getCargoEventoFP()) {
            if (cargoEventoFP.getFinalVigencia() == null) {
                FacesUtil.addAtencao("Para adicionar novo evento é necessário informar final de vigência do evento: " + cargoEventoFP.getEventoFP());
                return true;
            }
        }
        return false;
    }

    public void removerEvento(CargoEventoFP evento) {
        selecionado.getCargoEventoFP().remove(evento);
    }

    public void removerConfiguracaoEvento(ConfiguracaoCargoEventoFP evento) {
        selecionado.getItemConfiguracaoCargoEventoFP().remove(evento);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cargo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    public boolean validaAreaFormacao() {
        boolean valida = true;
        if (areaFormacaoSelecionada == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A Área de Formação é um campo obrigatório.");
        }

        for (CargoAreaFormacao obj : selecionado.getAreasFormacao()) {
            if (obj.getAreaFormacao().equals(areaFormacaoSelecionada)) {
                valida = false;
                FacesUtil.addOperacaoNaoPermitida("A Área de Formação já foi inserida.");
            }
        }

        return valida;
    }

    public boolean validaHabilidade() {
        boolean valida = true;
        if (habilidadeSelecionada == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A Habilidade é um campo obrigatório.");
        }

        for (CargoHabilidade obj : selecionado.getHabilidades()) {
            if (obj.getHabilidade().equals(habilidadeSelecionada)) {
                valida = false;
                FacesUtil.addOperacaoNaoPermitida("A Habilidade já foi inserida.");
            }
        }
        return valida;
    }

    public void adicionaAreaFormacao() {
        if (validaAreaFormacao()) {
            CargoAreaFormacao cargoAreaFormacao = new CargoAreaFormacao();
            cargoAreaFormacao.setCargo(selecionado);
            cargoAreaFormacao.setAreaFormacao(areaFormacaoSelecionada);
            selecionado.getAreasFormacao().add(cargoAreaFormacao);
            areaFormacaoSelecionada = null;
        }
    }

    public void adicionaHabilidade() {
        if (validaHabilidade()) {
            CargoHabilidade cargoHabilidade = new CargoHabilidade();
            cargoHabilidade.setCargo(selecionado);
            cargoHabilidade.setHabilidade(habilidadeSelecionada);
            selecionado.getHabilidades().add(cargoHabilidade);
            habilidadeSelecionada = null;
        }
    }

    public void alteraCargoAcumulavel() {
        if (!selecionado.getPermiteAcumulo()) {
            selecionado.setTipoCargoAcumulavelSIPREV(TipoCargoAcumulavel.NAO_ACUMULAVEL);
        }
    }

    public void removeFormacao(Object obj) {
        selecionado.getAreasFormacao().remove(obj);
    }

    public void removeHabilidade(Object obj) {
        selecionado.getHabilidades().remove(obj);
    }

    public Habilidade getHabilidadeSelecionada() {
        return habilidadeSelecionada;
    }

    public void setHabilidadeSelecionada(Habilidade habilidadeSelecionada) {
        this.habilidadeSelecionada = habilidadeSelecionada;
    }

    public AreaFormacao getAreaFormacaoSelecionada() {
        return areaFormacaoSelecionada;
    }

    public void setAreaFormacaoSelecionada(AreaFormacao areaFormacaoSelecionada) {
        this.areaFormacaoSelecionada = areaFormacaoSelecionada;
    }

    public List<AreaFormacao> completaAreaFormacao(String parte) {
        return cargoFacade.getAreaFormacaoFacade().completaAreaFormacao(parte);
    }

    public List<Habilidade> completaHabilidade(String parte) {
        return cargoFacade.getHabilidadeFacade().completaHabilidade(parte);
    }

    // Base Cargo
    public void novoBaseCargo() {
        baseCargo = new BaseCargo();
        baseCargo.setCargo(selecionado);
    }

    public void confirmarBaseCargo() {
        if (!validarBaseCargo()) {
            return;
        }
        selecionado.setBaseCargos(Util.adicionarObjetoEmLista(selecionado.getBaseCargos(), baseCargo));
        cancelarBaseCargo();
    }

    public void cancelarBaseCargo() {
        baseCargo = null;
    }

    // Vagas
    public void novoVaga() {
        vaga = new Vaga();
        vaga.setDataCriacao(new Date());
        vaga.setDataRegistro(new Date());
        vaga.setCargo(selecionado);
    }

    public void confirmarVaga() {
        if (!validarVaga()) {
            return;
        }

        selecionado.setVagas(Util.adicionarObjetoEmLista(selecionado.getVagas(), vaga));
        cancelarVaga();

    }

    public void cancelarVaga() {
        vaga = null;
    }

    // Área Formação
    public void novoAreaFormacao() {
        cargoAreaFormacaoSelecionado = new CargoAreaFormacao();
        cargoAreaFormacaoSelecionado.setCargo(selecionado);
    }

    public void confirmarAreaFormacao() {
        if (!Util.validaCampos(cargoAreaFormacaoSelecionado)) {
            return;
        }

        if (!validarFormacaoJaAdicionada(cargoAreaFormacaoSelecionado)) {
            return;
        }

        selecionado.setAreasFormacao(Util.adicionarObjetoEmLista(selecionado.getAreasFormacao(), cargoAreaFormacaoSelecionado));
        cancelarAreaFormacao();
    }

    private boolean validarFormacaoJaAdicionada(CargoAreaFormacao cargoAreaFormacaoSelecionado) {
        for (CargoAreaFormacao cargoArea : selecionado.getAreasFormacao()) {
            if (cargoArea.getAreaFormacao().equals(cargoAreaFormacaoSelecionado.getAreaFormacao())) {
                FacesUtil.addOperacaoNaoPermitida("A formação <b>" + cargoArea.getAreaFormacao() + "</b> já foi vinculada a este cargo e não pode ser informada novamente.");
                return false;
            }
        }
        return true;
    }

    public void cancelarAreaFormacao() {
        cargoAreaFormacaoSelecionado = null;
    }

    // Habilidades
    public void novoHabilidade() {
        cargoHabilidadeSelecionado = new CargoHabilidade();
        cargoHabilidadeSelecionado.setCargo(selecionado);
    }

    public void confirmarHabilidade() {
        if (!isHabilidadeInformada(cargoHabilidadeSelecionado)) {
            return;
        }

        if (!validarHabilidadeJaAdicionada(cargoHabilidadeSelecionado)) {
            return;
        }

        selecionado.setHabilidades(Util.adicionarObjetoEmLista(selecionado.getHabilidades(), cargoHabilidadeSelecionado));
        cancelarHabilidade();
    }

    private boolean isHabilidadeInformada(CargoHabilidade cargoHabilidadeSelecionado) {
        if (cargoHabilidadeSelecionado.getHabilidade() == null) {
            FacesUtil.addCampoObrigatorio("O campo habilidade é obrigatório.");
            return false;
        }
        return true;
    }

    private boolean validarHabilidadeJaAdicionada(CargoHabilidade cargoHabilidadeSelecionado) {
        for (CargoHabilidade cargoHabilidade : selecionado.getHabilidades()) {
            if (cargoHabilidade.getHabilidade().equals(cargoHabilidadeSelecionado.getHabilidade())) {
                FacesUtil.addOperacaoNaoPermitida("A habilidade <b>" + cargoHabilidade.getHabilidade() + "</b> já foi vinculada a este cargo e não pode ser informada novamente.");
                return false;
            }
        }
        return true;
    }

    public void cancelarHabilidade() {
        cargoHabilidadeSelecionado = null;
    }

    // CBO
    public List<CBO> completarCBO(String param) {
        return cboFacade.buscarCbosAtivos(param);
    }

    public ConverterAutoComplete getConverterCBO() {
        return new ConverterAutoComplete(CBO.class, cboFacade);
    }

    public void novoCbo() {
        cboSelecionado = new CBO();
        cboSelecionado.setCargos(Util.adicionarObjetoEmLista(cboSelecionado.getCargos(), selecionado));
    }

    public void confirmarCbo() {
        if (cboSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo CBO deve ser informado.");
            cboSelecionado = new CBO();
            return;
        }

        if (!validarCboJaAdicionado(cboSelecionado)) {
            return;
        }

        if (TipoCBO.SINONIMO.equals(cboSelecionado.getTipoCBO())) {
            cboSelecionado = cboFacade.buscarCboOcupacaoAtivoPorCodigo(cboSelecionado.getCodigo());
        }
        selecionado.setCbos(Util.adicionarObjetoEmLista(selecionado.getCbos(), cboSelecionado));
        cancelarCbo();
    }

    private boolean validarCboJaAdicionado(CBO cboParametro) {
        if (selecionado.getCbos() == null || selecionado.getCbos().isEmpty()) {
            return true;
        }

        for (CBO cbo : selecionado.getCbos()) {
            if (cboParametro.equals(cbo)) {
                FacesUtil.addOperacaoNaoPermitida("O CBO <b>" + cboParametro + "</b> já foi vinculado a este cargo e não pode ser informado novamente.");
                return false;
            }
        }
        return true;
    }

    public void cancelarCbo() {
        cboSelecionado = null;
    }

    public void cancelarSindicato() {
        itemCargoSindicatoSelecionado = null;
        if (itemCargoSindicatoOrigem != null) {
            selecionado.getItensCargoSindicato().add(itemCargoSindicatoOrigem);
        }
    }

    public ItemCargoSindicato getItemCargoSindicatoSelecionado() {
        return itemCargoSindicatoSelecionado;
    }

    public void setItemCargoSindicatoSelecionado(ItemCargoSindicato itemCargoSindicatoSelecionado) {
        this.itemCargoSindicatoSelecionado = itemCargoSindicatoSelecionado;
    }

    public void adicionarItemCargoSindicato() {
        try {
            validarSindicato();
            if (DataUtil.isVigenciaValida(itemCargoSindicatoSelecionado, selecionado.getItensCargoSindicato())) {
                selecionado.getItensCargoSindicato().add(itemCargoSindicatoSelecionado);
                itemCargoSindicatoSelecionado = null;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarSindicato() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(itemCargoSindicatoSelecionado);
        if (itemCargoSindicatoSelecionado.getDataFim() != null && itemCargoSindicatoSelecionado.getDataInicio().compareTo(itemCargoSindicatoSelecionado.getDataFim()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início de vigência deve ser anterior a data de fim de vigência.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<>();
        List<Cargo> cargos = cargoFacade.filtraCargosVigentes(sistemaFacade.getDataOperacao());
        sort(cargos);
        for (Cargo obj : cargos) {
            toReturn.add(new SelectItem(obj, obj.getCodigoDoCargo() + " - " + obj.getDescricao() + " - " + (obj.getTipoPCS() != null ? obj.getTipoPCS().getDescricao() : "")));
        }
        return toReturn;
    }

    private void getEventoPorIdentificador(Long id) {
        selecionado.setEventosEsocial(cargoFacade.getEventoPorIdentificador(id));
    }

    public List<SelectItem> getTipoEscpecificacaoCargo() {
        return Util.getListSelectItem(TipoEspecificacaoCargo.values(), false);
    }

    public List<ReferenciaFP> completarReferenciaFP(String s) {
        return referenciaFPFacade.listaFiltrandoPorTipoDescricao(TipoReferenciaFP.VALOR_VALOR, s);
    }

    public List<SelectItem> getTiposAcumulaveisSIG() {
        return Util.getListSelectItem(TipoAcumulavelSIG.values(), false);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaizAndUsuario(parte.trim(), sistemaFacade.getDataOperacao());
    }

    public void adicionarUnidadeOrganizacional() {
        try {
            validarUnidadeOrganizacional();
            unidadeCargo.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            unidadeCargo.setCargo(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidadesCargo(), unidadeCargo);
            unidadeCargo = new UnidadeCargo();
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerUnidadeOrganizacional(UnidadeCargo unidadeCargo) {
        selecionado.getUnidadesCargo().remove(unidadeCargo);
    }

    public void validarUnidadeOrganizacional() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hierarquia Organizacional deve ser informado.");
        }
        ve.lancarException();
    }

    public String hierarquiaDaUnidadeCargo(UnidadeCargo unidade) {
        if (mapCacheHierarquia.containsKey(unidade)) {
            return mapCacheHierarquia.get(unidade);
        }
        HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(unidade.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());

        String retorno = (hierarquia != null) ? hierarquia.getCodigo() + " - " + hierarquia.getDescricao() : null;
        if (retorno != null) {
            mapCacheHierarquia.put(unidade, retorno);
        }
        return retorno;
    }
}
