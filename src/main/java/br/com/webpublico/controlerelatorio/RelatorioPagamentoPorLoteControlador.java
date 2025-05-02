/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.BancoContaConfTributario;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.BancoFacade;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author gustavo
 */
@ManagedBean(name = "relatorioPagamentoPorLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPagamentoPorLote", pattern = "/relatorios/arrecadacao-por-lote/", viewId = "/faces/tributario/arrecadacao/relatorios/pagamentoporlote.xhtml")
})
public class RelatorioPagamentoPorLoteControlador extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioPagamentoPorLoteControlador.class);

    private Banco filtroBanco;
    private Date dataInicialPagamento;
    private Date dataFinalPagamento;
    private Date dataInicialFinanceira;
    private Date dataFinalFinanceira;
    @EJB
    private BancoFacade bancoFacade;
    private SituacaoLoteBaixa filtroSituacao;
    private BancoContaConfTributario conta;
    private transient Converter converterConta;
    @EJB
    private LoteBaixaFacade facade;
    private SubConta subConta;
    private StringBuilder filtros;
    private String relatorio;

    @URLAction(mappingId = "novoPagamentoPorLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPagamentoPorLote() {
        novo();
    }

    private void novo() {
        filtroBanco = null;
        dataFinalFinanceira = null;
        dataInicialFinanceira = null;
        dataInicialPagamento = null;
        dataFinalPagamento = null;
        geraNoDialog = true;
    }

    public BancoContaConfTributario getConta() {
        return conta;
    }

    public void setConta(BancoContaConfTributario conta) {
        this.conta = conta;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Date getDataFinalFinanceira() {
        return dataFinalFinanceira;
    }

    public void setDataFinalFinanceira(Date dataFinalFinanceira) {
        this.dataFinalFinanceira = dataFinalFinanceira;
    }

    public Date getDataFinalPagamento() {
        return dataFinalPagamento;
    }

    public void setDataFinalPagamento(Date dataFinalPagamento) {
        this.dataFinalPagamento = dataFinalPagamento;
    }

    public Date getDataInicialFinanceira() {
        return dataInicialFinanceira;
    }

    public void setDataInicialFinanceira(Date dataInicialFinanceira) {
        this.dataInicialFinanceira = dataInicialFinanceira;
    }

    public Date getDataInicialPagamento() {
        return dataInicialPagamento;
    }

    public void setDataInicialPagamento(Date dataInicialPagamento) {
        this.dataInicialPagamento = dataInicialPagamento;
    }

    public Banco getFiltroBanco() {
        return filtroBanco;
    }

    public void setFiltroBanco(Banco filtroBanco) {
        this.filtroBanco = filtroBanco;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public SituacaoLoteBaixa getFiltroSituacao() {
        return filtroSituacao;
    }

    public void setFiltroSituacao(SituacaoLoteBaixa filtroSituacao) {
        this.filtroSituacao = filtroSituacao;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicialFinanceira == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Movimento Inicial deve ser informado.");
        }
        if (dataFinalFinanceira == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Movimento Final deve ser informado.");
        }
        if (dataInicialPagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Pagamento Inicial deve ser informado.");
        }
        if (dataFinalPagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Pagamento Final deve ser informado.");
        }
        ve.lancarException();

    }

    private String montarCondicao() {
        filtros = new StringBuilder();
        String juncao = " where ";
        StringBuilder where = new StringBuilder();
        String caminhoBrasao = getCaminhoImagem();
        if (filtroBanco != null && filtroBanco.getId() != null) {
            where.append(juncao).append(" banco.id = ").append(filtroBanco.getId());
            juncao = " and ";
            filtros.append("Banco: ").append(filtroBanco.getDescricao().trim()).append("; ");
        }
        if (subConta != null && subConta.getId() != null) {
            where.append(juncao).append(" subconta.id = ").append(subConta.getId());
            juncao = " and ";
            filtros.append("Conta: ").append(subConta.getContaBancariaEntidade().getNumeroConta()).append(" - ").append(subConta.getContaBancariaEntidade().getDigitoVerificador()).append("; ");
        }
        if (dataInicialFinanceira != null) {
            where.append(juncao).append(" lotebaixa.datafinanciamento >= to_date(").append(formataData(dataInicialFinanceira)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtros.append(" Data de Movimento Inicial: ").append(DataUtil.getDataFormatada(dataInicialFinanceira)).append("; ");
        }
        if (dataFinalFinanceira != null) {
            where.append(juncao).append(" lotebaixa.datafinanciamento <= to_date(").append(formataData(dataFinalFinanceira)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtros.append(" Data de Movimento Final: ").append(DataUtil.getDataFormatada(dataFinalFinanceira)).append("; ");
        }

        if (dataInicialPagamento != null) {
            where.append(juncao).append(" lotebaixa.datapagamento >= to_date(").append(formataData(dataInicialPagamento)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtros.append("Data do Pagamento Inicial: ").append(DataUtil.getDataFormatada(dataInicialPagamento)).append("; ");
            if (dataFinalPagamento != null) {
                where.append(juncao).append(" lotebaixa.datapagamento <= to_date(").append(formataData(dataFinalPagamento)).append(", 'dd/MM/yyyy')");
                filtros.append("Data do Pagamento Final: ").append(DataUtil.getDataFormatada(dataFinalPagamento));
                juncao = " and ";
            }
            if (filtroSituacao != null) {
                where.append(juncao).append(" lotebaixa.situacaolotebaixa = '").append(filtroSituacao.name()).append("'");
                juncao = " and ";
                filtros.append(" Situação do Lote: ").append(filtroSituacao.descricao);

            }
        }
        return where.toString();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("WHERE", montarCondicao());
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RELATÓRIO-DE-ARRECAÇÃO-POR-LOTE");
            dto.setApi("tributario/pagamento-por-lote/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String formataData(Date data) {
        return "'" + DataUtil.getDataFormatada(data) + "'";
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (SituacaoLoteBaixa situacao : SituacaoLoteBaixa.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public Converter getConverterConta() {
        if (converterConta == null) {
            converterConta = new Converter() {

                @Override
                public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    return facade.recuperar(BancoContaConfTributario.class, Long.parseLong(string));
                }

                @Override
                public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                    if (o == null) {
                        return null;
                    } else {
                        return String.valueOf(((BancoContaConfTributario) o).getId());
                    }
                }
            };
        }
        return converterConta;
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<BancoContaConfTributario> lista = facade.recuperaContasConfiguracao();
        Collections.sort(lista, new Comparator<BancoContaConfTributario>() {

            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (BancoContaConfTributario object : lista) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void selecionaContaTst() {
        if (conta != null && conta.getSubConta().getContaBancariaEntidade() != null && conta.getSubConta().getContaBancariaEntidade().getAgencia() != null && conta.getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {
            filtroBanco = (conta.getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            subConta = (conta.getSubConta());

        } else {
            filtroBanco = new Banco();
            subConta = new SubConta();
            FacesUtil.addError("Atenção", "A conta selecionada não está relacionada a um banco");
        }
    }
}
