update pontocomercial
set tipopontocomercial_id = (select l.tipopontocomercial_id
                             from localizacao l
                             where l.id = pontocomercial.LOCALIZACAO_ID),
    valormetroquadrado    = (select l.valormetroquadrado
                             from localizacao l
                             where l.id = pontocomercial.LOCALIZACAO_ID),
    ativo                 = 1
