/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 *
 * @author Fabio
 */
@Etiqueta(value = "Lotação do Usuário do Tributário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
public class LotacaoTribUsuario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VigenciaTribUsuario vigenciaTribUsuario;
    @ManyToOne
    private LotacaoVistoriadora lotacao;

    public LotacaoTribUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotacaoVistoriadora getLotacao() {
        return lotacao;
    }

    public void setLotacao(LotacaoVistoriadora lotacao) {
        this.lotacao = lotacao;
    }

    public VigenciaTribUsuario getVigenciaTribUsuario() {
        return vigenciaTribUsuario;
    }

    public void setVigenciaTribUsuario(VigenciaTribUsuario vigenciaTribUsuario) {
        this.vigenciaTribUsuario = vigenciaTribUsuario;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.LotacaoTribUsuario[ id=" + id + " ]";
    }

}
