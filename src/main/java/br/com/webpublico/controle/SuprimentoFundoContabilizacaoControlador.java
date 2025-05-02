/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigSuprimentoDeFundos;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.DesdobramentoDiaria;
import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.DiariaContabilizacao;
import br.com.webpublico.enums.*;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DiariaContabilizacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 * @author Usuário
 */
@ManagedBean(name = "suprimentoFundoContabilizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novocontabilizacaodiaria", pattern = "/contabilizacao-suprimento/novo/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundocontabilizacao/edita.xhtml"),
        @URLMapping(id = "editarcontabilizacaodiaria", pattern = "/contabilizacao-suprimento/editar/#{suprimentoFundoContabilizacaoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundocontabilizacao/edita.xhtml"),
        @URLMapping(id = "vercontabilizacaodiaria", pattern = "/contabilizacao-suprimento/ver/#{suprimentoFundoContabilizacaoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundocontabilizacao/visualizar.xhtml"),
        @URLMapping(id = "listarcontabilizacaodiaria", pattern = "/contabilizacao-suprimento/listar/", viewId = "/faces/financeiro/concessaodediarias/suprimentodefundocontabilizacao/lista.xhtml")
})
public class SuprimentoFundoContabilizacaoControlador extends PrettyControlador<DiariaContabilizacao> implements Serializable, CRUD {

    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private ConverterAutoComplete converterDiaria;
    private ConverterAutoComplete converterConta;
    private MoneyConverter moneyConverter;
    private DesdobramentoDiaria desdobramentoDiaria;

    @Override
    public AbstractFacade getFacede() {
        return diariaContabilizacaoFacade;
    }

    public SuprimentoFundoContabilizacaoControlador() {
        super(DiariaContabilizacao.class);
    }

    public DiariaContabilizacaoFacade getFacade() {
        return diariaContabilizacaoFacade;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDiariaContabilizacao odc : OperacaoDiariaContabilizacao.values()) {
            toReturn.add(new SelectItem(odc, odc.getDescricao()));
        }
        return toReturn;
    }

    public void setaDespesaORC() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        componenteTreeDespesaORC.setCodigo(p.getPropostaConcessaoDiaria().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(p.getPropostaConcessaoDiaria().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().getDespesaORCFacade().recuperaStrDespesaPorId(p.getPropostaConcessaoDiaria().getDespesaORC().getId()).getConta());
        selecionado.setValor(selecionado.getPropostaConcessaoDiaria().getValor());
        desdobramentoDiaria.setValor(selecionado.getPropostaConcessaoDiaria().getValor());
    }

    public List<Conta> completaContaDespesa(String parte) {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        if (p.getPropostaConcessaoDiaria() != null) {
            return diariaContabilizacaoFacade.getContaFacade().listaContasFilhasDespesaORC(parte.trim(), p.getPropostaConcessaoDiaria().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), sistemaControlador.getExercicioCorrente());
        } else {
            return new ArrayList<>();
        }
    }

    public BigDecimal getRetornaSaldo() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        if (p.getPropostaConcessaoDiaria() != null) {
            return diariaContabilizacaoFacade.retornaSaldoTotalSuprimentoFundo(p.getPropostaConcessaoDiaria());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Boolean habilitaPainelDesdobramento() {
        Boolean controle = Boolean.FALSE;
        if (selecionado.getOperacaoDiariaContabilizacao() != null) {
            if (selecionado.getOperacaoDiariaContabilizacao().equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
                if (selecionado.getPropostaConcessaoDiaria() != null) {
                    controle = Boolean.TRUE;
                }
            }
        }
        return controle;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, diariaContabilizacaoFacade.getContaFacade());
        }
        return converterConta;
    }

    public Boolean getVerificaEdicao() {
        if (selecionado.getId() != null) {
            return true;

        } else {
            return false;
        }
    }

    private void validaSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoLancamento.NORMAL.equals(selecionado.getTipoLancamento())) {
            if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(selecionado.getPropostaConcessaoDiaria().getValor()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor da Inscrição deve ser menor que o valor do Suprimento de Fundo.");
            }
            if (OperacaoDiariaContabilizacao.BAIXA.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(getRetornaSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor da Baixa deve ser menor que o saldo de <b>" + Util.formataValor(getRetornaSaldo()) + "</b> disponível para o Suprimento de Fundos: " + selecionado.getPropostaConcessaoDiaria().getCodigo() + ".");
            }
            if (OperacaoDiariaContabilizacao.APROPRIACAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(getRetornaSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor da Apropriação deve ser menor que o saldo de <b>" + Util.formataValor(getRetornaSaldo()) + "</b> disponível no Suprimento de Fundos: " + selecionado.getPropostaConcessaoDiaria().getCodigo() + ".");
            }
        }
        if (TipoLancamento.ESTORNO.equals(selecionado.getTipoLancamento())) {
            if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(diariaContabilizacaoFacade.saldoInscricaoPorDiaria(selecionado.getPropostaConcessaoDiaria())) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(selecionado.getValor()) + "</b> deve ser menor que o valor de <b> " + Util.formataValor(diariaContabilizacaoFacade.saldoInscricaoPorDiaria(selecionado.getPropostaConcessaoDiaria())) + "</b> para a operação 'Inscrição'.");
            }
            if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(getRetornaSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(selecionado.getValor()) + "</b> deve ser menor que o valor de <b> " + Util.formataValor(getRetornaSaldo()) + "</b> disponível no Suprimento de Fundos: " + selecionado.getPropostaConcessaoDiaria().getCodigo() + ".");
            }
            if (OperacaoDiariaContabilizacao.APROPRIACAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(diariaContabilizacaoFacade.saldoApropriacaoPorDiaria(selecionado.getPropostaConcessaoDiaria())) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(selecionado.getValor()) + "</b> deve ser menor que o valor de <b> " + Util.formataValor(diariaContabilizacaoFacade.saldoApropriacaoPorDiaria(selecionado.getPropostaConcessaoDiaria())) + "</b> para a operação 'Apropriação'.");
            }
            if (OperacaoDiariaContabilizacao.BAIXA.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && selecionado.getValor().compareTo(diariaContabilizacaoFacade.saldoBaixaPorDiaria(selecionado.getPropostaConcessaoDiaria())) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(selecionado.getValor()) + "</b> deve ser menor que o valor de <b> " + Util.formataValor(diariaContabilizacaoFacade.saldoBaixaPorDiaria(selecionado.getPropostaConcessaoDiaria())) + "</b> para a operação 'Baixa'.");
            }
        }
        ve.lancarException();
    }

    public BigDecimal totalDesdobramentos() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (DesdobramentoDiaria dd : selecionado.getDesdobramentoDiaria()) {
            total = total.add(dd.getValor());
        }
        return total;
    }

    public List<PropostaConcessaoDiaria> completaDiaria(String parte) {
        return diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().listaFiltrandoDiariasPorUnidadePessoaETipo(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), TipoProposta.SUPRIMENTO_FUNDO);
    }

    public List<PropostaConcessaoDiaria> completaSuprimento(String parte) {
        return diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().buscarDiariasPorUnidadeOrganizacionalETipoProposta(parte.trim(), TipoProposta.SUPRIMENTO_FUNDO, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), false);
    }

    public ConverterAutoComplete getConverterDiaria() {
        if (converterDiaria == null) {
            converterDiaria = new ConverterAutoComplete(PropostaConcessaoDiaria.class, diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade());
        }
        return converterDiaria;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public DesdobramentoDiaria getDesdobramentoDiaria() {
        return desdobramentoDiaria;
    }

    public void setDesdobramentoDiaria(DesdobramentoDiaria desdobramentoDiaria) {
        this.desdobramentoDiaria = desdobramentoDiaria;
    }

    private void validaDesdobramentoDiaria() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperacaoDiariaContabilizacao() != null) {
            if (selecionado.getOperacaoDiariaContabilizacao().equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
                BigDecimal somaTotalDesdobramentoTabela = BigDecimal.ZERO;
                for (DesdobramentoDiaria ds : selecionado.getDesdobramentoDiaria()) {
                    somaTotalDesdobramentoTabela = somaTotalDesdobramentoTabela.add(ds.getValor());
                }
                if (somaTotalDesdobramentoTabela.compareTo(selecionado.getPropostaConcessaoDiaria().getValor()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" O total dos desdobramentos de <b> " + Util.formataValor(totalDesdobramentos()) + " </b> deve ser menor que o valor de <b> " + Util.formataValor(selecionado.getPropostaConcessaoDiaria().getValor()) + " </b> diponível para este suprimento de fundos. ");
                }
                selecionado.setValor(somaTotalDesdobramentoTabela);
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaTipoLancamentos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoLancamento tl : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tl, tl.getDescricao()));
        }
        return toReturn;
    }

    public Boolean validaDesdobramento() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        Boolean retorno = true;
        if (desdobramentoDiaria.getDesdobramento() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Desdobramento deve ser informado.");
            retorno = Boolean.FALSE;
        }
        return retorno;
    }

    public void adicionaDesdobramento() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        if (validaDesdobramento()) {
            if (desdobramentoDiaria.getValor().compareTo(BigDecimal.ZERO) != 0) {
                desdobramentoDiaria.setDiariaContabilizacao(p);
                p.getDesdobramentoDiaria().add(desdobramentoDiaria);
                desdobramentoDiaria = new DesdobramentoDiaria();
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Valor deve ser maior que zero(0)");
            }
        }
    }

    public void excluirDesdobramento(ActionEvent evento) {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        desdobramentoDiaria = (DesdobramentoDiaria) evento.getComponent().getAttributes().get("objeto");
        p.getDesdobramentoDiaria().remove(desdobramentoDiaria);
        desdobramentoDiaria = new DesdobramentoDiaria();
    }

    public void editarDesdobramento(ActionEvent evento) {
        desdobramentoDiaria = (DesdobramentoDiaria) evento.getComponent().getAttributes().get("objeto");
        selecionado.getDesdobramentoDiaria().remove(desdobramentoDiaria);
    }

    @URLAction(mappingId = "novocontabilizacaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataDiaria(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setTipoProposta(TipoProposta.SUPRIMENTO_FUNDO);
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        operacao = Operacoes.NOVO;
        desdobramentoDiaria = new DesdobramentoDiaria();

        if (diariaContabilizacaoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", diariaContabilizacaoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "vercontabilizacaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = diariaContabilizacaoFacade.recuperar(selecionado.getId());
    }

    @URLAction(mappingId = "editarcontabilizacaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = diariaContabilizacaoFacade.recuperar(selecionado.getId());
        componenteTreeDespesaORC.setCodigo(selecionado.getPropostaConcessaoDiaria().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getPropostaConcessaoDiaria().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getPropostaConcessaoDiaria().getDespesaORC().getId()).getConta());
    }

    public void limpaCamposDetalhamento() {
        desdobramentoDiaria = new DesdobramentoDiaria();
        selecionado.getDesdobramentoDiaria().clear();
    }

    private void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPropostaConcessaoDiaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Suprimento de Fundos deve ser informado.");
        }
        if (selecionado.getOperacaoDiariaContabilizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Operação deve ser informado.");
        }
        if (selecionado.getHistorico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Histórico deve ser informado.");
        }
        if (selecionado.getEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Evento Contábil deve ser informado.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            if (selecionado.getPropostaConcessaoDiaria() != null) {
                selecionado.setUnidadeOrganizacional(selecionado.getPropostaConcessaoDiaria().getUnidadeOrganizacional());
            }
            validaCampos();
            validaDesdobramentoDiaria();
            validaSalvar();
            if (isOperacaoNovo()) {
                diariaContabilizacaoFacade.salvarNovo(selecionado, true);
            } else {
                diariaContabilizacaoFacade.salvar(selecionado);
            }
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Suprimento de Fundos foi contabilizado com sucesso na " + selecionado.toString() + ".");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Erro: " + ex.getMessage());
        }
    }

    public String setaEvento() {
        DiariaContabilizacao dc = ((DiariaContabilizacao) selecionado);
        ConfigSuprimentoDeFundos configSuprimentoDeFundos;
        dc.setEventoContabil(null);
        try {
            if (dc.getTipoLancamento() != null && dc.getOperacaoDiariaContabilizacao() != null) {
                configSuprimentoDeFundos = diariaContabilizacaoFacade.getConfigSuprimentoDeFundosFacade().recuperaEvento(dc);
                dc.setEventoContabil(configSuprimentoDeFundos.getEventoContabil());
                return dc.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabilizacao-suprimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
