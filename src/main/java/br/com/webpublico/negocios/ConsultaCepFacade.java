/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.model.ConsultaCEP;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author munif
 */
@Stateless
public class ConsultaCepFacade {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaCepFacade.class);
    private final String urlApiViaCep = "https://viacep.com.br/ws/";
    private final String tipoRetornoViaCep = "/json";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CEPLocalidadeFacade cepLocalidadeFacade;
    @EJB
    private CEPBairroFacade cepBairroFacade;
    @EJB
    private CEPLogradouroFacade cepLogradouroFacade;

    public List<CEPLogradouro> consultaLogradouroPorCEP(String cep) {
        Query q = em.createQuery("FROM CEPLogradouro cl where cl.cep=:cep");
        q.setParameter("cep", cep);
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<CEPLogradouro> consultaLogradouroPorParteCEP(String parte) {
        Query q = em.createQuery("FROM CEPLogradouro cl where cl.cep like :cep");
        q.setParameter("cep", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> consultaLogradouroPorParteCEPByString(String parte) {
        Query q = em.createQuery("select cl.cep FROM CEPLogradouro cl where cl.cep like :cep");
        q.setParameter("cep", parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CEPUF> consultaUf(String parte) {
        Query q = em.createQuery("FROM CEPUF cepuf where lower(cepuf.nome) like :parte");
        q.setParameter("parte", parte.toLowerCase() + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<CEPLocalidade> consultaLocalidades(CEPUF uf, String parte) {
        Query q = em.createQuery("FROM CEPLocalidade localidade where localidade.uf=:uf AND localidade.nome like :parte");
        q.setParameter("uf", uf);
        q.setParameter("parte", parte + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<CEPLocalidade> consultaLocalidades(String uf, String parte) {
        Query q = em.createQuery("select localidade FROM CEPLocalidade localidade, CEPUF uf where lower(uf.nome) like :uf and localidade.uf = uf AND lower(localidade.nome) like :parte");
        q.setParameter("uf", "%" + uf.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CEPLogradouro> consultaLogradouros(CEPLocalidade localidade, String parte) {
        Query q = em.createQuery("FROM CEPLogradouro logradouro where "
            + " logradouro.localidade=:localidade AND logradouro.nome like :parte");
        q.setParameter("localidade", localidade);
        q.setParameter("parte", "%" + parte + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<CEPLogradouro> consultaLogradouros(String localidade, String parte) {
        Query q = em.createQuery("select logradouro FROM CEPLogradouro logradouro "
            + " where lower(logradouro.localidade.nome) "
            + " like :localidade and lower(logradouro.nome) like :parte");
        q.setParameter("localidade", "%" + localidade.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CEPLogradouro> consultaCEPs(String logradouro, String cidade, String parte) {
        Query q = em.createQuery("select cep FROM CEPLogradouro logradouro "
            + " where lower(logradouro.nome) like :logra " +
            "   and lower(logradouro.cep) like :parte " +
            "   and lower(logradouro.localidade.nome) like :cid");
        q.setParameter("logra", "%" + logradouro.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("cid", "%" + cidade.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CEPBairro> consultaBairros(String localidade, String parte) {
        Query q = em.createQuery("select bairro FROM CEPBairro bairro, CEPLocalidade localidade where lower(localidade.nome) like :localidade and lower(bairro.nome) like :parte");
        q.setParameter("localidade", "%" + localidade.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CEPLocalidade> consultaLocalidades(String parte) {
        Query q = em.createQuery("FROM CEPLocalidade localidade where localidade.nome like :parte");
        q.setParameter("parte", parte + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<CEPLogradouro> consultaLogradouros(String parte) {
        Query q = em.createQuery("FROM CEPLogradouro logradouro where logradouro.nome like :parte");
        q.setParameter("parte", "%" + parte + "%");
        q.setMaxResults(100);
        return q.getResultList();
    }

    public CEPUF recuperaCEPUF(Long chave) {
        return em.find(CEPUF.class, chave);
    }

    public CEPLocalidade recuperaCEPLocalidade(Long chave) {
        return em.find(CEPLocalidade.class, chave);
    }

    public CEPLogradouro recuperaCEPLogradouro(Long chave) {
        return em.find(CEPLogradouro.class, chave);
    }

    public List<String> listaCEPString(String parte) {
        Query q = em.createQuery("select distinct replace(replace(replace(log.cep,'.',''),'-',''),'/','') from CEPLogradouro log where log.cep like :parte");
        q.setParameter("parte", "%" + parte + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> listaUfString() {
        Query q = em.createQuery("select distinct cepuf.nome FROM CEPUF cepuf ");
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<CEPUF> listaUf() {
        Query q = em.createQuery("select cepuf FROM CEPUF cepuf order by cepuf.nome");
        q.setMaxResults(30);
        return q.getResultList();
    }

    public String siglaDoEstado(String estado) {
        Query q = em.createQuery("select distinct cepuf.sigla FROM CEPUF cepuf where cepuf.nome = :nome");
        q.setParameter("nome", estado);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return null;
    }

    public List<String> listaLocalidadesString(String uf, String parte) {
        Query q = em.createQuery("select distinct localidade.nome FROM CEPLocalidade localidade, CEPUF uf where lower(uf.nome) like :uf and localidade.uf = uf AND lower(localidade.nome) like :parte");
        q.setParameter("uf", "%" + uf.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public String consultaLocalidadePorCep(String cep) {
        Query q = em.createQuery("select distinct localidade.nome FROM CEPLocalidade localidade where localidade.cep = :cep");
        q.setParameter("cep", cep);
        q.setMaxResults(1);
        return q.getResultList().isEmpty() ? null : (String) q.getResultList().get(0);
    }

    public List<String> listaBairrosString(String localidade, String parte) {
        Query q = em.createQuery("select distinct bairro.nome FROM CEPBairro bairro, CEPLocalidade localidade where lower(localidade.nome) like :localidade and lower(bairro.nome) like :parte");
        q.setParameter("localidade", "%" + localidade.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> listaLogradourosString(String localidade, String parte) {
        Query q = em.createQuery("select distinct logradouro.tipo || ' ' || logradouro.nome "
            + " FROM CEPLogradouro logradouro, CEPLocalidade localidade " +
            " where (lower(localidade.nome) like :localidade " +
            " and localidade = logradouro.localidade)"
            + " and (lower(logradouro.nome) like :parte or lower(logradouro.tipo || ' ' || logradouro.nome) like :parte)");
        q.setParameter("localidade", "%" + localidade.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public ConsultaCEP buscarCEP(String cep) {
        try {
            if (!Strings.isNullOrEmpty(cep.trim())) {
                RestTemplate rt = new RestTemplate();
                String url = urlApiViaCep + cep + tipoRetornoViaCep;
                ResponseEntity<ConsultaCEP> reConsultaCep = rt.exchange(url, HttpMethod.GET, null, ConsultaCEP.class);

                return reConsultaCep.getBody();
            }
        } catch (Exception ex) {
            FacesUtil.addError("Atenção", "O cep " + cep +
                " é inválido ou inexistente.");
        }
        return null;
    }

    public void atualizarLogradouros(EnderecoCorreio endereco) {
        try {
            try {
                validarCep(endereco.getCep() != null ? endereco.getCep().trim() : "");
                String cep = StringUtil.removeCaracteresEspeciaisSemEspaco(endereco.getCep());
                List<CEPLogradouro> logradouros = preencherLogradouros(cep);
                if (logradouros != null && !logradouros.isEmpty()) {
                    CEPLogradouro logradouro = logradouros.get(0);
                    if (!Strings.isNullOrEmpty(logradouro.getCodigoIBGE())) {
                        atribuirEndereco(logradouro, endereco);
                    } else {
                        atribuirCodigoIbgeNoEndereco(cep, logradouro);
                        atribuirEndereco(logradouro, endereco);
                    }
                } else {
                    CEPLogradouro novoCEPLogradouro = criarNovoCEPLogradouro(cep);
                    if (novoCEPLogradouro != null) {
                        atribuirEndereco(novoCEPLogradouro, endereco);
                    }
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } catch (Exception ex) {
            logger.error("Erro ao Confirmar: ", ex);
        }
    }

    private void validarCep(String cep) {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(cep)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um CEP para pesquisar.");
        }
        if (!Strings.isNullOrEmpty(cep)) {
            String regex = "\\d{5}+[-]?\\d{3}[0-9]*";
            if (!cep.matches(regex)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CEP informado é inválido");
            }
        }
        ve.lancarException();
    }

    private List<CEPLogradouro> preencherLogradouros(String cep) {
        return consultaLogradouroPorCEP(cep);
    }

    private void atribuirEndereco(CEPLogradouro logradouro, EnderecoCorreio endereco) {
        if (logradouro != null) {
            endereco.setBairro(logradouro.getBairro() != null ? logradouro.getBairro().getNome() : "");
            endereco.setCep(logradouro.getCep() != null ? logradouro.getCep() : "");
            endereco.setLogradouro((logradouro.getTipo() != null ? logradouro.getTipo() + " " : "")
                + (logradouro.getNome() != null ? logradouro.getNome().trim() : ""));
            endereco.setUf(logradouro.getLocalidade() != null ? logradouro.getLocalidade().getCepuf().getSigla() : "");
            endereco.setLocalidade(logradouro.getLocalidade() != null ? logradouro.getLocalidade().getNome() : "");
            endereco.setComplemento(logradouro.getComplemento() != null ? logradouro.getComplemento() : "");
        }
    }

    private void atribuirCodigoIbgeNoEndereco(String cep, CEPLogradouro logradouro) {
        if (logradouro != null && Strings.isNullOrEmpty(logradouro.getCodigoIBGE())) {
            ConsultaCEP cepConsulta = buscarCEP(cep);
            if (cepConsulta != null) {
                logradouro.setCodigoIBGE(!Strings.isNullOrEmpty(cepConsulta.getIbge()) ? cepConsulta.getIbge() : "");
            }
            cepLogradouroFacade.salvarCEPLogradouro(logradouro);
        }
    }

    private CEPLogradouro criarNovoCEPLogradouro(String cep) {
        ConsultaCEP consultaCEP = buscarCEP(cep);
        if (consultaCEP != null) {
            CEPLogradouro cepLogradouro = new CEPLogradouro();
            cepLogradouro.setCep(cep);
            definirTipoLogradouro(consultaCEP, cepLogradouro);
            definirNomeLogradouro(cepLogradouro.getTipo(), cepLogradouro, consultaCEP);
            cepLogradouro.setComplemento(consultaCEP.getComplemento());
            cepLogradouro.setCodigoIBGE(consultaCEP.getIbge());

            CEPLocalidade cepLocalidade = atribuirCEPLocalidade(consultaCEP, cepLogradouro);
            atribuirCEPBairro(consultaCEP, cepLogradouro, cepLocalidade);

            return cepLogradouroFacade.salvarCEPLogradouro(cepLogradouro);
        }
        return null;
    }

    private void definirTipoLogradouro(ConsultaCEP consultaCEP, CEPLogradouro cepLogradouro) {
        String tipoLogradouro = "";
        List tipos = tipoLogradouros();
        if (tipos != null && !tipos.isEmpty()) {
            for (Object logradouro : tipos) {
                if (logradouro != null &&
                    consultaCEP.getLogradouro().toLowerCase().trim().startsWith(logradouro.toString().toLowerCase().trim())) {
                    tipoLogradouro = logradouro.toString().trim();
                }
            }
        }
        cepLogradouro.setTipo(tipoLogradouro);
    }

    private void definirNomeLogradouro(String tipo, CEPLogradouro cepLogradouro, ConsultaCEP consultaCEP) {
        cepLogradouro.setNome(consultaCEP.getLogradouro().replace(tipo, "").trim());
    }

    private void atribuirCEPBairro(ConsultaCEP consultaCEP, CEPLogradouro cepLogradouro, CEPLocalidade cepLocalidade) {
        CEPBairro cepBairro = cepBairroFacade.buscarCEPLocalidadePeloPorLocalidadeAndUF(consultaCEP.getBairro(), cepLocalidade);
        if (cepBairro == null) {
            cepBairro = criarCEPBairro(consultaCEP, cepLocalidade);
            cepLogradouro.setBairro(cepBairro);
        } else {
            cepLogradouro.setBairro(cepBairro);
        }
    }

    private CEPLocalidade atribuirCEPLocalidade(ConsultaCEP consultaCEP, CEPLogradouro cepLogradouro) {
        CEPLocalidade cepLocalidade = cepLocalidadeFacade.buscarCEPLocalidadePeloPorLocalidadeAndUF(consultaCEP);
        if (cepLocalidade == null) {
            cepLocalidade = criarCepLocalidade(consultaCEP);
            cepLogradouro.setLocalidade(cepLocalidade);
        } else {
            cepLogradouro.setLocalidade(cepLocalidade);
        }
        return cepLocalidade;
    }

    private CEPLocalidade criarCepLocalidade(ConsultaCEP consultaCEP) {
        CEPLocalidade cepLocalidade = new CEPLocalidade();
        cepLocalidade.setNome(consultaCEP.getLogradouro());
        cepLocalidade.setTipo("M");
        cepLocalidade.setCepuf(buscarCEPUFPeloUF(consultaCEP.getUf()));
        cepLocalidade.setCep(consultaCEP.getCep());
        cepLocalidade.setNomeSimples(consultaCEP.getLogradouro());

        cepLocalidade = cepLocalidadeFacade.salvarCEPLocalidade(cepLocalidade);

        return cepLocalidade;
    }

    private CEPBairro criarCEPBairro(ConsultaCEP consultaCEP, CEPLocalidade cepLocalidade) {
        CEPBairro cepBairro = new CEPBairro();
        cepBairro.setNome(consultaCEP.getBairro());
        cepBairro.setLocalidade(cepLocalidade);

        cepBairro = cepBairroFacade.salvarCEPBairro(cepBairro);

        return cepBairro;
    }

    private CEPUF buscarCEPUFPeloUF(String uf) {
        String sql = " select cepuf.* from cepuf " +
            " where lower(sigla) = :uf ";

        Query q = em.createNativeQuery(sql, CEPUF.class);
        q.setParameter("uf", uf.toLowerCase().trim());

        if (!q.getResultList().isEmpty()) {
            return (CEPUF) q.getResultList().get(0);
        }
        return null;
    }

    private List tipoLogradouros() {
        String sql = " select tipo from ceplogradouro " +
            " group by tipo order by (tipo) asc ";

        Query q = em.createNativeQuery(sql);

        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }
}
