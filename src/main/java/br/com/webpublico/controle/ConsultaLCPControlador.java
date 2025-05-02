package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.PesquisaLCP;
import br.com.webpublico.negocios.ConsultaLCPFacade;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 04/10/17.
 */

@ManagedBean(name = "consultaLCPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-lcp", pattern = "/consulta-lcp/", viewId = "/faces/financeiro/clp/consultalcp/lista.xhtml")
})
public class ConsultaLCPControlador implements Serializable {

    @EJB
    private ConsultaLCPFacade consultaLCPFacade;
    private List<PesquisaLCP> pesquisas;
    private List<Pagina> paginas;
    private Pagina paginaAtual;
    private Integer total;
    private PesquisaLCP filtros;

    @URLAction(mappingId = "consulta-lcp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = new PesquisaLCP();
    }

    private void buscarLcps() {
        montarPaginas();
        pesquisas = consultaLCPFacade.buscarLcps(filtros, paginaAtual.index, 10);
    }

    public void buscarLcpsLimpandoPaginas() {
        paginas = Lists.newArrayList();
        buscarLcps();
    }

    public List<PesquisaLCP> getPesquisas() {
        return pesquisas;
    }

    public void setPesquisas(List<PesquisaLCP> pesquisas) {
        this.pesquisas = pesquisas;
    }

    public void montarPaginas() {
        if (paginas == null || paginas.isEmpty()) {
            paginas = Lists.newArrayList();
            total = consultaLCPFacade.buscarQuantidadeLcps(filtros);
            double totalPaginas = total / 10;
            if ((total % 10) > 0) {
                totalPaginas++;
            }
            for (int numeroPagina = 0; numeroPagina < totalPaginas; numeroPagina++) {
                Pagina pagina = new Pagina(numeroPagina + 1, numeroPagina > 0 ? (10 * numeroPagina) : 0);
                paginas.add(pagina);
            }
            if (!paginas.isEmpty()) {
                paginaAtual = paginas.get(0);
            } else {
                paginaAtual = new Pagina(1, 0);
            }
        }
    }

    public String getTextoRodapeTabela() {
        if (paginaAtual != null) {
            int de = paginaAtual.index == 0 ? 1 : paginaAtual.index + 1;
            int ate = paginaAtual.index == 0 ? pesquisas.size() :
                paginaAtual.index + (pesquisas.size());
            return "Exibindo de " + de + " até " + ate + " em um total de " + total + " registros";
        }
        return "";
    }

    public void avancarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero);
        buscarLcps();
    }

    public void voltarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero - 2);
        buscarLcps();
    }

    public String getNumeroPaginas() {
        if (paginaAtual != null) {
            return paginaAtual.numero + " / " + paginas.size();
        }
        return "1/1";
    }

    public List<Pagina> getPaginas() {
        return paginas;
    }

    public void setPaginas(List<Pagina> paginas) {
        this.paginas = paginas;
    }

    public Pagina getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(Pagina paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public PesquisaLCP getFiltros() {
        return filtros;
    }

    public void setFiltros(PesquisaLCP filtros) {
        this.filtros = filtros;
    }

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
}
