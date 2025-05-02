package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by israeleriston
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioVerbasCargo", pattern = "/relatorio-verbas-cargo-detalhado/",
        viewId = "/faces/rh/relatorios/relatorioverbascargo.xhtml")
})
public class RelatorioVerbasCargoControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioVerbasCargoControlador.class);


    private SistemaService sistemaService;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private ConverterAutoComplete converterAutoCompleteHierarquia;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Mes mes;
    private String ano;
    private TipoPCS tipoPCS;
    private TipoFolhaDePagamento tipoFolhaPagamento;
    private TipoEventoFP tipoEventoFp;
    private String filtro;
    private Integer versao;

    @PostConstruct
    public void init() {
        sistemaService = (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
    }

    @URLAction(mappingId = "relatorioVerbasCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        mes = null;
        ano = null;
        tipoPCS = null;
        tipoFolhaPagamento = null;
        tipoEventoFp = null;
    }

    public List<SelectItem> tipoPcs() {
        return Util.getListSelectItem(TipoPCS.values());
    }

    public List<SelectItem> tipoEvento() {
        return Util.getListSelectItem(TipoEventoFP.values());
    }

    public List<SelectItem> tipoFolhaPagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values());
    }

    public List<SelectItem> meses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (Mes m : Mes.values()) {
            retorno.add(new SelectItem(m, m.getDescricao()));
        }

        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String filtro) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(
            filtro.trim(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            getData());
    }

    public Date getData() {
        if (mes != null && !ano.trim().isEmpty()) {
            return DataUtil.montaData(1, mes.getNumeroMes(), Integer.valueOf(ano)).getTime();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public void limparOrgao() {
        setHierarquiaOrganizacional(null);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConverterAutoComplete getConverterAutoCompleteHierarquia() {
        if (converterAutoCompleteHierarquia == null) {
            converterAutoCompleteHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterAutoCompleteHierarquia;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public void setConverterAutoCompleteHierarquia(ConverterAutoComplete converterAutoCompleteHierarquia) {
        this.converterAutoCompleteHierarquia = converterAutoCompleteHierarquia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoEventoFP getTipoEventoFp() {
        return tipoEventoFp;
    }

    public void setTipoEventoFp(TipoEventoFP tipoEventoFp) {
        this.tipoEventoFp = tipoEventoFp;
    }

    public TipoFolhaDePagamento getTipoFolhaPagamento() {
        return tipoFolhaPagamento;
    }

    public void setTipoFolhaPagamento(TipoFolhaDePagamento tipoFolhaPagamento) {
        this.tipoFolhaPagamento = tipoFolhaPagamento;
    }

    public TipoPCS getTipoPCS() {
        return tipoPCS;
    }

    public void setTipoPCS(TipoPCS tipoPCS) {
        this.tipoPCS = tipoPCS;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado");
        }
        if (ano == null || ano.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado");
        }
        if (mes == null || mes.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado");
        }
        if (tipoPCS == null || tipoPCS.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo PCS deve ser informado");
        }
        if (tipoEventoFp == null || tipoEventoFp.toString().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Evento deve ser informado");
        }
        if (tipoFolhaPagamento == null || tipoFolhaPagamento.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Folha Pagamento deve ser informado");
        }
        ve.lancarException();
    }

    public String montarFiltro() {
        setFiltro("");
        filtro += "Mês: " + mes.getDescricao() + "; ";
        filtro += "Ano: " + ano + "; ";
        filtro += "Tipo Folha de Pagamento: " + tipoFolhaPagamento.getDescricao() + "; ";
        filtro += versao != null ? "Versão: " + versao + "; " : "Versão: Todas as Versões; ";
        filtro += "Órgão: " + hierarquiaOrganizacional.getCodigoSemZerosFinais().substring(0,
            hierarquiaOrganizacional.getCodigoSemZerosFinais().length() - 1) + "; ";
        filtro += "Tipo PCS: " + tipoPCS.getDescricao() + "; ";
        filtro += "Tipo EventoFP: " + tipoEventoFp.toString() + " ";
        return filtro;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-verbas-cargo-detalhado/");
            dto.setNomeRelatorio("relatorio-verbas-cargo-detalhado");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("TIPOPCS", tipoPCS.name());
            dto.adicionarParametro("TIPOFOLHAPAGAMENTO", tipoFolhaPagamento.name());
            dto.adicionarParametro("TIPOEVENTOFP", tipoEventoFp.name());
            dto.adicionarParametro("MES", mes.getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacional.getDescricao().toUpperCase());
            dto.adicionarParametro("CODIGOORGAO", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("FILTRO", montarFiltro());
            dto.adicionarParametro("USUARIO", sistemaService.getUsuarioCorrente().toString());
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("VERSAO", versao);
            ReportService.getInstance().gerarRelatorio(sistemaService.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório Relatorio de verbas por cargo", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaPagamento != null) {
            retorno.add(new SelectItem(null, ""));
            for (Integer versao : getFolhaDePagamentoFacade().recuperarVersao(mes, Integer.valueOf(ano), tipoFolhaPagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

}
