package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.HistoricoResponsavelUnidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ParametroPatrimonioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HardRock on 24/05/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "historicoResponsvelUnidade", pattern = "/historico-responsavel-unidade/", viewId = "/faces/administrativo/patrimonio/relatorios/historico-responsavel-unidade.xhtml"
    )})
public class RelatorioHistoricoResponsavelUnidadeControlador extends AbstractReport implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataReferencia;

    @URLAction(mappingId = "historicoResponsvelUnidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataReferencia = new Date();
        hierarquiaOrganizacional = null;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            setGeraNoDialog(true);
            String arquivoJasper = "RelatorioHistoricoResponsavelUnidade.jasper";
            HashMap parametros = new HashMap();
            parametros.put("USER", getNomeUsuarioLogado());
            parametros.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("IMAGEM", getCaminhoImagem());
            parametros.put("UNIDADE_ADM", hierarquiaOrganizacional.getCodigo() + " - " + hierarquiaOrganizacional.getDescricao());
            parametros.put("DATA_REFERENCIA", dataReferencia);

            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSql());
            parametros.put("MODULO", "Patrimônio");
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
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade administrativa deve ser informado.");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
    }

    private String getNomerPDF() {
        return "HISTORICO-REPONSAVEL-UNIDADE";
    }

    private List<HistoricoResponsavelUnidade> gerarSql() {
        if (hierarquiaOrganizacional != null && dataReferencia != null) {
            return parametroPatrimonioFacade.buscarHistoricoResponsavelPorUnidade(hierarquiaOrganizacional, dataReferencia);
        }
        return new ArrayList<>();
    }

    public String getNomeUsuarioLogado() {
        if (sistemaFacade.getUsuarioCorrente().getPessoaFisica() != null) {
            return sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return sistemaFacade.getUsuarioCorrente().getUsername();
        }
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
}
