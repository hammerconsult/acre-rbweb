package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Estorno de Empenho Lote")
public class EmpenhoEstornoLote extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Lote")
    private Date dataLote;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade")
    private UnidadeOrganizacional unidadeOrganizacional;
    private String erros;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empenhoEstornoLote", orphanRemoval = true)
    private List<EmpenhoEstornoLoteEmpenhoEstorno> empenhosEstornos;

    public EmpenhoEstornoLote() {
        empenhosEstornos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataLote() {
        return dataLote;
    }

    public void setDataLote(Date dataLote) {
        this.dataLote = dataLote;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getErros() {
        return erros;
    }

    public void setErros(String erros) {
        this.erros = erros;
    }

    public List<EmpenhoEstornoLoteEmpenhoEstorno> getEmpenhosEstornos() {
        return empenhosEstornos;
    }

    public void setEmpenhosEstornos(List<EmpenhoEstornoLoteEmpenhoEstorno> empenhosEstornos) {
        this.empenhosEstornos = empenhosEstornos;
    }

    public BigDecimal getValorTotalEstornos() {
        BigDecimal retorno = BigDecimal.ZERO;
        if (empenhosEstornos != null && !empenhosEstornos.isEmpty()) {
            for (EmpenhoEstornoLoteEmpenhoEstorno esl : empenhosEstornos) {
                retorno = retorno.add(esl.getEmpenhoEstorno().getValor());
            }
        }
        return retorno;
    }
}
