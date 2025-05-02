package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoSolicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SolicitacaoDoctoOficialFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 23/02/15
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "cancelamentoSolicitacaoDoctoOficialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarCancelamentoSolicitacaoDoctoOficial", pattern = "/cancelamento-solicitacao-documento-oficial/listar/", viewId = "/faces/tributario/certidao/cancelamentodocumento/lista.xhtml"),
        @URLMapping(id = "novoCancelamentoSolicitacaoDoctoOficial", pattern = "/cancelamento-solicitacao-documento-oficial/novo/", viewId = "/faces/tributario/certidao/cancelamentodocumento/edita.xhtml"),
        @URLMapping(id = "verCancelamentoSolicitacaoDoctoOficial", pattern = "/cancelamento-solicitacao-documento-oficial/ver/#{cancelamentoSolicitacaoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/cancelamentodocumento/visualizar.xhtml")
})


public class CancelamentoSolicitacaoDoctoOficialControlador extends PrettyControlador<SolicitacaoDoctoOficial> implements Serializable, CRUD {
    private Long codigoSolicitacao;
    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<PropriedadeRural> propriedadesRurais;
    private List<Propriedade> propriedades;
    private Lote lote;
    private Testada testada;
    private List<ResultadoParcela> resultadoConsulta;
    private List<SolicitacaoDoctoOficial> solicitacoes;


    public CancelamentoSolicitacaoDoctoOficialControlador() {
        super(SolicitacaoDoctoOficial.class);
        propriedadesRurais = Lists.newArrayList();
        propriedades = Lists.newArrayList();
        resultadoConsulta = Lists.newArrayList();
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoDoctoOficialFacade;
    }

    public SolicitacaoDoctoOficial getSolicitacao() {
        return selecionado;
    }

    public void setSolicitacao(SolicitacaoDoctoOficial solicitacao) {
        this.selecionado = solicitacao;
    }


    public Long getCodigoSolicitacao() {
        return codigoSolicitacao;
    }

    public void setCodigoSolicitacao(Long codigoSolicitacao) {
        this.codigoSolicitacao = codigoSolicitacao;
    }

    public List<PropriedadeRural> getPropriedadesRurais() {
        return propriedadesRurais;
    }

    public void setPropriedadesRurais(List<PropriedadeRural> propriedadesRurais) {
        this.propriedadesRurais = propriedadesRurais;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Testada getTestada() {
        return testada;
    }

    public void setTestada(Testada testada) {
        this.testada = testada;
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<SolicitacaoDoctoOficial> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<SolicitacaoDoctoOficial> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    public void buscarSolicitacao() {
        if (codigoSolicitacao != null) {
            solicitacoes = solicitacaoDoctoOficialFacade.buscarSolicitacoesPorCodigo(codigoSolicitacao);
            if (!solicitacoes.isEmpty()) {
                if (solicitacoes.size() == 1) {
                    selecionado = solicitacoes.get(0);
                    recuperarAtribuirSolicitacao(selecionado);
                } else {
                    FacesUtil.atualizarComponente("formSolicitacoes");
                    FacesUtil.executaJavaScript("solicitacoes.show()");
                }
            } else {
                selecionado = new SolicitacaoDoctoOficial();
                FacesUtil.addOperacaoNaoPermitida("Nenhuma Solicitação de Documento Oficial encontrada com número: " + codigoSolicitacao);
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Informe o número da Solicitação do Documento.");
        }
    }

    public void cancelarSolicitacao() {
        try {
            validarCancelamentoSolicitacao();
            FacesUtil.executaJavaScript("cancelarContrato.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarCancelamento() {
        solicitacaoDoctoOficialFacade.cancelarParcela(resultadoConsulta);
        selecionado.setSituacaoSolicitacao(SituacaoSolicitacao.CANCELADO);
        solicitacaoDoctoOficialFacade.salvar(selecionado);
        redireciona();
        FacesUtil.addOperacaoRealizada("Solicitação de Documento cancelada com sucesso.");
    }

    private void validarCancelamentoSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoCancelamento().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Motivo do Cancelamento da Solicitação.");
        }
        if (selecionado.getSituacaoSolicitacao() != null && selecionado.getSituacaoSolicitacao().equals(SituacaoSolicitacao.CANCELADO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Solicitação de Documento Oficial já está cancelada.");
        }
        ve.lancarException();
    }

    private void recuperarAtribuirSolicitacao(SolicitacaoDoctoOficial solicitacaoDocto) {
        if (solicitacaoDocto != null) {
            solicitacaoDocto.setDataCancelamento(sistemaFacade.getDataOperacao());

            CalculoDoctoOficial calculo = recuperarCalculo();
            resultadoConsulta.clear();
            if (calculo != null) {
                pesquisarParcelas(calculo);
            }
            if (solicitacaoDocto.getCadastroEconomico() != null) {
                CadastroEconomico ce = solicitacaoDoctoOficialFacade.recuperarCadastroEconomico(selecionado);
                selecionado.setCadastroEconomico(ce);
            }
            if (solicitacaoDocto.getPessoaFisica() != null) {
                PessoaFisica pf = solicitacaoDoctoOficialFacade.recuperarPessoaFisica(selecionado);
                selecionado.setPessoaFisica(pf);
            }
            if (solicitacaoDocto.getPessoaJuridica() != null) {
                PessoaJuridica pj = solicitacaoDoctoOficialFacade.recuperarPessoaJuridica(selecionado);
                selecionado.setPessoaJuridica(pj);
            }
            if (solicitacaoDocto.getCadastroImobiliario() != null) {
                carregarDadosDoCadastroImobiliario();
            }
        }
    }

    private void carregarDadosDoCadastroImobiliario() {
        CadastroImobiliario ci = solicitacaoDoctoOficialFacade.recuperarCadastroImobiliario(selecionado);
        selecionado.setCadastroImobiliario(ci);
        if (selecionado.getDataSolicitacao() != null) {
            propriedades = selecionado.getCadastroImobiliario().getPropriedadeVigenteNaData(selecionado.getDataSolicitacao());
        } else {
            propriedades = selecionado.getCadastroImobiliario().getPropriedadeVigente();
        }
        if (selecionado.getCadastroImobiliario() != null) {
            lote = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperar(selecionado.getCadastroImobiliario().getLote().getId());
            testada = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperarTestadaPrincipal(lote);
            if (testada != null) {
                testada.getFace().getLogradouroBairro();
            }
        }
    }

    private CalculoDoctoOficial recuperarCalculo() {
        return solicitacaoDoctoOficialFacade.retornaCalculoDaSolicitacao(selecionado);
    }

    public void pesquisarParcelas(CalculoDoctoOficial calculo) {
        resultadoConsulta.clear();
        ConsultaParcela consulta = new ConsultaParcela();
        adicionaParametro(consulta, calculo);
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
    }

    public void adicionaParametro(ConsultaParcela consulta, CalculoDoctoOficial calculo) {
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado = (SolicitacaoDoctoOficial) obj;
        recuperarAtribuirSolicitacao(selecionado);
        codigoSolicitacao = selecionado.getCodigo();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/cancelamento-solicitacao-documento-oficial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verCancelamentoSolicitacaoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        codigoSolicitacao = selecionado.getCodigo();
        propriedades = Lists.newArrayList();
        propriedadesRurais = Lists.newArrayList();
        recuperarAtribuirSolicitacao(selecionado);
    }

    @URLAction(mappingId = "novoCancelamentoSolicitacaoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    public void selecionarSolicitacao(SolicitacaoDoctoOficial solicitacao) {
        selecionado = solicitacao;
        recuperarAtribuirSolicitacao(selecionado);
    }

}
