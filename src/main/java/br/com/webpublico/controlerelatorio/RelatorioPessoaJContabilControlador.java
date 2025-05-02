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
    @URLMapping(id = "relatorioPessoaJContabil", pattern = "/relatorio/pessoa-juridica/", viewId = "/faces/financeiro/relatorio/relatoriopessoajcontabil.xhtml")
})
public class RelatorioPessoaJContabilControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private String razaoSocial;
    private String nomeReduzido;
    private String nomeFantasia;
    private String cnpj;

    public RelatorioPessoaJContabilControlador() {
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    @URLAction(mappingId = "relatorioPessoaJContabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPessoasJuridicas() {
        this.razaoSocial = null;
        this.nomeReduzido = null;
        this.nomeFantasia = null;
        this.cnpj = null;
    }

    public static void gerarRelatorio(List<ParametrosRelatorios> parametros, UsuarioSistema usuarioSistema) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", usuarioSistema.getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
        dto.setNomeRelatorio("RELATÓRIO DE PESSOA(S) JURÍDICA(S) - CREDOR");
        dto.setApi("contabil/pessoa-juridica/");
        ReportService.getInstance().gerarRelatorio(usuarioSistema, dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(razaoSocial) && Strings.isNullOrEmpty(nomeReduzido) && Strings.isNullOrEmpty(nomeFantasia) && Strings.isNullOrEmpty(cnpj)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione um filtro ou mais para gerar o relatório");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(cnpj)) {
            parametros.add(new ParametrosRelatorios("replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')", ":cnpj", null, OperacaoRelatorio.IGUAL, cnpj.trim().replace(".", "").replace("-", "").replace("/", ""), null, 1, false));
        }
        if (!Strings.isNullOrEmpty(razaoSocial)) {
            parametros.add(new ParametrosRelatorios("upper(pj.razaoSocial)", ":razaoSocial", null, OperacaoRelatorio.LIKE, "%" + razaoSocial.trim().toUpperCase() + "%", null, 1, false));
        }
        if (!Strings.isNullOrEmpty(razaoSocial)) {
            parametros.add(new ParametrosRelatorios("upper(pj.nomeReduzido)", ":nomeReduzido", null, OperacaoRelatorio.LIKE, "%" + nomeReduzido.trim().toUpperCase() + "%", null, 1, false));
        }
        if (!Strings.isNullOrEmpty(nomeFantasia)) {
            parametros.add(new ParametrosRelatorios("upper(pj.nomeReduzido)", ":nomeReduzido", null, OperacaoRelatorio.LIKE, "%" + nomeFantasia.trim().toUpperCase() + "%", null, 1, false));
        }
        return parametros;
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
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
