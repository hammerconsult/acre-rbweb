/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValidadorDatasEMesesOutorga;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "contribuinteDebitoOutorgaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoOutorga",
        pattern = "/outorga/novo/",
        viewId = "/faces/tributario/rbtrans/contribuintedebitooutorga/edita.xhtml"),
    @URLMapping(id = "editarOutorga",
        pattern = "/outorga/editar/#{contribuinteDebitoOutorgaControlador.id}/",
        viewId = "/faces/tributario/rbtrans/contribuintedebitooutorga/edita.xhtml"),
    @URLMapping(id = "listarOutorga",
        pattern = "/outorga/listar/",
        viewId = "/faces/tributario/rbtrans/contribuintedebitooutorga/lista.xhtml"),
    @URLMapping(id = "verOutorga",
        pattern = "/outorga/ver/#{contribuinteDebitoOutorgaControlador.id}/",
        viewId = "/faces/tributario/rbtrans/contribuintedebitooutorga/visualizar.xhtml")
})
public class ContribuinteDebitoOutorgaControlador extends PrettyControlador<ContribuinteDebitoOutorga> implements Serializable, CRUD {

    @EJB
    private ContribuinteDebitoOutorgaFacade contribuinteDebitoOutorgaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private ConverterAutoComplete converterCMC;
    private ConverterAutoComplete converterExercicio;
    private EnderecoCorreio enderecoSelecionado;
    @EJB
    private PessoaFacade pessoaFacade;
    private Pessoa pessoaCMC;
    @EJB
    private SistemaFacade sistemaFacade;
    private OutorgaIPO outorgaIPO;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ContribuinteDebitoOutorgaControlador() {
        super(ContribuinteDebitoOutorga.class);
        outorgaIPO = new OutorgaIPO();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/outorga/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.pessoaCMC = null;
        this.enderecoSelecionado = null;
        selecionado.setPercentual(BigDecimal.ZERO);
        selecionado.setUsuarioCadastrou(getSistemaControlador().getUsuarioCorrente());
        selecionado.setCadastradoEm(getSistemaControlador().getDataOperacao());
    }

    @URLAction(mappingId = "editarOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        ver();
        operacao = Operacoes.EDITAR;
        selecionado.setUsuarioAlterou(getSistemaControlador().getUsuarioCorrente());
        selecionado.setAtualizadoEm(new Date());
        Collections.sort(selecionado.getListaIpo());

    }

    @Override
    @URLAction(mappingId = "verOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.setSessao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao"));
        operacao = Operacoes.VER;
        selecionado = (ContribuinteDebitoOutorga) Web.pegaDaSessao(metadata.getEntidade());
        if (selecionado == null) {
            selecionado = (ContribuinteDebitoOutorga) getFacede().recuperar(getId());
        }
        Collections.sort(selecionado.getListaIpo());
    }

    public void adicionarIPO() {
        try {
            validarOutorgaIpo();
            if (outorgaIPO.getUsuarioCadastro() == null) {
                outorgaIPO.setUsuarioCadastro(getSistemaControlador().getUsuarioCorrente());
            }
            outorgaIPO.setDataCadastro(SistemaFacade.getDataCorrente());
            outorgaIPO.setContribuinteDebitoOutorga(selecionado);
            selecionado.getListaIpo().add(outorgaIPO);
            Collections.sort(selecionado.getListaIpo());
            outorgaIPO = new OutorgaIPO();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void excluirIpo(OutorgaIPO ipo) {
        selecionado.getListaIpo().remove(ipo);
    }

    public void alterarIpo(OutorgaIPO ipo) {
        ipo.setUsuarioAtualizacao(getSistemaControlador().getUsuarioCorrente());
        ipo.setDataAtualizacao(SistemaFacade.getDataCorrente());
        this.outorgaIPO = ipo;

        excluirIpo(ipo);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (selecionado.getExercicio() == null) {
            FacesUtil.addError("Campo Obrigatório!", "O campo Exercício de Refêrencia é obrigatório");
            retorno = false;
        }
        if (selecionado.getCadastroEconomico() == null) {
            FacesUtil.addError("Campo Obrigatório!", "O campo CMC é obrigatório");
            retorno = false;
        } else if ((selecionado.getExercicio() != null)
            && contribuinteDebitoOutorgaFacade.obterPorContribuinte(selecionado.getCadastroEconomico(), selecionado.getExercicio()) != null
            && Operacoes.NOVO.equals(operacao)) {
            FacesUtil.addError("Operação não realizada!", "O C.M.C. " + selecionado.getCadastroEconomico().getInscricaoCadastral() + " já possui outorga para o exercício <b>" +
                selecionado.getExercicio().getAno() + "</b>");
            retorno = false;
        }

        if (selecionado.getListaIpo().isEmpty()) {
            FacesUtil.addError("Operação não realizada!", "Deve ser inserido ao menos um IPO e Percentual Mensal da Outorga");
            retorno = false;
        }

        return retorno;
    }


    private void validarOutorgaIpo() {
        ValidacaoException ve = new ValidacaoException();
        if (outorgaIPO.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é Obrigatório");
        }
        if (outorgaIPO.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final é Obrigatório");
        }
        if (outorgaIPO.getTipoPassageiro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Passageiro é obrigatório");
        }
        if (outorgaIPO.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês de Refêrencia é obrigatório");
        }
        if (outorgaIPO.getPercentual() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual é obrigatório");
        }

        if (outorgaIPO.getIpo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo IPO é obrigatório");
        } else {
            if (outorgaIPO.getIpo().compareTo(new BigDecimal(100)) > 0 || outorgaIPO.getIpo().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo IPO não pode exceder 100% e não pode ser menor que Zero.");
            }
        }

        if (outorgaIPO.getAlunos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Alunos é obrigatório");
        }

        if (outorgaIPO.getAlunosTransportados() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Alunos Transportados é obrigatório");
        }


        if ((outorgaIPO.getPercentual() != null)) {
            if (outorgaIPO.getPercentual().compareTo(new BigDecimal(100)) > 0 || outorgaIPO.getPercentual().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual da Outorga não pode exceder 100% e não pode ser menor que Zero.");
            }
        }

        if ((selecionado.getExercicio() != null)
            && contribuinteDebitoOutorgaFacade.obterPorContribuinte(selecionado.getCadastroEconomico(), selecionado.getExercicio()) != null
            && Operacoes.NOVO.equals(operacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O C.M.C. " + selecionado.getCadastroEconomico().getInscricaoCadastral() + " já possui outorga para o exercício <b>" +
                selecionado.getExercicio().getAno() + "</b>");
        }

        ValidadorDatasEMesesOutorga validador = new ValidadorDatasEMesesOutorga(outorgaIPO.getDataInicial(), outorgaIPO.getDataFinal(), outorgaIPO.getMes());
        validador.validarDatasAndMes();


        if (outorgaIPO.getTipoPassageiro() != null && outorgaIPO.getMes() != null) {
            for (OutorgaIPO ipo : selecionado.getListaIpo()) {
                if (outorgaIPO.getTipoPassageiro().equals(ipo.getTipoPassageiro()) && outorgaIPO.getMes().equals(ipo.getMes())) {
                    if (ipo.getDataInicial() != null && ipo.getDataFinal() != null) {
                        validador = new ValidadorDatasEMesesOutorga(outorgaIPO.getDataInicial(), outorgaIPO.getDataFinal(), ipo.getDataInicial(), ipo.getDataFinal());
                        validador.validarPeriodosVigentes();
                    }
                }
            }
        }
        ve.lancarException();

    }

    @Override
    public AbstractFacade getFacede() {
        return contribuinteDebitoOutorgaFacade;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }


    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public OutorgaIPO getOutorgaIPO() {
        return outorgaIPO;
    }

    public void setOutorgaIPO(OutorgaIPO outorgaIPO) {
        this.outorgaIPO = outorgaIPO;
    }

    public boolean hasSubvencaoLancada(OutorgaIPO ipo) {
        return ipo.getId() != null ? contribuinteDebitoOutorgaFacade.getSubvencaoProcessoFacade().hasLancamentoValidoParaACompetenciaEEmpresa(ipo) : false;
    }

    @Override
    public void excluir() {
        ValidacaoException ve = new ValidacaoException();
        try {
            for (OutorgaIPO ipo : selecionado.getListaIpo()) {
                if (hasSubvencaoLancada(ipo)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível remover esse Cadastro de Outorga, existe Processo de Subvenção lançado para o mesmo.");
                    ve.lancarException();
                    break;
                }
            }
            super.excluir();
        } catch (ValidacaoException validacao) {
            FacesUtil.printAllFacesMessages(validacao.getMensagens());
        }
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public List<SelectItem> getTipoPassageiro() {
        return Util.getListSelectItem(Lists.newArrayList(TipoPassageiro.values()));
    }

}
