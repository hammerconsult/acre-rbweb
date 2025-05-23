UPDATE EVENTOCALCULO
SET REGRA = 'valorMetroQuadrado();
      function fatorAtributo(atributo) {
         return typeof atributo === ''undefined'' || atributo.fator == null ? 0.0 : atributo.fator;
      }
      function valorMetroQuadrado() {
        var agua = fatorAtributo(atributo_ni_agua);
        var energia = fatorAtributo(atributo_ni_energia);
        var lixo = fatorAtributo(atributo_ni_lixo);
        var pavimentacao = fatorAtributo(atributo_ni_pavimentacao);
        var esgoto = fatorAtributo(atributo_ni_esgoto);
        var iluminacao = fatorAtributo(atributo_ni_iluminacao);
        var galeria = fatorAtributo(atributo_ni_galeria);
        var limpeza = fatorAtributo(atributo_ni_limpeza);
        var transporte = fatorAtributo(atributo_ni_limpeza);
        var calcada = fatorAtributo(atributo_ni_calcada);
        var sarjeta = fatorAtributo(atributo_ni_sarjeta);
        var arborizacao = fatorAtributo(atributo_ni_arborizacao);
        var telefone = fatorAtributo(atributo_ni_telefone);
        var soma = (((agua + energia)/2)*0.475) + (((lixo+pavimentacao)/2)*0.25) + (((esgoto+iluminacao+galeria+limpeza)/4)*0.15)
        + (((transporte+calcada+sarjeta)/3)*0.075) + (((arborizacao+telefone)/2)*0.05);
        if (soma <= 2) {
           return 0.4;
        } else if (soma <= 4) {
           return 0.55;
        } else if (soma <= 6) {
           return 0.7;
        } else if (soma <= 8) {
           return 0.85;
        } else {
           return 1.0;
        }
      }'
where identificacao = 'niValorMetroQuadradoLoteReferencia'
