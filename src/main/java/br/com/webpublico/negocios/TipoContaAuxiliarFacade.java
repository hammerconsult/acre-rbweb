/*
 * Codigo gerado automaticamente em Mon Jul 02 14:00:30 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcContaAuxiliarDetalhadaDAO;
import br.com.webpublico.negocios.contabil.singleton.SingletonContaAuxiliar;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.*;

@Stateless
public class TipoContaAuxiliarFacade extends AbstractFacade<TipoContaAuxiliar> {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private SingletonContaAuxiliar singletonContaAuxiliar;
    @EJB
    private AtualizaEventoContabilFacade atualizaEventoContabilFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoContaAuxiliarFacade() {
        super(TipoContaAuxiliar.class);
    }

    public List<TipoContaAuxiliar> listaTodos(String parte, ClassificacaoContaAuxiliar classificacaoContaAuxiliar) {
        String sql = "SELECT tca.* FROM tipocontaauxiliar tca "
            + "WHERE tca.classificacaoContaAuxiliar = :classificacaoContaAuxiliar"
            + "  AND (tca.chave LIKE :parte "
            + "   OR tca.codigo LIKE :parte "
            + "   OR lower(tca.descricao) LIKE :parte "
            + "   OR tca.mascara LIKE :parte "
            + "   OR lower(tca.itens) LIKE :parte)";
        Query q = getEntityManager().createNativeQuery(sql, TipoContaAuxiliar.class);
        q.setParameter("classificacaoContaAuxiliar", classificacaoContaAuxiliar.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public boolean verificaCodigoExistente(TipoContaAuxiliar tc) {
        String sql = "SELECT * FROM TIPOCONTAAUXILIAR WHERE CODIGO = LOWER(:param)";
        if (tc.getId() != null) {
            sql += " and id <> :id ";
        }
        Query q = em.createNativeQuery(sql, TipoContaAuxiliar.class);
        q.setParameter("param", tc.getCodigo().trim().toLowerCase());
        if (tc.getId() != null) {
            q.setParameter("id", tc.getId());
        }
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public TipoContaAuxiliar recuperaPorChave(String chave) {
        String sql = "SELECT tca.* FROM tipocontaauxiliar tca WHERE tca.chave LIKE :chave ORDER BY tca.id";
        Query q = getEntityManager().createNativeQuery(sql, TipoContaAuxiliar.class);
        q.setParameter("chave", "%" + chave + "%");
        q.setMaxResults(1);
        return (TipoContaAuxiliar) q.getSingleResult();
    }

    public Object localizarEntidade(String idObjeto, String classeObjeto) {
        try {
            String hql = "from " + classeObjeto + " obj where obj.id=:param";
            Query q = em.createQuery(hql);
            q.setParameter("param", Long.parseLong(idObjeto));
            return q.getSingleResult();
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("A pesquisa pela classe " + classeObjeto + " com o ID " + idObjeto + " Retornou mais de um elemento!");
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("A pesquisa pela classe " + classeObjeto + " com o ID " + idObjeto + " não retornou nenhum um elemento!");
        }
    }

    private ContaAuxiliar buscarContaAuxiliarPorCodigo(PlanoDeContas planoDeContas, String codigo, ContaContabil contaContabil) {
        ContaAuxiliar contaAuxiliar = singletonContaAuxiliar.buscarContaAuxiliarNoMap(planoDeContas, codigo, contaContabil);
        if (contaAuxiliar != null) {
            return contaAuxiliar;
        }
        Query q = em.createQuery("SELECT obj FROM ContaAuxiliar obj where obj.planoDeContas=:pdc AND obj.codigo=:codigo AND obj.contaContabil=:contaContabil ");
        q.setParameter("codigo", codigo);
        q.setParameter("pdc", planoDeContas);
        q.setParameter("contaContabil", contaContabil);
        q.setMaxResults(1);
        List<ContaAuxiliar> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            contaAuxiliar = resultado.get(0);
            singletonContaAuxiliar.adicionarContaAuxiliarNoMap(planoDeContas, codigo, contaAuxiliar);
            return contaAuxiliar;
        }
        return null;
    }

    private ContaAuxiliarDetalhada buscarContaAuxiliarDetalhadaPorHash(ContaAuxiliarDetalhada cad) {
        ContaAuxiliarDetalhada contaAuxiliarDetalhada = singletonContaAuxiliar.buscarContaAuxiliarDetalhadaNoMap(cad);
        if (contaAuxiliarDetalhada != null) {
            return contaAuxiliarDetalhada;
        }
        String sql = "SELECT c.*, " +
            "                obj.ContaDeDestinacao_id," +
            "                obj.conta_id," +
            "                obj.exercicioAtual_id," +
            "                obj.exercicioOrigem_id," +
            "                obj.unidadeOrganizacional_id, " +
            "                obj.subSistema, " +
            "                obj.dividaConsolidada, " +
            "                obj.es, " +
            "                obj.classificacaoFuncional, " +
            "                obj.hashContaAuxiliarDetalhada, " +
            "                obj.contaContabil_id, " +
            "                obj.tipoContaAuxiliar_id, " +
            "                obj.codigoCO " +
            "     FROM ContaAuxiliarDetalhada obj " +
            "    inner join conta c on c.id = obj.id " +
            "    where c.codigo = :codigo " +
            "      and c.exercicio_id = :exercicioConta ";
        if (cad.getTipoContaAuxiliar() != null) {
            sql += " and obj.tipoContaAuxiliar_id = :tipoContaAuxiliarId ";
        }
        if (cad.getContaContabil() != null) {
            sql += " and obj.contaContabil_id = :contaContabilId ";
        }
        if (cad.getClassificacaoFuncional() != null) {
            sql += " and obj.classificacaoFuncional = :classificacao ";
        }
        if (cad.getContaDeDestinacao() != null) {
            sql += " and obj.ContaDeDestinacao_id = :destId ";
        }
        if (cad.getConta() != null) {
            sql += " and obj.conta_id = :contaId ";
        }
        if (cad.getExercicioOrigem() != null) {
            sql += " and obj.exercicioOrigem_id = :exercicioOrigem ";
        }
        if (cad.getExercicioAtual() != null) {
            sql += " and obj.exercicioAtual_id = :exercicioAtual ";
        }
        if (cad.getDividaConsolidada() != null) {
            sql += " and obj.dividaConsolidada = :dividaConsolidada ";
        }
        if (cad.getEs() != null) {
            sql += " and obj.es = :es ";
        }
        if (cad.getSubSistema() != null) {
            sql += " and obj.subSistema = :subSistema ";
        }
        if (cad.getUnidadeOrganizacional() != null) {
            sql += " and obj.unidadeOrganizacional_id = :unidadeId ";
        }
        if (cad.getCodigoCO() != null) {
            sql += " and obj.codigoCO = :codigoCO ";
        }
        Query q = em.createNativeQuery(sql, ContaAuxiliarDetalhada.class);
        q.setParameter("codigo", cad.getCodigo());
        q.setParameter("exercicioConta", cad.getExercicio().getId());
        if (cad.getTipoContaAuxiliar() != null) {
            q.setParameter("tipoContaAuxiliarId", cad.getTipoContaAuxiliar().getId());
        }
        if (cad.getContaContabil() != null) {
            q.setParameter("contaContabilId", cad.getContaContabil().getId());
        }
        if (cad.getClassificacaoFuncional() != null) {
            q.setParameter("classificacao", cad.getClassificacaoFuncional());
        }
        if (cad.getContaDeDestinacao() != null) {
            q.setParameter("destId", cad.getContaDeDestinacao().getId());
        }
        if (cad.getConta() != null) {
            q.setParameter("contaId", cad.getConta().getId());
        }
        if (cad.getExercicioAtual() != null) {
            q.setParameter("exercicioAtual", cad.getExercicioAtual().getId());
        }
        if (cad.getExercicioOrigem() != null) {
            q.setParameter("exercicioOrigem", cad.getExercicioOrigem().getId());
        }
        if (cad.getDividaConsolidada() != null) {
            q.setParameter("dividaConsolidada", cad.getDividaConsolidada());
        }
        if (cad.getEs() != null) {
            q.setParameter("es", cad.getEs());
        }
        if (cad.getSubSistema() != null) {
            q.setParameter("subSistema", cad.getSubSistema().name());
        }
        if (cad.getUnidadeOrganizacional() != null) {
            q.setParameter("unidadeId", cad.getUnidadeOrganizacional().getId());
        }
        if (cad.getCodigoCO() != null) {
            q.setParameter("codigoCO", cad.getCodigoCO());
        }
        q.setMaxResults(1);
        List<ContaAuxiliarDetalhada> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            contaAuxiliarDetalhada = resultado.get(0);
            singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(contaAuxiliarDetalhada);
            return contaAuxiliarDetalhada;
        }
        return null;
    }

    public void popularContasAuxiliaresDetalhadas(Exercicio ex) {
        singletonContaAuxiliar.limparContasDetalhadas();
        String sql = "SELECT c.*, " +
            "                obj.contaDeDestinacao_id," +
            "                obj.conta_id," +
            "                obj.exercicioAtual_id," +
            "                obj.unidadeOrganizacional_id, " +
            "                obj.subSistema, " +
            "                obj.dividaConsolidada, " +
            "                obj.es, " +
            "                obj.classificacaoFuncional, " +
            "                obj.hashContaAuxiliarDetalhada, " +
            "                obj.contaContabil_id, " +
            "                obj.tipoContaAuxiliar_id, " +
            "                obj.exercicioOrigem_id, " +
            "                obj.codigoCO " +
            "     FROM ContaAuxiliarDetalhada obj " +
            "    inner join conta c on c.id = obj.id " +
            "   where c.EXERCICIO_ID = :exercicio";
        Query q = em.createNativeQuery(sql, ContaAuxiliarDetalhada.class);
        q.setParameter("exercicio", ex.getId());
        List<ContaAuxiliarDetalhada> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (ContaAuxiliarDetalhada conta : resultado) {
                ContaAuxiliarDetalhada contaAuxiliarDetalhada = singletonContaAuxiliar.buscarContaAuxiliarDetalhadaNoMap(conta);
                if (contaAuxiliarDetalhada == null) {
                    singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(conta);
                }
            }

        }
    }

    public PlanoDeContasExercicio recuperaCriaPlanoDeContaPlanoDeContasExercicio(int ano) {
        try {
            if (singletonContaAuxiliar.getPlanoDeContasExercicio() != null) {
                if (singletonContaAuxiliar.getPlanoDeContasExercicio().getExercicio().getAno().equals(ano)) {
                    return singletonContaAuxiliar.getPlanoDeContasExercicio();
                }
            }
            Query q = em.createQuery("SELECT obj from PlanoDeContasExercicio obj where obj.exercicio.ano=:param");
            q.setParameter("param", ano);
            PlanoDeContasExercicio planoDeContasExercicio = (PlanoDeContasExercicio) q.getSingleResult();
            criaPlanoDeContasAuxiliarSeNaoExiste(planoDeContasExercicio);
            singletonContaAuxiliar.setPlanoDeContasExercicio(planoDeContasExercicio);
            return planoDeContasExercicio;
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum Plano de Contas Exercicio para o ano " + ano + "! Entre em contato com o suporte");
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de um Plano de Contas Exercicio para o ano " + ano + "! Entre em contato com o suporte.");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar o Plano de Contas Exercicio para o ano " + ano + "! Exceção: " + ex.getMessage());
        }
    }

    private void criaPlanoDeContasAuxiliarSeNaoExiste(PlanoDeContasExercicio plde) {

        if (plde.getPlanoAuxiliar() == null) {
            PlanoDeContas pdc = new PlanoDeContas();
            pdc.setDataRegistro(new Date());
            pdc.setDescricao("Plano de Contas Auxiliar " + plde.getExercicio().getAno());
            pdc.setExercicio(plde.getExercicio());
//            pdc.setTipoConta();
            plde.setPlanoAuxiliar(pdc);
            atualizaEventoContabilFacade.salvarContaAuxiliar(plde);
            plde = em.find(PlanoDeContasExercicio.class, plde.getId());
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ContaAuxiliar buscarContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar, List<ObjetoParametro> objetoParametros, ContaContabil contaContabil, boolean isContaAuxiliarSistema, ParametroEvento parametroEvento,  ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        Object entidade = null;
        try {
            ObjetoParametro objPar = null;
            for (ObjetoParametro objetoParametro : objetoParametros) {
                if (ParametroEvento.isTipoObjetoParametro(tipoObjetoParametro, objetoParametro)) {
                    entidade = localizarEntidade(objetoParametro.getIdObjeto(), objetoParametro.getClasseObjeto());
                    if (entidade instanceof IGeraContaAuxiliar) {
                        objPar = objetoParametro;
                        break;
                    }
                }
            }
            if (objPar == null) {
                throw new ExcecaoNegocioGenerica(" O tipo de Conta Auxiliar " + tipoContaAuxiliar.getCodigo() + " - " + tipoContaAuxiliar.getDescricao() + " contem uma chave(\"" + tipoContaAuxiliar.getChave() + "\") que o sistema não reconhece.");
            }

            Exercicio exercicio = objPar.getItemParametroEvento().getParametroEvento().getExercicio();
            IGeraContaAuxiliar gca = (IGeraContaAuxiliar) entidade;
            if (entidade instanceof TransferenciaContaFinanceira ||
                entidade instanceof EstornoTransferencia ||
                entidade instanceof LiberacaoCotaFinanceira ||
                entidade instanceof EstornoLibCotaFinanceira ||
                entidade instanceof TransferenciaMesmaUnidade ||
                entidade instanceof EstornoTransfMesmaUnidade ||
                entidade instanceof TransfBensMoveis ||
                entidade instanceof TransfBensImoveis ||
                entidade instanceof TransferenciaBensEstoque ||
                entidade instanceof TransfBensIntangiveis) {
                return gerarMapContaAuxiliar(tipoContaAuxiliar, contaContabil,
                    (ParametroEvento.ComplementoId.RECEBIDO.equals(parametroEvento.getComplementoId()) ?
                        gca.getMapContaAuxiliarSiconfiRecebido(tipoContaAuxiliar, contaContabil) :
                        gca.getMapContaAuxiliarSiconfiConcedido(tipoContaAuxiliar, contaContabil)),
                    exercicio);
            } else {
                return gerarMapContaAuxiliar(tipoContaAuxiliar, contaContabil,
                    gca.getMapContaAuxiliarSiconfi(tipoContaAuxiliar, contaContabil),
                    exercicio);
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            logger.error("entidade:  " + entidade.getClass().toString() + " - tipoContaAuxiliar:  " + tipoContaAuxiliar);
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ContaAuxiliarDetalhada buscarContaAuxiliarDetalhada(TipoContaAuxiliar tipoContaAuxiliar, List<ObjetoParametro> objetoParametros, ContaContabil contaContabil, boolean isContaAuxiliarSistema, ParametroEvento parametroEvento, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        Object entidade = null;
        try {
            ObjetoParametro objPar = null;
            for (ObjetoParametro objetoParametro : objetoParametros) {
                if (ParametroEvento.isTipoObjetoParametro(tipoObjetoParametro, objetoParametro)) {
                    entidade = localizarEntidade(objetoParametro.getIdObjeto(), objetoParametro.getClasseObjeto());
                    if (entidade instanceof IGeraContaAuxiliar) {
                        objPar = objetoParametro;
                        break;
                    }
                }
            }
            if (objPar == null) {
                throw new ExcecaoNegocioGenerica(" O tipo de Conta Auxiliar Detalhada " + tipoContaAuxiliar.getCodigo() + " - " + tipoContaAuxiliar.getDescricao() + " contem uma chave(\"" + tipoContaAuxiliar.getChave() + "\") que o sistema não reconhece.");
            }

            Exercicio exercicio = objPar.getItemParametroEvento().getParametroEvento().getExercicio();
            IGeraContaAuxiliar gca = (IGeraContaAuxiliar) entidade;
            if (entidade instanceof TransferenciaContaFinanceira ||
                entidade instanceof EstornoTransferencia ||
                entidade instanceof LiberacaoCotaFinanceira ||
                entidade instanceof EstornoLibCotaFinanceira ||
                entidade instanceof TransferenciaMesmaUnidade ||
                entidade instanceof EstornoTransfMesmaUnidade ||
                entidade instanceof TransfBensMoveis ||
                entidade instanceof TransfBensImoveis ||
                entidade instanceof TransferenciaBensEstoque ||
                entidade instanceof TransfBensIntangiveis) {
                return gerarMapContaAuxiliarDetalhada(tipoContaAuxiliar, contaContabil,
                    (ParametroEvento.ComplementoId.RECEBIDO.equals(parametroEvento.getComplementoId()) ?
                        gca.getMapContaAuxiliarDetalhadaSiconfiRecebido(tipoContaAuxiliar, contaContabil) :
                        gca.getMapContaAuxiliarDetalhadaSiconfiConcedido(tipoContaAuxiliar, contaContabil)),
                    exercicio);
            } else {
                return gerarMapContaAuxiliarDetalhada(tipoContaAuxiliar, contaContabil,
                    gca.getMapContaAuxiliarDetalhadaSiconfi(tipoContaAuxiliar, contaContabil),
                    exercicio);
            }
        } catch (Exception e) {
            logger.error("detalhada-> entidade:  " + entidade.getClass().toString() + " - tipoContaAuxiliar:  " + tipoContaAuxiliar);
            logger.error("Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ContaAuxiliar gerarMapContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil, TreeMap mapContaAuxiliar, Exercicio exercicio) {
        TreeMap contasAuxiliares = mapContaAuxiliar;

        PlanoDeContasExercicio pdc = recuperaCriaPlanoDeContaPlanoDeContasExercicio(exercicio.getAno());
        ContaAuxiliar contaAuxiAnalitica = buscarContaAuxiliarPorCodigo(pdc.getPlanoAuxiliar(), tipoContaAuxiliar.getCodigo() + "." + contasAuxiliares.lastKey().toString(), contaContabil);

        if (contaAuxiAnalitica == null) {
            ContaAuxiliar c = new ContaAuxiliar();
            ContaAuxiliar contaAuxiliarNivel = buscarContaAuxiliarPorCodigo(pdc.getPlanoAuxiliar(), tipoContaAuxiliar.getCodigo(), contaContabil);
            if (contaAuxiliarNivel == null) {
                c.setContaContabil(contaContabil);
                c.setExercicio(exercicio);
                c.setDataRegistro(new Date());
                c.setAtiva(Boolean.TRUE);
                c.setTipoContaAuxiliar(tipoContaAuxiliar);
                c.setDescricao(tipoContaAuxiliar.getDescricao());
                c.setCodigo(tipoContaAuxiliar.getCodigo());
                c.setPlanoDeContas(pdc.getPlanoAuxiliar());
                c.setSuperior(contaContabil);
                if (buscarContaAuxiliarPorCodigo(pdc.getPlanoAuxiliar(), c.getCodigo(), contaContabil) == null) {
                    c = (ContaAuxiliar) atualizaEventoContabilFacade.salvarContaAuxiliar(c);
                }
            } else {
                c = contaAuxiliarNivel;
            }
            singletonContaAuxiliar.adicionarContaAuxiliarNoMap(pdc.getPlanoAuxiliar(), c.getCodigo(), c);
            ContaAuxiliar cfim = c;
            Set set = contasAuxiliares.entrySet();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                ContaAuxiliar cinicio = new ContaAuxiliar();
                cinicio.setContaContabil(contaContabil);
                cinicio.setExercicio(exercicio);
                cinicio.setAtiva(Boolean.TRUE);
                cinicio.setDataRegistro(new Date());
                cinicio.setTipoContaAuxiliar(tipoContaAuxiliar);
                ContaAuxiliar contaAuxiliarNivel2 = buscarContaAuxiliarPorCodigo(pdc.getPlanoAuxiliar(), tipoContaAuxiliar.getCodigo() + "." + me.getKey().toString(), contaContabil);
                if (contaAuxiliarNivel2 == null) {
                    if (me.getKey().toString() != null && !me.getKey().toString().trim().isEmpty()) {
                        cinicio.setCodigo(tipoContaAuxiliar.getCodigo() + "." + me.getKey().toString());
                        cinicio.setDescricao(me.getValue().toString());
                        cinicio.setSuperior(cfim);
                        cinicio.setPlanoDeContas(pdc.getPlanoAuxiliar());
                        cinicio = (ContaAuxiliar) atualizaEventoContabilFacade.salvarContaAuxiliar(cinicio);
                    }
                } else {
                    cinicio = contaAuxiliarNivel2;
                }
                singletonContaAuxiliar.adicionarContaAuxiliarNoMap(pdc.getPlanoAuxiliar(), cinicio.getCodigo(), cinicio);
                cfim = cinicio;
            }
            return cfim;
        } else {
            singletonContaAuxiliar.adicionarContaAuxiliarNoMap(pdc.getPlanoAuxiliar(), contaAuxiAnalitica.getCodigo(), contaAuxiAnalitica);
            return contaAuxiAnalitica;
        }
    }

    public ContaAuxiliarDetalhada gerarMapContaAuxiliarDetalhada(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil, TreeMap mapContaAuxiliarDetalhada, Exercicio exercicio) {
        TreeMap contasAuxiliaresDetalhadas = mapContaAuxiliarDetalhada;

        PlanoDeContasExercicio pdc = recuperaCriaPlanoDeContaPlanoDeContasExercicio(exercicio.getAno());
        ContaAuxiliarDetalhada contaAuxiAnalitica = (ContaAuxiliarDetalhada) contasAuxiliaresDetalhadas.lastEntry().getValue();
        contaAuxiAnalitica.setContaContabil(contaContabil);
        contaAuxiAnalitica.setCodigo(tipoContaAuxiliar.getCodigo() + "." + contasAuxiliaresDetalhadas.lastKey().toString());
        contaAuxiAnalitica.setTipoContaAuxiliar(tipoContaAuxiliar);
        contaAuxiAnalitica.setExercicio(exercicio);
        ContaAuxiliarDetalhada retorno = buscarContaAuxiliarDetalhadaPorHash(contaAuxiAnalitica);
        if (retorno == null) {
            ContaAuxiliarDetalhada c = new ContaAuxiliarDetalhada();
            c.setContaContabil(contaContabil);
            c.setCodigo(tipoContaAuxiliar.getCodigo());
            c.setTipoContaAuxiliar(tipoContaAuxiliar);
            c.setExercicio(exercicio);
            ContaAuxiliarDetalhada contaAuxiliarNivel = buscarContaAuxiliarDetalhadaPorHash(c);
            if (contaAuxiliarNivel == null) {
                c.setContaContabil(contaContabil);
                c.setExercicio(exercicio);
                c.setDataRegistro(new Date());
                c.setAtiva(Boolean.TRUE);
                c.setDescricao(tipoContaAuxiliar.getDescricao());
                c.setCodigo(tipoContaAuxiliar.getCodigo());
                c.setPlanoDeContas(pdc.getPlanoAuxiliar());
                c.setSuperior(contaContabil);
                c.setHashContaAuxiliarDetalhada(c.hashCode());
                if (buscarContaAuxiliarDetalhadaPorHash(c) == null) {
                    em.persist(c);
                    singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(c);
                } else {
                    c = buscarContaAuxiliarDetalhadaPorHash(c);
                }
            } else {
                c = contaAuxiliarNivel;
            }

            singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(c);
            ContaAuxiliarDetalhada cfim = c;
            Set set = contasAuxiliaresDetalhadas.entrySet();
            Iterator i = set.iterator();
            ContaAuxiliarDetalhada contaAuxiliarDetalhadaSuperior = c;
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                ContaAuxiliarDetalhada contaAuxiliarDetalhada = (ContaAuxiliarDetalhada) me.getValue();
                contaAuxiliarDetalhada.setCodigo(tipoContaAuxiliar.getCodigo() + "." + me.getKey().toString());
                contaAuxiliarDetalhada.setContaContabil(contaContabil);
                contaAuxiliarDetalhada.setExercicio(exercicio);
                contaAuxiliarDetalhada.setPlanoDeContas(pdc.getPlanoAuxiliar());
                contaAuxiliarDetalhada.setDataRegistro(new Date());
                contaAuxiliarDetalhada.setAtiva(Boolean.TRUE);
                contaAuxiliarDetalhada.setTipoContaAuxiliar(tipoContaAuxiliar);
                if (buscarContaAuxiliarDetalhadaPorHash(contaAuxiliarDetalhada) == null) {
                    if (contaAuxiliarDetalhadaSuperior != null) {
                        contaAuxiliarDetalhada.setSuperior(contaAuxiliarDetalhadaSuperior);
                    }
                    contaAuxiliarDetalhada.setHashContaAuxiliarDetalhada(contaAuxiliarDetalhada.hashCode());
                    em.persist(contaAuxiliarDetalhada);
                    singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(contaAuxiliarDetalhada);
                } else {
                    contaAuxiliarDetalhada = buscarContaAuxiliarDetalhadaPorHash(contaAuxiliarDetalhada);
                }
                contaAuxiliarDetalhadaSuperior = contaAuxiliarDetalhada;
                cfim = contaAuxiliarDetalhada;
            }
            return cfim;
        } else {
            singletonContaAuxiliar.adicionarContaAuxiliarDetalhadaNoMap(retorno);
            return retorno;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void limparContasDetalhadas() {
        singletonContaAuxiliar.limparContasDetalhadas();
    }
}
