package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.EfetivacaoSolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.SolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoSolicitacaoIncorporacaoMovelFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 03/02/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoIncorporacaoMovel", pattern = "/efetivacao-solicitacao-incorporacao-movel/novo/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/efetivacao/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoIncorporacaoMovel", pattern = "/efetivacao-solicitacao-incorporacao-movel/editar/#{efetivacaoSolicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/efetivacao/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoIncorporacaoMovel", pattern = "/efetivacao-solicitacao-incorporacao-movel/listar/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/efetivacao/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoIncorporacaoMovel", pattern = "/efetivacao-solicitacao-incorporacao-movel/ver/#{efetivacaoSolicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/efetivacao/visualizar.xhtml")
})
public class EfetivacaoSolicitacaoIncorporacaoMovelControlador extends PrettyControlador<EfetivacaoSolicitacaoIncorporacaoMovel> implements Serializable, CRUD {

    @EJB
    private EfetivacaoSolicitacaoIncorporacaoMovelFacade facade;
    private SolicitacaoIncorporacaoMovel solicitacaoIncorporacaoMovel;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    private RelatorioEfetivacaoIncorporacaoControlador relatorioEfetivacaoIncorporacaoControlador;

    public EfetivacaoSolicitacaoIncorporacaoMovelControlador() {
        super(EfetivacaoSolicitacaoIncorporacaoMovel.class);
        relatorioEfetivacaoIncorporacaoControlador = (RelatorioEfetivacaoIncorporacaoControlador) Util.getControladorPeloNome("relatorioEfetivacaoIncorporacaoControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-solicitacao-incorporacao-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaEfetivacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setDateEfetivaacao(getDataOperacao());
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verEfetivacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        setSolicitacaoIncorporacaoMovel(selecionado.getSolicitacaoIncorporacao());
    }

    @URLAction(mappingId = "editarEfetivacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        FacesUtil.addOperacaoNaoPermitida("A Efetivação de Solicitação de Incorporação Móvel não pode ser editada.");
        redireciona();
    }

    @Override
    public void salvar() {
        try {
            validarSalvarEfetivacao();
            selecionado = facade.salvarIncorporacao(selecionado, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            if (selecionado.getId() != null) {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvarEfetivacao() {
        Util.validarCampos(selecionado);
        verificarSeUsuarioIsGestorPatrimonioDaUnidade();
        validarRegrasConfiguracaoMovimentacaoBem();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDateEfetivaacao(), OperacaoMovimentacaoBem.INCORPORACAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDateEfetivaacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public SolicitacaoIncorporacaoMovel getSolicitacaoIncorporacaoMovel() {
        return solicitacaoIncorporacaoMovel;
    }

    public void setSolicitacaoIncorporacaoMovel(SolicitacaoIncorporacaoMovel solicitacaoIncorporacaoMovel) {
        if (solicitacaoIncorporacaoMovel != null) {
            this.solicitacaoIncorporacaoMovel = solicitacaoIncorporacaoMovel;
            selecionado.setSolicitacaoIncorporacao(solicitacaoIncorporacaoMovel);
            recuperarSolicitacao();
        }
    }

    public void recuperarSolicitacao() {
        if (this.solicitacaoIncorporacaoMovel != null) {
            this.solicitacaoIncorporacaoMovel = facade.getSolicitacaoIncorporacaoMovelFacade().recuperar(this.solicitacaoIncorporacaoMovel.getId());
        }
    }

    public void verificarSeUsuarioIsGestorPatrimonioDaUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoIncorporacaoMovel != null) {
            List<UnidadeOrganizacional> unidades = facade.getHierarquiaOrganizacionalFacade().buscarUnidadesOndeUsuarioEhGestorPatrimonio(facade.getSistemaFacade().getUsuarioCorrente(),
                getDataOperacao(), solicitacaoIncorporacaoMovel.getUnidadeAdministrativa());
            if (unidades.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário deve ser Gestor da unidade de Solicitação de Incorporação de bem Móvel.");

            }
            ve.lancarException();
        }
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            relatorioEfetivacaoIncorporacaoControlador.gerarRelatorio(selecionado, tipoRelatorioExtensao);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório", e);
        }
    }

}
