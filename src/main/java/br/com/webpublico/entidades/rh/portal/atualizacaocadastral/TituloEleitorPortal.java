package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.pessoa.dto.TituloEleitorDTO;
import br.com.webpublico.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class TituloEleitorPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String numero;
    private String zona;
    private String sessao;
    private Date dataEmissao;
    @ManyToOne
    private Cidade cidade;

    public TituloEleitorPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public Date getDataEmissao() {
        return dataEmissao != null ? DataUtil.dataSemHorario(dataEmissao) : dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public static TituloEleitorPortal dtoToTituloEleitorPortal(TituloEleitorDTO dto) {
        TituloEleitorPortal titulo = new TituloEleitorPortal();
        titulo.setCidade(Cidade.dtoToCidade(dto.getCidadeTitulo()));
        titulo.setDataEmissao(dto.getDataEmissaoTitulo());
        titulo.setNumero(dto.getNumeroTitulo());
        titulo.setSessao(dto.getSessao());
        titulo.setZona(dto.getZona());
        return titulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TituloEleitorPortal that = (TituloEleitorPortal) o;
        return Objects.equals(numero != null ? numero.trim() : null, that.numero != null ? that.numero.trim() : null)
            && Objects.equals(zona != null ? zona.trim() : null, that.zona != null ? that.zona.trim() : null)
            && Objects.equals(sessao != null ? sessao.trim(): null, that.sessao != null ? that.sessao.trim() : null)
            && Objects.equals(DataUtil.getDataFormatada(dataEmissao), DataUtil.getDataFormatada(that.dataEmissao))
            && Objects.equals(cidade, that.cidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, zona, sessao, dataEmissao, cidade);
    }
}
