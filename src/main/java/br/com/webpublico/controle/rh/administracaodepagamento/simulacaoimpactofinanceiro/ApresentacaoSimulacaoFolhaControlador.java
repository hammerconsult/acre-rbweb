/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.rh.administracaodepagamento.simulacaoimpactofinanceiro;

import br.com.webpublico.controlerelatorio.rh.RelatorioPagamentoRH;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.EnquadramentoPCSSimulacao;
import br.com.webpublico.entidadesauxiliares.rh.ServidorSalario;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.EnquadramentoPCSSimulacaoFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.FichaFinanceiraFPSimulacaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "apresentacao-simulacao-folha", pattern = "/apresentacao-simulacao-folha/", viewId = "/faces/rh/simulacaofolha/apresentacao/lista.xhtml")})
public class ApresentacaoSimulacaoFolhaControlador extends RelatorioPagamentoRH implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ApresentacaoSimulacaoFolhaControlador.class);

    @EJB
    private FichaFinanceiraFPSimulacaoFacade fichaFinanceiraSimulacaoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EnquadramentoPCSSimulacaoFacade enquadramentoPCSSimulacaoFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private VinculoFP contratoFPSelecionado;
    private Integer mes;
    private Integer ano;
    private ConverterAutoComplete converterContratoFP;

    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private BigDecimal totalSalarioBase = BigDecimal.ZERO;
    private BigDecimal totalSalarioBaseReajustado = BigDecimal.ZERO;
    private List<ServidorSalario> servidorSalarios;
    private List<EnquadramentoPCSSimulacao> categoriasReajustadas;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<ServidorSalario> getServidorSalarios() {
        return servidorSalarios;
    }

    public void setServidorSalarios(List<ServidorSalario> servidorSalarios) {
        this.servidorSalarios = servidorSalarios;
    }

    @URLAction(mappingId = "apresentacao-simulacao-folha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mes = null;
        ano = null;
        contratoFPSelecionado = null;
        tipoFolhaDePagamento = null;
        totalSalarioBase = BigDecimal.ZERO;
        totalSalarioBaseReajustado = BigDecimal.ZERO;
        servidorSalarios = Lists.newLinkedList();
        hierarquiaOrganizacional = null;
        categoriasReajustadas = Lists.newLinkedList();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void compararFolha() {
        try {
            validaCampos();
            servidorSalarios = fichaFinanceiraSimulacaoFPFacade.buscarServidores(mes, ano, tipoFolhaDePagamento, hierarquiaOrganizacional);
            atualizarContadores(servidorSalarios);
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro, ", e);
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    private String buscarReajustes(Integer mes, Integer ano) {
        String categorias = "";
        DateTime mesAno = DataUtil.criarDataComMesEAno(mes, ano);
        categoriasReajustadas = enquadramentoPCSSimulacaoFacade.buscarCategoriasReajustadas(mesAno.toDate());
        for (EnquadramentoPCSSimulacao categoriasReajustada : categoriasReajustadas) {
            categorias += " " + categoriasReajustada.getCategoriaPCS().toString().trim() + " - " + categoriasReajustada.getPercentualReajuste() + " %";
        }
        return categorias;
    }


    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
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
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));

        }
        return toReturn;
    }

    public void validaCampos() {
        ValidacaoException val = new ValidacaoException();
        if (ano == null) {
            val.adicionarMensagemDeCampoObrigatorio("O Campo Ano é obrigatório !");
        }
        if (mes == null || (mes != null && Objects.equals(mes, ""))) {
            val.adicionarMensagemDeCampoObrigatorio("O Campo mês é obrigatório !");
        }
        if (tipoFolhaDePagamento == null) {
            val.adicionarMensagemDeCampoObrigatorio("O Campo Tipo de Folha de Pagamento é obrigatório !");
        }
        val.lancarException();
        if (mes < 1 || mes > 12) {
            val.adicionarMensagemDeCampoObrigatorio("O mês informado é inválido !");
        }
        val.lancarException();
    }


    public void atualizarContadores(List<ServidorSalario> fichas) {
        totalSalarioBase = BigDecimal.ZERO;
        totalSalarioBaseReajustado = BigDecimal.ZERO;
        for (ServidorSalario itens : fichas) {
            totalSalarioBase = totalSalarioBase.add(itens.getSalarioBase());
            totalSalarioBaseReajustado = totalSalarioBaseReajustado.add(itens.getSalarioBaseReajustado());
        }
    }

    public void excluirSimulacao() {
        throw new NotImplementedException();
    }

    public void podeExcluir() {
        throw new NotImplementedException();
    }

    public BigDecimal getTotalSalarioBase() {
        return totalSalarioBase;
    }

    public void setTotalSalarioBase(BigDecimal totalSalarioBase) {
        this.totalSalarioBase = totalSalarioBase;
    }

    public BigDecimal getTotalSalarioBaseReajustado() {
        return totalSalarioBaseReajustado;
    }

    public void setTotalSalarioBaseReajustado(BigDecimal totalSalarioBaseReajustado) {
        this.totalSalarioBaseReajustado = totalSalarioBaseReajustado;
    }

    public Boolean incidenciaINSS(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaINSS(eventoFP);
    }

    public Boolean incidenciaIRRF(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaIRRF(eventoFP);
    }

    public Boolean incidenciaRPPS(EventoFP eventoFP) {
        return contratoFPFacade.getEventoFPFacade().incidenciaRPPS(eventoFP);
    }

    public BigDecimal getBuscarDiferenca() {
        return totalSalarioBaseReajustado.subtract(totalSalarioBase);
    }

    public void gerarRelatorio() {
        try {
            validaCampos();
            verificarServidores();
            RelatorioDTO dto = new RelatorioDTO();
            adicionarParametrosRelatorio(dto);
            dto.setNomeRelatorio("RELATORIO-APRESENTACAO-SIMULACAO-FOLHA-ANALITICO");
            dto.adicionarParametro("TIPO_RELATORIO", "ANALÍTICO");
            dto.adicionarParametro("DIFERENCA_REAJUSTE", getBuscarDiferenca());
            dto.setApi(getAPI());
            dto.adicionarParametro("sintetico", false);
            dto.adicionarParametro("servidores", ServidorSalario.servidoresToServidoresDTO(servidorSalarios));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void gerarRelatorioSintetico() {
        try {
            validaCampos();
            verificarServidores();
            RelatorioDTO dto = new RelatorioDTO();
            adicionarParametrosRelatorio(dto);
            dto.setNomeRelatorio("RELATORIO-APRESENTACAO-SIMULACAO-FOLHA-SINTÉTICO");
            dto.adicionarParametro("TIPO_RELATORIO", "SINTÉTICO");
            dto.adicionarParametro("DIFERENCA_REAJUSTE", getBuscarDiferenca());
            dto.setApi(getAPI());
            dto.adicionarParametro("sintetico", true);
            dto.adicionarParametro("servidores", ServidorSalario.servidoresToServidoresDTO(servidorSalarios));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private String getAPI(){
        return "rh/apresentacao-simulacao-folha/";
    }

    private void verificarServidores() {
        if (servidorSalarios == null || servidorSalarios.isEmpty()) {
            compararFolha();
        }
    }

    public void adicionarParametrosRelatorio(RelatorioDTO dto) {
        dto.adicionarParametro("USUARIO", getNomeUsuarioLogado());
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("TITULO", "SIMULAÇÃO DE SALÁRIO BASE");
        dto.adicionarParametro("MES", String.valueOf(getMes()));
        dto.adicionarParametro("ANO", String.valueOf(getAno()));
        dto.adicionarParametro("CATEGORIAS", buscarReajustes(mes, ano));
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
    }

    public String getNomeUsuarioLogado() {
        if (getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            return getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return getSistemaFacade().getUsuarioCorrente().getUsername();
        }
    }

    public List<EnquadramentoPCSSimulacao> getCategoriasReajustadas() {
        return categoriasReajustadas;
    }

    public void setCategoriasReajustadas(List<EnquadramentoPCSSimulacao> categoriasReajustadas) {
        this.categoriasReajustadas = categoriasReajustadas;
    }
}
