package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.rh.RelatorioPagamentoRH;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@ManagedBean(name = "relatorioServidoresPorVerbaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioServidoresPorVerba", pattern = "/relatorio/servidores-por-verba/", viewId = "/faces/rh/relatorios/relatorioservidoresporverba.xhtml")
})
public class RelatorioServidoresPorVerbaControlador extends RelatorioPagamentoRH implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private EventoFP eventoFPSelecionado;
    private TipoEventoFPFicha tipoEventoFPFicha;

    public RelatorioServidoresPorVerbaControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    @Override
    @URLAction(mappingId = "gerarRelatorioServidoresPorVerba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        eventoFPSelecionado = null;
        hierarquiaOrganizacionalSelecionada = null;
        setMes(null);
        setAno(null);
        setTipoFolhaDePagamento(null);
        setTipoEventoFPFicha(TipoEventoFPFicha.TODOS);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/relatorio-servidores-por-verba/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE SERVIDORES POR VERBA");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SERVIDORES POR VERBA");
        dto.adicionarParametro("VERSAO", getVersao() != null ? String.valueOf(getVersao()) : "Todas");
        dto.adicionarParametro("TIPOVERBA", !TipoEventoFPFicha.TODOS.equals(tipoEventoFPFicha) ? tipoEventoFPFicha.name() : eventoFPSelecionado.getTipoEventoFP().name());
        dto.adicionarParametro("DESCRICAOMESANO", Mes.getMesToInt(getMes()).getDescricao().toUpperCase() + " DE " + getAno());
        dto.adicionarParametro("DESCRICAOVERBA", eventoFPSelecionado.getCodigo() + " " + eventoFPSelecionado.getDescricao());
        dto.adicionarParametro("DATAREFERENCIA", DataUtil.montaData(1, getMes() - 1, getAno()).getTime());
        try {
            dto.adicionarParametro("DESCRICAOORGAO", hierarquiaOrganizacionalSelecionada.getCodigoNo() + " " + hierarquiaOrganizacionalSelecionada.getSuperior().getDescricao());
        } catch (NullPointerException npe) {
            dto.adicionarParametro("DESCRICAOORGAO", hierarquiaOrganizacionalSelecionada.getCodigoNo() + " " + hierarquiaOrganizacionalSelecionada.getSubordinada());
        }
        dto.adicionarParametro("ORGAO", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.adicionarParametro("TIPO_RELATORIO", "FOLHA " + getTipoFolhaDePagamento().getDescricao().toUpperCase());
        dto.adicionarParametro("COMPLEMENTO_QUERY", gerarFiltrosSql());
        dto.adicionarParametro("FILTRO_TXT_EXCEL", gerarFiltro());
        dto.adicionarParametro("DATA_POR_EXTENSO", (DataUtil.recuperarDataPorExtenso(getSistemaFacade().getDataOperacao())));
        return dto;

    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private String gerarFiltro() {
        return "Órgão: " + hierarquiaOrganizacionalSelecionada.getCodigoNo() + " " + (hierarquiaOrganizacionalSelecionada.getSuperior() != null ? hierarquiaOrganizacionalSelecionada.getSuperior().getDescricao() : hierarquiaOrganizacionalSelecionada.getDescricao())
            + ", Verba: " + eventoFPSelecionado.getCodigo() + " " + eventoFPSelecionado.getDescricao();
    }

    private String gerarFiltrosSql() {
        String filtros = "";
        if (getMes() != null) {
            filtros += " and folha.mes = " + (getMes() - 1);
        }
        if (getAno() != null) {
            filtros += " and folha.ano = " + getAno();
        }
        if (getTipoFolhaDePagamento() != null) {
            filtros += " and folha.tipofolhadepagamento = " + "'" + getTipoFolhaDePagamento().name() + "'";
        }
        if (hierarquiaOrganizacionalSelecionada != null) {
            filtros += " and ho.codigo like " + "'" + hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%" + "'";
        }
        if (eventoFPSelecionado != null) {
            filtros += " and evento.id = " + eventoFPSelecionado.getId();
        }
        if (getVersao() != null) {
            filtros += " and folha.versao = " + getVersao();
        }
        if (!TipoEventoFPFicha.TODOS.equals(tipoEventoFPFicha)) {
            filtros += " and item.TIPOEVENTOFP = '" + tipoEventoFPFicha.name() + "'";
        }
        return filtros;
    }

    private String gerarFiltroLeftSql() {
        return " and to_date('" + getMes() + "/" + getAno() + "','MM/yyyy') between " +
            "trunc(cc.INICIOVIGENCIA) and coalesce(trunc(cc.FIMVIGENCIA), to_date('" + getMes() + "/" + getAno() + "','MM/yyyy'))";
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (getAno() == null || (getAno() != null && getAno() == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (getMes() != null && (getMes() < 1 || getMes() > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (eventoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Verba deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todas"));
        if (getMes() != null && getAno() != null && getTipoFolhaDePagamento() != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(getMes()), getAno(), getTipoFolhaDePagamento())) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTipoFolhasPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (Object obj : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoEventosFPFicha() {
        return Util.getListSelectItemSemCampoVazio(TipoEventoFPFicha.values());
    }

    public EventoFP getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(EventoFP eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public TipoEventoFPFicha getTipoEventoFPFicha() {
        return tipoEventoFPFicha;
    }

    public void setTipoEventoFPFicha(TipoEventoFPFicha tipoEventoFPFicha) {
        this.tipoEventoFPFicha = tipoEventoFPFicha;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia());
    }

    public List<EventoFP> completarEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public void limparHierarquiaSelecionada() {
        setHierarquiaOrganizacionalSelecionada(null);
    }

    private Date getDataVigencia() {
        if (getMes() != null && getAno() != null) {
            return DataUtil.criarDataComMesEAno(getMes(), getAno()).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public enum TipoEventoFPFicha {
        VANTAGEM("Vantagem"),
        DESCONTO("Desconto"),
        TODOS("Todos");
        private String descricao;

        TipoEventoFPFicha(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
