package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 05/09/14
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lancamento-eventos-por-servidor", pattern = "/relatorio/lancamento-eventos-por-servidor/", viewId = "/faces/rh/relatorios/relatoriolancamentoeventosporservidor.xhtml")
})
public class RelatorioLancamentoEventosPorServidorControlador extends AbstractReport implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(RelatorioLancamentoEventosPorServidorControlador.class);
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private VinculoFP vinculoFPSelecionado;
    private ContratoFP contratoFPSelecionado;
    private ConverterAutoComplete converterVinculoFP;
    private List<FichaFinanceiraFP> fichaFinanceiraFP;
    private Mes mes;
    private Integer ano;
    private Integer mesFinal;

    public RelatorioLancamentoEventosPorServidorControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    public Integer getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Integer mesFinal) {
        this.mesFinal = mesFinal;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFPHO(s.trim(), hierarquiaOrganizacionalSelecionada, sistemaControlador.getDataOperacao());
    }

    public List<FichaFinanceiraFP> getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(List<FichaFinanceiraFP> fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public List<SelectItem> getTipoDeFolhaPagamentos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperaListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(vinculoFPSelecionado, mes.getNumeroMesIniciandoEmZero(), ano, tipoFolhaDePagamento);
            validarCampos();
            DateTime dateTime = new DateTime(new Date());
            DateTime dateMes = new DateTime(getSistemaFacade().getDataOperacao());
            dateTime = dateTime.withMonthOfYear(mes.getNumeroMesIniciandoEmZero());
            dateTime = dateTime.withYear(ano);
            if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
                dateTime.withDayOfMonth(dateMes.getDayOfMonth());
            } else {
                dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
            }
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            dto.adicionarParametro("MESANO", getMesAno());
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.name());
            dto.adicionarParametro("FICHA_ID", montaIdFichas());
            dto.adicionarParametro("DATAVIGENCIA", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
            dto.adicionarParametro("ORGAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE LANÇAMENTO DE VERBAS POR SERVIDOR");
            dto.setNomeRelatorio("RELATÓRIO-DE-LANÇAMENTO-DE-VERBAS-POR-SERVIDOR");
            dto.setApi("rh/lancamento-eventos-por-servidor/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório. ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getMesAno() {
        return ((String.valueOf(mes.getNumeroMes()).length() <= 1 ? "0" + mes.getNumeroMes() : mes.getNumeroMes()) + "/" + ano);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório.");
        }
        if (vinculoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }
        if (fichaFinanceiraFP == null || fichaFinanceiraFP.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe ficha financeira para os filtros informados.");
        }
        ve.lancarException();
    }

    private String montaIdFichas() {
        String retorno = " and ficha.id in (";
        for (FichaFinanceiraFP financeiraFP : fichaFinanceiraFP) {
            retorno += financeiraFP.getId() + ",";
        }
        return retorno.substring(0, retorno.length() - 1) + ")";
    }

    @URLAction(mappingId = "relatorio-lancamento-eventos-por-servidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.mes = null;
        this.ano = null;
        this.hierarquiaOrganizacionalSelecionada = null;
        this.tipoFolhaDePagamento = null;
        this.vinculoFPSelecionado = null;
    }

    public List<SelectItem> getMesesRelatorio() {
        return Util.getListSelectItem(Mes.values(), false);
    }
}
