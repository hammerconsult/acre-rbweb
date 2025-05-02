/*
 * Codigo gerado automaticamente em Tue Feb 14 10:59:19 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.service.S2400Service;
import br.com.webpublico.esocial.service.S2405Service;
import br.com.webpublico.esocial.service.S2410Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class AposentadoriaFacade extends AbstractFacade<Aposentadoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private PrevidenciaVinculoFPFacade previdenciaVinculoFPFacade;
    @EJB
    private OpcaoValeTransporteFPFacade opcaoValeTransporteFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private SindicatoVinculoFPFacade sindicatoVinculoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private MedicoFacade medicoFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    private S2400Service s2400Service;
    private S2405Service s2405Service;
    private S2410Service s2410Service;

    public AposentadoriaFacade() {
        super(Aposentadoria.class);

    }

    @PostConstruct
    private void init() {
        s2400Service = (S2400Service) Util.getSpringBeanPeloNome("s2400Service");
        s2405Service = (S2405Service) Util.getSpringBeanPeloNome("s2405Service");
        s2410Service = (S2410Service) Util.getSpringBeanPeloNome("s2410Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Aposentadoria> lista() {
        return em.createQuery("from Aposentadoria ap order by ap.contratoFP.matriculaFP.pessoa.nome").getResultList();
    }

    @Override
    public Aposentadoria recuperar(Object id) {
        Aposentadoria ap = em.find(Aposentadoria.class, id);
        ap.getAssociacaosVinculosFPs().size();
        ap.getSindicatosVinculosFPs().size();
        ap.getItemAposentadorias().size();
        ap.getInvalidezAposentados().size();
        ap.getLotacaoFuncionals().size();
        ap.getRecursosDoVinculoFP().size();
        ap.getOpcaoSalarialVinculoFP().size();
        ap.getAtosLegais().size();
        return ap;
    }

    public Aposentadoria recuperarParaArquivoAtuarial(Object id) {
        Aposentadoria ap = em.find(Aposentadoria.class, id);
        ap.getLotacaoFuncionals().size();
        ap.getContratoFP().getCargos().size();
        return ap;
    }

    public Aposentadoria recuperarItensAposentadoria(Object id) {
        Aposentadoria ap = em.find(Aposentadoria.class, id);
        ap.getItemAposentadorias().size();
        return ap;
    }

    public void salvarAposentadoria(Aposentadoria aposentadoria) {
        try {
            em.merge(aposentadoria);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    // Verifica se o vinculo possuía alguma pensão alimentícia, se tiver duplica a mesma atribuindo a nova ao vinculo aposentado
    public void migrarPensoes(Aposentadoria aposentadoria) {
        List<PensaoAlimenticia> pensoes = pensaoAlimenticiaFacade.buscarPensoesVigentesPorInstituidor(aposentadoria.getContratoFP(), aposentadoria.getInicioVigencia());
        if (pensoes != null && !pensoes.isEmpty()) {
            for (PensaoAlimenticia pensao : pensoes) {
                List<BeneficioPensaoAlimenticia> beneficiarios = Lists.newArrayList();
                List<ValorPensaoAlimenticia> valores = Lists.newArrayList();
                PensaoAlimenticia novoObjeto = new PensaoAlimenticia();
                novoObjeto.setVinculoFP(aposentadoria);
                for (BeneficioPensaoAlimenticia beneficio : pensao.getBeneficiosPensaoAlimenticia()) {
                    BeneficioPensaoAlimenticia novoBeneficio = new BeneficioPensaoAlimenticia();
                    novoBeneficio.setPessoaFisicaBeneficiario(beneficio.getPessoaFisicaBeneficiario());
                    novoBeneficio.setPessoaFisicaResponsavel(beneficio.getPessoaFisicaResponsavel());
                    novoBeneficio.setPensaoAlimenticia(novoObjeto);
                    novoBeneficio.setDataRegistro(beneficio.getDataRegistro());
                    novoBeneficio.setInicioVigencia(beneficio.getInicioVigencia());
                    novoBeneficio.setFinalVigencia(beneficio.getFinalVigencia());
                    for (ValorPensaoAlimenticia valorPensaoAlimenticia : beneficio.getValoresPensaoAlimenticia()) {
                        ValorPensaoAlimenticia valor = new ValorPensaoAlimenticia();
                        valor.setBeneficioPensaoAlimenticia(novoBeneficio);
                        valor.setInicioVigencia(valorPensaoAlimenticia.getInicioVigencia());
                        valor.setFimVigencia(valorPensaoAlimenticia.getFinalVigencia());
                        valor.setValor(valorPensaoAlimenticia.getValor());
                        valor.setDataRegistro(valorPensaoAlimenticia.getDataRegistro());
                        valor.setBaseFP(valorPensaoAlimenticia.getBaseFP());
                        valor.setIrrfFicticio(valorPensaoAlimenticia.getIrrfFicticio());
                        valor.setFinalVigencia(valorPensaoAlimenticia.getFinalVigencia());
                        valores.add(valor);
                    }
                    novoBeneficio.setValoresPensaoAlimenticia(valores);
                    novoBeneficio.setHierarquiaOrganizacional(beneficio.getHierarquiaOrganizacional());
                    novoBeneficio.setAtoLegal(beneficio.getAtoLegal());
                    novoBeneficio.setTipoValorPensaoAlimenticia(beneficio.getTipoValorPensaoAlimenticia());
                    novoBeneficio.setEventoFP(beneficio.getEventoFP());
                    novoBeneficio.setContaCorrenteBancaria(beneficio.getContaCorrenteBancaria());
                    novoBeneficio.setGrauParentesco(beneficio.getGrauParentesco());
                    beneficiarios.add(novoBeneficio);
                }
                novoObjeto.setBeneficiosPensaoAlimenticia(beneficiarios);
                em.persist(novoObjeto);
            }
        }
    }

    @Override
    public void salvar(Aposentadoria entity) {
        ContratoFP c = em.find(ContratoFP.class, entity.getContratoFP().getId());
        Calendar calend = Calendar.getInstance();
        calend.setTime(entity.getInicioVigencia());
        calend.add(Calendar.DAY_OF_YEAR, -1);
        c.setFinalVigencia(calend.getTime());
        em.merge(c);
        em.merge(entity);

        try {
            Entidade entidade = entidadeFacade.buscarEntidadePorUnidade(entity.getUnidadeOrganizacional(), new Date());

            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(entidade);

            try {
                EmpregadorESocial empregadorPorCnpj = s2405Service.geteSocialService().getEmpregadorPorCnpj(config.getEntidade().getPessoaJuridica().getCnpj());
                List<EventoESocialDTO> eventosPorIdEsocial = s2405Service.geteSocialService().getEventosPorEmpregadorAndTipoArquivo(empregadorPorCnpj, TipoArquivoESocial.S2400, null,0, 10);
                if (eventosPorIdEsocial.isEmpty()) {
                    return;
                }
            } catch (HttpClientErrorException e) {
                logger.info("Erro enviarS2405 {}", e.getMessage());
                return;
            } catch (Exception e) {
                logger.error("Erro enviarS2405", e);
                return;
            }

            enviarS2405ESocial(config, entity);
            //enviarS2410ESocial(config, entity);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2405: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2405: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS2405: ", e);
        }

    }

    @Override
    public void salvarNovo(Aposentadoria entity) {
        em.persist(entity);
        //ao aposentar o servidor o vinculo de contrato fica com a data da aposetadoria com 1 dia a menos.
        Calendar c = Calendar.getInstance();
        c.setTime(entity.getInicioVigencia());
        c.add(Calendar.DAY_OF_YEAR, -1);
        contratoFPFacade.processarHistoricoAndEncerrarVigenciasPorContratoFP(entity.getContratoFP(), c.getTime());

        //pego a data da aposentadoria e aumento 1 dia
        //para ser o inicio da vigencia da nova situação
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(entity.getInicioVigencia());
//        calendario.add(Calendar.DATE, 1);

        criarSituacaoContratoFP(entity, calendario.getTime());

        //criar o novo provimentoFP
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(entity);
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.APOSENTADORIA));
        provimentoFP.setDataProvimento(entity.getInicioVigencia());
        provimentoFP.setAtoLegal(entity.getAtosLegais().get(0).getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(entity.getContratoFP()));
        em.persist(provimentoFP);

        entity.setProvimentoFP(provimentoFP);
        em.merge(entity);
        migrarPensoes(entity);
    }

    private void criarSituacaoContratoFP(Aposentadoria aposentadoria, Date data) {
        SituacaoContratoFP situacaoContratoFPExoneracaoRescisao = situacaoContratoFPFacade.buscarSituacaoContratoFPPorContratoFPAndCodigoSituacaoFuncional(aposentadoria.getContratoFP(), SituacaoFuncional.EXONERADO_RESCISO);
        if (situacaoContratoFPExoneracaoRescisao != null && situacaoContratoFPExoneracaoRescisao.getInicioVigencia().compareTo(data) == 0) {
            ContratoFP contrato = contratoFPFacade.recuperarContratoComSituacaoFuncional(aposentadoria.getContratoFP().getId());
            contrato.getSituacaoFuncionals().remove(situacaoContratoFPExoneracaoRescisao);
            em.merge(contrato);
        }
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setInicioVigencia(data);
        situacaoContratoFP.setContratoFP(aposentadoria.getContratoFP());
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.APOSENTADO));
        em.persist(situacaoContratoFP);
    }


    private void atualizarFinalVigenciaContrato(VinculoFP vinculoFP) {
        em.createNativeQuery("update vinculofp set finalvigencia = null where id = " + vinculoFP.getId()).executeUpdate();
    }

    @Override
    public void remover(Aposentadoria entity) throws ValidacaoException {
        Object chave = Persistencia.getId(entity);
        Object obj = recuperar(chave);

        ContratoFP contratoFP = entity.getContratoFP();
        atualizarFinalVigenciaContrato(contratoFP);
        contratoFP = contratoFPFacade.recuperar(contratoFP.getId());
        contratoFPFacade.voltarFinalVigenciaPrevidenciaVinculoFP(contratoFP);
        contratoFPFacade.voltarFinalVigenciaOpcaoValeTransporteFP(contratoFP);
        contratoFPFacade.voltarFinalVigenciaLotacaoFuncionalAndHorarioContratoFP(contratoFP);
        contratoFPFacade.voltarFinalVigenciaSindicatoVinculoFP(contratoFP);
        contratoFPFacade.voltarFinalVigenciaSituacaoContratoFP(contratoFP, SituacaoFuncional.APOSENTADO);
        contratoFPFacade.voltarFinalVigenciaRecursoDoVinculoFP(contratoFP);
        contratoFPFacade.voltarFinalVigenciaEnquadramentoFuncional(contratoFP);
        contratoFPFacade.voltarFinalVigenciaLancamentoFP(contratoFP);

        if (obj != null) {
            getEntityManager().remove(obj);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "O Registro foi excluído"));
    }

    public void removerProvimentoAposentadoria(Aposentadoria entity) {
        if(entity.getProvimentoFP() != null){
            ProvimentoFP provimento = em.find(ProvimentoFP.class, entity.getProvimentoFP().getId());
            entity.setProvimentoFP(null);
            em.merge(entity);
            provimento.setVinculoFP(null);
            em.remove(provimento);
        }
    }

    public boolean temAposentadoriaCalculada(Aposentadoria ap) {
        Query q = em.createQuery("from FichaFinanceiraFP ficha where ficha.vinculoFP.id = :id and rownum = 1");
        q.setParameter("id", ap.getId());
        return q.getResultList().isEmpty();
    }

    public void fecharVigenciaAposentadoria(PessoaFisica pf, Date finalVigencia) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" from Aposentadoria aposentadoria");
        queryString.append(" where aposentadoria.contratoFP.matriculaFP.pessoa = :pessoa");
        queryString.append(" and :data >= aposentadoria.inicioVigencia");
        queryString.append(" and :data <= coalesce(aposentadoria.finalVigencia, :data)");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("pessoa", pf);
        q.setParameter("data", finalVigencia);

        for (Aposentadoria aposentadoria : (List<Aposentadoria>) q.getResultList()) {
            aposentadoria.setFinalVigencia(finalVigencia);
            em.merge(aposentadoria);
        }
    }

    /**
     * Verifica se o ContratoFP é de um servidor já aposentado, não respeitando
     * vigências
     *
     * @param contratoFP
     * @return
     */
    public boolean isAposentado(ContratoFP contratoFP) {
        Query q = em.createQuery(" from Aposentadoria a where a.contratoFP = :parametroContrato ");
        q.setParameter("parametroContrato", contratoFP);
        return !q.getResultList().isEmpty();
    }

    public Aposentadoria recuperaAposentadoriaPorContratoFP(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from Aposentadoria a where a.contratoFP = :parametroContrato ");
            q.setMaxResults(1);
            q.setParameter("parametroContrato", contratoFP);
            return (Aposentadoria) q.getSingleResult();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }


    public List<Aposentadoria> recuperaTodasAposentadoriasPorContratoFP(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from Aposentadoria a where a.contratoFP = :parametroContrato ");
            q.setParameter("parametroContrato", contratoFP);
            return (List<Aposentadoria>) q.getResultList();
        } catch (Exception e) {
            //e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public Aposentadoria recuperarUltimaAposentadoriaPorContratoFP(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from Aposentadoria a " +
                " where a.contratoFP = :parametroContrato" +
                " and (:dataAtual between trunc(a.inicioVigencia) and coalesce(trunc(a.finalVigencia), :dataAtual) " +
                " or a.inicioVigencia > :dataAtual )" +
                " order by a.inicioVigencia desc");
            q.setMaxResults(1);
            q.setParameter("parametroContrato", contratoFP);
            q.setParameter("dataAtual", new Date());
            return (Aposentadoria) q.getSingleResult();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }


    public boolean aposentadoriaPosteriorAoProvimentoReversao(Aposentadoria aposentadoria) {

        Query q = em.createQuery(" from Aposentadoria a " +
            " where a.matriculaFP.matricula = :matriculaAposentadoria" +
            " and a.numero > :contratoAposentadoria " +
            " order by a.inicioVigencia desc");
        q.setMaxResults(1);
        q.setParameter("matriculaAposentadoria", aposentadoria.getMatriculaFP().getMatricula());
        q.setParameter("contratoAposentadoria", aposentadoria.getNumero());
        return !q.getResultList().isEmpty();

    }

    public Aposentadoria recuperarUltimaAposentadoriaPosteriorAoProvimentoReversao(String matrincula) {

        Query q = em.createQuery(" from Aposentadoria a " +
            " where a.matriculaFP.matricula = :matriculaContrato" +
            " order by a.numero desc");
        q.setMaxResults(1);
        q.setParameter("matriculaContrato", matrincula);
        return (Aposentadoria) q.getSingleResult();

    }

    public List<ContratoFP> recuperaContratos(List<ContratoFP> contratos) {
        Query q = em.createQuery("select contrato from Aposentadoria aposentadoria "
            + "inner join aposentadoria.contratoFP contrato "
            + "where contrato in (:conts)");
        q.setParameter("conts", contratos);

        return q.getResultList();
    }

    public List<Aposentadoria> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select aposentadoria from Aposentadoria aposentadoria "
            + " inner join aposentadoria.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<Aposentadoria> recuperaAposentadoInvalidez(String s) {
        String sql = "  select * from Aposentadoria ap " +
            "  inner join vinculofp vinculo on ap.id = vinculo.id  " +
            "  inner join matriculafp mat on vinculo.matriculafp_id = mat.id  " +
            "  inner join pessoafisica pf on mat.pessoa_id = pf.id  " +
            "  inner join TipoAposentadoria tipo on tipo.id = ap.tipoaposentadoria_id  " +
            "             where tipo.codigo = 3 " +
            " and not exists(select 1 from provimentoreversao pr where pr.aposentadoria_id = ap.id)" +
            "             and ((lower(pf.nome) like :filtro) or (lower(mat.matricula) like :filtro)or (lower(pf.cpf) like :filtro))";
        Query q = em.createNativeQuery(sql, Aposentadoria.class);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<Aposentadoria> recuperarAposentadoriasVigentesComFichaFinanceira(Integer ano, Integer mes, Date data) {
        Query q = em.createQuery("select apo from FichaFinanceiraFP ff, Aposentadoria apo join ff.vinculoFP vinculo where vinculo = apo " +
            "                       and ff.folhaDePagamento.ano = :ano and ff.folhaDePagamento.mes = :mes" +
            "                       and :data between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, :data) order by cast(apo.matriculaFP.matricula as integer)");
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("data", data, TemporalType.DATE);
        //System.out.println("total de aposentados -> " + q.getResultList().size());
        return q.getResultList();
    }

    public List<ItemAposentadoria> getItemAposentadoriaEventoFPByAposetado(VinculoFP vinculoFP) {
        Query q = em.createQuery("select item from ItemAposentadoria  item inner join item.aposentadoria apo " +
            "   where apo.id = :id and :data between item.inicioVigencia and coalesce(item.finalVigencia,:data) ");
        q.setParameter("data", new Date());
        q.setParameter("id", vinculoFP.getId());
        return q.getResultList();
    }

    public List<ItemAposentadoria> getItemAposentadoriaEventoFP(VinculoFP vinculoFP) {
        Query q = em.createQuery("select item from ItemAposentadoria  item inner join item.aposentadoria apo " +
            "   where apo.id = :id ");
        q.setParameter("id", vinculoFP.getId());
        return q.getResultList();
    }

    public List<EventoFP> getEventoFPByAposetado(VinculoFP vinculoFP) {
        Query q = em.createQuery("select item.eventoFP from ItemAposentadoria  item inner join item.aposentadoria apo " +
            "   where apo.id = :id and :data between item.inicioVigencia and coalesce(item.finalVigencia,:data) ");
        q.setParameter("data", new Date());
        q.setParameter("id", vinculoFP.getId());
        return q.getResultList();
    }

    public ItemAposentadoria recuperaItemAposentadoriaVigente(VinculoFP vinculoFP, Date dataReferencia) {
        try {
            String hql = "select item from ItemAposentadoria item " +
                " where item.aposentadoria = :aposentadoria" +
                " and :dataVigencia between item.inicioVigencia and coalesce(item.finalVigencia, :dataVigencia)";

            Query q = em.createQuery(hql);
            q.setParameter("dataVigencia", dataReferencia);
            q.setParameter("aposentadoria", vinculoFP);
            return (ItemAposentadoria) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<Aposentadoria> recuperarAposentadosParaInvalidez() {
        logger.debug("iniciando a recuperação dos aposentados invalidos...");
        List<Aposentadoria> aposentadoriasInvalidas = new ArrayList<>();
        HashMap<String, String> valores = recuperarValores();
        for (Map.Entry<String, String> objeto : valores.entrySet()) {
            Query q = em.createQuery("select apo from Aposentadoria apo where apo.matriculaFP.matricula = :matricula and apo.numero = :numero ");
            q.setParameter("matricula", objeto.getKey());
            q.setParameter("numero", objeto.getValue());
            aposentadoriasInvalidas.addAll(q.getResultList());
        }
        return aposentadoriasInvalidas;
    }

    public HashMap<String, String> recuperarValores() {
        HashMap<String, String> valores = new LinkedHashMap<>();
        valores.put("3743", "3");//FRANCISCO NEVES DE SOUZA
        valores.put("2232", "2");//FRANCISCA COSTA DE LIMA
        valores.put("9040", "1");//CREUZUMIRA LUIZA SILVA DA CONCEIÇÃO
        valores.put("9040", "1");//CREUZUMIRA LUIZA SILVA DA CONCEIÇÃO
        return valores;
    }

    public List<Long> buscarAposentadoriasArquivoAtuarial(Date dataReferencia) {
        String hql = "  select distinct v.id from VinculoFP v, Aposentadoria a"
            + " inner join v.fichasFinanceiraFP ffp "
            + " inner join ffp.folhaDePagamento fp  "
            + "      where fp.mes = :mes            "
            + "        and fp.ano = :ano            "
            + "        and a.id = v.id              ";

        Query q = em.createQuery(hql);
        q.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(dataReferencia)));
        q.setParameter("ano", DataUtil.getAno(dataReferencia));
        return q.getResultList();
    }

    public List<Long> buscarAposentadosComRegistroObito(Date dataReferencia) {
        String sql = "  select distinct vinculo.id from vinculofp vinculo " +
            " inner join APOSENTADORIA aposentado on vinculo.id = aposentado.id " +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.id " +
            " inner join pessoafisica pf on mat.PESSOA_ID =pf.id " +
            " inner join REGISTRODEOBITO obito on obito.PESSOAFISICA_ID = pf.id " +
            " where extract(year from obito.DATAFALECIMENTO) = :ano " +
            " and extract (month from obito.DATAFALECIMENTO) = :mes";
        Query q = em.createNativeQuery(sql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("mes", date.getMonthOfYear());
        q.setParameter("ano", date.getYear());
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }


    public MedicoFacade getMedicoFacade() {
        return medicoFacade;
    }

    public List<VinculoFP> buscarAposentadodosNomeadosPorPeriodoAndTipoReajusteAposentadoria(Date inicio, Date fim, TipoReajusteAposentadoria tipo) {
        Query q = em.createQuery("select apo from Aposentadoria  apo " +
            "   where apo.tipoReajusteAposentadoria = :tipo and apo.inicioVigencia between :inicio and :fim ");
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<VinculoFP> buscarAposentadodosNomeadosAtePorTipoReajusteAposentadoria(Date inicio, Date refe, TipoReajusteAposentadoria tipo) {
        String hql = "select apo from Aposentadoria  apo " +
            "   where apo.tipoReajusteAposentadoria = :tipo and to_date(to_char(apo.inicioVigencia,'MM/yyyy'),'MM/yyyy') <= to_date(to_char(:inicio,'MM/yyyy'),'MM/yyyy') " +
            "   and to_date(to_char(:dataOperacao,'MM/yyyy'),'MM/yyyy') between to_date(to_char(apo.inicioVigencia,'MM/yyyy'),'MM/yyyy') and to_date(to_char(coalesce(apo.finalVigencia, :dataOperacao),'MM/yyyy'),'MM/yyyy') ";
        Query q = em.createQuery(hql);
        q.setParameter("inicio", inicio);
        q.setParameter("dataOperacao", refe);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public Collection<? extends ItemValorPrevidencia> buscarItemAposentadoriaPorReajuste(ReajusteMediaAposentadoria reajuste) {
        Query q = em.createQuery("select item from Aposentadoria apo join apo.itemAposentadorias item where item.reajusteRecebido = :reajuste");
        q.setParameter("reajuste", reajuste);
        return q.getResultList();
    }

    public List<Aposentadoria> buscarAposentadoriasPorReajuste(ReajusteMediaAposentadoria reajuste) {
        Query q = em.createQuery("select a from Aposentadoria a join a.itemAposentadorias item where item.reajusteRecebido = :reajuste");
        q.setParameter("reajuste", reajuste);
        return q.getResultList();
    }

    public List<ItemAposentadoria> buscarItensComReajusteDoExercicio(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        try {
            Query q = em.createQuery("select item from ItemAposentadoria item where item.reajusteRecebido in (select reajuste from ReajusteMediaAposentadoria reajuste where reajuste.exercicio = :ex  and reajuste.exercicioReferencia = :exReferencia)");
            q.setParameter("ex", exercicioAplicacao);
            q.setParameter("exReferencia", exercicioReferencia);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhum Item Aposentadoria encontrado com Reajuste em " + exercicioAplicacao.getAno());
        }
    }

    public List<ItemValorPrevidencia> buscarLancamentosVigentesAtivosENaoAutomaticos(VinculoFP vinculo, int mes, int ano) {
        Query q = em.createQuery("from ItemAposentadoria item "
            + " where item.aposentadoria = :vinculo and item.eventoFP.ativo = true and coalesce(item.eventoFP.automatico,false) = false "
            + " and (to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between "
            + " to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy') "
            + " and to_date(to_char(coalesce(item.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy')) ");
        q.setParameter("vinculo", vinculo);
        Date date = Util.criaDataPrimeiroDiaDoMesFP(Mes.getMesToInt(mes).ordinal(), ano);
        q.setParameter("dataParam", date);
        List<ItemValorPrevidencia> itensAposentadoria = q.getResultList();
        return itensAposentadoria;
    }

    public void enviarS2400ESocial(ConfiguracaoEmpregadorESocial empregador, Aposentadoria aposentadoria) throws ValidacaoException {
        s2400Service.enviarS2400(empregador, aposentadoria);
    }

    public void enviarS2405ESocial(ConfiguracaoEmpregadorESocial empregador, VinculoFP vinculofp) throws ValidacaoException {
        s2405Service.enviarS2405(empregador, vinculofp);
    }

    public void enviarS2410ESocial(ConfiguracaoEmpregadorESocial empregador, VinculoFP vinculofp) throws ValidacaoException {
        s2410Service.enviarS2410(empregador, vinculofp);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal buscarValorAposentadoriaPorAposentado(VinculoFP vinculoFP, Date data) {
        Query q = em.createQuery("select sum(item.valor) from ItemAposentadoria  item inner join item.aposentadoria apo " +
            "   where apo.id = :id and to_date(:data,'dd/mm/yyyy') between item.inicioVigencia and coalesce(item.finalVigencia,to_date(:data,'dd/mm/yyyy'))" +
            "   and item.eventoFP.tipoEventoFP = :vantagem ");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("id", vinculoFP.getId());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0);
    }

    public boolean existeAposentadoriaParaOContratoFP(ContratoFP contratoFP, Aposentadoria aposentadoria, Date dataAposentadoria){
            if(contratoFP == null || contratoFP.getId() == null){
                return false;
            }

            String sql = "select 1 from vinculofp vinculo " +
                " inner join aposentadoria ap on ap.id = vinculo.id " +
                " where ap.contratofp_id = :contratoID ";
                if(aposentadoria.getId() != null){
                    sql +=" and ap.id <> :aposentadoriaID";
                }
                sql += " and :dataAposentadoria <= coalesce(vinculo.finalvigencia, :dataAposentadoria) ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("contratoID", contratoFP.getId());
            if(aposentadoria.getId() != null){
                q.setParameter("aposentadoriaID", aposentadoria.getId());
            }

            q.setParameter("dataAposentadoria", dataAposentadoria);
            q.setMaxResults(1);
            return !q.getResultList().isEmpty();
        }

    public Date aposentadoriaPosterior(ContratoFP contratoFP, Aposentadoria aposentadoria) {

        String sql = "select trunc(vinculo.inicioVigencia) from vinculofp vinculo " +
            " inner join aposentadoria ap on ap.id = vinculo.id " +
            " where ap.contratofp_id = :contratoID " +
            " and vinculo.numero > :numeroVinculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoID", contratoFP.getId());
        q.setParameter("numeroVinculo", aposentadoria.getNumero());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Date) q.getResultList().get(0);
    }
}
