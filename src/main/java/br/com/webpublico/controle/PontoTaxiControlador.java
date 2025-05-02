/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.PermissaoTransporte;
import br.com.webpublico.entidades.PontoTaxi;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PontoTaxiFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author cheles
 */
@ManagedBean(name = "pontoTaxiControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPontoDeTaxi",
        pattern = "/ponto-de-taxi/novo/",
        viewId = "/faces/tributario/rbtrans/pontodetaxi/edita.xhtml"),
    @URLMapping(id = "editarPontoDeTaxi",
        pattern = "/ponto-de-taxi/editar/#{pontoTaxiControlador.id}/",
        viewId = "/faces/tributario/rbtrans/pontodetaxi/edita.xhtml"),
    @URLMapping(id = "listarPontoDeTaxi",
        pattern = "/ponto-de-taxi/listar/",
        viewId = "/faces/tributario/rbtrans/pontodetaxi/lista.xhtml"),
    @URLMapping(id = "verPontoDeTaxi",
        pattern = "/ponto-de-taxi/ver/#{pontoTaxiControlador.id}/",
        viewId = "/faces/tributario/rbtrans/pontodetaxi/visualizar.xhtml")
})
public class PontoTaxiControlador extends PrettyControlador<PontoTaxi> implements Serializable, CRUD {

    @EJB
    private PontoTaxiFacade pontoTaxiFacade;
    private List<PermissaoTransporte> permissoesDoPonto = Lists.newArrayList();
    private final Map<Date, PermissaoTransporte> permissoesAlteracao = Maps.newHashMap();
    private PermissaoTransporte permissaoSelecionadaParaAdicionar;


    public PontoTaxiControlador() {
        super(PontoTaxi.class);
    }

    public List<PermissaoTransporte> getPermissoesDoPonto() {
        return permissoesDoPonto;
    }

    public void setPermissoesDoPonto(List<PermissaoTransporte> permissoesDoPonto) {
        this.permissoesDoPonto = permissoesDoPonto;
    }

    public PermissaoTransporte getPermissaoSelecionadaParaAdicionar() {
        return permissaoSelecionadaParaAdicionar;
    }

    public void setPermissaoSelecionadaParaAdicionar(PermissaoTransporte permissaoSelecionadaParaAdicionar) {
        this.permissaoSelecionadaParaAdicionar = permissaoSelecionadaParaAdicionar;
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            if (isOperacaoNovo()) {
                pontoTaxiFacade.salvaNovo(selecionado);
            } else {
                selecionado = pontoTaxiFacade.salva(selecionado);
            }
            salvarNovosPermissionarios();
            FacesUtil.addInfo("Salvo com sucesso!", "");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Não foi possivel salvar o ponto de taxi", e);
        }
    }

    private void salvarNovosPermissionarios() {
        Map<Date, PermissaoTransporte> mapaOrdenadoPelaKey = new TreeMap<>(permissoesAlteracao);
        for (PermissaoTransporte permissao : mapaOrdenadoPelaKey.values()) {
            pontoTaxiFacade.getPermissaoTransporteFacade().salvar(permissao);
        }
    }

    public void validaCampos() throws Exception {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLocalizacao().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a localização.");
        }
        if (selecionado.getTotalDeVagas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade de vagas.");
        }
        if (selecionado.getLogradouroDe().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o logradouro inicial do perímetro.");
        }
        if (selecionado.getBairroDe().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o bairro inicial do perímetro.");
        }
        if (selecionado.getLogradouroAte().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o logradouro final do perímetro.");
        }
        if (selecionado.getBairroAte().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o pairro final do perímetro.");
        }
        if (selecionado.getTipoPermissaoRBTrans() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de permissão.");
        }
        PontoTaxi pontoPorNumero = pontoTaxiFacade.recuperarPontoTaxiPeloNumero(selecionado.getNumero());
        if ((isOperacaoNovo() || pontoPorNumero != null && !selecionado.getId().equals(pontoPorNumero.getId())) && pontoTaxiFacade.existePontoDeTaxiPorNumero(selecionado.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Ponto de Táxi cadastrado com o número " + selecionado.getNumero());
        }
        for (PermissaoTransporte permissao : permissoesDoPonto) {
            if (selecionado.getTipoPermissaoRBTrans() != null && !selecionado.getTipoPermissaoRBTrans().equals(permissao.getTipoPermissaoRBTrans())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Esse ponto só permite permissionários que possuam a permissão de " + selecionado.getTipoPermissaoRBTrans().getDescricao());
                break;
            }
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ponto-de-taxi/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoPontoDeTaxi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @URLAction(mappingId = "verPontoDeTaxi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        permissoesDoPonto = pontoTaxiFacade.buscarPermissoesDoPonto(selecionado);
    }

    @URLAction(mappingId = "editarPontoDeTaxi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        permissoesDoPonto = pontoTaxiFacade.buscarPermissoesDoPonto(selecionado);
    }

    private void parametrosIniciais() {
        selecionado.setDataDeCadastro(SistemaFacade.getDataCorrente());
        selecionado.setAtivo(true);
    }

    @Override
    public AbstractFacade getFacede() {
        return pontoTaxiFacade;
    }

    public Integer getNumeroDeVagasOcupadas() {
        if (selecionado.getId() == null) return 0;
        return pontoTaxiFacade.buscarNumeroDeVagasOcupadas(selecionado);
    }

    public Integer getNumeroDeVagasLivre() {
        return selecionado.getTotalDeVagas() - getNumeroDeVagasOcupadas();
    }

    public void validaTotalDeVagasInformado() {
        if (selecionado.getTotalDeVagas() != null && selecionado.getTotalDeVagas() < 0) {
            if (selecionado.getId() != null) {
                selecionado.setTotalDeVagas(pontoTaxiFacade.numeroDeVagas(selecionado));
                exibirMensagemTotalDeVagasNegativo();
            } else {
                selecionado.setTotalDeVagas(0);
                exibirMensagemTotalDeVagasNegativo();
            }
        } else if (selecionado.getTotalDeVagas() != null && selecionado.getTotalDeVagas() < getNumeroDeVagasOcupadas()) {
            selecionado.setTotalDeVagas(pontoTaxiFacade.numeroDeVagas(selecionado));
            exibirMensagemTotalDeVagasMenorQueAsOcupadas();
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    private void exibirMensagemTotalDeVagasNegativo() {
        FacesUtil.addError("Número inválido!",
            "O total de vagas não pode ser negativo.");
    }

    private void exibirMensagemTotalDeVagasMenorQueAsOcupadas() {
        FacesUtil.addError("Número inválido!",
            "O total de vagas não pode ser menor do que o número de vagas ocupadas.");
    }

    public List<SelectItem> getTiposPermissoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans tp : TipoPermissaoRBTrans.tiposDefinidos()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public void removerPermissionario(PermissaoTransporte permissao) {
        permissao.setPontoTaxi(null);
        permissoesAlteracao.put(new Date(), permissao);
        permissoesDoPonto.remove(permissao);
    }

    private void validarAdicaoPermissionario() {
        ValidacaoException ve = new ValidacaoException();
        if (permissaoSelecionadaParaAdicionar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um permissionário.");
        } else {
            PermissaoTransporte permissaoDaLista = permissoesDoPonto.stream().filter(p -> p.getId().equals(permissaoSelecionadaParaAdicionar.getId())).findFirst().orElse(null);
            if (permissaoDaLista != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Permissionario " + permissaoDaLista + " já está na lista.");
                permissaoSelecionadaParaAdicionar = null;
            }
        }
        ve.lancarException();
    }

    public void verificarAdicaoPermissionario() {
        try {
            validarAdicaoPermissionario();
            if (permissaoSelecionadaParaAdicionar.getPontoTaxi() != null && !permissaoSelecionadaParaAdicionar.getPontoTaxi().getId().equals(selecionado.getId())) {
                FacesUtil.executaJavaScript("dialogConfirmaTrocaPonto.show()");
                return;
            }
            FacesUtil.executaJavaScript("dialogAddPermissionario.show()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Não foi possivel adicionar o permissionário à lista", e);
        }
    }

    public void adicionarPermissao() {
        permissaoSelecionadaParaAdicionar.setPontoTaxi(selecionado);
        permissoesDoPonto.add(permissaoSelecionadaParaAdicionar);
        permissoesAlteracao.put(new Date(), permissaoSelecionadaParaAdicionar);
        permissaoSelecionadaParaAdicionar = null;
    }

    public void cancelarAdicaoPermissao() {
        permissaoSelecionadaParaAdicionar = null;
    }

    public void alterouTipoPermissao() {
        if (selecionado.getTipoPermissaoRBTrans() == null) return;
        FacesUtil.addAtencao("Somente permissionários que possuem o tipo de permissão " + selecionado.getTipoPermissaoRBTrans().getDescricao() + " devem estar na lista de permissionários, verifique antes de salvar.");
    }

    public List<PermissaoTransporte> completaPermissoesTransporte(String parte) {
        if (selecionado.getTipoPermissaoRBTrans() == null) return Lists.newArrayList();
        return pontoTaxiFacade.getPermissaoTransporteFacade().buscarPermissoesFiltrandoPorPermissionarioETipoPermissao(selecionado.getTipoPermissaoRBTrans(), parte);
    }

    public String mensagemRemocaoPermissao(PermissaoTransporte permissao) {
        return "Tem certeza de que deseja remover a permissão " + permissao.getNumero() + " do ponto " + permissao.getPontoTaxi().getLocalizacao() + "?";
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Relatório de Ponto de Táxi, Mototáxi e Frete");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", pontoTaxiFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            dto.adicionarParametro("SECRETARIA", "Superintendência Municipal de Transporte e Trânsito - RBTRANS");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de Ponto de Táxi, Mototáxi e Frete");
            dto.adicionarParametro("ID_PONTO", selecionado.getId());
            dto.adicionarParametro("TIPO_PERMISSAO_RBTRANS", selecionado.getTipoPermissaoRBTrans().getDescricao());
            dto.setApi("tributario/ponto-taxi/");
            ReportService.getInstance().gerarRelatorio(pontoTaxiFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException ex) {
            logger.error("Erro ao gerar relatório. ", ex);
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }
}
