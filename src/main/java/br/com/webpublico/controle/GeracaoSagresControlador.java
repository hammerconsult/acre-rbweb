/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.ArquivosSagres;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.negocios.GeracaoSagresFacade;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fabio
 */
@ManagedBean(name = "geracaoSagresControlador")
@ViewScoped
@URLMapping(id = "novo-sagres", pattern = "/sagres/", viewId = "/faces/financeiro/sagres/geracao/gerar.xhtml")
public class GeracaoSagresControlador implements Serializable {

    @EJB
    private GeracaoSagresFacade geracaoSagresFacade;
    private Mes mesReferencia;
    private Integer anoReferencia;
    private List<ArquivosSagres> arquivosSelecionados;
    private Boolean todosArquivos;
    private File arquivo;
    private StreamedContent file;
    private transient Converter converterArquivoSagres = new Converter() {
        @Override
        public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
            Object retorno = null;
            for (ArquivosSagres arquivo : ArquivosSagres.values()) {
                if (string.equals(arquivo.name())) {
                    retorno = arquivo;
                }
            }
            return retorno;
        }

        @Override
        public String getAsString(FacesContext fc, UIComponent uic, Object o) {
            return ((ArquivosSagres) o).name();
        }
    };

    @URLAction(mappingId = "novo-sagres", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        Calendar cal = Calendar.getInstance();
        mesReferencia = Mes.getMesToInt(cal.get(Calendar.MONTH) + 1);
        anoReferencia = cal.get(Calendar.YEAR);
        arquivosSelecionados = new ArrayList<ArquivosSagres>();
        todosArquivos = false;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getArquivosSagres() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (ArquivosSagres arq : ArquivosSagres.values()) {
            retorno.add(new SelectItem(arq, arq.getDescricao()));
        }
        return retorno;
    }

    public Mes getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Mes mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public Integer getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(Integer anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public List<ArquivosSagres> getArquivosSelecionados() {
        return arquivosSelecionados;
    }

    public void setArquivosSelecionados(List<ArquivosSagres> arquivosSelecionados) {
        this.arquivosSelecionados = arquivosSelecionados;
    }

    public Converter getConverterArquivo() {
        return converterArquivoSagres;
    }

    public Boolean getTodosArquivos() {
        return todosArquivos;
    }

    public void setTodosArquivos(Boolean todosArquivos) {
        this.todosArquivos = todosArquivos;
    }

    public void selecionarTodos() {
        arquivosSelecionados.clear();
        if (todosArquivos) {
            arquivosSelecionados.addAll(Arrays.asList(ArquivosSagres.values()));
        }
    }

    public String getNomeArquivo(ArquivosSagres arq) {
        return StringUtil.preencheString(mesReferencia.getNumeroMes() + "", 2, '0')
                + StringUtil.preencheString(anoReferencia + "", 4, '0')
                + arq.getNomeArquivo() + ".txt";
    }

    public void gerarArquivos() throws FileNotFoundException, IOException {
        if (arquivosSelecionados.size() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Favor Informar pelo menos 1 arquivo para ser gerado!", ""));
        } else {
            for (ArquivosSagres arq : arquivosSelecionados) {
                geraArquivo(arq);
            }
        }
    }

    public void setaNullFile() {
        file = null;
    }

    public StreamedContent getFile() throws IOException {
        gerarArquivos();
        return file;
    }

    private void geraArquivo(ArquivosSagres arq) throws FileNotFoundException, IOException {
        arquivo = new File(getNomeArquivo(arq));
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            StringBuilder sb = new StringBuilder();

            //Method m=this.getClass().getMethod("gerarArquivo"+arq.name(), StringBuilder.class);
            //m.invoke(this, sb);

            switch (arq) {
                case ORCAMENTO: { // 01
                    geraArquivoOrcamento(sb);
                    break;
                }
                case UNIDADE_ORCAMENTARIA: { // 02
                    geraArquivoUnidadeOrcamentaria(sb);
                    break;
                }
                case PROGRAMAS: { // 03
                    geraArquivoProgramas(sb);
                    break;
                }
                case ACAO: { // 04
                    geraArquivoAcoes(sb);
                    break;
                }
                case DOTACAO: { // 05
                    geraArquivoDotacao(sb);
                    break;
                }
                case ELENCO_CONTAS: { // 06
                    geraArquivoElencoContas(sb);
                    break;
                }
                case CADASTRO_CONTAS: { // 07
                    geraArquivoCadastroContas(sb);
                    break;
                }
                case RELACIONAMENTO_RECEITA_ORCAM: { // 08
                    geraArquivoRelacionamentoReceitaOrcamentaria(sb);
                    break;
                }
                case RELACIONAMENTO_RECEITA_EXTRA: { // 09
                    geraArquivoRelacionamentoReceitaExtra(sb);
                    break;
                }
                case RELACIONAMENTO_DESPESA_EXTRA: { // 10
                    geraArquivoRelacionamentoDespesaExtra(sb);
                    break;
                }
                case PREVISAO_RECEITA: { // 11
                    geraArquivoPrevisaoReceita(sb);
                    break;
                }
                case ATUALIZACAO_ORCAMENTARIA: { // 12
                    geraArquivoAtualizacaoOrcamentaria(sb);
                    break;
                }
                case DECRETOS: { // 13
                    geraArquivoDecretos(sb);
                    break;
                }
                case EMPENHOS: { // 14
                    geraArquivoEmpenhos(sb);
                    break;
                }
                case EMPENHO_ESTORNO: { // 15
                    geraArquivoEmpenhoEstorno(sb);
                    break;
                }
                case LIQUIDACAO: { // 16
                    geraArquivoLiquidacao(sb);
                    break;
                }
                case PAGAMENTOS: { // 17
                    geraArquivoPagamentos(sb);
                    break;
                }
                case RETENCAO: { // 18
                    geraArquivoRetencao(sb);
                    break;
                }
                case RECEITA_ORCAMENTARIA: { // 19
                    geraArquivoReceitaOrcamentaria(sb);
                    break;
                }
                case RECEITA_EXTRA: { // 20
                    geraArquivoReceitaExtra(sb);
                    break;
                }
                case DESPESA_EXTRA: { // 21
                    geraArquivoDespesaExtra(sb);
                    break;
                }
                case RESTOS_INSCRITOS: { // 22
                    geraArquivoRestosInscritos(sb);
                    break;
                }
                case ESTORNO_RESTOS: { // 23
                    geraArquivoEstornoRestos(sb);
                    break;
                }
                case PAGAMENTOS_RESTOS: { // 24
                    geraArquivoPagamentosRestos(sb);
                    break;
                }
                case RETENCAO_RESTOS: { // 25
                    geraArquivoRetencaoRestos(sb);
                    break;
                }
                case CONCILIACAO_BANCARIA: { // 26
                    geraArquivoConciliacaoBancaria(sb);
                    break;
                }
                case SALDO_INICIAL: { // 27
                    geraArquivoSaldoInicial(sb);
                    break;
                }
                case SALDO_MENSAL: { // 28
                    geraArquivoSaldoMensal(sb);
                    break;
                }
                case LACAMENTO_CONTABIL: { // 29
                    geraArquivoLancamentoContabil(sb);
                    break;
                }
                case LACAMENTO_CONTABIL_ITEM: { // 30
                    geraArquivoLancamentoContabilItem(sb);
                    break;
                }
                case FORNACEDORES: { // 31
                    geraArquivoFornecedores(sb);
                    break;
                }
                case EXCESSO_ARRECADACAO: { // 32
                    geraArquivoExcessoArrecadacao(sb);
                    break;
                }
                case LIQUIDACAO_RESTOS_ESTORNO: { // 33
                    geraArquivoLiquidacaoRestosEstorno(sb);
                    break;
                }
                case PAGAMENTO_ESTORNO: { // 34
                    geraArquivoPagamentoEstorno(sb);
                    break;
                }
                case LIQUIDACAO_ESTONO: { // 35
                    geraArquivoLiquidacaoEstorno(sb);
                    break;
                }
                case LIQUIDACAO_RESTOS: { // 36
                    geraArquivoLiquidacaoRestos(sb);
                    break;
                }
                case PAGAMENTO_RESTO_ESTORNO: { // 37
                    geraArquivoPagamentoRestoEstorno(sb);
                    break;
                }
                case AGENTE_POLITICO: { // 38
                    geraArquivoAgentePolitico(sb);
                    break;
                }
                case ORDENADOR: { // 39
                    geraArquivoOrdenador(sb);
                    break;
                }
                case GESTOR: { // 40
                    geraArquivoGestor(sb);
                    break;
                }
            }
            fos.write(sb.toString().getBytes());
        }
        InputStream stream = new FileInputStream(arquivo);
        file = new DefaultStreamedContent(stream, "text/plain", getNomeArquivo(arq));
    }

    public String getDataFormatada(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        if (data != null) {
            return sdf.format(data);
        }
        return "";
    }

    private String zeroEsquerda(String str, int size) {
        if (str == null) {
            str = "";
        }
        str = str.replaceAll(" ", "");
        str = str.replaceAll("/", "");
        str = str.replaceAll("\\.", "");
        str = str.replaceAll(",", "");
        str = str.replaceAll("-", "");
        return StringUtils.leftPad(str.trim(), size, "0");
    }

    private String campoCaractere(String str, int size) {
        if (str == null) {
            str = "";
        }
        return StringUtils.rightPad(str.replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").trim(), size, ' ').substring(0, size);
    }

    public boolean desabilitarGeracao() {
        return arquivosSelecionados.isEmpty();
    }

    private String valorCampo(Object object) {
        if (object != null) {
            return object.toString();
        }
        return "";
    }

    private String campoReservadoTce(Integer size) {
        return StringUtil.preencheString("", size, ' ');
    }

    // 01
    private void geraArquivoOrcamento(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosOrcamento(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Ano de Vigência da Lei Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Data de aprovação da LOA
            linha.append(getDataFormatada((Date) registro[1]));
            // Número da Lei Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[2]), 4)).append(zeroEsquerda(valorCampo(registro[3]), 4));
            // Data de aprovação da LDO
            linha.append(getDataFormatada((Date) registro[4]));
            // Número da LDO
            linha.append(zeroEsquerda(valorCampo(registro[5]), 4)).append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 02
    private void geraArquivoUnidadeOrcamentaria(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosUnidadeOrcamentaria(anoReferencia);

        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código da unidade orçamentária utilizada pela LOA
            linha.append(zeroEsquerda(valorCampo(registro[0]), 5));
            // Denominação da unidade orçamentária
            linha.append(campoCaractere(valorCampo(registro[1]), 50));
            // CPF do gestor
            linha.append(campoCaractere(valorCampo(registro[2]), 11));
            // Reservado ao TCE
            linha.append(campoReservadoTce(71));
            // Tipo do ato que nomeou o secretário
            if (valorCampo(registro[3]).equals(TipoAtoLegal.LEI_ORDINARIA.name())) {
                linha.append("1");
            } else if (valorCampo(registro[3]).equals(TipoAtoLegal.DECRETO.name())) {
                linha.append("2");
            } else if (valorCampo(registro[3]).equals(TipoAtoLegal.PORTARIA.name())) {
                linha.append("3");
            } else {
                linha.append("4");
            }
            // Número da unidade Gestora
            linha.append(zeroEsquerda(valorCampo(registro[4]), 6));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 03
    private void geraArquivoProgramas(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosProgramas(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código do programa utilizado pela LOA
            linha.append(zeroEsquerda(valorCampo(registro[0]), 6));
            // Denominação do Programa
            linha.append(campoCaractere(valorCampo(registro[1]), 70));
            // Descrição do objetivo do Programa
            linha.append(campoCaractere(valorCampo(registro[2]), 150));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 04
    private void geraArquivoAcoes(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosAcoes(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código da Ação utilizada pelo LOA
            linha.append(zeroEsquerda(valorCampo(registro[0]), 6));
            // Denominação da Ação
            linha.append(campoCaractere(valorCampo(registro[1]), 70));
            // Identificação da Ação
            linha.append(valorCampo(registro[2]));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 05
    private void geraArquivoDotacao(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosDotacao(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de vigência da Lei Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Código da Função
            linha.append(zeroEsquerda(valorCampo(registro[2]), 2));
            // Código da SubFunção
            linha.append(zeroEsquerda(valorCampo(registro[3]), 3));
            // Código do Programa
            linha.append(zeroEsquerda(valorCampo(registro[4]), 4));
            // Código da Ação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 6));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código da Categoria Econômica
            linha.append(zeroEsquerda(valorCampo(registro[6]), 1));
            // Código da Natureza de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[7]), 1));
            // Código da Modalidade de Aplicação
            linha.append(zeroEsquerda(valorCampo(registro[8]), 2));
            // Código de Elemento de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[9]), 2));
            // Valor Fixado na Lei Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[10]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(38)).append("\n");

            sb.append(linha);
        }
    }

    // 06
    private void geraArquivoElencoContas(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosElencoContas(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano da criação da Conta
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Conta Contábil
            linha.append(zeroEsquerda(valorCampo(registro[1]), 14));
            // Título da Conta Contábil
            linha.append(campoCaractere(valorCampo(registro[2]), 80));
            // Código da Conta Reduzida
            linha.append(zeroEsquerda(valorCampo(registro[3]), 8));
            // Função da Conta
            linha.append(campoCaractere(valorCampo(registro[4]), 120));
            // Natureza do Saldo
            switch (valorCampo(registro[5])) {
                case "D":
                    linha.append("0");
                    break;
                case "C":
                    linha.append("1");
                    break;
                default:
                    linha.append("2");
                    break;
            }
            // Funcionamento da Conta
            linha.append(campoCaractere(valorCampo(registro[6]), 200));
            // Se é escriturada
            if (registro[7] != null && registro[7] instanceof BigDecimal) {
                linha.append("S");
            } else {
                linha.append("N");
            }
            // Sistema Contábil 1-Orçamentário 2-Financeiro 3-Patrimonial 4-Compensação
            linha.append("1");
            // Tipo da conta
            switch (valorCampo(registro[9])) {
                case "CONTABIL":
                    linha.append("1");
                    break;
                case "DESTINACAO":
                    linha.append("7");
                    break;
                case "DESPESA":
                    linha.append("3");
                    break;
                case "RECEITA":
                    linha.append("2");
                    break;
                case "AUXILIAR":
                    linha.append("4");
                    break;
                case "EXTRAORCAMENTARIA":
                    linha.append("5");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Conta Superior
            linha.append(zeroEsquerda(valorCampo(registro[10]), 14));
            // Nivel da Conta
            Long IdConta = Long.valueOf(valorCampo(registro[11]));
            Conta conta = geracaoSagresFacade.getContaFacade().recuperar(IdConta);
            if (conta != null) {
                linha.append(zeroEsquerda(conta.getNivel() + "", 2));
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 07
    private void geraArquivoCadastroContas(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosCadastroContas();
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número da Conta Bancária
            linha.append(campoCaractere(valorCampo(registro[0]), 12));
            // Código do Banco (FABRABAN)
            linha.append(zeroEsquerda(valorCampo(registro[1]), 3));
            // Código da Agência
            linha.append(campoCaractere(valorCampo(registro[2]), 6));
            // Título da Conta, que deverá identificar a sua origem
            linha.append(campoCaractere(valorCampo(registro[3]), 60));
            // Código da conta contábil correspondente
            linha.append(zeroEsquerda(valorCampo(registro[4]), 14));
            // Tipo da Conta Bancária
            switch (valorCampo(registro[5])) {
                case "CORRENTE":
                    linha.append("1");
                    break;
                case "APLICACAO_CORRENTE":
                    linha.append("2");
                    break;
                case "POUPANCA":
                    linha.append("3");
                    break;
                case "SALARIO":
                    linha.append("4");
                    break;
                case "VINCULADA":
                    linha.append("5");
                    break;
                case "APLICACAO_VINCULADA":
                    linha.append("6");
                    break;
                default:
                    linha.append("1");
                    break;
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 08
    private void geraArquivoRelacionamentoReceitaOrcamentaria(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRelacionamentoReceitaOrcamentaria(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Receita Orçamentária utilizado pelo unidade gestora
            linha.append(zeroEsquerda(valorCampo(registro[0]), 9));
            // Denominação da Receita Orçamentária
            linha.append(campoCaractere(valorCampo(registro[1]), 80));
            // STN, com a qual está relacionada
            linha.append(zeroEsquerda(valorCampo(registro[2]), 9));
            // Código da Conta Correspondente
            linha.append(zeroEsquerda(valorCampo(registro[3]), 14));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 09
    private void geraArquivoRelacionamentoReceitaExtra(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRelacionamentoReceitaExtra();
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Conta Contábil Correspondente
            linha.append(zeroEsquerda(valorCampo(registro[0]), 14));
            // Código de Receita Extra padrão, com a qual está relacionada
            linha.append(zeroEsquerda(valorCampo(registro[1]), 8));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 10
    private void geraArquivoRelacionamentoDespesaExtra(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRelacionamentoDespesaExtra();
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Conta Contábil Correspondente
            linha.append(zeroEsquerda(valorCampo(registro[0]), 14));
            // Código de Despesa Extra padrão, com a qual está relacionada
            linha.append(zeroEsquerda(valorCampo(registro[1]), 8));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 11
    private void geraArquivoPrevisaoReceita(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosPrevisaoReceita(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano da Lei Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Receita Orçamentária Prevista
            linha.append(zeroEsquerda(valorCampo(registro[1]), 9));
            // Valor previsto do orçamento
            linha.append(zeroEsquerda(valorCampo(registro[2]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 12
    private void geraArquivoAtualizacaoOrcamentaria(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosAtualizacaoOrcamentaria(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano da Atualização orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Código da Função
            linha.append(zeroEsquerda(valorCampo(registro[2]), 2));
            // Código da SubFunção
            linha.append(zeroEsquerda(valorCampo(registro[3]), 3));
            // Código do Programa
            linha.append(zeroEsquerda(valorCampo(registro[4]), 4));
            // Código da Ação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 6));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Número do decreto de abertura do crédito
            linha.append(zeroEsquerda(valorCampo(registro[6]), 4)).append(zeroEsquerda(valorCampo(registro[7]), 4));
            // Tipo de alteração orçamentária
            switch (valorCampo(registro[8])) {
                case "ANULACAO":
                    linha.append("4");
                    break;
                case "EXCESSO":
                    linha.append("3");
                    break;
                case "SUPERAVIT":
                    linha.append("2");
                    break;
                case "OPERACAO_CREDITO":
                    linha.append("1");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Código da Categoria Econômica
            linha.append(zeroEsquerda(valorCampo(registro[9]), 1));
            // Código da Natureza de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[10]), 1));
            // Código da Modalidade de Aplicação
            linha.append(zeroEsquerda(valorCampo(registro[11]), 2));
            // Código de Elemento de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[12]), 2));
            // Valor autorizado
            linha.append(zeroEsquerda(valorCampo(registro[13]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 13
    private void geraArquivoDecretos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosDecretos(anoReferencia, mesReferencia.getNumeroMes());

        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do Decreto
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Número do Decreto
            linha.append(zeroEsquerda(valorCampo(registro[1]), 4)).append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Número da Lei que autorizou
            linha.append(zeroEsquerda(valorCampo(registro[2]), 4)).append(zeroEsquerda(valorCampo(registro[3]), 4));
            // Data do Decreto
            linha.append(getDataFormatada((Date) registro[4]));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 14
    private void geraArquivoEmpenhos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosEmpenhos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de Emissão do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Código da Função
            linha.append(zeroEsquerda(valorCampo(registro[2]), 2));
            // Código da Subfunção
            linha.append(zeroEsquerda(valorCampo(registro[3]), 3));
            // Código do Programa
            linha.append(zeroEsquerda(valorCampo(registro[4]), 4));
            // Código da Ação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 6));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código da Categoria Econômica
            linha.append(zeroEsquerda(valorCampo(registro[6]), 1));
            // Código da Natureza de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[7]), 1));
            // Código da Modalidade de Aplicação
            linha.append(zeroEsquerda(valorCampo(registro[8]), 2));
            // Código do Elemento de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[9]), 2));
            // Código do SubElemento de Despesa // quando não possuir usar 999
            if (valorCampo(registro[10]).equals("00") || "".equals(valorCampo(registro[10]).trim())) {
                linha.append("999");
            } else {
                linha.append(zeroEsquerda(valorCampo(registro[10]), 3));
            }
            // Modalidade de Licitação
            switch (valorCampo(registro[11])) {
                case "CONVITE":
                    linha.append("03");
                    break;
                case "TOMADA_PRECO":
                    linha.append("02");
                    break;
                case "CONCORRENCIA":
                    linha.append("01");
                    break;
                case "CONCURSO":
                    linha.append("04");
                    break;
                case "LEILAO":
                    linha.append("05");
                    break;
                case "PREGAO":
                    linha.append("00");
                    break;
                case "DISPENSA_LICITACAO":
                    linha.append("07");
                    break;
                case "INEXIGIBILIDADE":
                    linha.append("08");
                    break;
                case "REGISTRO_PRECO":
                    linha.append("10");
                    break;
                default:
                    linha.append("09");
                    break;
            }
            // Número do Processo da Licitação ref. a despesa
            linha.append(zeroEsquerda(valorCampo(registro[12]), 9));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[13]), 7));
            // Tipo do Empenho
            switch (valorCampo(registro[14])) {
                case "ORDINARIO":
                    linha.append("1");
                    break;
                case "GLOBAL":
                    linha.append("3");
                    break;
                case "ESTIMATIVO":
                    linha.append("2");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Data de emissão do empenho
            linha.append(getDataFormatada((Date) registro[15]));
            // Valor empenhado
            linha.append(zeroEsquerda(valorCampo(registro[16]), 16));
            // Histórico do empenho
            linha.append(campoCaractere(valorCampo(registro[17]), 510));
            // CPF / CNPJ do credor
            linha.append(zeroEsquerda(valorCampo(registro[18]), 14));
            // Número do Procedimento Licitação ref. a despesa
            linha.append(zeroEsquerda(valorCampo(registro[19]), 9));
            // Código do tipo de fonte de recurso
            linha.append(zeroEsquerda(valorCampo(registro[20]), 2));
            // CPF do ordenador de despesa
            linha.append(zeroEsquerda(valorCampo(registro[21]), 11));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 15
    private void geraArquivoEmpenhoEstorno(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosEmpenhoEstorno(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de Emissão do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do estorno
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do estorno
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Histórico do estorno
            linha.append(campoCaractere(valorCampo(registro[6]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 16
    private void geraArquivoLiquidacao(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLiquidacao(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da liquidação
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data da liquidação
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor da liquidação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Tipo do Documento
            if ("Nota Fiscal".equals(valorCampo(registro[6]))) {
                linha.append("1");
            } else {
                linha.append("9");
            }
            // Número do Documento
            linha.append(campoCaractere(valorCampo(registro[7]), 20));
            // Série do Documento
            linha.append(campoCaractere(valorCampo(registro[8]), 5));
            // UF do documento
            linha.append(campoCaractere(valorCampo(registro[9]), 2));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 17
    private void geraArquivoPagamentos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosPagamentos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho a ser pago
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da parcela do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do pagamento
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Número do conta bancária de débito
            linha.append(campoCaractere(valorCampo(registro[6]), 12));

            if ("CHEQUE".equals(valorCampo(registro[8]))) {
                // Número do cheque emitido
                linha.append(campoCaractere(valorCampo(registro[7]), 6));
                // Número do Documento de Débito automático
                linha.append(campoCaractere("", 11));
            } else {
                // Número do cheque emitido
                linha.append(campoCaractere("", 6));
                // Número do Documento de Débito automático
                linha.append(campoCaractere(valorCampo(registro[7]), 11));
            }

            // Código do banco para crédito em conta
            linha.append(campoCaractere(valorCampo(registro[9]), 3));
            // Código da agência para crédito em conta
            linha.append(campoCaractere(valorCampo(registro[10]), 6));
            // Número da conta bancária para crédito
            linha.append(campoCaractere(valorCampo(registro[11]), 12));
            // Origem de recursos
            linha.append(zeroEsquerda(valorCampo(registro[12]), 2));
            // Tipo da conta bancária
            switch (valorCampo(registro[13])) {
                case "CONTA_CORRENTE":
                    linha.append("1");
                    break;
                case "CONTA_POUPANCA":
                    linha.append("3");
                    break;
                default:
                    linha.append("1");
                    break;
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 18
    private void geraArquivoRetencao(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRetencao(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho de origem
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da parcela do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Valor da retenção
            linha.append(zeroEsquerda(valorCampo(registro[4]), 16));
            // Tipo de retenção efetuada
            linha.append(zeroEsquerda(valorCampo(registro[5]), 2));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 19
    private void geraArquivoReceitaOrcamentaria(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosReceitaOrcamentaria(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Receita Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 9));
            // Tipo de registro
            switch (valorCampo(registro[1])) {
                case "NORMAL":
                    linha.append("1");
                    break;
                case "ESTORNO":
                    linha.append("2");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Valor Mensal lançado
            linha.append(zeroEsquerda(valorCampo(registro[2]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 20
    private void geraArquivoReceitaExtra(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosReceitaExtra(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Receita Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 14));
            // Tipo de registro
            linha.append("1");
            // Valor Mensal lançado
            linha.append(zeroEsquerda(valorCampo(registro[1]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 21
    private void geraArquivoDespesaExtra(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosDespesaExtra(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Código da Receita Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[0]), 14));
            // Tipo de registro
            linha.append("1");
            // Valor Mensal lançado
            linha.append(zeroEsquerda(valorCampo(registro[1]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 22
    private void geraArquivoRestosInscritos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRestosInscritos(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de Emissão do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Código da Função
            linha.append(zeroEsquerda(valorCampo(registro[2]), 2));
            // Código da Subfunção
            linha.append(zeroEsquerda(valorCampo(registro[3]), 3));
            // Código do Programa
            linha.append(zeroEsquerda(valorCampo(registro[4]), 4));
            // Código da Ação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 6));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));
            // Código da Categoria Econômica
            linha.append(zeroEsquerda(valorCampo(registro[6]), 1));
            // Código da Natureza de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[7]), 1));
            // Código da Modalidade de Aplicação
            linha.append(zeroEsquerda(valorCampo(registro[8]), 2));
            // Código do Elemento de Despesa
            linha.append(zeroEsquerda(valorCampo(registro[9]), 2));
            // Código do SubElemento de Despesa // quando não possuir usar 999
            if (valorCampo(registro[10]).equals("00") || "".equals(valorCampo(registro[10]).trim())) {
                linha.append("999");
            } else {
                linha.append(zeroEsquerda(valorCampo(registro[10]), 3));
            }
            // Modalidade de Licitação
            switch (valorCampo(registro[11])) {
                case "CONVITE":
                    linha.append("03");
                    break;
                case "TOMADA_PRECO":
                    linha.append("02");
                    break;
                case "CONCORRENCIA":
                    linha.append("01");
                    break;
                case "CONCURSO":
                    linha.append("04");
                    break;
                case "LEILAO":
                    linha.append("05");
                    break;
                case "PREGAO":
                    linha.append("00");
                    break;
                case "DISPENSA_LICITACAO":
                    linha.append("07");
                    break;
                case "INEXIGIBILIDADE":
                    linha.append("08");
                    break;
                case "REGISTRO_PRECO":
                    linha.append("10");
                    break;
                default:
                    linha.append("09");
                    break;
            }
            // Número do Processo da Licitação ref. a despesa
            linha.append(zeroEsquerda(valorCampo(registro[12]), 9));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[13]), 7));
            // Tipo do Empenho
            switch (valorCampo(registro[14])) {
                case "ORDINARIO":
                    linha.append("1");
                    break;
                case "GLOBAL":
                    linha.append("3");
                    break;
                case "ESTIMATIVO":
                    linha.append("2");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Data de emissão do empenho
            linha.append(getDataFormatada((Date) registro[15]));
            // Valor empenhado
            linha.append(zeroEsquerda(valorCampo(registro[16]), 16));
            // Histórico do empenho
            linha.append(campoCaractere(valorCampo(registro[17]), 510));
            // CPF / CNPJ do credor
            linha.append(zeroEsquerda(valorCampo(registro[18]), 14));
            // Código do tipo de fonte de recurso
            linha.append(zeroEsquerda(valorCampo(registro[19]), 2));
            // Valor processado
            linha.append(zeroEsquerda(valorCampo(registro[20]), 16));
            // Valor não processado
            linha.append(zeroEsquerda(valorCampo(registro[21]), 16));
            // CPF do ordenador de despesa
            linha.append(zeroEsquerda(valorCampo(registro[22]), 11));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 23
    private void geraArquivoEstornoRestos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosEstornoRestos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do Empenho estornado
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho estornado
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do estorno
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do estorno
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Motivo do estorno
            linha.append(campoCaractere(valorCampo(registro[6]), 120));

            // Reservado ao TCE
            linha.append(campoReservadoTce(1));
            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 24
    private void geraArquivoPagamentosRestos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosPagamentosRestos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do Empenho estornado
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho a ser pago
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da parcela do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do pagamento
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Número da conta bancária de débito
            linha.append(campoCaractere(valorCampo(registro[6]), 12));
            // Número do cheque emitido
            linha.append(campoCaractere(valorCampo(registro[7]), 6));
            // Número do documento de débito automático
            linha.append(campoCaractere(valorCampo(registro[8]), 11));
            // Código do Banco para crédito em conta
            linha.append(campoCaractere(valorCampo(registro[9]), 3));
            // Código da Agência para crédito em conta
            linha.append(campoCaractere(valorCampo(registro[10]), 6));
            // Número da conta bancária para crédito
            linha.append(campoCaractere(valorCampo(registro[11]), 12));
            // Fonte de recurso
            linha.append(zeroEsquerda(valorCampo(registro[12]), 2));
            // Tipo de conta bancária
            linha.append(campoCaractere(valorCampo(registro[13]), 1));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 25
    private void geraArquivoRetencaoRestos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosRetencaoRestos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do Empenho estornado
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho de origem
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da parcela do pagamento
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Valor da Retenção
            linha.append(zeroEsquerda(valorCampo(registro[4]), 16));
            // Identifica o tipo de retenção efetuada
            linha.append(zeroEsquerda(valorCampo(registro[5]), 2));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 26
    private void geraArquivoConciliacaoBancaria(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosConciliacaoBancaria(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número da Conta bancária
            linha.append(campoCaractere(valorCampo(registro[0]), 12));
            // Número sequencial
            linha.append(zeroEsquerda(valorCampo(registro[1]), 8));
            // Forma de conciliação
            linha.append(zeroEsquerda(valorCampo(registro[2]), 1));
            // Descrição sobre o valor conciliado
            linha.append(campoCaractere(valorCampo(registro[3]), 150));
            // Data do fato conciliado
            linha.append(getDataFormatada((Date) registro[4]));
            // Número do cheque
            linha.append(zeroEsquerda(valorCampo(registro[5]), 6));
            // Número do documento do Débito Automático
            linha.append(campoCaractere(valorCampo(registro[6]), 11));
            // Valor conciliado
            linha.append(zeroEsquerda(valorCampo(registro[7]), 16));
            // Tipo da conta bancária
            linha.append(campoCaractere(valorCampo(registro[8]), 1));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 27
    private void geraArquivoSaldoInicial(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosSaldoInicial(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número da Conta Bancária
            linha.append(campoCaractere(valorCampo(registro[0]), 12));
            // Valor do saldo conciliado
            linha.append(zeroEsquerda(valorCampo(registro[1]), 16));
            // Tipo da Conta Bancária
            switch (valorCampo(registro[2])) {
                case "CORRENTE":
                    linha.append("1");
                    break;
                case "APLICACAO_CORRENTE":
                    linha.append("2");
                    break;
                case "POUPANCA":
                    linha.append("3");
                    break;
                case "SALARIO":
                    linha.append("4");
                    break;
                case "VINCULADA":
                    linha.append("5");
                    break;
                case "APLICACAO_VINCULADA":
                    linha.append("6");
                    break;
                default:
                    linha.append("1");
                    break;
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 28
    private void geraArquivoSaldoMensal(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosSaldoMensal(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número da Conta Bancária
            linha.append(campoCaractere(valorCampo(registro[0]), 12));
            // Valor do saldo conciliado
            linha.append(zeroEsquerda(valorCampo(registro[1]), 16));
            // Tipo da Conta Bancária
            switch (valorCampo(registro[2])) {
                case "CORRENTE":
                    linha.append("1");
                    break;
                case "APLICACAO_CORRENTE":
                    linha.append("2");
                    break;
                case "POUPANCA":
                    linha.append("3");
                    break;
                case "SALARIO":
                    linha.append("4");
                    break;
                case "VINCULADA":
                    linha.append("5");
                    break;
                case "APLICACAO_VINCULADA":
                    linha.append("6");
                    break;
                default:
                    linha.append("1");
                    break;
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 29
    private void geraArquivoLancamentoContabil(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLancamentoContabil(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número do registro ou lançamento
            linha.append(zeroEsquerda(valorCampo(registro[0]), 8));
            // Data do registro ou lançamento
            linha.append(getDataFormatada((Date) registro[1]));
            // Tipo do registro ou lançamento
            switch (valorCampo(registro[2])) {
                case "ORDINARIO":
                    linha.append("1");
                    break;
                case "ESTORNO":
                    linha.append("2");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Tipo do Movimento Contábil
            switch (valorCampo(registro[3])) {
                case "ABERTURA":
                    linha.append("1");
                    break;
                case "ENCERRAMENTO":
                    linha.append("3");
                    break;
                case "APURACAO":
                    linha.append("4");
                    break;
                case "MENSAL":
                    linha.append("2");
                    break;
                default:
                    linha.append("1");
                    break;
            }
            // Histórico do registro contábil
            linha.append(campoCaractere(valorCampo(registro[4]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 30
    private void geraArquivoLancamentoContabilItem(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLancamentoContabilItem(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número do registro ou lançamento
            linha.append(zeroEsquerda(valorCampo(registro[0]), 8));
            // Código da conta contábil
            linha.append(zeroEsquerda(valorCampo(registro[1]), 14));
            // Natureza do lançamento
            linha.append(zeroEsquerda(valorCampo(registro[2]), 1));
            // Valor lançado
            linha.append(zeroEsquerda(valorCampo(registro[3]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 31
    private void geraArquivoFornecedores(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosFornecedores(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Número do CPF / CNPJ
            linha.append(zeroEsquerda(valorCampo(registro[0]), 14));
            // Nome, denominação ou razão social
            linha.append(campoCaractere(valorCampo(registro[1]), 80));
            // Tipo de credor
            linha.append(zeroEsquerda(valorCampo(registro[2]), 1));
            // Sigla da UF
            linha.append(campoCaractere(valorCampo(registro[3]), 2));
            // Município
            linha.append(campoCaractere(valorCampo(registro[4]), 60));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 32
    private void geraArquivoExcessoArrecadacao(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosExcessoArrecadacao(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Data do registro da receita excedida
            linha.append(getDataFormatada((Date) registro[0]));
            // Código da Receita Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 9));
            // Valor excedido
            linha.append(zeroEsquerda(valorCampo(registro[2]), 16));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 33
    private void geraArquivoLiquidacaoRestosEstorno(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLiquidacaoRestosEstorno(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de Emissão do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data da liquidação
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor liquidado
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Histórico da liquidação
            linha.append(campoCaractere(valorCampo(registro[6]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 34
    private void geraArquivoPagamentoEstorno(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosPagamentoEstorno(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do estorno
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do estorno
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Histórico do estorno
            linha.append(campoCaractere(valorCampo(registro[6]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 35
    private void geraArquivoLiquidacaoEstorno(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLiquidacaoEstorno(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano de Emissão do Empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do estorno
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do estorno
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Histórico do estorno
            linha.append(campoCaractere(valorCampo(registro[6]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 36
    private void geraArquivoLiquidacaoRestos(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosLiquidacaoRestos(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número da liquidação
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data da liquidação
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor da liquidação
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Tipo do Documento
            if ("Nota Fiscal".equals(valorCampo(registro[6]))) {
                linha.append("1");
            } else {
                linha.append("9");
            }
            // Número do Documento
            linha.append(campoCaractere(valorCampo(registro[7]), 20));
            // Série do Documento
            linha.append(campoCaractere(valorCampo(registro[8]), 5));
            // UF do documento
            linha.append(campoCaractere(valorCampo(registro[9]), 2));


            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 37
    private void geraArquivoPagamentoRestoEstorno(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosPagamentoRestoEstorno(anoReferencia, mesReferencia.getNumeroMes());
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // Ano do empenho
            linha.append(zeroEsquerda(valorCampo(registro[0]), 4));
            // Código da unidade orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Número do empenho
            linha.append(zeroEsquerda(valorCampo(registro[2]), 7));
            // Número do estorno
            linha.append(zeroEsquerda(valorCampo(registro[3]), 7));
            // Data do estorno
            linha.append(getDataFormatada((Date) registro[4]));
            // Valor do estorno
            linha.append(zeroEsquerda(valorCampo(registro[5]), 16));
            // Histórico do estorno
            linha.append(campoCaractere(valorCampo(registro[6]), 150));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 38
    private void geraArquivoAgentePolitico(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosAgentePolitico(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // CPF
            linha.append(zeroEsquerda(valorCampo(registro[0]), 11));
            // RG
            linha.append(zeroEsquerda(valorCampo(registro[1]), 11));
            // Órgão expedidor
            linha.append(campoCaractere(valorCampo(registro[2]), 10));
            // Nome
            linha.append(campoCaractere(valorCampo(registro[3]), 60));
            // Município
            linha.append(campoCaractere(valorCampo(registro[4]), 60));
            // UF
            linha.append(campoCaractere(valorCampo(registro[5]), 2));
            // Email
            linha.append(campoCaractere(valorCampo(registro[6]), 50));
            // Tipo de agente político
            switch (valorCampo(registro[7]).toUpperCase()) {
                case "PREFEITO":
                    linha.append("01");
                    break;
                case "VICE-PREFEITO":
                    linha.append("02");
                    break;
                case "VEREADOR":
                    linha.append("03");
                    break;
                case "VEREADOR-PRESIDENTE":
                    linha.append("04");
                    break;
                case "SECRETARIO MUNICIPAL":
                    linha.append("05");
                    break;
                case "SECRETARIO ESTADUAL":
                    linha.append("06");
                    break;
                case "DIRETOR":
                    linha.append("07");
                    break;
                case "SUPERINTENDENTE":
                    linha.append("08");
                    break;
                case "CHEFE DE GABINETE":
                    linha.append("10");
                    break;
                case "PROCURADOR":
                    linha.append("11");
                    break;
                case "PRESIDENTE":
                    linha.append("12");
                    break;
                default:
                    linha.append("09");
                    break;
            }

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 39
    private void geraArquivoOrdenador(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosOrdenador(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // CPF
            linha.append(zeroEsquerda(valorCampo(registro[0]), 11));
            // Código da Unidade Orçamentária
            linha.append(zeroEsquerda(valorCampo(registro[1]), 5));
            // Data do início ou fim de vigência
            linha.append(getDataFormatada((Date) registro[2]));
            // Tipo da data
            linha.append(zeroEsquerda(valorCampo(registro[3]), 1));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }

    // 40
    private void geraArquivoGestor(StringBuilder sb) {
        List lista = geracaoSagresFacade.listaRegistrosGestor(anoReferencia);
        for (int i = 0; i < lista.size(); i++) {
            StringBuilder linha = new StringBuilder();
            Object registro[] = (Object[]) lista.get(i);
            // Reservado ao TCE
            linha.append(campoReservadoTce(6));

            // CPF do Agente Político
            linha.append(zeroEsquerda(valorCampo(registro[0]), 11));
            // Data do início ou fim de vigência
            linha.append(getDataFormatada((Date) registro[1]));
            // Tipo da data
            linha.append(zeroEsquerda(valorCampo(registro[2]), 1));

            // Reservado ao TCE
            linha.append(campoReservadoTce(6)).append("\n");

            sb.append(linha);
        }
    }
}
