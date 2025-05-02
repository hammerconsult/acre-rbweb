package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ListaPaginada<T> {

    private Integer quantidadeRegistros;
    private Integer numeroPagina;
    private List<T> registros;
    private Integer totalRegistros;
    private List<T> registrosSelecionados;

    public ListaPaginada() {
        quantidadeRegistros = 10;
        numeroPagina = 1;
        registros = Lists.newArrayList();
        totalRegistros = 0;
        registrosSelecionados = Lists.newArrayList();
    }

    public void pesquisar() {
        quantidadeRegistros = 10;
        numeroPagina = 1;
        registros = Lists.newArrayList();
        totalRegistros = 0;
        registrosSelecionados = Lists.newArrayList();
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
        totalRegistros = contarRegistros();
    }

    protected abstract List<T> buscarDados(int firstResult, int maxResult);

    protected abstract Integer contarRegistros();


    private int getFirstResult() {
        return (numeroPagina - 1) * quantidadeRegistros;
    }

    public Integer getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public List<T> getRegistros() {
        return registros;
    }

    public List<T> getRegistrosSelecionados() {
        return registrosSelecionados;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public int getTotalPaginas() {
        return new BigDecimal(totalRegistros)
            .divide(new BigDecimal(quantidadeRegistros), RoundingMode.UP)
            .intValue();
    }

    public void alterarQuantidadeRegistros(Integer quantidade) {
        quantidadeRegistros = quantidade;
        numeroPagina = 1;
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
    }

    public void primeiraPagina() {
        numeroPagina = 1;
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
    }

    public void paginaAnterior() {
        numeroPagina -= 1;
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
    }

    public void proximaPagina() {
        numeroPagina += 1;
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
    }

    public void ultimaPagina() {
        numeroPagina = getTotalPaginas();
        registros = buscarDados(getFirstResult(), quantidadeRegistros);
    }

    public boolean hasTodosRegistrosSelecionados() {
        if (registros.isEmpty()) return false;
        for (T registro : registros) {
            if (!hasRegistroSelecionado(registro)) {
                return false;
            }
        }
        return true;
    }

    public void addTodosRegistros() {
        for (T registro : registros) {
            if (!hasRegistroSelecionado(registro)) {
                registrosSelecionados.add(registro);
            }
        }
    }

    public void removeTodosRegistros() {
        for (T registro : registros) {
            if (hasRegistroSelecionado(registro)) {
                registrosSelecionados.remove(registro);
            }
        }
    }

    public boolean hasRegistroSelecionado(T registro) {
        return registrosSelecionados.stream().anyMatch(r -> r.equals(registro));
    }

    public void addRegistro(T registro) {
        registrosSelecionados.add(registro);
    }

    public void removeRegistro(T registro) {
        registrosSelecionados = registrosSelecionados.stream()
            .filter(c -> !c.equals(registro)).collect(Collectors.toList());
    }
}
