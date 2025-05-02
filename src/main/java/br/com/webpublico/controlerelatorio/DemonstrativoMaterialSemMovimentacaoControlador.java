package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidadesauxiliares.DemonstrativoMaterialSemMovimentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.DemonstrativoMaterialSemMovimentacaoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HardRock on 25/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-demonst-materias-sem-mov", pattern = "/demonstrativo-materiais-sem-movimentacoes/", viewId = "/faces/administrativo/materiais/relatorios/demonstrativo-material-sem-movimentacao.xhtml")})
public class DemonstrativoMaterialSemMovimentacaoControlador extends AbstractReport {

    @EJB
    private DemonstrativoMaterialSemMovimentacaoFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private LocalEstoque localEstoque;
    private Material material;
    private Date dataInicial;
    private Date dataFinal;
    private String filtro;

    @URLAction(mappingId = "novo-demonst-materias-sem-mov", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        hierarquiaOrganizacional = null;
        material = null;
        localEstoque = null;
        filtro = "";
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            setGeraNoDialog(true);
            String arquivoJasper = "DemonstrativoMaterialSemMovimentacao.jasper";
            HashMap parametros = new HashMap();
            parametros.put("USER", getNomeUsuarioLogado());
            parametros.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("IMAGEM", getCaminhoImagem());
            parametros.put("FILTROS", filtro);
            parametros.put("MODULO", "Materiais");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSql());
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomerPDF(), arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final  deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser superior ou igual a data inicial.");
        }
        ve.lancarException();
    }

    public List<LocalEstoque> buscarLocalEstoque(String filtro) {
        if (hierarquiaOrganizacional != null) {
            return facade.getLocalEstoqueFacade().completarLocaisEstoquePrimeiroNivel(filtro, hierarquiaOrganizacional.getSubordinada());
        }
        return facade.getLocalEstoqueFacade().completarLocaisEstoquePrimeiroNivel(filtro);
    }

    private String getNomerPDF() {
        return "DEMONSTRATIVO-MATERIAL-SEM-MOVIMENTACAO";
    }

    private List<DemonstrativoMaterialSemMovimentacao> gerarSql() {
        return facade.gerarConsulta(montarCondicao(), dataInicial, dataFinal);
    }

    private String montarCondicao() {
        filtro = "";
        StringBuilder filtro = new StringBuilder();
        StringBuilder and = new StringBuilder();
        if (dataInicial != null && dataFinal != null) {
            filtro.append("Período: ").append(DataUtil.getDataFormatada(dataInicial)).append(" à ").append(DataUtil.getDataFormatada(dataFinal)).append(", ");
        }
        if (hierarquiaOrganizacional != null) {
            and.append(" and adm.id = ").append(hierarquiaOrganizacional.getSubordinada().getId());
            filtro.append("Unidade: ").append(hierarquiaOrganizacional.getCodigo()).append(" - ").append(hierarquiaOrganizacional.getDescricao()).append(", ");
        }
        if (localEstoque != null) {
            and.append(" and l.id = ").append(localEstoque.getId());
            filtro.append("Local de Estoque: ").append(localEstoque).append(", ");
        }
        if (material != null) {
            and.append(" and mat.id = ").append(material.getId());
            filtro.append("Material: ").append(material).append(", ");
        }
        this.filtro = filtro.toString().substring(0, filtro.toString().length() - 2);
        return and.toString();
    }

    public String getNomeUsuarioLogado() {
        if (facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            return facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return facade.getSistemaFacade().getUsuarioCorrente().getUsername();
        }
    }

    public void definirValoresComoNull() {
        setHierarquiaOrganizacional(null);
        setLocalEstoque(null);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
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
}
