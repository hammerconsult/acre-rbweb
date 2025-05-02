package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.rh.TipoRelatorioServidor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alex
 * @since 01/02/2017 17:50
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidor-exonerado", pattern = "/relatorio/servidores-nomeados-exonerados/", viewId = "/faces/rh/relatorios/relatorioServidoresNomeadosExonerados.xhtml")
})
public class RelatorioServidorNomeadoExoneradoControlador extends AbstractReport {
    @EJB
    private ModalidadeContratoFPFacade modalidadeFace;
    private Date inicio;
    private Date fim;
    private HierarquiaOrganizacional lotacao;
    private List<ModalidadeContratoFP> modalidades;
    private ModalidadeContratoFP modalidadeContratoFP;
    private boolean filtrarHierarquiaAtiva;
    private TipoRelatorioServidor tipoRelatorioServidor;
    private List<ModalidadeContratoFP> modalidadesExistentes;

    public RelatorioServidorNomeadoExoneradoControlador() {
        filtrarHierarquiaAtiva = true;
    }

    @URLAction(mappingId = "relatorio-servidor-exonerado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        modalidades = new ArrayList<>();
        filtrarHierarquiaAtiva = true;
        tipoRelatorioServidor = TipoRelatorioServidor.NOMEADOS;
        lotacao = null;
        inicio = new Date();
        fim = new Date();
        setGeraNoDialog(true);
    }


    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório: " + e);
        }
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório: " + e);
        }
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE SERVIDORES NOMEADOS EXONERADOS");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("ORGAO", "Recursos Humanos");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("DESCRICAOVIGENCIA", getNomeColunaVigencia());
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("PERIODO", "Período " + DataUtil.getDataFormatada(inicio) + " a " + DataUtil.getDataFormatada(fim));
        dto.adicionarParametro("INICIO_EXONERACAO", DataUtil.getDataFormatada(inicio));
        dto.adicionarParametro("FIM_EXONERACAO", DataUtil.getDataFormatada(fim));
        dto.adicionarParametro("LOTACAO", lotacao.getCodigoSemZerosFinais());
        dto.adicionarParametro("IS_FILTRAR_HIERARQUIA", filtrarHierarquiaAtiva);
        dto.adicionarParametro("DATAOPERACAO", getSistemaFacade().getDataOperacao().getTime());
        dto.adicionarParametro("CONCATENAR_MODALIDADES", concatenarModalidades());
        dto.adicionarParametro("TIPO_RELATORIO_SELECIONADO", tipoRelatorioServidor.getToDto());
        dto.setApi("rh/relatorio-servidor-nomeado-exonerado/");
        return dto;
    }

    private String concatenarModalidades() {
        String retorno = "";
        if (modalidades == null || modalidades.isEmpty()) {
            retorno = " SELECT DISTINCT id FROM MODALIDADECONTRATOFP ";
        } else {
            for (ModalidadeContratoFP modalidade : modalidades) {
                retorno += modalidade.getId() + ",";
            }
        }
        return retorno.substring(0, retorno.length() - 1);
    }

    private String getNomeColunaVigencia() {
        if (TipoRelatorioServidor.EXONERADO.equals(tipoRelatorioServidor)) {
            return "Exoneração";
        }
        return "Nomeação";
    }

    private String getNomeRelatorio() {
        if (TipoRelatorioServidor.EXONERADO.equals(tipoRelatorioServidor)) {
            return "SERVIDORES EXONERADOS";
        }
        return "SERVIDORES NOMEADOS";
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoRelatorioServidor.values(), false);
    }


    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (inicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Inicio do período é obrigatório!");
        }
        if (fim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fim do período é obrigatório!");
        }
        if (tipoRelatorioServidor == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo do Relatório é obrigatório!");
        }
        if (lotacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório!");
        }
        if (inicio != null && fim != null && inicio.after(fim)) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de Inicio deve ser inferior a data de Fim do período!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getModalidadesContrato() {
        if (modalidadesExistentes == null) {
            modalidadesExistentes = modalidadeFace.lista();
        }
        return Util.getListSelectItem(modalidadesExistentes);
    }

    public void adicionarModalidade() {
        if (!modalidades.contains(modalidadeContratoFP)) {
            modalidades.add(modalidadeContratoFP);
            modalidadeContratoFP = null;
        }
    }

    public void removerModalidade(ModalidadeContratoFP modalidadeContratoFP) {
        if (modalidades.contains(modalidadeContratoFP)) {
            modalidades.remove(modalidadeContratoFP);
        }
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public HierarquiaOrganizacional getLotacao() {
        return lotacao;
    }

    public void setLotacao(HierarquiaOrganizacional lotacao) {
        this.lotacao = lotacao;
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public List<ModalidadeContratoFP> getModalidadesExistentes() {
        return modalidadesExistentes;
    }

    public void setModalidadesExistentes(List<ModalidadeContratoFP> modalidadesExistentes) {
        this.modalidadesExistentes = modalidadesExistentes;
    }

    public boolean isFiltrarHierarquiaAtiva() {
        return filtrarHierarquiaAtiva;
    }

    public void setFiltrarHierarquiaAtiva(boolean filtrarHierarquiaAtiva) {
        this.filtrarHierarquiaAtiva = filtrarHierarquiaAtiva;
    }

    public TipoRelatorioServidor getTipoRelatorioServidor() {
        return tipoRelatorioServidor;
    }

    public void setTipoRelatorioServidor(TipoRelatorioServidor tipoRelatorioServidor) {
        this.tipoRelatorioServidor = tipoRelatorioServidor;
    }

}
