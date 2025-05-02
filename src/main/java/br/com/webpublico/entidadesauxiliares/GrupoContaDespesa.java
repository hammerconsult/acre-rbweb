package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

public class GrupoContaDespesa {

    private String grupo;
    private Long idGrupo;
    private TipoObjetoCompra tipoObjetoCompra;
    private GrupoObjetoCompra grupoObjetoCompra;
    private List<Conta> contasDespesa;
    private String tituloGrupo;
    private List<String> mensagensValidacao;

    public GrupoContaDespesa(TipoObjetoCompra tipoObjetoCompra) {
        this.contasDespesa = Lists.newArrayList();
        this.tipoObjetoCompra = tipoObjetoCompra;
        this.mensagensValidacao = Lists.newArrayList();
        this.tituloGrupo = tipoObjetoCompra.getDescricaoGrupo();
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public List<Conta> getContasDespesa() {
        return contasDespesa;
    }

    public void setContasDespesa(List<Conta> contasDespesa) {
        this.contasDespesa = contasDespesa;
    }

    public String getTituloGrupo() {
        return tituloGrupo;
    }

    public void setTituloGrupo(String tituloGrupo) {
        this.tituloGrupo = tituloGrupo;
    }

    public List<String> getMensagensValidacao() {
        return mensagensValidacao;
    }

    public void setMensagensValidacao(List<String> mensagensValidacao) {
        this.mensagensValidacao = mensagensValidacao;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public boolean hasContasDespesa() {
        return contasDespesa != null && !contasDespesa.isEmpty();
    }

    public boolean hasMensagensValidacao() {
        return mensagensValidacao != null && !mensagensValidacao.isEmpty();
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrupoContaDespesa that = (GrupoContaDespesa) o;
        return Objects.equals(grupo, that.grupo) && Objects.equals(contasDespesa, that.contasDespesa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grupo, contasDespesa);
    }

    public void lancarMensagens() {
        if (hasMensagensValidacao()) {
            ValidacaoException ve = new ValidacaoException();
            for (String msg : mensagensValidacao) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
            }
            ve.lancarException();
        }
    }
}
