/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaDAM;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoDePagamentoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "consultaDAMControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaDam", pattern = "/tributario/contacorrente/consultadam/", viewId = "/faces/tributario/contacorrente/consultadam/consulta.xhtml"),
})
public class ConsultaDAMControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaDAMControlador.class);
    @EJB
    private ConsultaDAMFacade consultaDAMFacade;
    private ConverterAutoComplete converterPessoa, converterCadastroImobiliario, converterCadastroEconomico, converterCadastroRendasPatrimoniais, converterCadastroRural, converterExercicio;
    private List<DAM> dams;
    private FiltroConsultaDAM filtroConsultaDAM;
    private DAM dam;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CalculoFacade calculoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    private List<ItemLoteBaixa> itemLoteBaixas;
    private List<ItemProcessoDebito> itemProcessoDebitos;
    private List<PagamentoAvulso> pagamentosAvulsos;
    private RelatoriosDAM relatoriosDAM;
    private Map<ValorDivida, Cadastro> cadastroPorValordivida;
    private Map<ValorDivida, String> pessoasPorValordivida;
    @EJB
    private DividaFacade dividaFacade;
    private List<HistoricoSituacaoDAM> historicoSituacoes;
    private List<OcorrenciaPix> ocorrencias;
    @EJB
    private PagamentoInternetBankingFacade pagamentoInternetBankingFacade;

    public ConsultaDAMControlador() {
        super();
        relatoriosDAM = new RelatoriosDAM();
    }

    @URLAction(mappingId = "novaConsultaDam", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroConsultaDAM = new FiltroConsultaDAM();
        dam = new DAM();
        limparTodosFiltros();
    }

    public FiltroConsultaDAM getFiltroConsultaDAM() {
        return filtroConsultaDAM;
    }

    public void setFiltroConsultaDAM(FiltroConsultaDAM filtroConsultaDAM) {
        this.filtroConsultaDAM = filtroConsultaDAM;
    }

    public List<DAM> getDams() {
        return dams;
    }

    public void setDams(List<DAM> dams) {
        this.dams = dams;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return consultaDAMFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, consultaDAMFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return consultaDAMFacade.getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, consultaDAMFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return consultaDAMFacade.getCadastroRuralFacade().listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, consultaDAMFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return consultaDAMFacade.getContratoRendasPatrimoniaisFacade().listaFiltrando(parte.trim(), "numeroContrato");
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniais.class, consultaDAMFacade.getContratoRendasPatrimoniaisFacade());
        }
        return converterCadastroRendasPatrimoniais;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return consultaDAMFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, consultaDAMFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return consultaDAMFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, consultaDAMFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoesDam() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (DAM.Situacao situacao : DAM.Situacao.values()) {
            toReturn.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return toReturn;
    }

    public RelatoriosDAM getRelatoriosDAM() {
        return relatoriosDAM;
    }

    public void limpaCadastroPessoa() {
        filtroConsultaDAM.setCadastro(null);
        filtroConsultaDAM.setPessoa(null);
    }

    public void limparTodosFiltros() {
        filtroConsultaDAM.setAnoDAM(null);
        filtroConsultaDAM.setNumeroDAM(null);
        filtroConsultaDAM.setCodigoLote(null);
        filtroConsultaDAM.setCodigoBarra(new String[8]);
        dams = null;
        limpaCadastroPessoa();
    }

    public void pesquisar() {
        try {
            validarPesquisaDAM();
            if (filtroConsultaDAM.getDividasSeleciondas().isEmpty() && filtroConsultaDAM.getDivida() != null && filtroConsultaDAM.getDivida().getId() != null) {
                filtroConsultaDAM.addDivida();
            }
            dams = consultaDAMFacade.listaDamPorFiltroETipoDeCadastroNumeroDAM(filtroConsultaDAM);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarPesquisaDAM() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroConsultaDAM.getTipoCadastroTributario() != null) {
            if (filtroConsultaDAM.getCadastro() == null && filtroConsultaDAM.getPessoa() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o filtro para " + filtroConsultaDAM.getTipoCadastroTributario().getDescricaoLonga());
            }
        } else {
            if ((filtroConsultaDAM.getNumeroDAM() == null || filtroConsultaDAM.getNumeroDAM() <= 0) &&
                (filtroConsultaDAM.getCodigoLote() == null || filtroConsultaDAM.getCodigoLote().trim().isEmpty()) &&
                (filtroConsultaDAM.getDataPagamento() == null || filtroConsultaDAM.getDataPagamento().toString().isEmpty()) &&
                (filtroConsultaDAM.getDataMovimento() == null || filtroConsultaDAM.getDataMovimento().toString().isEmpty()) &&
                !hasCodigoBarrasInformado()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para realizar a consulta utilize pelo menos um dos seguintes filtros: Tipo de Cadastro, " +
                    "Número do DAM, Data de Pagamento, Data de Movimento, Código do Lote ou Código de Barras.");
            }
        }
        ve.lancarException();
    }

    private boolean hasCodigoBarrasInformado() {
        return (filtroConsultaDAM.getCodigoBarra()[0].length() == 11
            && filtroConsultaDAM.getCodigoBarra()[1].length() == 1
            && filtroConsultaDAM.getCodigoBarra()[2].length() == 11
            && filtroConsultaDAM.getCodigoBarra()[3].length() == 1
            && filtroConsultaDAM.getCodigoBarra()[4].length() == 11
            && filtroConsultaDAM.getCodigoBarra()[5].length() == 1
            && filtroConsultaDAM.getCodigoBarra()[6].length() == 11
            && filtroConsultaDAM.getCodigoBarra()[7].length() == 1);
    }

    public List<ItemLoteBaixa> getItemLoteBaixas() {
        return itemLoteBaixas;
    }

    public void setItemLoteBaixas(List<ItemLoteBaixa> itemLoteBaixas) {
        this.itemLoteBaixas = itemLoteBaixas;
    }

    public List<ItemProcessoDebito> getItemProcessoDebitos() {
        return itemProcessoDebitos;
    }

    public void setItemProcessoDebitos(List<ItemProcessoDebito> itemProcessoDebitos) {
        this.itemProcessoDebitos = itemProcessoDebitos;
    }

    public List<PagamentoAvulso> getPagamentosAvulsos() {
        return pagamentosAvulsos;
    }

    public void setPagamentosAvulsos(List<PagamentoAvulso> pagamentosAvulsos) {
        this.pagamentosAvulsos = pagamentosAvulsos;
    }

    public List<HistoricoSituacaoDAM> getHistoricoSituacoes() {
        return historicoSituacoes;
    }

    public void setHistoricoSituacoes(List<HistoricoSituacaoDAM> historicoSituacoes) {
        this.historicoSituacoes = historicoSituacoes;
    }

    public List<OcorrenciaPix> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaPix> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public boolean damPago() {
        return DAM.Situacao.PAGO.equals(dam.getSituacao());
    }

    public void atribuirDAM(DAM dam) {
        this.dam = damFacade.recuperar(dam.getId());
        if (this.dam.getPix() == null) {
            ocorrencias = consultaDAMFacade.buscarOcorrenciasPix(dam.getId());
        }
        itemLoteBaixas = consultaDAMFacade.getLoteBaixaFacade().itensLoteBaixaPorDam(dam);
        preencherItemLoteBaixasPorPix(dam);
        preencherItemLoteBaixasPorPagamentoInternetBanking(dam);

        itemProcessoDebitos = Lists.newArrayList();
        pagamentosAvulsos = Lists.newArrayList();
        historicoSituacoes = damFacade.buscarHistoricoDeSituacoesDam(dam.getId());

        if (itemLoteBaixas.isEmpty()) {
            itemProcessoDebitos = consultaDAMFacade.itensProcessoDebitoPorDam(dam);
        }
        if (itemLoteBaixas.isEmpty() && itemProcessoDebitos.isEmpty()) {
            pagamentosAvulsos = consultaDAMFacade.pagamentosAvulsosPorDam(dam);
        }
        RequestContext.getCurrentInstance().update("formDetalhesDAM");
        RequestContext.getCurrentInstance().execute("detalhesDAM.show()");
    }

    private void preencherItemLoteBaixasPorPix(DAM dam) {
        if (itemLoteBaixas.isEmpty() && dam.getPix() != null) {
            if (dam.getPix().getValorPago() != null &&
                dam.getPix().getValorPago().compareTo(new BigDecimal("0")) > 0) {
                ItemLoteBaixa ilbPix = new ItemLoteBaixa();
                LoteBaixa lbPix = new LoteBaixa();
                lbPix.setFormaPagamento(LoteBaixa.FormaPagamento.NORMAL);
                lbPix.setDataPagamento(dam.getPix().getDataPagamento());
                ilbPix.setDam(dam);
                ilbPix.setValorPago(dam.getPix().getValorPago());
                ilbPix.setDataPagamento(dam.getPix().getDataPagamento());
                ilbPix.setLoteBaixa(lbPix);
                itemLoteBaixas.add(ilbPix);
            }
        }
    }

    private void preencherItemLoteBaixasPorPagamentoInternetBanking(DAM dam) {
        if (!itemLoteBaixas.isEmpty()) return;
        PagamentoInternetBanking pagamentoInternetBanking = pagamentoInternetBankingFacade.buscarPagamentoPorDam(dam);
        if (pagamentoInternetBanking == null) return;
        ItemLoteBaixa itemLoteBaixa = new ItemLoteBaixa();
        LoteBaixa loteBaixa = new LoteBaixa();
        loteBaixa.setFormaPagamento(LoteBaixa.FormaPagamento.NORMAL);
        loteBaixa.setDataPagamento(pagamentoInternetBanking.getDataPagamento());
        loteBaixa.setTipoDePagamentoBaixa(TipoDePagamentoBaixa.INTERNET_BANKING);
        itemLoteBaixa.setDam(dam);
        itemLoteBaixa.setValorPago(pagamentoInternetBanking.getValorPago());
        itemLoteBaixa.setDataPagamento(pagamentoInternetBanking.getDataPagamento());
        itemLoteBaixa.setDataCredito(pagamentoInternetBanking.getDataCredito());
        itemLoteBaixa.setLoteBaixa(loteBaixa);
        itemLoteBaixas.add(itemLoteBaixa);
    }

    public Cadastro recuperaCadastro(ValorDivida valorDivida) {
        if (cadastroPorValordivida == null) {
            cadastroPorValordivida = Maps.newHashMap();
        }
        if (cadastroPorValordivida.containsKey(valorDivida)) {
            return cadastroPorValordivida.get(valorDivida);
        }
        Calculo c = recuperaCalculo(valorDivida);
        if (c != null && c.getCadastro() != null) {
            cadastroPorValordivida.put(valorDivida, cadastroFacade.recuperar(c.getCadastro().getId()));
        }
        return cadastroPorValordivida.get(valorDivida);
    }

    public Calculo recuperaCalculo(ValorDivida valorDivida) {
        return calculoFacade.retornaCalculoDoValorDivida(valorDivida);
    }

    public String recuperaPessoas(ValorDivida valorDivida) {
        if (pessoasPorValordivida == null) {
            pessoasPorValordivida = Maps.newHashMap();
        }
        if (cadastroPorValordivida.containsKey(valorDivida)) {
            return pessoasPorValordivida.get(valorDivida);
        }
        Calculo c = recuperaCalculo(valorDivida);
        StringBuilder pessoas = new StringBuilder();
        if (c != null) {
            for (CalculoPessoa cp : c.getPessoas()) {
                pessoas.append(cp.getPessoa().getNomeCpfCnpj());
                pessoas.append("\n");
            }
        }
        pessoasPorValordivida.put(valorDivida, pessoas.toString());
        return pessoasPorValordivida.get(valorDivida);
    }

    public Date recuperaDataPagamentoDAM() {
        if (this.dam != null && this.dam.getId() != null && this.dam.getSituacao().equals(DAM.Situacao.PAGO)) {
            Date dataPagamentoDAM = damFacade.recuperaDataPagamentoDAM(this.dam);
            if (dataPagamentoDAM == null && dam.getPix() != null && dam.getPix().getDataPagamento() != null) {
                return dam.getPix().getDataPagamento();
            }
            return dataPagamentoDAM;
        }
        return null;
    }

    public String getNomeClasse() {
        if (filtroConsultaDAM.getTipoCadastroTributario() != null) {
            switch (filtroConsultaDAM.getTipoCadastroTributario()) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {
            filtroConsultaDAM.setCadastro((Cadastro) obj);
        } else if (obj instanceof Pessoa) {
            filtroConsultaDAM.setPessoa((Pessoa) obj);
        }
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (filtroConsultaDAM != null) {
            if (filtroConsultaDAM.getTipoCadastroTributario() != null) {
                switch (filtroConsultaDAM.getTipoCadastroTributario()) {
                    case IMOBILIARIO:
                        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                    case ECONOMICO:
                        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                    case PESSOA:
                        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
                }
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public List<HistoricoImpressaoDAMSemParcela> getHistorico() {
        List<HistoricoImpressaoDAM> historicoParcelas = damFacade.listaHistoricoImpressaoDam(dam);
        List<HistoricoImpressaoDAMSemParcela> historicoDAMs = Lists.newArrayList();
        for (HistoricoImpressaoDAM historicoParcela : historicoParcelas) {
            historicoDAMs.add(new HistoricoImpressaoDAMSemParcela(historicoParcela.getDataOperacao(), historicoParcela.getUsuarioSistema(), historicoParcela.getTipoImpressao()));
        }
        Collections.sort(historicoDAMs);
        return historicoDAMs;
    }


    public String numeroDoArquivoBancario(LoteBaixa loteBaixa) {
        return consultaDAMFacade.getLoteBaixaFacade().retornaNumeroArquivoLote(loteBaixa);
    }

    public String sequenciaParcela(ParcelaValorDivida pvd) {
        return damFacade.getNumeroParcela(pvd);
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        List<Divida> dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
        for (Divida divida : dividas) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    public class RelatoriosDAM extends AbstractReport {


        public void imprimirDetalhamento() {
            try {
                String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF") + "/report/";
                HashMap parameters = new HashMap();
                parameters.put("SUBREPORT_DIR", subReport);
                parameters.put("BRASAO", getCaminhoImagem());
                parameters.put("USUARIO", SistemaFacade.obtemLogin());
                parameters.put("IDS_DAM", dam.getId().toString());
                this.setGeraNoDialog(true);
                gerarRelatorio("DetalhamentoDAM.jasper", parameters);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public String infoTipoPagamento(LoteBaixa loteBaixa) {
        if (loteBaixa.getTipoDePagamentoBaixa() == null) {
            return "PIX";
        }
        String retorno = loteBaixa.getTipoDePagamentoBaixa().getDescricao();
        if(TipoDePagamentoBaixa.BAIXA_ARQUIVO_RETORNO.equals(loteBaixa.getTipoDePagamentoBaixa()) && !loteBaixa.getArquivosLoteBaixa().isEmpty()) {
            if (loteBaixa.getArquivosLoteBaixa() != null &&
                !loteBaixa.getArquivosLoteBaixa().isEmpty() &&
                loteBaixa.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa() != null &&
                loteBaixa.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa().getTipoArquivoBancarioTributario() != null) {
                retorno += " (" + loteBaixa.getArquivosLoteBaixa().get(0).getArquivoLoteBaixa().getTipoArquivoBancarioTributario().name() + ")";
            }
        }
        return retorno;
    }

    public static class HistoricoImpressaoDAMSemParcela implements Comparable<HistoricoImpressaoDAMSemParcela> {
        @Temporal(TemporalType.TIMESTAMP)
        private Date dataImpressao;
        private UsuarioSistema usuarioSistema;
        private HistoricoImpressaoDAM.TipoImpressao tipoImpressao;

        public HistoricoImpressaoDAMSemParcela(Date dataImpressao, UsuarioSistema usuarioSistema, HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
            this.dataImpressao = dataImpressao;
            this.usuarioSistema = usuarioSistema;
            this.tipoImpressao = tipoImpressao;
        }

        public Date getDataImpressao() {
            return dataImpressao;
        }

        public void setDataImpressao(Date dataImpressao) {
            this.dataImpressao = dataImpressao;
        }

        public UsuarioSistema getUsuarioSistema() {
            return usuarioSistema;
        }

        public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
            this.usuarioSistema = usuarioSistema;
        }

        public HistoricoImpressaoDAM.TipoImpressao getTipoImpressao() {
            return tipoImpressao;
        }

        public void setTipoImpressao(HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
            this.tipoImpressao = tipoImpressao;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HistoricoImpressaoDAMSemParcela)) return false;

            HistoricoImpressaoDAMSemParcela that = (HistoricoImpressaoDAMSemParcela) o;

            if (dataImpressao != null ? !dataImpressao.equals(that.dataImpressao) : that.dataImpressao != null)
                return false;
            if (tipoImpressao != that.tipoImpressao) return false;
            if (usuarioSistema != null ? !usuarioSistema.equals(that.usuarioSistema) : that.usuarioSistema != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = dataImpressao != null ? dataImpressao.hashCode() : 0;
            result = 31 * result + (usuarioSistema != null ? usuarioSistema.hashCode() : 0);
            result = 31 * result + (tipoImpressao != null ? tipoImpressao.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(HistoricoImpressaoDAMSemParcela o) {
            int i = getDataImpressao() != null ? this.getDataImpressao().compareTo(o.getDataImpressao()) : 0;
            if (i == 0) {
                i = getUsuarioSistema() != null ? this.getUsuarioSistema().compareTo(o.getUsuarioSistema()) : 0;
            }
            if (i == 0) {
                i = getTipoImpressao() != null ? this.getTipoImpressao().compareTo(o.getTipoImpressao()) : 0;
            }
            return i;
        }
    }

}
