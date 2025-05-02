package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.AreaFormacao;
import br.com.webpublico.entidades.NivelEscolaridade;
import br.com.webpublico.pessoa.dto.FormacaoPessoaDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class FormacaoPessoaPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    private Date dataInicio;
    private Date dataFim;
    private Boolean concluido;
    private String nomeCurso;
    @ManyToOne
    private NivelEscolaridade nivelEscolaridade;
    @ManyToOne
    private AreaFormacao areaFormacao;
    private Boolean instituicaoExistente;
    private String instituicao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
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

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

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

    public static List<FormacaoPessoaPortal> dtoToFormacoesPortal(List<FormacaoPessoaDTO> dtos, PessoaFisicaPortal pessoa) {
        List<FormacaoPessoaPortal> formacoes = Lists.newLinkedList();
        if (dtos != null) {
            for (FormacaoPessoaDTO dto : dtos) {
                FormacaoPessoaPortal f = dtoToFormacaoPortal(dto, pessoa);
                if (f != null) {
                    formacoes.add(f);
                }
            }
        }
        return formacoes;
    }

    public static FormacaoPessoaPortal dtoToFormacaoPortal(FormacaoPessoaDTO dto, PessoaFisicaPortal pessoa) {
        FormacaoPessoaPortal formacao = new FormacaoPessoaPortal();
        formacao.setAreaFormacao(AreaFormacao.dtoToAreaFormacao(dto.getFormacao().getAreaFormacao()));
        formacao.setConcluido(dto.getConcluido());
        formacao.setDataFim(dto.getDataFim());
        formacao.setDataInicio(dto.getDataInicio());
        formacao.setInstituicao(dto.getInstituicao());
        formacao.setNomeCurso(dto.getFormacao() != null ? dto.getFormacao().getNomeCurso() : null);
        formacao.setInstituicaoExistente(dto.getInstituicaoExistente());
        formacao.setNivelEscolaridade(dto.getFormacao() != null && dto.getFormacao().getNivelEscolaridade() != null ? NivelEscolaridade.dtoToNivelEscolaridade(dto.getFormacao().getNivelEscolaridade()) : null);
        formacao.setPessoaFisicaPortal(pessoa);
        return formacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormacaoPessoaPortal that = (FormacaoPessoaPortal) o;
        return Objects.equals(DataUtil.getDataFormatada(dataInicio), DataUtil.getDataFormatada(that.dataInicio))
            && Objects.equals(DataUtil.getDataFormatada(dataFim), DataUtil.getDataFormatada(that.dataFim))
            && Objects.equals(concluido, that.concluido)
            && Objects.equals(nomeCurso != null ? nomeCurso.toUpperCase().trim() : null, that.nomeCurso != null ? that.nomeCurso.toUpperCase().trim() : null)
            && Objects.equals(areaFormacao, that.areaFormacao)
            && Objects.equals(instituicao != null ? instituicao.toUpperCase().trim() : null, that.instituicao != null ? that.instituicao.toUpperCase().trim() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataInicio, dataFim, concluido, nomeCurso, areaFormacao, instituicao);
    }
}
