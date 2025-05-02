package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoCEDO;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.DetalheArquivoCEDO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.tributario.enumeration.MotivoDevolucaoArquivoCEDO;
import br.com.webpublico.util.ArquivoUtil;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ArquivoCEDOFacade extends AbstractFacade<ArquivoCEDO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoCEDOFacade() {
        super(ArquivoCEDO.class);
    }

    @Override
    public void preSave(ArquivoCEDO arquivoCEDO) {
        arquivoCEDO.realizarValidacoes();
        arquivoCEDO.gerarHashMd5();
        if (isArquivoJaImportado(arquivoCEDO)) {
            throw new ValidacaoException("O arquivo selecionado já foi importado.");
        }
    }

    private boolean isArquivoJaImportado(ArquivoCEDO arquivoCEDO) {
        if (arquivoCEDO == null) return false;
        Query query = em.createQuery("from ArquivoCEDO ace " +
            " where ace.hashMd5 = :hashMd5 " +
            (arquivoCEDO.getId() != null ? " and ace.id != :id " : ""));
        query.setParameter("hashMd5", arquivoCEDO.getHashMd5());
        if (arquivoCEDO.getId() != null) {
            query.setParameter("id", arquivoCEDO.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public ArquivoCEDO processarArquivo(AssistenteBarraProgresso assistenteBarraProgresso,
                                        ArquivoCEDO arquivoCEDO) {
        try {
            List<DetalheArquivoCEDO> detalhes = Lists.newArrayList();
            Arquivo arquivo = arquivoCEDO.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
            arquivo = cadastroImobiliarioFacade.getArquivoFacade().recuperaDependencias(arquivo.getId());
            arquivo.montarImputStream();
            InputStream inputStream = arquivo.getInputStream();
            List<String> linhasArquivo = IOUtils.readLines(inputStream);
            for (String linha : linhasArquivo) {
                if (linha != null && !linha.isEmpty()) {
                    String tipo = linha.substring(0, 1);
                    switch (tipo) {
                        case "1": {
                            atribuirCabecalho(arquivoCEDO, linha);
                            break;
                        }
                        case "2": {
                            atribuirDetalhe(detalhes, linha);
                            break;
                        }
                    }
                }
            }
            assistenteBarraProgresso.setDescricaoProcesso("Salvando dados do arquivo importado");
            assistenteBarraProgresso.setTotal(detalhes.size() + 1);
            assistenteBarraProgresso.conta();
            for (DetalheArquivoCEDO detalhe : detalhes) {
                detalhe.setArquivoCEDO(arquivoCEDO);
                salvarDetalheArquivoCEDO(detalhe);
                assistenteBarraProgresso.conta();
            }
            arquivoCEDO.setProcessado(true);
            arquivoCEDO = salvarArquivoCEDO(arquivoCEDO);
        } catch (Exception e) {
            logger.error("Erro ao importar o arquivo CEDO. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao importar o arquivo CEDO.", e);
        }
        return arquivoCEDO;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    private ArquivoCEDO salvarArquivoCEDO(ArquivoCEDO arquivoCEDO) {
        return em.merge(arquivoCEDO);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    private void salvarDetalheArquivoCEDO(DetalheArquivoCEDO detalheArquivoCEDO) {
        em.persist(detalheArquivoCEDO);
    }

    private void atribuirCabecalho(ArquivoCEDO arquivoCEDO, String linha) {
        String dataGeracao = linha.substring(1, 9);
        String horaGeracao = linha.substring(9, 13);
        arquivoCEDO.setDataGeracao(DateUtils.getData(dataGeracao + horaGeracao, "ddMMyyyyHHmm"));
    }

    public static void main(String[] args) {
        String identificacaoObjeto = "0100058734060611000001999730030907567891025479896554";
        String sequencial = identificacaoObjeto.substring(16, 22);
        String teste = identificacaoObjeto.substring(0, 34);
        System.out.println(teste);
        teste = identificacaoObjeto.substring(34, 36);
        System.out.println(teste);
    }

    private void atribuirDetalhe(List<DetalheArquivoCEDO> detalhes, String linha) {
        String identificacaoObjeto = linha.substring(1, 35);
        String codigoMotivoDevolucao = linha.substring(35, 37);
        String sequencial = identificacaoObjeto.substring(16, 22);
        String inscricao = identificacaoObjeto.substring(22, 34);
        DetalheArquivoCEDO detalheArquivoCEDO = new DetalheArquivoCEDO();
        detalheArquivoCEDO.setIdentificacaoObjeto(identificacaoObjeto);
        detalheArquivoCEDO.setSequencial(sequencial);
        detalheArquivoCEDO.setInscricao(inscricao);
        CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade
            .recuperaPorInscricao("100" + inscricao, false, false);
        detalheArquivoCEDO.setCadastroImobiliario(cadastroImobiliario);
        detalheArquivoCEDO.setMotivoDevolucao(MotivoDevolucaoArquivoCEDO.findByCodigo(codigoMotivoDevolucao));
        detalhes.add(detalheArquivoCEDO);
    }

    public Number contarDetalhes(ArquivoCEDO arquivoCEDO) {
        return (Number) em.createNativeQuery(" select count(1) " +
                "   from detalhearquivocedo d " +
                " where d.arquivocedo_id = :arquivoId ")
            .setParameter("arquivoId", arquivoCEDO.getId())
            .getSingleResult();
    }

    public List<DetalheArquivoCEDO> buscarDetalhes(ArquivoCEDO arquivoCEDO, int firstResult, int maxResults) {
        return em.createNativeQuery(" select d.* " +
                "   from detalhearquivocedo d " +
                " where d.arquivocedo_id = :arquivoId ", DetalheArquivoCEDO.class)
            .setParameter("arquivoId", arquivoCEDO.getId())
            .setFirstResult(firstResult)
            .setMaxResults(maxResults)
            .getResultList();
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    private void removerDetalheArquivoCEDO(DetalheArquivoCEDO detalheArquivoCEDO) {
        em.remove(detalheArquivoCEDO);
    }

    public List<DetalheArquivoCEDO> buscarDetalhes(ArquivoCEDO arquivoCEDO) {
        return em.createQuery("from DetalheArquivoCEDO d where d.arquivoCEDO = :arquivo ")
            .setParameter("arquivo", arquivoCEDO)
            .getResultList();
    }

    @Override
    public void remover(ArquivoCEDO entity) {
        List<DetalheArquivoCEDO> detalheArquivoCEDOS = buscarDetalhes(entity);
        for (DetalheArquivoCEDO detalhe : detalheArquivoCEDOS) {
            removerDetalheArquivoCEDO(detalhe);
        }
        super.remover(entity);
    }
}
