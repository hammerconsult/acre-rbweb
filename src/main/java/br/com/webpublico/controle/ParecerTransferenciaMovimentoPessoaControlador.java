package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.ParecerTransferenciaMovimentoPessoa;
import br.com.webpublico.entidades.TransferenciaMovPessoa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ParecerTransferenciaMovimentoPessoaFacade;
import br.com.webpublico.negocios.TransferenciaMovPessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/15
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-parecer-transf-mov-pessoa", pattern = "/parecer-transferencia-mov-pessoa/novo/", viewId = "/faces/tributario/cadastromunicipal/pessoaparecertransf/edita.xhtml"),
    @URLMapping(id = "ver-parecer-transf-mov-pessoa", pattern = "/parecer-transferencia-mov-pessoa/ver/#{parecerTransferenciaMovimentoPessoaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoaparecertransf/visualizar.xhtml"),
    @URLMapping(id = "listar-parecer-transf-mov-pessoa", pattern = "/parecer-transferencia-mov-pessoa/listar/", viewId = "/faces/tributario/cadastromunicipal/pessoaparecertransf/lista.xhtml")
})
public class ParecerTransferenciaMovimentoPessoaControlador extends PrettyControlador<ParecerTransferenciaMovimentoPessoa> implements Serializable, CRUD {

    @EJB
    private ParecerTransferenciaMovimentoPessoaFacade parecerTransferenciaMovimentoPessoaFacade;
    @EJB
    private TransferenciaMovPessoaFacade transferenciaMovPessoaFacade;
    private ConverterAutoComplete converterTransferenciaMovPessoa;
    private TransferenciaMovPessoa transferenciaMovPessoa;
    Future<TransferenciaMovPessoa> future;

    public ParecerTransferenciaMovimentoPessoaControlador() {
        super(ParecerTransferenciaMovimentoPessoa.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parecerTransferenciaMovimentoPessoaFacade;
    }

    @URLAction(mappingId = "novo-parecer-transf-mov-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setRealizadoEm(parecerTransferenciaMovimentoPessoaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(parecerTransferenciaMovimentoPessoaFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-parecer-transf-mov-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.setTransferenciaMovPessoa(parecerTransferenciaMovimentoPessoaFacade.getTransferenciaMovPessoaFacade().recuperar(selecionado.getTransferenciaMovPessoa().getId()));
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parecer-transferencia-mov-pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void deferir() {
        if (Util.validaCampos(selecionado)) {
            try {
                validarTransferenciasAnteriores();
                AbstractReport ar = AbstractReport.getAbstractReport();
                ParametrosRelatorioTransferenciaPessoa parametro = new ParametrosRelatorioTransferenciaPessoa(ar.getCaminho(), ar.getCaminhoImagem(), transferenciaMovPessoa.getUsuarioSistema(), ar.getCaminho());
                future = parecerTransferenciaMovimentoPessoaFacade.deferirParecerSolicitacao(selecionado, parametro, parecerTransferenciaMovimentoPessoaFacade.getSistemaFacade().getUsuarioCorrente(), parecerTransferenciaMovimentoPessoaFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
                FacesUtil.executaJavaScript("aguarde.hide()");
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        } else {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void validarTransferenciasAnteriores() {
        ValidacaoException ve = new ValidacaoException();
        List<TransferenciaMovPessoa> trasferencias = transferenciaMovPessoaFacade.buscarTransferenciaMovPessoaPorPessoaAbertas(selecionado.getTransferenciaMovPessoa().getPessoaOrigem());
        trasferencias.addAll(transferenciaMovPessoaFacade.buscarTransferenciaMovPessoaPorPessoaAbertas(selecionado.getTransferenciaMovPessoa().getPessoaDestino()));
        for (TransferenciaMovPessoa trasferencia : trasferencias) {
            if (!trasferencia.getId().equals(selecionado.getTransferenciaMovPessoa().getId())
                && selecionado.getTransferenciaMovPessoa().getDataTransferencia().after(trasferencia.getDataTransferencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe solicitação anterior em aberto de transferência de movimentos da pessoa envolvendo a pessoa de origem ou destino desta solicitação.");
                break;
            }
        }
        ve.lancarException();
    }

    public void verificarSeAcabou() {
        if (future != null && future.isDone()) {
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        }
    }

    public void indeferir() {
        if (Util.validaCampos(selecionado)) {
            try {
                parecerTransferenciaMovimentoPessoaFacade.indeferirParecerSolicitacao(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redireciona();
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        }
    }

    public ConverterAutoComplete getConverterTransferenciaMovPessoa() {
        if (converterTransferenciaMovPessoa == null) {
            converterTransferenciaMovPessoa = new ConverterAutoComplete(TransferenciaMovPessoa.class, parecerTransferenciaMovimentoPessoaFacade.getTransferenciaMovPessoaFacade());
        }
        return converterTransferenciaMovPessoa;
    }

    public List<TransferenciaMovPessoa> completarTransferenciaMovPessoa(String parte) {
        try {
            return parecerTransferenciaMovimentoPessoaFacade.getTransferenciaMovPessoaFacade().buscarSolicitacaoTransfMoviPessoaAberta(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void definirTransferenciaMovPessoa() {
        selecionado.setTransferenciaMovPessoa(parecerTransferenciaMovimentoPessoaFacade.getTransferenciaMovPessoaFacade().recuperar(transferenciaMovPessoa.getId()));
    }

    public void selecionarTransferenciaMovPessoa(ActionEvent e) {
        transferenciaMovPessoa = (TransferenciaMovPessoa) e.getComponent().getAttributes().get("objeto");
        selecionado.setTransferenciaMovPessoa(parecerTransferenciaMovimentoPessoaFacade.getTransferenciaMovPessoaFacade().recuperar(transferenciaMovPessoa.getId()));
    }


    public StreamedContent montarArquivoParaDownload(ArquivoComposicao arquivo) {
        StreamedContent s = null;
        s = parecerTransferenciaMovimentoPessoaFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivoComposicao(arquivo);
        return s;
    }

    public TransferenciaMovPessoa getTransferenciaMovPessoa() {
        return transferenciaMovPessoa;
    }

    public void setTransferenciaMovPessoa(TransferenciaMovPessoa transferenciaMovPessoa) {
        this.transferenciaMovPessoa = transferenciaMovPessoa;
    }

    public static class ParametrosRelatorioTransferenciaPessoa {
        private String subReport;
        private String caminhoBrasao;
        private UsuarioSistema usuario;
        private String caminhoReport;

        public ParametrosRelatorioTransferenciaPessoa(String subReport, String caminhoBrasao, UsuarioSistema usuario, String caminhoReport) {
            this.subReport = subReport;
            this.caminhoBrasao = caminhoBrasao;
            this.usuario = usuario;
            this.caminhoReport = caminhoReport;
        }

        public ParametrosRelatorioTransferenciaPessoa() {
        }

        public String getSubReport() {
            return subReport;
        }

        public void setSubReport(String subReport) {
            this.subReport = subReport;
        }

        public String getCaminhoBrasao() {
            return caminhoBrasao;
        }

        public void setCaminhoBrasao(String caminhoBrasao) {
            this.caminhoBrasao = caminhoBrasao;
        }

        public UsuarioSistema getUsuario() {
            return usuario;
        }

        public void setUsuario(UsuarioSistema usuario) {
            this.usuario = usuario;
        }

        public String getCaminhoReport() {
            return caminhoReport;
        }

        public void setCaminhoReport(String caminhoReport) {
            this.caminhoReport = caminhoReport;
        }
    }
}
