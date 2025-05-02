package br.com.webpublico.nfse.domain.pgdas.registros;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.RegistroLinhaPgdas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Abertura do Arquivo Digital")
public class PgdasRegistroAAAAA extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("AAAAA")
    private String regAbertura;
    @Etiqueta("Versão")
    private String codVer;
    @Etiqueta("Data Inicial")
    private String dtIni;
    @Etiqueta("Data Final")
    private String dtFim;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;

    public PgdasRegistroAAAAA() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegAbertura() {
        return regAbertura;
    }

    public void setRegAbertura(String regAbertura) {
        this.regAbertura = regAbertura;
    }

    public String getCodVer() {
        return codVer;
    }

    public void setCodVer(String codVer) {
        this.codVer = codVer;
    }

    public String getDtIni() {
        return dtIni;
    }

    public void setDtIni(String dtIni) {
        this.dtIni = dtIni;
    }

    public String getDtFim() {
        return dtFim;
    }

    public void setDtFim(String dtFim) {
        this.dtFim = dtFim;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente,
                           List<String> pipes) {
        setArquivoPgdas(assistente.getArquivoPgdas());
        setRegAbertura(pipes.get(0));
        setCodVer(pipes.get(1));
        setDtIni(pipes.get(2));
        setDtFim(pipes.get(3));
        assistente.getArquivoPgdas().getRegistrosAAAAA().add(this);
    }
}
