/*
 * Codigo gerado automaticamente em Wed Jan 04 11:23:36 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.interfaces.ExecutaScriptEventoFP;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.script.Resultado;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class FichaRPAFacade extends AbstractFacade<FichaRPA> {

    private static final Logger logger = LoggerFactory.getLogger(FichaRPAFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoFPFacade eventoFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FichaRPAFacade() {
        super(FichaRPA.class);
    }

    public FichaRPA gerarFichaRPA(final PrestadorServicos autonomo, FichaRPA fichaRPA, final ExecutaScriptEventoFP executaScriptEventoFP, List<ItemErroScript> itemErroScripts) {
        fichaRPA.setItemFichaRPAs(new ArrayList<ItemFichaRPA>());
        fichaRPA.setGeradoEm(new Date());
        fichaRPA.setPrestadorServicos(autonomo);
        fichaRPA = em.merge(fichaRPA);

        List<EventoFP> eventos = eventoFPFacade.recuperarEventosAtivosRHPorTipoExecucao(TipoExecucaoEP.RPA);
        geraItem(eventos, executaScriptEventoFP, autonomo, fichaRPA, itemErroScripts);
        if (itemErroScripts.isEmpty()) {
            Double valorRetirado = 0.0;
            Double totalVantagen = 0.0;
            fichaRPA.setValorLiquido(new BigDecimal(BigInteger.ZERO));
            for (ItemFichaRPA item : fichaRPA.getItemFichaRPAs()) {
                if (item.getEventoFP().getTipoEventoFP() == TipoEventoFP.INFORMATIVO) {
                    continue;
                }
                if (item.getEventoFP().getTipoEventoFP() == TipoEventoFP.VANTAGEM) {
                    totalVantagen += item.getValor().doubleValue();
                }
                if (item.getEventoFP().getTipoEventoFP() == TipoEventoFP.DESCONTO) {
                    valorRetirado += item.getValor().doubleValue();
                }

            }
            fichaRPA.setValor(new BigDecimal(totalVantagen));
            fichaRPA.setValorLiquido(fichaRPA.getValor().subtract(new BigDecimal(valorRetirado)));
            em.merge(fichaRPA);
        } else {
            for (ItemErroScript item : itemErroScripts) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ops", "Ocorregam erros ao tentar gerar o RPA... Evento: " + item.getNomeFunction() + "  Código do erro: " + item.getMensagemException()));
            }
        }
        return fichaRPA;
    }

    private void geraItem(List<EventoFP> eventos, final ExecutaScriptEventoFP executaScriptEventoFP, PrestadorServicos autonomo, final FichaRPA fichaRPA, List<ItemErroScript> itemErroScripts) {
        for (EventoFP evento : eventos) {
            logger.debug("Evento: {}", evento);
            Resultado<Boolean> resultadoRegra = executaScriptEventoFP.executaRegra(evento, autonomo);
            if (resultadoRegra.naoTemErro()) {
                logger.debug("Regra de Evento: [{}], n\u00e3o tem erro.", evento.getDescricao());
                if (resultadoRegra.getValor()) {
                    logger.debug("Regra de Evento: [{}], deve ser executada.", evento.getDescricao());
                    Resultado<Double> resultadoFormula = executaScriptEventoFP.executaFormula(evento, autonomo);
                    Resultado<Double> resultadoReferencia = executaScriptEventoFP.executaReferencia(evento, autonomo);
                    Resultado<Double> resultadoBaseDeCalculo = executaScriptEventoFP.executaValorBaseDeCalculo(evento, autonomo);
                    if (resultadoFormula.naoTemErro() && resultadoReferencia.naoTemErro() && resultadoBaseDeCalculo.naoTemErro()) {
                        logger.debug("Regra de Evento: [{0}], n\u00e3o possui nenhum erro.", evento.getDescricao());
                        ItemFichaRPA itemFicha = new ItemFichaRPA();
                        itemFicha.setEventoFP(evento);
                        itemFicha.setFichaRPA(fichaRPA);

                        logger.debug("Evento: {}", evento.getCodigo());
                        final Double valorFormula = resultadoFormula.getValor();
                        if (valorFormula.compareTo(0.0) > 0) {
                            logger.debug("Valor Formula: {}", valorFormula);
                            if (itemFicha.getEventoFP().getArredondarValor()) {
                                itemFicha.setValor(BigDecimal.valueOf(valorFormula).setScale(2, BigDecimal.ROUND_HALF_UP));
                                itemFicha.setValorBaseDeCalculo(BigDecimal.valueOf(resultadoBaseDeCalculo.getValor()).setScale(2, BigDecimal.ROUND_HALF_UP));
                                itemFicha.setValorReferencia(BigDecimal.valueOf(resultadoReferencia.getValor()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            } else {
                                itemFicha.setValor(UtilRH.truncate(String.valueOf(valorFormula)));
                                itemFicha.setValorBaseDeCalculo(UtilRH.truncate(String.valueOf(resultadoBaseDeCalculo.getValor())));
                                itemFicha.setValorReferencia(UtilRH.truncate(String.valueOf(resultadoReferencia.getValor())));
                            }

                            if (itemErroScripts.isEmpty()) {
                                logger.debug("Persistindo item: [" + itemFicha + "]");
                                em.persist(itemFicha);
                                fichaRPA.getItemFichaRPAs().add(itemFicha);
                            }
                        }
                    } else {

                        logger.debug("Regra de Evento: [{0}], com erro em algum script.", evento.getDescricao());
                        logger.debug("gerarItemFichaRPA, Tem erro na formula:{0}", resultadoFormula.temErro());
                        logger.debug("Tem erro no valor integral:{0}", resultadoBaseDeCalculo.temErro());
                        logger.debug("Tem erro no valor referencia:{0}", resultadoReferencia.temErro());

                        Resultado[] resultados = {resultadoFormula, resultadoBaseDeCalculo, resultadoReferencia};
                        for (Resultado r : resultados) {
                            addListaErros(itemErroScripts, r.getItemErroScript());
                        }
                    }
                }
            } else {
                addListaErros(itemErroScripts, resultadoRegra.getItemErroScript());
            }

        }
    }

    public void addListaErros(List<ItemErroScript> itemErroScripts, ItemErroScript item) {
        if (item != null) {
            itemErroScripts.add(item);
        }
    }

    @Override
    public FichaRPA recuperar(Object id) {
        FichaRPA ficha = em.find(FichaRPA.class, id);
        ficha.getItemFichaRPAs().size();
        return ficha;
    }

    @Override
    public void remover(FichaRPA entity) {
        entity = recuperar(entity.getId());
        em.remove(entity);
    }

    public String retornaUltimoCodigo() {
        Integer i;
        String hql = " select max(cast(obj.numero as integer)) from FichaRPA obj ";
        Query query = getEntityManager().createQuery(hql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            i = (Integer) query.getSingleResult();
            if (i == null) {
                i = 0;
            }
            i += 1;
        } else {
            return "1";
        }
        return i + "";
    }

    public List<VinculoFPEventoEsocial> buscarPrestadorServicoPorMesAndAnoAndUnidades(RegistroEventoEsocial registroEventoEsocial,
                                                                                      Pessoa pessoa, List<Long> idsUnidade,
                                                                                      TipoArquivoESocial tipoArquivoESocial,
                                                                                      Long empregadorId) {
        String sql = "select distinct pf.id, pf.nome, pf.cpf from ficharpa ficha " +
            "                  inner join PRESTADORSERVICOS prestador on ficha.PRESTADORSERVICOS_ID = prestador.ID " +
            "                  inner join TomadorDeServico tomador on ficha.tomador_ID = tomador.ID " +
            "                  inner join pessoafisica pf on prestador.PRESTADOR_ID = pf.id " +
            " where extract (year from ficha.DATASERVICO)  = :ano and extract(month  from ficha.DATASERVICO) = :mes ";
        if (pessoa != null) {
            sql += " and pf.id = :idPessoa";
        }
        if (registroEventoEsocial.getPrestadorServicos() != null) {
            sql += " and prestador.id = :idPrestador ";
        }
        if (idsUnidade != null) {
            sql += " and prestador.LOTACAO_ID in (:unidades) ";
        }

        sql += " order by pf.NOME";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMes());
        if (pessoa != null) {
            q.setParameter("idPessoa", pessoa.getId());
        }
        if (registroEventoEsocial.getPrestadorServicos() != null) {
            q.setParameter("idPrestador", registroEventoEsocial.getPrestadorServicos().getId());
        }
        if (idsUnidade != null) {
            q.setParameter("unidades", idsUnidade);
        }

        List resultList = q.getResultList();

        logger.info("Qtde fichas RPA > " + resultList.size());
        if (!resultList.isEmpty()) {
            List<VinculoFPEventoEsocial> item = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                VinculoFPEventoEsocial vinculoFPEventoEsocial = new VinculoFPEventoEsocial();
                vinculoFPEventoEsocial.setIdPessoa(Long.parseLong(obj[0].toString()));
                vinculoFPEventoEsocial.setNome((String) obj[1]);
                vinculoFPEventoEsocial.setCpf((String) obj[2]);
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                vinculoFPEventoEsocial.setRegistroEventoEsocial(registroEventoEsocial);
                item.add(vinculoFPEventoEsocial);
            }
            return item;
        }
        return null;
    }

    public List<Long> buscarIdsFichas(RegistroEventoEsocial registroEventoEsocial, Long idPrestador) {
        String sql = " select distinct ficha.id " +
            " from ficharpa ficha " +
            "         inner join PRESTADORSERVICOS prestador on ficha.PRESTADORSERVICOS_ID = prestador.ID " +
            "         inner join TomadorDeServico tomador on ficha.tomador_ID = tomador.ID " +
            "         inner join pessoafisica pf on prestador.PRESTADOR_ID = pf.id " +
            " where pf.id = :idPrestador and " +
            "      extract(year from ficha.DATASERVICO) = :ano " +
            "  and extract(month from ficha.DATASERVICO) = :mes ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", registroEventoEsocial.getExercicio().getAno());
        q.setParameter("mes", registroEventoEsocial.getMes().getNumeroMes());
        q.setParameter("idPrestador", idPrestador);
        List resultList = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object o : resultList) {
                ids.add(((BigDecimal) o).longValue());
            }
        }
        return ids;
    }

    public List<FichaRPA> buscarRPAsPorMesAndAno(PrestadorServicos prestador, Integer mes, Integer ano) {
        String sql = " select distinct ficha.* " +
            " from itemficharpa itemficha " +
            "       inner join ficharpa ficha on ficha.id = itemficha.ficharpa_id " +
            "       inner join eventofp evento on evento.id = itemficha.eventofp_id " +
            "       inner join prestadorservicos vinculo on vinculo.id = ficha.prestadorservicos_id " +
            " where vinculo.prestador_id  = :pessoaId " +
            "  and extract(month from ficha.dataservico) = :mes " +
            "  and extract(year from ficha.dataservico) = :ano";
        Query q = em.createNativeQuery(sql, FichaRPA.class);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("pessoaId", prestador.getPrestador().getId());
        return q.getResultList();
    }

    public FichaRPA buscarFichaRPAPorId(Long idFicha) {
        String sql = " select ficha.*" +
            "    from FICHARPA ficha " +
            "    where ficha.id = :idFicha ";
        Query q = em.createNativeQuery(sql, FichaRPA.class);
        q.setParameter("idFicha", idFicha);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (FichaRPA) resultList.get(0);
        }
        return null;
    }

    public List<Long> buscarIdsFichaRPAPorPrestador(PrestadorServicos prestador) {
        String sql = "select distinct ficha.id " +
            " from ficharpa ficha " +
            " where ficha.prestadorservicos_id = :prestador ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("prestador", prestador.getId());
        List<BigDecimal> resultList = q.getResultList();
        List<Long> retorno = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (BigDecimal resultado : resultList) {
                retorno.add(resultado.longValue());
            }
        }
        return retorno;
    }
}
