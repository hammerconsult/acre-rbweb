package br.com.webpublico.controlerelatorio;


import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by israeleriston on 27/10/15.
 */
@ManagedBean(name = "relatorioPrevisaoFeriasSecretariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioPrevisaoFerias",
        pattern = "/relatorio/previsao-ferias-secretaria/",
        viewId = "/faces/rh/relatorios/relatorioprevisaoferiassecretaria.xhtml")
})
public class RelatorioPrevisaoFeriasSecretariaControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Date inicio;
    private Date fim;
    public static final String ARQUIVO_JASPER = "RelatorioPrevisaoFeriasSecretaria.jasper";
    private String filtro;


    public RelatorioPrevisaoFeriasSecretariaControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void setConverterHierarquiaOrganizacional(ConverterAutoComplete converterHierarquiaOrganizacional) {
        this.converterHierarquiaOrganizacional = converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String filtro) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(filtro.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (inicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Início deve ser informado!");
        }
        if (fim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Fim deve ser informado!");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado!");
        }

        if (ve.temMensagens()) throw ve;
    }


    private String montarCondicaoWhere() {
        StringBuilder sb = new StringBuilder("  ");
        sb.append(" where  vw.codigo   like  '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%' " +
            " AND (to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy') BETWEEN " +
            "    (lotacao.INICIOVIGENCIA) AND COALESCE(lotacao.FINALVIGENCIA,to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy')) " +
            " OR to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy') BETWEEN " +
            "                  (lotacao.INICIOVIGENCIA) AND COALESCE(lotacao.FINALVIGENCIA,to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy')) ) " +
            " AND (to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy') BETWEEN " +
            "    (aquisitivo.INICIOVIGENCIA) AND COALESCE(aquisitivo.FINALVIGENCIA,to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy')) " +
            " OR to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy') BETWEEN " +
            "                  (aquisitivo.INICIOVIGENCIA) AND COALESCE(aquisitivo.FINALVIGENCIA,to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy'))) " +
            " AND (to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy') BETWEEN " +
            "    (vw.INICIOVIGENCIA) AND COALESCE(vw.FIMVIGENCIA,to_date('" + Util.dateToString(inicio) + "','dd/MM/yyyy'))" +
            " OR to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy') BETWEEN " +
            "                  (vw.INICIOVIGENCIA) AND COALESCE(vw.FIMVIGENCIA,to_date('" + Util.dateToString(fim) + "','dd/MM/yyyy'))) ");
        filtro = null;
        filtro = "Período: " + Util.dateToString(inicio) + " à " + Util.dateToString(fim);

        return sb.toString();
    }

    public void limparCampos() {
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        inicio = null;
        fim = null;
    }

    public void gerarRelatorio() throws SQLException, JRException {
        try {
            validarCampos();
            HashMap parameter = new HashMap();
            parameter.put("NAO_CONCEDIDO", StatusPeriodoAquisitivo.NAO_CONCEDIDO.name());
            parameter.put("PARCIAL", StatusPeriodoAquisitivo.PARCIAL.name());
            parameter.put("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            parameter.put("NOMERELATORIO", "RELATÓRIO PREVISÃO DE FÉRIAS POR SECRETARIA");
            parameter.put("BRASAO", getCaminhoImagem());
            parameter.put("MONTAR_CONDICAO", montarCondicaoWhere());
            parameter.put("USUARIO", getSistemaControlador().getUsuarioCorrente().toString());
            parameter.put("MODULO", "RECURSOS HUMANOS");
            parameter.put("ORGAO", hierarquiaOrganizacional.getCodigo() + hierarquiaOrganizacional.getDescricaoSubordinadaConcatenada());
            parameter.put("FILTRO", filtro);

            gerarRelatorio(ARQUIVO_JASPER, parameter);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio("");
        }
    }
}
