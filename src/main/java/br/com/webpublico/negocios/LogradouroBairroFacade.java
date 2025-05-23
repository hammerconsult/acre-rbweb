package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoLogradouro;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class LogradouroBairroFacade {
    private static final Logger logger = LoggerFactory.getLogger(LogradouroBairroFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;

    public Logradouro buscarLogradouroPeloNome(String nome, String tipoLogradouro) {
        try {
            String sql = " select l.* from logradouro l " +
                " left join tipologradouro tl on l.tipologradouro_id = tl.id " +
                " where (upper(l.nome)) = :logr " +
                " and l.situacao <> :inativo " +
                " and (tl.id is null or (upper(tl.descricao)) = :tipoLogr)";

            Query q = em.createNativeQuery(sql, Logradouro.class);
            q.setParameter("logr", nome.trim().toUpperCase());
            q.setParameter("inativo", SituacaoLogradouro.INATIVO.name());
            q.setParameter("tipoLogr", tipoLogradouro.trim().toUpperCase());

            List<Logradouro> logradouros = q.getResultList();
            return (logradouros != null && !logradouros.isEmpty()) ? logradouros.get(0) : null;
        } catch (Exception e) {
            logger.error("Erro ao buscar logradouro. ", e);
        }
        return null;
    }

    public Bairro buscarBairroPelaDescricao(String descricao) {
        try {
            String sql = " select b.* from bairro b " +
                " where upper(b.descricao) = :descricao ";

            Query q = em.createNativeQuery(sql, Bairro.class);
            q.setParameter("descricao", descricao.trim().toUpperCase());

            List<Bairro> bairros = q.getResultList();
            return (bairros != null && !bairros.isEmpty()) ? bairros.get(0) : null;
        } catch (Exception e) {
            logger.error("Erro ao buscar bairro. ", e);
        }
        return null;
    }

    public LogradouroBairro buscarLogradouroBairro(EnderecoCorreio enderecoCorreio) {
        try {
            CEPLogradouro cepLogradouro = buscarCepLogradouro((EnderecoCorreio) Util.clonarObjeto(enderecoCorreio));
            Logradouro logradouro = buscarLogradouroPeloNome(enderecoCorreio.getLogradouro(), cepLogradouro.getTipo());
            Bairro bairro = buscarBairroPelaDescricao(enderecoCorreio.getBairro());

            if (logradouro != null && bairro != null) {
                String sql = " select lb.* from logradourobairro lb " +
                    " where lb.cep = :cep " +
                    " and lb.logradouro_id = :logradouro " +
                    " and lb.bairro_id = :bairro ";

                Query q = em.createNativeQuery(sql, LogradouroBairro.class);
                q.setParameter("cep", enderecoCorreio.getCep());
                q.setParameter("logradouro", logradouro.getId());
                q.setParameter("bairro", bairro.getId());

                List<LogradouroBairro> logrBairros = q.getResultList();
                return (logrBairros != null && !logrBairros.isEmpty()) ? logrBairros.get(0) : null;
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar logradouro e bairro. ", e);
        }
        return null;
    }

    public LogradouroBairro criarLogradouroBairro(EnderecoCorreio enderecoCorreio) {
        try {
            CEPLogradouro cepLogradouro = buscarCepLogradouro((EnderecoCorreio) Util.clonarObjeto(enderecoCorreio));
            Logradouro logradouro = buscarLogradouroPeloNome(enderecoCorreio.getLogradouro(), cepLogradouro.getTipo());
            Bairro bairro = buscarBairroPelaDescricao(enderecoCorreio.getBairro());

            LogradouroBairro logradouroBairro = new LogradouroBairro();
            logradouroBairro.setCep(enderecoCorreio.getCep());
            logradouroBairro.setLogradouro(logradouro != null ? logradouro : criarLogradouro(enderecoCorreio));
            logradouroBairro.setBairro(bairro != null ? bairro : criarBairro(enderecoCorreio.getBairro()));

            return em.merge(logradouroBairro);
        } catch (Exception e) {
            logger.error("Erro ao criar logradouro e bairro. ", e);
        }
        return null;
    }

    public Logradouro criarLogradouro(EnderecoCorreio enderecoCorreio) {
        try {
            CEPLogradouro cepLogradouro = buscarCepLogradouro((EnderecoCorreio) Util.clonarObjeto(enderecoCorreio));
            if (cepLogradouro != null) {
                Logradouro logradouro = new Logradouro();
                logradouro.setTipoLogradouro(buscarTipoLogradouroPelaDescricao(cepLogradouro.getTipo()));
                logradouro.setNome(definirNomeLogradouro(logradouro.getTipoLogradouro().getDescricao(), enderecoCorreio.getLogradouro()));
                logradouro.setNomeUsual(montarNomeUsual(logradouro));
                logradouro.setCodigo(logradouroFacade.proximoCodigo());
                logradouro.setSituacao(SituacaoLogradouro.ATIVO);

                return em.merge(logradouro);
            }
        } catch (Exception e) {
            logger.error("Erro ao criar logradouro. ", e);
        }
        return null;
    }

    private String definirNomeLogradouro(String tipo, String nome) {
        return nome.replace(tipo, "").trim();
    }

    private String montarNomeUsual(Logradouro logradouro) {
        String nomeUsual = "";
        if (logradouro.getTipoLogradouro() != null) {
            nomeUsual += logradouro.getTipoLogradouro().getDescricao() + " ";
        }
        nomeUsual += logradouro.getNome();
        return nomeUsual;
    }

    public Bairro criarBairro(String desricaoBairro) {
        try {
            Bairro bairro = new Bairro();
            bairro.setDescricao(desricaoBairro.trim().toUpperCase());
            bairro.setCodigo(bairroFacade.ultimoNumeroMaisUm());
            bairro.setAtivo(true);

            return em.merge(bairro);
        } catch (Exception e) {
            logger.error("Erro ao criar bairro. ", e);
        }
        return null;
    }

    private CEPLogradouro buscarCepLogradouro(EnderecoCorreio enderecoCorreio) {
        CEPLogradouro cepLogradouro;
        cepLogradouro = buscarCepLogradourosPorCep(enderecoCorreio);
        if (cepLogradouro == null) {
            atualizarEndereco(enderecoCorreio);
        }
        cepLogradouro = buscarCepLogradourosPorCep(enderecoCorreio, cepLogradouro);
        return cepLogradouro;
    }

    private CEPLogradouro buscarCepLogradourosPorCep(EnderecoCorreio enderecoCorreio) {
        return buscarCepLogradourosPorCep(enderecoCorreio, null);
    }

    private CEPLogradouro buscarCepLogradourosPorCep(EnderecoCorreio enderecoCorreio, CEPLogradouro cepLogradouro) {
        List<CEPLogradouro> cepLogradouros = consultaCepFacade.consultaLogradouroPorCEP(enderecoCorreio.getCep());
        if (cepLogradouros != null && !cepLogradouros.isEmpty()) {
            cepLogradouro = cepLogradouros.get(0);
        }
        return cepLogradouro;
    }

    private TipoLogradouro buscarTipoLogradouroPelaDescricao(String descricao) {
        try {
            String sql = " select tipo.* from tipologradouro tipo" +
                " where upper(tipo.descricao) = :descricao ";

            Query q = em.createNativeQuery(sql, TipoLogradouro.class);
            q.setParameter("descricao", descricao.trim().toUpperCase());

            List<TipoLogradouro> tipos = q.getResultList();
            return (tipos != null && !tipos.isEmpty()) ? tipos.get(0) : null;
        } catch (Exception e) {
            logger.error("Erro ao buscar tipo logradouro. ", e);
        }
        return null;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void atualizarEndereco(EnderecoCorreio enderecoCorreio) {
        consultaCepFacade.atualizarLogradouros(enderecoCorreio);
    }
}
