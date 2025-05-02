package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 03/09/14
 * Time: 08:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Detentor de Origem de Recurso", genero = "M")
public class DetentorOrigemRecurso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "detentorOrigemRecurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrigemRecursoBem> listaDeOriemRecursoBem;

    public DetentorOrigemRecurso() {
        this.listaDeOriemRecursoBem = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrigemRecursoBem> getListaDeOriemRecursoBem() {
        return listaDeOriemRecursoBem;
    }

    public void setListaDeOriemRecursoBem(List<OrigemRecursoBem> listaDeOriemRecursoBem) {
        this.listaDeOriemRecursoBem = listaDeOriemRecursoBem;
    }

    public boolean temRecursos() {
        return !listaDeOriemRecursoBem.isEmpty();
    }

    public void adicionarOrigemRecurso(OrigemRecursoBem origemRecursoBem) throws ValidacaoException{
        validarAdicao(origemRecursoBem);
        origemRecursoBem.setDetentorOrigemRecurso(this);
        listaDeOriemRecursoBem.add(origemRecursoBem);
    }

    public void removerOrigemRecurso(OrigemRecursoBem origemRecursoBem) {
        listaDeOriemRecursoBem.remove(origemRecursoBem);
    }

    private void validarAdicao(OrigemRecursoBem origemRecursoBem) throws ValidacaoException{
        origemRecursoBem.validarCamposObrigatorios();

        for (OrigemRecursoBem origem : this.getListaDeOriemRecursoBem()) {
            if (origem.getTipoRecursoAquisicaoBem().equals(origemRecursoBem.getTipoRecursoAquisicaoBem())) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Adicione origens de tipos diferentes.");
                throw ve;
            }
        }
    }
}
