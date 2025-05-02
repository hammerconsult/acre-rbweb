package br.com.webpublico.controle.rh.portal;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.TipoRelatorioAtualizacaoServidor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by william on 06/11/17.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "atualizacao-servidores-portal", pattern = "/relatorio/atualizacao-servidores-portal/", viewId = "/faces/rh/portal/relatorio/atualizacao-servidores-portal.xhtml")
})
public class RelatorioAtualizacaoServidoresPortalControlador extends AbstractReport implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioAtualizacaoServidoresPortalControlador.class);
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    @EJB
    private SistemaFacade sistemaFacade;
    private String filtros;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais = Lists.newArrayList();
    private HierarquiaOrganizacional hierarquiaOrganizacionalFiltro;
    private Date inicioVigencia;
    private Date fimVigencia;
    private ModalidadeContratoFP modalidadeContratoFP;
    private Boolean todosVinculos;
    private TipoRelatorioAtualizacaoServidor tipoRelatorioAtualizacaoServidor;

    public RelatorioAtualizacaoServidoresPortalControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "atualizacao-servidores-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = "";
        hierarquiaOrganizacionalFiltro = new HierarquiaOrganizacional();
        todosVinculos = true;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
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


    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacionalFiltro() {
        return hierarquiaOrganizacionalFiltro;
    }

    public void setHierarquiaOrganizacionalFiltro(HierarquiaOrganizacional hierarquiaOrganizacionalFiltro) {
        this.hierarquiaOrganizacionalFiltro = hierarquiaOrganizacionalFiltro;
    }

    private void atribuirFiltrosRelatorio() {
        filtros = "";
        if (inicioVigencia != null && fimVigencia != null) {
            filtros += "Vigência Inicial: " + DataUtil.getDataFormatada(inicioVigencia);
            filtros += "Início Final: " + DataUtil.getDataFormatada(fimVigencia);
        }
        if (hierarquiaOrganizacionalFiltro != null) {
            filtros += "Vínculo: " + hierarquiaOrganizacionalFiltro.getCodigo() + " - " + hierarquiaOrganizacionalFiltro.getDescricao();
        }
        if (!todosVinculos) {
            filtros += "Vínculo: " + modalidadeContratoFP.getDescricao();
        } else {
            filtros += "Vínculo: Todos";
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try{
            validarCampos();
            atribuirFiltrosRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Recadastramento dos Servidores");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Administração");
            dto.adicionarParametro("FILTROS", filtros != null ? filtros.length() > 2000 ? filtros.substring(0, 2000) + "..." : filtros : "");
            dto.adicionarParametro("TITULO_HEADER", "");
            dto.adicionarParametro("TITULO_FOOTER", "");
            dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("NOMERELATORIO", "Recadastramento dos Servidores");
            dto.adicionarParametro("TIPO_RELATORIO", "");
            dto.adicionarParametro("USER", StringUtil.primeiroCaracterMaiusculo(sistemaFacade.getUsuarioCorrente().getLogin()), true);
            dto.adicionarParametro("DATA_INICIO", inicioVigencia);
            dto.adicionarParametro("DATA_FINAL", fimVigencia);
            dto.adicionarParametro("HIERARQUIA_ORGANIZACIONAL_CODIGO", hierarquiaOrganizacionalFiltro != null ? hierarquiaOrganizacionalFiltro.getCodigoSemZerosFinais() : null);
            dto.adicionarParametro("TIPO_RELATORIO_SELECIONADO", tipoRelatorioAtualizacaoServidor.getToDto());
            dto.adicionarParametro("MODALIDADE_ID", !todosVinculos ? modalidadeContratoFP.getId() : null);
            dto.adicionarParametro("DATA_OPERACAO", sistemaFacade.getDataOperacao());
            dto.setApi("rh/relatorio-atualizacao-servidores-portal/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (inicioVigencia != null && fimVigencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Foi informado a Vigência Inicial, é necessário informar a Vigência Final.");
        }
        if (inicioVigencia == null && fimVigencia != null) {
            ve.adicionarMensagemDeCampoObrigatorio("Foi informado a Vigência Final, é necessário informar a Vigência Inicial.");
        }
        if ((inicioVigencia != null && fimVigencia != null) && inicioVigencia.after(fimVigencia)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Vigência Inicial não pode ser maior que a Vigência Final");
        }
        if (!todosVinculos && modalidadeContratoFP == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe algum vínculo.");
        }
        ve.lancarException();
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }


    public List<SelectItem> getModalidadeContratoServidor() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP object : modalidadeContratoFPFacade.modalidadesAtivas()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRelatorioServidor() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorioAtualizacaoServidor object : TipoRelatorioAtualizacaoServidor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public TipoRelatorioAtualizacaoServidor getTipoRelatorioAtualizacaoServidor() {
        return tipoRelatorioAtualizacaoServidor;
    }

    public void setTipoRelatorioAtualizacaoServidor(TipoRelatorioAtualizacaoServidor tipoRelatorioAtualizacaoServidor) {
        this.tipoRelatorioAtualizacaoServidor = tipoRelatorioAtualizacaoServidor;
    }
}
