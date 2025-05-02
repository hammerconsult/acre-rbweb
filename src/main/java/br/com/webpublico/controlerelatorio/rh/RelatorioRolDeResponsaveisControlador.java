package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rol-de-responsaveis", pattern = "/relatorio/rol-de-responsaveis/", viewId = "/faces/rh/relatorios/relatoriorolderesponsaveis.xhtml")
})
public class RelatorioRolDeResponsaveisControlador {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioRolDeResponsaveisControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquias;
    private ModalidadeContratoFP modalidadeContratoFP;
    private List<ModalidadeContratoFP> modalidades;
    private String filtrosUtilizados;
    private Boolean buscarCargosEmComissao;
    private Boolean buscarAcessoSubsidio;
    private Boolean todasModalidades;

    @URLAction(mappingId = "relatorio-rol-de-responsaveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquias = Lists.newArrayList();
        modalidades = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        modalidadeContratoFP = null;
        dataInicial = new Date();
        dataFinal = new Date();
        buscarCargosEmComissao = true;
        buscarAcessoSubsidio = true;
        todasModalidades = false;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/rol-de-responsaveis/");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
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
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("ROL DE RESPONSÁVEIS");
        dto.adicionarParametro("ORGAO", "MUNICÍPIO DE RIO BRANCO");
        dto.adicionarParametro("ANO", sistemaFacade.getExercicioCorrente().getAno());
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("dataInicial", dataInicial.getTime());
        dto.adicionarParametro("dataFinal", dataFinal.getTime());
        dto.adicionarParametro("filtros", gerarFiltrosSql());
        if (filtrosUtilizados.length() > 500) {
            filtrosUtilizados = filtrosUtilizados.substring(0, 497) + "...";
        }
        dto.adicionarParametro("FILTROS_UTILIZADOS", "Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " - " + filtrosUtilizados);
        return dto;
    }

    private String gerarFiltrosSql() {
        String filtros = "";
        filtrosUtilizados = "";
        if (hierarquias != null && !hierarquias.isEmpty()) {
            filtros += " and (";
            filtrosUtilizados += " Secretaria(s): ";
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                filtros += " vw.codigo like '" + hierarquia.getCodigoSemZerosFinais() + "%' or ";
                filtrosUtilizados += hierarquia.getCodigo() + ", ";
            }
            filtros = filtros.substring(0, filtros.length() - 3) + ") ";
            filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
        }
        if ((modalidades != null && !modalidades.isEmpty()) || buscarCargosEmComissao || buscarAcessoSubsidio) {
            filtros += " and ( ";
            if ((modalidades != null && !modalidades.isEmpty())) {
                filtros += " moda.id in (";
                filtrosUtilizados += " Modalidade(s): ";
                for (ModalidadeContratoFP modalidadeContratoFP : modalidades) {
                    filtros += modalidadeContratoFP.getId() + ",";
                    filtrosUtilizados += modalidadeContratoFP.getCodigo() + " - " + modalidadeContratoFP.getDescricao() + ", ";
                }
                filtros = filtros.substring(0, filtros.length() - 1) + ") ";
                filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
                if (buscarCargosEmComissao || buscarAcessoSubsidio) {
                    filtros += " or ";
                } else {
                    filtrosUtilizados += " -";
                }
            }
            if (buscarCargosEmComissao) {
                filtrosUtilizados += " Buscar Acesso a Cargos de Comissão?: Sim ";

                filtros += " v.id in (select ac.CONTRATOFP_ID " +
                    "            from CARGOCONFIANCA ac " +
                    "            where ac.CONTRATOFP_ID = v.id " +
                    "                  and (ac.INICIOVIGENCIA between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy')  " +
                    "                    or  ac.FINALVIGENCIA between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy'))" +
                    "                    or to_date(:dataInicial, 'dd/MM/yyyy') between ac.INICIOVIGENCIA and coalesce(ac.FINALVIGENCIA, sysdate)) ";
                if (buscarAcessoSubsidio) {
                    filtros += " or ";
                }
            }
            if (buscarAcessoSubsidio) {
                filtrosUtilizados += " Buscar Acesso a Subsídio?: Sim ";

                filtros += " v.id in  (select subsidio.CONTRATOFP_ID from ACESSOSUBSIDIO subsidio " +
                    " where subsidio.CONTRATOFP_ID = v.id " +
                    " and (subsidio.INICIOVIGENCIA between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
                    " or  subsidio.FINALVIGENCIA between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy')) " +
                    " or to_date(:dataInicial, 'dd/MM/yyyy') between subsidio.INICIOVIGENCIA and coalesce(subsidio.FINALVIGENCIA, to_date(:dataInicial, 'dd/MM/yyyy')))";
            }
            filtros += ")";
        }
        return filtros;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public StreamedContent gerarExcel() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("NOMERELATORIO", "ROL DE RESPONSÁVEIS");
            dto.setApi("rh/rol-de-responsaveis/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "ROL-DE-RESPONSÁVEIS.xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), dataFinal);
    }

    public void adicionarHierarquia() {
        try {
            validarHierarquia();
            Util.adicionarObjetoEmLista(hierarquias, hierarquiaOrganizacional);
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarHierarquia() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Secretaria deve ser informado.");
        }
        ve.lancarException();
    }

    public List<ModalidadeContratoFP> completarModalidades(String parte) {
        return modalidadeContratoFPFacade.buscarModalidadesPorCodigoOrDescricao(parte.trim());
    }

    public void adicionarTodasModalidades() {
        if (todasModalidades) {
            for (ModalidadeContratoFP mod : modalidadeContratoFPFacade.buscarTodasAsModalidades()) {
                Util.adicionarObjetoEmLista(modalidades, mod);
            }
        } else {
            modalidades = Lists.newArrayList();
        }
    }

    public void adicionarModalidade() {
        try {
            validarModalidade();
            Util.adicionarObjetoEmLista(modalidades, modalidadeContratoFP);
            modalidadeContratoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarModalidade() {
        ValidacaoException ve = new ValidacaoException();
        if (modalidadeContratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Modalidade de Contrato deve ser informado.");
        }
        ve.lancarException();
    }

    public void removerHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        hierarquias.remove(hierarquiaOrganizacional);
    }

    public void removerModalidade(ModalidadeContratoFP modalidade) {
        modalidades.remove(modalidade);
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    public Boolean getBuscarCargosEmComissao() {
        return buscarCargosEmComissao;
    }

    public void setBuscarCargosEmComissao(Boolean buscarCargosEmComissao) {
        this.buscarCargosEmComissao = buscarCargosEmComissao;
    }

    public Boolean getTodasModalidades() {
        return todasModalidades;
    }

    public void setTodasModalidades(Boolean todasModalidades) {
        this.todasModalidades = todasModalidades;
    }

    public Boolean getBuscarAcessoSubsidio() {
        return buscarAcessoSubsidio;
    }

    public void setBuscarAcessoSubsidio(Boolean buscarAcessoSubsidio) {
        this.buscarAcessoSubsidio = buscarAcessoSubsidio;
    }
}
