package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.FormacaoPessoaPortal;
import br.com.webpublico.pessoa.dto.FormacaoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by AndreGustavo on 23/09/2014.
 */
@Entity
@Audited
@Etiqueta("Formação")
public class Formacao extends EventoDeRH {
    @Etiqueta("Nível de Escolaridade")
    @ManyToOne
    private NivelEscolaridade nivelEscolaridade;
    @ManyToOne
    private AreaFormacao areaFormacao;

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public AreaFormacao getAreaFormacao() {
        return areaFormacao;
    }

    public void setAreaFormacao(AreaFormacao areaFormacao) {
        this.areaFormacao = areaFormacao;
    }

    public static FormacaoDTO toFormacaoDTO(Formacao formacao) {
        if (formacao == null) {
           return new FormacaoDTO();
        }
        FormacaoDTO dto = new FormacaoDTO();
        dto.setId(formacao.getId());
        dto.setNivelEscolaridade(NivelEscolaridade.toNivelEscolaridadeDTO(formacao.getNivelEscolaridade()));
        dto.setNomeCurso(formacao.getNome());
        dto.setAreaFormacao(AreaFormacao.toAreaFormacaoDTO(formacao.getAreaFormacao()));
        return dto;
    }

    public static List<FormacaoDTO> toFormacoesDTO(List<Formacao> lista) {
        if (lista == null) {
            return null;
        }
        List<FormacaoDTO> dtos = Lists.newLinkedList();
        for (Formacao formacao : lista) {
            FormacaoDTO dto = toFormacaoDTO(formacao);
            if (dto != null) {
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public static Formacao toFormacao(FormacaoPessoaPortal conselho) {
        if (conselho != null && conselho.getNomeCurso() != null) {
            Formacao f = new Formacao();
            f.setAreaFormacao(conselho.getAreaFormacao());
            f.setNivelEscolaridade(conselho.getNivelEscolaridade());
            f.setNome(conselho.getNomeCurso());
            return f;
        }
        return null;
    }

    public static FormacaoDTO toFormacaoDTO(AreaFormacao areaFormacao, String nomeCurso, NivelEscolaridade nivelEscolaridade) {
        Formacao f = new Formacao();
        f.setAreaFormacao(areaFormacao);
        f.setNome(nomeCurso);
        f.setNivelEscolaridade(nivelEscolaridade);
        FormacaoDTO dto = toFormacaoDTO(f);
        return dto;
    }
}
