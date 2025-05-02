package br.com.webpublico.entidades.rh.esocial;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "TIPOBENEFICIOESOCIAL")
@Entity
@Audited
@Etiqueta("Tabela 25 - Tipos de Benefícios")
public class TipoBeneficioEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Benefício")
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Grupo Benefício")
    @Enumerated(EnumType.STRING)
    private GrupoBeneficioEsocial grupoBeneficio;

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public GrupoBeneficioEsocial getGrupoBeneficio() {
        return grupoBeneficio;
    }

    public void setGrupoBeneficio(GrupoBeneficioEsocial grupoBeneficio) {
        this.grupoBeneficio = grupoBeneficio;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " - " + grupoBeneficio;
    }
}
