package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorioPessoaFContabil", pattern = "/relatorio/pessoa-fisica/", viewId = "/faces/financeiro/relatorio/relatoriopessoafcontabil.xhtml")
})
public class RelatorioPessoaFContabilControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private String nome;
    private String cpf;

    public RelatorioPessoaFContabilControlador() {
    }

    @URLAction(mappingId = "relatorioPessoaFContabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPessoasFisicas() {
        this.nome = null;
        this.cpf = null;
    }

    protected void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(nome) && Strings.isNullOrEmpty(cpf)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione um filtro ou mais para gerar o relatório");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            gerarRelatorio(montarParametros(), sistemaFacade.getUsuarioCorrente());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(cpf)) {
            parametros.add(new ParametrosRelatorios("replace(replace(replace(pf.cpf,'.',''),'-',''),'/','')", ":cpf", null, OperacaoRelatorio.IGUAL, cpf.trim().replace(".", "").replace("-", "").replace("/", ""), null, 1, false));
        }
        if (!Strings.isNullOrEmpty(nome)) {
            parametros.add(new ParametrosRelatorios("upper(pf.nome)", ":nome", null, OperacaoRelatorio.LIKE, "%"+ nome.trim().toUpperCase()+"%", null, 1, false));
        }
        return parametros;
    }

    public static void gerarRelatorio(List<ParametrosRelatorios> parametros, UsuarioSistema usuarioCorrente) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("USER", usuarioCorrente.getNome(), false);
        dto.setNomeRelatorio("RELATÓRIO DE PESSOA(S) FÍSICA(S) - CREDOR");
        dto.setApi("contabil/pessoa-fisica-contabil/");
        ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

}
