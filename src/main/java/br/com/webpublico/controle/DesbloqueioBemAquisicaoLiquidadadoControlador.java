package br.com.webpublico.controle;

import br.com.webpublico.entidades.Aquisicao;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidadesauxiliares.DesbloqueioBemAquisicaoDoctoAquisicao;
import br.com.webpublico.entidadesauxiliares.DesbloqueioBemAquisicaoLiquidado;
import br.com.webpublico.entidadesauxiliares.ItemDoctoFiscalAquisicao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.AgrupadorMovimentoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.DesbloqueioBemAquisicaoLiquidadadoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMapping(id = "desbloqueio-bem-aquisicao", pattern = "/desbloqueio-bem-aquisicao-liquidado/", viewId = "/faces/administrativo/patrimonio/desbloqueio-bem-aquisicao/edita.xhtml")

public class DesbloqueioBemAquisicaoLiquidadadoControlador implements Serializable {

    @EJB
    private DesbloqueioBemAquisicaoLiquidadadoFacade facade;
    private DesbloqueioBemAquisicaoLiquidado selecionado;

    @URLAction(mappingId = "desbloqueio-bem-aquisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novaDesbloqueio();
    }

    public void salvar() {
        try {
            validarCamposObrigatorios();
            facade.salvarDesbloqueio(selecionado);
            FacesUtil.addOperacaoRealizada("Bens Desbloqueados com Sucesso!");
            novo();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/");
    }

    public void novaDesbloqueio() {
        selecionado = new DesbloqueioBemAquisicaoLiquidado();
    }

    public void limparDadosAquisicao() {
        selecionado.setAquisicao(null);
        selecionado.setDocumentosAquisicao(Lists.newArrayList());
    }

    public List<Aquisicao> completarAquisicao(String parte) {
        return facade.getEfetivacaoAquisicaoFacade().buscarAquisicaoAguardandoLiquidacao(parte.trim());
    }

    public void distribuirValorLiquidado(DesbloqueioBemAquisicaoDoctoAquisicao docto) {
        for (ItemDoctoFiscalAquisicao item : docto.getItensDocumento()) {
            item.getEstadoBem().setValorLiquidado(item.getEstadoBem().getValorOriginal());
            item.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            if (AgrupadorMovimentoBem.AQUISICAO.equals(item.getMovimentoBloqueioBem().getAgrupador())) {
                item.getBem().getMovimentosBloqueioBem().remove(item.getMovimentoBloqueioBem());
            }
            item.setMovimentoBloqueioBem(item.getBem().getMovimentosBloqueioBem().get(0));
            List<EstadoBem> estadosBem = facade.getBemFacade().buscarEstadoResultanteBemPosteriorAData(item.getBem(), item.getBem().getDataAquisicao());
            item.setEstadosBem(estadosBem);
        }
        facade.gerarOrigemRecursoAndNotasEmpenho(docto);
        facade.preencherNotas(docto);
        docto.setDocumentoDesbloqueado(true);
    }

    public void selecionarDocumentoLiquidacao(DesbloqueioBemAquisicaoDoctoAquisicao docto) {
        selecionado.setDocumentoLiquidacaoSelecionado(docto);
    }

    public void buscarDocumentoFiscalAquisicao() {
        try {
            List<DesbloqueioBemAquisicaoDoctoAquisicao> documentos = facade.buscarDocumentos(selecionado.getAquisicao());
            if (documentos.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum documento fiscal encontrado para a aquisição " + selecionado.getAquisicao() + ".");
                return;
            }
            selecionado.setDocumentosAquisicao(documentos);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAquisicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo efetivação de aquisição deve ser informado.");
        }
        ve.lancarException();
        selecionado.getDocumentosAquisicao().stream()
            .filter(doc -> !doc.getDocumentoDesbloqueado())
            .map(doc -> "Para continuar, é necessário distribuir o valor liquidado dos bens em todos os documentos, clicando no botão 'Distribuir Valor Liquidado'")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();

        selecionado.getDocumentosAquisicao().stream().flatMap(docto -> docto.getItensDocumento().stream()).forEach(item -> {
            if (item.getEstadoBem().getValorOriginal().compareTo(item.getEstadoBem().getValorLiquidado()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor liquidado deve ser igual ao valor original do bem.");
            }
        });
        ve.lancarException();
    }

    public DesbloqueioBemAquisicaoLiquidado getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(DesbloqueioBemAquisicaoLiquidado selecionado) {
        this.selecionado = selecionado;
    }
}
