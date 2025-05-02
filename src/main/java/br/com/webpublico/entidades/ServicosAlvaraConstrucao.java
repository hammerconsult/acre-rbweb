package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ItemServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class ServicosAlvaraConstrucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AlvaraConstrucao alvaraConstrucao;
    @ManyToOne
    private CaracteristicaConstrucaoHabitese caractConstruHabitese;
    @ManyToOne
    private ServicoConstrucao servicoConstrucao;
    private BigDecimal area;
    @ManyToOne
    private ItemServicoConstrucao itemServicoConstrucao;
    @ManyToOne
    private HabiteseClassesConstrucao habiteseClassesConstrucao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlvaraConstrucao getAlvaraConstrucao() {
        return alvaraConstrucao;
    }

    public void setAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao) {
        this.alvaraConstrucao = alvaraConstrucao;
    }

    public ServicoConstrucao getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(ServicoConstrucao servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public BigDecimal getArea() {
        return area == null ? BigDecimal.ZERO : area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public HabiteseClassesConstrucao getHabiteseClassesConstrucao() {
        return habiteseClassesConstrucao;
    }

    public void setHabiteseClassesConstrucao(HabiteseClassesConstrucao habiteseClassesConstrucao) {
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
    }

    public CaracteristicaConstrucaoHabitese getCaractConstruHabitese() {
        return caractConstruHabitese;
    }

    public void setCaractConstruHabitese(CaracteristicaConstrucaoHabitese caractConstruHabitese) {
        this.caractConstruHabitese = caractConstruHabitese;
    }

    public ItemServicoConstrucao getItemServicoConstrucao() {
        return itemServicoConstrucao;
    }

    public void setItemServicoConstrucao(ItemServicoConstrucao itemServicoConstrucao) {
        this.itemServicoConstrucao = itemServicoConstrucao;
    }
}
