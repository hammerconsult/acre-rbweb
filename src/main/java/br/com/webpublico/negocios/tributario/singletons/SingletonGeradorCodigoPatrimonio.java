package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EntidadeSequenciaPropria;
import br.com.webpublico.entidades.ParametroPatrimonio;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ParametroPatrimonioFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 20, unit = TimeUnit.MINUTES)
public class SingletonGeradorCodigoPatrimonio implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(SingletonGeradorCodigoPatrimonio.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private BemFacade bemFacade;

    private Map<EntidadeSequenciaPropria, Long> mapaCodigosInserviveis = Maps.newHashMap();
    private Map<EntidadeSequenciaPropria, Long> mapaCodigos = Maps.newHashMap();
    private boolean reset = false;
    private Long codigoReset = null;
    private Long codigoResetInservivel = null;
    private EntidadeSequenciaPropria sequenciaPropriaReset;
    private EntidadeSequenciaPropria sequenciaPropriaResetInservivel;
    private List<Class> movimentosBloqueados;

    @Lock(LockType.WRITE)
    public void bloquearMovimento(Class movimento) {
        if (movimentosBloqueados == null) {
            resetarBloqueio();
        }
        verificarBloqueio(movimento);
        movimentosBloqueados.add(movimento);
    }

    public void resetarBloqueio() {
        movimentosBloqueados = Lists.newArrayList();
    }

    @Lock(LockType.WRITE)
    public void desbloquear(Class movimento) {
        if (movimentosBloqueados != null && movimentosBloqueados.contains(movimento)) {
            movimentosBloqueados.remove(movimento);
        }
    }

    @Lock(LockType.WRITE)
    public void verificarBloqueio(Class movimento) {
        ValidacaoException ve = new ValidacaoException();
        if (movimentosBloqueados == null) {
            resetarBloqueio();
        }
        for (Class movimentoBloqueado : movimentosBloqueados) {
            if (movimentoBloqueado.equals(movimento)) {
                String nomeClasse = buscarNomeDaClasse(movimento);
                ve.adicionarMensagemDeOperacaoNaoPermitida("Estão sendo gerados novos movimentos para " + nomeClasse + ". Por favor, aguarde alguns instantes e execute o processo novamente.");
            }
        }
        ve.lancarException();
    }

    public boolean hasMovimentoBloqueado(Class movimento) {
        if (movimentosBloqueados == null) {
            resetarBloqueio();
        }
        for (Class movimentoBloqueado : movimentosBloqueados) {
            if (movimentoBloqueado.equals(movimento)) {
                return true;
            }
        }
        return false;
    }

    private String buscarNomeDaClasse(Class movimento) {
        return Util.buscarNomeDaClasse(movimento);
    }

    @Lock(LockType.WRITE)
    public Long getProximoCodigoAquisicao(Entidade entidade, TipoBem tipoBem, ParametroPatrimonio parametroPatrimonio) throws ExcecaoNegocioGenerica {

        if (mapaCodigos == null) {
            mapaCodigos = Maps.newHashMap();
        }
        EntidadeSequenciaPropria sequenciaPropria = validarAtributosIniciais(entidade, parametroPatrimonio, tipoBem);
        Long codigo = null;
        if (mapaCodigos.containsKey(sequenciaPropria)) {
            codigo = mapaCodigos.get(sequenciaPropria);
        } else {
            logger.debug("foi buscar o código do bem por entidade");
            codigo = bemFacade.recuperarUltimoCodigoDoBemPorEntidade(entidade, parametroPatrimonio, tipoBem);
            logger.debug("código recuperado " + codigo);
        }

        if (reset && codigoReset == null) {
            codigoReset = codigo;
            sequenciaPropriaReset = sequenciaPropria;
        }

        codigo++;
        long faixaInicial = Long.parseLong(sequenciaPropria.getFaixaInicial());
        long faixaFinal = Long.parseLong(sequenciaPropria.getFaixaFinal());

        if (validarSequencia(faixaInicial, faixaFinal, codigo, entidade)) {
            mapaCodigos.put(sequenciaPropria, codigo);
            return codigo;
        }
        throw new ExcecaoNegocioGenerica("Não foi possível gerar o código para o bem!");
    }

    @Lock(LockType.WRITE)
    public Long getProximoCodigo(Entidade entidade, TipoBem tipoBem, ParametroPatrimonio parametroPatrimonio) throws ExcecaoNegocioGenerica {

        if (mapaCodigos == null) {
            mapaCodigos = Maps.newHashMap();
        }
        EntidadeSequenciaPropria sequenciaPropria = validarAtributosIniciais(entidade, parametroPatrimonio, tipoBem);

        Long codigo = null;
        if (mapaCodigos.containsKey(sequenciaPropria)) {
            codigo = mapaCodigos.get(sequenciaPropria);
        } else {
            logger.debug("foi buscar o código do bem por entidade");
            codigo = bemFacade.recuperarUltimoCodigoDoBemPorEntidade(entidade, parametroPatrimonio, tipoBem);
            logger.debug("código recuperado " + codigo);
        }
        if (reset && codigoReset == null) {
            codigoReset = codigo;
            sequenciaPropriaReset = sequenciaPropria;
        }
        codigo++;
        long faixaInicial = Long.parseLong(sequenciaPropria.getFaixaInicial());
        long faixaFinal = Long.parseLong(sequenciaPropria.getFaixaFinal());

        if (validarSequencia(faixaInicial, faixaFinal, codigo, entidade)) {
            mapaCodigos.put(sequenciaPropria, codigo);
            return codigo;
        }
        throw new ExcecaoNegocioGenerica("Não foi possível gerar o código para o bem!");
    }

    private Boolean validarSequencia(Long faixaInicial, Long faixaFinal, Long codigo, Entidade entidade) {

        if (codigo < faixaInicial) {
            codigo = faixaInicial;
        }

        if (codigo > faixaFinal) {
            throw new ExcecaoNegocioGenerica("A faixa para geração do código de identificação patrimônial definida no parâmetro do patrimônio para a entidade " + entidade.getNome() + " já chegou ao fim, defina uma faixa maior.");
        }
        return Boolean.TRUE;
    }

    @Lock(LockType.WRITE)
    public Long getProximoCodigoInservivel(Entidade entidade, TipoBem tipoBem, ParametroPatrimonio parametroPatrimonio) throws ExcecaoNegocioGenerica {
        if (mapaCodigosInserviveis == null) {
            mapaCodigosInserviveis = Maps.newHashMap();
        }
        EntidadeSequenciaPropria sequenciaPropria = validarAtributosIniciais(entidade, parametroPatrimonio, tipoBem);
        Long codigo = null;
        if (mapaCodigosInserviveis.containsKey(sequenciaPropria)) {
            codigo = mapaCodigosInserviveis.get(sequenciaPropria);
            if (codigo >= parametroPatrimonio.getFaixaFinalParaInsevivel()) {
                throw new ExcecaoNegocioGenerica("A faixa de registros patrimoniais definida no parâmetro do patrimonio para bens inservivel chegou ao fim.");
            }
        } else {
            logger.debug("foi buscar o código inservivel do bem por entidade");
            codigo = parametroPatrimonioFacade.retornaUltimoCodigoInservivelParaEfetivacaoDoLevantamento(entidade);
            logger.debug("buscou o codigo inservivel " + codigo);
        }
        if (reset && codigoResetInservivel == null) {
            sequenciaPropriaResetInservivel = sequenciaPropria;
            codigoResetInservivel = codigo;
        }
        codigo++;
        mapaCodigosInserviveis.put(sequenciaPropria, codigo);
        return codigo;
    }

    public EntidadeSequenciaPropria validarAtributosIniciais(Entidade entidade, ParametroPatrimonio parametroPatrimonio, TipoBem tipoBem) {
        if (entidade == null) {
            throw new ExcecaoNegocioGenerica("A entidade não pode ser vazia. ");
        }
        if (parametroPatrimonio == null) {
            throw new ExcecaoNegocioGenerica("Não existe parâmetro definido para o patrimônio. ");
        }
        EntidadeSequenciaPropria sequenciaPropria;
        if (tipoBem != null) {
            sequenciaPropria = parametroPatrimonioFacade.recuperarSequenciaPropriaPorTipoGeracao(entidade, tipoBem);
        } else {
            sequenciaPropria = parametroPatrimonioFacade.recuperarSequenciaPropria(entidade);
        }

        if (sequenciaPropria == null) {
            throw new ExcecaoNegocioGenerica("A entidade " + entidade.getNome() + " não possui uma sequência de geração do código de identificação patrimônial definida no parâmetro do patrimônio. ");
        }
        if (parametroPatrimonio.getFaixaInicialParaInsevivel() == null && parametroPatrimonio.getFaixaFinalParaInsevivel() == null) {
            throw new ExcecaoNegocioGenerica("Não foi definido no parâmetro do patrimonio uma faixa para bens inservíveis.");
        }
        return sequenciaPropria;
    }

    public void inicializarReset() {
        reset = true;
    }

    public void reset() {
        if (reset) {
            try {
                if (mapaCodigos != null) {
                    mapaCodigos.put(sequenciaPropriaReset, codigoReset);
                }
                if (mapaCodigosInserviveis != null) {
                    mapaCodigosInserviveis.put(sequenciaPropriaResetInservivel, codigoResetInservivel);
                }
            } catch (NullPointerException nu) {
                nu.printStackTrace();
            }
        }
    }

    public void finalizarReset() {
        sequenciaPropriaReset = null;
        codigoReset = null;
        sequenciaPropriaResetInservivel = null;
        codigoResetInservivel = null;
        reset = false;
    }

    public void resetarMapas() {
        mapaCodigos = null;
        mapaCodigosInserviveis = null;
    }

    public List<Class> getMovimentosBloqueados() {
        return movimentosBloqueados;
    }

}

