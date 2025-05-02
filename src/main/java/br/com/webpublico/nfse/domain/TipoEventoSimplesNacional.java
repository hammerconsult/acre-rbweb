package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.TipoEventoSimplesNacionalNfseDTO;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Audited
public class TipoEventoSimplesNacional extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Integer codigo;

    private String nome;

    private Integer codigoNatureza;

    private String descricao;

    private char tipo;

    private Boolean sansao;

    private Boolean mei;

    public TipoEventoSimplesNacional() {
        super();
        sansao = Boolean.FALSE;
        mei = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCodigoNatureza() {
        return codigoNatureza;
    }

    public void setCodigoNatureza(Integer codigoNatureza) {
        this.codigoNatureza = codigoNatureza;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public Boolean getSansao() {
        return sansao;
    }

    public void setSansao(Boolean sansao) {
        this.sansao = sansao;
    }

    public boolean isInclusao() {
        return tipo == 'I';
    }

    public boolean isExclusao() {
        return tipo == 'E';
    }

    public Boolean getMei() {
        return mei;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }

    @Override
    public TipoEventoSimplesNacionalNfseDTO toNfseDto() {
        TipoEventoSimplesNacionalNfseDTO tipoEventoSimplesNacionalNfseDTO = new TipoEventoSimplesNacionalNfseDTO();
        tipoEventoSimplesNacionalNfseDTO.setId(id);
        tipoEventoSimplesNacionalNfseDTO.setCodigo(codigo);
        tipoEventoSimplesNacionalNfseDTO.setCodigoNatureza(codigoNatureza);
        tipoEventoSimplesNacionalNfseDTO.setDescricao(descricao);
        tipoEventoSimplesNacionalNfseDTO.setNome(nome);
        tipoEventoSimplesNacionalNfseDTO.setSansao(sansao);
        tipoEventoSimplesNacionalNfseDTO.setTipo(tipo);
        return tipoEventoSimplesNacionalNfseDTO;
    }

//    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder();
//
//        try (BufferedReader br = Files.newBufferedReader(Paths.get("/home/wellington/Documentos/eventos-simei.txt"))) {
//
//            AtomicInteger numeroLinha = new AtomicInteger(0);
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append("insert into tipoeventosimplesnacional (id, codigo, nome, codigonatureza, descricao, tipo, sansao, mei) values (nextVal('hibernate_sequence'), ")
//                    .append(line.substring(1, 5)).append(", ")
//                    .append("'").append(line.substring(11, 268).trim()).append("', ")
//                    .append(line.substring(268, 269)).append(", ")
//                    .append("'").append(line.substring(281, 538).trim()).append("', ")
//                    .append("'").append(line.substring(538, 539).trim()).append("', ")
//                    .append(line.substring(550, 551).equals("1") ? "true" : "false").append(", true); ");
//                numeroLinha.addAndGet(1);
//            }
//            System.out.println(sb.toString());
//        } catch (IOException e) {
//            System.err.format("IOException: %s%n", e);
//        }
//    }
}
