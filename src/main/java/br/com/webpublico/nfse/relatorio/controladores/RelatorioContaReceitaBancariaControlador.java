package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.RelatorioContaReceitaBancariaDTO;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.util.Strings;

import javax.faces.bean.ManagedBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-conta-receita-bancaria",
        pattern = "/nfse/relatorio/conta-receita-bancaria/",
        viewId = "/faces/tributario/nfse/relatorio/conta-receita-bancaria.xhtml")
})
public class RelatorioContaReceitaBancariaControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private CadastroEconomico cadastroEconomico;
    private String cnpjInicial;
    private String cnpjFinal;

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    @URLAction(mappingId = "novo-relatorio-conta-receita-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Relatório das Contas de Receitas Bancárias");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("TITULO", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("FILTROS", montarFiltros());
            dto.adicionarParametro("CONDICAO", montarClausulaWhere());
            dto.setApi("tributario/nfse/conta-receita-bancaria/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarClausulaWhere() {
        StringBuilder sql = new StringBuilder();
        String clausula = " where ";
        if (cadastroEconomico != null) {
            sql.append(clausula).append(" ce.id = ").append(cadastroEconomico.getId());
            clausula = " and ";
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            sql.append(clausula).append(" replace(replace(replace(pj.cnpj, '/', ''), '/', ''), '/', '') >= '").append(StringUtil.retornaApenasNumeros(cnpjInicial)).append("'");
            clausula = " and ";
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            sql.append(clausula).append(" replace(replace(replace(pj.cnpj, '/', ''), '/', ''), '/', '') <= '").append(StringUtil.retornaApenasNumeros(cnpjFinal)).append("'");
            clausula = " and ";
        }
        return sql.toString();
    }

    private String montarFiltros() {
        StringBuilder sql = new StringBuilder();
        if (cadastroEconomico != null) {
            sql.append(" Cadastro Econômico: ").append(cadastroEconomico).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            sql.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            sql.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        return sql.toString();
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}
