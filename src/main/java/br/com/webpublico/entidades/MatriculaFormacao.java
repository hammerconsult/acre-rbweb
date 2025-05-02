package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.FormacaoPessoaPortal;
import br.com.webpublico.pessoa.dto.FormacaoPessoaDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by AndreGustavo on 03/10/2014.
 */
@Audited
@Entity
@Etiqueta("Matrícula Formação")
public class MatriculaFormacao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início")
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Término")
    private Date dataFim;
    @Etiqueta("Concluído")
    private Boolean concluido;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @ManyToOne
    @Cascade(CascadeType.ALL)
    @Etiqueta("Formação")
    private Formacao formacao;
    private Boolean instituicaoExistente;
    private String instituicao;

    public MatriculaFormacao() {
        formacao = new Formacao();
        instituicaoExistente = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Boolean getConcluido() {
        return concluido;
    }

    public void setConcluido(Boolean concluido) {
        this.concluido = concluido;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Boolean getInstituicaoExistente() {
        return instituicaoExistente;
    }

    public void setInstituicaoExistente(Boolean instituicaoExistente) {
        this.instituicaoExistente = instituicaoExistente;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public Formacao getFormacao() {
        return formacao;
    }

    public void setFormacao(Formacao formacao) {
        this.formacao = formacao;
    }

    public static List<FormacaoPessoaDTO> toFormacoesDTOs(List<MatriculaFormacao> formacoes) {
        if (formacoes == null) {
            return null;
        }
        List<FormacaoPessoaDTO> dtos = Lists.newLinkedList();
        for (MatriculaFormacao formacao : formacoes) {
            FormacaoPessoaDTO dto = toFormacaoPessoaDTO(formacao);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static FormacaoPessoaDTO toFormacaoPessoaDTO(MatriculaFormacao conselho) {
        if (conselho == null) {
            return null;
        }
        FormacaoPessoaDTO dto = new FormacaoPessoaDTO();
        dto.setId(conselho.getId());
        dto.setDataInicio(conselho.getDataInicio());
        dto.setDataFim(conselho.getDataFim());
        dto.setConcluido(conselho.getConcluido());
        dto.setInstituicaoExistente(conselho.getInstituicaoExistente());
        dto.setInstituicao(conselho.getInstituicao() != null && !conselho.getInstituicao().isEmpty() ? conselho.getInstituicao() : (conselho.getFormacao() != null && conselho.getFormacao().getPromotorEvento() != null ? conselho.getFormacao().getPromotorEvento() + "" : null));
        dto.setFormacao(Formacao.toFormacaoDTO(conselho.getFormacao()));
        return dto;
    }

    public static List<FormacaoPessoaDTO> toFormacoesPortalDTOs(List<FormacaoPessoaPortal> formacoes) {
        if (formacoes == null) {
            return Lists.newArrayList();
        }
        List<FormacaoPessoaDTO> dtos = Lists.newLinkedList();
        for (FormacaoPessoaPortal formacao : formacoes) {
            FormacaoPessoaDTO dto = toFormacaoPessoaPortalDTO(formacao);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static FormacaoPessoaDTO toFormacaoPessoaPortalDTO(FormacaoPessoaPortal conselho) {
        if (conselho == null) {
            return null;
        }
        FormacaoPessoaDTO dto = new FormacaoPessoaDTO();
        dto.setId(conselho.getId());
        dto.setDataInicio(conselho.getDataInicio());
        dto.setDataFim(conselho.getDataFim());
        dto.setConcluido(conselho.getConcluido());
        dto.setInstituicaoExistente(conselho.getInstituicaoExistente());
        dto.setInstituicao(conselho.getInstituicao());
        dto.setFormacao(Formacao.toFormacaoDTO(conselho.getAreaFormacao(), conselho.getNomeCurso(), conselho.getNivelEscolaridade()));
        return dto;
    }

    public static List<MatriculaFormacao> toMatriculasFormacao(PessoaFisica pessoaFisica, List<FormacaoPessoaPortal> formacoesPessoa) {
        List<MatriculaFormacao> formacaos = Lists.newLinkedList();
        for (FormacaoPessoaPortal formacao : formacoesPessoa) {
            MatriculaFormacao dto = toMatriculaFormacao(pessoaFisica, formacao);
            if (dto != null) {
                formacaos.add(dto);
            }
        }
        return formacaos;

    }

    public static MatriculaFormacao toMatriculaFormacao(PessoaFisica pessoaFisica, FormacaoPessoaPortal conselho) {
        MatriculaFormacao dto = new MatriculaFormacao();
        dto.setPessoaFisica(pessoaFisica);
        dto.setDataInicio(conselho.getDataInicio());
        dto.setDataFim(conselho.getDataFim());
        dto.setConcluido(conselho.getConcluido());
        dto.setInstituicaoExistente(conselho.getInstituicaoExistente());
        dto.setInstituicao(conselho.getInstituicao());
        dto.setFormacao(Formacao.toFormacao(conselho));
        return dto;
    }


}

