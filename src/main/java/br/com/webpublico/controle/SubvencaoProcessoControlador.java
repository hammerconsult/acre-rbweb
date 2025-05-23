package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaSubvencao;
import br.com.webpublico.entidadesauxiliares.ParcelaSubvencao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoSubvencao;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 18/12/13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "subvencaoProcessoControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoSubvencao", pattern = "/subvencao-processo/novo/", viewId = "/faces/tributario/dividaativa/subvencao/processosubvencao/edita.xhtml"),
    @URLMapping(id = "editarProcessoSubvencao", pattern = "/subvencao-processo/editar/#{subvencaoProcessoControlador.id}/", viewId = "/faces/tributario/dividaativa/subvencao/processosubvencao/edita.xhtml"),
    @URLMapping(id = "listarProcessoSubvencao", pattern = "/subvencao-processo/listar/", viewId = "/faces/tributario/dividaativa/subvencao/processosubvencao/lista.xhtml"),
    @URLMapping(id = "verProcessoSubvencao", pattern = "/subvencao-processo/ver/#{subvencaoProcessoControlador.id}/", viewId = "/faces/tributario/dividaativa/subvencao/processosubvencao/visualizar.xhtml"),
    @URLMapping(id = "debitoPesquisaProcessoSubvencao", pattern = "/subvencao-processo/debitos/", viewId = "/faces/tributario/dividaativa/subvencao/processosubvencao/consulta.xhtml")
})
public class SubvencaoProcessoControlador extends PrettyControlador<SubvencaoProcesso> implements CRUD, Serializable {

    @EJB
    private SubvencaoProcessoFacade subvencaoProcessoFacade;
    @EJB
    private SubvencaoParametroFacade subvencaoParametroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade dam;
    @EJB
    private ContribuinteDebitoOutorgaFacade contribuinteDebitoOutorgaFacade;
    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;
    @EJB
    private SingletonGeradorCodigoSubvencao singletonGeradorCodigoSubvencao;
    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private BloqueioOutorgaFacade bloqueioOutorgaFacade;
    private List<SubvencaoEmpresas> subvencaoEmpresas;
    private TipoCadastroTributario tipoCadastroTributario;
    private List<ResultadoParcela> resultadoConsulta;
    private Cadastro cadastro;
    private List<ResultadoParcela> debitoSelecionados;
    private BigDecimal valorTotalSubvencionar;
    private BigDecimal saldoSubvencao;
    private BigDecimal saldo;
    private SubvencaoEmpresas sub;
    private Boolean bloqueiaAdicionarParcela = false;
    private Boolean bloqueiaCalculoDasDividas = true;
    private BigDecimal saldoDaEmpresaRecuperado;
    private List<SubvencaoParcela> subvencaoParcelasLista;
    private Exercicio exercicio;
    private boolean subvencaoReincidenteNoMes;
    private List<ParcelaSubvencao> itemParcelaSubvencao;
    private Map<SubvencaoEmpresas, List<SubvencaoParcela>> parcelasParaImpressaoDAM;
    private SubvencaoEmpresas empresaParaDamNoDialog;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<Future> futuresEfetivacao;
    private List<ResultadoParcela> parcelas;

    public SubvencaoProcessoControlador() {
        super(SubvencaoProcesso.class);
        subvencaoParcelasLista = Lists.newArrayList();
        itemParcelaSubvencao = Lists.newArrayList();
        parcelasParaImpressaoDAM = new HashMap<>();
    }

    @URLAction(mappingId = "novoProcessoSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        recuperarAtributoDaSessao();
        selecionado.setSituacao(SituacaoSubvencao.EM_ABERTO);
        selecionado.setDataLancamento(SistemaFacade.getDataCorrente());

    }

    public boolean isSubvencaoReincidenteNoMes() {
        return subvencaoReincidenteNoMes;
    }

    public void setSubvencaoReincidenteNoMes(boolean subvencaoReincidenteNoMes) {
        this.subvencaoReincidenteNoMes = subvencaoReincidenteNoMes;
    }

    @URLAction(mappingId = "verProcessoSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atribuirValorSubvencionarDeAcordoComOParametro();
        for (SubvencaoEmpresas subvencaoEmpresas : selecionado.getSubvencaoEmpresas()) {
            Collections.sort(subvencaoEmpresas.getSubvencaoParcela());
        }
        Collections.sort(selecionado.getSubvencaoEmpresas());
    }

    @URLAction(mappingId = "editarProcessoSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        ver();
        operacao = Operacoes.EDITAR;
        if (SituacaoSubvencao.EFETIVADO.equals(selecionado.getSituacao())) {
            FacesUtil.addOperacaoNaoPermitida("Não é possíver a edição, o Processo de Subvenção está efetivado.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        }
        subvencaoReincidente();
    }

    private void atribuirValorSubvencionarDeAcordoComOParametro() {
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            try {
                empresas.setValorParametro(empresas.getAlunosTransportados().multiply(selecionado.getValorPassagem()));
            } catch (NullPointerException ex) {
                empresas.setValorParametro(BigDecimal.ZERO);
            }
        }
    }

    private void buscarEmpresasCredorasVigentesNoParametro() {
        for (CadastroEconomicoSubvencao cadastroEconomicoSubvencao : subvencaoProcessoFacade.buscarCadastrosEconomicoVigenteNoParametro(selecionado.getTipoPassageiro(), selecionado.getDataReferenciaParametro())) {
            SubvencaoEmpresas empresaCredora = new SubvencaoEmpresas();
            empresaCredora.setTemDebito(true);
            empresaCredora.setSituacao(SituacaoSubvencao.EM_ABERTO);
            empresaCredora.setCadastroEconomico(cadastroEconomicoSubvencao.getCadastroEconomico());
            empresaCredora.setSubvencaoProcesso(selecionado);
            selecionado.getSubvencaoEmpresas().add(empresaCredora);
        }
    }

    private ParametrosOutorgaRBTrans getParametrosOutorgaPorExercicio(Exercicio exercicio) {
        return parametroOutorgaFacade.recuperarParametroSubvencao(exercicio);
    }

    public ParametroOutorgaSubvencao getParametroSubVencaoAtual() {
        ParametrosOutorgaRBTrans parametrosOutorgaRBTrans = getParametrosOutorgaPorExercicio(selecionado.getExercicio());
        for (ParametroOutorgaSubvencao parametroOutorgaSubvencao : parametrosOutorgaRBTrans.getParametroOutorgaSubvencao()) {
            if (parametroOutorgaSubvencao.getMes().equals(selecionado.getMes()) &&
                parametroOutorgaSubvencao.getTipoPassageiro().equals(selecionado.getTipoPassageiro())) {
                return parametroOutorgaSubvencao;
            }
        }
        return null;
    }

    private void atribuirEmpresaDevedora() {
        for (SubvencaoEmpresas empresaCredora : selecionado.getSubvencaoEmpresas()) {
            List<EmpresaDevedoraSubvencao> empresaDevedoraSubvencao = subvencaoProcessoFacade.recuperarEmpresasDevedoras(empresaCredora.getCadastroEconomico());
            if (empresaDevedoraSubvencao != null) {
                for (EmpresaDevedoraSubvencao devedoraSubvencao : empresaDevedoraSubvencao) {
                    resultadoConsulta = Lists.newArrayList();
                    pesquisarParcelas(devedoraSubvencao.getCadastroEconomico());
                    if (!resultadoConsulta.isEmpty()) {
                        empresaCredora.setEmpresaDevedora(devedoraSubvencao.getCadastroEconomico());
                        break;
                    }
                }
                if (empresaCredora.getEmpresaDevedora() == null) {
                    empresaCredora.setEmpresaDevedora(empresaDevedoraSubvencao.get(empresaDevedoraSubvencao.size() - 1).getCadastroEconomico());
                }
            }
        }
    }


    @URLAction(mappingId = "debitoPesquisaProcessoSubvencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void pesquisaDebitos() {
        setOperacao(Operacoes.EDITAR);
        cadastro = (CadastroEconomico) Web.pegaDaSessao("cadastroEconomico");
        resultadoConsulta = Lists.newArrayList();
        tipoCadastroTributario = TipoCadastroTributario.ECONOMICO;
        debitoSelecionados = new ArrayList<>();
        valorTotalSubvencionar = BigDecimal.ZERO;
        definirSubvencaoReicidenteSessao();
        selecionado = (SubvencaoProcesso) Web.pegaDaSessao("selecionado");
        subvencaoEmpresas = (List<SubvencaoEmpresas>) Web.pegaDaSessao("listaCadastroEconomicoSubvencao");
        sub = (SubvencaoEmpresas) Web.pegaDaSessao("subvencaoEmpresa");
        saldoSubvencao = sub.getSaldoSubvencionar();
        saldo = (BigDecimal) Web.pegaDaSessao("saldo");
        valorASerSubvencionado();
        valorDoSaldo();
    }


    public void recuperarAtributoDaSessao() {
        subvencaoEmpresas = (List<SubvencaoEmpresas>) Web.pegaDaSessao("listaCadastroEconomicoSubvencao");

        selecionado = (SubvencaoProcesso) Web.pegaDaSessao("selecionado");
        if (selecionado == null) {
            selecionado = new SubvencaoProcesso();
        }
        saldo = (BigDecimal) Web.pegaDaSessao("saldo");
        definirSubvencaoReicidenteSessao();
        sub = (SubvencaoEmpresas) Web.pegaDaSessao("sub");
        if (sub == null) {
            sub = new SubvencaoEmpresas();
        }


        debitoSelecionados = (List<ResultadoParcela>) Web.pegaDaSessao("debitoSelecionados");
        List<SubvencaoEmpresas> empresasCredoras = Lists.newArrayList(selecionado.getSubvencaoEmpresas());
    }

    private void definirSubvencaoReicidenteSessao() {
        Boolean subvencaoReincidenteNoMes = (Boolean) Web.pegaDaSessao("subvencaoReincidenteNoMes");
        this.subvencaoReincidenteNoMes = subvencaoReincidenteNoMes != null ? subvencaoReincidenteNoMes : false;
    }

    @Override
    public void salvar() {
        try {
            validarSeOProcessoTemEmpresasAdicionadas();
            verificarSeTemDebitoParaSubvencao();
            Web.limpaNavegacao();
            if (selecionado.getId() == null) {
                selecionado.setNumeroDoProcesso(Long.parseLong(gerarNumeroProcesso(selecionado.getDataLancamento())));
            }
            selecionado = subvencaoProcessoFacade.salva(selecionado);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar o processo: " + ex.getMessage());
        }
    }


    public String gerarNumeroProcesso(Date dataLancamento) {
        try {
            String numeroProcesso = singletonGeradorCodigoSubvencao.getProximoCodigo(dataLancamento).toString();
            if (numeroProcesso.length() < 2) {
                numeroProcesso = StringUtil.cortaOuCompletaDireita(DataUtil.getAno(dataLancamento) + "", 9, "0").concat(numeroProcesso);
            }
            return numeroProcesso;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new ExcecaoNegocioGenerica("");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/subvencao-processo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return subvencaoProcessoFacade;
    }

    public Map<SubvencaoEmpresas, List<SubvencaoParcela>> getParcelasParaImpressaoDAM() {
        return parcelasParaImpressaoDAM;
    }

    public void setParcelasParaImpressaoDAM(Map<SubvencaoEmpresas, List<SubvencaoParcela>> parcelasParaImpressaoDAM) {
        this.parcelasParaImpressaoDAM = parcelasParaImpressaoDAM;
    }

    private void adicionarParametro(ConsultaParcela consulta, DividaSubvencao dividaSubvencao, CadastroEconomico cadastroEconomico) {
        if (cadastroEconomico != null && cadastroEconomico.getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastroEconomico.getId());
        }
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, dividaSubvencao.getDivida().getId());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, dividaSubvencao.getExercicioInicial().getAno());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, dividaSubvencao.getExercicioFinal().getAno());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);

        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    private List<OrdenacaoParcelaSubvencao> getOrdenacaoParcela() {
        return subvencaoParametroFacade.buscarListaOrdenacaoSubvencao();
    }

    public void pesquisar() {
        pesquisarParcelas((CadastroEconomico) cadastro);
        if (getOrdenacaoParcela() != null) {
            Collections.sort(itemParcelaSubvencao, new OrdenaResultadoParcelaSubvencao(getOrdenacaoParcela()));
        }
        bloqueiaCalculoDasDividas = false;
        try {
            verificaParcelasAdicionadas();
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "Não existe débitos em aberto de acordo com as dívidas adicionas no parâmetro para esse C.M.C.");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }

    private void pesquisarParcelas(CadastroEconomico cadastroEconomico) {
        resultadoConsulta.clear();
        itemParcelaSubvencao.clear();
        for (DividaSubvencao dividaSubvencao : subvencaoProcessoFacade.recuperarDividaDoParametro()) {
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta, dividaSubvencao, cadastroEconomico);
            consulta.executaConsulta();
            if (dividaSubvencao.getQtdeMinimaParcela() != null && dividaSubvencao.getQtdeMaximaParcela() != null) {
                for (ResultadoParcela parcela : consulta.getResultados()) {
                    String[] quebra = parcela.getParcela().split("/");
                    String numeroParcela = quebra[1];
                    if (Integer.parseInt(numeroParcela) >= dividaSubvencao.getQtdeMinimaParcela() && Integer.parseInt(numeroParcela) <= dividaSubvencao.getQtdeMaximaParcela()) {
                        resultadoConsulta.add(parcela);
                        criarItemParcelaSubvencao(parcela, dividaSubvencao.getNaturezaDividaSubvencao());
                    }
                }
            } else {
                resultadoConsulta.addAll(consulta.getResultados());
                for (ResultadoParcela resultadoParcela : resultadoConsulta) {
                    criarItemParcelaSubvencao(resultadoParcela, dividaSubvencao.getNaturezaDividaSubvencao());
                }
            }
        }
    }

    private void criarItemParcelaSubvencao(ResultadoParcela resultadoParcela, NaturezaDividaSubvencao naturezaDividaSubvencao) {
        ParcelaSubvencao parcelaSubvencao = new ParcelaSubvencao();
        parcelaSubvencao.setNaturezaDividaSubvencao(naturezaDividaSubvencao);
        parcelaSubvencao.setResultadoParcela(resultadoParcela);
        itemParcelaSubvencao.add(parcelaSubvencao);
    }

    public List<ParcelaSubvencao> getItemParcelaSubvencao() {
        return itemParcelaSubvencao;
    }

    public void setItemParcelaSubvencao(List<ParcelaSubvencao> itemParcelaSubvencao) {
        this.itemParcelaSubvencao = itemParcelaSubvencao;
    }

    public Date getDataOperacao() {
        return getSistemaControlador().getDataOperacao();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void efetivar() {
        try {
            if (subvencaoProcessoFacade.isSubvencaoProcessoComDebitoResidualParcelamento(selecionado)) {
                sub = null;
                FacesUtil.executaJavaScript("dlgResidualParcelamento.show()");
                return;
            }
            verificarSeTemDebitoParaSubvencao();
            validarDebitoResidualProcesso();
            selecionado.setSituacao(SituacaoSubvencao.EFETIVADO);
            futuresEfetivacao = Lists.newArrayList();

            int qtdeParcelas = getQtdeParcelas();
            assistenteBarraProgresso = new AssistenteBarraProgresso("Efetivando processo de Subvenção...", qtdeParcelas);
            for (SubvencaoEmpresas empresa : selecionado.getSubvencaoEmpresas()) {
                if (SituacaoSubvencao.EM_ABERTO.equals(empresa.getSituacao())) {
                    futuresEfetivacao.add(subvencaoProcessoFacade.pagarParcelaAndCancelarDam(selecionado, empresa, assistenteBarraProgresso));
                }
            }
            FacesUtil.executaJavaScript("dialogEfetivando.show()");
            FacesUtil.executaJavaScript("comecarEfetivacao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao pagar a parcela e cancelar o Dam! {}", ex);
            descobrirETratarException(ex);
        }
    }

    private void validarDebitoResidualEmpresa(SubvencaoEmpresas subvencaoEmpresas) {
        SubvencaoParcela ultimaSubvencaoParcela = subvencaoEmpresas.getUltimaSubvencaoParcela();
        if (ultimaSubvencaoParcela != null &&
            ultimaSubvencaoParcela.getValorResidual() != null &&
            ultimaSubvencaoParcela.getValorResidual().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Existem parcelas com valor residual negativo, por favor edite a subvenção e reajuste as parcelas.");
        }
    }

    private void validarDebitoResidualProcesso() {
        for (SubvencaoEmpresas subvencaoEmpresas : selecionado.getSubvencaoEmpresas()) {
            validarDebitoResidualEmpresa(subvencaoEmpresas);
        }
    }


    private void recuperarParcelasGeradas(SubvencaoProcesso processo) {
        for (SubvencaoEmpresas pagamentoSubvencao : processo.getSubvencaoEmpresas()) {
            List<CalculoPagamentoSubvencao> calculo = subvencaoProcessoFacade.recuperarCalculo(pagamentoSubvencao);
            List<Long> ids = Lists.newArrayList();
            if (calculo != null) {
                for (CalculoPagamentoSubvencao calculoPagamentoSubvencao : calculo) {
                    ids.add(calculoPagamentoSubvencao.getId());
                }
                parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, ids).executaConsulta().getResultados();
            }
        }
    }

    private int getQtdeParcelas() {
        int qtdeParcelas = 0;
        for (SubvencaoEmpresas empresa : selecionado.getSubvencaoEmpresas()) {
            if (empresa.getSituacao().equals(SituacaoSubvencao.EM_ABERTO)) {
                qtdeParcelas = qtdeParcelas + empresa.getSubvencaoParcela().size();
            }
        }
        return qtdeParcelas;
    }

    public void concluirEfetivacao() {
        boolean terminou = true;
        for (Future future : futuresEfetivacao) {
            if (!future.isDone()) {
                terminou = false;
            }
        }

        if (terminou) {
            selecionado = subvencaoProcessoFacade.salva(selecionado);
            FacesUtil.executaJavaScript("terminarEfetivacao()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addOperacaoRealizada("Efetivação concluída com sucesso!");
        }
    }

    public void efetivacaoIndividual(SubvencaoEmpresas empresa) {
        try {
            if (subvencaoProcessoFacade.isSubvencaoParcelaParcelamento(empresa.getUltimaSubvencaoParcela())) {
                sub = empresa;
                FacesUtil.executaJavaScript("dlgResidualParcelamento.show()");
                return;
            }
            validarDebitoResidualEmpresa(empresa);
            salvarSubvencaoEContinuarEditando();
            empresa.setSubvencaoProcesso(selecionado);
            verificarEmpresaEfetivacao(empresa);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
            FacesUtil.addInfo("Efetivação concluída com sucesso!", "");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao realizar a efetivação individual {}", ex);
        }
    }

    private void verificarEmpresaEfetivacao(SubvencaoEmpresas empresa) throws Exception {
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            if (empresas.getCadastroEconomico().equals(empresa.getCadastroEconomico())) {
                empresas.setSituacao(SituacaoSubvencao.EFETIVADO);
                subvencaoProcessoFacade.pagarParcelaAndCancelarDamIndividual(selecionado, empresas);
            }
        }
    }

    private void salvarSubvencaoEContinuarEditando() {
        Web.limpaNavegacao();
        if (selecionado.getId() == null) {
            selecionado.setNumeroDoProcesso(Long.parseLong(gerarNumeroProcesso(selecionado.getDataLancamento())));
        }
        selecionado = subvencaoProcessoFacade.salva(selecionado);
    }

    public void estornar() {
        try {
            for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
                if (empresas.getSituacao() != null) {
                    if (SituacaoSubvencao.EFETIVADO.equals(empresas.getSituacao())) {
                        for (SubvencaoParcela subvencaoParcela : empresas.getSubvencaoParcela()) {
                            subvencaoProcessoFacade.estornarParcelas(subvencaoParcela.getParcelaValorDivida(), selecionado);
                            recuperarParcelasGeradas(selecionado);
                            estornarParcelasGeradasValorResidual();
                        }
                    }
                    empresas.setSituacao(SituacaoSubvencao.EM_ABERTO);
                    subvencaoProcessoFacade.salvarSubvencaoEmpresa(empresas);
                }
            }
            FacesUtil.addOperacaoRealizada("Estorno concluído com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void estornarParcelasGeradasValorResidual() {
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                ParcelaValorDivida pvd = subvencaoProcessoFacade.buscarParcelaValorDivida(parcela.getId());
                if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                    subvencaoProcessoFacade.estornarParcelasGeradasResidual(pvd);
                }
            }
        }
    }


    private void verificarSeTemDebitoParaSubvencao() {
        ValidacaoException ve = new ValidacaoException();
        boolean todasSemParcela = true;
        for (SubvencaoEmpresas empresa : selecionado.getSubvencaoEmpresas()) {
            if (!empresa.getSubvencaoParcela().isEmpty()) {
                todasSemParcela = false;
            }
        }
        if (todasSemParcela) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Todas as empresas estão sem parcelas adicionadas");
            throw ve;
        }
    }

    public void verificaParcelasAdicionadas() {
        List<Integer> indicesParaExcluir = new ArrayList<>();
        for (ParcelaSubvencao parcela : itemParcelaSubvencao) {
            for (SubvencaoParcela sp : sub.getSubvencaoParcela()) {
                if (parcela.getResultadoParcela().getIdParcela().equals(sp.getParcelaValorDivida().getId())) {
                    indicesParaExcluir.add(itemParcelaSubvencao.indexOf(parcela));
                }
            }
        }
        removeParcelasJaAdicionas(indicesParaExcluir);
    }

    public void removeParcelasJaAdicionas(List<Integer> indices) {
        int qtdeExcluidos = 0;
        for (Integer index : indices) {
            itemParcelaSubvencao.remove(itemParcelaSubvencao.get(index - qtdeExcluidos));
            qtdeExcluidos = qtdeExcluidos + 1;
        }
    }

    public List<SelectItem> getMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public void inserirEmpresasSubvencao() {
        try {
            validarSubvencaoEmAberto();
            validarInformacoesDoProcesso();
            if (hasParametroCadastrado()) {
                buscarEmpresasCredorasVigentesNoParametro();
                verificarSeTodasEmpresasCredorasPossuemEmpresaDevedora();
                atribuirEmpresaDevedora();
                recuperarEmpresasOutorga();
                FacesUtil.atualizarComponente("tabelaCadastros");
                Collections.sort(selecionado.getSubvencaoEmpresas());
                selecionado.setBloqueiaDadosDoProcesso(true);
            } else {
                FacesUtil.addOperacaoNaoRealizada("O parâmetro de Outorga para o mês, exercício e tipo de passageiro informado devem ser configurados antes de realizar a Subvenção!");
            }
        } catch (ValidacaoException ve) {
            selecionado.getSubvencaoEmpresas().clear();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSubvencaoEmAberto() {
        ValidacaoException ve = new ValidacaoException();
        SubvencaoProcesso subvencaoProcesso = subvencaoProcessoFacade.buscarSubvencaoEmAberto();
        if (subvencaoProcesso != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A subvenção de número: " + subvencaoProcesso.getNumeroDoProcesso() + " ainda está " + subvencaoProcesso.getSituacao().getDescricao().toUpperCase() + ". Realize a efetivação para criar uma nova subvenção! ");
        }
        ve.lancarException();
    }

    private void verificarSeTodasEmpresasCredorasPossuemEmpresaDevedora() {
        ValidacaoException ve = new ValidacaoException();
        for (SubvencaoEmpresas empresa : selecionado.getSubvencaoEmpresas()) {
            List<EmpresaDevedoraSubvencao> empresaDevedoraSubvencao = subvencaoProcessoFacade.recuperarEmpresasDevedoras(empresa.getCadastroEconomico());
            if (empresaDevedoraSubvencao == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar cadastre uma empresa devedora para a empresa credora " + empresa.getCadastroEconomico().getCmcNomeCpfCnpj());
            }
        }
        ve.lancarException();
    }

    private boolean hasParametroCadastrado() {
        ParametrosOutorgaRBTrans parametrosOutorgaRBTrans = parametroOutorgaFacade.recuperarParametroSubvencao(selecionado.getExercicio());
        if (parametrosOutorgaRBTrans != null) {
            for (ParametroOutorgaSubvencao parametro : parametrosOutorgaRBTrans.getParametroOutorgaSubvencao()) {
                if (parametro.getDataInicial() != null && parametro.getDataFinal() != null) {
                    if ((DataUtil.isBetween(parametro.getDataInicial(), parametro.getDataFinal(), selecionado.getDataReferenciaParametro())) && parametro.getTipoPassageiro().equals(selecionado.getTipoPassageiro())) {
                        selecionado.setQtdeAlunosTransportados(parametro.getQtdeAlunosTransportados());
                        selecionado.setValorPassagem(parametro.getValorPassagem());
                        selecionado.setPercentualSubvencao(parametro.getPercentualSubvencao());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void recuperarEmpresasOutorga() {
        for (SubvencaoEmpresas subvencaoEmpresas : selecionado.getSubvencaoEmpresas()) {
            CadastroEconomico cmc = subvencaoEmpresas.getCadastroEconomico();
            ContribuinteDebitoOutorga contribuinteDebitoOutorga = contribuinteDebitoOutorgaFacade.recuperarLacancamentoPorCMC(cmc, selecionado.getExercicio(), selecionado.getMes());
            if (contribuinteDebitoOutorga != null) {
                for (OutorgaIPO outorgaIPO : contribuinteDebitoOutorga.getListaIpo()) {
                    if (outorgaIPO.getDataInicial() != null && outorgaIPO.getDataFinal() != null) {
                        if (DataUtil.isBetween(outorgaIPO.getDataInicial(), outorgaIPO.getDataFinal(), selecionado.getDataReferenciaParametro()) &&
                            selecionado.getTipoPassageiro().equals(outorgaIPO.getTipoPassageiro())) {
                            subvencaoEmpresas.setOutorgaIPO(outorgaIPO);
                            subvencaoEmpresas.setIpo(outorgaIPO.getIpo());
                            subvencaoEmpresas.setNumeroDeAlunos(outorgaIPO.getAlunos());
                            subvencaoEmpresas.setAlunosTransportados(outorgaIPO.getAlunosTransportados());
                            calcular(subvencaoEmpresas);
                        }
                    }
                }
            }
        }
    }

    public Boolean bloqueiaDadosDoProcesso() {
        if (selecionado.getBloqueiaDadosDoProcesso()) {
            return true;
        }
        if (!selecionado.getSubvencaoEmpresas().isEmpty()) {
            return true;
        }
        return false;
    }

    private void validarInformacoesDoProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoPassageiro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Passageiro é Obrigatório.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é Obrigatório.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Exercício de Referência é Obrigatório.");
        }
        if (selecionado.getDataReferenciaParametro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Data de Referência do Parâmetro é Obrigatório.");
        } else {
            if (selecionado.getMes() != null && selecionado.getExercicio() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(selecionado.getDataReferenciaParametro());
                if (cal.get(Calendar.YEAR) != selecionado.getExercicio().getAno() || cal.get(Calendar.MONTH) != selecionado.getMes().getNumeroMesIniciandoEmZero()) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Data de Referência do Parâmetro deve pertencer ao mês e ano do processo!");
                }
            }
        }
        if (selecionado.getMes() != null) {
            SubvencaoProcesso subvencaoProcesso = subvencaoProcessoFacade.buscarUltimaSubvencaoLancadaDeOutroMes(selecionado.getMes());
            if (subvencaoProcesso != null) {
                subvencaoProcesso = subvencaoProcessoFacade.recuperar(subvencaoProcesso.getId());
                for (SubvencaoEmpresas empresas : subvencaoProcesso.getSubvencaoEmpresas()) {
                    if (empresas.getSaldoSubvencionar().compareTo(BigDecimal.ZERO) > 0 && subvencaoProcesso.getSituacao().equals(SituacaoSubvencao.EM_ABERTO)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A empresa " + empresas.getCadastroEconomico().getCmcNomeCpfCnpj() + " ainda possui saldo a ser subvencionado " +
                            " na subvenção de número <b>" + subvencaoProcesso.getNumeroDoProcesso() + "</b>, é necessário realizar a baixa do saldo para o mês de <b>"
                            + subvencaoProcesso.getMes().getDescricao() + "</b> antes de continuar.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    private void validarSeOProcessoTemEmpresasAdicionadas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSubvencaoEmpresas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo de subvenção não possui empresas participantes.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void pesquisaDebito(CadastroEconomico cadastroEconomico, SubvencaoEmpresas sub) {
        Web.poeNaSessao("cadastroEconomico", cadastroEconomico);
        Web.poeNaSessao("listaCadastroEconomicoSubvencao", selecionado.getSubvencaoEmpresas());
        Web.poeNaSessao("subvencaoEmpresa", sub);
        Web.poeNaSessao("selecionado", selecionado);
        Web.poeNaSessao("subvencaoReincidenteNoMes", subvencaoReincidenteNoMes);
        Web.poeNaSessao("saldo", sub.getSaldoSubvencionar());
        Web.navegacao(getUrlAtual(), "/subvencao-processo/debitos/");
    }

    public List<SubvencaoEmpresas> getSubvencaoEmpresas() {
        return subvencaoEmpresas;
    }

    public void setSubvencaoEmpresas(List<SubvencaoEmpresas> subvencaoEmpresas) {
        this.subvencaoEmpresas = subvencaoEmpresas;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public BigDecimal getSaldoDaEmpresaRecuperado() {
        return saldoDaEmpresaRecuperado;
    }

    public void setSaldoDaEmpresaRecuperado(BigDecimal saldoDaEmpresaRecuperado) {
        this.saldoDaEmpresaRecuperado = saldoDaEmpresaRecuperado;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }


    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<ResultadoParcela> getDebitoSelecionados() {
        return debitoSelecionados;
    }

    public void setDebitoSelecionados(List<ResultadoParcela> debitoSelecionados) {
        this.debitoSelecionados = debitoSelecionados;
    }

    public BigDecimal getValorTotalSubvencionar() {
        return valorTotalSubvencionar;
    }

    public void setValorTotalSubvencionar(BigDecimal valorTotalSubvencionar) {
        this.valorTotalSubvencionar = valorTotalSubvencionar;
    }

    public BigDecimal getSaldoSubvencao() {
        return saldoSubvencao;
    }

    public void setSaldoSubvencao(BigDecimal saldoSubvencao) {
        this.saldoSubvencao = saldoSubvencao;
    }

    public List<SubvencaoParcela> getSubvencaoParcelasLista() {
        return subvencaoParcelasLista;
    }

    public void setSubvencaoParcelasLista(List<SubvencaoParcela> subvencaoParcelasLista) {
        this.subvencaoParcelasLista = subvencaoParcelasLista;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Boolean getBloqueiaCalculoDasDividas() {
        return bloqueiaCalculoDasDividas;
    }

    public void setBloqueiaCalculoDasDividas(Boolean bloqueiaCalculoDasDividas) {
        this.bloqueiaCalculoDasDividas = bloqueiaCalculoDasDividas;
    }

    public SubvencaoEmpresas getSub() {
        return sub;
    }

    public void setSub(SubvencaoEmpresas sub) {
        this.sub = sub;
    }

    public Boolean getBloqueiaAdicionarParcela() {
        return bloqueiaAdicionarParcela;
    }

    public void setBloqueiaAdicionarParcela(Boolean bloqueiaAdicionarParcela) {
        this.bloqueiaAdicionarParcela = bloqueiaAdicionarParcela;
    }

    public void calcular(SubvencaoEmpresas cadastro) {
        if (cadastro.getAlunosTransportados() != null) {
            BloqueioOutorga bloqueioOutorga = bloqueioOutorgaFacade.buscarBloqueioFinalizadoParaParametroSubVencao(cadastro.getCadastroEconomico(), getParametroSubVencaoAtual());
            if (bloqueioOutorga != null) {
                if (subvencaoProcessoFacade.buscarProcessosParaBloqueio(bloqueioOutorga).isEmpty()) {
                    cadastro.setBloqueioOutorga(bloqueioOutorga);
                }
            }
            if (subvencaoProcessoFacade.hasLancamentoValidoParaACompetencia(cadastro.getOutorgaIPO())) {
                SubvencaoEmpresas subvencaoEmpresa = subvencaoProcessoFacade.buscarEmpresaDaUltimaSubvencaoLancadaPorCMC(cadastro.getOutorgaIPO(), cadastro.getCadastroEconomico());
                cadastro.setValorSubvencao(subvencaoEmpresa.getSaldoSubvencionar());
                cadastro.setSaldoSubvencionar(subvencaoEmpresa.getSaldoSubvencionar());
                subvencaoReincidenteNoMes = true;
                if (cadastro.getBloqueioOutorga() != null && cadastro.getSaldoSubvencionar().compareTo(BigDecimal.ZERO) > 0) {
                    cadastro.setSaldoSubvencionar(cadastro.getSaldoSubvencionar().subtract(cadastro.getBloqueioOutorga().totalBloqueado()));
                }
            } else {
                cadastro.setSaldoSubvencionar(cadastro.getAlunosTransportados().multiply(selecionado.getValorPassagem()));
                cadastro.setValorSubvencao(cadastro.getAlunosTransportados().multiply(selecionado.getValorPassagem()));
                if (cadastro.getBloqueioOutorga() != null && cadastro.getSaldoSubvencionar().compareTo(BigDecimal.ZERO) > 0) {
                    cadastro.setSaldoSubvencionar(cadastro.getSaldoSubvencionar().subtract(cadastro.getBloqueioOutorga().totalBloqueado()));
                }
            }
        } else {
            cadastro.setValorSubvencao(BigDecimal.ZERO);
        }
        cadastro.setValorParametro(cadastro.getAlunosTransportados().multiply(selecionado.getValorPassagem()));
    }

    private void subvencaoReincidente() {
        if (subvencaoProcessoFacade.existeLancamentoParaACompetencia(selecionado.getMes(), selecionado.getExercicio().getId(), selecionado.getTipoPassageiro())) {
            this.subvencaoReincidenteNoMes = true;
        }
    }

    public Boolean podePesquisarDebitos(SubvencaoEmpresas subvencaoEmpresas) {
        boolean retorno = false;
        if (subvencaoEmpresas.getAlunosTransportados() == null || subvencaoEmpresas.getAlunosTransportados().equals(BigDecimal.ZERO)) {
            retorno = true;
        }
        if (subvencaoEmpresas.getSaldoSubvencionar().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = true;
        }
        if (subvencaoEmpresas.getSituacao() != null && subvencaoEmpresas.getSituacao().equals(SituacaoSubvencao.EFETIVADO)) {
            retorno = true;
        }
        return retorno;
    }

    public void voltarParaEdita() {
        Web.poeNaSessao("debitoSelecionados", debitoSelecionados);
        Web.poeNaSessao("selecionado", selecionado);
        Web.poeNaSessao("sub", sub);
        Web.poeNaSessao("subvencaoReincidenteNoMes", subvencaoReincidenteNoMes);
        Web.navegacao("/subvencao-processo/debitos/", "/subvencao-processo/novo/", selecionado);
    }


    public void removerParcela(SubvencaoParcela parcela, SubvencaoEmpresas empresa) {
        try {
            validarExclusaoDeParcela(parcela);
            empresa.getSubvencaoParcela().remove(parcela);
            BigDecimal valorSubvencionado = BigDecimal.ZERO;
            for (SubvencaoParcela subvencaoParcela : empresa.getSubvencaoParcela()) {
                valorSubvencionado = valorSubvencionado.add(getValorSubvencionado(subvencaoParcela));
            }
            BigDecimal valorBloqueado = empresa.getBloqueioOutorga() == null ? BigDecimal.ZERO : empresa.getBloqueioOutorga().totalBloqueado();
            empresa.setValorSubvencionado(valorSubvencionado);
            empresa.setSaldoSubvencionar(empresa.getValorSubvencao().subtract(valorBloqueado).subtract(empresa.getValorSubvencionado()));
            empresa.setTemDebito(true);
            saldo = empresa.getSaldoSubvencionar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarExclusaoDeParcela(SubvencaoParcela parcela) {
        ValidacaoException ve = new ValidacaoException();
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            for (SubvencaoParcela subvencaoParcela : empresas.getSubvencaoParcela()) {
                if (subvencaoParcela.getParcelaValorDivida().equals(parcela.getParcelaValorDivida()) && parcela.getDataLancamento().before(subvencaoParcela.getDataLancamento())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir essa parcela, ela se encontra adicionada posteriormente para a empresa "
                        + empresas.getCadastroEconomico().getDescricao() + " para realizar a exclusão deve ser removido primeiramente dessa empresa respectiva.");
                }
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }


    public void addParcela(ParcelaSubvencao parcelaSubvencao) {
        BigDecimal valorBloqueado = sub.getBloqueioOutorga() == null ? BigDecimal.ZERO : sub.getBloqueioOutorga().totalBloqueado();
        valorTotalSubvencionar = sub.getValorSubvencionado().add(parcelaSubvencao.getResultadoParcela().getValorTotal());
        sub.setValorSubvencionado(valorTotalSubvencionar);
        sub.setSaldoSubvencionar(sub.getValorSubvencao().subtract(valorBloqueado).subtract(sub.getValorSubvencionado()));
        parcelaSubvencao.getResultadoParcela().setTotal(parcelaSubvencao.getResultadoParcela().getValorTotal());
        saldoSubvencao = sub.getSaldoSubvencionar();
        defineValorSaldo();

        if (saldoSubvencao.compareTo(BigDecimal.ZERO) > 0) {
            saldo = saldoSubvencao.setScale(2, RoundingMode.HALF_UP);
        }
        verificaSeExisteSaldoParaSubvencionar(parcelaSubvencao.getResultadoParcela(), saldo);
        itemParcelaSubvencao.remove(parcelaSubvencao);
        atribuirValoresDaPesquisaDeDebitos(parcelaSubvencao.getResultadoParcela(), parcelaSubvencao.getNaturezaDividaSubvencao());
    }

    private void defineValorSaldo() {
        if (saldo == null) {
            saldo = saldoSubvencao.setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void verificaSeExisteSaldoParaSubvencionar(ResultadoParcela resultadoParcela, BigDecimal saldo) {
        BigDecimal valorBloqueio = sub.getBloqueioOutorga() == null ? BigDecimal.ZERO : sub.getBloqueioOutorga().totalBloqueado();
        if (sub.getValorSubvencao().subtract(valorBloqueio).compareTo(valorTotalSubvencionar) < 0) {

            //para quando a primeira parcela adiciona possui valor maior que o saldo da subvenção
            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                saldo = sub.getValorSubvencao().subtract(valorBloqueio);
            }

            BigDecimal percentualAbate = percentualAbate(resultadoParcela.getValorTotal(), saldo.abs());

            BigDecimal totalMenosHonorarios = resultadoParcela.getValorTotal()
                .subtract(resultadoParcela.getValorHonorarios());

            BigDecimal valorMultiplicadoPorPecentual = totalMenosHonorarios
                .multiply(percentualAbate);
            valorMultiplicadoPorPecentual.setScale(2, RoundingMode.HALF_UP);

            BigDecimal valorFinal = valorMultiplicadoPorPecentual
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            if (resultadoParcela.getValorHonorarios().compareTo(BigDecimal.ZERO) == 0) {
                if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                    resultadoParcela.setTotal(resultadoParcela.getValorTotal().subtract(saldo.abs()));
                } else {
                    resultadoParcela.setTotal(saldo);
                }
            } else {
                resultadoParcela.setTotal(valorFinal);
            }


            if (saldoSubvencao.compareTo(BigDecimal.ZERO) < 0) {
                saldoSubvencao = BigDecimal.ZERO;
            }
            sub.setValorSubvencionado(sub.getValorSubvencao().subtract(valorBloqueio));
            sub.setValorQueSobrouDaParcela(valorTotalSubvencionar.subtract(sub.getValorSubvencao().subtract(valorBloqueio)));
            mensagemParcelaPassouDoSaldo();
        } else if (sub.getValorSubvencao().subtract(valorBloqueio).compareTo(valorTotalSubvencionar) <= 0) {
            mensagemParcelaPassouDoSaldo();
        }
    }


    public void bloqueiaAdicionarParcela() {
        bloqueiaAdicionarParcela = true;
    }

    public void mensagemParcelaPassouDoSaldo() {
        if (isOperacaoEditar()) {
            FacesUtil.executaJavaScript("subvencionarDivida.show()");
        }
    }

    @Override
    public void cancelar() {
        Web.limpaNavegacao();
        redireciona();
    }

    private void atribuirValoresDaPesquisaDeDebitos(ResultadoParcela resultado, NaturezaDividaSubvencao naturezaDividaSubvencao) {
        SubvencaoParcela subvencaoParcelas = new SubvencaoParcela();
        subvencaoParcelas.setDataLancamento(new Date());
        subvencaoParcelas.setEmissao(resultado.getEmissao());
        subvencaoParcelas.setHonorarioAtualCalculo(recuperarPercentualHonorarios(resultado));
        subvencaoParcelas.setTotal(resultado.getValorTotal());
        subvencaoParcelas.setValorHonorarios(resultado.getValorHonorarios());
        subvencaoParcelas.setImposto(resultado.getValorImposto());
        subvencaoParcelas.setTaxa(resultado.getValorTaxa());
        subvencaoParcelas.setDesconto(resultado.getValorDesconto());
        subvencaoParcelas.setJuros(resultado.getValorJuros());
        subvencaoParcelas.setMulta(resultado.getValorMulta());
        subvencaoParcelas.setCorrecao(resultado.getValorCorrecao());
        subvencaoParcelas.setSubvencaoEmpresas(sub);
        subvencaoParcelas.setParcelaValorDivida(subvencaoProcessoFacade.buscarParcelaValorDivida(resultado.getIdParcela()));
        subvencaoParcelas.setCadastro(resultado.getCadastro());
        subvencaoParcelas.setTipoCadastro(resultado.getTipoCadastro());
        subvencaoParcelas.setTipoDeDebito(resultado.getTipoDeDebito());
        subvencaoParcelas.setDivida(resultado.getDivida());
        subvencaoParcelas.setExercicio(resultado.getExercicio());
        subvencaoParcelas.setParcela(resultado.getParcela());
        subvencaoParcelas.setSd(resultado.getSd());
        subvencaoParcelas.setVencimento(resultado.getVencimento());
        subvencaoParcelas.setValorOriginal(resultado.getValorOriginal());
        subvencaoParcelas.setSituacao(resultado.getSituacaoDescricaoEnum());
        subvencaoParcelas.setReferencia(resultado.getReferencia());
        subvencaoParcelas.setValorSubvencionado(resultado.getTotal());
        subvencaoParcelas.setNaturezaDividaSubvencao(naturezaDividaSubvencao);

        if (sub.getSaldoSubvencionar().compareTo(BigDecimal.ZERO) < 0) {
            subvencaoParcelas.setValorResidual((resultado.getValorTotal().subtract(resultado.getValorHonorarios()).subtract(subvencaoParcelas.getValorSubvencionado())));
            sub.setSaldoSubvencionar(BigDecimal.ZERO);
        } else {
            subvencaoParcelas.setValorResidual(BigDecimal.ZERO);
        }
        if (subvencaoParcelas.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            subvencaoParcelas.setValorHonorarios(getValorHonorariosDoTotalSubvencionado(subvencaoParcelas));
        }
        if (sub.getValorQueSobrouDaParcela().compareTo(BigDecimal.ZERO) > 0) {
            subvencaoParcelas.setRestoDoValorSubvencionado(sub.getValorQueSobrouDaParcela());
            sub.setValorQueSobrouDaParcela(BigDecimal.ZERO);
        }
        sub.getSubvencaoParcela().add(subvencaoParcelas);
    }

    private BigDecimal percentualAbate(BigDecimal valorParcela, BigDecimal saldoSubvencao) {
        BigDecimal saldoXcem = (saldoSubvencao.multiply(new BigDecimal(100)));
        return saldoXcem.divide(valorParcela, 8, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorHonorarios(SubvencaoParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorParcela = parcela.getImposto().add(parcela.getTaxa()).add(parcela.getJuros()).add(parcela.getMulta()).add(parcela.getCorrecao());
            BigDecimal honorarios = valorParcela.multiply(parcela.getHonorarioAtualCalculo());
            return honorarios.setScale(2, RoundingMode.HALF_UP);
        } else
            return BigDecimal.ZERO;
    }

    public BigDecimal getValorHonorariosDoTotalSubvencionado(SubvencaoParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            return parcela.getValorSubvencionado().multiply(parcela.getHonorarioAtualCalculo());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorSubvencionado(SubvencaoParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            return parcela.getValorSubvencionado().add(getValorHonorariosDoTotalSubvencionado(parcela));
        } else {
            return parcela.getValorSubvencionado();
        }
    }

    public BigDecimal getValorResidual(SubvencaoParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal honorariosDoValorResidual = parcela.getValorResidual().multiply(parcela.getHonorarioAtualCalculo());
            return parcela.getValorResidual().add(honorariosDoValorResidual);
        } else {
            return parcela.getValorResidual();
        }
    }

    public void estorno() {
        FacesUtil.addWarn("Não foi possível estornar", "Funcionalidade em desenvolvimento!");
    }

    public BigDecimal valorASerSubvencionado() {
        if (sub != null) {
            return sub.getValorSubvencao();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorSubvencionarVindoDoParametro() {
        if (sub != null) {
            return sub.getValorParametro();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal valorDoSaldo() {
        if (sub != null) {
            return sub.getSaldoSubvencionar();
        }
        return BigDecimal.ZERO;
    }


    public Boolean parcelaJaAdicionadaNoProcessoDeSubvencao(ResultadoParcela pvd) {
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            for (SubvencaoParcela parcelas : empresas.getSubvencaoParcela()) {
                if (parcelas.getParcelaValorDivida().getId().equals(pvd.getIdParcela()) && empresas.getSituacao().equals(SituacaoSubvencao.EM_ABERTO)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<SubvencaoParcela> listaDeParcelasJaAdicionasNaSubvencao() {
        if (sub != null) {
            return sub.getSubvencaoParcela();
        }
        return Lists.newArrayList();
    }


    public boolean renderizaTabelaDeParcelasJahAdicionadas() {
        if (sub != null && sub.getSubvencaoParcela() != null && sub.getSubvencaoParcela().isEmpty()) {
            return false;
        }
        return true;
    }

    public void gerarCertidaoCompensacao(SubvencaoEmpresas empresa) throws JRException, IOException {
        try {
            SubvencaoParametro subvencaoParametro = subvencaoParametroFacade.retornarParametro();
            if (subvencaoParametro != null && subvencaoParametro.getTipoDoctoOficialTermo() != null) {

                DocumentoOficial documentoOficial = documentoOficialFacade.gerarDocumentoSubvencao(empresa, empresa.getDocumentoOficial(), empresa.getCadastroEconomico(), subvencaoParametro.getTipoDoctoOficialTermo(), getSistemaControlador());
                if (empresa.getDocumentoOficial() == null) {
                    empresa.setDocumentoOficial(documentoOficial);
                    getFacede().salvar(empresa);
                }
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi localização o parâmetro do documento oficial para a certidão!");
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro", "Erro ao emitir a certidão: " + e.getMessage());
            logger.error("Erro ao emitir a certidão: {}", e);
        }
    }

    public Boolean podeImprimirCertidaoAndDam(SubvencaoEmpresas subvencaoEmpresa) {
        return !(SituacaoSubvencao.EFETIVADO.equals(selecionado.getSituacao()) && !subvencaoEmpresa.getSubvencaoParcela().isEmpty());
    }

    public Boolean podeEfetivarDebitosEmpresa(SubvencaoEmpresas subvencaoEmpresa) {
        return (subvencaoEmpresa.getSituacao() != null && subvencaoEmpresa.getSituacao().equals(SituacaoSubvencao.EM_ABERTO))
            && !subvencaoEmpresa.getSubvencaoParcela().isEmpty();

    }

    public Boolean possuiEmpresaEfetivadaComProcessoEmAberto() {
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            if (empresas.getSituacao().equals(SituacaoSubvencao.EFETIVADO) && !selecionado.getSituacao().equals(SituacaoSubvencao.EFETIVADO)) {
                return true;
            }
        }
        return false;
    }

    public boolean podeEditarOuExcluirOProcesso() {
        for (SubvencaoEmpresas empresas : selecionado.getSubvencaoEmpresas()) {
            if (empresas.getSituacao().equals(SituacaoSubvencao.EFETIVADO) || selecionado.getSituacao().equals(SituacaoSubvencao.EFETIVADO)) {
                return false;
            }
        }
        return true;
    }

    public boolean getPrimeiraSubvencaoMes() {
        SubvencaoProcesso primeiraSubvencaoMes = subvencaoProcessoFacade.primeiraSubvencaoDoMes(selecionado.getExercicio(), selecionado.getMes());
        if (primeiraSubvencaoMes != null) {
            if (primeiraSubvencaoMes.getNumeroDoProcesso().equals(selecionado.getNumeroDoProcesso())) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal recuperarPercentualHonorarios(ResultadoParcela resultadoParcela) {
        ConfiguracaoAcrescimos configuracaoAcrescimo = getConfiguracaoAcrescimo(resultadoParcela.getIdConfiguracaoAcrescimo());
        BigDecimal honorarios = configuracaoAcrescimo.getHonorariosAdvocaticiosNaData(resultadoParcela.getEmissao());
        return honorarios.divide(CEM, 2, BigDecimal.ROUND_HALF_UP);
    }

    private ConfiguracaoAcrescimos getConfiguracaoAcrescimo(Long idConfiguracao) {
        return configuracaoAcrescimosFacade.recuperar(idConfiguracao);
    }

    public List<SelectItem> getTipoPassageiro() {
        return Util.getListSelectItem(Lists.newArrayList(TipoPassageiro.values()));
    }

    public void verificarQualDamGerar(SubvencaoEmpresas empresas) {
        try {
            validarImpressaoDAM(empresas);
            if (parcelasParaImpressaoDAM.get(empresas).size() > 1) {
                empresaParaDamNoDialog = empresas;
                FacesUtil.atualizarComponente("idConfirmaDam");
                FacesUtil.executaJavaScript("confirmaDam.show()");
            } else {
                gerarDAM(empresas);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void gerarDAM(SubvencaoEmpresas empresas) throws Exception {
        if (empresas == null) {
            empresas = empresaParaDamNoDialog;
        }
        List<DAM> dams = subvencaoProcessoFacade.gerarDAMIndividualSubvencao(parcelasParaImpressaoDAM.get(empresas), selecionado.getExercicio());
        imprimirDam(dams, parcelasParaImpressaoDAM.get(empresas));
    }

    public void gerarDAMAgrupado() throws Exception {
        DAM dam = subvencaoProcessoFacade.gerarDAMComposto(parcelasParaImpressaoDAM.get(empresaParaDamNoDialog), selecionado.getExercicio(), selecionado);
        imprimirDamAgrupado(dam, parcelasParaImpressaoDAM.get(empresaParaDamNoDialog));
    }

    private void validarImpressaoDAM(SubvencaoEmpresas empresas) {
        ValidacaoException ve = new ValidacaoException();
        if (parcelasParaImpressaoDAM.get(empresas) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe parcelas selecionadas para a impressão do DAM.");
        }
        ve.lancarException();
    }

    private void imprimirDam(List<DAM> dams, List<SubvencaoParcela> subvencaoParcela) {
        HashMap parameters = new HashMap();
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        parameters.put("SUBVENCAO_PARCELA", obterIdsParcelas(subvencaoParcela));
        imprimeDAM.setGeraNoDialog(true);
        try {
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            FacesUtil.addError("Não foi possível continuar!", ex.getMessage());
        }
    }

    private void imprimirDamAgrupado(DAM dam, List<SubvencaoParcela> subvencaoParcelas) {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);

            List<ResultadoParcela> parcelasDoDamAgrupado = Lists.newArrayList();
            for (SubvencaoParcela subvencaoParcela : subvencaoParcelas) {
                ResultadoParcela parcela = new ResultadoParcela();

                BigDecimal valorSubvencionado;
                if (subvencaoParcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && subvencaoParcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
                    valorSubvencionado = subvencaoParcela.getValorSubvencionado().add(subvencaoParcela.getValorHonorarios());
                } else {
                    valorSubvencionado = subvencaoParcela.getValorSubvencionado();
                }
                parcela.setValorJuros(subvencaoParcela.getJuros());
                parcela.setValorMulta(subvencaoParcela.getMulta());
                parcela.setValorHonorarios(subvencaoParcela.getValorHonorarios());
                parcela.setValorCorrecao(subvencaoParcela.getCorrecao());
                parcela.setValorDesconto(BigDecimal.ZERO);
                parcela.setValorImposto(subvencaoParcela.getImposto());
                parcela.setValorTaxa(subvencaoParcela.getTaxa());

                parcela.setSituacao(SituacaoParcela.PAGO_SUBVENCAO.name());
                parcela.setVencimento(subvencaoParcela.getVencimento());
                parcela.setEmissao(subvencaoParcela.getDataLancamento());
                parcela.setTipoCadastro(TipoCadastroTributario.ECONOMICO.name());
                parcela.setTipoCalculo(Calculo.TipoCalculo.SUBVENCAO.name());
                parcela.setParcela(subvencaoParcela.getParcela());
                parcela.setExercicio(subvencaoParcela.getExercicio());
                parcela.setSd(subvencaoParcela.getSd());
                parcela.setBloqueiaImpressao(false);
                parcela.setPermissaoEmissaoDAM(PermissaoEmissaoDAM.HABILITA.name());
                parcela.setCadastro(subvencaoParcela.getCadastro());
                parcela.setReferencia(subvencaoParcela.getReferencia());
                parcela.setDivida(subvencaoParcela.getDivida());

                if (subvencaoParcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal valorTotal = parcela.getValorTotal();

                    if (subvencaoParcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
                        if (valorSubvencionado.compareTo(parcela.getValorJuros()) > 0) {
                            valorSubvencionado = valorSubvencionado.subtract(parcela.getValorJuros());
                        } else {
                            parcela.setValorJuros(valorSubvencionado);
                            valorSubvencionado = BigDecimal.ZERO;
                        }
                        if (valorSubvencionado.compareTo(parcela.getValorMulta()) > 0) {
                            valorSubvencionado = valorSubvencionado.subtract(parcela.getValorMulta());
                        } else {
                            parcela.setValorMulta(valorSubvencionado);
                            valorSubvencionado = BigDecimal.ZERO;
                        }
                        if (valorSubvencionado.compareTo(parcela.getValorCorrecao()) > 0) {
                            valorSubvencionado = valorSubvencionado.subtract(parcela.getValorCorrecao());
                        } else {
                            parcela.setValorCorrecao(valorSubvencionado);
                            valorSubvencionado = BigDecimal.ZERO;
                        }
                        if (valorSubvencionado.compareTo(parcela.getValorHonorarios()) > 0) {
                            valorSubvencionado = valorSubvencionado.subtract(parcela.getValorHonorarios());
                        } else {
                            parcela.setValorHonorarios(valorSubvencionado);
                            valorSubvencionado = BigDecimal.ZERO;
                        }
                        if (valorSubvencionado.compareTo(parcela.getValorImposto()) > 0) {
                            valorSubvencionado = valorSubvencionado.subtract(parcela.getValorImposto());
                        } else {
                            parcela.setValorImposto(valorSubvencionado);
                            valorSubvencionado = BigDecimal.ZERO;
                        }
                        if (valorSubvencionado.compareTo(parcela.getValorTaxa()) > 0) {
                        } else {
                            parcela.setValorTaxa(valorSubvencionado);
                        }
                        parcela.setValorDesconto(BigDecimal.ZERO);
                    }
                    parcela.setSituacao(SituacaoParcela.EM_ABERTO.name());
                }
                parcelasDoDamAgrupado.add(parcela);
            }

            imprimeDAM.imprimirDamCompostoViaApi(dam);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static String obterIdsParcelas(List<SubvencaoParcela> parcelas) {
        String ids = "";
        for (SubvencaoParcela parcela : parcelas) {
            ids += parcela.getId() + ",";
        }
        return ids.substring(0, ids.length() - 1);
    }


    public void selecionarSubvencaoParcela(SubvencaoParcela subvencaoParcela, SubvencaoEmpresas subvencaoEmpresas) {

        if (parcelasParaImpressaoDAM.get(subvencaoEmpresas) != null && parcelasParaImpressaoDAM.get(subvencaoEmpresas).contains(subvencaoParcela)) {
            parcelasParaImpressaoDAM.get(subvencaoEmpresas).remove(subvencaoParcela);
        } else {
            if (parcelasParaImpressaoDAM.get(subvencaoEmpresas) == null) {
                parcelasParaImpressaoDAM.put(subvencaoEmpresas, Lists.<SubvencaoParcela>newArrayList());
            }
            parcelasParaImpressaoDAM.get(subvencaoEmpresas).add(subvencaoParcela);
        }
    }

    public void selecionarTodasParcelasEmpresa(SubvencaoEmpresas subvencaoEmpresas) {
        if (parcelasParaImpressaoDAM.get(subvencaoEmpresas) == null) {
            parcelasParaImpressaoDAM.put(subvencaoEmpresas, Lists.<SubvencaoParcela>newArrayList());
        }

        if (parcelasParaImpressaoDAM.get(subvencaoEmpresas).size() != subvencaoEmpresas.getSubvencaoParcela().size()) {
            parcelasParaImpressaoDAM.get(subvencaoEmpresas).clear();
            parcelasParaImpressaoDAM.get(subvencaoEmpresas).addAll(subvencaoEmpresas.getSubvencaoParcela());
        } else {
            parcelasParaImpressaoDAM.get(subvencaoEmpresas).clear();
        }
    }

    public boolean todasParcelasAdicionadas(SubvencaoEmpresas subvencaoEmpresas) {
        if (parcelasParaImpressaoDAM.get(subvencaoEmpresas) != null) {
            if (parcelasParaImpressaoDAM.get(subvencaoEmpresas) != null && parcelasParaImpressaoDAM.get(subvencaoEmpresas).size() == subvencaoEmpresas.getSubvencaoParcela().size()) {
                return true;
            }
        }
        return false;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public void corrigirSubvencao() {
        try {
            for (SubvencaoEmpresas subvencaoEmpresa : selecionado.getSubvencaoEmpresas()) {
                sub = subvencaoEmpresa;
                ajustarSubvencaoParcelas(subvencaoEmpresa);
            }
            selecionado.setSituacao(SituacaoSubvencao.EM_ABERTO);
            selecionado = subvencaoProcessoFacade.salva(selecionado);
            FacesUtil.addOperacaoRealizada("Correção efetuada com sucesso!");
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void realocarParcelasParcelamento() {
        try {
            for (SubvencaoEmpresas subvencaoEmpresa : selecionado.getSubvencaoEmpresas()) {
                ajustarSubvencaoParcelas(subvencaoEmpresa);
            }
            FacesUtil.addOperacaoRealizada("Parcelas realocadas com sucesso!");
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void ajustarSubvencaoParcelas(SubvencaoEmpresas subvencaoEmpresa) {
        sub = subvencaoEmpresa;
        sub.setValorSubvencionado(BigDecimal.ZERO);
        sub.setSaldoSubvencionar(sub.getValorSubvencao());
        saldoSubvencao = sub.getValorSubvencao();
        valorTotalSubvencionar = BigDecimal.ZERO;

        List<ParcelaSubvencao> parcelasSubvencao = recriarParcelasSubvencaoCorrecaoSubvencao(subvencaoEmpresa);
        sub.setSubvencaoParcela(Lists.newArrayList());
        for (ParcelaSubvencao parcelaSubvencao : parcelasSubvencao) {
            addParcela(parcelaSubvencao);
        }
        sub.setSituacao(SituacaoSubvencao.EM_ABERTO);
    }

    private List<ParcelaSubvencao> recriarParcelasSubvencaoCorrecaoSubvencao(SubvencaoEmpresas subvencaoEmpresa) {
        List<ParcelaSubvencao> parcelasSubvencao = Lists.newArrayList();
        for (SubvencaoParcela subvencaoParcela : subvencaoEmpresa.getSubvencaoParcela()) {
            ParcelaSubvencao parcelaSubvencao = new ParcelaSubvencao();
            parcelaSubvencao.setResultadoParcela(subvencaoProcessoFacade
                .recuperarResultadoParcela(subvencaoParcela.getParcelaValorDivida().getId()));
            parcelaSubvencao.setNaturezaDividaSubvencao(subvencaoParcela.getNaturezaDividaSubvencao());
            Divida divida = subvencaoProcessoFacade.getDividaFacade()
                .recuperar(parcelaSubvencao.getResultadoParcela().getIdDivida());
            parcelaSubvencao.setParcelamento(divida.getIsParcelamento());
            parcelasSubvencao.add(parcelaSubvencao);
        }
        ordenarParcelasCorrecaoSubvencao(parcelasSubvencao);
        return parcelasSubvencao;
    }

    private static void ordenarParcelasCorrecaoSubvencao(List<ParcelaSubvencao> parcelasSubvencao) {
        parcelasSubvencao.sort((o1, o2) -> {
            Integer tipoDebito1 = o1.getParcelamento() ? 0 : 1;
            Integer tipoDebito2 = o2.getParcelamento() ? 0 : 1;
            return tipoDebito1.compareTo(tipoDebito2);
        });
    }
}

