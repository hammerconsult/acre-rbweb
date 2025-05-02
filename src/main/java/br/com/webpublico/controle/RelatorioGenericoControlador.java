/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoFolha;
import br.com.webpublico.enums.TipoFuncaoAgrupador;
import br.com.webpublico.enums.TipoRelatorio;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.negocios.CabecalhoRelatorioFacade;
import br.com.webpublico.negocios.RecuperadorFacade;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class RelatorioGenericoControlador implements Serializable {

    @EJB
    private RecuperadorFacade recuperadorFacade;
    @EJB
    private CabecalhoRelatorioFacade cabecalhoRelatorioFacade;

    public void imprimirRelatorio(List lista, RelatorioPesquisaGenerico relatorio, boolean validarCabecalho) {
        if (relatorio.getTipoRelatorio().equals(TipoRelatorio.UNICO_REGISTRO)) {
            if (validarCabecalho) {
                if (relatorio.getCabecalhoRelatorio() == null) {
                    FacesUtil.addError("Não foi possível imprimir o Relatório.", "Informe o cabeçalho que deseja utilizar.");
                    return;
                }
                String conteudo = cabecalhoRelatorioFacade.mesclaTags(cabecalhoRelatorioFacade.alteraURLImagens(relatorio.getCabecalhoRelatorio().getConteudo()));
                relatorio.setCabecalhoMesclado(conteudo);
            }

            geraRelatorioUnicoRegistro(relatorio);
        }
        if (relatorio.getTipoRelatorio().equals(TipoRelatorio.TABELA)) {
            if (podeImprimir(lista)) {
                if (validarCabecalho) {
                    if (relatorio.getCabecalhoRelatorio() == null) {
                        FacesUtil.addError("Não foi possível imprimir o Relatório.", "Informe o cabeçalho que deseja utilizar.");
                        return;
                    }
                    String conteudo = cabecalhoRelatorioFacade.mesclaTags(cabecalhoRelatorioFacade.alteraURLImagens(relatorio.getCabecalhoRelatorio().getConteudo()));
                    relatorio.setCabecalhoMesclado(conteudo);
                }
                geraRelatorioTabela(lista, relatorio);
            } else {
                if (lista != null || lista.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar gerar o relatório!", "Não foi possível encontrar registros para gerar o relatório."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar gerar o relatório!", "Efetue a pesquisa para gerar o relatório."));
                }
            }
        }
    }

    public boolean podeImprimir(List lista) {
        if (lista != null) {
            if (!lista.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    private void geraRelatorioTabela(List lista, RelatorioPesquisaGenerico relatorioPesquisaGenerico) {

        String conteudo = criarCabecalho(relatorioPesquisaGenerico)
                + "<table border=\"1\" style=\"width:100%; text-align:center\" class=\"igualDataTable\">"
                + "<tr>";
        List<AtributoRelatorioGenerico> atributos = new ArrayList<AtributoRelatorioGenerico>();
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : relatorioPesquisaGenerico.getTarget()) {
            if (atributoRelatorioGenerico.getPodeImprimir()) {
                atributos.add(atributoRelatorioGenerico);
            }
        }
        for (AtributoRelatorioGenerico atributo : atributos) {
            conteudo += "<th style=\"text-align: center;\"> " + atributo.getLabel() + " </th>";
        }

        Object[] somaDeValores = new Object[relatorioPesquisaGenerico.getTarget().size()];

        for (Object objeto : lista) {
            String cor = (relatorioPesquisaGenerico.getColorido() ? (lista.indexOf(objeto) % 2 == 0 ? relatorioPesquisaGenerico.getCorFundoZebrado1() : relatorioPesquisaGenerico.getCorFundoZebrado2()) : RelatorioPesquisaGenerico.COR_BRANCO);
            conteudo += "<tr style=\"background: #" + cor + ";\"> ";

            int posicaoDaSomaDeValores = 0;

            for (AtributoRelatorioGenerico atributo : atributos) {
                conteudo += "<td style=\"text-align:" + atributo.getAlinhamento().getValor() + ";\"> " + recuperadorFacade.preencherCampo(objeto, atributo) + " </td>";

                if (relatorioPesquisaGenerico.getMostrarTotalizador()) {
                    if (atributo.renderIsNumber()) {
                        Object objetoValor = getAtributoDaCondicao(objeto, atributo.getCondicao());

                        if (somaDeValores[posicaoDaSomaDeValores] == null) {
                            somaDeValores[posicaoDaSomaDeValores] = objetoValor;
                        } else {
                            somaDeValores = somarValores(somaDeValores, posicaoDaSomaDeValores, atributo, objetoValor);
                        }

                    }
                }
                posicaoDaSomaDeValores++;
            }
            conteudo += "</tr> ";
        }
        if (relatorioPesquisaGenerico.getMostrarTotalizador()) {
            conteudo += criarRodapeTotalizador(lista.size(), relatorioPesquisaGenerico, somaDeValores);
        }
        conteudo += "</table>";

        conteudo += relatorioPesquisaGenerico.getObservacao();

        conteudo += adicionaRodape(relatorioPesquisaGenerico, lista.size());

        try {
            Util.geraPDF(relatorioPesquisaGenerico.getNomeRelatorio(), conteudo, FacesContext.getCurrentInstance());
        } catch (Exception e) {

        }
    }

    private String criarRodapeTotalizador(int quantidadeRegistro, RelatorioPesquisaGenerico relatorioPesquisaGenerico, Object[] somaDeValores) {
        String conteudo = "<tr style=\"background: #" + (relatorioPesquisaGenerico.getColorido() ? relatorioPesquisaGenerico.getCorFundoTotalizador() : RelatorioPesquisaGenerico.COR_BRANCO) + ";\"> ";
        for (int j = 0; j < relatorioPesquisaGenerico.getTarget().size(); j++) {
            if (somaDeValores[j] != null) {
                if (somaDeValores[j] instanceof BigDecimal) {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(4);
                    if (relatorioPesquisaGenerico.getTarget().get(j).getFuncaoAgrupador() != null) {
                        if (relatorioPesquisaGenerico.getTarget().get(j).getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MEDIA)) {
                            somaDeValores[j] = ((BigDecimal) somaDeValores[j]).divide(new BigDecimal(quantidadeRegistro), 4, RoundingMode.HALF_DOWN);
                        }
                    }
                    conteudo += "<td style=\"text-align:" + relatorioPesquisaGenerico.getTarget().get(j).getAlinhamento().getValor() + "; \"> " + nf.format(new BigDecimal(somaDeValores[j].toString())) + "</td>";
                } else {
                    conteudo += "<td style=\"text-align:" + relatorioPesquisaGenerico.getTarget().get(j).getAlinhamento().getValor() + "; \"> " + somaDeValores[j].toString() + "</td>";
                }
            } else {
                conteudo += "<td> </td>";
            }
        }
        conteudo += "</tr> ";
        return conteudo;
    }

    private String criarCabecalho(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {

        String corBranco = RelatorioPesquisaGenerico.COR_BRANCO;
        String tipoFolha = relatorioPesquisaGenerico.getTipoFolha().equals(TipoFolha.RETRATO) ? "portrait" : "landscape";

        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
                + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
                + " <html>"
                + " <head>"
                + " <style type=\"text/css\">@page{size: A4 " + tipoFolha + ";}</style>"
                + " <style type=\"text/css\">"
                + ".igualDataTable{\n"
                + "    border-collapse: collapse; /* CSS2 */\n"
                + "   border: 1px solid #aaaaaa;  width: 100%;\n"
                + "    ;\n"
                + "}"
                + ".igualDataTable th{\n"
                + "    border: 1px solid #aaaaaa;  \n"
                + "    height: 20px;\n"
                + "    background:#" + (relatorioPesquisaGenerico.getColorido() ? relatorioPesquisaGenerico.getCorFundoTituloTabela() : corBranco) + " 50% 50% repeat-x;\n"
                + "}\n"
                + "\n"
                + ".igualDataTable td{\n"
                + "    padding: 2px;\n"
                + "    border: 1px solid #aaaaaa;  \n"
                + "    height: 20px;\n"
                + "\n"
                + "}\n"
                + "\n"
                + ".igualDataTable thead td{\n"
                + "    border: 1px solid #aaaaaa;  \n"
                + "    background:#" + (relatorioPesquisaGenerico.getColorido() ? relatorioPesquisaGenerico.getCorFundoTituloTabela() : corBranco) + "repeat-x scroll 50% 50%; \n"
                + "    border: 1px; \n"
                + "    text-align: center; \n"
                + "    height: 20px;\n"
                + "}"
                + "body{"
                + "font-family:" + relatorioPesquisaGenerico.getTipoFonteRelatorio().getValue() + "; font-size: " + relatorioPesquisaGenerico.getTamanhoDaFonte() + "px"
                + "}"
                + ""
                + "</style>"
                + " <title>"
                + " Sistema WebPúblico "
                + " </title>"
                + " </head>"
                + " <body>"
                + "" + (relatorioPesquisaGenerico.getCabecalhoMesclado() != null ? relatorioPesquisaGenerico.getCabecalhoMesclado() : "")
                + "<center><h3> " + relatorioPesquisaGenerico.getTitulo() + "</h3></center>"
                + "<br/><br/>";
    }

    private static String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append(quebrado[0]);
        b.append("/").append(quebrado[1]);
        b.append("/").append(quebrado[2]);
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    private String getUsuarioLogado() {
        SistemaControlador controladorPeloNome = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        try {
            return controladorPeloNome.getUsuarioCorrente().getPessoaFisica().getNome();
        } catch (Exception e) {
            return controladorPeloNome.getUsuarioCorrente().getLogin();
        }
    }

    private void geraRelatorioUnicoRegistro(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        String conteudo = criarCabecalho(relatorioPesquisaGenerico);
        List<AtributoRelatorioGenerico> atributos = new ArrayList<AtributoRelatorioGenerico>();
        for (AtributoRelatorioGenerico atributo : relatorioPesquisaGenerico.getTarget()) {
            if (atributo.getPodeImprimir()) {
                atributos.add(atributo);
            }
        }


        conteudo += "<table>";

        int i = 0;
        for (AtributoRelatorioGenerico atributo : atributos) {

            String corDaLinha = (relatorioPesquisaGenerico.getColorido() ? (i % 2 == 0 ? relatorioPesquisaGenerico.getCorFundoZebrado1() : relatorioPesquisaGenerico.getCorFundoZebrado2()) : RelatorioPesquisaGenerico.COR_BRANCO);
            i++;
            conteudo += "<tr style='background: #" + corDaLinha + "'> ";

            if (atributo.getAtributoDeLista()) {


                conteudo += "<td style='text-align:left' >";
                conteudo += " Lista De " + atributo.getLabel() + " ";
                conteudo += "</td>";

                conteudo += "<td style='text-align:" + atributo.getAlinhamento().getValor() + "' >";
                try {
                    Object obterCampo = recuperadorFacade.obterCampo(relatorioPesquisaGenerico.getObjetoSelecionadoRelatorio(), atributo.getCondicao());
                    List objetos = (List) obterCampo;


                    EntidadeMetaData metadataObetoLista = new EntidadeMetaData(objetos.get(0).getClass());
                    conteudo += "<div style='width:100%; text-align:center; height:30px; border: 1px solid #aaaaaa; " + (relatorioPesquisaGenerico.getColorido() ? "background :#CDC9C9" : "") + "; padding:0px;'> <h3> " + atributo.getLabel() + " </h3> </div>";
                    conteudo += "<table border='1' style='width:100%;' class='igualDataTable'> ";
                    conteudo += "<tr>";
                    for (AtributoRelatorioGenerico atributoRelatorioPesquisaGenerico : getAtributosImprimiveisMostraTabela(metadataObetoLista)) {
                        conteudo += "<th style='text-align:" + atributoRelatorioPesquisaGenerico.getAlinhamento().getValor() + ";'> " + atributoRelatorioPesquisaGenerico.getLabel() + " </th>";
                    }
                    conteudo += "</tr>";

                    for (Object objeto : objetos) {
                        String cor = (relatorioPesquisaGenerico.getColorido() ? (objetos.indexOf(objeto) % 2 == 0 ? relatorioPesquisaGenerico.getCorFundoZebrado1() : relatorioPesquisaGenerico.getCorFundoZebrado2()) : RelatorioPesquisaGenerico.COR_BRANCO);
                        conteudo += "<tr style='background: #" + cor + "'> ";
                        for (AtributoRelatorioGenerico atributoRelatorioPesquisaGenerico : getAtributosImprimiveisMostraTabela(metadataObetoLista)) {
                            conteudo += "<td style=\"text-align:" + atributoRelatorioPesquisaGenerico.getAlinhamento().getValor() + "; \"> " + recuperadorFacade.preencherCampo(objeto, atributoRelatorioPesquisaGenerico) + " </td>";
                        }
                        conteudo += "</tr> ";
                    }
                    conteudo += "</tr>"
                            + "</table>";
                } catch (Exception e) {
                }
                conteudo += "</td>";

            } else {
                try {
                    String label = atributo.getLabel();
                    String valor = recuperadorFacade.preencherCampo(relatorioPesquisaGenerico.getObjetoSelecionadoRelatorio(), atributo);

                    conteudo += "<td style='text-align:left' >";
                    conteudo += "<strong> " + label + "</strong> : ";
                    conteudo += "</td>";


                    conteudo += "<td style='text-align:" + atributo.getAlinhamento().getValor() + "' >";
                    conteudo += " " + valor + " ";
                    conteudo += "</td>";

                } catch (Exception e) {
                }
            }


            conteudo += "</tr>";
        }
        conteudo += "</table>";

        conteudo += relatorioPesquisaGenerico.getObservacao();

        conteudo += adicionaRodape(relatorioPesquisaGenerico, 0);

        try {
            Util.geraPDF(relatorioPesquisaGenerico.getNomeRelatorio(), conteudo, FacesContext.getCurrentInstance());
        } catch (Exception e) {

        }
    }

    private List<AtributoRelatorioGenerico> getAtributosImprimiveisMostraTabela(EntidadeMetaData entidadeMetaData) {
        List<AtributoRelatorioGenerico> retorno = new ArrayList<AtributoRelatorioGenerico>();
        for (AtributoMetadata atributoMetadata : entidadeMetaData.getAtributos()) {
            if (atributoMetadata.getAtributo().isAnnotationPresent(Tabelavel.class)) {
                AtributoRelatorioGenerico atributo = new AtributoRelatorioGenerico().instanciaAtributoRelatorioGenerico(atributoMetadata);
                if (atributo.getPodeImprimir()) {
                    retorno.add(atributo);
                }
            }
        }
        return retorno;
    }

    public void imprimirRelatorioAgrupador(List lista, RelatorioPesquisaGenerico relatorioTabela) {
        if (!podeImprimir(lista)) {
            if (lista != null || lista.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar gerar o relatório!", "Não foi possível encontrar registros para gerar o relatório."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar gerar o relatório!", "Efetue a pesquisa para gerar o relatório."));
            }
            return;
        }
        int quantidadeAtributosMarcadosComoAgrupador = getQuantidadeAtributosMarcadoComoAgrupador(relatorioTabela);
        AtributoRelatorioGenerico[] atributosAgrupador = new AtributoRelatorioGenerico[quantidadeAtributosMarcadosComoAgrupador];

        int contadorAtributosAgrupador = adicionarAtributosAgrupador(atributosAgrupador, relatorioTabela);
        if (validaAtibutoSelecionado(contadorAtributosAgrupador)) {
            return;
        }

        String html = criarCabecalho(relatorioTabela) + "<table border=\"1\" style=\"width:100%;\" class=\"igualDataTable\">";

        Set<Object[]> objetosAgrupado = montaObjetosAgrupador(contadorAtributosAgrupador, atributosAgrupador, lista);

        for (Object[] objetoAgrupador : objetosAgrupado) {
            html += montaDivSuperiorAgrupador(objetoAgrupador, relatorioTabela);
            html += montaObjetoDetalhes(atributosAgrupador, objetoAgrupador, relatorioTabela, lista);
        }
        html += "</table>";

        html += relatorioTabela.getObservacao();

        html += adicionaRodape(relatorioTabela, lista.size());


        try {
            Util.geraPDF(relatorioTabela.getNomeRelatorio(), html, FacesContext.getCurrentInstance());
        } catch (Exception e) {

        }
    }


    private int getQuantidadeAtributosMarcadoComoAgrupador(RelatorioPesquisaGenerico relatorioTabela) {
        int i = 0;
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : relatorioTabela.getTarget()) {
            if (atributoRelatorioGenerico.getAgrupador()) {
                i++;
            }
        }
        return i;
    }

    private Set<Object[]> montaObjetosAgrupador(int contadorAtributosAgrupador, AtributoRelatorioGenerico[] atributosAgrupador, List lista) {
        Set<Object[]> objetosAgrupado = new HashSet<Object[]>();
        //Objetos Detalhes do Objeto Agrupador
        for (Object objetoDavez : lista) {
            int posicaoDoValor = 0;
            Object[] novoObjetoAgrupador = new Object[contadorAtributosAgrupador];
            for (AtributoRelatorioGenerico atributo : atributosAgrupador) {
                if (atributo != null) {
                    Object valorDoAtributo = getAtributoDaCondicao(objetoDavez, atributo.getCondicao());
                    novoObjetoAgrupador[posicaoDoValor] = valorDoAtributo;
                    posicaoDoValor++;
                }
            }
            boolean podeAdicionar = true;
            for (Object objetoAgrupado : novoObjetoAgrupador) {
                for (Object[] objects : objetosAgrupado) {
                    for (Object objetoJaAgrupado : objects) {
                        if (objetoAgrupado != null && objetoJaAgrupado != null) {
                            if (objetoAgrupado.equals(objetoJaAgrupado)) {
                                podeAdicionar = false;
                            }
                        }
                    }
                }
            }
            if (podeAdicionar) {
                objetosAgrupado.add(novoObjetoAgrupador);
            }
        }
        return objetosAgrupado;
    }

    private String montaObjetoDetalhes(AtributoRelatorioGenerico[] atributosAgrupador, Object[] objetoAgrupador, RelatorioPesquisaGenerico relatorioTabela, List lista) {

        String html = adicionarCabecalhoDetalhesTabela(relatorioTabela);
        Object[] somaDeValores = new Object[relatorioTabela.getTarget().size()];

        List<Object> objetosOrdenadosPorAtributoAgrupador = getObjetosListaOrdenadosPorAtributosAgrupador(lista, atributosAgrupador);
        int contador = 0;
        for (Object objectDaVez : objetosOrdenadosPorAtributoAgrupador) {
            boolean pode = podeAdicionarObjeto(atributosAgrupador, objetoAgrupador, objectDaVez);
            if (pode) {
                if (relatorioTabela.getMostrarDetalhes()) {
                    String cor = (relatorioTabela.getColorido() ? (objetosOrdenadosPorAtributoAgrupador.indexOf(objectDaVez) % 2 == 0 ? relatorioTabela.getCorFundoZebrado1() : relatorioTabela.getCorFundoZebrado2()) : RelatorioPesquisaGenerico.COR_BRANCO);
                    html += "<tr style=\"background: #" + cor + ";\"> ";
                }
                int posicaoDaSomaDeValores = 0;
                for (AtributoRelatorioGenerico atributo : relatorioTabela.getTarget()) {
                    if (relatorioTabela.getMostrarDetalhes()) {
                        html += "<td style=\"text-align:" + atributo.getAlinhamento().getValor() + "; \"> " + recuperadorFacade.preencherCampo(objectDaVez, atributo) + "</td>";
                    }
                    //CALCULA OS VALORES PARA OS RODAPES
                    if (atributo.renderIsNumber()) {
                        Object objeto = getAtributoDaCondicao(objectDaVez, atributo.getCondicao());
                        if (somaDeValores[posicaoDaSomaDeValores] == null) {
                            somaDeValores[posicaoDaSomaDeValores] = objeto;
                        } else {
                            somaDeValores = somarValores(somaDeValores, posicaoDaSomaDeValores, atributo, objeto);
                        }
                    }
                    posicaoDaSomaDeValores++;
                }
                if (relatorioTabela.getMostrarDetalhes()) {
                    html += "</tr>";
                }
                contador++;
            }
        }
        if (relatorioTabela.getMostrarTotalizador()) {
            html += criarRodapeTotalizador(contador, relatorioTabela, somaDeValores);
            html += adicionaContadorRegistro(contador, relatorioTabela);
        }
        return html;
    }

    private Object[] somarValores(Object[] somaDeValores, int posicaoDaSomaDeValores, AtributoRelatorioGenerico atributo, Object objeto) {
        if (atributo.getFuncaoAgrupador() != null) {
            if (objeto instanceof BigDecimal) {
                BigDecimal valor = (BigDecimal) objeto;
                if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.SOMAR)
                        || atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MEDIA)) {

                    somaDeValores[posicaoDaSomaDeValores] = ((BigDecimal) somaDeValores[posicaoDaSomaDeValores]).add(valor);
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MAIOR)) {
                    if (valor.compareTo(((BigDecimal) somaDeValores[posicaoDaSomaDeValores])) == 1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MENOR)) {
                    if (valor.compareTo(((BigDecimal) somaDeValores[posicaoDaSomaDeValores])) == -1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                }
            }
            if (objeto instanceof Long) {
                Long valor = (Long) objeto;
                if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.SOMAR)
                        || atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MEDIA)) {
                    somaDeValores[posicaoDaSomaDeValores] = ((Long) somaDeValores[posicaoDaSomaDeValores]) + valor;
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MAIOR)) {
                    if (valor.compareTo(((Long) somaDeValores[posicaoDaSomaDeValores])) == 1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MENOR)) {
                    if (valor.compareTo(((Long) somaDeValores[posicaoDaSomaDeValores])) == -1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                }
            }
            if (objeto instanceof Integer) {
                Integer valor = (Integer) objeto;
                if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.SOMAR)
                        || atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MEDIA)) {
                    somaDeValores[posicaoDaSomaDeValores] = ((Integer) somaDeValores[posicaoDaSomaDeValores]) + valor;
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MAIOR)) {
                    if (valor.compareTo(((Integer) somaDeValores[posicaoDaSomaDeValores])) == 1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MENOR)) {
                    if (valor.compareTo(((Integer) somaDeValores[posicaoDaSomaDeValores])) == -1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                }
            }
            if (objeto instanceof Double) {
                Double valor = (Double) objeto;
                if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.SOMAR)
                        || atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MEDIA)) {
                    somaDeValores[posicaoDaSomaDeValores] = ((Double) somaDeValores[posicaoDaSomaDeValores]) + valor;
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MAIOR)) {
                    if (valor.compareTo(((Double) somaDeValores[posicaoDaSomaDeValores])) == 1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                } else if (atributo.getFuncaoAgrupador().equals(TipoFuncaoAgrupador.MENOR)) {
                    if (valor.compareTo(((Double) somaDeValores[posicaoDaSomaDeValores])) == -1) {
                        somaDeValores[posicaoDaSomaDeValores] = objeto;
                    }
                }
            }
        }
        return somaDeValores;
    }

    private boolean validaAtibutoSelecionado(int contadorAtributosAgrupador) {
        if (contadorAtributosAgrupador == 0) {
            FacesUtil.addError("Não foi possível Imprimir o Relátorio.", "Não existe nenhum Campo Marcado como Agrupador.");
            return true;
        }
        return false;
    }

    private int adicionarAtributosAgrupador(AtributoRelatorioGenerico[] atributosAgrupador, RelatorioPesquisaGenerico relatorio) {
        int contadorAtributosAgrupador = 0;
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : relatorio.getTarget()) {
            if (atributoRelatorioGenerico.getAgrupador()) {
                atributosAgrupador[contadorAtributosAgrupador] = atributoRelatorioGenerico;
                contadorAtributosAgrupador++;
            }
        }
        return contadorAtributosAgrupador;
    }

    public Object getAtributoDaCondicao(Object o, String condicao) {
        if (!condicao.contains(".")) {
            Object retorno = recuperadorFacade.getAtributo(o, condicao);
            return retorno;
        }
        String[] split = condicao.split("\\.");
        o = recuperadorFacade.getAtributo(o, split[0]);
        condicao = condicao.replace(split[0] + ".", "");
        return getAtributoDaCondicao(o, condicao);
    }

    private String adicionarCabecalhoDetalhesTabela(RelatorioPesquisaGenerico relatorioTabela) {
        String html = "";
        for (AtributoRelatorioGenerico atributoRelatorioGenerico : relatorioTabela.getTarget()) {
            html += "<th style=\"text-align:center; \"> " + atributoRelatorioGenerico.getLabel() + "</th>";
        }
        return html;
    }

    private List<Object> getObjetosListaOrdenadosPorAtributosAgrupador(List lista, final AtributoRelatorioGenerico[] atributosAgrupador) {

        Collections.sort(lista, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                for (AtributoRelatorioGenerico atributoRelatorioGenerico : atributosAgrupador) {
                    String valor1 = recuperadorFacade.preencherCampo(o1, atributoRelatorioGenerico);
                    String valor2 = recuperadorFacade.preencherCampo(o2, atributoRelatorioGenerico);
                    if (valor1 != null && valor2 != null) {
                        return valor1.compareTo(valor2);
                    }
                }
                return 0;
            }
        });
        return lista;
    }

    private boolean podeAdicionarObjeto(AtributoRelatorioGenerico[] atributosAgrupador, Object[] objetoAgrupador, Object objectDaVez) {
        Boolean[] atributosIguais = new Boolean[atributosAgrupador.length];
        int i = 0;
        for (Object object : objetoAgrupador) {
            for (AtributoRelatorioGenerico atributo : atributosAgrupador) {
                Object atributoDaCondicao = getAtributoDaCondicao(objectDaVez, atributo.getCondicao());
                if (object != null) {
                    if (object.equals(atributoDaCondicao)) {
                        atributosIguais[i] = Boolean.TRUE;
                        i++;
                    }
                }
            }
        }
        boolean pode = false;
        int contadorDeAtributosIguais = 0;
        for (Boolean boolean1 : atributosIguais) {
            if (boolean1 != null) {
                if (boolean1) {
                    contadorDeAtributosIguais++;
                }
            }
        }
        if (contadorDeAtributosIguais == atributosAgrupador.length) {
            pode = true;
        }
        return pode;
    }

    private String adicionaRodape(RelatorioPesquisaGenerico relatorioTabela, int quantidadeDeRegistros) {
        String html = "";
        if (relatorioTabela.getMostrarRodape()) {
            html += "<hr/>"
                    + "<table border=\"0\" style=\"width:100%;\">"
                    + "<tr>"
                    + "<td style=\"text-align:left\">Usúario: " + getUsuarioLogado() + " </td>";
            if (quantidadeDeRegistros != 0) {
                html += "<td style=\"text-align:center\">Quantidade de Registro(s): " + quantidadeDeRegistros + " </td>";
            }
            html += "<td style=\"text-align:right\">Data: " + Util.dateHourToString(new Date()) + "</td>"
                    + "</tr>"
                    + "</table>"
                    + "<hr/>"
                    + "<br/><br/>"
                    + " </body>"
                    + " </html>";
        } else {
            html += "<br/><br/>"
                    + " </body>"
                    + " </html>";
        }
        return html;
    }

    private String adicionaContadorRegistro(int contador, RelatorioPesquisaGenerico relatorioTabela) {
        String cor = relatorioTabela.getColorido() ? relatorioTabela.getCorFundoTotalizador() : RelatorioPesquisaGenerico.COR_BRANCO;
        String html = "<tr style=\"background: #" + cor + ";\"> "
                + "   <td colspan=\"" + relatorioTabela.getTarget().size() + "\"> "
                + "     <div> "
                + "         <center>"
                + "              Quantidade de Registro : " + contador
                + "           </center> "
                + "     </div> "
                + " </td> "
                + "</tr>";
        return html;
    }

    private String montaDivSuperiorAgrupador(Object[] objetosAgrupador, RelatorioPesquisaGenerico relatorioTabela) {
        String html = "";
        //MONTA A DIV SUPERIOR DO AGRUPAMENTO
        String stringObjetoAgrupador = "";
        String letraEOuVirgula = objetosAgrupador.length == 2 ? " e " : " , ";
        int indice = 0;
        for (Object objetoAgrupador : objetosAgrupador) {
            if (objetoAgrupador != null) {
                if (objetoAgrupador instanceof Date) {
                    Date data = (Date) objetoAgrupador;
                    stringObjetoAgrupador += Util.formatterDiaMesAno.format(data).toString() + letraEOuVirgula;
                } else {
                    stringObjetoAgrupador += objetoAgrupador.toString() + letraEOuVirgula;
                }
                if (objetosAgrupador.length - 1 == indice) {
                    stringObjetoAgrupador = stringObjetoAgrupador.substring(0, stringObjetoAgrupador.length() - 3) + ".";
                }
            }
            indice++;
        }
        String cor = relatorioTabela.getColorido() ? relatorioTabela.getCorFundoAgrupador() : RelatorioPesquisaGenerico.COR_BRANCO;
        html += "<tr style=\"background: #" + cor + ";\"> <td colspan=\"" + relatorioTabela.getTarget().size() + "\"> <strong> Agrupando por: " + stringObjetoAgrupador + " </strong> </td> </tr> ";
        return html;
    }
}
