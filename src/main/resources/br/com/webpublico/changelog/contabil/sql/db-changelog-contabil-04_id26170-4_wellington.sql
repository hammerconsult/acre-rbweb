update lcp l set L.TIPOCONTAAUXILIARCREDITO_ID = (select id from tipocontaauxiliar where codigo = 80) where id in (
            select lcp.id from lcp lcp
            inner join clp clp on lcp.clp_id = clp.id
            inner join conta c on lcp.contacredito_id = c.id
            where
            lcp.TIPOCONTAAUXILIARCREDITO_ID is null
            and c.codigo in ('7.9.1.1.9.00.00','8.9.1.1.9.00.00')
            and to_date('23/07/2018', 'dd/mm/yyyy') between CLP.INICIOVIGENCIA and coalesce(CLP.FIMVIGENCIA, to_date('23/07/2018', 'dd/mm/yyyy')));

update lcp l set L.TIPOCONTAAUXILIARDEBITO_ID = (select id from tipocontaauxiliar where codigo = 80) where id in (
            select lcp.id from lcp lcp
            inner join clp clp on lcp.clp_id = clp.id
            inner join conta c on lcp.contadebito_id = c.id
            where
            lcp.TIPOCONTAAUXILIARDEBITO_ID is null
            and c.codigo in ('7.9.1.1.9.00.00','8.9.1.1.9.00.00')
            and to_date('23/07/2018', 'dd/mm/yyyy') between CLP.INICIOVIGENCIA and coalesce(CLP.FIMVIGENCIA, to_date('23/07/2018', 'dd/mm/yyyy')));
