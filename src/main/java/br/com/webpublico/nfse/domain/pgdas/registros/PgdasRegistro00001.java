package br.com.webpublico.nfse.domain.pgdas.registros;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.RegistroLinhaPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Processo para não Optante")
public class PgdasRegistro00001 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("00001")
    private String reg;
    @Etiqueta("Esfera Administrativa")
    private String admTrib;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("Descrição do Município")
    private String munic;
    @Etiqueta("Código do Município")
    private String codMunic;
    @Etiqueta("Processo")
    private String numProc;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro00001() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getAdmTrib() {
        return admTrib;
    }

    public void setAdmTrib(String admTrib) {
        this.admTrib = admTrib;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunic() {
        return munic;
    }

    public void setMunic(String munic) {
        this.munic = munic;
    }

    public String getCodMunic() {
        return codMunic;
    }

    public void setCodMunic(String codMunic) {
        this.codMunic = codMunic;
    }

    public String getNumProc() {
        return numProc;
    }

    public void setNumProc(String numProc) {
        this.numProc = numProc;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistro00000 getPgdasRegistro00000() {
        return pgdasRegistro00000;
    }

    public void setPgdasRegistro00000(PgdasRegistro00000 pgdasRegistro00000) {
        this.pgdasRegistro00000 = pgdasRegistro00000;
    }

    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 6);
        setReg(pipes.get(0));
        setAdmTrib(pipes.get(1));
        setUf(pipes.get(2));
        setMunic(pipes.get(3));
        setCodMunic(pipes.get(4));
        setNumProc(pipes.get(5));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro00000(assistente.getPgdasRegistro00000());
        assistente.getArquivoPgdas().getRegistros00001().add(this);
    }
}
