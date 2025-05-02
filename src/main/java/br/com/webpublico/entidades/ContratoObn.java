package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by israeleriston on 19/05/16.
 */
@Entity
@Audited
@Etiqueta("Contrato Obn")
public class ContratoObn extends SuperEntidade implements Serializable {

    public static final Long serialVersionUID = 1l;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número Contrato")
    @Obrigatorio
    private String numeroContrato;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número Header")
    private String numeroHeaderObn600;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Complemento Nome Arquivo")
    private String complementoNomeArquivo;

    @ManyToOne
    private BancoObn bancoObn;


    @OneToMany(mappedBy = "contratoObn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeObn> unidades;

    public ContratoObn(String numeroContrato, BancoObn bancoObn, List<UnidadeObn> unidades) {
        this.numeroContrato = numeroContrato;
        this.bancoObn = new BancoObn();
        this.unidades = Lists.newArrayList();
    }

    public ContratoObn() {
        this.unidades = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public BancoObn getBancoObn() {
        return bancoObn;
    }

    public void setBancoObn(BancoObn bancoObn) {
        this.bancoObn = bancoObn;
    }

    public List<UnidadeObn> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeObn> unidades) {
        this.unidades = unidades;
    }

    public String getNumeroHeaderObn600() {
        return numeroHeaderObn600;
    }

    public void setNumeroHeaderObn600(String numeroHeaderObn600) {
        this.numeroHeaderObn600 = numeroHeaderObn600;
    }

    public String getComplementoNomeArquivo() {
        return complementoNomeArquivo;
    }

    public void setComplementoNomeArquivo(String complementoNomeArquivo) {
        this.complementoNomeArquivo = complementoNomeArquivo;
    }

    @Override
    public String toString() {
        return numeroContrato;
    }
}
