package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.ProtocoloSimplesControlador;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.util.List;

public class FiltrosPesquisaProtocolo {

    private int inicioConsulta;
    private FiltroListaProtocolo filtro;
    private ProtocoloSimplesControlador.FiltroGuiaRecebimento filtroGuiaRecebimento;
    private List<VoProcesso> processos;
    private List<VoTramite> tramites;
    private List<VoTramite> tramitesAuxiliares;
    private List<Pagina> paginas;
    private Pagina paginaAtual;
    private Integer numeroPaginaAtual;
    private QuantidadeRegistros quantidadeRegistros;
    private Integer quantidadeTotal;

    public FiltrosPesquisaProtocolo() {
        inicioConsulta = 0;
        quantidadeTotal = 1;
        paginas = Lists.newArrayList();
        processos = Lists.newArrayList();
        tramites = Lists.newArrayList();
        tramitesAuxiliares = Lists.newArrayList();
        paginaAtual = new Pagina(1, 0);
        paginas.add(paginaAtual);
        numeroPaginaAtual = 1;
        quantidadeRegistros = QuantidadeRegistros.DEZ;
    }

    public int getInicioConsulta() {
        return inicioConsulta;
    }

    public void setInicioConsulta(int inicioConsulta) {
        this.inicioConsulta = inicioConsulta;
    }

    public FiltroListaProtocolo getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroListaProtocolo filtro) {
        this.filtro = filtro;
    }

    public List<VoProcesso> getProcessos() {
        return processos;
    }

    public void setProcessos(List<VoProcesso> processos) {
        this.processos = processos;
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

    public Integer getNumeroPaginaAtual() {
        return numeroPaginaAtual;
    }

    public void setNumeroPaginaAtual(Integer numeroPaginaAtual) {
        this.numeroPaginaAtual = numeroPaginaAtual;
    }

    public QuantidadeRegistros getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(QuantidadeRegistros quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public ProtocoloSimplesControlador.FiltroGuiaRecebimento getFiltroGuiaRecebimento() {
        return filtroGuiaRecebimento;
    }

    public void setFiltroGuiaRecebimento(ProtocoloSimplesControlador.FiltroGuiaRecebimento filtroGuiaRecebimento) {
        this.filtroGuiaRecebimento = filtroGuiaRecebimento;
    }

    public List<VoTramite> getTramites() {
        return tramites;
    }

    public void setTramites(List<VoTramite> tramites) {
        this.tramites = tramites;
    }

    public List<VoTramite> getTramitesAuxiliares() {
        return tramitesAuxiliares;
    }

    public void setTramitesAuxiliares(List<VoTramite> tramitesAuxiliares) {
        this.tramitesAuxiliares = tramitesAuxiliares;
    }

    public String getNumeroPaginas() {
        if (paginas != null) {
            return "/" + paginas.size();
        }
        return "1";
    }

    public void montarPaginas(Integer quantidadePaginas) {
        if (paginas == null || paginas.isEmpty()) {
            paginas = Lists.newArrayList();
            quantidadeTotal = quantidadePaginas;

            double totalPaginas = quantidadeTotal.doubleValue() / quantidadeRegistros.getQuantidade().doubleValue();
            for (int numeroPagina = 0; numeroPagina < totalPaginas; numeroPagina++) {
                paginas.add(novaPagina(numeroPagina + 1, numeroPagina > 0 ? (quantidadeRegistros.getQuantidade() * numeroPagina) : 0));
            }
            if (!paginas.isEmpty()) {
                paginaAtual = paginas.get(0);
            } else {
                paginaAtual = novaPagina(1, 0);
            }
        }
    }

    public String getTextoTabela() {
        if (paginaAtual != null) {
            if (processos != null && !processos.isEmpty()) {
                return buscarTextoTabela(processos);
            }
            if (tramitesAuxiliares != null && !tramitesAuxiliares.isEmpty()) {
                return buscarTextoTabela(tramitesAuxiliares);
            }
            if (tramites != null && !tramites.isEmpty()) {
                return buscarTextoTabela(tramites);
            }
        }
        return "1";
    }

    private String buscarTextoTabela(List lista) {
        int de = paginaAtual.getIndex() == 0 ? 1 : paginaAtual.getIndex() + 1;
        int ate = paginaAtual.getIndex() == 0 ? lista.size() :
            paginaAtual.getIndex() + (lista.size());
        return de + " - " + ate;
    }

    public void anteriores() {
        inicioConsulta -= quantidadeRegistros.getQuantidade();
        paginaAtual = paginas.get(paginaAtual.numero - 2);
        numeroPaginaAtual = paginaAtual.numero;
        if (inicioConsulta < 0) {
            inicioConsulta = 0;
        }
    }

    public List<SelectItem> getQuantidades() {
        return Util.getListSelectItemSemCampoVazio(QuantidadeRegistros.values(), false);
    }

    public void irParaPrimeiro() {
        inicioConsulta = 0;
        paginaAtual = paginas.get(0);
        numeroPaginaAtual = paginaAtual.numero;
    }

    public void proximos() {
        inicioConsulta += quantidadeRegistros.getQuantidade();
        paginaAtual = paginas.get(paginaAtual.numero);
        numeroPaginaAtual = paginaAtual.numero;
    }

    public void irParaUltimo() {
        numeroPaginaAtual = paginas.size();
        paginaAtual = paginas.get(numeroPaginaAtual - 1);
        inicioConsulta = (quantidadeRegistros.getQuantidade() * paginaAtual.numero) - quantidadeRegistros.getQuantidade();
    }

    public void resetarPaginas() {
        if (paginas != null && !paginas.isEmpty()) {
            paginaAtual = paginas.get(0);
        } else {
            paginaAtual = novaPagina(1, 0);
        }
        numeroPaginaAtual = 1;
    }

    public void limparPaginas() {
        numeroPaginaAtual = 1;
        inicioConsulta = 0;
        paginas.clear();
        tramitesAuxiliares = Lists.newArrayList();
    }

    public void alterarPagina() {
        validarPagina();
        paginaAtual = paginas.get(numeroPaginaAtual - 1);
        inicioConsulta = (quantidadeRegistros.getQuantidade() * paginaAtual.numero) - quantidadeRegistros.getQuantidade();
    }

    private void validarPagina() {
        ValidacaoException ve = new ValidacaoException();
        if (numeroPaginaAtual == null || numeroPaginaAtual <= 0 || numeroPaginaAtual > paginas.size()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número deve ser maior que zero e menor que a quantidade total de páginas.");
        }
        ve.lancarException();
    }

    public void validarPesquisaGuiaRecebimento() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroGuiaRecebimento != null) {
            if (filtroGuiaRecebimento.getUnidadeOrganizacional() == null && filtroGuiaRecebimento.isInterno()) {
                ve.adicionarMensagemDeCampoObrigatorio("A unidade organizacional deve ser informada para o tipo de processo interno!");
            }
            if ((filtroGuiaRecebimento.getDestinoExterno() == null || filtroGuiaRecebimento.getDestinoExterno().trim().isEmpty()) && filtroGuiaRecebimento.isExterno()) {
                ve.adicionarMensagemDeCampoObrigatorio("O destino externo deve ser informado para o tipo de processo externo!");
            }
        }
        ve.lancarException();
    }

    public Pagina novaPagina(int numero, int index) {
        return new Pagina(numero, index);
    }

    public class Pagina {
        private int numero, index;

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

    public enum QuantidadeRegistros {
        DEZ(10),
        VINTE_E_CINCO(25),
        CINQUENTA(50),
        CEM(100),
        QUINHENTOS(500);

        QuantidadeRegistros(Integer quantidade) {
            this.quantidade = quantidade;
        }

        private Integer quantidade;

        public Integer getQuantidade() {
            return quantidade;
        }

        @Override
        public String toString() {
            return quantidade.toString();
        }
    }
}
