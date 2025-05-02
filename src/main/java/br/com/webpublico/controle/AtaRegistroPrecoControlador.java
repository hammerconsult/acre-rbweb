package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtaRegistroPrecoFacade;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/04/14
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "ataRegistroPrecoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ata-registro-preco", pattern = "/ata-registro-preco/novo/", viewId = "/faces/administrativo/licitacao/ataregistrodepreco/edita.xhtml"),
    @URLMapping(id = "editar-ata-registro-preco", pattern = "/ata-registro-preco/editar/#{ataRegistroPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/ataregistrodepreco/edita.xhtml"),
    @URLMapping(id = "ver-ata-registro-preco", pattern = "/ata-registro-preco/ver/#{ataRegistroPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/ataregistrodepreco/visualizar.xhtml"),
    @URLMapping(id = "listar-ata-registro-preco", pattern = "/ata-registro-preco/listar/", viewId = "/faces/administrativo/licitacao/ataregistrodepreco/lista.xhtml")
})
public class AtaRegistroPrecoControlador extends PrettyControlador<AtaRegistroPreco> implements Serializable, CRUD {

    @EJB
    private AtaRegistroPrecoFacade facade;
    private FornecedorAtaRegistroPreco fornecedorSelecionado;
    private List<FornecedorAtaRegistroPreco> fornecedoresVencedores;
    private Adesao adesaoSelecionada;
    private ExecucaoProcesso execucaoProcesso;
    private PublicacaoLicitacao publicacaoLicitacao;
    private Boolean licitacaoIRP;
    private List<ParticipanteIRP> participantesIRP;
    private ParticipanteIRP participanteIRP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private SaldoProcessoLicitatorioVO saldoAtaVO;
    private List<ExecucaoProcesso> execucoes;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    private List<AlteracaoContratual> aditivos;
    private List<AlteracaoContratual> apostilamentos;

    public AtaRegistroPrecoControlador() {
        super(AtaRegistroPreco.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ata-registro-preco/";
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
    @URLAction(mappingId = "novo-ata-registro-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataInicio(facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "editar-ata-registro-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        setHierarquiaOrganizacional(getHierarquiaDaUnidade(selecionado.getUnidadeOrganizacional()));
        recuperarUltimaPublicacaoDaLicitacao();
        verificarLicitacaoIRP();
        preencherUnidadesAta();
        recuperarFornecedoresAndItensVencedoresLicitacao();
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
        buscarSaldoAta();
    }

    private void verificarLicitacaoIRP() {
        setLicitacaoIRP(facade.getLicitacaoFacade().isLicitacaoIRP(selecionado.getLicitacao()));
    }

    @Override
    @URLAction(mappingId = "ver-ata-registro-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependencias(getId());
        setHierarquiaOrganizacional(getHierarquiaDaUnidade(selecionado.getUnidadeOrganizacional()));
        verificarLicitacaoIRP();
        recuperarFornecedoresAndItensVencedoresLicitacao();
        buscarSaldoAta();
        preencherUnidadesAta();
        execucoes = facade.getExecucaoProcessoFacade().buscarExecucoesPorAtaRegistroPreco(selecionado);
        novoFiltroResumoExecucao();
        aditivos = facade.getAlteracaoContratualFacade().buscarAlteracoesContratuaisAndDependecias(selecionado.getId(), TipoAlteracaoContratual.ADITIVO);
        apostilamentos = facade.getAlteracaoContratualFacade().buscarAlteracoesContratuaisAndDependecias(selecionado.getId(), TipoAlteracaoContratual.APOSTILAMENTO);
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado.setDataVencimentoAtual(selecionado.getDataVencimento());
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer(selecionado);
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void novoFiltroResumoExecucao() {
        filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.PROCESSO);
        filtroResumoExecucaoVO.setIdProcesso(selecionado.getId());
    }

    public void redirecionarParaVer(AtaRegistroPreco entity) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + entity.getId() + "/");
    }

    private void validarSalvar() {
        Util.validarCampos(selecionado);
        validarRegrasEspecificas();
        facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
        facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        AtaRegistroPreco ata = facade.recuperarAtaRegistroPrecoPorUnidade(selecionado.getUnidadeOrganizacional(), selecionado.getLicitacao(), selecionado);
        if (ata != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + getHierarquiaOrganizacional() + " pertence a ata registro de preço nº " + ata.getNumero() + ".");
        }
        ve.lancarException();
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        selecionado.setIdPncp(eventoPncpVO.getIdPncp());
        selecionado.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(selecionado.getId(), selecionado.getNumero() + "/" + selecionado.getAnoPcnp());
        selecionado = facade.salvarRetornandoAta(selecionado);
        redirecionarParaVer(selecionado);
    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.ATA_REGISTRO_PRECO);
        eventoPncpVO.setIdOrigem(selecionado.getId());
        eventoPncpVO.setIdPncp(selecionado.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(selecionado.getSequencialIdPncp());
        eventoPncpVO.setSequencialIdPncpLicitacao(selecionado.getLicitacao().getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(selecionado.getAnoPcnp());
        return eventoPncpVO;
    }

    private boolean validarLicitacaoAdjudicadaEHomologada() {
        return selecionado.getLicitacao().isHomologada();
    }

    private boolean validarNatureza() {
        return selecionado.getLicitacao().isRegistroDePrecos();
    }

    private boolean isModalidadePermitidaNaAta() {
        return selecionado.getLicitacao().getModalidadeLicitacao() != null
            && (selecionado.getLicitacao().getModalidadeLicitacao().isConcorrencia()
            || selecionado.getLicitacao().getModalidadeLicitacao().isPregao()
            || selecionado.getLicitacao().getModalidadeLicitacao().isRDC()
        );
    }

    public void listenerLicitacao() {
        try {
            selecionado.setLicitacao(facade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
            verificarLicitacaoIRP();
            validarLicitacao();
            recuperarUltimaPublicacaoDaLicitacao();
            recuperarFornecedoresAndItensVencedoresLicitacao();
            if (licitacaoIRP) {
                preencherUnidadesAta();
                selecionado.setGerenciadora(true);
            } else {
                setHierarquiaOrganizacional(getHierarquiaDaUnidade(selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional()));
                buscarSaldoAta();
            }
        } catch (ValidacaoException ve) {
            selecionado.setLicitacao(null);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void preencherUnidadesAta() {
        participantesIRP = facade.getParticipanteIRPFacade().buscarParticipantesIRPAprovado("", selecionado.getLicitacao());
        if (isOperacaoEditar()) {
            for (ParticipanteIRP part : participantesIRP) {
                if (part.getHierarquiaOrganizacional().getSubordinada().equals(selecionado.getUnidadeOrganizacional())) {
                    setParticipanteIRP(part);
                }
            }
        }
    }

    public List<SelectItem> getUnidadesParticipantesIrp() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (participantesIRP != null && !participantesIRP.isEmpty()) {
            for (ParticipanteIRP part : participantesIRP) {
                toReturn.add(new SelectItem(part, part.toString()));
            }
        }
        return toReturn;
    }

    private void buscarSaldoAta() {
        FiltroSaldoProcessoLicitatorioVO filtro = new FiltroSaldoProcessoLicitatorioVO(TipoExecucaoProcesso.ATA_REGISTRO_PRECO);
        filtro.setProcessoIrp(licitacaoIRP);
        filtro.setIdProcesso(selecionado.getLicitacao().getId());
        if (selecionado.getId() !=null) {
            filtro.setIdAta(selecionado.getId());
        }
        filtro.setTipoAvaliacaoLicitacao(selecionado.getLicitacao().getTipoAvaliacao());
        filtro.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
        saldoAtaVO = facade.getSaldoProcessoLicitatorioFacade().getSaldoProcesso(filtro);
    }

    public void validarLicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (!isModalidadePermitidaNaAta()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Só é permitido realizar ata de registro de preços para licitações com a modalidade ('Concorrência', 'Pregão' e 'Regime Diferenciado de Contratações – RDC').");
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO.getDescricao(), "Modalidade da licitação selecionada: " + selecionado.getLicitacao().getModalidadeLicitacao().getDescricao());
        }
        if (!validarNatureza()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Só é permitido realizar ata de registro de preços para licitações de registro de preço.");
        }
        if (!validarLicitacaoAdjudicadaEHomologada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Só é permitido realizar ata de registro de preços para licitações homologadas.");
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO.getDescricao(), "Status Atual da licitação selecionada: " + selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().getDescricao());
        }
        ve.lancarException();
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            unidadeOrganizacional,
            selecionado.getDataInicio());
    }

    private void recuperarUltimaPublicacaoDaLicitacao() {
        if (selecionado.getLicitacao() != null) {
            PublicacaoLicitacao pl = facade.getLicitacaoFacade().recuperarUltimaPublicacaoDaLicitacao(selecionado.getLicitacao());
            if (pl != null) {
                publicacaoLicitacao = pl;
            }
        }
    }

    private void recuperarFornecedoresAndItensVencedoresLicitacao() {
        if (selecionado.getLicitacao() != null) {
            fornecedoresVencedores = Lists.newArrayList();
            List<Pessoa> vencedores = facade.getLicitacaoFacade().recuperarVencedoresDaLicitacao(selecionado.getLicitacao());
            for (Pessoa pessoa : vencedores) {
                FornecedorAtaRegistroPreco fornecedor = new FornecedorAtaRegistroPreco();
                fornecedor.setFornecedor(pessoa);
                fornecedoresVencedores.add(fornecedor);
            }
        }
    }

    public void selecionarFornecedor(FornecedorAtaRegistroPreco fornecedor) {
        this.fornecedorSelecionado = fornecedor;
        if (Util.isListNullOrEmpty(fornecedorSelecionado.getItens())) {
            saldoAtaVO.getSaldosAgrupadosPorOrigem().stream().flatMap(saldo -> saldo.getItens().stream())
                .filter(item -> item.getIdFornecedor().equals(fornecedorSelecionado.getFornecedor().getId()))
                .forEach(item -> fornecedorSelecionado.getItens().add(item));

            for (SaldoProcessoLicitatorioItemVO item : fornecedorSelecionado.getItens()) {
                ObjetoCompra objetoCompra = item.getItemProcessoCompra().getObjetoCompra();
                objetoCompra.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
            }
        }
    }

    public Adesao getAdesaoSelecionada() {
        return adesaoSelecionada;
    }

    public void setAdesaoSelecionada(Adesao adesaoSelecionada) {
        this.adesaoSelecionada = adesaoSelecionada;
    }

    public void selecionarAdesao(Adesao adesao) {
        adesaoSelecionada = adesao;
    }

    public void selecionarAdesaoParaAvaliacao(Adesao adesao) {
        adesaoSelecionada = facade.getAdesaoFacade().recuperar(adesao.getId());
    }

    public void aceitarAdesao() {
        adesaoSelecionada.setAdesaoAceita(Boolean.TRUE);
        facade.atualizarAdesao(adesaoSelecionada);
    }

    public void naoAceitarAdesao() {
        adesaoSelecionada.setAdesaoAceita(Boolean.FALSE);
        facade.atualizarAdesao(adesaoSelecionada);
    }

    public boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String numero) {
        return facade.buscarAtaRegistroPrecoPorNumeroOndeUsuarioGestorLicitacao(numero, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<AtaRegistroPreco> buscarAtasRegistroPrecoVigentes(String numero) {
        return facade.buscarAtasRegistroPrecoVigentes(numero.trim());
    }

    public PublicacaoLicitacao getPublicacaoLicitacao() {
        return publicacaoLicitacao;
    }

    public void setPublicacaoLicitacao(PublicacaoLicitacao publicacaoLicitacao) {
        this.publicacaoLicitacao = publicacaoLicitacao;
    }

    public Boolean getLicitacaoIRP() {
        return licitacaoIRP;
    }

    public void setLicitacaoIRP(Boolean licitacaoIRP) {
        this.licitacaoIRP = licitacaoIRP;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public FornecedorAtaRegistroPreco getFornecedorSelecionado() {
        return fornecedorSelecionado;
    }

    public void setFornecedorSelecionado(FornecedorAtaRegistroPreco fornecedorSelecionado) {
        this.fornecedorSelecionado = fornecedorSelecionado;
    }

    public List<FornecedorAtaRegistroPreco> getFornecedoresVencedores() {
        return fornecedoresVencedores;
    }

    public void setFornecedoresVencedores(List<FornecedorAtaRegistroPreco> fornecedoresVencedores) {
        this.fornecedoresVencedores = fornecedoresVencedores;
    }

    public List<ParticipanteIRP> getParticipantesIRP() {
        return participantesIRP;
    }

    public void setParticipantesIRP(List<ParticipanteIRP> participantesIRP) {
        this.participantesIRP = participantesIRP;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void listenerParticipanteIrp() {
        setHierarquiaOrganizacional(null);
        selecionado.setUnidadeOrganizacional(null);
        participanteIRP.setHierarquiaOrganizacional(facade.getParticipanteIRPFacade().buscarUnidadeParticipanteVigente(participanteIRP));
        if (participanteIRP != null) {
            setHierarquiaOrganizacional(participanteIRP.getHierarquiaOrganizacional());
            selecionado.setUnidadeOrganizacional(participanteIRP.getHierarquiaOrganizacional().getSubordinada());
            selecionado.setGerenciadora(participanteIRP.getGerenciador());
        }
        buscarSaldoAta();
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.ATA_REGISTRO_PRECO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public boolean hasExecucoes() {
        return !Util.isListNullOrEmpty(execucoes);
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public SaldoProcessoLicitatorioVO getSaldoAtaVO() {
        return saldoAtaVO;
    }

    public void setSaldoAtaVO(SaldoProcessoLicitatorioVO saldoAtaVO) {
        this.saldoAtaVO = saldoAtaVO;
    }

    public List<ExecucaoProcesso> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<ExecucaoProcesso> execucoes) {
        this.execucoes = execucoes;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoVO() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoVO(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }

    public List<AlteracaoContratual> getAditivos() {
        return aditivos;
    }

    public void setAditivos(List<AlteracaoContratual> aditivos) {
        this.aditivos = aditivos;
    }

    public List<AlteracaoContratual> getApostilamentos() {
        return apostilamentos;
    }

    public void setApostilamentos(List<AlteracaoContratual> apostilamentos) {
        this.apostilamentos = apostilamentos;
    }
}
