package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 10/07/14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-aviso-licenca-premio", pattern = "/relatorio/aviso-licenca-premio/", viewId = "/faces/rh/relatorios/relatorioavisolicencapremio.xhtml")
})
public class RelatorioAvisoLicencaPremioControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    private Date dtInicial;
    private Date dtFinal;

    private static final Logger logger = LoggerFactory.getLogger(RelatorioAvisoLicencaPremioControlador.class);

    public RelatorioAvisoLicencaPremioControlador() {
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-aviso-licenca-premio/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE AVISO DE LICENÇA PREMIO");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("NOMERELATORIO", "AVISO DE LICENÇA PRÊMIO");
        dto.adicionarParametro("SQL", gerarSql());
        dto.adicionarParametro("DATAOPERACAO",  UtilRH.getDataOperacao().getTime());
        dto.adicionarParametro("CODIGOHO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");

        return dto;
    }

    private String gerarSql() {
        String retorno = " and (trunc(CON.DATAINICIAL) between  to_date('" + formataData(dtInicial) + "','dd/MM/yyyy') and to_date('" + formataData(dtFinal) + "','dd/MM/yyyy')) ";
        return retorno;
    }

    @URLAction(mappingId = "relatorio-aviso-licenca-premio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.hierarquiaOrganizacionalSelecionada = null;
        this.dtFinal = null;
        this.dtInicial = null;
    }

    private String formataData(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(data);
    }

    private void validarCampos(){
        ValidacaoException ve = new ValidacaoException();
        if (this.hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (this.dtInicial == null ) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (this.dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        if (dtInicial != null && dtFinal != null && this.dtInicial.after(this.dtFinal)) {
            ve.adicionarMensagemDeCampoObrigatorio("A data inicial deve ser anterior a data final.");
        }
        ve.lancarException();
    }

    public ConverterAutoComplete getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
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
