package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.redis.RedisIndice;
import br.com.webpublico.entidadesauxiliares.redis.RedisSistema;
import br.com.webpublico.seguranca.NotificacaoService;
//import br.com.webpublico.seguranca.RedisService;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import redis.clients.jedis.Jedis;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Criado por Romanini
 * Data: 16/11/2022.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "monitoramento-redis", pattern = "/monitoramento/redis/", viewId = "/faces/redis/monitoramento.xhtml")
})
public class MonitoramentoRedisControlador implements Serializable {

//    private RedisService redisService;
    private NotificacaoService notificacaoService;
    private RedisSistema redisSistema;

    private RedisIndice redisIndice;
    private Jedis jedis;

    @URLAction(mappingId = "monitoramento-redis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
//        redisService = RedisService.getService();
        notificacaoService = NotificacaoService.getService();
        redisSistema = new RedisSistema();
        buscarIndeces();
        buscarInformacao();
//        jedis = redisService.getJedis();
    }

    public void buscarInformacao() {
//        redisService.buscarInformacao(redisSistema);
    }

    public void buscarIndeces() {
        try {
//            redisSistema.setIndices(redisService.getTodosIndices());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar indices.", e.getMessage());
        }
    }

    public void reconectar() {
//        RedisService.getService().atribuirNullJedis();
//        jedis = RedisService.getService().getJedis();
    }

    public void recriarIndicesNotificacao() {
        try {
//            notificacaoService.carregarTodasNotificacoes();
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar indices.", e.getMessage());
        }
    }

    public void apagarTodosIndice() {
        if (getRedisSistema() != null && getRedisSistema().getIndices() != null) {
            for (RedisIndice indice : getRedisSistema().getIndices()) {
//                redisService.apagarIndice(indice);
            }
            redisSistema.getIndices().clear();
        }
    }

    public void apagarIndice(RedisIndice indice) {
        try {
//            redisService.apagarIndice(indice);
            redisSistema.getIndices().remove(indice);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao apagar indice.", e.getMessage());
        }
    }

    public void informacaoIndice(RedisIndice indice) {
        try {
            redisIndice = indice;
//            redisService.buscarInformacaoIndice(indice);
            FacesUtil.atualizarComponente("formDlgExecucao");
            FacesUtil.executaJavaScript("$('#modalDetalhesIndice').modal('show');");
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar informação do indice.", e.getMessage());
        }
    }

    public RedisSistema getRedisSistema() {
        return redisSistema;
    }

    public void setRedisSistema(RedisSistema redisSistema) {
        this.redisSistema = redisSistema;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public RedisIndice getRedisIndice() {
        return redisIndice;
    }

    public void setRedisIndice(RedisIndice redisIndice) {
        this.redisIndice = redisIndice;
    }
}
