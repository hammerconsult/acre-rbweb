package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.CalculoIptuBean;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.script.ScriptEngine;
import java.io.Serializable;
import java.math.BigDecimal;

public class SimuladorIPTU implements Serializable {

    public static void simulaFormulas(CalculoIptuBean calculoIptuBean,
                                      CadastroImobiliario exemplo,
                                      ScriptEngine engine,
                                      BigDecimal total,
//                                      ConfiguracaoIPTU configuracaoIPTU,
//                                      List<ConfiguracaoIPTUItem> itens,
                                      Construcao construcaoExemplo,
                                      AtributoFacade atributoFacade) {

        if (exemplo == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:simular",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar",
                            "Para Realizar uma Simulação, Escolha um Cadastro Imobiliario e uma Construção de Exemplo"));
        } else {
            try {
//                engine.eval(configuracaoIPTU.getBibliotecaFormulas());
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage("Formulario:simular",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Validar Biblioteca de Fórmulas", ex.getMessage()));
            } finally {
                total = BigDecimal.ZERO;

                if (construcaoExemplo == null) {
//                    construcaoExemplo = calculoIptuBean.criaConstrucaoDummy(exemplo);
                }
                atributoFacade.completaAtributos(construcaoExemplo.getAtributos(), ClasseDoAtributo.CONSTRUCAO);
//                for (ConfiguracaoIPTUItem item : itens) {
//                    if ((item.getFormula() != null) && (!"".equals(item.getFormula().trim()))) {
//                        ResultadoCalculo resultadoCalculo = calculoIptuBean.avaliaTaxa(item.getFormula(), configuracaoIPTU.getBibliotecaFormulas(), construcaoExemplo);
//                        item.setResultado(resultadoCalculo);
//                        if (item.getTributo() != null && item.getTributo().getId() != null) {
//                            total = total.add(item.getResultado().getValor());
//                        }
//                    }
//                }
//            }
        }
        }
    }
}
