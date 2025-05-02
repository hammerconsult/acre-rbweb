/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.AuxRelatorioSaldoGrupoMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioSaldoGrupoMaterialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "relatorioSaldoGrupoMaterial")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioSaldoGrupoMaterial", pattern = "/relatorio-de-saldo-grupo-material/", viewId = "/faces/administrativo/relatorios/relatorio-saldo-grupo-material.xhtml")
})
public class RelatorioSaldoGrupoMaterial extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioSaldoGrupoMaterial.class);
    @EJB
    private RelatorioSaldoGrupoMaterialFacade relatorioSaldoGrupoMaterialFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquiaOrganizacionalSelecionadas;
    private Date dataReferencia;
    private String filtro;

    public RelatorioSaldoGrupoMaterial() {
        this.dataReferencia = new Date();
        hierarquiaOrganizacionalSelecionadas = Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return relatorioSaldoGrupoMaterialFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getSistemaFacade().getDataOperacao());
    }

    public void adicionarHierarquiaSelecionada() {
        if (hierarquiaOrganizacionalSelecionadas.contains(hierarquiaOrganizacional)) {
            FacesUtil.addOperacaoNaoPermitida("A Unidade já foi adicionada a lista!");
        } else {
            hierarquiaOrganizacionalSelecionadas.add(hierarquiaOrganizacional);
        }
        hierarquiaOrganizacional = null;
    }

    public void removerHierarquiaDaLista(HierarquiaOrganizacional hoRemover) {
        if (hierarquiaOrganizacionalSelecionadas.contains(hoRemover)) {
            hierarquiaOrganizacionalSelecionadas.remove(hoRemover);
        }
    }

    public void gerarRelatorio() {
        try {
            validarCamposObrigatorio();
            String arquivoJasper = "RelatorioSaldoGrupoMaterial.jasper";
            HashMap parametros = Maps.newHashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(montarFiltrosAndBuscarDados());
            parametros.put("MODULO", "PATRIMÔNIO");
            parametros.put("MUNICIPIO", montarNomeDoMunicipio());
            parametros.put("NOMERELATORIO", "RELATÓRIO DE SALDO POR GRUPO MATERIAL");
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("FILTROS", filtro.trim());
            adicionarUserNoMap(parametros);
            gerarRelatorioComDadosEmCollection(arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio: " + ex);
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório ");
        }
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência é obrigatório!");
        }
        ve.lancarException();
    }

    private List<AuxRelatorioSaldoGrupoMaterial> montarFiltrosAndBuscarDados() {
        if (dataReferencia != null) {
            filtro = "Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + "; ";
        }
        if (!hierarquiaOrganizacionalSelecionadas.isEmpty()) {
            filtro += "Unidade: ";
            for (HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada : hierarquiaOrganizacionalSelecionadas) {
                filtro += hierarquiaOrganizacionalSelecionada.getSubordinada() + "; ";
            }
        } else {
            filtro += "Unidade: TODAS";
        }
        return relatorioSaldoGrupoMaterialFacade.buscarDados(retornarIDsUnidadeOrganizacionais(), dataReferencia);
    }

    private List<Long> retornarIDsUnidadeOrganizacionais() {
        List<Long> ids = Lists.newArrayList();
        if (!hierarquiaOrganizacionalSelecionadas.isEmpty()) {
            for (HierarquiaOrganizacional hierarquia : hierarquiaOrganizacionalSelecionadas) {
                ids.add(hierarquia.getSubordinada().getId());
            }
            return ids;
        }
        return Lists.newArrayList();
    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        dataReferencia = relatorioSaldoGrupoMaterialFacade.getSistemaFacade().getDataOperacao();
        hierarquiaOrganizacionalSelecionadas = Lists.newArrayList();
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalSelecionadas() {
        return hierarquiaOrganizacionalSelecionadas;
    }

    public void setHierarquiaOrganizacionalSelecionadas(List<HierarquiaOrganizacional> hierarquiaOrganizacionalSelecionadas) {
        this.hierarquiaOrganizacionalSelecionadas = hierarquiaOrganizacionalSelecionadas;
    }
}
