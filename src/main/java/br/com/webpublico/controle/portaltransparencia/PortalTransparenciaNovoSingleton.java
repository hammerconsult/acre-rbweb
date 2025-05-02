package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.portaltransparencia.entidades.EntidadePortalTransparencia;
import br.com.webpublico.controle.portaltransparencia.entidades.PrefeituraPortal;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LOA;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.json.JSONObject;

import javax.ejb.AccessTimeout;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by romanini on 25/08/15.
 */
@Singleton
@AccessTimeout(value = 50000)
public class PortalTransparenciaNovoSingleton implements Serializable {

    private static final Integer QUANTIDADE_LOG_OCORRENCIA = 20;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private List<String> resultado;
    private List<String> resultadoErros;
    private Boolean visualizarErros;
    private Boolean logDetalhado;
    private List<ObjetoPortalTransparencia> objetos;
    private Map<Object, JSONObject> mapa;
    private Map<Exercicio, LOA> loas;
    private PortalTransparenciaNovo selecionado;

    public void inicializar(PortalTransparenciaNovo selecionado) {
        this.selecionado = selecionado;
        this.selecionado.setCalculando(true);
        this.resultado = Lists.newArrayList();
        this.resultadoErros = Lists.newArrayList();
        this.objetos = Lists.newArrayList();
        this.mapa = new HashMap<>();
        this.loas = new HashMap<>();
        this.visualizarErros = Boolean.TRUE;
        this.logDetalhado = Boolean.FALSE;
    }

    public void definirUrl(SistemaFacade.PerfilApp perfilApp, PortalTransparenciaNovo selecionado) {
        this.selecionado = selecionado;
        List<PrefeituraPortal> prefeituraPortals = recuperarTodasPrefeitura();
        String url = PortalTransparenciaNovo.URL_PORTAL_HOMOLOGACAO;
        if (SistemaFacade.PerfilApp.PROD.equals(perfilApp)) {
            for (PrefeituraPortal prefeituraPortal : prefeituraPortals) {
                if (prefeituraPortal.getPrincipal() != null && prefeituraPortal.getPrincipal()) {
                    url = prefeituraPortal.getIp();
                }
            }
        }
        this.selecionado.setUrl(url);
    }

    public PortalTransparenciaNovo getSelecionado() {
        return selecionado;
    }

    public void addMensagem(String mensagem) {
        resultado.add(mensagem);
    }

    public void addMensagemErro(String mensagem) {
        resultadoErros.add(mensagem);
    }

    public JSONObject getJsonObject(Object objeto) {
        return mapa.get(objeto);
    }

    public Boolean contemObjeto(Object object) {
        if (mapa == null) {
            return false;
        }
        if (mapa.get(object) == null) {
            return false;
        }
        return true;
    }

    public void adicionarJsonObjet(Object objeto, JSONObject jsonObject) {
        mapa.put(objeto, jsonObject);
    }

    public void limpar() {
        resultado.clear();
        resultadoErros.clear();
    }

    public String getResultado() {
        try {
            if (selecionado == null) {
                return " ";
            }
            List<String> mensagens = visualizarErros ? resultadoErros : resultado;
            return gerarLogDoHistorico(mensagens);
        } catch (Exception e) {
            return "";
        }
    }

    public String gerarLogDoHistorico(List<String> mensagens) {
        String br = "</br>";
        if (selecionado == null) {
            return "";
        }
        if (selecionado.isCalculando()) {
            StringBuilder logFinal = new StringBuilder();
            int tamanhoTodo = QUANTIDADE_LOG_OCORRENCIA;
            if (mensagens.size() < QUANTIDADE_LOG_OCORRENCIA) {
                tamanhoTodo = mensagens.size();
            }
            try {
                List<String> logs = mensagens.subList(mensagens.size() - tamanhoTodo, mensagens.size());
                for (String log : logs) {
                    logFinal.append(log);
                    logFinal.append(br);
                }
                return logFinal.toString();
            } catch (ConcurrentModificationException e) {
                corrigeLog();
                return logFinal.toString();
            }
        } else {
            StringBuilder logFinal = new StringBuilder();
            try {
                for (String log : mensagens) {
                    logFinal.append(log);
                    logFinal.append(br);
                }
                return logFinal.toString();
            } catch (ConcurrentModificationException e) {
                corrigeLog();
                return logFinal.toString();
            }
        }
    }


    public void corrigeLog() {
        List<String> log = resultado;
        List<String> copia = new ArrayList<String>();
        copia.addAll(log);
        log = new ArrayList<String>();
        log.addAll(copia);
    }

    public List<ObjetoPortalTransparencia> getObjetos() {
        return objetos;
    }

    public Boolean getVisualizarErros() {
        return visualizarErros;
    }

    public void setVisualizarErros(Boolean visualizarErros) {
        this.visualizarErros = visualizarErros;
    }

    public Boolean getLogDetalhado() {
        return logDetalhado;
    }

    public void setLogDetalhado(Boolean logDetalhado) {
        this.logDetalhado = logDetalhado;
    }

    public Map<Exercicio, LOA> getLoas() {
        return loas;
    }

    public void setLoas(Map<Exercicio, LOA> loas) {
        this.loas = loas;
    }

    public List<PrefeituraPortal> recuperarTodasPrefeitura() {
        try {
            Query q = em.createNativeQuery("select p.* from PrefeituraPortal p", PrefeituraPortal.class);
            List<PrefeituraPortal> resultList = q.getResultList();
            for (PrefeituraPortal prefeituraPortal : resultList) {
                prefeituraPortal.getUnidades().size();
                prefeituraPortal.getUnidadesAdm().size();
                prefeituraPortal.getModulos().size();
            }
            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }


    public void salvarPrefeitura(PrefeituraPortal prefeituraPortal) {
        if (prefeituraPortal.getId() == null) {
            em.persist(prefeituraPortal);
        } else {
            prefeituraPortal = em.merge(prefeituraPortal);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarStatusEntidadeSalvar(EntidadePortalTransparencia entidadePortalTransparencia) {
        if (entidadePortalTransparencia.getId() != null) {
            String sql = "update EntidadePortalTransparencia set situacao = :situacao where id = :id ";
            Query consulta = em.createNativeQuery(sql);
            consulta.setParameter("id", entidadePortalTransparencia.getId());
            consulta.setParameter("situacao", PortalTransparenciaSituacao.PUBLICADO.name());

            consulta.executeUpdate();
        }
    }
}
