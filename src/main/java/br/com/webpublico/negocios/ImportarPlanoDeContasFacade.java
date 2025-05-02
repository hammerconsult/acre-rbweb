package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteImportacaoPlanoDeContas;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.ContaVO;
import br.com.webpublico.entidadesauxiliares.DeparaContaVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ImportarPlanoDeContasFacade {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private TipoContaFacade tipoContaFacade;


    public AssistenteImportacaoPlanoDeContas processarExcluirContas(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setMensagens(Lists.<String>newArrayList());
        try {
            iniciarBarraProgresso(assistente, assistente.getContasExcluidas().size());
            assistente.setSituacao(SituacaoImportarPlanoDeContas.EXCLUIDO);
            assistente.getBarraProgressoItens().setMensagens("Excluindo as contas....");
            Exercicio exercicio = assistente.getExercicio();
            removerContasPlanilhaExcluidas(assistente, exercicio);
            assistente.getBarraProgressoItens().setMensagens("Finalizou de excluir....");
        } catch (Exception e) {
            assistente.setMensagemErro(e.getMessage());
            finalizar(assistente);
            throw e;
        }
        return assistente;
    }

    private void removerContasPlanilhaExcluidas(AssistenteImportacaoPlanoDeContas assistente, Exercicio exercicio) {
        ordenarContasExcluidas(assistente);
        List<Conta> contasComFilhos = Lists.newArrayList();
        List<Conta> contasFilhas = Lists.newArrayList();
        removerContas(assistente, exercicio, contasComFilhos, contasFilhas);
        removerContasPai(assistente, contasComFilhos, contasFilhas);
    }

    private void removerContas(AssistenteImportacaoPlanoDeContas assistente, Exercicio exercicio, List<Conta> contasComFilhos, List<Conta> contasFilhas) {

        adicionarMensagemNegrito(assistente, assistente.getContasExcluidas().size() + " contas excluidas...");
        for (ContaVO conta : assistente.getContasExcluidas()) {
            try {
                recuperarConta(assistente, exercicio, conta);
                if (conta.getContaRecuperada() != null) {
                    Conta contaRecuperada = conta.getContaRecuperada();
                    Boolean verificarFilho = removerDependentesDaConta(contaRecuperada, contasComFilhos, contasFilhas);
                    if (verificarFilho) {
                        contaFacade.remover(contaRecuperada);
                    }
                } else {
                    String mensagem = "Ocorreu um erro ao remover a conta " + conta.getContaRecuperada().getCodigo() + ". Detalhes do erro: Conta não encontrada.";
                    adicionarMensagemNegritoVermelho(assistente, mensagem);
                }
            } catch (Exception ex) {
                adicionarMensagemNegritoVermelho(assistente, "Ocorreu um erro ao remover a conta " + conta.getCodigo() + ". Detalhes do erro: " + ex.getMessage());
                continue;
            }
            contarMaisUmProcessado(assistente);
        }
    }

    private void removerContasPai(AssistenteImportacaoPlanoDeContas assistente, List<Conta> contasComFilhos, List<Conta> contasFilhas) {
        for (Conta conta : contasFilhas) {
            try {
                contaFacade.remover(conta);
            } catch (Exception ex) {
                adicionarMensagemNegritoVermelho(assistente, "Ocorreu um erro ao remover a conta " + conta.getCodigo() + ". Detalhes do erro: " + ex.getMessage());
                continue;
            }
        }
        contasFilhas.clear();

        if (!contasComFilhos.isEmpty()) {
            Collections.reverse(contasComFilhos);

            Iterator<Conta> iterator = contasComFilhos.iterator();
            while (iterator.hasNext()) {
                Conta contaPai = iterator.next();
                try {
                    contaFacade.remover(contaPai);
                } catch (Exception ex) {
                    adicionarMensagemNegritoVermelho(assistente, "Ocorreu um erro ao remover a conta " + contaPai.getCodigo() + ". Detalhes do erro: " + ex.getMessage());
                    continue;
                }
            }

            contasComFilhos.clear();
        }
    }

    private Boolean removerDependentesDaConta(Conta contaRecuperada, List<Conta> contasComFilhos, List<Conta> contasFilhas) {
        Boolean verificarFilho = true;
        if (contaFacade.hasFilhos(contaRecuperada)) {
            contasComFilhos.add(contaRecuperada);
            List<BigDecimal> idContas = contaFacade.buscarIdsDeContasComFilhos(contaRecuperada);
            for (BigDecimal idConta : idContas) {
                contasFilhas.add(contaFacade.recuperar(idConta.longValue()));
            }
            verificarFilho = false;
        }
        return verificarFilho;
    }

    private void ordenarContasExcluidas(AssistenteImportacaoPlanoDeContas assistente) {
        Collections.sort(assistente.getContasExcluidas(), new Comparator<ContaVO>() {
            @Override
            public int compare(ContaVO o1, ContaVO o2) {
                return o2.getCodigo().compareTo(o1.getCodigo());
            }
        });
    }

    public AssistenteImportacaoPlanoDeContas inicializarImportacaoDoPlanoDeContas(AssistenteImportacaoPlanoDeContas assistente) {
        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        try {
            int tamanho = assistente.getContasImportadas().size()
                + assistente.getContasAlteradas().size()
                + assistente.getContasExcluidas().size();

            Exercicio exercicio = assistente.getExercicio();
            PlanoDeContas planoDeContas = planoDeContasFacade.recuperaPlanoDeContasPorTipoContaExercicio(assistente.getTipoConta(), exercicio);
            if (planoDeContas == null) {
                throw new ExcecaoNegocioGenerica("Nenhum plano de contas foi encontrado para a classe " + assistente.getTipoConta() + " e exercício " + exercicio);
            }
            assistente.setPlanoDeContas(planoDeContas);

            barraProgressoItens.setTotal(tamanho);
            assistente.setTotal(tamanho);

            assistente.getBarraProgressoItens().setMensagens("Importando dados das contas da planilha....");
            percorrerContas(assistente);
            assistente.setSituacao(SituacaoImportarPlanoDeContas.IMPORTADO);
            assistente.getBarraProgressoItens().finaliza();
        } catch (Exception e) {
            assistente.setMensagemErro(e.getMessage());
            finalizar(assistente);
            throw e;
        }
        return assistente;
    }

    private void percorrerContas(AssistenteImportacaoPlanoDeContas assistente) {
        adicionarMensagemNegrito(assistente, "Iniciando...");
        Exercicio exercicio = assistente.getExercicio();
        adicionarMensagemNegrito(assistente, assistente.getContasImportadas().size() + " contas importadas...");
        for (ContaVO conta : assistente.getContasImportadas()) {
            recuperarConta(assistente, exercicio, conta);
            contarMaisUmProcessado(assistente);
        }
        adicionarMensagemNegrito(assistente, assistente.getContasAlteradas().size() + " contas alteradas...");
        for (ContaVO conta : assistente.getContasAlteradas()) {
            recuperarConta(assistente, exercicio, conta);
            contarMaisUmProcessado(assistente);
        }
        adicionarMensagemNegrito(assistente, assistente.getContasExcluidas().size() + " contas excluidas...");
        for (ContaVO conta : assistente.getContasExcluidas()) {
            recuperarConta(assistente, exercicio, conta);
            contarMaisUmProcessado(assistente);
        }
    }

    public AssistenteImportacaoPlanoDeContas aplicarAtributosConta(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setMensagens(Lists.<String>newArrayList());
        aplicarContasWebPublicoComTCE(assistente);
        return assistente;
    }

    private void aplicarContasWebPublicoComTCE(AssistenteImportacaoPlanoDeContas assistente) {
        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        assistente.getBarraProgressoItens().setMensagens("Aplicando atributos de todas as contas (Webpúblico com TCE) ....");


        PlanoDeContas planoDeContas = assistente.getPlanoDeContas();

        List<Conta> contas = planoDeContasFacade.recuperaContasPorPlano(planoDeContas);
        barraProgressoItens.setTotal(contas.size());

        for (Conta conta : contas) {
            ContaVO contaVO = null;
            for (ContaVO vo : assistente.getTodasContas()) {
                if (conta.getCodigo().equals(vo.getCodigo())) {
                    contaVO = vo;
                    break;
                }
            }
            if (contaVO != null) {
                conta.setAtiva(Boolean.TRUE);
                if (!conta.getDescricao().equalsIgnoreCase(contaVO.getDescricao())) {
                    conta.setDescricao(contaVO.getDescricao());
                }
                if (!conta.getCategoria().equals(contaVO.getCategoriaConta())) {
                    conta.setCategoria(contaVO.getCategoriaConta());
                }
                if (!conta.getCodigoSICONFI().equals(contaVO.getCodigoSiconfi())) {
                    conta.setCodigoSICONFI(contaVO.getCodigoSiconfi());
                }
                if (!conta.getCodigoTCE().equals(contaVO.getCodigoTCE())) {
                    conta.setCodigoTCE(contaVO.getCodigoTCE());
                }
                if (ClasseDaConta.CONTABIL.equals(assistente.getTipoConta().getClasseDaConta())) {
                    ContaContabil contaContabil = (ContaContabil) conta;
                    contaContabil.setNaturezaSaldo(contaVO.getNaturezaDoSaldo());
                    contaContabil.setSubSistema(contaVO.getSubSistemaEnum());
                    contaContabil.setNaturezaConta(contaVO.getNaturezaContaEnum());
                    contaContabil.setNaturezaInformacao(contaVO.getNaturezaInformacaoEnum());

                    contaFacade.salvar(contaContabil);
                }
            }
            contarMaisUmProcessado(assistente);
        }
    }

    public AssistenteImportacaoPlanoDeContas validarContas(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setMensagens(Lists.<String>newArrayList());
        verificarContasTCEcomWebPublico(assistente);
        verificarContasWebPublicoComTCE(assistente);
        assistente.setSituacao(SituacaoImportarPlanoDeContas.VALIDADO);
        assistente.setMostrarContasNaoEncontrada(Boolean.TRUE);
        return assistente;
    }

    private void verificarContasTCEcomWebPublico(AssistenteImportacaoPlanoDeContas assistente) {
        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        int tamanho = assistente.getTodasContas().size();
        barraProgressoItens.setTotal(tamanho);

        assistente.getBarraProgressoItens().setMensagens("Verificando todas as contas (TCE com Webpúblico) ....");
        Exercicio exercicio = assistente.getExercicio();
        for (ContaVO conta : assistente.getTodasContas()) {
            recuperarConta(assistente, exercicio, conta);
            contarMaisUmProcessado(assistente);
        }
    }

    private void verificarContasWebPublicoComTCE(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setContasNaoEncontradaTCE(Lists.<Conta>newArrayList());

        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        assistente.getBarraProgressoItens().setMensagens("Verificando todas as contas (Webpúblico com TCE) ....");

        List<Conta> contas = planoDeContasFacade.recuperaContasPorPlano(assistente.getPlanoDeContas());
        barraProgressoItens.setTotal(contas.size());

        for (Conta conta : contas) {
            ContaVO contaEncontrada = null;
            for (ContaVO contaVO : assistente.getTodasContas()) {
                if (conta.getCodigo().equals(contaVO.getCodigo())) {
                    contaEncontrada = contaVO;
                    break;
                }
            }
            if (contaEncontrada == null) {
                assistente.getContasNaoEncontradaTCE().add(conta);
            } else {
                verificarAtributosConta(assistente, conta, contaEncontrada);
                verificarExisteOCCLCP(assistente, conta, contaEncontrada);
            }
            contarMaisUmProcessado(assistente);
        }
    }

    private void verificarAtributosConta(AssistenteImportacaoPlanoDeContas assistente, Conta conta, ContaVO contaVO) {
        Boolean inconsistencia = Boolean.FALSE;
        if (!conta.getDescricao().equalsIgnoreCase(contaVO.getDescricao())) {
            adicionarMensagemNegrito(assistente, "Descrição da conta " + conta.getCodigo() + " está diferente do plano TCE.");
            inconsistencia = Boolean.TRUE;
        }
        if (!conta.getCategoria().equals(contaVO.getCategoriaConta())) {
            adicionarMensagemNegrito(assistente, "Categoria da conta " + conta.getCodigo() + " está diferente do plano TCE.");
            inconsistencia = Boolean.TRUE;
        }
        if (ClasseDaConta.CONTABIL.equals(assistente.getTipoConta().getClasseDaConta())) {
            if (((ContaContabil) conta).getNaturezaSaldo() == null) {
                adicionarMensagemNegrito(assistente, "Natureza de saldo da conta " + conta.getCodigo() + " está vazia.");
                inconsistencia = Boolean.TRUE;
            } else {
                if (!((ContaContabil) conta).getNaturezaSaldo().equals(contaVO.getNaturezaDoSaldo())) {
                    adicionarMensagemNegrito(assistente, "Natureza de saldo da conta " + conta.getCodigo() + " está diferente do plano TCE.");
                    inconsistencia = Boolean.TRUE;
                }
            }
            if (((ContaContabil) conta).getNaturezaConta() == null) {
                adicionarMensagemNegrito(assistente, "Natureza da conta " + conta.getCodigo() + " está vazia.");
                inconsistencia = Boolean.TRUE;
            } else {
                if (!((ContaContabil) conta).getNaturezaConta().equals(contaVO.getNaturezaContaEnum())) {
                    adicionarMensagemNegrito(assistente, "Natureza da conta " + conta.getCodigo() + " está diferente do plano TCE.");
                    inconsistencia = Boolean.TRUE;
                }
            }
        }

        if (inconsistencia) {
            assistente.getContasNaoEncontradaTCE().add(conta);
        }
    }

    private void verificarExisteOCCLCP(AssistenteImportacaoPlanoDeContas assistente, Conta conta, ContaVO contaVO) {
        if (ClasseDaConta.CONTABIL.equals(assistente.getTipoConta().getClasseDaConta())) {
            if (CategoriaConta.SINTETICA.equals(conta.getCategoria())) {
                List<OrigemContaContabil> origemContaContabeis = origemOCCFacade.buscarOrigemContaContabil(conta, assistente.getDataOperacao());
                if (origemContaContabeis != null && !origemContaContabeis.isEmpty()) {
                    adicionarMensagemNegrito(assistente, "A conta " + conta.getCodigo() + " é " + CategoriaConta.SINTETICA.getDescricao() + " e possui Origem conta contábil vigente.");
                    assistente.getContasNaoEncontradaTCE().add(conta);
                }
                List<LCP> lcps = clpFacade.buscarLCPs(conta, assistente.getDataOperacao());
                if (lcps != null && !lcps.isEmpty()) {
                    adicionarMensagemNegrito(assistente, "A conta " + conta.getCodigo() + " é " + CategoriaConta.SINTETICA.getDescricao() + " e possui LCP vigente.");
                    assistente.getContasNaoEncontradaTCE().add(conta);
                }
            }
        }
    }

    public void recuperarConta(AssistenteImportacaoPlanoDeContas assistente, Exercicio exercicio, ContaVO conta) {
        List<Conta> contas = Lists.newArrayList();

        switch (assistente.getTipoConta().getClasseDaConta()) {
            case RECEITA:
                contas = contaFacade.listaFiltrandoContaReceitaPorExercicio(conta.getCodigo(), exercicio);
                break;
            case DESPESA:
                contas = contaFacade.listaFiltrandoContaDespesa(conta.getCodigo(), exercicio);
                break;
            case CONTABIL:
                contas = contaFacade.listaContasContabeis(conta.getCodigo(), exercicio);
                break;
            case EXTRAORCAMENTARIA:
                contas = contaFacade.listaFiltrandoExtraorcamentario(conta.getCodigo(), exercicio);
                break;
        }
        if (contas != null && !contas.isEmpty()) {
            conta.setContaRecuperada(contas.get(0));
        } else {
            adicionarMensagemNegritoVermelho(assistente, "Conta não encontrada " + conta.getCodigo() + "(" + exercicio.getAno() + ")");
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public AssistenteImportacaoPlanoDeContas processarIncluirAlterarContas(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setMensagens(Lists.<String>newArrayList());

        try {
            assistente.setSituacao(SituacaoImportarPlanoDeContas.NOVAS_CONTAS);
            iniciarBarraProgresso(assistente, assistente.getDeparaContas().size());
            BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
            barraProgressoItens.inicializa();
            int tamanho = assistente.getContasImportadas().size()
                + assistente.getContasAlteradas().size();
            barraProgressoItens.setTotal(tamanho);
            assistente.setTotal(tamanho);
            Exercicio exercicio = assistente.getExercicio();
            importarContas(assistente, exercicio, assistente.getPlanoDeContas());

        } catch (Exception e) {
            assistente.setMensagemErro(e.getMessage());
            finalizar(assistente);
            throw e;
        }
        return assistente;
    }

    private void importarContas(AssistenteImportacaoPlanoDeContas assistente, Exercicio exercicio, PlanoDeContas planoDeContas) {
        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        List<Conta> contas = Lists.newArrayList();
        assistente.getBarraProgressoItens().setMensagens("Incluindo as contas....");
        adicionarMensagemNegrito(assistente, assistente.getContasImportadas().size() + " contas importadas...");
        for (ContaVO conta : assistente.getContasImportadas()) {
            Util.adicionarObjetoEmLista(contas, salvarNovaConta(assistente, conta, exercicio, planoDeContas));
            contarMaisUmProcessado(assistente);
        }
        assistente.getBarraProgressoItens().setMensagens("Alterando as contas....");
        alterarContas(assistente, exercicio, assistente.getPlanoDeContas(), contas);
        ordernarContas(contas);
        barraProgressoItens.setTotal(contas.size());
        assistente.getBarraProgressoItens().setMensagens("Atualizando contas....");
        atualizarSuperior(assistente, contas);
    }

    private void atualizarSuperior(AssistenteImportacaoPlanoDeContas assistente, List<Conta> contas) {
        for (Conta conta : contas) {
            if (conta.getSuperior() == null && conta.getNivel() != 1) {
                for (Conta contaSuperior : contas) {
                    if (contaSuperior.getCodigoSemZerosAoFinal().equals(getCodigoRemovendoUltimoNivel(conta.getCodigoSemZerosAoFinal()))) {
                        conta.setSuperior(contaSuperior);
                        contaFacade.salvar(conta);
                        break;
                    }
                }
            }
            contarMaisUmProcessado(assistente);
        }
        for (Conta conta : contas) {
            if (conta.getSuperior() == null && conta.getNivel() != 1) {
                adicionarMensagemNegritoVermelho(assistente, "Não foi encontrado superior para a conta: " + conta.getCodigo());
            }
        }
    }

    private Conta salvarNovaConta(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Exercicio exercicio, PlanoDeContas planoDeContas) {
        switch (assistente.getTipoConta().getClasseDaConta()) {
            case RECEITA:
                return salvarContaReceita(assistente, conta, exercicio, planoDeContas);
            case DESPESA:
                return salvarContaDespesa(assistente, conta, exercicio, planoDeContas);
            case CONTABIL:
                return salvarContaContabil(assistente, conta, exercicio, planoDeContas);
            case EXTRAORCAMENTARIA:
                return salvarContaExtra(assistente, conta, exercicio, planoDeContas);
            default:
                return null;
        }
    }


    private void ordernarContas(List<Conta> contas) {
        Collections.sort(contas, new Comparator<Conta>() {
            @Override
            public int compare(Conta o1, Conta o2) {
                return o2.getCodigo().compareTo(o1.getCodigo());
            }
        });
    }

    private Conta salvarContaExtra(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Exercicio exercicio, PlanoDeContas planoDeContas) {
        String codigoSemZerosAoFinal;
        List<Conta> contas;
        List<Conta> contasExtras = contaFacade.listaFiltrandoExtraorcamentario(conta.getCodigo(), exercicio);
        if (!contasExtras.isEmpty()) {
            return contasExtras.get(0);
        }
        ContaExtraorcamentaria contaExtraorcamentaria = new ContaExtraorcamentaria();
        contaExtraorcamentaria.setAtiva(Boolean.TRUE);
        contaExtraorcamentaria.setCodigo(conta.getCodigo());
        contaExtraorcamentaria.setDescricao(conta.getDescricao());
        contaExtraorcamentaria.setCategoria(CategoriaConta.ANALITICA);
        contaExtraorcamentaria.setPermitirDesdobramento(false);
        contaExtraorcamentaria.setExercicio(exercicio);
        contaExtraorcamentaria.setPlanoDeContas(planoDeContas);
        contaExtraorcamentaria.setDataRegistro(assistente.getDataOperacao());
        contaExtraorcamentaria.setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria.DEPOSITOS_DIVERSOS);
        contaExtraorcamentaria.setCodigoSICONFI(conta.getCodigoSiconfi());
        contaExtraorcamentaria.setCodigoTCE(conta.getCodigoTCE());
        codigoSemZerosAoFinal = getCodigoRemovendoUltimoNivel(contaExtraorcamentaria.getCodigoSemZerosAoFinal());
        contas = contaFacade.listaFiltrandoExtraorcamentario(codigoSemZerosAoFinal, exercicio);
        if (!contas.isEmpty()) {
            contaExtraorcamentaria.setSuperior(contas.get(0));
        }
        verificarSalvarCategoriaSuperior(contaExtraorcamentaria);
        return contaFacade.salvarRetornando(contaExtraorcamentaria);
    }


    private Conta salvarContaContabil(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Exercicio exercicio, PlanoDeContas planoDeContas) {
        String codigoSemZerosAoFinal;
        List<Conta> contas;
        List<Conta> contasContabeis = contaFacade.listaContasContabeis(conta.getCodigo(), exercicio);
        if (!contasContabeis.isEmpty()) {
            return contasContabeis.get(0);
        }
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setAtiva(Boolean.TRUE);
        contaContabil.setCodigo(conta.getCodigo());
        contaContabil.setDescricao(conta.getDescricao());
        definirCategoriaConta(conta, contaContabil);
        contaContabil.setExercicio(exercicio);
        contaContabil.setPlanoDeContas(planoDeContas);
        contaContabil.setDataRegistro(assistente.getDataOperacao());
        contaContabil.setCodigoSICONFI(conta.getCodigoSiconfi());
        contaContabil.setCodigoTCE(conta.getCodigoTCE());

        contaContabil.setNaturezaSaldo(conta.getNaturezaDoSaldo());
        contaContabil.setSubSistema(conta.getSubSistemaEnum());
        contaContabil.setNaturezaConta(conta.getNaturezaContaEnum());
        contaContabil.setNaturezaInformacao(conta.getNaturezaInformacaoEnum());
        if (conta.getContaEquivalente() != null) {
            ContaEquivalente contaEquivalente = new ContaEquivalente();
            contaEquivalente.setContaOrigem(conta.getContaEquivalente());
            contaEquivalente.setContaDestino(contaContabil);
            contaEquivalente.setDataRegistro(assistente.getDataOperacao());
            contaEquivalente.setNaturezaSaldo(NaturezaSaldoContaEquivalente.NORMAL);
            LocalDate localDate = DataUtil.criarDataPrimeiroDiaMes(1, exercicio.getAno());
            contaEquivalente.setVigencia(DataUtil.localDateToDate(localDate));
            if (contaContabil.getCodigoSICONFI() == null) {
                contaContabil.setCodigoSICONFI(conta.getContaEquivalente().getCodigoSICONFI());
            }
            if (contaContabil.getCodigoTCE() == null) {
                contaContabil.setCodigoTCE(conta.getContaEquivalente().getCodigoTCE());
            }
            Util.adicionarObjetoEmLista(contaContabil.getContasEquivalentes(), contaEquivalente);
        }

        codigoSemZerosAoFinal = getCodigoRemovendoUltimoNivel(contaContabil.getCodigoSemZerosAoFinal());
        contas = contaFacade.listaContasContabeis(codigoSemZerosAoFinal, exercicio);
        if (!contas.isEmpty()) {
            contaContabil.setSuperior(contas.get(0));
        }
        verificarSalvarCategoriaSuperior(contaContabil);
        return contaFacade.salvarRetornando(contaContabil);
    }

    private void verificarSalvarCategoriaSuperior(Conta conta) {
        Conta superior = conta.getSuperior();
        if (superior != null) {
            superior.setCategoria(CategoriaConta.SINTETICA);
            superior.setPermitirDesdobramento(Boolean.TRUE);
            contaFacade.salvar(superior);
        }
    }

    private void definirCategoriaConta(ContaVO contaVO, Conta conta) {
        conta.setCategoria(contaVO.getCategoriaConta());
        if (CategoriaConta.SINTETICA.equals(conta.getCategoria())) {
            conta.setPermitirDesdobramento(true);
        } else {
            conta.setPermitirDesdobramento(false);
        }
    }


    private Conta salvarContaDespesa(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Exercicio exercicio, PlanoDeContas planoDeContas) {
        String codigoSemZerosAoFinal;
        List<Conta> contas;
        List<Conta> contasDeDespesa = contaFacade.listaFiltrandoContaDespesa(conta.getCodigo(), exercicio);
        if (!contasDeDespesa.isEmpty()) {
            return contasDeDespesa.get(0);
        }
        ContaDespesa contaDespesa = new ContaDespesa();
        contaDespesa.setAtiva(Boolean.TRUE);
        contaDespesa.setCodigo(conta.getCodigo());
        contaDespesa.setDescricao(conta.getDescricao());
        definirCategoriaConta(conta, contaDespesa);
        contaDespesa.setExercicio(exercicio);
        contaDespesa.setPlanoDeContas(planoDeContas);
        contaDespesa.setDataRegistro(assistente.getDataOperacao());
        contaDespesa.setTipoContaDespesa(TipoContaDespesa.NAO_APLICAVEL);
        contaDespesa.setCodigoSICONFI(conta.getCodigoSiconfi());
        contaDespesa.setCodigoTCE(conta.getCodigoTCE());
        codigoSemZerosAoFinal = getCodigoRemovendoUltimoNivel(contaDespesa.getCodigoSemZerosAoFinal());
        contas = contaFacade.listaFiltrandoContaDespesa(codigoSemZerosAoFinal, exercicio);
        if (!contas.isEmpty()) {
            contaDespesa.setSuperior(contas.get(0));
        }
        verificarSalvarCategoriaSuperior(contaDespesa);
        return contaFacade.salvarRetornando(contaDespesa);
    }


    private Conta salvarContaReceita(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Exercicio exercicio, PlanoDeContas planoDeContas) {
        String codigoSemZerosAoFinal;
        List<Conta> contas;
        List<Conta> contasDeReceita = contaFacade.listaFiltrandoReceita(conta.getCodigo(), exercicio);
        if (!contasDeReceita.isEmpty()) {
            return contasDeReceita.get(0);
        }
        ContaReceita contaReceita = new ContaReceita();
        contaReceita.setAtiva(Boolean.TRUE);
        contaReceita.setCodigo(conta.getCodigo());
        contaReceita.setDescricao(conta.getDescricao());
        contaReceita.setTiposCredito(TiposCredito.NAO_APLICAVEL);
        definirCategoriaConta(conta, contaReceita);
        contaReceita.setExercicio(exercicio);
        contaReceita.setPlanoDeContas(planoDeContas);
        contaReceita.setDataRegistro(assistente.getDataOperacao());
        contaReceita.setCodigoSICONFI(conta.getCodigoSiconfi());
        contaReceita.setCodigoTCE(conta.getCodigoTCE());
        codigoSemZerosAoFinal = getCodigoRemovendoUltimoNivel(contaReceita.getCodigoSemZerosAoFinal());
        contas = contaFacade.listaFiltrandoReceita(codigoSemZerosAoFinal, exercicio);
        if (!contas.isEmpty()) {
            contaReceita.setSuperior(contas.get(0));
        }
        verificarSalvarCategoriaSuperior(contaReceita);
        return contaFacade.salvarRetornando(contaReceita);
    }

    private String getCodigoRemovendoUltimoNivel(String codigo) {
        if (codigo.contains(".")) {
            String toReturn = codigo.substring(0, codigo.lastIndexOf("."));
            String ultimoCodigo;
            while (!toReturn.isEmpty()) {
                ultimoCodigo = toReturn.substring(toReturn.lastIndexOf(".") + 1, toReturn.length());
                if (ultimoCodigo.length() == 2 ? "00".equals(ultimoCodigo) : "0".equals(ultimoCodigo)) {
                    toReturn = toReturn.substring(0, toReturn.substring(0, (toReturn.lastIndexOf(".") - 1)).lastIndexOf("."));
                } else {
                    return toReturn;
                }
            }
            return toReturn;
        }
        return codigo;
    }

    private void alterarContas(AssistenteImportacaoPlanoDeContas assistente, Exercicio exercicio, PlanoDeContas planoDeContas, List<Conta> contas) {
        adicionarMensagemNegrito(assistente, assistente.getContasAlteradas().size() + " contas alteradas...");
        for (ContaVO conta : assistente.getContasAlteradas()) {
            recuperarConta(assistente, exercicio, conta);
            Conta contaRecuperada = conta.getContaRecuperada();
            if (contaRecuperada != null) {
                contaRecuperada = salvarContaAlterandoAtributos(assistente, conta, contaRecuperada);
                Util.adicionarObjetoEmLista(contas, contaRecuperada);
            } else {
                Util.adicionarObjetoEmLista(contas, salvarNovaConta(assistente, conta, exercicio, planoDeContas));
            }
            contarMaisUmProcessado(assistente);
        }
    }

    private Conta salvarContaAlterandoAtributos(AssistenteImportacaoPlanoDeContas assistente, ContaVO conta, Conta contaRecuperada) {
        if (ClasseDaConta.CONTABIL.equals(assistente.getTipoConta().getClasseDaConta())) {
            ContaContabil cc = (ContaContabil) contaRecuperada;
            definirCategoriaConta(conta, cc);
            cc.setDescricao(conta.getDescricao());
            cc.setNaturezaSaldo(conta.getNaturezaDoSaldo());
            cc.setSubSistema(conta.getSubSistemaEnum());
            cc.setNaturezaConta(conta.getNaturezaContaEnum());
            cc.setNaturezaInformacao(conta.getNaturezaInformacaoEnum());
            cc.setCodigoTCE(conta.getCodigoTCE());
            cc.setCodigoSICONFI(conta.getCodigoSiconfi());
            contaRecuperada = contaFacade.salvarRetornando(cc);
        } else {
            Conta cc = contaRecuperada;
            definirCategoriaConta(conta, cc);
            cc.setDescricao(conta.getDescricao());
            cc.setCodigoTCE(conta.getCodigoTCE());
            cc.setCodigoSICONFI(conta.getCodigoSiconfi());
            contaRecuperada = contaFacade.salvarRetornando(cc);
        }
        return contaRecuperada;
    }

    public AssistenteImportacaoPlanoDeContas iniciarImportarDepara(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setMensagens(Lists.<String>newArrayList());
        assistente.setSituacao(SituacaoImportarPlanoDeContas.CONTAS_DEPARA);
        iniciarBarraProgresso(assistente, assistente.getDeparaContas().size());
        importarDepara(assistente);
        finalizar(assistente);
        return assistente;
    }

    private void iniciarBarraProgresso(AssistenteImportacaoPlanoDeContas assistente, Integer tamanho) {
        BarraProgressoItens barraProgressoItens = assistente.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        barraProgressoItens.setTotal(tamanho);
        assistente.setTotal(tamanho);
    }

    private void finalizar(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.setSituacao(SituacaoImportarPlanoDeContas.FINALIZADO);
        assistente.getBarraProgressoItens().finaliza();
    }

    private void importarDepara(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.getBarraProgressoItens().setMensagens("Salvando as contas de/para ....");
        Exercicio exercicio = assistente.getExercicio();
        Exercicio exercicioAnterior = assistente.getExercicioAnterior();

        for (DeparaContaVO deparaConta : assistente.getDeparaContas()) {
            ContaVO contaOrigem = deparaConta.getContaOrigem();
            ContaVO contaDestino = deparaConta.getContaDestino();

            if (contaDestino != null && contaOrigem != null) {
                recuperarConta(assistente, exercicio, contaDestino);
                recuperarConta(assistente, exercicioAnterior, contaOrigem);

                ContaEquivalente contaEquivalente = new ContaEquivalente();
                contaEquivalente.setContaOrigem(contaOrigem.getContaRecuperada());
                contaEquivalente.setContaDestino(contaDestino.getContaRecuperada());
                contaEquivalente.setDataRegistro(assistente.getDataOperacao());
                contaEquivalente.setNaturezaSaldo(NaturezaSaldoContaEquivalente.NORMAL);

                if (contaDestino.getContaRecuperada() != null) {
                    Conta contaRecuperada = contaDestino.getContaRecuperada();
                    contaRecuperada = contaFacade.recuperar(contaRecuperada.getId());
                    Boolean achou = Boolean.FALSE;
                    for (ContaEquivalente ce : contaRecuperada.getContasEquivalentes()) {
                        if (ce.getContaDestino().getId().equals(contaDestino.getContaRecuperada().getId())) {
                            achou = Boolean.TRUE;
                            break;
                        }
                    }
                    if (!achou) {
                        contaRecuperada.getContasEquivalentes().add(contaEquivalente);
                        contaFacade.salvar(contaRecuperada);
                    }
                }
                contarMaisUmProcessado(assistente);
            }
        }
    }

    private void adicionarMensagemNegrito(AssistenteImportacaoPlanoDeContas assistente, String mensagem) {
        assistente.getMensagens().add("<b>" + mensagem + "</b>");
    }

    private void adicionarMensagemNegritoVermelho(AssistenteImportacaoPlanoDeContas assistente, String mensagem) {
        assistente.getMensagens().add("<b> <font color='red'> " + mensagem + "</font></b>");
    }

    private void contarMaisUmProcessado(AssistenteImportacaoPlanoDeContas assistente) {
        assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
        assistente.conta();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }

    public TipoContaFacade getTipoContaFacade() {
        return tipoContaFacade;
    }
}
