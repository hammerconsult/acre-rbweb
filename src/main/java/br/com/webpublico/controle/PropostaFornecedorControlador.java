/*
 * Codigo gerado automaticamente em Thu Dec 08 11:23:00 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ArquivoItemLicitacaoPropostaFornecedor;
import br.com.webpublico.entidadesauxiliares.ArquivoLicitacaoPropostaFornecedor;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PropostaFornecedorFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "propostaFornecedorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaProposta", pattern = "/licitacao/proposta-fornecedor/novo/", viewId = "/faces/administrativo/licitacao/propostafornecedor/edita.xhtml"),
    @URLMapping(id = "editarProposta", pattern = "/licitacao/proposta-fornecedor/editar/#{propostaFornecedorControlador.id}/", viewId = "/faces/administrativo/licitacao/propostafornecedor/edita.xhtml"),
    @URLMapping(id = "listarProposta", pattern = "/licitacao/proposta-fornecedor/listar/", viewId = "/faces/administrativo/licitacao/propostafornecedor/lista.xhtml"),
    @URLMapping(id = "verProposta", pattern = "/licitacao/proposta-fornecedor/ver/#{propostaFornecedorControlador.id}/", viewId = "/faces/administrativo/licitacao/propostafornecedor/visualizar.xhtml")
})
public class PropostaFornecedorControlador extends PrettyControlador<PropostaFornecedor> implements Serializable, CRUD {

    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    private ConverterAutoComplete converterPessoa;
    private MoneyConverter moneyConverter;
    private LotePropostaFornecedor lotePropostaFornecedorSelecionado;
    private ItemPropostaFornecedor itemPropostaFornecedor;
    private String modeloProduto;
    private String descricaoProduto;
    private ConverterAutoComplete converterRepresentante;


    public PropostaFornecedorControlador() {
        super(PropostaFornecedor.class);
    }

    @URLAction(mappingId = "novaProposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        recuperarArquivoPropostaFornecedorDaSessao();
    }

    private void recuperarArquivoPropostaFornecedorDaSessao() {
        try {
            Object o = Web.pegaDaSessao(ArquivoLicitacaoPropostaFornecedor.class);
            if (o != null) {
                ArquivoLicitacaoPropostaFornecedor arquivo = (ArquivoLicitacaoPropostaFornecedor) o;
                Licitacao licitacao = propostaFornecedorFacade.getLicitacaoFacade().recuperar(arquivo.getIdLicitacao());
                if (licitacao == null) {
                    FacesUtil.addOperacaoNaoPermitida("Licitação não encontrada no sistema para os dados do arquivo, licitação: " + arquivo.getLicitacao() + ".");
                    return;
                }
                selecionado.setLicitacao(licitacao);
                processaLicitacaoSelecionada();
                selecionado.setDataProposta(arquivo.getData());
                selecionado.setInstrumentoRepresentacao(arquivo.getInstrumento());
                selecionado.setTipoPrazoExecucao(TipoPrazo.valueOf(arquivo.getTipoPrazoExecucao().toUpperCase()));
                selecionado.setTipoPrazoValidade(TipoPrazo.valueOf(arquivo.getTipoPrazoValidade().toUpperCase()));
                selecionado.setPrazoDeExecucao(arquivo.getPrazoExecucao().intValue());
                selecionado.setValidadeDaProposta(arquivo.getPrazoValidade().intValue());
                atribuirFornecedorPropostaArquivo(arquivo);
                atribuirRepresentanteArquivo(arquivo);
                criarItensPropostaFornecedorArquivo(arquivo);
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            descobrirETratarException(e);
        }
    }

    private void criarItensPropostaFornecedorArquivo(ArquivoLicitacaoPropostaFornecedor arquivo) {
        for (ArquivoItemLicitacaoPropostaFornecedor item : arquivo.getItens()) {
            for (LotePropostaFornecedor lote : selecionado.getLotes()) {
                if (item.getDescricaoLote().equals(lote.getDescricaoLote())
                    && item.getNumeroLote().equals(lote.getNumeroLote())) {
                    for (ItemPropostaFornecedor itemPropostaFornecedor : lote.getItens()) {
                        if (item.getNumeroItem().equals(itemPropostaFornecedor.getNumeroItem())) {
                            itemPropostaFornecedor.setMarca(item.getMarca());
                            itemPropostaFornecedor.setPreco(item.getValor());
                            itemPropostaFornecedor.setDescricaoProduto(item.getDescricaoProduto());
                            itemPropostaFornecedor.setModelo(item.getModelo());
                            lotePropostaFornecedorSelecionado = itemPropostaFornecedor.getLotePropostaFornecedor();
                            atualizaValorDoLote();
                        }
                    }
                }
            }
        }
    }

    private void atribuirRepresentanteArquivo(ArquivoLicitacaoPropostaFornecedor arquivo) {
        if (!Strings.isNullOrEmpty(arquivo.getRepresentante())) {
            RepresentanteLicitacao representante = propostaFornecedorFacade.getRepresentanteLicitacaoFacade().buscarRepresentanteLicitacaoPorCpfOrNome(arquivo.getCpfCnpjRepresentante(), arquivo.getRepresentante());
            if (representante != null) {
                selecionado.setRepresentante(propostaFornecedorFacade.getRepresentanteLicitacaoFacade().recuperar(representante.getId()));
            }
        }
    }

    private void atribuirFornecedorPropostaArquivo(ArquivoLicitacaoPropostaFornecedor arquivo) {
        if (!Strings.isNullOrEmpty(arquivo.getFornecedor())) {
            Pessoa fornecedor = propostaFornecedorFacade.getPessoaFacade().buscarPessoaAtivasPorCpfOrCnpj(arquivo.getCpfCnpjFornecedor());
            if (fornecedor == null) {
                fornecedor = propostaFornecedorFacade.getPessoaFacade().buscarPessoaAtivasPorNomeOrRazaoSocial(arquivo.getFornecedor());
            }
            if (fornecedor != null) {
                selecionado.setFornecedor(propostaFornecedorFacade.getPessoaFacade().recuperar(fornecedor.getId()));
            }
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            getFacade().getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            validarNegativo();
            validarRepresentante();
            validarInstrumentoDeRepresentacao();
            validarDescontos();
            super.salvar();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDescontos() {
        ValidacaoException ve = new ValidacaoException();
        for (LotePropostaFornecedor lotePropostaFornecedor : selecionado.getLotes()) {
            if (selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto()) {
                if (selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoApuracao().isPorLote()) {
                    validarDescontoLote(ve, lotePropostaFornecedor);
                } else {
                    validarDescontoItens(ve, lotePropostaFornecedor);
                }
            }
        }
        ve.lancarException();
    }

    private void validarDescontoItens(ValidacaoException ve, LotePropostaFornecedor lotePropostaFornecedor) {
        for (ItemPropostaFornecedor itemPropostaFornecedor : lotePropostaFornecedor.getItens()) {
            validarDescontoItem(ve, itemPropostaFornecedor);
        }
    }

    private void validarDescontoItem(ValidacaoException ve, ItemPropostaFornecedor itemPropostaFornecedor) {
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            if (itemPropostaFornecedor.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto (%)' do item " + itemPropostaFornecedor +
                    " não pode ser menor que zero.");
            }
        }
    }

    private void validarDescontoLote(ValidacaoException ve, LotePropostaFornecedor lotePropostaFornecedor) {
        if (lotePropostaFornecedor.getPercentualDesconto() != null) {
            if (lotePropostaFornecedor.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto (%)' do lote " + lotePropostaFornecedor + " não pode ser menor que zero.");
            }
        }
    }

    private void validarRepresentante() {
        ValidacaoException exception = new ValidacaoException();
        if (selecionado.getFornecedor().isPessoaJuridica() && selecionado.getRepresentante() == null) {
            exception.adicionarMensagemDeCampoObrigatorio("O campo representante é obrigatório. Fornecedor informado é pessoa jurídica.");
        }
        exception.lancarException();
    }

    private void validarNegativo() {
        ValidacaoException exception = new ValidacaoException();
        if (selecionado.getValidadeDaProposta() < 0) {
            exception.adicionarMensagemDeOperacaoNaoPermitida("O campo Prazo de Validade não pode ser menor que zero.");
        }

        if (selecionado.getPrazoDeExecucao() < 0) {
            exception.adicionarMensagemDeOperacaoNaoPermitida("O campo Prazo de Execução não pode ser menor que zero.");
        }
        exception.lancarException();
    }

    public boolean isCertameGerado() {
        if (selecionado.getLicitacao() != null) {
            return propostaFornecedorFacade.getCertameFacade().isPossuiCertame(selecionado.getLicitacao());
        }
        return false;
    }

    private void validarInstrumentoDeRepresentacao() {
        if (selecionado.getFornecedor().isPessoaJuridica()) {
            if (selecionado.getInstrumentoRepresentacao() == null || selecionado.getInstrumentoRepresentacao().isEmpty()) {
                throw new ValidacaoException("O campo Instrumento de Representação é obrigatório, pois o fornecedor informado é pessoa jurídica.");
            }
        }
    }


    @URLAction(mappingId = "verProposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        realizarOperacoesComunsAoSelecionar();
    }

    @URLAction(mappingId = "editarProposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        recuperarObjeto();
        operacao = Operacoes.EDITAR;
        realizarOperacoesComunsAoSelecionar();
        try {
            getFacade().getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    private void realizarOperacoesComunsAoSelecionar() {
        recuperarObjeto();
        if (Operacoes.EDITAR.equals(operacao) && selecionado.getLicitacao().isHomologada()) {
            FacesUtil.addOperacaoNaoPermitida("A Licitação está homologada, portanto a proposta do fornecedor não poderá ser alterada");
            redireciona();
        }
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/proposta-fornecedor/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return propostaFornecedorFacade;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        selecionado.setListaDeItensPropostaFornecedor(recuperarItensPropostaFornecedor());
    }

    public LotePropostaFornecedor getLotePropostaFornecedorSelecionado() {
        return lotePropostaFornecedorSelecionado;
    }

    public void setLotePropostaFornecedorSelecionado(LotePropostaFornecedor lotePropostaFornecedorSelecionado) {
        this.lotePropostaFornecedorSelecionado = lotePropostaFornecedorSelecionado;
    }

    public PropostaFornecedorFacade getFacade() {
        return propostaFornecedorFacade;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setMoneyConverter(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }


    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, propostaFornecedorFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterRepresentante() {
        if (converterRepresentante == null) {
            converterRepresentante = new ConverterAutoComplete(RepresentanteLicitacao.class, propostaFornecedorFacade.getLicitacaoFacade().getRepresentanteLicitacaoFacade());
        }
        return converterRepresentante;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return propostaFornecedorFacade.getLicitacaoFacade().buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte.trim());
    }

    private void setarDataDaPropostaIgualDataDeAberturaDaLicitacao(Licitacao lic) {
        selecionado.setDataProposta(lic.getAbertaEm());
    }

    private List<ItemPropostaFornecedor> recuperarItensPropostaFornecedor() {
        return propostaFornecedorFacade.recuperaItensDaPropostaFornecedor(selecionado);
    }

    public void processaLicitacaoSelecionada() {
        carregarListasDaLicitacao();
        cancelarFornecedorSelecionado();
        cancelarItensProposta();
        cancelarLotesProposta();
        if (isLicitacaoValida()) {
            setarDataDaPropostaIgualDataDeAberturaDaLicitacao(selecionado.getLicitacao());
            propostaFornecedorFacade.criarLotesItensProposta(selecionado);
        }
    }

    private void cancelarLotesProposta() {
        selecionado.setLotes(null);
    }

    private void cancelarItensProposta() {
        selecionado.setListaDeItensPropostaFornecedor(null);
    }

    public boolean isLicitacaoValida() {
        if (!licitacaoComDataDeAberturaInformada()) {
            cancelarLicitacao();
            return false;
        }
        if (!licitacaoEmAndamento()) {
            cancelarLicitacao();
            return false;
        }
        if (todasAsPropostasRealizadas(selecionado.getLicitacao())) {
            FacesUtil.addAtencao("Todos os Fornecedores cadastrados para essa licitação já formularam propostas de preço.");
            cancelarLicitacao();
            return false;
        }
        if (isCertameGerado()) {
            FacesUtil.addOperacaoNaoPermitida("A Licitação selecionada já possui Mapa Comparativo gerado.");
            cancelarLicitacao();
            return false;
        }
        return true;
    }

    private boolean todasAsPropostasRealizadas(Licitacao licitacao) {
        List<LicitacaoFornecedor> fornecedoresLicitacao = licitacao.getFornecedores();
        List<PropostaFornecedor> propostasFornecedor = propostaFornecedorFacade.buscarPorLicitacao(licitacao);
        if (fornecedoresLicitacao == null || fornecedoresLicitacao.isEmpty() || propostasFornecedor == null || propostasFornecedor.isEmpty()) {
            return false;
        }
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedoresLicitacao) {
            boolean existeProposta = false;
            for (PropostaFornecedor propostaFornecedor : propostasFornecedor) {
                if (propostaFornecedor.getFornecedor().equals(licitacaoFornecedor.getEmpresa())) {
                    existeProposta = true;
                    break;
                }
            }
            if (!existeProposta) {
                return false;
            }
        }
        return true;
    }

    private void cancelarLicitacao() {
        selecionado.setLicitacao(null);
    }

    private boolean licitacaoEmAndamento() {
        if (!selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANDAMENTO)) {
            FacesUtil.addOperacaoNaoPermitida("A licitação selecionada não está em andamento. Status atual da licitação: " + selecionado.getLicitacao().getStatusAtual().getTipoSituacaoLicitacao().getDescricao());
            return false;
        }
        return true;
    }

    private boolean licitacaoComDataDeAberturaInformada() {
        if (selecionado.getLicitacao().getAbertaEm() == null) {
            FacesUtil.addOperacaoNaoPermitida("A licitação selecionada não tem data de abertura. Por favor, edite a mesma e informe a data de abertura desta licitação.");
            return false;
        }
        return true;
    }

    public void carregarListasDaLicitacao() {
        selecionado.setLicitacao(propostaFornecedorFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
    }

    public List<Pessoa> completarFornecedores(String parte) {
        List<Pessoa> retorno = Lists.newArrayList();
        parte = parte.trim().toLowerCase().replace(".", "").replace("-", "").replace("/", "");
        boolean temPropostaFaltando = false;
        if (licitacaoInformada()) {
            List<Pessoa> fornecedores = Lists.newArrayList();
            carregarListasDaLicitacao();
            temPropostaFaltando = isFornecedorPossuiPropostaLancada(temPropostaFaltando);
            if (!temPropostaFaltando) {
                FacesUtil.addAtencao("Todos os Fornecedores cadastrados para essa licitação já formularam propostas de preço.");
            }
            if (!registrouFornecedoresParaLicitacao(selecionado)) {
                FacesUtil.addAtencao("Não constam fornecedores cadastrados para essa licitação.");
            }
            for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
                if (selecionado.getLicitacao().isTecnicaEPreco()) {
                    adicionaFornecedorDaLicitacaoTecnicaPreco(fornecedores, licitacaoFornecedor);
                } else {
                    adicionaFornecedor(fornecedores, licitacaoFornecedor);
                }
            }
            removerFornecedoresQueJaFizeramPropostaParaALicitacao(selecionado.getLicitacao(), fornecedores);
            informarUsuario();

            for (Pessoa pessoa : fornecedores) {
                if (pessoa.getNome().toLowerCase().contains(parte)) {
                    retorno.add(pessoa);
                } else if (pessoa.getCpf_Cnpj().replace(".", "").replace("-", "").replace("/", "").contains(parte)) {
                    retorno.add(pessoa);
                }
            }

            return retorno;
        }
        return retorno;
    }

    private boolean isFornecedorPossuiPropostaLancada(boolean temPropostaFaltando) {
        if (getFornecedoresHabilitados() == null || getFornecedoresHabilitados().isEmpty()) {
            temPropostaFaltando = true;
            return temPropostaFaltando;
        }
        for (LicitacaoFornecedor licitacaoFornecedor : getFornecedoresHabilitados()) {
            if (!propostaFornecedorFacade.fornecedorJaFezPropostaParaEstaLicitacao(licitacaoFornecedor.getEmpresa(), selecionado.getLicitacao())) {
                temPropostaFaltando = true;
                break;
            }
        }
        return temPropostaFaltando;
    }

    private void informarUsuario() {
        informarFornecedoresInabilitados();
    }

    private boolean registrouFornecedoresParaLicitacao(PropostaFornecedor propostaFornecedor) {
        return !propostaFornecedor.getLicitacao().getFornecedores().isEmpty();
    }

    private boolean informarFornecedoresInabilitados() {
        if (!selecionado.getLicitacao().isPregao()) {
            if (getFornecedoresHabilitados().isEmpty()) {
                if (selecionado.getLicitacao().isTecnicaEPreco()) {
                    FacesUtil.addAtencao("Todos fornecedores desta licitação estão inabilitados referente a documentação ou critério técnico.");
                } else {
                    FacesUtil.addAtencao("Todos fornecedores desta licitação estão inabilitados referente a documentação.");
                }
                return true;
            }
        }
        return false;
    }

    private List<LicitacaoFornecedor> getFornecedoresHabilitados() {
        List<LicitacaoFornecedor> fornecedoresHabilitados = new ArrayList<>();
        for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
            if ((!selecionado.getLicitacao().isTecnicaEPreco())

                || (selecionado.getLicitacao().isTecnicaEPreco()
                && fornecedorEstaHabilitadoOuHabilitadoComRessalvaNaDocumentacao(licitacaoFornecedor)
                && fornecedorEstaHabilitadoNaTecnica(licitacaoFornecedor))

                || (selecionado.getLicitacao().isTecnicaEPreco()
                && licitacaoFornecedor.getLicitacao().isRDCMapaComparativo())

            ) {
                fornecedoresHabilitados.add(licitacaoFornecedor);
            }
        }
        return fornecedoresHabilitados;
    }

    private void removerFornecedoresQueJaFizeramPropostaParaALicitacao(Licitacao licitacao, List<Pessoa> fornecedores) {
        List<PropostaFornecedor> propostasDaLicitacao = propostaFornecedorFacade.buscarPorLicitacao(licitacao);
        List<Pessoa> fornecedoresComProposta = getFornecedoresComPropostaDePreco(propostasDaLicitacao);
        for (Pessoa pessoa : fornecedoresComProposta) {
            if (fornecedores.contains(pessoa)) {
                fornecedores.remove(pessoa);
            }
        }
    }

    private List<Pessoa> getFornecedoresComPropostaDePreco(List<PropostaFornecedor> propostasDaLicitacao) {
        List<Pessoa> fornecedoresComProposta = new ArrayList<>();
        for (PropostaFornecedor proposta : propostasDaLicitacao) {
            fornecedoresComProposta.add(proposta.getFornecedor());
        }
        return fornecedoresComProposta;
    }

    private boolean licitacaoInformada() {
        if (selecionado.getLicitacao() == null) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório informar a licitação antes do fornecedor.");
            return false;
        }
        return true;
    }

    private void adicionaFornecedor(List<Pessoa> retorno, LicitacaoFornecedor licitacaoFornecedor) {
        if (selecionado.getLicitacao().isPregao() || selecionado.getLicitacao().isRDC()) {
            retorno.add(licitacaoFornecedor.getEmpresa());
            return;
        }
        if (fornecedorEstaHabilitadoOuHabilitadoComRessalvaNaDocumentacao(licitacaoFornecedor)) {
            retorno.add(licitacaoFornecedor.getEmpresa());
        }
    }

    private void adicionaFornecedorDaLicitacaoTecnicaPreco(List<Pessoa> retorno, LicitacaoFornecedor licitacaoFornecedor) {
        if (fornecedorEstaHabilitadoOuHabilitadoComRessalvaNaDocumentacao(licitacaoFornecedor) && fornecedorEstaHabilitadoNaTecnica(licitacaoFornecedor)) {
            retorno.add(licitacaoFornecedor.getEmpresa());
        }
        if (licitacaoFornecedor.getLicitacao().isRDCMapaComparativo()) {
            retorno.add(licitacaoFornecedor.getEmpresa());
        }
    }

    private boolean fornecedorEstaHabilitadoNaTecnica(LicitacaoFornecedor licitacaoFornecedor) {
        return TipoClassificacaoFornecedor.HABILITADO.equals(licitacaoFornecedor.getClassificacaoTecnica());
    }

    private boolean fornecedorEstaHabilitadoOuHabilitadoComRessalvaNaDocumentacao(LicitacaoFornecedor licitacaoFornecedor) {
        return TipoClassificacaoFornecedor.HABILITADO.equals(licitacaoFornecedor.getTipoClassificacaoFornecedor()) || TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA.equals(licitacaoFornecedor.getTipoClassificacaoFornecedor());
    }

    public boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public void setaRepresentanteDoFornecedorSelecionado() {
        if (forncedorSelecionadoValido()) {
            for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
                if (licitacaoFornecedor.getEmpresa().equals(selecionado.getFornecedor())) {
                    selecionado.setRepresentante(licitacaoFornecedor.getRepresentante());
                    selecionado.setInstrumentoRepresentacao(licitacaoFornecedor.getInstrumentoRepresentacao());
                    return;
                }
            }
        }
    }

    public void cancelarFornecedorSelecionado() {
        selecionado.setFornecedor(null);
    }

    private boolean forncedorSelecionadoValido() {
        if (propostaFornecedorFacade.fornecedorJaFezPropostaParaEstaLicitacao(selecionado.getFornecedor(), selecionado.getLicitacao())) {
            FacesUtil.atualizarComponente("form-visualiza-proposta-fornecedor");
            FacesUtil.executaJavaScript("visualizarPropostaFornecedorDialog.show()");
            return false;
        }
        return true;
    }

    public void visualizarProposta() {
        PropostaFornecedor propostaRecuperada = propostaFornecedorFacade.recuperarPropostaDoFornecedorPorLicitacao(selecionado.getFornecedor(), selecionado.getLicitacao());
        if (propostaRecuperada != null) {
            selecionado = propostaRecuperada;
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + propostaRecuperada.getId() + "/");
        }
        cancelarFornecedorSelecionado();
    }

    public boolean isFornecedorPJ() {
        try {
            return selecionado.getFornecedor() instanceof PessoaJuridica;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void carregarLoteSelecionado(LotePropostaFornecedor loteProposta) {
        if (fornecedorInformado()) {
            lotePropostaFornecedorSelecionado = loteProposta;
            RequestContext.getCurrentInstance().update("formulario-proposta-fornecedor");
            FacesUtil.executaJavaScript("dialogPropostaFornecedor.show()");
        }
    }

    private boolean fornecedorInformado() {
        if (selecionado.getFornecedor() == null) {
            FacesUtil.addOperacaoNaoPermitida("Por favor, informe o fornecedor antes de realizar os lançamentos.");
            return false;
        }
        return true;
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public void confirmarProposta() {
        try {
            propostaFornecedorFacade.validarPropostas(selecionado.getLicitacao(), lotePropostaFornecedorSelecionado);
            atualizarItensDaProposta();
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:panel-proposta");
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:panel-item-proposta");
            FacesUtil.executaJavaScript("dialogPropostaFornecedor.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atualizarItensDaProposta() {
        limparItensDaProposta();
        for (LotePropostaFornecedor lote : selecionado.getLotes()) {
            for (ItemPropostaFornecedor item : lote.getItens()) {
                selecionado.setListaDeItensPropostaFornecedor(Util.adicionarObjetoEmLista(selecionado.getListaDeItensPropostaFornecedor(), item));
            }
        }
    }

    private void limparItensDaProposta() {
        selecionado.setListaDeItensPropostaFornecedor(null);
    }

    public void atualizaValorDoLote() {
        propostaFornecedorFacade.atualizaValorDoLote(lotePropostaFornecedorSelecionado);
    }

    public BigDecimal getValorTotalDaProposta() {
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (LotePropostaFornecedor lote : selecionado.getLotes()) {
                total = total.add(lote.getValor());
            }
            return total;
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }

    public void cancelarLancamentos() {
        for (LotePropostaFornecedor lote : selecionado.getLotes()) {
            if (lotePropostaFornecedorSelecionado.equals(lote)) {
                for (ItemPropostaFornecedor item : lote.getItens()) {
                    item.setMarca(null);
                    item.setModelo(null);
                    item.setDescricaoProduto(null);
                    item.setPreco(BigDecimal.ZERO);
                }
                lote.setValor(BigDecimal.ZERO);
            }
        }
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-lote");
        FacesUtil.executaJavaScript("dialogPropostaFornecedor.hide()");
    }

    public String getModeloProduto() {
        return modeloProduto;
    }

    public void setModeloProduto(String modeloProduto) {
        this.modeloProduto = modeloProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void carregarInformacoesProduto(ItemPropostaFornecedor item, String nomeComponente) {
        itemPropostaFornecedor = item;
        modeloProduto = itemPropostaFornecedor.getModelo();
        if (itemPropostaFornecedor.getItemProcessoDeCompra() != null && item.getItemProcessoDeCompra().getItemSolicitacaoMaterial() != null) {
            descricaoProduto = itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao();
        } else {
            descricaoProduto = itemPropostaFornecedor.getDescricaoProduto();
        }
        FacesUtil.atualizarComponente(nomeComponente);
    }

    public void confirmaInformacoesProduto() {
        ValidacaoException exception = new ValidacaoException();

        try {
            itemPropostaFornecedor.setModelo(modeloProduto);
            itemPropostaFornecedor.setDescricaoProduto(descricaoProduto);
            Util.validarQuantidadeCaracter(itemPropostaFornecedor, exception);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
        if (!exception.temMensagens())
            FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }

    public void removerInformacaoProduto() {
        itemPropostaFornecedor.setModelo("");
        itemPropostaFornecedor.setDescricaoProduto("");
    }

    public boolean isVerificarSeLicitacaoEhHomologada() {
        return selecionado.getLicitacao().isHomologada();
    }
}
