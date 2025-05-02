/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controlerelatorio.rh.SuperRelatorioRH;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 12/06/2018.
 */

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-cargos-verba", pattern = "/relatorio/servidores-cargos-verba/", viewId = "/faces/rh/relatorios/relatorioservidorescargosverba.xhtml")
})
public class RelatorioServidoresCargosVerbaControlador extends SuperRelatorioRH implements Serializable {
    private static final String NOME_ARQUIVO = "Relatório de Servidores Cargos e Verbas";
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private Cargo cargo;
    private Boolean agruparOrgao;
    private EventoFP eventoFP;
    private List<EventoFP> itemEventoFP;

    public RelatorioServidoresCargosVerbaControlador() {
        this.cargo = new Cargo();
        geraNoDialog = Boolean.TRUE;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public void setEventoFF(EventoFP eventoFF) {
        this.eventoFP = eventoFF;
    }

    public List<EventoFP> getItemEventoFP() {
        return itemEventoFP;
    }

    public void setItemEventoFP(List<EventoFP> itemEventoFP) {
        this.itemEventoFP = itemEventoFP;
    }

    @Override
    @URLAction(mappingId = "relatorio-servidores-cargos-verba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        cargo = null;
        setMes(null);
        setAno(null);
        setTipoFolhaDePagamento(null);
        setAgruparOrgao(Boolean.FALSE);
        itemEventoFP = Lists.newArrayList();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) throws JRException, IOException {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/servidores-cargos-verba/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public StreamedContent fileDownload() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("NOME_ARQUIVO", NOME_ARQUIVO);
            dto.setApi("rh/servidores-cargos-verba/excel/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", NOME_ARQUIVO + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SERVIDORES CARGOS E VERBAS");
        dto.adicionarParametro("MES", (getMes().getNumeroMesIniciandoEmZero()));
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("agruparOrgao", agruparOrgao);
        dto.adicionarParametro("idsEvento", getIdsEvento());
        dto.adicionarParametro("hierarquia", hierarquiaOrganizacionalSelecionada.getSuperior() == null ? hierarquiaOrganizacionalSelecionada.toString() : null);
        dto.setNomeRelatorio(getNomePDF());
        return dto;
    }

    public List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAOPERACAO", null, null, DataUtil.getDataFormatada(sistemaControlador.getDataOperacao()), null, 0, true));
        parametros.add(new ParametrosRelatorios("ficha.mes", ":mes", null, OperacaoRelatorio.IGUAL, getMes().getNumeroMesIniciandoEmZero(), null, 1, false));
        parametros.add(new ParametrosRelatorios("ficha.ano", ":ano", null, OperacaoRelatorio.IGUAL, getAno(), null, 1, false));
        parametros.add(new ParametrosRelatorios("ho.codigo", ":codigoho", null, OperacaoRelatorio.LIKE, getHierarquiaOrganizacionalSelecionada().getCodigoSemZerosFinais() + "%", null, 1, false));
        parametros.add(new ParametrosRelatorios("evento.id", ":idEvento", null, OperacaoRelatorio.IN, getIdsEvento(), null, 1, false));
        parametros.add(new ParametrosRelatorios("ficha.TIPOFOLHADEPAGAMENTO", ":tipoFolha", null, OperacaoRelatorio.IGUAL, getTipoFolhaDePagamento().name(), null, 1, false));
        if (cargo != null) {
            parametros.add(new ParametrosRelatorios("c.id ", ":cargo", null, OperacaoRelatorio.IGUAL, cargo.getId(), null, 1, false));
        }
        return parametros;
    }

    private List<Long> getIdsEvento() {
        List<Long> ids = Lists.newArrayList();
        for (EventoFP fp : itemEventoFP) {
            ids.add(fp.getId());
        }
        return ids;
    }

    private String getNomePDF() {
        return "RELATORIO-SERVIDORES-CARGOS-SALARIO-" + getMes() + "/" + getAno();
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
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (itemEventoFP.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um Evento Folha de Pagamento.");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<Cargo> completarCargos(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Boolean getAgruparOrgao() {
        return agruparOrgao;
    }

    public void setAgruparOrgao(Boolean agruparOrgao) {
        this.agruparOrgao = agruparOrgao;
    }


    public void adicionarItemEventoFP() {
        try {
            if (eventoFP != null) {
                hasEventoFPAdicionado();
                itemEventoFP.add(eventoFP);
                eventoFP = null;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void hasEventoFPAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        if (itemEventoFP.contains(eventoFP)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O evento <b>" + eventoFP + "</b> já foi adicionado.");
        }
        ve.lancarException();
    }

    public void removerItemEventoFP(EventoFP eventoFP) {
        itemEventoFP.remove(eventoFP);
    }
}
