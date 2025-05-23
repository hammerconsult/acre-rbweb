update ADQUIRENTESCALCULOITBI adq
set adq.PERCENTUAL = coalesce((select prop.PROPORCAO
                               from PROPRIEDADESIMULACAOITBI prop
                               where adq.ADQUIRENTE_ID = prop.PESSOA_ID
                                 and adq.CALCULOITBI_ID = prop.CALCULOITBI_ID
                               order by prop.PROPORCAO desc fetch first 1 row only), adq.PERCENTUAL);
