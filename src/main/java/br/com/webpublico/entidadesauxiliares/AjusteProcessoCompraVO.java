package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAjusteProcessoCompra;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AjusteProcessoCompraVO {

    private Long id;
    private Long idProcesso;
    private Long numero;
    private Date dataLancamento;
    private TipoMovimentoProcessoLicitatorio tipoProcesso;
    private TipoAjusteProcessoCompra tipoAjuste;
    private UsuarioSistema usuarioSistema;
    private PropostaFornecedor propostaFornecedor;
    private String motivo;
    private String historico;
    private String descricaoProcesso;
    private ObjetoCompra objetoCompra;
    private TipoControleLicitacao tipoControle;
    private AjusteProcessoCompra ajusteProcessoCompra;
    private Licitacao licitacao;
    private DispensaDeLicitacao dispensaLicitacao;
    private RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno;
    private RequisicaoDeCompra requisicaoDeCompra;
    private SolicitacaoMaterial solicitacaoCompra;
    private Cotacao cotacao;
    private FormularioCotacao formularioCotacao;
    private IntencaoRegistroPreco intencaoRegistroPreco;
    private LicitacaoFornecedor licitacaoFornecedor;
    private FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao;
    private Pessoa fornecedorSubstituicao;
    private RepresentanteLicitacao representanteLicitacao;
    private ItemPregao itemPregao;
    private RodadaPregao rodadaPregao;
    private LotePropostaFornecedor lotePropostaFornecedor;
    private ItemPropostaFornecedor itemPropostaFornecedor;
    private ItemAjusteProcessoCompraVO objetoCompraSelecionado;
    private String mensagem;
    private MovimentoAjusteProcessoCompraVO movimentoVO;
    private Boolean tipoObjetoCompraDiferenteDaReservaInicialProcesso;

    private List<AjusteProcessoCompraItem> itensAjuste;
    private List<ItemAjusteProcessoCompraVO> objetosCompraDoProcesso;
    private List<ItemAjusteProcessoCompraVO> objetosCompraDoProcessoFilterBy;
    private List<ItemAjusteProcessoCompraVO> objetosCompraSubstituicao;
    private List<ItemAjusteProcessoCompraVO> itensSubstituicaoTipoControle;
    private List<ItemPregao> itensPregao;
    private List<LotePropostaFornecedor> lotesProposta;
    private List<Contrato> contratos;
    private List<LicitacaoFornecedor> fornecedoresLicitacao;
    private List<PropostaFornecedor> propostas;

    public AjusteProcessoCompraVO() {
        itensAjuste = Lists.newArrayList();
        objetosCompraSubstituicao = Lists.newArrayList();
        lotesProposta = Lists.newArrayList();
        contratos = Lists.newArrayList();
        itensSubstituicaoTipoControle = Lists.newArrayList();
        fornecedoresLicitacao = Lists.newArrayList();
        propostas = Lists.newArrayList();
        tipoObjetoCompraDiferenteDaReservaInicialProcesso = false;
    }

    public AjusteProcessoCompra getAjusteProcessoCompra() {
        return ajusteProcessoCompra;
    }

    public void setAjusteProcessoCompra(AjusteProcessoCompra ajusteProcessoCompra) {
        this.ajusteProcessoCompra = ajusteProcessoCompra;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public List<AjusteProcessoCompraItem> getItensAjuste() {
        return itensAjuste;
    }

    public void setItensAjuste(List<AjusteProcessoCompraItem> itensAjuste) {
        this.itensAjuste = itensAjuste;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoAjusteProcessoCompra getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjusteProcessoCompra tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public MovimentoAjusteProcessoCompraVO getMovimentoVO() {
        return movimentoVO;
    }

    public void setMovimentoVO(MovimentoAjusteProcessoCompraVO movimentoVO) {
        this.movimentoVO = movimentoVO;
    }

    public Boolean getTipoObjetoCompraDiferenteDaReservaInicialProcesso() {
        return tipoObjetoCompraDiferenteDaReservaInicialProcesso;
    }

    public void setTipoObjetoCompraDiferenteDaReservaInicialProcesso(Boolean tipoObjetoCompraDiferenteDaReservaInicialProcesso) {
        this.tipoObjetoCompraDiferenteDaReservaInicialProcesso = tipoObjetoCompraDiferenteDaReservaInicialProcesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public List<ItemAjusteProcessoCompraVO> getItensSubstituicaoTipoControle() {
        return itensSubstituicaoTipoControle;
    }

    public void setItensSubstituicaoTipoControle(List<ItemAjusteProcessoCompraVO> itensSubstituicaoTipoControle) {
        this.itensSubstituicaoTipoControle = itensSubstituicaoTipoControle;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public SolicitacaoMaterial getSolicitacaoCompra() {
        return solicitacaoCompra;
    }

    public void setSolicitacaoCompra(SolicitacaoMaterial solicitacaoCompra) {
        this.solicitacaoCompra = solicitacaoCompra;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public FormularioCotacao getFormularioCotacao() {
        return formularioCotacao;
    }

    public void setFormularioCotacao(FormularioCotacao formularioCotacao) {
        this.formularioCotacao = formularioCotacao;
    }

    public IntencaoRegistroPreco getIntencaoRegistroPreco() {
        return intencaoRegistroPreco;
    }

    public void setIntencaoRegistroPreco(IntencaoRegistroPreco intencaoRegistroPreco) {
        this.intencaoRegistroPreco = intencaoRegistroPreco;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public FornecedorDispensaDeLicitacao getFornecedorDispensaLicitacao() {
        return fornecedorDispensaLicitacao;
    }

    public void setFornecedorDispensaLicitacao(FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao) {
        this.fornecedorDispensaLicitacao = fornecedorDispensaLicitacao;
    }

    public Pessoa getFornecedorSubstituicao() {
        return fornecedorSubstituicao;
    }

    public void setFornecedorSubstituicao(Pessoa fornecedorSubstituicao) {
        this.fornecedorSubstituicao = fornecedorSubstituicao;
    }

    public List<LicitacaoFornecedor> getFornecedoresLicitacao() {
        return fornecedoresLicitacao;
    }

    public void setFornecedoresLicitacao(List<LicitacaoFornecedor> fornecedoresLicitacao) {
        this.fornecedoresLicitacao = fornecedoresLicitacao;
    }

    public List<PropostaFornecedor> getPropostas() {
        return propostas;
    }

    public void setPropostas(List<PropostaFornecedor> propostas) {
        this.propostas = propostas;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public RepresentanteLicitacao getRepresentanteLicitacao() {
        return representanteLicitacao;
    }

    public void setRepresentanteLicitacao(RepresentanteLicitacao representanteLicitacao) {
        this.representanteLicitacao = representanteLicitacao;
    }

    public List<ItemPregao> getItensPregao() {
        return itensPregao;
    }

    public void setItensPregao(List<ItemPregao> itensPregao) {
        this.itensPregao = itensPregao;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public RodadaPregao getRodadaPregao() {
        return rodadaPregao;
    }

    public void setRodadaPregao(RodadaPregao rodadaPregao) {
        this.rodadaPregao = rodadaPregao;
    }

    public LotePropostaFornecedor getLotePropostaFornecedor() {
        return lotePropostaFornecedor;
    }

    public void setLotePropostaFornecedor(LotePropostaFornecedor lotePropostaFornecedor) {
        this.lotePropostaFornecedor = lotePropostaFornecedor;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    public List<LotePropostaFornecedor> getLotesProposta() {
        return lotesProposta;
    }

    public void setLotesProposta(List<LotePropostaFornecedor> lotesProposta) {
        this.lotesProposta = lotesProposta;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public Long getIdProcesso() {
        switch (tipoProcesso) {
            case LICITACAO:
            case CREDENCIAMENTO:
                return licitacao.getId();
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return dispensaLicitacao.getId();
            case ADESAO_EXTERNA:
                return registroSolicitacaoMaterialExterno.getId();
            case SOLICITACAO_COMPRA:
                return solicitacaoCompra.getId();
            case COTACAO:
                return cotacao.getId();
            case FORMULARIO_COTACAO:
                return formularioCotacao.getId();
            case IRP:
                return intencaoRegistroPreco.getId();
            default:
                throw new ValidacaoException("Id do processo não encontrado para o processo de " + tipoAjuste.getDescricao() + ".");
        }
    }

    public Long getIdProcessoCompra() {
        switch (tipoProcesso) {
            case LICITACAO:
            case CREDENCIAMENTO:
                return licitacao.getId();
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return dispensaLicitacao.getId();
            case ADESAO_EXTERNA:
                return registroSolicitacaoMaterialExterno.getId();
            case SOLICITACAO_COMPRA:
                return solicitacaoCompra.getId();
            case COTACAO:
                return cotacao.getId();
            case FORMULARIO_COTACAO:
                return formularioCotacao.getId();
            case IRP:
                return intencaoRegistroPreco.getId();
            default:
                throw new ValidacaoException("Id do processo não encontrado para o processo de " + tipoAjuste.getDescricao() + ".");
        }
    }

    public ItemAjusteProcessoCompraVO getObjetoCompraSelecionado() {
        return objetoCompraSelecionado;
    }

    public void setObjetoCompraSelecionado(ItemAjusteProcessoCompraVO objetoCompraSelecionado) {
        this.objetoCompraSelecionado = objetoCompraSelecionado;
    }

    public List<ItemAjusteProcessoCompraVO> getObjetosCompraDoProcesso() {
        return objetosCompraDoProcesso;
    }

    public void setObjetosCompraDoProcesso(List<ItemAjusteProcessoCompraVO> objetosCompraDoProcesso) {
        this.objetosCompraDoProcesso = objetosCompraDoProcesso;
    }

    public List<ItemAjusteProcessoCompraVO> getObjetosCompraDoProcessoFilterBy() {
        return objetosCompraDoProcessoFilterBy;
    }

    public void setObjetosCompraDoProcessoFilterBy(List<ItemAjusteProcessoCompraVO> objetosCompraDoProcessoFilterBy) {
        this.objetosCompraDoProcessoFilterBy = objetosCompraDoProcessoFilterBy;
    }

    public List<ItemAjusteProcessoCompraVO> getObjetosCompraSubstituicao() {
        return objetosCompraSubstituicao;
    }

    public void setObjetosCompraSubstituicao(List<ItemAjusteProcessoCompraVO> objetosCompraSubstituicao) {
        this.objetosCompraSubstituicao = objetosCompraSubstituicao;
    }

    public String getDescricaoProcesso() {
        return descricaoProcesso;
    }

    public void setDescricaoProcesso(String descricaoProcesso) {
        this.descricaoProcesso = descricaoProcesso;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolicitacaoMaterialExterno() {
        return registroSolicitacaoMaterialExterno;
    }

    public void setRegistroSolicitacaoMaterialExterno(RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno) {
        this.registroSolicitacaoMaterialExterno = registroSolicitacaoMaterialExterno;
    }

    public Pessoa getFornecedorLiciacaoOrDispensa() {
        if (licitacaoFornecedor != null) {
            return licitacaoFornecedor.getEmpresa();
        }
        return fornecedorDispensaLicitacao.getPessoa();
    }

    public boolean isAjusteFornecedor() {
        return tipoAjuste.isIncluirProposta()
            || tipoAjuste.isEditarProposta()
            || tipoAjuste.isIncluirFornecedor()
            || tipoAjuste.isEditarFornecedor()
            || tipoAjuste.isSubstituirFornecedor();
    }

    public boolean isProcessoNulo() {
        return
            Util.isNull(getLicitacao())
                && Util.isNull(getDispensaLicitacao())
                && Util.isNull(getRegistroSolicitacaoMaterialExterno())
                && Util.isNull(getSolicitacaoCompra())
                && Util.isNull(getCotacao())
                && Util.isNull(getFormularioCotacao())
                && Util.isNull(getIntencaoRegistroPreco());
    }

    public boolean hasObjetoCompraSelecionado() {
        return objetoCompraSelecionado != null;
    }

    public void limparDadosGerais() {
        setObjetosCompraDoProcesso(Lists.newArrayList());
        if (hasObjetoCompraSelecionado()) {
            getObjetoCompraSelecionado().setMovimentos(Lists.<MovimentoAjusteProcessoCompraVO>newArrayList());
            setObjetoCompraSelecionado(null);
        }
        setIntencaoRegistroPreco(null);
        setFormularioCotacao(null);
        setCotacao(null);
        setSolicitacaoCompra(null);
        setLicitacao(null);
        setDispensaLicitacao(null);
        setRegistroSolicitacaoMaterialExterno(null);
        setObjetoCompra(null);
        itensSubstituicaoTipoControle = Lists.newArrayList();
    }

    public void limparDadosFornecedor() {
        setLicitacaoFornecedor(null);
        setPropostaFornecedor(null);
        setFornecedorSubstituicao(null);
    }

    public void limparDadosObjetoCompraPara() {
        getObjetoCompraSelecionado().setGrupoContaDespesaPara(null);
        getObjetoCompraSelecionado().setEspecificacaoPara(null);
        getObjetoCompraSelecionado().setObjetoCompraPara(null);
    }

    public static AjusteProcessoCompraVO toAjusteProcessoCompraObjeto(AjusteProcessoCompra entity) {
        AjusteProcessoCompraVO ajusteVO = new AjusteProcessoCompraVO();
        ajusteVO.itensAjuste = Lists.newArrayList();
        ajusteVO.id = entity.getId();
        ajusteVO.numero = entity.getNumero();
        ajusteVO.dataLancamento = entity.getDataLancamento();
        ajusteVO.idProcesso = entity.getIdProcesso();
        ajusteVO.tipoAjuste = entity.getTipoAjuste();
        ajusteVO.tipoProcesso = entity.getTipoProcesso();
        ajusteVO.usuarioSistema = entity.getUsuarioSistema();
        ajusteVO.propostaFornecedor = entity.getPropostaFornecedor();
        ajusteVO.licitacaoFornecedor = entity.getLicitacaoFornecedor();
        ajusteVO.fornecedorDispensaLicitacao = entity.getFornecedorDispensaLicitacao();
        ajusteVO.motivo = entity.getMotivo();
        ajusteVO.historico = entity.getHistorico();
        ajusteVO.ajusteProcessoCompra = entity;
        return ajusteVO;
    }
}
