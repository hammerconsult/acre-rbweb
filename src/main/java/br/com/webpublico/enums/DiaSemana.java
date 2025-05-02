/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.entidades.usertype.DiaDaSemanaUserType;
import br.com.webpublico.entidades.usertype.LocalTimeUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
import org.joda.time.Partial;

import static org.joda.time.DateTimeConstants.*;

/**
 * @author fabio
 */
@TypeDefs({
    @TypeDef(name = "diaDaSemana", typeClass = DiaDaSemanaUserType.class),
    @TypeDef(name = "localTime", typeClass = LocalTimeUserType.class, defaultForType = LocalTime.class)})
public enum DiaSemana {

    DOMINGO(new Partial().with(DateTimeFieldType.dayOfWeek(), SUNDAY), "Domingo", 1, br.com.webpublico.tributario.enumeration.DiaSemana.DOMINGO),
    SEGUNDA(new Partial().with(DateTimeFieldType.dayOfWeek(), MONDAY), "Segunda-Feira", 2, br.com.webpublico.tributario.enumeration.DiaSemana.SEGUNDA),
    TERCA(new Partial().with(DateTimeFieldType.dayOfWeek(), TUESDAY), "Terça-Feira", 3, br.com.webpublico.tributario.enumeration.DiaSemana.TERCA),
    QUARTA(new Partial().with(DateTimeFieldType.dayOfWeek(), WEDNESDAY), "Quarta-Feira", 4, br.com.webpublico.tributario.enumeration.DiaSemana.QUARTA),
    QUINTA(new Partial().with(DateTimeFieldType.dayOfWeek(), THURSDAY), "Quinta-Feira", 5, br.com.webpublico.tributario.enumeration.DiaSemana.QUINTA),
    SEXTA(new Partial().with(DateTimeFieldType.dayOfWeek(), FRIDAY), "Sexta-Feira", 6, br.com.webpublico.tributario.enumeration.DiaSemana.SEXTA),
    SABADO(new Partial().with(DateTimeFieldType.dayOfWeek(), SATURDAY), "Sábado", 7, br.com.webpublico.tributario.enumeration.DiaSemana.SABADO);
    private final Partial dayOfWeek;
    private final String descricao;
    private final int diaDaSemana;
    private br.com.webpublico.tributario.enumeration.DiaSemana diaSemanaDto;

    private DiaSemana(Partial dayOfWeek, String descricao, int diaDaSemana, br.com.webpublico.tributario.enumeration.DiaSemana diaSemanaDto) {
        this.dayOfWeek = dayOfWeek;
        this.descricao = descricao;
        this.diaDaSemana = diaDaSemana;
        this.diaSemanaDto = diaSemanaDto;
    }

    public Partial getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getDiaDaSemana() {
        return diaDaSemana;
    }

    public br.com.webpublico.tributario.enumeration.DiaSemana getDiaSemanaDto() {
        return diaSemanaDto;
    }

    public static DiaSemana getEnumByDTO(br.com.webpublico.tributario.enumeration.DiaSemana dto) {
        for (DiaSemana value : values()) {
            if (value.diaSemanaDto.equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public static String converterDiaDaSemanaNumeroParaSiglaDaSemana(String periodo) {
        return periodo != null ?
            periodo.replace("1", DiaSemana.DOMINGO.name().substring(0, 1))
            .replace("2", DiaSemana.SEGUNDA.name().substring(0, 1))
            .replace("3", DiaSemana.TERCA.name().substring(0, 1))
            .replace("4", DiaSemana.QUARTA.name().substring(0, 1))
            .replace("5", DiaSemana.QUINTA.name().substring(0, 1))
            .replace("6", DiaSemana.SEXTA.name().substring(0, 1))
            .replace("7", DiaSemana.SABADO.name().substring(0, 1))
            : "";
    }
}
