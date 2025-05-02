package br.com.webpublico.nfse.domain.pgdas.registros;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.RegistroLinhaPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Etiqueta("Atividade Selecionada para Cada Estabelecimento")
public class PgdasRegistro03100 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03100")
    private String reg;
    @Etiqueta("Tipo Atividade")
    private String tipo;
    @Etiqueta("Total da Receita")
    private BigDecimal vlTotal;
    @ManyToOne
    private PgdasRegistro03000 pgdasRegistro03000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03100() {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getVlTotal() {
        return vlTotal;
    }

    public void setVlTotal(BigDecimal vlTotal) {
        this.vlTotal = vlTotal;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistro03000 getPgdasRegistro03000() {
        return pgdasRegistro03000;
    }

    public void setPgdasRegistro03000(PgdasRegistro03000 pgdasRegistro03000) {
        this.pgdasRegistro03000 = pgdasRegistro03000;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 13);
        setReg(pipes.get(0));
        setTipo(pipes.get(1));
        setVlTotal(UtilPgdas.getAsValor(pipes.get(2)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro03000(assistente.getPgdasRegistro03000());
        assistente.getArquivoPgdas().getRegistros03100().add(this);
    }
}
