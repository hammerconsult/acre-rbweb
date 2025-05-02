package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 04/07/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-formulario-boletim-frequencia", pattern = "/relatorio/formulario-boletim-frequencia-servidor/", viewId = "/faces/rh/relatorios/relatorioformularioboletimfrequencia.xhtml")
})
public class RelatorioFormularioBoletimFrequencia implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioFormularioBoletimFrequencia.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private HierarquiaOrganizacional ltInicial;
    private HierarquiaOrganizacional ltFinal;
    private Mes mes;
    private Integer ano;
    private String filtros;
    private ModalidadeContratoFP modalidadeContratoFP;
    private Boolean todosVinculos;
    private Boolean todasUnidades;

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório de Boletim de Frequência do Servidor: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório de Boletim de Frequência do Servidor: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        DateTime dateTime = new DateTime(new Date());
        DateTime dateMes = new DateTime(sistemaFacade.getDataOperacao());
        dateTime = dateTime.withMonthOfYear(mes.getNumeroMes());
        dateTime = dateTime.withYear(ano);
        if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
            dateTime.withDayOfMonth(dateMes.getDayOfMonth());
        } else {
            dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
        }
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("clausulaWhere", buscarClausulaWhere());
        dto.adicionarParametro("FILTROS", filtros);
        dto.adicionarParametro("dataOperacao", dateTime.toDate());
        dto.adicionarParametro("MES", mes.getNumeroMes());
        dto.adicionarParametro("MESANO", getMesAno());
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", "FORMULÁRIO DE PREENCHIMENTO - BOLETIM FREQUÊNCIA");
        dto.setNomeRelatorio("FORMULÁRIO-DE-PREENCHIMENTO-BOLETIM-FREQUÊNCIA");
        dto.setApi("rh/formulario-boletim-frequencia/");
        return dto;
    }


    private String buscarClausulaWhere() {
        filtros = "";
        StringBuilder stb = new StringBuilder(" ");
        if (ltInicial != null && ltFinal != null) {
            stb.append(" and ho.codigo between '").append(this.ltInicial.getCodigo()).append("' and '").append(this.ltFinal.getCodigo()).append("'");
            filtros += "Lotação Inicial: " + ltInicial.getCodigo() + " à " + "Lotacao Final: " + ltFinal.getCodigo() + " ";
        }
        if (!todasUnidades) {
            stb.append(" and ho.codigo like '").append(hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais()).append("%'");
        }
        if (!todosVinculos) {
            stb.append(" and modalidade.id = ").append(modalidadeContratoFP.getId());
        }
        return stb.toString();
    }


    private String getMesAno() {
        return ((String.valueOf(mes.getNumeroMes()).length() <= 1 ? "0" + mes.getNumeroMes() : mes.getNumeroMes()) + "/" + ano);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo mês deve ser informado.");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ano deve ser informado.");
        }
        if (!todasUnidades && hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo órgão deve ser informado.");
        }
        if (todosVinculos == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo vínculo deve ser informado.");
        }
        if (!todosVinculos && modalidadeContratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Vínculo");
        }
        if (ltFinal != null && ltInicial != null && ltFinal.getCodigo() != null && ltInicial.getCodigo() != null && ltFinal.getCodigo().compareTo(ltInicial.getCodigo()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Lotação Inicial não pode ser maior que a Lotação Final");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-formulario-boletim-frequencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        todosVinculos = Boolean.TRUE;
        todasUnidades = Boolean.FALSE;
        modalidadeContratoFP = null;
        hierarquiaOrganizacionalSelecionada = null;
        ltInicial = null;
        ltFinal = null;
        mes = null;
        ano = null;
        filtros = "";
    }

    public List<SelectItem> getModalidadeContratoServidor() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP object : modalidadeContratoFPFacade.modalidadesAtivas()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivelSemView(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarLotacoes(String parte) {
        return hierarquiaOrganizacionalFacade.getHIerarquiaFilhosDeX(parte.trim(), hierarquiaOrganizacionalSelecionada, UtilRH.getDataOperacao());
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

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public Boolean getTodosVinculos() {
        return todosVinculos;
    }

    public void setTodosVinculos(Boolean todosVinculos) {
        this.todosVinculos = todosVinculos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public HierarquiaOrganizacional getLtInicial() {
        return ltInicial;
    }

    public void setLtInicial(HierarquiaOrganizacional ltInicial) {
        this.ltInicial = ltInicial;
    }

    public HierarquiaOrganizacional getLtFinal() {
        return ltFinal;
    }

    public void setLtFinal(HierarquiaOrganizacional ltFinal) {
        this.ltFinal = ltFinal;
    }

    public Boolean getTodasUnidades() {
        return todasUnidades;
    }

    public void setTodasUnidades(Boolean todasUnidades) {
        this.todasUnidades = todasUnidades;
    }

    public void atribuirNullCampos() {
        ltInicial = null;
        ltFinal = null;
        hierarquiaOrganizacionalSelecionada = null;
    }

    public List<SelectItem> getMesesRelatorio() {
        return Util.getListSelectItem(Mes.values(), false);
    }
}
