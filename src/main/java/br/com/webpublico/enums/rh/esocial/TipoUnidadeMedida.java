package br.com.webpublico.enums.rh.esocial;

public enum TipoUnidadeMedida {
    DOSE_DIARIA_RUIDO("Dose diária de ruído", 1),
    DECIBEL_LINEAR("Decibel linear (dB (linear))", 2),
    DECIBEL_C("Decibel (C) (dB(C))", 3),
    DECIBEL_A("Decibel (A) (dB(A))", 4),
    METRO_SEGUNDO_AO_QUADRADO("Metro por segundo ao quadrado (m/s2)", 5),
    METRO_SEGUNDO_ELEVADO("Metro por segundo elevado a 1,75 (m/s1,75)", 6),
    PARTE_VAPOR_GAS_MILHAO("Parte de vapor ou gás por milhão de partes de ar contaminado (ppm)", 7),
    MILIGRAMA_METRO_CUBICO("Miligrama por metro cúbico de ar (mg/m3)", 8),
    FIBRA_CENTIMETRO("Fibra por centímetro cúbico (f/cm3)", 9),
    GRAU_CELSIUS("Grau Celsius (ºC)", 10),
    METRO_POR_SEGUNDO("Metro por segundo (m/s)", 11),
    PORCENTUAL("Porcentual", 12),
    LUX("Lux (lx)", 13),
    UNIDADE_FORMADORA_COLONIAS_METRO_CUBICO("unidade formadora de colônias por metro cúbico (ufc/m3)", 14),
    DOSE_DIARIA("Dose diária", 15),
    DOSE_MENSAL("Dose mensal", 16),
    DOSE_TRIMESTRAL("Dose trimestral", 17),
    DOSE_ANUAL("Dose anual", 18),
    WATT_POR_METRO_QUADRADO("Watt por metro quadrado (W/m2)", 19),
    AMPERE_METRO("Ampère por metro (A/m)", 20),
    MILITESLA("Militesla (mT)", 21),
    MICROTESLA("microtesla (μT)", 22),
    MILIAMPERE("miliampère (mA)", 23),
    QUILOVOLT_METRO("Quilovolt por metro (kV/m)", 24),
    VOLT_METRO("Volt por metro (V/m)", 25),
    JOULE_METRO("Joule por metro quadrado (J/m2)", 26),
    MILIJOULE_CENTIMETRO_QUADRADO("Milijoule por centímetro quadrado (mJ/cm2)", 27),
    MILISIERVERT("milisievert (mSv)", 28),
    MILHAO_PARTICULA_DECIMETRO_CUBIDO("Milhão de partículas por decímetro cúbico (mppdc)", 29),
    UR("Umidade relativa do ar (UR (%))", 30),;

    private String descricao;
    private Integer codigo;

    TipoUnidadeMedida(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo +" - " + descricao;
    }
}
