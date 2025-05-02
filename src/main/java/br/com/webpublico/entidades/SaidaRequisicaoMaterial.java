/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída de Materiais por Requisição")
public class SaidaRequisicaoMaterial extends SaidaMaterial implements Serializable {

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Requisição de Material")
    private RequisicaoMaterial requisicaoMaterial;

    public SaidaRequisicaoMaterial() {
        super();
    }

    public SaidaRequisicaoMaterial(Long id, Long numero, UsuarioSistema usuario, Date dataSaida, Boolean retroativo, Boolean processado, RequisicaoMaterial requisicaoMaterial, String retiradoPor) {
        super(id, numero, usuario, dataSaida, retroativo, processado, retiradoPor);
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    @Override
    public String toString() {
        return "Nrº " + super.getNumero() + " - " + DataUtil.getDataFormatada(super.getDataSaida()) + " - " + getHistorico();
    }

    @Override
    public TipoSaidaMaterial getTipoDestaSaida() {
        try {
            if (requisicaoMaterial.isRequisicaoConsumo()) {
                return TipoSaidaMaterial.CONSUMO;
            }

            if (requisicaoMaterial.isRequisicaoTransferencia()) {
                return TipoSaidaMaterial.TRANSFERENCIA;
            }
        } catch (NullPointerException ex) {
            return TipoSaidaMaterial.CONSUMO;
        }

        throw new ExcecaoNegocioGenerica("Nenhum tipo encontrado.");
    }

    public void diminuirQuantidadeEmTransito() {
        if (isSaidaTransferencia()) {
            BigDecimal acumulado;
            BigDecimal emTransito;

            for (ItemSaidaMaterial ism : getListaDeItemSaidaMaterial()) {
                acumulado = BigDecimal.ZERO;

                acumulado = ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getQuantidadeEmTransito();
                emTransito = acumulado.subtract(ism.getQuantidade());

                ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().setQuantidadeEmTransito(emTransito);
            }
        }
    }

    public void atribuirQuantidadeAtendida() {
        BigDecimal acumulado;
        BigDecimal atendida;

        for (ItemSaidaMaterial ism : getListaDeItemSaidaMaterial()) {
            acumulado = BigDecimal.ZERO;

            atendida = ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getQuantidadeAtendida();
            acumulado = acumulado.add(atendida);
            acumulado = acumulado.add(ism.getQuantidade());

            ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().setQuantidadeAtendida(acumulado);
        }
    }

    public void atribuirQuantidadeEmTransito() {
        if (isSaidaTransferencia()) {
            BigDecimal acumulado;
            BigDecimal emTransito;

            for (ItemSaidaMaterial ism : getListaDeItemSaidaMaterial()) {
                acumulado = BigDecimal.ZERO;

                emTransito = ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getQuantidadeEmTransito();
                acumulado = acumulado.add(emTransito);
                acumulado = acumulado.add(ism.getQuantidade());
                ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().setQuantidadeEmTransito(acumulado);
            }
        }
    }
}
