/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-nota-empenho-intervalo", pattern = "/relatorio/nota-empenho-intervalo", viewId = "/faces/financeiro/relatorio/relatorionotaempenho.xhtml")
})
@ManagedBean
public class RelatorioNotaEmpenhoControlador implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(RelatorioNotaEmpenhoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    private List<HierarquiaOrganizacional> listaUnidades;
    private Pessoa pessoa;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroInicial;
    private String numeroFinal;

    @URLAction(mappingId = "relatorio-nota-empenho-intervalo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = DataUtil.montaData(1, 0, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        this.dataFinal = new Date();
        this.numeroInicial = null;
        this.numeroFinal = null;
        listaUnidades = new ArrayList<>();
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return empenhoFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (!Strings.isNullOrEmpty(numeroInicial.trim()) && Strings.isNullOrEmpty(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Necessário informar o campo Número Final.");
        }
        if (Strings.isNullOrEmpty(numeroInicial.trim()) && !Strings.isNullOrEmpty(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Necessário informar o campo Número Inicial");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Data Inicial é maior que a Data Final.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            List<NotaExecucaoOrcamentaria> notas = empenhoFacade.buscarNotaEmpenho(montarCondicao(), CategoriaOrcamentaria.NORMAL, "NOTA DE EMPENHO");
            documentoOficialFacade.gerarRelatorioDasNotasOrcamentarias(notas, ModuloTipoDoctoOficial.NOTA_EMPENHO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório de nota de empenho: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuilder stb = new StringBuilder();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        stb.append(" and trunc(emp.dataempenho) between to_date(\'").append(formato.format(dataInicial)).append("\',\'dd/MM/yyyy\') ").append(" and to_date(\'").append(formato.format(dataFinal)).append("\',\'dd/MM/yyyy\') ");
        if (!Strings.isNullOrEmpty(numeroInicial.trim()) && !Strings.isNullOrEmpty(numeroFinal.trim())) {
            stb.append(" and emp.numero between \'").append(numeroInicial.trim()).append("\' and \'").append(numeroFinal.trim()).append("\'");
        }
        if (pessoa != null) {
            stb.append(" and pes.id = ").append(pessoa.getId());
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ",";
            }
            stb.append(" and HO_UNIDADE.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ", ";
            }
            stb.append(" and HO_UNIDADE.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }
}
