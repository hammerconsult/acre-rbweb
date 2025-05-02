package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Buzatto on 30/10/2015.
 */
public class EventoRelatorioTotalVerbasPorOrgao {

    private String codigo;
    private String tipoEventoFp;
    private String descricao;
    private String tipo;
    private BigDecimal quantidade;
    private BigDecimal valor;
    private String grupo;
    private String nomeServidor;
    private String matriculaServidor;
    private List<ServidorAux> servidores;
    private Integer mes;
    private String codigoHierarquia;

    public EventoRelatorioTotalVerbasPorOrgao() {
        quantidade = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
        servidores = Lists.newArrayList();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public List<ServidorAux> getServidores() {
        return servidores;
    }

    public void setServidores(List<ServidorAux> servidores) {
        this.servidores = servidores;
    }

    public String getTipoEventoFp() {
        return tipoEventoFp;
    }

    public void setTipoEventoFp(String tipoEventoFp) {
        this.tipoEventoFp = tipoEventoFp;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getMatriculaServidor() {
        return matriculaServidor;
    }

    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }

    public String getCodigoHierarquia() {
        return codigoHierarquia;
    }

    public void setCodigoHierarquia(String codigoHierarquia) {
        this.codigoHierarquia = codigoHierarquia;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof EventoRelatorioTotalVerbasPorOrgao)) {
            return false;
        }
        EventoRelatorioTotalVerbasPorOrgao relatorio = (EventoRelatorioTotalVerbasPorOrgao) obj;
        return Objects.equal(getCodigo(), relatorio.getCodigo()) && Objects.equal(getTipoEventoFp(), relatorio.getTipoEventoFp());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCodigo(), getTipoEventoFp());
    }
}
