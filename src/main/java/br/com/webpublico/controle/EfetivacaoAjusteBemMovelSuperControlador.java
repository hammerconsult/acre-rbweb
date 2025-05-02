package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.EfetivacaoAjusteBemMovel;
import br.com.webpublico.entidades.SolicitacaoAjusteBemMovel;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoAjusteBemMovelFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mga on 08/03/2018.
 */
public class EfetivacaoAjusteBemMovelSuperControlador extends PrettyControlador<EfetivacaoAjusteBemMovel> implements Serializable, CRUD {

    @EJB
    private EfetivacaoAjusteBemMovelFacade facade;
    private SituacaoMovimentoAdministrativo situacao;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public EfetivacaoAjusteBemMovelSuperControlador() {
        super(EfetivacaoAjusteBemMovel.class);
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.getTipoAjusteBemMovel().isOriginal()) {
            return "/efetivacao-ajuste-bem-movel-original/";
        } else if (selecionado.getTipoAjusteBemMovel().isDepreciacao()) {
            return "/efetivacao-ajuste-bem-movel-depreciacao/";
        } else {
            return "/efetivacao-ajuste-bem-movel-amortizacao/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setDataEfetivacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioEfetivacao(facade.getSistemaFacade().getUsuarioCorrente());
        try {
            novoAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    @Override
    public void salvar() {
        try {
            realizarValidacoes();
            bloquearEfetivacaoSingleton();
            if (selecionado.isSolicitacaoAceita()) {
                facade.simularContabilizacaoAjusteValorBem(assistenteMovimentacao);
            }
            assistenteMovimentacao.setFutureSalvar(facade.salvarEfetivacao(assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearEfetivacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearEfetivacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearEfetivacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void bloquearEfetivacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(EfetivacaoAjusteBemMovel.class, selecionado.getSolicitacaoAjusteBemMovel().getUnidadeAdministrativa(), assistenteMovimentacao);
    }

    private void desbloquearEfetivacaoSingleton() {
        if (hasSolicitacaoSelecionada()) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(EfetivacaoAjusteBemMovel.class, selecionado.getSolicitacaoAjusteBemMovel().getUnidadeAdministrativa());
        }
    }

    public void recuperarConfiguracaoMovimentacaoBem() {
        if (hasSolicitacaoSelecionada()) {
            ConfigMovimentacaoBem config = facade.getSolicitacaoAjusteBemMovelFacade().recuperarConfiguracaoMovimentacaoBem(selecionado.getSolicitacaoAjusteBemMovel());
            assistenteMovimentacao.setConfigMovimentacaoBem(config);
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            ConfigMovimentacaoBem configMovimentacaoBem = assistenteMovimentacao.getConfigMovimentacaoBem();
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), facade.getSistemaFacade().getDataOperacao(), operacao);
        }
    }

    public void consultarFutureSalvar() {
        if (assistenteMovimentacao.getFutureSalvar() != null && assistenteMovimentacao.getFutureSalvar().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getFutureSalvar().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (EfetivacaoAjusteBemMovel) assistente.getSelecionado();
                    desbloquearEfetivacaoSingleton();
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.setBloquearAcoesTela(true);
                assistenteMovimentacao.descobrirETratarException(ex);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                assistenteMovimentacao.setFutureSalvar(null);
                desbloquearEfetivacaoSingleton();
            }
        }
    }

    public void finalizarFutureSalvar() {
        getAssistenteMovimentacao().setFutureSalvar(null);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    private void realizarValidacoes() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (situacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo avaliar deve ser informado.");
        }
        if (selecionado.isSolicitacaoRejeitada() && Util.isStringNulaOuVazia(selecionado.getSolicitacaoAjusteBemMovel().getMotivoRejeicao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo da rejeição deve ser informado.");
        }
        if (!assistenteMovimentacao.hasBensMovimentadosVo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum bem encontrado para esta solicitação.");
        }
        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        if (selecionado.getUnidadeAdministrativa() != null) {
            assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getUnidadeAdministrativa());
        }
    }

    public void atribuirNullDadosSolicitacao() {
        if (hasSolicitacaoSelecionada()) {
            selecionado.getSolicitacaoAjusteBemMovel().setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
            selecionado.getSolicitacaoAjusteBemMovel().setMotivoRejeicao(null);
            selecionado.setSolicitacaoAjusteBemMovel(null);
            assistenteMovimentacao.setBensMovimentadosVo(new ArrayList<BemVo>());
            situacao = null;
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(SituacaoMovimentoAdministrativo.FINALIZADO, "Aceitar"));
        toReturn.add(new SelectItem(SituacaoMovimentoAdministrativo.RECUSADO, "Rejeitar"));
        return toReturn;
    }

    public List<SolicitacaoAjusteBemMovel> completarSolicitacao(String parte) {
        return facade.getSolicitacaoAjusteBemMovelFacade().buscarSolicitacaoPorSituacao(parte.trim(), SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO, selecionado.getTipoAjusteBemMovel());
    }

    public void pesquisarBens() {
        try {
            if (isOperacaoVer()) {
                novoAssistenteMovimentacao();
                assistenteMovimentacao.setFuturePesquisaBemVo(facade.pesquisarAssincronoBensEfetivacao(assistenteMovimentacao));
            } else {
                if (hasSolicitacaoSelecionada()) {
                    selecionado.setUnidadeAdministrativa(selecionado.getSolicitacaoAjusteBemMovel().getUnidadeAdministrativa());
                    recuperarConfiguracaoMovimentacaoBem();
                    validarDataLancamentoAndDataOperacaoBem();
                    assistenteMovimentacao.setFuturePesquisaBemVo(facade.pesquisarBensSolicitacao(assistenteMovimentacao));
                }
            }
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (assistenteMovimentacao.getFuturePesquisaBemVo() != null && assistenteMovimentacao.getFuturePesquisaBemVo().isDone()) {
            List<BemVo> bens = assistenteMovimentacao.getFuturePesquisaBemVo().get();
            assistenteMovimentacao.setBensMovimentadosVo(bens);

            if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
                for (String mensagem : assistenteMovimentacao.getMensagens()) {
                    FacesUtil.addOperacaoNaoPermitida(mensagem);
                }
            }
            assistenteMovimentacao.setFuturePesquisaBemVo(null);
            FacesUtil.executaJavaScript("finalizarPesquisa()");
        }
    }

    public void finalizarPesquisa() {
        FacesUtil.executaJavaScript("atualizaFormulario()");
    }

    public void atribuirSituacaoNaSolicitacao() {
        if (hasSolicitacaoSelecionada()) {
            assistenteMovimentacao.setSituacaoMovimento(situacao);
            selecionado.getSolicitacaoAjusteBemMovel().setSituacao(situacao);
            selecionado.getSolicitacaoAjusteBemMovel().setMotivoRejeicao(null);
        }
    }

    private boolean hasSolicitacaoSelecionada() {
        return selecionado.getSolicitacaoAjusteBemMovel() != null;
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            String nomeRelatorio = "RELATÓRIO DE EFETIVAÇÃO DE AJUSTE DE BENS MÓVEIS - " + selecionado.getTipoAjusteBemMovel().getDescricao().toUpperCase();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SOLICITACAO_REJEITADA", selecionado.isSolicitacaoRejeitada());
            dto.adicionarParametro("TIPO_AJUSTE", selecionado.getTipoAjusteBemMovel().getDescricao());
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("operacao", selecionado.getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel().getToDto());
            dto.adicionarParametro("idEfetivacao", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/efetivacao-ajuste-bem-movel/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacao) {
        this.situacao = situacao;
    }
}
