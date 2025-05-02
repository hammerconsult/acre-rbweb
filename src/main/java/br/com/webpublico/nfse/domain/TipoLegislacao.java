package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.TipoLegislacaoDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Created by wellington on 25/08/17.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Tipos de Legislação")
public class TipoLegislacao extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private String descricao;

    @Etiqueta("Ordem de Exibição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Min(value = 1)
    @Max(value = 999)
    private Integer ordemExibicao;

    @Etiqueta("Habilitar Exibição")
    @Pesquisavel
    @Tabelavel
    private Boolean habilitarExibicao;

    public TipoLegislacao() {
        super();
        habilitarExibicao = Boolean.FALSE;
    }

    public static List<TipoLegislacaoDTO> toListTipoLegislacaoDTO(List<TipoLegislacao> tipos) {
        List<TipoLegislacaoDTO> toReturn = Lists.newArrayList();
        if (tipos != null && !tipos.isEmpty()) {
            for (TipoLegislacao tipoLegislacao : tipos) {
                toReturn.add(tipoLegislacao.toTipoLegislacaoDTO());
            }
        }
        return toReturn;
    }

    public static TipoLegislacao toTipoLegislacao(TipoLegislacaoDTO tipoLegislacaoDTO) {
        TipoLegislacao tipoLegislacao = new TipoLegislacao();
        tipoLegislacao.setId(tipoLegislacaoDTO.getId());
        tipoLegislacao.setDescricao(tipoLegislacaoDTO.getDescricao());
        tipoLegislacao.setOrdemExibicao(tipoLegislacaoDTO.getOrdemExibicao());
        tipoLegislacao.setHabilitarExibicao(tipoLegislacaoDTO.getHabilitarExibicao());
        return tipoLegislacao;
    }

    public TipoLegislacaoDTO toTipoLegislacaoDTO() {
        TipoLegislacaoDTO tipoLegislacaoDTO = new TipoLegislacaoDTO();
        tipoLegislacaoDTO.setId(this.getId());
        tipoLegislacaoDTO.setDescricao(this.getDescricao());
        tipoLegislacaoDTO.setOrdemExibicao(this.getOrdemExibicao());
        tipoLegislacaoDTO.setHabilitarExibicao(this.getHabilitarExibicao());
        return tipoLegislacaoDTO;
    }

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

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
