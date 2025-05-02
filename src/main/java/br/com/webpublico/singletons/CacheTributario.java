package br.com.webpublico.singletons;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.util.ConverterExercicioCache;
import br.com.webpublico.util.ConverterGenericoCacheTributario;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class CacheTributario {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    @EJB
    private MoedaFacade moedaFacade;
    private List<Divida> dividas;
    private List<Exercicio> exercicios;
    private List<ConfiguracaoAcrescimos> acrescimos;
    private ConverterGenericoCacheTributario converterDivida;
    private Map<TipoCadastroTributario, List<Divida>> dividasPorTipoCadastro;
    private ConverterExercicioCache converterExercicio;
    private Map<Integer, BigDecimal> ufmPorAno;
    private ConfiguracaoTributario configuracaoTributario;
    private Map<Class, Object> cacheObjetos;
    private ConfiguracaoNfse configuracaoNfse;

    @PostConstruct
    public void init() {
        JdbcDividaAtivaDAO dividaAtivaDAO = (JdbcDividaAtivaDAO) Util.getSpringBeanPeloNome("dividaAtivaDAO");

        dividas = dividaAtivaDAO.findAll();
        exercicios = exercicioFacade.lista();
        acrescimos = configuracaoAcrescimosFacade.recuperarTodasComDependencias();
        dividasPorTipoCadastro = Maps.newHashMap();
        for (Divida divida : dividas) {
            if (!dividasPorTipoCadastro.containsKey(divida.getTipoCadastro())) {
                dividasPorTipoCadastro.put(divida.getTipoCadastro(), new ArrayList<Divida>());
            }
            dividasPorTipoCadastro.get(divida.getTipoCadastro()).add(divida);
        }
        cacheObjetos = Maps.newHashMap();
    }

    public Map<Class, Object> getCacheObjetos() {
        return cacheObjetos;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public List<Divida> getDividasPorTipoCadastro(TipoCadastroTributario tipoCadastro) {
        return Lists.newArrayList(dividasPorTipoCadastro.get(tipoCadastro));
    }


    public ConverterGenericoCacheTributario getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenericoCacheTributario(Divida.class, this);
        }
        return converterDivida;
    }

    public ConverterExercicioCache getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicioCache(this);
        }
        return converterExercicio;
    }

    public BigDecimal buscarValorUFMParaAno(Integer ano) {
        if (ufmPorAno == null) {
            ufmPorAno = Maps.newHashMap();
        }
        if (!ufmPorAno.containsKey(ano)) {
            ufmPorAno.put(ano, moedaFacade.buscarValorUFMParaAno(ano));
        }
        return ufmPorAno.get(ano);
    }

    public Exercicio getExercicioPorAno(int chave) {
        for (Exercicio exercicio : exercicios) {
            if (exercicio.getAno().equals(chave)) {
                return exercicio;
            }
        }
        return null;
    }

    public ConfiguracaoAcrescimos buscarConfiguracaoAcrescimos(Long idConfiguracao) {
        for (ConfiguracaoAcrescimos acrescimo : acrescimos) {
            if (acrescimo.getId().equals(idConfiguracao)) {
                return acrescimo;
            }
        }
        return null;
    }

    public Divida getDividasPorId(Long aLong) {
        for (Divida divida : dividas) {
            if (divida.getId().equals(aLong)) {
                return divida;
            }
        }
        return null;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }
    public ConfiguracaoNfse getConfiguracaoNfse() {
        return configuracaoNfse;
    }

    public void setConfiguracaoNfse(ConfiguracaoNfse configuracaoNfse) {
        this.configuracaoNfse = configuracaoNfse;
    }
}
