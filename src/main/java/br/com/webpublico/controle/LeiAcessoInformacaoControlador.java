package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.LeiAcessoInformacao;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.LeiAcessoInformacaoFacade;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by carlos on 28/07/15.
 */
@ManagedBean(name = "leiAcessoInformacaoControlador")
@ViewScoped
@URLMapping(id = "novo-LAI", pattern = "/lei-acesso-informacao/", viewId = "/faces/rh/relatorios/relatorioleiacessoinformacao.xhtml")
public class LeiAcessoInformacaoControlador extends AbstractReport implements Serializable {


    @EJB
    private LeiAcessoInformacaoFacade leiAcessoInformacaoFacade;
    private List<LeiAcessoInformacao> lista;
    private Integer mes;
    private Integer ano;
    private String filtros;
    private UnidadeOrganizacional unidadeOrganizacional;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private Boolean status;

    public LeiAcessoInformacaoControlador() {
    }

    @URLAction(mappingId = "novo-LAI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        mes = null;
        ano = null;
        unidadeOrganizacional = new UnidadeOrganizacional();
        lista = Lists.newArrayList();
    }

    public void salvar() {
        status = false;
        if (validaCampos()) {
            leiAcessoInformacaoFacade.removerLAI(mes, ano);
            for (LeiAcessoInformacao leiAcessoInformacao : lista) {
                leiAcessoInformacao.setUsuarioSistema(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUsuarioCorrente());
                if (leiAcessoInformacao.getVersao() == null) {
                    leiAcessoInformacao.setVersao(1);
                }
                if (leiAcessoInformacao.getVersao() >= 1) {
                    leiAcessoInformacao.setVersao(leiAcessoInformacao.getVersao() + 1);
                }
                leiAcessoInformacaoFacade.salvar(leiAcessoInformacao);
            }
            mudarStatus();
        }
    }

    public void mudarStatus() {
        if (!status) {
            status = true;
        } else {
            status = false;
        }
    }

    public void buscaInformacoes() {
        if (validaCampos()) {
            if (leiAcessoInformacaoFacade.verificaStatus(mes, ano)) {
                lista = leiAcessoInformacaoFacade.recuperarInformacoes(mes, ano);
            } else {
                FacesUtil.addOperacaoNaoPermitida("CompetenciaFP ainda não está fechada para esses parametros.");
            }
        }
    }

    public void geraRelatorioLAI() {

        if (validaCampos()) {
            String arquivo = "RelatorioLeiDeAcessoInformacao.jasper";
            HashMap parameter = new HashMap();
            parameter.put("IMAGEM", super.getCaminhoImagem());
            parameter.put("WHERE", isCriterio());
            parameter.put("MES", Mes.getMesToInt(mes).name());
            parameter.put("ANO", ano);

            try {
                gerarRelatorio(arquivo, parameter);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório.");
            }

        }
    }

    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        if (mes != null) {
            sb.append(juncao).append(" l.mes = ").append("'" + Mes.getMesToInt(mes).name() + "'").append(" ");
            filtros += " Mês " + Mes.getMesToInt(mes).name();
        }
        if (ano != null) {
            sb.append(juncao).append(" l.ano = ").append(ano).append(" ");
            filtros += " Ano " + ano;
        }
//        if (unidadeOrganizacional != null) {
//            sb.append(juncao).append(" uo.id = ").append(unidadeOrganizacional.getId()).append(" ");
//            filtros += " Unidade " + unidadeOrganizacional.getDescricao();
//        }

        return sb.toString();
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        if (mes == null || ano == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Mês e Ano devem ser preenchidos.");
            return null;
        } else {
            return unidadeOrganizacionalFacade.listaUnidadePorFolha(parte, mes, ano);
        }
    }

    public Boolean validaCampos() {
        Boolean retorno = true;
        if (mes == null) {
            FacesUtil.addCampoObrigatorio("Campo Mês deve ser preenchido.");
            retorno = false;

        }
        if (mes != null && (mes < 1 || mes > 12)) {
            FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
            retorno = false;

        }
        if (ano == null || (ano == 0)) {
            FacesUtil.addCampoObrigatorio("Campo Ano deve ser informado.");
            retorno = false;
        }
        return retorno;
    }

    public List<LeiAcessoInformacao> getLista() {
        return lista;
    }

    public void setLista(List<LeiAcessoInformacao> lista) {
        this.lista = lista;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
