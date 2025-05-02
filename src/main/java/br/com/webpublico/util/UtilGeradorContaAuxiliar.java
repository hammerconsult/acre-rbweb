package br.com.webpublico.util;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SubSistema;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class UtilGeradorContaAuxiliar {

    private final static Logger logger = LoggerFactory.getLogger(UtilGeradorContaAuxiliar.class);

    private static HashMap<UnidadeOrganizacional, Entidade> mapEntidades = Maps.newHashMap();

    private static HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        try {
            return (HierarquiaOrganizacionalFacade) new InitialContext().lookup("java:module/HierarquiaOrganizacionalFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
        return null;
    }

    private static EntidadeFacade getEntidadeFacade() {
        try {
            return (EntidadeFacade) new InitialContext().lookup("java:module/EntidadeFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
        return null;
    }

    private static ContaFacade getContaFacade() {
        try {
            return (ContaFacade) new InitialContext().lookup("java:module/ContaFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
        return null;
    }

    //PO
    //Poder/Órgão
    public static TreeMap gerarContaAuxiliar1(UnidadeOrganizacional unidadeOrganizacional) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional), "");
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada1(UnidadeOrganizacional unidadeOrganizacional) {
        TreeMap contaAuxiliarDetalhada = new TreeMap();
        contaAuxiliarDetalhada.put(getPoderOrgaoSiconfi(unidadeOrganizacional), new ContaAuxiliarDetalhada(unidadeOrganizacional));
        return contaAuxiliarDetalhada;
    }

    //PO - FP
    //Indicador do Superávit Financeiro – FP
    //1 - Financeiro
    //2 - Permanente
    //Indicado no plano de conta contábil no atributo subsistema.
    public static TreeMap gerarContaAuxiliar2(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar1(unidadeOrganizacional));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente), "");
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada2(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente) {
        TreeMap contaAuxiliarDetalhada = new TreeMap();
        contaAuxiliarDetalhada.putAll(gerarContaAuxiliarDetalhada1(unidadeOrganizacional));
        contaAuxiliarDetalhada.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente), new ContaAuxiliarDetalhada(unidadeOrganizacional, financeiroPermanente));
        return contaAuxiliarDetalhada;
    }

    //PO - FP - DC
    //Dívida Consolidada – DC é equivalente à dívida pública e precatório.
    //Indicado na relação de contas contábeis, atributo igual a ‘0’
    public static TreeMap gerarContaAuxiliar3(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Integer dividaConsolidada) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar2(unidadeOrganizacional, financeiroPermanente));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + dividaConsolidada, "");
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada3(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Integer dividaConsolidada) {
        TreeMap contaAuxiliarDetalhada = new TreeMap();
        contaAuxiliarDetalhada.putAll(gerarContaAuxiliarDetalhada2(unidadeOrganizacional, financeiroPermanente));
        contaAuxiliarDetalhada.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + dividaConsolidada, new ContaAuxiliarDetalhada(unidadeOrganizacional, financeiroPermanente, dividaConsolidada));
        return contaAuxiliarDetalhada;
    }

    //até 2021 PO - FP - FR
    //após 2021 PO - FP - FR - CO
    public static TreeMap gerarContaAuxiliar4(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Conta contaDeDestinacao) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar2(unidadeOrganizacional, financeiroPermanente));
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + contaDeDestinacao.getCodigoSICONFI(), "");
        } else {
            String codigoFonteDeRecurso = getCodigoFonteDeRecurso(contaDeDestinacao);
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + codigoFonteDeRecurso, "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), "");
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + codigoFonteDeRecurso, "");
            }
        }
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada4(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Conta contaDeDestinacao, Exercicio exercicioAtual) {
        TreeMap contaAuxiliarDetalhada = new TreeMap();
        contaAuxiliarDetalhada.putAll(gerarContaAuxiliarDetalhada2(unidadeOrganizacional, financeiroPermanente));
        contaAuxiliarDetalhada.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(contaDeDestinacao, financeiroPermanente, exercicioAtual, unidadeOrganizacional));
        return contaAuxiliarDetalhada;
    }

    //até 2021 PO - FR
    //aṕos 2021 PO - FR - CO
    public static TreeMap gerarContaAuxiliar5(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar1(unidadeOrganizacional));
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigoSICONFI(), "");
        } else {
            String codigoFonteDeRecurso = getCodigoFonteDeRecurso(contaDeDestinacao);
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso, "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), "");
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso, "");
            }
        }
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada5(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, Exercicio exercicioAtual) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliarDetalhada1(unidadeOrganizacional));
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(contaDeDestinacao, exercicioAtual, unidadeOrganizacional));
        } else {
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(contaDeDestinacao, exercicioAtual, unidadeOrganizacional));
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf().replace(".", ""), new ContaAuxiliarDetalhada(contaDeDestinacao, exercicioAtual, unidadeOrganizacional, ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf()));
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(contaDeDestinacao, exercicioAtual, unidadeOrganizacional));
            }
        }
        return contaAuxiliar;
    }

    //até 2021 PO - FR - NR
    //Natureza da Receita – NR – 08 dígitos - Indicado no plano de conta de receita no atributo Código SICONFI.
    //após 2021 PO - FR - CO - NR
    public static TreeMap gerarContaAuxiliar6(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, String naturezaReceita) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar5(unidadeOrganizacional, contaDeDestinacao));
        String codigoContaReceita = getCodigoContaDespesaReceita(naturezaReceita);
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigoSICONFI() + "." + codigoContaReceita, "");
        } else {
            String codigoFonteDeRecurso = getCodigoFonteDeRecurso(contaDeDestinacao);
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() + "." + codigoContaReceita, "");
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + codigoFonteDeRecurso + "." + codigoContaReceita, "");
            }
        }
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada6(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, Conta conta, Exercicio exercicioAtual) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliarDetalhada5(unidadeOrganizacional, contaDeDestinacao, exercicioAtual));
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, contaDeDestinacao, conta, exercicioAtual));
        } else {
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf().replace(".", "") + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, contaDeDestinacao, conta, exercicioAtual, ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf()));
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, contaDeDestinacao, conta, exercicioAtual));
            }
        }
        return contaAuxiliar;
    }

    //até 2021 PO - FS - FR - ND - ES
    //aṕos 2021 PO - FS - FR - CO - ND
    //Natureza da Despesa – ND – 08 dígitos - Indicado no plano de conta de despesa no atributo Código SICONFI.
    //Classificação Funcional – FS - Função/SubFunção
    //ES  =    Despesas com MDE e ASPS
    //1 – compõe MDE
    //2 – compõe ASPS
    public static TreeMap gerarContaAuxiliar7(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, String naturezaDespesa, Integer es) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar1(unidadeOrganizacional));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional, "");
        String codigoContaDespesa = getCodigoContaDespesaReceita(naturezaDespesa);
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigoSICONFI(), "");
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigoSICONFI() + "." + codigoContaDespesa, "");
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigoSICONFI() + "." + codigoContaDespesa + "." + es, "");
        } else {
            String codigoFonteDeRecurso = getCodigoFonteDeRecurso(contaDeDestinacao);
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso, "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() + "." + codigoContaDespesa, "");
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso, "");
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso + "." + codigoContaDespesa, "");
            }
        }
        return contaAuxiliar;
    }

    private static String getCodigoFonteDeRecurso(Conta contaDeDestinacao) {
        return contaDeDestinacao.getCodigoSICONFI().length() >= 4 ? contaDeDestinacao.getCodigoSICONFI().substring(0, 4) : contaDeDestinacao.getCodigoSICONFI();
    }

    public static TreeMap gerarContaAuxiliarDetalhada7(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta conta, Integer es) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliarDetalhada1(unidadeOrganizacional));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional, new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao));
        if (contaDeDestinacao.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta));
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", "") + "." + es, new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, es));
        } else {
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf()));
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf()));
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, ""));
            }
        }
        return contaAuxiliar;
    }

    //PO - FP - DC - FR
    public static TreeMap gerarContaAuxiliar8(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Integer dividaConsolidada, Conta contaDeDestinacao) {
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar3(unidadeOrganizacional, financeiroPermanente, dividaConsolidada));
        contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + dividaConsolidada + "." + contaDeDestinacao.getCodigoSICONFI(), "");
        return contaAuxiliar;
    }

    public static TreeMap gerarContaAuxiliarDetalhada8(UnidadeOrganizacional unidadeOrganizacional, SubSistema financeiroPermanente, Integer dividaConsolidada, Conta contaDeDestinacao) {
        TreeMap contaAuxiliarDetalhada = new TreeMap();
        contaAuxiliarDetalhada.putAll(gerarContaAuxiliarDetalhada3(unidadeOrganizacional, financeiroPermanente, dividaConsolidada));
        contaAuxiliarDetalhada.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + getFinanceiroPermanente(financeiroPermanente) + "." + dividaConsolidada + "." + contaDeDestinacao.getCodigo().replace(".", ""), new ContaAuxiliarDetalhada(unidadeOrganizacional, financeiroPermanente, dividaConsolidada, contaDeDestinacao));
        return contaAuxiliarDetalhada;
    }

    //até 2021 PO - FS - FR - ND - ES - AI
    //após 2021 PO - FS - FR - CO - ND - AI
    //Restos a Pagar
    //AI = Ano de Inscrição do Resto
    public static TreeMap gerarContaAuxiliar9(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta contaInscricao, Integer es, Integer anoInscricaoResto, Exercicio exercicioAtual) {
        Conta conta = contaInscricao;
        List<Conta> contasEquivalentes = getContaFacade().recuperarContasDespesaEquivalentesPorId(contaInscricao, exercicioAtual);
        if (!contasEquivalentes.isEmpty()) {
            conta = contasEquivalentes.get(0);
        }
        String naturezaDespesa = conta.getCodigoSICONFI() != null ?
            conta.getCodigoSICONFI().replace(".", "") :
            conta.getCodigo().replace(".", "");

        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliar7(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, naturezaDespesa, es));
        String codigoContaDespesa = getCodigoContaDespesaReceita(naturezaDespesa);
        if (conta.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigoSICONFI() + "." + codigoContaDespesa + "." + es + "." + anoInscricaoResto, "");
        } else {
            String codigoFonteDeRecurso = getCodigoFonteDeRecurso(contaDeDestinacao);
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() + "." + codigoContaDespesa + "." + anoInscricaoResto, "");
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + codigoFonteDeRecurso + "." + codigoContaDespesa + "." + anoInscricaoResto, "");
            }
        }
        return contaAuxiliar;
    }

    private static String getCodigoContaDespesaReceita(String naturezaDespesa) {
        String codigoContaDespesa = naturezaDespesa.length() >= 8 ? naturezaDespesa.substring(0, 8) : naturezaDespesa;
        return codigoContaDespesa;
    }

    public static TreeMap gerarContaAuxiliarDetalhada9(UnidadeOrganizacional unidadeOrganizacional, String classificacaoFuncional, Conta contaDeDestinacao, Conta contaInscricao, Integer es, Integer anoInscricaoResto, Exercicio exercicioAtual, Exercicio exercicioOrigem) {
        Conta conta = contaInscricao;
        List<Conta> contasEquivalentes = getContaFacade().recuperarContasDespesaEquivalentesPorId(contaInscricao, exercicioAtual);
        if (!contasEquivalentes.isEmpty()) {
            conta = contasEquivalentes.get(0);
        }
        TreeMap contaAuxiliar = new TreeMap();
        contaAuxiliar.putAll(gerarContaAuxiliarDetalhada7(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, es));
        if (conta.getExercicio().getAno() <= 2021) {
            contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", "") + "." + es + "." + anoInscricaoResto, new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, es, exercicioAtual, exercicioOrigem));
        } else {
            if (((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() != null) {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf() + "." + conta.getCodigo().replace(".", "") + "." + anoInscricaoResto, new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, ((ContaDeDestinacao) contaDeDestinacao).getCodigoCOSiconf(), exercicioAtual, exercicioOrigem));
            } else {
                contaAuxiliar.put(getPoderOrgaoSiconfi(unidadeOrganizacional) + "." + classificacaoFuncional + "." + contaDeDestinacao.getCodigo().replace(".", "") + "." + conta.getCodigo().replace(".", "") + "." + anoInscricaoResto, new ContaAuxiliarDetalhada(unidadeOrganizacional, classificacaoFuncional, contaDeDestinacao, conta, "", exercicioAtual, exercicioOrigem));
            }
        }
        return contaAuxiliar;
    }

    private static Integer getFinanceiroPermanente(SubSistema subSistema) {
        return SubSistema.FINANCEIRO.equals(subSistema) ? 1 : SubSistema.PERMANENTE.equals(subSistema) ? 2 : 0;
    }

    private static String getPoderOrgaoSiconfi(UnidadeOrganizacional unidadeOrganizacional) {
        try {
            if (mapEntidades.get(unidadeOrganizacional) != null) {
                return mapEntidades.get(unidadeOrganizacional).getPoderOrgaoSiconfi();
            } else {
                Entidade entidade = getEntidadeFacade().buscarEntidadePorUnidadeOrcamentaria(unidadeOrganizacional);
                if (entidade == null) {
                    return "00000";
                }
                mapEntidades.put(unidadeOrganizacional, entidade);
                return mapEntidades.get(unidadeOrganizacional).getPoderOrgaoSiconfi();
            }
        } catch (NullPointerException ex) {
            return "00000";
        }
    }

    public static TreeMap gerarContaAuxiliarDestinacaoDeRecursos(Conta contaDeDestinacao) {
        TreeMap contasAuxiliares = new TreeMap();
        contasAuxiliares.put(contaDeDestinacao.getCodigo(), contaDeDestinacao.getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarConta(Conta conta) {
        TreeMap contasAuxiliares = new TreeMap();
        List<Conta> contas = Lists.newArrayList();
        Conta registroAtual = conta;
        contas.add(registroAtual);
        while (registroAtual.getSuperior() != null) {
            contas.add(registroAtual.getSuperior());
            registroAtual = registroAtual.getSuperior();
        }
        String codigoConta = null;
        for (int i = (contas.size() - 1); i >= 0; i--) {
            codigoConta = contas.get(i).getCodigo().replace(".", "");
            contasAuxiliares.put(codigoConta, contas.get(i).getDescricao());
        }
        return contasAuxiliares;
    }


    public static TreeMap gerarContaAuxiliarContaReceita(Conta conta) {
        TreeMap contasAuxiliares = new TreeMap();
        contasAuxiliares.put(conta.getCodigo(), conta.getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarContaDeDestinacao(Conta conta) {
        TreeMap contasAuxiliares = new TreeMap();

        String codigoDestinacaoRecurso = conta.getCodigo();
        String codigoFonte = ((ContaDeDestinacao) conta).getFonteDeRecursos().getCodigo();

        String descricaoDestinacaoRecurso = conta.getDescricao();
        String descricaoFonte = ((ContaDeDestinacao) conta).getFonteDeRecursos().getDescricao();

        contasAuxiliares.put(codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
        contasAuxiliares.put(codigoDestinacaoRecurso + "." + codigoFonte, descricaoFonte);
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarPessoa(Pessoa pessoa) {
        TreeMap contasAuxiliares = new TreeMap();
        if (pessoa instanceof PessoaFisica) {
            contasAuxiliares.put("01", "Pessoa Física");
            contasAuxiliares.put("01.01", pessoa.getCpf_Cnpj());
        } else {
            contasAuxiliares.put("02", "Pessoa Jurídica");
            contasAuxiliares.put("02.01", pessoa.getCpf_Cnpj());
        }
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa) {
        TreeMap contasAuxiliares = new TreeMap();
        HierarquiaOrganizacional unidade = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), provisaoPPADespesa.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA();

        //recupera código
        for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
            String exercicio = String.valueOf(provisaoPPADespesa.getSubAcaoPPA().getExercicio().getAno());
            String codigoOrgao = orgao.getCodigoNo();
            String codigoUnidade = unidade.getCodigoNo();
            String codigoNatureza = provisaoPPADespesa.getContaDeDespesa().getCodigo();
            String codigoFuncao = acaoPPA.getFuncao().getCodigo();
            String codigoSubFuncao = acaoPPA.getSubFuncao().getCodigo();
            String codigoPrograma = acaoPPA.getPrograma().getCodigo();
            String codigoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getCodigo();
            String codigoAcao = acaoPPA.getCodigo();
            String codigoDestinacaoRecurso = provisaoPPAFonte.getDestinacaoDeRecursos().getCodigo().substring(2, 3);
            String codigoFonteRecurso = provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo();
            String codigoDetalhamentoFonte = "000000";

            //recupera descrição
            String descricaoOrgao = orgao.getSubordinada().getDescricao();
            String descricaoUnidade = unidade.getSubordinada().getDescricao();
            String descricaoNatureza = provisaoPPADespesa.getContaDeDespesa().getDescricao();
            String descricaoFuncao = acaoPPA.getFuncao().getDescricao();
            String descricaoSubFuncao = acaoPPA.getSubFuncao().getDescricao();
            String descricaoPrograma = acaoPPA.getPrograma().getDenominacao();
            String descricaoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getDescricao();
            String descricaoAcao = acaoPPA.getDescricao();
            String descricaoDestinacaoRecurso = provisaoPPAFonte.getDestinacaoDeRecursos().getDescricao();
            String descricaoFonteRecurso = provisaoPPAFonte.getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao();
            String descricaoDetalhamentoFonte = "Sem Detalhamento";

            //Tipo Conta Auxliar: 4 - Despesa
            contasAuxiliares.put(exercicio, exercicio);
            contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + codigoDetalhamentoFonte, descricaoDetalhamentoFonte);
        }
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarFonteDespesaOrc(FonteDespesaORC fonteDespesaORC) {
        TreeMap contasAuxiliares = new TreeMap();
        HierarquiaOrganizacional unidade = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(new Date(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();

        //recupera código
        String exercicio = String.valueOf(fonteDespesaORC.getDespesaORC().getExercicio().getAno());
        String codigoOrgao = orgao.getCodigoNo();
        String codigoUnidade = unidade.getCodigoNo();
        String codigoNatureza = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
        String codigoFuncao = acaoPPA.getFuncao().getCodigo();
        String codigoSubFuncao = acaoPPA.getSubFuncao().getCodigo();
        String codigoPrograma = acaoPPA.getPrograma().getCodigo();
        String codigoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getCodigo();
        String codigoAcao = acaoPPA.getCodigo();
        String codigoDestinacaoRecurso = fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonteRecurso = fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo();

        //recupera descrição
        String descricaoOrgao = orgao.getSubordinada().getDescricao();
        String descricaoUnidade = unidade.getSubordinada().getDescricao();
        String descricaoNatureza = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao();
        String descricaoFuncao = acaoPPA.getFuncao().getDescricao();
        String descricaoSubFuncao = acaoPPA.getSubFuncao().getDescricao();
        String descricaoPrograma = acaoPPA.getPrograma().getDenominacao();
        String descricaoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getDescricao();
        String descricaoAcao = acaoPPA.getDescricao();
        String descricaoDestinacaoRecurso = fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao();
        String descricaoFonteRecurso = fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao();

        contasAuxiliares.put(exercicio, exercicio);
        contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
        contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);

        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarEmpenho(Empenho empenho) {
        TreeMap contasAuxiliares = new TreeMap();
        HierarquiaOrganizacional unidade = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(empenho.getDataEmpenho(), empenho.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(empenho.getDataEmpenho(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();

        //recupera código
        String exercicio = String.valueOf(empenho.getExercicio().getAno());
        String codigoOrgao = orgao.getCodigoNo();
        String codigoUnidade = unidade.getCodigoNo();
        String codigoNatureza = empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
        String codigoFuncao = acaoPPA.getAcaoPrincipal().getFuncao().getCodigo();
        String codigoSubFuncao = acaoPPA.getAcaoPrincipal().getSubFuncao().getCodigo();
        String codigoPrograma = acaoPPA.getAcaoPrincipal().getPrograma().getCodigo();
        String codigoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getCodigo();
        String codigoAcao = acaoPPA.getCodigo();
        String codigoDestinacaoRecurso = empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonteRecurso = empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo();

        //recupera descrição
        String descricaoOrgao = orgao.getSubordinada().getDescricao();
        String descricaoUnidade = unidade.getSubordinada().getDescricao();
        String descricaoNatureza = empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao();
        String descricaoFuncao = acaoPPA.getAcaoPrincipal().getFuncao().getDescricao();
        String descricaoSubFuncao = acaoPPA.getAcaoPrincipal().getSubFuncao().getDescricao();
        String descricaoPrograma = acaoPPA.getAcaoPrincipal().getPrograma().getDenominacao();
        String descricaoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getDescricao();
        String descricaoAcao = acaoPPA.getDescricao();
        String descricaoDestinacaoRecurso = empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao();
        String descricaoFonteRecurso = empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao();

        //Tipo Conta Auxliar: 3 - Dotação
        if (CategoriaOrcamentaria.NORMAL.equals(empenho.getCategoriaOrcamentaria())) {

            contasAuxiliares.put(exercicio, exercicio);
            contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);
        } else {
            contasAuxiliares.put(exercicio, exercicio);
            contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenho.getEmpenho().getExercicio(), empenho.getEmpenho().getExercicio());
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenho.getEmpenho().getExercicio() + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenho.getEmpenho().getExercicio() + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenho.getEmpenho().getExercicio() + "." + codigoOrgao + "." + codigoUnidade + "." + empenho.getEmpenho().getNumero(), empenho.getEmpenho().getNumero());
        }
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        TreeMap contasAuxiliares = new TreeMap();
        HierarquiaOrganizacional unidade = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(empenhoEstorno.getDataEstorno(), empenhoEstorno.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(empenhoEstorno.getDataEstorno(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();

        //recupera código
        String exercicio = String.valueOf(empenhoEstorno.getEmpenho().getExercicio().getAno());
        String codigoOrgao = orgao.getCodigoNo();
        String codigoUnidade = unidade.getCodigoNo();
        String codigoNatureza = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
        String codigoFuncao = acaoPPA.getAcaoPrincipal().getFuncao().getCodigo();
        String codigoSubFuncao = acaoPPA.getAcaoPrincipal().getSubFuncao().getCodigo();
        String codigoPrograma = acaoPPA.getAcaoPrincipal().getPrograma().getCodigo();
        String codigoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getCodigo();
        String codigoAcao = acaoPPA.getCodigo();
        String codigoDestinacaoRecurso = empenhoEstorno.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonteRecurso = empenhoEstorno.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo();

        //recupera descrição
        String descricaoOrgao = orgao.getSubordinada().getDescricao();
        String descricaoUnidade = unidade.getSubordinada().getDescricao();
        String descricaoNatureza = empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao();
        String descricaoFuncao = acaoPPA.getAcaoPrincipal().getFuncao().getDescricao();
        String descricaoSubFuncao = acaoPPA.getAcaoPrincipal().getSubFuncao().getDescricao();
        String descricaoPrograma = acaoPPA.getAcaoPrincipal().getPrograma().getDenominacao();
        String descricaoAcaoPrincipal = acaoPPA.getAcaoPrincipal().getDescricao();
        String descricaoAcao = acaoPPA.getDescricao();
        String descricaoDestinacaoRecurso = empenhoEstorno.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao();
        String descricaoFonteRecurso = empenhoEstorno.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao();

        //Tipo Conta Auxliar: 3 - Dotação
        if (CategoriaOrcamentaria.NORMAL.equals(empenhoEstorno.getCategoriaOrcamentaria())) {

            contasAuxiliares.put(exercicio, exercicio);
            contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);
        } else {
            contasAuxiliares.put(exercicio, exercicio);
            contasAuxiliares.put(exercicio + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao, descricaoFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao, descricaoSubFuncao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma, descricaoPrograma);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal, descricaoAcaoPrincipal);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao, descricaoAcao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza, descricaoNatureza);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso, descricaoFonteRecurso);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenhoEstorno.getEmpenho().getExercicio(), empenhoEstorno.getEmpenho().getExercicio());
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenhoEstorno.getEmpenho().getExercicio() + "." + codigoOrgao, descricaoOrgao);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenhoEstorno.getEmpenho().getExercicio() + "." + codigoOrgao + "." + codigoUnidade, descricaoUnidade);
            contasAuxiliares.put(exercicio + "." + codigoOrgao + "." + codigoUnidade + "." + codigoFuncao + "." + codigoSubFuncao + "." + codigoPrograma + "." + codigoAcaoPrincipal + "." + codigoAcao + "." + codigoNatureza + "." + codigoDestinacaoRecurso + "." + codigoFonteRecurso + "." + empenhoEstorno.getEmpenho().getExercicio() + "." + codigoOrgao + "." + codigoUnidade + "." + empenhoEstorno.getEmpenho().getNumero(), empenhoEstorno.getEmpenho().getNumero());
        }

        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        TreeMap contaasAuxiliares = new TreeMap();

        String codigoContaReceita = receitaLOAFonte.getReceitaLOA().getContaDeReceita().getCodigo();
        String codigoDestinacaoRecurso = receitaLOAFonte.getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonte = receitaLOAFonte.getDestinacaoDeRecursos().getFonteDeRecursos().getCodigo();

        String descricaoDestinacaoRecurso = receitaLOAFonte.getDestinacaoDeRecursos().getDescricao();
        String descricaoFonte = receitaLOAFonte.getDestinacaoDeRecursos().getFonteDeRecursos().getDescricao();
        String descricaoContaReceita = receitaLOAFonte.getReceitaLOA().getContaDeReceita().getDescricao();

        contaasAuxiliares.put(codigoContaReceita, descricaoContaReceita);
        contaasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
        contaasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso + "." + codigoFonte, descricaoFonte);
        return contaasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarSubConta(SubConta subConta) {
        TreeMap contasAuxiliares = new TreeMap();
        String codBanco = subConta.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco();
        String codAgencia = subConta.getContaBancariaEntidade().getAgencia().getNumeroAgencia() + "-" + subConta.getContaBancariaEntidade().getAgencia().getDigitoVerificador();
        String codConta = subConta.getContaBancariaEntidade().getNumeroConta() + "-" + subConta.getContaBancariaEntidade().getDigitoVerificador();

        contasAuxiliares.put(codBanco, subConta.getContaBancariaEntidade().getAgencia().getBanco().getDescricao());
        contasAuxiliares.put(codBanco + "." + codAgencia, subConta.getContaBancariaEntidade().getAgencia().getNomeAgencia());
        contasAuxiliares.put(codBanco + "." + codAgencia + "." + codConta, subConta.getContaBancariaEntidade().getNumeroConta());
        contasAuxiliares.put(codBanco + "." + codAgencia + "." + codConta + "." + subConta.getCodigo(), subConta.getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDesdobramento(Desdobramento desdobramento) {
        TreeMap contasAuxiliares = new TreeMap();

        String codPrograma = desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo();
        String codTipoAcao = desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String codAcao = desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo();
        String codSubAcao = desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getCodigo();
        String codDespesa = desdobramento.getConta().getCodigo();

        contasAuxiliares.put(codPrograma, desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getDenominacao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao, desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao, desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao, desdobramento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao + "." + codDespesa, desdobramento.getConta().getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        TreeMap contasAuxiliares = new TreeMap();
        String codPrograma = desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo();
        String codTipoAcao = desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String codAcao = desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo();
        String codSubAcao = desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getCodigo();
        String codDespesa = desdobramentoEmpenho.getConta().getCodigo();

        contasAuxiliares.put(codPrograma, desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getDenominacao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao, desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao, desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao, desdobramentoEmpenho.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao + "." + codDespesa, desdobramentoEmpenho.getConta().getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDesdobramentoEmpenhoEstorno(DesdobramentoEmpenhoEstorno
                                                                            desdobramentoEmpenhoEstorno) {
        TreeMap contasAuxiliares = new TreeMap();

        String codPrograma = desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo();
        String codTipoAcao = desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String codAcao = desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo();
        String codSubAcao = desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getCodigo();
        String codDespesa = desdobramentoEmpenhoEstorno.getConta().getCodigo();

        contasAuxiliares.put(codPrograma, desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getDenominacao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao, desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao, desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao, desdobramentoEmpenhoEstorno.getEmpenhoEstorno().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao + "." + codDespesa, desdobramentoEmpenhoEstorno.getConta().getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDividaPublica(DividaPublica dividaPublica) {
        TreeMap contasAuxiliares = new TreeMap();
        contasAuxiliares.put(dividaPublica.getNumero(), dividaPublica.getDescricaoDivida());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarPropostaConcessaoDiaria(PropostaConcessaoDiaria
                                                                        propostaConcessaoDiaria) {
        TreeMap contasAuxiliares = new TreeMap();

        String pessoaCPF = propostaConcessaoDiaria.getPessoaFisica().getCpf_Cnpj();
        String pessoaNome = propostaConcessaoDiaria.getPessoaFisica().getNome();

        if (TipoProposta.CONCESSAO_DIARIA.equals(propostaConcessaoDiaria.getTipoProposta())) {
            contasAuxiliares.put(pessoaCPF, pessoaNome);
        } else if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(propostaConcessaoDiaria.getTipoProposta())) {
            contasAuxiliares.put(pessoaCPF, pessoaNome);
        } else if (TipoProposta.SUPRIMENTO_FUNDO.equals(propostaConcessaoDiaria.getTipoProposta())) {
            contasAuxiliares.put(pessoaCPF, pessoaNome);
        }
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarLancReceitaFonte(LancReceitaFonte lancReceitaFonte) {
        TreeMap contasAuxiliares = new TreeMap();

        String codigoContaReceita = lancReceitaFonte.getLancReceitaOrc().getReceitaLOA().getContaDeReceita().getCodigo();
        String codigoDestinacaoRecurso = lancReceitaFonte.getReceitaLoaFonte().getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonte = lancReceitaFonte.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().getCodigo();

        String descricaoDestinacaoRecurso = lancReceitaFonte.getReceitaLoaFonte().getDestinacaoDeRecursos().getDescricao();
        String descricaoFonte = lancReceitaFonte.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().getDescricao();
        String descricaoContaReceita = lancReceitaFonte.getReceitaLoaFonte().getReceitaLOA().getContaDeReceita().getDescricao();

        contasAuxiliares.put(codigoContaReceita, descricaoContaReceita);
        contasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
        contasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso + "." + codigoFonte, descricaoFonte);
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarReceitaORCFonteEstorno(ReceitaORCFonteEstorno
                                                                       receitaORCFonteEstorno) {
        TreeMap contasAuxiliares = new TreeMap();

        String codigoContaReceita = receitaORCFonteEstorno.getReceitaORCEstorno().getReceitaLOA().getContaDeReceita().getCodigo();
        String codigoDestinacaoRecurso = receitaORCFonteEstorno.getReceitaLoaFonte().getDestinacaoDeRecursos().getCodigo().substring(2, 3);
        String codigoFonte = receitaORCFonteEstorno.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().getCodigo();

        String descricaoDestinacaoRecurso = receitaORCFonteEstorno.getReceitaLoaFonte().getDestinacaoDeRecursos().getDescricao();
        String descricaoFonte = receitaORCFonteEstorno.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().getDescricao();
        String descricaoContaReceita = receitaORCFonteEstorno.getReceitaLoaFonte().getReceitaLOA().getContaDeReceita().getDescricao();

        contasAuxiliares.put(codigoContaReceita, descricaoContaReceita);
        contasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso, descricaoDestinacaoRecurso);
        contasAuxiliares.put(codigoContaReceita + "." + codigoDestinacaoRecurso + "." + codigoFonte, descricaoFonte);
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar
                                                                            desdobramentoObrigacaoPagar) {
        TreeMap contasAuxiliares = new TreeMap();

        String codPrograma = desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo();
        String codTipoAcao = desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String codAcao = desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo();
        String codSubAcao = desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getCodigo();
        String codDespesa = desdobramentoObrigacaoPagar.getConta().getCodigo();

        contasAuxiliares.put(codPrograma, desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getDenominacao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao, desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao, desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao, desdobramentoObrigacaoPagar.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao + "." + codDespesa, desdobramentoObrigacaoPagar.getConta().getDescricao());
        return contasAuxiliares;
    }

    public static TreeMap gerarContaAuxiliarDesdobramentoLiquidacaoEstorno(DesdobramentoLiquidacaoEstorno
                                                                               desdobramentoLiquidacaoEstorno) {
        TreeMap contasAuxiliares = new TreeMap();

        String codPrograma = desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo();
        String codTipoAcao = desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String codAcao = desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getCodigo();
        String codSubAcao = desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getCodigo();
        String codDespesa = desdobramentoLiquidacaoEstorno.getConta().getCodigo();

        contasAuxiliares.put(codPrograma, desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma().getDenominacao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao, desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao, desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao, desdobramentoLiquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getDescricao());
        contasAuxiliares.put(codPrograma + "." + codTipoAcao + "." + codAcao + "." + codSubAcao + "." + codDespesa, desdobramentoLiquidacaoEstorno.getConta().getDescricao());
        return contasAuxiliares;
    }
}
