/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.CorDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta(value = "Cor",
        genero = "F")
public class Cor extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    @Obrigatorio
    private String descricao;

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


    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public void realizarValidacoes() throws ValidacaoException {
        ValidacaoException exception = new ValidacaoException();

        try {
            super.realizarValidacoes();
        } catch (ValidacaoException ex) {
            exception = ex;
        }


        if (exception.temMensagens()) {
            throw exception;
        }
    }

    public CorDTO toDTO() {
        CorDTO dto = new CorDTO();
        dto.setId(this.id);
        dto.setDescricao(this.descricao);
        return dto;
    }
}
