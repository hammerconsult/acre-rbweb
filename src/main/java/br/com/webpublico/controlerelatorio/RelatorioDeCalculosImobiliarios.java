/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.negocios.NovoCalculoIPTUFacade;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ManagedBean(name = "relatorioDeCalculosImobiliarios")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDeCalculosImobiliarios",
        pattern = "/relatorios/relatorio-de-iptu/",
        viewId = "/faces/tributario/iptu/relatorio/relatoriodecalculosimobiliarios.xhtml"),
    @URLMapping(id = "editaRelatorioDeCalculosImobiliarios",
        pattern = "/relatorios/relatorio-de-iptu/#{relatorioDeCalculosImobiliarios.id}/",
        viewId = "/faces/tributario/iptu/relatorio/relatoriodecalculosimobiliarios.xhtml"),
})
public class RelatorioDeCalculosImobiliarios implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDeCalculosImobiliarios.class);

    private ProcessoCalculoIPTU processoCalculoIPTU;
    private Converter converterProcessoCalculo;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    @EJB
    private NovoCalculoIPTUFacade novoCalculoIPTUFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Boolean detalhado;
    private String ordenacao;
    private String login;
    private String cadastroInicial, cadastroFinal;
    private Long id;
    private BigDecimal ufm;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private ConverterExercicio converterExercicio;
    private Boolean filtrarUltimoRecalculo;
    private Boolean filtrarImoveisComIptu;
    private Boolean filtrarImoveisComTsu;
    private Boolean considerarDesconto;
    private String filtros;

    public RelatorioDeCalculosImobiliarios() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public Converter getConverterProcessoCalculo() {
        if (converterProcessoCalculo == null) {
            converterProcessoCalculo = new ConverterAutoComplete(ProcessoCalculoIPTU.class, processoCalculoIPTUFacade);
        }
        return converterProcessoCalculo;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public List<ProcessoCalculoIPTU> completaProcessoCalculo(String parte) {
        return processoCalculoIPTUFacade.listaProcessosPorDescricao(parte.trim());
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Boolean getFiltrarUltimoRecalculo() {
        return filtrarUltimoRecalculo;
    }

    public void setFiltrarUltimoRecalculo(Boolean filtrarUltimoRecalculo) {
        this.filtrarUltimoRecalculo = filtrarUltimoRecalculo;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) throws JRException, IOException {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("complementoWhere", getComplementoWhere());
            dto.adicionarParametro("filtroTributos", buscarFiltrosTributo());
            dto.adicionarParametro("filtroTributosProcesso", buscarFiltrosTributoProcesso());
            dto.adicionarParametro("detalhado", getDetalhado());
            dto.adicionarParametro("considerarDesconto", getConsiderarDesconto());
            dto.adicionarParametro("CADASTROINICIAL", cadastroInicial);
            dto.adicionarParametro("CADASTROFINAL", cadastroFinal);
            dto.adicionarParametro("EXERCICIOINICIAL", exercicioInicial.getAno());
            dto.adicionarParametro("EXERCICIOFINAL", exercicioFinal.getAno());
            if (ufm != null) {
                dto.adicionarParametro("UFM", ufm.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
            }
            dto.setNomeRelatorio("RELATÓRIO-CÁLCULO-IPTU");
            dto.setApi("tributario/relatorio-de-iptu/");
            ReportService.getInstance().gerarRelatorio(novoCalculoIPTUFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @URLAction(mappingId = "editaRelatorioDeCalculosImobiliarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void edita() {
        novo();
        processoCalculoIPTU = novoCalculoIPTUFacade.recuperar(id);
    }

    @URLAction(mappingId = "novoRelatorioDeCalculosImobiliarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        ufm = moedaFacade.recuperaValorVigenteUFM();
        cadastroInicial = "1";
        cadastroFinal = "999999999999999";
        exercicioInicial = novoCalculoIPTUFacade.getSistemaFacade().getExercicioCorrente();
        exercicioFinal = novoCalculoIPTUFacade.getSistemaFacade().getExercicioCorrente();
        filtrarUltimoRecalculo = true;
        processoCalculoIPTU = null;
        detalhado = false;
        ordenacao = " order by ci.inscricaocadastral, coalesce(construcao.descricao,'TERRITORIAL') ";
        login = SistemaFacade.obtemLogin();
        filtrarImoveisComIptu = false;
        filtrarImoveisComTsu = false;
        considerarDesconto = false;
        filtros = "";
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", novoCalculoIPTUFacade.getSistemaFacade().getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("DETALHADO", getDetalhado());
        dto.adicionarParametro("FILTROS", filtros);
        dto.adicionarParametro("EXIBIRDESCONTO", considerarDesconto);
        return dto;
    }

    public String getLogin() {
        return login;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Boolean getFiltrarImoveisComIptu() {
        return filtrarImoveisComIptu;
    }

    public void setFiltrarImoveisComIptu(Boolean filtrarImoveisComIptu) {
        this.filtrarImoveisComIptu = filtrarImoveisComIptu;
    }

    public Boolean getFiltrarImoveisComTsu() {
        return filtrarImoveisComTsu;
    }

    public void setFiltrarImoveisComTsu(Boolean filtrarImoveisComTsu) {
        this.filtrarImoveisComTsu = filtrarImoveisComTsu;
    }

    public Boolean getConsiderarDesconto() {
        return considerarDesconto;
    }

    public void setConsiderarDesconto(Boolean considerarDesconto) {
        this.considerarDesconto = considerarDesconto;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroInicial == null || cadastroInicial.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro inicial");
        }
        if (cadastroInicial == null || cadastroFinal.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro final");
        }
        if (exercicioInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial");
        }
        if (exercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final");
        }
        if (exercicioInicial != null && exercicioInicial.getAno() != null && exercicioFinal != null
            && exercicioFinal.getAno() != null && exercicioInicial.getAno().compareTo(exercicioFinal.getAno()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício inicial deve ser menor ou igual que o exercício final!");
        }
        if (exercicioInicial != null && exercicioFinal != null &&
            processoCalculoIPTU != null && processoCalculoIPTU.getExercicio() != null) {
            if (exercicioInicial.getAno() > processoCalculoIPTU.getExercicio().getAno() || exercicioFinal.getAno() < processoCalculoIPTU.getExercicio().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício inicial e o exercício final informados não contemplam o exercício do processo selecionado!");
            }
        }
        ve.lancarException();
    }

    private String buscarFiltrosTributo() {
        if (filtrarImoveisComIptu && filtrarImoveisComTsu) {
            filtros += " Filtrar Imóveis com IPTU: Sim; Filtrar Imóveis com TSU: Sim;";
            return " and tr.TIPOTRIBUTO in ('" + Tributo.TipoTributo.IMPOSTO.name() + "', '" + Tributo.TipoTributo.TAXA.name() + "')";
        } else if (filtrarImoveisComIptu) {
            filtros += " Filtrar Imóveis com IPTU: Sim;";
            return " AND NOT EXISTS " +
                "    (SELECT trib.id FROM ItemValorDivida itemVd " +
                "     INNER JOIN tributo trib ON trib.id = itemVd.tributo_id " +
                "     WHERE itemVd.valorDivida_id = vd.id " +
                "       AND trib.tipoTributo = '" + Tributo.TipoTributo.TAXA.name() + "' and coalesce(itemvd.isento,0) = 0 and coalesce(itemvd.valor,0) > 0)";
        } else if (filtrarImoveisComTsu) {
            filtros += " Filtrar Imóveis com TSU: Sim;";
            return " AND NOT EXISTS " +
                "    (SELECT trib.id FROM ItemValorDivida itemVd " +
                "     INNER JOIN tributo trib ON trib.id = itemVd.tributo_id " +
                "     WHERE itemVd.valorDivida_id = vd.id " +
                "       AND trib.tipoTributo = '" + Tributo.TipoTributo.IMPOSTO.name() + "' and coalesce(itemvd.isento,0) = 0 and coalesce(itemvd.valor,0) > 0)";
        }
        return "";
    }

    private String buscarFiltrosTributoProcesso() {
        if (filtrarImoveisComIptu && filtrarImoveisComTsu) {
            return " and tr.TIPOTRIBUTO in ('" + Tributo.TipoTributo.IMPOSTO.name() + "', '" + Tributo.TipoTributo.TAXA.name() + "')";
        } else if (filtrarImoveisComIptu) {
            return " AND NOT EXISTS\n" +
                "    (SELECT trib.id\n" +
                "    FROM eventoconfiguradoiptu eveConfig\n" +
                "    inner join eventocalculo eve on eve.id = eveConfig.eventocalculo_id\n" +
                "    INNER JOIN tributo trib ON trib.id = eve.tributo_id\n" +
                "    WHERE eveConfig.id = item.evento_id AND trib.tipoTributo  = '" + Tributo.TipoTributo.TAXA.name() + "')";
        } else if (filtrarImoveisComTsu) {
            return " AND NOT EXISTS " +
                "    (SELECT trib.id " +
                "    FROM eventoconfiguradoiptu eveConfig " +
                "    inner join eventocalculo eve on eve.id = eveConfig.eventocalculo_id " +
                "    INNER JOIN tributo trib ON trib.id = eve.tributo_id " +
                "    WHERE eveConfig.id = item.evento_id AND trib.tipoTributo  = '" + Tributo.TipoTributo.IMPOSTO.name() + "')";
        }
        return "";
    }

    private String getComplementoWhere() {
        filtros = "Filtros: ";
        if (!cadastroInicial.isEmpty()) {
            filtros += " Cadastro Inicial: " + cadastroInicial + ";";
        }
        if (!cadastroFinal.isEmpty()) {
            filtros += " Cadastro Final: " + cadastroFinal + ";";
        }
        if (exercicioInicial != null) {
            filtros += " Exerício Inicial: " + exercicioInicial.getAno() + ";";
        }
        if (exercicioFinal != null) {
            filtros += " Exercício Final: " + exercicioFinal.getAno() + ";";
        }
        String where = "";
        if (processoCalculoIPTU != null) {
            where += " and processo.id = " + processoCalculoIPTU.getId();
            filtros += " Processo de Cálculo: " + processoCalculoIPTU.getDescricao() + ";";
        } else if (filtrarUltimoRecalculo) {
            where += " and iptu.id = GETULTIMOCALCULOIPTU(cadastro.id, processo.exercicio_id)";
            filtros += " Filtrar Último Recálculo: Sim;";
        }

        if (considerarDesconto) {
            filtros += " Considerar desconto: Sim;";
        }
        return where;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(novoCalculoIPTUFacade.getConfiguracaoEventoFacade().getEventoCalculoFacade().getConsultaDebitoFacade().getExercicioFacade());
        }
        return converterExercicio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
