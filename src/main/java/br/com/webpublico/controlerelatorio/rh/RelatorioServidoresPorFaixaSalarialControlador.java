package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import liquibase.util.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "relatorioServidoresPorFaixaSalarialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioServidoresPorFaixaSalarial", pattern = "/relatorio/servidores-por-faixa-salarial/", viewId = "/faces/rh/relatorios/relatorioservidoresfaixasalarial.xhtml")
})
public class RelatorioServidoresPorFaixaSalarialControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Integer mes;
    private Integer ano;
    private BigDecimal faixaSalarialInicial;
    private BigDecimal faixaSalarialFinal;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private List<ModalidadeContratoFP> modalidades;
    private Boolean estagiarios;
    private Boolean aposentados;
    private Boolean pensionistasPrevidenciarios;
    private Boolean beneficiarios;
    private Boolean excluirExoneradosEncerrados;
    private Boolean valorLiquido;

    @URLAction(mappingId = "relatorioServidoresPorFaixaSalarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        modalidades = Lists.newArrayList();
        mes = DataUtil.getMes(getSistemaFacade().getDataOperacao());
        ano = getSistemaFacade().getExercicioCorrente().getAno();
        faixaSalarialInicial = BigDecimal.ZERO;
        faixaSalarialFinal = BigDecimal.ZERO;
        tipoFolhaDePagamento = null;
        estagiarios = false;
        aposentados = false;
        pensionistasPrevidenciarios = false;
        excluirExoneradosEncerrados = false;
        beneficiarios = false;
        valorLiquido = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/relatorio-servidores-faixa-salarial/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Relatório Quantidade de Servidores por Faixa Salarial");
        dto.setNomeRelatorio("Relatório Quantidade de Servidores por Faixa Salarial");
        dto.adicionarParametro("MES", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("COMPLEMENTOWHERE", complementoWhere());
        dto.adicionarParametro("COMPLEMENTOWHEREFAIXASALARIAL", complementoWhereFaixaSalarial());
        dto.adicionarParametro("COMPLEMENTOSQLVALOR", complementoSqlValor());
        dto.adicionarParametro("MODALIDADECONTRATO", modalidadesContrato());
        dto.adicionarParametro("FILTROS", filtrosUtilizados());
        dto.adicionarParametro("APOSENTADOS", aposentados);
        dto.adicionarParametro("PENSIONISTASPREVIDENCIARIOS", pensionistasPrevidenciarios);
        dto.adicionarParametro("ESTAGIARIOS", estagiarios);
        dto.adicionarParametro("VALORLIQUIDO", valorLiquido);
        dto.adicionarParametro("DESCRICAOVALORLIQUIDO", getDescricaoValorLiquido());
        dto.adicionarParametro("VINCULOSMODALIDADE", !modalidades.isEmpty());
        dto.adicionarParametro("BENEFICIARIOS", beneficiarios);
        return dto;
    }

    private String filtrosUtilizados() {
        StringBuilder sb = new StringBuilder(" ");
        sb.append(hierarquiasOrganizacionais.size() > 1 ? "Órgãos: " : "Órgão: ").append(StringUtils.join(getDescricaoHierarquias(), ", ")).append(" /");
        sb.append(" Mês/Ano: ").append(mes).append("/").append(ano).append(" /");
        sb.append(" Tipo de Folha: ").append(tipoFolhaDePagamento.getDescricao()).append(" /");
        if (faixaSalarialFinal.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" Faixa Salarial Inicial: R$").append(faixaSalarialInicial).append(" /");
            sb.append(" Faixa Salarial Final: R$").append(faixaSalarialFinal).append(" /");
        }
        sb.append(" Vínculos: ");
        if (!modalidades.isEmpty()) {
            sb.append(StringUtils.join(getDescricaoModalidades(), ", ")).append(",");
        }
        sb.append(aposentados ? " APOSENTADOS," : "").append(estagiarios ? " ESTAGIÁRIOS," : "").append(pensionistasPrevidenciarios ? " PENSIONISTAS PREVIDENCIÁRIOS," : "").append(beneficiarios ? " BENEFICIÁRIOS DE PENSÃO TRAMITADA E JULGADA." : "");
        return sb.toString().replaceFirst(".$", ".");
    }


    private String complementoWhere() {
        StringBuilder sb = new StringBuilder("");
        if (!hierarquiasOrganizacionais.isEmpty()) {
            sb.append(" and (ho.codigo like '").append(StringUtils.join(getCodigoHierarquias(), "' or ho.codigo like '")).append("') ");
        }
        if (tipoFolhaDePagamento != null) {
            sb.append(" and folha.TIPOFOLHADEPAGAMENTO = '").append(tipoFolhaDePagamento.name()).append("'");
        }
        if (excluirExoneradosEncerrados) {
            sb.append(" and vin.ID not in (select VINCULOFP_ID from exoneracaorescisao) ");
        }
        return sb.toString();
    }

    private String complementoWhereFaixaSalarial() {
        StringBuilder sb = new StringBuilder("");
        if (faixaSalarialFinal.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" where dados.valor between ").append(faixaSalarialInicial).append(" and ").append(faixaSalarialFinal);
        }
        return sb.toString();
    }

    private String complementoSqlValor() {
        if (valorLiquido) {
            return "((select sum(item.VALOR) " +
                "               from itemfichafinanceirafp item " +
                "               where item.TIPOEVENTOFP = :vantagem " +
                "                 and ficha.ID = item.FICHAFINANCEIRAFP_ID) - " +
                "              coalesce((select sum(item.VALOR) " +
                "               from itemfichafinanceirafp item " +
                "               where item.TIPOEVENTOFP = :desconto " +
                "                 and ficha.ID = item.FICHAFINANCEIRAFP_ID), 0)) as valor ";
        }
        return "(select sum(item.VALOR) " +
            "              from itemfichafinanceirafp item " +
            "              where item.TIPOEVENTOFP = :vantagem " +
            "                and ficha.ID = item.FICHAFINANCEIRAFP_ID) as valor ";
    }

    private String getDescricaoValorLiquido() {
        if (valorLiquido) {
            return "VALORES LÍQUIDOS";
        }
        return "VALORES BRUTOS";
    }

    public List<SelectItem> getModalidadesContratoFP() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ModalidadeContratoFP object : modalidadeContratoFPFacade.buscarTodasAsModalidades()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é deve ser informado.");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (faixaSalarialInicial.compareTo(BigDecimal.ZERO) != 0 && faixaSalarialFinal.compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Faixa Salarial Final deve ser informado.");
        }
        if (faixaSalarialInicial.compareTo(faixaSalarialFinal) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Faixa Salarial Inicial deve ser menor que Faixa Salarial Final.");
        }
        if (hierarquiasOrganizacionais.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo órgão deve ser informado.");
        }
        if (vinculosNaoSelecionados()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Vínculos deve ser informado.");
        }
        if (valorLiquido == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Valor deve ser informado.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private boolean isMesValido() {
        if (mes != null) {
            if (mes.compareTo(1) < 0 || mes.compareTo(12) > 0) {
                return false;
            }
        }
        return true;
    }

    private List<String> getDescricaoHierarquias() {
        List<String> hierarquias = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            hierarquias.add(ho.getDescricao());
        }
        return hierarquias;
    }

    private List<String> getCodigoHierarquias() {
        List<String> hierarquias = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquiasOrganizacionais) {
            hierarquias.add(ho.getCodigoSemZerosFinais() + "%");
        }
        return hierarquias;
    }

    private String modalidadesContrato() {
        StringBuilder sb = new StringBuilder("");
        if (!modalidades.isEmpty()) {
            List<String> modalidadesid = Lists.newArrayList();
            for (ModalidadeContratoFP mod : modalidades) {
                modalidadesid.add(mod.getId().toString());
            }
            sb.append(" and modalidade.id in (").append(StringUtils.join(modalidadesid, ",")).append(") ");
        }
        return sb.toString();
    }

    private List<String> getDescricaoModalidades() {
        List<String> modalidadesContrato = Lists.newArrayList();
        for (ModalidadeContratoFP mod : modalidades) {
            modalidadesContrato.add(mod.getDescricao());
        }
        return modalidadesContrato;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }

    public void adicionarHierarquia() {
        hierarquiasOrganizacionais.add(hierarquiaOrganizacional);
        hierarquiaOrganizacional = null;
    }

    public void removerHierarquia(HierarquiaOrganizacional h) {
        hierarquiasOrganizacionais.remove(h);
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    private Boolean vinculosNaoSelecionados() {
        return modalidades.isEmpty() && !estagiarios && !aposentados
            && !pensionistasPrevidenciarios && !beneficiarios;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getFaixaSalarialInicial() {
        return faixaSalarialInicial;
    }

    public void setFaixaSalarialInicial(BigDecimal faixaSalarialInicial) {
        this.faixaSalarialInicial = faixaSalarialInicial;
    }

    public BigDecimal getFaixaSalarialFinal() {
        return faixaSalarialFinal;
    }

    public void setFaixaSalarialFinal(BigDecimal faixaSalarialFinal) {
        this.faixaSalarialFinal = faixaSalarialFinal;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    public Boolean getEstagiarios() {
        return estagiarios;
    }

    public void setEstagiarios(Boolean estagiarios) {
        this.estagiarios = estagiarios;
    }

    public Boolean getAposentados() {
        return aposentados;
    }

    public void setAposentados(Boolean aposentados) {
        this.aposentados = aposentados;
    }

    public Boolean getPensionistasPrevidenciarios() {
        return pensionistasPrevidenciarios;
    }

    public void setPensionistasPrevidenciarios(Boolean pensionistasPrevidenciarios) {
        this.pensionistasPrevidenciarios = pensionistasPrevidenciarios;
    }

    public Boolean getExcluirExoneradosEncerrados() {
        return excluirExoneradosEncerrados;
    }

    public void setExcluirExoneradosEncerrados(Boolean excluirExoneradosEncerrados) {
        this.excluirExoneradosEncerrados = excluirExoneradosEncerrados;
    }

    public Boolean getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(Boolean valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Boolean getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(Boolean beneficiarios) {
        this.beneficiarios = beneficiarios;
    }
}
