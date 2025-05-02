package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExecucaoOperacaoCreditoVo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaExecucaoOperacaoCreditoFacade;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "consultaExecucaoOperacaoCreditoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-execucao-operacao-credito", pattern = "/consulta-execucao-operacao-credito/", viewId = "/faces/financeiro/execucao-operacao-credito/lista.xhtml")
})
public class ConsultaExecucaoOperacaoCreditoControlador implements Serializable {

    @EJB
    private ConsultaExecucaoOperacaoCreditoFacade facade;
    private Date dataInicial;
    private Date dataFinal;
    private ContaDeDestinacao contaDeDestinacao;
    private FonteDeRecursos fonteDeRecursos;
    private DividaPublica dividaPublica;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private List<ExecucaoOperacaoCreditoVo> operacoes;

    @URLAction(mappingId = "consulta-execucao-operacao-credito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        contaDeDestinacao = null;
        fonteDeRecursos = null;
        dividaPublica = null;
        hierarquiasOrganizacionais = Lists.newArrayList();
        operacoes = Lists.newArrayList();
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return facade.buscarContasDeDestinacao(filtro);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtros) {
        return facade.buscarFontesDeRecursos(filtros);
    }

    public List<DividaPublica> completarDividasPublicas(String filtro) {
        return facade.buscarDividasPublicas(filtro);
    }

    public void buscarDados() {
        try {
            validarCampos();
            operacoes = facade.buscarOperacoes(dataInicial, dataFinal, montarFiltros());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a busca das operações de crédito: " + ex.getMessage());
        }
    }

    private String montarFiltros() {
        String filtro = "";
        if (contaDeDestinacao != null) {
            filtro += " and cd.id = " + contaDeDestinacao.getId();
        }
        if (fonteDeRecursos != null) {
            filtro += " and fonte.id = " + fonteDeRecursos.getId();
        }
        if(dividaPublica != null) {
            filtro += " and dv.id = " + dividaPublica.getId();
        }
        if (!hierarquiasOrganizacionais.isEmpty()) {
            StringBuilder idsUnidades = new StringBuilder();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiasOrganizacionais) {
                idsUnidades.append(hierarquiaOrganizacional.getSubordinada().getId()).append(",");
            }
            filtro += " and vw.subordinada_id in (" + idsUnidades.toString().substring(0, idsUnidades.toString().length() - 1) + ") ";
        }
        return filtro;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    private void verificarCampoNull(ValidacaoException ve, Object campo, String mensagemValidacao) {
        if (campo == null) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagemValidacao);
        }
    }

    public StreamedContent fileDownload() {
        try {
            List<String> titulos = Lists.newArrayList();
            titulos.add("Dívida Pública");
            titulos.add("Programa");
            titulos.add("Unidade Orçamentária");
            titulos.add("Valor Pago de DE");
            titulos.add("Valor Pago de RP");
            titulos.add("Valor Pago");
            List<Object[]> objetos = Lists.newArrayList();
            for (ExecucaoOperacaoCreditoVo vo : operacoes) {
                Object[] saldoAnterior = new Object[6];
                saldoAnterior[0] = vo.getDivida();
                saldoAnterior[1] = vo.getPrograma();
                saldoAnterior[2] = vo.getUnidade();
                saldoAnterior[3] = vo.getNormais();
                saldoAnterior[4] = vo.getRestos();
                saldoAnterior[5] = vo.getTotal();
                objetos.add(saldoAnterior);
            }
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcelXLSX("Dívidas Públicas", "dividas-publicas", titulos, objetos, "", false);
            return excel.fileDownload();
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
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

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public List<ExecucaoOperacaoCreditoVo> getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(List<ExecucaoOperacaoCreditoVo> operacoes) {
        this.operacoes = operacoes;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }
}
