/*
 * Codigo gerado automaticamente em Tue Apr 19 13:57:30 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.enums.NaturezaSaldoContaEquivalente;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.dao.DataIntegrityViolationException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class PlanoDeContasFacade extends AbstractFacade<PlanoDeContas> {

    @EJB
    private MiddleRHFacade middleRHFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private TipoContaFacade tipoContaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private CelulaDeInformacaoFacade celulaDeInformacaoFacade;
    @EJB
    private TipoContaExtraFacade tipoContaExtraFacade;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PlanoDeContasExercicioFacade planoDeContasExercicioFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private HashMap<Conta, Conta> mapaContas;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public PlanoDeContasFacade() {
        super(PlanoDeContas.class);
    }

    @Override
    public PlanoDeContas recuperar(Object id) {
        PlanoDeContas pdc;
        if (id instanceof PlanoDeContas) {
            pdc = (PlanoDeContas) id;
            Query q = getEntityManager().createQuery("from PlanoDeContas plc where plc=:paramId");
            q.setParameter("paramId", pdc);
            if (q.getResultList().isEmpty()) {
                pdc.setContas(new ArrayList<Conta>());
                return pdc;
            }
            pdc = (PlanoDeContas) q.getSingleResult();
        } else {
            Query q = getEntityManager().createQuery("from PlanoDeContas plc where plc.id=:paramId");
            q.setParameter("paramId", id);
            if (q.getResultList().isEmpty()) {
                return new PlanoDeContas();
            }
            pdc = (PlanoDeContas) q.getSingleResult();
        }
        pdc.getContas().size();
//
//        for (Conta a : pdc.getContas()) {
//            a = contaFacade.recuperar(a.getId());
//        }
        return pdc;
    }

    public Boolean verificaContasFilhas(Conta c) {
        Query consulta = em.createQuery("SELECT c FROM Conta c WHERE c.superior.id = :idConta", Conta.class);
        consulta.setParameter("idConta", c.getId());
        try {
            if (!consulta.getResultList().isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public PlanoDeContas recuperarContas(Long id) {
        Query consulta = em.createQuery(" select p from PlanoDeContas p where p.id = :id ");
        consulta.setParameter("id", id);

        PlanoDeContas p = (PlanoDeContas) consulta.getSingleResult();
        p.getContas().size();

        for (Conta conta : p.getContas()) {
            conta.getContasEquivalentes().size();
        }

        return p;
    }

    public PlanoDeContas salva(PlanoDeContas selecionado) {

        if (selecionado.getId() == null) {
            em.persist(selecionado);
        } else {
            selecionado = em.merge(selecionado);
        }

        for (Conta conta : selecionado.getContas()) {
            for (ContaEquivalente contaEquivalente : conta.getContasEquivalentes()) {
                contaEquivalente.setContaOrigem(conta);
                if (contaEquivalente.getId() == null) {
                    em.persist(contaEquivalente);
                } else {
                    contaEquivalente = em.merge(contaEquivalente);
                }
            }
        }

        return selecionado;
    }

    @Override
    public void remover(PlanoDeContas entity) {

        super.remover(entity);
//        for (Conta conta : entity.getContas()) {
//            for (ContaEquivalente contaEquivalente : conta.getContasEquivalentes()) {
//                removeContaEquivalente(contaEquivalente);
//            }
//        }
    }

    public void removeContaEquivalente(ContaEquivalente conta) {
        em.remove(conta);
    }

    public void excluiContas(List<Conta> contas) {
        for (Conta c : contas) {
            getEntityManager().remove(c);
        }

    }

    public void salvarRemovendoAltera(PlanoDeContas pc, Conta excluida) {
        Conta c = new Conta();
//        try {
        if (excluida.getSuperior() != null) {
            c = contaFacade.recuperar(excluida.getSuperior().getId());
//            c.getFilhos().remove(excluida);
            getEntityManager().merge(c);
        } else {
            c = contaFacade.recuperar(excluida.getId());
            pc.getContas().remove(c);
            getEntityManager().remove(c);
            getEntityManager().merge(pc);
        }
    }

    public List<Conta> recuperaContasPorPlano(PlanoDeContas pdc) {

        String hql = "from Conta c where c.planoDeContas=:param ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", pdc);
        return q.getResultList();
    }

    public List<ContaDespesa> recuperaContasDespesaPorPlano(PlanoDeContas pdc) {
        String hql = "from ContaDespesa c where c.planoDeContas=:param ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", pdc);
        return q.getResultList();
    }

    public List<PlanoDeContas> recuperaPlanosPorExercicio(Exercicio ex) {

        String sql = " SELECT B.* "
            + " FROM PLANODECONTASEXERCICIO A"
            + " INNER JOIN PLANODECONTAS B ON B.ID = A.PLANOCONTABIL_ID "
            + "                            OR B.ID = A.PLANODEDESPESAS_ID "
            + "                            OR B.ID = A.PLANODEDESTINACAODERECURSOS_ID "
            + "                            OR B.ID = A.PLANOEXTRAORCAMENTARIO_ID "
            + "                            OR B.ID = A.PLANODERECEITAS_ID "
            + " WHERE A.EXERCICIO_ID = :exercicio";
        Query q = getEntityManager().createNativeQuery(sql, PlanoDeContas.class);
        q.setParameter("exercicio", ex.getId());
        return q.getResultList();
    }

    public List<PlanoDeContas> recuperarPlanoPorTipoContaEExercicio(TipoConta tipo, Exercicio exercicio) {
        String sql = " SELECT * FROM PLANODECONTAS pc " +
            " inner join tipoconta tipo on pc.tipoconta_id = tipo.id " +
            " WHERE tipo.classedaconta = :classe " +
            " and pc.exercicio_id = :exercicio ";
        Query q = getEntityManager().createNativeQuery(sql, PlanoDeContas.class);
        q.setParameter("classe", tipo.getClasseDaConta().name());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<PlanoDeContas> buscarPlanosPorClasseDaContaEExercicio(ClasseDaConta classeDaConta, Exercicio exercicio) {
        String sql = " SELECT * FROM PLANODECONTAS pc " +
            " inner join tipoconta tipo on pc.tipoconta_id = tipo.id " +
            " WHERE tipo.classedaconta = :classe " +
            " and pc.exercicio_id = :exercicio ";
        Query q = getEntityManager().createNativeQuery(sql, PlanoDeContas.class);
        q.setParameter("classe", classeDaConta.name());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public PlanoDeContas recuperaPlanoPorTipoContaExercicio(TipoConta tipo, Exercicio ex) {
        try {
            Query q = getEntityManager().createQuery("select p from PlanoDeContas p where p.exercicio=:exerc and p.tipoConta=:tipo");
            q.setParameter("tipo", tipo);
            q.setParameter("exerc", ex);
            return (PlanoDeContas) q.getSingleResult();
        } catch (NoResultException exc) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar o Plano de Contas cadastrado para o Exercício de " + ex.getAno() + " e Tipo " + tipo.getDescricao() + ", não foi encontrado nenhum registro! ");
        } catch (NonUniqueResultException exc) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar o Plano de Contas cadastrado para o Exercício de " + ex.getAno() + " e  Tipo " + tipo.getDescricao() + ", foi encontrado mais de um registro! ");
        }
    }

    public PlanoDeContas recuperaPlanoDeContasPorTipoContaExercicio(TipoConta tipo, Exercicio ex) {
        try {
            Query q = getEntityManager().createQuery("select p from PlanoDeContas p where p.exercicio=:exerc and p.tipoConta=:tipo");
            q.setParameter("tipo", tipo);
            q.setParameter("exerc", ex);
            return (PlanoDeContas) q.getSingleResult();
        } catch (NoResultException exc) {
            return null;
        } catch (NonUniqueResultException exc) {
            return null;
        }
    }

    public List<Exercicio> recuperaPlanosPorTipoEAnoExercicio(String parte, String anoVigente, ClasseDaConta classe) {
        String sql = " SELECT EX.* FROM EXERCICIO EX  "
            + " WHERE EX.ANO like :parte AND EX.ANO > :ANOVIGENTE AND ID NOT IN "
            + " (SELECT EX.ID FROM EXERCICIO EX "
            + "  INNER JOIN PLANODECONTASEXERCICIO PCE ON EX.ID = PCE.EXERCICIO_ID "
            + "  INNER JOIN PLANODECONTAS PC ON PC.ID = PCE.PLANOAUXILIAR_ID "
            + "                              OR PC.ID = PCE.PLANOCONTABIL_ID "
            + "                              OR PC.ID = PCE.PLANODEDESPESAS_ID "
            + "                              OR PC.ID = PCE.PLANODERECEITAS_ID "
            + "                              OR PC.ID = PCE.PLANOEXTRAORCAMENTARIO_ID "
            + "                              OR PC.ID = PCE.PLANODEDESTINACAODERECURSOS_ID "
            + "   INNER JOIN TIPOCONTA TC ON PC.TIPOCONTA_ID = TC.ID "
            + "   WHERE TC.CLASSEDACONTA LIKE :CLASSECONTA ) "
            + " order by ex.ano ";
        Query q = getEntityManager().createNativeQuery(sql, Exercicio.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("ANOVIGENTE", anoVigente);
        q.setParameter("CLASSECONTA", classe.name());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<Exercicio>) q.getResultList();
        }
    }

    public Conta recuperaContaPeloCodigo(Conta c, PlanoDeContas pla) {
        String hql = "select c from Conta c join c.planoDeContas pl where pl.id = :plano and c.codigo like :codigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", c.getCodigo() + "%");
        q.setParameter("plano", pla.getId());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return new Conta();
        } else {
            return (Conta) q.getSingleResult();
        }
    }

    public String salvaRemovendoContas(PlanoDeContas planoDeContas, List<Conta> removeContas, List<Conta> listaContasNovas) {
        for (Conta c : removeContas) {
            if (c.getId() != null) {
                em.remove(em.find(Conta.class, c.getId()));
            }
        }
        for (Conta c : listaContasNovas) {
            if (c.getId() != null) {
                em.merge(c);
            } else {
                em.persist(c);
            }
        }
        em.merge(planoDeContas);
        return "lista.xhtml";
    }

    public CelulaDeInformacaoFacade getCelulaDeInformacaoFacade() {
        return celulaDeInformacaoFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public TipoContaFacade getTipoContaFacade() {
        return tipoContaFacade;
    }

    public ConfiguracaoPlanejamentoPublicoFacade getConfiguracaoPlanejamentoPublicoFacade() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    public TipoContaExtraFacade getTipoContaExtraFacade() {
        return tipoContaExtraFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PlanoDeContasExercicioFacade getPlanoDeContasExercicioFacade() {
        return planoDeContasExercicioFacade;
    }

    @Override
    public void salvarNovo(PlanoDeContas entity) {
        validarPlanoDeContasExercicio(entity);
        super.salvarNovo(entity);
    }

    public void salvarPlanoDuplicado(PlanoDeContas entity) {
        validarPlanoDeContasExercicio(entity);

        if (isTipoDoPlanoDeContas(entity, ClasseDaConta.DESTINACAO)) {
            salvarFontesDeRecursosNovas(entity);
        }

        getEntityManager().persist(entity);

        corrigeSuperior(entity.getContas(), entity);

        getEntityManager().merge(entity);
    }

    private FonteDeRecursos recuperarFonteDeRecurso(ContaDeDestinacao contaDeDestinacaoOrigem, Exercicio ex) {
        FonteDeRecursos fonteDeRecursos = contaDeDestinacaoOrigem.getFonteDeRecursos();
        if (fonteDeRecursos == null) {
            return null;
        }

        FonteDeRecursos novaFonte = getFonteDeRecursosFacade().recuperaPorCodigoExericio(fonteDeRecursos, ex);

        if (novaFonte == null) {
            fonteDeRecursos = getFonteDeRecursosFacade().recuperar(fonteDeRecursos.getId());
            novaFonte = (FonteDeRecursos) Util.clonarObjeto(fonteDeRecursos);
            novaFonte.setId(null);
            novaFonte.setExercicio(ex);
            novaFonte.setFontesEquivalentes(Lists.<FonteDeRecursosEquivalente>newArrayList());
            novaFonte.setCodigosCOs(Lists.<FonteCodigoCO>newArrayList());
            FonteDeRecursosEquivalente fonteDeRecursosEquivalente = new FonteDeRecursosEquivalente();
            fonteDeRecursosEquivalente.setFonteDeRecursosDestino(novaFonte);
            fonteDeRecursosEquivalente.setFonteDeRecursosOrigem(fonteDeRecursos);
            Util.adicionarObjetoEmLista(novaFonte.getFontesEquivalentes(), fonteDeRecursosEquivalente);
            for (FonteCodigoCO fonteCodigoCO : fonteDeRecursos.getCodigosCOs()) {
                FonteCodigoCO novo = new FonteCodigoCO();
                novo.setFonteDeRecursos(novaFonte);
                novo.setCodigoCO(fonteCodigoCO.getCodigoCO());
                novaFonte.getCodigosCOs().add(novo);
            }
            getEntityManager().persist(novaFonte);
        }
        return getFonteDeRecursosFacade().recuperaPorCodigoExericio(fonteDeRecursos, ex);
    }

    private void gerarNovoPlanoDeConta(PlanoDeContas planoDeContasOriginal, Exercicio novo) {
        mapaContas = new HashMap();
        PlanoDeContas toReturn = new PlanoDeContas();
        toReturn.setDataRegistro(new Date());
        toReturn.setExercicio(novo);
        TipoConta tipoConta = tipoContaFacade.buscarTipoContaPorClassesExercicio(planoDeContasOriginal.getTipoConta().getClasseDaConta(), novo);
        if (tipoConta != null) {
            toReturn.setTipoConta(tipoConta);
        } else {
            tipoConta = new TipoConta();
            tipoConta.setExercicio(novo);
            tipoConta.setClasseDaConta(planoDeContasOriginal.getTipoConta().getClasseDaConta());
            tipoConta.setDescricao(planoDeContasOriginal.getTipoConta().getDescricao());
            tipoConta.setMascara(planoDeContasOriginal.getTipoConta().getMascara());
            tipoConta = tipoContaFacade.salvarRetornando(tipoConta);
            toReturn.setTipoConta(tipoConta);
        }
        toReturn.setTipoConta(planoDeContasOriginal.getTipoConta());
        toReturn.setDescricao(planoDeContasOriginal.getDescricao());

        getEntityManager().merge(toReturn);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void salvarPlanoDuplicado(PlanoDeContas planoDeContasOriginal, Exercicio novoExercicio) {
        mapaContas = new HashMap();
        PlanoDeContas novoPlano = recuperaPlanoDeContasPorTipoContaExercicio(planoDeContasOriginal.getTipoConta(), novoExercicio);
        if (novoPlano == null) {
            novoPlano = new PlanoDeContas();
            novoPlano.setDataRegistro(sistemaFacade.getDataOperacao());
            novoPlano.setExercicio(novoExercicio);
            TipoConta tipoConta = tipoContaFacade.buscarTipoContaPorClassesExercicio(planoDeContasOriginal.getTipoConta().getClasseDaConta(), novoExercicio);
            if (tipoConta != null) {
                novoPlano.setTipoConta(tipoConta);
            } else {
                tipoConta = new TipoConta();
                tipoConta.setExercicio(novoExercicio);
                tipoConta.setClasseDaConta(planoDeContasOriginal.getTipoConta().getClasseDaConta());
                tipoConta.setDescricao(planoDeContasOriginal.getTipoConta().getDescricao());
                tipoConta.setMascara(planoDeContasOriginal.getTipoConta().getMascara());
                tipoConta = tipoContaFacade.salvarRetornando(tipoConta);
                novoPlano.setTipoConta(tipoConta);
            }
            novoPlano.setDescricao(planoDeContasOriginal.getDescricao());
            novoPlano = getEntityManager().merge(novoPlano);
        }
        novoPlano.getContas().addAll(percorrerContasParaDuplicar(planoDeContasOriginal, novoPlano, novoExercicio));
        novoPlano = getEntityManager().merge(novoPlano);
        validarPlanoDeContasExercicio(novoPlano);
    }

    public PlanoDeContas salvarPlanoDuplicadoRetornando(PlanoDeContas planoDeContasOriginal, Exercicio novo) {
        validaPLanoDeContaSendoDuplicado(planoDeContasOriginal, novo);
        gerarNovoPlanoDeConta(planoDeContasOriginal, novo);
        PlanoDeContas novoPlano = recuperaPlanoPorTipoContaExercicio(planoDeContasOriginal.getTipoConta(), novo);
        novoPlano.getContas().addAll(percorreContasParaDuplicar(planoDeContasOriginal, novoPlano, novo));
        validarPlanoDeContasExercicio(novoPlano);
        novoPlano = getEntityManager().merge(novoPlano);

        return novoPlano;
    }

    private List<Conta> percorrerContasParaDuplicar(PlanoDeContas planoDeContasOriginal, PlanoDeContas planoDeContasNovo, Exercicio exercicioNovo) {
        List<Conta> contas = new ArrayList<>();
        Collections.sort(planoDeContasOriginal.getContas());
        Conta contaParaAlterar = null;
        for (Conta contaOriginal : planoDeContasOriginal.getContas()) {
            Boolean controle = true;
            for (Conta contaNova : planoDeContasNovo.getContas()) {
                if (contaNova.getCodigo().equals(contaOriginal.getCodigo())) {
                    contaParaAlterar = contaNova;
                    controle = false;
                    break;
                }
            }
            if (controle) {
                contas.add(triagemTipoContaEplanoDeContas(contaOriginal, planoDeContasOriginal.getTipoConta(), exercicioNovo, planoDeContasNovo));
            } else {
                contas.add(triagemTipoContaEplanoDeContasAlterandoContas(contaOriginal, contaParaAlterar, planoDeContasOriginal.getTipoConta(), exercicioNovo, planoDeContasNovo));
            }
        }
        return contas;
    }

    private List<Conta> percorreContasParaDuplicar(PlanoDeContas planoDeContasOriginal, PlanoDeContas planoDeContasNovo, Exercicio novo) {
        List<Conta> contas = new ArrayList<>();
        Collections.sort(planoDeContasOriginal.getContas());
        for (Conta c : planoDeContasOriginal.getContas()) {
            contas.add(triagemTipoContaEplanoDeContas(c, planoDeContasOriginal.getTipoConta(), novo, planoDeContasNovo));
        }
        return contas;
    }

    private Conta retornaSuperior(Conta contaSuperiorAntiga) {
        return mapaContas.get(contaSuperiorAntiga);
    }

    private Conta configuraCopia(Conta novaConta, Conta contaVelha, ContaEquivalente cev, Exercicio ex, PlanoDeContas novoPlano) {
        novaConta.setDataRegistro(new Date());
        novaConta.setPlanoDeContas(novoPlano);
        novaConta.setId(null);
        novaConta.setExercicio(ex);
        if (cev.getContaOrigem() != null) {
            Conta superior = retornaSuperior(cev.getContaOrigem().getSuperior());
            novaConta.setSuperior(superior);
        }
        mapaContas.put(contaVelha, novaConta);

        return novaConta;
    }

    private ContaEquivalente getContaEquivalente(Conta novaConta, Exercicio ex) {
        ContaEquivalente contaEquivalente = new ContaEquivalente();
        contaEquivalente.setContaDestino(novaConta);
        contaEquivalente.setNaturezaSaldo(NaturezaSaldoContaEquivalente.NORMAL);
        contaEquivalente.setDataRegistro(new Date());
        contaEquivalente.setVigencia((new DataUtil().montaData(01, 0, ex.getAno())).getTime());
        return contaEquivalente;
    }

    private void configurarContaCorrespondente(ContaReceita contaNova, ContaReceita contaOriginal, Exercicio ex, PlanoDeContas novoPlano) {
        ContaReceita contaCorrespondenteNova;
        if (contaOriginal.getCorrespondente() != null) {
            if (mapaContas.get(contaOriginal.getCorrespondente()) != null) {
                contaCorrespondenteNova = (ContaReceita) mapaContas.get(contaOriginal.getCorrespondente());
            } else {
                contaCorrespondenteNova = (ContaReceita) Util.clonarObjeto(contaOriginal.getCorrespondente());
                contaCorrespondenteNova.setId(null);
                contaCorrespondenteNova.setExercicio(ex);
                if (novoPlano != null) {
                    contaCorrespondenteNova.setPlanoDeContas(novoPlano);
                }
                Conta superior = retornaSuperior(contaOriginal.getCorrespondente().getSuperior());
                contaCorrespondenteNova.setSuperior(superior);
                mapaContas.put(contaOriginal.getCorrespondente(), contaCorrespondenteNova);
            }
            contaNova.setCorrespondente(contaCorrespondenteNova);
        }
    }

    private Conta triagemTipoContaEplanoDeContas(Conta conta, TipoConta tipoConta, Exercicio ex, PlanoDeContas novoPlano) {
        Conta novaConta = (Conta) Util.clonarObjeto(conta);
        novaConta.limpaContasEquivalentesEntidade();
        if (ClasseDaConta.DESPESA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = criarContaEquivalente(conta, ex, novaConta);
            novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
        } else if (ClasseDaConta.RECEITA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = criarContaEquivalente(conta, ex, novaConta);
            if (mapaContas.get(conta) != null) {
                novaConta = mapaContas.get(conta);
            } else {
                novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
            }
            configurarContaCorrespondente((ContaReceita) novaConta, (ContaReceita) conta, ex, novoPlano);
        } else if (ClasseDaConta.AUXILIAR.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = criarContaEquivalente(conta, ex, novaConta);
            novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
        } else if (ClasseDaConta.EXTRAORCAMENTARIA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = criarContaEquivalente(conta, ex, novaConta);
            novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
        } else if (ClasseDaConta.CONTABIL.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = criarContaEquivalente(conta, ex, novaConta);
            novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
        } else if (ClasseDaConta.DESTINACAO.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente cev = getContaEquivalenteDeClasseDestinacao(conta, ex, novaConta);
            novaConta = configuraCopia(novaConta, conta, cev, ex, novoPlano);
        } else {
            throw new ExcecaoNegocioGenerica("Erro ao duplicar o plano de contas. O tipo de conta: <b>" + tipoConta.getDescricao() + "</b> não está configurado para a cópia.");
        }
        mapaContas.put(conta, novaConta);
        return novaConta;
    }

    private ContaEquivalente getContaEquivalenteDeClasseDestinacao(Conta conta, Exercicio ex, Conta novaConta) {
        ContaEquivalente cev = getContaEquivalente(novaConta, ex);
        FonteDeRecursos fonteDeRecursosNova = recuperarFonteDeRecurso((ContaDeDestinacao) novaConta, ex);
        ((ContaDeDestinacao) novaConta).setFonteDeRecursos(fonteDeRecursosNova);
        cev.setContaOrigem(conta);
        novaConta.getContasEquivalentes().add(cev);
        return cev;
    }

    private ContaEquivalente criarContaEquivalente(Conta conta, Exercicio ex, Conta novaConta) {
        ContaEquivalente cev = getContaEquivalente(novaConta, ex);
        cev.setContaOrigem(conta);
        Util.adicionarObjetoEmLista(novaConta.getContasEquivalentes(), cev);
        return cev;
    }

    private Conta triagemTipoContaEplanoDeContasAlterandoContas(Conta contaOriginal, Conta contaParaAlterar, TipoConta tipoConta, Exercicio ex, PlanoDeContas novoPlano) {
        contaParaAlterar = (Conta) Util.clonarObjeto(contaParaAlterar);
        contaParaAlterar.limpaContasEquivalentesEntidade();
        if (ClasseDaConta.DESTINACAO.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = getContaEquivalenteDeClasseDestinacao(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configuraAlteracaoContaDestinacao(contaParaAlterar, contaOriginal, ex, contaEquivalente);
            FonteDeRecursos fonteDeRecursosNova = recuperarFonteDeRecurso((ContaDeDestinacao) contaParaAlterar, ex);
            ((ContaDeDestinacao) contaParaAlterar).setFonteDeRecursos(fonteDeRecursosNova);
        } else if (ClasseDaConta.DESPESA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = criarContaEquivalente(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configuraAlteracaoContaDespesa(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        } else if (ClasseDaConta.CONTABIL.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = criarContaEquivalente(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configuraAlteracaoContaContabil(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        } else if (ClasseDaConta.RECEITA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = criarContaEquivalente(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configurarAlteracaoContaReceita(contaParaAlterar, contaOriginal, ex, contaEquivalente, novoPlano);
        } else if (ClasseDaConta.AUXILIAR.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = criarContaEquivalente(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configuraAlteracaoContaAuxiliar(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        } else if (ClasseDaConta.EXTRAORCAMENTARIA.equals(tipoConta.getClasseDaConta())) {
            ContaEquivalente contaEquivalente = criarContaEquivalente(contaOriginal, ex, contaParaAlterar);
            contaParaAlterar = configuraAlteracaoContaExtra(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        } else {
            throw new ExcecaoNegocioGenerica("Erro ao duplicar o plano de contas. O tipo de conta: <b>" + tipoConta.getDescricao() + "</b> não está configurado para a cópia.");
        }
        return contaParaAlterar;
    }

    private Conta configuraAlteracaoContaDestinacao(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaDeDestinacao contaDestinacaoParaAlterar = ((ContaDeDestinacao) contaParaAlterar);
        ContaDeDestinacao contaDestinacaoOriginal = ((ContaDeDestinacao) contaOriginal);
        contaDestinacaoParaAlterar.setFonteDeRecursos(contaDestinacaoOriginal.getFonteDeRecursos());
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private Conta configuraAlteracaoContaDespesa(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaDespesa contaDespesaParaAlterar = ((ContaDespesa) contaParaAlterar);
        ContaDespesa contaDespesaOriginal = ((ContaDespesa) contaOriginal);
        contaDespesaParaAlterar.setTipoContaDespesa(contaDespesaOriginal.getTipoContaDespesa());
        contaDespesaParaAlterar.setCodigoReduzido(contaDespesaOriginal.getCodigoReduzido());
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private Conta configuraAlteracaoContaAuxiliar(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaAuxiliar contaAuxiliarParaAlterar = ((ContaAuxiliar) contaParaAlterar);
        ContaAuxiliar contaAuxiliarOriginal = ((ContaAuxiliar) contaOriginal);
        contaAuxiliarParaAlterar.setContaContabil(contaAuxiliarOriginal.getContaContabil());
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private Conta configurarAlteracaoContaReceita(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente, PlanoDeContas novoPlano) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaReceita contaReceitaParaAlterar = ((ContaReceita) contaParaAlterar);
        ContaReceita contaReceitaOriginal = ((ContaReceita) contaOriginal);
        contaReceitaParaAlterar.setCodigoReduzido(contaReceitaOriginal.getCodigoReduzido());
        contaReceitaParaAlterar.setTiposCredito(contaReceitaOriginal.getTiposCredito());
        contaReceitaParaAlterar.setDescricaoReduzida(contaReceitaOriginal.getDescricaoReduzida());
        contaReceitaParaAlterar.setGeraCreditoArrecadacao(contaReceitaOriginal.getGeraCreditoArrecadacao());
        configurarContaCorrespondente(contaReceitaParaAlterar, contaReceitaOriginal, ex, novoPlano);
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private Conta configuraAlteracaoContaExtra(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaExtraorcamentaria contaExtraParaAlterar = ((ContaExtraorcamentaria) contaParaAlterar);
        ContaExtraorcamentaria contaExtraOriginal = ((ContaExtraorcamentaria) contaOriginal);
        contaExtraParaAlterar.setContaContabil(contaExtraOriginal.getContaContabil());
        contaExtraParaAlterar.setContaExtraorcamentaria(contaExtraOriginal.getContaExtraorcamentaria());
        contaExtraParaAlterar.setTipoContaExtraorcamentaria(contaExtraOriginal.getTipoContaExtraorcamentaria());
        contaExtraParaAlterar.setTipoRetencao(contaExtraOriginal.getTipoRetencao());
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private Conta configuraAlteracaoContaContabil(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        configurarAlteracaoContaAtributoComuns(contaParaAlterar, contaOriginal, ex, contaEquivalente);
        ContaContabil contaContabilParaAlterar = ((ContaContabil) contaParaAlterar);
        ContaContabil contaContabilOriginal = ((ContaContabil) contaOriginal);
        contaContabilParaAlterar.setNaturezaConta(contaContabilOriginal.getNaturezaConta());
        contaContabilParaAlterar.setNaturezaInformacao(contaContabilOriginal.getNaturezaInformacao());
        contaContabilParaAlterar.setSubSistema(contaContabilOriginal.getSubSistema());
        contaContabilParaAlterar.setNaturezaSaldo(contaContabilOriginal.getNaturezaSaldo());
        mapaContas.put(contaOriginal, contaParaAlterar);

        return contaParaAlterar;
    }

    private void configurarAlteracaoContaAtributoComuns(Conta contaParaAlterar, Conta contaOriginal, Exercicio ex, ContaEquivalente contaEquivalente) {
        contaParaAlterar.setDataRegistro(new Date());
        contaParaAlterar.setCodigoTCE(contaOriginal.getCodigoTCE());
        contaParaAlterar.setDescricao(contaOriginal.getDescricao());
        contaParaAlterar.setAtiva(contaOriginal.getAtiva());
        contaParaAlterar.setTipoContaContabil(contaOriginal.getTipoContaContabil());
        contaParaAlterar.setFuncao(contaOriginal.getFuncao());
        contaParaAlterar.setCategoria(contaOriginal.getCategoria());
        contaParaAlterar.setRubrica(contaOriginal.getRubrica());
        contaParaAlterar.setPermitirDesdobramento(contaOriginal.getPermitirDesdobramento());
        contaParaAlterar.setCodigoSICONFI(contaOriginal.getCodigoSICONFI());
        Conta superior = retornaSuperior(contaEquivalente.getContaOrigem().getSuperior());
        contaParaAlterar.setSuperior(superior);
    }

    public void validaPLanoDeContaSendoDuplicado(PlanoDeContas planoDeContasOriginal, Exercicio novo) throws ExcecaoNegocioGenerica {
        try {
            Query q = getEntityManager().createQuery("select p from  PlanoDeContas p where p.exercicio=:ex and p.tipoConta=:tp");
            q.setParameter("tp", planoDeContasOriginal.getTipoConta());
            q.setParameter("ex", novo);

            if (q.getSingleResult() != null) {
                throw new ExcecaoNegocioGenerica("Já existe um Plano de Contas cadastrado para o Exercício de " + novo.getAno() + ", do Tipo " + planoDeContasOriginal.getTipoConta().getDescricao() + ".");
            }

        } catch (NoResultException ex) {
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Já existe mais de um Plano de Contas cadastrado para o Exercício de " + novo.getAno() + ", do Tipo " + planoDeContasOriginal.getTipoConta().getDescricao() + ".");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao duplicar o Plano de Contas, Exercício de " + novo.getAno() + ", do Tipo " + planoDeContasOriginal.getTipoConta().getDescricao() + ".: " + ex.getMessage());
        }
    }

    private void salvarFontesDeRecursosNovas(PlanoDeContas planoDeContas) {
        List<Conta> contas = planoDeContas.getContas();
        for (Conta conta : contas) {
            if (conta instanceof ContaDeDestinacao) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) conta;
                if (contaDeDestinacao.getFonteDeRecursos() != null) {
                    if (contaDeDestinacao.getFonteDeRecursos().getId() == null) {
                        contaDeDestinacao.setFonteDeRecursos(fonteDeRecursosFacade.salvarRetornando(contaDeDestinacao.getFonteDeRecursos()));
                    }
                }
            }
        }
    }

    private void corrigeSuperior(List<Conta> contas, PlanoDeContas planoDeContas) {
        for (Conta c : contas) {
            Conta superior = null;
            if (c.getSuperior() != null) {
                superior = recuperaContaPeloCodigo(c.getContasEquivalentes().get(0).getContaDestino().getSuperior(), planoDeContas);
            }
            c.setSuperior(superior);
        }
    }

    @Override
    public void salvar(PlanoDeContas entity) {
        validarPlanoDeContasExercicio(entity);
        super.salvar(entity);
    }

    public PlanoDeContas meuSalvar(Conta c) throws ExcecaoNegocioGenerica {
        if (c.getId() == null && c.getPlanoDeContas().getId() == null) {
            PlanoDeContas p = em.merge((c.getPlanoDeContas()));
            validarPlanoDeContasExercicio(p);
            return p;
        } else {
            validarPlanoDeContasExercicio(c.getPlanoDeContas());
            contaFacade.salvar(c);
            return c.getPlanoDeContas();
        }
    }

    private void validarPlanoDeContasExercicio(PlanoDeContas selecionado) throws ExcecaoNegocioGenerica {
        PlanoDeContasExercicio planoDeContasExercicio = getPlanoDeContasExercicioFacade().recuperarPorExercicio(selecionado.getExercicio());

        if (planoDeContasExercicio == null) {
            salvarNovoPlanoDeContasExercicio(selecionado, planoDeContasExercicio);
        } else {
            verificarPlanoDeContasESalvar(selecionado, planoDeContasExercicio);
        }
    }

    private void verificarPlanoDeContasESalvar(PlanoDeContas selecionado, PlanoDeContasExercicio planoDeContasExercicio) {
        String mensagem = " Já existe um Plano de Contas " + selecionado.getTipoConta().getClasseDaConta().getDescricao() + " para o exercício de " + selecionado.getExercicio().getAno() + ".";
        if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.CONTABIL)) {
            if (planoDeContasExercicio.getPlanoContabil() == null) {
                planoDeContasExercicio.setPlanoContabil(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoContabil())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.AUXILIAR)) {
            if (planoDeContasExercicio.getPlanoAuxiliar() == null) {
                planoDeContasExercicio.setPlanoAuxiliar(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoAuxiliar())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.DESPESA)) {
            if (planoDeContasExercicio.getPlanoDeDespesas() == null) {
                planoDeContasExercicio.setPlanoDeDespesas(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoDeDespesas())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.DESTINACAO)) {
            if (planoDeContasExercicio.getPlanoDeDestinacaoDeRecursos() == null) {
                planoDeContasExercicio.setPlanoDeDestinacaoDeRecursos(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoDeDestinacaoDeRecursos())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.EXTRAORCAMENTARIA)) {
            if (planoDeContasExercicio.getPlanoExtraorcamentario() == null) {
                planoDeContasExercicio.setPlanoExtraorcamentario(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoExtraorcamentario())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.RECEITA)) {
            if (planoDeContasExercicio.getPlanoDeReceitas() == null) {
                planoDeContasExercicio.setPlanoDeReceitas(selecionado);
                planoDeContasExercicioFacade.salvar(planoDeContasExercicio);
            } else {
                if (!selecionado.equals(planoDeContasExercicio.getPlanoDeReceitas())) {
                    throw new ExcecaoNegocioGenerica(mensagem);
                }
            }
        }
    }

    private void salvarNovoPlanoDeContasExercicio(PlanoDeContas selecionado, PlanoDeContasExercicio planoDeContasExercicio) {
        planoDeContasExercicio = new PlanoDeContasExercicio();
        planoDeContasExercicio.setExercicio(selecionado.getExercicio());
        if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.CONTABIL)) {
            planoDeContasExercicio.setPlanoContabil(selecionado);
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.AUXILIAR)) {
            planoDeContasExercicio.setPlanoAuxiliar(selecionado);
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.DESPESA)) {
            planoDeContasExercicio.setPlanoDeDespesas(selecionado);
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.DESTINACAO)) {
            planoDeContasExercicio.setPlanoDeDestinacaoDeRecursos(selecionado);
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.EXTRAORCAMENTARIA)) {
            planoDeContasExercicio.setPlanoExtraorcamentario(selecionado);
        } else if (isTipoDoPlanoDeContas(selecionado, ClasseDaConta.RECEITA)) {
            planoDeContasExercicio.setPlanoDeReceitas(selecionado);
        }
        em.persist(planoDeContasExercicio);
    }

    private boolean isTipoDoPlanoDeContas(PlanoDeContas planoDeContas, ClasseDaConta classeDaConta) {
        return planoDeContas.getTipoConta().getClasseDaConta().equals(classeDaConta);
    }

    public boolean verificaCodigoContaPlanoDeContas(Conta c) {
        if (c.getPlanoDeContas().getId() == null) {
            return true;
        }
        String hql = "select c from Conta c where c.planoDeContas=:idPlanoDeContas and c.ativa=true and c.codigo = :codigoConta ";
        if (c.getId() != null) {
            hql += "and c.id <> :idConta";
        }
        Query consulta = em.createQuery(hql);
        consulta.setParameter("idPlanoDeContas", c.getPlanoDeContas());
        consulta.setParameter("codigoConta", c.getCodigo());
        if (c.getId() != null) {
            consulta.setParameter("idConta", c.getId());
        }
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void removerConta(Conta conta) {
        try {
//            conta = em.find(Conta.class, conta);
            em.remove(conta);
        } catch (DataIntegrityViolationException d) {
            throw new ExcecaoNegocioGenerica(d.getMessage(), d.getCause());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<OrigemContaContabil> getOrigemContaContabil(Conta conta) {
        Query consulta = em.createQuery("select occ from OrigemContaContabil  occ"
            + " where ("
            + "        occ.contaContabil = :conta or"
            + "        occ.contaInterEstado = :conta or"
            + "        occ.contaInterMunicipal = :conta or"
            + "        occ.contaInterUniao = :conta or"
            + "        occ.contaIntra = :conta"
            + " )", OrigemContaContabil.class).setParameter("conta", conta);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<OrigemContaContabil>();
        }
    }

    public List<OCCConta> getOrigemContaContabilOccConta(Conta conta) {
        Query consulta = em.createQuery("select occ from OCCConta  occ"
            + " where ("
            + "        occ.contaOrigem = :conta"
            + " )", OCCConta.class).setParameter("conta", conta);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<OCCConta>();
        }
    }

    public List<EventoContabil> getEventosContabil(Conta conta) {
        Query consulta = em.createQuery("select e from EventoContabil e "
            + " inner join fetch e.itemEventoCLPs item"
            + " inner join item.clp clp"
            + " inner join clp.lcps lcp"
            + " where ("
            + "        lcp.contaCredito = :conta or"
            + "        lcp.contaCreditoInterEstado = :conta or"
            + "        lcp.contaCreditoInterMunicipal = :conta or"
            + "        lcp.contaCreditoInterUniao = :conta or"
            + "        lcp.contaCreditoIntra = :conta or"
            + "        lcp.contaDebito = :conta or"
            + "        lcp.contaDebitoInterEstado = :conta or"
            + "        lcp.contaDebitoInterMunicipal = :conta or"
            + "        lcp.contaDebitoInterUniao = :conta or"
            + "        lcp.contaDebitoIntra = :conta)"
            + "        order by e.codigo", EventoContabil.class).setParameter("conta", conta);

        List<EventoContabil> eventos = null;
        try {
            eventos = consulta.getResultList();
            for (EventoContabil evento : eventos) {
                for (ItemEventoCLP itemEventoCLP : evento.getItemEventoCLPs()) {
                    itemEventoCLP.getClp().setLcps(recuperarLCPs(itemEventoCLP.getClp(), conta));
                }
            }

        } catch (Exception e) {
            eventos = new ArrayList<>();
        }
        return eventos;
    }

    public List<LCP> recuperarLCPs(CLP clp, Conta conta) {
        Query consulta = em.createQuery("select clp from CLP clp "
            + " inner join fetch clp.lcps lcp"
            + " where ("
            + "        lcp.contaCredito = :conta or"
            + "        lcp.contaCreditoInterEstado = :conta or"
            + "        lcp.contaCreditoInterMunicipal = :conta or"
            + "        lcp.contaCreditoInterUniao = :conta or"
            + "        lcp.contaCreditoIntra = :conta or"
            + "        lcp.contaDebito = :conta or"
            + "        lcp.contaDebitoInterEstado = :conta or"
            + "        lcp.contaDebitoInterMunicipal = :conta or"
            + "        lcp.contaDebitoInterUniao = :conta or"
            + "        lcp.contaDebitoIntra = :conta)"
            + " and clp = :clp", CLP.class).setParameter("clp", clp).setParameter("conta", conta).setMaxResults(1);
        try {

            CLP clpRecuperada = (CLP) consulta.getSingleResult();
            return clpRecuperada.getLcps();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<PlanoDeContas> getListaPlanoDeContas(ClasseDaConta classeDaConta) {

        Query consulta = getEntityManager().createQuery("from PlanoDeContas plano where plano.tipoConta.classeDaConta = :contasContabil", PlanoDeContas.class);
        consulta.setParameter("contasContabil", classeDaConta);

        return consulta.getResultList();
    }

    public ContaDeDestinacao recuperarContaDestinacaoPorFonte(FonteDeRecursos fonteDeRecursos, Exercicio exercicio) {
        try {
            String sql = "select c.*, cd.fonteDeRecursos_id,cd.dataCriacao, cd.codigoco " +
                "  from planodecontas pc " +
                "inner join planodecontasexercicio pce on pc.id = pce.planodedestinacaoderecursos_id and pce.exercicio_id = :exercicio " +
                "inner join conta c on pc.id = c.planodecontas_id " +
                "inner join contadedestinacao cd on c.id = cd.id " +
                "inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id " +
                "where fonte.id = :fonte " +
                "order by c.codigo ";
            Query consulta = em.createNativeQuery(sql, ContaDeDestinacao.class);
            consulta.setParameter("exercicio", exercicio.getId());
            consulta.setParameter("fonte", fonteDeRecursos.getId());
            consulta.setMaxResults(1);
            return (ContaDeDestinacao) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificarSeExistePlanoContaEspecificoParaExercicio(Exercicio exercicio, ClasseDaConta classeDaConta) {
        String sql = "  SELECT pc.*  " +
            "                  FROM PLANODECONTASEXERCICIO pce " +
            "                  INNER JOIN PLANODECONTAS pc ON pc.ID = pce.PLANOCONTABIL_ID  " +
            "                                              OR pc.ID = pce.PLANODEDESPESAS_ID  " +
            "                                              OR pc.ID = pce.PLANODEDESTINACAODERECURSOS_ID  " +
            "                                              OR pc.ID = pce.PLANOEXTRAORCAMENTARIO_ID  " +
            "                                              OR pc.ID = pce.PLANODERECEITAS_ID  " +
            "            inner join tipoconta tp on tp.id = pc.tipoconta_id " +
            "            where tp.classedaconta = :classeConta " +
            "            and pce.exercicio_id = :idExercicio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("classeConta", classeDaConta.name());

        if (!q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }
}

