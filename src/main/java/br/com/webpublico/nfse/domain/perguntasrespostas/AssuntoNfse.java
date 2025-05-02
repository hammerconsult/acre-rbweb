/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.nfse.domain.perguntasrespostas;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.AssuntoNfseDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
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


@Entity

@Audited
@GrupoDiagrama(nome = "NFSE")
@Etiqueta("Assunto")
public class AssuntoNfse extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    @Obrigatorio
    private String descricao;

    @Pesquisavel
    @Etiqueta("Ordem")
    @Tabelavel
    @Obrigatorio
    private Integer ordem;

    @Pesquisavel
    @Etiqueta("Habilitar Exibição")
    @Tabelavel
    private Boolean habilitarExibicao;


    public static List<AssuntoNfseDTO> toListAssuntoNfseDTO(List<AssuntoNfse> assuntosNfse) {
        List<AssuntoNfseDTO> toReturn = Lists.newArrayList();
        if (assuntosNfse != null && !assuntosNfse.isEmpty()) {
            for (AssuntoNfse assuntoNfse : assuntosNfse) {
                toReturn.add(assuntoNfse.toAssuntoNfseDTO());
            }
        }
        return toReturn;
    }

    public static AssuntoNfse toAssuntoNfse(AssuntoNfseDTO assuntoNfseDTO) {
        AssuntoNfse assuntoNfse = new AssuntoNfse();
        assuntoNfse.setId(assuntoNfseDTO.getId());
        assuntoNfse.setDescricao(assuntoNfseDTO.getDescricao());
        assuntoNfse.setOrdem(assuntoNfseDTO.getOrdem());
        assuntoNfse.setHabilitarExibicao(assuntoNfseDTO.getHabilitarExibicao());
        return assuntoNfse;
    }

    public AssuntoNfseDTO toAssuntoNfseDTO() {
        AssuntoNfseDTO assuntoNfseDTO = new AssuntoNfseDTO();
        assuntoNfseDTO.setId(getId());
        assuntoNfseDTO.setDescricao(getDescricao());
        assuntoNfseDTO.setOrdem(getOrdem());
        assuntoNfseDTO.setHabilitarExibicao(getHabilitarExibicao());
        return assuntoNfseDTO;
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

    @Override
    public String toString() {
        return id == null ? "Assunto ainda não gravado" : ordem + " - " + descricao;
    }

}
