package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.entidadesauxiliares.ItemConsignacao;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoDeConsignacao;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoLancamentoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.BaseFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.LancamentoFPFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mateus on 29/09/17.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consignacao-servidor", pattern = "/consignacao/servidor/", viewId = "/faces/rh/estatisticas/informacoes/consignacao.xhtml")
})
public class ConsignacaoServidorControlador implements Serializable {

    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ObjetoPesquisa objetoPesquisa;
    private List<ItemConsignacao> itemConsignacoes;
    private BigDecimal totalBaseConsignacao;
    private BigDecimal totalBase5;
    private BigDecimal totalBase10;
    private BigDecimal totalBase20;
    private BigDecimal restante5;
    private BigDecimal restante10;
    private BigDecimal restante20;
    private BigDecimal totalCartao;
    private BigDecimal totalConvenio;
    private BigDecimal totalEmprestimo;
    private BigDecimal cambioEntreConvenioEmprestimo;
    private List<ItemFichaFinanceiraFP> itensFichaFinanceira;
    private Integer versao;
    private BigDecimal totalBaseEuConsigoMais;
    private BigDecimal valorMargemEuConsigoMais;
    private BigDecimal percentualMargemEuConsigoMais;
    private Integer minimoDiasDireitoEuConsigoMais;

    @URLAction(mappingId = "consignacao-servidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        objetoPesquisa = new ObjetoPesquisa();
        itensFichaFinanceira = new LinkedList<>();
        itemConsignacoes = new LinkedList<>();
        inicializarValores();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (objetoPesquisa.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor deve ser informado.");
        }
        if (objetoPesquisa.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo mês deve ser informado.");
        }
        if (objetoPesquisa.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ano deve ser informado.");
        }
        if (objetoPesquisa.getTipoFolhaDePagamentoWeb() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de folha deve ser informado.");
        }
        ve.lancarException();
    }

    public void consultarFicha() {
        try {
            validarCampos();
            itensFichaFinanceira = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(objetoPesquisa.getMes(), objetoPesquisa.getAno(), objetoPesquisa.getVinculoFP(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), versao);
            calcularConsignacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void calcularConsignacao() {
        inicializarValores();
        itemConsignacoes = new LinkedList<>();
        for (TipoDeConsignacao tipoDeConsignacao : TipoDeConsignacao.values()) {
            List<LancamentoFP> lancamentosFp = lancamentoFPFacade.listaLancamentosPorTipoConsignacao(objetoPesquisa.getVinculoFP(), objetoPesquisa.getAno(), objetoPesquisa.getMes(), tipoDeConsignacao);
            for (LancamentoFP lancamentoFP : lancamentosFp) {
                itemConsignacoes.add(new ItemConsignacao(null, lancamentoFP));
            }
        }
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : itensFichaFinanceira) {
            ItemConsignacao itemConsignacao = eventoContemNoLancamento(itemFichaFinanceiraFP.getEventoFP());
            if (itemConsignacao != null) itemConsignacao.setItemFichaFinanceiraFP(itemFichaFinanceiraFP);
        }
        ConfiguracaoFP configuracaoFP = baseFPFacade.buscarConfiguracaoFPVigente();
        totalBaseConsignacao = baseFPFacade.recuperarValoresBase(itensFichaFinanceira);
        totalBaseEuConsigoMais = baseFPFacade.recurperarValoresEuConsigoMais(itensFichaFinanceira, configuracaoFP);
        percentualMargemEuConsigoMais = configuracaoFP.getPercentualMargemEmprestimo();
        valorMargemEuConsigoMais = calcularMargemEuConsigoMais();
        minimoDiasDireitoEuConsigoMais = configuracaoFP.getQtdeMinimaDiasEuConsigMais();
        preencherValoresDoPercentualDaConsignacao();
        preencherValoresParaDescontados();
        verificarVerbasNaoDescontadas();
    }

    private BigDecimal calcularMargemEuConsigoMais() {
        return (totalBaseEuConsigoMais != null ? totalBaseEuConsigoMais : BigDecimal.ZERO).multiply(percentualMargemEuConsigoMais != null ?
            percentualMargemEuConsigoMais : BigDecimal.ZERO).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    private void preencherValoresParaDescontados() {
        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (TipoLancamentoFP.VALOR.equals(itemConsignacao.getLancamentoFP().getTipoLancamentoFP())) {
                if (TipoDeConsignacao.CARTAO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalCartao = totalCartao.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (TipoDeConsignacao.CONVENIO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalConvenio = totalConvenio.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (TipoDeConsignacao.EMPRESTIMO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalEmprestimo = totalEmprestimo.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
            } else {
                if (itemConsignacao.getItemFichaFinanceiraFP() != null) {
                    if (TipoDeConsignacao.CARTAO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalCartao = totalCartao.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (TipoDeConsignacao.CONVENIO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalConvenio = totalConvenio.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (TipoDeConsignacao.EMPRESTIMO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalEmprestimo = totalEmprestimo.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                }
            }
        }
    }

    private void preencherValoresDoPercentualDaConsignacao() {
        if (totalBaseConsignacao.compareTo(BigDecimal.ZERO) != 0) {
            totalBase5 = totalBaseConsignacao.multiply(new BigDecimal("0.05"));
            totalBase10 = totalBaseConsignacao.multiply(new BigDecimal("0.1"));
            totalBase20 = totalBaseConsignacao.multiply(new BigDecimal("0.2"));
        }
    }

    private void verificarVerbasNaoDescontadas() {
        cambioEntreConvenioEmprestimo = lancamentoFPFacade.verificarConvenio(itemConsignacoes,totalBase20,totalBase10, cambioEntreConvenioEmprestimo, totalConvenio);
        lancamentoFPFacade.verificarEmprestimo(itemConsignacoes,cambioEntreConvenioEmprestimo);
        lancamentoFPFacade.verificarCartao(itemConsignacoes,totalBase5);
    }

    private ItemConsignacao eventoContemNoLancamento(EventoFP eventoFP) {
        for (ItemConsignacao item : itemConsignacoes) {
            if (item.getLancamentoFP().getEventoFP().equals(eventoFP)) {
                return item;
            }
        }
        return null;
    }

    private void inicializarValores() {
        totalBaseConsignacao = BigDecimal.ZERO;

        totalBase5 = BigDecimal.ZERO;
        totalBase10 = BigDecimal.ZERO;
        totalBase20 = BigDecimal.ZERO;

        totalCartao = BigDecimal.ZERO;
        totalConvenio = BigDecimal.ZERO;
        totalEmprestimo = BigDecimal.ZERO;

        restante5 = BigDecimal.ZERO;
        restante10 = BigDecimal.ZERO;
        restante20 = BigDecimal.ZERO;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (objetoPesquisa.getMes() != null && objetoPesquisa.getAno() != null && objetoPesquisa.getTipoFolhaDePagamentoWeb() != null && objetoPesquisa.getVinculoFP() != null) {
            retorno.add(new SelectItem(null, "TODAS"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersaoWithIntevaloMes(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), objetoPesquisa.getTipoFolhaDePagamentoWeb(), Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getVinculoFP())) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public BaseFPFacade getBaseFPFacade() {
        return baseFPFacade;
    }

    public void setBaseFPFacade(BaseFPFacade baseFPFacade) {
        this.baseFPFacade = baseFPFacade;
    }

    public ObjetoPesquisa getObjetoPesquisa() {
        return objetoPesquisa;
    }

    public void setObjetoPesquisa(ObjetoPesquisa objetoPesquisa) {
        this.objetoPesquisa = objetoPesquisa;
    }

    public List<ItemConsignacao> getItemConsignacoes() {
        return itemConsignacoes;
    }

    public void setItemConsignacoes(List<ItemConsignacao> itemConsignacoes) {
        this.itemConsignacoes = itemConsignacoes;
    }

    public BigDecimal getTotalBaseConsignacao() {
        return totalBaseConsignacao;
    }

    public void setTotalBaseConsignacao(BigDecimal totalBaseConsignacao) {
        this.totalBaseConsignacao = totalBaseConsignacao;
    }

    public BigDecimal getTotalBase5() {
        return totalBase5;
    }

    public void setTotalBase5(BigDecimal totalBase5) {
        this.totalBase5 = totalBase5;
    }

    public BigDecimal getTotalBase10() {
        return totalBase10;
    }

    public void setTotalBase10(BigDecimal totalBase10) {
        this.totalBase10 = totalBase10;
    }

    public BigDecimal getTotalBase20() {
        return totalBase20;
    }

    public void setTotalBase20(BigDecimal totalBase20) {
        this.totalBase20 = totalBase20;
    }

    public BigDecimal getRestante5() {
        return restante5;
    }

    public void setRestante5(BigDecimal restante5) {
        this.restante5 = restante5;
    }

    public BigDecimal getRestante10() {
        return restante10;
    }

    public void setRestante10(BigDecimal restante10) {
        this.restante10 = restante10;
    }

    public BigDecimal getRestante20() {
        return restante20;
    }

    public void setRestante20(BigDecimal restante20) {
        this.restante20 = restante20;
    }

    public BigDecimal getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(BigDecimal totalCartao) {
        this.totalCartao = totalCartao;
    }

    public BigDecimal getTotalConvenio() {
        return totalConvenio;
    }

    public void setTotalConvenio(BigDecimal totalConvenio) {
        this.totalConvenio = totalConvenio;
    }

    public BigDecimal getTotalEmprestimo() {
        return totalEmprestimo;
    }

    public void setTotalEmprestimo(BigDecimal totalEmprestimo) {
        this.totalEmprestimo = totalEmprestimo;
    }

    public BigDecimal getCambioEntreConvenioEmprestimo() {
        return cambioEntreConvenioEmprestimo;
    }

    public void setCambioEntreConvenioEmprestimo(BigDecimal cambioEntreConvenioEmprestimo) {
        this.cambioEntreConvenioEmprestimo = cambioEntreConvenioEmprestimo;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public BigDecimal getTotalBaseEuConsigoMais() {
        return totalBaseEuConsigoMais;
    }

    public void setTotalBaseEuConsigoMais(BigDecimal totalBaseEuConsigoMais) {
        this.totalBaseEuConsigoMais = totalBaseEuConsigoMais;
    }

    public BigDecimal getValorMargemEuConsigoMais() {
        return valorMargemEuConsigoMais;
    }

    public void setValorMargemEuConsigoMais(BigDecimal valorMargemEuConsigoMais) {
        this.valorMargemEuConsigoMais = valorMargemEuConsigoMais;
    }

    public BigDecimal getPercentualMargemEuConsigoMais() {
        return percentualMargemEuConsigoMais;
    }

    public void setPercentualMargemEuConsigoMais(BigDecimal percentualMargemEuConsigoMais) {
        this.percentualMargemEuConsigoMais = percentualMargemEuConsigoMais;
    }

    public Integer getMinimoDiasDireitoEuConsigoMais() {
        return minimoDiasDireitoEuConsigoMais;
    }

    public void setMinimoDiasDireitoEuConsigoMais(Integer minimoDiasDireitoEuConsigoMais) {
        this.minimoDiasDireitoEuConsigoMais = minimoDiasDireitoEuConsigoMais;
    }
}
