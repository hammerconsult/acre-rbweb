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
@Etiqueta("Receitas Brutas de Períodos Anteriores à Opção")
public class PgdasRegistro01500 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("01500")
    private String reg;
    @Etiqueta("Período de Apuração")
    private String pa;
    @Etiqueta("Receita Bruta Simples Nacional")
    private BigDecimal valor;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro01500() {
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

    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        pipes = UtilPgdas.getListComplementar(pipes, 3);
        setReg(pipes.get(0));
        setPa(pipes.get(1));
        setValor(UtilPgdas.getAsValor(pipes.get(2)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro00000(assistente.getPgdasRegistro00000());
        assistente.getArquivoPgdas().getRegistros01500().add(this);
    }
}
