/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representa um Lote de determinado Material. Lotes são utilizados para controlar a validade
 * de determinadas mercadorias, e também servem para identificar materiais defeituosos em
 * eventuais reclamações junto aos fornecedores.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Materiais")
@Audited
@Etiqueta("Lote de Material")
@Entity
public class LoteMaterial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Identificação")
    private String identificacao;

    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Validade")
    private Date validade;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Material")
    private Material material;

    public LoteMaterial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    @Override
    public String toString() {
        return identificacao + " Validade: " + new SimpleDateFormat("dd/MM/yyyy").format(this.validade);
    }

    public boolean vencidoNaData(Date data) {
        return DataUtil.dataSemHorario(validade).before(DataUtil.dataSemHorario(data));
    }
}
