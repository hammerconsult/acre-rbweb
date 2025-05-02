/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoRelatorioGerencialArrecadacao;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author magraowar
 */

//relatorioGereincialDeArrecadacaoControlador


@ManagedBean(name = "relatorioGereincialDeArrecadacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioGerencialArrecadacao", pattern = "/tributario/conta-corrente/relatorio-gerencial-arrecadacao/", viewId = "/faces/tributario/contacorrente/relatorio/gerencialdearrecadacao.xhtml"),
})
public class RelatorioGereincialDeArrecadacaoControlador extends AbstractReport implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private CNAEFacade codigoAtividadeFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private ServicoFacade listaServicoFacade;
    private StringBuilder where;
    private int anoPagamentoInicial;
    private int anoPagamentoFinal;
    private TipoRelatorioGerencialArrecadacao tipoRelatorioGerencialArrecadacao;
    private Pessoa contribuinte;
    private ConverterAutoComplete converterPessoa;
    private Bairro bairro;
    private ConverterAutoComplete converterBairro;
    private CNAE codigoAtividade;
    private ConverterAutoComplete converterCodigoAtividade;
    private Banco banco;
    private ConverterAutoComplete converterBanco;
    private Tributo tributo;
    private ConverterAutoComplete converterTributo;
    private Servico listaServico;
    private ConverterAutoComplete converterListaServico;
    private StringBuilder semDados;
    private StringBuilder filtros;

    public ConverterAutoComplete getConverterListaServico() {
        if (converterListaServico == null) {
            converterListaServico = new ConverterAutoComplete(Servico.class, listaServicoFacade);
        }
        return converterListaServico;
    }

    public Servico getListaServico() {
        return listaServico;
    }

    public void setListaServico(Servico listaServico) {
        this.listaServico = listaServico;
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public ConverterAutoComplete getConverterCodigoAtividade() {
        if (converterCodigoAtividade == null) {
            converterCodigoAtividade = new ConverterAutoComplete(CNAE.class, codigoAtividadeFacade);
        }
        return converterCodigoAtividade;
    }

    public CNAE getCodigoAtividade() {
        return codigoAtividade;
    }

    public void setCodigoAtividade(CNAE codigoAtividade) {
        this.codigoAtividade = codigoAtividade;
    }

    public ConverterAutoComplete getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, bairroFacade);
        }
        return converterBairro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public TipoRelatorioGerencialArrecadacao getTipoRelatorioGerencialArrecadacao() {
        return tipoRelatorioGerencialArrecadacao;
    }

    public void setTipoRelatorioGerencialArrecadacao(TipoRelatorioGerencialArrecadacao tipoRelatorioGerencialArrecadacao) {
        this.tipoRelatorioGerencialArrecadacao = tipoRelatorioGerencialArrecadacao;
    }

    public int getAnoPagamentoFinal() {
        return anoPagamentoFinal;
    }

    public void setAnoPagamentoFinal(int anoPagamentoFinal) {
        this.anoPagamentoFinal = anoPagamentoFinal;
    }

    public int getAnoPagamentoInicial() {
        return anoPagamentoInicial;
    }

    public void setAnoPagamentoInicial(int anoPagamentoInicial) {
        this.anoPagamentoInicial = anoPagamentoInicial;
    }

    public void montaRelatorio() throws JRException, IOException {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();
        semDados = new StringBuilder("NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        filtros = new StringBuilder();

        String arquivoJasper = "relatorioGerencialDaArrecadacao.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("TIPOSUBREPORT", tipoRelatorioGerencialArrecadacao.name());
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTROS", filtros.toString());
        gerarRelatorio(arquivoJasper, parameters);
    }

    @URLAction(mappingId = "novoRelatorioGerencialArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        Calendar c = Calendar.getInstance();
        anoPagamentoInicial = c.get(Calendar.YEAR);
        anoPagamentoFinal = c.get(Calendar.YEAR);
        tipoRelatorioGerencialArrecadacao = null;
        contribuinte = null;
        bairro = null;
        codigoAtividade = null;
        banco = null;
        tributo = null;
        listaServico = null;
        where = new StringBuilder();
    }

    public void atribuiAnoInicalAoAnoFinal() {
        anoPagamentoFinal = anoPagamentoInicial;
        FacesUtil.atualizarComponente("Formulario:exercicioFinal");
    }

    private void montaCondicao() {

        String juncao = " where ";

        if (anoPagamentoInicial > 0) {
            this.where.append(juncao).append(" exercicio >= ").append(anoPagamentoInicial);
            juncao = " and ";
        }

        if (anoPagamentoFinal > 0) {
            this.where.append(juncao).append(" exercicio <= ").append(anoPagamentoFinal);
            juncao = " and ";
            filtros.append(" Exercicio entre ").append(anoPagamentoInicial).append(" e ").append(anoPagamentoFinal).append("; ");
        }


        if (tipoRelatorioGerencialArrecadacao != null) {

            if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.CONTRIBUINTE)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (contribuinte != null) {
                    this.where.append(juncao).append(" pessoa_id = ").append(contribuinte.getId());
                    juncao = " and ";
                    filtros.append(" Contribuinte = ").append(contribuinte.getId()).append("; ");
                }
            } else if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.BAIRRO)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (bairro != null) {
                    this.where.append(juncao).append(" bairro_id = ").append(bairro.getId());
                    juncao = " and ";
                    filtros.append(" Bairro = ").append(bairro.getDescricao()).append("; ");
                }
            } else if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.BANCO)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (banco != null) {
                    this.where.append(juncao).append(" agentearrecadador_id = ").append(banco.getId());
                    juncao = " and ";
                    filtros.append(" Banco = ").append(banco.getDescricao()).append("; ");
                }
            } else if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.CNAE)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (codigoAtividade != null) {
                    this.where.append(juncao).append(" codigoatividade_id = ").append(codigoAtividade.getId());
                    juncao = " and ";
                    filtros.append(" CNAE = ").append(codigoAtividade.getCodigoCnae()).append("; ");
                }
            } else if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.LISTA_SERVICO)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (listaServico != null) {
                    this.where.append(juncao).append(" codigoservico_id = ").append(listaServico.getId());
                    juncao = " and ";
                    filtros.append(" Serviço = ").append(listaServico.getCodigo()).append("; ");
                }
            } else if (tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.TRIBUTO)) {
                filtros.append("Classificação por ").append(tipoRelatorioGerencialArrecadacao.getDescricao()).append("; ");
                if (tributo != null) {
                    this.where.append(juncao).append(" tributo_id = ").append(tributo.getId());
                    juncao = " and ";
                    filtros.append(" Tributo = ").append(tributo.getDescricao()).append("; ");
                }
            }
        }
        //System.out.println("where ---> " + where.toString());
    }

    public List<SelectItem> getTiposRelatorioGerencialArrecadacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRelatorioGerencialArrecadacao tipo : TipoRelatorioGerencialArrecadacao.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<Pessoa> completaPessoas(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Bairro> completaBairro(String parte) {
        return bairroFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<CNAE> completaCnaes(String parte) {
        return codigoAtividadeFacade.listaFiltrando(parte.trim(), "codigoCnae");
    }

    public List<Banco> completaBancos(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Tributo> completaTributo(String parte) {
        return tributoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Servico> completaServico(String parte) {
        return listaServicoFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public boolean mostrarContribuinte() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.CONTRIBUINTE);
        }
        return retorno;
    }

    public boolean mostrarBairro() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.BAIRRO);
        }
        return retorno;
    }

    public boolean mostrarCnae() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.CNAE);
        }
        return retorno;
    }

    public boolean mostraBanco() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.BANCO);
        }
        return retorno;
    }

    public boolean mostraTributo() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.TRIBUTO);
        }
        return retorno;
    }

    public boolean mostraServico() {
        boolean retorno = false;
        if (tipoRelatorioGerencialArrecadacao != null) {
            retorno = tipoRelatorioGerencialArrecadacao.equals(TipoRelatorioGerencialArrecadacao.LISTA_SERVICO);
        }
        return retorno;
    }
}
