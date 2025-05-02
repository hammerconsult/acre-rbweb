package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.SituacaoVinculo;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 04/09/14
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-verbas-por-cargo", pattern = "/relatorio/verbas-por-cargo/", viewId = "/faces/rh/relatorios/relatorioverbasporcargo.xhtml")
})
public class RelatorioDeVerbasPorCargoControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private SituacaoVinculo situacaoVinculo;
    private Integer mes;
    private Integer ano;

    public RelatorioDeVerbasPorCargoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public SituacaoVinculo getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(SituacaoVinculo situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getTipoDeFolhaPagamentos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoVinculo object : SituacaoVinculo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            if (validaCampos()) {
                String caminhoBrasao = getCaminhoImagem();
                String arquivoJasper = "RelatorioDeVerbasPorCargo.jasper";
                HashMap parameters = new HashMap();
                parameters.put("BRASAO", caminhoBrasao);
                parameters.put("DATAOPERACAO", UtilRH.getDataOperacaoFormatada());
                parameters.put("MODULO", "RECURSOS HUMANOS");
                parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
                parameters.put("NOMERELATORIO", "RELATÓRIO DE VERBAS POR CARGO");
                parameters.put("SITUACAO", situacaoVinculo.name());
                parameters.put("TIPOFOLHA", tipoFolhaDePagamento.name());
                parameters.put("MES", mes);
                parameters.put("ANO", ano);
                parameters.put("ORGAO", hierarquiaOrganizacionalSelecionada.toString());
                parameters.put("CODIGOHO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
//            parameters.put("CODIGOHO", hierarquiaOrganizacionalSelecionada.getSuperior() == null ? hierarquiaOrganizacionalSelecionada.getCodigo().substring(0, 2) + "%" : hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
                if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                } else {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
                }
                //System.out.println("Gera relatório de verbas por cargo " + parameters);
                gerarRelatorio(arquivoJasper, parameters);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    @URLAction(mappingId = "relatorio-verbas-por-cargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.mes = null;
        this.ano = null;
        this.hierarquiaOrganizacionalSelecionada = null;
        this.tipoFolhaDePagamento = null;
        this.situacaoVinculo = null;
    }

    public Boolean validaCampos() {
        if (mes == null) {
            FacesUtil.addCampoObrigatorio("O campo Mês é obrigatório");
            return false;
        }
        if (ano == null) {
            FacesUtil.addCampoObrigatorio("O campo Ano é obrigatório");
            return false;
        }
        if (mes == null || (mes != null && mes < 1 || mes > 12)) {
            FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
            return false;
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            FacesUtil.addCampoObrigatorio("O campo Órgão é obrigatório");
            return false;
        }
        if (situacaoVinculo == null) {
            FacesUtil.addCampoObrigatorio("O campo Tipo é obrigatório");
            return false;
        }
        if (tipoFolhaDePagamento == null) {
            FacesUtil.addCampoObrigatorio("Informe um Tipo de Folha de Pagamento");
            return false;
        }
        return true;
    }

}
