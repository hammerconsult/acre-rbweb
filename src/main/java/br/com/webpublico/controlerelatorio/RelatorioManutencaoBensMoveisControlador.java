package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.administrativo.patrimonio.RelatorioPatrimonioSuperControlador;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.administrativo.TipoManutencao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 29/06/2017.
 */
@ViewScoped
@ManagedBean
@URLMapping(id = "relatorio-manutencao-bens-moveis",
    pattern = "/relatorio/manutencao-bens-moveis/",
    viewId = "/faces/administrativo/patrimonio/relatorios/relatoriomanutencaobemmovel.xhtml")
public class RelatorioManutencaoBensMoveisControlador extends RelatorioPatrimonioSuperControlador {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioManutencaoBensMoveisControlador.class);
    private Boolean seguro;
    private Boolean garantia;
    private TipoManutencao tipoManutencao;
    private Date dtInicial;
    private Date dtFinal;

    @URLAction(mappingId = "relatorio-manutencao-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroPatrimonio();
        setSeguro(Boolean.FALSE);
        setGarantia(Boolean.FALSE);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("SEGURO", hasSeguro());
            dto.adicionarParametro("GARANTIA", hasGarantia());
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtro.getFiltros());
            if (filtro.getHierarquiaAdm() != null) {
                dto.adicionarParametro("ADMINISTRATIVA", filtro.getHierarquiaAdm().getSubordinada().getDescricao());
                dto.adicionarParametro("CODADM", filtro.getHierarquiaAdm().getCodigo());
            }
            if (filtro.getHierarquiaOrc() != null) {
                dto.adicionarParametro("ORCAMENTARIA", filtro.getHierarquiaOrc().getSubordinada().getDescricao());
                dto.adicionarParametro("CODORC", filtro.getHierarquiaOrc().getCodigo());
            }
            dto.setNomeRelatorio("Relatório de Bens Móveis em Manutenção");
            dto.setApi("administrativo/manutencao-bens-moveis/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error(" erro.: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        String filtros = "";
        filtros += "Critério(s) Utilizado(s): ";
        if (filtro.getHierarquiaOrc() != null) {
            parametros.add(new ParametrosRelatorios(" orc.id ", ":orcamentaria", null, OperacaoRelatorio.IGUAL, filtro.getHierarquiaOrc().getId(), null, 1, false));
            filtros += " Unidade Orçamentária: " + filtro.getHierarquiaOrc().getCodigo();
            filtros += " " + filtro.getHierarquiaOrc().getSubordinada().getDescricao() + ",  ";
        }
        if (filtro.getHierarquiaAdm() != null) {
            parametros.add(new ParametrosRelatorios(" adm.id ", ":administrativa", null, OperacaoRelatorio.IGUAL, filtro.getHierarquiaAdm().getId(), null, 1, false));
            filtros += " Unidade Administrativa: " + filtro.getHierarquiaAdm().getCodigo();
            filtros += " " + filtro.getHierarquiaAdm().getSubordinada().getDescricao() + ",  ";
        }
        if (getTipoManutencao() != null) {
            parametros.add(new ParametrosRelatorios(" entrada.tipomanutencao ", ":tipoManutencao", null, OperacaoRelatorio.IGUAL, getTipoManutencao().name(), null, 1, false));
            filtros += " Tipo de Manutenção: " + getTipoManutencao().getDescricao() + ",  ";
        } else {
            filtros += " Tipo de Manutenção: Todas" + ",  ";
        }
        if (filtro.getDataReferencia() != null) {
            parametros.add(new ParametrosRelatorios(" trunc(entrada.inicioEm) ", ":referencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(filtro.getDataReferencia()), null, 0, true));
            filtros += " Data de Referência: " + DataUtil.getDataFormatada(filtro.getDataReferencia()) + ",  ";
        }
        if (dtInicial != null) {
            parametros.add(new ParametrosRelatorios(" trunc(entrada.inicioEm) ", ":inicioManutencao", null, OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(dtInicial), null, 1, true));
            filtros += " Data Inicial: " + DataUtil.getDataFormatada(dtInicial) + ",  ";
        }
        if (dtFinal != null) {
            parametros.add(new ParametrosRelatorios(" retorno.retornoEm ", ":retornoManutencao", null, OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(dtFinal), null, 1, true));
            filtros += " Data Final: " + DataUtil.getDataFormatada(dtFinal) + ",  ";
        }
        if (hasSeguro()) {
            parametros.add(new ParametrosRelatorios(" seguro.vencimento  ", "", null, OperacaoRelatorio.IS_NOT_NULL, null, null, 1, false));
            filtros += " Tem Seguro: " + Util.converterBooleanSimOuNao(hasSeguro()) + ",  ";
        }
        if (hasGarantia()) {
            parametros.add(new ParametrosRelatorios(" garantia.datavencimento ", "", null, OperacaoRelatorio.IS_NOT_NULL, null, null, 1, false));
            filtros += " Tem Garantia: " + Util.converterBooleanSimOuNao(hasGarantia()) + ",  ";
        }
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 3);
        filtros += ".";
        filtro.setFiltros(filtros);
        return parametros;
    }

    public List<SelectItem> getTiposManutencao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (TipoManutencao object : TipoManutencao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getHierarquiaAdm() == null && filtro.getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor informe ao menos uma unidade administrativa ou orçamentária ");
        }
        if (filtro.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor informe a data de referência ");
        }
        if (dtInicial != null && dtFinal != null) {
            if (DataUtil.verificaDataFinalMaiorQueDataInicial(dtInicial, dtFinal)) {
                FacesUtil.addOperacaoNaoPermitida("A Data Inicial não pode ser maior que a Data Final");
            }
        }
        ve.lancarException();
    }

    public Boolean getSeguro() {
        return seguro;
    }

    public void setSeguro(Boolean seguro) {
        this.seguro = seguro;
    }

    public Boolean getGarantia() {
        return garantia;
    }

    public void setGarantia(Boolean garantia) {
        this.garantia = garantia;
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public void setTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
    }

    public Boolean hasGarantia() {
        return this.garantia == null ? false : this.garantia;
    }

    public Boolean hasSeguro() {
        return this.seguro == null ? false : this.seguro;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }
}
