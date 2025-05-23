/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LoteProcessamentoFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Peixe
 */
@ManagedBean(name = "loteProcessamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLoteProcessamento", pattern = "/loteprocessamento/novo/", viewId = "/faces/rh/administracaodepagamento/lote/edita.xhtml"),
    @URLMapping(id = "editarLoteProcessamento", pattern = "/loteprocessamento/editar/#{associacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lote/edita.xhtml"),
    @URLMapping(id = "rotinaCriarLote", pattern = "/loteprocessamento/correcao/", viewId = "/faces/rh/administracaodepagamento/lote/correcao.xhtml"),
    @URLMapping(id = "listarLoteProcessamento", pattern = "/loteprocessamento/listar/", viewId = "/faces/rh/administracaodepagamento/lote/lista.xhtml"),
    @URLMapping(id = "verLoteProcessamento", pattern = "/loteprocessamento/ver/#{loteProcessamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lote/visualizar.xhtml")
})
public class LoteProcessamentoControlador extends PrettyControlador<LoteProcessamento> implements Serializable, CRUD {

    @EJB
    private LoteProcessamentoFacade facade;
    private List<CamposPesquisa> filtrosSelecionados;
    private VinculoFP vinculoFP;
    private List<VinculoFP> vinculoFPs;
    private List<Cargo> cargos;
    private List<SituacaoFuncional> situacaoFuncionals;
    private List<CategoriaPCS> categoriaPCSs;
    private List<EventoFP> eventos;
    private LazyDataModel<VinculoFP> vinculosTodos;
    private List<RecursoFP> recursosFP;
    private Cargo cargo;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private RecursoFP recursoFP;
    private SituacaoFuncional situacaoFuncional;
    private CategoriaPCS categoriaPCS;
    private Date dataInicio;
    private Date dataFim;
    private Integer numero;
    private ConverterAutoComplete converterContratoFP;
    private Boolean utilizaFiltroPeriodo;
    private ObjetoPesquisa objetoPesquisa;
    private List<String> log;
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalCargo;
    private List<HierarquiaOrganizacional> hierarquiaCargos;
    private EventoFP eventoFP;
    private Date dataInicioFiltroVerba;
    private Date dataFimFiltroVerba;

    public LoteProcessamentoControlador() {
        super(LoteProcessamento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/loteprocessamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public LoteProcessamento getSelecionado() {
        return selecionado;
    }

    public Date getDataInicioFiltroVerba() {
        return dataInicioFiltroVerba;
    }

    public void setDataInicioFiltroVerba(Date dataInicioFiltroVerba) {
        this.dataInicioFiltroVerba = dataInicioFiltroVerba;
    }

    public Date getDataFimFiltroVerba() {
        return dataFimFiltroVerba;
    }

    public void setDataFimFiltroVerba(Date dataFimFiltroVerba) {
        this.dataFimFiltroVerba = dataFimFiltroVerba;
    }

    public List<EventoFP> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoFP> eventos) {
        this.eventos = eventos;
    }

    @URLAction(mappingId = "novoLoteProcessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        vinculosTodos = new LazyCarDataModel(new ArrayList<VinculoFP>());
        inicializarListasEAtributos();
    }

    private void inicializarListasEAtributos() {
        filtrosSelecionados = Lists.newArrayList();
        vinculoFPs = Lists.newArrayList();
        cargos = Lists.newLinkedList();
        hierarquiaCargos = Lists.newLinkedList();
        cargo = null;
        eventoFP = null;
        situacaoFuncionals = Lists.newLinkedList();
        recursosFP = Lists.newLinkedList();
        selecionado.setUnidadeOrganizacionals(new LinkedList<UnidadeOrganizacional>());
        categoriaPCSs = Lists.newLinkedList();
        eventos = Lists.newLinkedList();
        situacaoFuncional = null;
        hierarquiaOrganizacional = null;
        categoriaPCS = null;
        utilizaFiltroPeriodo = Boolean.FALSE;
        dataInicio = null;
        dataFim = null;
    }

    @URLAction(mappingId = "rotinaCriarLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void correcoesLote() {
        objetoPesquisa = new ObjetoPesquisa();
        log = new ArrayList<>();
    }

    @URLAction(mappingId = "verLoteProcessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        gerarConsultaGeral();
    }

    @URLAction(mappingId = "editarLoteProcessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarExecutarLote();
            validaNumeroLote();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public ObjetoPesquisa getObjetoPesquisa() {
        return objetoPesquisa;
    }

    public void setObjetoPesquisa(ObjetoPesquisa objetoPesquisa) {
        this.objetoPesquisa = objetoPesquisa;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Boolean getUtilizaFiltroPeriodo() {
        return utilizaFiltroPeriodo;
    }

    public void setUtilizaFiltroPeriodo(Boolean utilizaFiltroPeriodo) {
        this.utilizaFiltroPeriodo = utilizaFiltroPeriodo;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalCargo() {
        return hierarquiaOrganizacionalCargo;
    }

    public void setHierarquiaOrganizacionalCargo(HierarquiaOrganizacional hierarquiaOrganizacionalCargo) {
        this.hierarquiaOrganizacionalCargo = hierarquiaOrganizacionalCargo;
    }

    public List<HierarquiaOrganizacional> getHierarquiaCargos() {
        return hierarquiaCargos;
    }

    public void setHierarquiaCargos(List<HierarquiaOrganizacional> hierarquiaCargos) {
        this.hierarquiaCargos = hierarquiaCargos;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public void definirNumeroLote() {
        if (selecionado.getMes() != null && selecionado.getAno() != null) {
            BigDecimal numero = facade.buscarProximoNumeroLote(selecionado.getMes().getNumeroMes() - 1, selecionado.getAno());
            if (numero != null) {
                selecionado.setNumeroLote(numero.intValue());
            } else {
                selecionado.setNumeroLote(1);
            }
        }
    }

    private void validaNumeroLote() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAno() != null && selecionado.getMes() != null && selecionado.getNumeroLote() != null) {
            if (facade.existeNumeroLoteNoAnoMes(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Número de Lote existente para o Mês e Ano informado!");
            }
        }
        ve.lancarException();
    }

    private void limparCampoDesmarcado(List<CamposPesquisa> campos) {
        List<CamposPesquisa> aLimpar = new ArrayList<>();
        List<CamposPesquisa> pqp = new ArrayList<>();
        aLimpar.addAll(Arrays.asList(CamposPesquisa.values()));

        for (CamposPesquisa cp : aLimpar) {
            if (!campos.contains(cp)) {
                pqp.add(cp);
            }
        }
        for (CamposPesquisa cp : pqp) {
            limpar(cp);
        }
    }

    public void limpar(CamposPesquisa campo) {
        if (CamposPesquisa.MATRICULA.equals(campo)) {
            limparMatricula();
        }
        if (CamposPesquisa.CARGO.equals(campo)) {
            limparCargo();
        }
        if (CamposPesquisa.SITUACAO_FUNCIONAL.equals(campo)) {
            limparSituacaoFuncional();
        }
        if (CamposPesquisa.ORGAO.equals(campo)) {
            limparOrgao();
        }
        if (CamposPesquisa.ORGAO_RECURSOFP.equals(campo)) {
            limparOrgaoRecurso();
        }
        if (CamposPesquisa.CATEGORIA.equals(campo)) {
            limparCategoria();
        }
        if (CamposPesquisa.PERIODO.equals(campo)) {
            limparPeriodo();
        }
    }

    private void limparMatricula() {
        vinculoFP = null;
        vinculoFPs = new ArrayList<>();
        retirarDoFiltro(CamposPesquisa.MATRICULA);
    }

    private void limparCargo() {
        cargo = null;
        cargos = new ArrayList<>();
        retirarDoFiltro(CamposPesquisa.CARGO);
    }

    private void limparSituacaoFuncional() {
        situacaoFuncional = null;
        situacaoFuncionals = new ArrayList<>();
        retirarDoFiltro(CamposPesquisa.SITUACAO_FUNCIONAL);
    }

    private void limparOrgao() {
        hierarquiaOrganizacional = null;
        selecionado.setUnidadeOrganizacionals(new ArrayList<UnidadeOrganizacional>());
        retirarDoFiltro(CamposPesquisa.ORGAO);
    }

    private void limparOrgaoRecurso() {
        recursoFP = null;
        recursosFP = new ArrayList<>();
        retirarDoFiltro(CamposPesquisa.ORGAO_RECURSOFP);
    }

    private void limparCategoria() {
        categoriaPCS = null;
        categoriaPCSs = new ArrayList<>();
        retirarDoFiltro(CamposPesquisa.CATEGORIA);
    }

    private void limparPeriodo() {
        dataFim = null;
        dataInicio = null;
        utilizaFiltroPeriodo = Boolean.FALSE;
        retirarDoFiltro(CamposPesquisa.PERIODO);
    }

    public void mudouFiltro(ValueChangeEvent e) {
        List<CamposPesquisa> camposPesquisa = CamposPesquisa.findAll(e.getNewValue().toString());
        limparCampoDesmarcado(camposPesquisa);
    }

    public CamposPesquisa[] getValores() {
        return CamposPesquisa.values();
    }

    public List<CamposPesquisa> getFiltrosSelecionados() {
        return filtrosSelecionados;
    }

    public void setFiltrosSelecionados(List<CamposPesquisa> filtrosSelecionados) {
        this.filtrosSelecionados = filtrosSelecionados;
    }

    public List<String> getCampos() {
        List<String> asList = new ArrayList<>();
        for (CamposPesquisa cp : filtrosSelecionados) {
            asList.add(cp.name());
        }
        return asList;
    }

    public List<SituacaoFuncional> getSituacaoFuncionals() {
        return situacaoFuncionals;
    }

    public void setSituacaoFuncionals(List<SituacaoFuncional> situacaoFuncionals) {
        this.situacaoFuncionals = situacaoFuncionals;
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public List<VinculoFP> getVinculoFPs() {
        return vinculoFPs;
    }

    public void setVinculoFPs(List<VinculoFP> vinculoFPs) {
        this.vinculoFPs = vinculoFPs;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public Converter getConverterFiltros() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                for (CamposPesquisa ordem : CamposPesquisa.values()) {
                    if (ordem.getDescricao().equals(string)) {
                        return ordem;
                    }
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                return ((CamposPesquisa) o).getDescricao();
            }
        };
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<CategoriaPCS> getCategoriaPCSs() {
        return categoriaPCSs;
    }

    public void setCategoriaPCSs(List<CategoriaPCS> categoriaPCSs) {
        this.categoriaPCSs = categoriaPCSs;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<RecursoFP> getRecursosFP() {
        return recursosFP;
    }

    public void setRecursosFP(List<RecursoFP> recursosFP) {
        this.recursosFP = recursosFP;
    }

    public List<CategoriaPCS> completarCategoriaPCSs(String parte) {
        return facade.getCategoriaPCSFacade().listaFiltrandoVigenteSuperior(parte.trim());
    }

    public List<VinculoFP> completarContrato(String s) {
        if (selecionado.getMes() != null && selecionado.getAno() != null) {
            return facade.getContratoFPFacade().buscaContratoFiltrandoAtributosVinculoMatriculaFPVigenteNaoCedidoSemOnus(s.trim(), DataUtil.criarDataComMesEAno(selecionado.getMes().getNumeroMes(), selecionado.getAno()).toDate(), DataUtil.criarDataUltimoDiaMes(selecionado.getMes().getNumeroMes(), selecionado.getAno()).toDate());
        }
        return Lists.newArrayList();
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, facade.getVinculoFPFacade());
        }
        return converterContratoFP;
    }

    public List<Cargo> completaCargos(String parte) {
        return facade.getCargoFacade().buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public List<SituacaoFuncional> completarSituacaoFuncional(String parte) {
        return facade.getSituacaoFuncionalFacade().buscarSituacaoFuncionalExcluindoCodigos(parte.trim(), SituacaoFuncional.INSTITUIDOR);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalByTipoAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().listaFiltrandoPorOrgaoAndTipoAdministrativa(parte);
    }

    public List<RecursoFP> completarRecursoFP(String parte) {
        return facade.getRecursoFPFacade().listaRecursosFPVigente(parte.trim(), UtilRH.getDataOperacao());
    }

    public void adicionarParametroMatricula() {
        try {
            validarMatricula();
            vinculoFPs.add(vinculoFP);
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.MATRICULA, vinculoFP.getClass().getSimpleName(), String.valueOf(vinculoFP.getId()));
            addItensLoteProcessamento(ilp);
            vinculoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarMatricula() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        ve.lancarException();
        if (vinculoFPs.contains(vinculoFP)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A matrícula " + vinculoFP + " já foi adicionada na lista.");
        }
        ve.lancarException();
    }

    public void adicionarParametroCargo() {
        try {
            validarCargo();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.CARGO, cargo.getClass().getSimpleName(), String.valueOf(cargo.getId()));
            addItensLoteProcessamento(ilp);
            cargos.add(cargo);
            cargo = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCargo() {
        ValidacaoException ve = new ValidacaoException();
        if (cargo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cargo deve ser informado.");
        }
        ve.lancarException();
        if (cargos.contains(cargo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O cargo" + cargo + " já foi adicionado na lista.");
        }
        ve.lancarException();
    }

    public void adicionarParametroSituacaoFuncional() {
        try {
            validarSituacaoFuncional();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.SITUACAO_FUNCIONAL, situacaoFuncional.getClass().getSimpleName(), String.valueOf(situacaoFuncional.getId()));
            addItensLoteProcessamento(ilp);
            situacaoFuncionals.add(situacaoFuncional);
            situacaoFuncional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSituacaoFuncional() {
        ValidacaoException ve = new ValidacaoException();
        if (situacaoFuncional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação Funcional deve ser informado.");
        }
        ve.lancarException();
        if (situacaoFuncionals.contains(situacaoFuncional)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A situação funcional " + situacaoFuncional + " já foi adicionada na lista.");
        }
        ve.lancarException();
    }

    public void adicionarParametroUnidade() {
        try {
            validarOrgaoLocalTrabalho();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.ORGAO, hierarquiaOrganizacional.getSubordinada().getClass().getSimpleName(), String.valueOf(hierarquiaOrganizacional.getSubordinada().getId()));
            addItensLoteProcessamento(ilp);
            selecionado.getUnidadeOrganizacionals().add(hierarquiaOrganizacional.getSubordinada());
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarOrgaoLocalTrabalho() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getUnidadeOrganizacionals().contains(hierarquiaOrganizacional.getSubordinada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade organizacional " + hierarquiaOrganizacional + " já foi adicionada na lista.");
        }
        ve.lancarException();
    }

    public void adicionarParametroRecursoFP() {
        try {
            validarOrgaoRecursoFP();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.ORGAO_RECURSOFP, recursoFP.getClass().getSimpleName(), String.valueOf(recursoFP.getId()));
            addItensLoteProcessamento(ilp);
            recursosFP.add(recursoFP);
            recursoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarOrgaoRecursoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (recursoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Recurso FP deve ser informado.");
        }
        ve.lancarException();
        if (recursosFP.contains(recursoFP)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A recursoFP " + recursoFP + "já foi adicionado na lista.");
        }
        ve.lancarException();
    }

    public void adicionarParametroCategoria() {
        try {
            validarCategoriaPCCR();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.CATEGORIA, categoriaPCS.getClass().getSimpleName(), String.valueOf(categoriaPCS.getId()));
            addItensLoteProcessamento(ilp);
            categoriaPCSs.add(categoriaPCS);
            categoriaPCS = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarParametroVerba() {
        try {
            validarParametroVerba();
            ItensLoteProcessamento ilp = new ItensLoteProcessamento(CamposPesquisa.VERBA, eventoFP.getClass().getSimpleName(), String.valueOf(eventoFP.getId()));
            ilp.setDataInicio(dataInicioFiltroVerba);
            ilp.setDataFim(dataFimFiltroVerba);
            ilp.setValor(eventoFP.getId().toString());
            addItensLoteProcessamento(ilp);
            eventoFP.setDataInicioFiltroVerba(dataInicioFiltroVerba);
            eventoFP.setDataFimFiltroVerba(dataFimFiltroVerba);
            eventos.add(eventoFP);
            eventoFP = null;
            dataInicioFiltroVerba = null;
            dataFimFiltroVerba = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCategoriaPCCR() {
        ValidacaoException ve = new ValidacaoException();
        if (categoriaPCS == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria PCCR deve ser informado.");
        } else {
            if (categoriaPCSs.contains(categoriaPCS)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A categoria " + categoriaPCS + "já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    private void validarParametroVerba() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicioFiltroVerba == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFimFiltroVerba == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if ((dataInicioFiltroVerba != null && dataFimFiltroVerba != null) && dataInicioFiltroVerba.after(dataFimFiltroVerba)) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial deve ser anterior a Data Final.");
        }
        if (eventoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Evento Folha de Pagamento deve ser informado.");
        }
        ve.lancarException();
    }

    public void adicionarParametroPeriodo() {
        try {
            if (utilizaFiltroPeriodo) {
                validarPeriodo();
                String campo = getNomeCampoData();
                ItensLoteProcessamento ilp = new ItensLoteProcessamento();
                ilp.setCampo(CamposPesquisa.PERIODO);
                ilp.setDataInicio(dataInicio);
                ilp.setDataFim(dataFim);
                ilp.setTipo(campo);
                addItensLoteProcessamento(ilp);
            } else {
                removerItenComPeriodo();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            removerItenComPeriodo();
        }
    }

    private void validarPeriodo() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicio == null && dataFim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um período para continuar.");
            utilizaFiltroPeriodo = Boolean.FALSE;
        } else {
            if (dataFim.before(dataInicio)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser superior ou igual a data inicial.");
                utilizaFiltroPeriodo = Boolean.FALSE;
            }
        }
        ve.lancarException();
    }

    public void clearMatricula() {
        vinculoFPs.clear();
        vinculoFP = null;
        retirarDoFiltro(CamposPesquisa.MATRICULA);
    }

    public void clearOrgao() {
        retirarDoFiltro(CamposPesquisa.ORGAO);
        selecionado.getUnidadeOrganizacionals().clear();
        hierarquiaOrganizacional = null;
    }

    public void clearRecursosFP() {
        retirarDoFiltro(CamposPesquisa.ORGAO_RECURSOFP);
        recursosFP.clear();
        recursoFP = null;
    }

    public void clearCargos() {
        cargos.clear();
        cargo = null;
        retirarDoFiltro(CamposPesquisa.CARGO);
    }

    public void clearSituacaoFuncional() {
        situacaoFuncionals.clear();
        situacaoFuncional = null;
        retirarDoFiltro(CamposPesquisa.SITUACAO_FUNCIONAL);
    }

    public void clearCategoria() {
        categoriaPCSs.clear();
        categoriaPCS = null;
        retirarDoFiltro(CamposPesquisa.CATEGORIA);
    }

    public void limparParametroVerba() {
        eventos.clear();
        eventoFP = null;
        dataInicioFiltroVerba = null;
        dataFimFiltroVerba = null;
        retirarDoFiltro(CamposPesquisa.VERBA);
    }

    private String cortaStringFim(String s, int quantidade) {
        return s.substring(0, s.length() - quantidade);
    }

    private void addItensLoteProcessamento(ItensLoteProcessamento ilp) {
        ilp.setLoteProcessamento(selecionado);
        getSelecionado().getItensLoteProcessamentos().add(ilp);
    }

    private String gerarQueryConsulta() {
        return facade.gerarQueryConsulta(selecionado);
    }

    public List<ItensLoteProcessamento> findObjetosByTipo(LoteProcessamento lote, CamposPesquisa campo) {
        List<ItensLoteProcessamento> list = new ArrayList<>();
        for (ItensLoteProcessamento item : lote.getItensLoteProcessamentos()) {
            if (item.getCampo().equals(campo)) {
                list.add(item);
            }
        }
        return list;
    }

    public void gerarConsultaGeral() {
        try {
            validarExecutarLote();
            String consulta = facade.gerarQueryConsulta(selecionado);
            if (consulta.contains("where ( )")) {
                return;
            }

            if (selecionado.getAno() != null && selecionado.getMes() != null) {
                Date dataInicial = DataUtil.criarDataComMesEAno(selecionado.getMes().getNumeroMes(), selecionado.getAno()).toDate();
                Date dataFinal = DataUtil.criarDataUltimoDiaMes(selecionado.getMes().getNumeroMes(), selecionado.getAno()).toDate();
                consulta += " and not exists ( " +
                    "    select 1 " +
                    "    from CedenciaContratoFP cedencia " +
                    "    where cedencia.contratoFP_id = vinculo.id " +
                    "      and cedencia.tipoContratoCedenciaFP = '" + TipoCedenciaContratoFP.SEM_ONUS.name() + "' " +
                    "      and ((to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') >= trunc(cedencia.dataCessao)  " +
                    "           and not exists (  " +
                    "                select 1 " +
                    "                from RetornoCedenciaContratoFP r " +
                    "                where r.cedenciaContratoFP_id = cedencia.id) " +
                    "                or exists (  " +
                    " select 1 from Afastamento a  " +
                    "  join tipoafastamento t on t.id = a.tipoafastamento_id " +
                    " where a.contratoFP_id = vinculo.id  " +
                    "  and t.naoCalcularFichaSemRetorno = 1  " +
                    "  and (a.retornoInformado = 0 or a.retornoInformado is null)  " +
                    "  and (  " +
                    "    (a.inicio <= to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') and (a.termino is null or a.termino >=  to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY')))  " +
                    "    or (  " +
                    "      a.inicio = (  " +
                    "        select min(a2.inicio) from Afastamento a2  " +
                    "          join tipoafastamento t2 on t2.id = a2.tipoafastamento_id " +
                    "        where a2.contratoFP_id = vinculo.id  " +
                    "          and t2.naoCalcularFichaSemRetorno = 1  " +
                    "          and (a2.retornoInformado = 0 or a2.retornoInformado is null)  " +
                    "            and a2.termino <= to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY')  " +
                    "        )  " +
                    "      ) or ( a.inicio <= to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') and (a.termino + 1 ) >= cedencia.dataCessao and  to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY') <= (select Max(r.dataRetorno) " +
                    " from RetornoCedenciaContratoFP r where r.cedenciaContratoFP_id = cedencia.id))  " +
                    " or (to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') >= cedencia.dataCessao " +
                    "          and a.termino >= ( " +
                    "              select max(r.dataRetorno) " +
                    "              from RetornoCedenciaContratoFP r " +
                    "              where r.cedenciaContratoFP_id = cedencia.id " +
                    "          ) and a.termino >= to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY')  and a.termino < cedencia.dataCessao) " +
                    "        or (a.inicio < cedencia.dataCessao and (a.inicio <= to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') and a.termino >= to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY')))" +
                    "         or (a.termino >= ( " +
                    "              select max(r.dataRetorno) " +
                    "              from RetornoCedenciaContratoFP r " +
                    "              where r.cedenciaContratoFP_id = cedencia.id  " +
                    "                ) and a.termino >= to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY') and a.inicio > cedencia.dataCessao)" +
                    "      ) " +
                    "   ))  OR (to_date('" + Util.dateToString(dataInicial) + "', 'DD/MM/YYYY') >= cedencia.dataCessao AND  to_date('" + Util.dateToString(dataFinal) + "', 'DD/MM/YYYY') <= (  " +
                    "                select Max(r.dataRetorno) " +
                    "                from RetornoCedenciaContratoFP r " +
                    "                where r.cedenciaContratoFP_id = cedencia.id " +
                    "            )) " +
                    "      )) " ;


            }
            List<Long> idsVinculo = facade.getVinculoFPFacade().findVinculosByConsultDinamicaSql(consulta);
            if (idsVinculo.isEmpty()) {
                vinculosTodos = new LazyCarDataModel(new ArrayList<VinculoFP>());
                return;
            }
            List<VinculoFP> vinculosRecuperados = Lists.newArrayList();
            for (Long id : idsVinculo) {
                vinculosRecuperados.add(facade.getVinculoFPFacade().recuperarSimples(id));
            }
            vinculosTodos = new LazyCarDataModel(vinculosRecuperados);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarExecutarLote() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItensLoteProcessamentos().isEmpty()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não existe nenhum filtro selecionado.");
        }
        ve.lancarException();
    }

    public LazyDataModel<VinculoFP> getVinculosTodos() {
        return vinculosTodos;
    }

    public void setVinculosTodos(LazyDataModel<VinculoFP> vinculosTodos) {
        this.vinculosTodos = vinculosTodos;
    }

    public List<SelectItem> getLoteProcessamentos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (LoteProcessamento object : facade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private String getNomeCampoData() {
        if (numero != null) {
            if (numero == 1) {
                return "dataExercicio";
            }
            if (numero == 2) {
                return "dataPosse";
            }
            if (numero == 3) {
                return "dataNomeacao";
            }
        }
        return "inicioVigencia";
    }

    private void removerItenComPeriodo() {
        ItensLoteProcessamento item = null;
        for (ItensLoteProcessamento i : getSelecionado().getItensLoteProcessamentos()) {
            if (i.getCampo().equals(CamposPesquisa.PERIODO)) {
                item = i;
                continue;
            }
        }
        if (item != null) {
            getSelecionado().getItensLoteProcessamentos().remove(item);
            utilizaFiltroPeriodo = Boolean.FALSE;
        }
    }

    private boolean isPodePesquisar() {
        if (objetoPesquisa == null) {
            FacesUtil.addError("Atenção", "ObjetoPesquisa não preenchido.");
            return false;
        }
        if (objetoPesquisa.getAno() == null || (objetoPesquisa.getAno() != null && String.valueOf(objetoPesquisa.getAno()).length() < 4)) {
            FacesUtil.addError("Atenção", "O ano deve ser preenchido e valido.");
            return false;
        }
        if (objetoPesquisa.getMes() == null || (objetoPesquisa.getMes() != null && String.valueOf(objetoPesquisa.getMes()).length() > 2)) {
            FacesUtil.addError("Atenção", "O mes deve ser preenchido.");
            return false;
        }
        if (objetoPesquisa.getMesAnoFa() == null || (objetoPesquisa.getMesAnoFa() != null && String.valueOf(objetoPesquisa.getMesAnoFa()).length() < 8)) {
            FacesUtil.addError("Atenção", "O campo mesAnoFa deve ser preenchido AAAA-MM-DD (ex: 20140401).");
            return false;
        }
        return true;
    }

    private void retirarDoFiltro(CamposPesquisa campo) {
        for (Iterator<ItensLoteProcessamento> it = selecionado.getItensLoteProcessamentos().iterator(); it.hasNext(); ) {
            ItensLoteProcessamento item = it.next();
            if (item.getCampo().equals(campo)) {
                it.remove();
            }
        }
    }

    private void retirarDoFiltro(CamposPesquisa campo, String tipo, String valor) {
        for (Iterator<ItensLoteProcessamento> it = getSelecionado().getItensLoteProcessamentos().iterator(); it.hasNext(); ) {
            ItensLoteProcessamento item = it.next();
            if (item.getCampo().equals(campo) && item.getTipo().equals(tipo) && item.getValor().equals(valor)) {
                it.remove();
            }
        }
    }

    public void removeVinculoDaLista(VinculoFP vinculoFP) {
        vinculoFPs.remove(vinculoFP);
        retirarDoFiltro(CamposPesquisa.MATRICULA, vinculoFP.getClass().getSimpleName(), String.valueOf(vinculoFP.getId()));
    }

    public void removerParametroCargo(Cargo cargo) {
        cargos.remove(cargo);
        retirarDoFiltro(CamposPesquisa.CARGO, cargo.getClass().getSimpleName(), String.valueOf(cargo.getId()));
    }

    public void removerParametroSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        situacaoFuncionals.remove(situacaoFuncional);
        retirarDoFiltro(CamposPesquisa.SITUACAO_FUNCIONAL, situacaoFuncional.getClass().getSimpleName(), String.valueOf(situacaoFuncional.getId()));
    }

    public void removerParametroUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        getSelecionado().getUnidadeOrganizacionals().remove(unidadeOrganizacional);
        retirarDoFiltro(CamposPesquisa.ORGAO, unidadeOrganizacional.getClass().getSimpleName(), String.valueOf(unidadeOrganizacional.getId()));
    }

    public void removerParametroRecursoFP(RecursoFP recursoFP) {
        recursosFP.remove(recursoFP);
        retirarDoFiltro(CamposPesquisa.ORGAO_RECURSOFP, recursoFP.getClass().getSimpleName(), String.valueOf(recursoFP.getId()));
    }

    public void removerParametroCategoria(CategoriaPCS categoriaPCS) {
        categoriaPCSs.remove(categoriaPCS);
        retirarDoFiltro(CamposPesquisa.CATEGORIA, categoriaPCS.getClass().getSimpleName(), String.valueOf(categoriaPCS.getId()));
    }

    public void removerParametroEventoFP(EventoFP eventoFP) {
        eventos.remove(eventoFP);
        retirarDoFiltro(CamposPesquisa.VERBA, eventoFP.getClass().getSimpleName(), String.valueOf(eventoFP.getId()));
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoFolha() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));

        }
        return toReturn;
    }

    public void setTipoFolha(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public void rotinaMigracaoEGeracaoLotesTurmalinaWebpublico() throws SQLException {
        if (isPodePesquisar()) {
            List<RegistroDB> novosRegistros = facade.getComparadorDeFollhasFacade().buscaMatriculaENumeroContratoParaRetroacaoComMaisDeUmMes(objetoPesquisa);
            if (!novosRegistros.isEmpty()) {
                log.add("gerando itens do lote para um total de " + novosRegistros.size() + " registros ");
                LoteProcessamento loteProcessamento = getLotePelaGeracaoAutomatica();
                if (loteProcessamento.getQtdeMesesRetroacao() == 0) {
                    log.add("Não é necessário criar lote para 0 meses de retroção.");
                    return;
                }
                for (RegistroDB registroDB : novosRegistros) {
                    BigDecimal matricula = (BigDecimal) registroDB.getCampoByIndex(0).getValor();
                    BigDecimal numero = (BigDecimal) registroDB.getCampoByIndex(1).getValor();
                    VinculoFP v = facade.getVinculoFPFacade().recuperarVinculoPorMatriculaENumero(matricula.intValue() + "", numero.intValue() + "");
                    gerarItemLoteProcessamento(loteProcessamento, v);

                }
                facade.salvarNovo(loteProcessamento);
            }
        }
    }

    private void gerarItemLoteProcessamento(LoteProcessamento loteProcessamento, VinculoFP v) {
        if (v != null) {
            ItensLoteProcessamento ilp = new ItensLoteProcessamento();
            ilp.setCampo(CamposPesquisa.MATRICULA);
            ilp.setTipo("ContratoFP");
            ilp.setValor(v.getId() + "");
            ilp.setLoteProcessamento(loteProcessamento);
            loteProcessamento.getItensLoteProcessamentos().add(ilp);
            log.add(v.toString());
        }
    }

    private LoteProcessamento getLotePelaGeracaoAutomatica() {
        LoteProcessamento loteProcessamento = new LoteProcessamento();
        loteProcessamento.setAno(objetoPesquisa.getAno());
        loteProcessamento.setMes(Mes.getMesToInt(objetoPesquisa.getMes()));
        loteProcessamento.setNumeroLote(123456);
        loteProcessamento.setDescricao("Lote Gerado Automaticamente " + objetoPesquisa.getAno() + " - " + loteProcessamento.getMes().getNumeroMes() + " - " + objetoPesquisa.getMesAnoFa());
        loteProcessamento.setQtdeMesesRetroacao(getQuantidadeMesesRetroativos());
        loteProcessamento.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
        return loteProcessamento;  //To change body of created methods use File | Settings | File Templates.
    }

    private Integer getQuantidadeMesesRetroativos() {
        String mesAnoFa = objetoPesquisa.getMesAnoFa();
        Integer mes = Integer.valueOf(mesAnoFa.substring(4, 6));
        Integer ano = Integer.valueOf(mesAnoFa.substring(0, 4));
        DateTime inicio = new DateTime(ano, mes, 1, 0, 0);
        DateTime termino = new DateTime(objetoPesquisa.getAno(), objetoPesquisa.getMes(), 1, 0, 0);
        Period p = new Period(inicio, termino);
        return p.getMonths();  //20140401
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().listaFiltrandoPorOrgaoAndTipoAdministrativa(parte.trim());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, facade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public void limparFiltros() {
        selecionado = new LoteProcessamento();
        inicializarListasEAtributos();
        filtrosSelecionados = Lists.newArrayList();
    }

    public List<SelectItem> getEventosFPSelectItem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP object : facade.getTipoPrevidenciaFPFacade().getEventoFPFacade().listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public class LazyCarDataModel extends LazyDataModel<VinculoFP> {

        private List<VinculoFP> datasource;

        public LazyCarDataModel(List<VinculoFP> datasource) {
            this.datasource = datasource;
        }

        @Override
        public VinculoFP getRowData(String rowKey) {
            for (VinculoFP vinculo : datasource) {
                if (vinculo.toString().equals(rowKey)) {
                    return vinculo;
                }
            }

            return null;
        }
//        @Override
//        public void setRowIndex(int rowIndex) {
////            if (rowIndex == 0) {
////                rowIndex = 1;
////            }
////            super.setRowIndex(rowIndex); //To change body of generated methods, choose Tools | Templates.
//        }

        @Override
        public Object getRowKey(VinculoFP vinculo) {
            return vinculo;
        }

        @Override
        public List<VinculoFP> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            List<VinculoFP> data = new ArrayList<>();

            //filter
            for (VinculoFP vinculos : datasource) {
                boolean match = true;

                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                    try {
                        String filterProperty = it.next();
                        String filterValue = filters.get(filterProperty);
                        String fieldValue = String.valueOf(vinculos.getClass().getField(filterProperty).get(vinculos));

                        if (filterValue == null || fieldValue.startsWith(filterValue)) {
                            match = true;
                        } else {
                            match = false;
                            break;
                        }
                    } catch (Exception e) {
                        match = false;
                    }
                }

                if (match) {
                    data.add(vinculos);
                }
            }

            //sort
//        if(sortField != null) {
//            Collections.sort(data, new LazySorter(sortField, sortOrder));
//        }

            //rowCount
            int dataSize = data.size();
            this.setRowCount(dataSize);

            //paginate
            if (dataSize > pageSize) {
                try {
                    return data.subList(first, first + pageSize);
                } catch (IndexOutOfBoundsException e) {
                    return data.subList(first, first + (dataSize % pageSize));
                }
            } else {
                return data;
            }
        }
    }
}
