/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import javax.ejb.Stateless;

/**
 * @author munif
 */
@Stateless
public class CalculoIptuBean {
//
//    private static final int ANO_ATUAL = Calendar.getInstance().get(Calendar.YEAR);
//    @PersistenceContext(unitName = "webpublicoPU")
//    private EntityManager em;
//    @EJB
//    private MoedaFacade moedaFacade;
//    @EJB
//    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
//
//    @EJB
//    private FacadeAutoGerenciado persisteCalculoIPTU;
//
//    private DependenciasCalculoIPTU getDependencias() {
////        return AuxilioCalculoIPTU.getInstance().getDependencias(this);
//    }
//
//    public Construcao criaConstrucaoDummy(CadastroImobiliario cadastroImobiliario) {
//        Construcao construcao = new Construcao();
//        construcao.setDummy(true);
//        construcao.setAtributos(new HashMap());
//        construcao.setAnoConstrucao(new Date());
//        construcao.setAreaConstruida(0.0);
//        construcao.setDataRegistro(new Date());
//        construcao.setDescricao("Dummy ");
//        construcao.setId(Long.MIN_VALUE);
//        construcao.setImovel(cadastroImobiliario);
//        return construcao;
//    }
//
//    public Testada recuperaTestadaPrincipal(Lote lote) {
//        Query q = em.createQuery("from Testada testada "
//                + "where testada.lote=:lote "
//                + "and testada.principal=true");
//
//        q.setParameter("lote", lote);
//        q.setMaxResults(1);
//        if (q.getResultList().size() > 0) {
//            return (Testada) q.getSingleResult();
//        } else {
//            return null;
//        }
//
//    }
//
//    public Map<String, Object> criaContextoImobiliario(Construcao construcao) {
//        Map<String, Object> contexto = new HashMap<>();
//        contexto.put("f", this);
//        contexto.put("construcao", construcao);
//        contexto.put("cadastroimobiliario", construcao.getImovel());
//        if (construcao.getImovel() == null || construcao.getImovel().getLote() == null) {
//            contexto.put("lote", "");
//        } else {
//            contexto.put("lote", construcao.getImovel().getLote());
//        }
//        contexto.put("testadaPrincipal", recuperaTestadaPrincipal(construcao.getImovel().getLote()));
//        BigDecimal ufmrb = getDependencias().getUfmVigente();
//        if (ufmrb == null) {
//            ufmrb = moedaFacade.recuperaValorVigenteUFM();
//        }
//        contexto.put("ufmrb", ufmrb.doubleValue());
//        if (construcao.getImovel() != null && construcao.getImovel().getLote() != null) {
//            for (Atributo at : construcao.getImovel().getLote().getAtributos().keySet()) {
//                contexto.put(at.getIdentificacao(), at);
//            }
//            for (Atributo at : construcao.getImovel().getAtributos().keySet()) {
//                contexto.put(at.getIdentificacao(), at);
//            }
//        }
//        for (Atributo at : construcao.getAtributos().keySet()) {
//            contexto.put(at.getIdentificacao(), at);
//        }
//        for (Pontuacao p : getPontuacoes()) {
//            contexto.put(p.getIdentificacao(), p);
//        }
//        for (ServicoUrbano su : getServicos()) {
//            contexto.put(su.getIdentificacao(), su);
//        }
//        return contexto;
//    }
//
//    @MetodoDisponivel
//    public Object atributo(CadastroImobiliario ci, Atributo hatributo) {
//
//        try {
//            ValorAtributo valor = ci.getAtributos().get(hatributo);
//            return retornaValorCorretoDoAtributo(hatributo, valor);
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @MetodoDisponivel
//    public Object atributo(Lote lote, Atributo hatributo) {
//        try {
//            ValorAtributo valor = lote.getAtributos().get(hatributo);
//            return retornaValorCorretoDoAtributo(hatributo, valor);
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @MetodoDisponivel
//    public Object atributo(Construcao construcao, Atributo atr) {
//        try {
//            ValorAtributo valor = construcao.getAtributos().get(atr);
//            return retornaValorCorretoDoAtributo(atr, valor);
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    private Object retornaValorCorretoDoAtributo(Atributo hatributo, ValorAtributo valor) {
//        switch (hatributo.getTipoAtributo()) {
//            case DATE:
//                return valor.getValorDate();
//            case DECIMAL:
//                return valor.getValorDecimal();
//            case DISCRETO:
//                return valor.getValorDiscreto();
//            case INTEIRO:
//                return valor.getValorInteiro();
//            case STRING:
//                return valor.getValorString();
//        }
//        return 0;
//    }
//
//    @MetodoDisponivel
//    public Object pontuacao(CadastroImobiliario cadastroImobiliario, Pontuacao pontuacao) {
//        try {
//            BigDecimal valor = BigDecimal.ZERO;
//            switch (pontuacao.getAtributos().size()) {
//                case 1: {
//                    Atributo atributo = pontuacao.getAtributos().get(0);
//                    ValorAtributo va = cadastroImobiliario.getAtributos().get(atributo);
//                    valor = CriaQueryNivel1Pontuacao(pontuacao, va);
//                    break;
//                }
//                case 2: {
//                    Atributo primeiro = pontuacao.getAtributos().get(0);
//                    Atributo segundo = pontuacao.getAtributos().get(1);
//                    ValorAtributo vaPrimeiro = cadastroImobiliario.getAtributos().get(primeiro);
//                    ValorAtributo vaSegundo = cadastroImobiliario.getAtributos().get(segundo);
//                    valor = CriaQueryNivel2Pontuacao(pontuacao, vaPrimeiro, vaSegundo);
//                    break;
//                }
//                case 3: {
//                    Atributo primeiro = pontuacao.getAtributos().get(0);
//                    Atributo segundo = pontuacao.getAtributos().get(1);
//                    Atributo terceiro = pontuacao.getAtributos().get(2);
//                    ValorAtributo vaPrimeiro = cadastroImobiliario.getAtributos().get(primeiro);
//                    ValorAtributo vaSegundo = cadastroImobiliario.getAtributos().get(segundo);
//                    ValorAtributo vaTerceiro = cadastroImobiliario.getAtributos().get(terceiro);
//                    pontuacao.getItens();
//                    valor = CriaQueryNivel3Pontuacao(pontuacao, vaPrimeiro, vaSegundo, vaTerceiro);
//                    break;
//                }
//                default: {
//                    break;
//                }
//            }
//            return valor;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @MetodoDisponivel
//    public Object pontuacao(Lote lote, Pontuacao pontuacao) {
//        try {
//            if (getDependencias().getPontuacaoLote().containsKey(lote)) {
//                if (getDependencias().getPontuacaoLote().get(lote).containsKey(pontuacao)) {
//                    return getDependencias().getPontuacaoLote().get(lote).get(pontuacao);
//                }
//            } else {
//                getDependencias().getPontuacaoLote().put(lote, new HashMap<Pontuacao, BigDecimal>());
//            }
//            BigDecimal valor = BigDecimal.ZERO;
//            switch (pontuacao.getAtributos().size()) {
//                case 1: {
//                    Atributo atributo = pontuacao.getAtributos().get(0);
//                    ValorAtributo va = lote.getAtributos().get(atributo);
//                    pontuacao.getItens();
//                    valor = CriaQueryNivel1Pontuacao(pontuacao, va);
//                    break;
//                }
//                case 2: {
//                    Atributo primeiro = pontuacao.getAtributos().get(0);
//                    Atributo segundo = pontuacao.getAtributos().get(1);
//                    ValorAtributo vaPrimeiro = lote.getAtributos().get(primeiro);
//                    ValorAtributo vaSegundo = lote.getAtributos().get(segundo);
//                    valor = CriaQueryNivel2Pontuacao(pontuacao, vaPrimeiro, vaSegundo);
//                    break;
//                }
//                case 3: {
//                    Atributo primeiro = pontuacao.getAtributos().get(0);
//                    Atributo segundo = pontuacao.getAtributos().get(1);
//                    Atributo terceiro = pontuacao.getAtributos().get(2);
//                    ValorAtributo vaPrimeiro = lote.getAtributos().get(primeiro);
//                    ValorAtributo vaSegundo = lote.getAtributos().get(segundo);
//                    ValorAtributo vaTerceiro = lote.getAtributos().get(terceiro);
//                    pontuacao.getItens();
//                    valor = CriaQueryNivel3Pontuacao(pontuacao, vaPrimeiro, vaSegundo, vaTerceiro);
//                    break;
//                }
//                default: {
//                    break;
//                }
//            }
//
//            getDependencias().getPontuacaoLote().get(lote).put(pontuacao, valor);
//            return valor;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return BigDecimal.ZERO;
//        }
//    }
//
//    @MetodoDisponivel
//    public Object pontuacao(Construcao construcao, Pontuacao pontuacao) {
//        if (construcao.isDummy()) {
//            return 0;
//        }
//        if (getDependencias().getPontuacaoConstrucao().containsKey(construcao)) {
//            if (getDependencias().getPontuacaoConstrucao().get(construcao).containsKey(pontuacao)) {
//
//                return getDependencias().getPontuacaoConstrucao().get(construcao).get(pontuacao);
//            }
//        } else {
//            getDependencias().getPontuacaoConstrucao().put(construcao, new HashMap<Pontuacao, BigDecimal>());
//        }
//
//        BigDecimal valor = BigDecimal.ZERO;
//        switch (pontuacao.getAtributos().size()) {
//            case 1: {
//                Atributo atributo = pontuacao.getAtributos().get(0);
//                ValorAtributo va = construcao.getAtributos().get(atributo);
//                valor = CriaQueryNivel1Pontuacao(pontuacao, va);
//                break;
//            }
//            case 2: {
//
//                Atributo primeiro = pontuacao.getAtributos().get(0);
//                Atributo segundo = pontuacao.getAtributos().get(1);
//                ValorAtributo valorPrimeiro = construcao.getAtributos().get(primeiro);
//                ValorAtributo valorSegundo = construcao.getAtributos().get(segundo);
//                valor = CriaQueryNivel2Pontuacao(pontuacao, valorPrimeiro, valorSegundo);
//                break;
//            }
//            case 3: {
//                Atributo primeiro = pontuacao.getAtributos().get(0);
//                Atributo segundo = pontuacao.getAtributos().get(1);
//                Atributo terceiro = pontuacao.getAtributos().get(2);
//                ValorAtributo vaPrimeiro = construcao.getAtributos().get(primeiro);
//                ValorAtributo vaSegundo = construcao.getAtributos().get(segundo);
//                ValorAtributo vaTerceiro = construcao.getAtributos().get(terceiro);
//
//                valor = CriaQueryNivel3Pontuacao(pontuacao, vaPrimeiro, vaSegundo, vaTerceiro);
//                break;
//            }
//            default: {
//                break;
//            }
//        }
//        getDependencias().getPontuacaoConstrucao().get(construcao).put(pontuacao, valor);
//
//        return valor;
//    }
//
//    public Exercicio getExercicioCorrente() {
//        if (getDependencias().getExercicio() == null) {
//            getDependencias().setExercicio((Exercicio) em.createQuery("from Exercicio ex where ex.ano = " + ANO_ATUAL).getSingleResult());
//        }
//        return getDependencias().getExercicio();
//    }
//
//    private Double avaliaFormula(Map<String, Object> contexto, String formula) throws ScriptException {
//        Double resultado;
//        ScriptEngineManager manager = new ScriptEngineManager();
//        getDependencias().setEngine(manager.getEngineByName("javascript"));
////        dependencias.getEngine().put("f", this);
//        for (String s : contexto.keySet()) {
//            getDependencias().getEngine().put(s, contexto.get(s));
//        }
//        resultado = (Double) getDependencias().getEngine().eval(formula);
//
//        return resultado;
//    }
//
//    @MetodoDisponivel
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public double fracaoIdealPorcentagem(Construcao construcao) {
//        if (construcao.isDummy()) {
//            return 1;
//        }
//        construcao = em.find(Construcao.class, construcao.getId());
//        Query q = em.createNativeQuery("select sum(c.areaConstruida) from Construcao c "
//                + " inner join cadastroimobiliario imovel on imovel.id = c.imovel_id"
//                + " inner join lote on lote.id = imovel.lote_id"
//                + " where lote.id = :lote");
//        q.setParameter("lote", construcao.getImovel().getLote().getId());
//        BigDecimal areaTotalConstruidaNoLote = (BigDecimal) q.getSingleResult();
//        double areaLote = construcao.getImovel().getLote().getAreaLote();
//        double fracaoIdeal = (areaLote * construcao.getAreaConstruida()) / areaTotalConstruidaNoLote.doubleValue();
//        fracaoIdeal = (fracaoIdeal / areaLote);
//        return fracaoIdeal;
//    }
//
//    @MetodoDisponivel
//    public double fracaoIdealApartamento(Construcao construcao) {
//        try {
//            if (construcao.isDummy()) {
//                return 1;
//            }
//            BigDecimal areaTotalConstruidaNoLote = construcao.getImovel().getAreaTotalConstruida();
//            BigDecimal areaLote = new BigDecimal(construcao.getImovel().getLote().getAreaLote());
//            BigDecimal fracaoIdeal = (areaLote.multiply(new BigDecimal(construcao.getAreaConstruida())).divide(areaTotalConstruidaNoLote, 2, RoundingMode.HALF_EVEN));
//            fracaoIdeal = (fracaoIdeal.divide(areaLote, 2, RoundingMode.HALF_EVEN));
//            return fracaoIdeal.doubleValue();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            return 0.0;
//        }
//    }
//
//    @MetodoDisponivel
//    public double fracaoIdealMetragem(Construcao construcao) {
//        if (construcao.isDummy()) {
//            return construcao.getImovel().getLote().getAreaLote();
//        }
//        double areaLote = construcao.getImovel().getLote().getAreaLote();
//        return fracaoIdealPorcentagem(construcao) * areaLote;
//    }
//
//    public List<String> getAtributosContexto() {
//        List<String> atributos = Lists.newArrayList();
//        atributos.add("cadastroimobiliario");
//        atributos.add("lote");
//        atributos.add("testada");
//        atributos.add("ufmrb");
//        atributos.add("construcoes");
//        Query q = em.createQuery("select atributo.identificacao from Atributo atributo order by atributo.classeDoAtributo, atributo.identificacao");
//        atributos.addAll(q.getResultList());
//        return atributos;
//    }
//
//    public ResultadoCalculo avaliaTaxa(String formula, String bibliotecaFormulas, Construcao exemplo) {
//        return avaliaTaxa(formula, bibliotecaFormulas, criaContextoImobiliario(exemplo));
//    }
//
//    public ResultadoCalculo avaliaTaxa(String formula, String bibliotecaFormulas, Map<String, Object> contexto) {
//        try {
//            String tudo = bibliotecaFormulas + "\n\n" + formula + "\n";
//            BigDecimal valor = new BigDecimal(String.valueOf(avaliaFormula(contexto, tudo)));
//            return ResultadoCalculo.newResultadoOK(valor);
//        } catch (Exception ex) {
//            return ResultadoCalculo.newResultadoFail("Erro ao executar a fórmula " + formula + ": " + ex.getMessage());
//        }
//    }
//
//    @MetodoDisponivel
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public boolean faceContemServico(Face face, ServicoUrbano su) {
//        if (face == null || su == null) {
//            return false;
//        }
//        Query q = em.createNativeQuery("select fs.id "
//                + " from faceservico fs where fs.servicourbano_id = :servicoUrbano and fs.face_id = :face");
//        q.setParameter("face", face.getId());
//        q.setParameter("servicoUrbano", su.getId());
//        return q.getResultList().size() == 1;
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public List<String> retornaAtributos(ClasseDoAtributo cl) {
//
//        Query q = em.createQuery("select atributo.identificacao from Atributo atributo where atributo.classeDoAtributo = :classeAtributo order by atributo.classeDoAtributo, atributo.identificacao");
//
//        // }
//        q.setParameter("classeAtributo", cl);
//        return q.getResultList();
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public List<String> retornaPontuacao() {
//
//        Query q = em.createQuery("select pontuacao.identificacao from Pontuacao pontuacao where pontuacao.exercicio=:exercicio");
//
//        q.setParameter("exercicio", getExercicioCorrente());
//        return q.getResultList();
//    }
//
//    public List<String> retornaMetodos() {
//        List<String> metodos = Lists.newArrayList();
//        Class classe = this.getClass();
//        for (Method metodo : classe.getDeclaredMethods()) {
//            if (metodo.isAnnotationPresent(MetodoDisponivel.class)) {
//                String tipos = "";
//                Class[] parameterTypes = metodo.getParameterTypes();
//                for (Class tipo : parameterTypes) {
//                    String sTipo = tipo.getSimpleName();
//                    sTipo = ("" + sTipo.charAt(0)).toLowerCase() + sTipo.substring(1);
//                    tipos += sTipo + ",";
//                }
//                tipos = tipos.substring(0, tipos.length() - 1);
//                tipos = "f." + metodo.getName() + "(" + tipos + ")";
//                metodos.add(tipos);
//            }
//        }
//        return metodos;
//    }
//
//    @MetodoDisponivel
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public Construcao[] contrucoesDoLote(Lote lote) {
//        Construcao[] toReturn = {};
//        try {
//
//            Query q = em.createQuery("from Construcao c where c.imovel.lote = :lote");
//
//            q.setParameter("lote", lote);
//            toReturn = (Construcao[]) q.getResultList().toArray(toReturn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return toReturn;
//    }
//
//    public ProcessoCalculoIPTU salvarNovoProcesso(ProcessoCalculoIPTU c) {
//        try {
//            return em.merge(c);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @MetodoDisponivel
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public Construcao[] construcoesDoCadastroImobiliario(CadastroImobiliario ci) {
//        Construcao[] retorno = {};
//        try {
//            Query q = em.createQuery("from Construcao c where c.imovel = :imovel");
//
//            q.setParameter("imovel", ci);
//            retorno = (Construcao[]) q.getResultList().toArray(retorno);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return retorno;
//    }
//
//    @MetodoDisponivel
//    public double areaConstruidaDoLote(Lote lote) {
//        try {
//            if (getDependencias().getAreaConstruidaLote().containsKey(lote)) {
//                return getDependencias().getAreaConstruidaLote().get(lote).doubleValue();
//            }
//            Double retorno;// = 0.0;
//            Query q = em.createQuery("select sum(c.areaConstruida) from Construcao c where c.imovel.lote = :lote");
//            q.setParameter("lote", lote);
//            retorno = (Double) q.getSingleResult();
//            if (retorno == null) {
//                return 1;
//            }
//            getDependencias().getAreaConstruidaLote().put(lote, new BigDecimal(retorno));
//            return retorno;
//        } catch (Exception e) {
//            return 0d;
//        }
//    }
//
//    public ProcessoCalculoIPTU criaProcessoDeCalculo(ProcessoCalculoIPTU processo, String cadastroImobiliarioInicio, String cadastroImobiliarioFim) throws Exception {
//        try {
//            Divida divida = configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();
//            if (divida == null) {
//                throw new ExcecaoNegocioGenerica("Não há uma dívida para o IPTU informada nas configurações do tributário");
//            }
//            ProcessoCalculoIPTU processoCalculoIPTU = processo;
//            processoCalculoIPTU.setDataLancamento(new Date());
//            processoCalculoIPTU.setDivida(divida);
//            processoCalculoIPTU.setCadastroInicial(cadastroImobiliarioInicio);
//            processoCalculoIPTU.setCadastroFinal(cadastroImobiliarioFim);
//            processoCalculoIPTU = salvarNovoProcesso(processoCalculoIPTU);
//            return processoCalculoIPTU;
//        } catch (Exception ex) {
//            Logger.getLogger(CalculoIptuBean.class.getName()).log(Level.SEVERE, null, ex);
//            return processo;
//        }
//    }
//
//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public void calculaIPTUSBusinnes(List<BigDecimal> lista, ProcessoCalculoIPTU processo) throws ScriptException, UFMException {
//        getDependencias().limpaTudo();
//        getDependencias().setUfmVigente(moedaFacade.recuperaValorVigenteUFM());
//        if (getDependencias().getUfmVigente() == null) {
//            throw new UFMException("Nenhum UFM encontrado");
//        }
////        getDependencias().setConfiguracaoIPTU(configuracaoIPTUFacade.recuperar(processo.getConfiguracaoIPTU().getId()));
//        getDependencias().setProcessoCalculoIPTU(processo);
//
//        Long inicio = System.currentTimeMillis();
//        int mais50 = 0;
//        if (getDependencias().getProcessoCalculoIPTU() != null) {
//            for (BigDecimal id : lista) {
////                if (!AuxilioCalculoIPTU.getInstance().getCalculando()) {
////                    break;
////                }
//                mais50++;
//                CadastroImobiliario cadastroImobiliario = (CadastroImobiliario) em.find(CadastroImobiliario.class, id.longValue());
//                calculaCadastroImobiliario(cadastroImobiliario);
////                AuxilioCalculoIPTU.getInstance().contaCalculo();
//                if (mais50 == 100) {
//                    mais50 = 0;
//                    try {
//                        persisteCalculoIPTU.persisteCalculos(getDependencias().getCalculos());
//                    } catch (SystemException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                    getDependencias().limpaMapas();
//                    em.flush();
////                    Util.imprime(" Calculados:  " + AuxilioCalculoIPTU.getInstance().getContadorCalculoIPTU());
//                    Util.imprime(" Tempo gasto: " + (System.currentTimeMillis() - inicio));
//                    Util.imprime("----------------------------");
//                }
//            }
//        }
//        if (!getDependencias().getCalculos().isEmpty()) {
//            try {
//                persisteCalculoIPTU.persisteCalculos(getDependencias().getCalculos());
//            } catch (SystemException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
////        AuxilioCalculoIPTU.getInstance().liberaCalculo();
//        Util.imprime(" Tempo total Gasto: " + (System.currentTimeMillis() - inicio));
//        Util.imprime("----------------------------");
//    }
//
//    private void calculaCadastroImobiliario(CadastroImobiliario cadastro) throws ScriptException, UFMException {
//        Lote lote = cadastro.getLote();
//        if (lote != null) {
//            List<Construcao> construcoes = em.createQuery("select c from Construcao c where c.imovel = :imovel").setParameter("imovel", cadastro).getResultList();
//            Integer subDebito = 0;
//            Map<Construcao, Integer> contadorSubDebito = Maps.newHashMap();
//            if (construcoes.isEmpty()) {
//                Construcao construcao = criaConstrucaoDummy(cadastro);
//                processaConstrucao(construcao, cadastro, contadorSubDebito);
//            } else {
//                Collections.sort(construcoes);
//                getDependencias().setConstrucoes(construcoes);
//                for (Construcao construcao : construcoes) {
//                    subDebito++;
//                    contadorSubDebito.put(construcao, subDebito);
//                    processaConstrucao(construcao, cadastro, contadorSubDebito);
//                }
//            }
//        }
//    }
//
////    private void processaConstrucao(Construcao construcao, CadastroImobiliario cadastro, Map<Construcao, Integer> contadorSubDebito) {
////        Map<String, Object> contexto = criaContextoImobiliario(construcao);
////        for (String s : contexto.keySet()) {
////            getDependencias().getEngine().put(s, contexto.get(s));
////        }
////        CalculoIPTU calculoIPTU = processarCalculoIPTU(cadastro, construcao);
////        if (construcao.getEnglobado() == null && !construcao.isDummy()) {
////            calculoIPTU.setSubDivida(contadorSubDebito.get(construcao).longValue());
////        } else if (construcao.getEnglobado() != null && contadorSubDebito.containsKey(construcao.getEnglobado())) {
////            calculoIPTU.setSubDivida(contadorSubDebito.get(construcao).longValue());
////        }
////        if (!construcao.isDummy()) {
////            calculoIPTU.setConstrucao(construcao);
////        } else {
////            calculoIPTU.setConstrucao(null);
////        }
////        List<Propriedade> propriedades = em.createQuery("select p from Propriedade p where p.imovel = :imovel").setParameter("imovel", cadastro).getResultList();
////        for (Propriedade propriedade : propriedades) {
////            CalculoPessoa calculoPessoa = new CalculoPessoa();
////            calculoPessoa.setCalculo(calculoIPTU);
////            calculoPessoa.setPessoa(propriedade.getPessoa());
////            calculoIPTU.getPessoas().add(calculoPessoa);
////        }
//////        if (!AuxilioCalculoIPTU.getInstance().getErros().containsKey(calculoIPTU)) {
////            getDependencias().getCalculos().add(calculoIPTU);
//////            gravaOcorrencia(calculoIPTU, NivelOcorrencia.INFORMACAO, "Gerado com Sucesso: " + calculoIPTU.toString(), null);
//////            AuxilioCalculoIPTU.getInstance().contaGerados();
////
//////        } else {
//////            calculoIPTU.setConsistente(Boolean.FALSE);
//////            getDependencias().getCalculos().add(calculoIPTU);
////            StringBuilder sb = new StringBuilder();
//////            for (String s : AuxilioCalculoIPTU.getInstance().getErros().get(calculoIPTU)) {
//////                sb.append(s).append(", ");
////            }
//////            gravaOcorrencia(calculoIPTU, NivelOcorrencia.ERRO, "Gerado com inconsistência: " + calculoIPTU.toString(), sb.toString());
//////            AuxilioCalculoIPTU.getInstance().contaNaoGerados();
//////        }
////    }
//
//    private void gravaOcorrencia(CalculoIPTU calculo, NivelOcorrencia nivelOcorrencia, String mensagem, String detalhesTecnicos) {
//        OcorrenciaCalculoIPTU ocorrenciaCalculoIPTU = new OcorrenciaCalculoIPTU();
//        Ocorrencia ocorrencia = new Ocorrencia();
//        ocorrencia.addMessagem(TipoOcorrencia.CALCULO, nivelOcorrencia, mensagem, detalhesTecnicos);
//        ocorrenciaCalculoIPTU.setOcorrencia(ocorrencia);
//        ocorrenciaCalculoIPTU.setCadastroImobiliario(calculo.getCadastroImobiliario());
//        ocorrenciaCalculoIPTU.setConstrucao(calculo.getConstrucao());
//        ocorrenciaCalculoIPTU.setCalculoIptu(calculo);
//        calculo.getOcorrenciaCalculoIPTUs().add(ocorrenciaCalculoIPTU);
//
//    }
//
//    private CalculoIPTU processarCalculoIPTU(CadastroImobiliario cadastroImobiliario, Construcao construcao) {
//        List<String> resultadosFalhos = new ArrayList<>();
//        CalculoIPTU calculoIPTU = new CalculoIPTU();
//        calculoIPTU.setCadastro(cadastroImobiliario);
//        calculoIPTU.setCadastroImobiliario(cadastroImobiliario);
//        calculoIPTU.setProcessoCalculoIPTU(getDependencias().getProcessoCalculoIPTU());
//        calculoIPTU.setItensCalculo(new ArrayList<ItemCalculoIPTU>());
//        calculoIPTU.setSimulacao(Boolean.TRUE);
//        calculoIPTU.setIsento(false);
//        calculoIPTU.setDataCalculo(new Date());
////        for (ConfiguracaoIPTUItem it : getDependencias().getConfiguracaoIPTU().getItens()) {
////            if ((it.getFormula() != null) && (!"".equals(it.getFormula().trim()))) {
////                String tudo = getDependencias().getConfiguracaoIPTU().getBibliotecaFormulas() + "\n\n" + it.getFormula() + "\n";
////                double resultado = 1;
////                ResultadoCalculo resultadoCalculo;
////                try {
////                    resultado = (Double) getDependencias().getEngine().eval(tudo);
////                    resultadoCalculo = ResultadoCalculo.newResultadoOK(new BigDecimal(resultado));
////                } catch (Exception ex) {
////                    resultadoCalculo = ResultadoCalculo.newResultadoFail("Erro ao executar a fórmula " + it.getFormula() + ": " + ex.getMessage());
////                }
////                it.setResultado(resultadoCalculo);
////                if (!it.getResultado().isSucesso()) {
////                    resultadosFalhos.add(it.getResultado().getMensagem());
////                }
////                ItemCalculoIPTU itemCalculo = new ItemCalculoIPTU();
////                itemCalculo.setDataRegistro(new Date());
////                itemCalculo.setCalculoIPTU(calculoIPTU);
////                itemCalculo.setConfiguracaoIPTUItem(it);
////                itemCalculo.setValorReal(it.getResultado().getValor());
////                calculoIPTU.setConstrucao(construcao);
////                calculoIPTU.getItensCalculo().add(itemCalculo);
////                if (it.getTributo() != null) {
////                    itemCalculo.setValorEfetivo(it.getResultado().getValor());
////                    calculoIPTU.setValorEfetivo(calculoIPTU.getValorEfetivo().add(itemCalculo.getValorEfetivo()));
////                    calculoIPTU.setValorReal(calculoIPTU.getValorReal().add(itemCalculo.getValorReal()));
////                    List<IsencaoCadastroImobiliario> isencoes = em.createQuery("select new IsencaoCadastroImobiliario(i.id, i.lancaImposto, i.lancaTaxa, i.construcao.id) "
////                            + " from IsencaoCadastroImobiliario i"
////                            + " where i.cadastroImobiliario = :cad "
////                            + " and (i.construcao is null or i.construcao = :construcao )"
////                            + " and (i.inicioVigencia is not null or i.inicioVigencia <= current_date)"
////                            + " and (i.finalVigencia is null or i.finalVigencia >= current_date)")
////                            .setParameter("cad", cadastroImobiliario)
////                            .setParameter("construcao", construcao)
////                            .getResultList();
////                    if (!isencoes.isEmpty()) {
////                        IsencaoCadastroImobiliario isencao = isencoes.get(0);
////                        if (isencao != null) {
////                            if (!isencao.getLancaImposto() && it.getNomenclatura().equals("V.I")) {
////                                itemCalculo.setIsento(Boolean.TRUE);
////                            }
////                            if (!isencao.getLancaTaxa() && (it.getNomenclatura().equals("C.L") || it.getNomenclatura().equals("Il. Publ"))) {
////                                itemCalculo.setIsento(Boolean.TRUE);
////                            }
////                        }
////                    }
////                }
////            }
////        }
//        if ((calculoIPTU.getValorReal().compareTo(BigDecimal.ZERO) <= 0 && calculoIPTU.getValorEfetivo().compareTo(BigDecimal.ZERO) <= 0)) {
//            resultadosFalhos.add("Confira os dados do cadastro, o valor do calculo foi igual a ZERO ");
//        }
//        if (resultadosFalhos.size() > 0) {
//            AuxilioCalculoIPTU.getInstance().getErros().put(calculoIPTU, resultadosFalhos);
//        }
//        return calculoIPTU;
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public List<ProcessoCalculoIPTU> listaProcessos() {
//        Query q = em.createQuery("from ProcessoCalculoIPTU");
//        return q.getResultList();
//    }
//
//    public ProcessoCalculoIPTU recarregar(ProcessoCalculoIPTU processo) {
//        ProcessoCalculoIPTU c = em.find(ProcessoCalculoIPTU.class, processo.getId());
//        return c;
//    }
//
//    public void remover(ProcessoCalculoIPTU selecionado) {
//        try {
//            em.remove(selecionado);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public String getPrimeiroCadastro(ProcessoCalculoIPTU selecionado) throws ExcecaoNegocioGenerica {
//        String sql = "select min(ci.inscricaocadastral) from cadastroimobiliario ci "
//                + " inner join calculoiptu on calculoiptu.cadastroimobiliario_id = ci.id"
//                + " inner join processocalculo on processocalculo.id = calculoiptu.processocalculoiptu_id "
//                + " where processocalculo.id = :id";
//        Query q = em.createNativeQuery(sql);
//        q.setParameter("id", selecionado.getId());
//        if (!q.getResultList().isEmpty()) {
//            return (String) q.getResultList().get(0);
//        }
//        throw new ExcecaoNegocioGenerica("Não foi possível encontrar o primeiro cadastro desse processo");
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    public String getUltimoCadastro(ProcessoCalculoIPTU selecionado) throws ExcecaoNegocioGenerica {
//        Query q = em.createQuery("select max(c.cadastroImobiliario.inscricaoCadastral) from CalculoIPTU c where c.processoCalculoIPTU = :processo");
//        q.setParameter("processo", selecionado);
//        if (!q.getResultList().isEmpty()) {
//            return (String) q.getResultList().get(0);
//        }
//        throw new ExcecaoNegocioGenerica("Não foi possível encontrar o primeiro cadastro desse processo");
//    }
//
//    public void excluiProcesso(ProcessoCalculoIPTU selecionado) {
//        selecionado = em.find(ProcessoCalculoIPTU.class, selecionado.getId());
//        em.remove(selecionado);
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    private BigDecimal CriaQueryNivel1Pontuacao(Pontuacao pontuacao, ValorAtributo va) {
//        try {
//            String sql = "select ip.pontos from itempontuacao ip"
//                    + " inner join IPont_VPos itemvalor on itemvalor.itempontuacao_id = ip.id "
//                    + " inner join ValorPossivel valor on valor.id = itemvalor.valores_id"
//                    + " where ip.pontuacao_id = :pontuacao and valor.id = :valor";
//            Query q = em.createNativeQuery(sql);
//            q.setParameter("pontuacao", pontuacao.getId());
//            q.setParameter("valor", va.getValorDiscreto().getId());
//            return (BigDecimal) q.getResultList().get(0);
//        } catch (Exception e) {
//            return BigDecimal.ZERO;
//        }
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    private BigDecimal CriaQueryNivel2Pontuacao(Pontuacao pontuacao, ValorAtributo primeiroValor, ValorAtributo segundoValor) {
//        try {
//            String sql = "select ip.pontos from itempontuacao ip"
//                    + " inner join IPont_VPos itemvalor on itemvalor.itempontuacao_id = ip.id  "
//                    + " where ip.pontuacao_id = :pontuacao "
//                    + " and :primeiroValor in (select valor.id from ValorPossivel valor where valor.id = itemvalor.valores_id)"
//                    + " and :segundoValor in (select valor.id from ValorPossivel valor where valor.id = itemvalor.valores_id)";
//            Query q = em.createNativeQuery(sql);
//            q.setParameter("pontuacao", pontuacao.getId());
//            q.setParameter("primeiroValor", primeiroValor.getValorDiscreto().getId());
//            q.setParameter("segundoValor", segundoValor.getValorDiscreto().getId());
//            return (BigDecimal) q.getResultList().get(0);
//        } catch (Exception e) {
//            return BigDecimal.ZERO;
//        }
//    }
//
//    //  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//    private BigDecimal CriaQueryNivel3Pontuacao(Pontuacao pontuacao, ValorAtributo primeiroValor, ValorAtributo segundoValor, ValorAtributo terceiroValor) {
//        try {
//            String sql = "select ip.pontos from itempontuacao ip"
//                    + " inner join IPont_VPos itemvalor on itemvalor.itempontuacao_id = ip.id  "
//                    + " where ip.pontuacao_id = :pontuacao "
//                    + " and :primeiroValor in (select valor.id from ValorPossivel valor where valor.id = itemvalor.valores_id)"
//                    + " and :segundoValor in (select valor.id from ValorPossivel valor where valor.id = itemvalor.valores_id)"
//                    + " and :terceiroValor in (select valor.id from ValorPossivel valor where valor.id = itemvalor.valores_id)";
//            Query q = em.createNativeQuery(sql);
//            q.setParameter("pontuacao", pontuacao.getId());
//            q.setParameter("primeiroValor", primeiroValor.getValorDiscreto().getId());
//            q.setParameter("segundoValor", segundoValor.getValorDiscreto().getId());
//            q.setParameter("terceiroValor", terceiroValor.getValorDiscreto().getId());
//            return (BigDecimal) q.getResultList().get(0);
//        } catch (Exception e) {
//            return BigDecimal.ZERO;
//        }
//    }
//
//    private List<Pontuacao> getPontuacoes() {
//        if (getDependencias().getPontuacoes() == null || getDependencias().getPontuacoes().isEmpty()) {
//            Query q = em.createQuery("from Pontuacao pontuacao join fetch pontuacao.atributos where pontuacao.exercicio=:exercicio");
//
//            q.setParameter("exercicio", getExercicioCorrente());
//            getDependencias().setPontuacoes(q.getResultList());
//        }
//        return getDependencias().getPontuacoes();
//    }
//
//    private List<ServicoUrbano> getServicos() {
//        if (getDependencias().getServicoUrbanos() == null || getDependencias().getServicoUrbanos().isEmpty()) {
//            Query q = em.createQuery("from ServicoUrbano");
//            getDependencias().setServicoUrbanos(q.getResultList());
//        }
//        return getDependencias().getServicoUrbanos();
//    }
//
//    @MetodoDisponivel
//    public int indiceDaConstrucao(Construcao construcao) {
//        if (getDependencias().getConstrucoes() != null && !getDependencias().getConstrucoes().isEmpty()) {
//            return getDependencias().getConstrucoes().indexOf(construcao) + 1;
//        }
//        return 1;
//    }
//
//    public void imprimeNoLog(String log) {
//        Util.imprime("vindo do JavaScript: " + log);
//    }
//
//    public IsencaoCadastroImobiliario getIsencaoVigente(List<IsencaoCadastroImobiliario> isencoes) {
//        Date hoje = new Date();
//        for (IsencaoCadastroImobiliario isencao : isencoes) {
//            if (isencao.getInicioVigencia() != null && isencao.getInicioVigencia().before(hoje) && (isencao.getFinalVigencia() == null || isencao.getFinalVigencia().after(hoje))) {
//                return isencao;
//            }
//        }
//        return null;
//    }
//
//    public List<ProcessoCalculoIPTU> filtraProcessos(CalculoIPTUControle.FiltrosIptu filtrosIptu) {
//        String juncao = " where ";
//        String hql = "select p from ProcessoCalculoIPTU p";
//        if (filtrosIptu.getDivida() != null && !filtrosIptu.getDivida().isEmpty()) {
//            hql += juncao + " lower(p.divida.descricao) like :divida";
//            juncao = " and ";
//        }
//        if (filtrosIptu.getDescricao() != null && !filtrosIptu.getDescricao().isEmpty()) {
//            hql += juncao + " lower(p.descricao) like :descricao";
//            juncao = " and ";
//        }
//        if (filtrosIptu.getBciInicial() != null && !filtrosIptu.getBciInicial().isEmpty()) {
//            hql += juncao + " p.cadastroInical >=  :bciInicial";
//            juncao = " and ";
//        }
//        if (filtrosIptu.getBciFinal() != null && !filtrosIptu.getBciFinal().isEmpty()) {
//            hql += juncao + " p.cadastroFinal <= :bciFinal";
//            juncao = " and ";
//        }
//        if (filtrosIptu.getDataLancamentoIncial() != null) {
//            hql += juncao + " p.dataLancamento >= :lancamentoInicial";
//            juncao = " and ";
//        }
//        if (filtrosIptu.getDataLancamentoFinal() != null) {
//            hql += juncao + " p.dataLancamento <= :lancamentoFinal";
//            juncao = " and ";
//        }
//
//        if (filtrosIptu.getExercicio() != null) {
//            hql += juncao + " p.exercicio = :exercicio";
//        }
//
//        Query q = em.createQuery(hql);
//
//        if (filtrosIptu.getDivida() != null && !filtrosIptu.getDivida().isEmpty()) {
//            q.setParameter("divida", "%" + filtrosIptu.getDivida().toLowerCase().trim() + "%");
//        }
//        if (filtrosIptu.getDescricao() != null && !filtrosIptu.getDescricao().isEmpty()) {
//            q.setParameter("descricao", "%" + filtrosIptu.getDescricao().toLowerCase().trim() + "%");
//        }
//        if (filtrosIptu.getBciInicial() != null && !filtrosIptu.getBciInicial().isEmpty()) {
//            q.setParameter("bciInicial", filtrosIptu.getBciInicial().trim());
//        }
//        if (filtrosIptu.getBciFinal() != null && !filtrosIptu.getBciFinal().isEmpty()) {
//            q.setParameter("bciFinal", filtrosIptu.getBciFinal().trim());
//        }
//        if (filtrosIptu.getDataLancamentoIncial() != null) {
//            q.setParameter("lancamentoInicial", filtrosIptu.getDataLancamentoIncial());
//        }
//        if (filtrosIptu.getDataLancamentoFinal() != null) {
//            q.setParameter("lancamentoFinal", filtrosIptu.getDataLancamentoFinal());
//        }
//        if (filtrosIptu.getExercicio() != null) {
//            q.setParameter("exercicio", filtrosIptu.getExercicio());
//        }
//
//        return q.getResultList();
//    }
}
