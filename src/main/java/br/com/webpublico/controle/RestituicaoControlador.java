package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OpcaoCredor;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.RestituicaoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "restituicaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaRestituicao", pattern = "/tributario/processo-de-restituicao/listar/",
        viewId = "/faces/tributario/contacorrente/processorestituicao/lista.xhtml"),
    @URLMapping(id = "novoRestituicao", pattern = "/tributario/processo-de-restituicao/novo/",
        viewId = "/faces/tributario/contacorrente/processorestituicao/edita.xhtml"),
    @URLMapping(id = "verRestituicao", pattern = "/tributario/processo-de-restituicao/ver/#{restituicaoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processorestituicao/visualizar.xhtml"),
    @URLMapping(id = "editarRestituicao", pattern = "/tributario/processo-de-restituicao/editar/#{restituicaoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processorestituicao/edita.xhtml"),
})
public class RestituicaoControlador extends PrettyControlador<Restituicao> implements Serializable, CRUD {

    @EJB
    private RestituicaoFacade restituicaoFacade;

    private List<EnderecoCorreio> enderecosContribuinte;
    private List<Telefone> telefonesContribuinte;
    private List<EnderecoCorreio> enderecosProcurador;
    private List<Telefone> telefonesProcurador;
    private List<ParcelaContaCorrenteTributaria> parcelasContaCorrente;

    private ContaCorrenteTributaria contaCorrenteTributaria;
    private Integer index;

    public RestituicaoControlador() {
        super(Restituicao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return restituicaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/processo-de-restituicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        Web.limpaNavegacao();
        inicializarDados();
        buscarContaCorrenteTributaria();
    }

    @Override
    @URLAction(mappingId = "editarRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (isOperacaoEditar()) {
            parcelasContaCorrente = Lists.newArrayList();
            preencherDadosPessoa();
            preencherDadosProcurador();
            for (ItemRestituicao item : selecionado.getItens()) {
                ParcelaContaCorrenteTributaria parcela = new ParcelaContaCorrenteTributaria();
                parcela.setParcelaValorDivida(item.getParcelaValorDivida());
                parcela.setResultadoParcela(item.getResultadoParcela());
                parcela.setValorDiferenca(item.getDiferenca());
                parcela.setValorDiferencaAtualizada(item.getDiferencaAtualizada());
                parcelasContaCorrente.add(parcela);
            }
            carregarContaCorrente();
            if (contaCorrenteTributaria != null) {
                carregarInformacoesDasParcelasEditar();
                carregarValorParaRestituicao();
            }
        }
    }

    @Override
    @URLAction(mappingId = "verRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarInformacoesProcessoVer();
    }

    @Override
    public void salvar() {
        try {
            validarRestituicao();
            selecionado = restituicaoFacade.salvarRestituicao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redicionarParaVisualiza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar restituição: ", ex);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar processo de restituição. Detalhes: " + ex.getMessage());
        }
    }

    private void redicionarParaVisualiza() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void validarRestituicao() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo ou fundamentação legal deve ser informado.");
        }
        if (selecionado.getContribuinte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo contribuinte deve ser informado.");
        }
        if (selecionado.getUnidadeEmpenho() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório selecionar a pessoa para o empenho através do botão renovar da aba dados da restituição.");
        }
        if (selecionado.getItens() != null && selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos uma parcela para restituição.");
        }
        if (selecionado.getContribuinte() != null) {
            Restituicao restituicao = restituicaoFacade.buscarRestituicaoEmAbertoPorIdPessoa(selecionado.getContribuinte().getId());
            if (restituicao != null && !restituicao.getId().equals(selecionado.getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um processo de restituição Em Aberto para o contribuinte: "
                    + selecionado.getContribuinte().getNomeCpfCnpj());
            }
        }
        ve.lancarException();
    }

    private void inicializarDados() {
        parcelasContaCorrente = Lists.newArrayList();
        enderecosContribuinte = Lists.newArrayList();
        enderecosProcurador = Lists.newArrayList();
        telefonesContribuinte = Lists.newArrayList();
        telefonesProcurador = Lists.newArrayList();

        selecionado.setDataLancamento(new Date());
        selecionado.setDataRestituicao(new Date());
        selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
        selecionado.setUsuarioResponsavel(restituicaoFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(restituicaoFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setCodigo(restituicaoFacade.buscarProxCodigoPorExercicio(selecionado.getExercicio()));
    }


    public List<ParcelaContaCorrenteTributaria> getParcelasContaCorrente() {
        return parcelasContaCorrente;
    }

    public void setParcelasContaCorrente(List<ParcelaContaCorrenteTributaria> parcelasContaCorrente) {
        this.parcelasContaCorrente = parcelasContaCorrente;
    }

    public List<EnderecoCorreio> getEnderecosContribuinte() {
        return enderecosContribuinte;
    }

    public void setEnderecosContribuinte(List<EnderecoCorreio> enderecosContribuinte) {
        this.enderecosContribuinte = enderecosContribuinte;
    }

    public List<Telefone> getTelefonesContribuinte() {
        return telefonesContribuinte;
    }

    public void setTelefonesContribuinte(List<Telefone> telefonesContribuinte) {
        this.telefonesContribuinte = telefonesContribuinte;
    }

    public List<EnderecoCorreio> getEnderecosProcurador() {
        return enderecosProcurador;
    }

    public void setEnderecosProcurador(List<EnderecoCorreio> enderecosProcurador) {
        this.enderecosProcurador = enderecosProcurador;
    }

    public List<Telefone> getTelefonesProcurador() {
        return telefonesProcurador;
    }

    public void setTelefonesProcurador(List<Telefone> telefonesProcurador) {
        this.telefonesProcurador = telefonesProcurador;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return restituicaoFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Pessoa> completarPessoasComContaCorrente(String filtro) {
        return restituicaoFacade.getContaCorrenteTributariaFacade().buscarPessoasComContaCorrente(filtro);
    }

    public List<Pessoa> completarProcuradores(String filtro) {
        return restituicaoFacade.getPessoaFacade().listaTodasPessoasAtivas(filtro);
    }

    public List<SelectItem> opcoesCredor() {
        return Util.getListSelectItem(OpcaoCredor.values());
    }

    public void buscarContaCorrenteTributaria() {
        selecionado.setPessoaEmpenho(null);
        selecionado.setUnidadeEmpenho(null);
        if (selecionado.getContribuinte() != null) {
            preencherDadosPessoa();
            carregarContaCorrente();

            if (contaCorrenteTributaria != null) {
                carregarInformacoesDasParcelas();
                carregarValorParaRestituicao();
            }
            if (parcelasContaCorrente.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("A conta corrente do contribuinte " + selecionado.getContribuinte().getCpf_Cnpj() +
                    " não possui parcelas de 'Crédito da Arrecadação'!");
                limparDados();
            }
        }
    }

    public void preencherDadosProcurador() {
        if (selecionado.getProcurador() != null) {
            Pessoa procurador = restituicaoFacade.getPessoaFacade().recuperar(selecionado.getProcurador().getId());
            enderecosProcurador = procurador.getEnderecos();
            telefonesProcurador = procurador.getTelefones();
            selecionado.setProcurador(procurador);
        }
    }

    private void preencherDadosPessoa() {
        Pessoa contribuinte = restituicaoFacade.getPessoaFacade().recuperar(selecionado.getContribuinte().getId());
        enderecosContribuinte = contribuinte.getEnderecos();
        telefonesContribuinte = contribuinte.getTelefones();
        selecionado.setContribuinte(contribuinte);
    }

    public void limparDados() {
        selecionado.setContribuinte(null);
        contaCorrenteTributaria = null;
        parcelasContaCorrente = Lists.newArrayList();
        enderecosContribuinte = Lists.newArrayList();
        telefonesContribuinte = Lists.newArrayList();
        selecionado.getItens().clear();
        removerTodosParcelaCredito();
    }


    private void carregarContaCorrente() {
        contaCorrenteTributaria = restituicaoFacade.getContaCorrenteTributariaFacade().buscarContaCorrentePorPessoa(selecionado.getContribuinte());
        if (contaCorrenteTributaria != null) {
            contaCorrenteTributaria = restituicaoFacade.getContaCorrenteTributariaFacade().recuperar(contaCorrenteTributaria.getId());
        }
    }

    private void carregarInformacoesDasParcelas() {
        selecionado.getItens().clear();
        carregarInformacoesDasParcelasEditar();
    }

    private void carregarInformacoesDasParcelasEditar() {
        parcelasContaCorrente = Lists.newArrayList();
        for (ParcelaContaCorrenteTributaria parcela : contaCorrenteTributaria.getParcelas()) {
            if (!parcela.getCompensada() && TipoDiferencaContaCorrente.CREDITO_ARRECADACAO.equals(parcela.getTipoDiferenca())) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getParcelaValorDivida().getId());
                consultaParcela.executaConsulta();
                restituicaoFacade.getProcessoCreditoContaCorrenteFacade().buscarValorPagoNaArrecadacaoRestituicao(selecionado, consultaParcela.getResultados());
                parcela.setResultadoParcela(consultaParcela.getResultados().get(0));
                parcela.setValorDiferencaAtualizada(restituicaoFacade.getContaCorrenteTributariaFacade().getValorDiferencaAtualizada(parcela).subtract(parcela.getValorCompesado()));
                parcelasContaCorrente.add(parcela);
            }
        }
    }

    private void carregarInformacoesProcessoVer() {
        for (ItemRestituicao item : selecionado.getItens()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, item.getParcelaValorDivida().getId());
            consultaParcela.executaConsulta();
            restituicaoFacade.getProcessoCreditoContaCorrenteFacade().buscarValorPagoNaArrecadacaoRestituicao(selecionado, consultaParcela.getResultados());

            item.setResultadoParcela(consultaParcela.getResultados().get(0));
        }
        carregarContaCorrente();
        carregarValorParaRestituicao();
        preencherDadosPessoa();
        preencherDadosProcurador();
    }

    private void carregarValorParaRestituicao() {
        selecionado.setValorRestituir(getValorRestituicao());
    }

    private BigDecimal getValorRestituicao() {
        BigDecimal total = BigDecimal.ZERO;
        if (contaCorrenteTributaria != null) {
            for (ItemRestituicao credito : selecionado.getItens()) {
                total = total.add((Operacoes.VER.equals(operacao) && !SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao())) ?
                    credito.getValorRestituido() : credito.getDiferencaAtualizada());
            }
        }
        return total;
    }

    public boolean hasTodasParcelasCredito() {
        if (parcelasContaCorrente != null) {
            for (ParcelaContaCorrenteTributaria parcela : parcelasContaCorrente) {
                boolean temItem = false;
                for (ItemRestituicao credito : selecionado.getItens()) {
                    if (credito.getParcelaValorDivida().equals(parcela.getParcelaValorDivida())) {
                        temItem = true;
                        break;
                    }
                }
                if (!temItem) {
                    return false;
                }
            }
        }
        return selecionado != null && !selecionado.getItens().isEmpty();
    }

    public boolean hasParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        for (ItemRestituicao credito : selecionado.getItens()) {
            if (credito.getParcelaValorDivida().equals(parcela.getParcelaValorDivida())) {
                return true;
            }
        }
        return false;
    }

    public void adicionarTodosParcelaCredito() {
        for (ParcelaContaCorrenteTributaria parcela : parcelasContaCorrente) {
            adicionarParcelaCredito(parcela);
        }
        carregarValorParaRestituicao();
    }

    public void adicionarParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        try {
            ItemRestituicao credito = new ItemRestituicao();
            credito.setRestituicao(selecionado);
            credito.setParcelaValorDivida(parcela.getParcelaValorDivida());

            credito.setResultadoParcela(parcela.getResultadoParcela());
            credito.setReferencia(credito.getResultadoParcela().getReferencia());
            credito.setDiferenca(parcela.getValorDiferenca());
            credito.setValor(credito.getResultadoParcela().getValorTotal());
            credito.setValorPago(credito.getResultadoParcela().getValorPago());
            credito.setDiferencaAtualizada(parcela.getValorDiferencaAtualizada());
            credito.setValorRestituido(parcela.getValorDiferencaAtualizada());
            selecionado.getItens().add(credito);

            carregarValorParaRestituicao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarTiposTributo() {
        ValidacaoException ve = new ValidacaoException();
        if (parcelasContaCorrente.isEmpty()) {
            selecionado.setUnidadeEmpenho(null);
        }
        if (selecionado.getUnidadeEmpenho() != null) {
            for (ParcelaContaCorrenteTributaria parcela : parcelasContaCorrente) {
                if (hasItemValorDivida(parcela.getParcelaValorDivida())) {
                    Tributo tributoParametro = parcela.getParcelaValorDivida().getValorDivida().getItemValorDividas().get(0).getTributo();
                    if (tributoParametro != null && tributoParametro.getEntidade() != null) {
                        if (!tributoParametro.getEntidade().getUnidadeOrganizacionalOrc().getId().equals(selecionado.getUnidadeEmpenho().getId())) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("As parcela selecionadas possuem tributos diferentes.");
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void removerTodosParcelaCredito() {
        selecionado.getItens().clear();
        selecionado.setPessoaEmpenho(null);
        selecionado.setUnidadeEmpenho(null);
        carregarValorParaRestituicao();
    }

    public void removerParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        ItemRestituicao aRemover = null;
        for (ItemRestituicao credito : selecionado.getItens()) {
            if (credito.getParcelaValorDivida().equals(parcela.getParcelaValorDivida())) {
                aRemover = credito;
            }
        }
        selecionado.getItens().remove(aRemover);
        selecionado.setPessoaEmpenho(null);
        selecionado.setUnidadeEmpenho(null);
        carregarValorParaRestituicao();
    }

    public boolean canMostrarTabelasPessoa(List<Object> lista) {
        return selecionado != null && (selecionado.getContribuinte() != null || selecionado.getProcurador() != null) && (lista != null && !lista.isEmpty());
    }

    public void poeNaSessao() {
        try {
            Web.poeTodosFieldsNaSessao(this);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void renovar() {
        try {
            validarRenovar();
            validarTiposTributo();
            atribuirUnidadeEmpenho();

            selecionado.setPessoaEmpenho(OpcaoCredor.CONTRIBUINTE.equals(selecionado.getOpcaoCredor()) ? selecionado.getContribuinte() : selecionado.getProcurador());
            FacesUtil.addOperacaoRealizada("Dados para empenho adicionados com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void processar() {
        try {
            ConfiguracaoContabil configuracaoContabil = restituicaoFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
            validarConfiguracaoContabil(configuracaoContabil);
            carregarValorParaRestituicao();

            restituicaoFacade.processarRestituicao(selecionado, configuracaoContabil);
            FacesUtil.addOperacaoRealizada("Restituição processada com sucesso!");
            redicionarParaVisualiza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica en) {
            logger.error("Rolback processo...", en);
            FacesUtil.addOperacaoNaoRealizada("Erro ao processar restituição. Detalhes: " + en.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao processar restituição: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao processar restituição. Detalhes: " + e.getMessage());
        }
    }

    private void validarConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabil.getClasseTribContRestituicao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário adicionar a classe de credor Restituição na configuração contábil.");
        }
        ve.lancarException();
    }

    public void estornarRestituicao() {
        try {
            Empenho empenho = restituicaoFacade.buscarEmpenhoDaSolicitacao(selecionado.getId());
            validarEstorno(empenho);
            restituicaoFacade.estornarRestituicaoAndSolicitacaoEmpenho(selecionado, empenho);
            FacesUtil.executaJavaScript("dialogEstorno.hide();");

            FacesUtil.addOperacaoRealizada("Estorno realizado com sucesso!");
            redicionarParaVisualiza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica en) {
            logger.error("Rolback estorno...", en);
            FacesUtil.addOperacaoNaoRealizada("Erro ao estornar restituição. Detalhes: " + en.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao estornar restituição: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao estornar processo de restituição. Detalhes: " + e.getMessage());
        }
    }

    private void validarRenovar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOpcaoCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a opção de credor.");
        } else {
            if (OpcaoCredor.PROCURADOR.equals(selecionado.getOpcaoCredor()) && selecionado.getProcurador() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um procurador na aba dados do contribuinte");
            }
        }
        if (selecionado.getItens() != null && selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo meno uma parcela para restituição.");
        }
        ve.lancarException();
    }

    private void validarEstorno(Empenho empenho) {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getMotivoEstorno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo do estorno é obrigatório.");
        }
        if (empenho != null && empenho.getEmpenhoEstornos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação de empenho referente a essa restituição já foi empenhada.");
        }
        ve.lancarException();
    }

    private void atribuirUnidadeEmpenho() {
        for (ItemRestituicao item : selecionado.getItens()) {
            if (selecionado.getUnidadeEmpenho() == null) {
                if (hasItemValorDivida(item.getParcelaValorDivida()) && !selecionado.getItens().isEmpty()) {
                    Tributo tributo = item.getParcelaValorDivida().getValorDivida().getItemValorDividas().get(0).getTributo();
                    if (tributo != null) {
                        Entidade entidade = tributo.getEntidade();
                        if (entidade != null) {
                            selecionado.setUnidadeEmpenho(entidade.getUnidadeOrganizacionalOrc());
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean hasItemValorDivida(ParcelaValorDivida parcela) {
        if (parcela != null && parcela.getValorDivida() != null) {
            ValorDivida valorDivida = restituicaoFacade.recuperarItens(parcela.getValorDivida().getId());
            parcela.setValorDivida(valorDivida);
            return valorDivida.getItemValorDividas() != null &&
                !valorDivida.getItemValorDividas().isEmpty();
        }
        return false;
    }

    public boolean canProcessar() {
        return SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao())
            && selecionado.getId() != null;
    }

    public void redirecionarParaContaCorrente() {
        ContaCorrenteTributaria contaCorrente = restituicaoFacade.getContaCorrenteTributariaFacade().buscarContaCorrentePorPessoa(selecionado.getContribuinte());
        if (contaCorrente != null) {
            Web.navegacao(getUrlAtual(), "/tributario/conta-corrente-tributaria/ver/" + contaCorrente.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível localizar a conta corrente desse contribuinte!");
        }
    }

    public void limparEstorno() {
        selecionado.setMotivoEstorno(null);
    }

    public void atribuirDataEstorno() {
        selecionado.setDataEstorno(new Date());
    }

    public boolean canEstornar() {
        return !SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao());
    }

    public void limparPessoaEmpenho() {
        selecionado.setPessoaEmpenho(null);
        selecionado.setUnidadeEmpenho(null);
    }

    public void alterarTab(TabChangeEvent evt) {
        TabView tabView = (TabView) evt.getComponent();
        index = tabView.getChildren().indexOf(evt.getTab());
    }

    public void validarDataRestituicao() {
        if (selecionado.getDataRestituicao() != null && selecionado.getDataRestituicao().after(new Date())) {
            selecionado.setDataRestituicao(new Date());
            FacesUtil.addOperacaoNaoRealizada("A data de Restituição não pode ser maior que a data atual.");
        }
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Processo de Restituição de Débitos");
            dto.adicionarParametro("USUARIO", restituicaoFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA DE FINANÇAS");
            dto.adicionarParametro("TITULO", "Processo de Restituição de Débitos");
            dto.adicionarParametro("ID", selecionado.getId());
            dto.setApi("tributario/restituicao/");
            ReportService.getInstance().gerarRelatorio(restituicaoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
