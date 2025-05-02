/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoInventarioEstoque;
import br.com.webpublico.enums.TipoInventario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Um InventarioEstoque é realizado para constatar a quantidade de mercadorias existentes e,
 * a partir disso, gerar os ajustes necessários nos saldos em estoque.
 *
 * @author arthur
 */
@Etiqueta("Inventário de Estoque")
public class InventarioEstoque extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Momento no qual o inventário foi iniciado. A partir deste momento
     * o LocalEstoque vinculado, bem como todos os seus sub-locais, não poderão mais
     * receber lançamentos.
     */
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Iniciado em")
    @Obrigatorio
    private Date iniciadoEm;
    /**
     * Horário de conclusão do inventário.
     */
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Encerrado em")
    private Date encerradoEm;
    /**
     * LocalEstoque no qual o inventário será realizado (com todos os sub-locais).
     */
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Local de Estoque")
    @Obrigatorio
    private LocalEstoque localEstoque;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Inventário")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoInventario tipoInventario;

    @Invisivel
    @Etiqueta("Itens Inventário Estoque")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventarioEstoque", orphanRemoval = true)
    private List<ItemInventarioEstoque> itensInventarioEstoque;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário")
    @OneToOne
    @Obrigatorio
    private UsuarioSistema usuarioSistema;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private SituacaoInventarioEstoque situacaoInventario;

    public InventarioEstoque() {
        situacaoInventario = SituacaoInventarioEstoque.ABERTO;
        itensInventarioEstoque = Lists.newArrayList();
    }

    public SituacaoInventarioEstoque getSituacaoInventario() {
        return situacaoInventario;
    }

    public void setSituacaoInventario(SituacaoInventarioEstoque situacaoInventario) {
        this.situacaoInventario = situacaoInventario;
    }

    public List<ItemInventarioEstoque> getItensInventarioEstoque() {
        return itensInventarioEstoque;
    }

    public void setItensInventarioEstoque(List<ItemInventarioEstoque> itensInventarioEstoque) {
        this.itensInventarioEstoque = itensInventarioEstoque;
    }

    public Date getEncerradoEm() {
        return encerradoEm;
    }

    public void setEncerradoEm(Date encerradoEm) {
        this.encerradoEm = encerradoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getIniciadoEm() {
        return iniciadoEm;
    }

    public void setIniciadoEm(Date iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public TipoInventario getTipoInventario() {
        return tipoInventario;
    }

    public void setTipoInventario(TipoInventario tipoInventario) {
        this.tipoInventario = tipoInventario;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public boolean isInventarioGeral() {
        return TipoInventario.GERAL.equals(this.tipoInventario);
    }

    public boolean isInventarioAmostragem() {
        return TipoInventario.AMOSTRAGEM.equals(this.tipoInventario);
    }

    @Override
    public String toString() {
        try {
            return iniciadoEm + " - " + localEstoque + " - " + tipoInventario;
        } catch (Exception ex) {
            return "";
        }
    }

    public boolean bloqueadoParaEdicao() {
        if (this.encerradoEm == null) {
            return false;
        }
        return new Date().compareTo(this.encerradoEm) > 0;
    }

    public Set<Material> materialSet() {
        Set<Material> retorno = new HashSet<>();
        if (itensInventarioEstoque != null && !itensInventarioEstoque.isEmpty()) {
            for (ItemInventarioEstoque itemInventarioEstoque : itensInventarioEstoque) {
                retorno.add(itemInventarioEstoque.getMaterial());
            }
        }
        return retorno;
    }

    public void validarMesmoObjetoEmLista(ItemInventarioEstoque item) {
        ValidacaoException ve = new ValidacaoException();
        if (itensInventarioEstoque != null && !itensInventarioEstoque.isEmpty()) {
            for (ItemInventarioEstoque itemInventarioEstoque : getItensInventarioEstoque()) {
                if (itemInventarioEstoque.getMaterial().equals(item.getMaterial())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O material: " + item.getMaterial() + " já foi adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public boolean isInventarioAberto() {
        return this.situacaoInventario != null && SituacaoInventarioEstoque.ABERTO.equals(this.situacaoInventario);
    }

    public boolean isInventarioConcluido() {
        return this.situacaoInventario != null && SituacaoInventarioEstoque.CONCLUIDO.equals(this.situacaoInventario);
    }

    public boolean isInventarioFinalizado() {
        return this.situacaoInventario != null && SituacaoInventarioEstoque.FINALIZADO.equals(this.situacaoInventario);
    }
}
