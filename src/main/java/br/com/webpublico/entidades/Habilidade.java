package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.HabilidadePortal;
import br.com.webpublico.pessoa.dto.HabilidadeDTO;
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
 * Created by AndreGustavo on 23/09/2014.
 */
@Entity
@Audited
@Etiqueta("Habilidade")
public class Habilidade extends SuperEntidade implements Serializable {
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

    public Habilidade() {
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
        return descricao;
    }

    public static HabilidadeDTO toHabilidadeDTO(Habilidade habilidade) {
        if (habilidade == null) {
            return null;
        }
        HabilidadeDTO dto = new HabilidadeDTO();
        dto.setId(habilidade.getId());
        dto.setCodigo(habilidade.getCodigo());
        dto.setDescricao(habilidade.getDescricao());
        return dto;
    }

    public static List<HabilidadeDTO> toHabilidadesDTO(List<Habilidade> habilidades) {
        if (habilidades == null) {
            return null;
        }
        List<HabilidadeDTO> dtos = Lists.newLinkedList();
        for (Habilidade habilidade : habilidades) {
            HabilidadeDTO dto = toHabilidadeDTO(habilidade);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }


    public static Habilidade toHabilidade(HabilidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        Habilidade h = new Habilidade();
        h.setId(dto.getId());
        return h;
    }

    public static PessoaHabilidade toHabilidade(HabilidadePortal dto) {
        if (dto == null && dto.getHabilidade() == null) {
            return null;
        }
        PessoaHabilidade h = new PessoaHabilidade();
        h.setHabilidade(dto.getHabilidade());
        return h;
    }

    public static List<PessoaHabilidade> toHabilidades(PessoaFisica pessoaFisica, List<HabilidadePortal> habilidades) {
        List<PessoaHabilidade> habilidadesPersistentes = Lists.newLinkedList();
        for (HabilidadePortal habilidade : habilidades) {
            if (habilidade.getHabilidade() != null) {
                PessoaHabilidade h = toHabilidade(habilidade);
                if (h != null) {
                    h.setPessoaFisica(pessoaFisica);
                    habilidadesPersistentes.add(h);
                }
            }
        }
        return habilidadesPersistentes;
    }
}
