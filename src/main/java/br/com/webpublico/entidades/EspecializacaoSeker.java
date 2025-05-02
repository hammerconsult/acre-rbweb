/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class EspecializacaoSeker implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    Long id;
    String field0;
    String field1;
    Integer field2;
    String field3;
    String field4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField0() {
        return field0;
    }

    public void setField0(String field0) {
        this.field0 = field0;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getField2() {
        return field2;
    }

    public void setField2(Integer field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }
}
