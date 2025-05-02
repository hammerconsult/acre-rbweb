/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@ManagedBean(name = "acaoPrincipalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoacao", pattern = "/acao-ppa/novo/", viewId = "/faces/financeiro/ppa/acao/edita.xhtml"),
    @URLMapping(id = "editaracao", pattern = "/acao-ppa/editar/#{acaoPrincipalControlador.id}/", viewId = "/faces/financeiro/ppa/acao/edita.xhtml"),
    @URLMapping(id = "veracao", pattern = "/acao-ppa/ver/#{acaoPrincipalControlador.id}/", viewId = "/faces/financeiro/ppa/acao/visualizar.xhtml"),
    @URLMapping(id = "listaracao", pattern = "/acao-ppa/listar/", viewId = "/faces/financeiro/ppa/acao/lista.xhtml")
})
public class AcaoPrincipalControlador extends PrettyControlador<AcaoPrincipal> implements Serializable, CRUD {

    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private LDOFacade ldoFacade;
    private ConverterGenerico converterAcao;
    private ConverterGenerico converterPPA;
    private PPA ppaFiltro;
    private ConverterAutoComplete converterProgramaPPA;
    private ConverterAutoComplete converterFuncao;
    private ConverterAutoComplete converterSubFuncao;
    private ConverterAutoComplete converterResp;
    private ConverterAutoComplete converterPart;
    private ConverterGenerico converterPeriodo;
    private ConverterGenerico converterPeriodicidade;
    private ConverterGenerico converterExercicio;
    private ConverterGenerico converterUnidadeMedida;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterGenerico converterItemPlanejamentoEstrategico;
    private MoneyConverter moneyConverter;
    private IndicadorProgramaPPA indicadorProgramaPPA;
    private ParticipanteAcaoPrincipal participanteAcaoPrincipal;
    private UnidadeOrganizacional unidadeOrgParticipante;
    private String filtro;
    private String filtroPaticipante;
    private BigDecimal soma;
    private String mensagem;
    private BigDecimal totalFinanceiro = new BigDecimal(BigInteger.ZERO);
    private UnidadeOrganizacional responsavelSelecionado;
    private UnidadeOrganizacional participanteSelecionado;
    private AcaoPrincipal comparador;
    private HierarquiaOrganizacional hierarquiaOrgParticipante;
    private HierarquiaOrganizacional hierarquiaOrgResponsavel;
    private HierarquiaOrganizacional hierarquiaOrgAnoRetroativo;
    private ConverterAutoComplete converterHirarquiaOrganizacional;
    private ProdutoPPA produtoPPASelecionado;
    private ProdutoPPA produtoPPA;
    private Boolean visibleSubAcao = false;
    private Boolean mostrarBotaoNovoProduto;
    private ConverterGenerico converterTipoAcaoPPA;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Boolean mostrarMedicoesProvisoesPPA;
    private ProvisaoPPA provisaoPPA;
    private Boolean mostrarEditarProvisao;

    public Boolean getMostrarEditarProvisao() {
        return mostrarEditarProvisao;
    }

    public void setMostrarEditarProvisao(Boolean mostrarEditarProvisao) {
        this.mostrarEditarProvisao = mostrarEditarProvisao;
    }

    public Boolean getMostrarMedicoesProvisoesPPA() {
        return mostrarMedicoesProvisoesPPA;
    }

    public void setMostrarMedicoesProvisoesPPA(Boolean mostrarMedicoesProvisoesPPA) {
        this.mostrarMedicoesProvisoesPPA = mostrarMedicoesProvisoesPPA;
    }

    public AcaoPrincipalControlador() {
        super(AcaoPrincipal.class);
    }

    public AcaoPrincipalFacade getFacade() {
        return acaoPrincipalFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return acaoPrincipalFacade;
    }

    public ProdutoPPA getProdutoPPASelecionado() {
        return produtoPPASelecionado;
    }

    public void setProdutoPPASelecionado(ProdutoPPA produtoPPASelecionado) {
        this.produtoPPASelecionado = produtoPPASelecionado;
    }

    public ProdutoPPA getProdutoPPA() {
        return produtoPPA;
    }

    public void setProdutoPPA(ProdutoPPA produtoPPA) {
        this.produtoPPA = produtoPPA;
    }

    public void retornaCodigo(AjaxBehaviorEvent e) {
        if (selecionado.getId() == null) {
            selecionado.setCodigo(acaoPrincipalFacade.buscarProximoCodigo(selecionado.getPrograma()));
        }
        if (selecionado.getPrograma() != null) {
            selecionado.setPrograma(acaoPrincipalFacade.getProgramaPPAFacade().recuperar(selecionado.getPrograma().getId()));
        }
        this.produtoPPASelecionado = null;
    }

    @Override
    public void validarExclusaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        for (ProdutoPPA produtoPPA : selecionado.getProdutoPPAs()) {
            List<LDO> ldos = ldoFacade.recuperarLdosComProdutoPPA(produtoPPA);
            for (LDO ldo : ldos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível remover essa Ação PPA pois o produto "
                    + produtoPPA.getDescricao() + " possui vínculo com a LDO "
                    + ldo.getExercicio().getAno() + " - " + ldo.getPpa().getDescricao());
            }
        }
        ve.lancarException();
    }

    public void retornaCodigoSubAcao() {
        Long codigoMaior = 0L;
        Long codigoSub = 0L;
        for (ProdutoPPA s : selecionado.getProdutoPPAs()) {
            codigoSub = Long.parseLong(s.getCodigo());
            if (codigoSub > codigoMaior) {
                codigoMaior = codigoSub;
            }
        }
        produtoPPA.setCodigo(String.valueOf(codigoMaior + 1));
    }

    private boolean validaHIerarquiaRecuperada(HierarquiaOrganizacional ho) {
        if (ho == null) {
            FacesUtil.addOperacaoNaoRealizada("A unidade " + selecionado.getResponsavel() + " não esta dotada em nenhuma Hierarquia Organizacional no exercicio corrente " + sistemaControlador.getExercicioCorrente() + " Defina uma Hierarquia Organizacional para este exercicio!");
            return true;
        } else {
            return false;
        }
    }

    public void preencherProvisoes(ProdutoPPA produtoPPA) {
        Calendar cinicio = new GregorianCalendar();
        Calendar cfim = new GregorianCalendar();
        if (selecionado.getPrograma() != null) {

            if (selecionado.getPrograma().getInicio() != null) {
                cinicio.setTime(selecionado.getPrograma().getInicio());
            }
            if (selecionado.getPrograma().getFim() != null) {
                cfim.setTime(selecionado.getPrograma().getFim());
            }
            try {
                validarItemPlanejamentoEstrategico();
                PlanejamentoEstrategico pe = acaoPrincipalFacade.getPlanejamentoEstrategicoFacade().recuperar(selecionado.getPrograma().getItemPlanejamentoEstrategico().getPlanejamentoEstrategico().getId());
                for (ExercicioPlanoEstrategico epe : pe.getExerciciosPlanoEstrategico()) {
                    produtoPPA.getProvisoes().add(new ProvisaoPPA(epe.getExercicio(), produtoPPA, new BigDecimal(BigInteger.ZERO), new BigDecimal(BigInteger.ZERO), new BigDecimal(BigInteger.ZERO), SistemaFacade.getDataCorrente()));
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    private void validarItemPlanejamentoEstrategico() {
        ValidacaoException ve = new ValidacaoException();
        PlanejamentoEstrategico pe = acaoPrincipalFacade.getPlanejamentoEstrategicoFacade().recuperar(selecionado.getPrograma().getItemPlanejamentoEstrategico().getPlanejamentoEstrategico().getId());
        if (pe == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O programa " + selecionado.getPrograma() + " não possui um Item Planejamento Estratégico.");
        }
        if (selecionado.getPrograma().getItemPlanejamentoEstrategico() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem Objetivos dos Eixos cadastrados!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {

        if (hierarquiaOrgResponsavel != null) {
            selecionado.setResponsavel(hierarquiaOrgResponsavel.getSubordinada());
        }
        try {
            validarAcao();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar. Detalhes do Erro: " + e.getMessage());
        }
    }

    private void validarAcao() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getSomenteLeitura()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta Ação PPA não pode sofrer modificações!");
        }
        if (!validaTotaisFinanceirosSubAcaoAoSalvar()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Verifique os Totais Financeiros das Sub-Ações desta Ação PPA. A Soma do Total Financeiro das Sub-Ações PPA dever ser igual o Valor Financeiro da Ação PPA.");
        }
        if (!validaTotaisFisicoSubAcaoAoSalvar()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Verifique os Totais Físico das Sub-Ações desta Ação PPA. A Soma do Total Físicos das Sub-Ações PPA dever ser igual o Valor Físico da Ação PPA.");
        }
        if (!validaSomaFinanceiraProvisoesAoSalvar()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Verifique os valores das Metas Financeiras nos Planos de Aplicação de cada Sub-Ação. A Soma Das Metas Financeiras do(s) Planos de Aplicação deve ser igual ao Total Financeira da Sub-Ação.");
        }
        if (selecionado.getMetaFisicaAcumulativa()) {
            if (!validaSomaFisicaProvisoesAoSalvar()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Verifique os valores das Metas Físicas  nos Planos de Aplicação de cada Sub-Ação. A Soma das Metas Físicas do(s) Planos de Aplicação deve ser igual ao Total Físico da Sub-Ação.");
            }
        }
        if (!acaoPrincipalFacade.validaCodigoIgualParaPrograma(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe o Código : " + selecionado.getCodigo() + " igual em outra Ação para o Mesmo Programa.");
        }
        if (selecionado.getTotalFisico().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Meta física não pode ser menor ou igual a zero(0)");
        }
        if (selecionado.getPrograma() != null) {
            selecionado.setPrograma(acaoPrincipalFacade.getProgramaPPAFacade().recuperar(selecionado.getPrograma().getId()));
        }
        if (selecionado.getPrograma().getPublicoAlvo().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Programa PPA selecionado não possui Público Alvo cadastrado.");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return acaoPrincipalFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public void validaDataMedicao(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        Calendar dataMed = Calendar.getInstance();
        dataMed.setTime(data);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataMed.get(Calendar.YEAR) != ano) {
            message.setDetail("Ano diferente do exercício corrente!");
            message.setSummary("Ano diferente do exercício corrente!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public Boolean validaTotaisFinanceirosSubAcaoAoSalvar() {
        BigDecimal valorSub = new BigDecimal(BigInteger.ZERO);
        for (ProdutoPPA sub : selecionado.getProdutoPPAs()) {
            valorSub = valorSub.add(sub.getTotalFinanceiro());

        }
        if (valorSub.compareTo(selecionado.getTotalFinanceiro()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaTotaisFisicoSubAcaoAoSalvar() {
        BigDecimal valorSub = new BigDecimal(BigInteger.ZERO);
        for (ProdutoPPA sub : selecionado.getProdutoPPAs()) {
            valorSub = valorSub.add(sub.getTotalFisico());

        }
        if (valorSub.compareTo(selecionado.getTotalFisico()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaSomaFinanceiraProvisoesAoSalvar() {

        boolean valido = true;
        for (ProdutoPPA produtoPPA1 : selecionado.getProdutoPPAs()) {
            soma = new BigDecimal(BigInteger.ZERO);

            for (ProvisaoPPA prov : produtoPPA1.getProvisoes()) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinanceiraCorrente()));
            }

            if (soma.compareTo(produtoPPA1.getTotalFinanceiro()) != 0) {
                valido = false;
            }
        }
        return valido;
    }

    public Boolean validaSomaFisicaProvisoesAoSalvar() {

        boolean valido = true;
        for (ProdutoPPA produtoPPA : selecionado.getProdutoPPAs()) {
            soma = new BigDecimal(BigInteger.ZERO);

            for (ProvisaoPPA prov : produtoPPA.getProvisoes()) {
                soma = soma.add(prov.getMetaFisica());
            }

            if (soma.compareTo(produtoPPA.getTotalFisico()) != 0) {
                valido = false;
            }
        }
        return valido;
    }

    public PPA getPpaFiltro() {
        return ppaFiltro;
    }

    public void setPpaFiltro(PPA ppaFiltro) {
        this.ppaFiltro = ppaFiltro;
    }

    public List<Funcao> completaFuncao(String parte) {
        return acaoPrincipalFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completaSubFuncao(String parte) {
        return acaoPrincipalFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public Boolean validaAdicionarUnidadeParticipante() {
        boolean controle = true;
        if (hierarquiaOrgParticipante == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Unidade Participante é obrigatório para adicionar.");
            controle = false;
        } else if (hierarquiaOrgParticipante.equals(hierarquiaOrgResponsavel)) {
            FacesUtil.addOperacaoNaoPermitida("A Unidade Participante deve ser diferente da Unidade Responsável.");
            controle = false;
        } else {
            for (ParticipanteAcaoPrincipal p : selecionado.getParticipanteAcaoPrincipals()) {
                if (p.getUnidadeOrganizacional().equals(hierarquiaOrgParticipante.getSubordinada())) {
                    FacesUtil.addOperacaoNaoPermitida("A Unidade Participante: " + hierarquiaOrgParticipante.getSubordinada() + " ja foi adicionada");
                    controle = false;
                }
            }
        }
        return controle;
    }

    public void adicionarUnidadeParticipante() {
        if (validaAdicionarUnidadeParticipante()) {
            unidadeOrgParticipante = hierarquiaOrgParticipante.getSubordinada();
            participanteAcaoPrincipal.setUnidadeOrganizacional(unidadeOrgParticipante);
            participanteAcaoPrincipal.setAcaoPrincipal(selecionado);
            (selecionado).getParticipanteAcaoPrincipals().add(participanteAcaoPrincipal);
            participanteAcaoPrincipal = new ParticipanteAcaoPrincipal();
            unidadeOrgParticipante = new UnidadeOrganizacional();
            hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        }
    }

    public List<ProvisaoPPA> getProvisoes() {
        return produtoPPA.getProvisoes();
    }

    public void editarProvisao(ProvisaoPPA provisaoPPA) {
        this.provisaoPPA = (ProvisaoPPA) Util.clonarObjeto(provisaoPPA);
        mostrarEditarProvisao = true;
    }

    public void recuperarMedicoesProvisoes(ProvisaoPPA prov) {
        provisaoPPA = (ProvisaoPPA) Util.clonarObjeto(prov);
        mostrarMedicoesProvisoesPPA = !prov.getMedicaoProvisaoPPAS().isEmpty();
    }

    public String emExecucao(MedicaoProvisaoPPA med) {
        try {
            validarEditarProvisaoPPA();
            return (med.getAcumulada().multiply(new BigDecimal(100))).divide(provisaoPPA.getMetaFisica(), 0, RoundingMode.HALF_UP).toString() + "%";
        }catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return "";
        }
    }

    public void adicionarProvisaoPPA() {
        try {
            validarEditarProvisaoPPA();
            Util.adicionarObjetoEmLista(produtoPPASelecionado.getProvisoes(), provisaoPPA);
            mostrarEditarProvisao = false;
            mostrarMedicoesProvisoesPPA = false;
            cancelarProvisaoPPA();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarEditarProvisaoPPA() {
        ValidacaoException ve = new ValidacaoException();
        if (provisaoPPA.getMetaFinanceiraCorrente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Meta Financeira Corrente deve ser informado");
        }
        if (provisaoPPA.getMetaFisica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Meta Físicadeve ser informado");
        }
        if (provisaoPPA.getMetaFinanceiraCapital() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Meta Financeira Capital deve ser informado");
        }
        ve.lancarException();
    }

    public void cancelarProvisaoPPA() {
        mostrarEditarProvisao = false;
        provisaoPPA = null;
    }

    public List<ProgramaPPA> completaProgramasPPA(String parte) {
        if (ppaFiltro != null) {
            if (ppaFiltro.getId() != null) {
                return acaoPrincipalFacade.getProgramaPPAFacade().listaFiltrandoProgramasPorPPA(parte.trim(), ppaFiltro);
            }
        }
        return acaoPrincipalFacade.getProgramaPPAFacade().listaNaoAprovados(parte.trim());
    }

    public List<SelectItem> getTipoAcao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAcaoPPA object : acaoPrincipalFacade.getTipoAcaoPPAFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : acaoPrincipalFacade.getExercicioFacade().lista()) {
            toReturn.add(new SelectItem(object, String.valueOf(object.getAno())));
        }
        return toReturn;
    }

    public List<SelectItem> getUnidadesMedidas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeMedida object : acaoPrincipalFacade.getUnidadeMedidaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getStatusDosProdutos() {
        return Util.getListSelectItem(StatusProduto.values(), false);
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Periodicidade object : acaoPrincipalFacade.getPeriodicidadeFacade().lista()) {
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

    public ConverterAutoComplete getConverterHirarquiaOrganizacional() {
        if (converterHirarquiaOrganizacional == null) {
            converterHirarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, acaoPrincipalFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHirarquiaOrganizacional;
    }

    public ConverterGenerico getConverterPeriodicidade() {
        if (converterPeriodicidade == null) {
            converterPeriodicidade = new ConverterGenerico(Periodicidade.class, acaoPrincipalFacade.getPeriodicidadeFacade());
        }
        return converterPeriodicidade;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, acaoPrincipalFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterGenerico getConverterTipoAcaoPPA() {
        if (converterTipoAcaoPPA == null) {
            converterTipoAcaoPPA = new ConverterGenerico(TipoAcaoPPA.class, acaoPrincipalFacade.getTipoAcaoPPAFacade());
        }
        return converterTipoAcaoPPA;
    }

    public ConverterAutoComplete getConverterSubFuncao() {
        if (converterSubFuncao == null) {
            converterSubFuncao = new ConverterAutoComplete(SubFuncao.class, acaoPrincipalFacade.getSubAcaoPPAFacade());
        }
        return converterSubFuncao;
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, acaoPrincipalFacade.getFuncaoFacade());
        }
        return converterFuncao;
    }

    public ConverterGenerico getConverterAcao() {
        if (converterAcao == null) {
            converterAcao = new ConverterGenerico(AcaoPPA.class, acaoPrincipalFacade);
        }
        return converterAcao;
    }

    public ConverterGenerico getConverterPeriodo() {
        if (converterPeriodo == null) {
            converterPeriodo = new ConverterGenerico(PeriodoFacade.class, acaoPrincipalFacade.getPeriodicidadeFacade());
        }
        return converterPeriodo;
    }

    public ConverterAutoComplete getConverterProgramaPPA() {
        if (converterProgramaPPA == null) {
            converterProgramaPPA = new ConverterAutoComplete(ProgramaPPA.class, acaoPrincipalFacade.getProgramaPPAFacade());
        }
        return converterProgramaPPA;
    }

    public ConverterGenerico getConverterUnidadeMedida() {
        if (converterUnidadeMedida == null) {
            converterUnidadeMedida = new ConverterGenerico(UnidadeMedida.class, acaoPrincipalFacade.getUnidadeMedidaFacade());
        }
        return converterUnidadeMedida;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, acaoPrincipalFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterGenerico getConverterItemPlanejamentoEstrategico() {
        if (converterItemPlanejamentoEstrategico == null) {
            converterItemPlanejamentoEstrategico = new ConverterGenerico(ItemPlanejamentoEstrategico.class, acaoPrincipalFacade.getItemPlanejamentoEstrategicoFacade());
        }
        return converterItemPlanejamentoEstrategico;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public BigDecimal getTotalFinanceiro() {
        return totalFinanceiro;
    }

    public void setTotalFinanceiro(BigDecimal totalFinanceiro) {
        this.totalFinanceiro = totalFinanceiro;
    }

    public IndicadorProgramaPPA getIndicadorProgramaPPA() {
        return indicadorProgramaPPA;
    }

    public void setIndicadorProgramaPPA(IndicadorProgramaPPA indicadorProgramaPPA) {
        this.indicadorProgramaPPA = indicadorProgramaPPA;
    }

    public ParticipanteAcaoPrincipal getParticipanteAcaoPrincipal() {
        return participanteAcaoPrincipal;
    }

    public void setParticipanteAcaoPrincipal(ParticipanteAcaoPrincipal participanteAcaoPrincipal) {
        this.participanteAcaoPrincipal = participanteAcaoPrincipal;
    }

    public BigDecimal getSoma() {
        return soma;
    }

    public void setSoma(BigDecimal soma) {
        this.soma = soma;
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

    public String getTotalSoma() {
        return soma + "";
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

    public AcaoPrincipal getComparador() {
        return comparador;
    }

    public void setComparador(AcaoPrincipal comparador) {
        this.comparador = comparador;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getResto() {
        if (selecionado.getTotalFinanceiro() != null) {
            return selecionado.getTotalFinanceiro().subtract(soma) + "";
        } else {
            return "0";
        }
    }

    public Boolean validaSomaFinanceiraProvisoes() {
        if (produtoPPASelecionado != null) {
            soma = new BigDecimal(BigInteger.ZERO);
            for (ProvisaoPPA prov : produtoPPASelecionado.getProvisoes()) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinanceiraCorrente()));
            }

            if (soma.compareTo(produtoPPASelecionado.getTotalFinanceiro()) != 0) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaSomaFisicaProvisoes() {
        if (produtoPPASelecionado != null) {
            soma = new BigDecimal(BigInteger.ZERO);
            for (ProvisaoPPA prov : produtoPPASelecionado.getProvisoes()) {
                soma = soma.add(prov.getMetaFisica());
            }

            if (produtoPPASelecionado.getTotalFisico().compareTo(BigDecimal.ZERO) != 0) {
                if (soma.compareTo(produtoPPASelecionado.getTotalFisico()) < 0) {
                    return true;
                } else if (soma.compareTo(produtoPPASelecionado.getTotalFisico()) == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public BigDecimal getSomaFinanceiraDasSubAcoes() {
        BigDecimal soma = BigDecimal.ZERO;
        if (selecionado.getProdutoPPAs() != null) {
            for (ProdutoPPA produtoPPA1 : selecionado.getProdutoPPAs()) {
                if (produtoPPA1.getTotalFinanceiro() != null) {
                    soma = soma.add(produtoPPA1.getTotalFinanceiro());
                }
            }
        }
        return soma;
    }

    public BigDecimal getSomaFisicoDasSubAcoes() {
        BigDecimal soma = BigDecimal.ZERO;
        if (selecionado.getProdutoPPAs() != null) {
            for (ProdutoPPA produtoPPA1 : selecionado.getProdutoPPAs()) {
                if (produtoPPA1.getTotalFisico() != null) {
                    soma = soma.add(produtoPPA1.getTotalFisico());
                }
            }
        }
        return soma;
    }

    public BigDecimal getSomaFinanceiraProvisao() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        if (produtoPPASelecionado != null) {
            for (ProvisaoPPA prov : produtoPPASelecionado.getProvisoes()) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinanceiraCorrente()));
            }
        }
        return soma;
    }

    public BigDecimal getSomaFisicaProvisao() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        try {
            if (provisaoPPA != null) {
                validarEditarProvisaoPPA();
            }
            if (selecionado.getMetaFisicaAcumulativa()) {
                if (produtoPPASelecionado != null) {
                    for (ProvisaoPPA prov : produtoPPASelecionado.getProvisoes()) {
                        soma = soma.add(prov.getMetaFisica());
                    }
                }
            } else {
                soma = selecionado.getTotalFisico();
            }
            return soma;
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return soma;
        }
    }

    public void removerProvisao(ActionEvent evento) {
        ProvisaoPPA provisao = (ProvisaoPPA) evento.getComponent().getAttributes().get("removeProvisoes");
        produtoPPA.getProvisoes().remove(provisao);
    }

    public UnidadeOrganizacional getResponsavelSelecionado() {
        return responsavelSelecionado;
    }

    public void setResponsavelSelecionado(UnidadeOrganizacional responsavelSelecionado) {
        this.responsavelSelecionado = responsavelSelecionado;
    }

    public UnidadeOrganizacional getParticipanteSelecionado() {
        return participanteSelecionado;
    }

    public void setParticipanteSelecionado(UnidadeOrganizacional participanteSelecionado) {
        this.participanteSelecionado = participanteSelecionado;
    }

    public List<UnidadeOrganizacional> completaResponsavel(String parte) {
        return acaoPrincipalFacade.getUnidadeOrganizacionalFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterResp() {
        if (converterResp == null) {
            converterResp = new ConverterAutoComplete(UnidadeOrganizacional.class, acaoPrincipalFacade.getUnidadeOrganizacionalFacade());
        }
        return converterResp;
    }

    public ConverterAutoComplete getConverterPart() {
        if (converterPart == null) {
            converterPart = new ConverterAutoComplete(UnidadeOrganizacional.class, acaoPrincipalFacade.getUnidadeOrganizacionalFacade());
        }
        return converterPart;
    }

    public void setaParticipante() {
        if (participanteSelecionado != null) {
            participanteAcaoPrincipal.setUnidadeOrganizacional(participanteSelecionado);
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:participante", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione uma Unidade Participante"));

        }
    }

    public void removeSubAcao(ProdutoPPA produtoPPA1) {
        selecionado.getProdutoPPAs().remove(produtoPPA1);
        setVisibleSubAcao(true);
    }

    public void alteraSubAcao(ActionEvent evt) {
        ProdutoPPA sub = (ProdutoPPA) evt.getComponent().getAttributes().get("altera");
        setVisibleSubAcao(true);
        produtoPPA = sub;
    }

    public void verificaExistencia(ProdutoPPA sub) {
        Boolean b = (selecionado).getProdutoPPAs().contains(sub);
        if (b) {
            (selecionado).getProdutoPPAs().remove(sub);
        }
    }

    public Boolean validaAdicionaSubAcao() {
        boolean controle = true;
        if (produtoPPA.getCodigo() == null || produtoPPA.getCodigo().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Código deve ser informado.");
            controle = false;
        }
        if (produtoPPA.getDescricao() == null || produtoPPA.getDescricao().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Descrição deve ser informado.");
            controle = false;
        }
        if (produtoPPA.getTotalFinanceiro().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Financeiro deve ser maior que zero(0).");
            controle = false;
        }
        if (produtoPPA.getTotalFisico().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Físico deve ser maior que zero(0).");
            controle = false;
        }
        if (produtoPPA.getTotalFinanceiro().compareTo(selecionado.getTotalFinanceiro()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Financeiro deve ser menor ou igual que o Total Financeiro da Ação.");
            controle = false;
        }
        if (produtoPPA.getTotalFisico().compareTo(selecionado.getTotalFisico()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo Total Físico deve ser menor ou igual que o Total Físico da Ação.");
            controle = false;
        }
        return controle;
    }

    public void adicionaSubAcao() {
        if (validaAdicionaSubAcao()) {
            if (validaCodigoRepetidoSubAcao()) {
                produtoPPA.setAcaoPrincipal(selecionado);
                produtoPPA.setExercicio(selecionado.getExercicio());
                if (produtoPPA.getProvisoes().isEmpty()) {
                    preencherProvisoes(produtoPPA);
                }
                selecionado.setProdutoPPAs(Util.adicionarObjetoEmLista(selecionado.getProdutoPPAs(), produtoPPA));
                produtoPPA = new ProdutoPPA();
                produtoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
                produtoPPA.setProvisoes(new ArrayList<ProvisaoPPA>());
                selecionado.ordernarProdutoPPA();
                setVisibleSubAcao(false);
                this.mostrarBotaoNovoProduto = true;
            }
        }

    }

    public void ordernarProdutoPPA() {
        Collections.sort(selecionado.getProdutoPPAs(), new Comparator<ProdutoPPA>() {
            @Override
            public int compare(ProdutoPPA o1, ProdutoPPA o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }

    private boolean validaCodigoRepetidoSubAcao() {
        for (ProdutoPPA subAcaoDaVez : selecionado.getProdutoPPAs()) {
            if (subAcaoDaVez.getCodigo().equals(produtoPPA.getCodigo()) && !subAcaoDaVez.equals(produtoPPA)) {
                FacesUtil.addError("Não foi possível adicionar a Sub-Ação", "Já existe uma Sub-Ação com o código " + subAcaoDaVez.getCodigo() + " adicionada na lista.");
                return false;
            }
        }
        return true;
    }

    public Boolean getSelecionaSubAcao() {
        if (produtoPPASelecionado == null) {
            return false;
        } else {
            validaSomaFinanceiraProvisoes();
            validaSomaFisicaProvisoes();
            return true;
        }
    }

    public void cancelaAddSubAcao() {
        setVisibleSubAcao(false);
        produtoPPA = new ProdutoPPA();
        produtoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        mostrarMedicoesProvisoesPPA = false;
        produtoPPA.setProvisoes(new ArrayList<ProvisaoPPA>());
        this.mostrarBotaoNovoProduto = true;
    }

    public Boolean getVisibleSubAcao() {
        return this.visibleSubAcao;
    }

    public void setVisibleSubAcao(Boolean visibleSubAcao) {
        this.visibleSubAcao = visibleSubAcao;
    }

    public void visibleSubAcaoTrue() {
        produtoPPA = new ProdutoPPA();
        produtoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        mostrarMedicoesProvisoesPPA = false;
        produtoPPA.setProvisoes(new ArrayList<ProvisaoPPA>());
        produtoPPA.setTotalFinanceiro(BigDecimal.ZERO);
        produtoPPA.setTotalFisico(BigDecimal.ZERO);
        retornaCodigoSubAcao();
        this.visibleSubAcao = true;
        this.mostrarBotaoNovoProduto = false;
    }

    public void desatribuirprodutoPPASelecionado() {
        mostrarMedicoesProvisoesPPA = false;
        this.produtoPPASelecionado = null;
    }

    public void atribuirprodutoPPASelecionado(ProdutoPPA produtoPPA1) {
        this.produtoPPASelecionado = (ProdutoPPA) Util.clonarObjeto(produtoPPA1);
        if (produtoPPASelecionado.getProvisoes().isEmpty()) {
            preencherProvisoes(produtoPPASelecionado);
        }
    }

    public List<SelectItem> getPpa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PPA object : acaoPrincipalFacade.getPpaFacade().listaTodosPpaExericicioCombo(sistemaControlador.getExercicioCorrente())) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getVersao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPPA() {
        if (converterPPA == null) {
            converterPPA = new ConverterGenerico(PPA.class, acaoPrincipalFacade.getPpaFacade());
        }
        return converterPPA;
    }

    public Boolean verificaAprovacao() {
        if (selecionado != null) {
            if ((selecionado).getSomenteLeitura() == false) {
                return true;
            }
        }
        return false;
    }

    public String getVerificaMascaraAcao() {
        ConfiguracaoPlanejamentoPublico cpp = acaoPrincipalFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoAcao() == null) {
            return null;
        }
        return cpp.getMascaraCodigoAcao().replaceAll("#", "9");
    }

    public String getVerificaMascaraSubAcao() {
        ConfiguracaoPlanejamentoPublico cpp = acaoPrincipalFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Boolean getMostrarBotaoNovoProduto() {
        return mostrarBotaoNovoProduto;
    }

    public void setMostrarBotaoNovoProduto(Boolean mostrarBotaoNovoProduto) {
        this.mostrarBotaoNovoProduto = mostrarBotaoNovoProduto;
    }

    public boolean getPpaAtivo() {
        try {
            PPA ppa = acaoPrincipalFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
            return ppa != null;
        } catch (NullPointerException ex) {
            FacesUtil.addError("Não existe PPA para o exercicio Logado", ex.getMessage());
            return false;
        } catch (ExcecaoNegocioGenerica ex) {
            return false;
        } catch (Exception ex) {
            FacesUtil.addError("Ocorreu um erro entre em contato com o Suporte!", ex.getMessage());
            return false;
        }
    }

    @URLAction(mappingId = "novoacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(acaoPrincipalFacade.getProximoCodigo());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        indicadorProgramaPPA = new IndicadorProgramaPPA();
        participanteAcaoPrincipal = new ParticipanteAcaoPrincipal();
        unidadeOrgParticipante = new UnidadeOrganizacional();
        selecionado.setParticipanteAcaoPrincipals(new ArrayList<ParticipanteAcaoPrincipal>());
        mostrarMedicoesProvisoesPPA = false;
        selecionado.setProdutoPPAs(new ArrayList<ProdutoPPA>());
        selecionado.setTotalFisico(BigDecimal.ZERO);
        mostrarBotaoNovoProduto = true;
        filtro = "";
        soma = new BigDecimal(BigInteger.ZERO);
        responsavelSelecionado = new UnidadeOrganizacional();
        participanteSelecionado = new UnidadeOrganizacional();
        produtoPPA = new ProdutoPPA();
        produtoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        produtoPPA.setProvisoes(new ArrayList<ProvisaoPPA>());
        produtoPPASelecionado = null;
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        setVisibleSubAcao(false);
        ppaFiltro = acaoPrincipalFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
        provisaoPPA = null;
        mostrarEditarProvisao = false;
    }

    @URLAction(mappingId = "veracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editaracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    public void recuperarEditaVer() {
        selecionado = acaoPrincipalFacade.recuperar(super.getId());
        mostrarMedicoesProvisoesPPA = false;
        provisaoPPA = null;
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        hierarquiaOrgAnoRetroativo = new HierarquiaOrganizacional();
        hierarquiaOrgResponsavel.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrgResponsavel.setSubordinada(selecionado.getResponsavel());
        hierarquiaOrgResponsavel = acaoPrincipalFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(sistemaControlador.getDataOperacao(), selecionado.getResponsavel(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrgAnoRetroativo = acaoPrincipalFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataRegistro(), selecionado.getResponsavel(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        validaHIerarquiaRecuperada(hierarquiaOrgResponsavel);
        responsavelSelecionado = new UnidadeOrganizacional();
        participanteSelecionado = new UnidadeOrganizacional();
        operacao = Operacoes.EDITAR;
        unidadeOrgParticipante = new UnidadeOrganizacional();
        indicadorProgramaPPA = new IndicadorProgramaPPA();
        participanteAcaoPrincipal = new ParticipanteAcaoPrincipal();
        soma = new BigDecimal(BigInteger.ZERO);
        comparador = selecionado;
        produtoPPA = new ProdutoPPA();
        produtoPPA.setDataRegistro(SistemaFacade.getDataCorrente());
        produtoPPA.setProvisoes(new ArrayList<ProvisaoPPA>());
        produtoPPASelecionado = null;
        mostrarEditarProvisao = false;

        if (selecionado.getPrograma() != null) {
            if (selecionado.getPrograma().getPpa() != null) {
                ppaFiltro = selecionado.getPrograma().getPpa();
            }
        }
        selecionado.ordernarProdutoPPA();
        if ((selecionado).getSomenteLeitura() == true) {
            FacesUtil.addOperacaoNaoPermitida(" Esta Ação PPA não poderá sofrer modificações.");
        }
        if (getVerificaMascaraAcao() == null || getSelecionaSubAcao() == null) {
            FacesUtil.addOperacaoNaoPermitida("Verifique a Configuração do Planejamento Público. Podem haver atributos não preenchidos.");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/acao-ppa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public HierarquiaOrganizacional getHierarquiaOrgAnoRetroativo() {
        return hierarquiaOrgAnoRetroativo;
    }

    public void setHierarquiaOrgAnoRetroativo(HierarquiaOrganizacional hierarquiaOrgAnoRetroativo) {
        this.hierarquiaOrgAnoRetroativo = hierarquiaOrgAnoRetroativo;
    }

    public ProvisaoPPA getProvisaoPPA() {
        return provisaoPPA;
    }

    public void setProvisaoPPA(ProvisaoPPA provisaoPPA) {
        this.provisaoPPA = provisaoPPA;
    }

    public List<SelectItem> buscarOrigensAcoes() {
        return Util.getListSelectItem(OrigemAcao.values());
    }

    public boolean isEmendaModificativa() {
        return OrigemAcao.PODER_LEGISLATIVO_EMENDA_MODIFICATIVA.equals(selecionado.getOrigemAcao());
    }

    public void limparCamposSeNaoForModificativa() {
        if (!isEmendaModificativa()) {
            selecionado.setModificaProduto(null);
            selecionado.setModificaMetaFisica(null);
        }
    }
}
