/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RegistrosArquivoRetorno;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author boy(JGordaum)
 */
@ManagedBean(name = "importacaoArquivoRetornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRetornoCreditoSalario", pattern = "/arquivo-retorno-bb/novo/", viewId = "/faces/rh/administracaodepagamento/importararquivoretornobb/edita.xhtml"),
    @URLMapping(id = "listarArquivoRetornoBB", pattern = "/arquivo-retorno-bb/listar/", viewId = "/faces/rh/administracaodepagamento/importararquivoretornobb/lista.xhtml"),
    @URLMapping(id = "verArquivoRetornoBB", pattern = "/arquivo-retorno-bb/ver/#{importacaoArquivoRetornoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/importararquivoretornobb/visualizar.xhtml")
})
public class ImportacaoArquivoRetornoControlador extends PrettyControlador<ArquivoRetornoBB> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoArquivoRetornoControlador.class);

    private UploadedFile file;
    private List<RegistrosArquivoRetorno> registrosArquivoRetornos = new ArrayList<>();
    private RegistrosArquivoRetorno registrosArquivoRetorno;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ArquivoRetornoBBFacade arquivoRetornoBBFacade;
    private String arquivoSelecionado;
    private Arquivo arquivo;
    private FileUploadEvent fileUploadEvent;
    @EJB
    private ArquivoFacade arquivoFacade;

    public ImportacaoArquivoRetornoControlador() {
        super(ArquivoRetornoBB.class);
        registrosArquivoRetornos = new ArrayList<>();
    }

    public RegistrosArquivoRetorno getRegistrosArquivoRetorno() {
        return registrosArquivoRetorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoRetornoBBFacade;
    }

    public void setRegistrosArquivoRetorno(RegistrosArquivoRetorno registrosArquivoRetorno) {
        this.registrosArquivoRetorno = registrosArquivoRetorno;
    }

    public List<RegistrosArquivoRetorno> getRegistrosArquivoRetornos() {
        return registrosArquivoRetornos;
    }

    public void setRegistrosArquivoRetornos(List<RegistrosArquivoRetorno> registrosArquivoRetornos) {
        this.registrosArquivoRetornos = registrosArquivoRetornos;
    }

    public UploadedFile getFile() {
        return file;
    }


    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getArquivoSelecionado() {
        return arquivoSelecionado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-retorno-bb/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado;
    }

    @URLAction(mappingId = "novoCreditoSalario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        file = null;
        registrosArquivoRetornos = new ArrayList<RegistrosArquivoRetorno>();
        arquivoSelecionado = "";
        arquivo = new Arquivo();
        selecionado = new ArquivoRetornoBB();
    }

    @URLAction(mappingId = "verArquivoRetornoBB", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    private void selecionar() {
        try {
            selecionado.setArquivo(arquivoFacade.recuperaDependencias(selecionado.getArquivo().getId()));
        } catch (Exception e) {
        }
    }

    public List<String> interpretaErros(String erro) {
        List<String> toReturn = new ArrayList<>();

        if (!erro.substring(0, 2).equals("  ")) {
            toReturn.add(retornaErro(erro.substring(0, 2)));
        }
        if (!erro.substring(2, 4).equals("  ")) {
            toReturn.add(retornaErro(erro.substring(2, 4)));
        }
        if (!erro.substring(4, 6).equals("  ")) {
            toReturn.add(retornaErro(erro.substring(4, 6)));
        }
        if (!erro.substring(6, 8).equals("  ")) {
            toReturn.add(retornaErro(erro.substring(6, 8)));
        }
        if (!erro.substring(8, 10).equals("  ")) {
            toReturn.add(retornaErro(erro.substring(8, 10)));
        }
        return toReturn;
    }

    public String retornaErro(String cod) {

        if (cod.equals("00")) {
            return ("Crédito ou Débito Efetivado");
        }
        if (cod.equals("01")) {
            return ("Insuficiência de Fundos");
        }
        if (cod.equals("02")) {
            return ("Crédito ou Débito Cancelado pelo Pagador/Credor");
        }
        if (cod.equals("03")) {
            return ("Débito Autorizado pela Agência - Efetuado");
        }
        if (cod.equals("AA")) {
            return ("Controle Inválido");
        }
        if (cod.equals("AB")) {
            return ("Tipo de Operação Inválido");
        }
        if (cod.equals("AC")) {
            return ("Tipo de Serviço Inválido");
        }
        if (cod.equals("AD")) {
            return ("Forma de Lançamento Inválida");
        }
        if (cod.equals("AE")) {
            return ("Tipo/Número de Inscrição Inválido");
        }
        if (cod.equals("AF")) {
            return ("Código de Convênio Inválido");
        }
        if (cod.equals("AG")) {
            return ("Agência/Conta Corrente/DV Inválido");
        }
        if (cod.equals("AH")) {
            return ("No Seqüencial do Registro no Lote Inválido");
        }
        if (cod.equals("AI")) {
            return ("Código de Segmento de Detalhe Inválido");
        }
        if (cod.equals("AJ")) {
            return ("Tipo de Movimento Inválido");
        }
        if (cod.equals("AK")) {
            return ("Código da Câmara de Compensação do Banco Favorecido/Depositário Inválido");
        }
        if (cod.equals("AL")) {
            return ("Agência Mantenedora da Conta Corrente do Favorecido Inválida");
        }
        if (cod.equals("AM")) {
            return ("Código do Banco Favorecido ou Depositário Inválido");
        }
        if (cod.equals("AN")) {
            return ("Conta Corrente/DV do Favorecido Inválido");
        }
        if (cod.equals("AO")) {
            return ("Nome do Favorecido Não Informado");
        }
        if (cod.equals("AP")) {
            return ("Data Lançamento Inválido");
        }
        if (cod.equals("AQ")) {
            return ("Tipo/Quantidade da Moeda Inválido");
        }
        if (cod.equals("AS")) {
            return ("Aviso ao Favorecido - Identificação Inválida");
        }
        if (cod.equals("AT")) {
            return ("Tipo/Número de Inscrição do Favorecido Inválido");
        }
        if (cod.equals("AU")) {
            return ("Logradouro do Favorecido Não Informado");
        }
        if (cod.equals("AV")) {
            return ("No do Local do Favorecido Não Informado");
        }
        if (cod.equals("AW")) {
            return ("Cidade do Favorecido Não Informada");
        }
        if (cod.equals("AX")) {
            return ("CEP/Complemento do Favorecido Inválido");
        }
        if (cod.equals("AY")) {
            return ("Sigla do Estado do Favorecido Inválida");
        }
        if (cod.equals("AZ")) {
            return ("Código/Nome do Banco Depositário Inválido");
        }
        if (cod.equals("BA")) {
            return ("Código/Nome da Agência Depositária Não Informado");
        }
        if (cod.equals("BB")) {
            return ("Seu Número Inválido");
        }
        if (cod.equals("BC")) {
            return ("Nosso Número Inválido");
        }
        if (cod.equals("BD")) {
            return ("Inclusão Efetuada com Sucesso");
        }
        if (cod.equals("BE")) {
            return ("Alteração Efetuada com Sucesso");
        }
        if (cod.equals("BF")) {
            return ("Exclusão Efetuada com Sucesso");
        }
        if (cod.equals("BG")) {
            return ("Agência/Conta Impedida Legalmente");
        }
        if (cod.equals("BH")) {
            return ("Empresa não pagou salário");
        }
        if (cod.equals("BI")) {
            return ("Falecimento do mutuário");
        }
        if (cod.equals("BJ")) {
            return ("Empresa não enviou remessa do mutuário");
        }
        if (cod.equals("BK")) {
            return ("Empresa não enviou remessa no vencimento");
        }
        if (cod.equals("BL")) {
            return ("Valor da parcela inválida");
        }
        if (cod.equals("BM")) {
            return ("Identificação do contrato inválida");
        }
        if (cod.equals("BN")) {
            return ("Operação de Consignação Incluída com Sucesso");
        }
        if (cod.equals("BO")) {
            return ("Operação de Consignação Alterada com Sucesso");
        }
        if (cod.equals("BP")) {
            return ("Operação de Consignação Excluída com Sucesso");
        }
        if (cod.equals("BQ")) {
            return ("Operação de Consignação Liquidada com Sucesso");
        }
        if (cod.equals("CA")) {
            return ("Código de Barras - Código do Banco Inválido");
        }
        if (cod.equals("CB")) {
            return ("Código de Barras - Código da Moeda Inválido");
        }
        if (cod.equals("CC")) {
            return ("Código de Barras - Dígito Verificador Geral Inválido");
        }
        if (cod.equals("CD")) {
            return ("Código de Barras - Valor do Título Inválido");
        }
        if (cod.equals("CE")) {
            return ("Código de Barras - Campo Livre Inválido");
        }
        if (cod.equals("CF")) {
            return ("Valor do Documento Inválido");
        }
        if (cod.equals("CG")) {
            return ("Valor do Abatimento Inválido");
        }
        if (cod.equals("CH")) {
            return ("Valor do Desconto Inválido");
        }
        if (cod.equals("CI")) {
            return ("Valor de Mora Inválido");
        }
        if (cod.equals("CJ")) {
            return ("Valor da Multa Inválido");
        }
        if (cod.equals("CK")) {
            return ("Valor do IR Inválido");
        }
        if (cod.equals("CL")) {
            return ("Valor do ISS Inválido");
        }
        if (cod.equals("CM")) {
            return ("Valor do IOF Inválido");
        }
        if (cod.equals("CN")) {
            return ("Valor de Outras Deduções Inválido");
        }
        if (cod.equals("CO")) {
            return ("Valor de Outros Acréscimos Inválido");
        }
        if (cod.equals("CP")) {
            return ("Valor do INSS Inválido");
        }
        if (cod.equals("HA")) {
            return ("Lote Não Aceito");
        }
        if (cod.equals("HB")) {
            return ("Inscrição da Empresa Inválida para o Contrato");
        }
        if (cod.equals("HC")) {
            return ("Convênio com a Empresa Inexistente/Inválido para o Contrato");
        }
        if (cod.equals("HD")) {
            return ("Agência/Conta Corrente da Empresa Inexistente/Inválido para o Contrato");
        }
        if (cod.equals("HE")) {
            return ("Tipo de Serviço Inválido para o Contrato");
        }
        if (cod.equals("HF")) {
            return ("Conta Corrente da Empresa com Saldo Insuficiente");
        }
        if (cod.equals("HG")) {
            return ("Lote de Serviço Fora de Seqüência");
        }
        if (cod.equals("HH")) {
            return ("Lote de Serviço Inválido");
        }
        if (cod.equals("HI")) {
            return ("Arquivo não aceito");
        }
        if (cod.equals("HJ")) {
            return ("Tipo de Registro Inválido");
        }
        if (cod.equals("HK")) {
            return ("Código Remessa / Retorno Inválido");
        }
        if (cod.equals("HL")) {
            return ("Código Remessa / Retorno Inválido");
        }
        if (cod.equals("HM")) {
            return ("Mutuário não identificado");
        }
        if (cod.equals("HN")) {
            return ("Tipo do beneficio não permite empréstimo");
        }
        if (cod.equals("HO")) {
            return ("Beneficio cessado/suspenso");
        }
        if (cod.equals("HP")) {
            return ("Beneficio possui representante legal");
        }
        if (cod.equals("HQ")) {
            return ("Beneficio é do tipo PA (Pensão alimentícia)");
        }
        if (cod.equals("HR")) {
            return ("Quantidade de contratos permitida excedida");
        }
        if (cod.equals("HS")) {
            return ("Beneficio não pertence ao Banco informado");
        }
        if (cod.equals("HT")) {
            return ("Início do desconto informado já ultrapassado");
        }
        if (cod.equals("HU")) {
            return ("Número da parcela inválida");
        }
        if (cod.equals("HV")) {
            return ("Quantidade de parcela inválida");
        }
        if (cod.equals("HW")) {
            return ("Margem consignável excedida para o mutuário dentro do prazo do contrato");
        }
        if (cod.equals("HX")) {
            return ("Empréstimo já cadastrado");
        }
        if (cod.equals("HY")) {
            return ("Empréstimo inexistente");
        }
        if (cod.equals("HZ")) {
            return ("Empréstimo já encerrado");
        }
        if (cod.equals("H1")) {
            return ("Arquivo sem trailer");
        }
        if (cod.equals("H2")) {
            return ("Mutuário sem crédito na competência");
        }
        if (cod.equals("H3")) {
            return ("Não descontado – outros motivos");
        }
        if (cod.equals("H4")) {
            return ("Retorno de Crédito não pago");
        }
        if (cod.equals("H5")) {
            return ("Cancelamento de empréstimo retroativo");
        }
        if (cod.equals("H6")) {
            return ("Outros Motivos de Glosa");
        }
        if (cod.equals("H7")) {
            return ("Margem consignável excedida para o mutuário acima do prazo do contrato");
        }
        if (cod.equals("H8")) {
            return ("Mutuário desligado do empregador");
        }
        if (cod.equals("H9")) {
            return ("Mutuário afastado por licença");
        }
        if (cod.equals("IA")) {
            return ("Primeiro nome do mutuário diferente do primeiro nome do "
                + "movimento do censo ou diferente da base de Titular do Benefício");
        }
        if (cod.equals("TA")) {
            return ("Lote Não Aceito - Totais do Lote com Diferença");
        }
        if (cod.equals("YA")) {
            return ("Título Não Encontrado");
        }
        if (cod.equals("YB")) {
            return ("Identificador Registro Opcional Inválido");
        }
        if (cod.equals("YC")) {
            return ("Código Padrão Inválido");
        }
        if (cod.equals("YD")) {
            return ("Código de Ocorrência Inválido");
        }
        if (cod.equals("YE")) {
            return ("Complemento de Ocorrência Inválido");
        }
        return null;
    }

    public void limpa() {
        file = null;
        registrosArquivoRetornos = new ArrayList<>();
        registrosArquivoRetorno = new RegistrosArquivoRetorno();
    }

    public void importaArquivo(FileUploadEvent event) throws IOException, FileNotFoundException {
        fileUploadEvent = event;
        file = event.getFile();
        arquivoSelecionado = file.getFileName();

    }

    public void gerar() throws IOException, FileNotFoundException {
        if (file != null) {

            InputStream stream = file.getInputstream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            registrosArquivoRetornos = new ArrayList<RegistrosArquivoRetorno>();
            String linha = null;

            while ((linha = reader.readLine()) != null) {
                String erro = "";
                if (linha.substring(7, 8).equals("1") || linha.substring(7, 8).equals("3")) {

                    if (!linha.substring(230, 240).trim().equals("")) {
                        registrosArquivoRetorno = new RegistrosArquivoRetorno();
                        registrosArquivoRetorno.setRegistro("");
                        registrosArquivoRetorno.setErros(new ArrayList<String>());
                        if (linha.substring(7, 8).equals("3") && linha.substring(13, 14).equals("A")) {
                            Integer matricula = Integer.parseInt(linha.substring(73, 80));
                            Integer numeroContrato = Integer.parseInt(linha.substring(82, 93).trim());

                            VinculoFP vinculoFP = contratoFPFacade.recuperarContratoMatricula(String.valueOf(matricula), String.valueOf(numeroContrato));
                            if (vinculoFP != null && vinculoFP.getId() != null) {
                                FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(vinculoFP);

                                if (ficha != null && ficha.getId() != null) {
                                    registrosArquivoRetorno.setFichaFinanceira(ficha);
                                    registrosArquivoRetorno.setRegistro(ficha.getVinculoFP().toString());
                                } else {
                                    registrosArquivoRetorno.setRegistro(vinculoFP + " - Ficha Financeira não encontrada");
                                }
                            } else {
                                registrosArquivoRetorno.setRegistro("Não encontrou o contrato para a matrícula " + matricula + "/" + numeroContrato);
                            }
                        } else if (linha.substring(7, 8).equals("1")) {
                            PessoaJuridica pj = new PessoaJuridica();
                            pj = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(linha.substring(18, 32));
                            if (pj != null) {
                                registrosArquivoRetorno.setRegistro(pj.toString());
                            } else {
                                registrosArquivoRetorno.setRegistro("Registro não encontrado");
                            }
                        }

                        erro = linha.substring(230, 240);
                        if (registrosArquivoRetorno.getRegistro().equals("")) {
                            continue;
                        }
                        registrosArquivoRetorno.getErros().addAll(interpretaErros(erro));
                        if (!registrosArquivoRetorno.getErros().isEmpty()) {
                            registrosArquivoRetornos.add(registrosArquivoRetorno);
                            registrosArquivoRetorno = new RegistrosArquivoRetorno();
                        }
                    }
                }
            }
            if (registrosArquivoRetornos.isEmpty()) {
                FacesUtil.addWarn("Atenção", "Nenhum erro encontrado");
            }
        } else {
            FacesUtil.addWarn("Atençao", "Selecione o arquivo e clique em Upload.");
        }
    }

    @Override
    public void salvar() {
        if (registrosArquivoRetornos != null && !registrosArquivoRetornos.isEmpty()) {
            boolean salvo = true;
            for (RegistrosArquivoRetorno erro : registrosArquivoRetornos) {
                if (erro.getErros().contains("Crédito ou Débito Efetivado")) {
                    FichaFinanceiraFP ficha = erro.getFichaFinanceira();
                    if (ficha != null) {
                        ficha.setCreditoSalarioPago(Boolean.TRUE);
                        fichaFinanceiraFPFacade.salvar(ficha);
                    } else {
                        salvo = false;
                    }
                }
            }
            if (salvo) {
                FacesUtil.addInfo("Salvo com sucesso!", "");
            } else {
                FacesUtil.addInfo("Salvo com sucesso!", "Porém houve fichas financeiras que não foram encontradas.");
            }
        } else {
            FacesUtil.addWarn("Atenção!", "Não existe registro a ser gravado.");
        }
    }

    public String retornaCor(RegistrosArquivoRetorno erro) {
        if (erro.getErros().contains("Crédito ou Débito Efetivado")) {
            return "color:#3c8d3a";
        } else {
            return "color:red";
        }
    }

    public void salvarArquivo() {
        if (fileUploadEvent != null) {
            arquivo = new Arquivo();
            arquivo.setNome(arquivoSelecionado);
            arquivo.setDescricao(arquivoSelecionado);
            arquivo.setTamanho(file.getSize());
            arquivo.setMimeType(file.getContentType());

            selecionado = new ArquivoRetornoBB();
            selecionado.setNomeArquivo(arquivoSelecionado);
            selecionado.setDataRegistro(new Date());

            arquivoRetornoBBFacade.salvarNovo(selecionado, arquivo, fileUploadEvent);
            FacesUtil.addInfo("Arquivo salvo com sucesso!", "");
        } else {
            FacesUtil.addWarn("Atenção!", "Selecione o arquivo de retorno.");
        }
    }

    public List<ArquivoRetornoBB> listaArquivosRetornoBB() {
        return arquivoRetornoBBFacade.listaDecrescente();
    }

    public StreamedContent recuperarArquivoParaDownload() {

        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : selecionado.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, selecionado.getArquivo().getMimeType(), selecionado.getArquivo().getNome());
        return s;
    }

}
