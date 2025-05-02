//package br.com.webpublico.seguranca;
//
//import br.com.webpublico.entidadesauxiliares.redis.RedisIndice;
//import br.com.webpublico.entidadesauxiliares.redis.RedisInformacao;
//import br.com.webpublico.entidadesauxiliares.redis.RedisSistema;
//import br.com.webpublico.exception.RedisException;
//import br.com.webpublico.util.Util;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
//import javax.enterprise.context.SessionScoped;
//import java.io.Serializable;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@Service
//@SessionScoped
//public class RedisService implements Serializable {
//
//    protected static final Logger logger = LoggerFactory.getLogger(RedisService.class);
//
//    public static String PREFIX_NOTIFICAO_USUARIO = "NOTIFICACAO-USUARIO-";
//    public static String PREFIX_NOTIFICAO_UNIDADE = "NOTIFICACAO-UNIDADE-";
//    private Jedis jedis;
//    private JedisPool jedisPool;
//
//    public static RedisService getService() {
//        return (RedisService) Util.getSpringBeanPeloNome("redisService");
//    }
//
//    public void add(String key, String value) throws RedisException {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        jedis.set(key, value);
//    }
//
//    private void validarRedis(Jedis jedis) throws RedisException {
//        if (jedis == null) {
//            new RedisException("Configuração do JEDIS não encontrado");
//        }
//    }
//
//    public String get(String key) {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        return jedis.get(key);
//    }
//
//    public void buscarInformacao(RedisSistema redisSistema) {
//        Jedis jedis = getJedis();
//        String info = jedis.info();
//        String[] split = info.split("\r\n");
//
//        redisSistema.setInformacoes(Lists.<RedisInformacao>newArrayList());
//        for (String s : split) {
//            redisSistema.getInformacoes().add(new RedisInformacao(s));
//        }
//    }
//
//    public void buscarInformacaoIndice(RedisIndice indice) {
//        Jedis jedis = getJedis();
//        Long tamanho = jedis.llen(indice.getNome());
//        List<String> lrange = jedis.lrange(indice.getNome(), 0, tamanho);
//        if (lrange != null) {
//            indice.setQuantidade(lrange.size());
//            indice.setNotificacoes(Lists.<NotificacaoService.NotificacaoDTO>newArrayList());
//            for (String s : lrange) {
//                try {
//                    NotificacaoService.NotificacaoDTO n = new ObjectMapper().readValue(s, NotificacaoService.NotificacaoDTO.class);
//                    indice.getNotificacoes().add(n);
//                } catch (Exception e) {
//                    logger.error("Erro ao buscar notificação do indice  " + indice.getNome() + ". Erro: {} ", e);
//                }
//            }
//
//        }
//    }
//
//    public List<RedisIndice> getTodosIndices() {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        Set<String> keys = jedis.keys("**");
//        List<RedisIndice> retorno = Lists.newArrayList();
//        if (keys != null) {
//            for (String key : keys) {
//                RedisIndice indice = new RedisIndice(key);
//                retorno.add(indice);
//            }
//        }
//        return retorno;
//    }
//
//    public void apagarIndice(RedisIndice indice) {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        jedis.del(indice.getNome());
//    }
//
//    public List<NotificacaoService.NotificacaoDTO> getTodasNotificacaoUnidade(Long unidadeId) {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        List<NotificacaoService.NotificacaoDTO> todasNotificacaoes = Lists.newArrayList();
//        if (jedis.isConnected()) {
//            Long tamanho = jedis.llen(PREFIX_NOTIFICAO_UNIDADE + unidadeId);
//            List<String> todasNotificacoesAsString = jedis.lrange(PREFIX_NOTIFICAO_UNIDADE + unidadeId, 0, tamanho);
//            try {
//                for (String notificacao : todasNotificacoesAsString) {
//                    NotificacaoService.NotificacaoDTO n = new ObjectMapper().readValue(notificacao, NotificacaoService.NotificacaoDTO.class);
//                    todasNotificacaoes.add(n);
//                }
//            } catch (Exception e) {
//                logger.error("Erro getTodasNotificacao da unidade " + unidadeId + ". Erro: {} ", e);
//            }
//        }
//        return todasNotificacaoes;
//    }
//
//    public List<NotificacaoService.NotificacaoDTO> getTodasNotificacaoUsuario(Long usuarioId) {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        List<NotificacaoService.NotificacaoDTO> todasNotificacaoes = Lists.newArrayList();
//        if (jedis.isConnected()) {
//            Long tamanho = jedis.llen(PREFIX_NOTIFICAO_USUARIO + usuarioId);
//            List<String> todasNotificacoesAsString = jedis.lrange(PREFIX_NOTIFICAO_USUARIO + usuarioId, 0, tamanho);
//            try {
//                for (String notificacao : todasNotificacoesAsString) {
//                    NotificacaoService.NotificacaoDTO n = new ObjectMapper().readValue(notificacao, NotificacaoService.NotificacaoDTO.class);
//                    todasNotificacaoes.add(n);
//                }
//            } catch (Exception e) {
//                logger.error("Erro getTodasNotificacao do usuário " + usuarioId + ". Erro: {} ", e);
//            }
//        }
//        return todasNotificacaoes;
//    }
//
//    public void removerNotificacao(List<NotificacaoService.NotificacaoDTO> todas) {
//        Jedis jedis = getJedis();
//        validarRedis(jedis);
//        for (NotificacaoService.NotificacaoDTO notificacao : todas) {
//            if (notificacao.getUsuarioId() != null) {
//                List<NotificacaoService.NotificacaoDTO> notificacoes = getTodasNotificacaoUsuario(notificacao.getUsuarioId());
//                if (notificacoes.contains(notificacao)) {
//                    notificacoes.remove(notificacao);
//                }
//                adicionarNotificacao(notificacoes, Boolean.TRUE);
//            }
//
//            if (notificacao.getUnidadeId() != null) {
//                List<NotificacaoService.NotificacaoDTO> notificacoes = getTodasNotificacaoUnidade(notificacao.getUnidadeId());
//                if (notificacoes.contains(notificacao)) {
//                    notificacoes.remove(notificacao);
//                }
//                adicionarNotificacao(notificacoes, Boolean.TRUE);
//            }
//        }
//
//
//    }
//
//    public void adicionarNotificacao(List<NotificacaoService.NotificacaoDTO> todas, Boolean limpar) {
//        Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUsuarios = new HashMap<>();
//        Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUnidades = new HashMap<>();
//
//        //AGRUPA AS NOTIFICAÇÕES POR USUARIO E UNIDADE
//        agruparNotificacaoUsuarioUnidade(todas, mapUsuarios, mapUnidades);
//
//        Jedis jedis = getJedis();
//
//        //ADICIONA AS NOTIFICAÇÔES
//        adicionarNotificacaoUsuario(limpar, mapUsuarios, jedis);
//        adicionarNotificacaoUnidade(limpar, mapUnidades, jedis);
//    }
//
//    private void agruparNotificacaoUsuarioUnidade(List<NotificacaoService.NotificacaoDTO> todas, Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUsuarios, Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUnidades) {
//        for (NotificacaoService.NotificacaoDTO dto : todas) {
//            Long usuarioId = dto.getUsuarioId();
//            if (usuarioId != null) {
//                if (mapUsuarios.containsKey(usuarioId)) {
//                    mapUsuarios.get(usuarioId).add(dto);
//                } else {
//                    List<NotificacaoService.NotificacaoDTO> lista = Lists.newArrayList();
//                    lista.add(dto);
//                    mapUsuarios.put(usuarioId, lista);
//                }
//            } else {
//                Long unidadeId = dto.getUnidadeId();
//                if (unidadeId != null) {
//                    if (mapUnidades.containsKey(unidadeId)) {
//                        mapUnidades.get(unidadeId).add(dto);
//                    } else {
//                        List<NotificacaoService.NotificacaoDTO> lista = Lists.newArrayList();
//                        lista.add(dto);
//                        mapUnidades.put(unidadeId, lista);
//                    }
//                }
//            }
//        }
//    }
//
//    private void adicionarNotificacaoUsuario(Boolean limpar, Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUsuarios, Jedis jedis) {
//        for (Long id : mapUsuarios.keySet()) {
//            List<NotificacaoService.NotificacaoDTO> notificacaoDTOS = mapUsuarios.get(id);
//            try {
//                if (limpar) {
//                    jedis.del(PREFIX_NOTIFICAO_USUARIO + id);
//                }
//                for (NotificacaoService.NotificacaoDTO notificacaoDTO : notificacaoDTOS) {
//                    String json = new ObjectMapper().writeValueAsString(notificacaoDTO);
//                    jedis.lpush(PREFIX_NOTIFICAO_USUARIO + id, json);
//                }
//            } catch (Exception e) {
//                logger.error("erro ao adicionar todas notificação do usuário " + id + ".", e);
//            }
//        }
//    }
//
//    private void adicionarNotificacaoUnidade(Boolean limpar, Map<Long, List<NotificacaoService.NotificacaoDTO>> mapUnidades, Jedis jedis) {
//        for (Long id : mapUnidades.keySet()) {
//            List<NotificacaoService.NotificacaoDTO> notificacaoDTOS = mapUnidades.get(id);
//            try {
//
//                if (limpar) {
//                    jedis.del(PREFIX_NOTIFICAO_UNIDADE + id);
//                }
//                for (NotificacaoService.NotificacaoDTO notificacaoDTO : notificacaoDTOS) {
//                    String json = new ObjectMapper().writeValueAsString(notificacaoDTO);
//                    jedis.lpush(PREFIX_NOTIFICAO_UNIDADE + id, json);
//                }
//            } catch (Exception e) {
//                logger.error("erro ao adicionar todas notificação da unidade " + id + ".", e);
//            }
//        }
//    }
//
//
//    public void atribuirNullJedis() {
//        this.jedis = null;
//    }
//
//    public Jedis getJedis() {
//        if (jedisPool == null) {
//            jedisPool = getJedisPool();
//        }
//        if(jedis == null) {
//            Jedis resource = jedisPool.getResource();
//            jedis = resource;
//        }
//        return jedis;
//    }
//
//    private JedisPool getJedisPool() {
//        try {
//            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxWaitMillis(1000);
//            config.setMaxTotal(1000);
//            config.setMaxIdle(5);
//            config.setMinIdle(1);
//            config.setTestOnBorrow(true);
//            config.setTestOnReturn(true);
//            config.setTestWhileIdle(true);
//            config.setNumTestsPerEvictionRun(10);
//            config.setTimeBetweenEvictionRunsMillis(60000);
//            JedisPool jedisPool = new JedisPool(config, new URI(getUrl()), 5000);
//            return jedisPool;
//        } catch (Exception e) {
//            logger.error("Erro ao criar jedisPool ", e);
//        }
//        return null;
//    }
//
//    private String getUrl() {
//        String url = System.getenv("REDIS_ADDR");
//        if (url == null) {
//            url = "redis://:senha10@localhost:6379";
//        }
//        return url;
//    }
//}
