/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.ws.procuradoria;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.WSIntegraSoftplan;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Arthur
 */
@WebService(
    endpointInterface = "br.com.webpublico.interfaces.WSIntegraSoftplan",
    targetNamespace = "http://procuradoria.ws.sistemasprefeitura.com.br/"
)
public class WSIntegraSoftplanImpl implements WSIntegraSoftplan {

    @EJB
    private IntegraSoftplanFacade integraSoftplanFacade;
    private static final Logger logger = LoggerFactory.getLogger(WSIntegraSoftplanImpl.class);
    private SimpleDateFormat SIMPLE_DATE = new SimpleDateFormat("yyyyMMdd");

    @Override
    public String informacaoDecisaoJudicial(String xml) {
        try {
            String xmlRetorno = processarInformacaoDecisaoJudicial(xml);
            return xmlRetorno;
        } catch (Exception e) {
            return gerarXmlDeErro(e);
        }
    }

    @Override
    public String informacaoAjuizamento(String xml) {
        try {
            String xmlRetorno = processarInformacaoAjuizamento(xml);
            return xmlRetorno;
        } catch (Exception e) {
            return gerarXmlDeErro(e);
        }
    }

    @Override
    public String informacaoPenhora(String xml) {
        try {
            String xmlRetorno = processarInformacaoPenhora(xml);
            return xmlRetorno;
        } catch (Exception e) {
            return gerarXmlDeErro(e);
        }
    }

    private Date menorDataPenhora(List<? extends BemCDA> bens, Date menorData) {
        for (BemCDA bemCDA : bens) {
            if (menorData == null || bemCDA.getDataPenhora().before(menorData)) {
                menorData = bemCDA.getDataPenhora();
            }
        }
        return menorData;
    }

    private String processarInformacaoPenhora(String xml) throws ExcecaoNegocioGenerica {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        Document doc = null;
        try {
            doc = Util.inicializarDOM(xml);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String nuCda = xPath.compile("/Message/MessageBody/Penhora/nuCda").evaluate(doc);
            Long numeroCDA = Long.parseLong(nuCda);
            List<VeiculoCDA> veiculos = recuperarVeiculosPenhorados(doc);
            List<ImovelCDA> imoveis = recuperarImoveisPenhorados(doc);
            List<TituloCDA> titulos = recuperarTitulosPenhorados(doc);
            List<GarantiaCDA> garantias = recuperarGarantiasPenhoradas(doc);
            Date dataPenhora = menorDataPenhora(
                veiculos,
                menorDataPenhora(
                    imoveis,
                    menorDataPenhora(
                        titulos,
                        menorDataPenhora(garantias, null))));
            respostas.addAll(integraSoftplanFacade.tratarInformacaoPenhora(numeroCDA, dataPenhora, veiculos, imoveis, titulos, garantias));
            String xmlRetorno = incluirRepostaXMLDeRetorno(doc, respostas);
            return xmlRetorno;
        } catch (Exception e) {
            logger.error("Erro ao processo informacaoPenhora: ", e);
            throw new ExcecaoNegocioGenerica(SummaryMessages.OPERACAO_NAO_REALIZADA.toString(), e);
        }
    }

    private String processarInformacaoAjuizamento(String xml) throws ExcecaoNegocioGenerica {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        Document doc;
        try {
            xml = xml.trim().replaceAll("^<\\?xml.*?\\\\?>", "");
            doc = Util.inicializarDOM(xml);
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList ajuizamentos = doc.getElementsByTagName("informarAjuizamento");
            List<IntegraSoftplanFacade.CDAAjuizada> cdas = Lists.newArrayList();
            for (int i = 0; i < ajuizamentos.getLength(); i++) {
                Node ajuizamento = ajuizamentos.item(i);
                String cdEvento = xPath.compile("cdEvento").evaluate(ajuizamento);
                String dtAjuizamento = xPath.compile("dtAjuizamento").evaluate(ajuizamento);
                String nuProcessoJud = xPath.compile("nuJudicial").evaluate(ajuizamento);
                IntegraSoftplanFacade.CDAAjuizada cdaAjuizada = new IntegraSoftplanFacade.CDAAjuizada();
                cdaAjuizada.numeroCDA = Long.parseLong(xPath.compile("nuCDA").evaluate(ajuizamento));
                cdaAjuizada.codigoEvento = Integer.parseInt(cdEvento);
                if (dtAjuizamento != null && !dtAjuizamento.isEmpty()) {
                    cdaAjuizada.dataAjuizamento = SIMPLE_DATE.parse(dtAjuizamento);
                }
                if (nuProcessoJud != null && !nuProcessoJud.isEmpty()) {
                    cdaAjuizada.numeroProcessoJudicial = nuProcessoJud;
                }
                cdas.add(cdaAjuizada);
            }
            respostas.addAll(integraSoftplanFacade.tratarInformacaoAjuizamento(cdas));
            String xmlRetorno = incluirRepostaXMLDeRetorno(doc, respostas);
            return xmlRetorno;
        } catch (Exception e) {
            logger.error("Erro ao processo informarAjuizamento: ", e);
            throw new ExcecaoNegocioGenerica(SummaryMessages.OPERACAO_NAO_REALIZADA.toString(), e);
        }
    }

    private String processarInformacaoDecisaoJudicial(String xml) throws ExcecaoNegocioGenerica {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        Document doc = null;
        try {
            doc = Util.inicializarDOM(xml);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String nuCda = xPath.compile("/Message/MessageBody/DecisaoJudicial/nuCda").evaluate(doc);
            String cdSituacao = xPath.compile("/Message/MessageBody/DecisaoJudicial/cdSituacao").evaluate(doc);
            String dtDecisao = xPath.compile("/Message/MessageBody/DecisaoJudicial/dtDecisao").evaluate(doc);
            String deObservacao = xPath.compile("/Message/MessageBody/DecisaoJudicial/deObservacao").evaluate(doc);

            Long numeroCDA = Long.parseLong(nuCda);
            SituacaoCertidaoDA situacaoCDA = recuperarSituacaoCDAEquivalenteCodigoSoftplan(cdSituacao);
            Date dataDecisao = (SIMPLE_DATE).parse(dtDecisao);

            respostas.addAll(integraSoftplanFacade.tratarDecisaoJudicial(numeroCDA, situacaoCDA, dataDecisao, deObservacao));
            String xmlRetorno = incluirRepostaXMLDeRetorno(doc, respostas);
            return xmlRetorno;
        } catch (Exception e) {
            logger.error("Erro ao processo decisaoJudicial: ", e);
            throw new ExcecaoNegocioGenerica(SummaryMessages.OPERACAO_NAO_REALIZADA.toString(), e);
        }
    }

    private SituacaoCertidaoDA recuperarSituacaoCDAEquivalenteCodigoSoftplan(String cdSituacao) {
        if ("1".equalsIgnoreCase(cdSituacao)) {
            return SituacaoCertidaoDA.CANCELADA;
        } else if ("2".equalsIgnoreCase(cdSituacao)) {
            return SituacaoCertidaoDA.SUSPENSA;
        }
        throw new ExcecaoNegocioGenerica("Código de situação da Softplan inválido: [" + cdSituacao + "]. Verifique.");
    }

    private String incluirRepostaXMLDeRetorno(Document doc, List<RespostaIntegraSoftplan> respostas) {
        try {
            if (respostas.isEmpty()) {
                respostas.add(new RespostaIntegraSoftplan(1L, "Processamento realizado com sucesso"));
            }
            //Só podemos enviar uma reposta por comunicação, portanto só a primeira será considerada...
            RespostaIntegraSoftplan primeiraResposta = respostas.get(0);

            XPath xPath = XPathFactory.newInstance().newXPath();
            Node messageBody = (Node) xPath.compile("/Message/MessageBody").evaluate(doc, XPathConstants.NODE);
            Node resposta = messageBody.appendChild(doc.createElement("Resposta"));

            Node cdRetorno = resposta.appendChild(doc.createElement("cdRetorno"));
            cdRetorno.setTextContent(primeiraResposta.getCdRetorno().toString());

            Node deRetorno = resposta.appendChild(doc.createElement("deRetorno"));
            deRetorno.setTextContent(primeiraResposta.getDeRetorno());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(baos);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return baos.toString();
        } catch (Exception e) {
            return gerarXmlDeErro(e);
        }
    }

    private String gerarXmlDeErro(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<erro>");
        e.printStackTrace(pw);
        pw.println("</erro>");
        pw.flush();
        return baos.toString();
    }

    private List<VeiculoCDA> recuperarVeiculosPenhorados(Document doc) throws Exception {
        List<VeiculoCDA> veiculos = new ArrayList<>();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList bens = (NodeList) xPath.compile("/Message/MessageBody/Penhora/ListaBem/Bem").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < bens.getLength(); i++) {
                Node bem = bens.item(i);
                if (isVeiculo(xPath, bem)) {
                    VeiculoCDA veiculo = new VeiculoCDA();

                    veiculo.setDataAvaliacao(SIMPLE_DATE.parse(xPath.compile("dtAvaliacao").evaluate(bem)));
                    veiculo.setDataPenhora(SIMPLE_DATE.parse(xPath.compile("dtPenhora").evaluate(bem)));
                    veiculo.setValorAvaliacao(Double.parseDouble(xPath.compile("vlAvaliacao").evaluate(bem)));
                    veiculo.setCodigoCategoria(Integer.parseInt(xPath.compile("cdCategoria").evaluate(bem)));
                    veiculo.setDescricaoCategoria(xPath.compile("deCategoria").evaluate(bem));

                    veiculo.setRenavam(Long.parseLong(xPath.compile(".//nuRenavam").evaluate(bem)));
                    veiculo.setPlaca(xPath.compile(".//nuPlaca").evaluate(bem));
                    veiculo.setChassi(xPath.compile(".//nuChassi").evaluate(bem));
                    veiculo.setTipo(xPath.compile(".//deTipo").evaluate(bem));
                    veiculo.setModelo(xPath.compile(".//deModelo").evaluate(bem));
                    veiculo.setChassi(xPath.compile(".//nuChassi").evaluate(bem));
                    veiculo.setAnoFabricacao(Integer.parseInt(xPath.compile(".//nuAnoFabricacao").evaluate(bem)));
                    veiculo.setAnoModelo(Integer.parseInt(xPath.compile(".//nuAnoModelo").evaluate(bem)));
                    veiculo.setCor(xPath.compile(".//deCor").evaluate(bem));
                    veiculo.setComplemento(xPath.compile(".//deComplemento").evaluate(bem));
                    veiculos.add(veiculo);
                }
            }
            return veiculos;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isVeiculo(XPath xPath, Node bem) throws XPathExpressionException {
        return xPath.compile(".//nuPlaca").evaluate(bem).length() > 0;
    }

    private List<ImovelCDA> recuperarImoveisPenhorados(Document doc) throws Exception {
        List<ImovelCDA> imoveis = new ArrayList<>();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList bens = (NodeList) xPath.compile("/Message/MessageBody/Penhora/ListaBem/Bem").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < bens.getLength(); i++) {
                Node bem = bens.item(i);
                if (isImovel(xPath, bem)) {
                    ImovelCDA imovel = new ImovelCDA();

                    imovel.setDataAvaliacao(SIMPLE_DATE.parse(xPath.compile("dtAvaliacao").evaluate(bem)));
                    imovel.setDataPenhora(SIMPLE_DATE.parse(xPath.compile("dtPenhora").evaluate(bem)));
                    imovel.setValorAvaliacao(Double.parseDouble(xPath.compile("vlAvaliacao").evaluate(bem)));
                    imovel.setCodigoCategoria(Integer.parseInt(xPath.compile("cdCategoria").evaluate(bem)));
                    imovel.setDescricaoCategoria(xPath.compile("deCategoria").evaluate(bem));

                    imovel.setRegistro(xPath.compile(".//nuRegistro").evaluate(bem));
                    imovel.setMatricula(xPath.compile(".//nuMatricula").evaluate(bem));
                    imovel.setComplementoImovel(xPath.compile(".//deComplemento").evaluate(bem));
                    imovel.setTipoLogradouro(xPath.compile(".//Endereco/deTipoLogradouro").evaluate(bem));
                    imovel.setLogradouro(xPath.compile(".//Endereco/deLogradouro").evaluate(bem));
                    imovel.setNumero(Integer.parseInt(xPath.compile(".//Endereco/nuRua").evaluate(bem)));
                    imovel.setComplementoEndereco(xPath.compile(".//Endereco/deComplemento").evaluate(bem));
                    imovel.setBairro(xPath.compile(".//Endereco/deBairro").evaluate(bem));
                    imovel.setMunicipio(xPath.compile(".//Endereco/nmMunicipio").evaluate(bem));
                    imovel.setUf(xPath.compile(".//Endereco/cdUf").evaluate(bem));
                    imovel.setCep(Integer.parseInt(xPath.compile(".//Endereco/nuCep").evaluate(bem)));
                    imoveis.add(imovel);
                }
            }
            return imoveis;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isImovel(XPath xPath, Node bem) throws XPathExpressionException {
        return xPath.compile(".//Endereco/deLogradouro").evaluate(bem).length() > 0;
    }

    private List<TituloCDA> recuperarTitulosPenhorados(Document doc) throws Exception {
        List<TituloCDA> titulos = new ArrayList<>();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList bens = (NodeList) xPath.compile("/Message/MessageBody/Penhora/ListaBem/Bem").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < bens.getLength(); i++) {
                Node bem = bens.item(i);
                if (isTitulo(xPath, bem)) {
                    TituloCDA titulo = new TituloCDA();

                    titulo.setDataAvaliacao(SIMPLE_DATE.parse(xPath.compile("dtAvaliacao").evaluate(bem)));
                    titulo.setDataPenhora(SIMPLE_DATE.parse(xPath.compile("dtPenhora").evaluate(bem)));
                    titulo.setValorAvaliacao(Double.parseDouble(xPath.compile("vlAvaliacao").evaluate(bem)));
                    titulo.setCodigoCategoria(Integer.parseInt(xPath.compile("cdCategoria").evaluate(bem)));
                    titulo.setDescricaoCategoria(xPath.compile("deCategoria").evaluate(bem));

                    titulo.setDescricao(xPath.compile(".//deTitulo").evaluate(bem));
                    titulo.setQuantidade(Integer.parseInt(xPath.compile(".//qtTitulo").evaluate(bem)));
                    titulo.setLocalizacao(xPath.compile(".//deLocalizacao").evaluate(bem));
                    titulo.setDataLocalizacao(SIMPLE_DATE.parse(xPath.compile(".//dtLocalizacao").evaluate(bem)));
                    titulo.setComplemento(xPath.compile(".//deComplemento").evaluate(bem));
                    titulos.add(titulo);
                }
            }
            return titulos;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isTitulo(XPath xPath, Node bem) throws XPathExpressionException {
        return xPath.compile(".//qtTitulo").evaluate(bem).length() > 0;
    }

    private List<GarantiaCDA> recuperarGarantiasPenhoradas(Document doc) throws Exception {
        List<GarantiaCDA> garantias = new ArrayList<>();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList bens = (NodeList) xPath.compile("/Message/MessageBody/Penhora/ListaBem/Bem").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < bens.getLength(); i++) {
                Node bem = bens.item(i);
                if (isGarantia(xPath, bem)) {
                    GarantiaCDA garantia = new GarantiaCDA();

                    garantia.setDataAvaliacao(SIMPLE_DATE.parse(xPath.compile("dtAvaliacao").evaluate(bem)));
                    garantia.setDataPenhora(SIMPLE_DATE.parse(xPath.compile("dtPenhora").evaluate(bem)));
                    garantia.setValorAvaliacao(Double.parseDouble(xPath.compile("vlAvaliacao").evaluate(bem)));
                    garantia.setCodigoCategoria(Integer.parseInt(xPath.compile("cdCategoria").evaluate(bem)));
                    garantia.setDescricaoCategoria(xPath.compile("deCategoria").evaluate(bem));

                    garantia.setDescricao(xPath.compile(".//deCartaDepositoSeguro").evaluate(bem));
                    garantia.setValidade(SIMPLE_DATE.parse(xPath.compile(".//dtValidade").evaluate(bem)));
                    garantia.setIndice(xPath.compile(".//nmIndice").evaluate(bem));
                    garantias.add(garantia);
                }
            }
            return garantias;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isGarantia(XPath xPath, Node bem) throws XPathExpressionException {
        return xPath.compile(".//deCartaDepositoSeguro").evaluate(bem).length() > 0;
    }

    @Override
    public String recuperarSaldoAtualizado(String nuCDA) {
        try {
            String xmlRetorno = processarSaldoAtualizado(nuCDA);
            return xmlRetorno;
        } catch (Exception e) {
            return gerarXmlDeErro(e);
        }
    }

    private String processarSaldoAtualizado(String nuCDA) {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        Document doc = null;
        try {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Message>"
                + "	<MessageId>"
                + "		<ServiceId/>"
                + "		<Version>1</Version>"
                + "		<MsgDesc/>"
                + "		<Code/>"
                + "		<FromAddress/>"
                + "		<ToAddress/>"
                + "		<Date/>"
                + "	</MessageId>"
                + "	<MessageBody>"
                + "		<RecuperarSaldoAtualizado>"
                + "			<nuCda/>"
                + "			<vlSaldoImpostoAtualizado/>"
                + "			<vlSaldoMultaAtualizado/>"
                + "			<vlSaldoJurosAtualizado/>"
                + "			<vlSaldoCorrecaoAtualizado/>"
                + "			<vlSaldoTotalAtualizado/>"
                + "			<vlPagoAtualizado/>"
                + "		</RecuperarSaldoAtualizado >"
                + "	</MessageBody>"
                + "</Message>";


            Long numeroCDA = Long.parseLong(nuCDA);
            ValoresAtualizadosCDA valoresAtualizadosCDA = new ValoresAtualizadosCDA();
            respostas.addAll(integraSoftplanFacade.tratarRecuperarSaldoAtualizado(numeroCDA, valoresAtualizadosCDA));

            doc = Util.inicializarDOM(xml);
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node nodeSaldos = (Node) xPath.compile("/Message/MessageBody/RecuperarSaldoAtualizado").evaluate(doc, XPathConstants.NODE);

            Node nodeNumeroCDA = (Node) xPath.compile(".//nuCda").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeNumeroCDA.setTextContent(nuCDA);

            Node nodeValorAtualizado = (Node) xPath.compile(".//vlSaldoImpostoAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeValorAtualizado.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorPrincipal().toPlainString().replace(".", ""), 18, "0"));

            Node nodeMulta = (Node) xPath.compile(".//vlSaldoMultaAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeMulta.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorMulta().toPlainString().replace(".", ""), 18, "0"));

            Node nodeJuros = (Node) xPath.compile(".//vlSaldoJurosAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeJuros.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorJuros().toPlainString().replace(".", ""), 18, "0"));

            Node nodeCorrecao = (Node) xPath.compile(".//vlSaldoCorrecaoAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeCorrecao.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorCorrecao().toPlainString().replace(".", ""), 18, "0"));

            Node nodeTotal = (Node) xPath.compile(".//vlSaldoTotalAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodeTotal.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorTotal().toPlainString().replace(".", ""), 18, "0"));

            Node nodePago = (Node) xPath.compile(".//vlPagoAtualizado").evaluate(nodeSaldos, XPathConstants.NODE);
            nodePago.setTextContent(StringUtil.cortaOuCompletaEsquerda(valoresAtualizadosCDA.getValorPago().toPlainString().replace(".", ""), 18, "0"));

            String xmlRetorno = incluirRepostaXMLDeRetorno(doc, respostas);
            return xmlRetorno;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(SummaryMessages.OPERACAO_NAO_REALIZADA.toString(), e);
        }
    }
}
