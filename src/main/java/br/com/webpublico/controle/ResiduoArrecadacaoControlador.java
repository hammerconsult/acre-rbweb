package br.com.webpublico.controle;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.ParcelaContaCorrenteTributaria;
import br.com.webpublico.entidadesauxiliares.FiltroResiduoArrecadacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ContaCorrenteTributariaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.OperacaoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.ParametrosRelatoriosDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-geracao-residuo-arrecadacao",
        pattern = "/tributario/conta-corrente-tributaria/consulta-geracao-residuo-arrecadacao/",
        viewId = "/faces/tributario/contacorrente/residuoarrecadacao/edita.xhtml")
})
public class ResiduoArrecadacaoControlador implements Serializable {

    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroResiduoArrecadacao filtro;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<List<ParcelaContaCorrenteTributaria>> futureConsulta;
    private Future futureGeracao;
    private List<ParcelaContaCorrenteTributaria> parcelasResiduos;
    private HashSet<ParcelaContaCorrenteTributaria> itens;

    public FiltroResiduoArrecadacao getFiltro() {
        return filtro;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public List<ParcelaContaCorrenteTributaria> getParcelasResiduos() {
        return parcelasResiduos;
    }

    public void setParcelasResiduos(List<ParcelaContaCorrenteTributaria> parcelasResiduos) {
        this.parcelasResiduos = parcelasResiduos;
    }

    @URLAction(mappingId = "consulta-geracao-residuo-arrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtro = new FiltroResiduoArrecadacao();
        parcelasResiduos = Lists.newArrayList();
        futureConsulta = null;
        itens = new HashSet<>();
        futureGeracao = null;
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }

    public void pesquisarDebitos() {
        try {
            parcelasResiduos = Lists.newArrayList();
            itens = new HashSet<>();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Consultando Débitos com Resíduo de Arrecadação");
            futureConsulta = contaCorrenteTributariaFacade.buscarParcelasResiduoArrecadacao(filtro);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futureConsulta.isDone()) {
            parcelasResiduos = futureConsulta.get();
            FacesUtil.executaJavaScript("finalizarPesquisa()");
        }
    }

    public boolean itemAdicionado(ParcelaContaCorrenteTributaria parcela) {
        return itens.contains(parcela);
    }

    public void adicionarItem(ParcelaContaCorrenteTributaria parcela) {
        itens.add(parcela);
    }

    public void removerItem(ParcelaContaCorrenteTributaria parcela) {
        itens.remove(parcela);
    }

    public BigDecimal getTotalResiduo() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelasResiduos) {
            total = total.add(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
        }
        return total;
    }

    public BigDecimal getValorResiduoUtilizado() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelasResiduos) {
            if (parcelaContaCorrenteTributaria.getCalculoContaCorrente() != null) {
                total = total.add(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
            }
        }
        return total;
    }

    public BigDecimal getSaldoResiduoDisponivel() {
        BigDecimal saldo = getTotalResiduo().subtract(getValorResiduoUtilizado());
        return saldo.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void gerarDebitos() {
        try {
            if (itens.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum débito de resíduo de arrecadação foi selecionado.");
                return;
            }
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureGeracao = contaCorrenteTributariaFacade.gerarDebitoResidualArrecadacao(assistenteBarraProgresso,
                Lists.newArrayList(itens));
            parcelasResiduos = Lists.newArrayList();
            itens = new HashSet<>();
            FacesUtil.executaJavaScript("iniciarGeracaoDebitos()");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharGeracaoDebitos() throws ExecutionException, InterruptedException {
        if (futureGeracao.isDone()) {
            FacesUtil.executaJavaScript("finalizarGeracaoDebitos()");
            FacesUtil.addOperacaoRealizada("Débitos de resíduos de arrecadação gerados com sucesso!");
        }
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Relatório de Resíduo de Débitos de Arrecadação");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            adicionarParametrosRelatorio(dto);
            dto.setApi("tributario/residuo-arrecadacao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void adicionarParametrosRelatorio(RelatorioDTO dto) {
        StringBuilder filtros = new StringBuilder();
        List<ParametrosRelatoriosDTO> parametros = Lists.newArrayList();
        if (filtro.getContribuinte() != null) {
            parametros.add(new ParametrosRelatoriosDTO("ct.pessoa_id", ":pessoa_id", null,
                OperacaoRelatorioDTO.IGUAL, filtro.getContribuinte().getId(), null, 1, false));
            filtros.append("Contribuinte: ").append(filtro.getContribuinte().getNomeCpfCnpj()).append("; ");
        }
        if (filtro.getDataPagamentoInicial() != null) {
            parametros.add(new ParametrosRelatoriosDTO(
                "trunc(pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id, spvd.situacaoparcela))",
                ":data_pagamento_inicial", null,
                OperacaoRelatorioDTO.MAIOR_IGUAL, DateUtils.getDataFormatada(filtro.getDataPagamentoInicial()),
                null, 1, true));
            filtros.append("Data de Pagamento Inicial: ")
                .append(DateUtils.getDataFormatada(filtro.getDataPagamentoInicial())).append("; ");
        }
        if (filtro.getDataPagamentoFinal() != null) {
            parametros.add(new ParametrosRelatoriosDTO(
                "trunc(pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id, spvd.situacaoparcela))",
                ":data_pagamento_final", null,
                OperacaoRelatorioDTO.MENOR_IGUAL, DateUtils.getDataFormatada(filtro.getDataPagamentoFinal()),
                null, 1, true));
            filtros.append("Data de Pagamento Final: ")
                .append(DateUtils.getDataFormatada(filtro.getDataPagamentoFinal())).append("; ");
        }
        if (filtro.getSituacao() != null) {
            parametros.add(new ParametrosRelatoriosDTO("p.calculocontacorrente_id", "", null,
                filtro.getSituacao().equals("1") ? OperacaoRelatorioDTO.IS_NOT_NULL : OperacaoRelatorioDTO.IS_NULL,
                null, null, 1, false));
            filtros.append("Situação: ").append(filtro.getSituacao().equals("1") ? "Gerado" : "Não Gerado")
                .append("; ");
        } else {
            filtros.append("Situação: ").append("Todos; ");
        }
        dto.adicionarParametro("PARAMETROS", parametros);
        dto.adicionarParametro("FILTROS", filtros.toString());
    }
}
