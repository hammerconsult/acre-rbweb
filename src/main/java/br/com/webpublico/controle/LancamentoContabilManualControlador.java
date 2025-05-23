package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LancamentoContabilManualFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-lancamento-contabil-manual", pattern = "/lancamento-contabil-manual/novo/", viewId = "/faces/financeiro/lancamento-contabil-manual/edita.xhtml"),
    @URLMapping(id = "editar-lancamento-contabil-manual", pattern = "/lancamento-contabil-manual/editar/#{lancamentoContabilManualControlador.id}/", viewId = "/faces/financeiro/lancamento-contabil-manual/edita.xhtml"),
    @URLMapping(id = "listar-lancamento-contabil-manual", pattern = "/lancamento-contabil-manual/listar/", viewId = "/faces/financeiro/lancamento-contabil-manual/lista.xhtml"),
    @URLMapping(id = "ver-lancamento-contabil-manual", pattern = "/lancamento-contabil-manual/ver/#{lancamentoContabilManualControlador.id}/", viewId = "/faces/financeiro/lancamento-contabil-manual/visualizar.xhtml")
})
public class LancamentoContabilManualControlador extends PrettyControlador<LancamentoContabilManual> implements Serializable, CRUD {
    @EJB
    private LancamentoContabilManualFacade facade;
    private ConfiguracaoContabil configuracaoContabil;
    private ContaLancamentoManual conta;
    private List<String> camposAdicionaisPCM;
    private List<String> tiposSelecionados;

    public LancamentoContabilManualControlador() {
        super(LancamentoContabilManual.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-contabil-manual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-lancamento-contabil-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(facade.getDataOperacao());
        selecionado.setExercicio(facade.getExercicioCorrente());
        selecionado.setTipoEventoContabil(TipoEventoContabil.AJUSTE_CONTABIL_MANUAL);
        selecionado.setUnidadeOrganizacional(facade.getUnidadeOrganizacionalOrcamentoCorrente());
        recuperarConfiguracaoContabil();
        camposAdicionaisPCM = Lists.newArrayList();
        tiposSelecionados = Lists.newArrayList(TipoPrestacaoDeContas.SISTEMA.name(), TipoPrestacaoDeContas.SICONFI.name(), TipoPrestacaoDeContas.PCM.name());
    }

    @URLAction(mappingId = "ver-lancamento-contabil-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        atualizarCamposInformacoesAdicionaisParaRenderizar();
        atualizarTiposSelecionados();
    }

    @URLAction(mappingId = "editar-lancamento-contabil-manual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarConfiguracaoContabil();
        atualizarCamposInformacoesAdicionaisParaRenderizar();
        atualizarTiposSelecionados();
    }

    @Override
    protected String getMensagemSucessoAoSalvar() {
        return "O Lançamento Contábil Manual " + selecionado.getNumero() + " foi salvo com sucesso.";
    }

    @Override
    protected String getMensagemSucessoAoExcluir() {
        return "O Lançamento Contábil Manual " + selecionado.getNumero() + " foi excluído com sucesso.";
    }

    @Override
    public void salvar() {
        try {
            if (validaRegrasParaSalvar()) {
                if (Operacoes.NOVO.equals(operacao)) {
                    atualizarTiposDoSelecionadoAoSalvarNovo();
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                depoisDeSalvar();
                redireciona();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void atualizarTiposDoSelecionadoAoSalvarNovo() {
        for (String tipo : tiposSelecionados) {
            TipoLancamentoManual tipoLancamentoManual = new TipoLancamentoManual();
            tipoLancamentoManual.setLancamento(selecionado);
            tipoLancamentoManual.setTipoPrestacaoDeContas(TipoPrestacaoDeContas.valueOf(tipo));
            selecionado.getTiposDePrestacoesDeContas().add(tipoLancamentoManual);
        }
    }

    private void recuperarConfiguracaoContabil() {
        configuracaoContabil = facade.buscarConfiguracaoContabilVigente();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return facade.buscarHierarquiaOrganizacional(parte, selecionado.getData());
    }


    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return facade.buscarContasDeDestinacao(parte);
    }

    public List<ContaContabil> completarContasContabeis(String parte) {
        if (conta != null) {
            return facade.buscarContasContabeis(parte, conta.getContaSintetica());
        }
        return Lists.newArrayList();
    }

    public void selecionarEventoContabil() {
        if (selecionado.getEventoContabil() != null) {
            selecionado.getContas().clear();
            List<CLP> clps = facade.buscarCLPComContasPorEventoContabil(selecionado.getEventoContabil(), selecionado.getData());
            for (CLP clp : clps) {
                for (LCP lcp : clp.getLcps()) {
                    criarContaLancamentoManual(lcp, TipoLancamentoContabil.DEBITO);
                    criarContaLancamentoManual(lcp, TipoLancamentoContabil.CREDITO);
                }
            }
        }
        limparCamposAdicionais();
        atualizarCamposInformacoesAdicionaisParaRenderizar();
    }

    private void limparCamposAdicionais() {
        selecionado.setPessoa(null);
        selecionado.setContaReceita(null);
        selecionado.setContaDespesa(null);
        selecionado.setFuncao(null);
        selecionado.setSubFuncao(null);
        selecionado.setProgramaPPA(null);
        selecionado.setAcaoPPA(null);
        selecionado.setExtensaoFonteRecurso(null);
        selecionado.setSubConta(null);
        selecionado.setTipoDocPagto(null);
        selecionado.setNumeroDocumentoFinanceiro(null);
        selecionado.setExercicioResto(null);
    }

    private void criarContaLancamentoManual(LCP lcp, TipoLancamentoContabil tipoLancamento) {
        Conta contaContabil = TipoLancamentoContabil.CREDITO.equals(tipoLancamento) ? lcp.getContaCredito() : lcp.getContaDebito();
        if (contaContabil != null) {
            ContaLancamentoManual contaManual = new ContaLancamentoManual();
            contaManual.setLancamento(selecionado);
            contaManual.setLcp(lcp);
            contaManual.setTipo(tipoLancamento);
            if (CategoriaConta.SINTETICA.equals(contaContabil.getCategoria())) {
                contaManual.setContaSintetica(contaContabil);
            } else {
                contaManual.setContaContabil((ContaContabil) contaContabil);
            }
            selecionado.getContas().add(contaManual);
        }
    }

    public void editarConta(ContaLancamentoManual contaLancamentoManual) {
        conta = (ContaLancamentoManual) Util.clonarObjeto(contaLancamentoManual);
    }

    public void adicionarConta() {
        Util.adicionarObjetoEmLista(selecionado.getContas(), conta);
        cancelarConta();
    }

    public void cancelarConta() {
        conta = null;
    }

    public List<EventoContabil> buscarEventoContabil(String filtro) {
        if (selecionado.getTipoLancamento() != null) {
            return facade.buscarEventoContabil(filtro, selecionado.getTipoLancamento());
        }
        return Lists.newArrayList();
    }

    public List<Conta> buscarContaReceita(String parte) {
        return facade.buscarContaReceita(parte.trim());
    }

    public List<Conta> buscarContasDespesa(String parte) {
        return facade.buscarContasDespesa(parte.trim());
    }

    public List<Funcao> buscarFuncoes(String parte) {
        return facade.buscarFuncoes(parte);
    }

    public List<SubFuncao> buscarSubFuncoes(String parte) {
        return facade.buscarSubFuncoes(parte);
    }

    public List<ProgramaPPA> buscarProgramas(String parte) {
        return facade.buscarProgramas(parte, selecionado.getExercicio());
    }

    public List<AcaoPPA> buscarAcoesPPAs(String parte) {
        return facade.buscarAcoesPPAs(parte, selecionado.getExercicio(), selecionado.getProgramaPPA());
    }

    public List<ExtensaoFonteRecurso> buscarExtensoes(String parte) {
        return facade.buscarExtensoes(parte);
    }

    public List<Pessoa> buscarCredores(String parte) {
        return facade.buscarCredores(parte);
    }

    public List<SubConta> buscarContasFinanceiras(String parte) {
        if (selecionado.getUnidadeOrganizacional() != null) {
            return facade.buscarContasFinanceiras(parte, selecionado.getUnidadeOrganizacional(), selecionado.getExercicio());
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getTiposDoctosPagamentos() {
        return Util.getListSelectItem(TipoDocPagto.values());
    }

    public List<SelectItem> getTipoLancamento() {
        return Util.getListSelectItem(TipoLancamento.values());
    }

    public List<TipoPrestacaoDeContas> getTiposPrestacoes() {
        return Arrays.asList(TipoPrestacaoDeContas.values());
    }

    private void atualizarTiposSelecionados() {
        tiposSelecionados = Lists.newArrayList();
        selecionado.getTiposDePrestacoesDeContas().forEach(tipoLancamento -> tiposSelecionados.add(tipoLancamento.getTipoPrestacaoDeContas().name()));
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public ContaLancamentoManual getConta() {
        return conta;
    }

    public void setConta(ContaLancamentoManual conta) {
        this.conta = conta;
    }


    public void redirecionarParaEventoContabil() {
        Web.poeNaSessao("IS_LANCAMENTO_MANUAL", Boolean.TRUE);
        Web.poeNaSessao(selecionado);
        Web.setCaminhoOrigem(getCaminhoPadrao() + "novo/");
        FacesUtil.redirecionamentoInterno("/eventocontabil/novo/");
        Web.getSessionMap().put(Web.ESPERARETORNO, true);
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }

    public List<String> getCamposAdicionaisPCM() {
        return camposAdicionaisPCM;
    }

    public void setCamposAdicionaisPCM(List<String> camposAdicionaisPCM) {
        this.camposAdicionaisPCM = camposAdicionaisPCM;
    }

    public List<String> getTiposSelecionados() {
        return tiposSelecionados;
    }

    public void setTiposSelecionados(List<String> tiposSelecionados) {
        this.tiposSelecionados = tiposSelecionados;
    }

    public String getDescricaoTiposSelecionados() {
        String retorno = "";
        if (selecionado.getTiposDePrestacoesDeContas() != null && !selecionado.getTiposDePrestacoesDeContas().isEmpty()) {
            String juncao = "";
            for (TipoLancamentoManual tipoLancamento : selecionado.getTiposDePrestacoesDeContas()) {
                retorno += juncao + tipoLancamento.getTipoPrestacaoDeContas().getDescricao();
                juncao = ", ";
            }
        }
        return retorno;
    }

    public void atualizarCamposInformacoesAdicionaisParaRenderizar() {
        camposAdicionaisPCM = Lists.newArrayList();
        if (selecionado.getEventoContabil() != null) {
            List<CLP> clps = facade.buscarCLPComContasPorEventoContabil(selecionado.getEventoContabil(), selecionado.getData());
            for (CLP clp : clps) {
                for (LCP lcp : clp.getLcps()) {
                    if (lcp.getTipoMovimentoTCECredito() != null) {
                        camposAdicionaisPCM.add(lcp.getTipoMovimentoTCECredito().name());
                    }
                    if (lcp.getTipoMovimentoTCEDebito() != null) {
                        camposAdicionaisPCM.add(lcp.getTipoMovimentoTCEDebito().name());
                    }
                }
            }
        }
    }

    public boolean canRenderizarCampoQuandoPCM(String chave) {
        try {
            for (String tipo : tiposSelecionados) {
                if (TipoPrestacaoDeContas.PCM.name().equals(tipo)) {
                    return camposAdicionaisPCM.contains(chave);
                }
            }
            return false;
        } catch (Exception ex) {
            logger.error("Erro no método canRenderizarCampoQuandoPCM: ", ex);
            return false;
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (tiposSelecionados.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Contabilização deve ter um ou mais tipos selecionados.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que 0(zero)!");
        }
        for (String campoAdicional : camposAdicionaisPCM) {
            TipoMovimentoTCE tipoMovimentoTCE = TipoMovimentoTCE.valueOf(campoAdicional);
            switch (tipoMovimentoTCE) {
                case RECEITA_ORCAMENTARIA:
                    if (selecionado.getContaReceita() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Receita deve ser informado.");
                    }
                    break;

                case DOTACAO:
                    if (selecionado.getFuncao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Função deve ser informado.");
                    }
                    if (selecionado.getSubFuncao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Subfunção deve ser informado.");
                    }
                    if (selecionado.getProgramaPPA() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Programa PPA deve ser informado.");
                    }
                    if (selecionado.getAcaoPPA() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Projeto Atividade deve ser informado.");
                    }
                    if (selecionado.getContaDespesa() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
                    }
                    break;

                case CREDOR:
                    if (selecionado.getPessoa() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Credor deve ser informado.");
                    }
                    break;

                case MOVIMENTACAO_FINANCEIRA:
                    if (selecionado.getSubConta() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
                    }
                    break;
            }
        }
        ve.lancarException();
        return true;
    }
}
