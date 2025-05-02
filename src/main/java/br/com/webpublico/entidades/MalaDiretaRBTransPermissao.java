package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Wellington on 22/12/2016.
 */
@Entity
@Audited
@Etiqueta("Mala Direta de RBtrans - Permissão")
public class MalaDiretaRBTransPermissao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Mala Direta de RBtrans")
    @ManyToOne
    private MalaDiretaRBTrans malaDiretaRBTrans;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Permissão de Transporte")
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @OneToMany(mappedBy = "malaDiretaRBTransPermissao")
    private List<MalaDiretaRBTransParcela> parcelas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MalaDiretaRBTrans getMalaDiretaRBTrans() {
        return malaDiretaRBTrans;
    }

    public void setMalaDiretaRBTrans(MalaDiretaRBTrans malaDiretaRBTrans) {
        this.malaDiretaRBTrans = malaDiretaRBTrans;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public List<MalaDiretaRBTransParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<MalaDiretaRBTransParcela> parcelas) {
        this.parcelas = parcelas;
    }

}
