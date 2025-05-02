package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AvaliacaoProrrogacaoCessaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "avaliacaoProrrogacaoCessaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAvaliacaoProrrogacao", pattern = "/avaliacao-prorrogacao-cessao/novo/", viewId = "/faces/administrativo/patrimonio/avaliacaoprorrogacaocessao/edita.xhtml"),
    @URLMapping(id = "editarAvaliacaoProrrogacao", pattern = "/avaliacao-prorrogacao-cessao/editar/#{avaliacaoProrrogacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaliacaoprorrogacaocessao/edita.xhtml"),
    @URLMapping(id = "listarAvaliacaoProrrogacao", pattern = "/avaliacao-prorrogacao-cessao/listar/", viewId = "/faces/administrativo/patrimonio/avaliacaoprorrogacaocessao/lista.xhtml"),
    @URLMapping(id = "verAvaliacaoProrrogacao", pattern = "/avaliacao-prorrogacao-cessao/ver/#{avaliacaoProrrogacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaliacaoprorrogacaocessao/visualizar.xhtml")
})
public class AvaliacaoProrrogacaoCessaoControlador extends PrettyControlador<AvaliacaoSolicitacaoProrrogacaoCessao> implements Serializable, CRUD {

    @EJB
    private AvaliacaoProrrogacaoCessaoFacade facade;
    private List<Cessao> listaCessoes;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public AvaliacaoProrrogacaoCessaoControlador() {
        super(AvaliacaoSolicitacaoProrrogacaoCessao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoAvaliacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setDataAvaliacao(getDataOperacao());
            selecionado.setAvaliador(facade.getSistemaFacade().getUsuarioCorrente());
            listaCessoes = Lists.newArrayList();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verAvaliacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recarregar();
    }

    @URLAction(mappingId = "editarAvaliacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao-prorrogacao-cessao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        UnidadeOrganizacional unidadeAvaliacao = null;
        try {
            validarAvaliacaoProrrogacaoCessao();
            unidadeAvaliacao = selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().getUnidadeOrigem();
            AssistenteMovimentacaoBens assistente = novoAssistenteMovimentacao();
            bloquearMovimentacaoSingleton(unidadeAvaliacao, assistente);
            selecionado = facade.salvarAvaliacao(selecionado, listaCessoes);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            desbloquearMovimentacaoSingleton(unidadeAvaliacao);
            redirecionaParaVer();
        } catch (MovimentacaoBemException ex) {
            if (facade.getSingletonBloqueioPatrimonio().verificarMovimentoBloqueadoPorUnidade(AvaliacaoSolicitacaoProrrogacaoCessao.class, unidadeAvaliacao)) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton(unidadeAvaliacao);
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton(unidadeAvaliacao);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            if (unidadeAvaliacao != null) {
                desbloquearMovimentacaoSingleton(unidadeAvaliacao);
            }
            descobrirETratarException(e);
        }
    }

    private void desbloquearMovimentacaoSingleton(UnidadeOrganizacional unidadeAvaliacao) {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(AvaliacaoSolicitacaoProrrogacaoCessao.class, unidadeAvaliacao);
    }

    private void bloquearMovimentacaoSingleton(UnidadeOrganizacional unidadeAvaliacao, AssistenteMovimentacaoBens assistente) {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(AvaliacaoSolicitacaoProrrogacaoCessao.class, unidadeAvaliacao, assistente);
    }

    private void validarAvaliacaoProrrogacaoCessao() {
        Util.validarCampos(selecionado);
        validarPermissaoGestorUnidade();
        validarDataLancamentoAndDataOperacaoBem();
        verificarDisponibilidadeBemParaMovimentacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataAvaliacao(), OperacaoMovimentacaoBem.AVALIACAO_PRORROGACAO_CESSAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataAvaliacao(), getDataOperacao(), operacao);
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void verificarDisponibilidadeBemParaMovimentacao() {
        AssistenteMovimentacaoBens assistente = novoAssistenteMovimentacao();
        List<Bem> bensSelecionados = Lists.newArrayList();
        for (Cessao cessao : listaCessoes) {
            bensSelecionados.add(cessao.getBem());
        }
        facade.getConfigMovimentacaoBemFacade().validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensSelecionados, assistente);

        if (assistente.getMensagens() != null && !assistente.getMensagens().isEmpty()) {
            ValidacaoException ve = new ValidacaoException();
            for (String msg : assistente.getMensagens()) {
                ve.adicionarMensagemDeCampoObrigatorio(msg);
            }
            ve.lancarException();
        }
    }

    private AssistenteMovimentacaoBens novoAssistenteMovimentacao() {
        AssistenteMovimentacaoBens assistente = new AssistenteMovimentacaoBens(selecionado.getDataAvaliacao(), operacao);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        return assistente;
    }

    private void validarPermissaoGestorUnidade() {
        ValidacaoException ve = new ValidacaoException();
        try {
            if (!facade.getLoteCessaoFacade().getBemFacade().usuarioIsGestorUnidade(facade.getSistemaFacade().getUsuarioCorrente(), selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().getUnidadeOrigem(),
                getDataOperacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Você " + facade.getSistemaFacade().getUsuarioCorrente().getNome() + " não é gestor pela unidade organizacional " + selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().getUnidadeDestino() + " e portanto não tem autorizacão para efetivação da transferência.");
            }
        } catch (NullPointerException np) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível verifica se o usuário é o gestor da unidade: " + np.getMessage());
        }
        ve.lancarException();
    }

    public void redirecionaParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void recarregar() {
        if (selecionado.getSolicitaProrrogacaoCessao() != null) {
            selecionado.setSolicitaProrrogacaoCessao(facade.getSolicitacaoProrrogacaoCessaoFacade().recuperar(selecionado.getSolicitaProrrogacaoCessao().getId()));
            selecionado.getSolicitaProrrogacaoCessao().setLoteCessao(facade.getLoteCessaoFacade().recuperar(selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().getId()));
            listaCessoes = facade.getLoteCessaoFacade().buscarCessaoParaDevolucao(selecionado.getSolicitaProrrogacaoCessao().getLoteCessao());
        }
    }

    public List<SelectItem> situacao() {
        return Util.getListSelectItem(Arrays.asList(AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao.values()));
    }

    public List<Cessao> getListaCessoes() {
        return listaCessoes;
    }

    public void setListaCessoes(List<Cessao> listaCessoes) {
        this.listaCessoes = listaCessoes;
    }

    public void imprimirTermoDeProrrogacaoDeCessao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao) {
        try {
            String nomeRelatorio =  "TERMO DE PRORROGAÇÃO DE CESSÃO DE USO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("idAvaliacaoSolicitacaoProrrogacaoCessao", avaliacao.getId());
            dto.adicionarParametro("RESPONSAVELORIGEM", getResponsavelUnidadeOrigem());
            dto.adicionarParametro("RESPONSAVELDESTINO", getResponsavelUnidadeDestino());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-prorrogacao-cessao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    public String getResponsavelUnidadeOrigem() {
        return selecionado.getSolicitaProrrogacaoCessao().getResponsavelOrigem().getNome();
    }

    public String getResponsavelUnidadeDestino() {
        return selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().isInterno()
            ? selecionado.getSolicitaProrrogacaoCessao().getResponsavelDestino().getNome()
            : selecionado.getSolicitaProrrogacaoCessao().getLoteCessao().getResponsavelExterno();
    }

    public void limparCampos() {
        selecionado.setSolicitaProrrogacaoCessao(null);
        if (listaCessoes != null) {
            listaCessoes.clear();
        }
    }

    public List<SolicitacaoProrrogacaoCessao> completarSolicitacaoSemAvaliacao(String s) {
        if (selecionado.getTipoCessao() == null) {
            FacesUtil.addCampoObrigatorio("Informe o tipo de cessão.");
            return null;
        }
        List<SolicitacaoProrrogacaoCessao> solicitacoes = facade.getSolicitacaoProrrogacaoCessaoFacade().completarSolicitacaoSemAvaliacao(s, selecionado.getTipoCessao());
        if (!solicitacoes.isEmpty()) {
            return solicitacoes;
        }
        FacesUtil.addAtencao("Solicitação de prorrogação não encontrada. Verifique se a mesma já foi avaliada ou se todos os bens já foram devolvidos.");
        return new ArrayList<>();
    }


}
