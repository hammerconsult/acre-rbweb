package br.com.webpublico.negocios.contabil.execucao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubConta;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaDestino;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaOrigem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Stateless
public class ConfiguracaoTransporteSaldoSubContaFacade extends AbstractFacade<ConfiguracaoTransporteSaldoSubConta> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ConfiguracaoTransporteSaldoSubContaFacade() {
        super(ConfiguracaoTransporteSaldoSubConta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoTransporteSaldoSubConta recuperar(Object id) {
        ConfiguracaoTransporteSaldoSubConta entity = super.recuperar(id);
        Hibernate.initialize(entity.getOrigens());
        for (ConfiguracaoTransporteSaldoSubContaOrigem origem : entity.getOrigens()) {
            Hibernate.initialize(origem.getDestinos());
        }
        return entity;
    }

    @Override
    public void preSave(ConfiguracaoTransporteSaldoSubConta entidade) {
        ValidacaoException ve = new ValidacaoException();
        if (hasConfiguracaoComMesmoExercicioDestino(entidade)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração com o exercício de destino em " + entidade.getExercicioDestino().getAno() + ".");
        }
        ve.lancarException();
    }

    @Override
    public void salvarNovo(ConfiguracaoTransporteSaldoSubConta entity) {
        atualizarVinculosDaSubConta(entity);
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(ConfiguracaoTransporteSaldoSubConta entity) {
        atualizarVinculosDaSubConta(entity);
        super.salvar(entity);
    }

    private void atualizarVinculosDaSubConta(ConfiguracaoTransporteSaldoSubConta entity) {
        HashMap<Long, SubConta> mapSubContasRecuperadasEAtualizadas = new HashMap<>();
        for (ConfiguracaoTransporteSaldoSubContaOrigem origem : entity.getOrigens()) {
            for (ConfiguracaoTransporteSaldoSubContaDestino destino : origem.getDestinos()) {
                SubConta subConta = destino.getSubConta();
                if (!mapSubContasRecuperadasEAtualizadas.containsKey(subConta.getId())) {
                    mapSubContasRecuperadasEAtualizadas.put(subConta.getId(), subContaFacade.recuperar(subConta.getId()));
                }
                subConta = atualizarVinculosSubConta(destino, mapSubContasRecuperadasEAtualizadas.get(subConta.getId()));
                mapSubContasRecuperadasEAtualizadas.put(subConta.getId(), subConta);
                destino.setSubConta(subConta);
                if (origem.getSubConta().getId().equals(subConta.getId())) {
                    origem.setSubConta(subConta);
                }
            }
        }
    }

    public SubConta atualizarVinculosSubConta(ConfiguracaoTransporteSaldoSubContaDestino destino, SubConta subConta) {
        Exercicio exercicio = destino.getConfiguracaoOrigem().getConfiguracao().getExercicioDestino();
        if (destino.getUnidadeOrganizacional() != null && !hasUnidadeNoExercicioDestinoParaSubConta(subConta, destino.getUnidadeOrganizacional(), exercicio)) {
            subConta.getUnidadesOrganizacionais().add(new SubContaUniOrg(destino.getUnidadeOrganizacional(), subConta, exercicio));
        }
        if (destino.getContaDeDestinacao() != null && !hasContaDeDestinacaoParaSubConta(subConta, destino.getContaDeDestinacao())) {
            subConta.getSubContaFonteRecs().add(new SubContaFonteRec(destino.getContaDeDestinacao().getFonteDeRecursos(), destino.getContaDeDestinacao(), subConta));
        }
        return subConta;
    }

    private boolean hasUnidadeNoExercicioDestinoParaSubConta(SubConta subConta, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        return subConta.getUnidadesOrganizacionais().stream().anyMatch(scUni -> (scUni.getUnidadeOrganizacional().equals(unidadeOrganizacional) && scUni.getExercicio().equals(exercicio)));
    }

    private boolean hasContaDeDestinacaoParaSubConta(SubConta subConta, ContaDeDestinacao contaDeDestinacao) {
        return subConta.getSubContaFonteRecs().stream().anyMatch(scFr -> scFr.getContaDeDestinacao().equals(contaDeDestinacao));
    }

    private boolean hasConfiguracaoComMesmoExercicioDestino(ConfiguracaoTransporteSaldoSubConta selecionado) {
        String sql = "select id from configtranspsaldosubc where exerciciodestino_id = :idExercicioDestino ";
        if (selecionado.getId() != null) {
            sql += " and id <> :idSelecionado ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicioDestino", selecionado.getExercicioDestino());
        if (selecionado.getId() != null) {
            q.setParameter("idSelecionado", selecionado.getId());
        }
        List<BigDecimal> resultado = q.getResultList();
        return !resultado.isEmpty();
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional uo) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), uo, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoDeRecursos(String filtro, Exercicio exercicio) {
        return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricao(filtro, exercicio);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte, Date dataReferencia) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte, sistemaFacade.getUsuarioCorrente(), dataReferencia, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<SubConta> buscarContasFinanceiras(String parte, Exercicio exercicio) {
        return subContaFacade.listaPorExercicio(parte, exercicio);
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Exercicio getExercicioPorAno(Integer ano) {
        return exercicioFacade.getExercicioPorAno(ano);
    }

    public void recuperarHierarquias(ConfiguracaoTransporteSaldoSubConta selecionado) {
        HashMap<Long, HierarquiaOrganizacional> mapHierarquiaPorUnidade = new HashMap<>();
        for (ConfiguracaoTransporteSaldoSubContaOrigem origem : selecionado.getOrigens()) {
            if (origem.getHierarquiaOrganizacional() == null && origem.getUnidadeOrganizacional() != null) {
                origem.setHierarquiaOrganizacional(getHierarquiaDaUnidade(origem.getUnidadeOrganizacional(), mapHierarquiaPorUnidade));

                for (ConfiguracaoTransporteSaldoSubContaDestino destino : origem.getDestinos()) {
                    if (destino.getHierarquiaOrganizacional() == null && destino.getUnidadeOrganizacional() != null) {
                        destino.setHierarquiaOrganizacional(getHierarquiaDaUnidade(destino.getUnidadeOrganizacional(), mapHierarquiaPorUnidade));
                    }
                }
            }
        }
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidade, HashMap<Long, HierarquiaOrganizacional> mapHierarquiaPorUnidade) {
        Long chave = unidade.getId();
        if (!mapHierarquiaPorUnidade.containsKey(chave)) {
            mapHierarquiaPorUnidade.put(chave, getHierarquiaDaUnidade(unidade));
        }
        return mapHierarquiaPorUnidade.get(chave);
    }

    public ConfiguracaoTransporteSaldoSubContaDestino criarNovoDestinoPadrao(ConfiguracaoTransporteSaldoSubContaOrigem origem, Exercicio exercicio, boolean isGrupo2) {
        String codigoContaDeDestinacao = isGrupo2 ? "2" + origem.getContaDeDestinacao().getCodigo().substring(1) : origem.getContaDeDestinacao().getCodigo();
        List<HierarquiaOrganizacional> hierarquias = origem.getHierarquiaOrganizacional() != null ? buscarHierarquiasOrganizacionais(origem.getHierarquiaOrganizacional().getCodigo(), DataUtil.montaData(1, 1, exercicio.getAno()).getTime()) : Lists.newArrayList();
        List<ContaDeDestinacao> contas = origem.getContaDeDestinacao() != null ? buscarContasDeDestinacaoDeRecursos(codigoContaDeDestinacao, exercicio) : Lists.newArrayList();

        ConfiguracaoTransporteSaldoSubContaDestino destino = new ConfiguracaoTransporteSaldoSubContaDestino();
        destino.setConfiguracaoOrigem(origem);
        destino.setSubConta(origem.getSubConta());
        if (hierarquias != null) {
            destino.setHierarquiaOrganizacional(hierarquias.get(0));
            destino.setUnidadeOrganizacional(hierarquias.get(0).getSubordinada());
        }
        destino.setContaDeDestinacao(!contas.isEmpty() ? contas.get(0) : null);
        destino.setValor(origem.getValor());
        return destino;
    }

    public void buscarOrigens(ConfiguracaoTransporteSaldoSubConta selecionado) {
        selecionado.setOrigens(Lists.newArrayList());
        String sql = " select sb.id as idSubConta, " +
            "       vw.id as idUnidade, " +
            "       cd.id as idContaDest, " +
            "       sum(coalesce(ssb.totalcredito - ssb.totaldebito, 0)) as soma, " +
            "       sb.codigo as subConta, " +
            "       vw.codigo as unidade, " +
            "       font.codigo as fonte " +
            " from saldosubconta ssb " +
            "         inner join subconta sb on ssb.subconta_id = sb.id " +
            "         inner join vwhierarquiaorcamentaria vw on ssb.unidadeorganizacional_id = vw.subordinada_id " +
            "         inner join fontederecursos font on ssb.fontederecursos_id = font.id and font.exercicio_id = :idExercicio " +
            "         inner join conta cd on ssb.contadedestinacao_id = cd.id and cd.exercicio_id = :idExercicio " +
            "         inner join (select trunc(max(datasaldo)) as datasaldo, " +
            "                            subconta_id, " +
            "                            fontederecursos_id, " +
            "                            contadedestinacao_id, " +
            "                            unidadeorganizacional_id " +
            "                     from saldosubconta " +
            "                     where trunc(datasaldo) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "                     group by subconta_id, fontederecursos_id, contadedestinacao_id, unidadeorganizacional_id) datasaldo " +
            "                    on datasaldo.subconta_id = ssb.subconta_id and trunc(ssb.datasaldo) = trunc(datasaldo.datasaldo) and " +
            "                       ssb.fontederecursos_id = datasaldo.fontederecursos_id and " +
            "                       ssb.contadedestinacao_id = datasaldo.contadedestinacao_id and " +
            "                       ssb.unidadeorganizacional_id = datasaldo.unidadeorganizacional_id " +
            " where to_date(:dataReferencia, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "   and coalesce(ssb.totalcredito - ssb.totaldebito, 0) <> 0 " +
            " group by sb.id, vw.id, cd.id, sb.codigo, vw.codigo, font.codigo " +
            " order by sb.codigo, vw.codigo, font.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", selecionado.getExercicioOrigem().getId());
        q.setParameter("dataReferencia", "31/12/" + selecionado.getExercicioOrigem().getAno());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SubConta subConta = em.find(SubConta.class, ((BigDecimal) obj[0]).longValue());
                HierarquiaOrganizacional hierarquia = em.find(HierarquiaOrganizacional.class, ((BigDecimal) obj[1]).longValue());
                ContaDeDestinacao contaDeDestinacao = em.find(ContaDeDestinacao.class, ((BigDecimal) obj[2]).longValue());
                ConfiguracaoTransporteSaldoSubContaOrigem origem = new ConfiguracaoTransporteSaldoSubContaOrigem();
                origem.setConfiguracao(selecionado);
                origem.setSubConta(subConta);
                origem.setUnidadeOrganizacional(hierarquia.getSubordinada());
                origem.setHierarquiaOrganizacional(hierarquia);
                origem.setContaDeDestinacao(contaDeDestinacao);
                origem.setValor((BigDecimal) obj[3]);
                origem.atualizarStyleText();
                selecionado.getOrigens().add(origem);
            }
        }
    }

    public List<ConfiguracaoTransporteSaldoSubContaDestino> buscarDestinosPorSubContaUnidadeContaDeDestinacaoEValorDaOrigem(Long idSubConta, Long idUnidade, Long idContaDeDestinacao, BigDecimal valor, Exercicio exercicio) {
        if (idSubConta == null || idUnidade == null || idContaDeDestinacao == null || valor == null || exercicio == null)
            return Lists.newArrayList();
        String sql = " select distinct destino.* " +
            " from configtranspsaldosubc config " +
            "         inner join configtranspsaldosubcorig origem on origem.configuracao_id = config.id " +
            "         inner join configtranspsaldosubcdest destino on destino.configuracaoorigem_id = origem.id " +
            " where config.exerciciodestino_id = :idExercicioDestino  " +
            "   and origem.subconta_id = :idSubConta " +
            "   and origem.unidadeorganizacional_id = :idUnidade " +
            "   and origem.contadedestinacao_id = :idContaDeDestinacao " +
            "   and origem.valor = :valorOrigem ";
        Query q = em.createNativeQuery(sql, ConfiguracaoTransporteSaldoSubContaDestino.class);
        q.setParameter("idExercicioDestino", exercicio.getId());
        q.setParameter("idSubConta", idSubConta);
        q.setParameter("idUnidade", idUnidade);
        q.setParameter("idContaDeDestinacao", idContaDeDestinacao);
        q.setParameter("valorOrigem", valor);
        return q.getResultList();
    }
}
