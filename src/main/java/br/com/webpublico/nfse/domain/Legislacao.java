package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.LegislacaoDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by wellington on 29/08/17.
 */
@GrupoDiagrama(nome = "Tributário")
@Audited
@Entity
@Etiqueta("Legislação")
public class Legislacao extends SuperEntidade implements PossuidorArquivo {

    private static Logger logger = LoggerFactory.getLogger(Legislacao.class);
    @Id
    @GeneratedValue
    private Long id;
    @Etiqueta("Tipo de Legislação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private TipoLegislacao tipoLegislacao;
    @Etiqueta("Nome da Legislação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    private String nome;
    @Etiqueta("Súmula")
    @Obrigatorio
    @Length(minimo = 3, maximo = 3000)
    private String sumula;
    @Etiqueta("Data da Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Etiqueta("Ordem de Exibição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer ordemExibicao;
    @Etiqueta("Habilitar Exibição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Boolean habilitarExibicao;

    public static List<LegislacaoDTO> toListLegislacaoDTO(List<Legislacao> legislacoes) {
        List<LegislacaoDTO> toReturn = Lists.newArrayList();
        if (legislacoes != null && !legislacoes.isEmpty()) {
            for (Legislacao legislacao : legislacoes) {
                toReturn.add(legislacao.toLegislacaoDTO());
            }
        }
        return toReturn;
    }

    public static Legislacao toLegislacao(LegislacaoDTO legislacaoDTO) {
        Legislacao legislacao = new Legislacao();
        legislacao.setId(legislacaoDTO.getId());
        legislacao.setTipoLegislacao(TipoLegislacao.toTipoLegislacao(legislacaoDTO.getTipoLegislacaoDTO()));
        legislacao.setNome(legislacaoDTO.getNome());
        legislacao.setSumula(legislacaoDTO.getSumula());
        legislacao.setDataPublicacao(legislacaoDTO.getDataPublicacao());
        legislacao.setOrdemExibicao(legislacaoDTO.getOrdemExibicao());
        legislacao.setHabilitarExibicao(legislacaoDTO.getHabilitarExibicao());
        if (legislacaoDTO.getArquivoDTO() != null) {
            if (legislacao.getDetentorArquivoComposicao() != null) {
                if (legislacao.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
                    legislacao.getDetentorArquivoComposicao().getArquivoComposicao().setArquivo(Arquivo.toArquivo(legislacaoDTO.getArquivoDTO()));
                }
            }
        }
        return legislacao;
    }

    public LegislacaoDTO toLegislacaoDTO() {
        LegislacaoDTO legislacaoDTO = new LegislacaoDTO();
        legislacaoDTO.setId(this.getId());
        legislacaoDTO.setTipoLegislacaoDTO(this.getTipoLegislacao().toTipoLegislacaoDTO());
        legislacaoDTO.setNome(this.getNome());
        legislacaoDTO.setSumula(this.getSumula());
        legislacaoDTO.setDataPublicacao(this.getDataPublicacao());
        legislacaoDTO.setOrdemExibicao(this.getOrdemExibicao());
        legislacaoDTO.setHabilitarExibicao(this.getHabilitarExibicao());
        if (this.getDetentorArquivoComposicao() != null) {
            legislacaoDTO.setArquivoDTO(this.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return legislacaoDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoLegislacao getTipoLegislacao() {
        return tipoLegislacao;
    }

    public void setTipoLegislacao(TipoLegislacao tipoLegislacao) {
        this.tipoLegislacao = tipoLegislacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSumula() {
        return sumula;
    }

    public void setSumula(String sumula) {
        this.sumula = sumula;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
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
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return this.detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
