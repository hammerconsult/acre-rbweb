package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoMaterial;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.negocios.AtaRegistroPrecoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SolicitacaoMaterialFacade;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by israeleriston on 05/05/15.
 */
@ManagedBean(name = "relatorioAtaRegistroPrecoVigenteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioAtaRegistroPrecoVigente", pattern = "/licitacao/relatorio-registro-ata-preco-vigente/",
                viewId = "/faces/administrativo/relatorios/relatorio-ata-registro-preco-vigente.xhtml")
})
public class RelatorioAtaRegistroPrecoVigente extends AbstractReport implements Serializable {

    private UnidadeOrganizacional unidadeOrganizacional;
    private SolicitacaoMaterial solicitacaoMaterial;
    private AtaRegistroPreco ataRegistroPreco;
    private Long numeroAta;
    private Date dataInicio;
    private Date dataVencimento;
    private String filtros;

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public static final String ARQUIVO_JASPER = "RelatorioAtaRegistroPrecoVigente.jasper";


    public RelatorioAtaRegistroPrecoVigente() {
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    @Override
    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public void setUnidadeOrganizacionalFacade(UnidadeOrganizacionalFacade unidadeOrganizacionalFacade) {
        this.unidadeOrganizacionalFacade = unidadeOrganizacionalFacade;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public Long getNumeroAta() {
        return numeroAta;
    }

    public void setNumeroAta(Long numeroAta) {
        this.numeroAta = numeroAta;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }


    public void gerarRelatorio() throws JRException, IOException {


        HashMap parameter = new HashMap();
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        parameter.put("BRASAO", super.getCaminhoImagem());
        parameter.put("SECRETARIA", sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao().toString());
        parameter.put("NOMERELATORIO", "Relatório Ata de Registro de Preço Vigente");
        parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
        parameter.put("MODULO", "Administrativo");
        parameter.put("FILTROS", filtros);
        parameter.put("WHERE", isCriterio());
        gerarRelatorio(ARQUIVO_JASPER, parameter);

    }

    @URLAction(mappingId = "relatorioAtaRegistroPrecoVigente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        unidadeOrganizacional = null;
        ataRegistroPreco = null;
        numeroAta = null;
        dataInicio = null;
        dataVencimento = null;
    }

    public List<UnidadeOrganizacional> getCompletaUnidade(String parametro) {
        List<UnidadeOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativaVigente : hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalAdministrativa(parametro.trim())) {
            if (!retorno.contains(hierarquiaOrganizacionalAdministrativaVigente.getSubordinada())) {
                retorno.add(hierarquiaOrganizacionalAdministrativaVigente.getSubordinada());
            }
        }
        return retorno;
    }

    private String isCriterio() {

        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        if (unidadeOrganizacional != null) {
            sb.append(juncao).append(" uo.descricao = ").append("  '" + unidadeOrganizacional.getDescricao() + "' ");

            filtros += "Unidade Organizacional: " + unidadeOrganizacional.getDescricao() + "  ";
        }
        if (numeroAta != null) {
            sb.append(juncao).append(" ata.numero = " + numeroAta);

            filtros += " Número Ata: " + numeroAta + " ";
        }
        if (dataInicio != null && dataVencimento != null) {
            sb.append(juncao).append(" trunc(ata.datainicio) >= to_date('" + DataUtil.getDataFormatada(dataInicio) + "','dd/MM/yyyy') ")
                .append(juncao).append(" trunc(ata.datavencimento) <= ").append(" to_date('" + DataUtil.getDataFormatada(dataVencimento) + "','dd/MM/yyyy')");

            filtros += " Périodo: " + DataUtil.getDataFormatada(dataInicio) + "  á  " + DataUtil.getDataFormatada(dataVencimento) + " ";
        }
        if (dataInicio == null && dataVencimento != null) {
            sb.append(juncao).append(" trunc(ata.datavencimento) <= to_date(' " + DataUtil.getDataFormatada(dataVencimento) + " ', 'dd/MM/yyyy') ");
            filtros += "Data Vencimento: " + DataUtil.getDataFormatada(dataVencimento) + " ";
        }
        if (dataVencimento == null && dataInicio != null) {
            sb.append(juncao).append(" trunc(ata.datainicio) >= to_date(' " + DataUtil.getDataFormatada(dataInicio) + " ', 'dd/MM/yyyy') ");

            filtros += " Data Inicio: " + DataUtil.getDataFormatada(dataInicio) + " ";
        }

        return sb.toString();
    }
}
