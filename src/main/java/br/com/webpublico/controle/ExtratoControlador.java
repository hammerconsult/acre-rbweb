/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.ExtratoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "extratoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioExtratodeDebito", pattern = "/tributario/relatorioextratodedebitos/", viewId = "/faces/tributario/contacorrente/relatorio/extrato.xhtml"),
})
public class ExtratoControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExtratoControlador.class);
    @EJB
    private ExtratoFacade extratoFacade;
    private ConverterAutoComplete converterDivida, converterPessoa;
    private ConverterExercicio converterExercicio;
    private TipoCadastroTributario tipoCadastroTributario;
    private String cadastroInicial;
    private String cadastroFinal;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private Divida divida;
    private Pessoa pessoa;
    private boolean detalhado;
    private StringBuilder where;
    private StringBuilder semDados;
    private StringBuilder filtros;
    private transient ByteArrayOutputStream dadosByte;
    private boolean gerando;
    private String brasao, report;

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public boolean isGerando() {
        return gerando;
    }

    public void setGerando(boolean gerando) {
        this.gerando = gerando;
    }

    public StringBuilder getFiltros() {
        return filtros;
    }

    public void setFiltros(StringBuilder filtros) {
        this.filtros = filtros;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getSemDados() {
        return semDados;
    }

    public void setSemDados(StringBuilder semDados) {
        this.semDados = semDados;
    }

    public boolean isDetalhado() {
        return detalhado;
    }

    public void setDetalhado(boolean detalhado) {
        this.detalhado = detalhado;
    }

    public List<Pessoa> completaPessoas(String parte) {
        return extratoFacade.completaPessoas(parte.trim());
    }

    public List<Exercicio> completaExercicio(String parte) {
        return extratoFacade.completaExercicio(parte.trim());
    }

    public List<Divida> completaDivida(String parte) {
        return extratoFacade.completaDivida(parte.trim());
    }

    public ByteArrayOutputStream getDadosByte() {
        return dadosByte;
    }

    public void setDadosByte(ByteArrayOutputStream dadosByte) {
        this.dadosByte = dadosByte;
    }

    public String getBrasao() {
        return brasao;
    }

    public String getReport() {
        return report;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, extratoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(extratoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, extratoFacade.getDividaFacade());
        }
        return converterDivida;
    }


    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
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

    public Exercicio getExercicio() {
        return exercicioInicial;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicioInicial = exercicio;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void montaCondicao() {
        where = new StringBuilder();
        semDados = new StringBuilder("Não foram encontrados registros");
        filtros = new StringBuilder();

        String juncao = " and ";
        String juncaoFiltro = "Filtros: ";
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        if (exercicioInicial != null && exercicioInicial.getAno() != null) {
            where.append(juncao).append(" exercicio.ano >= ").append(exercicioInicial.getAno());
            juncao = " and ";
            filtros.append(juncaoFiltro).append("Exercicio Inicial: ").append(exercicioInicial.getAno()).append(";");
            juncaoFiltro = " ";
        }
        if (exercicioFinal != null && exercicioFinal.getAno() != null) {
            where.append(juncao).append(" exercicio.ano <= ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtros.append(juncaoFiltro).append("Exercicio Final: ").append(exercicioInicial.getAno()).append(";");
            juncaoFiltro = " ";
        }
        if (vencimentoInicial != null) {
            where.append(juncao).append(" parcela.vencimento >= '").append(formatoData.format(vencimentoInicial)).append("' ");
            juncao = " and ";
            filtros.append(juncaoFiltro).append("Vencimento Inicial: ").append(formatoData.format(vencimentoInicial)).append(";");
            juncaoFiltro = " ";
        }
        if (vencimentoFinal != null) {
            where.append(juncao).append(" parcela.vencimento <= '").append(formatoData.format(vencimentoFinal)).append("' ");
            juncao = " and ";
            filtros.append(juncaoFiltro).append("Vencimento Final: ").append(formatoData.format(vencimentoFinal)).append(";");
            juncaoFiltro = " ";
        }
        if (divida != null) {
            where.append(juncao).append(" divida.id = ").append(divida.getId());
            juncao = " and ";
            filtros.append(juncaoFiltro).append("Dívida: ").append(divida.getDescricao()).append(";");
            juncaoFiltro = " ";
        }

        if (tipoCadastroTributario != null) {
            if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                if (cadastroInicial != null && !cadastroInicial.trim().isEmpty()) {
                    where.append(juncao).append(" cmc.inscricaocadastral >= '").append(cadastroInicial).append("' ");
                    juncao = " and ";
                }
                if (cadastroFinal != null && !cadastroFinal.trim().isEmpty()) {
                    where.append(juncao).append(" cmc.inscricaocadastral <= '").append(cadastroFinal).append("' ");
                    juncao = " and ";
                }
            } else if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                if (cadastroInicial != null && !cadastroInicial.trim().isEmpty()) {
                    where.append(juncao).append(" bci.inscricaocadastral >= '").append(cadastroInicial).append("' ");
                    juncao = " and ";
                }
                if (cadastroFinal != null && !cadastroFinal.trim().isEmpty()) {
                    where.append(juncao).append(" bci.inscricaocadastral <= '").append(cadastroFinal).append("' ");
                    juncao = " and ";
                }

            } else if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                if (cadastroInicial != null && !cadastroInicial.trim().isEmpty()) {
                    where.append(juncao).append(" bcr.codigo >= ").append(cadastroInicial);
                    juncao = " and ";
                }
                if (cadastroFinal != null && !cadastroFinal.trim().isEmpty()) {
                    where.append(juncao).append(" bcr.codigo >= ").append(cadastroFinal);
                    juncao = " and ";
                }
            } else if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                if (pessoa != null && pessoa.getId() != null) {
                    where.append(juncao).append(" pessoa.id = ").append(pessoa.getId());
                    juncao = " and ";
                }
            }
        }

        //FILTROS
        if (tipoCadastroTributario != null) {
            filtros.append(juncaoFiltro).append("Tipo de Cadastro: " + tipoCadastroTributario.getDescricao()).append(";");
            juncaoFiltro = " ";
            if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)
                || tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)
                || tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                if (!cadastroInicial.isEmpty()) {
                    filtros.append(juncaoFiltro).append("Cadastro Incial: ").append(cadastroInicial);
                    juncaoFiltro = " ";
                }
                if (!cadastroFinal.isEmpty()) {
                    filtros.append(juncaoFiltro).append("Cadastro Final: ").append(cadastroFinal);
                    juncaoFiltro = " ";
                }
            } else {
                if (pessoa != null) {
                    filtros.append(juncaoFiltro).append("Contribuinte: ").append(pessoa.getNomeCpfCnpj());
                    juncaoFiltro = " ";
                }
            }

        }
    }

    public void imprime() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        try {
            ////System.out.println("DadosByte............: " + dadosByte);
            byte[] bytes = dadosByte.toByteArray();
            if (bytes != null && bytes.length > 0) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"Extrato.pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada", "Ocorreu um problema de comunicação com o servidor");
            logger.error(e.getMessage());
        }
    }

    public void gerar() {
        setGerando(true);
        extratoFacade.geraExtrato(this);
    }

    public void limpaCadastro() {
        cadastroFinal = "999999999999999999";
        cadastroInicial = "1";
    }

    @URLAction(mappingId = "novoRelatorioExtratodeDebito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        tipoCadastroTributario = null;
        exercicioInicial = null;
        exercicioFinal = null;
        vencimentoInicial = null;
        vencimentoFinal = null;
        divida = null;
        pessoa = null;
        detalhado = false;
        gerando = false;
        brasao = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img");
        brasao += "/";
        report = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/ExtratoDebitos.jasper");
        limpaCadastro();
    }

    public boolean habilitaContribuinte() {
        ////System.out.println("passou no habilitaContribuinte");
        ////System.out.println("tipoCadastroTributario --> " + tipoCadastroTributario);
        if (tipoCadastroTributario != null) {
            if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                return true;
            }
        }
        return false;
    }

    public boolean habilitaCadastroInicialFinal() {
        if (tipoCadastroTributario != null) {
            if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)
                || tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)
                || tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                return true;
            }
        }
        return false;
    }

    public void copiaCadastroInicialParaCadastroFinal() {
        this.cadastroFinal = this.cadastroInicial;
    }
}
