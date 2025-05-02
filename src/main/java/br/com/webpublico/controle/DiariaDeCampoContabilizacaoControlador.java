/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DiariaContabilizacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Usuário
 */
@ManagedBean(name = "diariaDeCampoContabilizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-contabilizacao-diaria", pattern = "/contabilizacao-diaria/novo/", viewId = "/faces/financeiro/concessaodediarias/diariacampocontabilizacao/edita.xhtml"),
    @URLMapping(id = "novo-contabilizacao-diaria-sem-filtro", pattern = "/contabilizacao-diaria-sem-filtro/novo/", viewId = "/faces/financeiro/concessaodediarias/diariacampocontabilizacao/edita-sem-filtro.xhtml"),
    @URLMapping(id = "editar-contabilizacao-diaria", pattern = "/contabilizacao-diaria/editar/#{diariaDeCampoContabilizacaoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/diariacampocontabilizacao/edita.xhtml"),
    @URLMapping(id = "ver-contabilizacao-diaria", pattern = "/contabilizacao-diaria/ver/#{diariaDeCampoContabilizacaoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/diariacampocontabilizacao/visualizar.xhtml"),
    @URLMapping(id = "listar-contabilizacao-diaria", pattern = "/contabilizacao-diaria/listar/", viewId = "/faces/financeiro/concessaodediarias/diariacampocontabilizacao/lista.xhtml")
})
public class DiariaDeCampoContabilizacaoControlador extends PrettyControlador<DiariaContabilizacao> implements Serializable, CRUD {

    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private ConverterAutoComplete converterDiaria;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterPropostaConcessaoDiaria;
    private MoneyConverter moneyConverter;
    private String numero;
    private List<PropostaConcessaoDiaria> listaProposta;
    private ConfigDiariaDeCampo configDiariaDeCampo;
    private Boolean contabilizar;
    private Boolean semFiltro;

    @Override
    public AbstractFacade getFacede() {
        return diariaContabilizacaoFacade;
    }

    public DiariaDeCampoContabilizacaoControlador() {
        super(DiariaContabilizacao.class);
    }

    public DiariaContabilizacaoFacade getFacade() {
        return diariaContabilizacaoFacade;
    }

    public ConfigDiariaDeCampo getConfigDiariaDeCampo() {
        return configDiariaDeCampo;
    }

    public List<PropostaConcessaoDiaria> getListaProposta() {
        return listaProposta;
    }

    public void setListaProposta(List<PropostaConcessaoDiaria> listaProposta) {
        this.listaProposta = listaProposta;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDiariaContabilizacao odc : OperacaoDiariaContabilizacao.values()) {
            if (!odc.equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
                toReturn.add(new SelectItem(odc, odc.getDescricao()));
            }
        }
        return toReturn;
    }

    public void limpaTabela() {
        if (listaProposta != null) {
            listaProposta.clear();
        }
        selecionado.setPropostaConcessaoDiaria(null);
    }

    public void setaDespesaORC() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        componenteTreeDespesaORC.setCodigo(p.getPropostaConcessaoDiaria().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(p.getPropostaConcessaoDiaria().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().getDespesaORCFacade().recuperaStrDespesaPorId(p.getPropostaConcessaoDiaria().getDespesaORC().getId()).getConta());
    }

    public String setaEvento() {
        DiariaContabilizacao dc = ((DiariaContabilizacao) selecionado);
        ConfigDiariaDeCampo configuracaoDiariaCampo;
        ConfigDiariaCivil configuracaoDiariaCivil;
        dc.setEventoContabil(null);
        try {
            if (dc.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIACAMPO) && dc.getTipoLancamento() != null && dc.getOperacaoDiariaContabilizacao() != null) {
                configuracaoDiariaCampo = diariaContabilizacaoFacade.getConfigDiariaDeCampoFacade().recuperaEvento(dc);
                dc.setEventoContabil(configuracaoDiariaCampo.getEventoContabil());
                return dc.getEventoContabil().toString();
            } else if (dc.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA) && dc.getTipoLancamento() != null && dc.getOperacaoDiariaContabilizacao() != null) {
                configuracaoDiariaCivil = diariaContabilizacaoFacade.getConfigDiariaCivilFacade().recuperaEvento(dc);
                dc.setEventoContabil(configuracaoDiariaCivil.getEventoContabil());
                return dc.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }

        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    @URLAction(mappingId = "ver-contabilizacao-diaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-contabilizacao-diaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }


    @URLAction(mappingId = "novo-contabilizacao-diaria-sem-filtro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoSemFiltro() {
        novo();
        semFiltro = true;
    }

    @URLAction(mappingId = "novo-contabilizacao-diaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        p.setDataDiaria(sistemaControlador.getDataOperacao());
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        operacao = Operacoes.NOVO;
        numero = "";
        listaProposta = new ArrayList<>();
        selecionado.setTipoProposta(TipoProposta.CONCESSAO_DIARIA);
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        contabilizar = true;
        semFiltro = false;
        if (diariaContabilizacaoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", diariaContabilizacaoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public List<SelectItem> getTipoProposta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIA, TipoProposta.CONCESSAO_DIARIA.getDescricao()));
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIACAMPO, TipoProposta.CONCESSAO_DIARIACAMPO.getDescricao()));
        return toReturn;
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<TipoLancamento>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<Conta> completaContaDespesa(String parte) {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        if (p.getPropostaConcessaoDiaria() != null) {
            return diariaContabilizacaoFacade.getContaFacade().listaContasFilhasDespesaORC(parte.trim(), p.getPropostaConcessaoDiaria().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), sistemaControlador.getExercicioCorrente());
        } else {
            return new ArrayList<>();
        }
    }

    public List<PropostaConcessaoDiaria> completarDiarias(String parte) {
        if (TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta())) {
            return diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().buscarDiariasPorUnidadeOrganizacionalETipoProposta(parte.trim(), TipoProposta.CONCESSAO_DIARIA, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), semFiltro);
        }
        if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            return diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().buscarDiariasPorUnidadeOrganizacionalETipoProposta(parte.trim(), TipoProposta.CONCESSAO_DIARIACAMPO, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), semFiltro);
        }
        return new ArrayList<>();
    }

    public BigDecimal retornaSaldoInscrito(PropostaConcessaoDiaria p) {
        if (p != null) {
            return diariaContabilizacaoFacade.saldoInscricaoPorDiaria(p).subtract(diariaContabilizacaoFacade.saldoEstornoInscricaoPorDiaria(p));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal retornaSaldoBaixa(PropostaConcessaoDiaria p) {
        if (p != null) {
            return diariaContabilizacaoFacade.saldoBaixaPorDiaria(p).subtract(diariaContabilizacaoFacade.saldoEstornoBaixaPorDiaria(p));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getRetornaSaldo() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        if (p.getPropostaConcessaoDiaria() != null) {
            return diariaContabilizacaoFacade.retornaSaldoTotalDiaria(p.getPropostaConcessaoDiaria());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal retornaSaldo(PropostaConcessaoDiaria p) {

        if (p != null) {
            return diariaContabilizacaoFacade.retornaSaldoTotalDiaria(p);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Boolean habilitaAutoCompleteDesdobramento() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        Boolean controle = Boolean.TRUE;
        if (p.getOperacaoDiariaContabilizacao() != null && p.getPropostaConcessaoDiaria() != null) {
            if (p.getOperacaoDiariaContabilizacao().equals(OperacaoDiariaContabilizacao.INSCRICAO) || p.getOperacaoDiariaContabilizacao().equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
                controle = Boolean.FALSE;
            }
        } else {
            controle = Boolean.TRUE;
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
        if (operacao.equals(Operacoes.NOVO) || operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public void recuperarEditarVer() {
        selecionado = diariaContabilizacaoFacade.recuperar(selecionado.getId());
        selecionado.setPropostaConcessaoDiaria(diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().recuperar(selecionado.getPropostaConcessaoDiaria().getId()));
        operacao = Operacoes.EDITAR;
        componenteTreeDespesaORC.setCodigo(selecionado.getPropostaConcessaoDiaria().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getPropostaConcessaoDiaria().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade().getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getPropostaConcessaoDiaria().getDespesaORC().getId()).getConta());
    }

    public void somaValoresASerContabilzados() {
        DiariaContabilizacao p = ((DiariaContabilizacao) selecionado);
        BigDecimal soma = BigDecimal.ZERO;
        if (listaProposta != null) {
            for (PropostaConcessaoDiaria pdc : listaProposta) {
                soma = soma.add(pdc.getValorContabilizado());
                p.setValor(soma);
            }
        }

        RequestContext.getCurrentInstance().update(":Formulario:diarias:soma");
    }

    public void validarCampos() {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        ValidacaoException ve = new ValidacaoException();
        for (PropostaConcessaoDiaria pc : listaProposta) {
            BigDecimal saldoTotalDiaria = retornaSaldo(pc);
            if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor da contabilização deve ser maior que zero (0).");
            }
            if (TipoLancamento.NORMAL.equals(selecionado.getTipoLancamento())) {
                if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && pc.getValorContabilizado().add(diariaContabilizacaoFacade.saldoInscricaoPorDiaria(pc)).compareTo(pc.getValor()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Saldo da inscrição não pode ser  maior que o valor da diária. Valor da Diária.: " + Util.formataValor(pc.getValor()));
                }
                if (OperacaoDiariaContabilizacao.BAIXA.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && pc.getValorContabilizado().compareTo(saldoTotalDiaria) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor da baixa não pode ser maior que o saldo. Saldo.: " + df.format(saldoTotalDiaria));
                }
            }
            if (TipoLancamento.ESTORNO.equals(selecionado.getTipoLancamento())) {
                if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && pc.getValorContabilizado().compareTo(diariaContabilizacaoFacade.saldoInscricaoPorDiaria(pc)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor do estorno da inscrição não pode ser maior que o valor da inscrição. Valor da Inscrição.: " + df.format(diariaContabilizacaoFacade.saldoInscricaoPorDiaria(pc)));
                }
                if (OperacaoDiariaContabilizacao.INSCRICAO.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && pc.getValorContabilizado().compareTo(saldoTotalDiaria) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor do estorno da inscrição não pode ser maior que o saldo. Saldo.: " + df.format(saldoTotalDiaria));
                }
                if (OperacaoDiariaContabilizacao.BAIXA.equals(selecionado.getOperacaoDiariaContabilizacao())
                    && pc.getValorContabilizado().compareTo(diariaContabilizacaoFacade.saldoBaixaPorDiaria(pc)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor do estorno da baixa não pode ser maior que o valor da baixa. Valor do Baixa.: " + df.format(diariaContabilizacaoFacade.saldoBaixaPorDiaria(pc)));
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            for (PropostaConcessaoDiaria pcd : listaProposta) {
                selecionado.setPropostaConcessaoDiaria(pcd);
                selecionado.setValor(pcd.getValorContabilizado());
                selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
                diariaContabilizacaoFacade.salvarNovo(selecionado, contabilizar);
                selecionado.setId(null);
            }
            FacesUtil.addOperacaoRealizada(" Proposta Concessão de " + selecionado.getTipoProposta().getDescricao() + " na " + selecionado.toString() + " foi salvo com sucesso.");
            if (semFiltro) {
                FacesUtil.redirecionamentoInterno("/contabilizacao-diaria-sem-filtro/novo/");
            } else {
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void adicionarProposta() {
        if (listaProposta == null) {
            listaProposta = new ArrayList<PropostaConcessaoDiaria>();
        }
        if (!listaProposta.contains(selecionado.getPropostaConcessaoDiaria())) {
            listaProposta.add(selecionado.getPropostaConcessaoDiaria());
            selecionado.setPropostaConcessaoDiaria(null);

        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " A Diária " + selecionado.getPropostaConcessaoDiaria() + " já foi adicionada.");
        }

    }

    public void listaPropostaPorGrupo() {
        if (!numero.isEmpty() || numero != null) {
            listaProposta = diariaContabilizacaoFacade.listaDiariasPorAgrupador(numero);
            adicionarProposta();
        }
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ConverterAutoComplete getConverterPropostaConcessaoDiaria() {
        if (converterPropostaConcessaoDiaria == null) {
            converterPropostaConcessaoDiaria = new ConverterAutoComplete(PropostaConcessaoDiaria.class, diariaContabilizacaoFacade.getPropostaConcessaoDiariaFacade());
        }
        return converterPropostaConcessaoDiaria;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabilizacao-diaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean getContabilizar() {
        return contabilizar;
    }

    public void setContabilizar(Boolean contabilizar) {
        this.contabilizar = contabilizar;
    }
}
