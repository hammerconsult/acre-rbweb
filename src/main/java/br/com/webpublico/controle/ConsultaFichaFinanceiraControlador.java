/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioFichaFinanceira;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.ItemFichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.FichaFinanceiraFPSimulacao;
import br.com.webpublico.entidades.rh.administracaodepagamento.ItemFichaFinanceiraFPSimulacao;
import br.com.webpublico.entidadesauxiliares.rh.FiltroFolhaDePagamentoDTO;
import br.com.webpublico.entidadesauxiliares.rh.calculo.MemoriaCalculoRHDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorBaseFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.FichaFinanceiraFPSimulacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WebPublico07
 */
@ManagedBean(name = "consultaFichaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaFichaFinanceira", pattern = "/consultafichafinanceira/novo/", viewId = "/faces/rh/administracaodepagamento/consultafichafinanceira/lista.xhtml"),
    @URLMapping(id = "nova-consultaFichaFinanceira-simulacao", pattern = "/consultafichafinanceira-simulacao/novo/", viewId = "/faces/rh/administracaodepagamento/consultafichafinanceira-simulacao/lista.xhtml")
})
public class ConsultaFichaFinanceiraControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaFichaFinanceiraControlador.class);

    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private FichaFinanceiraFPSimulacaoFacade fichaFinanceiraSimulacaoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private VinculoFP contratoFPSelecionado;
    private String mes;
    private Integer ano;
    @ManagedProperty(name = "relatorioFichaFinanceira", value = "#{relatorioFichaFinanceira}")
    private RelatorioFichaFinanceira relatorioFichaFinanceira;
    private ConverterAutoComplete converterContratoFP;
    private FichaFinanceiraFP fichaFinanceiraFP;
    private FichaFinanceiraFPSimulacao fichaFinanceiraFPSimulacao;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private List<ItemFichaFinanceiraFP> listaItemFichaFinanceiraFP;
    private List<ItemFichaFinanceiraFPSimulacao> itensFichaSimulacao;
    private Map<EventoFP, List<MemoriaCalculoRHDTO>> mapMemoriaCalculo;
    private Double totalDescontos = 0.0;
    private Double totalVantagens = 0.0;
    private Integer versao;
    @ManagedProperty(name = "calculoFolhaDePagamentoControlador", value = "#{calculoFolhaDePagamentoControlador}")
    private CalculoFolhaDePagamentoControlador calculoFolhaDePagamentoControlador;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<ItemFichaFinanceiraFP> getListaItemFichaFinanceiraFP() {
        return listaItemFichaFinanceiraFP;
    }

    @URLAction(mappingId = "novaConsultaFichaFinanceira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsultaFichaFinanceira(){
        limpaCampos();
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            recuperarParametrosUrl();
            consultar();
        }
    }

    @URLAction(mappingId = "nova-consultaFichaFinanceira-simulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsultaFichaFinanceiraSimulacao(){
        limpaCampos();
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            recuperarParametrosUrl();
            consultarSimulacao();
        }
    }

    public void limpaCampos() {
        mes = null;
        ano = null;
        versao = null;
        contratoFPSelecionado = null;
        fichaFinanceiraFP = null;
        tipoFolhaDePagamento = null;
        listaItemFichaFinanceiraFP = null;
        totalDescontos = 0.0;
        totalVantagens = 0.0;
        itensFichaSimulacao = Lists.newLinkedList();
        mapMemoriaCalculo = Maps.newHashMap();
    }

    private void recuperarParametrosUrl() {
        if (buscarParametrosURL("contrato") != null && buscarParametrosURL("mes") != null
            && buscarParametrosURL("ano") != null && buscarParametrosURL("tipo") != null) {
            mes = buscarParametrosURL("mes");
            ano = Integer.valueOf(buscarParametrosURL("ano"));
            contratoFPSelecionado = contratoFPFacade.recuperar(Long.parseLong(buscarParametrosURL("contrato")));
            tipoFolhaDePagamento = TipoFolhaDePagamento.valueOf(buscarParametrosURL("tipo"));
        }
        versao = buscarParametrosURL("versao") != null ? Integer.valueOf(buscarParametrosURL("versao")) : null;
    }

    private String buscarParametrosURL(String texto) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(texto);
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public RelatorioFichaFinanceira getRelatorioFichaFinanceira() {
        return relatorioFichaFinanceira;
    }

    public void setRelatorioFichaFinanceira(RelatorioFichaFinanceira relatorioFichaFinanceira) {
        this.relatorioFichaFinanceira = relatorioFichaFinanceira;
    }

    public String concatenaIDSFichas(List<FichaFinanceiraFP> lista) {
        String ids = "";
        for (FichaFinanceiraFP fichas : lista) {
            ids += fichas.getId() + ",";
        }
        ids += ids.substring(0, ids.length() - 1);

        return ids;
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public List<VinculoFP> buscarFuncionariosPorFiltro(String s) {
        return contratoFPFacade.buscarDetalhesFuncionariosPorFiltro(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public VinculoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(VinculoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));

        }
        return toReturn;
    }

    public void consultar() {
        mapMemoriaCalculo = Maps.newHashMap();
        if (validaCampos()) {
            listaItemFichaFinanceiraFP = fichaFinanceiraFPFacade.recuperafichaFinanceiraFP(Integer.parseInt(mes), ano, contratoFPSelecionado, tipoFolhaDePagamento, versao);
            if (!listaItemFichaFinanceiraFP.isEmpty()) {
                fichaFinanceiraFP = (((ItemFichaFinanceiraFP) listaItemFichaFinanceiraFP.get(0)).getFichaFinanceiraFP());
            }
            atualizaValores(listaItemFichaFinanceiraFP);
            buscarMemoriaCalculo(listaItemFichaFinanceiraFP);
        } else {
            fichaFinanceiraFP = null;
            listaItemFichaFinanceiraFP = null;
        }
    }

    private void buscarMemoriaCalculo(List<ItemFichaFinanceiraFP> itens) {
        for (ItemFichaFinanceiraFP iten : itens) {
            mapMemoriaCalculo.put(iten.getEventoFP(), MemoriaCalculoRHDTO.toDto(fichaFinanceiraFPFacade.buscarMemoriasCalculoPorEvento(iten)));
        }
    }

    public List<MemoriaCalculoRHDTO> getMemoriaCalculo(EventoFP e) {
        if (mapMemoriaCalculo.containsKey(e)) {
            return mapMemoriaCalculo.get(e);
        }
        return null;
    }

    public void consultarSimulacao() {
        if (validaCampos()) {
            itensFichaSimulacao = fichaFinanceiraSimulacaoFPFacade.recuperarItemFichaFinanceiraSimulacao(Integer.parseInt(mes), ano, contratoFPSelecionado, tipoFolhaDePagamento);
            if (!itensFichaSimulacao.isEmpty()) {
                fichaFinanceiraFPSimulacao = itensFichaSimulacao.get(0).getFichaFinanceiraFPSimulacao();
            }
            atualizarContadores(itensFichaSimulacao);
        } else {
            fichaFinanceiraFP = null;
            itensFichaSimulacao = Lists.newLinkedList();
        }
    }

    public boolean validaCampos() {
        boolean valida = true;

        if (contratoFPSelecionado == null) {
            FacesUtil.addMessageWarn("Atenção !", "O Campo Servidor é obrigatório !");
            valida = false;
        }

        if (ano == null) {
            FacesUtil.addMessageWarn("Atenção !", "O Campo Ano é obrigatório !");
            valida = false;
        } else if (ano.toString().length() != 4) {
            FacesUtil.addMessageWarn("Atenção !", "O Ano informado é inválido !");
            valida = false;
        }


        if (mes == null || mes.equals("")) {
            FacesUtil.addMessageWarn("Atenção !", "O Campo mês é obrigatório !");
            valida = false;
            return valida;
        }
        int mesInteiro = Integer.parseInt(mes);
        if (mesInteiro < 1 || mesInteiro > 12) {
            FacesUtil.addMessageWarn("Atenção !", "O mês informado é inválido !");
            valida = false;
        }

        if (tipoFolhaDePagamento == null) {
            FacesUtil.addMessageWarn("Atenção !", "O Campo Tipo de Folha de Pagamento é obrigatório !");
            valida = false;
        }

        return valida;
    }

    public boolean isPodeExcluir() {
        return ((fichaFinanceiraFP != null)
            && (fichaFinanceiraFP.getFolhaDePagamento().getCompetenciaFP() != null)
            && (fichaFinanceiraFP.getFolhaDePagamento().getCompetenciaFP().getStatusCompetencia() != StatusCompetencia.EFETIVADA));
    }

    public void excluir() {
        try {
            checarRestricoes(fichaFinanceiraFP);
            fichaFinanceiraFPFacade.remover(fichaFinanceiraFP);
            fichaFinanceiraFP = null;
            listaItemFichaFinanceiraFP = null;
        } catch (ValidacaoException e) {
            logger.debug("Não é possível excluir ficha já efetivada ou com crédito salário pago.");
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao Excluir Ficha !", e.getMessage());
        }
    }

    public void excluirSimulacao() {
        try {
            checarRestricoesSimulacao(fichaFinanceiraFPSimulacao);
            fichaFinanceiraSimulacaoFPFacade.remover(fichaFinanceiraFPSimulacao);
            fichaFinanceiraFPSimulacao = null;
            itensFichaSimulacao = Lists.newLinkedList();
        } catch (ValidacaoException e) {
            logger.debug("Não é possível excluir ficha já efetivada ou com crédito salário pago.");
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        } catch (Exception e) {
            logger.error("erro: ", e);
            FacesUtil.addMessageError("Erro ao Excluir Ficha !", e.getMessage());
        }
    }

    private void checarRestricoes(FichaFinanceiraFP fichaFinanceiraFP) {
        ValidacaoException val = new ValidacaoException();
        if (fichaFinanceiraFP.getFolhaDePagamento().folhaEfetivada() || fichaFinanceiraFP.getCreditoSalarioPago()) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Folha já Efetivada!");
        }
        val.lancarException();
    }

    private void checarRestricoesSimulacao(FichaFinanceiraFPSimulacao fichaFinanceiraFP) {
        ValidacaoException val = new ValidacaoException();
        if (fichaFinanceiraFP.getFolhaDePagamentoSimulacao().folhaEfetivada()) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Folha já Efetivada!");
        }
        val.lancarException();
    }

    public void atualizaValores(List<ItemFichaFinanceiraFP> fichas) {
        totalDescontos = 0.0;
        totalVantagens = 0.0;
        for (ItemFichaFinanceiraFP itens : fichas) {
            if (itens.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                totalDescontos += itens.getValor().doubleValue();
            }
            if (itens.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                totalVantagens += itens.getValor().doubleValue();
            }
        }
    }

    public void atualizarContadores(List<ItemFichaFinanceiraFPSimulacao> fichas) {
        totalDescontos = 0.0;
        totalVantagens = 0.0;
        for (ItemFichaFinanceiraFPSimulacao itens : fichas) {
            if (itens.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                totalDescontos += itens.getValor().doubleValue();
            }
            if (itens.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                totalVantagens += itens.getValor().doubleValue();
            }
        }
    }

    public Double getTotalDescontos() {
        return totalDescontos;
    }

    public void setTotalDescontos(Double totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public Double getTotalVantagens() {
        return totalVantagens;
    }

    public void setTotalVantagens(Double totalVantagens) {
        this.totalVantagens = totalVantagens;
    }

    public Double getTotal() {
        return totalVantagens - totalDescontos;
    }

    public List<ItemFichaFinanceiraFPSimulacao> getItensFichaSimulacao() {
        return itensFichaSimulacao;
    }

    public void setItensFichaSimulacao(List<ItemFichaFinanceiraFPSimulacao> itensFichaSimulacao) {
        this.itensFichaSimulacao = itensFichaSimulacao;
    }

    public FichaFinanceiraFPSimulacao getFichaFinanceiraFPSimulacao() {
        return fichaFinanceiraFPSimulacao;
    }

    public void setFichaFinanceiraFPSimulacao(FichaFinanceiraFPSimulacao fichaFinanceiraFPSimulacao) {
        this.fichaFinanceiraFPSimulacao = fichaFinanceiraFPSimulacao;
    }

    public void gerarRelatorio() {

        relatorioFichaFinanceira.setVinculoFPSelecionado(contratoFPSelecionado);
        relatorioFichaFinanceira.setAno(ano);
        relatorioFichaFinanceira.setMes(Integer.parseInt(mes));
        relatorioFichaFinanceira.setMesFinal(Integer.parseInt(mes));
        relatorioFichaFinanceira.setTipoFolhaDePagamento(tipoFolhaDePagamento);
        relatorioFichaFinanceira.gerarRelatorio("PDF");

    }

    public void gerarFolhaParaAFicha() {
        calculoFolhaDePagamentoControlador.novo();
        calculoFolhaDePagamentoControlador.setOperacoes(Operacoes.EDITAR);
        FiltroFolhaDePagamentoDTO filtro = new FiltroFolhaDePagamentoDTO();
        filtro.setVinculoFP(contratoFPSelecionado);
        calculoFolhaDePagamentoControlador.setFiltro(filtro);
        calculoFolhaDePagamentoControlador.setSelecionado(fichaFinanceiraFP.getFolhaDePagamento());
        logger.debug("Iniciando o calculo individual a partir da tela de Consulta Ficha Financeira Mês/Ano {} Servidor {}.", mes + "/" + ano, contratoFPSelecionado);
        FacesUtil.addInfo("Atenção", "O servidor selecionado foi enviado para o cálculo, em alguns segundos realize a consulta novamente.");
        calculoFolhaDePagamentoControlador.calcularFolha();
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null && contratoFPSelecionado != null) {
            retorno.add(new SelectItem(null, "TODAS"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersaoWithIntevaloMes(Mes.getMesToInt(Integer.parseInt(mes)), ano, tipoFolhaDePagamento, Mes.getMesToInt(Integer.parseInt(mes)), contratoFPSelecionado)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public CalculoFolhaDePagamentoControlador getCalculoFolhaDePagamentoControlador() {
        return calculoFolhaDePagamentoControlador;
    }

    public void setCalculoFolhaDePagamentoControlador(CalculoFolhaDePagamentoControlador calculoFolhaDePagamentoControlador) {
        this.calculoFolhaDePagamentoControlador = calculoFolhaDePagamentoControlador;
    }

    public Boolean incidenciaINSS(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.BASE_PREVIDENCIA_GERAL_MENSAL, IdentificadorBaseFP.BASE_PREVIDENCIA_GERAL_13);
    }

    public Boolean incidenciaIRRF(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.BASE_IRRF_13, IdentificadorBaseFP.BASE_IRRF_MENSAL);
    }

    public Boolean incidenciaRPPS(EventoFP eventoFP) {
        if (TipoFolhaDePagamento.isFolha13Salario(tipoFolhaDePagamento) || TipoFolhaDePagamento.isFolhaAdiantamento13Salario(tipoFolhaDePagamento)) {
            return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.PREVIDENCIA_PROPRIA_13);
        }
        if (TipoFolhaDePagamento.isFolhaRescisao(tipoFolhaDePagamento)) {
            return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.PREVIDENCIA_PROPRIA_13, IdentificadorBaseFP.PREVIDENCIA_PROPRIA);
        }
        return contratoFPFacade.getEventoFPFacade().incidenciaPorIdentificador(eventoFP, IdentificadorBaseFP.PREVIDENCIA_PROPRIA);
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public boolean verificarEfetivacaoDoServidor() {
        return getFichaFinanceiraFP() != null && getFichaFinanceiraFP().getFolhaDePagamento().getEfetivadaEm() == null;
    }

    public String getSituacaoCreditoSalario() {
        if ((listaItemFichaFinanceiraFP != null && !listaItemFichaFinanceiraFP.isEmpty()) && listaItemFichaFinanceiraFP.get(0).getFichaFinanceiraFP().getCreditoSalarioPago()) {
            return "SIM";
        }
        return "NÃO";
    }
}
