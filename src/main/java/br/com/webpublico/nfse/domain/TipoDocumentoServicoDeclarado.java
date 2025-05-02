package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.TipoDocumentoServicoDeclaradoNfseDTO;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Tipos de Serviço Declarado")
@Table(name = "TIPODOCSERVICODECLARADO")
public class TipoDocumentoServicoDeclarado extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private Long codigo;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private String descricao;

    @Etiqueta("Série")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private String serie;

    @Etiqueta("Sub Série")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private String subSerie;


    @Etiqueta("Ativo")
    @Pesquisavel
    @Tabelavel
    private Boolean ativo;

    public TipoDocumentoServicoDeclarado() {
        super();
        ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getSubSerie() {
        return subSerie;
    }

    public void setSubSerie(String subSerie) {
        this.subSerie = subSerie;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    @Override
    public TipoDocumentoServicoDeclaradoNfseDTO toNfseDto() {
        return new TipoDocumentoServicoDeclaradoNfseDTO(id, descricao, serie, subSerie, ativo);
    }

    @Override
    public String toString() {
        return codigo + "";
    }

}
