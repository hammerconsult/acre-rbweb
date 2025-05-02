/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.TipoRegime;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoRegimeFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
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
 * @author leonardo
 */
@ManagedBean(name = "relatorioServidoresPorModalidadeFp")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioServidoresPorModalidade", pattern = "/rh/relatorio-de-servidores-por-vinculo/", viewId = "/faces/rh/relatorios/relatorioservidorespormodalidadefp.xhtml")
})
public class RelatorioServidoresPorModalidadeFp implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioServidoresPorModalidadeFp.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<ModalidadeContratoFP> modalidades;
    private Boolean efetivoCargoComissao;
    private List<TipoRegime> tiposRegimes;
    private String filtros;
    private Date mesEAno;

    @URLAction(mappingId = "relatorioServidoresPorModalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacional = null;
        efetivoCargoComissao = Boolean.FALSE;
        modalidades = Lists.newArrayList();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            if(mesEAno!=  null){
                Date ultimoDiaDoMes = DataUtil.primeiroDiaMes(mesEAno).getTime();
                dto.adicionarParametro("referenciaMesEAno", ultimoDiaDoMes);
            }

            dto.adicionarParametro("dataReferencia", UtilRH.getDataOperacao());
            dto.adicionarParametro("clausulaWhere", buscarClausulaWhere());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SERVIDORES ATIVOS POR VÍNCULO (MODALIDADE)");
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELATÓRIO-DE-SERVIDORES-ATIVOS-POR-VÍNCULO-MODALIDADE");
            dto.setApi("rh/servidores-ativos-por-vinculo-empregaticio/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório de Servidores Por Modalidade{}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    public List<SelectItem> getModalidadesContratoFP() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ModalidadeContratoFP object : modalidadeContratoFPFacade.buscarTodasAsModalidades()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getRegimes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoRegime obj : tipoRegimeFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    private String buscarClausulaWhere() {
        String retorno;
        filtros = "";

        if (mesEAno != null) {
            retorno = " and to_date(to_char(:diaDoMes, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(vinculo.iniciovigencia, 'mm/yyyy'), 'mm/yyyy') " +
                "     and to_date(to_char(coalesce(vinculo.finalvigencia, :diaDoMes),'mm/yyyy'), 'mm/yyyy')  ";
        }
        else{
            retorno = " and to_date('" + DataUtil.getDataFormatada(SistemaFacade.getDataCorrente()) + "', 'dd/MM/yyyy') between vinculo.iniciovigencia " +
                " and coalesce(vinculo.finalvigencia, to_date('" + DataUtil.getDataFormatada(SistemaFacade.getDataCorrente()) + "', 'dd/MM/yyyy')) ";
        }

        if (!modalidades.isEmpty()) {
            retorno += " and contrato.MODALIDADECONTRATOFP_ID  in (";
            filtros = "Vínculos (Modalidade): ";
            for (ModalidadeContratoFP modalidadeContratoFP : modalidades) {
                retorno += modalidadeContratoFP.getId() + ",";
                filtros += filtros = modalidadeContratoFP.getDescricao() + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 1) + ") ";
            filtros = filtros.substring(0, filtros.length() - 2);
            filtros += " - ";
        }


        if (efetivoCargoComissao) {
            retorno += " and exists( select 1 from CARGOCONFIANCA confianca " +
                "                     where confianca.CONTRATOFP_ID = vinculo.id " +
                "                       and to_date(to_char(:diaDoMes, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(confianca.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
                "                         and to_date(to_char(coalesce(confianca.FINALVIGENCIA, :diaDoMes),'mm/yyyy'), 'mm/yyyy'))" +
                "  and modalidade.codigo = 1 ";
            filtros = " Efetivo Cargo em Comissão ";
            filtros += " - ";
        }


        if (!tiposRegimes.isEmpty()) {
            filtros += " Tipo de Regime: ";
            retorno += " and contrato.tiporegime_id in(";
            for (TipoRegime tipoRegime : tiposRegimes) {
                retorno += tipoRegime.getId() + ",";
                filtros += tipoRegime.getCodigo() + " - " + tipoRegime.getDescricao() + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 1) + ") ";
            filtros = filtros.substring(0, filtros.length() - 2);
        }
        if (hierarquiaOrganizacional != null) {
            retorno += " and ho.codigo like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%'";
            filtros += " - Órgão: " + hierarquiaOrganizacional;
        }
        if (mesEAno != null) {
            filtros += "  Mês/Ano : ";
            filtros += DataUtil.getDataFormatada(mesEAno, "MM/yyyy");
        }

        filtros += Strings.isNullOrEmpty(filtros) ? "Todos." : ".";
        return retorno;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<TipoRegime> getTiposRegimes() {
        return tiposRegimes;
    }

    public void setTiposRegimes(List<TipoRegime> tiposRegimes) {
        this.tiposRegimes = tiposRegimes;
    }

    public Date getMesEAno() {
        return mesEAno;
    }

    public void setMesEAno(Date mesEAno) {
        this.mesEAno = mesEAno;
    }

    public Boolean getEfetivoCargoComissao() {
        return efetivoCargoComissao;
    }


    public void setEfetivoCargoComissao(Boolean efetivoCargoComissao) {
        this.efetivoCargoComissao = efetivoCargoComissao;
    }
}
