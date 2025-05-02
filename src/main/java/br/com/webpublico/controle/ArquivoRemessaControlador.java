/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.StringUtil;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author fabio
 */
@ManagedBean
@SessionScoped
public class ArquivoRemessaControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ArquivoRemessaControlador.class);

    private Bordero selecionado;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private BorderoFacade borderoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private SubConta subConta;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterSubConta;
    private File arquivo;
    private StreamedContent file;
    private List<FileUploadEvent> fileUploadEvents;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void novo() {
        selecionado = new Bordero();
        subConta = new SubConta();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
    }

    public void novoArquivo() {
        fileUploadEvents = new ArrayList<FileUploadEvent>();
    }

    public void selecionar(ActionEvent evento) {
        Bordero b = (Bordero) evento.getComponent().getAttributes().get("objeto");
        selecionado = borderoFacade.recuperar(b.getId());
        subConta = new SubConta();

        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        hierarquiaOrganizacional.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrganizacional = borderoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(((Bordero) selecionado).getUnidadeOrganizacional(), hierarquiaOrganizacional, sistemaControlador.getExercicioCorrente());
    }

    public List<Bordero> getLista() {
        return borderoFacade.lista();
    }

    public boolean mostraListasPagamentos() {
        return (selecionado.getListaPagamentos().size() > 0) || (selecionado.getListaPagamentosExtra().size() > 0);
    }

    public List<UnidadeOrganizacional> unidades(String parte) {
        return borderoFacade.getUnidadeOrganizacionalFacade().listaFiltrando(parte.trim());
    }

    public void confirmarBordero() {
        if (mostraListasPagamentos()) {

            for (BorderoPagamento pag : selecionado.getListaPagamentos()) {
                pag.getPagamento().setStatus(StatusPagamento.BORDERO);
                pagamentoFacade.salvar(pag.getPagamento());
            }

            for (BorderoPagamentoExtra pag : selecionado.getListaPagamentosExtra()) {
                pag.getPagamentoExtra().setStatus(StatusPagamento.BORDERO);
                pagamentoExtraFacade.salvar(pag.getPagamentoExtra());
            }

            borderoFacade.salvar(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Borderô salvo com sucesso!", ""));
            novo();
        }
    }

    public void cancelarBordero() {
        novo();
    }

    public void gerarBordero() {
        if (hierarquiaOrganizacional == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Favor informar a unidade organizacional!", ""));
        } else if (subConta == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Favor informar a conta financeira!", ""));
        } else if (selecionado.getDataDebito() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Favor informar a data de débito!", ""));
        } else {
            Boolean gerado = false;

            selecionado = new Bordero();
            selecionado.setSituacao(SituacaoBordero.ABERTO);
//            selecionado.setDataDebito(selecionado.getDataDebito());
            selecionado.setDataGeracao(new Date());
            selecionado.setDataGeracaoArquivo(null);
            selecionado.setSubConta(subConta);
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            selecionado.setSequenciaArquivo(borderoFacade.ultimoCodigoMaisUm());
//            selecionado.setObservacao(observacao);

            List<Pagamento> listaPagamentos = pagamentoFacade.listaPorSubContaStatus(subConta, hierarquiaOrganizacional.getSubordinada(), StatusPagamento.DEFERIDO);
            for (Pagamento pag : listaPagamentos) {
                selecionado.getListaPagamentos().add(new BorderoPagamento(selecionado,pag));
                gerado = true;
            }

            List<PagamentoExtra> listaPagamentosExtras = pagamentoExtraFacade.listaPorSubContaStatus(subConta, hierarquiaOrganizacional.getSubordinada(), StatusPagamento.DEFERIDO);
            for (PagamentoExtra pagExtra : listaPagamentosExtras) {
                selecionado.getListaPagamentosExtra().add(new BorderoPagamentoExtra(selecionado, pagExtra));
                gerado = true;
            }

            if (gerado) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Borderô gerado com sucesso!", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível gerar o borderô!", "Não existe pagamentos deferidos para a Conta Financeira na Unidade selecionada!"));
            }
        }

    }

    public List<SubConta> completaSubConta(String parte) {
        ////System.out.println(hierarquiaOrganizacional.getSubordinada());
        List<SubConta> lista = subContaFacade.listaPorUnidadeOrganizacional(parte.trim(), hierarquiaOrganizacional.getSubordinada(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao());
        if (lista.isEmpty()) {
            SubConta s = new SubConta();
            s.setDescricao("Nenhuma Conta encontrada para a Unidade selecionada!");
            lista.add(s);
        }
        return lista;
    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterSubConta;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public boolean desabilitaVerBordero() {
        return true;
    }

    public Bordero getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Bordero selecionado) {
        this.selecionado = selecionado;
    }

    public void removePagamentoExtra(PagamentoExtra pagExtra) {
        selecionado.getListaPagamentosExtra().remove(pagExtra);
    }

    public void removePagamento(Pagamento pag) {
        if (pag.getStatus().equals(StatusPagamento.BORDERO)) {
            pag.setStatus(StatusPagamento.DEFERIDO);
        }
        selecionado.getListaPagamentos().remove(pag);
        if (selecionado.getId() != null) {
            borderoFacade.salvar(selecionado);
        }
    }

    public void geraArquivoBordero() throws FileNotFoundException, IOException {
        String nomeSubConta = selecionado.getSubConta().getDescricao().replaceAll(" ", "").replaceAll("\\.", "").replaceAll("/", "").toUpperCase();
        String nomeArquivo = getDataFormatada(selecionado.getDataDebito()) + "_" + nomeSubConta + ".REM";
        arquivo = new File(nomeArquivo);
        Integer qtdeLinhas = 0;
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            StringBuilder sb = new StringBuilder();
            StringBuilder linha = new StringBuilder();

            FebrabanHeaderArquivo fha = new FebrabanHeaderArquivo();
            fha.setAgencia(selecionado.getSubConta().getContaBancariaEntidade().getAgencia());
            fha.setBanco(selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            fha.setContaBancariaEntidade(selecionado.getSubConta().getContaBancariaEntidade());
            fha.setEntidade(selecionado.getSubConta().getContaBancariaEntidade().getEntidade());
            fha.setPessoaJuridica(selecionado.getSubConta().getContaBancariaEntidade().getEntidade().getPessoaJuridica());
            fha.setLote("");
            fha.setSequencia(1);
            //fha.setUnidadeOrganizacional(selecionado.getSubConta().getUnidadesOrganizacionais().get(0).getUnidadeOrganizacional());
            sb.append(fha.getHeaderArquivo());
            qtdeLinhas += 1;

            // Forma de Pagamento
            // 01 = Crédito em Conta Corrente
            // 02 = Cheque Pagamento / Administrativo
            // 03 = DOC/TED (1) (2)
            // 04 = Cartão Salário(somente para Tipo de Serviço = '30')
            // 05 = Crédito em Conta Poupança
            // 10 = OP à Disposição
            // 11 = Pagamento de Contas e Tributos com Código de Barras
            // 16 = Tributo - DARF Normal
            // 17 = Tributo - GPS (Guia da Previdência Social)
            // 18 = Tributo - DARF Simples
            // 19 = Tributo - IPTU - Prefeituras
            // 20 = Pagamento com Autenticação
            // 21 = Tributo - DARJ
            // 22 = Tributo - GARE-SP ICMS
            // 23 = Tributo - GARE-SP DR
            // 24 = Tributo - GARE-SP ITCMD
            // 25 = Tributo - IPVA
            // 26 = Tributo - Licenciamento
            // 27 = Tributo - DPVAT
            // 30 = Liquidação de Títulos do Próprio Banco
            // 31 = Pagamento de Títulos de Outros Bancos
            // 40 = Extrato de Conta Corrente
            // 41 = TED - Outra Titularidade (1)
            // 43 = TED - Mesma Titularidade (1)
            // 44 = TED para Transferência de Conta Investimento
            // 50 = Débito em Conta Corrente
            // 70 = Extrato para Gestão de Caixa
            // 71 = Depósito Judicial em Conta Corrente
            // 72 = Depósito Judicial em Poupança
            // 73 = Extrato de Conta Investimento
            sb.append(gerarHeaderLote(fha, "03", 1));
            qtdeLinhas += 1;

            Integer sequencia = 0;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (BorderoPagamento pag : selecionado.getListaPagamentos()) {
                linha = new StringBuilder();
                qtdeLinhas += 2;
                sequencia += 1;
                linha.append(montarLinhaSegmentoA(fha, 1, sequencia, pag.getPagamento()));
                linha.append(montarLinhaSegmentoB(fha, 1, sequencia, pag.getPagamento()));
                sb.append(linha);
                valorTotal = valorTotal.add(pag.getPagamento().getValorFinal());
            }

            for (BorderoPagamentoExtra pag : selecionado.getListaPagamentosExtra()) {
                linha = new StringBuilder();
                qtdeLinhas += 2;
                sequencia += 1;
                linha.append(montarLinhaSegmentoA(fha, 1, sequencia, pag.getPagamentoExtra()));
                linha.append(montarLinhaSegmentoB(fha, 1, sequencia, pag.getPagamentoExtra()));
                sb.append(linha);
                valorTotal = valorTotal.add(pag.getPagamentoExtra().getValor());
            }

            linha = new StringBuilder();
            qtdeLinhas += 1;
            linha.append(gerarTrailerLote(selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco(), 1, qtdeLinhas, valorTotal));
            sb.append(linha);

            linha = new StringBuilder();
            qtdeLinhas += 1;
            linha.append(gerarTrailerArquivo(fha, 1, qtdeLinhas));
            sb.append(linha);


            fos.write(sb.toString().getBytes());
        }

        if (selecionado.getSituacao().equals(SituacaoBordero.ABERTO)) {
            selecionado.setDataGeracaoArquivo(new Date());
            selecionado.setSituacao(SituacaoBordero.EFETUADO);
            borderoFacade.salvar(selecionado);
        }

        InputStream stream = new FileInputStream(arquivo);
        file = new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
    }

    private void estornoPagamentosBordero() {
        for (BorderoPagamento pag : selecionado.getListaPagamentos()) {
            pag.getPagamento().setStatus(StatusPagamento.DEFERIDO);
            pagamentoFacade.salvar(pag.getPagamento());
        }

        for (BorderoPagamentoExtra pag : selecionado.getListaPagamentosExtra()) {
            pag.getPagamentoExtra().setStatus(StatusPagamento.DEFERIDO);
            pagamentoExtraFacade.salvar(pag.getPagamentoExtra());
        }
    }

    public void excluirSelecionado() {
        try {
            estornoPagamentosBordero();
            borderoFacade.remover(selecionado);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Este registro não pode ser excluído."));
        }
    }

    public boolean desabilitaExluir() {
        return selecionado.getSituacao().equals(SituacaoBordero.EFETUADO);
    }

    public StreamedContent getFile() throws FileNotFoundException, IOException {
        geraArquivoBordero();
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public String gerarHeaderLote(FebrabanHeaderArquivo headerArquivo, String formaLancamento, Integer sequenciaLote) {
        StringBuilder sb = new StringBuilder();
        EnderecoCorreio enderecoCorreio = null;
        PessoaJuridica pj = pessoaJuridicaFacade.recuperar(headerArquivo.getPessoaJuridica().getId());

        for (EnderecoCorreio end : pj.getEnderecos()) {
            enderecoCorreio = end;
        }
        validaEndereco(enderecoCorreio);
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getBanco().getNumeroBanco(), 3, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        sb.append("1");
        sb.append("C");
        sb.append("30");
        sb.append(formaLancamento);
        sb.append("044");
        sb.append(" ");
        sb.append("2");
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getPessoaJuridica().getCnpj().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getContaBancariaEntidade().getCodigoDoConvenio(), 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getAgencia().getNumeroAgencia() + "", 5, "0"));
        if (headerArquivo.getAgencia().getDigitoVerificador() == null) {
            sb.append(" ");
        } else {
            sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getAgencia().getDigitoVerificador().toString(), 1, " "));
        }
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        if (headerArquivo.getContaBancariaEntidade().getDigitoVerificador() == null) {
            sb.append(" ");
        } else {
            sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
        }
        sb.append(" ");
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getPessoaJuridica().getRazaoSocial(), 30, " "));

        switch (formaLancamento) {
            case "01":
                sb.append(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
                break;
            case "03":
                sb.append(StringUtil.cortaOuCompletaDireita("DOC/TED", 40, " "));
                break;
            case "05":
                sb.append(StringUtil.cortaOuCompletaDireita("DEPOSITO EM POUPANCA", 40, " "));
                break;
            default:
                sb.append(StringUtils.leftPad(" ", 40));
                break;
        }

        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro(), 30, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getNumero(), 5, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento(), 15, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade(), 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getCep().replaceAll("-", ""), 8, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getUf(), 2, " "));
        sb.append(StringUtils.repeat(" ", 2));
        sb.append(StringUtils.repeat(" ", 6));
        sb.append(StringUtils.repeat(" ", 10));
        sb.append("\n");
        return sb.toString();
    }

    private void validaEndereco(EnderecoCorreio endereco) {
        if (endereco.getBairro() == null) {
            endereco.setBairro("");
        }
        if (endereco.getCep() == null) {
            endereco.setCep("-");
        }
        if (endereco.getComplemento() == null) {
            endereco.setComplemento("");
        }
        if (endereco.getLocalidade() == null) {
            endereco.setLocalidade("");
        }
        if (endereco.getNumero() == null) {
            endereco.setNumero("");
        }
        if (endereco.getUf() == null) {
            endereco.setUf("");
        }
        if (endereco.getLogradouro() == null) {
            endereco.setLogradouro("");
        }
    }

    private String gerarTrailerLote(Banco banco, Integer seqLote, Integer qtdRegistros, BigDecimal somaValor) {
        somaValor = somaValor.movePointRight(2).round(MathContext.DECIMAL32);
        String strSomaValor = "" + somaValor.intValue();

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(banco.getNumeroBanco(), 3, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(seqLote.toString(), 4, "0"));
        sb.append("5");
        sb.append(StringUtils.repeat(" ", 9));
        sb.append(StringUtil.cortaOuCompletaEsquerda(qtdRegistros.toString(), 6, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(strSomaValor, 18, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda("0", 18, "0"));
        sb.append(StringUtils.repeat(" ", 6));
        sb.append(StringUtils.repeat(" ", 165));
        sb.append(StringUtils.repeat(" ", 10));
        sb.append("\n");
        return sb.toString();
    }

    public String gerarTrailerArquivo(FebrabanHeaderArquivo headerArquivo, Integer quantidadeLotes, Integer qtdeRegistros) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getBanco().getNumeroBanco(), 3, "0"));
        sb.append("9999");
        sb.append("9");
        sb.append(StringUtils.repeat(" ", 9));
        sb.append(StringUtil.cortaOuCompletaEsquerda(quantidadeLotes.toString(), 6, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(qtdeRegistros.toString(), 6, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda("0", 6, "0"));
        sb.append(StringUtils.repeat(" ", 205));
        sb.append("\n");
        return sb.toString();
    }

    // P001
    // 018 = TED (STR,CIP)
    // 700 = DOC (COMPE)
    private String montarLinhaSegmentoA(FebrabanHeaderArquivo headerArquivo, Integer sequenciaLote, Integer sequenciaLinhas, Pagamento pag) {
        StringBuilder sb = new StringBuilder();
        // Controle - Banco
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getBanco().getNumeroBanco(), 3, "0"));
        // Controle - Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        // Controle - Registro
        sb.append("3");
        // Serviço - Número do Registro
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 5, "0"));
        // Serviço - Segmento
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 1, "0"));
        // Serviço - Movimento - Tipo
        sb.append("0");
        // Serviço - Movimento - Código
        sb.append("00");
        // Favorecido - Câmara
        sb.append("018"); // P001

        Pessoa fornecedor = pessoaFacade.recuperar(pag.getLiquidacao().getEmpenho().getFornecedor().getId());
        List<ContaCorrenteBancPessoa> listaContasFornecedor = pessoaFacade.contasPorPessoa(fornecedor);

        ContaCorrenteBancPessoa contaFav = null;
        if (listaContasFornecedor.size() > 0) {
            contaFav = listaContasFornecedor.get(0);

            if (contaFav != null) {
                // Favorecido - Banco
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
                // Favorecido - Conta Corrente - Agência - Código
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getNumeroAgencia(), 5, "0"));
                // Favorecido - Conta Corrente - Agência - DV
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getDigitoVerificador(), 1, " "));
                // Favorecido - Conta Corrente - Conta - Número
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getNumeroConta(), 12, "0"));
                // Favorecido - Conta Corrente - Conta - DV
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getDigitoVerificador(), 1, " "));
            }
        }

        if (contaFav == null) {
            // Favorecido - Banco
            sb.append("000");
            // Favorecido - Conta Corrente - Agência - Código
            sb.append("00000");
            // Favorecido - Conta Corrente - Agência - DV
            sb.append("0");
            // Favorecido - Conta Corrente - Conta - Número
            sb.append("000000000000");
            // Favorecido - Conta Corrente - Conta - DV
            sb.append("0");
        }
        // Favorecido - Conta Corrente - DV
        sb.append("0");
        // Favorecido - Nome
        sb.append(StringUtil.cortaOuCompletaDireita(fornecedor.getNome(), 30, " "));
        // Crédito - Seu Número
        sb.append(StringUtil.cortaOuCompletaDireita(pag.getId().toString(), 20, " "));
        // Crédito - Data Pagamento
        sb.append(getDataFormatada(pag.getPrevistoPara()));
        // Crédito - Moeda - Tipo
        sb.append("BRL");
        // Crédito - Moeda - Quantidade
        sb.append(StringUtil.cortaOuCompletaEsquerda(100000 + "", 10, "0"));
        // Crédito - Valor Pagamento
        String strValor = "" + pag.getValor().movePointRight(2).round(MathContext.DECIMAL32).intValue();
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 13, "0"));
        // Crédito - Nosso Número
        sb.append(StringUtil.cortaOuCompletaDireita(" ", 20, " "));
        // Crédito - Data Real
        sb.append(getDataFormatada(pag.getPrevistoPara()));
        // Crédito - Valor Real
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 13, "0"));
        // Informação 2
        sb.append(StringUtil.cortaOuCompletaDireita(" ", 40, " "));
        // Código Finalidade Doc
        sb.append("07");
        // Código Finalidade TED
        sb.append(StringUtil.cortaOuCompletaDireita("", 2, " "));
        // Código Finalidade Complementar
        sb.append(StringUtil.cortaOuCompletaDireita("", 3, " "));
        // Aviso
        sb.append("0");
        // Ocorrências
        sb.append(StringUtil.cortaOuCompletaDireita("", 10, " "));
        sb.append("\n");

        return sb.toString();
    }

    private String montarLinhaSegmentoA(FebrabanHeaderArquivo headerArquivo, Integer sequenciaLote, Integer sequenciaLinhas, PagamentoExtra pag) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(headerArquivo.getBanco().getNumeroBanco(), 3, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        sb.append("3");
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 5, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 1, "0"));
        sb.append("0");
        sb.append("00");
        sb.append("018"); // P001

        Pessoa fornecedor = pessoaFacade.recuperar(pag.getFornecedor().getId());
        List<ContaCorrenteBancPessoa> listaContasFornecedor = pessoaFacade.contasPorPessoa(fornecedor);

        ContaCorrenteBancPessoa contaFav = null;
        if (listaContasFornecedor.size() > 0) {
            contaFav = listaContasFornecedor.get(0);

            if (contaFav != null) {
                // Favorecido - Banco
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
                // Favorecido - Conta Corrente - Agência - Código
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getNumeroAgencia(), 5, "0"));
                // Favorecido - Conta Corrente - Agência - DV
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getDigitoVerificador(), 1, " "));
                // Favorecido - Conta Corrente - Conta - Número
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getNumeroConta(), 12, "0"));
                // Favorecido - Conta Corrente - Conta - DV
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getDigitoVerificador(), 1, " "));
            }
        }

        if (contaFav == null) {
            // Favorecido - Banco
            sb.append("000");
            // Favorecido - Conta Corrente - Agência - Código
            sb.append("00000");
            // Favorecido - Conta Corrente - Agência - DV
            sb.append("0");
            // Favorecido - Conta Corrente - Conta - Número
            sb.append("000000000000");
            // Favorecido - Conta Corrente - Conta - DV
            sb.append("0");
        }
        // Favorecido - Conta Corrente - DV
        sb.append("0");
        // Favorecido - Nome
        sb.append(StringUtil.cortaOuCompletaDireita(fornecedor.getNome(), 30, " "));
        // Crédito - Seu Número
        sb.append(StringUtil.cortaOuCompletaDireita(pag.getId().toString(), 20, " "));
        // Crédito - Data Pagamento
        sb.append(getDataFormatada(pag.getPrevistoPara()));
        // Crédito - Moeda - Tipo
        sb.append("BRL");
        // Crédito - Moeda - Quantidade
        sb.append(StringUtil.cortaOuCompletaEsquerda(100000 + "", 10, "0"));
        // Crédito - Valor Pagamento
        String strValor = "" + pag.getValor().movePointRight(2).round(MathContext.DECIMAL32).intValue();
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 13, "0"));
        // Crédito - Nosso Número
        sb.append(StringUtil.cortaOuCompletaDireita(" ", 20, " "));
        // Crédito - Data Real
        sb.append(getDataFormatada(pag.getPrevistoPara()));
        // Crédito - Valor Real
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 13, "0"));
        // Informação 2
        sb.append(StringUtil.cortaOuCompletaDireita(" ", 40, " "));
        // Código Finalidade Doc
        sb.append("07");
        // Código Finalidade TED
        sb.append(StringUtil.cortaOuCompletaDireita("", 2, " "));
        // Código Finalidade Complementar
        sb.append(StringUtil.cortaOuCompletaDireita("", 3, " "));
        // Aviso
        sb.append("0");
        // Ocorrências
        sb.append(StringUtil.cortaOuCompletaDireita("", 10, " "));
        sb.append("\n");

        return sb.toString();
    }

    private String montarLinhaSegmentoB(FebrabanHeaderArquivo headerArquivo, Integer sequenciaLote, Integer sequenciaLinhas, Pagamento pag) {
        StringBuilder sb = new StringBuilder();

        Pessoa fornecedor = pag.getLiquidacao().getEmpenho().getFornecedor();
        fornecedor = pessoaFacade.recuperar(fornecedor.getId());

        List<ContaCorrenteBancPessoa> listaContasFornecedor = pessoaFacade.contasPorPessoa(fornecedor);
        ContaCorrenteBancPessoa contaFav = null;
        if (listaContasFornecedor.size() > 0) {
            contaFav = listaContasFornecedor.get(0);

            if (contaFav != null) {
                //Controle - Banco
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
            }
        }

        if (contaFav == null) {
            //Controle - Banco
            sb.append("000");
        }

        EnderecoCorreio end = null;
        if (!fornecedor.getEnderecos().isEmpty()) {
            end = fornecedor.getEnderecos().get(0);
        }
        if (end == null) {
            end = new EnderecoCorreio();
        }
        validaEndereco(end);

        //Controle - Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        //Controle - Registro
        sb.append("3");
        //Serviço - Número do Registro
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 5, "0"));
        //Serviço - Segmento
        sb.append("B");
        //Serviço - Movimento - Tipo
        sb.append("   ");
        //Favorecido - Inscrição - Tipo de Inscrição do favorecido
        sb.append("1");
        //Favorecido - Inscrição - Número de Inscrição do favorecido
        sb.append(StringUtil.cortaOuCompletaEsquerda(fornecedor.getCpf_Cnpj().replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        //Favorecido - Logradouro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLogradouro(), 30, " "));
        //Favorecido - Numero
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getNumero(), 5, "0"));
        //Favorecido - Complemento
        sb.append(StringUtil.cortaOuCompletaDireita(end.getComplemento(), 15, " "));
        //Favorecido - Bairro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getBairro(), 15, " "));
        //Favorecido - Bairro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLocalidade(), 20, " "));
        //Favorecido - CEP
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getCep().replaceAll("-", ""), 8, "0"));
        //Favorecido - Estado
        sb.append(StringUtil.cortaOuCompletaDireita(end.getUf(), 2, ""));
        //Pagamento - Vencimento
        sb.append(StringUtils.repeat("0", 8));
        //Pagamento - Valor Docum.
        String strValor = "" + pag.getValorFinal().movePointRight(2).round(MathContext.DECIMAL32).intValue();
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 15, "0"));
        //Pagamento - Valor Abatimento.
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor Desconto
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Mora
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Multa
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Cód/Doc. Favorec.
        sb.append(StringUtils.repeat(" ", 15));
        //Aviso
        sb.append(" ");
        //Código UG Centralizadora
        sb.append(StringUtils.repeat("0", 6));
        //CNAB
        sb.append(StringUtils.repeat(" ", 8));
        sb.append("\n");
        return sb.toString();
    }

    private String montarLinhaSegmentoB(FebrabanHeaderArquivo headerArquivo, Integer sequenciaLote, Integer sequenciaLinhas, PagamentoExtra pag) {
        StringBuilder sb = new StringBuilder();

        Pessoa fornecedor = pag.getFornecedor();
        fornecedor = pessoaFacade.recuperar(fornecedor.getId());

        List<ContaCorrenteBancPessoa> listaContasFornecedor = pessoaFacade.contasPorPessoa(fornecedor);
        ContaCorrenteBancPessoa contaFav = null;
        if (listaContasFornecedor.size() > 0) {
            contaFav = listaContasFornecedor.get(0);

            if (contaFav != null) {
                //Controle - Banco
                sb.append(StringUtil.cortaOuCompletaEsquerda(contaFav.getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
            }
        }

        if (contaFav == null) {
            //Controle - Banco
            sb.append("000");
        }

        EnderecoCorreio end = null;
        if (!fornecedor.getEnderecos().isEmpty()) {
            end = fornecedor.getEnderecos().get(0);
        }
        if (end == null) {
            end = new EnderecoCorreio();
        }
        validaEndereco(end);

        //Controle - Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        //Controle - Registro
        sb.append("3");
        //Serviço - Número do Registro
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLinhas.toString(), 5, "0"));
        //Serviço - Segmento
        sb.append("B");
        //Serviço - Movimento - Tipo
        sb.append("   ");
        //Favorecido - Inscrição - Tipo de Inscrição do favorecido
        sb.append("1");
        //Favorecido - Inscrição - Número de Inscrição do favorecido
        sb.append(StringUtil.cortaOuCompletaEsquerda(fornecedor.getCpf_Cnpj().replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        //Favorecido - Logradouro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLogradouro(), 30, " "));
        //Favorecido - Numero
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getNumero(), 5, "0"));
        //Favorecido - Complemento
        sb.append(StringUtil.cortaOuCompletaDireita(end.getComplemento(), 15, " "));
        //Favorecido - Bairro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getBairro(), 15, " "));
        //Favorecido - Bairro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLocalidade(), 20, " "));
        //Favorecido - CEP
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getCep().replaceAll("-", ""), 8, "0"));
        //Favorecido - Estado
        sb.append(StringUtil.cortaOuCompletaDireita(end.getUf(), 2, ""));
        //Pagamento - Vencimento
        sb.append(StringUtils.repeat("0", 8));
        //Pagamento - Valor Docum.
        String strValor = "" + pag.getValor().movePointRight(2).round(MathContext.DECIMAL32).intValue();
        sb.append(StringUtil.cortaOuCompletaEsquerda(strValor, 15, "0"));
        //Pagamento - Valor Abatimento.
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor Desconto
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Mora
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Valor da Multa
        sb.append(StringUtils.repeat("0", 15));
        //Pagamento - Cód/Doc. Favorec.
        sb.append(StringUtils.repeat(" ", 15));
        //Aviso
        sb.append(" ");
        //Código UG Centralizadora
        sb.append(StringUtils.repeat("0", 6));
        //CNAB
        sb.append(StringUtils.repeat(" ", 8));
        sb.append("\n");
        return sb.toString();
    }

    public BigDecimal retornaValorTotalPagamento() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!this.selecionado.getListaPagamentos().isEmpty() || this.selecionado.getListaPagamentos() != null) {
            for (BorderoPagamento pag : this.selecionado.getListaPagamentos()) {
                soma = soma.add(pag.getPagamento().getValorFinal());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalPagamentoExtra() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!this.selecionado.getListaPagamentosExtra().isEmpty() || this.selecionado.getListaPagamentosExtra() != null) {
            for (BorderoPagamentoExtra pag : this.selecionado.getListaPagamentosExtra()) {
                soma = soma.add(pag.getPagamentoExtra().getValor());
            }
        }
        return soma;
    }

    public String getDataFormatada(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        return sdf.format(data);
    }

    public void importarArquivo() {
    }

    public void sobeArquivos(FileUploadEvent event) throws FileNotFoundException, IOException {
        if (fileUploadEvents == null) {
            fileUploadEvents = new ArrayList<FileUploadEvent>();
        }
        fileUploadEvents.add(event);
        ////System.out.println("subiu! -----------> " + event.getFile().getFileName());
    }

    public void geraArquivos() {
        try {
            borderoFacade.geraArquivos(fileUploadEvents);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Arquivos importados com sucesso!", ""));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar: ", ex.getMessage()));
            logger.error("{}", ex);
        }
        fileUploadEvents = new ArrayList<>();
    }

    public void gerarRelatorio() throws JRException, IOException {

        Long vias = 1L;
        String arquivoJasper = "RelatorioBorderoPrincipal.jasper";
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "Rio Branco");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("BORDERO", selecionado.getId());
        parameters.put("VIAS", vias);
        gerarRelatorio(arquivoJasper, parameters);
    }
}
