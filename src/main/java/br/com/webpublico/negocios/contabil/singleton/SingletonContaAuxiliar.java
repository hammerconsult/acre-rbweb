package br.com.webpublico.negocios.contabil.singleton;

import br.com.webpublico.entidades.ContaAuxiliar;
import br.com.webpublico.entidades.ContaContabil;
import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.entidades.PlanoDeContasExercicio;
import br.com.webpublico.entidadesauxiliares.MapContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonContaAuxiliar implements Serializable {

    private HashMap<MapContaAuxiliar, ContaAuxiliar> contasAuxiliares = Maps.newHashMap();
    private HashMap<String, ContaAuxiliarDetalhada> contasAuxiliaresDetalhadas = Maps.newHashMap();

    private PlanoDeContasExercicio planoDeContasExercicio;

    @PostConstruct
    private void init() {
        limparContasAuxiliares();
        limparContasDetalhadas();
        planoDeContasExercicio = null;
    }

    @Lock(LockType.WRITE)
    public ContaAuxiliar buscarContaAuxiliarNoMap(PlanoDeContas planoDeContas, String codigo, ContaContabil contaContabil) {
        if (contasAuxiliares == null) {
            contasAuxiliares = Maps.newHashMap();
        }
        for (Map.Entry<MapContaAuxiliar, ContaAuxiliar> conta : contasAuxiliares.entrySet()) {
            if (conta.getKey().getCodigo().equals(codigo) && conta.getKey().getPdc().equals(planoDeContas) && conta.getValue().getContaContabil().equals(contaContabil)) {
                return conta.getValue();
            }
        }
        return null;
    }

    @Lock(LockType.WRITE)
    public ContaAuxiliarDetalhada buscarContaAuxiliarDetalhadaNoMap(ContaAuxiliarDetalhada cad) {
        if (contasAuxiliaresDetalhadas == null) {
            limparContasDetalhadas();
        }
        return contasAuxiliaresDetalhadas.get(montarHash(cad));
    }

    private String montarHash(ContaAuxiliarDetalhada cad) {
        return cad.getCodigo() +
            (cad.getUnidadeOrganizacional() != null ? cad.getUnidadeOrganizacional().getId() : "-") +
            (cad.getConta() != null ? cad.getConta().getId() : "-") +
            (cad.getContaContabil() != null ? cad.getContaContabil().getId() : "-") +
            (cad.getContaDeDestinacao() != null ? cad.getContaDeDestinacao().getId() : "-") +
            (cad.getExercicioAtual() != null ? cad.getExercicioAtual().getId() : "-") +
            (cad.getExercicioOrigem() != null ? cad.getExercicioOrigem().getId() : "-") +
            (cad.getSubSistema() != null ? cad.getSubSistema() : "-") +
            (cad.getEs() != null ? cad.getEs() : "-") +
            (cad.getExercicio() != null ? cad.getExercicio().getId() : "-") +
            (cad.getClassificacaoFuncional() != null ? cad.getClassificacaoFuncional() : "-") +
            (cad.getCodigoCO() != null ? cad.getCodigoCO() : "-") +
            (cad.getDividaConsolidada() != null ? cad.getDividaConsolidada() : "-");
    }

    @Lock(LockType.WRITE)
    public void adicionarContaAuxiliarNoMap(PlanoDeContas planoDeContas, String codigo, ContaAuxiliar contaAuxiliar) {
        if (contasAuxiliares == null) {
            contasAuxiliares = Maps.newHashMap();
        }
        if (buscarContaAuxiliarNoMap(planoDeContas, codigo, contaAuxiliar.getContaContabil()) == null) {
            MapContaAuxiliar mapContaAuxiliar = new MapContaAuxiliar(planoDeContas, codigo);
            contasAuxiliares.put(mapContaAuxiliar, contaAuxiliar);
        }
    }

    @Lock(LockType.WRITE)
    public void adicionarContaAuxiliarDetalhadaNoMap(ContaAuxiliarDetalhada contaAuxiliarDetalhada) {
        if (contasAuxiliaresDetalhadas == null) {
            limparContasDetalhadas();
        }
        if (buscarContaAuxiliarDetalhadaNoMap(contaAuxiliarDetalhada) == null) {
            contasAuxiliaresDetalhadas.put(montarHash(contaAuxiliarDetalhada), contaAuxiliarDetalhada);
        }
    }

    public void limparContasAuxiliares() {
        contasAuxiliares = Maps.newHashMap();
        planoDeContasExercicio = null;
    }

    public void limparContasDetalhadas() {
        contasAuxiliaresDetalhadas = Maps.newHashMap();
    }

    public HashMap<MapContaAuxiliar, ContaAuxiliar> getContasAuxiliares() {
        return contasAuxiliares;
    }

    public HashMap<String, ContaAuxiliarDetalhada> getContasAuxiliaresDetalhadas() {
        return contasAuxiliaresDetalhadas;
    }

    public PlanoDeContasExercicio getPlanoDeContasExercicio() {
        return planoDeContasExercicio;
    }

    public void setPlanoDeContasExercicio(PlanoDeContasExercicio planoDeContasExercicio) {
        this.planoDeContasExercicio = planoDeContasExercicio;
    }
}
