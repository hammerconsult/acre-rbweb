package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.TipoBaixaBens;
import br.com.webpublico.entidadesauxiliares.SaidaMaterialNaoUtilizado;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioSaidaMaterialNaoUtilizadoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HardRock on 26/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelSaidaMaterialNaoUtilizado", pattern = "/relatorio/saida-material-nao-utilizado/", viewId = "/faces/administrativo/materiais/relatorios/saida-material-nao-utilizado.xhtml")})

//Todo: quando for passar para o webreport o nome correto é 'RelatorioSaidaMaterialDesincorporacao'
public class RelatorioSaidaMaterialNaoUtilizadoControlador extends AbstractReport {

    @EJB
    private RelatorioSaidaMaterialNaoUtilizadoFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private LocalEstoque localEstoque;
    private Material material;
    private Date dataReferencia;
    private TipoBaixaBens tipoBaixa;
    private String filtro;

    @URLAction(mappingId = "novoRelSaidaMaterialNaoUtilizado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataReferencia = new Date();
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        tipoBaixa = null;
        material = null;
        localEstoque = null;
        filtro = "";
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            setGeraNoDialog(true);
            String arquivoJasper = "RelatorioSaidaMaterialNaoUtilizado.jasper";
            HashMap parametros = new HashMap();
            parametros.put("USER", getNomeUsuarioLogado());
            parametros.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("IMAGEM", getCaminhoImagem());
            parametros.put("MODULO", "Materiais");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSql());
            parametros.put("FILTROS", filtro);
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomerPDF(), arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência deve ser informado.");
        }
        if (hierarquiaAdministrativa == null
            && hierarquiaOrcamentaria == null
            && localEstoque == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade administrativa e ou unidade orçamentária ou local de estoque deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposBaixa() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoBaixaBens tipoBaixaBens : facade.getTipoBaixaBensFacade().buscarFiltrandoPorTipoBemAndTipoIngresso("", TipoBem.ESTOQUE, TipoIngressoBaixa.DESINCORPORACAO)) {
            retorno.add(new SelectItem(tipoBaixaBens, tipoBaixaBens.getDescricao()));
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (hierarquiaAdministrativa != null) {
            return facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada(), getSistemaFacade().getDataOperacao());
        }
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getSistemaFacade().getDataOperacao());
    }

    public List<LocalEstoque> buscarLocalEstoque(String filtro) {
        if (hierarquiaAdministrativa != null) {
            return facade.getLocalEstoqueFacade().completarLocaisEstoquePrimeiroNivel(filtro, hierarquiaAdministrativa.getSubordinada());
        }
        return facade.getLocalEstoqueFacade().completarLocaisEstoquePrimeiroNivel(filtro);
    }

    private String getNomerPDF() {
        return "RELATORIO-SAIDA-MATERIAL-NAO-UTILIZADO";
    }

    private List<SaidaMaterialNaoUtilizado> gerarSql() {
        return facade.gerarConsulta(montarCondicao());
    }

    private String montarCondicao() {
        filtro = "";
        StringBuilder filtro = new StringBuilder();
        StringBuilder and = new StringBuilder();
        if (dataReferencia != null) {
            and.append("and trunc(saida.datasaida) <= to_date('").append(DataUtil.getDataFormatada(dataReferencia)).append("', 'dd/MM/yyyy')");
            filtro.append("Data de Referência: ").append(DataUtil.getDataFormatada(dataReferencia)).append(", ");
        }
        if (hierarquiaAdministrativa != null) {
            and.append(" and adm.id = ").append(hierarquiaAdministrativa.getSubordinada().getId());
            filtro.append("Unidade Administrativa: ").append(hierarquiaAdministrativa.getCodigo()).append(" - ").append(hierarquiaAdministrativa.getDescricao()).append(", ");
        }
        if (hierarquiaOrcamentaria != null) {
            and.append(" and orc.id = ").append(hierarquiaOrcamentaria.getSubordinada().getId());
            filtro.append("Unidade Orçamentária: ").append(hierarquiaOrcamentaria.getCodigo()).append(" - ").append(hierarquiaOrcamentaria.getDescricao()).append(", ");
        }
        if (localEstoque != null) {
            and.append(" and loc.id = ").append(localEstoque.getId());
            filtro.append("Local de Estoque: ").append(localEstoque).append(", ");
        }
        if (material != null) {
            and.append(" and mat.id = ").append(material.getId());
            filtro.append("Material: ").append(material).append(", ");
        }
        if (tipoBaixa != null) {
            and.append(" and tipo.id = ").append(tipoBaixa.getId());
            filtro.append("Tipo de Baixa: ").append(tipoBaixa.getDescricao()).append(", ");
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
        setHierarquiaAdministrativa(null);
        setHierarquiaOrcamentaria(null);
        setLocalEstoque(null);
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public TipoBaixaBens getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(TipoBaixaBens tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
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
}
