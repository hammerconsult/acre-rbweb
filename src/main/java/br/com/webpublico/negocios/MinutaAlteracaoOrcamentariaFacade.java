/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemSuplementacaoORC;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.ValorPorExtenso;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class MinutaAlteracaoOrcamentariaFacade extends AbstractFacade<AlteracaoORC> {


    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MinutaAlteracaoOrcamentariaFacade() {
        super(AlteracaoORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private String montarQuadroAnulacaoDiario(AlteracaoORC alteracaoORC) {
        StringBuilder retorno = new StringBuilder();
        String br = "</br>";
        for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
            UnidadeOrganizacional unidade = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional();
            SubAcaoPPA subAcaoPPA = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getSubAcaoPPA();
            DespesaORC despesaORC = anulacaoORC.getFonteDespesaORC().getDespesaORC();
            String codigoDescricaoUnidade = "";
            String codigoDescricaoOrgao = "";
            Conta conta = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
            List<String> contasPai = alteracaoORCFacade.getContaFacade().getContasPai(conta);
            if (unidade != null) {
                HierarquiaOrganizacional ho_unidade = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidade, sistemaFacade.getDataOperacao());

                if (ho_unidade != null) {
                    codigoDescricaoUnidade = ho_unidade.getCodigo() + " - " + ho_unidade.getDescricao().toUpperCase();
                    HierarquiaOrganizacional ho_orgao = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getPaiDe(ho_unidade, sistemaFacade.getDataOperacao());
                    if (ho_orgao != null) {
                        codigoDescricaoOrgao = ho_orgao.getCodigo() + " - " + ho_orgao.getDescricao().toUpperCase();
                    }
                }

            }
            retorno.append("<table border='1' style='width:100%;'>");
            retorno.append("<tr>");
            retorno.append("<td colspan=\"3\"> " + codigoDescricaoOrgao + "</td>");
            retorno.append("</tr>");

            retorno.append("<tr>");
            retorno.append("<td colspan=\"3\">" + codigoDescricaoUnidade + "</td>");
            retorno.append("</tr>");

            retorno.append("<tr>");
            retorno.append("<td colspan=\"3\">" + despesaORC.getCodigo() + " - " + subAcaoPPA.getDescricao().toUpperCase() + "</td>");
            retorno.append("</tr>");

            for (String contaDaVez : contasPai) {
                if (contasPai.indexOf(contaDaVez) != contasPai.size() - 1) {
                    retorno.append("<tr>");
                    retorno.append("<td colspan=\"3\">" + contaDaVez + "</td>");
                    retorno.append("</tr>");
                } else {
                    FonteDeRecursos fonteDeRecursos = ((ContaDeDestinacao) anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos();
                    retorno.append("<tr>");
                    retorno.append("<td rowspan=\"10\">" + contaDaVez + "</td>"
                        + "<td rowspan=\"7\">" + fonteDeRecursos.getCodigo() + " - " + fonteDeRecursos.getDescricao() + "</td>"
                        + "<td rowspan=\"5\">" + Util.formataValor(anulacaoORC.getValor()) + "</td>");
                    retorno.append("</tr>");
                }
            }
            retorno.append("</table>");
        }
        return retorno.toString();
    }

    private String montarQuadroSuplementarDiario(AlteracaoORC alteracaoORC) {
        StringBuilder retorno = new StringBuilder();
        String br = "</br>";

        if (alteracaoORC.getSuplementacoesORCs() != null || alteracaoORC.getAnulacoesORCs() != null) {
            for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {

                UnidadeOrganizacional unidade = suplementacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();
                SubAcaoPPA subAcaoPPA = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getSubAcaoPPA();
                DespesaORC despesaORC = suplementacaoORC.getFonteDespesaORC().getDespesaORC();
                String codigoDescricaoUnidade = "";
                String codigoDescricaoOrgao = "";
                Conta conta = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
                List<String> contasPai = alteracaoORCFacade.getContaFacade().getContasPai(conta);
                if (unidade != null) {
                    HierarquiaOrganizacional ho_unidade = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidade, sistemaFacade.getDataOperacao());

                    if (ho_unidade != null) {
                        codigoDescricaoUnidade = ho_unidade.getCodigo() + " - " + ho_unidade.getDescricao().toUpperCase();
                        HierarquiaOrganizacional ho_orgao = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getPaiDe(ho_unidade, sistemaFacade.getDataOperacao());
                        if (ho_orgao != null) {
                            codigoDescricaoOrgao = ho_orgao.getCodigo() + " - " + ho_orgao.getDescricao().toUpperCase();
                        }
                    }

                }
                retorno.append("<table border='1' style='width:100%;'>");
                retorno.append("<tr>");
                retorno.append("<td colspan=\"3\"> " + codigoDescricaoOrgao + "</td>");
                retorno.append("</tr>");

                retorno.append("<tr>");
                retorno.append("<td colspan=\"3\">" + codigoDescricaoUnidade + "</td>");
                retorno.append("</tr>");

                retorno.append("<tr>");
                retorno.append("<td colspan=\"3\">" + despesaORC.getCodigo() + " - " + subAcaoPPA.getDescricao().toUpperCase() + "</td>");
                retorno.append("</tr>");

                for (String contaDaVez : contasPai) {
                    if (contasPai.indexOf(contaDaVez) != contasPai.size() - 1) {
                        retorno.append("<tr>");
                        retorno.append("<td colspan=\"3\">" + contaDaVez + "</td>");
                        retorno.append("</tr>");
                    } else {
                        FonteDeRecursos fonteDeRecursos = ((ContaDeDestinacao) suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos();
                        retorno.append("<tr>");
                        retorno.append("<td rowspan=\"10\">" + contaDaVez + "</td>"
                            + "<td rowspan=\"7\">" + fonteDeRecursos.getCodigo() + " - " + fonteDeRecursos.getDescricao() + "</td>"
                            + "<td rowspan=\"5\">" + Util.formataValor(suplementacaoORC.getValor()) + "</td>");
                        retorno.append("</tr>");
                    }
                }
                retorno.append("</table>");
            }
        }
        return retorno.toString();
    }

    public String recuperarValorSuplementacaoPorExtenso(AlteracaoORC alteracaoORC) {
        BigDecimal valor = BigDecimal.ZERO;
        for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
            valor = valor.add(suplementacaoORC.getValor());
        }
        return ValorPorExtenso.valorPorExtenso(valor);
    }

    public String recuperaValorSuplementacao(AlteracaoORC alteracaoORC) {
        BigDecimal valor = BigDecimal.ZERO;
        for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
            valor = valor.add(suplementacaoORC.getValor());
        }
        return Util.formataValor(valor);
    }

    public String recuperaValorAnulacao(AlteracaoORC alteracaoORC) {
        BigDecimal valor = BigDecimal.ZERO;
        for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
            valor = valor.add(anulacaoORC.getValor());
        }
        return Util.formataValor(valor);
    }

    public String recuperaValorAnulacaoPorExtensao(AlteracaoORC alteracaoORC) {
        BigDecimal valor = BigDecimal.ZERO;
        for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
            valor = valor.add(anulacaoORC.getValor());
        }
        return ValorPorExtenso.valorPorExtenso(valor);
    }

    public void ordenar(AlteracaoORC entity) {
        ordenarSuplementacao(entity.getSuplementacoesORCs());
        ordenarAnulacao(entity.getAnulacoesORCs());
        ordenarSolicitacaoElementoDespesa(entity.getSolicitacoes());
    }

    private void ordenarSuplementacao(List<SuplementacaoORC> suplementacaoORCs) {
        Collections.sort(suplementacaoORCs, new Comparator<SuplementacaoORC>() {
            @Override
            public int compare(SuplementacaoORC o1, SuplementacaoORC o2) {
                String codigoOrgao1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(3, 6);
                String codigoOrgao2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(3, 6);

                String codigoUnid1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(7, 10);
                String codigoUnid2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(7, 10);

                String codigoContaDestinacao1 = o1.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo();
                String codigoContaDestinacao2 = o2.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo();

                String codigoContaDesp1 = o1.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
                String codigoContaDesp2 = o2.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();

                String codigoDespesaOrc1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(23, o1.getFonteDespesaORC().getDespesaORC().getCodigo().length());
                String codigoDespesaOrc2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(23, o2.getFonteDespesaORC().getDespesaORC().getCodigo().length());

                String ordena01 = codigoOrgao1 + codigoUnid1 + codigoDespesaOrc1 + codigoContaDestinacao1 + codigoContaDesp1;
                String ordena02 = codigoOrgao2 + codigoUnid2 + codigoDespesaOrc2 + codigoContaDestinacao2 + codigoContaDesp2;

                return ordena01.compareTo(ordena02);
            }
        });
    }

    public void ordenarAnulacao(List<AnulacaoORC> anulacaoORCs) {
        Collections.sort(anulacaoORCs, new Comparator<AnulacaoORC>() {
            @Override
            public int compare(AnulacaoORC o1, AnulacaoORC o2) {
                String codigoOrgao1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(3, 6);
                String codigoOrgao2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(3, 6);

                String codigoUnid1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(7, 10);
                String codigoUnid2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(7, 10);

                String codigoContaDestinacao1 = o1.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo();
                String codigoContaDestinacao2 = o2.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo();

                String codigoContaDesp1 = o1.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
                String codigoContaDesp2 = o2.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();

                String codigoDespesaOrc1 = o1.getFonteDespesaORC().getDespesaORC().getCodigo().substring(23, o1.getFonteDespesaORC().getDespesaORC().getCodigo().length());
                String codigoDespesaOrc2 = o2.getFonteDespesaORC().getDespesaORC().getCodigo().substring(23, o2.getFonteDespesaORC().getDespesaORC().getCodigo().length());

                String ordena01 = codigoOrgao1 + codigoUnid1 + codigoDespesaOrc1 + codigoContaDestinacao1 + codigoContaDesp1;
                String ordena02 = codigoOrgao2 + codigoUnid2 + codigoDespesaOrc2 + codigoContaDestinacao2 + codigoContaDesp2;

                return ordena01.compareTo(ordena02);
            }
        });
    }

    private void ordenarSolicitacaoElementoDespesa(List<SolicitacaoDespesaOrc> solicitacaoDespesaOrcs) {
        Collections.sort(solicitacaoDespesaOrcs, new Comparator<SolicitacaoDespesaOrc>() {
            @Override
            public int compare(SolicitacaoDespesaOrc o1, SolicitacaoDespesaOrc o2) {
                UnidadeOrganizacional uo1 = o1.getUnidadeOrganizacional();
                UnidadeOrganizacional uo2 = o2.getUnidadeOrganizacional();

                HierarquiaOrganizacional unidade1 = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", uo1, sistemaFacade.getDataOperacao());
                HierarquiaOrganizacional unidade2 = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", uo2, sistemaFacade.getDataOperacao());

                HierarquiaOrganizacional orgao1 = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidade1.getSuperior(), sistemaFacade.getDataOperacao());
                HierarquiaOrganizacional orgao2 = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidade2.getSuperior(), sistemaFacade.getDataOperacao());

                String codigiReduzido1 = o1.getAcaoPPA().getTipoAcaoPPA().getCodigo() + o1.getAcaoPPA().getCodigo() + o1.getSubAcaoPPA().getCodigo();
                String codigiReduzido2 = o2.getAcaoPPA().getTipoAcaoPPA().getCodigo() + o2.getAcaoPPA().getCodigo() + o2.getSubAcaoPPA().getCodigo();

                Conta codigoContaDesp1 = o1.getConta();
                Conta codigoContaDesp2 = o2.getConta();

                Conta codigoContaDestinacao1 = o1.getDestinacaoDeRecursos();
                Conta codigoContaDestinacao2 = o2.getDestinacaoDeRecursos();

                String ordena01 = orgao1.getCodigoNo() + unidade1.getCodigoNo() + codigiReduzido1 + codigoContaDestinacao1 + codigoContaDesp1;
                String ordena02 = orgao2.getCodigoNo() + unidade2.getCodigoNo() + codigiReduzido2 + codigoContaDestinacao2 + codigoContaDesp2;

                return ordena01.compareTo(ordena02);
            }
        });
    }

    public String montaMinutaAlteracaoOrcamentariaDiario(AlteracaoORC alteracaoORC) {

        alteracaoORC = alteracaoORCFacade.recuperar(alteracaoORC.getId());
        ordenar(alteracaoORC);

        String conteudoMinutaDiario = "<?xml version='1.0' encoding='iso-8859-1'?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>" +
            "<html> " +
            "<div style='font-size: 08px; font-family: Arial'>" +
            "<div style='text-align:left'> " +
            "PREFEITURA DE RIO BRANCO " +
            "</br>" +
            " Gabinete do Prefeito" +
            "</div> " +
            "</br>" +
            " " +
            "<div style='text-align:left'> " +
            "DECRETO Nº &nbsp;&nbsp; DE &nbsp;&nbsp; DE &nbsp;&nbsp;" +
            "DE " + sistemaFacade.getExercicioCorrente() +
            "</div>" +
            " " +
            " <p align=\"justify\"> " +
            "Abre crédito suplementar ao orçamento financeiro de " + sistemaFacade.getExercicioCorrente() + " e dá outras providências." +
            "</p>" +
            " " +
            "<div style='text-align:justify'>" +
            "O PREFEITO DO MUNICÍPIO DE RIO BRANCO, no uso das atribuições que lhe confere o art. 58º, incisos V e VII, c/c artigo 62, inciso I, da " +
            " Lei Orgânica do Município de Rio Branco, e com fulcro no artigo 6º da Lei Municipal n.º " +
            " 2.031 de 23 de dezembro de 2013. " +
            "</div>" +
            "DECRETA:" +
            "<div style='text-align:justify'>" +
            "Art. 1º - Fica aberto Crédito Suplementar no valor de " + recuperaValorSuplementacao(alteracaoORC) + "(" + recuperarValorSuplementacaoPorExtenso(alteracaoORC) + ") " +
            "ao Orçamento Municipal em vigor, para reforço da(s) dotação(ões) orçamentária(s), conforme a discriminação abaixo:" +
            "</div>" +
            " " +
            montarQuadroSuplementarDiario(alteracaoORC) +
            " " +
            "<div style='text-align:justify'>" +
            "Art. 2º - O Crédito Adicional Suplementar de que trata o artigo anterior, no valor de " + recuperaValorAnulacao(alteracaoORC) + " (" + recuperaValorAnulacaoPorExtensao(alteracaoORC) + "), " +
            "será compensado de acordo com anulação da(s) dotação(ões) orçamentária(s), nos termos do disposto no inciso III do parágrafo 1º do artigo 43 da Lei Federal nº " +
            "4.320 de 17 de março de 1964, conforme a seguir:" +
            "</div>" +
            " " +
            montarQuadroAnulacaoDiario(alteracaoORC) +
            " " +
            "<div style='text-align:justify'>" +
            "Art. 3º - Este Decreto entrará em vigor na data de sua publicação, revogadas as disposições em contrário." +
            "</div>" +
            " " +
            "<div style='text-align:justify'>" +
            "Rio Branco-Acre, &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; de &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
            " de " + sistemaFacade.getExercicioCorrente() + ", 126º da República, 112º do Tratado de Petrópolis, 53º do Estado do Acre e 131º do Munícipio de Rio Branco." +
            "</div>" +
            "</font>" +
            "</div>" +
            " " +
            "</html>";
        return conteudoMinutaDiario;
    }

    public String montaFichaSolicitacaoCredito(AlteracaoORC selecionado) {
        ordenar(selecionado);
        HierarquiaOrganizacional orcamentaria = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", selecionado.getUnidadeOrganizacionalOrc(), sistemaFacade.getDataOperacao());
        String orgao = "";
        if (orcamentaria != null) {
            HierarquiaOrganizacional paiDe = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getPaiDe(orcamentaria, sistemaFacade.getDataOperacao());
            if (paiDe != null) {
                orgao = paiDe.getDescricao();
            }
        }
        String conteudoFicha = "<?xml version='1.0' encoding='iso-8859-1'?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>" +
            "<html> " +
            "<style> " +
            "table,th,td " +
            "{ " +
            "border:1px solid black; " +
            "border-collapse:collapse " +
            "}" +
            "</style> " +
            "<div style='text-align:right; font-size: 12px'>" +
            " Exercício: " + selecionado.getExercicio() +
            "</div> " +
            "</br>" +
            "<div style='border: 1px solid black;text-align:center'> " +
            "<b> PREFEITURA DE RIO BRANCO </br> " +
            "SECRETARIA MUNICIPAL DE PLANEJAMENTO </b> " +
            "</div> " +
            " " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;text-align:center'> " +
            "<b>FICHA DE SOLICITAÇÃO DE CRÉDITO</b> " +
            "</div> " +
            " " +
            "<div style='border: 1px solid black;text-align:center;border-top: 0px'>" +
            "<table border='1' style='width:100%;text-align: center;'>" +
            "<tr>" +
            "<td rowspan=\"2\"> " + montaCheckBoxTipoDespesaOrc(selecionado) + "</td>" +
            "<td> <b> Número do Pedido </b> </td>" +
            "</tr>" +
            "<tr>" +
            "<td rowspan=\"1\"> " + Util.zerosAEsquerda(selecionado.getCodigo(), 4) + " </td>" +
            "</tr>" +
            "</table>" +
            "</div> " +
            " " +
            "</br> " +
            "<div style='border: 1px solid black;'> " +
            "<b>01. IDENTIFICAÇÃO</b> " +
            "</div> " +
            "<div style='border: 1px solid black;text-align:center;border-top: 0px'> " +
            orgao + "</br>" +
            "<b>" + getDescricaoHierarquia(selecionado.getUnidadeOrganizacionalOrc()) + "</b> " +
            "<div style='border-top: 1px solid black;'>" +
            "</div>" +
            "</div> " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>02. JUSTIFICATIVA DA SUPLEMENTAÇÃO PRETENDIDA</b> " +
            "</div> " +
            "<div style='border: 1px solid black;border-top: 0px'> " +
            selecionado.getJustificativaSuplementacao() +
            "</div> " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>03. JUSTIFICATIVA DA ANULAÇÃO PROPOSTA</b> " +
            "</div> " +
            "<div style='border: 1px solid black;border-top: 0px'> " +
            selecionado.getJustificativaAnulacao() +
            "</div> " +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>04. QUADRO DE DETALHAMENTO DA DOTAÇÃO A SER ANULADA</b> " +
            "</div> " +
            montarQuadroAnulacao(selecionado) +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>05. QUADRO DE DETALHAMENTO DA DOTAÇÃO A SER SUPLEMENTADA</b> " +
            "</div> " +
            montarQuadroSuplementacao(selecionado) +
            "</br> " +
            " " +
            " " +
            "<div style='border: 1px solid black;'> " +
            "<b>06. QUADRO DE DETALHAMENTO DA DOTAÇÃO A SER CRIADA</b> " +
            "</div> " +
            montarQuadroDotacaoNovas(selecionado) +
            "</br> " +
            " " +
            "<div style='border: 1px solid black;height:100px;'> " +
            " <b>VISTO</b> " +
            "</div> " +
            " " +
            "<div style='position: fixed; bottom: 0; min-width: 100%;font-size: 11px'> " +
            "Emitido por: " + sistemaFacade.getUsuarioCorrente() + ", em " + DataUtil.getDataFormatada(new Date()) + " às " + Util.hourToString(new Date()) +
            "</div> " +
            "</html>";
        return conteudoFicha;
    }

    private String montaCheckBoxTipoDespesaOrc(AlteracaoORC selecionado) {
        if (selecionado.getTipoDespesaORC() != null) {
            StringBuilder retorno = new StringBuilder();
            if (selecionado.getTipoDespesaORC().equals(TipoDespesaORC.SUPLEMENTAR)) {
                retorno.append("[X] Crédito Suplementar ");
                retorno.append("[ ] Crédito Especial ");
                retorno.append("[ ] Crédito Extraordinário ");
            } else if (selecionado.getTipoDespesaORC().equals(TipoDespesaORC.ESPECIAL)) {
                retorno.append("[ ] Crédito Suplementar ");
                retorno.append("[X] Crédito Especial ");
                retorno.append("[ ] Crédito Extraordinário ");
            } else if (selecionado.getTipoDespesaORC().equals(TipoDespesaORC.EXTRAORDINARIA)) {
                retorno.append("[ ] Crédito Suplementar ");
                retorno.append("[ ] Crédito Especial ");
                retorno.append("[X] Crédito Extraordinário ");
            }
            return retorno.toString();
        }
        return "Tipo de Crédito não Informado";
    }

    private String montarQuadroDotacaoNovas(AlteracaoORC selecionado) {
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder retorno = new StringBuilder();
        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        retorno.append("<tr>");
        retorno.append("<td colspan=\"3\" > COD. PROJ./ATIVIDADE</td>");
        retorno.append("<td rowspan=\"2\"> FONTE</td>");
        retorno.append("<td rowspan=\"2\"> NATUREZA DE DESPESA</td>");
        retorno.append("<td rowspan=\"2\"> MÊS</td>");
        retorno.append("<td rowspan=\"2\"> TOTAL</td>");
        retorno.append("</tr>");
        retorno.append("<tr>");
        retorno.append("<td> ÓRGÃO</td>");
        retorno.append("<td> UNID</td>");
        retorno.append("<td> REDUZIDO</td>");
        retorno.append("</tr>");

        if (selecionado.getSolicitacoes().isEmpty()) {
            retorno.append("<tr>");
            retorno.append("<td> Nenhum Solicitação informada.</td>");
            retorno.append("</tr>");
        }
        for (SolicitacaoDespesaOrc solicitacaoDespesaOrc : selecionado.getSolicitacoes()) {
            UnidadeOrganizacional uo = solicitacaoDespesaOrc.getUnidadeOrganizacional();
            HierarquiaOrganizacional unidade = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", uo, sistemaFacade.getDataOperacao());
            HierarquiaOrganizacional orgao = alteracaoORCFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidade.getSuperior(), sistemaFacade.getDataOperacao());
            Conta conta = solicitacaoDespesaOrc.getConta();
            Conta contaDestinacao = solicitacaoDespesaOrc.getDestinacaoDeRecursos();
            retorno.append("<tr>");
            retorno.append("<td> " + orgao.getCodigoNo() + "</td>");
            retorno.append("<td> " + unidade.getCodigoNo() + "</td>");
            retorno.append("<td> " + solicitacaoDespesaOrc.getAcaoPPA().getTipoAcaoPPA().getCodigo() + solicitacaoDespesaOrc.getAcaoPPA().getCodigo() + solicitacaoDespesaOrc.getSubAcaoPPA().getCodigo() + "</td>");
            retorno.append("<td> " + ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().getCodigo() + "</td>");
            retorno.append("<td> " + conta.getCodigo() + "</td>");
            retorno.append("<td> " + " - " + "</td>");
            retorno.append("<td> ").append(solicitacaoDespesaOrc.getDeferidaEm() == null ? Util.formataValor(solicitacaoDespesaOrc.getValor()) : " - ").append("</td>");
            retorno.append("</tr>");
            if (solicitacaoDespesaOrc.getDeferidaEm() == null) {
                total = total.add(solicitacaoDespesaOrc.getValor());
            }
        }

        retorno.append("<tr>");
        retorno.append("<td colspan=\"6\"> <b>TOTAL</b></td>");
        retorno.append("<td> <b>").append(total.compareTo(BigDecimal.ZERO) != 0 ? Util.formataValor(total) : " - ").append("</b></td>");
        retorno.append("</tr>");
        retorno.append("</table>");
        return retorno.toString();
    }

    private String montarQuadroAnulacao(AlteracaoORC selecionado) {

        BigDecimal total = BigDecimal.ZERO;
        StringBuilder retorno = new StringBuilder();
        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        retorno.append("<tr>");
        retorno.append("<td colspan=\"3\"> COD. PROJ./ATIVIDADE</td>");
        retorno.append("<td rowspan=\"2\"> FONTE</td>");
        retorno.append("<td rowspan=\"2\"> NATUREZA DE DESPESA</td>");
        retorno.append("<td rowspan=\"2\"> MÊS</td>");
        retorno.append("<td rowspan=\"2\"> TOTAL</td>");
        retorno.append("</tr>");
        retorno.append("<tr>");
        retorno.append("<td> ÓRGÃO</td>");
        retorno.append("<td> UNID</td>");
        retorno.append("<td> REDUZIDO</td>");
        retorno.append("</tr>");

        if (selecionado.getAnulacoesORCs().isEmpty()) {
            retorno.append("<tr>");
            retorno.append("<td> Nenhuma Anulação informada.</td>");
            retorno.append("</tr>");
        }

        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            DespesaORC despesaORC = anulacaoORC.getFonteDespesaORC().getDespesaORC();
            Conta conta = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
            Conta contaDestinacao = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            retorno.append("<tr>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(3, 6) + "</td>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(7, 10) + "</td>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(23, despesaORC.getCodigo().length()).replace(".", "") + "</td>");
            retorno.append("<td> " + ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().getCodigo() + "</td>");
            retorno.append("<td> " + conta.getCodigo() + "</td>");
            retorno.append("<td> " + DataUtil.getDescricaoMes(anulacaoORC.getMes()) + "</td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(anulacaoORC.getValor()) + "</div></td>");
            retorno.append("</tr>");
            total = total.add(anulacaoORC.getValor());
        }

        retorno.append("<tr>");
        retorno.append("<td colspan=\"6\"> <b>TOTAL</b></td>");
        retorno.append("<td> <div style='text-align: right'><b>" + Util.formataValor(total) + "</b></div></td>");
        retorno.append("</tr>");
        retorno.append("</table>");
        return retorno.toString();
    }

    private String montarQuadroSuplementacao(AlteracaoORC selecionado) {
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder retorno = new StringBuilder();
        retorno.append("<table border='1' style='width:100%;text-align: center;border-top: 0px'>");
        retorno.append("<tr>");
        retorno.append("<td colspan=\"3\" > COD. PROJ./ATIVIDADE</td>");
        retorno.append("<td rowspan=\"2\"> FONTE</td>");
        retorno.append("<td rowspan=\"2\"> NATUREZA DE DESPESA</td>");
        retorno.append("<td rowspan=\"2\"> MÊS</td>");
        retorno.append("<td rowspan=\"2\"> TOTAL</td>");
        retorno.append("</tr>");
        retorno.append("<tr>");
        retorno.append("<td> ÓRGÃO</td>");
        retorno.append("<td> UNID</td>");
        retorno.append("<td> REDUZIDO</td>");
        retorno.append("</tr>");

        if (selecionado.getSuplementacoesORCs().isEmpty()) {
            retorno.append("<tr>");
            retorno.append("<td> Nenhuma Suplementação Informada.</td>");
            retorno.append("</tr>");
        }
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            DespesaORC despesaORC = suplementacaoORC.getFonteDespesaORC().getDespesaORC();
            Conta conta = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
            Conta contaDestinacao = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            retorno.append("<tr>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(3, 6) + "</td>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(7, 10) + "</td>");
            retorno.append("<td> " + despesaORC.getCodigo().substring(23, despesaORC.getCodigo().length()).replace(".", "") + "</td>");
            retorno.append("<td> " + ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().getCodigo() + "</td>");
            retorno.append("<td> " + conta.getCodigo() + "</td>");
            retorno.append("<td> " + DataUtil.getDescricaoMes(suplementacaoORC.getMes()) + "</td>");
            retorno.append("<td> <div style='text-align: right'>" + Util.formataValor(suplementacaoORC.getValor()) + "</div></td>");
            retorno.append("</tr>");
            total = total.add(suplementacaoORC.getValor());
        }

        retorno.append("<tr>");
        retorno.append("<td colspan=\"6\"> <b>TOTAL</b></td>");
        retorno.append("<td> <div style='text-align: right'><b>" + Util.formataValor(total) + "</b></div></td>");
        retorno.append("</tr>");
        retorno.append("</table>");
        return retorno.toString();
    }

    public AlteracaoORCFacade getAlteracaoORCFacade() {
        return alteracaoORCFacade;
    }

    public void setAlteracaoORCFacade(AlteracaoORCFacade alteracaoORCFacade) {
        this.alteracaoORCFacade = alteracaoORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public String getDescricaoHierarquia(UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        if (hierarquiaOrganizacional != null) {
            return hierarquiaOrganizacional.getDescricao();
        }
        return unidadeOrganizacional.getDescricao();
    }
}

