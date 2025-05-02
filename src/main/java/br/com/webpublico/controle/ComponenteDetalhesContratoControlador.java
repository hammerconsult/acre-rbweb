package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExclusaoContratoVO;
import br.com.webpublico.entidadesauxiliares.ItemAjusteContratoDadosCadastraisVO;
import br.com.webpublico.entidadesauxiliares.MovimentoSaldoItemContratoVO;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import br.com.webpublico.enums.TipoAjusteDadosCadastrais;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class ComponenteDetalhesContratoControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExclusaoContratoFacade exclusaoContratoFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;
    private SolicitacaoEmpenho solicitacaoEmpenho;
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;
    private ItemContrato itemContrato;
    private ExecucaoContratoTipo execucaoContratoTipo;
    private Contrato contrato;
    private AjusteContratoDadosCadastrais ajustesDadosAlteracao;
    private AjusteContratoDadosCadastrais ajustesDadosOriginais;
    private List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosVO;
    private List<ExclusaoContratoVO> exclusoesContrato;
    private List<MovimentoSaldoItemContratoVO> movimentosItemContrato;
    private SubTipoSaldoItemContrato subTipoSaldoItemContrato;

    public ComponenteDetalhesContratoControlador() {
        exclusoesContrato = Lists.newArrayList();
        movimentosItemContrato = Lists.newArrayList();
        solicitacaoEmpenho = null;
        itemContrato = null;
        execucaoContratoTipo = null;
    }

    public void recuperarContrato(Contrato contrato) {
        if (contrato != null) {
            this.contrato = contratoFacade.recuperarContratoComDependenciasFornecedor(contrato.getId());
        }
    }

    public List<SelectItem> getSubTiposMovimento() {
        return Util.getListSelectItemSemCampoVazio(SubTipoSaldoItemContrato.values());
    }

    public void listenerExecucaoVariacao() {
        buscarSaldoAndMovimentosItemContrator();
    }

    public void selecionarItemContrato(ItemContrato itemContrato) {
        try {
            if (itemContrato != null) {
                this.itemContrato = itemContrato;
                subTipoSaldoItemContrato = SubTipoSaldoItemContrato.EXECUCAO;
                buscarSaldoAndMovimentosItemContrator();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.addOperacaoNaoRealizada(ve.getMessage());
        }
    }

    private void buscarSaldoAndMovimentosItemContrator() {
        movimentosItemContrato = Lists.newArrayList();
        List<SaldoItemContrato> saldos = saldoItemContratoFacade.buscarSaldoPorItemContrato(this.itemContrato, subTipoSaldoItemContrato);
        Collections.sort(saldos);

        saldos.forEach(saldoItem -> {
            MovimentoSaldoItemContratoVO movSaldoVO = new MovimentoSaldoItemContratoVO();
            movSaldoVO.setSaldoItemContrato(saldoItem);
            List<MovimentoItemContrato> movimentos = saldoItemContratoFacade.buscarMovimentosItemContrato(saldoItem.getItemContrato(), saldoItem.getIdOrigem(), saldoItem.getOrigem(), saldoItem.getOperacao(), saldoItem.getSubTipo());
            Collections.sort(movimentos);
            movSaldoVO.setMovimentos(movimentos);
            movimentosItemContrato.add(movSaldoVO);
        });
    }

    public void selecionarExecucaoContratoTipo(ExecucaoContratoTipo execucaoContratoTipo) {
        this.execucaoContratoTipo = execucaoContratoTipo;
        for (ExecucaoContratoTipoFonte exFonte : this.execucaoContratoTipo.getFontes()) {
            execucaoContratoFacade.atribuirGrupoContaDespesa(exFonte.getItens());
        }
    }


    public void selecionarSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
        this.solicitacaoEmpenho.setHierarquiaOrcamentaria(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
            solicitacaoEmpenho.getUnidadeOrganizacional(),
            solicitacaoEmpenho.getDataSolicitacao()));
    }

    public void selecionarSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public ExecucaoContratoTipo getExecucaoContratoTipo() {
        return execucaoContratoTipo;
    }

    public void setExecucaoContratoTipo(ExecucaoContratoTipo execucaoContratoTipo) {
        this.execucaoContratoTipo = execucaoContratoTipo;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public void buscarAjustesDadosContrato(AjusteContrato ajusteContrato) {
        if (ajusteContrato.isAjusteDadosCadastrais()) {
            ajustesDadosAlteracao = ajusteContrato.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ALTERACAO);
            ajustesDadosOriginais = ajusteContrato.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ORIGINAL);

        } else if (ajusteContrato.isAjusteControleExecucao() || ajusteContrato.isSubstituicaoObjetoCompra() || ajusteContrato.isAjusteOperacaoAditivo()) {
            itensAjusteDadosVO = contratoFacade.getAjusteContratoFacade().buscarAjustesDadosOriginalPopulandoAjusteAlteracao(ajusteContrato);
        }
    }

    public void buscarExclusaoContato(Contrato contrato) {
        exclusoesContrato = Lists.newArrayList();
        if (contrato != null && contrato.getId() != null) {
            exclusoesContrato = exclusaoContratoFacade.buscarExclusaoContrato(contrato);
        }
    }

    public String getUrlEmpenhoEstorno() {
        if (solicitacaoEmpenhoEstorno.getEmpenhoEstorno() != null) {
            if (solicitacaoEmpenhoEstorno.getEmpenhoEstorno().isEmpenhoEstornoNormal()) {
                return "/estorno-empenho/ver/" + solicitacaoEmpenhoEstorno.getEmpenhoEstorno().getId() + "/";
            }
            return "/cancelamento-empenho-restos-a-pagar/ver/" + solicitacaoEmpenhoEstorno.getEmpenhoEstorno().getId() + "/";
        }
        return "";
    }

    public String getUrlEmpenho() {
        if (solicitacaoEmpenhoEstorno.getEmpenho() != null) {
            if (solicitacaoEmpenhoEstorno.getEmpenho().isEmpenhoNormal()) {
                return "/empenho/ver/" + solicitacaoEmpenhoEstorno.getEmpenho().getId() + "/";
            }
            return "/empenho/resto-a-pagar/ver/" + solicitacaoEmpenhoEstorno.getEmpenho().getId() + "/";
        }
        return "";
    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.CONTRATO_EMPENHO);
        eventoPncpVO.setIdOrigem(contrato.getId());
        eventoPncpVO.setIdPncp(contrato.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(contrato.getSequencialIdPncp());
        eventoPncpVO.setSequencialIdPncpLicitacao(contrato.getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(contrato.getExercicioContrato().getAno());
        return eventoPncpVO;
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        contrato.setIdPncp(eventoPncpVO.getIdPncp());
        contrato.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(contrato.getId(), contrato.getNumeroAnoTermo());
        contrato = contratoFacade.salvarRetornando(contrato);
        FacesUtil.redirecionamentoInterno("contrato-adm/ver/" + contrato.getId() + "/");
    }

    public boolean hasItens() {
        return itensAjusteDadosVO != null && !itensAjusteDadosVO.isEmpty();
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public AjusteContratoDadosCadastrais getAjustesDadosAlteracao() {
        return ajustesDadosAlteracao;
    }

    public void setAjustesDadosAlteracao(AjusteContratoDadosCadastrais ajustesDadosAlteracao) {
        this.ajustesDadosAlteracao = ajustesDadosAlteracao;
    }

    public AjusteContratoDadosCadastrais getAjustesDadosOriginais() {
        return ajustesDadosOriginais;
    }

    public void setAjustesDadosOriginais(AjusteContratoDadosCadastrais ajustesDadosOriginais) {
        this.ajustesDadosOriginais = ajustesDadosOriginais;
    }

    public List<ItemAjusteContratoDadosCadastraisVO> getItensAjusteDadosVO() {
        return itensAjusteDadosVO;
    }

    public void setItensAjusteDadosVO(List<ItemAjusteContratoDadosCadastraisVO> itensAjusteDadosVO) {
        this.itensAjusteDadosVO = itensAjusteDadosVO;
    }

    public List<ExclusaoContratoVO> getExclusoesContrato() {
        return exclusoesContrato;
    }

    public void setExclusoesContrato(List<ExclusaoContratoVO> exclusoesContrato) {
        this.exclusoesContrato = exclusoesContrato;
    }

    public List<MovimentoSaldoItemContratoVO> getMovimentosItemContrato() {
        return movimentosItemContrato;
    }

    public void setMovimentosItemContrato(List<MovimentoSaldoItemContratoVO> movimentosItemContrato) {
        this.movimentosItemContrato = movimentosItemContrato;
    }

    public SubTipoSaldoItemContrato getSubTipoSaldoItemContrato() {
        return subTipoSaldoItemContrato;
    }

    public void setSubTipoSaldoItemContrato(SubTipoSaldoItemContrato subTipoSaldoItemContrato) {
        this.subTipoSaldoItemContrato = subTipoSaldoItemContrato;
    }
}
