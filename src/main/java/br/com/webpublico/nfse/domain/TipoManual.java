package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.TipoAnexoTipoManualDTO;
import br.com.webpublico.nfse.domain.dtos.TipoManualDTO;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by william on 24/08/17.
 */
@Entity
@Audited
@Etiqueta("Tipo de Manual")
public class TipoManual extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Length(minimo = 3, maximo = 255)
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Obrigatorio
    @Etiqueta("Ordem de exibição")
    @Positivo(permiteZero = false)
    @Tabelavel
    @Pesquisavel
    private Integer ordem;
    @Tabelavel
    @Etiqueta("Habilitar Exibição?")
    private Boolean habilitarExibicao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoAnexo tipoAnexo;

    public TipoManual() {
    }

    public static List<TipoManualDTO> toListTipoManualDTO(List<TipoManual> tipos) {
        List<TipoManualDTO> retorno = Lists.newArrayList();
        if (tipos != null && !tipos.isEmpty()) {
            for (TipoManual tipo : tipos) {
                retorno.add(tipo.toTipoManualDTO());
            }
        }
        return retorno;
    }

    public static TipoManual toTipoManual(TipoManualDTO tipoManualDTO) {
        TipoManual tipoManual = new TipoManual();
        tipoManual.setId(tipoManualDTO.getId());
        tipoManual.setDescricao(tipoManualDTO.getDescricao());
        tipoManual.setHabilitarExibicao(tipoManualDTO.getHabilitarExibicao());
        tipoManual.setOrdem(tipoManualDTO.getOrdem());
        tipoManual.setTipoAnexo(TipoAnexo.fromDto(tipoManualDTO.getTipoAnexo()));
        return tipoManual;
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

    public TipoAnexo getTipoAnexo() {
        return tipoAnexo;
    }

    public void setTipoAnexo(TipoAnexo tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

    public TipoManualDTO toTipoManualDTO() {
        TipoManualDTO tipoManualDTO = new TipoManualDTO();
        tipoManualDTO.setId(this.getId());
        tipoManualDTO.setDescricao(this.getDescricao());
        tipoManualDTO.setHabilitarExibicao(this.getHabilitarExibicao());
        tipoManualDTO.setOrdem(this.getOrdem());
        tipoManualDTO.setTipoAnexo(this.getTipoAnexo().toDto());
        return tipoManualDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public enum TipoAnexo implements NfseEnum, EnumComDescricao {
        ARQUIVO("Arquivo", TipoAnexoTipoManualDTO.ARQUIVO), VIDEO("Vídeo", TipoAnexoTipoManualDTO.VIDEO);

        private String descricao;
        private TipoAnexoTipoManualDTO dto;

        TipoAnexo(String descricao, TipoAnexoTipoManualDTO dto) {
            this.descricao = descricao;
            this.dto = dto;
        }

        public static TipoAnexo fromDto(TipoAnexoTipoManualDTO dto) {
            for (TipoAnexo value : values()) {
                if(value.toDto().equals(dto)){
                    return value;
                }
            }
            return null;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public TipoAnexoTipoManualDTO toDto() {
            return dto;
        }
    }
}
