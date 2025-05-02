package br.com.webpublico.controlerelatorio.rh;


import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.FiltroBaseFP;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.rh.relatorios.TipoIncidenciaVerba;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.BaseFPFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by zaca.
 */
@ManagedBean(name = "relatorioIncidenciaVerbasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-incidencia-verbas",
        pattern = "/rh/relatorio/incidencia-verbas/",
        viewId = "/faces/rh/relatorios/incidencia-verbas/relatorioincidenciaverbas.xhtml")
})
public class RelatorioIncidenciaVerbasControlador extends SuperRelatorioRH {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioIncidenciaVerbasControlador.class);

    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private List<BaseFP> bases;
    private BaseFP baseFP;

    private ConverterAutoComplete converterBaseFP;

    @URLAction(mappingId = "relatorio-incidencia-verbas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
        this.buscarBaseFPsConfiguracaoRH();
    }

    public void limparCampos() {
        super.limpar();
        this.setFiltroBaseFP(FiltroBaseFP.NORMAL);
        setTipoIncidenciaVerba(TipoIncidenciaVerba.ATIVO);
        this.setBases(Lists.<BaseFP>newLinkedList());
        this.setFiltros(Lists.<ParametrosRelatorios>newLinkedList());
    }

    public StreamedContent emitirExcel() {
        try {
            validarCampos();
            definirFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            definirParametros(dto);
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "RELATORIO-DE-INCIDÊNCIA-DE-VERBAS.xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private void definirFiltros() {
        this.setFiltros(Lists.<ParametrosRelatorios>newArrayList());
        this.setCriterioUtilizado(new String());
        if (getFiltroBaseFP() != null) {
            getFiltros().add(new ParametrosRelatorios(null, ":filtroBase", null, null, getFiltroBaseFP().name(), null, 0, false));
            adicionarCriterio("Tipo de BaseFP: ", getFiltroBaseFP().getDescricao());
        }

        if (getTipoIncidenciaVerba() != null) {
            getFiltros().add(new ParametrosRelatorios(" coalesce(e.ATIVO, 0) ", ":tipoIncidencia", null, OperacaoRelatorio.IGUAL, getTipoIncidenciaVerba().getValor(), null, 1, false));
            adicionarCriterio("Tipo de Incidência de Evento FP: ", getTipoIncidenciaVerba().getDescricao());
        }

        if (!CollectionUtils.isEmpty(getBases())) {
            getFiltros().add(new ParametrosRelatorios(" b.id ", ":bases", null, OperacaoRelatorio.IN, buscarBasesToFiltro(), null, 1, false));
            adicionarCriterio("Bases: ", buscarBasesToCriterio());
        }

        setCriterioUtilizado(!getCriterioUtilizado().isEmpty()
            ? getCriterioUtilizado().substring(0, getCriterioUtilizado().length() - 2) + "."
            : "");

    }


    public void adicionarCriterio(String texto, Object valor) {
        if (valor instanceof Collection) {
            this.setCriterioUtilizado(this.getCriterioUtilizado().concat(texto));
            for (Object o : ((Collection) valor)) {
                this.setCriterioUtilizado(getCriterioUtilizado().concat(o + ", "));
            }
        } else {
            this.setCriterioUtilizado(getCriterioUtilizado().concat(texto).concat(" " + valor + ", "));
        }
    }

    private List<Long> buscarBasesToFiltro() {
        List<Long> bases = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(getBases())) {
            for (BaseFP base : getBases()) {
                bases.add(base.getId());
            }
        }

        return bases;
    }

    private List<String> buscarBasesToCriterio() {
        List<String> bases = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(getBases())) {
            for (BaseFP base : getBases()) {
                bases.add(base.toString());
            }
        }

        return bases;
    }

    private void definirParametros(RelatorioDTO dto) {
        dto.setNomeRelatorio("RELATÓRIO DE INCIDÊNCIA DE VERBAS");
        dto.setApi("rh/incidencia-verbas/excel/");
        dto.adicionarParametro("MODULO", RECURSOS_HUMANOS);
        dto.adicionarParametro("FILTRO_EXCEL", getCriterioUtilizado());
        dto.adicionarParametro("FILTROS_CONSULTA", ParametrosRelatorios.parametrosToDto(getFiltros()));
        adicionarParametroTotalBases(dto);
        definirParameterUsuario(dto);

    }

    private void adicionarParametroTotalBases(RelatorioDTO dto) {
        int total = getBases().size();
        dto.adicionarParametro("TOTAL_BASES", total);
    }

    public void definirParameterUsuario(RelatorioDTO dto) {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            dto.adicionarParametro("USUARIO", getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            dto.adicionarParametro("USUARIO", getSistemaControlador().getUsuarioCorrente().getLogin());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (getFiltroBaseFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe o filtro da baseFP. ");
        }

        if (CollectionUtils.isEmpty(getBases())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informar ao menos uma BaseFP para emissão do relatório.");
        }
        ve.lancarException();
    }

    public void buscarBaseFPsConfiguracaoRH() {
        try {
            setBases(getConfiguracaoRHFacade().buscarBaseFPsToConfiguracaoRelatorioRH());
        } catch (ExcecaoNegocioGenerica ex) {
            throw ex;
        }
    }

    public void removerBaseFP(BaseFP base) {
        getBases().remove(base);
    }

    public List<SelectItem> getFiltrosBaseFP() {
        return Util.getListSelectItem(FiltroBaseFP.values());
    }

    public List<BaseFP> completarBaseFP(String filtro) {
        if (getFiltroBaseFP() == null) {
            return Lists.newArrayList();
        }
        return getBaseFPFacade().buscarBasePFsPorCodigoOrDescricaoAndFiltro(filtro, getFiltroBaseFP());
    }

    public void adicionarBaseFP() {
        try {
            validarBaseFP();
            getBases().add(getBaseFP());
            setBaseFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (getBaseFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe a base para adicionar");
        }
        validarAdicionarBaseFP(ve);
        ve.lancarException();
    }

    private void validarAdicionarBaseFP(ValidacaoException ve) {
        if (getBaseFP() != null) {
            if (!CollectionUtils.isEmpty(getBases())) {
                for (BaseFP baseSelecionada : getBases()) {
                    if (baseSelecionada.equals(getBaseFP())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Base " + getBaseFP() + " já foi adicionada. Por favor informe outra base FP");
                        break;
                    }
                }
            }
        }
    }

    public List<SelectItem> getTiposIncidenciaVerba() {
        List<SelectItem> itens = Lists.newArrayList();
        itens.add(new SelectItem(null, "Todas"));
        for (TipoIncidenciaVerba objeto : TipoIncidenciaVerba.values()) {
            itens.add(new SelectItem(objeto, objeto.toString()));
        }

        return itens;
    }

    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        return configuracaoRHFacade;
    }

    public List<BaseFP> getBases() {
        return bases;
    }

    public void setBases(List<BaseFP> bases) {
        this.bases = bases;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public ConverterAutoComplete getConverterBaseFP() {
        return new ConverterAutoComplete(BaseFP.class, getBaseFPFacade());
    }

    public void setConverterBaseFP(ConverterAutoComplete converterBaseFP) {
        this.converterBaseFP = converterBaseFP;
    }

    public BaseFPFacade getBaseFPFacade() {
        return baseFPFacade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }
}
