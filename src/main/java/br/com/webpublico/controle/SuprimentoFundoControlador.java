/*
 * Codigo gerado automaticamente em Mon Mar 19 14:45:35 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "suprimentoFundoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-suprimento-fundos", pattern = "/suprimento-fundos/novo/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundo/edita.xhtml"),
    @URLMapping(id = "editar-suprimento-fundos", pattern = "/suprimento-fundos/editar/#{suprimentoFundoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundo/edita.xhtml"),
    @URLMapping(id = "ver-suprimento-fundos", pattern = "/suprimento-fundos/ver/#{suprimentoFundoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundo/visualizar.xhtml"),
    @URLMapping(id = "listar-suprimento-fundos", pattern = "/suprimento-fundos/listar/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundo/lista.xhtml")
})
public class SuprimentoFundoControlador extends PrettyControlador<PropostaConcessaoDiaria> implements Serializable, CRUD {

    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;
    private DiariaPrestacaoConta diariaPrestacaoContaselecionada;
    private DesdobramentoPropostaConcessao desdobramento;
    private BigDecimal saldoFonteDespesaORC;

    public SuprimentoFundoControlador() {
        super(PropostaConcessaoDiaria.class);
    }

    public PropostaConcessaoDiariaFacade getFacade() {
        return propostaConcessaoDiariaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return propostaConcessaoDiariaFacade;
    }

    @URLAction(mappingId = "novo-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        selecionado.setTipoProposta(TipoProposta.SUPRIMENTO_FUNDO);
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente());
        desdobramento = null;
        saldoFonteDespesaORC = BigDecimal.ZERO;
    }

    @URLAction(mappingId = "ver-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
        recuperarSaldoFonteDespesaOrc();
    }

    public void recuperarEditarVer() {
        diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        if (selecionado.getAprovado() == null) {
            selecionado.setAprovado(false);
        }
    }

    public Boolean renderizarBotaoDeferir() {
        return selecionado.getId() != null && selecionado.getSituacao().equals(SituacaoDiaria.ABERTO);
    }

    public Boolean renderizarBotaoIndeferir() {
        return selecionado.getId() != null && selecionado.getSituacao().equals(SituacaoDiaria.DEFERIDO) && !propostaConcessaoDiariaFacade.verificarVinculoDiariaComEmpenho(selecionado);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarSaldoFonteDespesaOrc();
            propostaConcessaoDiariaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getDataLancamento());
            if (isOperacaoNovo()) {
                propostaConcessaoDiariaFacade.salvarNovo(selecionado);
                FacesUtil.executaJavaScript("geraSol.show()");
            } else {
                propostaConcessaoDiariaFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Elemento de Despesa deve ser informado. ");
        }
        if (selecionado.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Fonte de Recurso deve ser informado. ");
        }
        if (selecionado.getPessoaFisica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Agente Suprido deve ser informado. ");
        }
        if (selecionado.getClasseCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Classe deve ser informado. ");
        }
        if (selecionado.getContaCorrenteBanc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta Bancária deve ser informado. ");
        }
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        ve.lancarException();
        if (!selecionado.getDesdobramentos().isEmpty() && selecionado.getValor().compareTo(selecionado.getValorTotalDesdobramentos()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do Suprimento de Fundo deve ser igual ao valor total dos Detalhamentos.");
        }
        ve.lancarException();
    }

    private void validaDemonstrativoGastos() {
        ValidacaoException ve = new ValidacaoException();
        if (diariaPrestacaoContaselecionada.getNumeroNota() == null || diariaPrestacaoContaselecionada.getNumeroNota().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Número do Documento deve ser informado.");
        }
        if (diariaPrestacaoContaselecionada.getCpfCnpj() == null || diariaPrestacaoContaselecionada.getCpfCnpj().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo CPF/CNPJ deve ser informado.");
        }
        if (diariaPrestacaoContaselecionada.getValor() == null || diariaPrestacaoContaselecionada.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        ve.lancarException();
    }

    public void adicionaDiariaPrestacao() {
        try {
            validaDemonstrativoGastos();
            diariaPrestacaoContaselecionada.setPropostaConcessaoDiaria(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDiariaPrestacaoContas(), diariaPrestacaoContaselecionada);
            diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerDocumento(DiariaPrestacaoConta diaria) {
        selecionado.getDiariaPrestacaoContas().remove(diaria);
    }

    public void editarDocumento(DiariaPrestacaoConta diaria) {
        diariaPrestacaoContaselecionada = (DiariaPrestacaoConta) Util.clonarObjeto(diaria);
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return propostaConcessaoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.SUPRIMENTO_FUNDO);
    }

    public List<ContaCorrenteBancPessoa> completaContaCorrente(String parte) {
        return propostaConcessaoDiariaFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(selecionado.getPessoaFisica(), parte.trim());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return propostaConcessaoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte, selecionado.getPessoaFisica(), TipoClasseCredor.SUPRIMENTO_FUNDO);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.buscarAtosLegaisPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    private void validarSolicitacaoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor R$ esta zerado!");
        }
        if (selecionado.isAprovado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Solicitação de Empenho para este registro!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (SituacaoCadastralContabil situacao : SituacaoCadastralContabil.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public List<Empenho> getListaEmpenhos() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaEmpenhoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> getListaEmpenhoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaEmpenhoEstornoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> getListaLiquidacao() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaLiquidacaoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> getListaLiquidacaoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaLiquidacaoEstornoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> getListaPagamento() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaPagamentoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> getListaPagamentoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaPagamentoEstornoPorProposta(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoBaixaNormal() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.BAIXA, TipoLancamento.NORMAL, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoBaixaEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.BAIXA, TipoLancamento.ESTORNO, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoInscricaoNormal() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.INSCRICAO, TipoLancamento.NORMAL, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoInscricaoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.INSCRICAO, TipoLancamento.ESTORNO, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoApropriacaoNormal() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.APROPRIACAO, TipoLancamento.NORMAL, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> getListaDiariaContabilizacaoApropriacaoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.APROPRIACAO, TipoLancamento.ESTORNO, selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public BigDecimal getSomaNotasFiscais() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaPrestacaoConta diaria : selecionado.getDiariaPrestacaoContas()) {
            valor = valor.add(diaria.getValor());
        }
        return valor;
    }


    private BigDecimal somaValores(PropostaConcessaoDiaria p) {
        BigDecimal total = BigDecimal.ZERO;
        for (DiariaPrestacaoConta d : p.getDiariaPrestacaoContas()) {
            total = total.add(d.getValor());
        }
        return total;
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno pe : getListaPagamentoEstorno()) {
            estornos = estornos.add(pe.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Pagamento p : getListaPagamento()) {
            valor = valor.add(p.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (Liquidacao l : getListaLiquidacao()) {
            liq = liq.add(l.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (LiquidacaoEstorno le : getListaLiquidacaoEstorno()) {
            liq = liq.add(le.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Empenho e : getListaEmpenhos()) {
            valor = valor.add(e.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaEstornoEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (EmpenhoEstorno ee : getListaEmpenhoEstorno()) {
            valor = valor.add(ee.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaBaixasNormal() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoBaixaNormal()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaBaixasEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoBaixaEstorno()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaInscricoesNormal() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoInscricaoNormal()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaInscricoesEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoInscricaoEstorno()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaApropriacaoNormal() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoApropriacaoNormal()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaApropriacaoEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getListaDiariaContabilizacaoApropriacaoEstorno()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    private void validarSituacaoDiaria() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Ato Legal deve ser informado.");
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Responsável deve ser informado.");
        }
        ve.lancarException();
    }


    public void deferirDiariaComSolicitacaoEmpenho() {
        try {
            validarSolicitacaoEmpenho();
            validarSituacaoDiaria();
            selecionado.setSituacao(SituacaoDiaria.DEFERIDO);
            RequestContext.getCurrentInstance().execute("geraSol.hide()");
            propostaConcessaoDiariaFacade.geraSolicitacaoEmpenho(selecionado, selecionado.getFonteDespesaORC(), Boolean.TRUE);
            FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi deferida com reserva de dotação orçamentária no valor de " + Util.formataValor(selecionado.getValor()));
            cancelar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void indeferirDiaria() {
        try {
            propostaConcessaoDiariaFacade.indeferirDiaria(selecionado);
            FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi Indeferida com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void geraRelatorioPrestacaoContas(PropostaConcessaoDiaria proposta) throws JRException, IOException {
        String nomeArquivo = "RelatorioPrestacaoContas.jasper";
        HashMap parameters = new HashMap();
        AbstractReport abr = AbstractReport.getAbstractReport();
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("IMAGEM", abr.getCaminhoImagem());
        parameters.put("ID_PROPOSTA", proposta.getId());
        parameters.put("USER", sistemaFacade.getUsuarioCorrente().getNome());
        abr.gerarRelatorio(nomeArquivo, parameters);
    }

    public void geraRelatorioSuprimentoFundo(PropostaConcessaoDiaria proposta) throws JRException, IOException {
        String nomeArquivo = "SuprimentoDeFundos.jasper";
        HashMap parameters = new HashMap();
        AbstractReport abr = AbstractReport.getAbstractReport();
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("IMAGEM", abr.getCaminhoImagem());
        parameters.put("ID_PROPOSTA", proposta.getId());
        parameters.put("USER", sistemaFacade.getUsuarioCorrente().getNome());
        abr.gerarRelatorio(nomeArquivo, parameters);
    }

    public void recuperarSaldoFonteDespesaOrc() {
        if (selecionado.getFonteDespesaORC() != null) {
            saldoFonteDespesaORC = propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().saldoRealPorFonte(selecionado.getFonteDespesaORC(), selecionado.getDataLancamento(), selecionado.getUnidadeOrganizacional());
        }
    }

    private void validarSaldoFonteDespesaOrc() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(saldoFonteDespesaORC) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor deve ser igual ou menor que o saldo da Conta de Destinação de Recurso.");
        }
        ve.lancarException();
    }

    public DiariaPrestacaoConta getDiariaPrestacaoContaselecionada() {
        return diariaPrestacaoContaselecionada;
    }

    public void setDiariaPrestacaoContaselecionada(DiariaPrestacaoConta diariaPrestacaoContaselecionada) {
        this.diariaPrestacaoContaselecionada = diariaPrestacaoContaselecionada;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/suprimento-fundos/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getCodigo();
    }

    public void cancelarSolicitacaoEmpenho() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada! ", " O Suprimento de Fundos: " + selecionado.toString() + " foi salvo com sucesso."));
        super.cancelar();
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        return propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().buscarFontesDespORCPorContaDeDestinacaoEPermiteSuprimentoDeFundo(parte.trim(), selecionado.getDespesaORC());
    }

    public List<TipoContaDespesa> getTipoContaDespesaSuprimentoFundo() {
        return Collections.singletonList(TipoContaDespesa.SUPRIMENTO_FUNDO);
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(BigDecimal saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public DesdobramentoPropostaConcessao getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoPropostaConcessao desdobramento) {
        this.desdobramento = desdobramento;
    }

    public Boolean getBloquearNovoDesdobramento() {
        return selecionado.getDespesaORC() == null || isOperacaoEditar() || (propostaConcessaoDiariaFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getBloquearUmDesdobramento() && !selecionado.getDesdobramentos().isEmpty());
    }

    public void novoDesdobramento() {
        desdobramento = new DesdobramentoPropostaConcessao();
    }

    public List<Conta> completarDesdobramento(String parte) {
        return contaFacade.buscarContasFilhasDespesaORCPorTipo(
            parte.trim(),
            selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
            selecionado.getExercicio(),
            TipoContaDespesa.SUPRIMENTO_FUNDO,
            false);
    }

    public void adicionarDesdobramento() {
        try {
            validarDesdobramento();
            desdobramento.setPropostaConcessaoDiaria(selecionado);
            desdobramento.setSaldo(desdobramento.getValor());
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            selecionado.setValor(selecionado.getValorTotalDesdobramentos());
            cancelarDesdobramento();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarDesdobramento() {
        desdobramento = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:btnNovoDesdobramento')");
    }

    private void validarDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo elemento de despesa deve ser informado.");
        }
        ve.lancarException();
        desdobramento.realizarValidacoes();
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        for (DesdobramentoPropostaConcessao desd : selecionado.getDesdobramentos()) {
            if (!desd.equals(desdobramento) && desd.getConta().equals(desdobramento.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de despesa já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void removerDesdobramento(DesdobramentoPropostaConcessao obj) {
        selecionado.getDesdobramentos().remove(obj);
        selecionado.setValor(selecionado.getValorTotalDesdobramentos());
    }

    public void editarDesdobramento(DesdobramentoPropostaConcessao obj) {
        desdobramento = (DesdobramentoPropostaConcessao) Util.clonarObjeto(obj);
    }

    public List<Pessoa> completarPessoasFisicasAtivas(String parte) {
        return propostaConcessaoDiariaFacade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }
}
