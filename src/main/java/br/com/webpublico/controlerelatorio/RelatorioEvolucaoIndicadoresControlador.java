package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-evolucao-indicadores", pattern = "/relatorio-evolucao-indicadores/", viewId = "/faces/financeiro/relatorio/relatorioevolucaoindicadores.xhtml")
})
public class RelatorioEvolucaoIndicadoresControlador extends RelatorioContabilSuperControlador {

    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PPA ppa;
    private ProgramaPPA programaPPA;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean mostrarDataEUsuario;

    public RelatorioEvolucaoIndicadoresControlador() {
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getMostrarDataEUsuario() {
        return mostrarDataEUsuario;
    }

    public void setMostrarDataEUsuario(Boolean mostrarDataEUsuario) {
        this.mostrarDataEUsuario = mostrarDataEUsuario;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-EVOLUCAO-DOS-INDICADORES";
    }

    public List<PPA> completarPpas(String parte){
        return ppaFacade.buscarPpas(parte);
    }

    public List<ProgramaPPA> completarProgramasPPA(String parte){
        if(ppa != null) {
            return programaPPAFacade.buscarProgramasPorPPA(parte, ppa);
        }
        return programaPPAFacade.buscarProgramas(parte);
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getSistemaFacade().getDataOperacao());
    }

    public void limparCampos(){
        ppa = null;
        programaPPA = null;
        hierarquiaOrganizacional = null;
        mostrarDataEUsuario = false;
    }

    public void validar(){
        ValidacaoException ve = new ValidacaoException();
        if(ppa == null){
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um PPA");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validar();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("DATA", DataUtil.getDataFormatadaDiaHora(new Date()));
            dto.adicionarParametro("idPpa", ppa.getId());
            dto.adicionarParametro("mostrarDataEUsuario", mostrarDataEUsuario);
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            if (programaPPA != null) {
                dto.adicionarParametro("idPrograma", programaPPA.getId());
            }
            if (hierarquiaOrganizacional != null) {
                dto.adicionarParametro("idUnidade", hierarquiaOrganizacional.getSubordinada().getId());
            }
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/evolucao-indicadores/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
