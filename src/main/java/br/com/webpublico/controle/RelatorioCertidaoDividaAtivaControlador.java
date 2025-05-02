/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "relatorioCertidaoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioCDA", pattern = "/tributario/dividaativa/relatoriocertidaocda", viewId = "/faces/tributario/dividaativa/relatorio/relatoriocertidaodividaativa.xhtml"),
})
public class RelatorioCertidaoDividaAtivaControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioCertidaoDividaAtivaControlador.class);

    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    private StringBuilder filtro;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private List<Divida> dividaselecionados;
    private Divida divida;
    private BigDecimal faixaValorInicial;
    private BigDecimal faixaValorFinal;
    private String cadastroInicial;
    private String cadastroFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroImobiliario;
    private Pessoa pessoa;
    private ConverterAutoComplete converterPessoa, converterDivida;
    private String ordenacao;
    private String ordemSql;
    private Long certidaoInicial;
    private Long certidaoFinal;

    public RelatorioCertidaoDividaAtivaControlador() {
        ordenacao = "S";
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public BigDecimal getFaixaValorFinal() {
        return faixaValorFinal;
    }

    public void setFaixaValorFinal(BigDecimal faixaValorFinal) {
        this.faixaValorFinal = faixaValorFinal;
    }

    public BigDecimal getFaixaValorInicial() {
        return faixaValorInicial;
    }

    public void setFaixaValorInicial(BigDecimal faixaValorInicial) {
        this.faixaValorInicial = faixaValorInicial;
    }

    public List<Divida> getDividaselecionados() {
        return dividaselecionados;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, certidaoDividaAtivaFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completaDivida(String parte) {
        return certidaoDividaAtivaFacade.getDividaFacade().listaDividasAtivas(parte.trim(), tipoCadastroTributario);
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public Long getCertidaoFinal() {
        return certidaoFinal;
    }

    public void setCertidaoFinal(Long certidaoFinal) {
        this.certidaoFinal = certidaoFinal;
    }

    public Long getCertidaoInicial() {
        return certidaoInicial;
    }

    public void setCertidaoInicial(Long certidaoInicial) {
        this.certidaoInicial = certidaoInicial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtro = new StringBuilder();
            StringBuilder where = montaWhere();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/certidao-divida-ativa/");
            dto.setNomeRelatorio("RELAÇÃO DE CERTIDÃO DE DÍVIDA ATIVA");
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin(), Boolean.FALSE);
            dto.adicionarParametro("complementoQuery", where.toString());
            dto.adicionarParametro("SEMDADOS", "SEM DADOS. NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
            dto.adicionarParametro("FILTRO", filtro.toString());
            dto.adicionarParametro("ORDER", ordemSql);
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "TODOS"));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return certidaoDividaAtivaFacade.getCadastroEconomicoFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return certidaoDividaAtivaFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "codigo");
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, certidaoDividaAtivaFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, certidaoDividaAtivaFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return certidaoDividaAtivaFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, certidaoDividaAtivaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public StringBuilder montaWhere() {
        StringBuilder where = new StringBuilder();
        ordemSql = "";
        if (pessoa != null && pessoa.getId() != null) {
            where.append(" and p.id = ").append(pessoa.getId());
            filtro.append("Nome Contribuinte = ").append(pessoa.getNome()).append("; ");
        }

        if (tipoCadastroTributario != null) {
            filtro.append("Tipo Cadastro ").append(tipoCadastroTributario.getDescricao()).append(";");
            where.append(" and ida.tipoCadastrotributario = '").append(tipoCadastroTributario.name()).append("'");
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(";");
                    where.append(" and ci.inscricaocadastral >= '").append(cadastroInicial).append("'");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(";");
                    where.append(" and ce.inscricaocadastral >= '").append(cadastroInicial).append("'");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(";");
                    where.append(" and cr.codigo >= '").append(cadastroInicial).append("'");
                }
            }


            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    filtro.append("Cadastro Final = ").append(cadastroFinal).append(";");
                    where.append(" and ci.inscricaocadastral <= '").append(cadastroFinal).append("'");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    filtro.append("Cadastro Final = ").append(cadastroFinal).append(";");
                    where.append(" and ce.inscricaocadastral <= '").append(cadastroFinal).append("'");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    filtro.append("Cadastro Final = ").append(cadastroFinal).append(";");
                    where.append(" and cr.codigo <= '").append(cadastroFinal).append("'");
                }
            }
        }

        if (exercicioInicial != null) {
            where.append(" and ex.ano >= ").append(exercicioInicial.getAno());
            filtro.append("Exercicio = ").append(exercicioInicial.getAno()).append("; ");
        }
        if (exercicioFinal != null) {
            where.append(" and ex.ano <= ").append(exercicioFinal.getAno());
            filtro.append("Exercicio = ").append(exercicioFinal.getAno()).append("; ");
        }

        if (dividaselecionados != null && dividaselecionados.size() > 0) {
            where.append(" and div.id in (").append(dividaMarcados()).append(") ");
        }

        if (faixaValorInicial != null && faixaValorInicial.compareTo(BigDecimal.ZERO) > 0) {
            where.append(" and spvd.saldo >= ").append(faixaValorInicial);
            filtro.append("Faixa Valor Inicial = ").append(faixaValorInicial).append("; ");
        }

        if (faixaValorFinal != null && faixaValorFinal.compareTo(BigDecimal.ZERO) > 0) {
            where.append(" and spvd.saldo <= ").append(faixaValorFinal);
            filtro.append("Faixa Valor Inicial = ").append(faixaValorFinal).append("; ");
        }

        if (certidaoInicial != null) {
            where.append(" and cda.numero >= ").append(certidaoInicial);
            filtro.append("Número da Certidão Inicial = ").append(certidaoInicial).append("; ");
        }

        if (certidaoFinal != null) {
            where.append(" and cda.numero <= ").append(certidaoFinal);
            filtro.append("Número da Certidão final = ").append(certidaoFinal).append("; ");
        }

        switch (ordenacao) {
            case "N":
                ordemSql = " cda.numero,ex.ano,livro.numero,nomeContribuinte ";
                break;
            case "B":
                ordemSql = " inscricaocadastral,nomeContribuinte ";
                break;
            case "A":
                ordemSql = " nomeContribuinte ";
                break;
            default:
                break;
        }

        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by nomeContribuinte";
        }
        return where;
    }

    public List<Divida> listadiDividas() {
        return certidaoDividaAtivaFacade.getDividaFacade().lista();
    }

    private String dividaMarcados() {
        StringBuilder sb = new StringBuilder();
        for (Divida div : dividaselecionados) {
            sb.append(div.getId()).append(",");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public void limpaCampos() {
        ordenacao = "S";
        novo();
    }

    public void copiaInicialCertidao() {
        this.certidaoFinal = this.certidaoInicial;
    }

    @URLAction(mappingId = "novoRelatorioCDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new StringBuilder();
        exercicioInicial = null;
        exercicioFinal = null;
        dividaselecionados = null;
        faixaValorInicial = null;
        faixaValorFinal = null;
        cadastroInicial = "1";
        cadastroFinal = "999999999999999";
        tipoCadastroTributario = null;
        pessoa = null;
        ordenacao = "S";
        ordemSql = "";
        certidaoInicial = null;
        certidaoFinal = null;
        dividaselecionados = Lists.newArrayList();
    }

    public void addDivida() {
        dividaselecionados.add(divida);
        divida = new Divida();
    }

    public void removeDivida(Divida divida) {
        dividaselecionados.remove(divida);
    }
}
