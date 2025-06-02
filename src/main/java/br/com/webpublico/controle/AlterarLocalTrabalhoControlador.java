/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "alterarLocalTrabalhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTransferencias", pattern = "/transferencias/novo/", viewId = "/faces/rh/administracaodepagamento/tranferencias/edita.xhtml"),
    @URLMapping(id = "editarTransferencias", pattern = "/transferencias/editar/#{alterarLocalTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tranferencias/edita.xhtml"),
    @URLMapping(id = "listarTransferencias", pattern = "/transferencias/listar/", viewId = "/faces/rh/administracaodepagamento/tranferencias/lista.xhtml"),
    @URLMapping(id = "verTransferencias", pattern = "/transferencias/ver/#{alterarLocalTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tranferencias/visualizar.xhtml")
})
public class AlterarLocalTrabalhoControlador extends PrettyControlador<ContratoFP> implements Serializable, CRUD {

    ProvimentoFP provimentoFP;
    @EJB
    private AlteracaoLocalTrabalhoFacade alteracaoLocalTrabalhoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterHorarioDeTrabalho;
    private ConverterAutoComplete converterRecursoFP;
    private ConverterAutoComplete converterAtoLegal;
    private List<HorarioContratoFP> horarioContratoFPs;
    private ContratoFP contratoFPSelecionado;
    private HorarioContratoFP horarioContratoFP;
    private RecursoDoVinculoFP recursoDoVinculoFP;
    private LotacaoFuncional lotacaoFuncional;
    private HierarquiaOrganizacional ho;
    private AtoLegal atoLegal;
    @EJB
    private CargoFacade cargoFacade;
    private boolean podeAlterarRecursoHorarioLotacao;

    public AlterarLocalTrabalhoControlador() {
        super(ContratoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;
    }

    public List<RecursoFP> completaRecursoFP(String parte) {
        return alteracaoLocalTrabalhoFacade.getRecursoFPFacade().listaRecursosFPVigente(parte, sistemaFacade.getDataOperacao());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return alteracaoLocalTrabalhoFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, alteracaoLocalTrabalhoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public ConverterAutoComplete getConverterRecursoFP() {
        if (converterRecursoFP == null) {
            converterRecursoFP = new ConverterAutoComplete(RecursoFP.class, alteracaoLocalTrabalhoFacade.getRecursoFPFacade());
        }
        return converterRecursoFP;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public HierarquiaOrganizacional getHo() {
        return ho;
    }

    public void setHo(HierarquiaOrganizacional ho) {
        this.ho = ho;
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public List<HorarioContratoFP> getHorarioContratoFPs() {
        return horarioContratoFPs;
    }

    public void setHorarioContratoFPs(List<HorarioContratoFP> horarioContratoFPs) {
        this.horarioContratoFPs = horarioContratoFPs;
    }

    public void validarRecursoDoVinculoFP() {
        if (!Util.validaCampos(recursoDoVinculoFP)) {
            throw new ValidacaoException();
        }
        if (!DataUtil.isVigenciaValida(recursoDoVinculoFP, selecionado.getRecursosDoVinculoFP())) {
            throw new ValidacaoException();
        }
    }

    private void validarRecursosIguais() {
        if (!recursoDoVinculoFP.temId() && !podeAlterarRecursoHorarioLotacao) {
            ValidacaoException ve = new ValidacaoException();
            VinculoFP vinculo = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(selecionado.getContratoFP().getId());
            if (recursoDoVinculoFP.getRecursoFP().equals(vinculo.getRecursoDoVinculoFPVigente().getRecursoFP())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor já se encontra no Recurso FP informado.");
            }
            ve.lancarException();
        }
    }

    public void novoRecurso() {
        recursoDoVinculoFP = new RecursoDoVinculoFP();
        recursoDoVinculoFP.setVinculoFP(selecionado);
    }

    public void confirmarRecurso() {
        try {
            validarRecursoDoVinculoFP();
            validarRecursosIguais();
            Util.adicionarObjetoEmLista(selecionado.getRecursosDoVinculoFP(), recursoDoVinculoFP);
            anularRecursoDoVinculoFP();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            logger.debug("Erro ao confirmar recurso do vinculof fp: " + ex.getMessage());
        }
    }

    public void anularRecursoDoVinculoFP() {
        recursoDoVinculoFP = null;
    }

    public void cancelarRecurso() {
        anularRecursoDoVinculoFP();
    }

    public void selecionarRecursoDoVinculoFP(RecursoDoVinculoFP rvFP) {
        recursoDoVinculoFP = (RecursoDoVinculoFP) Util.clonarObjeto(rvFP);
    }

    public void removerRecursoDoVinculoFP(RecursoDoVinculoFP rvFP) {
        selecionado.removerRecursoDoVinculoFP(rvFP);
    }

    public void novoHorarioContratoFP() {
        horarioContratoFP = new HorarioContratoFP();
    }

    public List<SelectItem> getHorariosDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : alteracaoLocalTrabalhoFacade.getHorarioDeTrabalhoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private void validarFolhaCalculada() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoFPFacade.hasFolhaCalculada(selecionado, horarioContratoFP.getInicioVigencia()) && horarioContratoFP.getId() == null && !podeAlterarRecursoHorarioLotacao) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Servidor(a) " + selecionado.getContratoFP() + " tem folha calculada após a data <b>" + DataUtil.getDataFormatada(horarioContratoFP.getInicioVigencia()) + "</b> não podendo ser transferido(a) nessa data.");
        }
        ve.lancarException();
    }

    public void confirmarHorarioContratoFP() {
        try {

            validarHorarioContratoFP();
            validarFolhaCalculada();
            Util.adicionarObjetoEmLista(horarioContratoFPs, horarioContratoFP);
            ordenarHorarios(horarioContratoFPs);
            atualizarLotacaoFuncional(horarioContratoFP);
            anularHorarioContratoFP();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            logger.debug("Erro ao confirmar horario contrato fp: " + ex.getMessage());
        }
    }

    private void atualizarLotacaoFuncional(HorarioContratoFP horarioContrato) {
        if (horarioContrato.temId()) {
            LotacaoFuncional lotacaoFuncionalPorHorarioContratoFP = selecionado.getLotacaoFuncionalPorHorarioContratoFP(horarioContrato);
            if (lotacaoFuncionalPorHorarioContratoFP != null) {
                lotacaoFuncionalPorHorarioContratoFP.setFinalVigencia(horarioContrato.getFinalVigencia());
            }
        }
    }

    public void cancelarHorarioContratoFP() {
        anularHorarioContratoFP();
    }

    public void anularHorarioContratoFP() {
        horarioContratoFP = null;
    }

    private void validarHorarioContratoFP() {
        if (!Util.validaCampos(horarioContratoFP)) {
            throw new ValidacaoException();
        }
        if (!DataUtil.isVigenciaValida(horarioContratoFP, horarioContratoFPs)) {
            throw new ValidacaoException();
        }
    }

    public void selecionarHorarioContratoFP(HorarioContratoFP hcFP) {
        horarioContratoFP = (HorarioContratoFP) Util.clonarObjeto(hcFP);
    }

    public void removerHorarioContratoFP(HorarioContratoFP hcFP) {
        horarioContratoFPs.remove(hcFP);
    }

    public void removerHorarioContratoFPAndLotacaoFuncional(HorarioContratoFP hcFP) {
        try {
            if (!podeAlterarRecursoHorarioLotacao) {
                validarFolhaCalculada(hcFP);
            }
            removerLotacaoFuncionalPorHorarioContratoFP(hcFP);
            removerHorarioContratoFP(hcFP);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarFolhaCalculada(HorarioContratoFP horario) {
        ValidacaoException ve = new ValidacaoException();
        if (!horario.temId()) {
            return;
        }
        if (contratoFPFacade.hasFolhaCalculada(selecionado, horario.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe folha calculada para " + DataUtil.getDataFormatada(horario.getInicioVigencia()) + "!");
        }
        ve.lancarException();
    }

    public void removerLotacaoFuncionalPorHorarioContratoFP(HorarioContratoFP hcFP) {
        try {
            if (!podeAlterarRecursoHorarioLotacao) {
                validarRemocaoLotacaoFuncional(hcFP);
            }
            selecionado.removerLotacaoFuncionalPorHorarioContratoFP(hcFP);
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            logger.error("erro ao remover lotação : ", e);
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    private void validarRemocaoLotacaoFuncional(HorarioContratoFP hcFP) {
        ValidacaoException val = new ValidacaoException();
        LotacaoFuncional lotacao = selecionado.getLotacaoFuncionalPorHorarioContratoFP(hcFP);
        if (lotacao != null && lotacao.getId() != null) {
            if (contratoFPFacade.hasFolhaCalculada(selecionado, lotacao.getInicioVigencia())) {
                val.adicionarMensagemDeOperacaoNaoPermitida("Já existe folha processada posterior à " + DataUtil.getDataFormatada(lotacao.getInicioVigencia()));
            }
        }
        val.lancarException();
    }

    public boolean desabilitarBotaoNovoRecurso() {
        return (selecionado == null || recursoDoVinculoFP != null || adicionouNovoRecurso()) && !podeAlterarRecursoHorarioLotacao;
    }

    public boolean desabilitarBotaoNovoHorario() {
        return selecionado == null || horarioContratoFP != null || adicionouNovoHorario();
    }

    public Boolean desabilitarBotaoRemoverLotacaoFuncional(HorarioContratoFP hcFP) {
        return contratoFPFacade.hasFolhaCalculada(selecionado, hcFP.getInicioVigencia()) && hcFP.temId();
    }

    public Boolean desabilitarBotaoNovaLotacaoFuncional(HorarioContratoFP hcFP) {
        return contratoFPFacade.hasFolhaCalculada(selecionado, hcFP.getInicioVigencia()) && hcFP.temId() && !podeAlterarRecursoHorarioLotacao;
    }

    public boolean desabilitarEdicaoPorRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        RecursoDoVinculoFP ultimoRecurdoDoVinculoFPVigente = alteracaoLocalTrabalhoFacade.getRecursoDoVinculoFPFacade().recuperarRecursosDoVinculoByVinculoOrderByID(selecionado);
        if (ultimoRecurdoDoVinculoFPVigente != null) {
            return !recursoDoVinculoFP.equals(ultimoRecurdoDoVinculoFPVigente) && (recursoDoVinculoFP.getFinalVigencia() != null && !podeAlterarRecursoHorarioLotacao);
        }
        return false;
    }

    public boolean desabilitarRemocaoPorRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        return recursoDoVinculoFP.temId() && !podeAlterarRecursoHorarioLotacao;
    }

    public boolean desabilitarEdicaoPorHorarioContratoFP(HorarioContratoFP horarioContrato) {
        LotacaoFuncional ultimaLotacaoVigente = alteracaoLocalTrabalhoFacade.getLotacaoFuncionalFacade().buscarUltimaLotacaoPorVinculoFPOderById(selecionado);
        if (ultimaLotacaoVigente != null) {
            return !horarioContrato.equals(ultimaLotacaoVigente.getHorarioContratoFP()) && (horarioContrato.getFinalVigencia() != null && !podeAlterarRecursoHorarioLotacao);
        }
        return false;
    }

    public boolean desabilitarRemocaoPorHorarioContratoFP(HorarioContratoFP horarioContrato) {
        return contratoFPFacade.hasFolhaCalculada(selecionado, horarioContrato.getInicioVigencia()) && horarioContrato.temId() && !podeAlterarRecursoHorarioLotacao;
    }

    public void ordenarHorarios(List<HorarioContratoFP> horarios) {
        Collections.sort(horarios, new Comparator<HorarioContratoFP>() {
            @Override
            public int compare(HorarioContratoFP o1, HorarioContratoFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void novaLotacaoFuncional(HorarioContratoFP horarioContrato) {
        lotacaoFuncional = new LotacaoFuncional();
        lotacaoFuncional.setDataRegistro(UtilRH.getDataOperacao());
        lotacaoFuncional.setVinculoFP(selecionado);
        lotacaoFuncional.setHorarioContratoFP(horarioContrato);
        lotacaoFuncional.setInicioVigencia(horarioContrato.getInicioVigencia());

        horarioContratoFP = horarioContrato;
        anularHierarquiaOrganizacional();
    }

    public void confirmarLotacaoFuncional() {
        try {
            validarLotacaoFuncional();
            lotacaoFuncional.setUnidadeOrganizacional(ho.getSubordinada());
            atualizarFinalVigenciaHorarioContratoFPPorLotacaoFuncional();
            atualizarUnidadeContratoFP();
            atualizarHierarquiaContratoFPComNivel2();

            Util.adicionarObjetoEmLista(selecionado.getLotacaoFuncionals(), lotacaoFuncional);
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:panel-horarios");
            FacesUtil.executaJavaScript("dialogLotacaoFuncional.hide()");

            anularHierarquiaOrganizacional();
            anularHorarioContratoFP();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            logger.debug("Erro ao confirmar lotacao funcional: " + ex.getMessage());
        }
    }

    private void atualizarUnidadeContratoFP() {
        if (lotacaoFuncional.getFinalVigencia() == null) {
            HierarquiaOrganizacional orgao = alteracaoLocalTrabalhoFacade.getHierarquiaOrganizacionalFacade()
                .buscarOrgaoAdministrativoPorUnidadeAndVigencia(lotacaoFuncional.getUnidadeOrganizacional(),
                    UtilRH.getDataOperacao());
            if (orgao != null) {
                selecionado.setUnidadeOrganizacional(orgao.getSubordinada());
            }
        }
    }

    public void atualizarHierarquiaContratoFPComNivel2() {

        if(lotacaoFuncional.getUnidadeOrganizacional() != null){
            HierarquiaOrganizacional orgao = alteracaoLocalTrabalhoFacade.getHierarquiaOrganizacionalFacade()
                .buscarOrgaoAdministrativoPorUnidadeAndVigencia(lotacaoFuncional.getUnidadeOrganizacional(),
                    UtilRH.getDataOperacao());
            if (orgao != null) {
                this.selecionado.setHierarquiaOrganizacional(orgao);
            }
        }
    }

    private void anularHierarquiaOrganizacional() {
        ho = null;
    }

    public void atualizarFinalVigenciaHorarioContratoFPPorLotacaoFuncional() {
        if (lotacaoFuncional.getFinalVigencia() != null) {
            horarioContratoFP.setFinalVigencia(lotacaoFuncional.getFinalVigencia());
        }
    }

    private void validarLotacaoFuncional() {
        ValidacaoException ve = new ValidacaoException();
        if (ho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hierarquia Organizacional deve ser informado!");
        } else if (!cargoFacade.cargoContemUnidadeOrganizacional(selecionado.getCargo().getId(), ho.getSubordinada().getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Cargo do servidor não pertence à Hierarquia/Lotação Funcional escolhida.");
        }
        if (lotacaoFuncional.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado!");
        }
        lancarExcecao(ve);
        if (!DataUtil.isVigenciaValida(lotacaoFuncional, selecionado.getLotacaoFuncionals())) {
            throw new ValidacaoException();
        }
        VinculoFP vinculo = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(selecionado.getContratoFP().getId());
        LotacaoFuncional lotacaoAtual = vinculo.getLotacaoFuncionalVigente();

        if (lotacaoAtual != null && ho.getSubordinada().equals(lotacaoAtual.getUnidadeOrganizacional())
            && lotacaoFuncional.getHorarioContratoFP().getHorarioDeTrabalho().equals(lotacaoAtual.getHorarioContratoFP().getHorarioDeTrabalho()) && !podeAlterarRecursoHorarioLotacao) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor já se encontra no Horário e Local de Trabalho informados.");
            lancarExcecao(ve);
        }
    }

    public void carregaContrato() {
        selecionado = contratoFPSelecionado != null ? contratoFPFacade.recuperar(contratoFPSelecionado.getId()) : contratoFPFacade.recuperar(selecionado.getId());
        limparListas();
        preencheHorariosDeTrabalho();
    }

    public void preencheHorariosDeTrabalho() {
        for (LotacaoFuncional l : selecionado.getLotacaoFuncionals()) {
            Util.adicionarObjetoEmLista(horarioContratoFPs, l.getHorarioContratoFP());
        }
    }

    public void limparListas() {
        horarioContratoFPs.clear();
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public boolean isPodeAlterarRecursoHorarioLotacao() {
        return podeAlterarRecursoHorarioLotacao;
    }

    public void setPodeAlterarRecursoHorarioLotacao(boolean podeAlterarRecursoHorarioLotacao) {
        this.podeAlterarRecursoHorarioLotacao = podeAlterarRecursoHorarioLotacao;
    }

    @URLAction(mappingId = "novoTransferencias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = null;
        horarioContratoFPs = new ArrayList<>();
        atoLegal = null;
        conferirTipoProvimento();
        podeAlterarRecursoHorarioLotacao = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_RECURSOFP_HORARIO_LOTACAO);
    }

    @URLAction(mappingId = "verTransferencias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        atoLegal = selecionado.getProvimentoFP().getAtoLegal();
    }

    @URLAction(mappingId = "editarTransferencias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        horarioContratoFPs = new ArrayList<>();
        carregaContrato();
        atoLegal = selecionado.getProvimentoFP().getAtoLegal();
        provimentoFP = selecionado.getProvimentoFP();
        podeAlterarRecursoHorarioLotacao = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_RECURSOFP_HORARIO_LOTACAO);
    }

    public boolean conferirTipoProvimento() {
        boolean toReturn = true;
        if (alteracaoLocalTrabalhoFacade.getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(TipoProvimento.TRANSFERENCIA) == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não existe o tipo de provimento cadastrado, necessário para esta ação."));
            toReturn = false;
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public ConverterAutoComplete getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterAutoComplete(HorarioDeTrabalho.class, alteracaoLocalTrabalhoFacade.getHorarioDeTrabalhoFacade());
        }
        return converterHorarioDeTrabalho;
    }

    public List<ContratoFP> completaContrato(String parte) {
        return contratoFPFacade.recuperaContratoVigenteMatricula(parte.trim());
    }

    public void associa(HorarioContratoFP e) {
        lotacaoFuncional.setHorarioContratoFP(e);
        ho = new HierarquiaOrganizacional();
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacional(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public void validarTransferencia() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposObrigatorios(ve);
        validarLotacaoFuncionalAdicionadaNoNovoHorario(ve);
        validarExistenciaPeloMenosUmHorario(ve);
    }

    public void validarLotacaoFuncionalAdicionadaNoNovoHorario(ValidacaoException ve) {
        for (HorarioContratoFP horarioContrato : horarioContratoFPs) {
            LotacaoFuncional lotacaoFuncionalPorHorarioContratoFP = selecionado.getLotacaoFuncionalPorHorarioContratoFP(horarioContrato);
            if (lotacaoFuncionalPorHorarioContratoFP == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo local de trabalho é obrigatório!");
                lancarExcecao(ve);
            }
        }
    }

    public void validarCamposObrigatorios(ValidacaoException ve) {
        if (selecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado!");
        }
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado!");
        }
        lancarExcecao(ve);
    }

    public void validarExistenciaPeloMenosUmHorario(ValidacaoException ve) {
        if (horarioContratoFPs.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido salvar transferência sem horário de trabalho.");
        }
        lancarExcecao(ve);
    }

    public void lancarExcecao(ValidacaoException ve) {
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean adicionouNovoRecurso() {
        return !selecionado.todosRecursosDoVinculoFPEstaoPersistidos();
    }

    public boolean adicionouNovoHorario() {
        for (HorarioContratoFP horarioContrato : horarioContratoFPs) {
            if (!horarioContrato.temId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void salvar() {
        try {
            atualizarHorariosDoSelecionado();
            validarTransferencia();
            alteracaoLocalTrabalhoFacade.salvarAlteracao(selecionado, atoLegal, provimentoFP);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void atualizarHorariosDoSelecionado() {
        for (LotacaoFuncional funcional : selecionado.getLotacaoFuncionals()) {
            HorarioContratoFP horario = buscarHorario(funcional.getHorarioContratoFP(), horarioContratoFPs);
            if (horario != null) {
                funcional.setHorarioContratoFP(horario);
            }
        }
    }

    private HorarioContratoFP buscarHorario(HorarioContratoFP horarioContratoFP, List<HorarioContratoFP> horarioContratoFPs) {
        for (HorarioContratoFP horarioController : horarioContratoFPs) {
            if (horarioContratoFP.equals(horarioController)) {
                return horarioController;
            }
        }
        return null;
    }

    @Override
    public void excluir() {
        try {
            getFacede().remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Registro excluído com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível excluír o registro: " + e.getMessage());
            logger.error("Nao foi possivel excluir a transferencia: " + e);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencias/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getDescricaoLotacaoFuncionalDescricaoHierarquia(HorarioContratoFP ob) {
        LotacaoFuncional lotacao = selecionado.getLotacaoFuncionalPorHorarioContratoFP(ob);
        String retorno = "";
        if (lotacao != null) {
            retorno = DataUtil.getDataFormatada(lotacao.getInicioVigencia()) + " - ";
            if (lotacao.getFimVigencia() != null) {
                retorno += DataUtil.getDataFormatada(lotacao.getFimVigencia()) + " - ";
            } else {
                retorno += " (Atual Vigente) - ";
            }
            retorno += getDescricaoHierarquiaOrganizacional(lotacao.getUnidadeOrganizacional());
        }
        return retorno;

    }

    public String getDescricaoHierarquiaOrganizacional(UnidadeOrganizacional uo) {
        HierarquiaOrganizacional hierarquia = alteracaoLocalTrabalhoFacade.getHierarquiaOrganizacionalFacade()
            .recuperaHierarquiaOrganizacionalPelaUnidadeUltima(uo.getId());

        return hierarquia.getDescricao();
    }
}
