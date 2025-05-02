/*
 * Codigo gerado automaticamente em Fri Apr 01 09:51:01 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.usertype.ParticipanteProgramaPPA;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-programa-ppa", pattern = "/programa-ppa/novo/", viewId = "/faces/financeiro/ppa/programappa/edita.xhtml"),
    @URLMapping(id = "edita-programa-ppa", pattern = "/programa-ppa/editar/#{programaPPAControlador.id}/", viewId = "/faces/financeiro/ppa/programappa/edita.xhtml"),
    @URLMapping(id = "ver-programa-ppa", pattern = "/programa-ppa/ver/#{programaPPAControlador.id}/", viewId = "/faces/financeiro/ppa/programappa/visualizar.xhtml"),
    @URLMapping(id = "listar-programa-ppa", pattern = "/programa-ppa/listar/", viewId = "/faces/financeiro/ppa/programappa/lista.xhtml")
})
public class ProgramaPPAControlador extends PrettyControlador<ProgramaPPA> implements Serializable, CRUD {

    protected ConverterAutoComplete converterPpa;
    protected ConverterGenerico converterPpaGenerico;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    private ConverterAutoComplete converterEixo;
    private ConverterAutoComplete converterIndicador;
    private ConverterAutoComplete converterValorIndicador;
    private ConverterGenerico converterPeriodicidade;
    private ConverterGenerico converterItem;
    private ConverterGenerico converterPublicoAlvo;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterAcao;

    private List<IndicadorProgramaPPA> indicadorProgramas;
    private List<ValorIndicador> valorIndicadores;
    private IndicadorProgramaPPA indicadorPrograma;
    private IndicadorProgramaPPA indicadorProgramaAux;
    private PublicoAlvoProgramaPPA publicoAlvoProgramaPPA;
    private AcaoPrincipal acaoPrincipal;
    private String filtro;
    private List<AcaoPrincipal> listaExcluir;
    private List<ProgramaPPA> programas;
    private Date dtInicial, dtFinal;
    private Long numeroSelecionado;
    private PPA ppaFiltro;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    private UnidadeOrganizacional unidadeOrgParticipante;
    private HierarquiaOrganizacional hierarquiaOrgParticipante;
    private ParticipanteProgramaPPA participanteProgramaPPA;

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public ProgramaPPAControlador() {
        super(ProgramaPPA.class);
    }

    public void visualizaEditaVer() {
        try {
            boolean isNovoOrIsEditarPppIsNull = ((operacao.equals(Operacoes.NOVO)) || (operacao.equals(Operacoes.EDITAR) && selecionado.getPpa() == null));

            if (isNovoOrIsEditarPppIsNull) {
                selecionado.setPpa(programaPPAFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente()));
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Erro: ", ex.getMessage()));
        } finally {
            participanteProgramaPPA = new ParticipanteProgramaPPA();
            verificaInstanciaValorIndicador();
            verificaInstanciaDeIndicador();
            indicadorProgramaAux = new IndicadorProgramaPPA();
            publicoAlvoProgramaPPA = new PublicoAlvoProgramaPPA();
            acaoPrincipal = new AcaoPrincipal();
            listaExcluir = new ArrayList<AcaoPrincipal>();
            verificaInstanciaDataIniciaisFinais();
            verificaInstanciaDeHierarquia();
            verificaInstanciaListaDeIndicador();
            removeSelecionadosDeSessao();
            selecionado.ordernarAcaoPrincipal();
        }
    }

    private void verificaInstanciaValorIndicador() {
        valorIndicadores = (List<ValorIndicador>) findObjectInSession("valorIndicadores");
        if (valorIndicadores == null) {
            valorIndicadores = new ArrayList<>();
        }
    }

    private void verificaInstanciaListaDeIndicador() {
        this.indicadorProgramas = (List<IndicadorProgramaPPA>) findObjectInSession("indicadorProgramas");
        if (this.indicadorProgramas == null) {
            this.indicadorProgramas = new ArrayList<IndicadorProgramaPPA>();
        }
    }

    private void verificaInstanciaDeIndicador() {
        this.indicadorPrograma = (IndicadorProgramaPPA) findObjectInSession("indicadorPrograma");
        if (this.indicadorPrograma == null) {
            this.indicadorPrograma = new IndicadorProgramaPPA();
        }
    }

    private void verificaInstanciaDataIniciaisFinais() {
        dtFinal = (Date) findObjectInSession("dtFinal");
        if (dtFinal == null) {
            dtFinal = new Date();
        }
        dtInicial = (Date) findObjectInSession("dtInicial");
        if (dtInicial == null) {
            dtInicial = new Date();
        }
    }

    private void verificaInstanciaDeHierarquia() {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) findObjectInSession("hierarquiaOrganizacional");
        if (hierarquiaOrganizacional == null) {
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programa-ppa/";
    }

    @URLAction(mappingId = "ver-programa-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void visualiza() {
        operacao = Operacoes.VER;
        selecionado = programaPPAFacade.recuperar(super.getId());
        visualizaEditaVer();
        hierarquiaOrganizacional.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrganizacional = programaPPAFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getResponsavel(), hierarquiaOrganizacional, sistemaControlador.getExercicioCorrente());
    }

    @URLAction(mappingId = "novo-programa-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            participanteProgramaPPA = new ParticipanteProgramaPPA();
            selecionado.setParticipantesProgramaPPA(new ArrayList<ParticipanteProgramaPPA>());
            selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
            selecionado.setDataCadastro(sistemaControlador.getDataOperacao());
            visualizaEditaVer();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addWarn("Atenção.", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addWarn("Atenção.", e.getMessage());
        }
    }

    @URLAction(mappingId = "edita-programa-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        try {
            super.ver();
            visualizaEditaVer();
            hierarquiaOrganizacional.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
            hierarquiaOrganizacional = programaPPAFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getResponsavel(), hierarquiaOrganizacional, sistemaControlador.getExercicioCorrente());
            if (selecionado.getSomenteLeitura()) {
                FacesUtil.addAtencao("Este Programa PPA não poderá sofrer modificações.");
            }
            if (getVerificaMascara() == null) {
                FacesUtil.addAtencao("Verifique a Configuração do Planejamento Público. Podem haver atributos não preenchidos.");
            }
        } catch (Exception e) {
        }
    }

    private void validarPrograma() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicio() != null || selecionado.getFim() != null) {
            if (selecionado.getInicio().after(selecionado.getFim())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial é maior que a data final.");
            }
        }
        if (programaPPAFacade.verificaExistenciaProgramaPPAPorCodigo(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Programa PPA cadastrado com o código: " + selecionado.getCodigo() + ".");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            if (selecionado.getSomenteLeitura()) {
                FacesUtil.addError("Programa PPA aprovado!", "Não pode sofrer modificações.");
            } else {
                validarPrograma();
                if (hierarquiaOrganizacional != null) {
                    selecionado.setResponsavel(hierarquiaOrganizacional.getSubordinada());
                }
                for (AcaoPrincipal a : listaExcluir) {
                    a.setPrograma(null);
                    programaPPAFacade.getAcaoPpaFacade().salvar(a);
                }
                if (selecionado.getPublicoAlvo().isEmpty()) {
                    FacesUtil.addWarn("ATENÇÃO: ", "Para finalizar o cadastro desse programa realize sua vinculação na tela do 'Público Alvo'.");
                }
                super.salvar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro, entre em contato com o Suporte", e.getMessage());
        }
    }

    private boolean validaNovoIndicador(IndicadorProgramaPPA indicador) {
        try {
            if (valorIndicadores.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Para adicionar é necessário informar Valores para os Indicadores na lista acima.");
            }
            if (indicador.getIndicador() == null
                || indicador.getValorReferencia() == null) {
                throw new ExcecaoNegocioGenerica("Para adicionar selecione um indicador na lista acima.");
            }
            if (indicador.getPeriodicidade() == null) {
                throw new ExcecaoNegocioGenerica("O Campo Periodicidade de Avaliação deve ser informado.");
            }
            if (indicador.getValorDesejado() == null) {
                throw new ExcecaoNegocioGenerica("O Campo Valor Desejado deve ser informado.");
            }
            for (IndicadorProgramaPPA indicadorProgramaPPA : selecionado.getIndicadores()) {
                if (indicadorProgramaPPA.getValorReferencia().getId().equals(indicador.getValorReferencia().getId())
                    && indicadorProgramaPPA.getIndicador().getId().equals(indicador.getIndicador().getId())
                    && !indicadorProgramaPPA.equals(indicador)) {
                    throw new ExcecaoNegocioGenerica("O Indicador: " + indicador.getIndicador() + " com o " + indicador.getValorReferencia() + " já foi utilizado.");
                }
            }
            return true;
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            return false;
        }
    }

    public void novoIndicador() {
        if (validaNovoIndicador(indicadorPrograma)) {
            indicadorPrograma.setPrograma(selecionado);
            Indicador indicador = indicadorPrograma.getIndicador();
            ValorIndicador valorReferencia = indicadorPrograma.getValorReferencia();
            selecionado.setIndicadores(Util.adicionarObjetoEmLista(selecionado.getIndicadores(), indicadorPrograma));
            indicadorPrograma = new IndicadorProgramaPPA();
            indicadorPrograma.setIndicador(indicador);
            indicadorPrograma.setValorReferencia(valorReferencia);
        }
    }

    private void validarNovaAcaoPpa() {
        ValidacaoException ve = new ValidacaoException();
        if (acaoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Ação PPA deve ser informado.");
        }
        if (selecionado.getPublicoAlvo().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessario primeiro adicionar Públicos Alvos no Programa PPA para poder adicionar uma Ação PPA.");
        }
        for (AcaoPrincipal ac : selecionado.getAcoes()) {
            if (ac.equals(acaoPrincipal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Ação: " + ac + " Já foi adicionada na lista.");
            }
            if (ac.getCodigo().trim().equals(acaoPrincipal.getCodigo().trim())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código da Ação já foi adiconado neste Programa.");
            }
        }
        ve.lancarException();
    }


    public void adicionarAcaoPpa() {
        try {
            validarNovaAcaoPpa();
            acaoPrincipal.setPrograma(selecionado);
            selecionado.getAcoes().add(acaoPrincipal);
            selecionado.ordernarAcaoPrincipal();
            acaoPrincipal = new AcaoPrincipal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void ordernarAcaoPrincipal() {
        Collections.sort(selecionado.getAcoes(), new Comparator<AcaoPrincipal>() {
            @Override
            public int compare(AcaoPrincipal o1, AcaoPrincipal o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }

    private boolean validanovoPublicoAlvo(PublicoAlvoProgramaPPA p) {
        if (p.getPublicoAlvo() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Público Alvo dever ser informado.");
            return false;
        }
        for (PublicoAlvoProgramaPPA pap : selecionado.getPublicoAlvo()) {
            if (p.getPublicoAlvo().equals(pap.getPublicoAlvo())) {
                FacesUtil.addOperacaoNaoPermitida("O Público Alvo: " + p.getPublicoAlvo() + " já foi adicionado na lista.");
                return false;
            }
        }
        return true;
    }

    public void adicionarPublicoAlvo() {

        if (validanovoPublicoAlvo(publicoAlvoProgramaPPA)) {
            publicoAlvoProgramaPPA.setProgramaPPA(selecionado);
            selecionado.getPublicoAlvo().add(publicoAlvoProgramaPPA);
        }
        publicoAlvoProgramaPPA = new PublicoAlvoProgramaPPA();
    }

    public void removeIndicador(ActionEvent evento) {
        selecionado.getIndicadores().remove((IndicadorProgramaPPA) evento.getComponent().getAttributes().get("removeIndicador"));
    }

    public void removePublicoAlvo(ActionEvent evento) {
        selecionado.getPublicoAlvo().remove((PublicoAlvoProgramaPPA) evento.getComponent().getAttributes().get("removePublicoAlvo"));
    }

    public void removeAcao(ActionEvent evento) {
        AcaoPrincipal a = (AcaoPrincipal) evento.getComponent().getAttributes().get("removeAcao");
        listaExcluir.add(a);
        selecionado.getAcoes().remove(a);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<IndicadorProgramaPPA> getIndicadores() {
        try {
            return selecionado.getIndicadores();
        } catch (NullPointerException e) {
            FacesUtil.addError("Problema ao recuperar Indicadores ", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Problema ao recuperar Indicadores, entre em contato com o Suporte! ", e.getMessage());
        }

        return new ArrayList<>();
    }

    public List<PublicoAlvoProgramaPPA> getPublicoAlvos() {
        try {
            return selecionado.getPublicoAlvo();
        } catch (NullPointerException e) {
            FacesUtil.addError("Problema ao recuperar Publicos Alvos ", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Problema ao recuperar Publicos Alvos, entre em contato com o Suporte! ", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<AcaoPrincipal> getAcoes() {
        try {
            ordenarCodigoAcao(selecionado.getAcoes());
            return selecionado.getAcoes();
        } catch (NullPointerException e) {
            FacesUtil.addError("Problema ao recuperar Ações, entre em contato com o Suporte!  ", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Problema ao recuperar Ações, entre em contato com o Suporte! ", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void validarExclusaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getAcoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido remover o Programa pois possui Ações vinculadas. Por favor, remova as Ações e tente novamente.");
        }
        ve.lancarException();
    }

    public List<ValorIndicador> getValorIndicadores() {
        return valorIndicadores;
    }

    public void setValorIndicadores(List<ValorIndicador> valorIndicadores) {
        this.valorIndicadores = valorIndicadores;
    }

    public void pesquisarAdicionarValores() throws ExcecaoNegocioGenerica {
        try {
            if (indicadorPrograma.getIndicador() == null) {
                throw new ExcecaoNegocioGenerica("O campo Indicador deve ser informado.");
            }
            if (dtInicial == null) {
                throw new ExcecaoNegocioGenerica("O campo Data Inicial deve ser informado.");
            }
            if (dtFinal == null) {
                throw new ExcecaoNegocioGenerica("O campo Data Final deve ser informado.");
            }
            if (dtInicial.after(dtFinal)) {
                throw new ExcecaoNegocioGenerica("A Data Inicial é maior que a Data Final.");
            }
            valorIndicadores = programaPPAFacade.getValorIndicadorFacade().recuperaPorIndicador(indicadorPrograma.getIndicador(), dtInicial, dtFinal);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void alteraIndicador(ActionEvent evento) {
        this.indicadorPrograma = (IndicadorProgramaPPA) evento.getComponent().getAttributes().get("alteraIndicador");
        valorIndicadores = new ArrayList<ValorIndicador>();
        valorIndicadores.add(this.indicadorPrograma.getValorReferencia());
    }

    public void confirmaAlteracaoIndicador() {
        Integer i = selecionado.getIndicadores().indexOf(indicadorPrograma);
        selecionado.getIndicadores().set(i, indicadorPrograma);
        indicadorPrograma = new IndicadorProgramaPPA();
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public IndicadorProgramaPPA getIndicadorPrograma() {
        return indicadorPrograma;
    }

    public void setIndicadorPrograma(IndicadorProgramaPPA indicadorPrograma) {
        this.indicadorPrograma = indicadorPrograma;
    }

    public IndicadorProgramaPPA getIndicadorProgramaAux() {
        return indicadorProgramaAux;
    }

    public void setIndicadorProgramaAux(IndicadorProgramaPPA indicadorProgramaAux) {
        this.indicadorProgramaAux = indicadorProgramaAux;
    }

    public List<IndicadorProgramaPPA> getIndicadorProgramas() {
        return indicadorProgramas;
    }

    public void setIndicadorProgramas(List<IndicadorProgramaPPA> indicadorProgramas) {
        this.indicadorProgramas = indicadorProgramas;
    }

    public PublicoAlvoProgramaPPA getPublicoAlvoProgramaPPA() {
        return publicoAlvoProgramaPPA;
    }

    public void setPublicoAlvoProgramaPPA(PublicoAlvoProgramaPPA publicoAlvoProgramaPPA) {
        this.publicoAlvoProgramaPPA = publicoAlvoProgramaPPA;
    }

    public ProgramaPPAFacade getFacade() {
        return programaPPAFacade;
    }

    public PPA getPpaFiltro() {
        return ppaFiltro;
    }

    public void setPpaFiltro(PPA ppaFiltro) {
        this.ppaFiltro = ppaFiltro;
    }

    public List<ProgramaPPA> getProgramas() {
        return programas;
    }

    public void setProgramas(List<ProgramaPPA> programas) {
        this.programas = programas;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    @Override
    public AbstractFacade getFacede() {
        return programaPPAFacade;
    }

    public List<SelectItem> getPpa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PPA object : programaPPAFacade.getPpaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getVersao()));
        }
        return toReturn;
    }

    public List<SelectItem> getHorizonte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (HorizonteTemporal object : HorizonteTemporal.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getBaseGeografica() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (BaseGeografica object : BaseGeografica.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getPublicoAlvosPrograma() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PublicoAlvo object : programaPPAFacade.getPublicoAlvoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoProgramas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProgramaPPA object : TipoProgramaPPA.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<MacroObjetivoEstrategico> completarEixoEstrategico(String parte) {
        List<MacroObjetivoEstrategico> eixos = Lists.newArrayList();
        if (selecionado.getPpa() != null) {
            eixos = programaPPAFacade.getMacroObjetivoEstrategicoFacade().buscarEixosPorPlanejamentoFiltrando(parte.trim(), selecionado.getPpa().getPlanejamentoEstrategico());
        }
        return eixos;
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return programaPPAFacade.getUnidadeOrganizacionalFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<AcaoPrincipal> completaAcao(String parte) {
        return programaPPAFacade.getAcaoPpaFacade().listaAcaoPPANaoVinculadaProgramas(parte.trim());
    }

    public List<Indicador> completaIndicador(String parte) {
        return programaPPAFacade.getIndicadorFacade().listaFiltrandoIndicador(parte.trim());
    }

    public List<SelectItem> getItensPlanDescricao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getMacroObjetivoEstrategico() != null) {
            for (ItemPlanejamentoEstrategico object : programaPPAFacade.getItemPlanejamentoEstrategicoFacade().buscarObjetivos(selecionado.getMacroObjetivoEstrategico())) {
                if (object.getDescricao() != null) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getPeriodicidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Periodicidade object : programaPPAFacade.getPeriodicidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterItem() {
        if (converterItem == null) {
            converterItem = new ConverterGenerico(ItemPlanejamentoEstrategico.class, programaPPAFacade.getItemPlanejamentoEstrategicoFacade());
        }
        return converterItem;
    }

    public ConverterAutoComplete getConverterIndicador() {
        if (converterIndicador == null) {
            converterIndicador = new ConverterAutoComplete(Indicador.class, programaPPAFacade.getIndicadorFacade());
        }
        return converterIndicador;
    }

    public ConverterAutoComplete getConverterAcao() {
        if (converterAcao == null) {
            converterAcao = new ConverterAutoComplete(AcaoPrincipal.class, programaPPAFacade.getAcaoPpaFacade());
        }
        return converterAcao;
    }

    public ConverterGenerico getConverterPeriodicidade() {
        if (converterPeriodicidade == null) {
            converterPeriodicidade = new ConverterGenerico(Periodicidade.class, programaPPAFacade.getPeriodicidadeFacade());
        }
        return converterPeriodicidade;
    }

    public Converter getConverterValorIndicador() {
        if (converterValorIndicador == null) {
            converterValorIndicador = new ConverterAutoComplete(ValorIndicador.class, programaPPAFacade.getValorIndicadorFacade());
        }
        return converterValorIndicador;
    }

    public ConverterGenerico getConverterPublicoAlvo() {
        if (converterPublicoAlvo == null) {
            converterPublicoAlvo = new ConverterGenerico(PublicoAlvo.class, programaPPAFacade.getPublicoAlvoFacade());
        }
        return converterPublicoAlvo;
    }

    public Converter getConverterPpa() {
        if (converterPpa == null) {
            converterPpa = new ConverterAutoComplete(PPA.class, programaPPAFacade.getPpaFacade());
        }
        return converterPpa;
    }

    public Converter getConverterPpaGenerico() {
        if (converterPpaGenerico == null) {
            converterPpaGenerico = new ConverterGenerico(PPA.class, programaPPAFacade.getPpaFacade());
        }
        return converterPpaGenerico;
    }

    public List<PPA> completaPPA(String parte) {
        return programaPPAFacade.getPpaFacade().listaNaoAprovados(parte.trim());
    }

    public Converter getConverterEixo() {
        if (converterEixo == null) {
            converterEixo = new ConverterAutoComplete(MacroObjetivoEstrategico.class, programaPPAFacade.getMacroObjetivoEstrategicoFacade());
        }
        return converterEixo;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, programaPPAFacade.getUnidadeOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public void setConverterUnidadeOrganizacional(ConverterAutoComplete converterUnidadeOrganizacional) {
        this.converterUnidadeOrganizacional = converterUnidadeOrganizacional;
    }

    public void codigoPPA(SelectEvent event) {
        selecionado.setPpa((PPA) event.getObject());
        if (((ProgramaPPA) selecionado).getId() == null && selecionado.getPpa() != null) {
            selecionado.setCodigo(programaPPAFacade.recuperaUltimoCodigo(((ProgramaPPA) selecionado).getPpa()));
        } else if (!operacao.equals(Operacoes.NOVO)) {
            if (!numeroSelecionado.equals(((ProgramaPPA) selecionado).getPpa().getId())) {
                selecionado.setCodigo(programaPPAFacade.recuperaUltimoCodigo(((ProgramaPPA) selecionado).getPpa()));
                numeroSelecionado = selecionado.getPpa().getId();
            }
        }
    }

    public Boolean hasProgramaNaProvisaoPPADespesa() {
        if (selecionado.getId() != null) {
            try {
                return programaPPAFacade.hasProgramaNaProvisaoPPADespesa(selecionado);
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
        return false;
    }

    public void validaExercicioCorrente(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Calendar data = Calendar.getInstance();
        data.setTime((Date) value);
        Integer exer = sistemaControlador.getExercicioCorrente().getAno();
        if (data.get(Calendar.YEAR) != exer) {
            message.setDetail("Ano diferente do exercício corrente!");
            message.setSummary("Ano diferente do exercício corrente!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public String getVerificaMascara() {
        ConfiguracaoPlanejamentoPublico cpp = programaPPAFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoPrograma() == null) {
            return null;
        }
        return cpp.getMascaraCodigoPrograma().replaceAll("#", "9");
    }

    public Boolean getVerificaConf() {
        if (getVerificaMascara() == null) {
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

    public void adicionaMensagemPPA() {
        FacesUtil.addOperacaoNaoRealizada("Não existe um PPA aberto para adicionar uma nova ação.");
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void excluir() {
        try {
            validarExclusaoEntidade();
            for (AcaoPPA acaoPPA : selecionado.getProjetosAtividades()) {
                acaoPPA = programaPPAFacade.getProjetoAtividadeFacade().recuperar(acaoPPA.getId());
                acaoPPA.setPrograma(null);
                programaPPAFacade.getProjetoAtividadeFacade().salvar(acaoPPA);
            }
            for (AcaoPrincipal acaoPrincipal : selecionado.getAcoes()) {
                acaoPrincipal = programaPPAFacade.getAcaoPpaFacade().recuperar(acaoPrincipal.getId());
                acaoPrincipal.setPrograma(null);
                programaPPAFacade.getAcaoPpaFacade().salvar(acaoPrincipal);

            }
            programaPPAFacade.remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            try {
                validarForeinKeysComRegistro(selecionado);
                descobrirETratarException(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public boolean getPpaAtivo() {
        try {
            PPA ppa = programaPPAFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
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

    private void objetosSelecionadosDeSessao() {
        addObjectInSession("dtInicial", this.dtInicial);
        addObjectInSession("dtFinal", this.dtFinal);
        addObjectInSession("hierarquiaOrganizacional", this.hierarquiaOrganizacional);
        addObjectInSession("valorIndicadores", this.valorIndicadores);
        addObjectInSession("indicadorPrograma", this.indicadorPrograma);
        addObjectInSession("indicadorProgramas", this.indicadorProgramas);
    }

    private void removeSelecionadosDeSessao() {
        Map mapObjects = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        mapObjects.remove("dtInicial");
        mapObjects.remove("dtFinal");
        mapObjects.remove("hierarquiaOrganizacional");
        mapObjects.remove("valorIndicadores");
        mapObjects.remove("indicadorPrograma");
        mapObjects.remove("indicadorProgramas");
    }

    public void btnCadastroNovoPublicoAlvo() {
        Web.navegacao("/programa-ppa/novo/",
            "/publico-alvo/novo/",
            selecionado);
        objetosSelecionadosDeSessao();
    }

    public void btnCadastroNovaAcaoPPA() {
        Web.navegacao("/programa-ppa/novo/",
            "/acao-ppa/novo/",
            selecionado);
        objetosSelecionadosDeSessao();
    }

    public void btnCadastroNovaPeriodicidade() {
        Web.navegacao("/programa-ppa/novo/",
            "/periodicidade/novo/",
            selecionado);
        objetosSelecionadosDeSessao();
    }

    public void btnCadastroNovoIndicador() {
        Web.navegacao("/programa-ppa/novo/",
            "/indicador/novo/",
            selecionado);
        objetosSelecionadosDeSessao();
    }

    private void addObjectInSession(String keyObjParameter, Object value) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(keyObjParameter, value);
    }

    private Object findObjectInSession(String keyObjParameter) {
        Map mapObjects = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Object toReturn = mapObjects.get(keyObjParameter);
        mapObjects.remove(keyObjParameter);
        return toReturn;
    }

    @Override
    public List<ProgramaPPA> completarEstaEntidade(String parte) {
        return programaPPAFacade.listaFiltrandoProgramasPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
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

    public ParticipanteProgramaPPA getParticipanteProgramaPPA() {
        return participanteProgramaPPA;
    }

    public void setParticipanteProgramaPPA(ParticipanteProgramaPPA participanteProgramaPPA) {
        this.participanteProgramaPPA = participanteProgramaPPA;
    }

    private void validarAdicionarUnidadesParticipantes() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrgParticipante == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Unidade Participante é obrigatório para adicionar.");
        } else if (hierarquiaOrgParticipante.equals(hierarquiaOrganizacional)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Participante deve ser diferente da Unidade Responsável.");
        } else {
            for (ParticipanteProgramaPPA p : selecionado.getParticipantesProgramaPPA()) {
                if (p.getUnidadeOrganizacional().equals(hierarquiaOrgParticipante.getSubordinada())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Participante: " + hierarquiaOrgParticipante.getSubordinada() + " ja foi adicionada");
                }
            }
        }
        ve.lancarException();
    }

    private void ordenarCodigoAcao(List<AcaoPrincipal> acoesPrincipais) {
        Collections.sort(acoesPrincipais, new Comparator<AcaoPrincipal>() {
            @Override
            public int compare(AcaoPrincipal o1, AcaoPrincipal o2) {
                Integer i1 = Integer.valueOf(o1.getCodigo());
                Integer i2 = Integer.valueOf(o2.getCodigo());
                return i1.compareTo(i2);
            }
        });
    }

    public void adicionarUnidadeParticipante() {
        try {
            validarAdicionarUnidadesParticipantes();
            unidadeOrgParticipante = hierarquiaOrgParticipante.getSubordinada();
            participanteProgramaPPA.setUnidadeOrganizacional(unidadeOrgParticipante);
            participanteProgramaPPA.setProgramaPPA(selecionado);
            selecionado.getParticipantesProgramaPPA().add(participanteProgramaPPA);
            participanteProgramaPPA = new ParticipanteProgramaPPA();
            unidadeOrgParticipante = new UnidadeOrganizacional();
            hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<HierarquiaOrganizacional> completarUnidades(String parte) {
        return programaPPAFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
    }
}
