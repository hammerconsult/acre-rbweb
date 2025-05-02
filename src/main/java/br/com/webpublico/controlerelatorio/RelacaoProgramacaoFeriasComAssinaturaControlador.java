package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateus on 18/09/17.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relacao-programacao-ferias-com-assinatura", pattern = "/relacao-programacao-ferias-com-assinatura/", viewId = "/faces/rh/relatorios/programacaoferiascomassinatura.xhtml")
})
public class RelacaoProgramacaoFeriasComAssinaturaControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Integer anoReferencia;
    private Integer anoInicial;
    @Enumerated(EnumType.STRING)
    private StatusPeriodoAquisitivo statusPeriodoAquisitivo;
    @Enumerated(EnumType.STRING)
    private TipoGeracao tipoGeracao;
    @Enumerated(EnumType.STRING)
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;
    private ModalidadeContratoFP modalidadeContratoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String filtros;
    private Boolean quebrarPorLotacao;

    public RelacaoProgramacaoFeriasComAssinaturaControlador() {
    }

    @URLAction(mappingId = "relacao-programacao-ferias-com-assinatura", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        anoReferencia = sistemaFacade.getExercicioCorrente().getAno();
        anoInicial = sistemaFacade.getExercicioCorrente().getAno();
        tipoGeracao = TipoGeracao.ORGAO;
        statusPeriodoAquisitivo = null;
        tipoPeriodoAquisitivo = null;
        hierarquiaOrganizacional = null;
        modalidadeContratoFP = null;
        filtros = "";
    }


    public List<SelectItem> getTiposDePeriodo() {
        return Util.getListSelectItem(StatusPeriodoAquisitivo.values(), false);
    }

    public List<SelectItem> getTiposDeGeracao() {
        return Util.getListSelectItemSemCampoVazio(TipoGeracao.values(), false);
    }

    public List<SelectItem> getTiposDePeriodoAquisitivo() {
        return Util.getListSelectItem(TipoPeriodoAquisitivo.values(), false);
    }

    public List<ModalidadeContratoFP> completarModalidades(String partes) {
        return modalidadeContratoFPFacade.buscarModalidadesPorCodigoOrDescricao(partes);
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        if (tipoGeracao.isOrgaoELotacao()) {
            return hierarquiaOrganizacionalFacade.listaTodasHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
        } else {
            return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
        }
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("TIPO_PERIODO", tipoPeriodoAquisitivo != null ? tipoPeriodoAquisitivo.getDescricao().toUpperCase() : "");
            dto.adicionarParametro("ANO", sistemaFacade.getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("QUEBRAR_POR_LOTACAO", quebrarPorLotacao);
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("isOrgaoELotacao", tipoGeracao.isOrgaoELotacao());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELAÇÃO-PROGRAMAÇÃO-FERIAS-COM-ASSINATURA");
            dto.setApi("rh/programacao-ferias-com-assinatura/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (anoReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Ano' deve ser informado.");
        }
        if (anoInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Verificar inconsistências desde' deve ser informado.");
        }
        ve.lancarException();
        if (anoInicial > anoReferencia) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O campo 'Verificar inconsistências desde' deve ser anterior ou igual ao campo 'Ano' ");
        }
        ve.lancarException();
    }

    public void limparHierarquia() {
        hierarquiaOrganizacional = null;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros =  new ArrayList<>();
        filtros = "Ano: " + anoReferencia + ", Verificar inconsistências desde: " + anoInicial;
        parametros.add(new ParametrosRelatorios(" extract(year from periodo.INICIOVIGENCIA) ", ":anoInicial", ":anoFinal", OperacaoRelatorio.BETWEEN, anoInicial, anoReferencia, 1, false));
        if (statusPeriodoAquisitivo != null) {
            parametros.add(new ParametrosRelatorios(" periodo.status ", ":status", null, OperacaoRelatorio.LIKE, statusPeriodoAquisitivo.name(), null, 1, false));
            filtros += ", Status: " + statusPeriodoAquisitivo.getDescricao();
        }
        if (tipoPeriodoAquisitivo != null) {
            parametros.add(new ParametrosRelatorios(" base.TIPOPERIODOAQUISITIVO ", ":tipoPeriodo", null, OperacaoRelatorio.LIKE, tipoPeriodoAquisitivo.name(), null, 1, false));
            filtros += ", Tipo de Período Aquisitivo: " + tipoPeriodoAquisitivo.getDescricao();
        }
        if (modalidadeContratoFP != null) {
            parametros.add(new ParametrosRelatorios(" moda.id ", ":modalidadeId", null, OperacaoRelatorio.IGUAL, modalidadeContratoFP.getId(), null, 1, false));
            filtros += ", Vínculo: " + modalidadeContratoFP;
        }
        if (hierarquiaOrganizacional != null) {
            parametros.add(new ParametrosRelatorios(" vw.codigo ", ":codigoVw", null, OperacaoRelatorio.LIKE, hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%", null, 1, false));
            filtros += ", Órgão: " + hierarquiaOrganizacional.getCodigo();
        }
        return parametros;
    }

    public Integer getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(Integer anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public Integer getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(Integer anoInicial) {
        this.anoInicial = anoInicial;
    }

    public StatusPeriodoAquisitivo getStatusPeriodoAquisitivo() {
        return statusPeriodoAquisitivo;
    }

    public void setStatusPeriodoAquisitivo(StatusPeriodoAquisitivo statusPeriodoAquisitivo) {
        this.statusPeriodoAquisitivo = statusPeriodoAquisitivo;
    }

    public TipoGeracao getTipoGeracao() {
        return tipoGeracao;
    }

    public void setTipoGeracao(TipoGeracao tipoGeracao) {
        this.tipoGeracao = tipoGeracao;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Boolean getQuebrarPorLotacao() {
        return quebrarPorLotacao;
    }

    public void setQuebrarPorLotacao(Boolean quebrarPorLotacao) {
        this.quebrarPorLotacao = quebrarPorLotacao;
    }

    public enum TipoGeracao {
        ORGAO("Órgão"),
        ORGAO_E_LOTACAO("Órgão e Lotação");

        private String descricao;

        TipoGeracao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        public boolean isOrgaoELotacao() {
            return ORGAO_E_LOTACAO.equals(this);
        }
    }
}
