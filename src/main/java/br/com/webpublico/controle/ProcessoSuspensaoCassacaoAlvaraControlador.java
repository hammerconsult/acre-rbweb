package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ActionConsultaEntidade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.entidadesauxiliares.VOAlvaraItens;
import br.com.webpublico.enums.SituacaoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.enums.TipoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.enums.TipoVerificacaoDebitoAlvara;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoSuspensaoCassacaoAlvaraFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "processoSuspensaoCassacaoAlvaraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoSuspensaoAlvara", pattern = "/suspensao-cassacao-alvara/novo/", viewId = "/faces/tributario/alvara/suspensao/edita.xhtml"),
    @URLMapping(id = "editaProcessoSuspensaoAlvara", pattern = "/suspensao-cassacao-alvara/editar/#{processoSuspensaoCassacaoAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/suspensao/edita.xhtml"),
    @URLMapping(id = "verProcessoSuspensaoAlvara", pattern = "/suspensao-cassacao-alvara/ver/#{processoSuspensaoCassacaoAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/suspensao/visualizar.xhtml"),
    @URLMapping(id = "listaProcessoSuspensaoAlvara", pattern = "/suspensao-cassacao-alvara/listar/", viewId = "/faces/tributario/alvara/suspensao/lista.xhtml"),
})
public class ProcessoSuspensaoCassacaoAlvaraControlador extends PrettyControlador<ProcessoSuspensaoCassacaoAlvara> implements CRUD {

    @EJB
    private ProcessoSuspensaoCassacaoAlvaraFacade facade;
    private CadastroEconomico cadastroEconomico;
    private List<Alvara> alvarasDoCadastro;
    private ConfiguracaoTributario configuracao;
    private Map<Long, List<VOAlvaraItens>> itensAlvara = Maps.newHashMap();
    private Map<Long, ProcessoCalculoAlvara> processoDoAlvara = Maps.newHashMap();

    public ProcessoSuspensaoCassacaoAlvaraControlador() {
        super(ProcessoSuspensaoCassacaoAlvara.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/suspensao-cassacao-alvara/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "verProcessoSuspensaoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarVariaveis();
    }

    @Override
    @URLAction(mappingId = "novoProcessoSuspensaoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
    }

    @Override
    @URLAction(mappingId = "editaProcessoSuspensaoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarVariaveis();
    }

    private void carregarVariaveis() {
        setCadastroEconomico(selecionado.getAlvaras().get(0).getAlvara().getCadastroEconomico());
        for (AlvaraProcessoSuspensaoCassacao alvaraProcesso : selecionado.getAlvaras()) {
            buscarProcessoDoAlvara(alvaraProcesso.getAlvara().getId());
            buscarItensProcCalc(alvaraProcesso.getAlvara().getId());
        }
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Map<Long, List<VOAlvaraItens>> getItensAlvara() {
        return itensAlvara;
    }

    public void setItensAlvara(Map<Long, List<VOAlvaraItens>> itensAlvara) {
        this.itensAlvara = itensAlvara;
    }

    public Map<Long, ProcessoCalculoAlvara> getProcessoDoAlvara() {
        return processoDoAlvara;
    }

    public void setProcessoDoAlvara(Map<Long, ProcessoCalculoAlvara> processoDoAlvara) {
        this.processoDoAlvara = processoDoAlvara;
    }

    public List<Alvara> getAlvarasDoCadastro() {
        return alvarasDoCadastro;
    }

    public void setAlvarasDoCadastro(List<Alvara> alvarasDoCadastro) {
        this.alvarasDoCadastro = alvarasDoCadastro;
    }

    public List<CadastroEconomico> buscarCmcPorRazaoSocialAndCnpj(String parte) {
        return facade.buscarCmcPorRazaoSocialAndCnpj(parte);
    }

    public void selecionouCadastro() {
        try {
            ValidacaoException ve = new ValidacaoException();
            alvarasDoCadastro = Lists.newArrayList();
            if (cadastroEconomico == null) return;
            selecionado.getAlvaras().clear();
            alvarasDoCadastro = facade.getCalculoAlvaraFacade().buscarAlvaraVigentePorCmc(cadastroEconomico.getId());
            for (Alvara alvara : alvarasDoCadastro) {
                buscarProcessoDoAlvara(alvara.getId());
                buscarItensProcCalc(alvara.getId());
            }
            if (alvarasDoCadastro.isEmpty()) {
                cadastroEconomico = null;
                ve.adicionarMensagemDeOperacaoNaoRealizada("O Cadastro selecionado não possui nenhum alvará vigente.");
                ve.lancarException();
            } else if (alvarasDoCadastro.size() == 1) {
                AlvaraProcessoSuspensaoCassacao alvaraProcesso = new AlvaraProcessoSuspensaoCassacao(selecionado);
                alvaraProcesso.setAlvara(alvarasDoCadastro.get(0));
                selecionado.getAlvaras().add(alvaraProcesso);
            } else {
                FacesUtil.atualizarComponente("formDialogAlvarasEscolha");
                FacesUtil.executaJavaScript("dialogAlvaras.show()");
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void buscarItensProcCalc(Long idAlvara) {
        if (itensAlvara.containsKey(idAlvara)) return;
        if (processoDoAlvara.containsKey(idAlvara)) {
            ProcessoCalculoAlvara proc = processoDoAlvara.get(idAlvara);
            List<VOAlvaraItens> itens = facade.getCalculoAlvaraFacade().buscarItensProcCalcVO(proc.getId());
            itensAlvara.put(idAlvara, itens);
        }
    }

    public void buscarProcessoDoAlvara(Long idAlvara) {
        if (processoDoAlvara.containsKey(idAlvara)) return;
        ProcessoCalculoAlvara processo = facade.getCalculoAlvaraFacade().recuperarProcessoCalculoDoAlvara(idAlvara);
        processoDoAlvara.put(idAlvara, processo);
    }

    public void confirmarProcessoSelecionado() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getAlvaras().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um alvará.");
                ve.lancarException();
            }
            FacesUtil.executaJavaScript("dialogAlvaras.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarAlvaraEscolha(Alvara alvara) {
        AlvaraProcessoSuspensaoCassacao alvaraProc = new AlvaraProcessoSuspensaoCassacao(selecionado);
        alvaraProc.setAlvara(alvara);
        selecionado.getAlvaras().add(alvaraProc);
    }

    public void removerAlvaraEscolha(Alvara alvara) {
        selecionado.getAlvaras().remove(selecionado.getAlvaras().stream().filter(ap -> ap.getAlvara().equals(alvara)).findFirst().get());
    }

    public boolean alvaraSelecionado(Alvara alvara) {
        return selecionado.getAlvaras().stream().anyMatch(ap -> ap.getAlvara().equals(alvara));
    }

    public List<SelectItem> getTiposProcesso() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProcessoSuspensaoCassacaoAlvara tipo : TipoProcessoSuspensaoCassacaoAlvara.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void efetivarProcesso() {
        facade.efetivarProcesso(selecionado);
        FacesUtil.addOperacaoRealizada("Processo efetivado com sucesso.");
        redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void encerrarProcesso() {
        facade.encerrarProcesso(selecionado);
        FacesUtil.addOperacaoRealizada("Processo encerrado com sucesso.");
        redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (cadastroEconomico == null) {
            FacesUtil.addOperacaoNaoPermitida("Infome um Cadastro Econômico.");
            return false;
        }
        if (selecionado.getAlvaras().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Infome ao menos um alvará.");
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        if (!selecionado.processoAberto()) return;
        if (TipoProcessoSuspensaoCassacaoAlvara.CASSACAO.equals(selecionado.getTipoProcesso())) {
            selecionado.setSituacao(SituacaoProcessoSuspensaoCassacaoAlvara.ENCERRADO);
        }
        if (isOperacaoNovo()) {
            if (!validaRegrasParaSalvar()) return;
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } else {
            super.salvar(Redirecionar.VER);
        }
    }

    public List<ActionConsultaEntidade> getActionsPesquisa() {
        ActionConsultaEntidade actionEditar = new ActionsComCondicao();
        actionEditar.setIcone("ui-icon-pencil");
        actionEditar.setTitle("Clique para editar este registro.");
        actionEditar.setHref("../editar/$identificador/");

        ActionConsultaEntidade actionVer = new ActionConsultaEntidade();
        actionVer.setIcone("ui-icon-search");
        actionVer.setTitle("Clique para visualizar este registro.");
        actionVer.setHref("../ver/$identificador/");
        return Lists.newArrayList(actionVer, actionEditar);
    }

    public class ActionsComCondicao extends ActionConsultaEntidade {
        @Override
        public boolean canRenderizar(Map<String, Object> registro) {
            Long idProcesso = ((Number) registro.get("identificador")).longValue();
            return facade.verificarSituacaoProcesso(idProcesso, SituacaoProcessoSuspensaoCassacaoAlvara.EM_ABERTO);
        }
    }

    public void enviarDeclaracaoDispensaLicenciamentoRedeSim(VOAlvara voAlvara) {
        try {
            ProcessoCalculoAlvara processoCalculoAlvara = facade.getCalculoAlvaraFacade().recuperar(voAlvara.getId());
            facade.getCalculoAlvaraFacade().enviarDispensaLicenciamento(processoCalculoAlvara);
            FacesUtil.addOperacaoRealizada("Declaração de Dispensa de Licenciamento enviada para RedeSim com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao enviar declaração de dispensa de licenciamento a RedeSim. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar declaração de dispensa de licenciamento a RedeSim.", e);
            FacesUtil.addErrorPadrao(e);
        }
    }

    private ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracao == null) {
            configuracao = facade.getCalculoAlvaraFacade().buscarConfiguracaoTriutarioVigente();
        }
        return configuracao;
    }

    public void enviarAlvaraParaRedeSim(Long idProcessoCalculo) {
        try {
            if (idProcessoCalculo != null) {
                ProcessoCalculoAlvara processoCalculo = facade.getCalculoAlvaraFacade().recuperarParaEnvioRedeSim(idProcessoCalculo);
                facade.getCalculoAlvaraFacade().enviarAlvara(processoCalculo, getConfiguracaoTributario());
                FacesUtil.addOperacaoRealizada("Alvarás enviados com sucesso!");
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar alvaras. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar os alvarás para RedeSim. Detalhes: " + e.getMessage());
        }
    }

    public boolean desabilitarEmitirAlvara(VOAlvara voAlvara) {
        if (TipoVerificacaoDebitoAlvara.PERMITIR_AVISAR.equals(getConfiguracaoTributario().getTipoVerificacaoDebitoAlvara())) {
            return false;
        }
        return !voAlvara.getEmitir();
    }
}
