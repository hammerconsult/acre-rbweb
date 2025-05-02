/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.nfse.domain.perguntasrespostas;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.PerguntasRespostasDTO;
import br.com.webpublico.nfse.enums.TipoPerguntasRespostas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity

@Audited
@GrupoDiagrama(nome = "NFSE")
@Etiqueta("Perguntas e Respostas")
public class PerguntasRespostas extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Etiqueta("Assunto")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private AssuntoNfse assunto;

    @Pesquisavel
    @Etiqueta("Pergunta")
    @Tabelavel
    @Obrigatorio
    private String pergunta;

    @Pesquisavel
    @Etiqueta("Resposta")
    @Tabelavel
    @Obrigatorio
    private String resposta;

    @Pesquisavel
    @Etiqueta("Ordem")
    @Tabelavel
    @Obrigatorio
    private Integer ordem;

    @Pesquisavel
    @Etiqueta("Habilitar Exibição")
    @Tabelavel
    private Boolean habilitarExibicao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Perguntas e Respostas")
    @Obrigatorio
    private TipoPerguntasRespostas tipoPerguntasRespostas;

    public static List<PerguntasRespostasDTO> toListPerguntasRespostasDTO(List<PerguntasRespostas> perguntasRespostas) {
        List<PerguntasRespostasDTO> toReturn = Lists.newArrayList();
        if (perguntasRespostas != null && !perguntasRespostas.isEmpty()) {
            for (PerguntasRespostas legislacao : perguntasRespostas) {
                toReturn.add(legislacao.toPerguntasRespostasDTO());
            }
        }
        return toReturn;
    }

    public static PerguntasRespostas toPerguntasRespostas(PerguntasRespostasDTO perguntasRespostasDTO) {
        PerguntasRespostas perguntasRespostas = new PerguntasRespostas();
        perguntasRespostas.setId(perguntasRespostasDTO.getId());
        perguntasRespostas.setAssunto(AssuntoNfse.toAssuntoNfse(perguntasRespostasDTO.getAssuntoNfseDTO()));
        perguntasRespostas.setPergunta(perguntasRespostasDTO.getPergunta());
        perguntasRespostas.setResposta(perguntasRespostasDTO.getResposta());
        perguntasRespostas.setHabilitarExibicao(perguntasRespostasDTO.getHabilitarExibicao());
        perguntasRespostas.setOrdem(perguntasRespostasDTO.getOrdem());
        return perguntasRespostas;
    }

    public PerguntasRespostasDTO toPerguntasRespostasDTO() {
        PerguntasRespostasDTO perguntasRespostasDTO = new PerguntasRespostasDTO();
        perguntasRespostasDTO.setId(getId());
        perguntasRespostasDTO.setAssuntoNfseDTO(getAssunto().toAssuntoNfseDTO());
        perguntasRespostasDTO.setPergunta(getPergunta());
        perguntasRespostasDTO.setResposta(getResposta());
        perguntasRespostasDTO.setHabilitarExibicao(getHabilitarExibicao());
        perguntasRespostasDTO.setOrdem(getOrdem());
        return perguntasRespostasDTO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssuntoNfse getAssunto() {
        return assunto;
    }

    public void setAssunto(AssuntoNfse assunto) {
        this.assunto = assunto;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
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

    public TipoPerguntasRespostas getTipoPerguntasRespostas() {
        return tipoPerguntasRespostas;
    }

    public void setTipoPerguntasRespostas(TipoPerguntasRespostas tipoPerguntasRespostas) {
        this.tipoPerguntasRespostas = tipoPerguntasRespostas;
    }

    @Override
    public String toString() {
        return id == null ? "Pergunta/Resposta ainda não gravada" : "Pergunta/Resposta código " + id;
    }

}
