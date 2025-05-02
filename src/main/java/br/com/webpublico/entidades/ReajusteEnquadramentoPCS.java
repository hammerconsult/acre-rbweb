package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/05/2015
 * Time: 11:18
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
public class ReajusteEnquadramentoPCS extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EnquadramentoPCS enquadramentoPCS;
    @ManyToOne
    private ReajustePCS reajustePCS;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    public ReajusteEnquadramentoPCS() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnquadramentoPCS getEnquadramentoPCS() {
        return enquadramentoPCS;
    }

    public void setEnquadramentoPCS(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public ReajustePCS getReajustePCS() {
        return reajustePCS;
    }

    public void setReajustePCS(ReajustePCS reajustePCS) {
        this.reajustePCS = reajustePCS;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public static class Comparators {

        public static Comparator<ReajusteEnquadramentoPCS> CATEGORIAPCSASC = new Comparator<ReajusteEnquadramentoPCS>() {
            @Override
            public int compare(ReajusteEnquadramentoPCS o1, ReajusteEnquadramentoPCS o2) {
                return o1.enquadramentoPCS.getCategoriaPCS().getDescricao().compareTo(o2.enquadramentoPCS.getCategoriaPCS().getDescricao());
            }
        };
//        public static Comparator<ReajusteEnquadramentoPCS> INICIOVIGENCIADESC = new Comparator<ReajusteEnquadramentoPCS>() {
//            @Override
//            public int compare(ReajusteEnquadramentoPCS o1, ReajusteEnquadramentoPCS o2) {
//                return o2.inicioVigencia.compareTo(o1.inicioVigencia);
//            }
//        };

//        public static Comparator<PeriodoAquisitivoFL> NAMEANDAGE = new Comparator<PeriodoAquisitivoFL>() {
//            @Override
//            public int compare(PeriodoAquisitivoFL o1, PeriodoAquisitivoFL o2) {
//                int i = o1.name.compareTo(o2.name);
//                if (i == 0) {
//                    i = o1.age - o2.age;
//                }
//                return i;
//            }
//        };
    }
}
