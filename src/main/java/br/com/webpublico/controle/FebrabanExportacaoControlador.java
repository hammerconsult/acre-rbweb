/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.FebrabanFacade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoFebraban", pattern = "/credito-de-salario-febraban/gerar/", viewId = "/faces/rh/administracaodepagamento/febrabanexportacao/edita.xhtml")
})
public class FebrabanExportacaoControlador implements Serializable {

    @EJB
    private FebrabanFacade febrabanFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquia;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    private FolhaDePagamento folhaPagamento;
    @EJB
    private FolhaDePagamentoFacade folhaPagamentoFacade;
    private ConverterGenerico converterConta;
    private ConverterGenerico converterFolha;
    private ConverterAutoComplete converterContratoFP;
    private ContaBancariaEntidade contaBancariaEntidade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private Date dataPagamento;
    private StreamedContent fileDownload;
    private File arquivo;
    private ContratoFP contrato;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public FebrabanExportacaoControlador() {
    }

    @URLAction(mappingId = "novoFebraban", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        hierarquiaOrganizacional = null;
        contaBancariaEntidade = null;
        dataPagamento = new Date();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalEntidadeTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (hierarquiaOrganizacional != null) {
            List<ContaBancariaEntidade> listaContas = contaBancariaEntidadeFacade.listaContasPorEntidade(hierarquiaOrganizacional.getSubordinada().getEntidade());
            if (!listaContas.isEmpty()) {
                contaBancariaEntidade = listaContas.get(0);
                for (ContaBancariaEntidade conta : contaBancariaEntidadeFacade.listaContasPorEntidade(hierarquiaOrganizacional.getSubordinada().getEntidade())) {
                    toReturn.add(new SelectItem(conta, conta.toString()));
                }
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterGenerico(ContaBancariaEntidade.class, contaBancariaEntidadeFacade);
        }
        return converterConta;
    }

    public List<SelectItem> getFolhas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        List<FolhaDePagamento> listaFolhas = folhaPagamentoFacade.lista();
        if (!listaFolhas.isEmpty()) {
            folhaPagamento = listaFolhas.get(0);
            for (FolhaDePagamento folha : listaFolhas) {
                toReturn.add(new SelectItem(folha, folha.toString()));
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterFolha() {
        if (converterFolha == null) {
            converterFolha = new ConverterGenerico(FolhaDePagamento.class, contaBancariaEntidadeFacade);
        }
        return converterFolha;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public FolhaDePagamento getFolhaPagamento() {
        return folhaPagamento;
    }

    public void setFolhaPagamento(FolhaDePagamento folhaPagamento) {
        this.folhaPagamento = folhaPagamento;
    }
//, List<FebrabanDetalheSegmentoA> detalhes

    public StringBuilder gerarArquivos(FebrabanHeaderArquivo headerArquivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        headerArquivo.setSequencia(1);
        sb.append(headerArquivo.getHeaderArquivo().toUpperCase());

        Integer seqLote = 0;
        ModalidadeConta modalidade = ModalidadeConta.CONTA_CORRENTE;
        Integer i = 0;
        Double valorTotalLote = 0.0;
        Integer totalRegistros = 2;

        /**
         * Codigo para o mesmo banco conta corrente ver valor liquido 01
         */
        List<FebrabanDetalheSegmentoA> listaContaCorrente = febrabanFacade.recuperaDetalheSegmentoAContaCorrente(hierarquiaOrganizacional, folhaPagamento, ModalidadeConta.CONTA_CORRENTE, headerArquivo.getBanco(), contrato);
        if (!listaContaCorrente.isEmpty()) {

            sb.append(gerarHeaderLote(headerArquivo, "01", ++seqLote).toUpperCase());

            for (FebrabanDetalheSegmentoA detalheSegmentoContaCorrente : listaContaCorrente) {
                //conferir operador i
                BigDecimal valorLiquido = fichaFinanceiraFPFacade.recuperaValorLiquidoFichaFinanceiraFP(detalheSegmentoContaCorrente.getFicha());
                valorTotalLote += valorLiquido.doubleValue();
                sb.append(StringUtil.removeCaracteresEspeciais(detalheSegmentoContaCorrente.gerarDetalheA(headerArquivo, valorLiquido.doubleValue(), dataPagamento, seqLote, ++i, folhaPagamento)));
                totalRegistros++;
                sb.append(StringUtil.removeCaracteresEspeciais(gerarDetalheB(headerArquivo, seqLote, i, detalheSegmentoContaCorrente.getContrato())));
                totalRegistros++;
            }
            sb.append(gerarTrailerLote(headerArquivo.getBanco(), seqLote, listaContaCorrente.size(), valorTotalLote).toUpperCase());
            totalRegistros += 2;
        }
        /**
         * Codigo para o mesmo banco conta poupança ver valor liquido 05
         */
        i = 0;
        valorTotalLote = 0.0;
        List<FebrabanDetalheSegmentoA> listaContaPoupanca = febrabanFacade.recuperaDetalheSegmentoAContaCorrente(hierarquiaOrganizacional, folhaPagamento, ModalidadeConta.CONTA_POUPANCA, headerArquivo.getBanco(), contrato);
        if (!listaContaPoupanca.isEmpty()) {
            sb.append(gerarHeaderLote(headerArquivo, "05", ++seqLote).toUpperCase());

            for (FebrabanDetalheSegmentoA detalheSegmentoPoupanca : listaContaPoupanca) {
                //conferir operador i
                BigDecimal valorLiquido = fichaFinanceiraFPFacade.recuperaValorLiquidoFichaFinanceiraFP(detalheSegmentoPoupanca.getFicha());
                valorTotalLote += valorLiquido.doubleValue();
                sb.append(StringUtil.removeCaracteresEspeciais(detalheSegmentoPoupanca.gerarDetalheA(headerArquivo, valorLiquido.doubleValue(), dataPagamento, seqLote, ++i, folhaPagamento)));
                totalRegistros++;
                sb.append(StringUtil.removeCaracteresEspeciais(gerarDetalheB(headerArquivo, seqLote, i, detalheSegmentoPoupanca.getContrato())));
                totalRegistros++;
            }
            totalRegistros += 2;
            sb.append(gerarTrailerLote(headerArquivo.getBanco(), seqLote, listaContaPoupanca.size(), valorTotalLote).toUpperCase());
        }
        /**
         * Codigo para outros bancos ver valor liquido 03
         */
        i = 0;
        valorTotalLote = 0.0;
        List<FebrabanDetalheSegmentoA> listaContaOutrosBancos = febrabanFacade.recuperaDetalheSegmentoOutrosBanco(hierarquiaOrganizacional, folhaPagamento, headerArquivo.getBanco(), contrato);
        if (!listaContaOutrosBancos.isEmpty()) {
            sb.append(gerarHeaderLote(headerArquivo, "03", ++seqLote).toUpperCase());

            for (FebrabanDetalheSegmentoA detalheSegmentoOutrosBancos : listaContaOutrosBancos) {
                BigDecimal valorLiquido = fichaFinanceiraFPFacade.recuperaValorLiquidoFichaFinanceiraFP(detalheSegmentoOutrosBancos.getFicha());
                valorTotalLote += valorLiquido.doubleValue();
                sb.append(StringUtil.removeCaracteresEspeciais(detalheSegmentoOutrosBancos.gerarDetalheA(headerArquivo, valorLiquido.doubleValue(), dataPagamento, seqLote, ++i, folhaPagamento)));
                totalRegistros++;
                sb.append(StringUtil.removeCaracteresEspeciais(gerarDetalheB(headerArquivo, seqLote, i, detalheSegmentoOutrosBancos.getContrato())));
                totalRegistros++;
            }
            totalRegistros += 2;
            sb.append(gerarTrailerLote(headerArquivo.getBanco(), seqLote, listaContaOutrosBancos.size(), valorTotalLote).toUpperCase());

        }
        sb.append(gerarTrailerArquivo(headerArquivo, seqLote, totalRegistros).toUpperCase());
        return sb;
    }

    //    private void gerarCabecalho(FebrabanHeaderArquivo header) {
//        header.setSequencia(1);
//        //System.out.println(header.getHeaderArquivo());
//    }
//    private void gerarDetalheSegmentoA(FebrabanHeaderArquivo header, FebrabanDetalheSegmentoA segmentoA, String numeroRegistro, Double valorLiquido, Date dataPagamento) {
//        segmentoA.setNumeroRegistro(numeroRegistro);
//        //System.out.println(segmentoA.gerarDetalheA(header, valorLiquido, dataPagamento));
//    }
    public String gerarHeaderLote(FebrabanHeaderArquivo headerArquivo, String formaLancamento, Integer sequenciaLote) {
        StringBuilder sb = new StringBuilder();
        EnderecoCorreio enderecoCorreio = null;
        PessoaJuridica pj = pessoaJuridicaFacade.recuperar(headerArquivo.getPessoaJuridica().getId());

        for (EnderecoCorreio end : pj.getEnderecos()) {
            enderecoCorreio = end;
        }

        if (enderecoCorreio == null) {
            enderecoCorreio = new EnderecoCorreio();
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
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getAgencia().getDigitoVerificador() != null ? headerArquivo.getAgencia().getDigitoVerificador() : " ", 1, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
        sb.append(" ");
        sb.append(StringUtil.cortaOuCompletaDireita(headerArquivo.getPessoaJuridica().getRazaoSocial(), 30, " "));

        if (formaLancamento == "01") {
            sb.append(StringUtil.cortaOuCompletaDireita("CREDITO EM CONTA CORRENTE", 40, " "));
        } else if (formaLancamento == "03") {
            sb.append(StringUtil.cortaOuCompletaDireita("DOC/TED", 40, " "));
        } else if (formaLancamento == "05") {
            sb.append(StringUtil.cortaOuCompletaDireita("DEPOSITO EM POUPANCA", 40, " "));
        } else {
            sb.append(StringUtils.leftPad(" ", 40));
        }

        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro(), 30, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getNumero(), 5, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento(), 15, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade(), 20, " "));
        sb.append(StringUtil.cortaOuCompletaEsquerda(enderecoCorreio.getCep().replaceAll("-", ""), 8, " "));
        sb.append(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getUf(), 2, " "));
        sb.append(StringUtils.repeat(" ", 8));
        sb.append(StringUtils.repeat(" ", 10));
        sb.append("\n");
        return sb.toString();
    }

    private String gerarTrailerLote(Banco banco, Integer seqLote, Integer qtdRegistros, Double somaValor) {
        String strSomaValor = Double.toString(Double.valueOf(somaValor * 100).intValue()).replaceAll(".", "");
//        String strSomaMoeda = Double.toString(Double.valueOf(somaMoeda * 100000).intValue()).replaceAll(".", "");;

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(banco.getNumeroBanco(), 3, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(seqLote.toString(), 4, "0"));
        sb.append("5");
        sb.append(StringUtils.repeat(" ", 9));
        sb.append(StringUtil.cortaOuCompletaEsquerda(qtdRegistros.toString(), 6, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda(strSomaValor, 16, "0"));
        sb.append(StringUtil.cortaOuCompletaEsquerda("0", 13, "0"));
        sb.append(StringUtils.repeat("0", 6));
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

    public String gerarDetalheB(FebrabanHeaderArquivo headerArquivo, Integer sequenciaLote, Integer sequencialRegistro, ContratoFP contrato) {
        StringBuilder sb = new StringBuilder();
        PessoaFisica p = contrato.getMatriculaFP().getPessoa();
        p = pessoaFisicaFacade.recuperar(p.getId());
        EnderecoCorreio end = null;
        if (!p.getEnderecos().isEmpty()) {
            end = p.getEnderecos().get(0);
        }

        if (end == null) {
            end = new EnderecoCorreio();
        }

        validaEndereco(end);

        //Controle - Banco
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        //Controle - Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        //Controle - Registro
        sb.append("3");
        //Serviço - Nº Seqüencial do Registro no Lote
        sb.append(StringUtil.cortaOuCompletaEsquerda(sequencialRegistro.toString(), 5, "0"));
        //Serviço - Segmento
        sb.append("B");
        //CNAB - FEBRABAN/CNAB
        sb.append(StringUtil.cortaOuCompletaEsquerda(" ", 3, " "));
        //Favorecido - Inscrição - Tipo de Inscrição do favorecido
        sb.append("1");
        //Favorecido - Inscrição - Número de Inscrição do favorecido
        sb.append(StringUtil.cortaOuCompletaEsquerda(contrato.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));
        //Favorecido - Logradouro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLogradouro(), 30, " "));
        //Favorecido - Numero
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getNumero(), 5, "0"));
        //Favorecido - Complemento
        sb.append(StringUtil.cortaOuCompletaDireita(end.getComplemento(), 15, " "));
        //Favorecido - Bairro
        sb.append(StringUtil.cortaOuCompletaDireita(end.getBairro(), 15, " "));
        //Favorecido - Cidade
        sb.append(StringUtil.cortaOuCompletaDireita(end.getLocalidade(), 20, " "));
        //Favorecido - CEP
        sb.append(StringUtil.cortaOuCompletaEsquerda(end.getCep().replaceAll("-", ""), 8, "0"));
//        //Favorecido - Complemento do CEP
//        sb.append(StringUtil.cortaOuCompletaEsquerda("0", 3, " "));
        //Favorecido - Estado
        sb.append(StringUtil.cortaOuCompletaDireita(end.getUf(), 2, ""));
        //Pagamento - Vencimento
        sb.append(StringUtils.repeat("0", 8));
        //Pagamento - Valor Docum.
        sb.append(StringUtils.repeat("0", 13));
        //Pagamento - Valor Abatimento.
        sb.append(StringUtils.repeat("0", 13));
        //Pagamento - Valor Desconto
        sb.append(StringUtils.repeat("0", 13));
        //Pagamento - Valor da Mora
        sb.append(StringUtils.repeat("0", 13));
        //Pagamento - Valor da Multa
        sb.append(StringUtils.repeat("0", 13));
        //Pagamento - Cód/Doc. Favorec.
        sb.append(StringUtils.repeat(" ", 15));
        //Aviso
        sb.append("0");
        //Código UG Centralizadora
        sb.append(StringUtils.repeat("0", 6));
        //CNAB
        sb.append(StringUtils.repeat(" ", 8));
        sb.append("\n");

        return sb.toString();
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public StreamedContent getFileDownload() throws FileNotFoundException, IOException {
        if (validaCampos()) {
            FebrabanHeaderArquivo fha = febrabanFacade.recuperaFebrabanHeaderArquivo(hierarquiaOrganizacional.getSubordinada(), contaBancariaEntidade);

            StringBuilder sb = gerarArquivos(fha);

            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
            InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
            fileDownload = new DefaultStreamedContent(is, "text", "Credito_Salario" + sf.format(new Date()) + ".txt");

            FacesContext.getCurrentInstance().addMessage("Formulario:msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Arquivo gerado com sucesso !", ""));
            novo();
        }
        return fileDownload;
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (hierarquiaOrganizacional == null) {
            FacesUtil.addWarn("Atenção!", "Por favor informe a Unidade Organizacional.");
            retorno = false;
        }
        if (contaBancariaEntidade == null) {
            FacesUtil.addWarn("Atenção!", "Por favor informe a Conta Bancária.");
            retorno = false;
        }
        if (folhaPagamento == null) {
            FacesUtil.addWarn("Atenção!", "Por favor informe a Folha de Pagamento.");
            retorno = false;
        }
        if (dataPagamento == null) {
            FacesUtil.addWarn("Atenção!", "Por favor informe a Data de Pagamento.");
            retorno = false;
        }
        return retorno;
    }

    public ContratoFP getContrato() {
        return contrato;
    }

    public void setContrato(ContratoFP contrato) {
        this.contrato = contrato;
    }

    public List<ContratoFP> completarContratoParaGeracaoDeArquivoCreditoSalario(String parte) {
        try {
            if (validaCampos()) {
                return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaParaCreditoDeSalario(parte, hierarquiaOrganizacional, dataPagamento, folhaPagamento);
            }
            return new ArrayList<ContratoFP>();
        } catch (Exception e) {
            return new ArrayList<ContratoFP>();
        }
    }
}
