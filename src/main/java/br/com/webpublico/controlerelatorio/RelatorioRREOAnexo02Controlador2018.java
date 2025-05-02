/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.SubFuncao;
import br.com.webpublico.entidadesauxiliares.RREOAnexo02Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo02Calculator2018;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo2-2018", pattern = "/relatorio/rreo/anexo2/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo02-2018.xhtml")})
public class RelatorioRREOAnexo02Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRREOAnexo02Calculator2018 relatorioRREOAnexo02Calculator;
    private String mesInicial;
    private String mesFinal;
    private BigDecimal totalFuncaoIntra;
    private BigDecimal totalFuncaoExcetoIntra;

    public RelatorioRREOAnexo02Controlador2018() {
    }

    @URLAction(mappingId = "relatorio-rreo-anexo2-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.mesInicial = null;
        this.mesFinal = null;
        totalFuncaoExcetoIntra = BigDecimal.ZERO;
        totalFuncaoIntra = BigDecimal.ZERO;
    }

    public List<RREOAnexo02Item> preparaDadosParaEmissao() {
        List<RREOAnexo02Item> toReturn = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalEmpenhadas = BigDecimal.ZERO;
        List<Funcao> resultado = relatorioRREOAnexo02Calculator.buscaCodigoFuncao(sistemaControlador.getExercicioCorrente(), " NOT ");
        for (Funcao funcao : resultado) {
            total = total.add(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            totalEmpenhadas = totalEmpenhadas.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
        }
        for (Funcao funcao : resultado) {
            List<SubFuncao> subFuncao = relatorioRREOAnexo02Calculator.buscaSubFuncao(funcao.getId(), " NOT ");
            RREOAnexo02Item item = new RREOAnexo02Item();
            item.setDescricao(funcao.getCodigo() + " - " + funcao.getDescricao());
            item.setNivel(1);
            item.setDotacaoInicial(recuperaDotacaoInicialAnexo02(sistemaControlador.getExercicioCorrente(), funcao, " NOT "));
            item.setDotacaoAtualizada(item.getDotacaoInicial().add(recuperaDotacaoAtualizadaAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT ")));
            item.setDespesaEmpenhadasNoBimestre(recuperaDespesasEmpenhadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            item.setDespesaEmpenhadasAteOBimestre(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            item.setDespesaLiquidadaNoBimestre(recuperaDespesasLiquidadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getId(), " NOT "));
            item.setDespesaLiquidadaAteOBimestre(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
            if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
            }

            if (!total.equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }

            if (!totalEmpenhadas.equals(BigDecimal.ZERO)) {
                item.setDespesaEmpenhadasAteOBimestrePercentualTotal(item.getDespesaEmpenhadasAteOBimestre().divide(totalEmpenhadas, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaEmpenhadasAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }
            if (mesFinal.equals("12")) {
                item.setInscritasEmRestosAPagar(recuperaRestosAPagarFuncao(sistemaControlador.getExercicioCorrente(), funcao, " NOT "));
//                if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentual((item.getDespesaLiquidadaAteOBimestre()).divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                }
//
//                if (!total.equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(total, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                }
//            } else {
//                item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
//                if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                }
//                if (!total.equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                }
            }
            totalFuncaoExcetoIntra = totalFuncaoExcetoIntra.add(item.getDespesaLiquidadaAteOBimestre());
            toReturn.add(item);
            if (subFuncao != null) {
                for (SubFuncao sf : subFuncao) {
                    item = new RREOAnexo02Item();
                    item.setDescricao("      " + funcao.getCodigo() + "." + sf.getCodigo() + " - " + sf.getDescricao());
                    item.setNivel(2);
                    item.setDotacaoInicial(recuperaDotacaoInicialSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), funcao, sf, " NOT "));
                    item.setDotacaoAtualizada(item.getDotacaoInicial().add(recuperaDotacaoAtualizadaSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, " NOT ")));
                    item.setDespesaEmpenhadasNoBimestre(recuperaDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, " NOT "));
                    item.setDespesaEmpenhadasAteOBimestre(recuperaDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, " NOT "));
                    item.setDespesaLiquidadaNoBimestre(recuperaDespesasLiquidadasNoBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, " NOT "));
                    item.setDespesaLiquidadaAteOBimestre(recuperaDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, " NOT "));

                    item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
                    if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
                        item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
                    }

                    if (!total.equals(BigDecimal.ZERO)) {
                        item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
                    }

                    if (!totalEmpenhadas.equals(BigDecimal.ZERO)) {
                        item.setDespesaEmpenhadasAteOBimestrePercentualTotal(item.getDespesaEmpenhadasAteOBimestre().divide(totalEmpenhadas, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaEmpenhadasAteOBimestrePercentualTotal(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setInscritasEmRestosAPagar(recuperaRestosAPagarSubFuncao(sistemaControlador.getExercicioCorrente(), funcao, sf, " NOT "));
//                        if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentual((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                        }
//                        if (!total.equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                        }
//                    } else {
//                        item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
//                        if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                        }
//                        if (!total.equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                        }
                    }
                    if (verificaPodeAdicionar(item)) {
                        toReturn.add(item);
                    }
                }
            }
        }
        toReturn = ajustaPercentualExcetoIntra(toReturn);
        return toReturn;
    }

    public List<RREOAnexo02Item> preparaDadosParaEmissaoIntra() {
        List<RREOAnexo02Item> toReturn = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalEmpenhadas = BigDecimal.ZERO;
        List<Funcao> resultado = relatorioRREOAnexo02Calculator.buscaCodigoFuncao(sistemaControlador.getExercicioCorrente(), "  ");
        for (Funcao funcao : resultado) {
            total = total.add(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            totalEmpenhadas = totalEmpenhadas.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " "));
        }
        for (Funcao funcao : resultado) {
            List<SubFuncao> subFuncao = relatorioRREOAnexo02Calculator.buscaSubFuncao(funcao.getId(), "  ");
            RREOAnexo02Item item = new RREOAnexo02Item();
            item.setDescricao(funcao.getCodigo() + " - " + funcao.getDescricao());
            item.setNivel(1);
            item.setDotacaoInicial(recuperaDotacaoInicialAnexo02(sistemaControlador.getExercicioCorrente(), funcao, "  "));
            BigDecimal atualizada = recuperaDotacaoAtualizadaAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  ");
            item.setDotacaoAtualizada(item.getDotacaoInicial().add(atualizada));
            item.setDespesaEmpenhadasNoBimestre(recuperaDespesasEmpenhadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            item.setDespesaEmpenhadasAteOBimestre(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            item.setDespesaLiquidadaNoBimestre(recuperaDespesasLiquidadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getId(), "  "));
            item.setDespesaLiquidadaAteOBimestre(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
            if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
            }

            if (!total.equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }

            if (!totalEmpenhadas.equals(BigDecimal.ZERO)) {
                item.setDespesaEmpenhadasAteOBimestrePercentualTotal(item.getDespesaEmpenhadasAteOBimestre().divide(totalEmpenhadas, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaEmpenhadasAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }
            if (mesFinal.equals("12")) {
                item.setInscritasEmRestosAPagar(recuperaRestosAPagarFuncao(sistemaControlador.getExercicioCorrente(), funcao, "  "));
//                if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentual((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                }
//
//                if (!total.equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(total, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                }
//            } else {
//                item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
//                if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                }
//
//                if (!total.equals(BigDecimal.ZERO)) {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                } else {
//                    item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                }
            }
            totalFuncaoIntra = totalFuncaoIntra.add(item.getDespesaLiquidadaAteOBimestre());

            toReturn.add(item);
            if (subFuncao != null) {
                for (SubFuncao sf : subFuncao) {
                    item = new RREOAnexo02Item();
                    item.setDescricao("      " + funcao.getCodigo() + "." + sf.getCodigo() + " - " + sf.getDescricao());
                    item.setNivel(2);
                    item.setDotacaoInicial(recuperaDotacaoInicialSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), funcao, sf, "  "));
                    item.setDotacaoAtualizada(item.getDotacaoInicial().add(recuperaDotacaoAtualizadaSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, "  ")));
                    item.setDespesaEmpenhadasNoBimestre(recuperaDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, "  "));
                    item.setDespesaEmpenhadasAteOBimestre(recuperaDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, "  "));
                    item.setDespesaLiquidadaNoBimestre(recuperaDespesasLiquidadasNoBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, "  "));
                    item.setDespesaLiquidadaAteOBimestre(recuperaDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(sistemaControlador.getExercicioCorrente(), sf.getCodigo(), funcao, "  "));
                    item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
                    if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
                        item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
                    }

                    if (!total.equals(BigDecimal.ZERO)) {
                        item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
                    }

                    if (!totalEmpenhadas.equals(BigDecimal.ZERO)) {
                        item.setDespesaEmpenhadasAteOBimestrePercentualTotal(item.getDespesaEmpenhadasAteOBimestre().divide(totalEmpenhadas, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        item.setDespesaEmpenhadasAteOBimestrePercentualTotal(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setInscritasEmRestosAPagar(recuperaRestosAPagarSubFuncao(sistemaControlador.getExercicioCorrente(), funcao, sf, " "));
//                        if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentual((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                        }
//                        if (!total.equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal((item.getDespesaLiquidadaAteOBimestre().add(item.getInscritasEmRestosAPagar())).divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                        }
//                    } else {
//                        item.setInscritasEmRestosAPagar(BigDecimal.ZERO);
//                        if (!item.getDotacaoAtualizada().equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(item.getDespesaLiquidadaAteOBimestre().divide(item.getDotacaoAtualizada(), 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentual(BigDecimal.ZERO);
//                        }
//                        if (!total.equals(BigDecimal.ZERO)) {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(total, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
//                        } else {
//                            item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
//                        }
                    }
                    if (verificaPodeAdicionar(item)) {
                        toReturn.add(item);
                    }
                }
            }
        }
        toReturn = ajustaPercentualIntra(toReturn);
        return toReturn;
    }

    private List<RREOAnexo02Item> ajustaPercentualIntra(List<RREOAnexo02Item> lista) {
        for (RREOAnexo02Item item : lista) {
            if (!totalFuncaoIntra.equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(totalFuncaoIntra, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }
        }
        return lista;
    }

    private List<RREOAnexo02Item> ajustaPercentualExcetoIntra(List<RREOAnexo02Item> lista) {
        for (RREOAnexo02Item item : lista) {
            if (!totalFuncaoExcetoIntra.equals(BigDecimal.ZERO)) {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(item.getDespesaLiquidadaAteOBimestre().divide(totalFuncaoExcetoIntra, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            } else {
                item.setDespesaLiquidadaAteOBimestrePercentualTotal(BigDecimal.ZERO);
            }
        }
        return lista;
    }

    private String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "JANEIRO";
                break;
            case "02":
                toReturn = "FEVEREIRO";
                break;
            case "03":
                toReturn = "MARÇO";
                break;
            case "04":
                toReturn = "ABRIL";
                break;
            case "05":
                toReturn = "MAIO";
                break;
            case "06":
                toReturn = "JUNHO";
                break;
            case "07":
                toReturn = "JULHO";
                break;
            case "08":
                toReturn = "AGOSTO";
                break;
            case "09":
                toReturn = "SETEMBRO";
                break;
            case "10":
                toReturn = "OUTUBRO";
                break;
            case "11":
                toReturn = "NOVEMBRO";
                break;
            case "12":
                toReturn = "DEZEMBRO";
                break;
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDadosParaEmissao());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
    public void salvarRelatorio() {
        try {
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDadosParaEmissao());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 2 - Demonstrativo da Execução das Despesas por Função/Subfunção");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        String arquivoJasper;
        if (mesFinal.equals("12")) {
            arquivoJasper = "RelatorioRREOAnexo022018SextoBimestre.jasper";
        } else {
            arquivoJasper = "RelatorioRREOAnexo022018.jasper";
        }
        return arquivoJasper;
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal));
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());

        BigDecimal dotacaoAtualizada = recuperaDotacaoAtualizadaAnexo02(sistemaControlador.getExercicioCorrente(), null, "");
        BigDecimal dotacaoInicial = recuperaDotacaoInicialAnexo02(sistemaControlador.getExercicioCorrente(), null, "");
        BigDecimal despesasLiquidadasAteBimestre = recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), null, "");
        BigDecimal despesasEmpenhadasAteBimestre = recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), null, "");

        parameters.put("DOTACAOINICIAL", dotacaoInicial);
        parameters.put("DOTACAOATUALIZADA", dotacaoInicial.add(dotacaoAtualizada));
        parameters.put("EMPENHADANOBIMESTRE", recuperaDespesasEmpenhadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), null, ""));
        parameters.put("EMPENHADAATEBIMESTRE", recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), null, ""));
        parameters.put("LIQUIDADANOBIMESTRE", recuperaDespesasLiquidadasNoBimestreAnexo02(sistemaControlador.getExercicioCorrente(), null, ""));
        parameters.put("LIQUIDADAATEBIMESTRE", despesasLiquidadasAteBimestre);
        if (dotacaoAtualizada.compareTo(BigDecimal.ZERO) != 0) {
            parameters.put("LIQUIDADAPERC", despesasLiquidadasAteBimestre.divide(dotacaoAtualizada, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        } else {
            parameters.put("LIQUIDADAPERC", BigDecimal.ZERO);
        }
        if (despesasLiquidadasAteBimestre.compareTo(BigDecimal.ZERO) != 0) {
            parameters.put("LIQUIDADAPERCTOTAL", despesasLiquidadasAteBimestre.divide(despesasLiquidadasAteBimestre, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        } else {
            parameters.put("LIQUIDADAPERCTOTAL", BigDecimal.ZERO);

        }
        if (despesasEmpenhadasAteBimestre.compareTo(BigDecimal.ZERO) != 0) {
            parameters.put("EMPENHADAPERCTOTAL", despesasEmpenhadasAteBimestre.divide(despesasEmpenhadasAteBimestre, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        } else {
            parameters.put("EMPENHADAPERCTOTAL", BigDecimal.ZERO);
        }
        parameters.put("SALDOALIQUIDAR", dotacaoInicial.add(dotacaoAtualizada).subtract(despesasLiquidadasAteBimestre));
        parameters.put("INSCRITASRESTOPAGAR", recuperaRestosAPagarFuncao(sistemaControlador.getExercicioCorrente(), null, ""));

        parameters.put("INTRA", preparaDadosParaEmissaoIntra());
        parameters.put("SUBREPORT_DIR", getCaminho());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        return parameters;
    }

    private Boolean verificaPodeAdicionar(RREOAnexo02Item item) {
        Boolean controle = false;
        if (item.getDotacaoInicial().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getDespesaEmpenhadasNoBimestre().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getDespesaEmpenhadasAteOBimestre().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getDespesaLiquidadaNoBimestre().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getDespesaLiquidadaAteOBimestre().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        if (!controle && item.getInscritasEmRestosAPagar().compareTo(BigDecimal.ZERO) != 0) {
            controle = true;
        }
        return controle;
    }


    private BigDecimal recuperaDotacaoInicialAnexo02(Exercicio exercicioCorrente, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDotacaoInicialAnexo02(exercicioCorrente, funcao, clausula);
    }

    private BigDecimal recuperaDotacaoAtualizadaAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDotacaoAtualizadaAnexo02(exercicioCorrente, codigo, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasEmpenhadasNoBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasEmpenhadasNoBimestreAnexo02(exercicioCorrente, codigo, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasEmpenhadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasEmpenhadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasNoBimestreAnexo02(Exercicio exercicioCorrente, Long id, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasLiquidadasNoBimestreAnexo02(exercicioCorrente, id, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasLiquidadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaRestosAPagarFuncao(Exercicio exercicioCorrente, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularRestoAPagarNaoProcessadosFuncao(exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    //SubFunção
    private BigDecimal recuperaDotacaoInicialSubFuncaoAnexo02(Exercicio exercicioCorrente, Funcao funcao, SubFuncao sf, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDotacaoInicialSubFuncaoAnexo02(exercicioCorrente, funcao, sf, clausula);
    }

    private BigDecimal recuperaDotacaoAtualizadaSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDotacaoAtualizadaSubFuncaoAnexo02(exercicioCorrente, codigo, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    private BigDecimal recuperaDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(exercicioCorrente, codigo, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    private BigDecimal recuperaDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasLiquidadasNoBimestreSubFuncaoAnexo02(exercicioCorrente, codigo, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, Funcao funcao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, clausula);
    }

    private BigDecimal recuperaRestosAPagarSubFuncao(Exercicio exercicioCorrente, Funcao funcao, SubFuncao subFuncao, String clausula) {
        return relatorioRREOAnexo02Calculator.calcularRestoAPagarNaoProcessadosSubFuncao(exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), funcao, subFuncao, clausula);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
        Integer mes = Integer.parseInt(this.mesFinal);
        if (mes == 12) {
            this.mesInicial = "11";
        } else {
            this.mesInicial = "0" + (mes - 1);
        }
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }
}
