package br.com.webpublico.controle;

import br.com.webpublico.interfaces.AssistentePaginacao;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by HardRock on 26/04/2017.
 */
@ManagedBean
@ViewScoped
public class PaginacaoControlador implements Serializable {

    private List<Pagina> paginas;
    private Integer total;
    private Pagina paginaAtual;
    private AssistentePaginacao assistente;

    public class Pagina {
        protected int numero, index;

        public Pagina(int numero, int index) {
            this.numero = numero;
            this.index = index;
        }

        public int getNumero() {
            return numero;
        }

        public void setNumero(int numero) {
            this.numero = numero;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public void montarPaginas(AssistentePaginacao assistente) {
        if (paginas == null || paginas.isEmpty()) {
            this.assistente = assistente;
            paginas = Lists.newArrayList();
            total = assistente.getTotalRegistro();
            double totalPaginas = total / assistente.getTotalRegistroPorPagina();
            for (int numeroPagina = 0; numeroPagina < totalPaginas; numeroPagina++) {
                Pagina pagina = new Pagina(numeroPagina + 1, numeroPagina > 0 ? (assistente.getTotalRegistroPorPagina().intValue() * numeroPagina) : 0);
                paginas.add(pagina);
            }
            if (!paginas.isEmpty()) {
                paginaAtual = paginas.get(0);
            } else {
                paginaAtual = new Pagina(1, 0);
            }
        }
    }

    public void reiniciarPaginas() {
        if (paginas != null) {
            paginas.clear();
            total = 0;
            paginaAtual.setIndex(0);
            paginaAtual.setNumero(0);
            assistente.setTamanhoListaPorPagina(assistente.getTamanhoListaPorPagina());
        }
    }

    public void avancarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero);
        assistente.setIndexPagina(paginaAtual.getIndex());
    }

    public void voltarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero - 2);
        assistente.setIndexPagina(paginaAtual.getIndex());
    }

    public List<Pagina> getPaginas() {
        if (paginas == null) {
            paginas = Lists.newArrayList();
        }
        return paginas;
    }

    public String getNumeroPaginas() {
        if (paginaAtual != null) {
            return paginaAtual.numero + " / " + paginas.size();
        }
        return "1/1";
    }

    public String getTextoRodapeTabela() {
        int de = paginaAtual.index == 0 ? 1 : paginaAtual.index + 1;
        int ate = paginaAtual.index == 0 ? assistente.getTamanhoListaPorPagina() : paginaAtual.index + assistente.getTamanhoListaPorPagina();
        return "Exibindo de " + de + " até " + ate + " em um total de " + total + " registros";
    }

    public void setPaginas(List<Pagina> paginas) {
        this.paginas = paginas;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Pagina getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(Pagina paginaAtual) {
        this.paginaAtual = paginaAtual;
    }
}
