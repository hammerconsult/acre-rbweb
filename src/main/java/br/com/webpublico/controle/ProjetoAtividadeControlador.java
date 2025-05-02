package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AndamentoAcao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 21/09/13
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "projetoAtividadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-projeto-atividade", pattern = "/projeto-atividade/novo/", viewId = "/faces/financeiro/ppa/projeto-atividade/edita.xhtml"),
    @URLMapping(id = "editar-projeto-atividade", pattern = "/projeto-atividade/editar/#{projetoAtividadeControlador.id}/", viewId = "/faces/financeiro/ppa/projeto-atividade/edita.xhtml"),
    @URLMapping(id = "ver-projeto-atividade", pattern = "/projeto-atividade/ver/#{projetoAtividadeControlador.id}/", viewId = "/faces/financeiro/ppa/projeto-atividade/visualizar.xhtml"),
    @URLMapping(id = "listar-projeto-atividade", pattern = "/projeto-atividade/listar/", viewId = "/faces/financeiro/ppa/projeto-atividade/lista.xhtml")
})
public class ProjetoAtividadeControlador extends PrettyControlador<AcaoPPA> implements Serializable, CRUD {
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    private ConverterGenerico converterPPA;
    private ConverterAutoComplete converterProgramaPPA;
    private ConverterAutoComplete converterFuncao;
    private ConverterAutoComplete converterSubFuncao;
    private ConverterAutoComplete converterAcaoPrincipal;
    private ConverterAutoComplete converterProdutoPPA;
    private ConverterGenerico converterPeriodicidade;
    private ConverterGenerico converterExercicio;
    private ConverterGenerico converterUnidadeMedida;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterGenerico converterTipoAcaoPPA;
    private MoneyConverter moneyConverter;
    private PPA ppaFiltro;
    private MedicaoSubAcaoPPA medicaoSubAcaoPPA;
    private ParticipanteAcaoPPA participanteAcaoPrincipal;
    private UnidadeOrganizacional unidadeOrgParticipante;
    private String filtro;
    private String filtroPaticipante;
    private String mensagem;
    private HierarquiaOrganizacional hierarquiaOrgParticipante;
    private HierarquiaOrganizacional hierarquiaOrgResponsavel;
    private SubAcaoPPA SubAcaoPPASelecionado;
    private SubAcaoPPA subAcaoPPA;
    private Boolean visibleSubAcao = false;

    private ProdutoPPA produtoPPAFiltro;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public ProjetoAtividadeControlador() {
        super(AcaoPPA.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/projeto-atividade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void setarNullAcaoPrincipal() {
        selecionado.setAcaoPrincipal(null);
    }

    @Override
    public AbstractFacade getFacede() {
        return projetoAtividadeFacade;
    }

    @URLAction(mappingId = "novo-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
        selecionado.setDataCadastro(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editar-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    public void retornaCodigoSubAcaoPPA() {
        projetoAtividadeFacade.retornaCodigoSubAcaoPPA(selecionado, subAcaoPPA);
    }

    private boolean validaHIerarquiaRecuperada(HierarquiaOrganizacional ho) {
        if (ho == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A unidade " + selecionado.getResponsavel() + " não esta dotada em nenhuma Hierarquia Organizacional no exercicio corrente " + sistemaControlador.getExercicioCorrente(), "Defina uma Hierarquia Organizacional para este exercicio!"));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void salvar() {
        if (hierarquiaOrgResponsavel != null) {
            selecionado.setResponsavel(hierarquiaOrgResponsavel.getSubordinada());
        }
        try {
            if (Util.validaCampos(selecionado)
                && validaCampos()) {
                super.salvar();
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }


    public Boolean validaCampos() {
        if (selecionado.getTotalFisico().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Físico deve ser maior ou igual a zero.");
            return false;
        }
        if (selecionado.getSomenteLeitura().equals(true)) {
            FacesUtil.addOperacaoNaoPermitida("Esta Ação PPA não pode sofrer modificações.");
            return false;
        }
        if (!projetoAtividadeFacade.hasCodigoIgualParaPrograma(selecionado, selecionado.getExercicio())) {
            FacesUtil.addOperacaoNaoPermitida("O código informado: " + selecionado.getCodigo() + ", pertence a outro Projeto/Atvidade para o mesmo Programa.");
            return false;
        }
        if (!projetoAtividadeFacade.validaCodigoIgualParaProgramaDiferente(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O código informado: " + selecionado.getCodigo() + ", pertence a outro Projeto/Atvidade cadastrado no sistema.");
            return false;
        }
        if (getSomaFinanceiraDasSubAcoes().compareTo(selecionado.getTotalFinanceiro()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Financeiro deve ser menor ou igual ao Total Financeiro da Ação.");
            return false;
        }
        if (getSomaFisicoDasSubAcoes().compareTo(selecionado.getTotalFisico()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Físico deve ser menor ou igual ao Total Físico da Ação.");
            return false;
        }
        return true;
    }

    public ConverterAutoComplete getConverterProdutoPPA() {
        if (converterProdutoPPA == null) {
            converterProdutoPPA = new ConverterAutoComplete(ProdutoPPA.class, projetoAtividadeFacade.getProdutoPPAFacade());
        }
        return converterProdutoPPA;
    }

    public ConverterAutoComplete getConverterAcaoPrincipal() {
        if (converterAcaoPrincipal == null) {
            converterAcaoPrincipal = new ConverterAutoComplete(AcaoPrincipal.class, projetoAtividadeFacade.getAcaoPrincipalFacade());
        }
        return converterAcaoPrincipal;
    }

    public List<ProdutoPPA> completaProdutoPPA(String parte) {
        if (selecionado.getAcaoPrincipal() != null) {
            return projetoAtividadeFacade.getProdutoPPAFacade().listaFiltrandoPorAcaoPrincipalPriozadaLDO(selecionado.getAcaoPrincipal(), parte.trim(), selecionado.getExercicio());
        } else {
            FacesUtil.addAtencao("Para listar o Produto PPA é necessário selecionar o campo Ação PPA.");
        }
        return new ArrayList<>();
    }

    public List<AcaoPrincipal> completarAcaoPrincipal(String parte) {
        if (selecionado.getPrograma() != null) {
            return projetoAtividadeFacade.getAcaoPrincipalFacade().buscarAcaoPPAPorProgramaPPAPriorizadaLDO(selecionado.getPrograma(), parte.trim(), selecionado.getExercicio());
        } else {
            return projetoAtividadeFacade.getAcaoPrincipalFacade().listaFiltrando(parte.trim(), "descricao");
        }
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return projetoAtividadeFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public void validaDataMedicao(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        Calendar dataMed = Calendar.getInstance();
        if (data != null && dataMed != null) {
            dataMed.setTime(data);
            Integer ano = sistemaControlador.getExercicioCorrente().getAno();
            if (dataMed.get(Calendar.YEAR) != ano) {
                message.setDetail("Ano diferente do exercício corrente!");
                message.setSummary("Ano diferente do exercício corrente!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public PPA getPpaFiltro() {
        return ppaFiltro;
    }

    public void setPpaFiltro(PPA ppaFiltro) {
        this.ppaFiltro = ppaFiltro;
    }

    public List<Funcao> completaFuncao(String parte) {
        return projetoAtividadeFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completaSubFuncao(String parte) {
        return projetoAtividadeFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }


    public void removeMedicoes(ActionEvent evt) {
        MedicaoSubAcaoPPA med = (MedicaoSubAcaoPPA) evt.getComponent().getAttributes().get("removeMedicao");
        SubAcaoPPASelecionado.getMedicoesSubAcaoPPA().remove(med);
    }

    public void salvarMedicoesPPAAprovado() {
        projetoAtividadeFacade.salvarMedicoesAcaoPPAAprovada(selecionado);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medições salvas com Sucesso!", " "));
    }

    public BigDecimal getValorFinanceiroSubAcao() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        if (SubAcaoPPASelecionado.getId() != null) {
            valor = projetoAtividadeFacade.buscaValorFinanceiroLiquidacoes(SubAcaoPPASelecionado, medicaoSubAcaoPPA.getDataMedicao());
        }
        return valor;
    }

    public Boolean validaSalvaParticipante() {
        boolean controle = true;
        if (hierarquiaOrgParticipante == null) {
            FacesUtil.addCampoObrigatorio("O campo Unidade Participante deve ser informado.");
            controle = false;
        } else if (hierarquiaOrgParticipante.equals(hierarquiaOrgResponsavel)) {
            FacesUtil.addOperacaoNaoPermitida("A Unidade Participante deve ser diferente da Unidade Responsável.");
            controle = false;
        } else {
            for (ParticipanteAcaoPPA p : selecionado.getParticipanteAcaoPPA()) {
                if (p.getUnidadeOrganizacional().equals(hierarquiaOrgParticipante.getSubordinada())) {
                    FacesUtil.addOperacaoNaoPermitida("A Unidade " + hierarquiaOrgParticipante.getSubordinada() + " ja foi adicionada.");
                    controle = false;
                }
            }
        }
        return controle;
    }

    public void salvarParticipante() {
        if (validaSalvaParticipante()) {
            unidadeOrgParticipante = hierarquiaOrgParticipante.getSubordinada();
            participanteAcaoPrincipal.setUnidadeOrganizacional(unidadeOrgParticipante);
            participanteAcaoPrincipal.setAcaoPPA(selecionado);
            (selecionado).getParticipanteAcaoPPA().add(participanteAcaoPrincipal);
            participanteAcaoPrincipal = new ParticipanteAcaoPPA();
            unidadeOrgParticipante = new UnidadeOrganizacional();
            hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        }
    }

    public List<MedicaoSubAcaoPPA> getMedicoes() {
        if (SubAcaoPPASelecionado != null) {
            return SubAcaoPPASelecionado.getMedicoesSubAcaoPPA();
        } else {
            return new ArrayList<MedicaoSubAcaoPPA>();
        }
    }

    public List<ProgramaPPA> completarProgramasPPA(String parte) {
        if (ppaFiltro != null && ppaFiltro.getId() != null) {
            return projetoAtividadeFacade.getProgramaPPAFacade().buscarProgramasPorPPAeExercicioPriorizadoLDO(parte.trim(), ppaFiltro, selecionado.getExercicio());
        }
        return projetoAtividadeFacade.getProgramaPPAFacade().listaNaoAprovados(parte.trim());
    }

    public List<SelectItem> getTipoAcao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAcaoPPA object : projetoAtividadeFacade.getTipoAcaoPPAFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getUnidadesMedidas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeMedida object : projetoAtividadeFacade.getUnidadeMedidaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Periodicidade object : projetoAtividadeFacade.getPeriodicidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getAndamentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (AndamentoAcao object : AndamentoAcao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPeriodicidade() {
        if (converterPeriodicidade == null) {
            converterPeriodicidade = new ConverterGenerico(Periodicidade.class, projetoAtividadeFacade.getPeriodicidadeFacade());
        }
        return converterPeriodicidade;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, projetoAtividadeFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterGenerico getConverterTipoAcaoPPA() {
        if (converterTipoAcaoPPA == null) {
            converterTipoAcaoPPA = new ConverterGenerico(TipoAcaoPPA.class, projetoAtividadeFacade.getTipoAcaoPPAFacade());
        }
        return converterTipoAcaoPPA;
    }

    public ConverterAutoComplete getConverterSubFuncao() {
        if (converterSubFuncao == null) {
            converterSubFuncao = new ConverterAutoComplete(SubFuncao.class, projetoAtividadeFacade.getSubFuncaoFacade());
        }
        return converterSubFuncao;
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, projetoAtividadeFacade.getFuncaoFacade());
        }
        return converterFuncao;
    }

    public ConverterAutoComplete getConverterProgramaPPA() {
        if (converterProgramaPPA == null) {
            converterProgramaPPA = new ConverterAutoComplete(ProgramaPPA.class, projetoAtividadeFacade.getProgramaPPAFacade());
        }
        return converterProgramaPPA;
    }

    public ConverterGenerico getConverterUnidadeMedida() {
        if (converterUnidadeMedida == null) {
            converterUnidadeMedida = new ConverterGenerico(UnidadeMedida.class, projetoAtividadeFacade.getUnidadeMedidaFacade());
        }
        return converterUnidadeMedida;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, projetoAtividadeFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    public BigDecimal getSomaFinanceiraDasSubAcoes() {
        BigDecimal soma = BigDecimal.ZERO;
        if (selecionado.getSubAcaoPPAs() != null) {
            for (SubAcaoPPA SubAcaoPPA1 : selecionado.getSubAcaoPPAs()) {
                if (SubAcaoPPA1.getTotalFinanceiro() != null) {
                    soma = soma.add(SubAcaoPPA1.getTotalFinanceiro());
                }
            }
        }
        return soma;
    }

    public BigDecimal getSomaFisicoDasSubAcoes() {
        BigDecimal soma = BigDecimal.ZERO;
        if (selecionado.getSubAcaoPPAs() != null) {
            for (SubAcaoPPA SubAcaoPPA1 : selecionado.getSubAcaoPPAs()) {
                if (SubAcaoPPA1.getTotalFisico() != null) {
                    soma = soma.add(SubAcaoPPA1.getTotalFisico());
                }
            }
        }
        return soma;
    }

    public void removeSubAcao(SubAcaoPPA SubAcaoPPA1) {
        selecionado.getSubAcaoPPAs().remove(SubAcaoPPA1);
        setVisibleSubAcao(true);
    }

    public void alteraSubAcao(ActionEvent evt) {
        SubAcaoPPA sub = (SubAcaoPPA) evt.getComponent().getAttributes().get("altera");
        setVisibleSubAcao(true);
        subAcaoPPA = sub;
        if (sub.getProvisaoPPA() != null) {
            produtoPPAFiltro = sub.getProvisaoPPA().getProdutoPPA();
        }
    }

    public void verificaExistencia(SubAcaoPPA sub) {
        Boolean b = (selecionado).getSubAcaoPPAs().contains(sub);
        if (b) {
            (selecionado).getSubAcaoPPAs().remove(sub);
        }
    }

    private Boolean validaAdicionaSubAcao() {
        boolean controle = true;
        if (subAcaoPPA.getCodigo() == null || subAcaoPPA.getCodigo().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Código deve ser informado.");
            controle = false;
        }
        if (subAcaoPPA.getDescricao() == null || subAcaoPPA.getDescricao().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Descrição deve ser informado");
            controle = false;
        }
        for (SubAcaoPPA subAcaoDaVez : selecionado.getSubAcaoPPAs()) {
            if (subAcaoDaVez.getCodigo().equals(subAcaoPPA.getCodigo())
                    && !subAcaoDaVez.equals(subAcaoPPA)) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um Projeto/Atividade com o código " + subAcaoDaVez.getCodigo() + " adicionada na lista.");
                controle = false;
            }
        }
        if (subAcaoPPA.getTotalFinanceiro().compareTo(selecionado.getTotalFinanceiro()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Financeiro deve ser menor ou igual ao Total Financeiro da Ação.");
            controle = false;
        }
        if (subAcaoPPA.getTotalFisico().compareTo(selecionado.getTotalFisico()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Físico deve ser menor ou igual ao Total Físico da Ação.");
            controle = false;
        }
        return controle;
    }

    public void adicionaSubAcao() {
        if (validaAdicionaSubAcao()) {
            subAcaoPPA.setAcaoPPA(selecionado);
            subAcaoPPA.setExercicio(selecionado.getExercicio());
            subAcaoPPA.setProvisaoPPA(projetoAtividadeFacade.getProvisaoPPAFacade().getProvisaoPPAPorProdutoAndExercicio(produtoPPAFiltro, selecionado.getExercicio()));
            selecionado.setSubAcaoPPAs(Util.adicionarObjetoEmLista(selecionado.getSubAcaoPPAs(), subAcaoPPA));
            subAcaoPPA = new SubAcaoPPA();
            subAcaoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
            subAcaoPPA.setMedicoesSubAcaoPPA(new ArrayList<MedicaoSubAcaoPPA>());
            produtoPPAFiltro = null;
            setVisibleSubAcao(false);
            selecionado.ordenarSubProjetoAtividade();
        }
    }


    public Boolean getSelecionaSubAcao() {
        if (SubAcaoPPASelecionado == null) {
            return false;
        } else {
            return true;
        }
    }

    public void cancelaAddSubAcao() {
        setVisibleSubAcao(false);
        subAcaoPPA = new SubAcaoPPA();
        subAcaoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        subAcaoPPA.setMedicoesSubAcaoPPA(new ArrayList<MedicaoSubAcaoPPA>());
    }

    public Boolean getVisibleSubAcao() {
        return this.visibleSubAcao;
    }

    public void setVisibleSubAcao(Boolean visibleSubAcao) {
        this.visibleSubAcao = visibleSubAcao;
    }

    public void novoSubProjetoAtividade() {
        subAcaoPPA = new SubAcaoPPA();
        subAcaoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        subAcaoPPA.setMedicoesSubAcaoPPA(new ArrayList<MedicaoSubAcaoPPA>());
        subAcaoPPA.setTotalFinanceiro(BigDecimal.ZERO);
        subAcaoPPA.setTotalFisico(BigDecimal.ZERO);
        retornaCodigoSubAcaoPPA();
        this.visibleSubAcao = true;
    }

    public List<SelectItem> getPpa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (PPA object : projetoAtividadeFacade.getPpaFacade().listaTodosPpaExericicioCombo(sistemaControlador.getExercicioCorrente())) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getVersao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPPA() {
        if (converterPPA == null) {
            converterPPA = new ConverterGenerico(PPA.class, projetoAtividadeFacade.getPpaFacade());
        }
        return converterPPA;
    }

    public Boolean verificaAprovacao() {
        if ((selecionado).getSomenteLeitura() == false) {
            return true;
        }
        return false;
    }

    public String getVerificaMascaraAcao() {
        ConfiguracaoPlanejamentoPublico cpp = projetoAtividadeFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoAcao() == null) {
            return null;
        }
        return cpp.getMascaraCodigoAcao().replaceAll("#", "9");
    }

    public String getVerificaMascaraSubAcao() {
        ConfiguracaoPlanejamentoPublico cpp = projetoAtividadeFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoSubAcao() == null) {
            return null;
        }
        return cpp.getMascaraCodigoSubAcao().replaceAll("#", "9");
    }

    public Boolean getVerificaConf() {
        if (getVerificaMascaraAcao() == null || getVerificaMascaraSubAcao() == null) {
            return true;
        }
        return false;
    }

    public boolean getPpaAtivo() {
        try {
            PPA ppa = projetoAtividadeFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
            return ppa != null;
        } catch (NullPointerException ex) {
            FacesUtil.addError("Não existe PPA para o exercício Logado", ex.getMessage());
            return false;
        } catch (ExcecaoNegocioGenerica ex) {
            return false;
        } catch (Exception ex) {
            FacesUtil.addError("Ocorreu um erro entre em contato com o Suporte!", ex.getMessage());
            return false;
        }
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Exercicio exercicio : projetoAtividadeFacade.getLoaFacade().buscarExerciciosLoa()) {
            toReturn.add(new SelectItem(exercicio, exercicio.getAno().toString()));
        }
        return toReturn;
    }

    private void parametrosIniciais() {
        medicaoSubAcaoPPA = new MedicaoSubAcaoPPA();
        participanteAcaoPrincipal = new ParticipanteAcaoPPA();
        unidadeOrgParticipante = new UnidadeOrganizacional();
        selecionado.setParticipanteAcaoPPA(new ArrayList<ParticipanteAcaoPPA>());
        selecionado.setSubAcaoPPAs(new ArrayList<SubAcaoPPA>());
        selecionado.setTotalFisico(BigDecimal.ZERO);
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setCodigo(projetoAtividadeFacade.getProximoCodigo(selecionado.getExercicio()));
        filtro = "";
        subAcaoPPA = new SubAcaoPPA();
        subAcaoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        SubAcaoPPASelecionado = null;
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        setVisibleSubAcao(false);
        atualizarPPA();
    }

    public void atualizarPPA() {
        if (selecionado.getExercicio() != null) {
            ppaFiltro = projetoAtividadeFacade.getPpaFacade().ultimoPpaDoExercicio(selecionado.getExercicio());
        }
    }

    public void recuperarEditaVer() {
        selecionado = projetoAtividadeFacade.recuperar(super.getId());
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrgResponsavel.setSubordinada(selecionado.getResponsavel());
        hierarquiaOrgResponsavel = projetoAtividadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(sistemaControlador.getDataOperacao(), selecionado.getResponsavel(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        validaHIerarquiaRecuperada(hierarquiaOrgResponsavel);
        operacao = Operacoes.EDITAR;
        unidadeOrgParticipante = new UnidadeOrganizacional();

        medicaoSubAcaoPPA = new MedicaoSubAcaoPPA();
        participanteAcaoPrincipal = new ParticipanteAcaoPPA();
        subAcaoPPA = new SubAcaoPPA();
        subAcaoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        selecionado.ordenarSubProjetoAtividade();

        SubAcaoPPASelecionado = null;

        if (selecionado.getPrograma() != null) {
            if (selecionado.getPrograma().getPpa() != null) {
                ppaFiltro = selecionado.getPrograma().getPpa();
            }
        }
        if ((selecionado).getSomenteLeitura() == true) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! Esta Ação PPA não poderá sofrer modificações.", " "));
        }
        if (getVerificaMascaraAcao() == null || getSelecionaSubAcao() == null) {
            FacesContext.getCurrentInstance().addMessage("formularioVisualizar:message", new FacesMessage(FacesMessage.SEVERITY_WARN, "Verifique a Configuração do Planejamento Público. Podem haver atributos não preenchidos.", " "));
        }
    }

    private void validarParaExcluir(){
        ValidacaoException ve = new ValidacaoException();
        for (SubAcaoPPA sa : selecionado.getSubAcaoPPAs()) {
            if (projetoAtividadeFacade.getProvisaoPPAFacade().getProvisaoPPADespesaFacade().verificarSubAcaoPPATemProvisao(sa)){
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existem valores com dependência.");
                break;
            }
        }
        ve.lancarException();
    }

    @Override
    public void excluir() {
        try{
            validarParaExcluir();
            super.excluir();
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public List<AcaoPPA> completarEstaEntidade(String parte) {
        return projetoAtividadeFacade.buscarAcoesPPAPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public SubAcaoPPA getSubAcaoPPASelecionado() {
        return SubAcaoPPASelecionado;
    }

    public void setSubAcaoPPASelecionado(SubAcaoPPA SubAcaoPPASelecionado) {
        this.SubAcaoPPASelecionado = SubAcaoPPASelecionado;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA SubAcaoPPA) {
        this.subAcaoPPA = SubAcaoPPA;
    }

    public MedicaoSubAcaoPPA getMedicaoSubAcaoPPA() {
        return medicaoSubAcaoPPA;
    }

    public void setMedicaoSubAcaoPPA(MedicaoSubAcaoPPA medicaoSubAcaoPPA) {
        this.medicaoSubAcaoPPA = medicaoSubAcaoPPA;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getFiltroPaticipante() {
        return filtroPaticipante;
    }

    public void setFiltroPaticipante(String filtroPaticipante) {
        this.filtroPaticipante = filtroPaticipante;
    }

    public UnidadeOrganizacional getUnidadeOrgParticipante() {
        return unidadeOrgParticipante;
    }

    public void setUnidadeOrgParticipante(UnidadeOrganizacional unidadeOrgParticipante) {
        this.unidadeOrgParticipante = unidadeOrgParticipante;
    }

    public HierarquiaOrganizacional getHierarquiaOrgParticipante() {
        return hierarquiaOrgParticipante;
    }

    public void setHierarquiaOrgParticipante(HierarquiaOrganizacional hierarquiaOrgParticipante) {
        this.hierarquiaOrgParticipante = hierarquiaOrgParticipante;
    }

    public HierarquiaOrganizacional getHierarquiaOrgResponsavel() {
        return hierarquiaOrgResponsavel;
    }

    public void setHierarquiaOrgResponsavel(HierarquiaOrganizacional hierarquiaOrgResponsavel) {
        this.hierarquiaOrgResponsavel = hierarquiaOrgResponsavel;
    }

    public ParticipanteAcaoPPA getParticipanteAcaoPrincipal() {
        return participanteAcaoPrincipal;
    }

    public void setParticipanteAcaoPrincipal(ParticipanteAcaoPPA participanteAcaoPrincipal) {
        this.participanteAcaoPrincipal = participanteAcaoPrincipal;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public ProdutoPPA getProdutoPPAFiltro() {
        return produtoPPAFiltro;
    }

    public void setProdutoPPAFiltro(ProdutoPPA produtoPPAFiltro) {
        this.produtoPPAFiltro = produtoPPAFiltro;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
