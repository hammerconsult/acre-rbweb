package br.com.webpublico.entidades;

import br.com.webpublico.pessoa.dto.AreaFormacaoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * Created by AndreGustavo on 24/09/2014.
 */
@Entity
@Audited
@Etiqueta("Área de Formação")
public class AreaFormacao extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    public AreaFormacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public static AreaFormacaoDTO toAreaFormacaoDTO(AreaFormacao areaFormacao) {
        if (areaFormacao == null) {
            return null;
        }
        AreaFormacaoDTO dto = new AreaFormacaoDTO();
        dto.setId(areaFormacao.getId());
        dto.setCodigo(areaFormacao.getCodigo());
        dto.setDescricao(areaFormacao.getDescricao());
        return dto;
    }

    public static List<AreaFormacaoDTO> toAreasFormacoesDTO(List<AreaFormacao> lista) {
        if (lista == null) {
            return null;
        }
        List<AreaFormacaoDTO> dtos = Lists.newLinkedList();
        for (AreaFormacao areaFormacao : lista) {
            AreaFormacaoDTO dto = toAreaFormacaoDTO(areaFormacao);
            if (dto != null) {
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public static AreaFormacao dtoToAreaFormacao(AreaFormacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        AreaFormacao a = new AreaFormacao();
        a.setId(dto.getId());
        return a;
    }
}
