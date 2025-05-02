package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.AcaoPPA;
import br.com.webpublico.entidades.AcaoPrincipal;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.AcaoPrincipalFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-vinculacoes-ppa", pattern = "/relatorio/ppa/vinculacoes-ppa/", viewId = "/faces/financeiro/relatorio/relatoriovinculacoesppa.xhtml")
})
public class RelatorioVinculacoesPPAControlador implements Serializable {
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private AcaoPPA acaoPPA;
    private AcaoPrincipal acaoPrincipal;
    private ProgramaPPA programaPPA;
    private Apresentacao apresentacao;

    public RelatorioVinculacoesPPAControlador() {
    }

    @URLAction(mappingId = "relatorio-vinculacoes-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        acaoPPA = null;
        acaoPrincipal = null;
        programaPPA = null;
        apresentacao = Apresentacao.COM_DESPESA;
    }

    public List<SelectItem> getListaApresentacao() {
        return Util.getListSelectItemSemCampoVazio(Apresentacao.values());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("isSemDespesa", Apresentacao.SEM_DESPESA.equals(apresentacao));
            dto.setNomeRelatorio("Vinculações do PPA Revisado em Relação a LDO e LOA do Exercício Anterior");
            dto.setApi("contabil/vinculacoes-ppa/");
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

    public List<ProgramaPPA> completaProgramaPPA(String parte) {
        return programaPPAFacade.listaFiltrando(parte.trim(), "denominacao");
    }

    public List<AcaoPPA> completaAcaoPPA(String parte) {
        return projetoAtividadeFacade.listaProjetoAtividadePorAcaoPrincipal(acaoPrincipal, parte.trim());
    }

    public List<AcaoPrincipal> completaAcaoPrincipal(String parte) {
        return acaoPrincipalFacade.listaAcaoPPAPorPrograma(programaPPA, parte.trim());
    }

    public void setaNullAcaoPrincipal() {
        acaoPrincipal = null;
        acaoPPA = null;
    }

    public void setaNullAcao() {
        acaoPPA = null;
    }

    private String montarCondicao() {
        StringBuilder stb = new StringBuilder();
        String juncao = " WHERE ";
        if (programaPPA != null) {
            stb.append(juncao).append(" PROG.ID = ").append(programaPPA.getId());
            juncao = " AND ";
        }
        if (acaoPrincipal != null) {
            stb.append(juncao).append(" AC.ID = ").append(acaoPrincipal.getId());
            juncao = " AND ";
        }
        if (acaoPPA != null) {
            stb.append(juncao).append(" ACPPA.ID = ").append(acaoPPA.getId());
        }
        return stb.toString();
    }

    private enum Apresentacao implements EnumComDescricao {
        COM_DESPESA("Com Despesa"),
        SEM_DESPESA("Sem Despesa");
        private String descricao;

        Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }
}
