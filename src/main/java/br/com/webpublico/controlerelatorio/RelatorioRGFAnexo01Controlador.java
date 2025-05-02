package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RGFAnexo01Pessoal;
import br.com.webpublico.enums.*;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo01Facade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRelatorioContabil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 23/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo1", pattern = "/relatorio/rgf/anexo1/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo01.xhtml")})
public class RelatorioRGFAnexo01Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;
    private BigDecimal percentual;
    private BigDecimal percentualPrudencial;
    private BigDecimal percentualLimite;
    private BigDecimal percentualDespesa;
    private BigDecimal limiteMaximo;
    private BigDecimal limitePrudencial;
    private BigDecimal limiteAlerta;
    private BigDecimal despesaTotalComPessoal;

    public RelatorioRGFAnexo01Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo1", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.percentual = BigDecimal.ZERO;
        this.percentualPrudencial = BigDecimal.ZERO;
        this.percentualDespesa = BigDecimal.ZERO;
        this.limiteMaximo = BigDecimal.ZERO;
        this.limitePrudencial = BigDecimal.ZERO;
        this.despesaTotalComPessoal = BigDecimal.ZERO;

        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        portalTipoAnexo = PortalTipoAnexo.ANEXO1_RGF;
        super.limparCampos();
    }

    public BigDecimal buscarDespesaTotalComPessoa(List<ParametrosRelatorios> parametros) {
        instanciarItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setParametros(parametros);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo01Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        despesaTotalComPessoal = BigDecimal.ZERO;
        buscarDadosGrupoUm(null);
        return despesaTotalComPessoal;
    }

    private List<RGFAnexo01Pessoal> buscarDadosGrupoUm(HashMap parameters) {
        List<RGFAnexo01Pessoal> toReturn = new ArrayList<>();
        relatoriosItemDemonst = relatorioRGFAnexo01Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        relatorioRGFAnexo01Facade.limparHashItensRecuperados();
        for (ItemDemonstrativoComponente item : itens) {
            if (item.getGrupo() == 1) {
                instanciarItemDemonstrativoFiltros();
                if (parameters != null) {
                    parameters.put("MES", Mes.getMesToInt(Integer.valueOf(itemDemonstrativoFiltros.getDataFinal().substring(3, 5))).getDescricao().substring(0, 3).toUpperCase() + "/" + itemDemonstrativoFiltros.getDataFinal().substring(6, 10));
                }
                ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo01Facade.recuperarItemDemonstrativoPeloNomeERelatorio(item.getDescricao(), exercicio, relatoriosItemDemonst);
                RGFAnexo01Pessoal it = new RGFAnexo01Pessoal();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setMesAtual(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-1", parameters);
                it.setMesMenosUm(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-2", parameters);
                it.setMesMenosDois(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-3", parameters);
                it.setMesMenosTres(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-4", parameters);
                it.setMesMenosQuatro(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-5", parameters);
                it.setMesMenosCinco(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-6", parameters);
                it.setMesMenosSeis(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-7", parameters);
                it.setMesMenosSete(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-8", parameters);
                it.setMesMenosOito(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-9", parameters);
                it.setMesMenosNove(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-10", parameters);
                it.setMesMenosDez(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-11", parameters);
                it.setMesMenosOnze(calcularLiquidacao(itemDemonstrativo));
                if (Mes.DEZEMBRO.equals(mes)) {
                    it.setInscritasRestos(relatorioRGFAnexo01Facade.calcularRestos(itemDemonstrativo,
                        exercicio,
                        "01/01/" + exercicio.getAno(),
                        Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno(),
                        "",
                        esferaDoPoder == null ? "" : " AND VW.esferadopoder = '" + esferaDoPoder.name() + "'",
                        itemDemonstrativoFiltros.getRelatorio()));
                } else {
                    instanciarItemDemonstrativoFiltros();
                    itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
                    it.setInscritasRestos(calcularInscricaoRestos(itemDemonstrativo));
                }
                it.setTotalUltimosMeses(it.getMesMenosOnze().add(it.getMesMenosDez()).add(it.getMesMenosNove()).add(it.getMesMenosOito()).add(it.getMesMenosSete()).add(
                    it.getMesMenosSeis()).add(it.getMesMenosCinco()).add(it.getMesMenosQuatro()).add(it.getMesMenosTres()).add(it.getMesMenosDois()).add(it.getMesMenosUm()).add(it.getMesAtual()));
                if (item.getOrdem() == 16) {
                    despesaTotalComPessoal = it.getTotalUltimosMeses().add(it.getInscritasRestos());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private void instanciarItemDemonstrativoFiltros() {
        relatoriosItemDemonst = relatorioRGFAnexo01Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        itemDemonstrativoFiltros.setDataInicial("01/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataReferencia(Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setParametros(criarParametros(null, 1));
    }

    private List<ParametrosRelatorios> criarParametros(RelatorioDTO dto, int local) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (esferaDoPoder != null) {
            ParametrosRelatorios parametroEsferaDoPoder = new ParametrosRelatorios(" vw.esferadopoder ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, local, false);
            if (dto != null) {
                dto.adicionarParametro("parametroEsferaDoPoder", ParametrosRelatorios.parametroToDto(parametroEsferaDoPoder));
            }
            parametros.add(parametroEsferaDoPoder);
        }
        return parametros;
    }

    private BigDecimal calcularLiquidacao(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo01Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularInscricaoRestos(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo01Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_INSCRITOS_NAO_PROCESSADOS);
    }

    private void removerUmMes(String data, String parametroMes, HashMap parameters) {
        data = Util.formatterMesAno.format(DataUtil.getDateParse(data));
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 0 && mes != 1) {
            mes -= 2;
        } else {
            mes = 11;
            ano -= 1;
        }
        itemDemonstrativoFiltros.setDataInicial(DataUtil.getDataFormatada(DataUtil.montaData(
            1, mes, ano).getTime()));
        itemDemonstrativoFiltros.setDataFinal(DataUtil.getDataFormatada(DataUtil.montaData(
            DataUtil.getDiasNoMes((mes + 1), ano), mes, ano).getTime()));
        if (ano < exercicio.getAno() && ano < itemDemonstrativoFiltros.getExercicio().getAno()) {
            itemDemonstrativoFiltros.setExercicio(getExercicioFacade().getExercicioPorAno(ano));
        }
        if (parameters != null) {
            parameters.put(parametroMes, Mes.getMesToInt((mes + 1)).getDescricao().substring(0, 3).toUpperCase() + "/" + ano);
        }
    }

    @Override
    public void acoesExtrasAoGerarOuSalvar() {
        instanciarItemDemonstrativoFiltros();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        if (esferaDoPoder != null) {
            itemDemonstrativoFiltros.getParametros().addAll(criarParametros(dto, 9));
        }
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(54);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(6);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
        } else {
            percentual = BigDecimal.valueOf(60);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DESPESA COM PESSOAL");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        }
        dto.adicionarParametro("PERCENTUAL", percentual);
        dto.adicionarParametro("PERCENTUALPRUD", percentualPrudencial);
        dto.adicionarParametro("PERCENTUALALERTA", percentualLimite);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("isDezembro", Mes.DEZEMBRO.equals(mes));
        dto.adicionarParametro("mesFinal", mes.getToDto());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("idExercicioAnterior", getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1).getId());
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("DATAINICIAL", getMesInicialParaCabecalho().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", mes.getDescricao().toUpperCase() + " DE " + exercicio.getAno());
        dto.adicionarParametro("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        RelatoriosItemDemonst relatorioAnexo03 = relatorioRGFAnexo01Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("LRF-RGF-ANEXO-1-" + exercicio.getAno() + "-" + mes.getNumeroMesString() + "-" + (esferaDoPoder != null ? esferaDoPoder.getDescricao().substring(0, 1) : "C"));
        UtilRelatorioContabil.adicionarItemDemonstrativoFiltrosCampoACampo(dto, itemDemonstrativoFiltros);
        salvarNotaExplicativaRGF();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRGFAnexo01";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo1/";
    }

    private Mes getMesInicialParaCabecalho() {
        if (Mes.ABRIL.equals(mes)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mes)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getPercentualPrudencial() {
        return percentualPrudencial;
    }

    public void setPercentualPrudencial(BigDecimal percentualPrudencial) {
        this.percentualPrudencial = percentualPrudencial;
    }

    public BigDecimal getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(BigDecimal limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }

    public BigDecimal getLimitePrudencial() {
        return limitePrudencial;
    }

    public void setLimitePrudencial(BigDecimal limitePrudencial) {
        this.limitePrudencial = limitePrudencial;
    }

    public BigDecimal getPercentualDespesa() {
        return percentualDespesa;
    }

    public void setPercentualDespesa(BigDecimal percentualDespesa) {
        this.percentualDespesa = percentualDespesa;
    }

    public BigDecimal getDespesaTotalComPessoal() {
        return despesaTotalComPessoal;
    }

    public void setDespesaTotalComPessoal(BigDecimal despesaTotalComPessoal) {
        this.despesaTotalComPessoal = despesaTotalComPessoal;
    }

    public BigDecimal getPercentualLimite() {
        return percentualLimite;
    }

    public void setPercentualLimite(BigDecimal percentualLimite) {
        this.percentualLimite = percentualLimite;
    }

    public BigDecimal getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(BigDecimal limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }
}

