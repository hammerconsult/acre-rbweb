/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author renato
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Avaliação de Material")
public class AprovacaoMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Long numero;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data da Aprovação")
    private Date dataDaAprovacao;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Aprovador da Requisição")
    private UsuarioSistema aprovador;

    @OneToOne
    @Obrigatorio
    @Etiqueta("Requisição de Material")
    private RequisicaoMaterial requisicaoMaterial;

    @Etiqueta("Itens Aprovados")
    @OneToMany(mappedBy = "aprovacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAprovacaoMaterial> itensAprovados;

    public AprovacaoMaterial() {
    }

    public UsuarioSistema getAprovador() {
        return aprovador;
    }

    public void setAprovador(UsuarioSistema aprovador) {
        this.aprovador = aprovador;
    }

    public Date getDataDaAprovacao() {
        return dataDaAprovacao;
    }

    public void setDataDaAprovacao(Date dataDaAprovacao) {
        this.dataDaAprovacao = dataDaAprovacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemAprovacaoMaterial> getItensAprovados() {
        return itensAprovados;
    }

    public void setItensAprovados(List<ItemAprovacaoMaterial> itensAprovados) {
        this.itensAprovados = itensAprovados;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Nrº " + this.numero + " - " + DataUtil.getDataFormatada(dataDaAprovacao) + " - " + requisicaoMaterial.getDescricao();
    }

    public void atribuirTipoSituacaoDaRequisicao() {
        requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.APROVADA_TOTALMENTE_E_NAO_ATENDIDA);
        int rejeitados = 0;

        for (ItemAprovacaoMaterial itemAprovacaoMaterial : itensAprovados) {
            if (itemAprovacaoMaterial.isAprovadoParcialmente()) {
                requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.APROVADA_PARCIALMENTE_E_NAO_ATENDIDA);
                return;
            }

            if (itemAprovacaoMaterial.isRejeitado()) {
                rejeitados++;
            }
        }

        if (rejeitados == itensAprovados.size()) {
            requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.REJEITADA);
        } else if (rejeitados > 0) {
            requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.APROVADA_PARCIALMENTE_E_NAO_ATENDIDA);
        }
    }

    public void zerarQuantidadeAprovadaDosItensRequisicaoMaterial() {
        for (ItemAprovacaoMaterial iam : itensAprovados) {
            requisicaoMaterial.setQuantidadeAprovadaEMaterialAprovadoDoItem(BigDecimal.ZERO, null, iam.getItemRequisicaoMaterial());
        }
    }

    public List<ItemAprovacaoMaterial> getMateriaisAprovados() {
        List<ItemAprovacaoMaterial> retorno = new ArrayList<>();

        for (ItemAprovacaoMaterial iam : itensAprovados) {
            if (!iam.isRejeitado()) {
                retorno.add(iam);
            }
        }

        return retorno;
    }

    public Map<Material, BigDecimal> getMapaDeItensAprovadosConsolidandoQuantidade() {
        Map<Material, BigDecimal> materiais = new HashMap<>();

        for (ItemAprovacaoMaterial iam : itensAprovados) {
            if (!iam.isRejeitado() && !iam.getItemRequisicaoMaterial().getQuantidade().equals(iam.getItemRequisicaoMaterial().getQuantidadeAtendida())) {
                BigDecimal quantidade = materiais.get(iam.getMaterial());
                materiais.put(iam.getMaterial(), quantidade != null ? quantidade.add(iam.getQuantidadeAprovada()) : iam.getQuantidadeAprovada());
            }
        }
        return materiais;
    }
}
