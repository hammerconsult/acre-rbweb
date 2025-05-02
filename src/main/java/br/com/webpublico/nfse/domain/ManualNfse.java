package br.com.webpublico.nfse.domain;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.ManualDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by william on 29/08/17.
 */
@Entity
@Audited
@Etiqueta("Manual")
public class ManualNfse extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Manual")
    @Tabelavel
    @Pesquisavel
    private TipoManual tipoManual;
    @Etiqueta("Nome do Manual")
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    @Tabelavel
    @Pesquisavel
    private String nome;
    @Etiqueta("Resumo do Manual")
    @Obrigatorio
    @Length(minimo = 3, maximo = 3000)
    private String resumo;
    private String link;
    private String tags;
    @Etiqueta("Ordem de Exibição")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer ordem;
    private Boolean habilitarExibicao;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ManualNfse() {
    }

    public static ManualNfse toManual(ManualDTO manualDTO) {
        ManualNfse manualNfse = new ManualNfse();
        manualNfse.setId(manualDTO.getId());
        manualNfse.setNome(manualDTO.getNome());
        manualNfse.setResumo(manualDTO.getResumo());
        manualNfse.setTipoManual(TipoManual.toTipoManual(manualDTO.getTipoManualDTO()));
        manualNfse.setHabilitarExibicao(manualDTO.getHabilitarExibicao());
        manualNfse.setOrdem(manualDTO.getOrdem());
        manualNfse.setLink(manualDTO.getLink());
        return manualNfse;
    }

    public static List<ManualDTO> toListManualNfseDTO(List<ManualNfse> manuais) {
        List<ManualDTO> retorno = Lists.newArrayList();
        if (manuais != null && !manuais.isEmpty()) {
            for (ManualNfse manuai : manuais) {
                retorno.add(manuai.toManualDTO());
            }
        }
        return retorno;
    }

    public static List<ManualDTO> toListManualSimpleNfseDTO(List<ManualNfse> manuais) {
        List<ManualDTO> retorno = Lists.newArrayList();
        if (manuais != null && !manuais.isEmpty()) {
            for (ManualNfse manuai : manuais) {
                retorno.add(manuai.toSimplelDTO());
            }
        }
        return retorno;
    }

    public ManualDTO toManualDTO() {
        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setId(this.getId());
        manualDTO.setNome(this.getNome());
        manualDTO.setResumo(this.getResumo());
        manualDTO.setTipoManualDTO(this.getTipoManual().toTipoManualDTO());
        manualDTO.setHabilitarExibicao(this.getHabilitarExibicao());
        manualDTO.setOrdem(this.getOrdem());
        manualDTO.setLink(this.getLink());
        if (this.getDetentorArquivoComposicao() != null) {
            manualDTO.setArquivoDTO(this.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return manualDTO;
    }

    public ManualDTO toSimplelDTO() {
        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setId(this.getId());
        manualDTO.setNome(this.getNome());
        manualDTO.setTipoManualDTO(this.getTipoManual().toTipoManualDTO());
        manualDTO.setOrdem(this.getOrdem());
        manualDTO.setLink(this.getLink());
        if (this.getDetentorArquivoComposicao() != null) {
            ArquivoDTO arquivoDTO = new ArquivoDTO();
            arquivoDTO.setId(this.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getId());
            manualDTO.setArquivoDTO(arquivoDTO);
        }
        return manualDTO;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoManual getTipoManual() {
        return tipoManual;
    }

    public void setTipoManual(TipoManual tipoManual) {
        this.tipoManual = tipoManual;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
